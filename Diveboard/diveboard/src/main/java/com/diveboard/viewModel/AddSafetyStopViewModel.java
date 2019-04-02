package com.diveboard.viewModel;

import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class AddSafetyStopViewModel extends BaseObservable {
    private Units.UnitsType units;
    private Integer depth;
    private Integer durationMinutes;
    public AddSafetyStopViewModel(Units.UnitsType units) {
        this.units = units;
        SafetyStop defaultSafetyStop = SafetyStop.getDefault();
        depth = defaultSafetyStop.depthInMeters;
        durationMinutes = defaultSafetyStop.durationInMinutes;
    }

    public static AddSafetyStopViewModel fromModel(SafetyStop safetyStop, Units.UnitsType units) {
        AddSafetyStopViewModel result = new AddSafetyStopViewModel(units);
        result.durationMinutes = safetyStop.durationInMinutes;
        result.depth = Converter.convertDistance(safetyStop.depthInMeters, units);
        return result;
    }

    public Units.UnitsType getUnits() {
        return units;
    }

    @Bindable
    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer value) {
        if (depth == value) {
            return;
        }
        depth = value;
        notifyPropertyChanged(BR.depth);
    }

    @Bindable
    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer value) {
        if (durationMinutes == value) {
            return;
        }
        durationMinutes = value;
        notifyPropertyChanged(BR.durationMinutes);
    }

    public SafetyStop toModel() {
        //TODO: add units
        return new SafetyStop(getDepth(), getDurationMinutes());
    }
}
