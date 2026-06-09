package com.bookstore.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
        scanBasePackages = {"com.bookstore.ai", "com.bookstore.common"},
        exclude = {DataSourceAutoConfiguration.class}
)
@EnableFeignClients
public class BookstoreAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreAiApplication.class, args);
    }
}
