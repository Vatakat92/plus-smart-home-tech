package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.converter.SensorEventConverter;

import java.util.Map;

@Component
public class SensorEventHandler {

    private final Map<String, SensorEventConverter> converters;

    public SensorEventHandler(Map<String, SensorEventConverter> converters) {
        this.converters = converters;
    }

    public SensorEventAvro toAvro(SensorEvent sensorEvent) {
        var converter = converters.get(sensorEvent.getType().toString());
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
