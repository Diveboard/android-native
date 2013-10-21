package com.diveboard.mobile;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class DiveDetailsFragment extends Fragment{
	private TabHost	 mTabHost;
	
	public DiveDetailsFragment()
	{
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dive_details, container, false);
		// create the TabHost that will contain the Tabs
	    mTabHost = (TabHost)rootView.findViewById(android.R.id.tabhost);
	    Intent intent = new Intent(getActivity().getApplicationContext(), TabEditGearActivity.class);
	    //intent.putExtra("index", mIndex);
	    setupTab(new TextView(getActivity().getApplicationContext()), getResources().getString(R.string.tab_details_label), intent);
	    	    
	    intent = new Intent(getActivity().getApplicationContext(), null);
	    setupTab(new TextView(getActivity().getApplicationContext()), getResources().getString(R.string.tab_notes_label), intent);
	
	    intent = new Intent(getActivity().getApplicationContext(), null);
	    setupTab(new TextView(getActivity().getApplicationContext()), getResources().getString(R.string.tab_gear_label), intent);
	    
	    intent = new Intent(getActivity().getApplicationContext(), null);
	    setupTab(new TextView(getActivity().getApplicationContext()), getResources().getString(R.string.tab_species_label), intent);
	    
	    intent = new Intent(getActivity().getApplicationContext(), null);
	    setupTab(new TextView(getActivity().getApplicationContext()), getResources().getString(R.string.tab_photos_label), intent);
		return rootView;
	}
	
	private void				setupTab(final View view, final String tag, final Intent content)
	{
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
		mTabHost.addTab(setContent);
	}
	
	private View				createTabView(final Context context, final String text)
	{
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		Typeface mFaceB = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.otf");
		tv.setTypeface(mFaceB);
		tv.setTextSize(12);
		tv.setText(text);
		return view;
	}
}
