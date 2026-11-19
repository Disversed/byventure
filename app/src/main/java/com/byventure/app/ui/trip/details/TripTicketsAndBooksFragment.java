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
import com.byventure.app.ui.trip.new_trip.model.TripEntry;
import com.byventure.app.ui.trip.new_trip.tickets.TripEntryBottomSheet;
import com.byventure.app.ui.trip.new_trip.tickets.TripRowAdapter;

import java.time.LocalDate;

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

        TripRowAdapter adapter = new TripRowAdapter(new TripRowAdapter.Listener() {
            @Override
            public void onAddClick(LocalDate date) {
                showSheet(TripEntryBottomSheet.forNewEntry(date));
            }

            @Override
            public void onEntryClick(TripEntry entry) {
                showSheet(TripEntryBottomSheet.forEditing(entry));
            }
        });
        binding.rvEntries.setAdapter(adapter);

        vm.getRows().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void showSheet(TripEntryBottomSheet sheet) {
        if (getChildFragmentManager().findFragmentByTag(TripEntryBottomSheet.TAG) != null) return;
        sheet.show(getChildFragmentManager(), TripEntryBottomSheet.TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvEntries.setAdapter(null);
        binding = null;
    }
}