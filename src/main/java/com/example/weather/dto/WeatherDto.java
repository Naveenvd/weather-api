package com.example.weather.dto;

public class WeatherDto
{
    private Integer humidity;
    private String condition;
    private String datetime;
    private Double pressure;
    private Double temperature;

    public WeatherDto(Integer humidity, String condition, String datetime, Double pressure, Double temperature) {
        this.humidity = humidity;
        this.condition = condition;
        this.datetime = datetime;
        this.pressure = pressure;
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public WeatherDto(){};

}
