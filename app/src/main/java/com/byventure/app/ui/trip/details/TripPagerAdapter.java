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
                return new TripSummaryFragment();
            case 1:
                return new TripBudgetFragment();
            case 2:
                return new TripEventsFragment();
            case 3:
                return new TripTicketsAndBooksFragment();
            default:
                return new TripSummaryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}