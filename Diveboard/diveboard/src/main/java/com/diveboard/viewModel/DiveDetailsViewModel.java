package com.diveboard.viewModel;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Spot2;
import com.diveboard.model.Units;

import java.util.Calendar;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

public class DiveDetailsViewModel extends BaseObservable {
    @Bindable
    public ObservableArrayList<SafetyStop> safetyStops = new ObservableArrayList<>();
    private Integer diveNumber = 0;
    private Calendar diveDateTime;
    private String tripName;
    private Double maxDepth;
    private Integer durationMin;
    private Spot2 spot;

    public static DiveDetailsViewModel createNewDive(int diveNumber, String lastTripName, Units.UnitsType units) {
        DiveDetailsViewModel result = new DiveDetailsViewModel();
        result.diveNumber = diveNumber;
        result.diveDateTime = Calendar.getInstance();
        result.diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        result.diveDateTime.set(Calendar.MINUTE, 0);
        result.tripName = lastTripName;
        result.addSafetyStop(SafetyStop.getDefault(units == Units.UnitsType.Metric ? Units.Depth.Meters : Units.Depth.Foots));
        return result;
    }

    @Bindable
    public Integer getDiveNumber() {
        return diveNumber;
    }

    public void setDiveNumber(Integer diveNumber) {
        this.diveNumber = diveNumber;
    }

    @Bindable
    public Calendar getUiDate() {
        return diveDateTime;
    }

    public void setUiDate(Calendar uiDate) {
        diveDateTime.set(Calendar.YEAR, uiDate.get(Calendar.YEAR));
        diveDateTime.set(Calendar.MONTH, uiDate.get(Calendar.MONTH));
        diveDateTime.set(Calendar.DAY_OF_MONTH, uiDate.get(Calendar.DAY_OF_MONTH));
    }

    @Bindable
    public Calendar getUiTime() {
        return diveDateTime;
    }

    public void setUiTime(Calendar uiTime) {
        diveDateTime.set(Calendar.HOUR_OF_DAY, uiTime.get(Calendar.HOUR_OF_DAY));
        diveDateTime.set(Calendar.MINUTE, uiTime.get(Calendar.MINUTE));
    }

    @Bindable
    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    @Bindable
    public Double getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Double maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Bindable
    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public List<String> getTripNames() {
        return ApplicationController.getInstance().getModel().getUser().getTripNames();
    }

    public void addSafetyStop(SafetyStop safetyStop) {
        safetyStops.add(safetyStop);
    }

    public void removeSafetyStop(SafetyStop safetyStop) {
        safetyStops.remove(safetyStop);
    }

    public Calendar getDiveDateTime() {
        return diveDateTime;
    }

    public void setDiveDateTime(Calendar diveDateTime) {
        this.diveDateTime = diveDateTime;
    }

    public void setSpot(Spot2 spot) {
        this.spot = spot;
    }

    public Spot2 getSpot() {
        return spot;
    }
}
