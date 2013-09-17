package com.ksso.model;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;

public class					DiveboardModel
{
	private int					_userId;
	private Context				_context;
	private	User				_user;
	//private ArrayList<Dive>		_dives = new ArrayList<Dive>();
	private ConnectivityManager	_connMgr;
	protected AndroidHttpClient	_client;

	public						DiveboardModel(final int userId, Context context)
	{
		_userId = userId;
		_context = context;
	}
	
	public void					loadData()
	{
		_connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
		{
			_client = AndroidHttpClient.newInstance("Android");
			HttpGet getRequest = new HttpGet("http://stage.diveboard.com/api/V2/user/".concat(Integer.toString(_userId)));
			try
			{
				HttpResponse response = _client.execute(getRequest);
				HttpEntity entity = response.getEntity();
				String result = ContentExtractor.getASCII(entity);
				JSONObject json = new JSONObject(result);
				json = json.getJSONObject("result");
				_user = new User(json);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public User					getUser()
	{
		return _user;
	}
}
