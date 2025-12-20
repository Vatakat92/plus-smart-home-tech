package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.model.sensor.events.ClimateSensorEvent;

@Component
public class ClimateSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEvent event) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) event;

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateSensorEvent.getCo2Level() != null ? 
                           climateSensorEvent.getCo2Level() : 0)
                .setHumidity(climateSensorEvent.getHumidity() != null ? 
                           climateSensorEvent.getHumidity() : 0)
                .setTemperatureC(climateSensorEvent.getTemperatureC() != null ? 
                               climateSensorEvent.getTemperatureC() : 0)
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}