package com.diveboard.util;

import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.ApplicationController;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DiveboardSpinnerAdapter extends ArrayAdapter<String> {

	private Typeface 			mFaceR;
	private int					mTextSize;
//	private ArrayList<String> 	mItemsList;
	
	public DiveboardSpinnerAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
		mFaceR = ((ApplicationController)context).getModel().getLatoR();
		mTextSize = 20;
	}
	 
	public DiveboardSpinnerAdapter(Context context, int resource, List<String> items) {
		super(context, resource, items);
		// TODO Auto-generated constructor stub
//		mItemsList = items;
		mFaceR = ((ApplicationController)context).getModel().getLatoR();
		mTextSize = 20;
		
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		 View v = super.getView(position, convertView, parent);
		 ((TextView) v).setTypeface(mFaceR);
		 ((TextView) v).setTextSize(mTextSize);
		 return v;
	 }
	 public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
		 View v = super.getDropDownView(position, convertView, parent);
		 ((TextView) v).setTypeface(mFaceR);
		 return v;
	 }

	public Typeface getFont() {
		return mFaceR;
	}

	public void setFont(Typeface mFaceR) {
		this.mFaceR = mFaceR;
	}

	public int getTextSize() {
		return mTextSize;
	}

	public void setTextSize(int mTextSize) {
		this.mTextSize = mTextSize;
	}
 }
