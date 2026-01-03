package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEventProto event) {
        LightSensorProto lightSensor = event.getLightSensor();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensor.getLinkQuality())
                .setLuminosity(lightSensor.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }
}