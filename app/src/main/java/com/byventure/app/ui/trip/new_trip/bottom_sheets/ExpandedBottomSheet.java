package com.byventure.app.ui.trip.new_trip.bottom_sheets;

import android.app.Dialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.byventure.app.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/** Попап, который сразу раскрыт полностью и поднимается над клавиатурой. */
public abstract class ExpandedBottomSheet extends BottomSheetDialogFragment {
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog instanceof BottomSheetDialog) {
            BottomSheetBehavior<FrameLayout> behavior = ((BottomSheetDialog) dialog).getBehavior();
            behavior.setSkipCollapsed(true);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        if (dialog != null) {
            Window w = dialog.getWindow();
            if (w != null) w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
    @Override
    public int getTheme() {
        return R.style.ThemeOverlay_ByVenture_BottomSheetDialog;
    }
}