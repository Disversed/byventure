package com.byventure.app.ui.trip.new_trip.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

/** Событие дня (заголовок, время, адрес). Объект неизменяемый. */
public final class TripEvent {

    public static final Comparator<TripEvent> COMPARATOR =
            Comparator.comparing(TripEvent::getDate)
                    .thenComparing(TripEvent::getTime)
                    .thenComparing(TripEvent::getId);

    private final String id;
    private final LocalDate date;
    private final String title;
    private final String address;
    private final LocalTime time;

    private TripEvent(@Nullable String id, LocalDate date, String title, String address, LocalTime time) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.date = date;
        this.title = title;
        this.address = address;
        this.time = time;
    }

    /** id == null -> будет сгенерирован новый. */
    public static TripEvent create(@Nullable String id, LocalDate date, String title,
                                   String address, LocalTime time) {
        return new TripEvent(id, date, title, address, time);
    }

    @NonNull public String getId() { return id; }
    @NonNull public LocalDate getDate() { return date; }
    @NonNull public String getTitle() { return title; }
    @NonNull public String getAddress() { return address; }
    @NonNull public LocalTime getTime() { return time; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripEvent)) return false;
        TripEvent e = (TripEvent) o;
        return id.equals(e.id) && date.equals(e.date) && title.equals(e.title)
                && address.equals(e.address) && time.equals(e.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, title, address, time);
    }
}