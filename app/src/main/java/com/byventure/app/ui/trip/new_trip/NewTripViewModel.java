package com.byventure.app.ui.trip.new_trip;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.byventure.app.ui.trip.new_trip.model.BudgetCategory;
import com.byventure.app.ui.trip.new_trip.model.SummaryRow;
import com.byventure.app.ui.trip.new_trip.model.TripEntry;
import com.byventure.app.ui.trip.new_trip.model.TripEvent;
import com.byventure.app.ui.trip.new_trip.model.TripRow;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Единый ViewModel флоу создания маршрута. Живёт на уровне Activity. */
public class NewTripViewModel extends ViewModel {

    public static final int MAX_TRIP_DAYS = 60;
    public static final int MAX_TITLE_LENGTH = 60;
    public static final int MAX_ADDRESS_LENGTH = 120;
    public static final BigDecimal MAX_BUDGET = new BigDecimal("100000000");

    // ---- даты ----
    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> endDate = new MutableLiveData<>();

    // ---- бюджет ----
    @Nullable private BigDecimal budget;
    private final Set<BudgetCategory> selectedCategories = EnumSet.noneOf(BudgetCategory.class);

    // ---- брони и билеты ----
    private final MutableLiveData<List<TripEntry>> entries =
            new MutableLiveData<>(Collections.<TripEntry>emptyList());
    private final MediatorLiveData<List<TripRow>> rows = new MediatorLiveData<>();

    // ---- события ----
    private final MutableLiveData<List<TripEvent>> events =
            new MutableLiveData<>(Collections.<TripEvent>emptyList());
    private final MediatorLiveData<List<TripRow>> eventRows = new MediatorLiveData<>();

    // ---- итоговый маршрут ----
    private final MediatorLiveData<List<SummaryRow>> summaryRows = new MediatorLiveData<>();

    private interface DayContent {
        void addRowsFor(LocalDate day, List<TripRow> out);
    }

    public NewTripViewModel() {
        rows.addSource(startDate, v -> rebuildRows());
        rows.addSource(endDate, v -> rebuildRows());
        rows.addSource(entries, v -> rebuildRows());

        eventRows.addSource(startDate, v -> rebuildEventRows());
        eventRows.addSource(endDate, v -> rebuildEventRows());
        eventRows.addSource(events, v -> rebuildEventRows());

        summaryRows.addSource(events, v -> rebuildSummary());
    }

    // ================= даты =================
    public LiveData<LocalDate> getStartDate() { return startDate; }
    public LiveData<LocalDate> getEndDate() { return endDate; }
    public void setStartDate(@Nullable LocalDate d) { startDate.setValue(d); }
    public void setEndDate(@Nullable LocalDate d) { endDate.setValue(d); }

    // ================= бюджет =================
    @Nullable public BigDecimal getBudgetValue() { return budget; }

    public Set<BudgetCategory> getSelectedCategoriesValue() {
        Set<BudgetCategory> copy = EnumSet.noneOf(BudgetCategory.class);
        copy.addAll(selectedCategories);
        return copy;
    }

    public List<BudgetCategory> getSelectedCategoryList() { return new ArrayList<>(selectedCategories); }

    public void setBudget(BigDecimal value, Set<BudgetCategory> categories) {
        budget = value;
        selectedCategories.clear();
        selectedCategories.addAll(categories);

        // записи с категорией, которую сняли, удаляем
        List<TripEntry> current = entries.getValue();
        if (current == null) return;
        List<TripEntry> kept = new ArrayList<>();
        for (TripEntry e : current) {
            if (selectedCategories.contains(e.getCategory())) kept.add(e);
        }
        if (kept.size() != current.size()) entries.setValue(Collections.unmodifiableList(kept));
    }

    // ================= брони и билеты =================
    public LiveData<List<TripEntry>> getEntries() { return entries; }
    public LiveData<List<TripRow>> getRows() { return rows; }

