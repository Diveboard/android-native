package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					Spot implements IModel
{
	private Integer				_id;
	private String				_shakenId;
	private String				_name;
	private Double				_lat;
	private Double				_lng;
	private Integer				_zoom;
	private Integer				_locationId;
	private String				_locationName;
	private Integer				_regionId;
	private String				_regionName;
	private Integer				_countryId;
	private Integer				_privateUserId;
	private String				_countryCode;
	private String				_countryName;
	private Picture				_countryFlagBig;
	private Picture				_countryFlagSmall;
	private String				_cname;
	private String				_location;
	
	public						Spot(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_shakenId = (json.isNull("shaken_id")) ? null : json.getString("shaken_id");
		_name = (json.isNull("name")) ? null : json.getString("name");
		_lat = (json.isNull("lat")) ? null : json.getDouble("lat");
		if (!json.isNull("long"))
			_lng = (json.isNull("long")) ? null : json.getDouble("long");
		else
			_lng = (json.isNull("lng")) ? null : json.getDouble("lng");
		_zoom = (json.isNull("zoom")) ? null : json.getInt("zoom");
		_locationId = (json.isNull("location_id")) ? null : json.getInt("location_id");
		_locationName = (json.isNull("location_name")) ? null : json.getString("location_name");
		_regionId = (json.isNull("region_id")) ? null : json.getInt("region_id");
		_regionName = (json.isNull("region_name")) ? null : json.getString("region_name");
		_countryId = (json.isNull("country_id")) ? null : json.getInt("country_id");
		_privateUserId = (json.isNull("private_user_id")) ? null : json.getInt("private_user_id");
		_countryCode = (json.isNull("country_code")) ? null : json.getString("country_code");
		_countryName = (json.isNull("country_name")) ? null : json.getString("country_name");
		_countryFlagBig = (json.isNull("country_flag_big")) ? null : new Picture(json.getString("country_flag_big"));
		_countryFlagSmall = (json.isNull("country_flag_small")) ? null : new Picture(json.getString("country_flag_small"));
		_cname = (json.isNull("cname")) ? null : json.getString("cname");
		_location = (json.isNull("location")) ? null : json.getString("location");
	}
	
	public JSONObject				getJson()
	{
		JSONObject json = new JSONObject();
		try {
			if (_id != null)
				json.put("id", _id);
			if (_shakenId != null)
				json.put("shaken_id", _shakenId);
			if (_name != null)
				json.put("name", _name);
			if (_lat != null)
				json.put("lat", _lat);
			if (_lng != null)
				json.put("lng", _lng);
			if (_zoom != null)
				json.put("zoom", _zoom);
			if (_locationId != null)
				json.put("location_id", _locationId);
			if (_locationName != null)
				json.put("location_name", _locationName);
			if (_regionId != null)
				json.put("region_id", _regionId);
			if (_regionName != null)
				json.put("region_name", _regionName);
			if (_countryId != null)
				json.put("country_id", _countryId);
			if (_privateUserId != null)
				json.put("private_user_id", _privateUserId);
			if (_countryCode != null)
				json.put("country_code", _countryCode);
			if (_countryName!= null)
				json.put("country_name", _countryName);
			if (_countryFlagBig!= null)
				json.put("country_flag_big", _countryFlagBig._urlDefault);
			if (_countryFlagSmall!= null)
				json.put("country_flag_small", _countryFlagSmall._urlDefault);
			if (_location!= null)
				json.put("location", _location);
			if (_cname!= null)
				json.put("cname", _cname);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getShakenId() {
		return _shakenId;
	}

	public void setShakenId(String _shakenId) {
		this._shakenId = _shakenId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
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

	public Integer getZoom() {
		return _zoom;
	}

	public void setZoom(int _zoom) {
		this._zoom = _zoom;
	}

	public int getLocationId() {
		return _locationId;
	}

	public void setLocationId(int _locationId) {
		this._locationId = _locationId;
	}

	public String getLocationName() {
		if (_locationName == null)
			return _location;
		return _locationName;
	}

	public void setLocationName(String _locationName) {
		this._locationName = _locationName;
	}

	public Integer getRegionId() {
		return _regionId;
	}

	public void setRegionId(Integer _regionId) {
		this._regionId = _regionId;
	}
	
	public String getRegionName() {
		return _regionName;
	}

	public void setRegionName(String _regionName) {
		this._regionName = _regionName;
	}

	public int getCountryId() {
		return _countryId;
	}

	public void setCountryId(int _countryId) {
		this._countryId = _countryId;
	}

	public Integer getPrivateUserId() {
		return _privateUserId;
	}

	public void setPrivateUserId(Integer _privateUserId) {
		this._privateUserId = _privateUserId;
	}

	public String getCountryCode() {
		return _countryCode;
	}

	public void setCountryCode(String _countryCode) {
		this._countryCode = _countryCode;
	}

	public String getCountryName() {
		if (_countryName == null)
			return _cname;
		return _countryName;
	}

	public void setCountryName(String _countryName) {
		this._countryName = _countryName;
	}

	public Picture getCountryFlagBig() {
		return _countryFlagBig;
	}

	public void setCountryFlagBig(Picture _countryFlagBig) {
		this._countryFlagBig = _countryFlagBig;
	}

	public Picture getCountryFlagSmall() {
		return _countryFlagSmall;
	}

	public void setCountryFlagSmall(Picture _countryFlagSmall) {
		this._countryFlagSmall = _countryFlagSmall;
	}

	@Override
	public ArrayList<Pair<String, String>> getEditList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearEditList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyEdit(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
