package com.diveboard.util;

import android.content.Context;
import android.view.View;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

import java.util.List;

import androidx.databinding.BindingConversion;
import androidx.databinding.InverseMethod;

public final class BindingConvertions {
    @BindingConversion
    public static int convertBooleanToVisibility(boolean visible) {
        return visible ? View.VISIBLE : View.GONE;
    }

    @BindingConversion
    public static String convertSafetyStopToString(SafetyStop safetyStop) {
        Context context = ApplicationController.getInstance().getApplicationContext();
        String safetyStopItemFormat = context.getString(R.string.safetyStopItemFormat);
        return String.format(safetyStopItemFormat, safetyStop.getDepth(), context.getString(safetyStop.getUnit() == Units.Depth.Meters ? R.string.meter : R.string.foot), safetyStop.getDuration());
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
