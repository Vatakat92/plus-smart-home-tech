package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScenarioAddedEventConverter implements HubEventConverter {

    @Override
    public SpecificRecordBase convert(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        ScenarioAddedEventAvro.Builder builder = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName());

        List<ScenarioConditionAvro> conditions = new ArrayList<>();
        for (ScenarioConditionProto conditionProto : scenarioAddedEvent.getConditionList()) {
            conditions.add(toAvroScenarioCondition(conditionProto));
        }
        builder.setConditions(conditions);

        List<DeviceActionAvro> actions = new ArrayList<>();
        for (DeviceActionProto actionProto : scenarioAddedEvent.getActionList()) {
            actions.add(toAvroDeviceAction(actionProto));
        }
        builder.setActions(actions);

        return builder.build();
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    private ScenarioConditionAvro toAvroScenarioCondition(ScenarioConditionProto proto) {
        ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(toAvroConditionType(proto.getType()))
                .setOperation(toAvroConditionOperation(proto.getOperation()));

        switch (proto.getValueCase()) {
            case BOOL_VALUE:
                builder.setValue(proto.getBoolValue());
                break;
            case INT_VALUE:
                builder.setValue(proto.getIntValue());
                break;
            case VALUE_NOT_SET:
                break;
        }

        return builder.build();
    }

    private DeviceActionAvro toAvroDeviceAction(DeviceActionProto proto) {
        DeviceActionAvro.Builder builder = DeviceActionAvro.newBuilder()
                .setSensorId(proto.getSensorId())
                .setType(toAvroActionType(proto.getType()));

        if (proto.hasValue()) {
            builder.setValue(proto.getValue());
        }

        return builder.build();
    }

    private ConditionTypeAvro toAvroConditionType(ConditionTypeProto proto) {
        return switch (proto) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            default -> throw new IllegalArgumentException("Unknown condition type: " + proto);
        };
    }

    private ConditionOperationAvro toAvroConditionOperation(ConditionOperationProto proto) {
        return switch (proto) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> throw new IllegalArgumentException("Unknown condition operation: " + proto);
        };
    }

    private ActionTypeAvro toAvroActionType(ActionTypeProto proto) {
        return switch (proto) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> throw new IllegalArgumentException("Unknown action type: " + proto);
        };
    }
}