package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.facebook.internal.Validate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

/*
 * Class DataManager
 * Manages online and offline data transactions such as changes stacking and commit changes.
 */
public class					DataManager
{
	private Context									_context;
	private ConnectivityManager						_connMgr;
	private SparseArray<HashMap<String, String>>	_cacheData; // <UserId, <Category, JSON>>
	private ArrayList<Pair<String, String>>			_editList = new ArrayList<Pair<String, String>>();
	private final Object							_lock = new Object(); // Lock for _editList
	private int										_userId;
	private String									_token;
	private DiveboardModel							_model;
	private ArrayList<DiveDeleteListener>			_diveDeleteListeners = new ArrayList<DiveDeleteListener>();
	private ArrayList<DiveCreateListener>			_diveCreateListeners = new ArrayList<DiveCreateListener>();
	private Thread									_dataRetrieveThread = null;
	
	/*
	 * Method DataManager
	 * Constructor, initialize the object
	 */
	public						DataManager(final Context context, final int userId, final String token, DiveboardModel model)
	{
		_context = context;
		_userId = userId;
		_cacheData = new SparseArray<HashMap<String, String>>();
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_token = token;
		_model = model;
	}
	
	/*
	 * Method saveCache
	 * Saves data into internal memory (without committing changes)
	 * Arguments :	userId : for which user ID the data belongs to
	 * 				category : in which category the data belongs to ("user"/"dives"/etc.)
	 * 				json : the data in JSON format
	 */
	public void					saveCache(final int userId, final String category, final String json)
	{
		HashMap<String, String>	userElem;
		String key = new String(category);
		String value = new String(json);

		System.err.println("Trying to save values in the local cache " + value);
		
		if ((userElem = _cacheData.get(userId)) == null)
		{
			// No such user id in cache
			HashMap<String, String> elem = new HashMap<String, String>();
			elem.put(key, value);
			_cacheData.put(userId, elem);
		}
		else
		{
			// Found an user in cache
			userElem.put(key, value);
			_cacheData.put(userId, userElem);
		}
	}
	
	/*
	 * Method commitCache
	 * Persists data contained in the internal memory (to offline files and online server)
	 */
	public void					commitCache() throws IOException
	{
			FileOutputStream		outputStream;
			for (int i = 0, length = _cacheData.size(); i < length; i++)
			{
				HashMap<String, String> elem = _cacheData.valueAt(i);
				Iterator<String> keyset = elem.keySet().iterator();
				while (keyset.hasNext())
				{
					String key = keyset.next();
					//Open file /[userid]_[category]
					File file = new File(_context.getFilesDir() + "_" + Integer.toString(_cacheData.keyAt(i)) + "_" + key);
					file.createNewFile();
					outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
					outputStream.write(elem.get(key).getBytes());
//					System.err.println("GET BYTES = " + elem.get(key));
					outputStream.close();
				}
			}
	}
	
