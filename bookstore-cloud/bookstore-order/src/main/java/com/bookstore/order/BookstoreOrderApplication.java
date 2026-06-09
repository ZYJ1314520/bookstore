package com.bookstore.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.bookstore.order", "com.bookstore.common", "com.bookstore.utils"})
public class BookstoreOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreOrderApplication.class, args);
    }
}
