package com.diveboard.viewModel;

import com.diveboard.model.Converter;
import com.diveboard.model.Statistic;
import com.diveboard.model.Units;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class StatisticViewModel {
    public final ArrayList<StatisticItem> items;

    public StatisticViewModel(ArrayList<StatisticItem> items) {
        this.items = items;
    }

    public static StatisticViewModel create(Statistic statistic, Units.UnitsType units) {
        ArrayList<StatisticItem> items = new ArrayList<>();
        items.add(new StatisticItem("Dives Count", Integer.toString(statistic.divesCount)));
//        items.add(new StatisticItem("Published Dives Count", Integer.toString(statistic.publishedDivesCount)));
        items.add(new StatisticItem("Dives This Year", Integer.toString(statistic.divesThisYear)));
        items.add(new StatisticItem("Total Time", getTimeFormatted(statistic.totalTime)));
        items.add(new StatisticItem("Max Depth", getMaxDepth(statistic.maxDepth, units)));
        items.add(new StatisticItem("Max Time", Integer.toString(statistic.maxTimeMinutes) + " min"));
        items.add(new StatisticItem("Dive Sites", Integer.toString(statistic.diveSitesNumber)));
        items.add(new StatisticItem("Countries", Integer.toString(statistic.countriesNumber)));
        items.add(new StatisticItem("Coldest", getTemp(statistic.coldest, units)));
        items.add(new StatisticItem("Warmest", getTemp(statistic.warmest, units)));
        return new StatisticViewModel(items);
    }

    private static String getTemp(Double temp, Units.UnitsType units) {
        if (temp == null) {
            return "--";
        }
        NumberFormat formatter = new DecimalFormat("#0.0");
        if (units == Units.UnitsType.Metric) {
            return formatter.format(temp) + " C";
        } else {
            return formatter.format(Converter.convert(temp, Units.Temperature.C, Units.Temperature.F)) + " F";
        }
    }

    private static String getMaxDepth(Double maxDepth, Units.UnitsType units) {
        NumberFormat formatter = new DecimalFormat("#0.0");
        if (units == Units.UnitsType.Metric) {
            return formatter.format(maxDepth) + " m";
        } else {
            return formatter.format(Converter.convert(maxDepth, Units.Distance.KM, Units.Distance.FT)) + " ft";
        }
    }

    private static String getTimeFormatted(int totalTime) {
        String result = "";
        double years = totalTime / (12 * 30 * 24 * 60);
        if (years > 1.0) {
            result += (int) years + "y ";
        }
        double months = totalTime / (30 * 24 * 60) - Math.floor(years);
        if (months > 1.0) {
            result += (int) months + "m ";
        }
        double days = totalTime / (24 * 60) - Math.floor(years) * 12 * 30 - Math.floor(months) * 30;
        if (days > 1.0) {
            result += (int) days + "d ";
        }
        double hours = totalTime / (60) - Math.floor(years) * 12 * 30 * 24 - Math.floor(months) * 30 * 24 - Math.floor(days) * 24;
        if (hours > 1.0) {
            result += (int) hours + "h ";
        }
        return result;
    }
}
