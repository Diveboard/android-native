package com.diveboard.model;

public class Volume {
    public Double value;
    public Units.UnitsType units;

    public Volume(Double value, Units.UnitsType units) {
        this.value = value;
        this.units = units;
    }
}
