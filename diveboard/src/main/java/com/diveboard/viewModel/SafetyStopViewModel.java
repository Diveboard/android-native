package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;
import com.diveboard.util.Utils;

public class SafetyStopViewModel extends BaseObservable {
    private Units.UnitsType units;
    private Integer depth;
    private Integer durationMinutes;

    public SafetyStopViewModel(Units.UnitsType units) {
        this.units = units;
        SafetyStop defaultSafetyStop = SafetyStop.getDefault();
        depth = defaultSafetyStop.depthInMeters;
        durationMinutes = defaultSafetyStop.durationInMinutes;
    }

    public static SafetyStopViewModel fromModel(SafetyStop safetyStop, Units.UnitsType units) {
        SafetyStopViewModel result = new SafetyStopViewModel(units);
        result.durationMinutes = safetyStop.durationInMinutes;
        result.depth = Converter.convertDistance(safetyStop.depthInMeters, units);
        return result;
    }

    public Units.UnitsType getUnits() {
        return units;
    }

    @Bindable
    public Integer getDepth() {
        return Converter.convertDistance(depth, units);
    }

    public void setDepth(Integer value) {
        if (Utils.equals(depth, value)) {
            return;
        }
        depth = Converter.convertDistanceToMetric(value, units);
        notifyPropertyChanged(BR.depth);
    }

    @Bindable
    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer value) {
        if (Utils.equals(durationMinutes, value)) {
            return;
        }
        durationMinutes = value;
        notifyPropertyChanged(BR.durationMinutes);
    }

    public SafetyStop toModel() {
        return new SafetyStop(depth, durationMinutes);
    }
}
