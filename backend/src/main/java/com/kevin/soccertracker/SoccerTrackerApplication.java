package com.kevin.soccertracker;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableScheduling
@SpringBootApplication
public class SoccerTrackerApplication {

    public static void main(String[] args) {

        SpringApplication.run(SoccerTrackerApplication.class, args);
    }

}
