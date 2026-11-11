package com.byventure.app.ui.trip.new_trip;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentNewTripBudgetBinding;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class NewTripBudgetFragment extends Fragment {

    private FragmentNewTripBudgetBinding binding;
    private NewTripViewModel vm;
    private final Map<BudgetCategory, CheckBox> checks = new EnumMap<>(BudgetCategory.class);
    private boolean categoriesTouched;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTripBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

        bindRow(BudgetCategory.ENTERTAINMENT, binding.cbEntertainment, binding.ivEntertainment, binding.tvEntertainment);
        bindRow(BudgetCategory.FOOD, binding.cbFood, binding.ivFood, binding.tvFood);
        bindRow(BudgetCategory.TICKETS, binding.cbTickets, binding.ivTickets, binding.tvTickets);
        bindRow(BudgetCategory.HOUSING, binding.cbHousing, binding.ivHousing, binding.tvHousing);

        // при первом входе восстанавливаем то, что уже лежит во ViewModel (вернулись назад)
        if (savedInstanceState == null) {
            BigDecimal saved = vm.getBudgetValue();
            if (saved != null) binding.etBudget.setText(saved.stripTrailingZeros().toPlainString());
            for (BudgetCategory c : vm.getSelectedCategoryList()) checks.get(c).setChecked(true);
        }

        binding.etBudget.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                if (budgetError() == null) binding.tilBudget.setError(null);
                updateButton();
            }
        });
        binding.etBudget.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) binding.tilBudget.setError(budgetError());
        });

        binding.btnContinue.setOnClickListener(v -> {
            vm.setBudget(parseBudget(), selectedCategories());
            NavHostFragment.findNavController(this).navigate(R.id.action_budget_to_tickets);
        });

        updateButton();
    }

    private void bindRow(BudgetCategory category, CheckBox cb, View icon, View label) {
        checks.put(category, cb);
        // клик по иконке или названию переключает чекбокс
        icon.setOnClickListener(v -> cb.toggle());
        label.setOnClickListener(v -> cb.toggle());
        cb.setOnCheckedChangeListener((button, checked) -> {
            categoriesTouched = true;
            updateButton();
        });
    }

    private Set<BudgetCategory> selectedCategories() {
        Set<BudgetCategory> result = EnumSet.noneOf(BudgetCategory.class);
        for (Map.Entry<BudgetCategory, CheckBox> e : checks.entrySet()) {
            if (e.getValue().isChecked()) result.add(e.getKey());
        }
        return result;
    }

    @Nullable
    private BigDecimal parseBudget() {
        try {
            return new BigDecimal(text().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String text() {
        return binding.etBudget.getText() == null ? "" : binding.etBudget.getText().toString().trim();
    }

    /** null — значение корректно. */
    @Nullable
    private String budgetError() {
        if (text().isEmpty()) return getString(R.string.error_budget_empty);
        BigDecimal v = parseBudget();
        if (v == null) return getString(R.string.error_budget_invalid);
        if (v.signum() <= 0) return getString(R.string.error_budget_positive);
        if (v.scale() > 2) return getString(R.string.error_budget_scale);
        if (v.compareTo(NewTripViewModel.MAX_BUDGET) > 0) return getString(R.string.error_budget_max);
        return null;
    }

    private void updateButton() {
        boolean hasCategories = !selectedCategories().isEmpty();
        binding.tvCategoriesError.setVisibility(categoriesTouched && !hasCategories ? View.VISIBLE : View.GONE);
        binding.btnContinue.setEnabled(budgetError() == null && hasCategories);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        checks.clear();
        binding = null;
    }
}