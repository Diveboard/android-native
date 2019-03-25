package com.diveboard.viewModel;

import com.diveboard.mobile.BR;
import com.diveboard.model.GasMixes;
import com.diveboard.model.Tank2;
import com.diveboard.model.Units;
import com.diveboard.util.ResourceHolder;

import java.util.Arrays;
import java.util.List;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class TankViewModel extends BaseObservable {
    private final List<String> materialsDictionary;
    private final List<String> gasMixDictionary;
    @Bindable
    public int cylindersCountPosition;
    @Bindable
    public int materialPosition;
    public int gasMixPosition;
    public Units.UnitsType units;
    private Double startPressure;
    private Integer o2;
    private Integer he;
    private Double endPressure;
    private Integer volume;
    private List<String> cylindersCountDictionary;

    public TankViewModel(Units.UnitsType units, ResourceHolder resourceHolder) {
        this.units = units;
        this.cylindersCountDictionary = Arrays.asList(resourceHolder.getCylindersCountValues());
        this.materialsDictionary = Arrays.asList(resourceHolder.getMaterialsValues());
        this.gasMixDictionary = Arrays.asList(resourceHolder.getGasMixValues());
    }

    public static TankViewModel createDefault(Units.UnitsType units, ResourceHolder resourceHolder) {
        TankViewModel result = new TankViewModel(units, resourceHolder);
        result.setCylindersCount(1);
        result.setGasMix("air");
        result.setMaterial("steel");
        result.setStartPressure(units == Units.UnitsType.Metric ? 220.0 : 3200);
        result.setEndPressure(units == Units.UnitsType.Metric ? 50.0 : 750);
        result.setVolume(units == Units.UnitsType.Metric ? 12 : 80);
        result.setO2(21);
        result.setHe(0);
        return result;
    }

    public static TankViewModel fromModel(Tank2 tank, Units.UnitsType unitsTyped, ResourceHolder resourceHolder) {
        TankViewModel result = new TankViewModel(unitsTyped, resourceHolder);
        result.setCylindersCount(tank.cylindersCount);
        result.setGasMix(tank.gasType);
        result.setMaterial(tank.material);
        result.setStartPressure(tank.startPressure);
        result.setEndPressure(tank.endPressure);
        result.setVolume(tank.volume);
        result.setO2(tank.o2 == null ? 21 : tank.o2);
        result.setHe(tank.he == null ? 0 : tank.he);
        return result;
    }

    public Double getStartPressure() {
        return startPressure;
    }

    public void setStartPressure(Double startPressure) {
        this.startPressure = startPressure;
    }

    public Integer getO2() {
        return o2;
    }

    public void setO2(Integer o2) {
        this.o2 = o2;
    }

    public Integer getHe() {
        return he;
    }

    public void setHe(Integer he) {
        this.he = he;
    }

    public Double getEndPressure() {
        return endPressure;
    }

    public void setEndPressure(Double endPressure) {
        this.endPressure = endPressure;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    @Bindable
    public int getGasMixPosition() {
        return gasMixPosition;
    }

    public void setGasMixPosition(int gasMixPosition) {
        this.gasMixPosition = gasMixPosition;
        notifyPropertyChanged(BR.gasMixPosition);
    }

    public int getCylindersCount() {
        return Integer.valueOf(cylindersCountDictionary.get(cylindersCountPosition));
    }

    public void setCylindersCount(int value) {
        int position = cylindersCountDictionary.indexOf(Integer.toString(value));
        if (position == -1) {
            throw new IllegalArgumentException("value provided is not from dictionary");
        }
        this.cylindersCountPosition = position;
        notifyPropertyChanged(BR.cylindersCountPosition);
    }

    public String getMaterial() {
        return materialsDictionary.get(materialPosition);
    }

    public void setMaterial(String value) {
        int position = materialsDictionary.indexOf(value);
        if (position == -1) {
            throw new IllegalArgumentException("value provided is not from dictionary");
        }
        this.materialPosition = position;
        notifyPropertyChanged(BR.materialPosition);
    }

    public String getGasMix() {
        return gasMixDictionary.get(gasMixPosition);
    }

    public void setGasMix(String value) {
        int position = gasMixDictionary.indexOf(value);
        if (position == -1) {
            throw new IllegalArgumentException("value provided is not from dictionary");
        }
        this.gasMixPosition = position;
        notifyPropertyChanged(BR.gasMixPosition);
    }

    public Tank2 toModel() {
        Tank2 result = new Tank2();
        result.cylindersCount = getCylindersCount();
        result.gasType = getGasMix();
        if (GasMixes.NITROX.equals(result.gasType) || GasMixes.TRIMIX.equals(result.gasType)) {
            result.o2 = o2;
        }
        if (GasMixes.TRIMIX.equals(result.gasType)) {
            result.he = he;
        }
        result.material = getMaterial();
        result.volume = volume;
        result.startPressure = startPressure;
        result.endPressure = endPressure;
        return result;
    }
}
