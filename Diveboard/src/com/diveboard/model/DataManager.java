package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ImageHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Pair;
import android.util.SparseArray;
import android.widget.RelativeLayout;

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
	
	/*
	 * Method DataManager
	 * Constructor, initialize the object
	 */
	public						DataManager(final Context context, final int userId, final String token)
	{
		_context = context;
		_userId = userId;
		_cacheData = new SparseArray<HashMap<String, String>>();
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		_token = token;
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
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
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
				byte[] buffer = new byte[1024];
				while (fileInputStream.read(buffer) != -1)
					fileContent.append(new String(buffer));
				String[] edit_list = fileContent.toString().split("#END#");
				for (int j = 0, edit_length = edit_list.length; j < edit_length; j++)
				{
					String[] elem_value = edit_list[j].split("#SEP#");
					if (elem_value.length != 2)
						continue ;
					Pair<String, String> new_elem = new Pair<String, String>(elem_value[0], elem_value[1]);
					_editList.add(new_elem);
				}
			}
		}
		if (elem.size() != 0)
			_cacheData.put(userId, elem);
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
		
	}
	
	public void					save(Object object)
	{
		synchronized (_lock)
		{
			if (object.getClass() == Dive.class)
				_saveDive(object);
			((IModel)object).clearEditList();
		}
		commit();
	}
	
	private void				_saveDive(Object object)
	{
		ArrayList<Pair<String, String>> edit_list = ((IModel)object).getEditList();
		Dive dive = (Dive) object;
		
		for (int i = 0, length = edit_list.size(); i < length; i++)
		{
			String json = "{\"id\":\"" + dive.getId() + "\", \"" + edit_list.get(i).first + "\":\"" + edit_list.get(i).second + "\"}";
			try
			{
				dive.applyEdit(new JSONObject(json));
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			Pair<String, String> new_elem = new Pair<String, String>("Dive:" + Integer.toString(dive.getId()), json);
			_editList.add(new_elem);
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
	
	private void				_cacheEditList()
	{
		FileOutputStream		outputStream;
		
		File file = new File(_context.getFilesDir() + "_" + Integer.toString(_userId) + "_edit");
		try
		{
			file.createNewFile();
			outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
			for (int i = 0, length = _editList.size(); i < length; i++)
			{
				String new_line = _editList.get(i).first + "#SEP#" + _editList.get(i).second + "#END#";
				outputStream.write(new_line.getBytes());
			}
			outputStream.close();
		}
		catch (IOException e)
		{
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
				for (int i = 0, length = jarray.length(); i < length; i++)
				{
					JSONObject temp = jarray.getJSONObject(i);
					if (temp.getInt("id") == Integer.parseInt(info[1]))
					{
						jarray.put(i, result_obj);
						break ;
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
	}
	
	private class CommitOnlineThread implements Runnable
	{
		@Override
		public void run()
		{
			NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
			HttpPost postRequest;
			
			System.out.println("SEND ONLINE ----------------------------------");
			while (_editList.size() != 0)
			{
				// Test connectivity
				if (networkInfo != null && networkInfo.isConnected())
				{
					Pair<String, String> elem;
					synchronized (_lock)
					{
						System.out.println(_editList.get(0).first + " " + _editList.get(0).second);
						elem = _editList.get(0);
						// Process
						AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
						String[] info = elem.first.split(":");
						if (info[0].compareTo("Dive") == 0)
							postRequest = new HttpPost("http://stage.diveboard.com/api/V2/dive");
						else
							postRequest = null;
						if (postRequest == null)
							break ;
						// Adding parameters
						ArrayList<NameValuePair> args = new ArrayList<NameValuePair>(4);
						args.add(new BasicNameValuePair("auth_token", _token));
						args.add(new BasicNameValuePair("apikey", "px6LQxmV8wQMdfWsoCwK"));
						args.add(new BasicNameValuePair("arg", elem.second));
						args.add(new BasicNameValuePair("flavour", "mobile"));
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
							if (json.getBoolean("success") == false)
								break ;
							_applyEditCache(elem.first, json.getJSONObject("result"));
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
						// Remove edit from cache
						_editList.remove(0);
						// Save edit in cache
						_cacheEditList();
					}
				}
				else
					break ;
			}
		}
	}
}
