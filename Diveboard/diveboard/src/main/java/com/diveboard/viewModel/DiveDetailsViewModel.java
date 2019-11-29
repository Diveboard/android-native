package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.Shop;
import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.SearchSpot;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.google.android.gms.common.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class DiveDetailsViewModel extends BaseObservable {
    @Bindable
    public ObservableArrayList<SafetyStopViewModel> safetyStops = new ObservableArrayList<>();
    public ObservableArrayList<Tank> tanks = new ObservableArrayList<>();
    @Bindable
    public ObservableArrayList<DiveTypeViewModel> diveTypes = new ObservableArrayList<>();
    @Bindable
    public int visibilityPosition = 0;
    @Bindable
    public int currentPosition = 0;
    public Units.UnitsType units;
    private Calendar diveDateTime;
    private List<String> currentDictionary;
    private List<String> visibilityDictionary;
    private SearchSpot spot;
    private List<String> tripNames = new ArrayList<>();
    //    private Dive model;
    private int number;
    private String tripName;
    private Double altitude;
    private Double maxDepth;
    private Integer duration;
    private Double weights;
    private Double tempSurface;
    private Double tempBottom;
    private Boolean isFreshWater;
    private String visibility;
    private String current;
    private Integer userId;
    private Integer id;
    private String shakenId;
    private String notes;
    private BuddyViewModel buddy;
    private Shop diveCenter;
    private String guide;

    public DiveDetailsViewModel(String[] visibilityDictionary, String[] currentDictionary, Units.UnitsType units, Integer userId) {
        this.visibilityDictionary = Arrays.asList(visibilityDictionary);
        this.currentDictionary = Arrays.asList(currentDictionary);
        this.units = units;
        this.userId = userId;
    }

    public static DiveDetailsViewModel createFromModel(Dive data, String[] visibilityDictionary, String[] currentDictionary, Units.UnitsType units) {
        DiveDetailsViewModel result = new DiveDetailsViewModel(visibilityDictionary, currentDictionary, units, data.userId);
        result.setId(data.id);
        result.setAltitude(data.altitude);
        result.setTripName(data.tripName);
        result.setMaxDepth(data.maxDepth);
        result.setDurationMin(data.durationMin);
        result.setWeights(data.weights);
        result.setAirTemp(data.airTemp);
        result.setWaterTemp(data.waterTemp);
        result.setDiveNumber(data.diveNumber);
        result.setWaterType(data.isFreshWater());
        result.setVisibility(data.visibility);
        result.setCurrent(data.current);
        result.setNotes(data.notes);
        result.diveDateTime = data.getTimeIn();
        result.spot = SearchSpot.createFromSpot(data.spot);
        result.diveDateTime = data.getTimeIn();
        result.shakenId = data.shakenId;
        result.buddy = new BuddyViewModel(data.buddies);
        result.diveCenter = data.diveCenter;
        result.guide = data.guide;

        for (SafetyStop ss : data.getSafetyStops()) {
            result.safetyStops.add(SafetyStopViewModel.fromModel(ss, units));
        }
        result.tanks.addAll(data.tanks);
        for (String diveType : data.divetype) {
            result.diveTypes.add(new DiveTypeViewModel(diveType));
        }
        return result;
    }

    public static DiveDetailsViewModel createNewDive(int diveNumber, String lastTripName, Units.UnitsType units, String[] visibilityDictionary, String[] currentDictionary, Integer userId) {
        DiveDetailsViewModel result = new DiveDetailsViewModel(visibilityDictionary, currentDictionary, units, userId);
        result.setDiveNumber(diveNumber);
        result.setTripName(lastTripName);
        result.diveDateTime = Calendar.getInstance();
        result.diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        result.diveDateTime.set(Calendar.MINUTE, 0);
        result.safetyStops.add(SafetyStopViewModel.fromModel(SafetyStop.getDefault(), units));
        result.shakenId = UUID.randomUUID().toString();
        return result;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public Double getMaxDepth() {
        return Converter.convertDistance(maxDepth, units);
    }

    public void setMaxDepth(Double maxDepth) {
        this.maxDepth = Converter.convertDistanceToMetric(maxDepth, units);
    }

    public Integer getDurationMin() {
        return duration;
    }

    public void setDurationMin(Integer durationMin) {
        this.duration = durationMin;
    }

    public Double getWeights() {
        return weights;
    }

    public void setWeights(Double weights) {
        this.weights = weights;
    }

    public Double getAirTemp() {
        return Converter.convertTemp(tempSurface, units);
    }

    public void setAirTemp(Double airTemp) {
        this.tempSurface = Converter.convertTempToMetric(airTemp, units);
    }

    public Double getWaterTemp() {
        return Converter.convertTemp(tempBottom, units);
    }

    public void setWaterTemp(Double temp) {
        tempBottom = Converter.convertTempToMetric(temp, units);
    }

    public Integer getDiveNumber() {
        return number;
    }

    public void setDiveNumber(Integer diveNumber) {
        this.number = diveNumber;
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

    public Dive getModel() {
        Dive result = new Dive();
        result.id = getId();
        result.setTimeIn(diveDateTime);
        result.durationMin = getDurationMin();
        result.maxDepth = getMaxDepth();
        result.userId = userId;
        result.spot = spot == null ? null : spot.toModel();
        result.airTemp = getAirTemp();
        result.waterTemp = getWaterTemp();
        result.weights = getWeights();
        result.visibility = Strings.isEmptyOrWhitespace(getVisibility()) ? null : getVisibility();
        result.tripName = getTripName();
        result.setFreshWater(isFreshWater);
        result.altitude = getAltitude();
        result.current = Strings.isEmptyOrWhitespace(getCurrent()) ? null : getCurrent();
        result.diveNumber = getDiveNumber();
        result.shakenId = shakenId;
        result.notes = getNotes();
        result.buddies = buddy.getModel();
        result.diveCenter = getDiveCenter();
        result.diveCenterId = getDiveCenter() != null ? getDiveCenter().id : null;
        result.guide = getGuide();

        ArrayList<SafetyStop> ssl = new ArrayList<>();
        for (SafetyStopViewModel ss : safetyStops) {
            ssl.add(ss.toModel());
        }
        result.setSafetyStops(ssl);

//TODO: copy tank models?
        result.tanks = tanks;

        if (!diveTypes.isEmpty()) {
            ArrayList<String> dts = new ArrayList<String>();
            for (DiveTypeViewModel dt : diveTypes) {
                dts.add(dt.diveType);
            }
            result.divetype = dts;
        }

        return result;
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
        visibility = visibilityDictionary.get(visibilityPosition);
        return visibility;
    }

    public void setVisibility(String value) {
        visibilityPosition = visibilityDictionary.indexOf(value);
        visibility = value;
        notifyPropertyChanged(BR.visibilityPosition);
    }

    public String getCurrent() {
        if (currentPosition == -1) {
            return null;
        }
        current = currentDictionary.get(currentPosition);
        return current;
    }

    public void setCurrent(String value) {
        currentPosition = currentDictionary.indexOf(value);
        current = value;
        notifyPropertyChanged(BR.currentPosition);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BuddyViewModel getBuddy() {
        return buddy;
    }

    public Shop getDiveCenter() {
        return diveCenter;
    }

    public void setDiveCenter(Shop shop) {
        this.diveCenter = shop;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }
}