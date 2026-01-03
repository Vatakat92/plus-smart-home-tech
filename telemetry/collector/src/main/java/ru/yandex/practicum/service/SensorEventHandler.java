package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.converter.SensorEventConverter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorEventHandler {

    private final Map<SensorEventProto.PayloadCase, SensorEventConverter> converters;

    public SensorEventHandler(List<SensorEventConverter> converterList) {
        this.converters = converterList.stream()
                .collect(Collectors.toMap(SensorEventConverter::getType, c -> c));
    }

    public SensorEventAvro toAvro(SensorEventProto sensorEvent) {
        SensorEventProto.PayloadCase payloadCase = sensorEvent.getPayloadCase();
        SensorEventConverter converter = converters.get(payloadCase);
        if (converter == null) {
            throw new IllegalArgumentException("Unknown sensor event type: " + payloadCase);
        }

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(convertTimestamp(sensorEvent.getTimestamp()))
                .setPayload(converter.convert(sensorEvent))
                .build();
    }

    private Instant convertTimestamp(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}