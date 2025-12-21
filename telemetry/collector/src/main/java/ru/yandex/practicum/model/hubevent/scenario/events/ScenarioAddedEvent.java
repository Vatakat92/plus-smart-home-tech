package ru.yandex.practicum.model.hubevent.scenario.events;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.model.hubevent.scenario.ScenarioEvent;

import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScenarioAddedEvent extends ScenarioEvent {

    @NotEmpty
    private List<ScenarioCondition> conditions;

    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}



