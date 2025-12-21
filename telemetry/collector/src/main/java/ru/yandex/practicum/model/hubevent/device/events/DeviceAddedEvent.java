package ru.yandex.practicum.model.hubevent.device.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.model.hubevent.device.DeviceEvent;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAddedEvent extends DeviceEvent {

    private DeviceType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}