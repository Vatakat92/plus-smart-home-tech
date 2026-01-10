package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.HubEventHandler;
import ru.yandex.practicum.service.SensorEventHandler;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class KafkaEventSenderImpl implements KafkaEventSender {

    private final HubEventHandler hubEventHandler;
    private final SensorEventHandler sensorEventHandler;

    private final Producer<String, SensorEventAvro> sensorProducer;
    private final Producer<String, HubEventAvro> hubProducer;

    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    @Value("${kafka.topic.hubs:telemetry.hubs.v1}")
    private String hubsTopic;

    public KafkaEventSenderImpl(
            HubEventHandler hubEventHandler,
            SensorEventHandler sensorEventHandler,
            Producer<String, SensorEventAvro> sensorProducer,
            Producer<String, HubEventAvro> hubProducer
    ) {
        this.hubEventHandler = hubEventHandler;
        this.sensorEventHandler = sensorEventHandler;
        this.sensorProducer = sensorProducer;
        this.hubProducer = hubProducer;
    }

    @Override
    public void send(SensorEventProto event) {
        SensorEventAvro avro = sensorEventHandler.toAvro(event);
        sendEvent(sensorsTopic, event.getHubId(), avro, sensorProducer, "SensorEventProto");
    }

    @Override
    public void send(HubEventProto event) {
        HubEventAvro avro = hubEventHandler.toAvro(event);
        sendEvent(hubsTopic, event.getHubId(), avro, hubProducer, "HubEventProto");
    }

    private <T> void sendEvent(
            String topic,
            String key,
            T payload,
            Producer<String, T> producer,
            String eventType
    ) {
        try {
            ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, payload);
            Future<RecordMetadata> future = producer.send(record);

            RecordMetadata metadata = future.get(2, TimeUnit.SECONDS);

            log.info("Sent {} → topic={}, key={}, partition={}, offset={}",
                    eventType, topic, key, metadata.partition(), metadata.offset());

        } catch (Exception e) {
            log.error("Failed to send {}: {}", eventType, e.getMessage(), e);
            throw new RuntimeException("Kafka send failed for " + eventType, e);
        }
    }
}
