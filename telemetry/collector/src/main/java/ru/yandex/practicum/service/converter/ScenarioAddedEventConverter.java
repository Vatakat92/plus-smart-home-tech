package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.hubevent.HubEventType;
import ru.yandex.practicum.model.hubevent.scenario.events.*;

import java.util.List;

@Component
public class ScenarioAddedEventConverter implements HubEventConverter {

    @Override
    public SpecificRecordBase convert(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;

        List<DeviceActionAvro> actionsAvros = scenarioAddedEvent.getActions().stream()
                .map(this::toAvro)
                .toList();

        List<ScenarioConditionAvro> conditionAvros = scenarioAddedEvent.getConditions().stream()
                .map(this::toAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName()) // используем существующую строку
                .setActions(actionsAvros)
                .setConditions(conditionAvros)
                .build();
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro toAvro(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setOperation(toAvro(scenarioCondition.getOperation()))
                .setType(toAvro(scenarioCondition.getType()))
                .setSensorId(scenarioCondition.getSensorId()) // используем существующую строку
                .setValue(scenarioCondition.getValue())
                .build();
    }

    private ConditionTypeAvro toAvro(ScenarioConditionType scenarioConditionType) {
        return switch (scenarioConditionType) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
        };
    }

    private ConditionOperationAvro toAvro(ScenarioConditionOperation scenarioConditionOperation) {
        return switch (scenarioConditionOperation) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
        };
    }

    private ActionTypeAvro toAvro(DeviceActionType deviceActionType) {
        return switch (deviceActionType) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
        };
    }

    private DeviceActionAvro toAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setType(toAvro(deviceAction.getType()))
                .setSensorId(deviceAction.getSensorId())
                .setValue(deviceAction.getValue())
                .build();
    }
}
