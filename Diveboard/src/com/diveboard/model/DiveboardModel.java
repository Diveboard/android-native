package com.diveboard.model;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Pair;

/*
 * Class DiveboardModel
 * Main class for model, manage all intern components
 */
public class					DiveboardModel
{
	private int					_userId;
	private String				_shakenId;
	private Context				_context;
	private	User				_user = null;
	private ConnectivityManager	_connMgr;
	private AndroidHttpClient	_client;
	private DataManager			_cache;
	private ArrayList<DataRefreshListener>	_listeners = new ArrayList<DataRefreshListener>();
	
	private String				_temp_user_json;
	private String				_temp_dives_json;
	private User				_temp_user = null;
	private boolean				_enable_overwrite = false;
	private boolean				_connected = false;
	private String				_token;

	/*
	 * Method DiveboardModel
	 * Constructor, initialize the object
	 */
	public						DiveboardModel(final String userId, final Context context)
	{
		_shakenId = userId;
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_cache = new DataManager(context, _shakenId);
	}
	
	public						DiveboardModel(final Context context)
	{
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public int					doLogin(final String login, final String password)
	{
		// TODO init cache
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			_client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost("http://stage.diveboard.com/api/login_email");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(3);
			args.add(new BasicNameValuePair("email", login));
			args.add(new BasicNameValuePair("password", password));
			args.add(new BasicNameValuePair("apikey", "px6LQxmV8wQMdfWsoCwK"));
			try
			{
				// Set parameters
				postRequest.setEntity(new UrlEncodedFormEntity(args));
				// Execute request
				HttpResponse response = _client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONObject json = new JSONObject(result);
				// Analyze data
				boolean success = json.getBoolean("success");
				if (success == false)
					return (-1);
				// Initialize user account
				_token = json.getString("token");
				_shakenId = json.getString("id");
				_cache = new DataManager(_context, _shakenId);
				return (_userId);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				return (-1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return (-1);
			} catch (JSONException e)
			{
				e.printStackTrace();
				return (-1);
			}
		}
		return (-1);
	}
	
	/*
	 * Method loadData
	 * Automatically load data from offline files or online source if available
	 */
	public void					loadData()
	{
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
		else
		{
			_applyEdit();
			_sendEditOnline();
		}
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
				if (_user == null)
					_loadOnlineData(false);
				else
				{
					_loadOnlineData(true);
					_applyEdit();
				}
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
	private void				_loadUser(final String json_str, final boolean temp_mode) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		json = json.getJSONObject("result");
		if (!temp_mode)
			_user = new User(json);
		else
			_temp_user = new User(json);
	}
	
	/*
	 * Method _loadDives
	 * Takes a JSON and load dives data to model
	 */
	private void				_loadDives(final String json_str, final boolean temp_mode) throws JSONException
	{
		JSONObject json = new JSONObject(json_str);
		JSONArray jarray = json.getJSONArray("result");
		ArrayList<Dive> dives = new ArrayList<Dive>();
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			Dive dive = new Dive(jarray.getJSONObject(i));
			dives.add(dive);
		}
		if (temp_mode == false && _user != null)
			_user.setDives(dives);
		else if (temp_mode == true && _temp_user != null)
			_temp_user.setDives(dives);
	}
	
	/*
	 * Method _loadOnlineData
	 * Sends requests to Diveboard server and retrieves data from online source
	 * temp_mode : Define if temporary mode is active (true = active, false = disabled)
	 */
	private void				_loadOnlineData(final boolean temp_mode) throws IOException, JSONException
	{
		// Load user information
		HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/".concat(Integer.toString(_userId)));
		HttpResponse response = _client.execute(getRequest);
		HttpEntity entity = response.getEntity();
		String result = ContentExtractor.getASCII(entity);
		if (!temp_mode)
			_cache.saveCache(_userId, "user", result);
		else
			_temp_user_json = result;
		_loadUser(result, temp_mode);
		
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
		if (!temp_mode)
			_cache.saveCache(_userId, "dives", result);
		else
			_temp_dives_json = result;
		_loadDives(result, temp_mode);
		if (!temp_mode)
			_cache.commitCache();
		else
			_enable_overwrite = true;
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
			_loadUser(tmp, false);
		tmp = _cache.get(_userId, "dives");
		if (tmp != null)
			_loadDives(tmp, false);
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
		// TODO Not implemented
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
		// TODO Not implemented
		return _user.getDives();
	}
	
	/*
	 * Method commit
	 * Commit all changes of the model
	 */
	public void					commit()
	{
		// TODO Not implemented
	}
	
	public void					setOnDataRefreshComplete(DataRefreshListener listener)
	{
		_listeners.add(listener);
	}
	
	public void					overwriteData() throws IOException
	{
		if (_enable_overwrite)
		{
			_cache.saveCache(_userId, "user", _temp_user_json);
			_cache.saveCache(_userId, "dives", _temp_dives_json);
			_cache.commitCache();
			_user = (User) _temp_user.clone();
			_temp_user = null;
		}
		_enable_overwrite = false;
		_applyEdit();
	}
	
	public DataManager			getDataManager()
	{
		return _cache;
	}
	
	private void				_applyEdit()
	{
		ArrayList<Pair<String, String>> edit_list = _cache.getEditList();
		try
		{
			for (int i = 0, length = edit_list.size(); i < length; i++)
			{
				String[] info = edit_list.get(i).first.split(":");
				if (info[0].compareTo("Dive") == 0)
					_applyEditDive(Integer.parseInt(info[1]), edit_list.get(i).second);
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
	private void				_applyEditDive(final int id, final String json) throws JSONException
	{
		ArrayList<Dive> dives = getDives();
		
		for (int i = 0, length = dives.size(); i < length; i++)
		{
			if (dives.get(i).getId() == id)
			{
				dives.get(i).applyEdit(new JSONObject(json));
				break ;
			}
		}
	}
	
	/*
	 * Method _sendEditOnline
	 * Send user modified data to Diveboard online API
	 */
	private void				_sendEditOnline()
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		System.out.println("SEND ONLINE ----------------------------------");
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			ArrayList<Pair<String, String>> edit_list = _cache.getEditList();
			for (int i = 0, length = edit_list.size(); i < length; i++)
			{
				System.out.println(edit_list.get(i).first + " " + edit_list.get(i).second);
			}
		}
	}
}
