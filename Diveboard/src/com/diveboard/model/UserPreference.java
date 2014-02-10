package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class					UserPreference
{
	private Context				_context;
	private int					_userId;
	public static JSONObject	_userPreferences;
	
	
	public						UserPreference(final Context context, final int userId)
	{
		_context = context;
		_userId = userId;
		
		
		System.out.println("CREATE PREFERENCE");
		File file = new File(_context.getFilesDir() + "_" + Integer.toString(_userId) + "_userpreferences");
		
		if (file.exists())
			_loadUserPreferences();
		else
			_createNewPreferenceFile();
	}
	
	private void				_loadUserPreferences()
	{
		File file = new File(_context.getFilesDir() + "_" + Integer.toString(_userId) + "_userpreferences");
		FileInputStream fileInputStream;
		StringBuffer fileContent = new StringBuffer("");
		byte[] buffer = new byte[1024];
		
		try
		{
			fileInputStream = _context.openFileInput(file.getName());
			while (fileInputStream.read(buffer) != -1)
				fileContent.append(new String(buffer));
			fileInputStream.close();
			JSONObject pref = new JSONObject(fileContent.toString());
			_userPreferences = pref;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private	void				_createNewPreferenceFile()
	{
		File file = new File(_context.getFilesDir() + "_" + Integer.toString(_userId) + "_userpreferences");
		JSONObject new_pref = new JSONObject();
		FileOutputStream outputStream;
		
		try
		{
			file.createNewFile();
			new_pref.put("network", 0);
			JSONObject units = new JSONObject();
			units.put("distance", "Km");
			units.put("weight", "Kg");
			units.put("temperature", "C");
			units.put("pressure", "bar");
			new_pref.put("units", units);
			new_pref.put("picture_quality", "m_qual");
			_userPreferences = new_pref;
			outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
			outputStream.write(new_pref.toString().getBytes());
			outputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private void				_savePreference()
	{
		File file = new File(_context.getFilesDir() + "_" + Integer.toString(_userId) + "_userpreferences");
		FileOutputStream outputStream;
		
		try
		{
			file.createNewFile();
			outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
			outputStream.write(_userPreferences.toString().getBytes());
			outputStream.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	static public int			getNetwork()
	{
		try {
			return _userPreferences.getInt("network");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void					setNetwork(final int value)
	{
		try
		{
			_userPreferences.put("network", value);
			_savePreference();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void					setPictureQuality(final String val)
	{
		try {
			_userPreferences.put("picture_quality", val);
			_savePreference();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	static public String		getPictureQuality()
	{
		if (_userPreferences.isNull("picture_quality"))
			return "m_qual";
		try {
			return _userPreferences.getString("picture_quality");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void					setUnits(final JSONObject units)
	{
		try
		{
			_userPreferences.put("units", units);
			_savePreference();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	static public JSONObject	getUnits()
	{
		try {
			return _userPreferences.getJSONObject("units");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void				setUnits(int unit)
	{
		JSONObject units = new JSONObject();
		try
		{
			if (unit == 1)
			{
				units.put("distance", "Km");
				units.put("weight", "Kg");
				units.put("temperature", "C");
				units.put("pressure", "bar");
			}
			else
			{
				units.put("distance", "ft");
				units.put("weight", "lbs");
				units.put("temperature", "F");
				units.put("pressure", "psi");
			}
			_userPreferences.put("units", units);
			_savePreference();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
