package com.cth.tradefinance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CthTradeFinanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CthTradeFinanceApplication.class, args);
    }
}
