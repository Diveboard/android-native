package com.diveboard.viewModel;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.BR;
import com.diveboard.mobile.DiveType;
import com.diveboard.model.Distance2;
import com.diveboard.model.SafetyStop2;
import com.diveboard.model.SearchSpot;
import com.diveboard.model.Tank2;
import com.diveboard.model.Temperature;
import com.diveboard.model.Units;
import com.diveboard.model.WaterType;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

public class DiveDetailsViewModel extends BaseObservable {
    @Bindable
    public ObservableArrayList<SafetyStop2> safetyStops = new ObservableArrayList<>();
    public ObservableArrayList<Tank2> tanks = new ObservableArrayList<>();
    public Integer diveNumber = 0;
    public Temperature airTemp;
    public Temperature waterTemp;
    @Bindable
    public ObservableArrayList<DiveType> diveTypes = new ObservableArrayList<>();
    public Boolean isFreshWater;
    public String visibility;
    public String current;
    public Double weights;
    public Distance2 altitude;
    public Calendar diveDateTime;
    public String tripName;
    public Distance2 maxDepth;
    public Integer durationMin;
    @Bindable
    public int visibilityPosition = 0;
    @Bindable
    public int currentPosition = 0;
    public Units.UnitsType units;
    private boolean freshWaterChecked = false;
    private boolean saltWaterChecked;
    private List<String> currentDictionary;
    private List<String> visibilityDictionary;
    private SearchSpot spot;

    public static DiveDetailsViewModel createNewDive(int diveNumber, String lastTripName, Units.UnitsType units, String[] visibilityDictionary, String[] currentDictionary) {
        DiveDetailsViewModel result = new DiveDetailsViewModel();
        result.visibilityDictionary = Arrays.asList(visibilityDictionary);
        result.currentDictionary = Arrays.asList(currentDictionary);
        result.diveNumber = diveNumber;
        result.units = units;
        result.airTemp = new Temperature(null, units);
        result.waterTemp = new Temperature(null, units);
        result.diveDateTime = Calendar.getInstance();
        result.diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        result.diveDateTime.set(Calendar.MINUTE, 0);
        result.tripName = lastTripName;
        result.safetyStops.add(SafetyStop2.getDefault(units));
        return result;
    }

    @Bindable
    public boolean getSaltWaterChecked() {
        return saltWaterChecked;
    }

    @Bindable
    public void setSaltWaterChecked(boolean value) {
        saltWaterChecked = value;
        if (value == true) {
            freshWaterChecked = !value;
            notifyPropertyChanged(BR.freshWaterChecked);
        }
    }

    @Bindable
    public boolean getFreshWaterChecked() {
        return freshWaterChecked;
    }

    @Bindable
    public void setFreshWaterChecked(boolean value) {
        freshWaterChecked = value;
        if (value == true) {
            saltWaterChecked = !value;
            notifyPropertyChanged(BR.saltWaterChecked);
        }
    }

    public WaterType getWaterType() {
        if (freshWaterChecked) {
            return WaterType.Fresh;
        }
        if (saltWaterChecked) {
            return WaterType.Salt;
        }
        return null;
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
    public SearchSpot getSpot() {
        return spot;
    }

    public void setSpot(SearchSpot spot) {
        this.spot = spot;
        notifyPropertyChanged(BR.spot);
    }

    public String getVisibility() {
        return visibilityDictionary.get(visibilityPosition);
    }

    public void setVisibility(String value) {
        visibilityPosition = visibilityDictionary.indexOf(value);
        notifyPropertyChanged(BR.visibilityPosition);
    }

    public String getCurrent() {
        return currentDictionary.get(currentPosition);
    }

    public void setCurrent(String value) {
        currentPosition = currentDictionary.indexOf(value);
        notifyPropertyChanged(BR.currentPosition);
    }
}
