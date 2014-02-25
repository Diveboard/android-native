package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

public class					Buddy implements IModel
{
	private String				_fullpermalink = null;
	private String				_nickname = null;
	private String				_location = null;
	private Picture				_picture_small = null;
	private String				_class = null;
	private Picture				_picture_large = null;
	private Picture				_picture = null;
	private Integer				_id = null;
	private String				_permalink = null;
	private String				_vanity_url = null;
	private String				_shaken_id = null;
	private String				_email = null;
	private Boolean				_notify = false;
	
	public						Buddy(JSONObject json)
	{
		try
		{
			_fullpermalink = (json.isNull("fullpermalink")) ? null : json.getString("fullpermalink");
			if (json.isNull("name") != false)
				_nickname = json.getString("name");
			else
				_nickname = (json.isNull("nickname")) ? null : json.getString("nickname");
			_location = (json.isNull("location")) ? null : json.getString("location");
			_picture_small = (json.isNull("picture_small")) ? null : new Picture(json.getString("picture_small"));
			_class = (json.isNull("class")) ? null : json.getString("class");
			_picture_large = (json.isNull("picture_large")) ? null : new Picture(json.getString("picture_large"));
			_picture = (json.isNull("picture")) ? null : new Picture(json.getString("picture"));
			_id = (json.isNull("id")) ? null : json.getInt("id");
			_permalink = (json.isNull("permalink")) ? null : json.getString("permalink");
			_vanity_url = (json.isNull("vanity_url")) ? null : json.getString("vanity_url");
			_email = (json.isNull("email")) ? null : json.getString("email");
			_shaken_id = (json.isNull("shaken_id")) ? null : json.getString("shaken_id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public						Buddy()
	{
		
	}
	
	public JSONObject				getJson()
	{
		JSONObject json = new JSONObject();
		try {
			if (_fullpermalink != null)
				json.put("fullpermalink", _fullpermalink);
			if (_nickname != null)
				json.put("nickname", _nickname);
			if (_location != null)
				json.put("location", _location);
			if (_picture_small != null)
				json.put("picture_small", _picture_small._urlDefault);
			if (_class != null)
				json.put("class", _class);
			if (_picture_large != null)
				json.put("picture_large", _picture_large._urlDefault);
			if (_picture != null)
				json.put("picture", _picture._urlDefault);
			if (_id != null)
				json.put("id", _id);
			if (_permalink != null)
				json.put("permalink", _permalink);
			if (_vanity_url != null)
				json.put("vanity_url", _vanity_url);
			if (_shaken_id != null)
				json.put("shaken_id", _shaken_id);
			if (_notify != null)
				json.put("notify", _notify);
			if (_email != null)
				json.put("email", _email);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
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

	public String getFullpermalink() {
		return _fullpermalink;
	}

	public void setFullpermalink(String _fullpermalink) {
		this._fullpermalink = _fullpermalink;
	}

	public String getNickname() {
		return _nickname;
	}

	public void setNickname(String _nickname) {
		this._nickname = _nickname;
	}

	public String getLocation() {
		return _location;
	}

	public void setLocation(String _location) {
		this._location = _location;
	}

	public Picture getPictureSmall() {
		return _picture_small;
	}

	public void setPictureSmall(Picture _picture_small) {
		this._picture_small = _picture_small;
	}

	public String get_class() {
		return _class;
	}

	public void set_class(String _class) {
		this._class = _class;
	}

	public Picture getPictureLarge() {
		return _picture_large;
	}

	public void setPictureLarge(Picture _picture_large) {
		this._picture_large = _picture_large;
	}

	public Picture getPicture() {
		return _picture;
	}

	public void setPicture(Picture _picture) {
		this._picture = _picture;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getPermalink() {
		return _permalink;
	}

	public void setPermalink(String _permalink) {
		this._permalink = _permalink;
	}

	public String getVanityUrl() {
		return _vanity_url;
	}

	public void setVanityUrl(String _vanity_url) {
		this._vanity_url = _vanity_url;
	}

	public String getShakenId() {
		return _shaken_id;
	}

	public void setShakenId(String _shaken_id) {
		this._shaken_id = _shaken_id;
	}
	
	public Boolean isNotified() {
		return this._notify;
	}
	
	public void setNotify(Boolean notify) {
		this._notify = notify;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String _email) {
		this._email = _email;
	}
}
