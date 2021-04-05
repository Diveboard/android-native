package com.diveboard.model;

public class Temperature {
    public Double value;
    public Units.UnitsType unit;

    public Temperature(Double value, Units.UnitsType unit) {
        this.value = value;
        this.unit = unit;
    }
}

