package com.byventure.app.ui.trip.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.Arrays;
import java.util.List;

public class TripBudgetViewModel extends ViewModel {

    public enum BudgetMode { ALL, TODAY }

    private final MutableLiveData<BudgetMode> currentMode = new MutableLiveData<>(BudgetMode.ALL);
    private final MutableLiveData<BudgetSummaryModel> budgetSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryExpenseModel>> categoriesList = new MutableLiveData<>();

    public TripBudgetViewModel() {
        loadDataForMode(BudgetMode.ALL);
    }

    public LiveData<BudgetMode> getCurrentMode() {
        return currentMode;
    }

    public LiveData<BudgetSummaryModel> getBudgetSummary() {
        return budgetSummary;
    }

    public LiveData<List<CategoryExpenseModel>> getCategoriesList() {
        return categoriesList;
    }

    public void setMode(BudgetMode mode) {
        currentMode.setValue(mode);
        loadDataForMode(mode);
    }

    /**
     * Метод-заглушка: запрос данных в зависимости от выбранного режима
     */
    private void loadDataForMode(BudgetMode mode) {
        // TODO: Заменить на реальный вызов репозитория / базы данных
        if (mode == BudgetMode.ALL) {
            budgetSummary.setValue(new BudgetSummaryModel(1000, 611, 389));
            categoriesList.setValue(Arrays.asList(
                    new CategoryExpenseModel("Развлечения", 150),
                    new CategoryExpenseModel("Еда", 233),
                    new CategoryExpenseModel("Билеты", 80),
                    new CategoryExpenseModel("Жилье", 170)
            ));
        } else {
            budgetSummary.setValue(new BudgetSummaryModel(150, 100, 50));
            categoriesList.setValue(Arrays.asList(
                    new CategoryExpenseModel("Еда", 40),
                    new CategoryExpenseModel("Развлечения", 40),
                    new CategoryExpenseModel("Билеты", 20)
            ));
        }
    }

    public void updateOverallBudget(int newTotal) {
        // TODO: Сохранение нового общего бюджета в репозиторий
        BudgetSummaryModel current = budgetSummary.getValue();
        if (current != null) {
            int remaining = newTotal - current.spent;
            budgetSummary.setValue(new BudgetSummaryModel(newTotal, current.spent, remaining));
        }
    }

    // Вспомогательные модели-заглушки
    public static class BudgetSummaryModel {
        public int planned;
        public int spent;
        public int remaining;

        public BudgetSummaryModel(int planned, int spent, int remaining) {
            this.planned = planned;
            this.spent = spent;
            this.remaining = remaining;
        }
    }

    public static class CategoryExpenseModel {
        public String name;
        public int spent;

        public CategoryExpenseModel(String name, int spent) {
            this.name = name;
            this.spent = spent;
        }
    }
}