    @Nullable
    public TripEntry findEntry(String id) {
        for (TripEntry e : entries.getValue()) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public void addEntry(TripEntry entry) {
        List<TripEntry> list = new ArrayList<>(entries.getValue());
        list.add(entry);
        Collections.sort(list, TripEntry.COMPARATOR);
        entries.setValue(Collections.unmodifiableList(list));
    }

    public void updateEntry(TripEntry updated) {
        List<TripEntry> list = new ArrayList<>();
        for (TripEntry e : entries.getValue()) {
            list.add(e.getId().equals(updated.getId()) ? updated : e);
        }
        Collections.sort(list, TripEntry.COMPARATOR);
        entries.setValue(Collections.unmodifiableList(list));
    }

    public void removeEntry(String id) {
        List<TripEntry> list = new ArrayList<>();
        for (TripEntry e : entries.getValue()) {
            if (!e.getId().equals(id)) list.add(e);
        }
        entries.setValue(Collections.unmodifiableList(list));
    }

    // ================= события =================
    public LiveData<List<TripEvent>> getEvents() { return events; }
    public LiveData<List<TripRow>> getEventRows() { return eventRows; }

    @Nullable
    public TripEvent findEvent(String id) {
        for (TripEvent e : events.getValue()) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public void addEvent(TripEvent event) {
        List<TripEvent> list = new ArrayList<>(events.getValue());
        list.add(event);
        Collections.sort(list, TripEvent.COMPARATOR);
        events.setValue(Collections.unmodifiableList(list));
    }

    public void updateEvent(TripEvent updated) {
        List<TripEvent> list = new ArrayList<>();
        for (TripEvent e : events.getValue()) {
            list.add(e.getId().equals(updated.getId()) ? updated : e);
        }
        Collections.sort(list, TripEvent.COMPARATOR);
        events.setValue(Collections.unmodifiableList(list));
    }

    public void removeEvent(String id) {
        List<TripEvent> list = new ArrayList<>();
        for (TripEvent e : events.getValue()) {
            if (!e.getId().equals(id)) list.add(e);
        }
        events.setValue(Collections.unmodifiableList(list));
    }

    // ================= итоговый маршрут =================
    public LiveData<List<SummaryRow>> getSummaryRows() { return summaryRows; }

    // ================= сброс =================
    /** Начало нового флоу создания маршрута. */
    public void reset() {
        budget = null;
        selectedCategories.clear();
        entries.setValue(Collections.<TripEntry>emptyList());
        events.setValue(Collections.<TripEvent>emptyList());
        startDate.setValue(null);
        endDate.setValue(null);
    }

    // ================= построение списков =================
    private void rebuildRows() {
        rows.setValue(buildDays((day, out) -> {
            for (TripEntry e : entries.getValue()) {
                if (e.getDate().equals(day)) out.add(new TripRow.Entry(e));
            }
        }));
    }

    private void rebuildEventRows() {
        eventRows.setValue(buildDays((day, out) -> {
            for (TripEvent e : events.getValue()) {
                if (e.getDate().equals(day)) out.add(new TripRow.EventItem(e));
            }
        }));
    }

    /** Для каждого дня поездки: заголовок, содержимое дня, кнопка «Добавить». */
    private List<TripRow> buildDays(DayContent content) {
        List<TripRow> result = new ArrayList<>();
        LocalDate s = startDate.getValue();
        LocalDate e = endDate.getValue();
        // защита от некорректного черновика дат (пока пользователь выбирает их в попапе)
        if (s == null || e == null || ChronoUnit.DAYS.between(s, e) >= MAX_TRIP_DAYS) return result;

        for (LocalDate d = s; !d.isAfter(e); d = d.plusDays(1)) {
            result.add(new TripRow.DateHeader(d));
            content.addRowsFor(d, result);
            result.add(new TripRow.AddButton(d));
        }
        return result;
    }

    /**
     * Итоговый маршрут: пока только события. Чтобы добавить брони и билеты, объедините их с events
     * в один список точек (дата, время, текст), отсортируйте и пройдите так же, как ниже.
     */
    private void rebuildSummary() {
        List<SummaryRow> result = new ArrayList<>();
        List<TripEvent> list = events.getValue(); // уже отсортирован по дате и времени
        int i = 0;
        while (i < list.size()) {
            LocalDate day = list.get(i).getDate();
            int j = i;
            while (j < list.size() && list.get(j).getDate().equals(day)) j++;

            result.add(new SummaryRow.DateHeader(day));
            for (int k = i; k < j; k++) {
                TripEvent ev = list.get(k);
                result.add(new SummaryRow.Point(ev.getId(), ev.getTime(), ev.getAddress(),
                        k == i, k == j - 1));
            }
            i = j;
        }
        summaryRows.setValue(result);
    }
}