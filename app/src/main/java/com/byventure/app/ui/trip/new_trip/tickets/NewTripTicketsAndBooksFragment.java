package com.byventure.app.ui.trip.new_trip.tickets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentNewTripTicketsAndBooksBinding;
import com.byventure.app.ui.trip.new_trip.NewTripViewModel;
import com.byventure.app.ui.trip.new_trip.model.TripEntry;

import java.time.LocalDate;

public class NewTripTicketsAndBooksFragment extends Fragment {

    private FragmentNewTripTicketsAndBooksBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTripTicketsAndBooksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewTripViewModel vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

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
        vm.getEntries().observe(getViewLifecycleOwner(),
                list -> binding.btnContinue.setEnabled(!list.isEmpty()));

        binding.btnContinue.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_tickets_to_events));
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