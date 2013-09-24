package com.diveboard.model;

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
	
	public 						Picture(String url)
	{
		_url = url;
		_loaded = false;
	}
	
	public void					loadPicture(final Context context) throws IOException
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if (networkInfo != null && networkInfo.isConnected())
		{
			URL url = new URL(_url);
			_bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			_loaded = true;
		}
	}
	
	public Bitmap				getPicture(final Context context) throws IOException
	{
		if (!_loaded)
			loadPicture(context);
		return _bitmap;
	}
}
