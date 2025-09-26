package com.example.weather.controller;

import com.example.weather.dto.MonthTempStat;
import com.example.weather.dto.WeatherDto;
import com.example.weather.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController
{

    private final WeatherService service;

    public WeatherController(WeatherService service)
    {
        this.service=service;
    }

    @GetMapping("/by-month")
    public List<WeatherDto> byMonth(@RequestParam int month)
    {
        return service.getByMonth(month);
    }

    @GetMapping("/by-date")
    public List<WeatherDto> byDate(@RequestParam int day,@RequestParam int month)
    {
        return service.getByDayAndMonth(day,month);
    }

    @GetMapping("/stats/{year}")
    public List<MonthTempStat> statsForYear(@PathVariable int year)
    {
        return service.statsForYear(year);
    }


}
