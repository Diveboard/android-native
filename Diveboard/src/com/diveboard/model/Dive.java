package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

/*
 * Class Dive
 * Model for Dives
 */
public class					Dive implements IModel
{
	private Distance			_altitude;
	// buddies
	private String				_class;
	private boolean				_complete;
	private String				_current;
	private String				_date;
	private ArrayList<DiveGear>	_diveGears = new ArrayList<DiveGear>();
	// dive shop
	private ArrayList<String>	_divetype;
	private int					_duration;
	private Boolean				_favorite;
	// flavour
	private String				_fullpermalink;
	// gears
	private String				_guide;
	private int					_id;
	private double				_lat;
	// legacy_buddies_hash
	private double				_lng;
	private Distance			_maxdepth;
	//notes
	private String				_permalink;
	private int					_privacy;
	// public_notes
	private String				_safetystops;
	private String				_shakenId;
	private Shop				_shop;
	private Integer				_shopId;
	private int					_spotId;
	private Spot				_spot;
	private Temperature			_tempBottom;
	private Temperature			_tempSurface;
	private Picture				_thumbnailImageUrl;
	private Picture				_thumbnailProfileUrl;
	private Picture				_profile;
	private String				_time;
	private String				_timeIn;
	private String				_tripName;
	private ArrayList<UserGear>	_userGears;
	private int					_userId;
	private String				_visibility;
	private String				_water;
	private Weight				_weights;
	private String				_notes;
	private String				_publicNotes;
	private Integer				_number;
	private ArrayList<Tank>		_tanks;
	private ArrayList<Species>	_species;
	private Picture				_featuredPicture;
	private ArrayList<Picture>	_pictures;
	private ArrayList<Pair<String, String>>	_editList = new ArrayList<Pair<String, String>>();
	
	public						Dive(JSONObject json) throws JSONException
	{
		_altitude = (json.isNull("altitude")) ? null : new Distance(json.getDouble("altitude"));
		// buddies
		_class = json.getString("class");
		_complete = json.getBoolean("complete");
		_current = (json.isNull("current")) ? null : json.getString("current");
		_date = (json.isNull("date")) ? null : json.getString("date");
		JSONArray jarray = json.getJSONArray("dive_gears");
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			DiveGear new_elem = new DiveGear(jarray.getJSONObject(i));
			_diveGears.add(new_elem);
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
		_fullpermalink = json.getString("fullpermalink");
		// gears
		_guide = (json.isNull("guide")) ? null : json.getString("guide");
		_id = json.getInt("id");
		_lat = json.getDouble("lat");
		// legacy_buddies_hash
		_lng = json.getDouble("lng");
		_maxdepth = new Distance(json.getDouble("maxdepth"));
		// notes
		_permalink = json.getString("permalink");
		_privacy = json.getInt("privacy");
		// public_notes
		_safetystops = (json.isNull("safetystops")) ? null : json.getString("safetystops");
		_shakenId = json.getString("shaken_id");
		_shopId = (json.isNull("shop_id")) ? null : json.getInt("shop_id");
		// species
		_spotId = json.getInt("spot_id");
		_spot = (json.isNull("spot")) ? null : new Spot(json.getJSONObject("spot"));
		_tempBottom = (json.isNull("temp_bottom")) ? null : new Temperature(json.getDouble("temp_bottom"));
		_tempSurface = (json.isNull("temp_surface")) ? null : new Temperature(json.getDouble("temp_surface"));
		_thumbnailImageUrl = new Picture(json.getString("thumbnail_image_url"));
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
		_weights = (json.isNull("weights")) ? null : new Weight(json.getDouble("weights"));
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
		_profile = new Picture("http://stage.diveboard.com/artic/" + Integer.toString(_id) + "/profile.png?g=mobile_v002");
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
		if (!json.isNull("number"))
			_number = json.getInt("number");
		if (!json.isNull("date"))
			_date = json.getString("date");
		if (!json.isNull("time_in"))
			_timeIn = json.getString("time_in");
		if (!json.isNull("maxdepth"))
			_maxdepth = new Distance(json.getDouble("maxdepth"));
		if (!json.isNull("duration"))
			_duration = json.getInt("duration");
		if (!json.isNull("temp_surface"))
			_tempSurface = new Temperature(json.getDouble("temp_surface"));
		if (!json.isNull("temp_bottom"))
			_tempBottom = new Temperature(json.getDouble("temp_bottom"));
		if (!json.isNull("weights"))
			_weights = new Weight(json.getDouble("weights"));
		if (!json.isNull("visibility"))
			_visibility = json.getString("visibility");
		if (!json.isNull("current"))
			_current = json.getString("current");
		if (!json.isNull("altitude"))
			_altitude = new Distance(json.getDouble("altitude"));
		if (!json.isNull("water"))
			_water = json.getString("water");
		if (!json.isNull("notes"))
			_notes = json.getString("notes");
	}
	
	public Distance getAltitude() {
		return _altitude;
	}

	public void setAltitude(Distance _altitude) {
		this._altitude = _altitude;
		Pair<String, String> new_elem = new Pair<String, String>("altitude", Double.toString(_altitude.getDistance()));
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
		return _date;
	}

	public void setDate(String _date) {
		this._date = _date;
		Pair<String, String> new_elem = new Pair<String, String>("date", _date);
		_editList.add(new_elem);
	}

	public int getDuration() {
		return _duration;
	}

