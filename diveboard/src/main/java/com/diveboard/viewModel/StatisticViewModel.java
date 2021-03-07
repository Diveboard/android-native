package com.diveboard.viewModel;

import android.content.Context;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.mobile.R;
import com.diveboard.model.Converter;
import com.diveboard.model.Units;
import com.diveboard.util.Utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticViewModel {
    private final Units.UnitsType units;
    public int publishedDivesCount;
    public int divesCount;
    public int divesThisYear;
    public int totalTimeMinutes;
    public Dive maxDepthDive;
    public int diveSitesNumber;
    public int countriesNumber;
    public Double coldest;
    public Double warmest;
    public String mostDivesInCountry = "-";
    public Map<Integer, Integer> divesPerYear = new HashMap<>();
    public Dive maxTimeDive;
    public Dive warmestDive;
    public Dive coldestDive;
    public List<Country> countries = new ArrayList<>();
    private ArrayList<String> countryCodes = new ArrayList<>();
    private Map<String, Integer> divesPerCountry = new HashMap<>();
    private int maxTimeMinutes;
    private double maxDepth;
    private Context ctx;

    public StatisticViewModel(Context ctx, Units.UnitsType units) {
        this.ctx = ctx;
        this.units = units;
    }

    public static StatisticViewModel create(List<Dive> dives, Context ctx, Units.UnitsType units) {
        StatisticViewModel result = new StatisticViewModel(ctx, units);
        result.publishedDivesCount = dives.size();
        ArrayList<String> diveSitesIds = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);

        for (Dive dive : dives) {
            if (dive.diveNumber != null && dive.diveNumber > result.divesCount) {
                result.divesCount = dive.diveNumber;
            }
            Integer diveYear = 0;
            try {
                Calendar diveCalendar = Calendar.getInstance();
                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                diveCalendar.setTime(format.parse(dive.timeIn));
                diveYear = diveCalendar.get(Calendar.YEAR);
                if (diveYear == currentYear) {
                    result.divesThisYear++;
                }
                result.divesPerYear.put(diveYear, result.divesPerYear.containsKey(diveYear) ? result.divesPerYear.get(diveYear) + 1 : 1);
            } catch (ParseException e) {
                Utils.logError(StatisticViewModel.class, "Cannot parse dive date", e);
            }
            result.totalTimeMinutes += (dive.durationMin == null ? 0 : dive.durationMin);

            if (result.maxDepth < dive.maxDepth) {
                result.maxDepth = dive.maxDepth;
                result.maxDepthDive = dive;
            }

            if (result.maxTimeMinutes < dive.durationMin) {
                result.maxTimeMinutes = dive.durationMin;
                result.maxTimeDive = dive;
            }
            if (dive.spot != null) {
                String name = dive.spot.name;
                if (name != null && !diveSitesIds.contains(name)) {
                    diveSitesIds.add(name);
                }
                String countryCode = dive.spot.countryCode;
                if (countryCode != null && !result.countryCodes.contains(countryCode)) {
                    result.countryCodes.add(countryCode);
                    //put most recent year
                    boolean found = false;
                    for (Country c : result.countries) {
                        if (c.name.equals(dive.spot.countryName)) {
                            if (c.year < diveYear) {
                                c.year = diveYear;
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        result.countries.add(new Country(dive.spot.countryName, diveYear));
                    }
                }
                if (countryCode != null) {
                    if (result.divesPerCountry.containsKey(dive.spot.countryName)) {
                        result.divesPerCountry.put(dive.spot.countryName, result.divesPerCountry.get(dive.spot.countryName) + 1);
                    } else {
                        result.divesPerCountry.put(dive.spot.countryName, 1);
                    }
                }
            }
            if (dive.waterTemp != null) {
                if (result.coldest == null || result.coldest > dive.waterTemp) {
                    result.coldest = dive.waterTemp;
                    result.coldestDive = dive;
                }
                if (result.warmest == null || result.warmest < dive.waterTemp) {
                    result.warmest = dive.waterTemp;
                    result.warmestDive = dive;
                }
            }
        }
        result.countriesNumber = result.countries.size();
        result.diveSitesNumber = diveSitesIds.size();
        result.divesCount = Math.max(result.divesCount, dives.size());

        //calculate most popular country
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : result.divesPerCountry.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        result.mostDivesInCountry = maxEntry == null ? ctx.getString(R.string.NA) : maxEntry.getKey();
        return result;
    }

    public String getColdest() {
        return getTemp(coldest);
    }

    public String getWarmest() {
        return getTemp(warmest);
    }

    public String getMaxTime() {
        return maxTimeMinutes + " " + getString(R.string.unit_min);
    }

    private String getTemp(Double temp) {
        if (temp == null) {
            return "--";
        }
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(1);
        return formatter.format(Converter.convertTemp(temp, units)) + " " + getString(units == Units.UnitsType.Metric ? R.string.unit_C_symbol : R.string.unit_F_symbol);
    }

    public String getMaxDepth() {
        NumberFormat df = NumberFormat.getInstance();
        df.setMaximumFractionDigits(units == Units.UnitsType.Metric ? 1 : 0);
        return df.format(Converter.convertDistance(maxDepth, units)) + " " + getString(units == Units.UnitsType.Metric ? R.string.unit_m : R.string.unit_ft);
    }

    public String getMaxDepthSubtitle() {
        return getDiveSubtitle(maxDepthDive);
    }

    public String getMaxTimeSubtitle() {
        return getDiveSubtitle(maxTimeDive);
    }

    public String getColdestSubtitle() {
        return getDiveSubtitle(coldestDive);
    }

    public String getWarmestSubtitle() {
        return getDiveSubtitle(warmestDive);
    }

    private String getDiveSubtitle(Dive dive) {
        if (dive == null || dive.spot == null || Utils.isNullOrEmpty(dive.spot.locationName)) {
            return "";
        }
        String result = getString(R.string.in) + " " + dive.spot.locationName;
        if (!Utils.isNullOrEmpty(dive.spot.countryName)) {
            result += ", " + dive.spot.countryName;
        }
        return result;
    }

    private String getString(int p) {
        return ctx.getResources().getString(p);
    }

    public String getTotalTime() {
        String result = "";
        double years = totalTimeMinutes / (12 * 30 * 24 * 60);
        if (years > 1.0) {
            result += (int) years + getString(R.string.y) + " ";
        }
        double months = totalTimeMinutes / (30 * 24 * 60) - Math.floor(years);
        if (months > 1.0) {
            result += (int) months + getString(R.string.m) + " ";
        }
        double days = totalTimeMinutes / (24 * 60) - Math.floor(years) * 12 * 30 - Math.floor(months) * 30;
        if (days > 1.0) {
            result += (int) days + getString(R.string.d) + " ";
        }
        double hours = totalTimeMinutes / (60) - Math.floor(years) * 12 * 30 * 24 - Math.floor(months) * 30 * 24 - Math.floor(days) * 24;
        if (hours > 1.0) {
            result += (int) hours + getString(R.string.h) + " ";
        }
        return result;
    }

    public static class Country {
        public final String name;
        public int year;

        public Country(String name, Integer year) {
            this.name = name;
            this.year = year;
        }
    }
}
