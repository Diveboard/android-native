package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.Picture;
import com.diveboard.dataaccess.datamodel.Shop;
import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.diveboard.util.Utils;
import com.google.android.gms.common.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DiveDetailsViewModel extends BaseObservable {
    private static Dive sourceDive;
    @Bindable
    public ObservableArrayList<SafetyStopViewModel> safetyStops = new ObservableArrayList<>();
    public ObservableArrayList<TankViewModel> tanks = new ObservableArrayList<>();
    @Bindable
    public ObservableArrayList<DiveTypeViewModel> diveTypes = new ObservableArrayList<>();
    @Bindable
    public int visibilityPosition = 0;
    @Bindable
    public int currentPosition = 0;
    public Units.UnitsType units;
    public List<Picture> pictures = new ArrayList();
    private Calendar diveDateTime;
    private List<String> currentDictionary;
    private List<String> visibilityDictionary;
    private Spot spot;
    private List<String> tripNames = new ArrayList<>();
    private Integer diveNumber;
    private String tripName;
    private Double altitude;
    private Double maxDepth;
    private Integer durationMin;
    private Double weights;
    private Double airTemp;
    private Double waterTemp;
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

    public static DiveDetailsViewModel createNewDive(int diveNumber,
                                                     String lastTripName,
                                                     Units.UnitsType units,
                                                     String[] visibilityDictionary,
                                                     String[] currentDictionary,
                                                     Integer userId,
                                                     String[] materialsDictionary,
                                                     String[] gasMixDictionary,
                                                     String[] cylindersCountDictionary,
                                                     List<String> tripNames) {
        Dive dive = new Dive();
        dive.userId = userId;
        dive.diveNumber = diveNumber;
        dive.tripName = lastTripName;
        dive.durationMin = 0;
        dive.maxDepth = 0.0;

        Calendar diveDateTime = Calendar.getInstance();
        diveDateTime.set(Calendar.HOUR_OF_DAY, 10);
        diveDateTime.set(Calendar.MINUTE, 0);
        dive.setTimeIn(diveDateTime);

        dive.setSafetyStops(Collections.singletonList(SafetyStop.getDefault()));
        dive.shakenId = UUID.randomUUID().toString();

        return createFromModel(dive,
                visibilityDictionary,
                currentDictionary,
                units,
                materialsDictionary,
                gasMixDictionary,
                cylindersCountDictionary,
                tripNames);
    }

    public static DiveDetailsViewModel createFromModel(Dive data,
                                                       String[] visibilityDictionary,
                                                       String[] currentDictionary,
                                                       Units.UnitsType units,
                                                       String[] materialsDictionary,
                                                       String[] gasMixDictionary,
                                                       String[] cylindersCountDictionary,
                                                       List<String> tripNames) {
        sourceDive = data;
        DiveDetailsViewModel result = new DiveDetailsViewModel(visibilityDictionary, currentDictionary, units, data.userId);
        result.tripNames = tripNames;
        result.id = data.id;
        result.altitude = data.altitude;
        result.setTripName(data.tripName);
        result.maxDepth = data.maxDepth;
        result.durationMin = data.durationMin;
        //don't use setters, they convert into user locale
        result.weights = data.weights;
        result.airTemp = data.airTemp;
        result.waterTemp = data.waterTemp;
        result.diveNumber = data.diveNumber;
        result.setWaterType(data.isFreshWater());
        result.setVisibility(data.visibility);
        result.setCurrent(data.current);
        result.notes = data.notes;
        result.spot = data.spot;
        result.diveDateTime = data.getTimeIn();
        result.shakenId = data.shakenId;
        result.buddy = new BuddyViewModel(data.buddies);
        result.diveCenter = data.diveCenter;
        result.guide = data.guide;

        for (SafetyStop ss : data.getSafetyStops()) {
            result.safetyStops.add(SafetyStopViewModel.fromModel(ss, units));
        }

        for (Tank tank : data.tanks) {
            result.tanks.add(TankViewModel.fromModel(tank, units, materialsDictionary, gasMixDictionary, cylindersCountDictionary));
        }

        for (String diveType : data.divetype) {
            result.diveTypes.add(new DiveTypeViewModel(diveType));
        }

        result.pictures.addAll(data.pictures);
        return result;
    }

    public String getShakenId() {
        return shakenId;
    }

    public Dive getModel() {
        Dive result = new Dive();
        result.id = id;
        result.setTimeIn(diveDateTime);
        result.durationMin = durationMin;
        result.maxDepth = maxDepth;
        result.userId = userId;
        result.spot = spot;
        //don't use getters, they convert into user locale
        result.airTemp = airTemp;
        result.waterTemp = waterTemp;
        result.weights = weights;
        result.visibility = Strings.isEmptyOrWhitespace(getVisibility()) ? null : getVisibility();
        result.tripName = tripName;
        result.setFreshWater(isFreshWater);
        result.altitude = altitude;
        result.current = Strings.isEmptyOrWhitespace(getCurrent()) ? null : getCurrent();
        result.diveNumber = diveNumber;
        result.shakenId = shakenId;
        result.notes = notes;
        result.buddies = buddy.getModel();
        result.diveCenter = diveCenter;
        result.diveCenterId = diveCenter != null ? diveCenter.id : null;
        result.guide = guide;

        ArrayList<SafetyStop> ssl = new ArrayList<>();
        for (SafetyStopViewModel ss : safetyStops) {
            ssl.add(ss.toModel());
        }
        result.setSafetyStops(ssl);

        ArrayList<Tank> tnks = new ArrayList<>();
        for (TankViewModel tnk : tanks) {
            tnks.add(tnk.toModel());
        }
        result.tanks = tnks;

        if (!diveTypes.isEmpty()) {
            ArrayList<String> dts = new ArrayList<String>();
            for (DiveTypeViewModel dt : diveTypes) {
                dts.add(dt.diveType);
            }
            result.divetype = dts;
        }

        result.pictures = pictures;

        return result;
    }

    public boolean isModified() {
        return !sourceDive.equals(getModel());
    }

    public Double getAltitude() {
        return Converter.convertDistance(altitude, units);
    }

    public void setAltitude(Double altitude) {
        this.altitude = Converter.convertDistanceToMetric(altitude, units);
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = Utils.isNullOrEmpty(tripName) ? null : tripName;
    }

    public Double getMaxDepth() {
        return Converter.convertDistance(maxDepth, units);
    }

    public void setMaxDepth(Double maxDepth) {
        this.maxDepth = Converter.convertDistanceToMetric(maxDepth, units);
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
    }

    public Double getWeights() {
        return Converter.convertWeight(weights, units);
    }

    public void setWeights(Double weights) {
        this.weights = Converter.convertWeightToMetric(weights, units);
    }

    public Double getAirTemp() {
        return Converter.convertTemp(airTemp, units);
    }

    public void setAirTemp(Double airTemp) {
        this.airTemp = Converter.convertTempToMetric(airTemp, units);
    }

    public Double getWaterTemp() {
        return Converter.convertTemp(waterTemp, units);
    }

    public void setWaterTemp(Double temp) {
        waterTemp = Converter.convertTempToMetric(temp, units);
    }

    public Integer getDiveNumber() {
        return diveNumber;
    }

    public void setDiveNumber(Integer diveNumber) {
        this.diveNumber = diveNumber;
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

    @Bindable
    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        this.spot = spot;
        notifyPropertyChanged(BR.spot);
    }

    public String getVisibility() {
        if (visibilityPosition == -1) {
            return null;
        }
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

    @Bindable
    public Shop getDiveCenter() {
        return diveCenter;
    }

    public void setDiveCenter(Shop shop) {
        this.diveCenter = shop;
        notifyPropertyChanged(BR.diveCenter);
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }
}