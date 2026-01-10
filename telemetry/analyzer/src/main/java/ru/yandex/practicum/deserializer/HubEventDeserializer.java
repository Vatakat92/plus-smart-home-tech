package ru.yandex.practicum.deserializer;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@SuppressWarnings("unused")
public class HubEventDeserializer extends BaseAvroDeserializer<HubEventAvro> {
    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}