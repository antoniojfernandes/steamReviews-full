package com.steam.reviews.steamreviews.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Utils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static String formatTimestamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
                .format(DATE_TIME_FORMATTER);
    }

    public static Double parsePlaytime(String playtime) {
        if (playtime == null) {
            return null;
        }
        return Math.round(Double.parseDouble(playtime) / 6) / 10.0;
    }

    public static LocalDate timeStampToLocalDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDate.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    public static LocalDate stringToDate(String dateAsString) {
        return LocalDate.parse(dateAsString, LOCAL_DATE_FORMATTER);
    }


}
