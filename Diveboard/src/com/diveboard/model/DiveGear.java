package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					DiveGear implements IModel
{
	private Integer				_id;
	private String				_category;
	private String				_manufacturer;
	private String				_model;
	private Boolean				_featured;
	
	public						DiveGear(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_category = (json.isNull("category")) ? null : json.getString("category");
		_manufacturer = (json.isNull("manufacturer")) ? null : json.getString("manufacturer");
		_model = (json.isNull("model")) ? null : json.getString("model");
		_featured = (json.isNull("featured")) ? null : json.getBoolean("featured");
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getCategory() {
		return _category;
	}

	public void setCategory(String _category) {
		this._category = _category;
	}

	public String getManufacturer() {
		return _manufacturer;
	}

	public void setManufacturer(String _manufacturer) {
		this._manufacturer = _manufacturer;
	}

	public String getModel() {
		return _model;
	}

	public void setModel(String _model) {
		this._model = _model;
	}

	public Boolean getFeatured() {
		return _featured;
	}

	public void setFeatured(Boolean _featured) {
		this._featured = _featured;
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
