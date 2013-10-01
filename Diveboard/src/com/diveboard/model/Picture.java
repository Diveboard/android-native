package com.diveboard.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class					Picture
{
	private String				_url;
	private Bitmap				_bitmap;
	private boolean				_loaded;
	
	public 						Picture(final String url)
	{
		_url = url;
		_loaded = false;
	}
	
	public boolean					loadPicture(final Context context) throws IOException
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.isConnected())
		{
			URL url = new URL(_url);
			_bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			_loaded = true;
			return true;
		}
		return false;
	}
	
	public Bitmap				getPicture(final Context context) throws IOException
	{
		if (!_loaded && !_loadCachePicture(context))
		{
			if (!loadPicture(context))
				return null;
			_savePicture(context);
		}
		return _bitmap;
	}
	
	/*
	 * Private Method _savePicture
	 * Save the picture into local cache
	 * Argument:	picture : Picture to save
	 */
	private void			_savePicture(final Context context) throws IOException
	{
		String[] picture_name = _url.split("/");
		
		// Create a new file on cache_picture_[picture name]
		File file = new File(context.getCacheDir() + "_picture_" + picture_name[picture_name.length - 1]);
		file.createNewFile();
		// Get the ouput stream
		FileOutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
		// Compress the image and put into file
		_bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
	}
	
	/*
	 * Private Method _loadCachePicture
	 * Load picture from cache, returns false if picture not found
	 * Argument :	pictureName : name of the picture to retrieve
	 */
	private boolean			_loadCachePicture(final Context context) throws FileNotFoundException
	{
		String[] picture_name = _url.split("/");
		
		File file = new File(context.getCacheDir() + "_picture_" + picture_name[picture_name.length - 1]);
		if (file.exists())
		{
			FileInputStream inputStream = context.openFileInput(file.getName());
			_bitmap = BitmapFactory.decodeStream(inputStream);
			_loaded = true;
			return true;
		}
		return false;
	}
}
