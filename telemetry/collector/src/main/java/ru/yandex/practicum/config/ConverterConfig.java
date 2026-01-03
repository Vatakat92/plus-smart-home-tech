package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.service.converter.HubEventConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ConverterConfig {

    @Bean
    public Map<HubEventProto.PayloadCase, HubEventConverter> hubConverters(List<HubEventConverter> converters) {
        return converters.stream()
                .collect(Collectors.toMap(
                        HubEventConverter::getType,
                        c -> c
                ));
    }
}