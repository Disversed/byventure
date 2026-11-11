package com.byventure.app.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<String> cityName = new MutableLiveData<>("Брест");
    private final MutableLiveData<BudgetModel> budgetState = new MutableLiveData<>();
    private final MutableLiveData<RouteItemModel> nextRouteItem = new MutableLiveData<>();

    public MainViewModel() {
        loadMainData();
    }

    public LiveData<String> getCityName() {
        return cityName;
    }

    public LiveData<BudgetModel> getBudgetState() {
        return budgetState;
    }

    public LiveData<RouteItemModel> getNextRouteItem() {
        return nextRouteItem;
    }

    /**
     * Метод-заглушка: здесь в будущем будет запрос в базу данных или на сервер
     */
    public void loadMainData() {
        // TODO: Дописать логику запроса активного путешествия и финансов

        // Эмулируем данные для примера:
        budgetState.setValue(new BudgetModel(150, 100, 50));
        nextRouteItem.setValue(new RouteItemModel("Bloom cafe", "11:00", "Брестская крепость", "12:20"));
    }

    public static class BudgetModel {
        public int planned;
        public int spent;
        public int remaining;

        public BudgetModel(int planned, int spent, int remaining) {
            this.planned = planned;
            this.spent = spent;
            this.remaining = remaining;
        }
    }

    public static class RouteItemModel {
        public String currentTitle;
        public String currentTime;
        public String nextTitle;
        public String nextTime;

        public RouteItemModel(String currentTitle, String currentTime, String nextTitle, String nextTime) {
            this.currentTitle = currentTitle;
            this.currentTime = currentTime;
            this.nextTitle = nextTitle;
            this.nextTime = nextTime;
        }
    }

}