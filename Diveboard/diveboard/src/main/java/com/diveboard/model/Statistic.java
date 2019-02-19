package com.diveboard.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public final Double coldest;
    public final Double warmest;

    public Statistic(int publishedDivesCount, int divesCount, int divesThisYear, int totalTime, double maxDepth, int maxTimeMinutes, int diveSitesNumber, int countriesNumber, Double coldest, Double warmest) {
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
        ArrayList<String> diveSitesIds = new ArrayList<>();
        ArrayList<String> countries = new ArrayList<>();
        Double coldest = null;
        Double warmest = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);

        for (Dive dive : dives) {
            if (dive.getNumber() > divesCount) {
                divesCount = dive.getNumber();
            }

            try {
                Calendar diveCalendar = Calendar.getInstance();
                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                diveCalendar.setTime(format.parse(dive.getDate()));
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
                String name = dive.getSpot().getName();
                if (name != null && !diveSitesIds.contains(name)) {
                    diveSitesIds.add(name);
                }
                String countryId = dive.getSpot().getCountryCode();
                if (countryId != null && !countries.contains(countryId)) {
                    countries.add(countryId);
                }
            }
            if (dive.getTempBottomCelcius() != null) {
                if (coldest == null || coldest > dive.getTempBottomCelcius()) {
                    coldest = dive.getTempBottomCelcius();
                }
                if (warmest == null || warmest < dive.getTempBottomCelcius()) {
                    warmest = dive.getTempBottomCelcius();
                }
            }
        }

        return new Statistic(publishedDivesCount, divesCount, divesThisYear, totalUnderwaterTimeMinutes, maxDepth, maxTime, diveSitesIds.size(), countries.size(), coldest, warmest);
    }
}
