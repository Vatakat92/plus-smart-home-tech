package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventConverter {
    SpecificRecordBase convert(HubEventProto event);

    HubEventProto.PayloadCase getType();
}