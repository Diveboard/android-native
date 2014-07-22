package com.diveboard.mobile;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Shop;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ClosestShopActivity extends NavDrawer {
	private 										GoogleMap mMap;
	private boolean 								earthViewActive = false;
	LocationManager 								mLocationManager;
	myLocationListener 								mLocationListener;
	Double 											mLatitude = 0.0;
	Double 											mLongitude = 0.0;
	Marker 											myMarker;
	ApplicationController 							AC;
	private ArrayList<Pair<Marker,Shop>> 			mListShops;
	private int										mMaxNumShops = 10;
//	private List<Marker>		mListMarkers = new ArrayList<Marker>();
//	private List<Shop> 			mListShops;
	
	private class myLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			mLongitude = location.getLongitude();
			mLatitude = location.getLatitude();
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

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
	
	
	@Override
	public void onPause() {
		if (mLocationManager != null && mLocationListener != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		System.out.println("destroy");
		if (mLocationManager != null && mLocationListener != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		super.onDestroy();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_closest_shop);
//		setContentView(R.layout.activity_closest_shop);
		AC = (ApplicationController)getApplicationContext();
		Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Lato-Light.ttf");
		Typeface mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
		TextView mTitle = (TextView) findViewById(R.id.title);
		mTitle.setTypeface(mFaceB);
		mTitle.setText(getResources().getString(R.string.title_banner_closest_shop));
		Button save = (Button) findViewById(R.id.save_button);
		save.setTypeface(mFaceB);
		save.setText(getResources().getString(R.string.save_button));
			
		if (mMap == null) {
			SupportMapFragment suppMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mMap = suppMap.getMap();
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

			((ImageView) findViewById(R.id.GPSImage)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (myMarker != null)
						myMarker.remove();
					activeGPS(true);
				}
			});
			mMap.getUiSettings().setAllGesturesEnabled(true);
			mMap.getUiSettings().setMyLocationButtonEnabled(true);
			mMap.getUiSettings().setZoomControlsEnabled(true);
			mMap.getUiSettings().setZoomGesturesEnabled(true);

			mMap.getUiSettings().setRotateGesturesEnabled(true);
			mMap.getUiSettings().setScrollGesturesEnabled(true);
			mMap.getUiSettings().setCompassEnabled(true);
			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {
					// Calls Google Maps					
//					if(myMarker != null){
//						if(!myMarker.equals(marker)){
//							String gmapsquery = "http://maps.google.com/maps?saddr=" + String.valueOf(myMarker.getPosition().latitude) + "," + String.valueOf(myMarker.getPosition().longitude) + "&daddr=" + String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude);
//							System.out.println("About to call GOOGLE MAPS WITH:\n" + gmapsquery);
//							Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(gmapsquery));
//							startActivity(intent);
//						}
//					} else {
//						double latitude = marker.getPosition().latitude;
//						double longitude = marker.getPosition().longitude;
//						String label = marker.getTitle();
//						String uriBegin = "geo:" + latitude + "," + longitude;
//						String query = latitude + "," + longitude + "(" + label + ")";
//						String encodedQuery = Uri.encode(query);
//						String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
//						Uri uri = Uri.parse(uriString);
//						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
//						startActivity(intent);
//					}
					if(myMarker != null && !myMarker.equals(marker)){
						String mShopUrl = ""; 
						for(int i = 0; i < mListShops.size(); i++){
							if( mListShops.get(i).first.equals(marker) && mListShops.get(i).second != null){
								mShopUrl = "http://scu.bz/" + mListShops.get(i).second.getShakenId();
								break;
								}
						}
						if(!mShopUrl.isEmpty()){
							Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mShopUrl));
							startActivity(intent);
						}
							
					}
				}
			});
			mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
				
				@Override
				public void onCameraChange(CameraPosition arg0) {
					// TODO Auto-generated method stub
					ShopsTask shops_task = new ShopsTask();
					LatLngBounds b = mMap.getProjection().getVisibleRegion().latLngBounds;
					mLatitude = mMap.getCameraPosition().target.latitude;
					mLongitude = mMap.getCameraPosition().target.longitude;
					if (b != null)
						shops_task.execute(mLatitude.toString(), mLongitude.toString(), Double.toString(b.southwest.latitude), Double.toString(b.northeast.latitude), Double.toString(b.southwest.longitude), Double.toString(b.northeast.longitude));
					else
						shops_task.execute(mLatitude.toString(), mLongitude.toString(), null, null, null, null);
				}
			});
			
			
			activeGPS(false);
