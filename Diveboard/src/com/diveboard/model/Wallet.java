package com.diveboard.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.model.Picture;

public class Wallet {

	private Integer 				mUserId;
	private ArrayList<Picture> 		mPicturesList;
	private ArrayList<Integer> 		mPicturesIDS;
	private Integer					mSize;
	
	public Wallet(){
		mUserId = -1;
		mPicturesList = null;
		mPicturesIDS = null;
		mSize = 0;
	}
	
	public Wallet(final JSONObject new_wallet) throws JSONException{
		
		mUserId = new_wallet.getInt("user_id");
		mSize = new_wallet.getInt("size");
		JSONArray array = new_wallet.getJSONArray("wallet_pictures_ids");
		for (int i = 0; i < array.length() ; i++){
			mPicturesIDS.add(array.getInt(i));
		}
	}
	
	public ArrayList<Picture> getPictures (){
		
		
		
		return mPicturesList;
	}
		
}
