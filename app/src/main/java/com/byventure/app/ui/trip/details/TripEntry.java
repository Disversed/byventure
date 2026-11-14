package com.byventure.app.ui.trip.details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byventure.app.ui.trip.budget.BudgetCategory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.UUID;

/** Бронь или билет на транспорт. Для брони toAddress/toTime == null. Объект неизменяемый. */
public final class TripEntry {

    public enum Type { BOOKING, TRANSPORT_TICKET }

    /** Сортировка: дата -> время начала -> id (для стабильности). */
    public static final Comparator<TripEntry> COMPARATOR =
            Comparator.comparing(TripEntry::getDate)
                    .thenComparing(TripEntry::getFromTime)
                    .thenComparing(TripEntry::getId);

    private final String id;
    private final Type type;
    private final LocalDate date;
    private final String title;
    private final BudgetCategory category;
    private final String fromAddress;
    private final LocalTime fromTime;
    @Nullable private final String toAddress;
    @Nullable private final LocalTime toTime;

    private TripEntry(Type type, LocalDate date, String title, BudgetCategory category,
                      String fromAddress, LocalTime fromTime,
                      @Nullable String toAddress, @Nullable LocalTime toTime) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.date = date;
        this.title = title;
        this.category = category;
        this.fromAddress = fromAddress;
        this.fromTime = fromTime;
        this.toAddress = toAddress;
        this.toTime = toTime;
    }

    public static TripEntry booking(LocalDate date, String title, BudgetCategory category,
                                    String address, LocalTime time) {
        return new TripEntry(Type.BOOKING, date, title, category, address, time, null, null);
    }

    public static TripEntry transportTicket(LocalDate date, String title, BudgetCategory category,
                                            String from, LocalTime fromTime,
                                            String to, LocalTime toTime) {
        return new TripEntry(Type.TRANSPORT_TICKET, date, title, category, from, fromTime, to, toTime);
    }

    @NonNull public String getId() { return id; }
    @NonNull public Type getType() { return type; }
    @NonNull public LocalDate getDate() { return date; }
    @NonNull public String getTitle() { return title; }
    @NonNull public BudgetCategory getCategory() { return category; }
    @NonNull public String getFromAddress() { return fromAddress; }
    @NonNull public LocalTime getFromTime() { return fromTime; }
    @Nullable public String getToAddress() { return toAddress; }
    @Nullable public LocalTime getToTime() { return toTime; }
    public boolean isTransport() { return type == Type.TRANSPORT_TICKET; }
}