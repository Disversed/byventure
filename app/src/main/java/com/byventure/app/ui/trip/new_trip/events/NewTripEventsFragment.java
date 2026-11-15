package com.byventure.app.ui.trip.new_trip.events;

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
import com.byventure.app.databinding.FragmentNewTripEventsBinding;
import com.byventure.app.ui.trip.new_trip.NewTripViewModel;
import com.byventure.app.ui.trip.new_trip.model.TripEvent;

import java.time.LocalDate;

public class NewTripEventsFragment extends Fragment {

    private FragmentNewTripEventsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNewTripEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewTripViewModel vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

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
        vm.getEvents().observe(getViewLifecycleOwner(),
                list -> binding.btnContinue.setEnabled(!list.isEmpty()));

        binding.btnContinue.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_events_to_summary));
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