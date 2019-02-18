package com.diveboard.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.util.Pair;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;

/*
 * Class DiveboardModel
 * Main class for model, manage all intern components
 */
public class					DiveboardModel
{
	private int					_userId;
	private String				_userEmail = null;
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
	private Wallet 				_wallet = null;
	public static ArrayList<Pair<String, Picture>>	pictureList;
	public static ArrayList<String>	savedPictureList;
	public static Semaphore		savedPictureLock;
	private Integer				_pictureCount = 0;
	private LoadPictureThread	_pictureThread1 = null;
	private LoadPictureThread	_pictureThread2 = null;
	private final Object		_lock_el = new Object();
	private Object				_lock1 = new Object();
	private Object				_lock2 = new Object();
	private RefreshDataThread	_refreshDataThread = null;
	public static Integer		_coTimeout = 5000;
	public static Integer		_soTimeout = 5000;
	public static Integer		_searchTimeout = 5000;
	public static boolean 		_cotimedout = false;
	public static boolean 		_sotimedout = false;
	public static boolean 		_searchtimedout = false;
	private TokenExpireListener	mTokenExpireListener = null;
	private boolean 			_force_refresh = false;
	//Application fonts
	private Typeface 			_latoR = null;
	
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
		_latoR = Typeface.createFromAsset(_context.getAssets(), "fonts/Lato-Light.ttf");
		DiveboardModel.pictureList = new ArrayList<Pair<String, Picture>>();
		DiveboardModel.savedPictureList = new ArrayList<String>();
		DiveboardModel.savedPictureLock = new Semaphore(1);
		_initSavedPictures();
	}
	
	public						DiveboardModel(final Context context)
	{
		_context = context;
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_latoR = Typeface.createFromAsset(_context.getAssets(), "fonts/Lato-Light.ttf");
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
//			if (_user == null)    //Old way of loading data
//				refreshData();
//			else
//				_applyEdit();
			
			if (_user == null){
				System.out.println("REFRESHING DATA");
				refreshData();
			}
			else if(_cache.getEditList().size() > 0){
				System.out.println("APPLYING EDIT CHANGES TO DATA");
				// there are changes to be applied
				_applyEdit();
			} 
			else if (ApplicationController.mForceRefresh) {
//				// force refreshDataThread to null so that there is a full sync with the data stored in the server
				System.out.println("FORCING REFRESH");
				NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
				// Test connectivity
				if (networkInfo != null && networkInfo.isConnected()) {
					_user = null;
					_refreshDataThread = null;
					ApplicationController.mForceRefresh = false;
					refreshData();

				}

			}
				
		}
	}
	
	
	public void 				updateUser(){
		System.out.println("Entered in updateUser");
		ArrayList<Pair<String, String>> edit_list = _cache.getEditList();
		for (int i = 0, length = edit_list.size(); i < length; i++) {
			String[] info = edit_list.get(i).first.split(":");
			if (info[0].equals("User"))
				_applyEditUser(edit_list.get(i).second);
		}
	}
	
	public synchronized void	refreshData()
	{
		if (_refreshDataThread == null)
		{
			System.out.println("Refresh data thread has JUST BEEN LAUNCHED");
			_refreshDataThread = new RefreshDataThread();
			_refreshDataThread.start();
			// Wait synchronously for data refresh end
			try {
				_refreshDataThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else
			System.out.println("Refresh data thread was still running...");
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
			System.out.println("Refresh data Thread run method ON");
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
	 * Method refreshUser
	 * Takes a JSON with the new user and updates the user data to model
	 */
	public void				refreshUser(final String json_str) throws JSONException
	{
		
		JSONObject json = new JSONObject(json_str);
		json = json.getJSONObject("result");
		_user = new User(json);
		
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
		String dive_str = "[";
		System.out.println("Data loaded " + json);
		//Storing locally the session email
		if(!json.isNull("contact_email")){
			_userEmail = json.getString("contact_email");	
		}
		JSONArray jarray = json.getJSONArray("all_dive_ids");
		if (json.isNull("all_dive_ids"))
		{
			client.close();
			return false;
		}
		
		for (int i = 0, length = jarray.length(); i < length; i++)
		{
			if (i != 0)
				dive_str = dive_str.concat(", ");
			dive_str = dive_str.concat("{\"id\":").concat(Integer.toString(jarray.getInt(i))).concat("}");
	
		}
		dive_str = dive_str.concat("]");
		postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/dive");

		MultipartEntity margs = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null,Charset.forName("UTF-8"));

		margs.addPart("auth_token", new StringBody(_token));
		margs.addPart("apikey", new StringBody("xJ9GunZaNwLjP4Dz2jy3rdF"));
		margs.addPart("flavour", new StringBody("mobile"));
		margs.addPart("arg", new StringBody(dive_str));
		postRequest.setEntity(margs);

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
	
	private void _applyEdit() {
			try {
				ArrayList<Pair<String, String>> edit_list = _cache.getEditList();
				for (int i = 0, length = edit_list.size(); i < length; i++) {
					System.out.println("EDIT ITEM : " + edit_list.get(i).first + " whose content is: " + edit_list.get(i).second);
					String[] info = edit_list.get(i).first.split(":");
					if (info[0].compareTo("Dive") == 0){
						try{ 
							//Checking the edit_list contains indeed a JSONObject well parsed
							JSONObject temp = new JSONObject(edit_list.get(i).second);
							_applyEditDive(Integer.parseInt(info[1]),edit_list.get(i).second);
						}catch(JSONException e){
							e.printStackTrace();
							continue;
						}
						
					}
						
					else if (info[0].equals("Dive_delete"))
						_applyDeleteDive(Integer.parseInt(info[1]));
					else if (info[0].equals("User"))
						_applyEditUser(edit_list.get(i).second);
					
				}
			} catch (NumberFormatException e) {
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
	
	private void 				_applyEditUser(final String json){
		System.out.println("####Object received in_applyEditUser: " + json);
//		ArrayList<Picture> mWalletPics = new ArrayList<Picture>();
//		ArrayList<Integer> mWalletPicIds = new ArrayList<Integer>();
////		JSONObject changes = new JSONObject();
//		JSONObject j = new JSONObject();
//		try {
//			JSONObject changes = new JSONObject(json);
//		if (!changes.isNull("wallet_pictures")){
//			j.put("wallet_pictures", new JSONArray(changes.getString("wallet_pictures")));
//			System.out.println("Value of j " + j);
//			JSONArray array = j.getJSONArray("wallet_pictures");
//			for (int i = 0; i < array.length(); i++){
//				JSONObject p = array.getJSONObject(i);
//				mWalletPics.add(new Picture(p));
//			}
//			_user.setWalletPictures(mWalletPics);
//		}
//		if(!changes.isNull("wallet_picture_ids")){
//			System.out.println("####Assigning wallet_pictures_ids from Cache to current user:");
//			j.put("wallet_picture_ids", new JSONArray(changes.getString("wallet_picture_ids")));
//			JSONArray array = j.getJSONArray("wallet_picture_ids");
//			for (int i = 0; i < array.length(); i++){
//				mWalletPicIds.add(array.getInt(i));
//			}
//			_user.setWalletPictureIds(mWalletPicIds);
//		}
//		else System.out.println("There was an ERROR transfering the wallet pictures from the Model to current user: \n" + json);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void				_applyEditDive(final int id, final String json) 
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
			try {
				Dive new_dive = new Dive(new JSONObject(json));
				new_dive.setId(id);
				dives.add(0, new_dive);
			} catch (JSONException e){
				e.printStackTrace();
				System.err.println("Dive " + id + " could not be added to the dives list");
			}
			
			
		}
		else
		{
			JSONObject temp = null;
			try {
				System.err.println("Attempting to apply edit list to the dive: " + i + " with this EDIT LIST " + json.toString());
				temp = new JSONObject(json);
				dives.get(i).applyEdit(temp);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Error while parsing the JSONObject");
				return;
			}
				
		}
	}
	
	private boolean checkNetwork()
	{
		final NetworkInfo wifiNetwork = _connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (getPreference().getNetwork() == 0 && wifiNetwork.isConnected() == false)
			return false;
		return true;
	}
	
	/*
	 * Method preloadPictures
	 * When launched, it will preload all created picture in the model.
	 */
	public void					preloadPictures()
	{
		if (checkNetwork() == false)
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
			Log.d("Picture", "start");
			if (_increment > 0)
			{
				for (int i = _start, size = pictureList.size(); i < size && _pictureCount > 0 && _run; i += _increment)
				{
					try
					{
						if (_locknb == 1)
						{
							synchronized (_lock1)
							{
								if(UserPreference.getPictureQuality().equals("m_qual")){
									if (!_run || !checkNetwork())
									break ;
								pictureList.get(i).second.checkPicture(_context, Picture.Size.MEDIUM);
								}
								else{
									if (!_run || !checkNetwork())
									break ;
								pictureList.get(i).second.checkPicture(_context, Picture.Size.LARGE);
								}
//								//System.out.println("Loading pictures " + i);
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
								
								
							}
						}
						else if (_locknb == 2)
						{
							synchronized (_lock2)
							{
								if(UserPreference.getPictureQuality().equals("m_qual")){
									if (!_run || !checkNetwork())
									break ;
								pictureList.get(i).second.checkPicture(_context, Picture.Size.MEDIUM);
								}
								else{
									if (!_run || !checkNetwork())
									break ;
								pictureList.get(i).second.checkPicture(_context, Picture.Size.LARGE);
								}
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.MEDIUM);
//								if (!_run || !wifiNetwork.isConnected())
//									break ;
//								pictureList.get(i).second.getPicture(_context, Picture.Size.LARGE);
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
					//System.out.println("Loading pictures " + i);
					try
					{
//						if (!_run || !wifiNetwork.isConnected())
//							break ;
//						pictureList.get(i).second.getPicture(_context, Picture.Size.THUMB);
//						if (!_run || !wifiNetwork.isConnected())
//							break ;
//						pictureList.get(i).second.getPicture(_context, Picture.Size.SMALL);
						if(UserPreference.getPictureQuality().equals("m_qual")){
							if (!_run || !checkNetwork())
								break ;
							pictureList.get(i).second.checkPicture(_context, Picture.Size.MEDIUM);
						}
						else{
							if (!_run || !checkNetwork())
								break ;
							pictureList.get(i).second.checkPicture(_context, Picture.Size.LARGE);
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
		}
	}
	
	public JSONObject				doRegister(final String email, final String password, final String confirm_password, final String nickname, final boolean loop)
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
			//args.add(new BasicNameValuePair("vanity_url", vanity));
			args.add(new BasicNameValuePair("assign_vanity_url", "true"));
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
							DiveboardModel.savedPictureList.add(savedPictureArray[i]);
						}
						 
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
		JSONObject result = new JSONObject();
		boolean errorDB = false;
		System.err.println("OFFLINE SEARCH of SPOTS");
		if (lng != null)
		{
			Double lng_d = Double.parseDouble(lng);
			lng_d = lng_d - Math.floor((lng_d + 180.0) / 360.0) * 360.0;
			lng = lng_d.toString();
		}
		String DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";
		String DB_NAME = "spots.db";
		File file_db = new File(DB_PATH + DB_NAME);
		if (!file_db.exists()){
			
			try {
				result.put("success", false);
				result.put("error", _context.getResources().getString(R.string.no_db));
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return result;
		}
		
		if(SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY) != null){
		
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
			Cursor c = null;
			try {
				result.put("success", true);
				JSONArray jarray = new JSONArray();
				
				if (term == null)
				{
					c = mDataBase.query("spots", new String[] {"id", "name", "location_name", "country_name", "lat", "lng", "private_user_id"}, condition_str + " LIMIT 30", null, null, null, null);
				}
				else
					c = mDataBase.rawQuery("SELECT spots_fts.docid, spots_fts.name, spots.location_name, spots.country_name, spots.lat, spots.lng FROM spots_fts, spots WHERE spots_fts.docid = spots.id AND " + condition_str + " LIMIT 30", null);
				if (c.getCount() == 0){
					c.close();
					return null;
				}
					
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
				c.close();
				result.put("spots", jarray);
				return result;
			} catch (JSONException e) {
				e.printStackTrace();
				c.close();
			} catch(SQLiteException e){
				e.printStackTrace();
				c.close();
				errorDB = true;
			}
		}
		if(errorDB){
			//DB does not exist or was not loaded properly
			try {
				result.put("success", false);
				result.put("error", _context.getResources().getString(R.string.no_db)); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
		
		return result;
	}
	
	public JSONObject					offlineSearchRegionLocationText(String lat, String lng, String dist)
	{

		System.err.println("OFFLINE SEARCH OF REGIONS/LOCATIONS");
		JSONObject result = new JSONObject();
		boolean errorDB = false;
		
		if(lat != null && lng != null && dist != null)
		{
			String DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";
			String DB_NAME = "spots.db";
			File file_db = new File(DB_PATH + DB_NAME);
			if (!file_db.exists()){
				try {
					result.put("success", false);
					result.put("error", _context.getResources().getString(R.string.no_db));
				
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				return result;
			}
				
			if(SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY) != null){
				SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
				String condition_str_country = "";
				String condition_str_reg = "";
				String condition_str_loc = "";
				String NElat = "";
				String SWlat = "";
				String NElng = "";
				String SWlng = "";
				Double lat_d = Double.parseDouble(lat);
				Double lng_d = Double.parseDouble(lng);
				Double dist_d = Double.parseDouble(dist);
				boolean onEdge = false; 
				
				
						//Calculate the space of search
				//Check adding the distance does not take the search coordinate "out of the map"
				if(lat_d + dist_d < 90.0){
					NElat = String.valueOf(lat_d + dist_d);
				}
				else{
					NElat = "90.0";
				}
					
				if(lat_d - dist_d > -90.0){
					SWlat = String.valueOf(lat_d - dist_d);
				}
				else{
					SWlat = "-90.0";
				}		
				
				if(lng_d + dist_d < 180.0){
					NElng = String.valueOf(lng_d + dist_d);
				}
				else{
					onEdge = true;
					Double add = lng_d + dist_d - 180.0;
					NElng = String.valueOf((180.0 - add) * (-1));
				}
				
				if(lng_d - dist_d > -180.0){
					SWlng = String.valueOf(lng_d - dist_d);
				}
				else{
					onEdge = true;
					Double add = lng_d - dist_d + 180.0;
					SWlng = String.valueOf(180.0 + add);
				}
			
				if(!onEdge)
				{
					condition_str_country += "(spots.lng BETWEEN " + SWlng + " AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
					condition_str_reg += "(spots.lng BETWEEN " + SWlng + " AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
					condition_str_loc += "(spots.lng BETWEEN " + SWlng + " AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
				}
				else
				{
					condition_str_country += "(spots.lng BETWEEN " + SWlng + " AND 180 OR spots.lng BETWEEN -180 AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
					condition_str_reg += "(spots.lng BETWEEN " + SWlng + " AND 180 OR spots.lng BETWEEN -180 AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
					condition_str_loc += "(spots.lng BETWEEN " + SWlng + " AND 180 OR spots.lng BETWEEN -180 AND " + NElng + " AND spots.lat BETWEEN " + SWlat + " AND " + NElat + ")";
				}
				
				if (lat != null && lng != null)
				{
					Double lat_sqr = Math.pow(Double.parseDouble(lat), 2.0);
					condition_str_country +=  " GROUP BY spots.country_id having count(*) > 2 ORDER BY MIN(MIN ((((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + ")*(spots.lng - " + lng + ") * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " + 360)*(spots.lng - " + lng + " + 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + " )*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " - 360)*(spots.lng - " + lng + " - 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))))) ASC ";
					condition_str_reg += 	 		" GROUP BY regions.id having count(*) > 2 ORDER BY MIN(MIN ((((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + ")*(spots.lng - " + lng + ") * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " + 360)*(spots.lng - " + lng + " + 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + " )*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " - 360)*(spots.lng - " + lng + " - 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))))) ASC ";
					condition_str_loc +=     " GROUP BY spots.location_id having count(*) > 2 ORDER BY MIN(MIN ((((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + ")*(spots.lng - " + lng + ") * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " + 360)*(spots.lng - " + lng + " + 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))),(((spots.lat - " + lat + " )*(spots.lat - " + lat + ")) + ((spots.lng - " + lng + " - 360)*(spots.lng - " + lng + " - 360) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)))))) ASC ";
				}
				
	//			if (SWlat != null && NElat != null && SWlng != null && NElng != null)
	//			{
	//				if (Double.parseDouble(lngSW) >= 0 && Double.parseDouble(lngNE) < 0)
	//				{
	//					if (condition_str.length() == 0)
	//						condition_str += "(spots.lng BETWEEN " + lngSW + " AND 180 AND SPOTS.lng BETWEEN 0 AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
	//					else
	//						condition_str += " AND (spots.lng BETWEEN " + lngSW + " AND 180 AND SPOTS.lng BETWEEN 0 AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
	//				}
	//				if (condition_str.length() == 0)
	//					condition_str += "(spots.lng BETWEEN " + lngSW + " AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
	//				else
	//					condition_str += " AND (spots.lng BETWEEN " + lngSW + " AND " + lngNE + " AND spots.lat BETWEEN " + latSW + " AND " + latNE + ")";
	//			}
	//			if (condition_str.length() == 0)
	//				condition_str += "(spots.private_user_id IS NULL OR spots.private_user_id = " + _userId + ")";
	//			else
	//				condition_str += " AND (spots.private_user_id IS NULL OR spots.private_user_id = " + _userId + ")";
	//			if (lat != null && lng != null)
	//			{
	//				Double lat_sqr = Math.pow(Double.parseDouble(lat), 2.0);
	//				condition_str += " ORDER BY ((spots.lat - " + lat + ")*(spots.lat - " + lat + ")) + (MIN((spots.lng - " + lng + ")*(spots.lng - " + lng + "), (spots.lng - " + lng + " + 360)*(spots.lng - " + lng + " + 360), (spots.lng - " + lng + " - 360)*(spots.lng - " + lng + " - 360))) * (1 - (((spots.lat * spots.lat) + " + lat_sqr + ") / 8100)) ASC";
	//			}
				
				Cursor c = null;
				Cursor r = null;
				Cursor l = null;
				try {
					result.put("success", true);
					JSONArray jCountries = new JSONArray();
					JSONArray jRegions = new JSONArray();
					JSONArray jLocations = new JSONArray();
					
					c = mDataBase.query("spots", new String[] {"country_id", "country_name"}, condition_str_country + " LIMIT 10", null, null, null, null);
					r = mDataBase.rawQuery("SELECT spots.region_id, regions.name FROM spots, regions WHERE spots.region_id = regions.id AND " + condition_str_reg + " LIMIT 10", null);
					l = mDataBase.query("spots", new String[] {"location_id", "location_name"}, condition_str_loc + " LIMIT 10", null, null, null, null);
					
					if (c.getCount() == 0)
					{
						c.close();
						System.out.println("NO Countries were found");
						return offlineSearchRegionLocationText(lat, lng, String.valueOf(Double.parseDouble(dist) * 2));	
					}
					if (r.getCount() == 0)
					{
						r.close();
						System.out.println("NO Regions were found");
						return offlineSearchRegionLocationText(lat, lng, String.valueOf(Double.parseDouble(dist) * 2));
					}
					if (l.getCount() == 0)
					{
						l.close();
						System.out.println("NO Locations were found");
						return offlineSearchRegionLocationText(lat, lng, String.valueOf(Double.parseDouble(dist) * 2));
					}
					
					while (c.moveToNext())
					{
						if(!c.getString(1).trim().isEmpty()){
							JSONObject country = new JSONObject();
							country.put("id", c.getInt(0));
							country.put("name", c.getString(1));
							jCountries.put(country);
						}
						
					}
					
					while (r.moveToNext()){
						if(!r.getString(1).trim().isEmpty()){
							JSONObject region = new JSONObject();
							region.put("id", r.getInt(0));
							region.put("name", r.getString(1));
							jRegions.put(region);
						}
						
					}
					
					while (l.moveToNext()){
						if(!l.getString(1).trim().isEmpty()){
							JSONObject location = new JSONObject();
							location.put("id", l.getInt(0));
							location.put("name", l.getString(1));
							jLocations.put(location);
						}
						
					}
					
					result.put("countries", jCountries);
					result.put("regions", jRegions);
					result.put("locations", jLocations);
					
					System.out.println("The result is " + result.toString());
					c.close();
					r.close();
					l.close();
					return result;
					
				} catch (JSONException e) {
					e.printStackTrace();
					c.close();
					r.close();
					l.close();
				}catch(SQLiteException e){
					e.printStackTrace();
					c.close();
					r.close();
					l.close();
					errorDB = true;
				}
			} 
		}
		if(errorDB){
			//DB does not exist or was not loaded properly
			try {
				result.put("success", false);
				result.put("error", _context.getResources().getString(R.string.no_db)); 
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
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
//				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (SocketTimeoutException e) {
				DiveboardModel._sotimedout = true;
//				return offlineSearchSpotText(term, lat, lng, latSW, latNE, lngSW, lngNE);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			return null;
		}
		return null;
	}
	
	public JSONObject					searchRegionLocationText(String latitude, String longitude){
		
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
			HttpPost postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/search_region_text");
			// Adding parameters
			ArrayList<NameValuePair> args = new ArrayList<NameValuePair>();
			
			if (latitude != null)
				args.add(new BasicNameValuePair("lat", latitude));
			if (longitude != null)
				args.add(new BasicNameValuePair("lng", longitude));
			
			try
			{
				args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
				postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				System.out.println("Server raw response " + result);
				JSONObject json = new JSONObject(result);
//				client.close();
				return (json);
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ConnectTimeoutException e) {
				DiveboardModel._cotimedout = true; //NEEDS TO BE HANDLED FOR OFFLINE MODE
				return null;
			} catch (SocketTimeoutException e) {
				DiveboardModel._sotimedout = true;
				return null;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			
			return offlineSearchRegionLocationText(latitude, longitude, String.valueOf(2.0));
		}
		
		return null;
	}
	
	public JSONObject					obtainNewSpotID(final String mNewManua){
		
		JSONObject result = new JSONObject();
		
		
		return result;
		
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
				args.add(new BasicNameValuePair("auth_token", _token));
				args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
				postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
				// Execute request
				HttpResponse response = client.execute(postRequest);
				// Get response
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				//System.out.println(result);
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
	
	public String							getToken(){
		return _token;
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
				System.out.println("PICTURE UPLOADED SUCCESSFULLY!\n " + result);
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
	
	public static interface ProgressListener {
		
		void progress(int progress);
	}
	
	public Picture							uploadPicture(File picture_file, ProgressListener mProgressListener)
	{
		HttpClient							httpClient = new DefaultHttpClient();
		HttpContext							localContext = new BasicHttpContext();
		HttpPost							httpPost = new HttpPost(AppConfig.SERVER_URL + "/api/picture/upload");
		int 								mUploadProgress = 0;
		
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			try {
				MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				Bitmap bm = BitmapFactory.decodeFile(picture_file.getPath());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
				bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
				mUploadProgress += 10;
				mProgressListener.progress(mUploadProgress);
				byte[] b = baos.toByteArray();
				//String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
				entity.addPart("qqfile", new ByteArrayBody(b, "file.jpg"));
				entity.addPart("auth_token", new StringBody(_token));
				entity.addPart("apikey", new StringBody("xJ9GunZaNwLjP4Dz2jy3rdF"));
				entity.addPart("flavour", new StringBody("private"));
				httpPost.setEntity(entity);
				mUploadProgress += 10;
				mProgressListener.progress(mUploadProgress);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				HttpEntity entity_response = response.getEntity();
				String result = ContentExtractor.getASCII(entity_response);
				mUploadProgress += 70;
				mProgressListener.progress(mUploadProgress);
				System.out.println("PICTURE UPLOADED SUCCESSFULLY!\n " + result);
				JSONObject json = new JSONObject(result);
				if (json.getBoolean("success") == false)
					return null;
				Picture p = new Picture(json.getJSONObject("result"));
				mUploadProgress += 10;
				mProgressListener.progress(mUploadProgress);
				return p;
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
	
	public JSONObject						uploadWalletPicture(File picture_file)
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
//				entity.addPart("album", new StringBody("wallet"));
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost, localContext);
				HttpEntity entity_response = response.getEntity();
				String result = ContentExtractor.getASCII(entity_response);
				System.out.println("WALLET PICTURE UPLOADED SUCCESSFULLY!\n" + result);
				JSONObject json = new JSONObject(result);
				if (json.getBoolean("success") == false)
					return null;
				return (json);
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
	
	public String 			getSessionEmail(){
		return _userEmail;
	}
	
	public void				setHasRatedApp(boolean hasRated){
		if(hasRated)
			_cache.saveCache(_userId, "hasRatedApp", "true");
		else
			_cache.saveCache(_userId, "hasRatedApp", "false");
		try {
			_cache.commitCache();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean			hasRatedApp(){

		String tmp = _cache.get(_userId, "hasRatedApp");
		if(tmp == null)
			return null;
		if (tmp != null){
			if(tmp.equals("true"))
				return true;
		}
		return false;
	}
	
	public void				setFirstLaunch(Long launch){
		_cache.saveCache(_userId, "firstLaunch", launch.toString());
		try {
			_cache.commitCache();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Long			getFirstLaunch(){

		String tmp = _cache.get(_userId, "firstLaunch");
		if (tmp != null){
			return Long.parseLong(tmp);
		}
		return null;
	}
	
	public void				setLaunchCount(Long launch){
		_cache.saveCache(_userId, "launchCount", launch.toString());
		try {
			_cache.commitCache();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Long			getLaunchCount(){

		String tmp = _cache.get(_userId, "launchCount");
		if (tmp != null){
			return Long.valueOf(tmp);
		}
		return null;
	}
	
	public Typeface getLatoR() {
		return _latoR;
	}
}
