package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.model.sensor.events.MotionSensorEvent;

@Component
public class MotionSensorConverter implements SensorEventConverter {

    @Override
    public SpecificRecordBase convert(SensorEvent event) {
        MotionSensorEvent motionEvent = (MotionSensorEvent) event;
        return MotionSensorAvro.newBuilder()
                .setMotion(motionEvent.getMotion())
                .setVoltage(motionEvent.getVoltage() != null ? motionEvent.getVoltage() : 0)
                .setLinkQuality(motionEvent.getLinkQuality() != null ? motionEvent.getLinkQuality() : 0)
                .build();
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
