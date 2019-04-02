package com.diveboard.dataaccess.datamodel;

import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Dive {
    @SerializedName("id")
    public Integer id;
    @SerializedName("shaken_id")
    public String shakenId;
    @SerializedName("time_in")
    public String timeIn;
    @SerializedName("duration")
    public Integer duration;
    @SerializedName("maxdepth")
    public Double maxDepth;
    @SerializedName("user_id")
    public Integer userId;
    @SerializedName("spot_id")
    public Integer spotId;
    @SerializedName("temp_surface")
    public Double tempSurface;
    @SerializedName("temp_bottom")
    public Double tempBottom;
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
    public Integer number;
    @SerializedName("spot")
    public Spot spot;

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
}
