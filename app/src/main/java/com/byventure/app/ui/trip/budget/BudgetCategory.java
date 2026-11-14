package com.byventure.app.ui.trip.budget;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.byventure.app.R;

public enum BudgetCategory {
    ENTERTAINMENT(R.string.category_entertainment, R.color.category_entertainment, R.drawable.ic_category_entertainment),
    FOOD(R.string.category_food, R.color.category_food, R.drawable.ic_category_food),
    TICKETS(R.string.category_tickets, R.color.category_tickets, R.drawable.ic_category_tickets),
    HOUSING(R.string.category_housing, R.color.category_housing, R.drawable.ic_category_housing);

    @StringRes public final int labelRes;
    @ColorRes public final int colorRes;
    @DrawableRes public final int iconRes;

    BudgetCategory(@StringRes int labelRes, @ColorRes int colorRes, @DrawableRes int iconRes) {
        this.labelRes = labelRes;
        this.colorRes = colorRes;
        this.iconRes = iconRes;
    }
}