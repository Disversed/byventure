package com.byventure.app.ui.trip.details;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byventure.app.databinding.FragmentTripDetailBinding;
import com.byventure.app.ui.trip.details.dialogs.ChangeBudgetSheetFragment;
import com.google.android.material.tabs.TabLayoutMediator;

public class TripDetailFragment extends Fragment {

    private FragmentTripDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTripDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TripPagerAdapter pagerAdapter = new TripPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        String[] titles = new String[]{"Маршрут", "Бюджет", "События", "Билеты"};

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(titles[position]);
        }).attach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}