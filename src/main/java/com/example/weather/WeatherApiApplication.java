package com.example.weather;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication
public class WeatherApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherApiApplication.class, args);
    }

    // On startup, load CSV into DB. Path comes from application.properties
    @Bean
    CommandLineRunner init(WeatherService weatherService, @Value("${weather.csv.path}") String csvPath) {
        return args -> {
            weatherService.loadFromCsv(csvPath);
        };
    }
}