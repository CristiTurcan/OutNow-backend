package com.example.outnowbackend;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OutNowBackendApplication {

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL Driver loaded manually before Spring Boot starts.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL Driver NOT FOUND in static block: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(OutNowBackendApplication.class, args);
    }

}
