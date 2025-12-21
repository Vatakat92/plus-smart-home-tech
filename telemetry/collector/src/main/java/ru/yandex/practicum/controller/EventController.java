package ru.yandex.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.kafka.KafkaEventSender;
import ru.yandex.practicum.model.hubevent.HubEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final KafkaEventSender kafkaEventSender;
    private final ObjectMapper objectMapper;

    public EventController(KafkaEventSender kafkaEventSender, ObjectMapper objectMapper) {
        this.kafkaEventSender = kafkaEventSender;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sensors")
    public ResponseEntity<Void> collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        try {
            String json = objectMapper.writeValueAsString(sensorEvent);
            log.info("Received sensor event: {}", json);

            kafkaEventSender.send(sensorEvent);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize sensor event to JSON: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            log.error("Error processing sensor event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/hubs")
    public ResponseEntity<Void> collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        try {
            String json = objectMapper.writeValueAsString(hubEvent);
            log.info("Received hub event: {}", json);

            kafkaEventSender.send(hubEvent);
            return ResponseEntity.ok().build();
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize hub event to JSON: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            log.error("Error processing hub event: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
}
