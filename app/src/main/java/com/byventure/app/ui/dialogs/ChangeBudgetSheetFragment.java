package com.byventure.app.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byventure.app.databinding.FragmentChangeBudgetSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ChangeBudgetSheetFragment extends BottomSheetDialogFragment {

    private FragmentChangeBudgetSheetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangeBudgetSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Обработка клика по крестику
        binding.btnClose.setOnClickListener(v -> dismiss());

        // Обработка клика по кнопке добавления
        binding.btnChangeBudget.setOnClickListener(v -> {
            // TODO: Считать данные из полей и передать во ViewModel / родительский фрагмент
            dismiss();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}