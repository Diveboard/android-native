package com.diveboard.model;

public class SafetyStop2 {
    public Integer duration;
    public Integer depth;
    public Units.UnitsType unit;

    public SafetyStop2(Integer depth, Integer duration, Units.UnitsType unit) {
        this.duration = duration;
        this.depth = depth;
        this.unit = unit;
    }

    public static SafetyStop2 getDefault(Units.UnitsType units) {
        return new SafetyStop2(units == Units.UnitsType.Metric ? 5 : 15, 3, units);
    }
}
