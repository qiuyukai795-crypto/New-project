package com.example.dianping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DianpingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DianpingDemoApplication.class, args);
    }
}
