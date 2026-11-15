package com.byventure.app.ui.trip.new_trip.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/** Строка экрана «Итоговый маршрут». */
public abstract class SummaryRow {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_POINT = 1;

    public abstract int getType();
    public abstract String getKey();

    public static final class DateHeader extends SummaryRow {
        public final LocalDate date;
        public DateHeader(LocalDate date) { this.date = date; }
        @Override public int getType() { return TYPE_HEADER; }
        @Override public String getKey() { return "header_" + date; }
    }

    /** Точка маршрута. first/last нужны, чтобы нарисовать скругления карточки и линию таймлайна. */
    public static final class Point extends SummaryRow {
        public final String id;
        public final LocalTime time;
        public final String address;
        public final boolean first;
        public final boolean last;

        public Point(String id, LocalTime time, String address, boolean first, boolean last) {
            this.id = id;
            this.time = time;
            this.address = address;
            this.first = first;
            this.last = last;
        }

        @Override public int getType() { return TYPE_POINT; }
        @Override public String getKey() { return "point_" + id; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point p = (Point) o;
            return id.equals(p.id) && time.equals(p.time) && address.equals(p.address)
                    && first == p.first && last == p.last;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, time, address, first, last);
        }
    }
}