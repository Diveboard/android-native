package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class					NewDiveActivity extends TabActivity
{
	private TabHost				mTabHost;
	private Typeface			mFaceB;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_edit_dive);
	    
	    Dive new_dive = new Dive();
	    ((ApplicationController)getApplicationContext()).setTempDive(new_dive);
	
	    mFaceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
	    
	    // create the TabHost that will contain the Tabs
	    mTabHost = (TabHost)findViewById(android.R.id.tabhost);
	    
	    Intent intent = new Intent(this, TabNewDetailsActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_details_label), intent);
	    	    
	    intent = new Intent(this,TabNewDetailsActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_notes_label), intent);
	
	    intent = new Intent(this,TabNewDetailsActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_gear_label), intent);
	    
	    intent = new Intent(this,TabNewDetailsActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_species_label), intent);
	    
	    intent = new Intent(this,TabNewDetailsActivity.class);
	    setupTab(new TextView(this), getResources().getString(R.string.tab_photos_label), intent);
	}
	
	private void					setupTab(final View view, final String tag, final Intent content)
	{
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
		
		mTabHost.addTab(setContent);
	}
	
	private View				createTabView(final Context context, final String text)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setTypeface(mFaceB);
		tv.setTextSize(12);
		tv.setText(text);
		return view;
	}
}
