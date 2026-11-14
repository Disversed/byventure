package com.byventure.app.ui.trip.details;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TripFormats {
    private static final Locale RU = new Locale("ru");
    public static final DateTimeFormatter DAY_HEADER = DateTimeFormatter.ofPattern("d MMMM", RU);
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy", RU);
    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", RU);

    private TripFormats() {}

    /** MaterialDatePicker работает с UTC-полуночью. */
    public static long toPickerMillis(LocalDate d) {
        return d.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static LocalDate fromPickerMillis(long ms) {
        return Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate();
    }
}