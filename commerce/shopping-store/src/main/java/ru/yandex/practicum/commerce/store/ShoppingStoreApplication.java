package ru.yandex.practicum.commerce.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "ru.yandex.practicum.commerce.store")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.practicum.commerce.feign")
public class ShoppingStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingStoreApplication.class, args);
    }
}