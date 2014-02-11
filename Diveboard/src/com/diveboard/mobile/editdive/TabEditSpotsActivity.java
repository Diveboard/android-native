package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment.EditConfirmDialogListener;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Spot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class					TabEditSpotsActivity extends FragmentActivity implements EditConfirmDialogListener
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private DiveboardModel				mModel;
	private int							mIndex;
	private SpotsTask 					mSpotsTask;
	private Spot						mSpot;
	private JSONObject					mSelectedObject = null;
	private JSONArray					mArray;
	private Boolean						mHasChanged = false;
	private GoogleMap					mMap;
	private List<Marker>				mListMarkers = new ArrayList<Marker>();
	private Marker						mMyMarker = null;
	LocationManager 					mLocationManager;
	myLocationListener 					mLocationListener;
	Double 								mLatitude = 0.0;
	Double 								mLongitude = 0.0;
	public final int					mZoom = 12;
	
	private class myLocationListener implements LocationListener
	{
		public void onLocationChanged(Location location)
		{
			ApplicationController AC = (ApplicationController)getApplicationContext();
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
	public void onBackPressed()
	{
		if (mModel.getDives().get(mIndex).getEditList().size() > 0)
		{
			EditConfirmDialogFragment dialog = new EditConfirmDialogFragment();
	    	Bundle args = new Bundle();
	    	args.putInt("index", mIndex);
	    	dialog.setArguments(args);
	    	dialog.show(getSupportFragmentManager(), "EditConfirmDialogFragment");
		}
		else
		{
			clearEditList();
		}
	};
	
	public void clearEditList()
	{
		super.onBackPressed();
		Bundle bundle = new Bundle();
		
		// put
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		mModel.getDives().get(mIndex).clearEditList();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        ApplicationController AC = (ApplicationController)getApplicationContext();
        if (AC.handleLowMemory() == true)
			return ;
	    setContentView(R.layout.tab_edit_spots);
	    mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
	    mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
	    mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", 0);
		
		TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(mFaceB);
	    title.setText(getResources().getString(R.string.tab_spots_title));
	    ((TextView)findViewById(R.id.no_spot)).setTypeface(mFaceR);
	     if (mModel.getDives().get(mIndex).getSpot().getId() != 1)
	    	 ((EditText)findViewById(R.id.search_bar)).setText(mModel.getDives().get(mIndex).getSpot().getName());
	    ((TextView)findViewById(R.id.search_bar)).setTypeface(mFaceR);
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.save_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mModel.getDataManager().save(mModel.getDives().get(mIndex));
				ApplicationController AC = (ApplicationController)getApplicationContext();
				AC.setRefresh(2);
				finish();
			}
		});
