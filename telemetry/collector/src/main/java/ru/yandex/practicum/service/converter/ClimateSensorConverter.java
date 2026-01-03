package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Component
public class ClimateSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEventProto event) {
        ClimateSensorProto climateSensor = event.getClimateSensor();
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateSensor.getTemperatureC())
                .setHumidity(climateSensor.getHumidity())
                .setCo2Level(climateSensor.getCo2Level())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }
}