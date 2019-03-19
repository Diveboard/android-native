package com.diveboard.viewModel;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.BR;
import com.diveboard.mobile.DiveType;
import com.diveboard.model.Distance2;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Spot2;
import com.diveboard.model.Temperature;
import com.diveboard.model.Units;

import java.util.Calendar;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

public class DiveDetailsViewModel extends BaseObservable {
    @Bindable
    public ObservableArrayList<SafetyStop> safetyStops = new ObservableArrayList<>();
    public Integer diveNumber = 0;
    public Temperature airTemp;
    public Temperature waterTemp;
    @Bindable
    public ObservableArrayList<DiveType> diveTypes = new ObservableArrayList<>();
    public Boolean isFreshWater;
    public String visibility;
    public String current;
    public Distance2 altitude;
    public Calendar diveDateTime;
    public String tripName;
    public Distance2 maxDepth;
    public Integer durationMin;
    private Spot2 spot;

    public static DiveDetailsViewModel createNewDive(int diveNumber, String lastTripName, Units.UnitsType units) {
        DiveDetailsViewModel result = new DiveDetailsViewModel();
        result.diveNumber = diveNumber;
        result.diveDateTime = Calendar.getInstance();
        result.diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        result.diveDateTime.set(Calendar.MINUTE, 0);
        result.tripName = lastTripName;
        result.safetyStops.add(SafetyStop.getDefault(units == Units.UnitsType.Metric ? Units.Depth.Meters : Units.Depth.Foots));
        return result;
    }

    public Calendar getUiDate() {
        return diveDateTime;
    }

    public void setUiDate(Calendar uiDate) {
        diveDateTime.set(Calendar.YEAR, uiDate.get(Calendar.YEAR));
        diveDateTime.set(Calendar.MONTH, uiDate.get(Calendar.MONTH));
        diveDateTime.set(Calendar.DAY_OF_MONTH, uiDate.get(Calendar.DAY_OF_MONTH));
    }

    public Calendar getUiTime() {
        return diveDateTime;
    }

    public void setUiTime(Calendar uiTime) {
        diveDateTime.set(Calendar.HOUR_OF_DAY, uiTime.get(Calendar.HOUR_OF_DAY));
        diveDateTime.set(Calendar.MINUTE, uiTime.get(Calendar.MINUTE));
    }

    public List<String> getTripNames() {
        return ApplicationController.getInstance().getModel().getUser().getTripNames();
    }

    @Bindable
    public Spot2 getSpot() {
        return spot;
    }

    public void setSpot(Spot2 spot) {
        this.spot = spot;
        notifyPropertyChanged(BR.spot);
    }
}
