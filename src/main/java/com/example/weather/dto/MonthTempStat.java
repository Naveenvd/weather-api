package com.example.weather.dto;

public class MonthTempStat
{
    private int month;
    private Double min;
    private Double median;
    private Double max;

    public MonthTempStat()
    {

    }
    public MonthTempStat(int month, Double min, Double median, Double max) {
        this.month = month;
        this.min = min;
        this.median = median;
        this.max = max;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMedian() {
        return median;
    }

    public void setMedian(Double median) {
        this.median = median;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
