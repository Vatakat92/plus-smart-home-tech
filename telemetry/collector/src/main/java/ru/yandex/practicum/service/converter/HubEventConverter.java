package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.hubevent.HubEventType;

public interface HubEventConverter {
    SpecificRecordBase convert(HubEvent event);

    HubEventType getType();
}