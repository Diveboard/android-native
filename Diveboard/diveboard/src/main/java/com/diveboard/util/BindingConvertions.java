package com.diveboard.util;

import android.databinding.BindingConversion;
import android.view.View;

public final class BindingConvertions {
    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }
}
