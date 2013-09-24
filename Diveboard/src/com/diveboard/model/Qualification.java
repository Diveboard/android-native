package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					Qualification implements IModel
{
	private String				_org;
	private String				_title;
	private String				_date;
	
	public						Qualification(JSONObject json) throws JSONException
	{
		_org = json.getString("org");
		_title = json.getString("title");
		_date = json.getString("date");
	}
	
	public void					save()
	{
		
	}
	
	public void					delete()
	{
		
	}
	
	public String getOrg() {
		return _org;
	}
	public void setOrg(String _org) {
		this._org = _org;
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String _title) {
		this._title = _title;
	}
	public String getDate() {
		return _date;
	}
	public void setDate(String _date) {
		this._date = _date;
	}
	
	
}
