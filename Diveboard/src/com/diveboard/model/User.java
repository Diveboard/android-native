package com.diveboard.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Class User
 * Model for User
 */
public class					User implements IModel, Cloneable
{
	private int											_id;
	private String										_shaken_id;
	private String										_vanity_url;
	private HashMap<String, ArrayList<Qualification>>	_qualifications = new HashMap<String, ArrayList<Qualification>>();
	private Picture										_picture;
	private Picture										_picture_small;
	private Picture										_picture_large;
	private String										_fullpermalink;
	private int											_total_nb_dives;
	private int											_public_nb_dives;
	private String										_location;
	private String										_nickname;
	private ArrayList<UserGear>							_userGears;
	private Integer										_totalExtDives;
	private ArrayList<Dive>								_dives = new ArrayList<Dive>();

	public						User(final JSONObject json) throws JSONException
	{
		_id = json.getInt("id");
		_shaken_id = json.getString("shaken_id");
		_vanity_url = json.getString("vanity_url");
		JSONObject root_qual = json.getJSONObject("qualifications");
		JSONArray featured_qual = root_qual.getJSONArray("featured");
		JSONArray other_qual = root_qual.getJSONArray("other");
		ArrayList<Qualification> temp_arraylist = new ArrayList<Qualification>();
		for (int i = 0, length = featured_qual.length(); i < length; i++)
		{
			Qualification new_elem = new Qualification(featured_qual.getJSONObject(i));
			temp_arraylist.add(new_elem);
		}
		_qualifications.put("featured", temp_arraylist);
		temp_arraylist = new ArrayList<Qualification>();
		for (int i = 0, length = other_qual.length(); i < length; i++)
		{
			Qualification new_elem = new Qualification(other_qual.getJSONObject(i));
			temp_arraylist.add(new_elem);
		}
		_qualifications.put("other", temp_arraylist);
		_picture = new Picture(json.getString("picture"));
		_picture_small = new Picture(json.getString("picture_small"));
		_picture_large = new Picture(json.getString("picture_large"));
		_vanity_url = json.getString("fullpermalink");
		_total_nb_dives = json.getInt("total_nb_dives");
		_public_nb_dives = json.getInt("public_nb_dives");
		_location = json.getString("location");
		_nickname = json.getString("nickname");
		if (!json.isNull("user_gears"))
		{
			_userGears = new ArrayList<UserGear>();
			JSONArray jarray = json.getJSONArray("user_gears");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				UserGear new_elem = new UserGear(jarray.getJSONObject(i));
				_userGears.add(new_elem);
			}
		}
		else
			_userGears = null;
		_totalExtDives = (json.isNull("total_ext_dives")) ? null : json.getInt("total_ext_dives");
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
	
	public String getShakenId() {
		return _shaken_id;
	}

	public void setShakenId(String _shaken_id) {
		this._shaken_id = _shaken_id;
	}

	public String getVanityUrl() {
		return _vanity_url;
	}

	public void setVanityUrl(String _vanity_url) {
		this._vanity_url = _vanity_url;
	}
	
	public HashMap<String, ArrayList<Qualification>> getQualifications() {
		return _qualifications;
	}

	public void setQualifications(
			HashMap<String, ArrayList<Qualification>> _qualifications) {
		this._qualifications = _qualifications;
	}

	public Picture getPicture() {
		return _picture;
	}

	public void setPicture(Picture _picture) {
		this._picture = _picture;
	}

	public Picture getPictureSmall() {
		return _picture_small;
	}

	public void setPictureSmall(Picture _picture_small) {
		this._picture_small = _picture_small;
	}

	public Picture getPictureLarge() {
		return _picture_large;
	}

	public void setPictureLarge(Picture _picture_large) {
		this._picture_large = _picture_large;
	}
	
	public String getFullpermalink() {
		return _fullpermalink;
	}

	public void setFullpermalink(String _fullpermalink) {
		this._fullpermalink = _fullpermalink;
	}

	public int getTotalNbDives() {
		return _total_nb_dives;
	}

	public void setTotalNbDives(int _total_nb_dives) {
		this._total_nb_dives = _total_nb_dives;
	}

	public int getPublicNbDives() {
		return _public_nb_dives;
	}

	public void setPublicNbDives(int _public_nb_dives) {
		this._public_nb_dives = _public_nb_dives;
	}

	public String getLocation() {
		return _location;
	}

	public void setLocation(String _location) {
		this._location = _location;
	}

	public String getNickname() {
		return _nickname;
	}

	public void setNickname(String _nickname) {
		this._nickname = _nickname;
	}
	
	public ArrayList<Dive> getDives() {
		return _dives;
	}

	public void setDives(ArrayList<Dive> _dives) {
		this._dives = _dives;
	}

	public ArrayList<UserGear> getUserGears() {
		return _userGears;
	}

	public void setUserGears(ArrayList<UserGear> _userGears) {
		this._userGears = _userGears;
	}

	public Integer getTotalExtDives() {
		return _totalExtDives;
	}

	public void setTotalExtDives(Integer _totalExtDives) {
		this._totalExtDives = _totalExtDives;
	}
	
	protected Object clone()
	{
        try
        {
			return super.clone();
		}
        catch (CloneNotSupportedException e)
        {
			e.printStackTrace();
		}
        return null;
	}
}
