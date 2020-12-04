package com.lokep.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lokep.mall.dao")
@SpringBootApplication
public class FashionMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(FashionMallApplication.class, args);
    }
}
