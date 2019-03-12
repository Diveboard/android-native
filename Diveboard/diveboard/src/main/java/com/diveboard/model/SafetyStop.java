package com.diveboard.model;

public class SafetyStop {
    private Integer _duration;
    private Integer _depth;
    private Units.Depth _unit;

    public SafetyStop(Integer depth, Integer duration, Units.Depth unit) {
        this._duration = duration;
        this._depth = depth;
        this._unit = unit;
    }

    public static SafetyStop getDefault(Units.Depth units) {
        return new SafetyStop(units == Units.Depth.Meters ? 5 : 15, 3, units);
    }

    public Integer getDuration() {
        return this._duration;
    }

    public void setDuration(Integer duration) {
        this._duration = duration;
    }

    public Integer getDepth() {
        return this._depth;
    }

    public void setDepth(Integer depth) {
        this._depth = depth;
    }

    public Units.Depth getUnit() {
        return this._unit;
    }

    public void setUnit(Units.Depth unit) {
        this._unit = unit;
    }
}
