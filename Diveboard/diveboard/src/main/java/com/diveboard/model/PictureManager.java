package com.diveboard.model;

import android.content.Context;

public class					PictureManager
{
	private Context				_context;
	//private String				DB_NAME = "picture.db";
	
	public						PictureManager(Context context)
	{
		_context = context;
//		File file_db = new File(context.getFilesDir() + "/" + DB_NAME);
//		if (!file_db.exists())
//			_createDatabase();
	}
	
//	private void				_createDatabase()
//	{
//		SQLiteDatabase db = SQLiteDatabase.openDatabase(_context.getFilesDir() + "/" + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
//		db.execSQL("CREATE TABLE IF NOT EXISTS pictures (id INTEGER PRIMARY KEY, id_dive INTEGER, position INTEGER, url_large TEXT, url_medium TEXT, url_small TEXT, url_thumbnail TEXT, is_loaded INTEGER DEFAULT 0)");
//		db.close();
//	}
	
//	public void					addPicture()
//	{
//		
//	}
	

}
