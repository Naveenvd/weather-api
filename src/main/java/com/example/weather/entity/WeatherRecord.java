package com.example.weather.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_records")
public class WeatherRecord {

    public WeatherRecord() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "datetime_utc")
    private LocalDateTime datetimeUtc;


    @Column(name = "year_number", nullable = true)
    private Integer year;

    @Column(name = "month_of_year", nullable = true)
    private Integer month;

    @Column(name = "day_of_month", nullable = true)
    private Integer day;

    @Column(name = "cond")
    private String cond;

    @Column(name = "humid")
    private Integer humid;

    @Column(name = "temp")
    private Double temp;

    @Column(name = "pressure")
    private Double pressure;


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public LocalDateTime getDatetimeUtc() { return datetimeUtc; }
    public void setDatetimeUtc(LocalDateTime datetimeUtc) { this.datetimeUtc = datetimeUtc; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public String getCond() { return cond; }
    public void setCond(String cond) { this.cond = cond; }

    public Integer getHumid() { return humid; }
    public void setHumid(Integer humid) { this.humid = humid; }

    public Double getTemp() { return temp; }
    public void setTemp(Double temp) { this.temp = temp; }

    public Double getPressure() { return pressure; }
    public void setPressure(Double pressure) { this.pressure = pressure; }
}
