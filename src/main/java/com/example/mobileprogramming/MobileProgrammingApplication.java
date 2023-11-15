package com.example.mobileprogramming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MobileProgrammingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MobileProgrammingApplication.class, args);
    }

}
