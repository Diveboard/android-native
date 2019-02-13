package com.diveboard.mobile;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MapActivity extends Activity {
	private 					GoogleMap mMap;
	private int					mIndex;
	private boolean 			earthViewActive = false;

	@Override
	public void onStart() {
		super.onStart();
		((ApplicationController) getApplication()).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		((ApplicationController) getApplication()).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
		ApplicationController AC = (ApplicationController)getApplicationContext();
		mIndex = getIntent().getIntExtra("index", -1);
		if ((AC.getModel().getDives().get(mIndex).getLat() == null || AC.getModel().getDives().get(mIndex).getLng() == null)
				|| AC.getModel().getDives().get(mIndex).getLat() == 0 && AC.getModel().getDives().get(mIndex).getLng() == 0)
		{
			TextView tv = new TextView(this);
			tv.setText(getResources().getString(R.string.no_gps));
			tv.setTypeface(face);
			tv.setGravity(Gravity.CENTER);
			setContentView(tv);
		}
		else
		{
			setContentView(R.layout.activity_map);
			
			if (mMap == null) {
		        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
		                            .getMap();
		        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
		        // Check if we were successful in obtaining the map.
		        if (mMap != null) {
		        	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		            // The Map is verified. It is now safe to manipulate the map
		        }
		        else
		        	System.out.println("Map non safe");
		        
		        ((ImageView) findViewById(R.id.ic_change_view)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!earthViewActive){
							((ImageView) findViewById(R.id.ic_change_view)).setImageResource(R.drawable.ic_map_view);
							earthViewActive = true;
							System.out.println(String.valueOf(earthViewActive));
							mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						}
						else{
							((ImageView) findViewById(R.id.ic_change_view)).setImageResource(R.drawable.ic_earth_view);
							earthViewActive = false;
							System.out.println(String.valueOf(earthViewActive));
							mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						}				
					}
				});
		        mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(true);
				mMap.getUiSettings().setZoomGesturesEnabled(true);
				
				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);
				Marker marker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(AC.getModel().getDives().get(mIndex).getLat(), AC.getModel().getDives().get(mIndex).getLng())));
				//.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
				System.out.println(AC.getModel().getDives().get(mIndex).getSpot().getId());
				Integer zoom = AC.getModel().getDives().get(mIndex).getSpot().getZoom();
				if (zoom == null)
					zoom = 10;
				if (AC.getModel().getDives().get(mIndex).getLat() != null && AC.getModel().getDives().get(mIndex).getLng() != null)
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(AC.getModel().getDives().get(mIndex).getLat(), AC.getModel().getDives().get(mIndex).getLng()), zoom));
			}
	    }
	}

}
