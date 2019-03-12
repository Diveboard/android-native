package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.diveboard.mobile.BR;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

public class AddSafetyStopViewModel extends BaseObservable {
    private Units.Depth units;
    private Integer depth;
    private Integer durationMinutes;

    public AddSafetyStopViewModel(Units.Depth units) {
        this.units = units;
        SafetyStop defaultSafetyStop = SafetyStop.getDefault(units);
        depth = defaultSafetyStop.getDepth();
        durationMinutes = defaultSafetyStop.getDuration();
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
        return new SafetyStop(getDepth(), getDurationMinutes(), units);
    }
}
