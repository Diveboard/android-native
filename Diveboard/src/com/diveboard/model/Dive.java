package com.diveboard.model;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;

import android.util.Pair;

/*
 * Class Dive
 * Model for Dives
 */
public class					Dive implements IModel
{
	private Distance			_altitude;
	private ArrayList<Buddy>	_buddies;
	private String				_class;
	private Boolean				_complete;
	private String				_current;
	private String				_date;
	private ArrayList<DiveGear>	_diveGears = new ArrayList<DiveGear>();
	// dive shop
	private ArrayList<String>	_divetype;
	private Integer				_duration;
	private Boolean				_favorite;
	// flavour
	private String				_fullpermalink;
	// gears
	private String				_guide;
	private int					_id;
	private Double				_lat;
	// legacy_buddies_hash
	private Double				_lng;
	//private Distance			_maxdepth;
	private Double				_maxdepth;
	private String				_maxdepthUnit = null;
	//notes
	private String				_permalink;
	private Integer				_privacy;
	// public_notes
	private String				_shakenId;
	private Shop				_shop;
	private Integer				_shopId;
	private Integer				_spotId;
	private Spot				_spot;
//	private Temperature			_tempBottom;
	private Double				_tempBottom;
	private String				_tempBottomUnit = null;
//	private Temperature			_tempSurface;
	private Double				_tempSurface;
	private String				_tempSurfaceUnit = null;
	private Picture				_thumbnailImageUrl;
	private Picture				_thumbnailProfileUrl;
	private Picture				_profile;
	private Picture				_profileV3;
	private String				_time;
	private String				_timeIn;
	private String				_tripName;
	private ArrayList<UserGear>	_userGears;
	private int					_userId;
	private String				_visibility;
	private String				_water;
//	private Weight				_weights;
	private Double				_weights;
	private String				_weightsUnit = null;
	private String				_notes;
	private String				_publicNotes;
	private Integer				_number;
	private ArrayList<Tank>		_tanks;
	private ArrayList<Species>	_species;
	private Picture				_featuredPicture;
	private ArrayList<Picture>	_pictures;
	private ArrayList<Pair<String, String>>	_editList = new ArrayList<Pair<String, String>>();
	private String				_shopName;
	private Picture				_shopPicture;
	private ArrayList<SafetyStop>	_safetyStops = new ArrayList<SafetyStop>();
	private Review				_diveReviews;
	
	public						Dive()
	{
		Calendar c = Calendar.getInstance();
		_id = -1;
		_date = Integer.toString(c.get(Calendar.YEAR)) + "-";
		if (c.get(Calendar.MONTH) + 1 < 10)
			_date += "0";
		_date += Integer.toString(c.get(Calendar.MONTH) + 1) + "-";
		if (c.get(Calendar.DATE) < 10)
			_date += "0";
		_date += Integer.toString(c.get(Calendar.DATE));
		_timeIn = _date + "T";
		if (c.get(Calendar.HOUR_OF_DAY) < 10)
			_timeIn += "0";
		_timeIn += Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + ":";
		if (c.get(Calendar.MINUTE) < 10)
			_timeIn += "0";
		_timeIn += Integer.toString(c.get(Calendar.MINUTE)) + ":00Z";
		_time = "";
		if (c.get(Calendar.HOUR_OF_DAY) < 10)
			_time += "0";
		_time += Integer.toString(c.get(Calendar.HOUR_OF_DAY)) + ":";
		if (c.get(Calendar.MINUTE) < 10)
			_time += "0";
		_time += Integer.toString(c.get(Calendar.MINUTE));
		_maxdepth = null;
		_duration = null;
		_tripName = null;
		_altitude = null;
		_visibility = null;
		_current = null;
		_tempSurface = null;
		_tempBottom = null;
		_water = null;
		_notes = "";
		_weights = null;
		JSONObject arg = new JSONObject();
		try {
			arg.put("id", 1);
			Spot new_spot = new Spot(arg);
			_spot = new_spot;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		_privacy = 0;
		_divetype = new ArrayList<String>();
		_guide = null;
		_pictures = new ArrayList<Picture>();
		_shop = null;
		_buddies = new ArrayList<Buddy>();
		_diveReviews = null;
	}
	
	public						Dive(JSONObject json) throws JSONException
	{
		_altitude = (json.isNull("altitude")) ? null : new Distance(json.getDouble("altitude"), Units.Distance.KM);
		if (!json.isNull("buddies"))
		{
			JSONArray jarray = (json.getJSONArray("buddies"));
			_buddies = new ArrayList<Buddy>();
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Buddy new_elem = new Buddy(jarray.getJSONObject(i));
				_buddies.add(new_elem);
			}
		}
		_class = (json.isNull("class")) ? null : json.getString("class");
		_complete = (json.isNull("complete")) ? null : json.getBoolean("complete");
		_current = (json.isNull("current")) ? null : json.getString("current");
		_date = (json.isNull("date")) ? null : json.getString("date");
		JSONArray jarray = (json.isNull("dive_gears")) ? null : json.getJSONArray("dive_gears");
		if (jarray != null)
		{
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				DiveGear new_elem = new DiveGear(jarray.getJSONObject(i));
				_diveGears.add(new_elem);
			}
		}
		// dive shop
		if (!json.isNull("divetype"))
		{
			_divetype = new ArrayList<String>();
			jarray = json.getJSONArray("divetype");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				String new_elem = new String(jarray.getString(i));
				_divetype.add(new_elem);
			}
		}
		else
			_divetype = null;
		_duration = json.getInt("duration");
		_favorite = (json.isNull("favorite")) ? null : json.getBoolean("favorite");
		// flavour
		_fullpermalink = (json.isNull("fullpermalink")) ? null : json.getString("fullpermalink");
		// gears
		_guide = (json.isNull("guide")) ? null : json.getString("guide");
		_id = (json.isNull("id")) ? -1 : json.getInt("id");
		_lat = (json.isNull("lat")) ? 0.0 : json.getDouble("lat");
		// legacy_buddies_hash
		_lng = (json.isNull("lng")) ? 0.0 : json.getDouble("lng");
		//_maxdepth = new Distance(json.getDouble("maxdepth"), Units.Distance.KM);
		_maxdepth = json.getDouble("maxdepth_value");
		_maxdepthUnit = (json.isNull("maxdepth_unit")) ? null : json.getString("maxdepth_unit");
		// notes
		_permalink = (json.isNull("permalink")) ? null : json.getString("permalink");
		_privacy = (json.isNull("privacy")) ? null : json.getInt("privacy");
		// public_notes
		_shakenId = (json.isNull("shaken_id")) ? null : json.getString("shaken_id");
		_shopId = (json.isNull("shop_id")) ? null : json.getInt("shop_id");
		// species
		_spotId = (json.isNull("spot_id")) ? null : json.getInt("spot_id");
		_spot = (json.isNull("spot")) ? null : new Spot(json.getJSONObject("spot"));
