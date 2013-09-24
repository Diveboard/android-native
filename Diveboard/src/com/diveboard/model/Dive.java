package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Class Dive
 * Model for Dives
 */
public class					Dive implements IModel
{
	private int					_id;
	private String				_date;
	private int					_duration;
	private double				_lat;
	private double				_lng;
	private double				_maxdepth;
	private String				_time;
	
	public						Dive(JSONObject json) throws JSONException
	{
		_id = json.getInt("id");
		_date = json.getString("date");
		_duration = json.getInt("duration");
		_lat = json.getDouble("lat");
		_lng = json.getDouble("lng");
		_maxdepth = json.getDouble("maxdepth");
		_time = json.getString("time");
	}
	
	public void					save()
	{
		
	}
	
	public void					delete()
	{
		
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
	}

	public int getDuration() {
		return _duration;
	}

	public void setDuration(int _duration) {
		this._duration = _duration;
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

	public double getMaxdepth() {
		return _maxdepth;
	}

	public void setMaxdepth(double _maxdepth) {
		this._maxdepth = _maxdepth;
	}

	public String getTime() {
		return _time;
	}

	public void setTime(String _time) {
		this._time = _time;
	}
}
