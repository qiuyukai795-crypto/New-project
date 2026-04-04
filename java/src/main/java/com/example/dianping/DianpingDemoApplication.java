package com.example.dianping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@MapperScan("com.example.dianping.mapper")
@SpringBootApplication
public class DianpingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DianpingDemoApplication.class, args);
    }
}
