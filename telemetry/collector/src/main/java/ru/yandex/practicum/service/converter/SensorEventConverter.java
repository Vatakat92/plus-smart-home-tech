package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

public interface SensorEventConverter {
    SpecificRecordBase convert(SensorEvent event);

    SensorEventType getEventType();
}
