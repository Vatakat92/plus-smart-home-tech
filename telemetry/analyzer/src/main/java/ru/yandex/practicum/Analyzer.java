package ru.yandex.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(Analyzer.class, args);

        HubEventProcessor hubEventProcessor =
                context.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor =
                context.getBean(SnapshotProcessor.class);

        Thread hubEventsThread = new Thread(hubEventProcessor);
        hubEventsThread.setName("HubEventProcessor-Thread");

        Thread snapshotThread = new Thread(snapshotProcessor);
        snapshotThread.setName("SnapshotProcessor-Thread");

        hubEventsThread.start();
        snapshotThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown signal received. Stopping processors...");

            hubEventProcessor.shutdown();
            snapshotProcessor.shutdown();

            try {
                hubEventsThread.join(2000);
                snapshotThread.join(2000);
            } catch (InterruptedException e) {
                log.warn("Shutdown interrupted", e);
                Thread.currentThread().interrupt();
            }

            log.info("Analyzer shut down cleanly.");
        }));
    }
}
