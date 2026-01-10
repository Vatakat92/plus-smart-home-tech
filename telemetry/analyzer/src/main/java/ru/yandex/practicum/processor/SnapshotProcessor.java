package ru.yandex.practicum.processor;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class SnapshotProcessor implements Runnable {

    private final ScenarioRepository scenarioRepository;
    private final HubRouterControllerBlockingStub hubRouterClient;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.snapshots}")
    private String snapshotsTopic;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private KafkaConsumer<String, SensorsSnapshotAvro> consumer;

    public SnapshotProcessor(
            ScenarioRepository scenarioRepository,
            @GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient
    ) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterClient = hubRouterClient;
    }

    @Override
    public void run() {
        try {
            consumer = new KafkaConsumer<>(consumerProps());
            consumer.subscribe(Collections.singletonList(snapshotsTopic));

            while (running.get()) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        consumer.poll(Duration.ofMillis(200));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    processSnapshot(record.value());
                }

                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            if (running.get()) {
                throw e;
            }
        } catch (Exception e) {
            log.error("SnapshotProcessor error", e);
        } finally {
            if (consumer != null) {
                try {
                    consumer.commitSync();
                } finally {
                    consumer.close();
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
        if (consumer != null) {
            consumer.wakeup();
        }
    }

    private Properties consumerProps() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-snapshots-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "ru.yandex.practicum.deserializer.SensorsSnapshotDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    private void processSnapshot(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : scenarios) {
            if (checkScenarioConditions(scenario, snapshot)) {
                executeScenarioActions(hubId, scenario);
            }
        }
    }

    private boolean checkScenarioConditions(Scenario scenario, SensorsSnapshotAvro snapshot) {
        for (ScenarioCondition sc : scenario.getConditions()) {
            SensorStateAvro state =
                    snapshot.getSensorsState().get(sc.getSensor().getId());

            if (state == null || !evaluateCondition(sc.getCondition(), state.getData())) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateCondition(Condition condition, Object data) {
        Integer expected = condition.getValue();
        if (expected == null) {
            return false;
        }

        if (data instanceof TemperatureSensorAvro t) {
            return compare(t.getTemperatureC(), expected, condition.getOperation());
        }
        if (data instanceof ClimateSensorAvro c) {
            return compare(c.getTemperatureC(), expected, condition.getOperation());
        }
        if (data instanceof LightSensorAvro l) {
            return compare(l.getLuminosity(), expected, condition.getOperation());
        }
        if (data instanceof MotionSensorAvro m) {
            return m.getMotion() && expected == 1;
        }
        if (data instanceof SwitchSensorAvro s) {
            return s.getStat() == (expected == 1);
        }

        return false;
    }

    private boolean compare(int actual, int expected, String operation) {
        return switch (operation) {
            case "GREATER_THAN" -> actual > expected;
            case "LOWER_THAN" -> actual < expected;
            case "EQUALS" -> actual == expected;
            default -> false;
        };
    }

    private void executeScenarioActions(String hubId, Scenario scenario) {
        Instant now = Instant.now();

        for (ScenarioAction sa : scenario.getActions()) {
            Action action = sa.getAction();
            int value = action.getValue() != null ? action.getValue() : 0;

            DeviceActionProto grpcAction = DeviceActionProto.newBuilder()
                    .setSensorId(sa.getSensor().getId())
                    .setType(ActionTypeProto.valueOf(action.getType()))
                    .setValue(value)
                    .build();

            DeviceActionRequest request = DeviceActionRequest.newBuilder()
                    .setHubId(hubId)
                    .setScenarioName(scenario.getName())
                    .setAction(grpcAction)
                    .setTimestamp(Timestamp.newBuilder()
                            .setSeconds(now.getEpochSecond())
                            .setNanos(now.getNano())
                            .build())
                    .build();

            try {
                Empty ignored = hubRouterClient.handleDeviceAction(request);
            } catch (StatusRuntimeException e) {
                log.error("gRPC error for scenario {}", scenario.getName(), e);
            }
        }
    }
}
