package com.example.weather.service;

import com.example.weather.dto.MonthTempStat;
import com.example.weather.dto.WeatherDto;
import com.example.weather.entity.WeatherRecord;
import com.example.weather.repository.WeatherRecordRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService {

    private final WeatherRecordRepository repo;

    public WeatherService(WeatherRecordRepository repo) {
        this.repo = repo;
    }

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm");

    public void loadFromCsv(String csvpath) {
        if (csvpath == null || csvpath.isBlank()) {
            System.out.println("No CSV path configured, skipping import");
            return;
        }

        BufferedReader br = null;
        InputStream is = null;
        try {
            // 1.here i check with  classpath: explicit
            if (csvpath.startsWith("classpath:")) {
                String cp = csvpath.substring("classpath:".length());
                Resource res = new ClassPathResource(cp);
                if (!res.exists()) {
                    System.out.println("CSV not found on classpath: " + cp);
                    return;
                }
                is = res.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
            } else {
                Path p = Path.of(csvpath);
                if (Files.exists(p)) {
                    br = Files.newBufferedReader(p);
                } else {
                    //2.here i check with full class path for existance of csv file
                    Resource res = new ClassPathResource(csvpath);
                    if (res.exists()) {
                        is = res.getInputStream();
                        br = new BufferedReader(new InputStreamReader(is));
                    } else {
                        System.out.println("file not found - skipping import (tried filesystem and classpath): " + csvpath);
                        return;
                    }
                }
            }

            repo.deleteAll();
            String header = br.readLine();
            if (header == null) {
                System.out.println("csv file is empty");
                return;
            }

            String[] cols = header.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            Map<String, Integer> idx = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                idx.put(cols[i].trim(), i);
            }

            String line;
            List<WeatherRecord> buffer = new ArrayList<>(1000);
            int batch = 0;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                try {
                    Integer dtIdx = idx.get("datetime_utc");
                    if (dtIdx == null) {
                        continue;
                    }

                    String datetimeraw = safeGet(parts, dtIdx);
                    if (datetimeraw == null || datetimeraw.isBlank()) {
                        continue;
                    }

                    LocalDateTime dt;
                    try {
                        dt = LocalDateTime.parse(datetimeraw, dtf);
                    } catch (Exception pe) {
                        continue;
                    }

                    WeatherRecord wr = new WeatherRecord();
                    wr.setDatetimeUtc(dt);
                    wr.setYear(dt.getYear());
                    wr.setMonth(dt.getMonthValue());
                    wr.setDay(dt.getDayOfMonth());

                    String cond = safeGet(parts, idx.get("_conds"));
                    wr.setCond(emptyToNull(cond));

                    String tempraw = safeGet(parts, idx.get("_tempm"));
                    wr.setTemp(parseDoubleOrNull(tempraw));

                    String humraw = safeGet(parts, idx.get("_hum"));
                    wr.setHumid(parseIntOrNull(humraw));

                    String pressureraw = safeGet(parts, idx.get("_pressurem"));
                    wr.setPressure(parseDoubleOrNull(pressureraw));

                    buffer.add(wr);
                } catch (Exception e) {
                    System.out.println("skipping malformed CSV line: " + e.getMessage());
                }

                if (++batch % 1000 == 0) {
                    repo.saveAll(buffer);
                    buffer.clear();
                }
            }

            if (!buffer.isEmpty()) {
                repo.saveAll(buffer);
            }

            System.out.println("Import finished");
        } catch (Exception e) {
            System.out.println("Failed to import csv: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (br != null) br.close(); } catch (Exception ignored) {}
            try { if (is != null) is.close(); } catch (Exception ignored) {}
        }
    }
    public List<WeatherDto> getByMonth(int month) {
        return repo.findByMonth(month).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<WeatherDto> getByDayAndMonth(int day, int month) {
        return repo.findByDayAndMonth(day, month).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MonthTempStat> statsForYear(int year) {
        List<MonthTempStat> stats = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            List<Double> temps = repo.findByYearAndMonth(year, m).stream()
                    .map(WeatherRecord::getTemp)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            if (temps.isEmpty()) {
                stats.add(new MonthTempStat(m, null, null, null));
            } else {
                Double min = temps.get(0);
                Double max = temps.get(temps.size() - 1);
                Double median = computeMedian(temps);
                stats.add(new MonthTempStat(m, min, median, max));
            }
        }
        return stats;
    }

    private WeatherDto toDto(WeatherRecord r) {
        return new WeatherDto(
                r.getHumid(),
                r.getCond(),
                r.getDatetimeUtc() != null ? r.getDatetimeUtc().toString() : null,
                r.getPressure(),
                r.getTemp()
        );
    }


    private static String safeGet(String[] arr, Integer idx) {
        if (idx == null) return null;
        if (idx < 0 || idx >= arr.length) return null;
        String val = arr[idx];
        if (val == null) return null;
        return val.trim();
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        if (s.equals("-9999")) return null;
        return s;
    }

    private static Double parseDoubleOrNull(String s) {
        s = emptyToNull(s);
        if (s == null) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseIntOrNull(String s) {
        s = emptyToNull(s);
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Double computeMedian(List<Double> list) {
        int n = list.size();
        if (n == 0) return null;
        if (n % 2 == 1) {
            return list.get(n / 2);
        } else {
            return (list.get(n / 2 - 1) + list.get(n / 2)) / 2.0;
        }
    }
}