package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	private DataManager			_cache;
	private ArrayList<DataRefreshListener>	_listeners = new ArrayList<DataRefreshListener>();
	
	private String				_temp_user_json;
	private String				_temp_dives_json;
	private User				_temp_user = null;
	private boolean				_enable_overwrite = false;
	private boolean				_connected = false;
	private String				_token;
	private String				_unitPreferences;
	
	public static ArrayList<Picture>	pictureList = new ArrayList<Picture>();
	private Integer				_pictureCount = 0;
	private LoadPictureThread	_pictureThread1 = null;
	private LoadPictureThread	_pictureThread2 = null;
	
	/*
	 * Method DiveboardModel
	 * Constructor, initialize the object
	 */
	public						DiveboardModel(final int userId, final Context context)
	{
		_userId = userId;
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_cache = new DataManager(context, _userId, _token);
	}
	
	public						DiveboardModel(final Context context)
	{
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public boolean				isLogged()
	{
		File file_id = new File(_context.getFilesDir() + "_logged_id");
		File file_token = new File(_context.getFilesDir() + "_logged_token");
		File unit_preferences = new File(_context.getFilesDir() + "_unit_preferences");
		if (file_id.exists() && file_token.exists())
		{
			try
			{
				FileInputStream fileInputStream = _context.openFileInput(file_id.getName());
				StringBuffer fileContent = new StringBuffer("");
				byte[] buffer = new byte[1];
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
				_userId = Integer.parseInt(fileContent.toString());
				fileInputStream.close();
				
				fileInputStream = _context.openFileInput(file_token.getName());
				fileContent = new StringBuffer("");
				buffer = new byte[1];
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
				_token = fileContent.toString();
				fileInputStream.close();
				
				fileInputStream = _context.openFileInput(unit_preferences.getName());
				fileContent = new StringBuffer("");
				buffer = new byte[1];
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
				_unitPreferences = fileContent.toString();
				fileInputStream.close();
				_cache = new DataManager(_context, _userId, _token);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return false;
			} catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
	
	public int					doLogin(final String login, final String password)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
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
				HttpResponse response = client.execute(postRequest);
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
				_unitPreferences = json.getJSONObject("units").toString();
				// Get user ID
				HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/" + _shakenId);
				response = client.execute(getRequest);
				entity = response.getEntity();
				result = ContentExtractor.getASCII(entity);
				json = new JSONObject(result);
				json = json.getJSONObject("result");
				_userId = json.getInt("id");
				// Initialize DataManager
				_cache = new DataManager(_context, _userId, _token);
				_connected = true;
				client.close();
				
				File file = new File(_context.getFilesDir() + "_logged_id");
				file.createNewFile();
				FileOutputStream outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(Integer.toString(_userId).getBytes());
				
				file = new File(_context.getFilesDir() + "_logged_token");
				file.createNewFile();
				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(_token.getBytes());
				
				file = new File(_context.getFilesDir() + "_unit_preferences");
				file.createNewFile();
				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(_unitPreferences.getBytes());
				
				return (_userId);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			} catch (JSONException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			}
		}
		return (-1);
	}
	
	public int					doFbLogin(final String fb_id, final String fb_token)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost("http://stage.diveboard.com/api/login_fb");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(3);
			args.add(new BasicNameValuePair("fbid", fb_id));
			args.add(new BasicNameValuePair("fbtoken", fb_token));
			args.add(new BasicNameValuePair("apikey", "px6LQxmV8wQMdfWsoCwK"));
			try
			{
				// Set parameters
				postRequest.setEntity(new UrlEncodedFormEntity(args));
				// Execute request
				HttpResponse response = client.execute(postRequest);
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
				_unitPreferences = json.getJSONObject("units").toString();
				// Get user ID
				HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/" + _shakenId);
				response = client.execute(getRequest);
				entity = response.getEntity();
				result = ContentExtractor.getASCII(entity);
				json = new JSONObject(result);
				json = json.getJSONObject("result");
				_userId = json.getInt("id");
				// Initialize DataManager
				_cache = new DataManager(_context, _userId, _token);
				_connected = true;
				client.close();
				
				File file = new File(_context.getFilesDir() + "_logged_id");
				file.createNewFile();
				FileOutputStream outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(Integer.toString(_userId).getBytes());
				
				file = new File(_context.getFilesDir() + "_logged_token");
				file.createNewFile();
				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(_token.getBytes());
				
				file = new File(_context.getFilesDir() + "_unit_preferences");
				file.createNewFile();
				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				outputStream.write(_unitPreferences.getBytes());
				
				return (_userId);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			} catch (JSONException e)
			{
				e.printStackTrace();
				client.close();
				return (-1);
			}
		}
		return (-1);
	}
	
	public void					doLogout()
	{
		File file = new File(_context.getFilesDir() + "_logged_id");
		file.delete();
		
		file = new File(_context.getFilesDir() + "_logged_token");
		file.delete();
		
		file = new File(_context.getFilesDir() + "_unit_preferences");
		file.delete();
		
		_user.setDives(null);
		_user = null;
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
			_applyEdit();
	}
	public void					refreshData()
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Online Mode
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
		_cache.commitEditOnline();
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
			_user = new User(json, _unitPreferences);
		else
			_temp_user = new User(json, _unitPreferences);
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
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");	
		HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/" + Integer.toString(_userId) + "?flavour=mobile");
		HttpResponse response = client.execute(getRequest);
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
		response = client.execute(getRequest);
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
		client.close();
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
	 * Method preloadPictures
	 * When launched, it will preload all created picture in the model.
	 */
	public void					preloadPictures()
	{
		if (_pictureThread1 == null && _pictureThread2 == null)
		{
			_pictureCount = pictureList.size();
			_pictureThread1 = new LoadPictureThread(0, 2);
			_pictureThread2 = new LoadPictureThread(1, 2);
			_pictureThread1.start();
			_pictureThread2.start();
		}
	}
	
	public void					stopPreloadPictures()
	{
		if (_pictureThread1 != null)
			_pictureThread1.cancel();
		if (_pictureThread2 != null)
			_pictureThread2.cancel();
	}
	
	private class				LoadPictureThread extends Thread
	{
		private int				_start;
		private int				_increment;
		private boolean			_run;
		
		public LoadPictureThread(int start, int increment)
		{
			_start = start;
			_increment = increment;
			_run = true;
		}
		
		public void				cancel()
		{
			_run = false;
		}
		
		@Override
		public void run()
		{
			if (_increment > 0)
			{
				for (int i = _start, size = pictureList.size(); i < size && _pictureCount > 0 && _run; i += _increment)
				{
					System.out.println("Loading pictures " + i);
					try
					{
						pictureList.get(i).getPicture(_context, Picture.Size.THUMB);
						pictureList.get(i).getPicture(_context, Picture.Size.SMALL);
						pictureList.get(i).getPicture(_context, Picture.Size.MEDIUM);
						pictureList.get(i).getPicture(_context, Picture.Size.LARGE);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					synchronized (_pictureCount)
					{
						_pictureCount--;
					}
					if (_pictureCount < 0)
					{
						_pictureCount = 0;
					}
					System.out.println(_pictureCount + " pictures remaining");
				}
			}
			else if (_increment < 0)
			{
				for (int i = _start; i >= 0 && _pictureCount > 0 && _run; i += _increment)
				{
					System.out.println("Loading pictures " + i);
					try
					{
						pictureList.get(i).getPicture(_context, Picture.Size.THUMB);
						pictureList.get(i).getPicture(_context, Picture.Size.SMALL);
						pictureList.get(i).getPicture(_context, Picture.Size.MEDIUM);
						pictureList.get(i).getPicture(_context, Picture.Size.LARGE);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					synchronized (_pictureCount)
					{
						_pictureCount--;
					}
					if (_pictureCount < 0)
					{
						_pictureCount = 0;
					}
					System.out.println(_pictureCount + " pictures remaining");
				}
			}
		}
	}
	
	public JSONObject				doRegister(final String email, final String password, final String confirm_password, final String vanity, final String nickname, final boolean loop)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost("http://stage.diveboard.com/api/register_email");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(7);
			args.add(new BasicNameValuePair("email", email));
			args.add(new BasicNameValuePair("vanity_url", vanity));
			args.add(new BasicNameValuePair("password", password));
			args.add(new BasicNameValuePair("nickname", nickname));
			args.add(new BasicNameValuePair("password_check", confirm_password));
			args.add(new BasicNameValuePair("accept_newsletter_email", (loop == true) ? "true" : "false"));
			args.add(new BasicNameValuePair("apikey", "px6LQxmV8wQMdfWsoCwK"));
			try
			{
				// Set parameters
				postRequest.setEntity(new UrlEncodedFormEntity(args));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONObject json = new JSONObject(result);
				return (json);
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
				client.close();
				return (null);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				client.close();
				return (null);
			} catch (JSONException e)
			{
				e.printStackTrace();
				client.close();
				return (null);
			}
		}
		return (null);
	}

	public Integer getPictureCount() {
		return _pictureCount;
	}

	public void setPictureCount(Integer _pictureCount) {
		this._pictureCount = _pictureCount;
	}
}