//		_tempBottom = (json.isNull("temp_bottom")) ? null : new Temperature(json.getDouble("temp_bottom"), Units.Temperature.C);
		_tempBottom = (json.isNull("temp_bottom_value")) ? null : json.getDouble("temp_bottom_value");
		_tempBottomUnit = (json.isNull("temp_bottom_unit")) ? null : json.getString("temp_bottom_unit");
//		_tempSurface = (json.isNull("temp_surface")) ? null : new Temperature(json.getDouble("temp_surface"), Units.Temperature.C);
		_tempSurface = (json.isNull("temp_surface_value")) ? null : json.getDouble("temp_surface_value");
		_tempSurfaceUnit = (json.isNull("temp_surface_unit")) ? null : json.getString("temp_surface_unit");
		_thumbnailImageUrl = (json.isNull("thumbnail_image_url")) ? null : new Picture(json.getString("thumbnail_image_url"));
		_thumbnailProfileUrl = (json.isNull("thumbnail_profile_url")) ? null : new Picture(json.getString("thumbnail_profile_url"));
		_time = (json.isNull("time")) ? null : json.getString("time");
		_timeIn = json.getString("time_in");
		_tripName = (json.isNull("trip_name")) ? null : json.getString("trip_name");
		if (!json.isNull("user_gears"))
		{
			_userGears = new ArrayList<UserGear>();
			jarray = json.getJSONArray("user_gears");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				UserGear new_elem = new UserGear(jarray.getJSONObject(i));
				_userGears.add(new_elem);
			}
		}
		else
			_userGears = null;
		_userId = json.getInt("user_id");
		_visibility = (json.isNull("visibility")) ? null : json.getString("visibility");
		_water = (json.isNull("water")) ? null : json.getString("water");
		//_weights = (json.isNull("weights")) ? null : new Weight(json.getDouble("weights"), Units.Weight.KG);
		_weights = (json.isNull("weights_value")) ? null : json.getDouble("weights_value");
		_weightsUnit = (json.isNull("weights_unit")) ? null : json.getString("weights_unit");
		_notes = (json.isNull("notes")) ? null : json.getString("notes");
		_publicNotes = (json.isNull("public_notes")) ? null : json.getString("public_notes");
		_number = (json.isNull("number")) ? null : json.getInt("number");
		if (!json.isNull("tanks"))
		{
			_tanks = new ArrayList<Tank>();
			jarray = json.getJSONArray("tanks");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Tank new_elem = new Tank(jarray.getJSONObject(i));
				_tanks.add(new_elem);
			}
		}
		else
			_tanks = null;
		if (!json.isNull("species"))
		{
			_species = new ArrayList<Species>();
			jarray = json.getJSONArray("species");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Species new_elem = new Species(jarray.getJSONObject(i));
				_species.add(new_elem);
			}
		}
		else
			_species = null;
		_shop = (json.isNull("shop")) ? null : new Shop(json.getJSONObject("shop"));
		_featuredPicture = (json.isNull("featured_picture") ? null : new Picture(json.getJSONObject("featured_picture")));
		if (!json.isNull("pictures"))
		{
			_pictures = new ArrayList<Picture>();
			jarray = json.getJSONArray("pictures");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Picture new_elem = new Picture(jarray.getJSONObject(i));
				_pictures.add(new_elem);
			}
		}
		else
			_pictures = null;
		if (_fullpermalink != null)
		{
			String unit = (Units.getDistanceUnit() == Units.Distance.KM) ? "&u=m" : "&u=i";
			_profile = new Picture(_fullpermalink + "/profile.png?g=mobile_v002" + unit, Integer.toString(_id));
			_profileV3 = new Picture(_fullpermalink + "/profile.png?g=mobile_v003" + unit, Integer.toString(_id));
		}
		else
		{
			_profile = null;
			_profileV3 = null;
		}
		_shopName = (json.isNull("shop_name")) ? null : json.getString("shop_name");
		_shopPicture = (json.isNull("shop_picture")) ? null : new Picture(json.getString("shop_picture"));
		if (!json.isNull("safetystops_unit_value"))
			_safetyStops = _parseSafetyStops(json.getString("safetystops_unit_value"));
		if (!json.isNull("divetype"))
		{
			jarray = new JSONArray(json.getString("divetype"));
			ArrayList<String> new_elem = new ArrayList<String>();
			for (int i = 0, length = jarray.length(); i < length; i++)
				new_elem.add(jarray.getString(i));
			_divetype = new_elem;
		}
		_guide = (json.isNull("guide")) ? null : json.getString("guide");
		_diveReviews = (json.isNull("dive_reviews")) ? null : new Review(json.getJSONObject("dive_reviews"));
	}

	public ArrayList<Pair<String, String>> getEditList()
	{
		return _editList;
	}
	
	public void					clearEditList()
	{
		_editList = null;
		_editList = new ArrayList<Pair<String, String>>();
	}
	
	public void					applyEdit(JSONObject json) throws JSONException
	{
		if (!json.isNull("trip_name"))
			_tripName = json.getString("trip_name");
		if (json.has("number"))
			_number = (json.isNull("number")) ? null : json.getInt("number");
		if (!json.isNull("date"))
			_date = json.getString("date");
		if (!json.isNull("time_in"))
			_timeIn = json.getString("time_in");
		if (!json.isNull("maxdepth_value"))
			_maxdepth = json.getDouble("maxdepth_value");
		if (!json.isNull("maxdepth_unit"))
			_maxdepthUnit = json.getString("maxdepth_unit");
		if (!json.isNull("duration"))
			_duration = json.getInt("duration");
		if (json.has("temp_surface_value"))
			_tempSurface = (json.isNull("temp_surface_value")) ? null : json.getDouble("temp_surface_value");
		if (!json.isNull("temp_surface_unit"))
			_tempSurfaceUnit = json.getString("temp_surface_unit");
		if (json.has("temp_bottom_value"))
			_tempBottom = (json.isNull("temp_bottom_value")) ? null : json.getDouble("temp_bottom_value");
		if (!json.isNull("temp_bottom_unit"))
			_tempBottomUnit = json.getString("temp_bottom_unit");
		if (json.has("weights_value"))
			_weights = (json.isNull("weights_value")) ? null : json.getDouble("weights_value");
		if (!json.isNull("weights_unit"))
			_weightsUnit = json.getString("weights_unit");
		if (json.has("visibility"))
			_visibility = (json.isNull("visibility")) ? null : json.getString("visibility");
		if (json.has("current"))
			_current = (json.isNull("current")) ? null : json.getString("current");
		if (json.has("altitude"))
			_altitude = (json.isNull("altitude")) ? null : new Distance(json.getDouble("altitude"), Units.Distance.KM);
		if (json.has("water"))
			_water = (json.isNull("water")) ? null : json.getString("water");
		if (!json.isNull("notes"))
			_notes = json.getString("notes");
		if (!json.isNull("spot"))
		{
			_spot = new Spot(json.getJSONObject("spot"));
			_lng = _spot.getLng();
			_lat = _spot.getLat();
			if(_spot.getId() != null){
				_spotId = _spot.getId();//Added
				System.out.println("OldSpot");
			}
				
			else{
				System.out.println("NewSpot");
			}
		}
		if (!json.isNull("shop"))
		{
			_shop = new Shop(json.getJSONObject("shop"));
			_shopId = _shop.getId();
			_shopName = _shop.getName();
			_shopPicture = _shop.getLogo();
		}
		if (!json.isNull("time"))
			_time = json.getString("time");
		if (!json.isNull("privacy"))
			_privacy = json.getInt("privacy");
		if (!json.isNull("safetystops_unit_value"))
			_safetyStops = _parseSafetyStops(json.getString("safetystops_unit_value"));
		if (!json.isNull("divetype"))
		{
			JSONArray jarray = new JSONArray(json.getString("divetype"));
			ArrayList<String> new_elem = new ArrayList<String>();
			for (int i = 0, length = jarray.length(); i < length; i++)
				new_elem.add(jarray.getString(i));
			_divetype = new_elem;
		}
		if (!json.isNull("guide"))
			_guide = json.getString("guide");
		if (!json.isNull("pictures"))
		{
			JSONArray jarray = new JSONArray(json.getString("pictures"));
			ArrayList<Picture> new_elem = new ArrayList<Picture>();
			for (int i = 0, length = jarray.length(); i < length; i++)
				new_elem.add(new Picture(jarray.getJSONObject(i)));
			_pictures = new_elem;
		}
		if (!json.isNull("buddies"))
		{
			JSONArray jarray = new JSONArray(json.getString("buddies"));
			ArrayList<Buddy> new_elem = new ArrayList<Buddy>();
			for (int i = 0, length = jarray.length(); i < length; i++)
				new_elem.add(new Buddy(jarray.getJSONObject(i)));
			_buddies = new_elem;
		}
		if (!json.isNull("dive_reviews"))
			_diveReviews = new Review(json.getJSONObject("dive_reviews"));
	}
	
	private ArrayList<SafetyStop>	_parseSafetyStops(String safetystring)
	{
		ArrayList<SafetyStop> result = new ArrayList<SafetyStop>();

		try {
			JSONArray list = new JSONArray(safetystring);
			for (int i = 0, length = list.length(); i < length; i++)
			{
				JSONArray stop = list.getJSONArray(i);
				SafetyStop elem = new SafetyStop(stop.getInt(0), stop.getInt(1), stop.getString(2));
				//Pair<Integer, Integer> elem = new Pair<Integer, Integer>(stop.getInt(0), stop.getInt(1));
				result.add(elem);
			}
		} catch (JSONException e) {
			System.out.println("Failed to parse JSON");
		}
		return result;
	}
	
	public Distance getAltitude() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("altitude"))
			{
				if (_editList.get(i).second == null)
					return null;
				Distance result = new Distance(Double.parseDouble(_editList.get(i).second), Units.Distance.KM);
				return (result);
			}
		}
		return _altitude;
	}

	public void setAltitude(Distance _altitude) {
		//this._altitude = _altitude;
		Pair<String, String> new_elem;
		
		if (_altitude == null)
			new_elem = new Pair<String, String>("altitude", null);
		else
			new_elem = new Pair<String, String>("altitude", Double.toString(_altitude.getDistance(Units.Distance.KM)));
		_editList.add(new_elem);
	}

	public String getDiveClass() {
		return _class;
	}

	public void setDiveClass(String _class) {
		this._class = _class;
	}

	public boolean isComplete() {
		return _complete;
	}

	public void setComplete(boolean _complete) {
		this._complete = _complete;
	}
	
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getDate() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("date"))
				return (_editList.get(i).second);
		}
		return _date;
	}

	public void setDate(String _date) {
		//this._date = _date;
		Pair<String, String> new_elem = new Pair<String, String>("date", _date);
		_editList.add(new_elem);
	}

	public Integer getDuration() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("duration"))
				return (Integer.parseInt(_editList.get(i).second));
		}
		return _duration;
	}

	public void setDuration(int _duration) {
		//this._duration = _duration;
		Pair<String, String> new_elem = new Pair<String, String>("duration", Integer.toString(_duration));
		_editList.add(new_elem);
	}

	public Double getLat() {
		return _lat;
	}

	public void setLat(double _lat) {
		this._lat = _lat;
	}

	public Double getLng() {
		return _lng;
	}

	public void setLng(double _lng) {
		this._lng = _lng;
	}

