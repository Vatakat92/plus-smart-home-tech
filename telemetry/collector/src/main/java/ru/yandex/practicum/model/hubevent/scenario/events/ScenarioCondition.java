package ru.yandex.practicum.model.hubevent.scenario.events;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScenarioCondition {
    private String sensorId;

    private ScenarioConditionType type;

    private ScenarioConditionOperation operation;

    private Integer value;
}