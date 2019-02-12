package com.diveboard.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Statistic {
    public final int publishedDivesCount;
    public final int divesCount;
    public final int divesThisYear;
    public final int totalTime;
    public final double maxDepth;
    public final int maxTimeMinutes;
    public final int diveSitesNumber;
    public final int countriesNumber;
    public final double coldest;
    public final double warmest;

    public Statistic(int publishedDivesCount, int divesCount, int divesThisYear, int totalTime, double maxDepth, int maxTimeMinutes, int diveSitesNumber, int countriesNumber, double coldest, double warmest) {
        this.publishedDivesCount = publishedDivesCount;
        this.divesCount = divesCount;
        this.divesThisYear = divesThisYear;
        this.totalTime = totalTime;
        this.maxDepth = maxDepth;
        this.maxTimeMinutes = maxTimeMinutes;
        this.diveSitesNumber = diveSitesNumber;
        this.countriesNumber = countriesNumber;
        this.coldest = coldest;
        this.warmest = warmest;
    }

    public static Statistic create(ArrayList<Dive> dives) {
        int publishedDivesCount = dives.size();
        int divesCount = 0;
        int divesThisYear = 0;
        int totalUnderwaterTimeMinutes = 0;
        double maxDepth = 0;
        int maxTime = 0;
        ArrayList<Integer> diveSitesIds = new ArrayList<Integer>();
        ArrayList<Integer> countries = new ArrayList<Integer>();
        double coldest = 0;
        double warmest = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);

        for (Dive dive : dives) {
            if (dive.getNumber() > divesCount) {
                divesCount = dive.getNumber();
            }

            try {
                Calendar diveCalendar = Calendar.getInstance();
                diveCalendar.setTime((DateFormat.getDateInstance()).parse(dive.getDate()));
                if (diveCalendar.get(Calendar.YEAR) == currentYear) {
                    divesThisYear++;
                }
            } catch (ParseException e) {

            }
            totalUnderwaterTimeMinutes += (dive.getDuration() == null ? 0 : dive.getDuration());

            if (maxDepth < dive.getMaxDepthInMeters()) {
                maxDepth = dive.getMaxDepthInMeters();
            }

            if (maxTime < dive.getDuration()) {
                maxTime = dive.getDuration();
            }
            if (dive.getSpot() != null) {
                if (dive.getSpot().getId() != null && diveSitesIds.contains(dive.getSpot().getId())) {
                    diveSitesIds.add(dive.getSpot().getId());
                }
                if (dive.getSpot().getCountryId() != null && countries.contains(dive.getSpot().getCountryId())) {
                    countries.add(dive.getSpot().getCountryId());
                }
            }
            if (dive.getTempBottomCelcius() != null) {
                if (coldest > dive.getTempBottomCelcius()) {
                    coldest = dive.getTempBottomCelcius();
                }
                if (warmest < dive.getTempBottomCelcius()) {
                    warmest = dive.getTempBottomCelcius();
                }
            }
        }

        return new Statistic(publishedDivesCount, divesCount, divesThisYear, totalUnderwaterTimeMinutes, maxDepth, maxTime, diveSitesIds.size(), countries.size(), coldest, warmest);
    }
}
