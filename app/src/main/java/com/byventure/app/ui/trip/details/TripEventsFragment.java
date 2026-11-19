package com.byventure.app.ui.trip.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.databinding.FragmentTripEventsBinding;
import com.byventure.app.ui.trip.new_trip.events.TripEventAdapter;
import com.byventure.app.ui.trip.new_trip.events.TripEventBottomSheet;
import com.byventure.app.ui.trip.new_trip.model.TripEvent;

import java.time.LocalDate;

public class TripEventsFragment extends Fragment {

    private FragmentTripEventsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTripEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TripViewModel vm = new ViewModelProvider(requireActivity()).get(TripViewModel.class);

        TripEventAdapter adapter = new TripEventAdapter(new TripEventAdapter.Listener() {
            @Override
            public void onAddClick(LocalDate date) {
                showSheet(TripEventBottomSheet.forNewEvent(date));
            }

            @Override
            public void onEventClick(TripEvent event) {
                showSheet(TripEventBottomSheet.forEditing(event));
            }
        });
        binding.rvEvents.setAdapter(adapter);

        vm.getEventRows().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void showSheet(TripEventBottomSheet sheet) {
        if (getChildFragmentManager().findFragmentByTag(TripEventBottomSheet.TAG) != null) return;
        sheet.show(getChildFragmentManager(), TripEventBottomSheet.TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvEvents.setAdapter(null);
        binding = null;
    }
}