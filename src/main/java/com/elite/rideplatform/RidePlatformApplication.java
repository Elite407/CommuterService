package com.elite.rideplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RidePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RidePlatformApplication.class, args);
    }

}