//	    ((EditText)findViewById(R.id.search_bar)).setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				v.requestFocusFromTouch();
//				return false;
//			}
//		});
//	    ((EditText)findViewById(R.id.search_bar)).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				ListView lv = ((ListView)findViewById(R.id.list_view));
////				SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, new ArrayList<Spot>());
////				lv.setAdapter(adapter);
////				if (mMyMarker != null)
////					mMyMarker.remove();
////				ApplicationController AC = (ApplicationController)getApplicationContext();
////				Integer zoom = AC.getModel().getDives().get(mIndex).getSpot().getZoom();
////				if (zoom == null)
////					zoom = mZoom;
////				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), zoom));
////				mMyMarker = mMap.addMarker(new MarkerOptions()
////				.position(new LatLng(0, 0))
////				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//				((EditText)findViewById(R.id.search_bar)).setText("");
////				mSelectedObject = null;
////				mHasChanged = true;
////				JSONObject jobject = new JSONObject();
////				try {
////					jobject.put("id", 1);
////				} catch (JSONException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
////				mModel.getDives().get(mIndex).setSpot(jobject);
//			}
//		});

	    EditText editText = (EditText) findViewById(R.id.search_bar);
	    editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
	            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	            	mMap.setOnCameraChangeListener(null);
	                doMySearch("search", ((TextView)findViewById(R.id.search_bar)).getText().toString(), null, null, null);
	            	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        		imm.toggleSoftInput(0, 0);
	                handled = true;
	            }
	            return handled;
			}
	    });
		if (mMap == null) {
			FragmentManager fm = getSupportFragmentManager();
	        Fragment fragment = fm.findFragmentById(R.id.mapfragment);
	        SupportMapFragment support = (SupportMapFragment)fragment;
	        mMap = support.getMap();
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
			if (AC.getModel().getDives().get(mIndex).getSpot().getId() != 1)
			{
				((LinearLayout)findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.details_name)).setTypeface(mFaceB);
				((TextView)findViewById(R.id.details_name_content)).setTypeface(mFaceR);
				((TextView)findViewById(R.id.details_gps)).setTypeface(mFaceB);
				((TextView)findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
				((TextView)findViewById(R.id.details_name_content)).setText(AC.getModel().getDives().get(mIndex).getSpot().getName());
				((TextView)findViewById(R.id.details_gps_content)).setText(getPosition());
				((Button)findViewById(R.id.goToSearch)).setTypeface(mFaceB);
				
				mMyMarker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(AC.getModel().getDives().get(mIndex).getSpot().getLat(), AC.getModel().getDives().get(mIndex).getSpot().getLng()))
				.title(AC.getModel().getDives().get(mIndex).getSpot().getName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
				System.out.println(AC.getModel().getDives().get(mIndex).getSpot().getId());
				Integer zoom = AC.getModel().getDives().get(mIndex).getSpot().getZoom();
				if (zoom == null || zoom > mZoom)
					zoom = mZoom;
				if (AC.getModel().getDives().get(mIndex).getSpot().getLat() != null && AC.getModel().getDives().get(mIndex).getSpot().getLng() != null)
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(AC.getModel().getDives().get(mIndex).getSpot().getLat(), AC.getModel().getDives().get(mIndex).getSpot().getLng()), zoom));
			}
			else
			{
				((LinearLayout)findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
				activeGPS(null);
				

				//doMySearch(((TextView)findViewById(R.id.search_bar)).getText().toString());
//				mMyMarker = mMap.addMarker(new MarkerOptions()
//				.position(new LatLng(0, 0))
//				.title(AC.getModel().getDives().get(mIndex).getSpot().getName())
//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//				((LinearLayout)findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
			}
			
		}
    }
    
    public void goToSearch(View view)
    {
    	((LinearLayout)findViewById(R.id.view_details)).setVisibility(View.GONE);
    	((LinearLayout)findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        ((EditText) findViewById(R.id.search_bar)).requestFocusFromTouch();
        ((EditText) findViewById(R.id.search_bar)).setSelection(((EditText) findViewById(R.id.search_bar)).length());
    }
    
    public void activeGPS(View view)
    {
    	InputMethodManager imm = (InputMethodManager)getSystemService(
    		      Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(((EditText) findViewById(R.id.search_bar)).getWindowToken(), 0);
    	((EditText) findViewById(R.id.search_bar)).setText("");
    	
    	//removeMarkers();
    	mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		mLocationListener = new myLocationListener();
		// Register the listener with the Location Manager to receive location updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		//mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastKnownLocation == null)
			lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastKnownLocation != null)
		{
			//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), mZoom));
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), mZoom);
			mMap.animateCamera(update, new CancelableCallback() {
				
				@Override
				public void onFinish() {
					LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
					System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
					doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(), bounds);
					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					
						@Override
						public void onCameraChange(CameraPosition pos) {
							LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
							System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
							mLatitude = mMap.getCameraPosition().target.latitude;
							mLongitude = mMap.getCameraPosition().target.longitude;
							doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(), bounds);
						}
					});
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					
				}
			});
			mLongitude = lastKnownLocation.getLongitude();
			mLatitude = lastKnownLocation.getLatitude();
//			mMyMarker = mMap.addMarker(new MarkerOptions()
//			.position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
//			.title("My location")
//			.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		}
    }
    
