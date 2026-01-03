package ru.yandex.practicum.service.converter;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Component
public class DeviceAddedEventConverter implements HubEventConverter {

    @Override
    public SpecificRecordBase convert(HubEventProto event) {
        DeviceAddedEventProto deviceAddedEvent = event.getDeviceAdded();
        return DeviceAddedEventAvro.newBuilder()
                .setId(deviceAddedEvent.getId())
                .setType(toAvro(deviceAddedEvent.getType()))
                .build();
    }

    @Override
    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    private DeviceTypeAvro toAvro(ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto deviceType) {
        return switch (deviceType) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            default -> throw new IllegalArgumentException("Unknown device type: " + deviceType);
        };
    }
}