package com.byventure.app.ui.trip.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentTripBudgetBinding;
import com.byventure.app.ui.dialogs.AddReceiptSheetFragment;

import java.util.List;

public class TripBudgetFragment extends Fragment {

    private FragmentTripBudgetBinding binding;
    private TripBudgetViewModel viewModel;

    public static TripBudgetFragment newInstance() {
        return new TripBudgetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Инициализация ViewBinding
        binding = FragmentTripBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TripBudgetViewModel.class);

        setupRecyclerView();
        setupModeSwitcher();
        initObservers();

    }

    private void setupRecyclerView() {
        binding.rvBudgetCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        // TODO: Создать и подключить CategoryBudgetAdapter для binding.rvBudgetCategories
    }

    private void setupModeSwitcher() {
        // Обработка нажатий на сегментированные кнопки («Весь» / «Сегодня»)
        binding.toggleGroupBudget.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnTabAll) {
                    viewModel.setMode(TripBudgetViewModel.BudgetMode.ALL);
                } else if (checkedId == R.id.btnTabToday) {
                    viewModel.setMode(TripBudgetViewModel.BudgetMode.TODAY);
                }
            }
        });
    }

    private void initObservers() {
        // Наблюдаем за изменением финансовых показателей
        viewModel.getBudgetSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary != null) {
                updateBudgetUI(summary.planned, summary.spent, summary.remaining);
            }
        });

        // Наблюдаем за списком категорий трат
        viewModel.getCategoriesList().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                updateCategoriesAdapterStub(categories);
            }
        });

        // Синхронизация состояния кнопок при программном изменении режима
        viewModel.getCurrentMode().observe(getViewLifecycleOwner(), mode -> {
            if (mode == TripBudgetViewModel.BudgetMode.ALL) {
                binding.toggleGroupBudget.check(R.id.btnTabAll);
            } else {
                binding.toggleGroupBudget.check(R.id.btnTabToday);
            }
        });
    }

    private void updateBudgetUI(int planned, int spent, int remaining) {
        binding.tvBudgetPlanned.setText(getString(R.string.bel_rub, planned));
        binding.tvBudgetSpent.setText(getString(R.string.bel_rub, spent));
        binding.tvBudgetRemaining.setText(getString(R.string.bel_rub, remaining));
    }

    private void updateCategoriesAdapterStub(List<TripBudgetViewModel.CategoryExpenseModel> categories) {
        // TODO: Передать список в адаптер RecyclerView
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Предотвращаем утечки памяти
    }
}