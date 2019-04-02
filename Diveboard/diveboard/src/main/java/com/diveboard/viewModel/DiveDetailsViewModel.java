package com.diveboard.viewModel;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.SearchSpot;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

public class DiveDetailsViewModel extends BaseObservable {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @Bindable
    public ObservableArrayList<AddSafetyStopViewModel> safetyStops = new ObservableArrayList<>();
    public ObservableArrayList<Tank> tanks = new ObservableArrayList<>();
    public Integer diveNumber = 0;
    public Double airTemp;
    public Double waterTemp;
    @Bindable
    public ObservableArrayList<DiveTypeViewModel> diveTypes = new ObservableArrayList<>();
    public String visibility;
    public String current;
    public Double weights;
    public Double altitude;
    public Calendar diveDateTime;
    public String tripName;
    public Double maxDepth;
    public Integer durationMin;
    @Bindable
    public int visibilityPosition = 0;
    @Bindable
    public int currentPosition = 0;
    public Units.UnitsType units;
    private Boolean isFreshWater;
    private List<String> currentDictionary;
    private List<String> visibilityDictionary;
    private SearchSpot spot;
    private List<String> tripNames = new ArrayList<>();

    public DiveDetailsViewModel(String[] visibilityDictionary, String[] currentDictionary, Units.UnitsType units) {
        this.visibilityDictionary = Arrays.asList(visibilityDictionary);
        this.currentDictionary = Arrays.asList(currentDictionary);
        this.units = units;
    }

    public static DiveDetailsViewModel createNewDive(int diveNumber, String lastTripName, Units.UnitsType units, String[] visibilityDictionary, String[] currentDictionary) {
        DiveDetailsViewModel result = new DiveDetailsViewModel(visibilityDictionary, currentDictionary, units);
        result.diveNumber = diveNumber;
        result.airTemp = null;
        result.waterTemp = null;
        result.diveDateTime = Calendar.getInstance();
        result.diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        result.diveDateTime.set(Calendar.MINUTE, 0);
        result.tripName = lastTripName;
        result.safetyStops.add(AddSafetyStopViewModel.fromModel(SafetyStop.getDefault(), units));
        return result;
    }

    public static DiveDetailsViewModel createFromModel(Dive data, String[] visibilityDictionary, String[] currentDictionary, Units.UnitsType units) {
        DiveDetailsViewModel result = new DiveDetailsViewModel(visibilityDictionary, currentDictionary, units);
        result.diveNumber = data.number;
        result.airTemp = Converter.convertTemp(data.tempSurface, units);
        result.waterTemp = Converter.convertTemp(data.tempBottom, units);
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime(dateFormat.parse(data.timeIn));
            result.diveDateTime = calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result.tripName = data.tripName;
        result.spot = SearchSpot.createFromSpot(data.spot);
        result.maxDepth = Converter.convertDistance(data.maxDepth, units);
        result.durationMin = data.duration;
        for (SafetyStop ss : data.getSafetyStops()) {
            result.safetyStops.add(AddSafetyStopViewModel.fromModel(ss, units));
        }
        result.weights = data.weights;
        result.tanks.addAll(data.tanks);
        for (String diveType : data.divetype) {
            result.diveTypes.add(new DiveTypeViewModel(diveType));
        }
        result.setWaterType(data.isFreshWater());
        result.visibility = data.visibility;
        result.current = data.current;
        result.altitude = data.altitude;
        return result;
    }

    @Bindable
    public boolean getSaltWaterChecked() {
        return isFreshWater != null && !isFreshWater;
    }

    @Bindable
    public void setSaltWaterChecked(boolean value) {
        setWaterType(!value);
    }

    @Bindable
    public boolean getFreshWaterChecked() {
        return isFreshWater != null && isFreshWater;
    }

    @Bindable
    public void setFreshWaterChecked(boolean value) {
        setWaterType(value);
    }

    private void setWaterType(Boolean isFreshWater) {
        if (this.isFreshWater == isFreshWater) {
            return;
        }
        this.isFreshWater = isFreshWater;
        notifyPropertyChanged(BR.freshWaterChecked);
        notifyPropertyChanged(BR.saltWaterChecked);
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
        return tripNames;
    }

    public void setTripNames(List<String> tripNames) {
        this.tripNames = tripNames;
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
