package com.diveboard.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.text.DateFormat;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class					DatabaseUpdater
{
	private Context				_context;
	private String				DB_PATH;
	private String				DB_NAME = "spots.db";
	
	public						DatabaseUpdater(Context context)
	{
		this._context = context;
	}
	
	public void					launchUpdate()
	{
		DownloadDbThread dlthread = new DownloadDbThread();
		dlthread.start();
	}
	
	private class				DownloadDbThread extends Thread
	{
		@Override
		public void run()
		{
			ConnectivityManager connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetwork.isConnected() == false)
				return ;
			try {
				URL url = new URL("http://stage.diveboard.com/assets/mobilespots.db.gz");
				DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";  
				InputStream mInput = url.openConnection().getInputStream();
				GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(mInput));
				String outFileName = DB_PATH + DB_NAME;
				OutputStream mOutput = new FileOutputStream(outFileName);
				byte[] mBuffer = new byte[1024];
				int mLength;
				while ((mLength = zis.read(mBuffer))>0)
		            mOutput.write(mBuffer, 0, mLength);
		        mOutput.flush();
		        mOutput.close();
		        mInput.close();
		        zis.close();
		        File file = new File(_context.getFilesDir() + "_db_update_date");
				file.createNewFile();
				FileOutputStream outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				Date date = new Date();
				outputStream.write(dateFormat.format(date).getBytes());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
