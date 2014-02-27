package com.diveboard.model;

import java.io.File;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveboardLoginActivity;
import com.diveboard.mobile.SettingsActivity;
import com.diveboard.mobile.newdive.NewDiveNumberDialogFragment.EditDiveNumberDialogListener;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Pair;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * Class DiveboardModel
 * Main class for model, manage all intern components
 */
public class					DiveboardModel
{
	private int					_userId;
	private String				_shakenId;
	public static Context		_context;
	private User				_user = null;
	private ConnectivityManager	_connMgr;
	private DataManager			_cache;
	private ArrayList<DataRefreshListener>	_dataRefreshListeners = new ArrayList<DataRefreshListener>();
	
	private String				_temp_user_json;
	private String				_temp_dives_json;
	private User				_temp_user = null;
	private boolean				_enable_overwrite = false;
	private boolean				_connected = false;
	private String				_token;
	private String				_unitPreferences;
	private UserPreference		_preference;
	
	public static ArrayList<Pair<String, Picture>>	pictureList;
	public static ArrayList<String>	savedPictureList;
	public static Semaphore		savedPictureLock;
	private Integer				_pictureCount = 0;
	private LoadPictureThread	_pictureThread1 = null;
	private LoadPictureThread	_pictureThread2 = null;
	private Object				_lock1 = new Object();
	private Object				_lock2 = new Object();
	private RefreshDataThread	_refreshDataThread = null;
	public static Integer		_coTimeout = 15000;
	public static Integer		_soTimeout = 15000;
	public static Integer		_searchTimeout = 15000;
	public static boolean 		_cotimedout = false;
	public static boolean 		_sotimedout = false;
	public static boolean 		_searchtimedout = false;
	private TokenExpireListener	mTokenExpireListener = null;
	
	/*
	 * Method DiveboardModel
	 * Constructor, initialize the object
	 */
	public						DiveboardModel(final int userId, final Context context)
	{
		_userId = userId;
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_cache = new DataManager(context, _userId, _token, this);
		DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
		DiveboardModel.savedPictureList = new ArrayList<String>();
		DiveboardModel.savedPictureLock = new Semaphore(1);
		_initSavedPictures();
	}
	
	public						DiveboardModel(final Context context)
	{
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
		DiveboardModel.savedPictureList = new ArrayList<String>();
		DiveboardModel.savedPictureLock = new Semaphore(1);
		_initSavedPictures();
	}
	
	public boolean				isLogged()
	{
		File file_id = new File(_context.getFilesDir() + "_logged_id");
		File file_token = new File(_context.getFilesDir() + "_logged_token");
//		File unit_preferences = new File(_context.getFilesDir() + "_unit_preferences");
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
				_preference = new UserPreference(_context, _userId);
				fileInputStream.close();
				
				fileInputStream = _context.openFileInput(file_token.getName());
				fileContent = new StringBuffer("");
				buffer = new byte[1];
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
				_token = fileContent.toString();
				fileInputStream.close();
				
//				fileInputStream = _context.openFileInput(unit_preferences.getName());
//				fileContent = new StringBuffer("");
//				buffer = new byte[1];
//				while (fileInputStream.read(buffer) != -1)
//					fileContent.append(new String(buffer));
//				_unitPreferences = fileContent.toString();
//				fileInputStream.close();
				_cache = new DataManager(_context, _userId, _token, this);
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
			_initSavedPictures();
			return true;
		}
		return false;
	}
	
	public JSONObject					doLogin(final String login, final String password)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/login_email");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(3);
			args.add(new BasicNameValuePair("email", login));
			args.add(new BasicNameValuePair("password", password));
			args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
			args.add(new BasicNameValuePair("extralong_token", "true"));
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
				JSONObject status = new JSONObject(result);
				// Analyze data
				boolean success = json.getBoolean("success");
				if (success == false)
					return (status);
				// Initialize user account
				_token = json.getString("token");
				_shakenId = json.getString("id");
				_unitPreferences = json.getJSONObject("units").toString();
				// Get user ID
				HttpGet getRequest = new HttpGet(AppConfig.SERVER_URL + "/api/V2/user/" + _shakenId);
				response = client.execute(getRequest);
				entity = response.getEntity();
				result = ContentExtractor.getASCII(entity);
				json = new JSONObject(result);
				json = json.getJSONObject("result");
				_userId = json.getInt("id");
				_preference = new UserPreference(_context, _userId);
				// Initialize DataManager
				_cache = new DataManager(_context, _userId, _token, this);
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
				
