package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

/*
 * Interface IModel
 * Interface for every Model class of Diveboard Mobile Model API
 */
public interface					IModel
{
//	/*
//	 * Method save
//	 * Save the modifications into offline files and Diveboard server,
//	 * stack into edit list if online mode isn't available
//	 */
//	public void						save();
//	
//	/*
//	 * Method delete
//	 * Erase the model on offlines files and send delete request to Diveboard server,
//	 * stack into edit list if online mode isn't available
//	 */
//	public void						delete();
	
	public ArrayList<Pair<String, String>>	getEditList();
	public void						clearEditList();
	public void						applyEdit(JSONObject json) throws JSONException;
}
