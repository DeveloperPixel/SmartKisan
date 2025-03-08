package com.example.optimizer.nointernet;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataParser {

    public static List<WeatherEntry> parseWeatherData(StringBuilder sb) {
        List<WeatherEntry> entries = new ArrayList<>();
        String[] sections = sb.toString().split("\n\n");

        for (String section : sections) {
            String[] lines = section.split("\n");
            if (lines.length >= 3) {
                String date = lines[0].split(": ")[1];
                String weather = lines[1].split(": ")[1];
                String rain = lines[2].split(": ")[1];
                entries.add(new WeatherEntry(date, weather, rain));
            }
        }
        return entries;
    }

    public static class WeatherEntry {
        public String date;
        public String weather;
        public String rain;

        public WeatherEntry(String date, String weather, String rain) {
            this.date = date;
            this.weather = weather;
            this.rain = rain;
        }
    }
}

