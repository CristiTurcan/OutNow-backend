package com.example.outnowbackend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutNowBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OutNowBackendApplication.class, args);
    }

}
