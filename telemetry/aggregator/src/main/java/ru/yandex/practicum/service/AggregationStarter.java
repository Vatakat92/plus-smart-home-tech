package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.serializer.GeneralAvroSerializer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final SnapshotAggregator snapshotAggregator;
    private final GeneralAvroSerializer avroSerializer = new GeneralAvroSerializer();

    @Value("${aggregator.topics.sensors}")
    private String sensorsTopic;

    @Value("${aggregator.topics.snapshots}")
    private String snapshotsTopic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final AtomicBoolean running = new AtomicBoolean(true);

    public void start() {
        log.info("Aggregator started. Subscribing to topic: {}", sensorsTopic);

        try (KafkaConsumer<String, SensorEventAvro> consumer = new KafkaConsumer<>(createConsumerProperties());
             KafkaProducer<String, byte[]> producer = new KafkaProducer<>(createProducerProperties())) {

            consumer.subscribe(Collections.singletonList(sensorsTopic));

            while (running.get()) {
                ConsumerRecords<String, SensorEventAvro> records;
                try {
                    records = consumer.poll(Duration.ofMillis(100));
                } catch (WakeupException e) {
                    log.info("Consumer woken up for shutdown");
                    break;
                }

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    log.debug("Event: {} from hub: {}", event.getId(), event.getHubId());

                    Optional<SensorsSnapshotAvro> updatedSnap = snapshotAggregator.updateState(event);

                    updatedSnap.ifPresent(snapshot -> {
                        try {
                            byte[] payload = avroSerializer.serialize(snapshotsTopic, snapshot);
                            producer.send(new ProducerRecord<>(snapshotsTopic, snapshot.getHubId(), payload));
                            log.info("Snapshot sent for hub {} to topic {}", snapshot.getHubId(), snapshotsTopic);
                        } catch (Exception e) {
                            log.error("Failed to serialize/send snapshot for hub {}", snapshot.getHubId(), e);
                        }
                    });
                }

                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.error("Async commit failed", exception);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Unexpected error in aggregator loop", e);
        } finally {
            log.info("Aggregator shutting down");
        }
    }

    private Properties createConsumerProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregator-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.deserializer.SensorEventDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    private Properties createProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        return props;
    }
}