	/*
	 * Method loadCache
	 * Loads data from offline files into internal memory for a specific user ID passed through argument
	 */
	public void					loadCache(final int userId) throws IOException
	{
			
			FileInputStream			fileInputStream;
			File file = _context.getFilesDir();
			System.err.println("LOADING FILES FROM CACHE FROM PATH: " + file.getAbsolutePath());
			
			String[] file_list = file.list();
			HashMap<String, String> elem = new HashMap<String, String>();
			
			for (int i = 0, length = file_list.length; i < length; i++)
			{
				// If file name starts with files_[userId]_ and is not a picture and is not edit list
				if ((file_list[i].indexOf("files_" + Integer.toString(userId) + "_") == 0) && (file_list[i].indexOf("files_" + Integer.toString(userId) + "_picture") != 0)
						&& (file_list[i].indexOf("files_" + Integer.toString(userId) + "_edit") != 0))
				{
				String[] name_split = file_list[i].split("_");
				fileInputStream = _context.openFileInput(file_list[i]);
				StringBuffer fileContent = new StringBuffer("");
				byte[] buffer = new byte[1024];
				int n;
				while ((n=fileInputStream.read(buffer)) != -1)
					fileContent.append(new String(buffer, 0, n));
				elem.put(name_split[2], fileContent.toString());
				fileInputStream.close();
			}
				else if ((file_list[i].indexOf("files_" + Integer.toString(userId) + "_edit") == 0))
				{
					// If it's an edit list file
					_editList = null;
					_editList = new ArrayList<Pair<String, String>>();
					fileInputStream = _context.openFileInput(file_list[i]);
					StringBuffer fileContent = new StringBuffer("");
					int n;
					byte[] buffer = new byte[1024];
					while ((n=fileInputStream.read(buffer)) != -1)
						fileContent.append(new String(buffer, 0, n));
//					System.err.println("@" + fileContent);
					String[] edit_list = fileContent.toString().split("#END#");
					for (int j = 0, edit_length = edit_list.length; j < edit_length; j++)
					{
						boolean error = false;
						String[] elem_value = edit_list[j].split("#SEP#");
						if (elem_value.length != 2)
							continue ;
						try  //Check  
						{
							if(!elem_value[0].startsWith("Dive:") && !elem_value[0].startsWith("User:") && !elem_value[0].startsWith("Dive_delete:"))
								continue;
							if(elem_value[0].startsWith("Dive:") || elem_value[0].startsWith("User:")){
								JSONObject jerr = new JSONObject(elem_value[1]);
							}
						}catch (JSONException e){
							e.printStackTrace();
							error= true;
						}
						
						if(!error){
							Pair<String, String> new_elem = new Pair<String, String>(elem_value[0], elem_value[1]);
							System.err.println("New elem added to the EDIT LIST loaded from CACHEE: " + elem_value[0] + " - " + elem_value[1]);
							_editList.add(new_elem);
						}
					}
				}
			}
			if (elem.size() != 0)
				_cacheData.put(userId, elem);
			
	}
	
	public long 				getMemoryUsed(){
		File file = _context.getFilesDir();
		long size = 0;

	    //clear SD cache
	    File[] files = file.listFiles();
	    for (File f:files) {
	    		size = size+f.length();
	    }
	    
//	    File cacheDir = _context.getCacheDir();
//	    File[] cachefiles = cacheDir.listFiles();
//	    long tmpSize;
//	    for (File a:cachefiles) {
//	    	tmpSize = a.length();
//	        size = size + tmpSize;
//	    }
	    
	    Long l = Long.valueOf(size);
	    Double s = l.doubleValue()/1024/1024;
		System.err.println("THE SIZE OF THE PACKAGE PATH IS: " + s + "MB");
		return size;
	}
	
	/*
	 * Method get
	 * Returns data contained on internal memory
	 * Arguments :	userId : which user id we want to retrieve
	 * 				category : what category of data we want to retrieve ("user"/"dives"/etc.)
	 */
	public String				get(final int userId, final String category)
	{
		if (_cacheData.get(userId) != null)
			return (_cacheData.get(userId).get(category));
		return (null);
	}
	
	public void					delete(Object object)
	{
		synchronized (_lock)
		{
			if (object.getClass() == Dive.class)
				_deleteDive(object);
		}
		//commit();
	}
	
	private void				_deleteDive(Object object)
	{
		Dive dive = (Dive) object;
		Pair<String, String> new_elem = new Pair<String, String>("Dive_delete:" + dive.getId(), null);
		_editList.add(new_elem);
		commit();
	}
	
	public void					save(Object object)
	{
		synchronized (_lock)
		{
			if (object.getClass() == Dive.class)
			{
				if (((Dive)object).getId() < 0 && (((Dive)object).getEditList() == null || ((Dive)object).getEditList().size() == 0))
				{
					System.out.println("Entered in _addDive");
					_addDive(object);
					commit();
				}
				else
				{
					System.out.println("Entered in _saveDive");
					_saveDive(object);
					commit();
				}
			}else if(object.getClass() == User.class){
				System.out.println("Entered in _saveUser");
				_saveUser(object);
				commit();
			}
			((IModel)object).clearEditList();
		}
	}
	
