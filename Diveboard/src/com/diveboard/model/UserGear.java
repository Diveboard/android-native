package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					UserGear implements IModel
{
	private Integer				_id;
	private String				_category;
	private String				_manufacturer;
	private String				_model;
	private Boolean				_featured;
	private String				_acquisition;
	private String				_autoFeature;
	private String				_lastRevision;
	private String				_reference;
	
	public						UserGear(JSONObject json) throws JSONException
	{
		_id = (json.isNull("id")) ? null : json.getInt("id");
		_category = (json.isNull("category")) ? null : json.getString("category");
		_manufacturer = (json.isNull("manufacturer")) ? null : json.getString("manufacturer");
		_model = (json.isNull("model")) ? null : json.getString("model");
		_featured = (json.isNull("featured")) ? null : json.getBoolean("featured");
		_acquisition = (json.isNull("acquisition")) ? null : json.getString("acquisition");
		_autoFeature = (json.isNull("auto_feature")) ? null : json.getString("auto_feature");
		_lastRevision = (json.isNull("last_revision")) ? null : json.getString("last_revision");
		_reference = (json.isNull("reference")) ? null : json.getString("reference");
	}
	
	public void					save()
	{
		
	}
	
	public void					delete()
	{
		
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

	public String getAcquisition() {
		return _acquisition;
	}

	public void setAcquisition(String _acquisition) {
		this._acquisition = _acquisition;
	}

	public String getAutoFeature() {
		return _autoFeature;
	}

	public void setAutoFeature(String _autoFeature) {
		this._autoFeature = _autoFeature;
	}

	public String getLastRevision() {
		return _lastRevision;
	}

	public void setLastRevision(String _lastRevision) {
		this._lastRevision = _lastRevision;
	}

	public String getReference() {
		return _reference;
	}

	public void setReference(String _reference) {
		this._reference = _reference;
	}
}
