package com.diveboard.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.text.DateFormat;

import com.diveboard.config.AppConfig;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class					DatabaseUpdater
{
	private Context				_context;
	private String				DB_PATH;
	private String				TEMP_DB_PATH;
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
		private boolean success = false;
		
		@Override
		public void run()
		{
			try{
					ConnectivityManager connMgr = null;
				connMgr = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo wifiNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo dataNetwork = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(connMgr != null)
					if (wifiNetwork.isConnected() == false){
						return ;
				}
			}catch (NullPointerException e){
				
				e.printStackTrace();
				return;
			}
			
			
			try {
				//We download first the database to a temp folder and once it has successfully be downloaded then it is copied to the final directory
				URL url = new URL(AppConfig.SERVER_URL + "/assets/mobilespots.db.gz");
				DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/" : "/data/data/" + _context.getPackageName() + "/databases/";  
				TEMP_DB_PATH = (android.os.Build.VERSION.SDK_INT >= 17) ? _context.getApplicationInfo().dataDir + "/databases/aux/" : "/data/data/" + _context.getPackageName() + "/databases/aux/";  
				InputStream mInput = url.openConnection().getInputStream();
				GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(mInput));
				File tempFile = new File(TEMP_DB_PATH);
				File file = new File(DB_PATH);
				file.mkdirs();
				tempFile.mkdirs();
				File outputFile = new File(TEMP_DB_PATH, DB_NAME);
				OutputStream mOutput = new FileOutputStream(outputFile);
				byte[] mBuffer = new byte[1024];
				int mLength;
				while ((mLength = zis.read(mBuffer))>0)
		            mOutput.write(mBuffer, 0, mLength);
		        mOutput.flush();
		        mOutput.close();
		        mInput.close();
		        zis.close();
				System.out.println("DB downloaded correctly");

//				//We copy the recently downloaded db to the final directory
//				InputStream in = new FileInputStream(TEMP_DB_PATH);
//			    OutputStream out = new FileOutputStream(DB_PATH);
//
//			    // Transfer bytes from temp to DB_PATH
//			    byte[] buf = new byte[1024];
//			    int len;
//			    while ((len = in.read(buf)) > 0) {
//			        out.write(buf, 0, len);
//			    }
//			    in.close();
//			    out.close();
				
				//We copy the recently downloaded db to the final directory
				FileInputStream inStream = new FileInputStream(TEMP_DB_PATH + DB_NAME);
			    FileOutputStream outStream = new FileOutputStream(DB_PATH + DB_NAME);
			    FileChannel inChannel = inStream.getChannel();
			    FileChannel outChannel = outStream.getChannel();
			    inChannel.transferTo(0, inChannel.size(), outChannel);
			    inStream.close();
			    outStream.close();
		        success = true;
				
				file = new File(_context.getFilesDir() + "_db_update_date");
				file.createNewFile();
				FileOutputStream outputStream = _context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				Date date = new Date();
				outputStream.write(dateFormat.format(date).getBytes());
				outputStream.close();
				System.out.println("SPOTS.DB READY TO USE");

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