//	public Distance getMaxdepth() {
//		for (int i = _editList.size() - 1; i >= 0; i--)
//		{
//			if (_editList.get(i).first.contentEquals("maxdepth"))
//			{
//				Distance result = new Distance(Double.parseDouble(_editList.get(i).second), Units.Distance.KM);
//				return (result);
//			}
//		}
//		return _maxdepth;
//	}
	
	public Double getMaxdepth() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("maxdepth_value"))
			{
				Double result = Double.parseDouble(_editList.get(i).second);
				return (result);
			}
		}
		return _maxdepth;
	}

	public void setMaxdepth(Double _maxdepth) {
		//this._maxdepth = _maxdepth;
		Pair<String, String> new_elem = new Pair<String, String>("maxdepth_value", Double.toString(_maxdepth));
		_editList.add(new_elem);
	}
	
	public String getMaxdepthUnit() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("maxdepth_unit"))
				return _editList.get(i).second;
		}
		return _maxdepthUnit;
	}

	public void setMaxdepthUnit(String _maxdepth_unit) {
		//this._maxdepth = _maxdepth;
		Pair<String, String> new_elem = new Pair<String, String>("maxdepth_unit", _maxdepth_unit);
		_editList.add(new_elem);
	}

	public String getTime() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("time"))
				return (_editList.get(i).second);
		}
		return _time;
	}

	public void setTime(String _time) {
		//this._time = _time;
		Pair<String, String> new_elem = new Pair<String, String>("time", _time);
		_editList.add(new_elem);
	}
	
	public String getCurrent() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("current"))
				return (_editList.get(i).second);
		}
		return _current;
	}

	public void setCurrent(String _current) {
		//this._current = _current;
		Pair<String, String> new_elem = new Pair<String, String>("current", _current);
		_editList.add(new_elem);
	}

	public Boolean isFavorite() {
		return _favorite;
	}

	public void setFavorite(Boolean _favorite) {
		this._favorite = _favorite;
	}

	public String getFullpermalink() {
		return _fullpermalink;
	}

	public void setFullpermalink(String _fullpermalink) {
		this._fullpermalink = _fullpermalink;
	}

	public String getPermalink() {
		return _permalink;
	}

	public void setPermalink(String _permalink) {
		this._permalink = _permalink;
	}

	public int getPrivacy() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("privacy"))
				return (Integer.parseInt(_editList.get(i).second));
		}
		return _privacy;
	}

	public void setPrivacy(int _privacy) {
		//this._privacy = _privacy;
		Pair<String, String> new_elem = new Pair<String, String>("privacy", Integer.toString(_privacy));
		_editList.add(new_elem);
	}

	public ArrayList<SafetyStop> getSafetyStops() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("safetystops_unit_value"))
				return (_parseSafetyStops(_editList.get(i).second));
		}
		return _safetyStops;
	}

	public String getSafetyString(){
		String safetystring = "[";
		ArrayList<SafetyStop> stops = getSafetyStops();

		if (stops != null) {
			for (int i = 0, length = stops.size(); i < length; i++)
			{
				if (i != 0)
					safetystring += ",";
				safetystring += "[\"" + stops.get(i).getDuration() + "\",\"" + stops.get(i).getDepth() + "\",\"" + stops.get(i).getUnit() + "\"]";
			}
		}
		safetystring += "]";
		return safetystring;
	}

	public void setSafetyStops(ArrayList<SafetyStop> safetystops) {
		//this._safetyStops = _safetystops;
		String safetystring = "[";
		
		for (int i = 0, length = safetystops.size(); i < length; i++)
		{
			if (i != 0)
				safetystring += ",";
			safetystring += "[\"" + safetystops.get(i).getDepth() + "\",\"" + safetystops.get(i).getDuration() + "\",\"" + safetystops.get(i).getUnit() + "\"]";
		}
		safetystring += "]";
		System.out.println("SET SAFETY STR : " + safetystring);
		Pair<String, String> new_elem = new Pair<String, String>("safetystops_unit_value", safetystring);
		_editList.add(new_elem);
	}
	
	public ArrayList<Tank> getTanks(){
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("tanks"))
			{
				ArrayList<Tank> result;
				try {
					if (_editList.get(i).second.equals("null"))
						return null;
					JSONArray mTanks = new JSONArray(_editList.get(i).second);
					result = new ArrayList<Tank>();
					for (int j =0; j < mTanks.length(); j++){
						result.add(new Tank(mTanks.getJSONObject(j)));
					}
					
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return _tanks;
	}
	
	public void setTanks(ArrayList<Tank> tanks) {

		try{
			JSONArray tanksArray = new JSONArray();
	
			for (int i = 0, length = tanks.size(); i < length; i++)
			{
				JSONObject jTank = new JSONObject();
				Tank tank = tanks.get(i);
				if(tank.getId() != null)
					jTank.put("id",tank.getId());
				jTank.put("gas",tank.getGas());
				jTank.put("he", tank.getHe());
				jTank.put("n2", tank.getN2());
				jTank.put("o2", tank.getO2());
				jTank.put("material", tank.getMaterial());
				jTank.put("multitank", tank.getMultitank());
				jTank.put("p_start_value", tank.getPStartValue());
				jTank.put("p_end_value", tank.getPEndValue());
				jTank.put("p_start_unit", tank.getPUnit());
				jTank.put("p_end_unit", tank.getPUnit());
				jTank.put("time_start", tank.getTimeStart());
				jTank.put("volume_value", tank.getVolumeValue());
				jTank.put("volume_unit", tank.getVolumeUnit());
				
				tanksArray.put(jTank);
			}
	
			System.out.println("SETTING TANKS: " + tanksArray.toString());
			Pair<String, String> new_elem = new Pair<String, String>("tanks", tanksArray.toString());
			_editList.add(new_elem);
		} catch (JSONException e){
			e.printStackTrace();
		}
	}

	public String getShakenId() {
		return _shakenId;
	}

	public void setShakenId(String _shakenId) {
		this._shakenId = _shakenId;
	}

	public Integer getShopId() {
		return _shopId;
	}

	public void setShopId(Integer _shopId) {
		this._shopId = _shopId;
	}

	public int getSpotId() {
		return _spotId;
	}

	public void setSpotId(int _spotId) {
		this._spotId = _spotId;
	}

	public Spot getSpot() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("spot"))
			{
				Spot result;
				try {
					if (_editList.get(i).second.equals("null"))
						return null;
					result = new Spot(new JSONObject(_editList.get(i).second));
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return _spot;
	}

	public void setSpot(JSONObject _spot) {
		//this._spot = _spot;
		Pair<String, String> new_elem = new Pair<String, String>("spot", _spot.toString());
		_editList.add(new_elem);
	}

	
	public Double getTempBottom() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("temp_bottom_value"))
			{
				if (_editList.get(i).second == null)
					return null;
				return Double.parseDouble(_editList.get(i).second);
			}
		}
		return _tempBottom;
	}

	public void setTempBottom(Double _tempBottom) {
		//this._tempBottom = _tempBottom;
		Pair<String, String> new_elem;
		
		if (_tempBottom == null)
			new_elem = new Pair<String, String>("temp_bottom_value", null);
		else
			new_elem = new Pair<String, String>("temp_bottom_value", _tempBottom.toString());
		_editList.add(new_elem);
	}

	public String getTempBottomUnit() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("temp_bottom_unit"))
			{
				if (_editList.get(i).second == null)
					return null;
				return _editList.get(i).second;
			}
		}
		return _tempBottomUnit;
	}

	public void setTempBottomUnit(String _tempBottom_unit) {
		//this._tempBottom = _tempBottom;
		Pair<String, String> new_elem;
		
		if (_tempBottom_unit == null)
			new_elem = new Pair<String, String>("temp_bottom_unit", null);
		else
			new_elem = new Pair<String, String>("temp_bottom_unit", _tempBottom_unit);
		_editList.add(new_elem);
	}
	
