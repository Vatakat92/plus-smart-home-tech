package ru.yandex.practicum.model.hubevent.device.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.model.hubevent.device.DeviceEvent;

@Getter
@SuperBuilder
@NoArgsConstructor
public class DeviceRemovedEvent extends DeviceEvent {

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
