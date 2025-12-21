package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.service.converter.HubEventConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HubEventHandler {
    private final Map<HubEventType, HubEventConverter> converters;

    public HubEventHandler(List<HubEventConverter> converterList) {
        this.converters = converterList.stream()
                .collect(Collectors.toMap(HubEventConverter::getType, c -> c));
    }

    public HubEventAvro toAvro(HubEvent hubEvent) {
        HubEventAvro.Builder hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp());

        HubEventConverter converter = converters.get(hubEvent.getType());
        if (converter == null) {
            throw new IllegalArgumentException("Unknown hub event type: " + hubEvent.getType());
        }

        return hubEventAvro.setPayload(converter.convert(hubEvent)).build();
    }
}