//    public void activeGPS()
//    {
//    	//removeMarkers();
//    	mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//		// Define a listener that responds to location updates
//		mLocationListener = new myLocationListener();
//		// Register the listener with the Location Manager to receive location updates
//		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
//		//mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
//		Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//		if (lastKnownLocation == null)
//			lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (lastKnownLocation != null)
//		{
//			//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), mZoom));
//			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), mZoom);
//			mMap.animateCamera(update, new CancelableCallback() {
//				
//				@Override
//				public void onFinish() {
//					LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//					System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
//					doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(), bounds);
//					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
//					
//						@Override
//						public void onCameraChange(CameraPosition pos) {
//							LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
//							System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
//							mLatitude = mMap.getCameraPosition().target.latitude;
//							mLongitude = mMap.getCameraPosition().target.longitude;
//							doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(), bounds);
//						}
//					});
//				}
//				
//				@Override
//				public void onCancel() {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			mLongitude = lastKnownLocation.getLongitude();
//			mLatitude = lastKnownLocation.getLatitude();
////			mMyMarker = mMap.addMarker(new MarkerOptions()
////			.position(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
////			.title("My location")
////			.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
//		}
//    }
    
//    public void displaySpotsAround()
//    {
//    	removeMarkers();
//	    if (mMyMarker != null)
//	    	mMyMarker.remove();
//    }
    
    public String getPosition()
	{
    	ApplicationController AC = (ApplicationController)getApplicationContext();
		String pos = "";
		if (AC.getModel().getDives().get(mIndex).getSpot().getLat() == null)
		{
			pos += "0° ";
			pos += "N";
		}
		else if (AC.getModel().getDives().get(mIndex).getSpot().getLat() >= 0)
		{
			pos += String.valueOf(AC.getModel().getDives().get(mIndex).getSpot().getLat()) + "° ";
			pos += "N";
		}
		else if (AC.getModel().getDives().get(mIndex).getSpot().getLat() < 0)
		{
			pos += String.valueOf(AC.getModel().getDives().get(mIndex).getSpot().getLat() * (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (AC.getModel().getDives().get(mIndex).getSpot().getLng() == null)
		{
			pos += "0° ";
			pos += "E";
		}
		else if (AC.getModel().getDives().get(mIndex).getSpot().getLng() >= 0)
		{
			pos += String.valueOf(AC.getModel().getDives().get(mIndex).getSpot().getLng()) + "° ";
			pos += "E";
		}
		else if (AC.getModel().getDives().get(mIndex).getSpot().getLng() < 0)
		{
			pos += String.valueOf(AC.getModel().getDives().get(mIndex).getSpot().getLng() * (-1)) + "° ";
			pos += "W";
		}
		if ((AC.getModel().getDives().get(mIndex).getSpot().getLat() == null || AC.getModel().getDives().get(mIndex).getSpot().getLat() == 0) && 
				(AC.getModel().getDives().get(mIndex).getSpot().getLng() == null || AC.getModel().getDives().get(mIndex).getSpot().getLng() == 0))
			pos = "";
		return (pos);
	}
    
    public void doMySearch(String swipe, String text, String latitude, String longitude, LatLngBounds bounds)
    {  	
    	ListView lv = ((ListView)findViewById(R.id.list_view));
    	List<Spot> listSpots = new ArrayList<Spot>();
    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
    	lv.setAdapter(adapter);
    	((TextView)findViewById(R.id.no_spot)).setVisibility(View.GONE);
//    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(0, 0);
		((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
    	SpotsTask spots_task = new SpotsTask();
    	if (bounds != null)
    		spots_task.execute(swipe, text, latitude, longitude, Double.toString(bounds.southwest.latitude), Double.toString(bounds.northeast.latitude), Double.toString(bounds.southwest.longitude), Double.toString(bounds.northeast.longitude));
    	else
    		spots_task.execute(swipe, text, latitude, longitude, null, null, null, null);
    	removeMarkers();
	    if (mMyMarker != null)
	    	mMyMarker.remove();
	    //((TextView)findViewById(R.id.search_bar)).setText("");
    }
    
    public void removeMarkers()
    {
    	for (int i = 0; i < mListMarkers.size(); i++)
    	{
    		mListMarkers.get(i).remove();
    	}
    	mListMarkers.clear();
    }
    
    private class SpotsTask extends AsyncTask<String, Void, JSONObject>
	{
    	private JSONObject	result;
    	private boolean		searchDone = false;
    	private String 		swipe;
    	
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
    			ApplicationController AC = (ApplicationController)getApplicationContext();
    			System.out.println(query[0] + " " + query[1] + " " + query[2] + " " + query[3] + " " + query[4] + " " + query[5] + " " + query[6]);
    			result = AC.getModel().searchSpotText(query[0], query[1], query[2], query[3], query[4], query[5], query[6]);
    			System.out.println(result);
    			searchDone = true;
    		}
    	}
    	
		protected JSONObject doInBackground(String... query)
		{
			swipe = query[0];
			ApplicationController AC = (ApplicationController)getApplicationContext();
			SearchTimer task = new SearchTimer(query[1], query[2], query[3], query[4], query[5], query[6], query[7]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false)
				{
					DiveboardModel._searchtimedout = true;
					return AC.getModel().offlineSearchSpotText(query[1], query[2], query[3], query[4], query[5], query[6], query[7]);
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
//			ApplicationController AC = (ApplicationController)getApplicationContext();
//			return AC.getModel().searchSpotText(query[0], null, null, null, null, null, null);
		}
		
		protected void onPostExecute(JSONObject result)
		{
			if (DiveboardModel._searchtimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Spot Search Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			}
			else if (DiveboardModel._sotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getApplicationContext(), "Socket Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			((ProgressBar)findViewById(R.id.progressBar)).setVisibility(View.GONE);
			try {
				if (result != null && result.getBoolean("success") == true)
				{
					try {
						//((ListView)findViewById(R.id.list_view)).setVisibility(View.VISIBLE);
						mArray = result.getJSONArray("spots");
						final ListView lv = ((ListView)findViewById(R.id.list_view));
						final List<Spot> listSpots = new ArrayList<Spot>();
						for (int i = 0; i < mArray.length(); i++)
						{
							Spot spot = new Spot(mArray.getJSONObject(i));
							listSpots.add(spot);
							
						}
						if (listSpots.size() == 0)
						{
							((TextView)findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
						}
						else
						{
							for (int i = 1; i <= listSpots.size(); i++)
							{
								Marker marker = mMap.addMarker(new MarkerOptions()
								.position(new LatLng(listSpots.get(i - 1).getLat(), listSpots.get(i - 1).getLng()))
								.title(i + ": " + listSpots.get(i - 1).getName())
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
								mListMarkers.add(marker);
							}
							mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
								
								@Override
								public boolean onMarkerClick(Marker marker) {
									
//									int id = 0;
//									for (int i = 0; i < listSpots.size(); i++)
//									{
//										System.out.println(listSpots.get(i).getId() + " " + (Integer.valueOf(marker.getId().replace("m", ""))));
//										if (listSpots.get(i).getId() == (Integer.valueOf(marker.getId().replace("m", ""))))
//										{
//											id = listSpots.get(i).getId();
//											break;
//										}
//									}
									if (!marker.equals(mMyMarker)){
										int id = Integer.valueOf(marker.getTitle().substring(0, marker.getTitle().indexOf(":")));
										System.out.println(id);
										lv.smoothScrollToPosition(id);
									}
									marker.showInfoWindow();
									return true;
								}
							});
							SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
							//Zoom out to show markers
							if (swipe.contentEquals("search"))
							{
								LatLngBounds.Builder builder = new LatLngBounds.Builder();
								for (Marker marker : mListMarkers) {
								    builder.include(marker.getPosition());
								}
								LatLngBounds bounds = builder.build();
								int padding = 100; // offset from edges of the map in pixels
								CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
								mMap.animateCamera(cu, new CancelableCallback(){

									@Override
									public void onCancel() {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onFinish() {
										if (mMap.getCameraPosition().zoom > mZoom)
											mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom));
									}
									
								});
							}
							lv.setAdapter(adapter);
							lv.setOnItemClickListener(new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									mMap.setOnCameraChangeListener(null);
									System.out.println("ZOOM = " + mMap.getCameraPosition().zoom);
									removeMarkers();
									ApplicationController AC = (ApplicationController)getApplicationContext();
									
									((LinearLayout)findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
							    	((LinearLayout)findViewById(R.id.view_search)).setVisibility(View.GONE);
									//((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
							    	String text = ((TextView)view.findViewById(R.id.name)).getText().toString();
							    	text = text.substring(text.indexOf(" ") + 1);
									((EditText)findViewById(R.id.search_bar)).setText(text);
							    	ListView lv = ((ListView)findViewById(R.id.list_view));
							    	List<Spot> listSpots = new ArrayList<Spot>();
							    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
							    	lv.setAdapter(adapter);
//							    	ImageView remove = (ImageView) findViewById(R.id.remove_button);
//							    	remove.setVisibility(View.VISIBLE);
							    	mHasChanged = true;
							    	try {
										mSelectedObject = mArray.getJSONObject(position);
										mModel.getDives().get(mIndex).setSpot(mSelectedObject);
										if (mMyMarker != null)
											mMyMarker.remove();
										Integer zoom = AC.getModel().getDives().get(mIndex).getSpot().getZoom();
										if (zoom == null || zoom > mZoom)
											zoom = mZoom;
										Spot spot = mModel.getDives().get(mIndex).getSpot();
										mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(spot.getLat(), spot.getLng()), zoom));
										mMyMarker = mMap.addMarker(new MarkerOptions()
										.position(new LatLng(spot.getLat(), spot.getLng()))
										.title(spot.getName())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
										
										((TextView)findViewById(R.id.details_name)).setTypeface(mFaceB);
										((TextView)findViewById(R.id.details_name_content)).setTypeface(mFaceR);
										((TextView)findViewById(R.id.details_gps)).setTypeface(mFaceB);
										((TextView)findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
										((TextView)findViewById(R.id.details_name_content)).setText(AC.getModel().getDives().get(mIndex).getSpot().getName());
										((TextView)findViewById(R.id.details_gps_content)).setText(getPosition());
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					((TextView)findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onCancelled() {
			mSpotsTask = null;
		}
	}
    
    public class SpotAdapter extends BaseAdapter
    {
    	LayoutInflater	mLayoutInflater;
    	List<Spot>		mSpotsList;
    	
    	public SpotAdapter(Context context, List<Spot> spotsList)
    	{
    		mLayoutInflater = LayoutInflater.from(context);
    		this.mSpotsList = spotsList;
    	}
    	
		@Override
		public int getCount() {
			return mSpotsList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSpotsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.item_spots, null);

				//holder.id = (TextView)convertView.findViewById(R.id.id);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				holder.location_country = (TextView)convertView.findViewById(R.id.location_country);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			//holder.id.setText(Integer.toString(mSpotsList.get(position).getId()));
			holder.name.setText((position + 1) + ": " + mSpotsList.get(position).getName());
			holder.name.setTypeface(mFaceR);
			holder.location_country.setText(mSpotsList.get(position).getLocationName() + ", " + mSpotsList.get(position).getCountryName());
			holder.location_country.setTypeface(mFaceR);
			return convertView;
		}
		
		private class ViewHolder {
			TextView id;
			TextView name;
			TextView location_country;
		}
    	
    }

	@Override
	public void onConfirmEditComplete(DialogFragment dialog) {
		clearEditList();
	}
	
	@Override
	protected void onDestroy()
	{
		if (mLocationManager != null && mLocationListener != null)
		{
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
		}
		super.onDestroy();
	}
	
	@Override
    protected void onPause() {
        // Save the current setting for updates
		if (mLocationManager != null && mLocationListener != null)
		{
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
		}
        super.onPause();
    }
}
