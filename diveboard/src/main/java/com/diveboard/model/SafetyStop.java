package com.diveboard.model;

public class SafetyStop {
    public Integer durationInMinutes;
    public Integer depthInMeters;

    public SafetyStop(Integer depthInMeters, Integer durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
        this.depthInMeters = depthInMeters;
    }

    public static SafetyStop getDefault() {
        return new SafetyStop(5, 3);
    }
}
