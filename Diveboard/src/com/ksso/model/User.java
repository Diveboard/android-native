package com.ksso.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class					User
{
	private int					_id;
	private int					_total_nb_dives;
	private int					_public_nb_dives;
	private String				_location;
	private String				_nickname;
	private ArrayList<Dive>		_dives = new ArrayList<Dive>();

	public						User(final JSONObject json)
	{
		try
		{
			_id = json.getInt("id");
			_total_nb_dives = json.getInt("total_nb_dives");
			_public_nb_dives = json.getInt("public_nb_dives");
			_location = json.getString("location");
			_nickname = json.getString("nickname");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
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
}