//	public Temperature getTempSurface() {
//		for (int i = _editList.size() - 1; i >= 0; i--)
//		{
//			if (_editList.get(i).first.contentEquals("temp_surface"))
//			{
//				if (_editList.get(i).second == null)
//					return null;
//				Temperature result = new Temperature(Double.parseDouble(_editList.get(i).second), Units.Temperature.C);
//				return (result);
//			}
//		}
//		return _tempSurface;
//	}
//
//	public void setTempSurface(Temperature _tempSurface) {
//		//this._tempSurface = _tempSurface;
//		Pair<String, String> new_elem;
//		
//		if (_tempSurface == null)
//			new_elem = new Pair<String, String>("temp_surface", null);
//		else
//			new_elem = new Pair<String, String>("temp_surface", Double.toString(_tempSurface.getTemperature(Units.Temperature.C)));
//		_editList.add(new_elem);
//	}
	
	public Double getTempSurface() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("temp_surface_value"))
			{
				if (_editList.get(i).second == null)
					return null;
				return Double.parseDouble(_editList.get(i).second);
			}
		}
		return _tempSurface;
	}

	public void setTempSurface(Double _tempSurface) {
		//this._tempSurface = _tempSurface;
		Pair<String, String> new_elem;
		
		if (_tempSurface == null)
			new_elem = new Pair<String, String>("temp_surface_value", null);
		else
			new_elem = new Pair<String, String>("temp_surface_value", _tempSurface.toString());
		_editList.add(new_elem);
	}
	
	public String getTempSurfaceUnit() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("temp_surface_unit"))
			{
				if (_editList.get(i).second == null)
					return null;
				return _editList.get(i).second;
			}
		}
		return _tempSurfaceUnit;
	}

	public void setTempSurfaceUnit(String _tempSurface_unit) {
		//this._tempSurface = _tempSurface;
		Pair<String, String> new_elem;
		
		if (_tempSurface_unit == null)
			new_elem = new Pair<String, String>("temp_surface_unit", null);
		else
			new_elem = new Pair<String, String>("temp_surface_unit", _tempSurface_unit);
		_editList.add(new_elem);
	}

	public Picture getThumbnailImageUrl() {
		return _thumbnailImageUrl;
	}

	public void setThumbnailImageUrl(Picture _thumbnailImageUrl) {
		this._thumbnailImageUrl = _thumbnailImageUrl;
	}

	public Picture getThumbnailProfileUrl() {
		return _thumbnailProfileUrl;
	}

	public void setThumbnailProfileUrl(Picture _thumbnailProfileUrl) {
		this._thumbnailProfileUrl = _thumbnailProfileUrl;
	}

	public String getTimeIn() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("time_in"))
				return (_editList.get(i).second);
		}
		return _timeIn;
	}

	public void setTimeIn(String _timeIn) {
		//this._timeIn = _timeIn;
		Pair<String, String> new_elem = new Pair<String, String>("time_in", _timeIn);
		_editList.add(new_elem);
	}

	public String getTripName() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("trip_name"))
				return (_editList.get(i).second);
		}
		return _tripName;
	}

	public void setTripName(String _tripName) {
		//this._tripName = _tripName;
		Pair<String, String> new_elem = new Pair<String, String>("trip_name", _tripName);
		_editList.add(new_elem);
	}
	
	public String getGuide() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("guide"))
				return (_editList.get(i).second);
		}
		return _guide;
	}

	public void setGuide(String guideName) {
		//this._tripName = _tripName;
		Pair<String, String> new_elem = new Pair<String, String>("guide", guideName);
		_editList.add(new_elem);
	}

	public ArrayList<UserGear> getUserGears() {
		return _userGears;
	}

	public void setUserGears(ArrayList<UserGear> _userGears) {
		this._userGears = _userGears;
	}

	public int getUserId() {
		return _userId;
	}

	public void setUserId(int _userId) {
		this._userId = _userId;
	}

	public String getVisibility() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("visibility"))
				return (_editList.get(i).second);
		}
		return _visibility;
	}

	public void setVisibility(String _visibility) {
		//this._visibility = _visibility;
		Pair<String, String> new_elem;
		new_elem = new Pair<String, String>("visibility", _visibility);
		_editList.add(new_elem);
	}

	public String getWater() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("water"))
				return (_editList.get(i).second);
		}
		return _water;
	}

	public void setWater(String _water) {
		//this._water = _water;
		Pair<String, String> new_elem = new Pair<String, String>("water", _water);
		_editList.add(new_elem);
	}

