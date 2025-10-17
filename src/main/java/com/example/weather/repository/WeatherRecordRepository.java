package com.example.weather.repository;

import com.example.weather.entity.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord,Long>
{
    List<WeatherRecord> findByMonth(int month);
    List<WeatherRecord> findByYearAndMonth(int year,int month);
    List<WeatherRecord> findByDayAndMonth(int day, int month);
}
