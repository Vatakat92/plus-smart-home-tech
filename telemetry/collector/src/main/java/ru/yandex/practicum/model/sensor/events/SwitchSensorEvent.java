package ru.yandex.practicum.model.sensor.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchSensorEvent extends SensorEvent {
    private boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
