package com.diveboard.viewModel;

import android.text.TextUtils;

import com.diveboard.dataaccess.datamodel.Spot;
import com.diveboard.model.Converter;
import com.diveboard.model.Units;
import com.diveboard.util.DateConverter;

import java.util.Calendar;

public class DiveItemViewModel implements Comparable {
    public final int index;
    public final Integer number;
    public final Integer id;
    public final String thumbnailImageUrl;
    public final String shakenId;
    public final String date;
    public final String location;
    public final String siteName;
    public final String durationStr;
    public final String maxDepthStr;
    public final boolean unsynced;
    private final Calendar dateTyped;
    public String tripName;
    public int divesInTrip;
    public boolean isGroupStart;
    public String divesInTripTitle;

    public DiveItemViewModel(DateConverter conversion, int index, Integer id, String shakenId, Integer number, Calendar date, Spot spot,
                             String tripName, Integer minutes, Double maxDepthMetric, String thumbnailImageUrl, Units.UnitsType units, boolean unsynced) {
        this.index = index;
        this.shakenId = shakenId;
        this.number = number;
        this.id = id;
        this.tripName = tripName;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.date = conversion.convertDateToStringMedium(date);
        dateTyped = date;
        this.unsynced = unsynced;
        if (spot == null) {
            location = "N/A";
            siteName = "N/A";
        } else {
            location = getString(spot.countryName);
            siteName = getString(spot.name);
        }
        this.durationStr = minutes == null ? "" : minutes + " min";
        this.maxDepthStr = maxDepthMetric == null ? "" : String.format("%.0f", Converter.convertDistance(maxDepthMetric, units)) + " " + (units == Units.UnitsType.Metric ? "m" : "ft");
    }

    private String getString(String str) {
        return TextUtils.isEmpty(str) ? "N/A" : str;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof DiveItemViewModel && o != null) {
            DiveItemViewModel another = (DiveItemViewModel) o;
            int dateResult = another.dateTyped.compareTo(dateTyped);
            if (dateResult == 0 && another.number != null) {
                return another.number.compareTo(number);
            }
            return dateResult;
        }
        return -1;
    }
}
