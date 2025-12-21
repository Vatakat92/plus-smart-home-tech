package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.converter.SensorEventConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ConverterConfig {

    @Bean
    public Map<SensorEventType, SensorEventConverter> sensorConverters(List<SensorEventConverter> converters) {
        return converters.stream()
                .collect(Collectors.toMap(
                        SensorEventConverter::getType,
                        c -> c
                ));
    }
}