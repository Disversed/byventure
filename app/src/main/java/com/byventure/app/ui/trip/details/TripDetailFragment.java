package com.byventure.app.ui.trip.details;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.byventure.app.databinding.FragmentTripDetailBinding;
import com.byventure.app.ui.dialogs.AddReceiptSheetFragment;
import com.byventure.app.ui.dialogs.ChangeBudgetSheetFragment;
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

        // 1. Устанавливаем адаптер для ViewPager2
        TripPagerAdapter pagerAdapter = new TripPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        // 2. Связываем TabLayout и ViewPager2 через TabLayoutMediator (задаем заголовки табов)
        String[] titles = new String[]{"Маршрут", "Бюджет", "События", "Билеты"};

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(titles[position]);
        }).attach();

        setupBottomBarForCurrentTab(0); // Устанавливаем состояние для первой вкладки по умолчанию
        // Слушаем смену вкладок в ViewPager2, чтобы менять текст и действие нижней кнопки под активный раздел
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setupBottomBarForCurrentTab(position);
            }
        });
    }

    private void setupBottomBarForCurrentTab(int position) {
        switch (position) {
            case 0: // Маршрут
                binding.bottomBar.btnBottomAction.setText("Добавить точку маршрута");
                binding.bottomBar.btnBottomAction.setOnClickListener(v -> {
                    // TODO: Действие для Маршрута
                });
                break;

            case 1: // Бюджет
                binding.bottomBar.btnBottomAction.setText("Настроить бюджет");
                binding.bottomBar.btnBottomAction.setOnClickListener(v -> {
                    ChangeBudgetSheetFragment bottomSheet = new ChangeBudgetSheetFragment();
                    bottomSheet.show(getParentFragmentManager(), "ChangeBudgetSheet");
                });
                break;

            case 2: // События
                binding.bottomBar.btnBottomAction.setText("Добавить событие");
                binding.bottomBar.btnBottomAction.setOnClickListener(v -> {
                    // TODO: Действие для Событий
                });
                break;

            case 3: // Билеты
                binding.bottomBar.btnBottomAction.setText("Добавить билет");
                binding.bottomBar.btnBottomAction.setOnClickListener(v -> {
                    // TODO: Действие для Билетов
                });
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}