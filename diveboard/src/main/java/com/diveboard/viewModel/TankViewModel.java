package com.diveboard.viewModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.diveboard.mobile.BR;
import com.diveboard.model.Converter;
import com.diveboard.model.GasMixes;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;

import java.util.Arrays;
import java.util.List;

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
    private Double volume;
    private List<String> cylindersCountDictionary;

    public TankViewModel(Units.UnitsType units, String[] materialsDictionary, String[] gasMixDictionary, String[] cylindersCountDictionary) {
        this.units = units;
        this.materialsDictionary = Arrays.asList(materialsDictionary);
        this.gasMixDictionary = Arrays.asList(gasMixDictionary);
        this.cylindersCountDictionary = Arrays.asList(cylindersCountDictionary);
    }

    public static TankViewModel createDefault(Units.UnitsType units, String[] materialsDictionary, String[] gasMixDictionary, String[] cylindersCountDictionary) {
        TankViewModel result = new TankViewModel(units, materialsDictionary, gasMixDictionary, cylindersCountDictionary);
        result.setCylindersCount(1);
        result.setGasMix("air");
        result.setMaterial("steel");
        result.setStartPressure(units == Units.UnitsType.Metric ? 220.0 : 3200);
        result.setEndPressure(units == Units.UnitsType.Metric ? 50.0 : 750);
        result.setVolume(units == Units.UnitsType.Metric ? 12.0 : 80);
        result.setO2(21);
        result.setHe(0);
        return result;
    }

    public static TankViewModel fromModel(Tank tank, Units.UnitsType units, String[] materialsDictionary, String[] gasMixDictionary, String[] cylindersCountDictionary) {
        TankViewModel result = new TankViewModel(units, materialsDictionary, gasMixDictionary, cylindersCountDictionary);
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
        return Converter.convertPressure(startPressure, units);
    }

    public void setStartPressure(Double startPressure) {
        this.startPressure = Converter.convertPressureToMetric(startPressure, units);
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
        return Converter.convertPressure(endPressure, units);
    }

    public void setEndPressure(Double endPressure) {
        this.endPressure = Converter.convertPressureToMetric(endPressure, units);
    }

    public Double getVolume() {
        return Converter.convertVolume(volume, units);
    }

    public void setVolume(Double volume) {
        this.volume = Converter.convertVolumeToMetric(volume, units);
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
            throw new IllegalArgumentException("diveType provided is not from dictionary");
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
            throw new IllegalArgumentException("diveType provided is not from dictionary");
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
            throw new IllegalArgumentException("diveType provided is not from dictionary");
        }
        this.gasMixPosition = position;
        notifyPropertyChanged(BR.gasMixPosition);
    }

    public Tank toModel() {
        Tank result = new Tank();
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
