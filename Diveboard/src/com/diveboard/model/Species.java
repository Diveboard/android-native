package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					Species implements IModel
{
	private String				_id;
	private String				_name;
	private String				_sName;
	private String				_link;
	private Picture				_thumbnailHref;
	private Picture				_picture;
	
	public						Species(JSONObject json) throws JSONException
	{
		_id = json.getString("id");
		_name = (json.isNull("name")) ? null : json.getString("name");
		_sName = json.getString("sname");
		_link = (json.isNull("link")) ? null : json.getString("link");
		_thumbnailHref = (json.isNull("thumbnail_href")) ? null : new Picture(json.getString("thumbnail_href"));
		_picture = (json.isNull("picture")) ? null : new Picture(json.getString("picture"));
	}
	
	public void					save()
	{
		
	}
	
	public void					delete()
	{
		
	}

	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getSName() {
		return _sName;
	}

	public void setSName(String _sName) {
		this._sName = _sName;
	}

	public String getLink() {
		return _link;
	}

	public void setLink(String _link) {
		this._link = _link;
	}

	public Picture getThumbnailHref() {
		return _thumbnailHref;
	}

	public void setThumbnailHref(Picture _thumbnailHref) {
		this._thumbnailHref = _thumbnailHref;
	}

	public Picture getPicture() {
		return _picture;
	}

	public void setPicture(Picture _picture) {
		this._picture = _picture;
	}
}
