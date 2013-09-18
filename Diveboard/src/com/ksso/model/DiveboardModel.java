package com.ksso.model;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;

public class					DiveboardModel
{
	private int					_userId;
	private Context				_context;
	private	User				_user = null;
	private ConnectivityManager	_connMgr;
	protected AndroidHttpClient	_client;

	public						DiveboardModel(final int userId, Context context)
	{
		_userId = userId;
		_context = context;
	}
	
	public void					loadData()
	{
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Online Mode
			_client = AndroidHttpClient.newInstance("Android");
			try
			{
				// Load data online
				_loadOnlineData();
				return ;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// Offline Mode
		_loadOfflineData();
	}
	
	private void				_loadUser(String json_str) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		json = json.getJSONObject("result");
		_user = new User(json);
	}
	
	private void				_loadDives(String json_str) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		JSONArray jarray = json.getJSONArray("result");
		ArrayList<Dive> dives = new ArrayList<Dive>();
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			Dive dive = new Dive(jarray.getJSONObject(i));
			dives.add(dive);
		}
		if (_user != null)
			_user.setDives(dives);
	}
	
	private void				_loadOnlineData() throws IOException, JSONException
	{
		// Load user information
		HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/".concat(Integer.toString(_userId)));
		HttpResponse response = _client.execute(getRequest);
		HttpEntity entity = response.getEntity();
		String result = ContentExtractor.getASCII(entity);
		_loadUser(result);
		
		// Load dive information
		JSONObject json = new JSONObject(result);
		json = json.getJSONObject("result");
		String dive_str = "%5B";
		JSONArray jarray = json.getJSONArray("public_dive_ids");
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			if (i != 0)
				dive_str = dive_str.concat("%2C%20");
			dive_str = dive_str.concat("%7B%22id%22:").concat(Integer.toString(jarray.getInt(i))).concat("%7D");
		}
		dive_str = dive_str.concat("%5D");
		getRequest = new HttpGet("http://stage.diveboard.com/api/V2/dive?arg=".concat(dive_str));
		response = _client.execute(getRequest);
		entity = response.getEntity();
		result = ContentExtractor.getASCII(entity);
		_loadDives(result);
	}
	
	private void				_loadOfflineData()
	{
		
	}
	
	private void				_saveJSON()
	{
		
	}
	
	public User					getUser()
	{
		return _user;
	}
	
	public User					getUser(int userId)
	{
		return _user;
	}
	
	public ArrayList<Dive>		getDives()
	{
		return _user.getDives();
	}
	
	public ArrayList<Dive>		getDives(int userId)
	{
		return _user.getDives();
	}
}
