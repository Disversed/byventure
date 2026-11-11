package com.byventure.app.ui.trip.new_trip;

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
import com.byventure.app.databinding.FragmentTripsBinding;

public class TripsFragment extends Fragment {

    private FragmentTripsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NewTripViewModel vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

        binding.btnCreate.setOnClickListener(v -> {
            if (getChildFragmentManager().findFragmentByTag(NewTripDatesBottomSheet.TAG) != null) return;
            vm.reset();
            new NewTripDatesBottomSheet().show(getChildFragmentManager(), NewTripDatesBottomSheet.TAG);
        });

        // попап сообщает, что даты введены -> идём на «Бюджет»
        getChildFragmentManager().setFragmentResultListener(
                NewTripDatesBottomSheet.RESULT_KEY, getViewLifecycleOwner(),
                (key, bundle) -> NavHostFragment.findNavController(this)
                        .navigate(R.id.action_trips_to_budget));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}