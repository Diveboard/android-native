package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Units {
    public enum UnitsType {
        Metric("Metric"),
        Imperial("Imperial");
        private final String text;

        UnitsType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum Distance {
        KM,
        FT
    }

    public enum Weight {
        KG,
        LBS
    }

    public enum Temperature {
        C,
        F
    }

    public enum Pressure {
        BAR,
        PSI
    }

    private static Distance _distanceUnit = Distance.KM;
    private static Weight _weightUnit = Weight.KG;
    private static Temperature _temperatureUnit = Temperature.C;
    private static Pressure _pressureUnit = Pressure.BAR;
    private final Distance mDistanceUnit;
    private final Weight mWeightUnit;
    private final Temperature mTemperatureUnit;
    private final Pressure mPressureUnit;

    public Distance getmDistanceUnit() {
        return mDistanceUnit;
    }

    public Weight getmWeightUnit() {
        return mWeightUnit;
    }

    public Temperature getmTemperatureUnit() {
        return mTemperatureUnit;
    }

    public Pressure getmPressureUnit() {
        return mPressureUnit;
    }

    public Units(final JSONObject json) throws JSONException {
        Units._distanceUnit = (json.getString("distance").compareTo("Km") == 0) ? Distance.KM : Distance.FT;
        Units._weightUnit = (json.getString("weight").compareTo("Kg") == 0) ? Weight.KG : Weight.LBS;
        Units._temperatureUnit = (json.getString("temperature").compareTo("C") == 0) ? Temperature.C : Temperature.F;
        Units._pressureUnit = (json.getString("pressure").compareTo("bar") == 0) ? Pressure.BAR : Pressure.PSI;
        mDistanceUnit = Units._distanceUnit;
        mWeightUnit = Units._weightUnit;
        mTemperatureUnit = Units._temperatureUnit;
        mPressureUnit = Units._pressureUnit;
    }

    public Units(UnitsType units) {
        Units._distanceUnit = units == UnitsType.Metric ? Distance.KM : Distance.FT;
        Units._weightUnit = units == UnitsType.Metric ? Weight.KG : Weight.LBS;
        Units._temperatureUnit = units == UnitsType.Metric ? Temperature.C : Temperature.F;
        Units._pressureUnit = units == UnitsType.Metric ? Pressure.BAR : Pressure.PSI;
        mDistanceUnit = Units._distanceUnit;
        mWeightUnit = Units._weightUnit;
        mTemperatureUnit = Units._temperatureUnit;
        mPressureUnit = Units._pressureUnit;
    }

    public final static Distance getDistanceUnit() {
        return _distanceUnit;
    }

    public void setDistanceUnit(Distance _distanceUnit) {
        Units._distanceUnit = _distanceUnit;
    }

    public final static Weight getWeightUnit() {
        return _weightUnit;
    }

    public void setWeightUnit(Weight _weightUnit) {
        Units._weightUnit = _weightUnit;
    }

    public final static Temperature getTemperatureUnit() {
        return _temperatureUnit;
    }

    public void setTemperatureUnit(Temperature _temperatureUnit) {
        Units._temperatureUnit = _temperatureUnit;
    }

    public final static Pressure getPressureUnit() {
        return _pressureUnit;
    }

    public void setPressureUnit(Pressure _pressureUnit) {
        Units._pressureUnit = _pressureUnit;
    }
}