//				file = new File(_context.getFilesDir() + "_unit_preferences");
//				file.createNewFile();
//				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
//				outputStream.write(_unitPreferences.getBytes());
				
				DiveboardModel.pictureList = null;
				DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
				DiveboardModel.savedPictureList = new ArrayList<String>();
				_initSavedPictures();
				return (status);
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
	
	public int					doFbLogin(final String fb_id, final String fb_token)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/login_fb");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(3);
			args.add(new BasicNameValuePair("fbid", fb_id));
			args.add(new BasicNameValuePair("fbtoken", fb_token));
			args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
			args.add(new BasicNameValuePair("extralong_token", "true"));
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
				//JSONObject status = new JSONObject(result);
				// Analyze data
				boolean success = json.getBoolean("success");
				if (success == false)
					return (-1);
				// Initialize user account
				_token = json.getString("token");
				_shakenId = json.getString("id");
				_unitPreferences = json.getJSONObject("units").toString();
				// Get user ID
				HttpGet getRequest = new HttpGet(AppConfig.SERVER_URL + "/api/V2/user/" + _shakenId);
				response = client.execute(getRequest);
				entity = response.getEntity();
				result = ContentExtractor.getASCII(entity);
				json = new JSONObject(result);
				json = json.getJSONObject("result");
				_userId = json.getInt("id");
				_preference = new UserPreference(_context, _userId);
				// Initialize DataManager
				_cache = new DataManager(_context, _userId, _token, this);
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
				
