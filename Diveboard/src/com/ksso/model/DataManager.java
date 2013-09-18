package com.ksso.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.SparseArray;

public class					DataManager
{
	private Context									_context;
	private ConnectivityManager						_connMgr;
	private SparseArray<HashMap<String, String>>	_cacheData; // <UserId, <Category, JSON>>
	
	public						DataManager(final Context context)
	{
		_context = context;
		_cacheData = new SparseArray<HashMap<String, String>>();
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
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
	
	public void					loadCache(final int userId) throws IOException
	{
		FileInputStream			fileInputStream;
		File file = _context.getFilesDir();
		String[] file_list = file.list();
		HashMap<String, String> elem = new HashMap<String, String>();
		
		for (int i = 0, length = file_list.length; i < length; i++)
		{
			// If file name starts with files_[userId]_
			if (file_list[i].indexOf("files_" + Integer.toString(userId) + "_") == 0)
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
		}
		if (elem.size() != 0)
			_cacheData.put(userId, elem);
	}
	
	public String				get(final int userId, final String category)
	{
		if (_cacheData.get(userId) != null)
			return (_cacheData.get(userId).get(category));
		return (null);
	}
}
