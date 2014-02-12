package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					Shop implements IModel
{
	private Integer				_id;
	private String				_name;
	private Double				_lat;
	private Double				_lng;
	private String				_address;
	private String				_email;
	private String				_web;
	private String				_phone;
	private Picture				_logo;
	private ArrayList<Integer>	_diveIds;
	private Integer				_diveCount;
	private String				_cname;
	
	public						Shop(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_name = (json.isNull("name")) ? null : json.getString("name");
		_lat = (json.isNull("lat")) ? null : json.getDouble("lat");
		_lng = (json.isNull("lng")) ? null : json.getDouble("lng");
		_address = (json.isNull("address")) ? null : json.getString("address");
		_email = (json.isNull("email")) ? null : json.getString("email");
		_web = (json.isNull("web")) ? null : json.getString("web");
		_phone = (json.isNull("phone")) ? null : json.getString("phone");
		_logo = (json.isNull("logo_url")) ? null : new Picture(json.getString("logo_url"));
		if (!json.isNull("dive_ids"))
		{
			_diveIds = new ArrayList<Integer>();
			JSONArray jarray = json.getJSONArray("dive_ids");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Integer new_elem = Integer.valueOf(jarray.getInt(i));
				_diveIds.add(new_elem);
			}
		}
		else
			_diveIds = null;
		_diveCount = (json.isNull("dive_count")) ? null : json.getInt("dive_count");
		_cname = (json.isNull("cname")) ? null : json.getString("cname");
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
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

	public void setLat(Double _lat) {
		this._lat = _lat;
	}

	public Double getLng() {
		return _lng;
	}

	public void setLng(Double _lng) {
		this._lng = _lng;
	}

	public String getAddress() {
		return _address;
	}

	public void setAddress(String _address) {
		this._address = _address;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String _email) {
		this._email = _email;
	}

	public String getWeb() {
		return _web;
	}

	public void setWeb(String _web) {
		this._web = _web;
	}

	public String getPhone() {
		return _phone;
	}

	public void setPhone(String _phone) {
		this._phone = _phone;
	}

	public Picture getLogo() {
		return _logo;
	}

	public void setLogo(Picture _logo) {
		this._logo = _logo;
	}

	public ArrayList<Integer> getDiveIds() {
		return _diveIds;
	}

	public void setDiveIds(ArrayList<Integer> _diveIds) {
		this._diveIds = _diveIds;
	}

	public Integer getDiveCount() {
		return _diveCount;
	}

	public void setDiveCount(Integer _diveCount) {
		this._diveCount = _diveCount;
	}
	
	public void	setCountryName(String cname) {
		this._cname = cname;
	}
	
	public String getCountryName() {
		return this._cname;
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
