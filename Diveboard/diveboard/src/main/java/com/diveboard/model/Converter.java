package com.diveboard.model;

public class Converter {
    private static Double M2FT = 3.28084;
    private static Double KgToLbs = 2.2046;
    private static Double LitreToCuft = 0.0353147;
    private static Double BarToPsi = 14.5137;

    public static Double convertTemp(Double tempInCelsius, Units.UnitsType to) {
        return tempInCelsius == null ? null : to == Units.UnitsType.Metric ? tempInCelsius : (Double) ((tempInCelsius * 1.8) + 32);
    }

    public static Double convertDistance(Double distanceInMeters, Units.UnitsType to) {
        return distanceInMeters == null ? null : to == Units.UnitsType.Metric ? distanceInMeters : (Double) (distanceInMeters * M2FT);
    }

    public static Double convertDistanceToMetric(Double distance, Units.UnitsType from) {
        return distance == null ? null : from == Units.UnitsType.Metric ? distance : (Double) (distance / M2FT);
    }

    public static Integer convertDistanceToMetric(Integer distance, Units.UnitsType from) {
        return distance == null ? null : from == Units.UnitsType.Metric ? distance : (int) (distance / M2FT);
    }

    public static Double convertWeight(Double weightInKg, Units.UnitsType to) {
        return weightInKg == null ? null : to == Units.UnitsType.Metric ? weightInKg : (Double) (weightInKg * KgToLbs);
    }

    public static Double convertWeightToMetric(Double weight, Units.UnitsType from) {
        return weight == null ? null : from == Units.UnitsType.Metric ? weight : (Double) (weight / KgToLbs);
    }

    public static Double convertVolume(Double volumeInLitre, Units.UnitsType to) {
        return volumeInLitre == null ? null : to == Units.UnitsType.Metric ? volumeInLitre : (Double) (volumeInLitre * LitreToCuft);
    }

    public static Double convertVolumeToMetric(Double volume, Units.UnitsType from) {
        return volume == null ? null : from == Units.UnitsType.Metric ? volume : (Double) (volume / LitreToCuft);
    }

    public static Double convertPressure(Double pressureInBar, Units.UnitsType to) {
        return pressureInBar == null ? null : to == Units.UnitsType.Metric ? pressureInBar : (Double) (pressureInBar * BarToPsi);
    }

    public static Double convertPressureToMetric(Double pressure, Units.UnitsType from) {
        return pressure == null ? null : from == Units.UnitsType.Metric ? pressure : (Double) (pressure / BarToPsi);
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