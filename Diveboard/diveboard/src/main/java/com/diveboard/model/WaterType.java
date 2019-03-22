package com.diveboard.model;

public enum WaterType {
    Fresh("fresh"),
    Salt("salt");
    private final String text;

    WaterType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
