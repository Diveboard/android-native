package com.diveboard.dataaccess.datamodel;

import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.viewModel.SafetyStopViewModel;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Dive {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    @SerializedName("id")
    public Integer id;
    @SerializedName("shaken_id")
    public String shakenId;
    @SerializedName("time_in")
    public String timeIn;
    @SerializedName("duration")
    public Integer durationMin;
    @SerializedName("maxdepth")
    public Double maxDepth;
    @SerializedName("user_id")
    public Integer userId;
    @SerializedName("spot_id")
    public Integer spotId;
    @SerializedName("temp_surface")
    public Double airTemp;
    @SerializedName("temp_bottom")
    public Double waterTemp;
    @SerializedName("privacy")
    public Integer privacy;
    @SerializedName("weights")
    public Double weights;
    //    @SerializedName("weights_value")
//    public Double weightsValue;
//    @SerializedName("weights_unit")
//    public String weightsUnit;
    @SerializedName("safetystops")
    public String safetystops;
    @SerializedName("safetystops_unit_value")
    public String safetystopsUnitValue;
    @SerializedName("divetype")
    public List<String> divetype = new ArrayList<>();
    @SerializedName("favorite")
    public Boolean favorite;
    @SerializedName("visibility")
    public String visibility;
    @SerializedName("trip_name")
    public String tripName;
    @SerializedName("water")
    public String water;
    @SerializedName("altitude")
    public Double altitude;
    @SerializedName("fullpermalink")
    public String fullpermalink;
    @SerializedName("permalink")
    public String permalink;
    @SerializedName("complete")
    public Boolean complete;
    @SerializedName("thumbnail_image_url")
    public String thumbnailImageUrl;
    @SerializedName("thumbnail_profile_url")
    public String thumbnailProfileUrl;
    @SerializedName("guide")
    public String guide;
    @SerializedName("notes")
    public String notes;
    @SerializedName("public_notes")
    public String publicNotes;
    @SerializedName("current")
    public String current;
    @SerializedName("lat")
    public Double lat;
    @SerializedName("lng")
    public Double lng;
    @SerializedName("date")
    public String date;
    @SerializedName("time")
    public String time;
    @SerializedName("tanks")
    public List<Tank> tanks = new ArrayList();
    @SerializedName("updated_at")
    public String updatedAt;
    @SerializedName("featured_picture")
    public String featuredPicture;
    @SerializedName("number")
    public Integer diveNumber;
    @SerializedName("spot")
    public Spot spot;

    public Calendar getTimeIn() {
        try {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTime(dateFormat.parse(timeIn));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setTimeIn(Calendar value) {
        timeIn = dateFormat.format(value.getTime());
    }

    public Boolean isFreshWater() {
        if (water == null) {
            return null;
        }
        if ("fresh".equals(water)) {
            return true;
        }
        return false;
    }

    public List<SafetyStop> getSafetyStops() {
        if (safetystops == null) {
            return new ArrayList<>();
        }
        List<SafetyStop> result = new ArrayList<>();
        Gson gson = new Gson();
        try {
            String[][] res = gson.fromJson(safetystops, String[][].class);
            for (String[] ss : res) {
                result.add(new SafetyStop(Integer.parseInt(ss[0]), Integer.parseInt(ss[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return result;
    }

    public void setFreshWater(Boolean isFreshWater) {
        if (isFreshWater == null) {
            water = null;
            return;
        }
        water = isFreshWater ? "fresh" : "salt";
    }

    public void setSafetyStops(List<SafetyStop> safetyStops) {
        //TODO: implement
    }
}
