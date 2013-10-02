package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class					Picture
{
	private String				_urlDefault;
	private String				_urlLarge;
	private String				_urlMedium;
	private String				_urlSmall;
	private String				_urlThumbnail;
	private Bitmap				_bitmap;
	private boolean				_loaded;
	
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
		_loaded = false;
	}
	
	public 							Picture(final JSONObject json) throws JSONException
	{
		_urlDefault = json.getString("large");
		_urlLarge = json.getString("large");
		_urlMedium = json.getString("medium");
		_urlSmall = json.getString("small");
		_urlThumbnail = json.getString("thumbnail");
		_loaded = false;
	}
	
	public synchronized boolean		loadPicture(final Context context) throws IOException
	{
		return (loadPicture(context, Size.DEFAULT));
	}
	
	public synchronized boolean		loadPicture(final Context context, final Size size) throws IOException
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
					return false;
			}
			_bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			_loaded = true;
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
		if (!_loaded && !_loadCachePicture(context, size))
		{
			if (!loadPicture(context, size))
				return null;
			_savePicture(context, size);
		}
		return _bitmap;
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
		
		// Create a new file on cache_picture_[picture name]
		File file = new File(context.getCacheDir() + "_picture_" + picture_name[picture_name.length - 1]);
		file.createNewFile();
		// Get the ouput stream
		FileOutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
		// Compress the image and put into file
		if (outputStream != null)
		{
			if (_bitmap == null || !_bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream))
				file.delete();
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
		
		File file = new File(context.getCacheDir() + "_picture_" + picture_name[picture_name.length - 1]);
		if (file.exists())
		{
			FileInputStream inputStream = context.openFileInput(file.getName());
			_bitmap = BitmapFactory.decodeStream(inputStream);
			if (_bitmap == null)
				return false;
			_loaded = true;
			return true;
		}
		return false;
	}
}
