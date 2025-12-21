package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.model.sensor.events.TemperatureSensorEvent;

@Component
public class TemperatureSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEvent event) {
        TemperatureSensorEvent tempEvent = (TemperatureSensorEvent) event;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(tempEvent.getTemperatureC() != null ? tempEvent.getTemperatureC() : 0)
                .setTemperatureF(tempEvent.getTemperatureF() != null ? tempEvent.getTemperatureF() : 0)
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
