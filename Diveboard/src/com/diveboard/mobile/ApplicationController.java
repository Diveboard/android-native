package com.diveboard.mobile;

import com.diveboard.model.DiveboardModel;

import android.app.Application;
import android.content.res.Configuration;

public class ApplicationController extends Application {
	private static ApplicationController singleton;
	private DiveboardModel mModel = null;
	private boolean mDataReady = false;
	
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
}
