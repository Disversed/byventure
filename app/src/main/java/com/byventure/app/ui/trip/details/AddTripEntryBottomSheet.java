package com.byventure.app.ui.trip.details;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.ViewModelProvider;

import com.byventure.app.R;
import com.byventure.app.databinding.SheetAddTripEntryBinding;
import com.byventure.app.ui.trip.budget.BudgetCategory;
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

public class AddTripEntryBottomSheet extends ExpandedBottomSheet {

    public static final String TAG = "AddTripEntryBottomSheet";
    private static final String ARG_DATE = "date";
    private static final String STATE_FROM = "from_minutes";
    private static final String STATE_TO = "to_minutes";
    private static final String STATE_CATEGORY = "category";

    public static AddTripEntryBottomSheet newInstance(LocalDate date) {
        AddTripEntryBottomSheet f = new AddTripEntryBottomSheet();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.toEpochDay());
        f.setArguments(args);
        return f;
    }

    private SheetAddTripEntryBinding b;
    private TripViewModel vm;
    private LocalDate date;
    private List<BudgetCategory> categories;

    @Nullable private LocalTime fromTime;
    @Nullable private LocalTime toTime;
    @Nullable private BudgetCategory category;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = LocalDate.ofEpochDay(requireArguments().getLong(ARG_DATE));
        if (savedInstanceState != null) {
            int f = savedInstanceState.getInt(STATE_FROM, -1);
            int t = savedInstanceState.getInt(STATE_TO, -1);
            int c = savedInstanceState.getInt(STATE_CATEGORY, -1);
            if (f >= 0) fromTime = LocalTime.of(f / 60, f % 60);
            if (t >= 0) toTime = LocalTime.of(t / 60, t % 60);
            if (c >= 0) category = BudgetCategory.values()[c];
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_FROM, fromTime == null ? -1 : fromTime.getHour() * 60 + fromTime.getMinute());
        outState.putInt(STATE_TO, toTime == null ? -1 : toTime.getHour() * 60 + toTime.getMinute());
        outState.putInt(STATE_CATEGORY, category == null ? -1 : category.ordinal());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = SheetAddTripEntryBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        categories = vm.getSelectedCategoryList();

        // категории — только выбранные на экране «Бюджет»
        List<String> names = new ArrayList<>();
        for (BudgetCategory c : categories) names.add(getString(c.labelRes));
        b.actCategory.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, names));
        b.actCategory.setOnItemClickListener((parent, v, position, id) -> {
            category = categories.get(position);
            validateCategory(false);
            updateButton();
        });
        if (category != null && categories.contains(category)) {
            b.actCategory.setText(getString(category.labelRes), false);
        } else {
            category = null;
        }

        b.btnClose.setOnClickListener(v -> dismiss());

        b.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { applyMode(); updateButton(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        bindText(b.etTitle, this::validateTitle);
        bindText(b.etFromAddress, this::validateFromAddress);
        bindText(b.etToAddress, this::validateToAddress);

        b.actCategory.setOnFocusChangeListener((v, hasFocus) -> { if (!hasFocus) validateCategory(true); });

        b.etFromTime.setOnClickListener(v -> pickTime(fromTime, t -> {
            fromTime = t;
            renderTimes();
            validateFromTime(true);
            validateToTime(false);
            updateButton();
        }, () -> { validateFromTime(true); }));

        b.etToTime.setOnClickListener(v -> pickTime(toTime, t -> {
            toTime = t;
            renderTimes();
            validateToTime(true);
            updateButton();
        }, () -> { validateToTime(true); }));

        b.btnAdd.setOnClickListener(v -> {
            String title = text(b.etTitle);
            String from = text(b.etFromAddress);
            TripEntry entry = isTransport()
                    ? TripEntry.transportTicket(date, title, category, from, fromTime,
                    text(b.etToAddress), toTime)
                    : TripEntry.booking(date, title, category, from, fromTime);
            vm.addEntry(entry);
            dismiss();
        });

        applyMode();
        renderTimes();
        updateButton();
    }

    private boolean isTransport() {
        return b.tabs.getSelectedTabPosition() == 1;
    }

    private void applyMode() {
        boolean t = isTransport();
        b.tilFromAddress.setHint(getString(t ? R.string.hint_from : R.string.hint_address));
        b.tilFromTime.setHint(getString(t ? R.string.hint_departure_time : R.string.hint_time));
        b.groupTo.setVisibility(t ? View.VISIBLE : View.GONE);
        // ошибки полей, которые скрылись, не нужны
        if (!t) {
            b.tilToAddress.setError(null);
            b.tilToTime.setError(null);
        }
    }

    private void renderTimes() {
        b.etFromTime.setText(fromTime == null ? "" : fromTime.format(TripFormats.TIME));
        b.etToTime.setText(toTime == null ? "" : toTime.format(TripFormats.TIME));
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

    /** err == null -> поле корректно, ошибка снимается. Иначе показываем её только если show. */
    private boolean check(TextInputLayout til, @Nullable String err, boolean show) {
        if (err == null) {
            til.setError(null);
            return true;
        }
        if (show) til.setError(err);
        return false;
    }

    private String err(@StringRes int res) { return getString(res); }

    private boolean validateTitle(boolean show) {
        String t = text(b.etTitle);
        String e = t.isEmpty() ? err(R.string.error_title_empty)
                : t.length() > TripViewModel.MAX_TITLE_LENGTH
                  ? getString(R.string.error_title_long, TripViewModel.MAX_TITLE_LENGTH) : null;
        return check(b.tilTitle, e, show);
    }

    private boolean validateCategory(boolean show) {
        return check(b.tilCategory, category == null ? err(R.string.error_category_empty) : null, show);
    }

    private boolean validateAddress(TextInputLayout til, TextInputEditText et, boolean show) {
        String t = text(et);
        String e = t.isEmpty() ? err(R.string.error_address_empty)
                : t.length() > TripViewModel.MAX_ADDRESS_LENGTH
                  ? getString(R.string.error_address_long, TripViewModel.MAX_ADDRESS_LENGTH) : null;
        return check(til, e, show);
    }

    private boolean validateFromAddress(boolean show) {
        return validateAddress(b.tilFromAddress, b.etFromAddress, show);
    }

    private boolean validateToAddress(boolean show) {
        return !isTransport() || validateAddress(b.tilToAddress, b.etToAddress, show);
    }

    private boolean validateFromTime(boolean show) {
        return check(b.tilFromTime, fromTime == null ? err(R.string.error_time_empty) : null, show);
    }

    private boolean validateToTime(boolean show) {
        if (!isTransport()) return true;
        if (toTime == null) return check(b.tilToTime, err(R.string.error_time_empty), show);
        if (fromTime != null && !toTime.isAfter(fromTime)) {
            return check(b.tilToTime, err(R.string.error_arrival_before_departure), true);
        }
        return check(b.tilToTime, null, show);
    }

    private void updateButton() {
        // '&' намеренно: все валидаторы должны отработать
        boolean ok = validateTitle(false) & validateCategory(false)
                & validateFromAddress(false) & validateFromTime(false)
                & validateToAddress(false) & validateToTime(false);
        b.btnAdd.setEnabled(ok);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}