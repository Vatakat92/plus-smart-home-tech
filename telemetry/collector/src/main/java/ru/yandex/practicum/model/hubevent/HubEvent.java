package ru.yandex.practicum.model.hubevent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.device.events.DeviceAddedEvent;
import ru.yandex.practicum.model.hubevent.device.events.DeviceRemovedEvent;
import ru.yandex.practicum.model.hubevent.scenario.events.ScenarioAddedEvent;
import ru.yandex.practicum.model.hubevent.scenario.events.ScenarioRemovedEvent;

import java.time.Instant;

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
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})

@NoArgsConstructor
@AllArgsConstructor
public abstract class HubEvent {

    @NotNull
    private String hubId;

    @NotNull
    @Builder.Default
    private Instant timestamp = Instant.now();

    public abstract HubEventType getType();
}