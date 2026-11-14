package com.byventure.app.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byventure.app.databinding.SheetAddReceiptBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AddReceiptSheetFragment extends BottomSheetDialogFragment {

    private SheetAddReceiptBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = SheetAddReceiptBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Обработка клика по крестику
        binding.btnClose.setOnClickListener(v -> dismiss());

        // Обработка клика по кнопке добавления
        binding.btnAddReceipt.setOnClickListener(v -> {
            // TODO: Считать данные из полей и передать во ViewModel / родительский фрагмент
            dismiss();
        });

        // В методе onViewCreated:
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvCategories.setLayoutManager(layoutManager);

        // TODO: Подключить адаптер категорий (CategoryAdapter)
        // CategoryAdapter adapter = new CategoryAdapter(categoriesList, selectedCategory -> { ... });
        // binding.rvCategories.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}