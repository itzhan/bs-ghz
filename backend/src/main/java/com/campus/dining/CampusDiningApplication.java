package com.campus.dining;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.campus.dining.mapper")
public class CampusDiningApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusDiningApplication.class, args);
    }
}
