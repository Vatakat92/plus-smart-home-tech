package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEventProto event) {
        MotionSensorProto motionSensor = event.getMotionSensor();
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensor.getLinkQuality())
                .setMotion(motionSensor.getMotion())
                .setVoltage(motionSensor.getVoltage())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }
}