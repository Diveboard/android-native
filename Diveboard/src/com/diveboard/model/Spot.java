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
	private Integer				_countryId;
	private Integer				_privateUserId;
	private String				_countryCode;
	private String				_countryName;
	private Picture				_countryFlagBig;
	private Picture				_countryFlagSmall;
	
	public						Spot(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_shakenId = (json.isNull("shaken_id")) ? null : json.getString("shaken_id");
		_name = (json.isNull("name")) ? null : json.getString("name");
		_lat = (json.isNull("lat")) ? null : json.getDouble("lat");
		_lng = (json.isNull("lng")) ? null : json.getDouble("lng");
		_zoom = (json.isNull("zoom")) ? null : json.getInt("zoom");
		_locationId = (json.isNull("location_id")) ? null : json.getInt("location_id");
		_locationName = (json.isNull("location_name")) ? null : json.getString("location_name");
		_regionId = (json.isNull("region_id")) ? null : json.getInt("region_id");
		_countryId = (json.isNull("country_id")) ? null : json.getInt("country_id");
		_privateUserId = (json.isNull("private_user_id")) ? null : json.getInt("private_user_id");
		_countryCode = (json.isNull("country_code")) ? null : json.getString("country_code");
		_countryName = (json.isNull("country_name")) ? null : json.getString("country_name");
		_countryFlagBig = (json.isNull("country_flag_big")) ? null : new Picture(json.getString("country_flag_big"));
		_countryFlagSmall = (json.isNull("country_flag_small")) ? null : new Picture(json.getString("country_flag_small"));
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

	public int getZoom() {
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
