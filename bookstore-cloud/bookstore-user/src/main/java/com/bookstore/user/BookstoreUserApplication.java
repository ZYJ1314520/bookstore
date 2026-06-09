package com.bookstore.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.bookstore.user", "com.bookstore.common", "com.bookstore.utils"})
public class BookstoreUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookstoreUserApplication.class, args);
    }
}
