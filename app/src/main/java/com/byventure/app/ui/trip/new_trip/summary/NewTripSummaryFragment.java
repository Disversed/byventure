package com.byventure.app.ui.trip.new_trip.summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.databinding.FragmentNewTripSummaryBinding;
import com.byventure.app.ui.trip.new_trip.NewTripViewModel;

public class NewTripSummaryFragment extends Fragment {

    private FragmentNewTripSummaryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTripSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewTripViewModel vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

        SummaryAdapter adapter = new SummaryAdapter();
        binding.rvSummary.setAdapter(adapter);

        vm.getSummaryRows().observe(getViewLifecycleOwner(), rows -> {
            adapter.submitList(rows);
            binding.btnSave.setEnabled(!rows.isEmpty());
        });

        binding.btnSave.setOnClickListener(v -> {
            // TODO: сохранить маршрут в БД (данные берём из NewTripViewModel)
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvSummary.setAdapter(null);
        binding = null;
    }
}