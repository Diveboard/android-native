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
import android.os.Parcel;
import android.os.Parcelable;

/*
 * Class DiveboardModel
 * Main class for model, manage all intern components
 */
public class					DiveboardModel
{
	private int					_userId;
	private Context				_context;
	private	User				_user = null;
	private ConnectivityManager	_connMgr;
	private AndroidHttpClient	_client;
	private DataManager			_cache;
	private ArrayList<DataRefreshListener>	_listeners = new ArrayList<DataRefreshListener>();

	/*
	 * Method DiveboardModel
	 * Constructor, initialize the object
	 */
	public						DiveboardModel(final int userId, final Context context)
	{
		_userId = userId;
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_cache = new DataManager(context);
	}
	
	/*
	 * Method loadData
	 * Automatically load data from offline files or online source if available
	 */
	public void					loadData()
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		// Offline Mode
		try
		{
			_loadOfflineData();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		if (_user == null)
			refreshData();
	}
	
	public void					refreshData()
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Online Mode
			_client = AndroidHttpClient.newInstance("Android");			
			// Load data online
			try
			{
				_loadOnlineData();
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
		// Fire refresh complete event
		for (DataRefreshListener listener : _listeners)
			listener.onDataRefreshComplete();
	}
	
	/*
	 * Method _loadUser
	 * Takes a JSON and load user data to model
	 */
	private void				_loadUser(final String json_str) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		json = json.getJSONObject("result");
		_user = new User(json);
	}
	
	/*
	 * Method _loadDives
	 * Takes a JSON and load dives data to model
	 */
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
	
	/*
	 * Method _loadOnlineData
	 * Sends requests to Diveboard server and retrieves data from online source
	 */
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
		getRequest = new HttpGet("http://stage.diveboard.com/api/V2/dive?arg=".concat(dive_str) + "&flavour=mobile");
		response = _client.execute(getRequest);
		entity = response.getEntity();
		result = ContentExtractor.getASCII(entity);
		_cache.saveCache(_userId, "dives", result);
		_loadDives(result);
		_cache.commitCache();
	}
	
	/*
	 * Method _loadOfflineData
	 * Get previously saved offline data and loads it into model
	 */
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
	
	/*
	 * Method getUser : void
	 * Returns default user data (default user ID from constructor)
	 */
	public User					getUser()
	{
		return _user;
	}
	
	/*
	 * Method getUser : int userId
	 * Returns user data for a specific user ID passed through argument
	 */
	public User					getUser(int userId)
	{
		// Not implemented
		return _user;
	}
	
	/*
	 * Method getDives : void
	 * Returns default user's dives data (default user ID from constructor)
	 */
	public ArrayList<Dive>		getDives()
	{
		if (_user == null)
			return null;
		return _user.getDives();
	}
	
	/*
	 * Method getDives : int userId
	 * Returns user's dives data for a specific user ID passed through argument
	 */
	public ArrayList<Dive>		getDives(int userId)
	{
		// Not implemented
		return _user.getDives();
	}
	
	/*
	 * Method commit
	 * Commit all changes of the model
	 */
	public void					commit()
	{
		// Not implemented
	}
	
	public void					setOnDataRefreshComplete(DataRefreshListener listener)
	{
		_listeners.add(listener);
	}
}
