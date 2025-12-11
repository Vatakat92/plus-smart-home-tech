package ru.yandex.practicum.kafka;


import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

public interface KafkaEventSender {

    void send(SensorEvent event);

    void send(HubEvent event);
}