	private void				_addDive(Object object)
	{
		JSONObject				json = new JSONObject();
		Dive					dive = (Dive) object;
		
		try
		{
			System.out.println("ADD DIVE");
			json.put("date", dive.getDate());
			json.put("time_in", dive.getTimeIn());
			json.put("time", dive.getTime());
			json.put("maxdepth_value", dive.getMaxdepth().toString());
			if (dive.getMaxdepthUnit() != null)
				json.put("maxdepth_unit", dive.getMaxdepthUnit());
			json.put("duration", dive.getDuration());
			json.put("trip_name", dive.getTripName());
			if (dive.getAltitude() != null)
				json.put("altitude", dive.getAltitude().getDistance(Units.Distance.KM).toString());
			if (dive.getVisibility() != null)
				json.put("visibility", dive.getVisibility());
			if (dive.getCurrent() != null)
				json.put("current", dive.getCurrent());
			if (dive.getTempSurface() != null)
				json.put("temp_surface_value", dive.getTempSurface());
			if (dive.getTempSurfaceUnit() != null)
				json.put("temp_surface_unit", dive.getTempSurfaceUnit());
			if (dive.getTempBottom() != null)
				json.put("temp_bottom_value", dive.getTempBottom());
			if (dive.getTempBottomUnit() != null)
				json.put("temp_bottom_unit", dive.getTempBottomUnit());
			if (dive.getWater() != null)
				json.put("water", dive.getWater());
			json.put("notes", dive.getNotes());
			if (dive.getWeights() != null)
				json.put("weights_value", dive.getWeights().toString());
			if (dive.getWeightsUnit() != null)
				json.put("weights_unit", dive.getWeightsUnit());
			json.put("user_id", _model.getUser().getId());
			if (dive.getSpot() != null)
			{
				JSONObject temp = new JSONObject();
				if (dive.getSpot().getId() != null){
					temp.put("id", dive.getSpot().getId());//If Spot exists
					json.put("spot", temp);
				}
				if(dive.getSpot().getLocationId() != null && dive.getSpot().getRegionId() != null){
					JSONObject loc = new JSONObject();
					JSONObject reg = new JSONObject();
					loc.put("id", dive.getSpot().getLocationId());
					reg.put("id", dive.getSpot().getRegionId());
					//temp.put("region_id", dive.getSpot().getRegionId());
					temp.put("location", loc);
					temp.put("region", reg);
				}
					temp.put("country_name", dive.getSpot().getCountryName());
					temp.put("lat", dive.getSpot().getLat());
					temp.put("lng", dive.getSpot().getLng());
					temp.put("name", dive.getSpot().getName());
					json.put("spot", temp);
				
				System.out.println("ADDED spot on _addDive " + json.toString());
				
				
			}
			if (dive.getShop() != null)
			{
				JSONObject temp = new JSONObject();
				temp.put("id", dive.getShop().getId());
				json.put("shop", temp);
			}
			if (dive.getNumber() != null)
				json.put("number", dive.getNumber());
			json.put("privacy", Integer.toString(dive.getPrivacy()));
			if (dive.getDivetype() != null)
			{
				JSONArray jarray = new JSONArray();
				ArrayList<String> elem = dive.getDivetype();
				for (int i = 0, length = elem.size(); i < length; i++)
				{
					System.out.println("elem : " + elem.get(i));
					jarray.put(elem.get(i));
				}
				json.put("divetype", jarray);
			}
			json.put("safetystops_unit_value", dive.getSafetyString());
			if (dive.getGuide() != null)
				json.put("guide", dive.getGuide());
			if (dive.getPictures() != null)
			{
				JSONArray jarray = new JSONArray();
				ArrayList<Picture> elem = dive.getPictures();
				for (int i = 0, length = elem.size(); i < length; i++)
					jarray.put(elem.get(i).getJson());
				json.put("pictures", jarray);
			}
			if (dive.getTanks() != null){
				JSONArray jarray = new JSONArray();
				ArrayList<Tank> mTanks = dive.getTanks();
				for (int i = 0; i < mTanks.size(); i++){
					jarray.put(mTanks.get(i).getJson());
				}
				System.out.println("HAS TANKS ATTACHED " + jarray.toString());
				json.put("tanks", jarray);
			}
			if (dive.getBuddies() != null)
			{
				JSONArray jarray = new JSONArray();
				ArrayList<Buddy> elem = dive.getBuddies();
				for (int i = 0, length = elem.size(); i < length; i++)
					jarray.put(elem.get(i).getJson());
				json.put("buddies", jarray);
			}
			if (dive.getDiveReviews() != null)
				json.put("dive_reviews", dive.getDiveReviews().getJson());
			//Pair<String, String> new_elem = new Pair<String, String>("Dive:-1", json.toString());
			Pair<String, String> new_elem = new Pair<String, String>("Dive:" + Integer.toString(dive.getId()), json.toString());
			System.err.println("New elem added to the EDIT LIST in _addDive: " + new_elem.first +" - " + new_elem.second);
			_editList.add(new_elem);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void				_saveDive(Object object)
	{
//		ArrayList<Pair<String, String>> edit_list = ((IModel)object).getEditList();
		Dive dive = (Dive) object;
		ArrayList<Pair<String, String>> edit_list = dive.getEditList();
		
		for (int i = 0, length = edit_list.size(); i < length; i++)
		{
			String json = "{\"id\":\"" + dive.getId() + "\"}";
			
			try
			{
				JSONObject obj = new JSONObject(json);
				System.out.println("SAVE DIVE : " + edit_list.get(i).first + " " + edit_list.get(i).second);
				if (edit_list.get(i).first.equals("spot")){
					JSONObject temp = new JSONObject(edit_list.get(i).second);
					Spot mSpot = new Spot(temp);
					JSONObject resul = new JSONObject();
					JSONObject loc = new JSONObject();
					JSONObject reg = new JSONObject();
					if(mSpot.getLocationId() != null && mSpot.getRegionId() != null)
					{
						loc.put("id", mSpot.getLocationId());
						reg.put("id", mSpot.getRegionId());
						resul.put("location", loc);
						resul.put("region", reg);
					}
					
					resul.put("country_name", mSpot.getCountryName());
					resul.put("lat", mSpot.getLat());
					resul.put("lng", mSpot.getLng());
					resul.put("name", mSpot.getName());
					if(!temp.isNull("id"))
					{
						resul.put("id", mSpot.getId());
					}
					obj.put(edit_list.get(i).first, resul);
				}
					
				else if (edit_list.get(i).first.equals("shop"))
					obj.put(edit_list.get(i).first, new JSONObject(edit_list.get(i).second));
				else if (edit_list.get(i).first.equals("divetype"))
					obj.put(edit_list.get(i).first, new JSONArray(edit_list.get(i).second));
				else if (edit_list.get(i).first.equals("pictures"))
					obj.put(edit_list.get(i).first, new JSONArray(edit_list.get(i).second));
				else if (edit_list.get(i).first.equals("buddies"))
					obj.put(edit_list.get(i).first, new JSONArray(edit_list.get(i).second));
				else if (edit_list.get(i).first.equals("dive_reviews"))
					obj.put(edit_list.get(i).first, new JSONObject(edit_list.get(i).second));
				else if (edit_list.get(i).first.equals("tanks"))
					obj.put(edit_list.get(i).first, new JSONArray(edit_list.get(i).second));
				else
				{
					if (edit_list.get(i).second == null)
						obj.put(edit_list.get(i).first, JSONObject.NULL);
					else
						obj.put(edit_list.get(i).first, edit_list.get(i).second);
				}
				dive.applyEdit(obj);
				Pair<String, String> new_elem = new Pair<String, String>("Dive:" + Integer.toString(dive.getId()), obj.toString());
				System.err.println("New elem added to the EDIT LIST in _saveDive: " + new_elem.first +" - " + new_elem.second);
				_editList.add(new_elem);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void				_saveUser(Object object)
	{
		User user = (User) object;
		ArrayList<Pair<String, String>> edit_list = user.getEditList();
		
		for (int i = 0, length = edit_list.size(); i < length; i++)
		{
			String json = "{\"id\":\"" + user.getId() + "\"}";
			
			try
			{
				JSONObject obj = new JSONObject(json);
				System.out.println("SAVE USER : " + edit_list.get(i).first + " " + edit_list.get(i).second);
				
				if (edit_list.get(i).first.equals("wallet_picture_ids"))     
					obj.put(edit_list.get(i).first, edit_list.get(i).second);
				if (edit_list.get(i).first.equals("wallet_pictures"))     
					obj.put(edit_list.get(i).first, edit_list.get(i).second);
				
//				user.applyEdit(obj);
				Pair<String, String> new_elem = new Pair<String, String>("User:" + Integer.toString(user.getId()), obj.toString());
				System.err.println("New elem added to the DataManager EDIT LIST in _saveUser: " + new_elem.first +" - " + new_elem.second);
				_editList.add(new_elem);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Method commit
	 * Save all change of the model into cache and online server
	 */
	public synchronized void	commit()
	{
		synchronized (_lock)
		{
			_cacheEditList();
		}
		commitEditOnline();
	}
	
	private void				_cacheEditList() {
		FileOutputStream outputStream;
		File file = new File(_context.getFilesDir() + "_"
				+ Integer.toString(_userId) + "_edit");
		try {
			file.createNewFile();
			outputStream = _context.openFileOutput(file.getName(),
					Context.MODE_PRIVATE);
			System.err.println("Storing editlist in cache: \n");
			for (int i = 0, length = _editList.size(); i < length; i++) {
				boolean error = false;
				if (_editList.get(i).first.compareTo("Dive") == 0) {
					try // Check
					{
						JSONObject j = new JSONObject(_editList.get(i).second);
					} catch (JSONException e) {
						e.printStackTrace();
						error = true;
					}
				}
				if (!error) {
					String new_line = _editList.get(i).first + "#SEP#"
							+ _editList.get(i).second + "#END#";
					System.err.println(new_line);
					outputStream.write(new_line.getBytes());
				}
			}
			outputStream.close();
		} catch (IOException e) {
			System.err.println("Error: Saving edit list on cache");
		}
	}
	
	public ArrayList<Pair<String, String>>	getEditList()
	{
		return _editList;
	}
	
	/*
	 * Method commitEditOnline
	 * Send user modified data to Diveboard online API
	 */
	public void							commitEditOnline()
	{
		Thread commitOnline = new Thread(new CommitOnlineThread());
		commitOnline.start();
	}
	
	// Apply modifications on Cache List
	private void						_applyEditCache(String elemtag, JSONObject result_obj)
	{
		String[] info = elemtag.split(":");
		
		if (info[0].compareTo("Dive") == 0)
		{
			String result = get(_userId, "dives");
			try
			{
				JSONObject json = new JSONObject(result);
				JSONArray jarray = json.getJSONArray("result");
				//if (Integer.parseInt(info[1]) == -1)
				if (Integer.parseInt(info[1]) < 0)
				{
					JSONArray new_array = new JSONArray();
					new_array.put(0, result_obj);
					for (int i = 0; i < jarray.length(); i++)
						new_array.put(i + 1, jarray.get(i));
					json.put("result", new_array);
				}
				else
				{
					for (int i = 0, length = jarray.length(); i < length; i++)
					{
						JSONObject temp = jarray.getJSONObject(i);
						if (temp.getInt("id") == Integer.parseInt(info[1]))
						{
							jarray.put(i, result_obj);
							json.put("result", jarray);
							break ;
						}
					}
				}
				saveCache(_userId, "dives", json.toString());
				commitCache();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else if (info[0].equals("Dive_delete"))
		{
			String result = get(_userId, "dives");
			JSONObject json;
			try
			{
				json = new JSONObject(result);
				JSONArray jarray = json.getJSONArray("result");
				for (int i = 0, length = jarray.length(); i < length; i++)
				{
					JSONObject temp = jarray.getJSONObject(i);
					if (temp.getInt("id") == Integer.parseInt(info[1]))
					{
						ArrayList<JSONObject> list = new ArrayList<JSONObject>();
						for (int j = 0, len = jarray.length(); j < len; j++)
						{
						    list.add(jarray.getJSONObject(j));
						}
						list.remove(i);
						JSONArray new_jarray = new JSONArray(list);
						
						json.put("result", new_jarray);
						saveCache(_userId, "dives", json.toString());
						commitCache();
						break ;
					}
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (info[0].equals("User"))
		{
			String result = get(_userId, "user");
//			String picturesIds = get(_userId, "wallet_picture_ids");
//			JSONObject pics;
			JSONObject res;
//			JSONArray res;
			try
			{
				res = new JSONObject(result);
//				ids = new JSONObject(picturesIds);
				
//					JSONObject temp = jarray.getJSONObject(i);
					if (res.getJSONObject("result").getInt("id") == Integer.parseInt(info[1]))
					{
//						res = result_obj.getJSONArray("wallet_pictures");
						JSONObject tmp = new JSONObject();
						tmp.put("result", result_obj);
						saveCache(_userId, "user",tmp.toString());
						commitCache();
						return ;
					}
			}
			catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class CommitOnlineThread implements Runnable
	{
		@Override
		public void run()
		{
			NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
			HttpPost postRequest;
			HttpContext localContext = null;
			HttpResponse response;
			
			if (ApplicationController.SudoId != 0)
			{
				CookieStore cookieStore = new BasicCookieStore();
				localContext = new BasicHttpContext();
				BasicClientCookie cookie = new BasicClientCookie("sudo", Integer.toString(ApplicationController.SudoId));
				cookie.setDomain(".diveboard.com");
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
			
			System.out.println("SEND ONLINE ----------------------------------");
			while (_editList.size() != 0)
			{
				// Test connectivity
				if (networkInfo != null && networkInfo.isConnected())
				{
					Pair<String, String> elem;
					synchronized (_lock)
					{
						if (_editList.size() != 0)
						{
							//System.out.println(_editList.get(0).first + " " + _editList.get(0).second);
							elem = _editList.get(0);
							System.out.println("SEND ITEM : " + elem.second);
//							Log.d("SEND ITEM : ", elem.second);
							// Process
							AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
							String[] info = elem.first.split(":");
							if (info[0].compareTo("Dive") == 0)
							{
								postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/dive");
							}
							else if (info[0].equals("Dive_delete"))
							{
								_deleteDive(client, elem.first);
								_editList.remove(0);
								_cacheEditList();
								continue ;
							}else if(info[0].compareTo("User") == 0){
								postRequest = new HttpPost(AppConfig.SERVER_URL + "/api/V2/user/");
							}
							else
								postRequest = null;
							
							if (postRequest == null)
								break ;
							try
							{
								// Adding parameters
								ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(4);
								args.add(new BasicNameValuePair("auth_token", _token));
								args.add(new BasicNameValuePair("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF"));
								JSONObject checkObject = new JSONObject(elem.second);
								if (checkObject.isNull("spot") == false)
								{
									JSONObject spot = checkObject.getJSONObject("spot");
									JSONObject temp = new JSONObject();
									if(!spot.isNull("id")){
										temp.put("id", spot.get("id"));
										checkObject.put("spot", temp);
									}
									else{
										checkObject.put("spot", spot);
									}
									
									args.add(new BasicNameValuePair("arg", checkObject.toString()));
								}
								else 
									args.add(new BasicNameValuePair("arg", elem.second));
								args.add(new BasicNameValuePair("flavour", "mobile"));
								// Set parameters
								postRequest.setEntity(new UrlEncodedFormEntity(args, "UTF-8"));
								// Execute request
								if (ApplicationController.SudoId == 0)
									response = client.execute(postRequest);
								else
									response = client.execute(postRequest, localContext);
								// Get response
								HttpEntity entity = response.getEntity();
								String result = ContentExtractor.getASCII(entity);
								System.out.println("RESULT " + result);
								JSONObject json = new JSONObject(result);
								if (json.getBoolean("success") == false)
									break ;
								if (Integer.parseInt(info[1]) < 0)
								{
									// New Dive segment
									_refreshNewDiveEditList(Integer.parseInt(info[1]), json);
								}
								if (ApplicationController.SudoId == 0){
									_applyEditCache(elem.first, json.getJSONObject("result"));
									if(info[0].equals("User"))
										_model.refreshUser(result);
					
//									_model.updateUser();
								}
									// Fire dive created event
								for (DiveCreateListener listener : _diveCreateListeners)
									listener.onDiveCreateComplete();
							}
							catch (UnsupportedEncodingException e) 
							{
								e.printStackTrace();
								break ;
							}
							catch (IOException e)
							{
								e.printStackTrace();
								break ;
							} catch (JSONException e)
							{
								e.printStackTrace();
								break ;
							}
							client.close();
							
							synchronized (_lock) {
								// Remove edit from cache
								_editList.remove(0);
								// Save edit in cache
								_cacheEditList();
							}
							
						}
					}
				}
				else
				{
					// Fire dive created event
					for (DiveCreateListener listener : _diveCreateListeners)
						listener.onDiveCreateComplete();
					
					for (DiveDeleteListener listener : _diveDeleteListeners)
						listener.onDiveDeleteComplete();
					
					break ;
				}
			}
	        if (_dataRetrieveThread == null)
	        {
	        	_dataRetrieveThread = new Thread(new Runnable()
				{
					public void run()
					{
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("Refresh Thread");
						_model.refreshData();
					}
				});
	        	_dataRetrieveThread.start();
	        }
		}
		
		private void					_deleteDive(AndroidHttpClient client, String elemtag)
		{
			String[]					info = elemtag.split(":");

			HttpDelete deleteRequest = new HttpDelete(AppConfig.SERVER_URL + "/api/V2/dive/" + info[1] + "?auth_token=" + URLEncoder.encode(_token) + "&apikey=" + URLEncoder.encode("xJ9GunZaNwLjP4Dz2jy3rdF") + "&flavour=mobile");
			HttpContext localContext = null;
			
			if (ApplicationController.SudoId != 0)
			{
				CookieStore cookieStore = new BasicCookieStore();
				localContext = new BasicHttpContext();
				BasicClientCookie cookie = new BasicClientCookie("sudo", Integer.toString(ApplicationController.SudoId));
				cookie.setDomain(".diveboard.com");
				cookie.setPath("/");
				cookieStore.addCookie(cookie);
				localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
			
			try
			{
				if (ApplicationController.SudoId == 0)
					client.execute(deleteRequest);
				else
					client.execute(deleteRequest, localContext);
				if (ApplicationController.SudoId == 0)
					_applyEditCache(elemtag, null);
				client.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			for (DiveDeleteListener listener : _diveDeleteListeners)
				listener.onDiveDeleteComplete();
		}
		
		/*
		 * Refresh information of edit list with new dive id
		 */
		private void					_refreshNewDiveEditList(int id, JSONObject json) throws JSONException
		{
			System.err.println("Entering _refreshNewDiveEditList: " + _editList);
			ArrayList<Dive> dives = _model.getDives();
			System.out.println("REFRESH : " + json);
			JSONObject new_dive = json.getJSONObject("result");
			for (int i = 0, size = dives.size(); i < size; i++)
			{
				if (dives.get(i).getId() == id)
				{
					Dive dive = new Dive(new_dive);
					//Apply the changes on the mode dive list
					_model.getDives().set(i, dive);
					break ;
				}
			}
			//Apply the changes on the edit list (update new id)
			for (int j = 0, size2 = _editList.size(); j < size2; j++)
			{
				String[] elem_tag = _editList.get(j).first.split(":");
				System.out.println("CHECKING : " + elem_tag[1] + " = " + id);
				if (Integer.parseInt(elem_tag[1]) == id)
				{
					System.out.println("REFRESH NEW DIVE: " + _editList.get(j).second);
					JSONObject temp_obj;
					try
					{
						if (_editList.get(j).second.equals("null") == false)
						{
							temp_obj = new JSONObject(_editList.get(j).second);
							temp_obj.put("id", new_dive.getInt("id"));
							_editList.set(j, new Pair<String, String>(elem_tag[0] + ":" + new_dive.getInt("id"), temp_obj.toString()));
						}
						else
							_editList.set(j, new Pair<String, String>(elem_tag[0] + ":" + new_dive.getInt("id"), "null"));
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			_cacheEditList();
		}
	}
	
	public void					setOnDiveDeleteComplete(DiveDeleteListener listener)
	{
		_diveDeleteListeners.add(listener);
	}

	public void					setOnDiveCreateComplete(DiveCreateListener listener)
	{
		_diveCreateListeners.add(listener);
	}
	
}
