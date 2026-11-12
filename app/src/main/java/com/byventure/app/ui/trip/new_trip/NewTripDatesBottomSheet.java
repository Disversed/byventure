package com.byventure.app.ui.trip.new_trip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.R;
import com.byventure.app.databinding.SheetNewTripDatesBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class NewTripDatesBottomSheet extends ExpandedBottomSheet {

    public static final String TAG = "NewTripDatesBottomSheet";
    public static final String RESULT_KEY = "new_trip_dates_result";

    private SheetNewTripDatesBinding binding;
    private NewTripViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = SheetNewTripDatesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.etStart.setOnClickListener(v -> showPicker(true));
        binding.etEnd.setOnClickListener(v -> showPicker(false));
        binding.btnContinue.setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResult(RESULT_KEY, new Bundle());
            dismiss();
        });

        // даты живут во ViewModel -> переживают поворот экрана
        vm.getStartDate().observe(getViewLifecycleOwner(), d -> render());
        vm.getEndDate().observe(getViewLifecycleOwner(), d -> render());
    }

    private void render() {
        LocalDate s = vm.getStartDate().getValue();
        LocalDate e = vm.getEndDate().getValue();

        binding.etStart.setText(s == null ? "" : s.format(TripFormats.DATE));
        binding.etEnd.setText(e == null ? "" : e.format(TripFormats.DATE));

        String startError = null;
        String endError = null;
        if (s != null && s.isBefore(LocalDate.now())) {
            startError = getString(R.string.error_date_past);
        }
        if (s != null && e != null) {
            if (e.isBefore(s)) {
                endError = getString(R.string.error_end_before_start);
            } else if (ChronoUnit.DAYS.between(s, e) + 1 > NewTripViewModel.MAX_TRIP_DAYS) {
                endError = getString(R.string.error_trip_too_long, NewTripViewModel.MAX_TRIP_DAYS);
            }
        }
        binding.tilStart.setError(startError);
        binding.tilEnd.setError(endError);

        binding.btnContinue.setEnabled(s != null && e != null && startError == null && endError == null);
    }

    private void showPicker(boolean isStart) {
        LocalDate today = LocalDate.now();
        LocalDate start = vm.getStartDate().getValue();
        LocalDate current = isStart ? start : vm.getEndDate().getValue();

        LocalDate min = isStart ? today : (start != null && !start.isBefore(today) ? start : today);
        LocalDate initial = current != null ? current : min;
        if (initial.isBefore(min)) initial = min;

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setStart(TripFormats.toPickerMillis(min))
                .setValidator(DateValidatorPointForward.from(TripFormats.toPickerMillis(min)))
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(isStart ? R.string.picker_start_title : R.string.picker_end_title)
                .setSelection(TripFormats.toPickerMillis(initial))
                .setCalendarConstraints(constraints)
                .build();

        picker.addOnPositiveButtonClickListener(ms -> {
            LocalDate picked = TripFormats.fromPickerMillis(ms);
            if (isStart) vm.setStartDate(picked); else vm.setEndDate(picked);
        });
        picker.show(getChildFragmentManager(), isStart ? "pick_start" : "pick_end");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}