package com.diveboard.mobile.newdive;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Spot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class					TabNewSpotsFragment extends Fragment
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private int							mIndex;
	private JSONObject					mSelectedObject = null;
	private JSONArray					mArray;
	private boolean						mError = false;
	private GoogleMap					mMap;
	private List<Marker>				mListMarkers = new ArrayList<Marker>();
	private Marker						mMyMarker;
	LocationManager 					mLocationManager;
	myLocationListener 					mLocationListener;
	Double 								mLatitude = 0.0;
	Double 								mLongitude = 0.0;
	public final int					mZoom = 12;
	private Boolean						mHasChanged = false;
	private SpotsTask 					mSpotsTask;
	private ViewGroup					rootView;

	private class myLocationListener implements LocationListener
	{
		public void onLocationChanged(Location location)
		{
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
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		if (mLocationManager != null && mLocationListener != null)
		{
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		System.out.println("destroy");
		if (mLocationManager != null && mLocationListener != null)
		{
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_spots, container, false);
		ApplicationController AC = (ApplicationController) getActivity().getApplicationContext();

		mFaceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
		mFaceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
		mIndex = getActivity().getIntent().getIntExtra("index", 0);

		((TextView)rootView.findViewById(R.id.no_spot)).setTypeface(mFaceR);
		if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getId() != 1)
		{
			//((TextView)findViewById(R.id.current_spot)).setText(mModel.getDives().get(mIndex).getSpot().getName());
			((EditText)rootView.findViewById(R.id.search_bar)).setText(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName());
		}
		//	     if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getId() != 1)
		//	    	 ((TextView)findViewById(R.id.current_spot)).setText(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName());
		((TextView)rootView.findViewById(R.id.search_bar)).setTypeface(mFaceR);

		((Button)rootView.findViewById(R.id.goToSearch)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToSearch(rootView);
			}
		});
		((Button)rootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
		((ImageView)rootView.findViewById(R.id.GPSImage)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activeGPS(rootView);
			}
		});

		EditText editText = (EditText) rootView.findViewById(R.id.search_bar);
		editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mMap.setOnCameraChangeListener(null);
					doMySearch("search", ((TextView)rootView.findViewById(R.id.search_bar)).getText().toString(), null, null, null);
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, 0);
					handled = true;
				}
				return handled;
			}
		});


		if (mMap == null) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			Fragment fragment = fm.findFragmentById(R.id.mapfragmentspot);
			SupportMapFragment support = (SupportMapFragment)fragment;
			mMap = support.getMap();
			//mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			// Check if we were successful in obtaining the map.
			if (mMap == null) {
				System.out.println("Map non safe");
				// The Map is verified. It is now safe to manipulate the map
			}
			else
			{
				System.out.println("Map non safe");
				mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(true);
				mMap.getUiSettings().setZoomGesturesEnabled(true);

				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);
				if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getId() != 1)
				{
					((LinearLayout)rootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
					((TextView)rootView.findViewById(R.id.details_name)).setTypeface(mFaceB);
					((TextView)rootView.findViewById(R.id.details_name_content)).setTypeface(mFaceR);
					((TextView)rootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
					((TextView)rootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
					((TextView)rootView.findViewById(R.id.details_name_content)).setText(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName());
					((TextView)rootView.findViewById(R.id.details_gps_content)).setText(getPosition());
					((Button)rootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);

					mMyMarker = mMap.addMarker(new MarkerOptions()
					.position(new LatLng(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat(), ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng()))
					.title(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
					System.out.println(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getId());
					Integer zoom = ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getZoom();
					if (zoom == null || zoom > mZoom)
						zoom = mZoom;
					if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() != null && ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() != null)
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat(), ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng()), zoom));
				}
				else
				{
					System.out.println("activeGPS");
					((LinearLayout)rootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
					activeGPS(null);
					//				mMyMarker = mMap.addMarker(new MarkerOptions()
					//				.position(new LatLng(0, 0))
					//				.title(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName())
					//				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
					//				((LinearLayout)findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
				}
			}

		}
		return rootView;
	}

	public String getPosition()
	{
		String pos = "";
		if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() == null)
		{
			pos += "0° ";
			pos += "N";
		}
		else if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() >= 0)
		{
			pos += String.valueOf(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat()) + "° ";
			pos += "N";
		}
		else if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() < 0)
		{
			pos += String.valueOf(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() * (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() == null)
		{
			pos += "0° ";
			pos += "E";
		}
		else if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() >= 0)
		{
			pos += String.valueOf(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng()) + "° ";
			pos += "E";
		}
		else if (((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() < 0)
		{
			pos += String.valueOf(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() * (-1)) + "° ";
			pos += "W";
		}
		if ((((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() == null ||((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat() == 0) && 
				(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() == null || ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng() == 0))
			pos = "";
		return (pos);
	}

	public void goToSearch(View view)
	{
		((LinearLayout)rootView.findViewById(R.id.view_details)).setVisibility(View.GONE);
		((LinearLayout)rootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		((EditText) rootView.findViewById(R.id.search_bar)).requestFocusFromTouch();
		((EditText) rootView.findViewById(R.id.search_bar)).setSelection(((EditText) rootView.findViewById(R.id.search_bar)).length());
	}


	public void activeGPS(View view)
	{
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText) rootView.findViewById(R.id.search_bar)).getWindowToken(), 0);
		((EditText) rootView.findViewById(R.id.search_bar)).setText("");
		//removeMarkers();
		if (mLocationManager != null && mLocationListener != null)
		{
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
		}
	}

	//    public void setCurrentSpot(View view)
	//    {  	
	//    	((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
	//    	ListView lv = ((ListView)findViewById(R.id.list_view));
	//    	List<Spot> listSpots = new ArrayList<Spot>();
	//    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
	//    	lv.setAdapter(adapter);
	////    	SpotAdapter adapter = ((SpotAdapter)lv.getAdapter());
	////    	for (int i = 0; i < adapter.getCount(); i++)
	////    	{
	////    		adapter.
	////    	}
	//    }

	public void doMySearch(String swipe, String text, String latitude, String longitude, LatLngBounds bounds)
	{  	
		ListView lv = ((ListView)rootView.findViewById(R.id.list_view));
		List<Spot> listSpots = new ArrayList<Spot>();
		SpotAdapter adapter = new SpotAdapter(getActivity().getApplicationContext(), listSpots);
		lv.setAdapter(adapter);
		((TextView)rootView.findViewById(R.id.no_spot)).setVisibility(View.GONE);
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
				ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
				System.out.println(query[0] + " " + query[1] + " " + query[2] + " " + query[3] + " " + query[4] + " " + query[5] + " " + query[6]);
				result = AC.getModel().searchSpotText(query[0], query[1], query[2], query[3], query[4], query[5], query[6]);
				System.out.println(result);
				searchDone = true;
			}
		}

		protected JSONObject doInBackground(String... query)
		{
			swipe = query[0];
			SearchTimer task = new SearchTimer(query[1], query[2], query[3], query[4], query[5], query[6], query[7]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false)
				{
					DiveboardModel._searchtimedout = true;
					ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
					return AC.getModel().offlineSearchSpotText(query[1], query[2], query[3], query[4], query[5], query[6], query[7]);
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
			//			ApplicationController AC = (ApplicationController)getApplicationContext();
			//			
			//			return AC.getModel().searchSpotText(query[0], null, null, null, null, null, null);
		}

		protected void onPostExecute(JSONObject result)
		{
			if (DiveboardModel._searchtimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Spot Search Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			}
			else if (DiveboardModel._sotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Socket Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			((ProgressBar)rootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
			try {
				if (result != null && result.getBoolean("success") == true)
				{
					try {
						mArray = result.getJSONArray("spots");
						final ListView lv = ((ListView)rootView.findViewById(R.id.list_view));
						final List<Spot> listSpots = new ArrayList<Spot>();
						for (int i = 0; i < mArray.length(); i++)
						{
							Spot spot = new Spot(mArray.getJSONObject(i));
							listSpots.add(spot);
						}
						if (listSpots.size() == 0)
						{
							((TextView)rootView.findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
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
									if (!marker.equals(mMyMarker)){
										int id = Integer.valueOf(marker.getTitle().substring(0, marker.getTitle().indexOf(":")));
										System.out.println(id);
										lv.smoothScrollToPosition(id);
									}
									marker.showInfoWindow();
									return true;
								}
							});
							System.out.println(result.toString());
							SpotAdapter adapter = new SpotAdapter(getActivity().getApplicationContext(), listSpots);
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
									removeMarkers();

									((Button)rootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
									((LinearLayout)rootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
									((LinearLayout)rootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
									String text = ((TextView)view.findViewById(R.id.name)).getText().toString();
									text = text.substring(text.indexOf(" ") + 1);
									((EditText)rootView.findViewById(R.id.search_bar)).setText(text);
									//((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
									ListView lv = ((ListView)rootView.findViewById(R.id.list_view));
									List<Spot> listSpots = new ArrayList<Spot>();
									SpotAdapter adapter = new SpotAdapter(getActivity().getApplicationContext(), listSpots);
									lv.setAdapter(adapter);
									//							    	ImageView remove = (ImageView) findViewById(R.id.remove_button);
									//							    	remove.setVisibility(View.VISIBLE);
									mHasChanged = true;
									try {
										mSelectedObject = mArray.getJSONObject(position);
										((ApplicationController)getActivity().getApplicationContext()).getTempDive().setSpot(mSelectedObject);
										System.out.println(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().toString());
										if (mMyMarker != null)
											mMyMarker.remove();
										Integer zoom = ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getZoom();
										if (zoom == null || zoom > mZoom)
											zoom = mZoom;
										Spot spot = ((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot();
										mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(spot.getLat(), spot.getLng()), zoom));
										mMyMarker = mMap.addMarker(new MarkerOptions()
										.position(new LatLng(spot.getLat(), spot.getLng()))
										.title(spot.getName())
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

										((TextView)rootView.findViewById(R.id.details_name)).setTypeface(mFaceB);
										((TextView)rootView.findViewById(R.id.details_name_content)).setTypeface(mFaceR);
										((TextView)rootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
										((TextView)rootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
										((TextView)rootView.findViewById(R.id.details_name_content)).setText(((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getName());
										((TextView)rootView.findViewById(R.id.details_gps_content)).setText(getPosition());
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
					((TextView)rootView.findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
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
			holder.name.setTextColor(getActivity().getResources().getColor(R.color.dark_grey));
			holder.location_country.setTextColor(getActivity().getResources().getColor(R.color.dark_grey));
			return convertView;
		}

		private class ViewHolder {
			TextView id;
			TextView name;
			TextView location_country;
		}

	}
}
