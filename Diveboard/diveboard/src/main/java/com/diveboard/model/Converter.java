package com.diveboard.model;

public class Converter {
    private static Double M2FT = 3.28084;
    private static Double _KgLbs = 2.2046;
    private static Double _BarPsi = 14.5137;

    public static Double convertTemp(Double tempInCelsius, Units.UnitsType to) {
        return to == Units.UnitsType.Metric ? tempInCelsius : (Double) ((tempInCelsius * 1.8) + 32);
    }

    public static Double convertDistance(Double distanceInMeters, Units.UnitsType to) {
        return to == Units.UnitsType.Metric ? distanceInMeters : (Double) (distanceInMeters * M2FT);
    }

    public static Integer convertDistance(Integer distanceInMeters, Units.UnitsType to) {
        return to == Units.UnitsType.Metric ? distanceInMeters : (int) Math.round((Double) (distanceInMeters * M2FT));
    }
}