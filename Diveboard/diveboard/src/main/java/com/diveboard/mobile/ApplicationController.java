package com.diveboard.mobile;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

public class ApplicationController extends Application {

	private static ApplicationController singleton;
	private DiveboardModel mModel = null;
	private boolean mDataReady = false;
	public static boolean mDataRefreshed = false;
	public static boolean UserVoiceReady = false;
	private int mPageIndex;
	private int mCarouselIndex = 0;
	private Dive mtempDive = null;
	private int mRefresh = 0;
	private int mCurrentTab = 0;
	public static boolean mForceRefresh = false;
	public static int SudoId = 0;
	public static boolean tokenExpired = false;
	
	public Boolean handleLowMemory()
	{
		if (getModel() == null)
		{
			Intent editDiveActivity = new Intent(getApplicationContext(), DiveboardLoginActivity.class);
			editDiveActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(editDiveActivity);
			return true;
		}
		return false;
	}
	
	public boolean isDataRefreshed() {
		return mDataRefreshed;
	}

	public void setDataRefreshed(boolean mDataRefreshed) {
		this.mDataRefreshed = mDataRefreshed;
	}

	public ApplicationController getInstance(){
		return singleton;
	}

	public DiveboardModel getModel() {
		return mModel;
	}

	public void setModel(DiveboardModel mModel) {
		this.mModel = mModel;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
 
	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
 
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public int getPageIndex() {
		return mPageIndex;
	}

	public void setPageIndex(int mPageIndex) {
		this.mPageIndex = mPageIndex;
	}

	public int getCarouselIndex() {
		return mCarouselIndex;
	}

	public void setCarouselIndex(int mCarouselIndex) {
		this.mCarouselIndex = mCarouselIndex;
	}

	public boolean isDataReady() {
		return mDataReady;
	}

	public void setDataReady(boolean mDataReady) {
		this.mDataReady = mDataReady;
	}

	public Dive getTempDive() {
		return mtempDive;
	}

	public void setTempDive(Dive mtempDive) {
		this.mtempDive = mtempDive;
	}

	public int getRefresh() {
		return mRefresh;
	}

	public void setRefresh(int mRefresh) {
		this.mRefresh = mRefresh;
	}

	public int getCurrentTab() {
		return mCurrentTab;
	}

	public void setCurrentTab(int mCurrentTab) {
		this.mCurrentTab = mCurrentTab;
	}
}
