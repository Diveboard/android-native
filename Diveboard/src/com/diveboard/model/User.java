package com.diveboard.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

//cookie name sudo:id user;

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
	private Wallet 										_wallet;
	private ArrayList<Picture>							_wallet_pictures = new ArrayList<Picture>();
	private Integer										_totalExtDives;
	private ArrayList<Dive>								_dives = new ArrayList<Dive>();
	private ArrayList<Pair<String, String>>				_editList = new ArrayList<Pair<String, String>>();
	private String										_countryName;
	private Units										_unitPreferences;
	private Integer										_admin_rights;

	public						User(final JSONObject json) throws JSONException
	{
		_id = json.getInt("id");
		_shaken_id = json.getString("shaken_id");
		_vanity_url = json.getString("vanity_url");
		JSONObject root_qual = json.getJSONObject("qualifications");
		JSONArray featured_qual = (root_qual.isNull("featured")) ? null : root_qual.getJSONArray("featured");
		JSONArray other_qual = (root_qual.isNull("other")) ? null : root_qual.getJSONArray("other");
		ArrayList<Qualification> temp_arraylist = new ArrayList<Qualification>();
		if (featured_qual != null)
			for (int i = 0, length = featured_qual.length(); i < length; i++)
			{
				Qualification new_elem = new Qualification(featured_qual.getJSONObject(i));
				temp_arraylist.add(new_elem);
			}
		_qualifications.put("featured", temp_arraylist);
		temp_arraylist = new ArrayList<Qualification>();
		if (other_qual != null)
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
		if (!json.isNull("wallet_picture_ids"))
		{
			JSONObject wallet = new JSONObject();
			JSONArray array = new JSONArray();
			wallet.put("user_id", _id); 
			wallet.put("size", json.optJSONArray("wallet_picture_ids").length());
			wallet.put("wallet_picture_ids", json.getJSONArray("wallet_picture_ids"));
			Wallet new_wallet = new Wallet(wallet);
			_wallet = new_wallet;
			System.err.println("@@@@@@@@@@@@ new wallet added " + wallet.toString());
		}
		else
			_wallet = null;
		if (!json.isNull("wallet_pictures"))
		{
			JSONArray jarray;
			_wallet_pictures = new ArrayList<Picture>();
			jarray = json.getJSONArray("wallet_pictures");
			for (int i = 0, length = jarray.length(); i < length; i++)
			{
				Picture new_elem = new Picture(jarray.getJSONObject(i));
				_wallet_pictures.add(new_elem);
			}
		}
		else
			_wallet_pictures = null;
		_totalExtDives = (json.isNull("total_ext_dives")) ? null : json.getInt("total_ext_dives");
		_countryName = (json.isNull("country_name")) ? null : json.getString("country_name");
		_unitPreferences = new Units(UserPreference.getUnits());
		_admin_rights = (json.isNull("admin_rights")) ? null : json.getInt("admin_rights");
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

	@Override
	public ArrayList<Pair<String, String>> getEditList() {
		return _editList;
	}

	@Override
	public void clearEditList() {
		_editList = null;
		_editList = new ArrayList<Pair<String, String>>();
	}

	@Override
	public void applyEdit(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	public String getCountryName() {
		return _countryName;
	}

	public void setCountryName(String _countryName) {
		this._countryName = _countryName;
	}

	public Units getUnitPreferences() {
		return _unitPreferences;
	}

	public void setUnitPreferences(Units _unitPreferences) {
		this._unitPreferences = _unitPreferences;
	}

	public Integer getAdminRights() {
		return _admin_rights;
	}

	public Wallet getWallet() {
		return _wallet;
	}

	public void setWallet(Wallet wallet) {
		this._wallet = wallet;
		Pair<String, String> new_elem;
		if(!wallet.getPicturesIds().isEmpty())
			new_elem = new Pair<String, String>("wallet_picture_ids", wallet.getPicturesIds().toString());
		else
			new_elem = new Pair<String, String>("wallet_picture_ids", new JSONArray().toString());
		
		//We add the walletPicturesIds so that they can be updated in the server
		 
		System.out.println("ADDED new_elem to USER EDIT LIST\n" + new_elem.first + new_elem.second);
		_editList.add(new_elem);
	}
	
	public void setWalletPictures(ArrayList<Picture> _pictures)
	{
		Pair<String, String> new_elem;
		JSONArray jarray = new JSONArray();
		for (int i = 0, size = _pictures.size(); i < size; i++)
			jarray.put(_pictures.get(i).getJson());
		new_elem = new Pair<String, String>("wallet_pictures", jarray.toString());
		_editList.add(new_elem);
		_wallet_pictures = _pictures;
	}
	
	public ArrayList<Picture> getWalletPictures()
	{
		for (int i = _editList.size() - 1; i >= 0; i--)
		{
			if (_editList.get(i).first.contentEquals("wallet_pictures"))
			{
				if (_editList.get(i).second == null)
					return null;
				ArrayList<Picture> result = new ArrayList<Picture>();
				JSONArray jarray;
				try {
					jarray = new JSONArray(_editList.get(i).second);
					for (int j = 0, length = jarray.length(); j < length; j++)
					{
						result.add(new Picture(jarray.getJSONObject(j)));
					}
					return (result);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		if(_wallet_pictures != null)
			return (ArrayList<Picture>) _wallet_pictures.clone();
//		return null;
		return _wallet_pictures;
	}
}
