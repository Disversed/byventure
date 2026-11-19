package com.byventure.app.ui.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.databinding.FragmentTripSummaryBinding;
import com.byventure.app.ui.trip.new_trip.summary.SummaryAdapter;

public class TripSummaryFragment extends Fragment {

    private FragmentTripSummaryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTripSummaryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TripViewModel vm = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        SummaryAdapter adapter = new SummaryAdapter();
        binding.rvSummary.setAdapter(adapter);

        vm.getSummaryRows().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvSummary.setAdapter(null);
        binding = null;
    }
}