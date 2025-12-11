package ru.yandex.practicum.model.hubevent.scenario.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.model.hubevent.scenario.ScenarioEvent;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ScenarioRemovedEvent extends ScenarioEvent {

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