//			LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//			if (bounds != null)
//				shops_task.execute(mLatitude.toString(), mLongitude.toString(), Double.toString(bounds.southwest.latitude), Double.toString(bounds.northeast.latitude), Double.toString(bounds.southwest.longitude), Double.toString(bounds.northeast.longitude));
//			else
//				shops_task.execute(mLatitude.toString(), mLongitude.toString(), null, null, null, null);
			
		}
	}
	
	public void activeGPS(final boolean showMarker) {
		
		if (mLocationManager != null && mLocationListener != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates
		mLocationListener = new myLocationListener();
		// Register the listener with the Location Manager to receive location updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		
		Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		float mZoom;
		if(showMarker) 
			mZoom = 15.0f;
		else
			mZoom = 12.0f;
		
		if (lastKnownLocation == null)
			lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		if (lastKnownLocation != null) {
			mLongitude = lastKnownLocation.getLongitude();
			mLatitude = lastKnownLocation.getLatitude();
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude,mLongitude), mZoom);
			mMap.animateCamera(update, new CancelableCallback() {

				@Override
				public void onFinish() {
					if (showMarker){
						myMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(mLatitude, mLongitude))
						.title("My current location")
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.diveboard_marker)));
					}
				}

				@Override
				public void onCancel() {
					// TODO Auto-generated method stub

				}
			});
		}
	}
	
	private class ShopsTask extends AsyncTask<String, Void, JSONObject>
	{
    	private JSONObject	result;
    	private JSONArray	mArray;
    	private boolean		searchDone = false;
    	
    	private class SearchTimer extends Thread
    	{
    		private String[] query;
    		
    		
    		public SearchTimer(String... query)
    		{
    			this.query = query;
    		}
    		
    		@Override
    		public void run()
    		{
    			//ApplicationController AC = (ApplicationController)mContext;
//    			System.out.println(query[0] + " " + query[1] + " " + query[2] + " " + query[3] + " " + query[4] + " " + query[5] + " " + query[6]);
    			result = AC.getModel().searchShopText(null, query[0], query[1], query[2], query[3], query[4], query[5]);
//    			System.out.println(result);
    			searchDone = true;
    		}
    	}
    	
		protected JSONObject doInBackground(String... query)
		{
			SearchTimer task = new SearchTimer(query);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false)
				{
					return null;
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(JSONObject result)
		{
			if (DiveboardModel._searchtimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(AC, "Shop Search Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(AC, "Connection Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			}
			else if (DiveboardModel._sotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(AC, "Socket Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			try {
				if (result != null && result.getBoolean("success") == true)
				{
					try {
						mArray = result.getJSONArray("shops");
						mListShops = new ArrayList<Pair<Marker,Shop>>();
						
						if (mArray.length() > 0)
						{	
							mMap.clear();
							if(myMarker != null){
								myMarker = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(myMarker.getPosition().latitude, myMarker.getPosition().longitude))
								.title("My current location")
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.diveboard_marker)));
							}
							for (int i = 0; i < Math.min(mArray.length(), mMaxNumShops) ; i++)
							{
								Shop shop = new Shop(mArray.getJSONObject(i));
								if(shop.getLat() != null && shop.getLng() != null){
									Marker shopMarker = mMap.addMarker(new MarkerOptions()
									.position(new LatLng(shop.getLat(), shop.getLng()))
									.title(shop.getName() + ", " + shop.getCountryName())
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
									mListShops.add(new Pair<Marker, Shop>(shopMarker, shop));

								}
								
							}
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					Toast toast = Toast.makeText(AC, "We are sorry, but something went wrong while looking for shops around. Check your internet connection", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onCancelled() {
		}
	}

}
