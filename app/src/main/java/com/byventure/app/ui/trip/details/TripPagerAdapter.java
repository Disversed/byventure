package com.byventure.app.ui.trip.details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.byventure.app.ui.trip.budget.TripBudgetFragment;

public class TripPagerAdapter extends FragmentStateAdapter {

    public TripPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // return new RouteFragment(); // Заглушка для "Маршрут"
                return new TripBudgetFragment(); // Пока вернем BudgetFragment для теста
            case 1:
                return new TripBudgetFragment(); // Вкладка "Бюджет"
            case 2:
                // return new EventsFragment(); // Заглушка для "События"
                return new TripBudgetFragment();
            case 3:
                // return new TicketsFragment(); // Заглушка для "Билеты"
                return new TripTicketsAndBooksFragment();
            default:
                return new TripBudgetFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Всего 4 вкладки: Маршрут, Бюджет, События, Билеты
    }
}