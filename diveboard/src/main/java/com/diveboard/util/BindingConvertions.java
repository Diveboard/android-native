package com.diveboard.util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.InverseMethod;

import com.android.volley.toolbox.NetworkImageView;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.GasMixes;
import com.diveboard.model.Units;
import com.diveboard.viewModel.SafetyStopViewModel;
import com.diveboard.viewModel.TankViewModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

public final class BindingConvertions {
    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    public static int convertBooleanToVisibilityNotGone(boolean visible) {
        return visible ? View.VISIBLE : View.INVISIBLE;
    }

    @BindingConversion
    public static String convertSafetyStopToString(SafetyStopViewModel safetyStop) {
        Context context = ApplicationController.getInstance().getApplicationContext();
        String safetyStopItemFormat = context.getString(R.string.safetyStopItemFormat);
        return String.format(safetyStopItemFormat, safetyStop.getDepth(), context.getString(safetyStop.getUnits() == Units.UnitsType.Metric ? R.string.meter : R.string.foot), safetyStop.getDurationMinutes());
    }

    public static String convertTankToString(TankViewModel tank, Context view) {
        DecimalFormat format = new DecimalFormat("#");
        String result = format.format(tank.getStartPressure()) + " - " + format.format(tank.getEndPressure()) + " (";
        switch (tank.getGasMix()) {
            case GasMixes.NITROX:
                result += view.getResources().getString(R.string.nitrox_short) + " " + tank.getO2();
                break;
            case GasMixes.TRIMIX:
                result += view.getResources().getString(R.string.trimix_short) + " " + tank.getO2() + "/" + tank.getHe();
                break;
            default:
                result += view.getResources().getString(R.string.air_mix_short);
        }
        result += ");";
        return result;
    }

    @BindingAdapter("imageUrl")
    public static void getImageUrl(View view, String imageUrl) {
        NetworkImageView imageView = (NetworkImageView) view;
        imageView.setImageUrl(imageUrl, VolleySingleton.getInstance().getImageLoader());
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setLayoutMarginTop(View view, Float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.topMargin = dimen.intValue();
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setLayoutMarginBottom(View view, Float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.bottomMargin = dimen.intValue();
    }

    @BindingAdapter("android:layout_marginStart")
    public static void setLayoutMarginStart(View view, Float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMarginStart(dimen.intValue());
    }

    @BindingAdapter("android:layout_marginEnd")
    public static void setLayoutMarginEnd(View view, Float dimen) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMarginEnd(dimen.intValue());
    }

    @InverseMethod("stringToIntegerConverter")
    public String integerToStringConverter(Integer value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public Integer stringToIntegerConverter(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public static String intToStringConverter(int value) {
        return Integer.toString(value);
    }

    @InverseMethod("stringToDoubleConverter")
    public String doubleToStringConverter(Double value) {
        if (value == null) {
            return null;
        } else {
            return String.format("%.1f", value);
        }
    }

    public Double stringToDoubleConverter(String value) {
        try {
            //workaround for Android bug for locales where decimal separator is ','. It is not available on virtual keyboard
            if (!Utils.isNullOrEmpty(value)) {
                char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
                char oldSeparator = separator == '.' ? ',' : '.';
                value = value.replace(oldSeparator, separator);
            }
            NumberFormat format = NumberFormat.getInstance();
            Number number = format.parse(value);
            return number.doubleValue();
        } catch (ParseException e) {
            Log.i("BindingConvertions", "Cannot parse Double", e);
        }
        return null;
    }

    public String getTitleByUnits(Units.UnitsType units, String metric, String imperial) {
        return units == Units.UnitsType.Imperial ? imperial : metric;
    }

    public String[] getArrayByUnits(Units.UnitsType units, String[] metricArray, String[] imperialArray) {
        return units == Units.UnitsType.Imperial ? imperialArray : metricArray;
    }
}
