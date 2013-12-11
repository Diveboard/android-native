package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.diveboard.mobile.ApplicationController;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.DialogFragment;
import android.util.Pair;

public class					Picture
{
	private String				_urlDefault;
	private String				_urlLarge;
	private String				_urlMedium;
	private String				_urlSmall;
	private String				_urlThumbnail;
	private Bitmap				_bitmap;
	private String				_uniqId = null;
	
	public enum					Size
	{
		LARGE,
		MEDIUM,
		SMALL,
		THUMB,
		DEFAULT
	}
	
	public							Picture(final String url)
	{
		_urlDefault = url;
		_urlLarge = url;
		_urlMedium = url;
		_urlSmall = url;
		_urlThumbnail = url;
		Pair<String, Picture> new_elem = new Pair<String, Picture>(url, this);
		DiveboardModel.pictureList.add(new_elem);
	}
	
	public							Picture(final String url, final String uniqid)
	{
		_urlDefault = url;
		_urlLarge = url;
		_urlMedium = url;
		_urlSmall = url;
		_urlThumbnail = url;
		_uniqId = uniqid;
		Pair<String, Picture> new_elem = new Pair<String, Picture>(url, this);
		DiveboardModel.pictureList.add(new_elem);
	}
	
	public 							Picture(final JSONObject json) throws JSONException
	{
		_urlDefault = json.getString("large");
		Pair<String, Picture> new_elem = new Pair<String, Picture>(_urlDefault, this);
		DiveboardModel.pictureList.add(new_elem);
		_urlLarge = json.getString("large");
		new_elem = new Pair<String, Picture>(_urlLarge, this);
		DiveboardModel.pictureList.add(new_elem);
		_urlMedium = json.getString("medium");
		new_elem = new Pair<String, Picture>(_urlMedium, this);
		DiveboardModel.pictureList.add(new_elem);
		_urlSmall = json.getString("small");
		new_elem = new Pair<String, Picture>(_urlSmall, this);
		DiveboardModel.pictureList.add(new_elem);
		_urlThumbnail = json.getString("thumbnail");
		new_elem = new Pair<String, Picture>(_urlThumbnail, this);
		DiveboardModel.pictureList.add(new_elem);
	}
	
	public synchronized boolean		loadPicture(final Context context) throws IOException
	{
		return (loadPicture(context, Size.DEFAULT));
	}
	
	public synchronized boolean		loadPicture(final Context context, final Size size) throws IOException
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//ApplicationController AC = (ApplicationController)context;
		//NetworkInfo networkInfo = (AC.getModel().getPreference().getNetwork() == 0) ? connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI) : connMgr.getActiveNetworkInfo();
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected())
		{
			URL url;
			switch (size)
			{
				case LARGE:
					url = new URL(_urlLarge);
					break ;
				case MEDIUM:
					url = new URL(_urlMedium);
					break ;
				case SMALL:
					url = new URL(_urlSmall);
					break ;
				case THUMB:
					url = new URL(_urlThumbnail);
					break ;
				default:
					url = new URL(_urlDefault);
					break ;
			}
			_bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			return true;
		}
		return false;
	}
	
	public synchronized Bitmap				getPicture(final Context context) throws IOException
	{
		return (getPicture(context, Size.DEFAULT));
	}
	
	public synchronized Bitmap				getPicture(final Context context, final Size size) throws IOException
	{
		if (!_loadCachePicture(context, size))
		{
			if (!loadPicture(context, size))
				return null;
			_savePicture(context, size);
		}
		Bitmap bitmap = _bitmap;
		_bitmap = null;
		return bitmap;
	}
	
	/*
	 * Private Method _savePicture
	 * Save the picture into local cache
	 * Argument:	context : context of application
	 * 				size : size of picture
	 */
	private synchronized void			_savePicture(final Context context, final Size size) throws IOException
	{
		String[] picture_name;
		String url;
		
		switch (size)
		{
			case LARGE:
				picture_name = _urlLarge.split("/");
				url = _urlLarge;
				break ;
			case MEDIUM:
				picture_name = _urlMedium.split("/");
				url = _urlMedium;
				break ;
			case SMALL:
				picture_name = _urlSmall.split("/");
				url = _urlSmall;
				break ;
			case THUMB:
				picture_name = _urlThumbnail.split("/");
				url = _urlThumbnail;
				break ;
			default:
				picture_name = _urlDefault.split("/");
				url = _urlDefault;
				break ;
		}
		
		// Create a new file on cache_picture_[picture name]
		File file;
		if (_uniqId == null)
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1]);
		else
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1] + _uniqId);
		file.createNewFile();
		System.out.println("Saving picture: " + file.getAbsolutePath());
		// Get the ouput stream
		FileOutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
		// Compress the image and put into file
		if (outputStream != null)
		{
			if (_bitmap == null || !_bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream))
				file.delete();
		}
		_updateSaveList(context, url);
	}
	
	private synchronized void				_updateSaveList(final Context context, String url)
	{
		System.out.println("UPDATE SAVE LIST");
		synchronized (DiveboardModel.savedPictureList)
		{
			//DiveboardModel.savedPictureList.put(url);
			DiveboardModel.savedPictureList.add(url);
			url += ";";
			File file = new File(context.getFilesDir() + "_saved_pictures");
			if (!file.exists())
				try {
					file.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			try
			{
				DiveboardModel.savedPictureLock.acquire();
				System.out.println("START WRITE");
				FileOutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_APPEND);
				outputStream.write(url.getBytes());
				outputStream.close();
				System.out.println("END WRITE");
				DiveboardModel.savedPictureLock.release();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Private Method _loadCachePicture
	 * Load picture from cache, returns false if picture not found
	 * Argument :	pictureName : name of the picture to retrieve
	 */
	private synchronized boolean			_loadCachePicture(final Context context, final Size size) throws FileNotFoundException
	{
		String[] picture_name;
		
		switch (size)
		{
			case LARGE:
				picture_name = _urlLarge.split("/");
				break ;
			case MEDIUM:
				picture_name = _urlMedium.split("/");
				break ;
			case SMALL:
				picture_name = _urlSmall.split("/");
				break ;
			case THUMB:
				picture_name = _urlThumbnail.split("/");
				break ;
			default:
				picture_name = _urlDefault.split("/");
				break ;
		}
		
		File file;
		if (_uniqId == null)
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1]);
		else
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1] + _uniqId);
		if (file.exists())
		{
			FileInputStream inputStream = context.openFileInput(file.getName());
			_bitmap = BitmapFactory.decodeStream(inputStream);
			if (_bitmap == null)
				return false;
			return true;
		}
		return false;
	}
	
	public String					getLocalPath(final Context context, final Size size)
	{
		String[] picture_name;
		
		switch (size)
		{
			case LARGE:
				picture_name = _urlLarge.split("/");
				break ;
			case MEDIUM:
				picture_name = _urlMedium.split("/");
				break ;
			case SMALL:
				picture_name = _urlSmall.split("/");
				break ;
			case THUMB:
				picture_name = _urlThumbnail.split("/");
				break ;
			default:
				picture_name = _urlDefault.split("/");
				break ;
		}
		File file;
		if (_uniqId == null)
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1]);
		else
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1] + _uniqId);
		if (file.exists())
			try {
				return (file.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	public void					deletePicture(final Context context)
	{
		String[] picture_name = _urlDefault.split("/");
		File file;
		if (_uniqId == null)
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1]);
		else
			file = new File(context.getCacheDir(), "picture_" + picture_name[picture_name.length - 1] + _uniqId);
		if (file.exists())
		{
			file.delete();
		}
	}
}
