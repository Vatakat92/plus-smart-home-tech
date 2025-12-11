package ru.yandex.practicum.model.hubevent.device;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.hubevent.device.events.DeviceAddedEvent;
import ru.yandex.practicum.model.hubevent.device.events.DeviceRemovedEvent;

@Getter
@ToString
@SuperBuilder

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
})

@NoArgsConstructor
public abstract class DeviceEvent extends HubEvent {

    @NotNull
    private String id;
}



