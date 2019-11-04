package com.diveboard.util;

import android.content.Context;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.GasMixes;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.diveboard.viewModel.SafetyStopViewModel;

import java.text.DecimalFormat;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.InverseMethod;

public final class BindingConvertions {
    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingConversion
    public static String convertSafetyStopToString(SafetyStopViewModel safetyStop) {
        Context context = ApplicationController.getInstance().getApplicationContext();
        String safetyStopItemFormat = context.getString(R.string.safetyStopItemFormat);
        return String.format(safetyStopItemFormat, safetyStop.getDepth(), context.getString(safetyStop.getUnits() == Units.UnitsType.Metric ? R.string.meter : R.string.foot), safetyStop.getDurationMinutes());
    }

    public static String convertTankToString(Tank tank, Context view) {
        DecimalFormat format = new DecimalFormat("#");
        String result = format.format(tank.startPressure) + " - " + format.format(tank.endPressure) + " (";
        switch (tank.gasType) {
            case GasMixes.NITROX:
                result += view.getResources().getString(R.string.nitrox_short) + " " + tank.o2;
                break;
            case GasMixes.TRIMIX:
                result += view.getResources().getString(R.string.trimix_short) + " " + tank.o2 + "/" + tank.he;
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

    @InverseMethod("stringToDoubleConverter")
    public String doubleToStringConverter(Double value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public Double stringToDoubleConverter(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
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
