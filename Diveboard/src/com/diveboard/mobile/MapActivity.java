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
import android.widget.TextView;

public class MapActivity extends Activity {
	private 					GoogleMap mMap;
	private int					mIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		ApplicationController AC = (ApplicationController)getApplicationContext();
		mIndex = getIntent().getIntExtra("index", -1);
		if ((AC.getModel().getDives().get(mIndex).getLat() == null || AC.getModel().getDives().get(mIndex).getLng() == null)
				|| AC.getModel().getDives().get(mIndex).getLat() == 0 && AC.getModel().getDives().get(mIndex).getLng() == 0)
		{
			TextView tv = new TextView(this);
			tv.setText("No GPS found for actual dive");
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
		        mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(true);
				mMap.getUiSettings().setZoomGesturesEnabled(true);
				
				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);
				Marker marker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(AC.getModel().getDives().get(mIndex).getLat(), AC.getModel().getDives().get(mIndex).getLng()))
				.title("Hero")
				.snippet("Actual quest"));
				//.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle)));
				if (AC.getModel().getDives().get(mIndex).getLat() != null && AC.getModel().getDives().get(mIndex).getLng() != null)
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(AC.getModel().getDives().get(mIndex).getLat(), AC.getModel().getDives().get(mIndex).getLng()), 14));
			}
	    }
	}

}
