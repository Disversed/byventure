package com.byventure.app.ui.trip.new_trip;

import java.time.LocalDate;

public abstract class TripRow {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ENTRY = 1;
    public static final int TYPE_ADD = 2;

    public abstract int getType();
    public abstract String getKey();

    public static final class DateHeader extends TripRow {
        public final LocalDate date;
        public DateHeader(LocalDate date) { this.date = date; }
        @Override public int getType() { return TYPE_HEADER; }
        @Override public String getKey() { return "header_" + date; }
    }

    public static final class Entry extends TripRow {
        public final TripEntry entry;
        public Entry(TripEntry entry) { this.entry = entry; }
        @Override public int getType() { return TYPE_ENTRY; }
        @Override public String getKey() { return "entry_" + entry.getId(); }
    }

    public static final class AddButton extends TripRow {
        public final LocalDate date;
        public AddButton(LocalDate date) { this.date = date; }
        @Override public int getType() { return TYPE_ADD; }
        @Override public String getKey() { return "add_" + date; }
    }
}