package com.demo.mslu.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

/**
 * @author Yahor Svidzinski
 */
@SpringBootApplication
public class ScheduleApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(ScheduleApplication.class, args);
    }
}
