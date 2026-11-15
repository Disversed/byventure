package com.byventure.app.ui.trip.new_trip.tickets;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.R;
import com.byventure.app.databinding.SheetTripEntryBinding;
import com.byventure.app.ui.trip.new_trip.NewTripViewModel;
import com.byventure.app.ui.trip.new_trip.common.ExpandedBottomSheet;
import com.byventure.app.ui.trip.new_trip.common.FieldUi;
import com.byventure.app.ui.trip.new_trip.common.SimpleTextWatcher;
import com.byventure.app.ui.trip.new_trip.model.BudgetCategory;
import com.byventure.app.ui.trip.new_trip.model.TripEntry;
import com.byventure.app.ui.trip.new_trip.common.TripFormats;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Попап добавления и редактирования брони/билета. */
public class TripEntryBottomSheet extends ExpandedBottomSheet {

    public static final String TAG = "TripEntryBottomSheet";
    private static final String ARG_DATE = "date";
    private static final String ARG_ENTRY_ID = "entry_id";
    private static final String STATE_FROM = "from_minutes";
    private static final String STATE_TO = "to_minutes";
    private static final String STATE_CATEGORY = "category";
    private static final String STATE_TAB = "tab";

    public static TripEntryBottomSheet forNewEntry(LocalDate date) {
        TripEntryBottomSheet f = new TripEntryBottomSheet();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.toEpochDay());
        f.setArguments(args);
        return f;
    }

    public static TripEntryBottomSheet forEditing(TripEntry entry) {
        TripEntryBottomSheet f = forNewEntry(entry.getDate());
        f.getArguments().putString(ARG_ENTRY_ID, entry.getId());
        return f;
    }

    private SheetTripEntryBinding b;
    private NewTripViewModel vm;
    private LocalDate date;
    @Nullable private String entryId;
    @Nullable private TripEntry existing;
    private List<BudgetCategory> categories;

    private int initialTab;
    @Nullable private LocalTime fromTime;
    @Nullable private LocalTime toTime;
    @Nullable private BudgetCategory category;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = new ViewModelProvider(requireActivity()).get(NewTripViewModel.class);
        date = LocalDate.ofEpochDay(requireArguments().getLong(ARG_DATE));
        entryId = requireArguments().getString(ARG_ENTRY_ID);
        existing = entryId == null ? null : vm.findEntry(entryId);

        if (savedInstanceState != null) {
            int f = savedInstanceState.getInt(STATE_FROM, -1);
            int t = savedInstanceState.getInt(STATE_TO, -1);
            int c = savedInstanceState.getInt(STATE_CATEGORY, -1);
            if (f >= 0) fromTime = LocalTime.of(f / 60, f % 60);
            if (t >= 0) toTime = LocalTime.of(t / 60, t % 60);
            if (c >= 0) category = BudgetCategory.values()[c];
            initialTab = savedInstanceState.getInt(STATE_TAB, 0);
        } else if (existing != null) {
            category = existing.getCategory();
            fromTime = existing.getFromTime();
            toTime = existing.getToTime();
            initialTab = existing.isTransport() ? 1 : 0;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FROM, fromTime == null ? -1 : fromTime.getHour() * 60 + fromTime.getMinute());
        outState.putInt(STATE_TO, toTime == null ? -1 : toTime.getHour() * 60 + toTime.getMinute());
        outState.putInt(STATE_CATEGORY, category == null ? -1 : category.ordinal());
        outState.putInt(STATE_TAB, b == null ? 0 : b.tabs.getSelectedTabPosition());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = SheetTripEntryBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // запись могли удалить, пока попап был закрыт/пересоздавался
        if (entryId != null && existing == null) {
            dismissAllowingStateLoss();
            return;
        }
        boolean editing = existing != null;
        categories = vm.getSelectedCategoryList();
        if (category != null && !categories.contains(category)) category = null;

        b.tvTitle.setText(editing ? R.string.entry_title_edit : R.string.entry_title_add);
        b.btnAdd.setVisibility(editing ? View.GONE : View.VISIBLE);
        b.groupEdit.setVisibility(editing ? View.VISIBLE : View.GONE);
        b.btnClose.setOnClickListener(v -> dismiss());

        // выпадающий список категорий (первый элемент — подсказка)
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.hint_category));
        for (BudgetCategory c : categories) items.add(getString(c.labelRes));
        b.spinnerCategory.setAdapter(new CategorySpinnerAdapter(requireContext(), items));
        b.spinnerCategory.setSelection(category == null ? 0 : categories.indexOf(category) + 1, false);
        b.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                category = position == 0 ? null : categories.get(position - 1);
                updateButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        TabLayout.Tab tab = b.tabs.getTabAt(initialTab);
        if (tab != null) tab.select();
        b.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab t) { applyMode(); updateButton(); }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });

        // начальные значения при редактировании
        if (savedInstanceState == null && existing != null) {
            b.etTitle.setText(existing.getTitle());
            b.etFromAddress.setText(existing.getFromAddress());
            if (existing.getToAddress() != null) b.etToAddress.setText(existing.getToAddress());
        }

        bindText(b.etTitle, this::validateTitle);
        bindText(b.etFromAddress, this::validateFromAddress);
        bindText(b.etToAddress, this::validateToAddress);

        b.btnFromTime.setOnClickListener(v -> pickTime(fromTime, t -> {
            fromTime = t;
            renderTimes();
            validateFromTime(true);
            validateToTime(false);
            updateButton();
        }, () -> validateFromTime(true)));

        b.btnToTime.setOnClickListener(v -> pickTime(toTime, t -> {
            toTime = t;
            renderTimes();
            validateToTime(true);
            updateButton();
        }, () -> validateToTime(true)));

        b.btnAdd.setOnClickListener(v -> {
            vm.addEntry(buildEntry(null));
            dismiss();
        });
        b.btnSave.setOnClickListener(v -> {
            vm.updateEntry(buildEntry(entryId));
            dismiss();
        });
        b.btnDelete.setOnClickListener(v -> confirmDelete());

        applyMode();
        updateButton();
    }

    private TripEntry buildEntry(@Nullable String id) {
        String title = text(b.etTitle);
        String from = text(b.etFromAddress);
        return isTransport()
                ? TripEntry.transportTicket(id, date, title, category, from, fromTime,
                text(b.etToAddress), toTime)
                : TripEntry.booking(id, date, title, category, from, fromTime);
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_title)
                .setMessage(R.string.delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.action_delete, (d, w) -> {
                    vm.removeEntry(entryId);
                    dismiss();
                })
                .show();
    }

    private boolean isTransport() {
        return b.tabs.getSelectedTabPosition() == 1;
    }

    private void applyMode() {
        boolean t = isTransport();
        b.tilFromAddress.setHint(getString(t ? R.string.hint_from : R.string.hint_address));
        b.groupTo.setVisibility(t ? View.VISIBLE : View.GONE);
        if (!t) {
            b.tilToAddress.setError(null);
            FieldUi.setError(b.btnToTime, b.tvToTimeError, null);
        }
        renderTimes();
    }

    private void renderTimes() {
        boolean t = isTransport();
        FieldUi.setPickerValue(b.btnFromTime,
                fromTime == null ? null : fromTime.format(TripFormats.TIME),
                getString(t ? R.string.time_departure : R.string.time_booking));
        FieldUi.setPickerValue(b.btnToTime,
                toTime == null ? null : toTime.format(TripFormats.TIME),
                getString(R.string.time_arrival));
    }

    private void pickTime(@Nullable LocalTime initial, Consumer<LocalTime> onPicked, Runnable onCancel) {
        LocalTime init = initial != null ? initial : LocalTime.of(12, 0);
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(init.getHour())
                .setMinute(init.getMinute())
                .setTitleText(R.string.picker_time_title)
                .build();
        picker.addOnPositiveButtonClickListener(v ->
                onPicked.accept(LocalTime.of(picker.getHour(), picker.getMinute())));
        picker.addOnNegativeButtonClickListener(v -> onCancel.run());
        picker.addOnCancelListener(d -> onCancel.run());
        picker.show(getChildFragmentManager(), "pick_time");
    }

    // ---------- валидация ----------

    private void bindText(TextInputEditText et, Predicate<Boolean> validator) {
        et.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void afterTextChanged(Editable s) {
                validator.test(false);
                updateButton();
            }
        });
        et.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) validator.test(true); });
    }

    private String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /** err == null -> ошибка снимается. Иначе показываем её только если show. */
    private boolean check(TextInputLayout til, @Nullable String err, boolean show) {
        if (err == null) {
            til.setError(null);
            return true;
        }
        if (show) til.setError(err);
        return false;
    }

    private boolean validateTitle(boolean show) {
        String t = text(b.etTitle);
        String e = t.isEmpty() ? getString(R.string.error_title_empty)
                : t.length() > NewTripViewModel.MAX_TITLE_LENGTH
                ? getString(R.string.error_title_long, NewTripViewModel.MAX_TITLE_LENGTH) : null;
        return check(b.tilTitle, e, show);
    }

    private boolean validateAddress(TextInputLayout til, TextInputEditText et, boolean show) {
        String t = text(et);
        String e = t.isEmpty() ? getString(R.string.error_address_empty)
                : t.length() > NewTripViewModel.MAX_ADDRESS_LENGTH
                ? getString(R.string.error_address_long, NewTripViewModel.MAX_ADDRESS_LENGTH) : null;
        return check(til, e, show);
    }

    private boolean validateFromAddress(boolean show) {
        return validateAddress(b.tilFromAddress, b.etFromAddress, show);
    }

    private boolean validateToAddress(boolean show) {
        return !isTransport() || validateAddress(b.tilToAddress, b.etToAddress, show);
    }

    private boolean validateFromTime(boolean show) {
        if (fromTime != null) {
            FieldUi.setError(b.btnFromTime, b.tvFromTimeError, null);
            return true;
        }
        if (show) FieldUi.setError(b.btnFromTime, b.tvFromTimeError, getString(R.string.error_time_empty));
        return false;
    }

    private boolean validateToTime(boolean show) {
        if (!isTransport()) return true;
        if (toTime == null) {
            if (show) FieldUi.setError(b.btnToTime, b.tvToTimeError, getString(R.string.error_time_empty));
            return false;
        }
        if (fromTime != null && !toTime.isAfter(fromTime)) {
            FieldUi.setError(b.btnToTime, b.tvToTimeError, getString(R.string.error_arrival_before_departure));
            return false;
        }
        FieldUi.setError(b.btnToTime, b.tvToTimeError, null);
        return true;
    }

    private void updateButton() {
        // '&' намеренно: все валидаторы должны отработать
        boolean ok = validateTitle(false) & (category != null)
                & validateFromAddress(false) & validateFromTime(false)
                & validateToAddress(false) & validateToTime(false);
        b.btnAdd.setEnabled(ok);
        b.btnSave.setEnabled(ok);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}