//				file = new File(_context.getFilesDir() + "_unit_preferences");
//				file.createNewFile();
//				outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
//				outputStream.write(_unitPreferences.getBytes());
				
				DiveboardModel.pictureList = null;
				DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
				DiveboardModel.savedPictureList = new ArrayList<String>();
				_initSavedPictures();
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
		if (_refreshDataThread != null)
			_refreshDataThread.cancel();
		
		File file = new File(_context.getFilesDir() + "_logged_id");
		file.delete();
		
		file = new File(_context.getFilesDir() + "_logged_token");
		file.delete();
		
		file = new File(_context.getFilesDir() + "_unit_preferences");
		file.delete();
		if (_user != null)
			_user.setDives(null);
		_user = null;
		
		stopPreloadPictures();
		DiveboardModel.pictureList = null;
		DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
		
		ApplicationController.SudoId = 0;
	}
	
	/*
	 * Method loadData
	 * Automatically load data from offline files or online source if available
	 */
	public void					loadData()
	{
		// Sudo Mode
		if (ApplicationController.SudoId != 0)
		{
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			HttpGet getRequest = new HttpGet(AppConfig.SERVER_URL + "/api/V2/user/" + ApplicationController.SudoId);
			try {
				HttpResponse response = client.execute(getRequest);
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				client.close();
				_loadUser(result, false);
				_loadOnlineData(false);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else
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
	}
	
	public synchronized void	refreshData()
	{
		if (_refreshDataThread == null)
		{
			_refreshDataThread = new RefreshDataThread();
			_refreshDataThread.start();
			// Wait synchronously for data refresh end
			try {
				_refreshDataThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void					refreshData(boolean sync)
	{
		if (_refreshDataThread == null)
		{
			_refreshDataThread = new RefreshDataThread();
			_refreshDataThread.start();
			// Wait synchronously for data refresh end
			if (sync == true)
			{
				try {
					_refreshDataThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class					RefreshDataThread extends Thread
	{
		Boolean						_run;
		
		public						RefreshDataThread()
		{
			_run = true;
		}
		
		public void					cancel()
		{
			_run = false;
		}
		
		@Override
		public void run()
		{
//			while (_run)
//			{
				NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
				
				// Test connectivity
				if (networkInfo != null && networkInfo.isConnected())
				{
					// Online Mode
					// Load data online
					try
					{
						if (_user == null)
						{
							if (_loadOnlineData(false) == false)
							{
								if (mTokenExpireListener != null)
									mTokenExpireListener.onTokenExpire();
								return ;
							}
						}
						else
						{
							if (_loadOnlineData(true) == false)
							{
								if (mTokenExpireListener != null)
									mTokenExpireListener.onTokenExpire();
								return ;
							}
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
				for (DataRefreshListener listener : _dataRefreshListeners)
					listener.onDataRefreshComplete();
				System.out.println("Data refreshed");
				_cache.commitEditOnline();
//				try {
//					Thread.sleep(180000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
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
		{
//			_preference.setUnits(json.getJSONObject("units"));
			_user = new User(json);
		}
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
	private Boolean			_loadOnlineData(final boolean temp_mode) throws IOException, JSONException
	{
		// Load user information
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		HttpPost postRequest;
		HttpContext localContext = null;
		if (ApplicationController.SudoId == 0)
			postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/user/" + Integer.toString(_userId));
		else
		{
			postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/user/" + ApplicationController.SudoId);
			CookieStore cookieStore = new BasicCookieStore();
			localContext = new BasicHttpContext();
			BasicClientCookie cookie = new BasicClientCookie("sudo", Integer.toString(ApplicationController.SudoId));
			cookie.setDomain(".diveboard.com");
			cookie.setPath("/");
			cookieStore.addCookie(cookie);
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			//client.execute(postRequest, localContext);
		}
		ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("auth_token", _token));
		args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
		args.add(new BasicNameValuePair("flavour", "mobile"));
		postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
		HttpResponse response;
		if (ApplicationController.SudoId == 0)
			response = client.execute(postRequest);
		else
			response = client.execute(postRequest, localContext);
		HttpEntity entity = response.getEntity();
		String result = ContentExtractor.getASCII(entity);
		if (ApplicationController.SudoId == 0)
		{
			if (!temp_mode)
				_cache.saveCache(_userId, "user", result);
			else
				_temp_user_json = result;
		}
		_loadUser(result, temp_mode);
		
		// Load dive information
		JSONObject json = new JSONObject(result);
		json = json.getJSONObject("result");
		String dive_str = "%5B";
		System.out.println(json);
		JSONArray jarray = json.getJSONArray("all_dive_ids");
		if (json.isNull("all_dive_ids"))
		{
			client.close();
			return false;
		}
		
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			if (i != 0)
				dive_str = dive_str.concat("%2C%20");
			dive_str = dive_str.concat("%7B%22id%22:").concat(Integer.toString(jarray.getInt(i))).concat("%7D");
		}
		dive_str = dive_str.concat("%5D");
		postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/dive?arg=".concat(dive_str));
		args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("auth_token", _token));
		args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
		args.add(new BasicNameValuePair("flavour", "mobile"));
		args.add(new BasicNameValuePair("arg", dive_str));
		postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
		if (ApplicationController.SudoId == 0)
			response = client.execute(postRequest);
		else
			response = client.execute(postRequest, localContext);
		entity = response.getEntity();
		result = ContentExtractor.getASCII(entity);
		if (ApplicationController.SudoId == 0)
		{
			if (!temp_mode)
				_cache.saveCache(_userId, "dives", result);
			else
				_temp_dives_json = result;
		}
		_loadDives(result, temp_mode);
		if (ApplicationController.SudoId == 0)
		{
			if (!temp_mode)
				_cache.commitCache();
			else
				_enable_overwrite = true;
		}
		client.close();
		return true;
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
		_dataRefreshListeners.add(listener);
	}
	
	public void					overwriteData() throws IOException
	{
		if (_enable_overwrite)
		{
			_cache.saveCache(_userId, "user", _temp_user_json);
			_cache.saveCache(_userId, "dives", _temp_dives_json);
			_cache.commitCache();
			
			// Copy new User into model;
//			_user = (User) _temp_user.clone();
//			_temp_user = null;
		}
//		if (_user != null)
//		{
//			ArrayList<Dive> dives = _user.getDives();
//			for (int i = 0, len = dives.size(); i < len; i++)
//			{
//				if (dives.get(i).getProfile() != null)
//					dives.get(i).getProfile().deletePicture(_context);
//				if (dives.get(i).getProfileV3() != null)
//					dives.get(i).getProfileV3().deletePicture(_context);
//			}
//			_enable_overwrite = false;
//			_applyEdit();
//		}
		_enable_overwrite = false;
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
				System.out.println("EDIT ITEM : " + edit_list.get(i).first);
				String[] info = edit_list.get(i).first.split(":");
				if (info[0].compareTo("Dive") == 0)
					_applyEditDive(Integer.parseInt(info[1]), edit_list.get(i).second);
				else if (info[0].equals("Dive_delete"))
					_applyDeleteDive(Integer.parseInt(info[1]));
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
	
	private void				_applyDeleteDive(final int id)
	{
		ArrayList<Dive>			dives = getDives();
		
		for (int i = 0, size = dives.size(); i < size; i++)
			if (dives.get(i).getId() == id)
			{
				dives.remove(i);
				break ;
			}
	}
	
	private void				_applyEditDive(final int id, final String json) throws JSONException
	{
		ArrayList<Dive> dives = getDives();
		boolean exist = false;
		int i = 0;
		
		// Create Dive if new dive type
		System.out.println("APPLY EDIT " + id + " : " + json);
		for (int size = dives.size(); i < size; i++)
			if (dives.get(i).getId() == id)
			{
				exist = true;
				break ;
			}
		//if (id == -1)
		if (exist == false)
		{
			Dive new_dive = new Dive(new JSONObject(json));
			new_dive.setId(id);
			dives.add(0, new_dive);
		}
		else
		{
			// Apply edit if edit type
//			for (int i = 0, length = dives.size(); i < length; i++)
//			{
//				if (dives.get(i).getId() == id)
//				{
					dives.get(i).applyEdit(new JSONObject(json));
//					break ;
//				}
//			}
		}
	}
	
	/*
	 * Method preloadPictures
	 * When launched, it will preload all created picture in the model.
	 */
	public void					preloadPictures()
	{
		ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//NetworkInfo mobileNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (getPreference().getNetwork() == 0 && wifiNetwork.isConnected() == false)
			return ;
		if (_pictureThread1 == null && _pictureThread2 == null)
		{
			_refreshPictureList();
			_pictureCount = DiveboardModel.pictureList.size();
			_pictureThread1 = new LoadPictureThread(0, 2, 1);
			_pictureThread2 = new LoadPictureThread(1, 2, 2);
			_pictureThread1.start();
			_pictureThread2.start();
		}
	}
	
	public void					pausePreloadPictures()
	{
		System.out.println("PAUSE PRELOAD");
//		if (_pictureThread1 != null)
//			_pictureThread1.pause();
//		if (_pictureThread2 != null)
//			_pictureThread2.pause();
	}
	
	private void				_refreshPictureList()
	{
		System.out.println("Refresh PICTURES");
		
		for (int i = 0, size = DiveboardModel.savedPictureList.size(); i < size; i++)
		{
			String url;
			url = DiveboardModel.savedPictureList.get(i);
			//System.out.println("REFRESH : " + url);
			for (int j = 0; j < DiveboardModel.pictureList.size(); j++)
			{
				if (DiveboardModel.pictureList.get(j).first.equals(url))
				{
					DiveboardModel.pictureList.remove(j);
					j = 0;
				}
			}
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
		private int				_locknb;
		
		public LoadPictureThread(int start, int increment, int locknb)
		{
			_start = start;
			_increment = increment;
			_run = true;
			_locknb = locknb;
		}
		
		public void				pause()
		{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void				cancel()
		{
			_run = false;
		}
		
		@Override
		public void run()
		{
			ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			if (_increment > 0)
			{
				for (int i = _start, size = pictureList.size(); i < size && _pictureCount > 0 && _run; i += _increment)
				{
					wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					try
					{
						if (_locknb == 1)
						{
							synchronized (_lock1)
							{
								//System.out.println("Loading pictures " + i);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.MEDIUM);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.LARGE);
							}
						}
						else if (_locknb == 2)
						{
							synchronized (_lock2)
							{
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.MEDIUM);
								if (!_run || !wifiNetwork.isConnected())
									break ;
								pictureList.get(i).second.getPicture(_context, Picture.Size.LARGE);
							}
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (IndexOutOfBoundsException e)
					{
						e.printStackTrace();
						return ;
					}
					synchronized (_pictureCount)
					{
						_pictureCount--;
					}
					if (_pictureCount < 0)
					{
						_pictureCount = 0;
					}
					//System.out.println(_pictureCount + " pictures remaining");
				}
			}
			else if (_increment < 0)
			{
				for (int i = _start; i >= 0 && _pictureCount > 0 && _run; i += _increment)
				{
					wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					//System.out.println("Loading pictures " + i);
					try
					{
						if (!_run || !wifiNetwork.isConnected())
							break ;
						pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
						if (!_run || !wifiNetwork.isConnected())
							break ;
						pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
						if (!_run || !wifiNetwork.isConnected())
							break ;
						pictureList.get(i).second.getPicture(_context, Picture.Size.MEDIUM);
						if (!_run || !wifiNetwork.isConnected())
							break ;
						pictureList.get(i).second.getPicture(_context, Picture.Size.LARGE);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					catch (IndexOutOfBoundsException e)
					{
						e.printStackTrace();
						return ;
					}
					synchronized (_pictureCount)
					{
						_pictureCount--;
					}
					if (_pictureCount < 0)
					{
						_pictureCount = 0;
					}
					//System.out.println(_pictureCount + " pictures remaining");
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
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/register_email");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(7);
			args.add(new BasicNameValuePair("email", email));
			args.add(new BasicNameValuePair("vanity_url", vanity));
			args.add(new BasicNameValuePair("password", password));
			args.add(new BasicNameValuePair("nickname", nickname));
			args.add(new BasicNameValuePair("password_check", confirm_password));
			args.add(new BasicNameValuePair("accept_newsletter_email", (loop == true) ? "true" : "false"));
			args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
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
	
	private void					_initSavedPictures()
	{
		System.out.println("INIT SAVED PICTURE");
		File file = new File(_context.getFilesDir() + "_saved_pictures");
		if (file.exists())
		{
				FileInputStream fileInputStream;
				try
				{
					fileInputStream = _context.openFileInput(file.getName());
					StringBuffer fileContent = new StringBuffer("");
					byte[] buffer = new byte[2048];
					while (fileInputStream.read(buffer) != -1)
						fileContent.append(new String(buffer));
					
					try
					{
						DiveboardModel.savedPictureLock.acquire();
						
						String[] savedPictureArray = fileContent.toString().split(";");
						
						for (int i = 0, length = savedPictureArray.length; i < length; i++)
						{
							//System.err.println("--------------------REFRESH: " + savedPictureArray[i]);
							DiveboardModel.savedPictureList.add(savedPictureArray[i]);
						}
						
						//DiveboardModel.savedPictureList = new JSONArray(fileContent.toString());
						DiveboardModel.savedPictureLock.release();
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					fileInputStream.close();
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		else
		{
			DiveboardModel.savedPictureList = new ArrayList<String>();
		}
	}
	
	public JSONObject					offlineSearchSpotText(final String term, String lat, String lng, String latSW, String latNE, String lngSW, String lngNE)
	{
//		lat = "-41.298734";
//		lng = "174.781237";
//		latSW = "39.92476837932741";
//		lngSW = "-13.183000099999958";
//		latNE = "59.095806348000615";
//		lngNE = "16.17246865000004";
		if (lng != null)
		{
			Double lng_d = Double.parseDouble(lng);
			lng_d = lng_d - Math.floor((lng_d + 180.0) / 360.0) * 360.0;
			lng = lng_d.toString();
		}
		String DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";
		String DB_NAME = "spots.db";
		File file_db = new File(DB_PATH + DB_NAME);
		if (!file_db.exists())
			return null;
		SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		String condition_str = "";
		if (term != null)
		{
			String[] strarr = term.split(" ");
			String match_str = "";
			for (int i = 0, length = strarr.length; i < length; i++)
			{
				if (i != 0)
					match_str += " ";
				match_str += strarr[i] + "*";
			}
			if (condition_str.length() == 0)
				condition_str += "spots_fts.name MATCH '" + match_str + "'";
			else
				condition_str += " AND spots_fts.name MATCH '" + match_str + "'";
			
		}
		if (latSW != null && latNE != null && lngSW != null && lngNE != null)
		{
			if (Double.parseDouble(lngSW) >= 0 && Double.parseDouble(lngNE) < 0)
			{
				if (condition_str.length() == 0)
					condition_str += "(spots.lng BETWEEN " + lngSW + " AND 180 AND SPOTS.lng BETWEEN 0 AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
				else
					condition_str += " AND (spots.lng BETWEEN " + lngSW + " AND 180 AND SPOTS.lng BETWEEN 0 AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
			}
			if (condition_str.length() == 0)
				condition_str += "(spots.lng BETWEEN " + lngSW + " AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
			else
				condition_str += " AND (spots.lng BETWEEN " + lngSW + " AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
		}
		if (condition_str.length() == 0)
			condition_str += "(spots.private_user_id IS NULL OR spots.private_user_id = " + _userId + ")";
		else
			condition_str += " AND (spots.private_user_id IS NULL OR spots.private_user_id = " + _userId + ")";
		if (lat != null && lng != null)
		{
			Double lat_sqr = Math.pow(Double.parseDouble(lat), 2.0);
			condition_str += " ORDER BY ((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + (MIN((spots.lng - " + lng + ")*(spots.lng - " + lng + "), (spots.lng - " + lng + " + 360)*(spots.lng - " + lng + " + 360), (spots.lng - " + lng + " - 360)*(spots.lng - " + lng + " - 360))) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)) ASC";
		}
		JSONObject result = new JSONObject();
		try {
			result.put("success", true);
			JSONArray jarray = new JSONArray();
			Cursor c;
			if (term == null)
			{
				System.out.println();
				c = mDataBase.query("spots", new String[] {"id", "name", "location_name", "country_name", "lat", "lng", "private_user_id"}, condition_str + " LIMIT 30", null, null, null, null);
			}
			else
				c = mDataBase.rawQuery("SELECT spots_fts.docid, spots_fts.name, spots.location_name, spots.country_name, spots.lat, spots.lng FROM spots_fts, spots WHERE spots_fts.docid = spots.id AND " + condition_str + " LIMIT 30", null);
			if (c.getCount() == 0)
				return null;
			while (c.moveToNext())
			{
				JSONObject new_elem = new JSONObject();
				new_elem.put("id", c.getInt(0));
				new_elem.put("name", c.getString(1));
				new_elem.put("location_name", c.getString(2));
				new_elem.put("country_name", c.getString(3));
				new_elem.put("lat", c.getDouble(4));
				new_elem.put("lng", c.getDouble(5));
				jarray.put(new_elem);
			}
			
			result.put("spots", jarray);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public JSONObject					searchShopText(final String term, final String lat, final String lng, final String latSW, final String latNE, final String lngSW, final String lngNE)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = DiveboardModel._coTimeout;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = DiveboardModel._soTimeout;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			// Initiate POST request
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/search_shop_text");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("term", term));
			if (lat != null)
				args.add(new BasicNameValuePair("lat", lat));
			if (lng != null)
				args.add(new BasicNameValuePair("lng", lng));
			if (latSW != null)
				args.add(new BasicNameValuePair("latSW", latSW));
			if (latNE != null)
				args.add(new BasicNameValuePair("latNE", latNE));
			if (lngSW != null)
				args.add(new BasicNameValuePair("lngSW", lngSW));
			if (lngNE != null)
				args.add(new BasicNameValuePair("lngNE", lngNE));
			try
			{
				args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
				postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				System.out.println("result = " + result);
				JSONObject json = new JSONObject(result);
//				System.out.println(json);
//				client.close();
				return (json);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				DiveboardModel._cotimedout = true;
				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (SocketTimeoutException e) {
				DiveboardModel._sotimedout = true;
				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			//return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			return null;
		}
		return null;
	}
	
	public JSONObject					searchSpotText(final String term, final String lat, final String lng, final String latSW, final String latNE, final String lngSW, final String lngNE)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = DiveboardModel._coTimeout;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = DiveboardModel._soTimeout;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			// Initiate POST request
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/search_spot_text");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("term", term));
			if (lat != null)
				args.add(new BasicNameValuePair("lat", lat));
			if (lng != null)
				args.add(new BasicNameValuePair("lng", lng));
			if (latSW != null)
				args.add(new BasicNameValuePair("latSW", latSW));
			if (latNE != null)
				args.add(new BasicNameValuePair("latNE", latNE));
			if (lngSW != null)
				args.add(new BasicNameValuePair("lngSW", lngSW));
			if (lngNE != null)
				args.add(new BasicNameValuePair("lngNE", lngNE));
			try
			{
				args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
				postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONObject json = new JSONObject(result);
//				System.out.println(json);
//				client.close();
				return (json);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				DiveboardModel._cotimedout = true;
				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (SocketTimeoutException e) {
				DiveboardModel._sotimedout = true;
				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
			return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
		return null;
	}
	
	private JSONObject					_offlineSearchSpotCoord(final String lat_min, final String lng_min, final String lat_max, final String lng_max)
	{
		String DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";
		String DB_NAME = "spots.db";
		File file_db = new File(DB_PATH + DB_NAME);
		if (!file_db.exists())
			return null;
		return null;
	}
	
	public JSONObject					searchSpotCoord(final String lat_min, final String lng_min, final String lat_max, final String lng_max)
	{
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			// Creating web client
			AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			// Initiate POST request
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/search_spot_coord");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
			args.add(new BasicNameValuePair("lat_min", lat_min));
			args.add(new BasicNameValuePair("lng_min", lng_min));
			args.add(new BasicNameValuePair("lat_max", lat_max));
			args.add(new BasicNameValuePair("lng_max", lng_max));
			try
			{
				postRequest.setEntity(new UrlEncodedFormEntity(args));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONObject json = new JSONObject(result);
				client.close();
				return (json);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else
			return _offlineSearchSpotCoord(lat_min, lng_min, lat_max, lng_max);
		return null;
	}
	
	public UserPreference					getPreference()
	{
		return _preference;
	}
	
	public Picture							uploadPicture(File picture_file)
	{
		HttpClient							httpClient = new DefaultHttpClient();
		HttpContext							localContext = new BasicHttpContext();
		HttpPost							httpPost = new HttpPost(AppConfig.SERVER_URL + "/api/picture/upload");
		
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			try {
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				Bitmap bm = BitmapFactory.decodeFile(picture_file.getPath());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
				byte[] b = baos.toByteArray();
				//String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
				entity.addPart("qqfile", new ByteArrayBody(b, "file.jpg"));
				entity.addPart("auth_token", new StringBody(_token));
				entity.addPart("apikey", new StringBody("xJ9GunZaNwLjP4Dz2jy3rdF"));
				entity.addPart("flavour", new StringBody("private"));
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				HttpEntity entity_response = response.getEntity();
				String result = ContentExtractor.getASCII(entity_response);
				JSONObject json = new JSONObject(result);
				if (json.getBoolean("success") == false)
					return null;
				return (new Picture(json.getJSONObject("result")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void					attackTokenExpireListener(TokenExpireListener listener)
	{
		mTokenExpireListener = listener;
	}
	
	public interface			TokenExpireListener
	{
        void					onTokenExpire();
    }
	
	public ArrayList<Buddy>		getOldBuddies()
	{
		ArrayList<Buddy> result = new ArrayList<Buddy>();
		ArrayList<Dive> dives = getDives();
		
		for (int i = 0, length = dives.size(); i < length; i++)
		{
			ArrayList<Buddy> buddies = dives.get(i).getBuddies();
			if (buddies != null && buddies.size() != 0)
			{
				for (int j = 0, buddy_size = buddies.size(); j < buddy_size; j++)
				{
					boolean flag = false;
					for (int k = 0, result_size = result.size(); k < result_size; k++)
					{
						if (result.get(k).getNickname().equals(buddies.get(j).getNickname()))
						{
							flag = true;
							break ;
						}
					}
					if (flag == false)
						result.add(buddies.get(j));
				}
			}
		}
		return result;
	}
	
	public ArrayList<Buddy>		searchBuddy(final String name)
	{		
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			try {
				// Creating web client
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
				// Initiate POST request
				HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/search/user.json");
				// Adding parameters
				ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
				args.add(new BasicNameValuePair("q", name));

				postRequest.setEntity(new UrlEncodedFormEntity(args));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONArray jarray = new JSONArray(result);
				client.close();
				ArrayList<Buddy> buddy_list = new ArrayList<Buddy>();
				for (int i = 0, length = jarray.length(); i < length; i++)
				{
					Buddy buddy = new Buddy(jarray.getJSONObject(i));
					buddy.set_class("User");
					buddy_list.add(buddy);
				}
					
				return (buddy_list);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}