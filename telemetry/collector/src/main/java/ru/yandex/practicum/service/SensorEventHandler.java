package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.converter.SensorEventConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SensorEventHandler {
    private final Map<String, SensorEventConverter> converters;

    public SensorEventHandler(List<SensorEventConverter> converterList) {
        this.converters = converterList.stream()
                .collect(Collectors.toMap(
                        c -> c.getEventType().toString(),
                        c -> c
                ));
    }

    public SensorEventAvro toAvro(SensorEvent sensorEvent) {
        SensorEventAvro.Builder sensorEventAvro = SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp());

        var converter = converters.get(sensorEvent.getType().toString());
        if (converter == null) {
            throw new IllegalArgumentException("Unknown sensor event type: " + sensorEvent.getType());
        }

        return sensorEventAvro.setPayload(converter.convert(sensorEvent)).build();
    }
}
