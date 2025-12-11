package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.HubEventHandler;
import ru.yandex.practicum.service.SensorEventHandler;

import jakarta.annotation.PostConstruct;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class KafkaEventSenderImpl implements KafkaEventSender, DisposableBean {

    private final HubEventHandler hubEventHandler;
    private final SensorEventHandler sensorEventHandler;

    private Producer<String, SensorEventAvro> sensorProducer;
    private Producer<String, HubEventAvro> hubProducer;

    @Value("${kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${kafka.topic.sensors:telemetry.sensors.v1}")
    private String sensorsTopic;

    @Value("${kafka.topic.hubs:telemetry.hubs.v1}")
    private String hubsTopic;

    @Value("${kafka.producer.acks:all}")
    private String acks;

    @Value("${kafka.producer.retries:3}")
    private String retries;

    @Value("${kafka.producer.request-timeout-ms:30000}")
    private String requestTimeoutMs;

    @Value("${kafka.producer.delivery-timeout-ms:60000}")
    private String deliveryTimeoutMs;

    public KafkaEventSenderImpl(HubEventHandler hubEventHandler, SensorEventHandler sensorEventHandler) {
        this.hubEventHandler = hubEventHandler;
        this.sensorEventHandler = sensorEventHandler;
    }

    @PostConstruct
    public void init() {
        log.info("Initializing Kafka producers for topics: {} (sensors), {} (hubs)", sensorsTopic, hubsTopic);
        sensorProducer = createProducer();
        hubProducer = createProducer();
        log.info("Kafka producers initialized successfully");
    }

    private <T> Producer<String, T> createProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class.getName());
        config.put(ProducerConfig.ACKS_CONFIG, acks);
        config.put(ProducerConfig.RETRIES_CONFIG, retries);
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeoutMs);
        return new KafkaProducer<>(config);
    }

    @Override
    public void send(SensorEvent event) {
        SensorEventAvro avro = sensorEventHandler.toAvro(event);
        sendEvent(sensorsTopic, event.getHubId(), avro, sensorProducer, "SensorEvent");
    }

    @Override
    public void send(HubEvent event) {
        HubEventAvro avro = hubEventHandler.toAvro(event);
        sendEvent(hubsTopic, event.getHubId(), avro, hubProducer, "HubEvent");
    }

    private <T> void sendEvent(String topic, String key, T payload, Producer<String, T> producer, String eventType) {
        try {
            ProducerRecord<String, T> record = new ProducerRecord<>(topic, key, payload);
            log.debug("Sending {} to Kafka topic {} with key {}", eventType, topic, key);

            Future<RecordMetadata> future = producer.send(record);
            RecordMetadata metadata = future.get(2, TimeUnit.SECONDS);

            log.info("Sent {} to Kafka topic {} (hubId: {}, partition: {}, offset: {})",
                    eventType, topic, key, metadata.partition(), metadata.offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending {}: {}", eventType, e.getMessage(), e);
            throw new RuntimeException("Interrupted while sending " + eventType + " to Kafka", e);
        } catch (ExecutionException e) {
            log.error("Failed to send {}: {}", eventType, e.getCause().getMessage(), e.getCause());
            throw new RuntimeException("Failed to send " + eventType + " to Kafka", e.getCause());
        } catch (TimeoutException e) {
            log.warn("Timeout while sending {}: {}", eventType, e.getMessage());
            throw new RuntimeException("Timeout while sending " + eventType + " to Kafka", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending {}: {}", eventType, e.getMessage(), e);
            throw new RuntimeException("Unexpected error while sending " + eventType + " to Kafka", e);
        }
    }

    @Override
    public void destroy() {
        if (sensorProducer != null) {
            sensorProducer.close();
            log.info("Sensor producer closed");
        }
        if (hubProducer != null) {
            hubProducer.close();
            log.info("Hub producer closed");
        }
    }
}
