package com.diveboard.mobile;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.TextView;

public class PhotosActivity extends Activity {

	@Override
	protected void onResume()
	{
		super.onResume();
		//System.out.println("onResume");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		TextView tv = ((TextView)findViewById(R.id.activity_in_progress));
		tv.setTypeface(face);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

}