	public void setDuration(int _duration) {
		this._duration = _duration;
		Pair<String, String> new_elem = new Pair<String, String>("duration", Integer.toString(_duration));
		_editList.add(new_elem);
	}

	public double getLat() {
		return _lat;
	}

	public void setLat(double _lat) {
		this._lat = _lat;
	}

	public double getLng() {
		return _lng;
	}

	public void setLng(double _lng) {
		this._lng = _lng;
	}

	public Distance getMaxdepth() {
		return _maxdepth;
	}

	public void setMaxdepth(Distance _maxdepth) {
		this._maxdepth = _maxdepth;
		Pair<String, String> new_elem = new Pair<String, String>("maxdepth", Double.toString(_maxdepth.getDistance()));
		_editList.add(new_elem);
	}

	public String getTime() {
		return _time;
	}

	public void setTime(String _time) {
		this._time = _time;
	}
	
	public String getCurrent() {
		return _current;
	}

	public void setCurrent(String _current) {
		this._current = _current;
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

	public String getGuide() {
		return _guide;
	}

	public void setGuide(String _guide) {
		this._guide = _guide;
	}

	public String getPermalink() {
		return _permalink;
	}

	public void setPermalink(String _permalink) {
		this._permalink = _permalink;
	}

	public int getPrivacy() {
		return _privacy;
	}

	public void setPrivacy(int _privacy) {
		this._privacy = _privacy;
	}

	public String getSafetystops() {
		return _safetystops;
	}

	public void setSafetystops(String _safetystops) {
		this._safetystops = _safetystops;
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
		return _spot;
	}

	public void setSpot(Spot _spot) {
		this._spot = _spot;
	}

	public Temperature getTempBottom() {
		return _tempBottom;
	}

	public void setTempBottom(Temperature _tempBottom) {
		this._tempBottom = _tempBottom;
		Pair<String, String> new_elem = new Pair<String, String>("temp_bottom", Double.toString(_tempBottom.getTemperature()));
		_editList.add(new_elem);
	}

	public Temperature getTempSurface() {
		return _tempSurface;
	}

	public void setTempSurface(Temperature _tempSurface) {
		this._tempSurface = _tempSurface;
		Pair<String, String> new_elem = new Pair<String, String>("temp_surface", Double.toString(_tempSurface.getTemperature()));
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
		return _timeIn;
	}

	public void setTimeIn(String _timeIn) {
		this._timeIn = _timeIn;
		Pair<String, String> new_elem = new Pair<String, String>("time_in", _timeIn);
		_editList.add(new_elem);
	}

	public String getTripName() {
		return _tripName;
	}

	public void setTripName(String _tripName) {
		this._tripName = _tripName;
		Pair<String, String> new_elem = new Pair<String, String>("trip_name", _tripName);
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
		return _visibility;
	}

	public void setVisibility(String _visibility) {
		this._visibility = _visibility;
		Pair<String, String> new_elem = new Pair<String, String>("visibility", _visibility);
		_editList.add(new_elem);
	}

	public String getWater() {
		return _water;
	}

	public void setWater(String _water) {
		this._water = _water;
		Pair<String, String> new_elem = new Pair<String, String>("water", _water);
		_editList.add(new_elem);
	}

	public Weight getWeights() {
		return _weights;
	}

	public void setWeights(Weight _weights) {
		this._weights = _weights;
		Pair<String, String> new_elem = new Pair<String, String>("weights", Double.toString(_weights.getWeight()));
		_editList.add(new_elem);
	}

	public String getNotes() {
		return _notes;
	}

	public void setNotes(String notes) {
		this._notes = notes.replaceAll("\"", "\\\\\"");
		Pair<String, String> new_elem = new Pair<String, String>("notes", _notes);
		_editList.add(new_elem);
	}

	public String getPublicNotes() {
		return _publicNotes;
	}

	public void setPublicNotes(String _publicNotes) {
		this._publicNotes = _publicNotes;
	}

	public Integer getNumber() {
		return _number;
	}

	public void setNumber(Integer _number) {
		this._number = _number;
		Pair<String, String> new_elem = new Pair<String, String>("number", Integer.toString(_number));
		_editList.add(new_elem);
	}

	public ArrayList<DiveGear> getDiveGears() {
		return _diveGears;
	}

	public void setDiveGears(ArrayList<DiveGear> _diveGears) {
		this._diveGears = _diveGears;
	}

	public ArrayList<Tank> getTanks() {
		return _tanks;
	}

	public void setTanks(ArrayList<Tank> _tanks) {
		this._tanks = _tanks;
	}

	public Shop getShop() {
		return _shop;
	}

	public void setShop(Shop _shop) {
		this._shop = _shop;
	}

	public Picture getFeaturedPicture() {
		return _featuredPicture;
	}

	public void setFeaturedPicture(Picture _featuredPicture) {
		this._featuredPicture = _featuredPicture;
	}

	public ArrayList<Picture> getPictures() {
		return _pictures;
	}

	public void setPictures(ArrayList<Picture> _pictures) {
		this._pictures = _pictures;
	}

	public ArrayList<String> getDivetype() {
		return _divetype;
	}

	public void setDivetype(ArrayList<String> _divetype) {
		this._divetype = _divetype;
	}

	public Picture getProfile() {
		return _profile;
	}

	public void setProfile(Picture _profile) {
		this._profile = _profile;
	}
}
