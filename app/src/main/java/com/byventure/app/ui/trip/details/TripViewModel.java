package com.byventure.app.ui.trip.details;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.byventure.app.ui.trip.budget.BudgetCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TripViewModel extends ViewModel {

    public static final int MAX_TRIP_DAYS = 60;
    public static final int MAX_TITLE_LENGTH = 60;
    public static final int MAX_ADDRESS_LENGTH = 120;
    public static final BigDecimal MAX_BUDGET = new BigDecimal("100000000");

    private final MutableLiveData<LocalDate> startDate = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> endDate = new MutableLiveData<>();
    private final MutableLiveData<List<TripEntry>> entries =
            new MutableLiveData<>(Collections.<TripEntry>emptyList());
    /** Список для RecyclerView: заголовок дня, записи, кнопка «Добавить». */
    private final MediatorLiveData<List<TripRow>> rows = new MediatorLiveData<>();

    @Nullable private BigDecimal budget;
    private final Set<BudgetCategory> selectedCategories = EnumSet.noneOf(BudgetCategory.class);

    public TripViewModel() {
        rows.addSource(startDate, v -> rebuildRows());
        rows.addSource(endDate, v -> rebuildRows());
        rows.addSource(entries, v -> rebuildRows());

        // Загружаем тестовые данные для демонстрации интерфейса
        loadMockData();
    }

    /** Метод для заполнения ViewModel логичными тестовыми данными */
    public void loadMockData() {
        // 1. Устанавливаем даты поездки (например, на 3 дня вперед начиная с сегодняшнего)
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);
        startDate.setValue(start);
        endDate.setValue(end);

        // 2. Устанавливаем тестовый бюджет и выбираем все категории
        budget = new BigDecimal("75000");
        selectedCategories.addAll(EnumSet.allOf(BudgetCategory.class));

        // 3. Создаем тестовые записи (билеты и брони)
        List<TripEntry> mockEntries = new ArrayList<>();

        // Билет на поезд/самолет в первый день
        mockEntries.add(TripEntry.transportTicket(
                start,
                "Авиабилет Минск — Москва",
                BudgetCategory.TICKETS,
                "Аэропорт Минск-2", LocalTime.of(8, 30),
                "Аэропорт Шереметьево", LocalTime.of(10, 0)
        ));

        // Бронь отеля в первый день
        mockEntries.add(TripEntry.booking(
                start,
                "Отель «Cosmos»",
                BudgetCategory.HOUSING,
                "Проспект Мира, 150", LocalTime.of(14, 0)
        ));

        // Событие/развлечение на второй день
        mockEntries.add(TripEntry.booking(
                start.plusDays(1),
                "Экскурсия в Третьяковскую галерею",
                BudgetCategory.ENTERTAINMENT,
                "Лаврушинский переулок, 10", LocalTime.of(11, 0)
        ));

        // Обед на второй день
        mockEntries.add(TripEntry.booking(
                start.plusDays(1),
                "Обед в ресторане",
                BudgetCategory.FOOD,
                "Улица Тверская, 5", LocalTime.of(14, 30)
        ));

        // Записываем данные в LiveData
        entries.setValue(Collections.unmodifiableList(mockEntries));
    }

    // даты
    public LiveData<LocalDate> getStartDate() { return startDate; }
    public LiveData<LocalDate> getEndDate() { return endDate; }
    public void setStartDate(@Nullable LocalDate d) { startDate.setValue(d); }
    public void setEndDate(@Nullable LocalDate d) { endDate.setValue(d); }

    // бюджет
    @Nullable public BigDecimal getBudgetValue() { return budget; }
    public Set<BudgetCategory> getSelectedCategoriesValue() { return EnumSet.copyOf(selectedCategories.isEmpty()
            ? EnumSet.noneOf(BudgetCategory.class) : selectedCategories); }
    public List<BudgetCategory> getSelectedCategoryList() { return new ArrayList<>(selectedCategories); }

    public void setBudget(BigDecimal value, Set<BudgetCategory> categories) {
        budget = value;
        selectedCategories.clear();
        selectedCategories.addAll(categories);

        // записи с категорией, которую сняли, удаляются
        List<TripEntry> current = entries.getValue();
        if (current == null) return;
        List<TripEntry> kept = new ArrayList<>();
        for (TripEntry e : current) {
            if (selectedCategories.contains(e.getCategory())) kept.add(e);
        }
        if (kept.size() != current.size()) entries.setValue(Collections.unmodifiableList(kept));
    }

    // брони и билеты
    public LiveData<List<TripEntry>> getEntries() { return entries; }
    public LiveData<List<TripRow>> getRows() { return rows; }

    public void addEntry(TripEntry entry) {
        List<TripEntry> list = new ArrayList<>(entries.getValue());
        list.add(entry);
        Collections.sort(list, TripEntry.COMPARATOR);
        entries.setValue(Collections.unmodifiableList(list));
    }

    /** Начало нового создания маршрута. */
    public void reset() {
        budget = null;
        selectedCategories.clear();
        entries.setValue(Collections.<TripEntry>emptyList());
        startDate.setValue(null);
        endDate.setValue(null);
    }

    private void rebuildRows() {
        LocalDate s = startDate.getValue();
        LocalDate e = endDate.getValue();
        List<TripRow> result = new ArrayList<>();
        if (s != null && e != null) {
            List<TripEntry> all = entries.getValue();
            for (LocalDate d = s; !d.isAfter(e); d = d.plusDays(1)) {
                result.add(new TripRow.DateHeader(d));
                for (TripEntry entry : all) {
                    if (entry.getDate().equals(d)) result.add(new TripRow.Entry(entry));
                }
                result.add(new TripRow.AddButton(d));
            }
        }
        rows.setValue(result);
    }
}