package com.byventure.app.ui.trip.new_trip.events;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.R;
import com.byventure.app.databinding.SheetTripEventBinding;
import com.byventure.app.ui.trip.new_trip.NewTripViewModel;
import com.byventure.app.ui.trip.new_trip.common.FieldUi;
import com.byventure.app.ui.trip.new_trip.common.FormBottomSheet;
import com.byventure.app.ui.trip.new_trip.common.TripFormats;
import com.byventure.app.ui.trip.new_trip.model.TripEvent;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

/** Попап добавления и редактирования события. */
public class TripEventBottomSheet extends FormBottomSheet {

    public static final String TAG = "TripEventBottomSheet";
    private static final String ARG_DATE = "date";
    private static final String ARG_EVENT_ID = "event_id";
    private static final String STATE_TIME = "time_minutes";

    public static TripEventBottomSheet forNewEvent(LocalDate date) {
        TripEventBottomSheet f = new TripEventBottomSheet();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.toEpochDay());
        f.setArguments(args);
        return f;
    }

    public static TripEventBottomSheet forEditing(TripEvent event) {
        TripEventBottomSheet f = forNewEvent(event.getDate());
        f.getArguments().putString(ARG_EVENT_ID, event.getId());
        return f;
    }

    private SheetTripEventBinding b;
    private NewTripViewModel vm;
    private LocalDate date;
    @Nullable private String eventId;
    @Nullable private TripEvent existing;
    @Nullable private LocalTime time;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);
        date = LocalDate.ofEpochDay(requireArguments().getLong(ARG_DATE));
        eventId = requireArguments().getString(ARG_EVENT_ID);
        existing = eventId == null ? null : vm.findEvent(eventId);

        if (savedInstanceState != null) {
            int t = savedInstanceState.getInt(STATE_TIME, -1);
            if (t >= 0) time = LocalTime.of(t / 60, t % 60);
        } else if (existing != null) {
            time = existing.getTime();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_TIME, time == null ? -1 : time.getHour() * 60 + time.getMinute());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = SheetTripEventBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // событие могли удалить, пока попап пересоздавался
        if (eventId != null && existing == null) {
            dismissAllowingStateLoss();
            return;
        }
        boolean editing = existing != null;

        b.tvTitle.setText(editing ? R.string.entry_title_edit : R.string.entry_title_add);
        b.btnAdd.setVisibility(editing ? View.GONE : View.VISIBLE);
        b.groupEdit.setVisibility(editing ? View.VISIBLE : View.GONE);
        b.btnClose.setOnClickListener(v -> dismiss());

        if (savedInstanceState == null && existing != null) {
            b.etTitle.setText(existing.getTitle());
            b.etAddress.setText(existing.getAddress());
        }

        bindText(b.etTitle, this::validateTitle, this::updateButton);
        bindText(b.etAddress, this::validateAddress, this::updateButton);

        b.btnTime.setOnClickListener(v -> pickTime(time, t -> {
            time = t;
            renderTime();
            validateTime(true);
            updateButton();
        }, () -> validateTime(true)));

        b.btnAdd.setOnClickListener(v -> {
            vm.addEvent(buildEvent(null));
            dismiss();
        });
        b.btnSave.setOnClickListener(v -> {
            vm.updateEvent(buildEvent(eventId));
            dismiss();
        });
        b.btnDelete.setOnClickListener(v -> confirmDelete());

        renderTime();
        updateButton();
    }

    private TripEvent buildEvent(@Nullable String id) {
        return TripEvent.create(id, date, text(b.etTitle), text(b.etAddress), time);
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_delete, (d, w) -> {
                    vm.removeEvent(eventId);
                    dismiss();
                })
                .show();
    }

    private void renderTime() {
        FieldUi.setPickerValue(b.btnTime,
                time == null ? null : time.format(TripFormats.TIME),
                getString(R.string.time_event));
    }

    // ---------- валидация ----------

    private boolean validateTitle(boolean show) {
        String t = text(b.etTitle);
        String e = t.isEmpty() ? getString(R.string.error_title_empty)
                : t.length() > NewTripViewModel.MAX_TITLE_LENGTH
                ? getString(R.string.error_title_long, NewTripViewModel.MAX_TITLE_LENGTH) : null;
        return check(b.tilTitle, e, show);
    }

    private boolean validateAddress(boolean show) {
        String t = text(b.etAddress);
        String e = t.isEmpty() ? getString(R.string.error_address_empty)
                : t.length() > NewTripViewModel.MAX_ADDRESS_LENGTH
                ? getString(R.string.error_address_long, NewTripViewModel.MAX_ADDRESS_LENGTH) : null;
        return check(b.tilAddress, e, show);
    }

    private boolean validateTime(boolean show) {
        if (time != null) {
            FieldUi.setError(b.btnTime, b.tvTimeError, null);
            return true;
        }
        if (show) FieldUi.setError(b.btnTime, b.tvTimeError, getString(R.string.error_time_empty));
        return false;
    }

    private void updateButton() {
        // '&' намеренно: все валидаторы должны отработать
        boolean ok = validateTitle(false) & validateAddress(false) & validateTime(false);
        b.btnAdd.setEnabled(ok);
        b.btnSave.setEnabled(ok);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}