package ru.yandex.practicum.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${analyzer.topics.hub-events}")
    private String hubEventsTopic;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private KafkaConsumer<String, HubEventAvro> consumer;

    @Override
    public void run() {
        log.info("HubEventProcessor started. Subscribe to topic: {}", hubEventsTopic);

        try {
            consumer = new KafkaConsumer<>(consumerProps());
            consumer.subscribe(Collections.singletonList(hubEventsTopic));

            while (running.get()) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(Duration.ofMillis(200));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    processEvent(record.value());
                }

                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            if (running.get()) {
                throw e;
            }
            log.info("HubEventProcessor stopped");
        } catch (Exception e) {
            log.error("HubEventProcessor error", e);
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "analyzer-hub-events-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "ru.yandex.practicum.deserializer.HubEventDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    private void processEvent(HubEventAvro event) {
        Object payload = event.getPayload();
        log.debug("HubEvent received: {}", payload);

        if (payload instanceof DeviceAddedEventAvro) {
            handleDeviceAdded(event);
        } else if (payload instanceof DeviceRemovedEventAvro) {
            handleDeviceRemoved(event);
        } else if (payload instanceof ScenarioAddedEventAvro) {
            handleScenarioAdded(event);
        } else if (payload instanceof ScenarioRemovedEventAvro) {
            handleScenarioRemoved(event);
        } else {
            log.warn("Unknown event type: {}", payload.getClass());
        }
    }

    private void handleDeviceAdded(HubEventAvro event) {
        DeviceAddedEventAvro payload = (DeviceAddedEventAvro) event.getPayload();

        sensorRepository.findById(payload.getId()).ifPresentOrElse(
                s -> log.debug("Sensor {} already exists in HUB {}", payload.getId(), event.getHubId()),
                () -> {
                    sensorRepository.save(Sensor.builder()
                            .id(payload.getId())
                            .hubId(event.getHubId())
                            .build());
                    log.info("New sensor {} added in HUB {}", payload.getId(), event.getHubId());
                }
        );
    }

    private void handleDeviceRemoved(HubEventAvro event) {
        DeviceRemovedEventAvro payload = (DeviceRemovedEventAvro) event.getPayload();

        if (sensorRepository.existsById(payload.getId())) {
            sensorRepository.deleteById(payload.getId());
            log.info("Sensor {} deleted", payload.getId());
        } else {
            log.debug("Sensor {} not found", payload.getId());
        }
    }

    private void handleScenarioAdded(HubEventAvro event) {
        ScenarioAddedEventAvro payload = (ScenarioAddedEventAvro) event.getPayload();

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(event.getHubId(), payload.getName())
                .orElseGet(() -> Scenario.builder()
                        .hubId(event.getHubId())
                        .name(payload.getName())
                        .build());

        scenarioRepository.save(scenario);

        scenario.getConditions().clear();
        scenario.getActions().clear();

        saveConditions(payload, scenario, event.getHubId());
        saveActions(payload, scenario, event.getHubId());

        scenarioRepository.save(scenario);

        log.info("Scenario {} added for HUB {} ({} conditions, {} actions)",
                payload.getName(),
                event.getHubId(),
                payload.getConditions().size(),
                payload.getActions().size());
    }

    private void saveConditions(ScenarioAddedEventAvro payload, Scenario scenario, String hubId) {
        payload.getConditions().forEach(cond -> {
            Integer value = convertConditionValue(cond.getValue());

            Condition condition = conditionRepository.save(Condition.builder()
                    .type(cond.getType().name())
                    .operation(cond.getOperation().name())
                    .value(value)
                    .build());

            sensorRepository.findByIdAndHubId(cond.getSensorId(), hubId)
                    .ifPresent(sensor -> scenario.getConditions().add(
                            ScenarioCondition.builder()
                                    .id(new ScenarioConditionId(
                                            scenario.getId(),
                                            sensor.getId(),
                                            condition.getId()))
                                    .scenario(scenario)
                                    .sensor(sensor)
                                    .condition(condition)
                                    .build()
                    ));
        });
    }

    private void saveActions(ScenarioAddedEventAvro payload, Scenario scenario, String hubId) {
        payload.getActions().forEach(act -> {
            Action action = actionRepository.save(Action.builder()
                    .type(act.getType().name())
                    .value(act.getValue())
                    .build());

            sensorRepository.findByIdAndHubId(act.getSensorId(), hubId)
                    .ifPresent(sensor -> scenario.getActions().add(
                            ScenarioAction.builder()
                                    .id(new ScenarioActionId(
                                            scenario.getId(),
                                            sensor.getId(),
                                            action.getId()))
                                    .scenario(scenario)
                                    .sensor(sensor)
                                    .action(action)
                                    .build()
                    ));
        });
    }

    private Integer convertConditionValue(Object rawValue) {
        if (rawValue instanceof Integer i) {
            return i;
        }
        if (rawValue instanceof Boolean b) {
            return b ? 1 : 0;
        }
        return null;
    }

    private void handleScenarioRemoved(HubEventAvro event) {
        ScenarioRemovedEventAvro payload = (ScenarioRemovedEventAvro) event.getPayload();

        scenarioRepository.findByHubIdAndName(event.getHubId(), payload.getName())
                .ifPresentOrElse(
                        scenario -> {
                            scenarioRepository.delete(scenario);
                            log.info("Scenario {} deleted in HUB {}", payload.getName(), event.getHubId());
                        },
                        () -> log.debug("Scenario {} not found in HUB {}", payload.getName(), event.getHubId())
                );
    }
}
