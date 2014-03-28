package com.diveboard.model;

import java.util.ArrayList;

import com.diveboard.model.Picture;

public class Wallet {

	private Integer 				mUserId;
	private ArrayList<Picture> 		mPicturesList;
	private ArrayList<String> 		mPicturesIDS;
	private Integer					mSize;
	
	public Wallet (){
		mUserId = -1;
		mPicturesList = null;
		mPicturesIDS = null;
		mSize = 0;
	}
	
	public ArrayList<Picture> getPictures (){
		
		return mPicturesList;
	}
		
}
