package com.diveboard.viewModel;

import com.diveboard.mobile.BR;
import com.diveboard.model.SafetyStop2;
import com.diveboard.model.Units;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class AddSafetyStopViewModel extends BaseObservable {
    private Units.UnitsType units;
    private Integer depth;
    private Integer durationMinutes;

    public AddSafetyStopViewModel(Units.UnitsType units) {
        this.units = units;
        SafetyStop2 defaultSafetyStop = SafetyStop2.getDefault(units);
        depth = defaultSafetyStop.depth;
        durationMinutes = defaultSafetyStop.duration;
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

    public SafetyStop2 toModel() {
        return new SafetyStop2(getDepth(), getDurationMinutes(), units);
    }
}