//	public Weight getWeights() {
//		for (int i = _editList.size() - 1; i >= 0; i--)
//		{
//			if (_editList.get(i).first.contentEquals("weights"))
//			{
//				if (_editList.get(i).second == null)
//					return null;
//				Weight result = new Weight(Double.parseDouble(_editList.get(i).second), Units.Weight.KG);
//				return (result);
//			}
//		}
//		return _weights;
//	}
//
//	public void setWeights(Weight _weights) {
//		//this._weights = _weights;
//		Pair<String, String> new_elem;
//		
//		if (_weights == null)
//			new_elem = new Pair<String, String>("weights", null);
//		else
//			new_elem = new Pair<String, String>("weights", Double.toString(_weights.getWeight(Units.Weight.KG)));
//		_editList.add(new_elem);
//	}
	
	public Double getWeights() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("weights_value"))
			{
				if (_editList.get(i).second == null)
					return null;
				return (Double.parseDouble(_editList.get(i).second));
			}
		}
		return _weights;
	}

	public void setWeights(Double _weights) {
		//this._weights = _weights;
		Pair<String, String> new_elem;
		
		if (_weights == null)
			new_elem = new Pair<String, String>("weights_value", null);
		else
			new_elem = new Pair<String, String>("weights_value", Double.toString(_weights));
		_editList.add(new_elem);
	}
	
	public String getWeightsUnit() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("weights_unit"))
				return _editList.get(i).second;
		}
		return _weightsUnit;
	}

	public void setWeightsUnit(String _weights_unit) {
		//this._maxdepth = _maxdepth;
		Pair<String, String> new_elem = new Pair<String, String>("weights_unit", _weights_unit);
		_editList.add(new_elem);
	}

	public String getNotes() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("notes"))
				return (_editList.get(i).second);
		}
		return _notes;
	}

	public void setNotes(String notes) {
		//this._notes = notes;
		Pair<String, String> new_elem = new Pair<String, String>("notes", notes.replaceAll("\"", "\\\\\""));
		_editList.add(new_elem);
	}

	public String getPublicNotes() {
		return _publicNotes;
	}

	public void setPublicNotes(String _publicNotes) {
		this._publicNotes = _publicNotes;
	}

	public Integer getNumber() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("number"))
			{
				if (_editList.get(i).second == null)
					return null;
				return (Integer.parseInt(_editList.get(i).second));
			}
		}
		return _number;
	}

	public void setNumber(Integer _number) {
		//this._number = _number;
		Pair<String, String> new_elem;
		
		if (_number == null)
			new_elem = new Pair<String, String>("number", null);
		else
			new_elem = new Pair<String, String>("number", Integer.toString(_number));
		_editList.add(new_elem);
	}

	public ArrayList<DiveGear> getDiveGears() {
		return _diveGears;
	}

	public void setDiveGears(ArrayList<DiveGear> _diveGears) {
		this._diveGears = _diveGears;
	}

	public Shop getShop() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("shop"))
			{
				Shop result;
				try {
					if (_editList.get(i).second.equals("null"))
						return null;
					result = new Shop(new JSONObject(_editList.get(i).second));
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return _shop;
	}

	public void setShop(JSONObject _shop) {
		//this._shop = _shop;
		Pair<String, String> new_elem = new Pair<String, String>("shop", _shop.toString());
		_editList.add(new_elem);
	}

	public Picture getFeaturedPicture() {
		return _featuredPicture;
	}

	public void setFeaturedPicture(Picture _featuredPicture) {
		this._featuredPicture = _featuredPicture;
	}

	public ArrayList<Picture> getPictures()
	{
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("pictures"))
			{
				if (_editList.get(i).second == null)
					return null;
				ArrayList<Picture> result = new ArrayList<Picture>();
				JSONArray jarray;
				try {
					jarray = new JSONArray(_editList.get(i).second);
					for (int j = 0, length = jarray.length(); j < length; j++)
					{
						result.add(new Picture(jarray.getJSONObject(j)));
					}
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return (ArrayList<Picture>) _pictures.clone();
	}

	public void setPictures(ArrayList<Picture> _pictures)
	{
		Pair<String, String> new_elem;
		JSONArray jarray = new JSONArray();
		for (int i = 0, size = _pictures.size(); i < size; i++)
			jarray.put(_pictures.get(i).getJson());
		new_elem = new Pair<String, String>("pictures", jarray.toString());
		_editList.add(new_elem);
	}

	public ArrayList<String> getDivetype() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("divetype"))
			{
				if (_editList.get(i).second == null)
					return null;
				ArrayList<String> result = new ArrayList<String>();
				JSONArray jarray;
				try {
					jarray = new JSONArray(_editList.get(i).second);
					for (int j = 0, length = jarray.length(); j < length; j++)
						result.add(jarray.getString(j));
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return _divetype;
	}

	public void setDivetype(ArrayList<String> divetype) {
		//this._divetype = _divetype;
		Pair<String, String> new_elem;
		JSONArray jarray = new JSONArray();
		
		for (int i = 0, length = divetype.size(); i < length; i++)
			jarray.put(divetype.get(i));
		new_elem = new Pair<String, String>("divetype", jarray.toString());
		_editList.add(new_elem);
	}

	public Picture getProfile() {
		return _profile;
	}

	public void setProfile(Picture _profile) {
		this._profile = _profile;
	}
	
	public Picture getProfileV3() {
		return _profileV3;
	}

	public void setProfileV3(Picture _profile) {
		this._profileV3 = _profile;
	}

	public String getShopName() {
		return _shopName;
	}

	public void setShopName(String _shopName) {
		this._shopName = _shopName;
	}

	public Picture getShopPicture() {
		return _shopPicture;
	}

	public void setShopPicture(Picture _shopPicture) {
		this._shopPicture = _shopPicture;
	}

	public ArrayList<Buddy> getBuddies() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("buddies"))
			{
				if (_editList.get(i).second == null)
					return null;
				ArrayList<Buddy> result = new ArrayList<Buddy>();
				JSONArray jarray;
				try {
					jarray = new JSONArray(_editList.get(i).second);
					for (int j = 0, length = jarray.length(); j < length; j++)
						result.add(new Buddy(jarray.getJSONObject(j)));
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return (ArrayList<Buddy>) _buddies.clone();
	}

	public void setBuddies(ArrayList<Buddy> buddies) {
		//this._buddies = _buddies;
		Pair<String, String> new_elem;
		JSONArray jarray = new JSONArray();
		
		for (int i = 0, length = buddies.size(); i < length; i++)
			jarray.put(buddies.get(i).getJson());
		new_elem = new Pair<String, String>("buddies", jarray.toString());
		_editList.add(new_elem);
	}

	public Review getDiveReviews() {
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("dive_reviews"))
			{
				if (_editList.get(i).second == null)
					return null;
				try {
					return (new Review(new JSONObject(_editList.get(i).second)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return _diveReviews;
	}

	public void setDiveReviews(Review review) {
		Pair<String, String> new_elem = new Pair<String, String>("dive_reviews", review.getJson().toString());
		_editList.add(new_elem);
	}
}
