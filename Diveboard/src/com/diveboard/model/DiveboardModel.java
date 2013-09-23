package com.diveboard.model;

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
	private AndroidHttpClient	_client;
	private DataManager			_cache;

	public						DiveboardModel(final int userId, final Context context)
	{
		_userId = userId;
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_cache = new DataManager(context);
	}
	
	public void					loadData()
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		// Test connectivity
		try
		{
			if (networkInfo != null && networkInfo.isConnected())
			{
				// Online Mode
				_client = AndroidHttpClient.newInstance("Android");
				// Load data online
				_loadOnlineData();
				return ;
			}
			// Offline Mode
			_loadOfflineData();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void				_loadUser(final String json_str) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		json = json.getJSONObject("result");
		_user = new User(json);
	}
	
	private void				_loadDives(final String json_str) throws JSONException
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
		_cache.saveCache(_userId, "user", result);
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
		_cache.saveCache(_userId, "dives", result);
		_loadDives(result);
		_cache.commitCache();
	}
	
	private void				_loadOfflineData() throws IOException, JSONException
	{
		String					tmp;
		
		_cache.loadCache(_userId);
		tmp = _cache.get(_userId, "user");
		if (tmp != null)
			_loadUser(tmp);
		tmp = _cache.get(_userId, "dives");
		if (tmp != null)
			_loadDives(tmp);
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
		// Check Null return
		return _user.getDives();
	}
	
	public ArrayList<Dive>		getDives(int userId)
	{
		return _user.getDives();
	}
}
