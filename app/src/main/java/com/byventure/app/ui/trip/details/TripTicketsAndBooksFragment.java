package com.byventure.app.ui.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.databinding.FragmentTripTicketsAndBooksBinding;

public class TripTicketsAndBooksFragment extends Fragment {

    private FragmentTripTicketsAndBooksBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTripTicketsAndBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TripViewModel vm = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        TripRowAdapter adapter = new TripRowAdapter(date -> {
            if (getChildFragmentManager().findFragmentByTag(AddTripEntryBottomSheet.TAG) != null) return;
            AddTripEntryBottomSheet.newInstance(date)
                    .show(getChildFragmentManager(), AddTripEntryBottomSheet.TAG);
        });
        binding.rvEntries.setAdapter(adapter);

        vm.getRows().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvEntries.setAdapter(null);
        binding = null;
    }
}