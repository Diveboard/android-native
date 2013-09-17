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
	private	User				_user;
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
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Online Mode
			_client = AndroidHttpClient.newInstance("Android");
			try
			{
				_loadOnlineData();
				return ;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// Offline Mode
	}
	
	private void				_loadOnlineData() throws IOException, JSONException
	{
		// Load user information
		HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/".concat(Integer.toString(_userId)));
		HttpResponse response = _client.execute(getRequest);
		HttpEntity entity = response.getEntity();
		String result = ContentExtractor.getASCII(entity);
		JSONObject json = new JSONObject(result);
		json = json.getJSONObject("result");
		_user = new User(json);
		
		// Load dive information
		String dive_str = "[";
		JSONArray jarray = json.getJSONArray("public_dive_ids");
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			if (i != 0)
				dive_str.concat(",");
			dive_str.concat("{\"id\":").concat(Integer.toString(jarray.getInt(i))).concat("}");
		}
		dive_str.concat("]");
		getRequest = new HttpGet("http://stage.diveboard.com/api/V2/dive?arg=".concat(dive_str));
		response = _client.execute(getRequest);
		entity = response.getEntity();
		result = ContentExtractor.getASCII(entity);
		json = new JSONObject(result);
		jarray = json.getJSONArray("result");
		ArrayList<Dive> dives = new ArrayList<Dive>();
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			Dive dive = new Dive(jarray.getJSONObject(i));
			dives.add(dive);
		}
		_user.setDives(dives);
	}
	
	public User					getUser()
	{
		return _user;
	}
	
	public ArrayList<Dive>		getDives()
	{
		return _user.getDives();
	}
}
