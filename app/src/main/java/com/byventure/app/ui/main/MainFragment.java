package com.byventure.app.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentMainBinding;
import com.byventure.app.ui.dialogs.AddReceiptSheetFragment;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initObservers();
        initListeners();
        setupTicketsAdapterStub();

        binding.fabAction.setOnClickListener(v -> {
            AddReceiptSheetFragment bottomSheet = new AddReceiptSheetFragment();
            bottomSheet.show(getParentFragmentManager(), "AddReceiptBottomSheet");
        });

    }

    /**
     * Подписка на изменения данных из ViewModel
     */
    private void initObservers() {
        // Наблюдаем за бюджетом
        viewModel.getBudgetState().observe(getViewLifecycleOwner(), budget -> {
            if (budget != null) {
                updateBudgetUI(budget.planned, budget.spent, budget.remaining);
            }
        });

        // Наблюдаем за маршрутом
        viewModel.getNextRouteItem().observe(getViewLifecycleOwner(), route -> {
            if (route != null) {
                updateRouteUI(route);
            }
        });

        // Здесь же можно подписаться на имя города для динамической смены заголовка в Toolbar
        viewModel.getCityName().observe(getViewLifecycleOwner(), city -> {
            updateToolbarTitleStub(city);
        });
    }

    /**
     * Заглушки для обработки кликов пользователя
     */
    private void initListeners() {
        // TODO: Обработка клика по карточке бюджета (например, переход на экран финансов)
        // binding.getRoot().findViewById(...).setOnClickListener(v -> { ... });
    }

    /**
     * Заглушка для инициализации RecyclerView с билетами
     */
    private void setupTicketsAdapterStub() {
        // TODO: Создать TicketAdapter и привязать к binding.rvTickets
    }


    private void updateBudgetUI(int planned, int spent, int remaining) {
        binding.tvBudgetPlanned.setText(getString(R.string.bel_rub, planned));
        binding.tvBudgetSpent.setText(getString(R.string.bel_rub, spent));
        binding.tvBudgetRemaining.setText(getString(R.string.bel_rub, remaining));
    }

    private void updateRouteUI(MainViewModel.RouteItemModel route) {
        binding.tvRouteCurrentTitle.setText(route.currentTitle);
        binding.tvRouteCurrentTime.setText(route.currentTime);
        binding.tvRouteNextTitle.setText(route.nextTitle);
        binding.tvRouteNextTime.setText(route.nextTime);
    }

    private void updateToolbarTitleStub(String cityName) {
        // Если MainActivity управляет общим Toolbar, здесь можно обновить его заголовок
        // Например: ((MainActivity) requireActivity()).setToolbarTitle(cityName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}