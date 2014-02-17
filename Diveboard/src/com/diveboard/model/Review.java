package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					Review
{
	private Integer				_overall;
	private Integer				_difficulty;
	private Integer				_life;
	private Integer				_wreck;
	private Integer				_fish;
	private JSONObject			_json;
	
	public						Review(JSONObject json)
	{
		try {
			_overall = (json.isNull("overall")) ? null : json.getInt("overall");
			_difficulty = (json.isNull("difficulty")) ? null : json.getInt("difficulty");
			_life = (json.isNull("life")) ? null : json.getInt("life");
			_wreck = (json.isNull("wreck")) ? null : json.getInt("wreck");
			_fish = (json.isNull("fish")) ? null : json.getInt("fish");
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

	public Integer getLife() {
		return _life;
	}

	public void setLife(Integer _life) {
		this._life = _life;
	}

	public Integer getWreck() {
		return _wreck;
	}

	public void setWreck(Integer _wreck) {
		this._wreck = _wreck;
	}

	public Integer getFish() {
		return _fish;
	}

	public void setFish(Integer _fish) {
		this._fish = _fish;
	}

	public JSONObject getJson() {
		return _json;
	}

	public void setJson(JSONObject _json) {
		this._json = _json;
	}
}
