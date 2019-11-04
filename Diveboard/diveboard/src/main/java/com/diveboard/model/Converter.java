package com.diveboard.model;

public class Converter {
    private static Double M2FT = 3.28084;
    private static Double _KgLbs = 2.2046;
    private static Double _BarPsi = 14.5137;

    public static Double convertTemp(Double tempInCelsius, Units.UnitsType to) {
        return tempInCelsius == null ? null : to == Units.UnitsType.Metric ? tempInCelsius : (Double) ((tempInCelsius * 1.8) + 32);
    }

    public static Double convertDistance(Double distanceInMeters, Units.UnitsType to) {
        return distanceInMeters == null ? null : to == Units.UnitsType.Metric ? distanceInMeters : (Double) (distanceInMeters * M2FT);
    }

    public static Double convertDistanceToMetric(Double distance, Units.UnitsType from) {
        return distance == null ? null : from == Units.UnitsType.Metric ? distance : (Double) (distance / M2FT);
    }

    public static Integer convertDistance(Integer distanceInMeters, Units.UnitsType to) {
        if (distanceInMeters == null) {
            return null;
        }
        return to == Units.UnitsType.Metric ? distanceInMeters : (int) Math.round((Double) (distanceInMeters * M2FT));
    }

    public static Double convertTempToMetric(Double temp, Units.UnitsType from) {
        if (temp == null) {
            return null;
        }
        return from == Units.UnitsType.Metric ? temp : (int) ((temp - 32) / 1.8);
    }
}