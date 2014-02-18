package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					Review
{
	private Integer				_overall;
	private Integer				_difficulty;
	private Integer				_marine;
	private Integer				_wreck;
	private Integer				_bigfish;
	private JSONObject			_json;
	
	public						Review(JSONObject json)
	{
		try {
			_overall = (json.isNull("overall")) ? null : json.getInt("overall");
			_difficulty = (json.isNull("difficulty")) ? null : json.getInt("difficulty");
			_marine = (json.isNull("marine")) ? null : json.getInt("marine");
			_wreck = (json.isNull("wreck")) ? null : json.getInt("wreck");
			_bigfish = (json.isNull("bigfish")) ? null : json.getInt("bigfish");
			_json = json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Integer getOverall() {
		return _overall;
	}

	public void setOverall(Integer _overall) {
		this._overall = _overall;
	}

	public Integer getDifficulty() {
		return _difficulty;
	}

	public void setDifficulty(Integer _difficulty) {
		this._difficulty = _difficulty;
	}

	public Integer getMarine() {
		return _marine;
	}

	public void setMarine(Integer _marine) {
		this._marine = _marine;
	}

	public Integer getWreck() {
		return _wreck;
	}

	public void setWreck(Integer _wreck) {
		this._wreck = _wreck;
	}

	public Integer getBigFish() {
		return _bigfish;
	}

	public void setBigFish(Integer _bigfish) {
		this._bigfish = _bigfish;
	}

	public JSONObject getJson()
	{
		JSONObject result = new JSONObject();
		try {
			result.put("overall", _overall);
			result.put("difficulty", _difficulty);
			result.put("marine", _marine);
			result.put("wreck", _wreck);
			result.put("bigfish", _bigfish);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
