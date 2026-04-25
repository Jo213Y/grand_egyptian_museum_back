package com.gem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GemApplication {
    public static void main(String[] args) {
        SpringApplication.run(GemApplication.class, args);
        System.out.println("\n✅ Grand Egyptian Museum API started → http://localhost:8080\n");
    }
}
