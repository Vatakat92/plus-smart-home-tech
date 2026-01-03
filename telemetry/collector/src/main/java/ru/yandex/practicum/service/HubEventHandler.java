package ru.yandex.practicum.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.converter.HubEventConverter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HubEventHandler {
    private final Map<HubEventProto.PayloadCase, HubEventConverter> converters;

    public HubEventHandler(List<HubEventConverter> converterList) {
        this.converters = converterList.stream()
                .collect(Collectors.toMap(HubEventConverter::getType, c -> c));
    }

    public HubEventAvro toAvro(HubEventProto hubEvent) {
        HubEventAvro.Builder hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(convertTimestamp(hubEvent.getTimestamp()));

        HubEventProto.PayloadCase payloadCase = hubEvent.getPayloadCase();
        HubEventConverter converter = converters.get(payloadCase);
        if (converter == null) {
            throw new IllegalArgumentException("Unknown hub event payload case: " + payloadCase);
        }

        return hubEventAvro.setPayload(converter.convert(hubEvent)).build();
    }

    private Instant convertTimestamp(com.google.protobuf.Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
}