package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					Qualification implements IModel
{
	private String				_org;
	private String				_title;
	private String				_date;
	private ArrayList<Pair<String, String>> _editList = new ArrayList<Pair<String, String>>(); 
	
	public						Qualification(JSONObject json) throws JSONException
	{
		_org = json.getString("org");
		_title = json.getString("title");
		_date = json.getString("date");
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

	public ArrayList<Pair<String, String>> getEditList()
	{
		
		return null;
	}

	public void clearEditList()
	{
		
	}

	@Override
	public void applyEdit(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
