package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.converter.SensorEventConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorEventHandler {

    private final Map<SensorEventType, SensorEventConverter> converters;

    public SensorEventHandler(List<SensorEventConverter> converterList) {
        this.converters = converterList.stream()
                .collect(Collectors.toMap(SensorEventConverter::getType, c -> c));
    }

    public SensorEventAvro toAvro(SensorEvent sensorEvent) {
        SensorEventConverter converter = converters.get(sensorEvent.getType());
        if (converter == null) {
            throw new IllegalArgumentException("Unknown sensor event type: " + sensorEvent.getType());
        }

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(converter.convert(sensorEvent))
                .build();
    }
}
