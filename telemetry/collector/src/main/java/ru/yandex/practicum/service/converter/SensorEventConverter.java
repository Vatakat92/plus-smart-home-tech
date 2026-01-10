package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventConverter {
    SpecificRecordBase convert(SensorEventProto event);

    SensorEventProto.PayloadCase getType();
}