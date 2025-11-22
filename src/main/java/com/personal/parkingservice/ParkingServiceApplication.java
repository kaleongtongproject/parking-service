package com.personal.parkingservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.personal.parkingservice.service.ParkingService;

@SpringBootApplication
public class ParkingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(
            StringRedisTemplate redisTemplate,
            @Value("${parking.initial-spots:30}") int initialSpots) {
        return args -> {
            String key = ParkingService.AVAILABLE_SPOTS_KEY;

            try {
                Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(initialSpots));
                if (Boolean.TRUE.equals(wasSet)) {
                    System.out.println("Initialized " + key + " = " + initialSpots);
                } else {
                    String current = redisTemplate.opsForValue().get(key);
                    System.out.println(key + " already exists = " + current);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to connect to Redis: " + e.getMessage());
                throw e;
            }
        };
    }

}