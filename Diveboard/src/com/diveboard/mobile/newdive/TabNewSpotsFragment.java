package com.diveboard.mobile.newdive;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
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

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class TabNewSpotsFragment extends Fragment implements
		OnMapLongClickListener, OnMarkerDragListener {
	private Typeface mFaceR;
	private Typeface mFaceB;
	private int mIndex;
	private JSONObject mSelectedObject = null;
	private JSONArray mArray;
	private JSONArray mRegionsArray;
	private JSONArray mLocationsArray;
	private JSONArray mCountriesArray;
	private boolean mError = false;
	private GoogleMap mMap;
	private List<Marker> mListMarkers = new ArrayList<Marker>();
	private Marker mMyMarker;
	private Marker mManualMarker = null;
	LocationManager mLocationManager;
	myLocationListener mLocationListener;
	Double mLatitude = 0.0;
	Double mLongitude = 0.0;
	public final int mZoom = 12;
	private Boolean mHasChanged = false;
	private SpotsTask mSpotsTask;
	private RegionLocationTask mRegionLocationTask;
	private ViewGroup rootView;
	private boolean manualSpotActivated;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstance) {
		rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_spots,
				container, false);
		ApplicationController AC = (ApplicationController) getActivity()
				.getApplicationContext();

		
		mFaceR = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Quicksand-Regular.otf");
		mFaceB = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Quicksand-Bold.otf");
		mIndex = getActivity().getIntent().getIntExtra("index", 0);
		
		manualSpotActivated = false;
		
		WindowManager wm = (WindowManager)AC.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int width = size.x;
		final int height = size.y;
		
		

		((TextView) rootView.findViewById(R.id.no_spot)).setTypeface(mFaceR);
		if (((ApplicationController) getActivity().getApplicationContext())
				.getTempDive().getSpot().getId() != 1) {
			((EditText) rootView.findViewById(R.id.search_bar))
					.setText(((ApplicationController) getActivity()
							.getApplicationContext()).getTempDive().getSpot()
							.getName());
		}
		((TextView) rootView.findViewById(R.id.search_bar)).setTypeface(mFaceR);

		((Button) rootView.findViewById(R.id.goToSearch))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//If I come from set manual Spot i need to reset the listView layout
						if(manualSpotActivated){
							manualSpotActivated = false;
							if(mManualMarker != null)	
								mManualMarker.remove();
							((ListView) rootView.findViewById(R.id.list_view)).setVisibility(View.VISIBLE);
							
							
						}
						goToSearch(rootView);
					}
				});
		((Button) rootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
		((ImageView) rootView.findViewById(R.id.GPSImage))
				.setOnClickListener(new OnClickListener() {
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
					doMySearch("search", ((TextView) rootView
							.findViewById(R.id.search_bar)).getText()
							.toString(), null, null, null);
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, 0);
					handled = true;
					
					
					
				}
				return handled;
			}
		});

		ImageView setManualSpot = (ImageView) rootView.findViewById(R.id.setManualSpot);

		setManualSpot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				manualSpotActivated = true;
				removeMarkers();
				mMap.setOnCameraChangeListener(null);
				((ListView) rootView.findViewById(R.id.list_view)).setVisibility(View.GONE);
				goToSetManualSpot(rootView);
			}
		});
		
		Spinner mCountries= (Spinner)rootView.findViewById(R.id.details_country_content); 
		Spinner mRegions= (Spinner)rootView.findViewById(R.id.details_region_content);
		Spinner mLocations= (Spinner)rootView.findViewById(R.id.details_location_content); 
		
		
		//mRegions.setBackgroundColor(getResources().getColor(R.color.yellow));
		
		ArrayAdapter<CharSequence> countriesAdapter = ArrayAdapter.createFromResource((ApplicationController) getActivity().getApplicationContext(), R.array.countries, R.layout.custom_spinner_layout);
		mCountries.setAdapter(countriesAdapter);
//		mRegions.setAdapter(countriesAdapter);
//		mLocations.setAdapter(countriesAdapter);
		
		
		

		if (mMap == null) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			Fragment fragment = fm.findFragmentById(R.id.mapfragmentspot);
			LinearLayout mapLayout = (LinearLayout)rootView.findViewById(R.id.mapLayout); 
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mapLayout.getLayoutParams(); 
			
			int heightAdjusted = 0;
			System.out.println("Dimensions are " + height + "x" + width);
			//If Portrait mode Map 40% of the screen height 
			if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
				heightAdjusted = (int) Math.round(height * 0.38); 
			}
			//Else --> Landscape mode
			else{
				heightAdjusted = (int) Math.round(height * 0.3);
			}
			mapLayout.setMinimumHeight(heightAdjusted);
			lp.height = heightAdjusted;
			
			SupportMapFragment support = (SupportMapFragment) fragment;

			mMap = support.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap == null) {
				System.out.println("Map non safe");
				
			// The Map is verified. It is now safe to manipulate the map
			} else {
				System.out.println("Map safe");
				mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(true);
				mMap.getUiSettings().setZoomGesturesEnabled(true);

				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);
				
				//Spot ID is 1 when there is no spot assigned yet
				if (((ApplicationController) getActivity()
						.getApplicationContext()).getTempDive().getSpot()
						.getId() != 1) {
					
					//Then we show the "selected spot" controls
					((LinearLayout) rootView.findViewById(R.id.view_details))
							.setVisibility(View.VISIBLE);
					((TextView) rootView.findViewById(R.id.details_name))
							.setTypeface(mFaceB);
					((TextView) rootView
							.findViewById(R.id.details_name_content))
							.setTypeface(mFaceR);
					((TextView) rootView.findViewById(R.id.details_gps))
							.setTypeface(mFaceB);
					((TextView) rootView.findViewById(R.id.details_gps_content))
							.setTypeface(mFaceR);
					((TextView) rootView
							.findViewById(R.id.details_name_content))
							.setText(((ApplicationController) getActivity()
									.getApplicationContext()).getTempDive()
									.getSpot().getName());
					((TextView) rootView.findViewById(R.id.details_gps_content))
							.setText(getPosition());
					((Button) rootView.findViewById(R.id.goToSearch))
							.setTypeface(mFaceB);

					mMyMarker = mMap.addMarker(new MarkerOptions()
									.position(new LatLng(
											((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLat(),
											((ApplicationController)getActivity().getApplicationContext()).getTempDive().getSpot().getLng()))
									.title(((ApplicationController) getActivity().getApplicationContext()).getTempDive().getSpot().getName())
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
					System.out.println(((ApplicationController) getActivity()
							.getApplicationContext()).getTempDive().getSpot()
							.getId());
					Integer zoom = ((ApplicationController) getActivity()
							.getApplicationContext()).getTempDive().getSpot()
							.getZoom();
					if (zoom == null || zoom > mZoom)
						zoom = mZoom;
					if (((ApplicationController) getActivity()
							.getApplicationContext()).getTempDive().getSpot()
							.getLat() != null
							&& ((ApplicationController) getActivity()
									.getApplicationContext()).getTempDive()
									.getSpot().getLng() != null)
						mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(
										((ApplicationController) getActivity()
												.getApplicationContext())
												.getTempDive().getSpot()
												.getLat(),
										((ApplicationController) getActivity()
												.getApplicationContext())
												.getTempDive().getSpot()
												.getLng()), zoom));

				} else if(!manualSpotActivated){
					System.out.println("activeGPS");
					((LinearLayout) rootView.findViewById(R.id.view_search))
							.setVisibility(View.VISIBLE);
					activeGPS(null);
				}
				if (manualSpotActivated){
					//Then we show the manual spot controls
					

					
					
					System.out.println("Manual Spot mode on");
				}
			}
			
			mMap.setOnMapLongClickListener(this);
			mMap.setOnMarkerDragListener(this);

			
		  
		}

		return rootView;
	}
	
	

	public String getPosition() {
		String pos = "";
		if (((ApplicationController) getActivity().getApplicationContext())
				.getTempDive().getSpot().getLat() == null) {
			pos += "0° ";
			pos += "N";
		} else if (((ApplicationController) getActivity()
				.getApplicationContext()).getTempDive().getSpot().getLat() >= 0) {
			pos += String.valueOf(((ApplicationController) getActivity()
					.getApplicationContext()).getTempDive().getSpot().getLat())
					+ "° ";
			pos += "N";
		} else if (((ApplicationController) getActivity()
				.getApplicationContext()).getTempDive().getSpot().getLat() < 0) {
			pos += String.valueOf(((ApplicationController) getActivity()
					.getApplicationContext()).getTempDive().getSpot().getLat()
					* (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (((ApplicationController) getActivity().getApplicationContext())
				.getTempDive().getSpot().getLng() == null) {
			pos += "0° ";
			pos += "E";
		} else if (((ApplicationController) getActivity()
				.getApplicationContext()).getTempDive().getSpot().getLng() >= 0) {
			pos += String.valueOf(((ApplicationController) getActivity()
					.getApplicationContext()).getTempDive().getSpot().getLng())
					+ "° ";
			pos += "E";
		} else if (((ApplicationController) getActivity()
				.getApplicationContext()).getTempDive().getSpot().getLng() < 0) {
			pos += String.valueOf(((ApplicationController) getActivity()
					.getApplicationContext()).getTempDive().getSpot().getLng()
					* (-1)) + "° ";
			pos += "W";
		}
		if ((((ApplicationController) getActivity().getApplicationContext())
				.getTempDive().getSpot().getLat() == null || ((ApplicationController) getActivity()
				.getApplicationContext()).getTempDive().getSpot().getLat() == 0)
				&& (((ApplicationController) getActivity()
						.getApplicationContext()).getTempDive().getSpot()
						.getLng() == null || ((ApplicationController) getActivity()
						.getApplicationContext()).getTempDive().getSpot()
						.getLng() == 0))
			pos = "";
		return (pos);
	}
	
	public String getCoordinatesDegrees(LatLng position){
		//returns the position formatted on degrees and with 5 decimals
		
		String mCoordinates ="";
		if(position.latitude > 0.0)
			mCoordinates += String.valueOf(roundToN(position.latitude, 5)) + "°N, ";
		else
			mCoordinates += String.valueOf(roundToN((position.latitude * -1), 5)) + "°S, ";
		
		if(mManualMarker.getPosition().longitude > 0.0)
			mCoordinates += String.valueOf(roundToN(position.longitude, 5)) + "°E";
		else
			mCoordinates += String.valueOf(roundToN((position.longitude * -1), 5)) + "°W";
		
		return mCoordinates;
	}

	public void goToSearch(View view) {
		
		if(manualSpotActivated){
			
			manualSpotActivated = false;
			
			if(mManualMarker != null)	
				mManualMarker.remove();
			mMap.clear();
	
			((RelativeLayout) rootView.findViewById(R.id.new_spot_name_layout))
					.setVisibility(View.GONE);
			((RelativeLayout) rootView.findViewById(R.id.country_layout))
					.setVisibility(View.GONE);
			((RelativeLayout) rootView.findViewById(R.id.region_layout))
					.setVisibility(View.GONE);
			((RelativeLayout) rootView.findViewById(R.id.location_layout))
					.setVisibility(View.GONE);
			((RelativeLayout) rootView.findViewById(R.id.gps_layout))
					.setVisibility(View.VISIBLE);
			
			
		}

		((RelativeLayout) rootView.findViewById(R.id.details_name_layout))
				.setVisibility(View.VISIBLE);
		((LinearLayout) rootView.findViewById(R.id.view_details))
				.setVisibility(View.GONE);
		((LinearLayout) rootView.findViewById(R.id.view_search))
				.setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		((EditText) rootView.findViewById(R.id.search_bar)).requestFocusFromTouch();
		((EditText) rootView.findViewById(R.id.search_bar)).setSelection(((EditText) rootView.findViewById(R.id.search_bar)).length());
		
		activeGPS(null);
	}
	
	public void goToSetManualSpot(View view){
		
		Toast toast = Toast.makeText(getActivity()
				.getApplicationContext(), getResources().getString(R.string.new_spot_marker_toast),
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		
		//Remove all the previous markers
		if (mMyMarker != null)
			mMyMarker.remove();
		
		
		//We load the necessary layouts for setting a manual marker
		((LinearLayout) rootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
		
		((LinearLayout) rootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
		
		((RelativeLayout) rootView.findViewById(R.id.details_name_layout))
				.setVisibility(View.GONE);
		((RelativeLayout) rootView.findViewById(R.id.gps_layout))
				.setVisibility(View.GONE);
		
		((RelativeLayout) rootView.findViewById(R.id.new_spot_name_layout))
				.setVisibility(View.VISIBLE);
		((RelativeLayout) rootView.findViewById(R.id.country_layout))
				.setVisibility(View.VISIBLE);
		((RelativeLayout) rootView.findViewById(R.id.region_layout))
				.setVisibility(View.VISIBLE);
		((RelativeLayout) rootView.findViewById(R.id.location_layout))
				.setVisibility(View.VISIBLE);
		
		((TextView) rootView.findViewById(R.id.nameManualSpotTV))
				.setTypeface(mFaceB);
		((EditText) rootView.findViewById(R.id.nameManualSpotET))
				.setTypeface(mFaceR);
		
		((TextView) rootView.findViewById(R.id.details_country))
				.setTypeface(mFaceB);
		
		((TextView) rootView.findViewById(R.id.details_region))
				.setTypeface(mFaceB);
		
		((TextView) rootView.findViewById(R.id.details_location))
		.setTypeface(mFaceB);
		
		
		
		
		
		
		mManualMarker = mMap.addMarker(new MarkerOptions() 
		.position(mMap.getCameraPosition().target)
		.title("1: My spot")
		.draggable(true)
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.diveboard_marker)));
		
		TextView gpsCoordinates = (TextView) rootView.findViewById(R.id.details_gps_content);
		gpsCoordinates.setTypeface(mFaceR);
		
		gpsCoordinates.setText(getCoordinatesDegrees(mManualMarker.getPosition()));
		
		
		
	}

	public void activeGPS(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText) rootView
				.findViewById(R.id.search_bar)).getWindowToken(), 0);
		((EditText) rootView.findViewById(R.id.search_bar)).setText("");
		// removeMarkers();
		if (mLocationManager != null && mLocationListener != null) {
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager = null;
			mLocationListener = null;
		}
		mLocationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		// Define a listener that responds to location updates
		mLocationListener = new myLocationListener();
		// Register the listener with the Location Manager to receive location
		// updates
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
		
		Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if (lastKnownLocation == null)
			lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		if (lastKnownLocation != null) {
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(),	lastKnownLocation.getLongitude()), mZoom);
			mMap.animateCamera(update, new CancelableCallback() {

				@Override
				public void onFinish() {
					LatLngBounds bounds = mMap.getProjection()
							.getVisibleRegion().latLngBounds;
					System.out.println(Double
							.toString(bounds.southwest.latitude)
							+ " "
							+ Double.toString(bounds.northeast.latitude)
							+ " "
							+ Double.toString(bounds.southwest.longitude)
							+ " "
							+ Double.toString(bounds.northeast.longitude));
					doMySearch("swipe", null, mLatitude.toString(),
							mLongitude.toString(), bounds);
					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

						@Override
						public void onCameraChange(CameraPosition pos) {
							LatLngBounds bounds = mMap.getProjection()
									.getVisibleRegion().latLngBounds;
							System.out.println(Double
									.toString(bounds.southwest.latitude)
									+ " "
									+ Double.toString(bounds.northeast.latitude)
									+ " "
									+ Double.toString(bounds.southwest.longitude)
									+ " "
									+ Double.toString(bounds.northeast.longitude));
							mLatitude = mMap.getCameraPosition().target.latitude;
							mLongitude = mMap.getCameraPosition().target.longitude;
							doMySearch("swipe", null, mLatitude.toString(),
									mLongitude.toString(), bounds);
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

	public void setManualMarker(View view, LatLng mMarker) {

		// Vibrate for 500 milliseconds
				ApplicationController AC = (ApplicationController) getActivity().getApplicationContext();
				Vibrator v = (Vibrator) AC.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(500);
				
				//Remove the previous one and set the new one 
				if(mManualMarker != null)	
					mManualMarker.remove();
				
				mManualMarker = mMap.addMarker(new MarkerOptions()
						.position(mMarker)
						.title("1: My spot")
						.draggable(true)
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.diveboard_marker)));
				
				TextView gpsCoordinates = (TextView) rootView.findViewById(R.id.details_gps_content);
				gpsCoordinates.setTypeface(mFaceR);
				
				gpsCoordinates.setText(getCoordinatesDegrees(mManualMarker.getPosition()));
				
				RegionLocationTask regionLocation_task = new RegionLocationTask();
				regionLocation_task.execute(String.valueOf(mMarker.latitude), String.valueOf(mMarker.longitude));
				
				
				System.out.println("New marker whose latitude is " + mManualMarker.getPosition().latitude + " and the longitude is " + mManualMarker.getPosition().longitude);
	}


	public void doMySearch(String swipe, String text, String latitude,
			String longitude, LatLngBounds bounds) {
		ListView lv = ((ListView) rootView.findViewById(R.id.list_view));
		List<Spot> listSpots = new ArrayList<Spot>();
		SpotAdapter adapter = new SpotAdapter(getActivity()
				.getApplicationContext(), listSpots);
		lv.setAdapter(adapter);
		((TextView) rootView.findViewById(R.id.no_spot))
				.setVisibility(View.GONE);
		SpotsTask spots_task = new SpotsTask();
		if (bounds != null)
			spots_task.execute(swipe, text, latitude, longitude,
					Double.toString(bounds.southwest.latitude),
					Double.toString(bounds.northeast.latitude),
					Double.toString(bounds.southwest.longitude),
					Double.toString(bounds.northeast.longitude));
		else
			spots_task.execute(swipe, text, latitude, longitude, null, null,
					null, null);
		removeMarkers();
		if (mMyMarker != null)
			mMyMarker.remove();
		// ((TextView)findViewById(R.id.search_bar)).setText("");
	}

	public void removeMarkers() {
		for (int i = 0; i < mListMarkers.size(); i++) {
			mListMarkers.get(i).remove();
		}
		mListMarkers.clear();
	}
	
	private class RegionLocationTask extends AsyncTask<String, Void, JSONObject>{
		private JSONObject result;
		private boolean searchDone = false;
		
		
		private class SearchTimer extends Thread {
			private String[] query;

			public SearchTimer(String... query) {
				this.query = query;
			}

			@Override
			public void run() {
				ApplicationController AC = (ApplicationController) getActivity()
						.getApplicationContext();
				System.out.println("Region-location API call" + " " + query[0] + " " + query[1]);
				result = AC.getModel().searchRegionLocationText(query[0], query[1]);
				System.out.println(result);
				searchDone = true;
			}
		}
		
		protected JSONObject doInBackground(String... query) {
			
			SearchTimer task = new SearchTimer(query[0], query[1]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false) {
					DiveboardModel._searchtimedout = true;
					ApplicationController AC = (ApplicationController) getActivity()
							.getApplicationContext();
					return null;
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(JSONObject result) {
			if (DiveboardModel._searchtimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Spot Search Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Connection Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			} else if (DiveboardModel._sotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Socket Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			
			try{
				if (result != null && result.getBoolean("success") == true) {
				
					mLocationsArray = result.getJSONArray("locations");
					mRegionsArray = result.getJSONArray("regions");
					mCountriesArray = result.getJSONArray("countries");
				
				}
				
				
				//End of the TEMP CODE
				
			}catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Override
		protected void onCancelled() {
			mSpotsTask = null;
		}
	}

	
	
	
	
	private class SpotsTask extends AsyncTask<String, Void, JSONObject> {
		private JSONObject result;
		private boolean searchDone = false;
		private String swipe;

		private class SearchTimer extends Thread {
			private String[] query;

			public SearchTimer(String... query) {
				this.query = query;
			}

			@Override
			public void run() {
				ApplicationController AC = (ApplicationController) getActivity()
						.getApplicationContext();
				System.out.println(query[0] + " " + query[1] + " " + query[2]
						+ " " + query[3] + " " + query[4] + " " + query[5]
						+ " " + query[6]);
				result = AC.getModel().searchSpotText(query[0], query[1],
						query[2], query[3], query[4], query[5], query[6]);
				System.out.println(result);
				searchDone = true;
			}
		}

		protected JSONObject doInBackground(String... query) {
			swipe = query[0];
			SearchTimer task = new SearchTimer(query[1], query[2], query[3],
					query[4], query[5], query[6], query[7]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false) {
					DiveboardModel._searchtimedout = true;
					ApplicationController AC = (ApplicationController) getActivity()
							.getApplicationContext();
					return AC.getModel().offlineSearchSpotText(query[1],
							query[2], query[3], query[4], query[5], query[6],
							query[7]);
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
			// ApplicationController AC =
			// (ApplicationController)getApplicationContext();
			//
			// return AC.getModel().searchSpotText(query[0], null, null, null,
			// null, null, null);
		}

		protected void onPostExecute(JSONObject result) {
			if (DiveboardModel._searchtimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Spot Search Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Connection Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			} else if (DiveboardModel._sotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(getActivity()
							.getApplicationContext(), "Socket Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			((ProgressBar) rootView.findViewById(R.id.progressBar))
					.setVisibility(View.GONE);
			try {
				if (result != null && result.getBoolean("success") == true) {
					try {
						mArray = result.getJSONArray("spots");
						final ListView lv = ((ListView) rootView
								.findViewById(R.id.list_view));
						final List<Spot> listSpots = new ArrayList<Spot>();
						for (int i = 0; i < mArray.length(); i++) {
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
									if (!marker.equals(mMyMarker)) {
										int id = Integer.valueOf(marker
												.getTitle().substring(0, marker.getTitle().indexOf(":")));
										System.out.println(id);
										lv.smoothScrollToPosition(id);
									}
									marker.showInfoWindow();
									return true;
								}
							});
							System.out.println(result.toString());
							SpotAdapter adapter = new SpotAdapter(getActivity()
									.getApplicationContext(), listSpots);
							// Zoom out to show markers
							if (swipe.contentEquals("search")) {
								LatLngBounds.Builder builder = new LatLngBounds.Builder();
								for (Marker marker : mListMarkers) {
									builder.include(marker.getPosition());
								}
								LatLngBounds bounds = builder.build();
								int padding = 100; // offset from edges of the
													// map in pixels
								CameraUpdate cu = CameraUpdateFactory
										.newLatLngBounds(bounds, padding);
								mMap.animateCamera(cu,
										new CancelableCallback() {

											@Override
											public void onCancel() {
												// TODO Auto-generated method
												// stub

											}

											@Override
											public void onFinish() {
												if (mMap.getCameraPosition().zoom > mZoom)
													mMap.animateCamera(CameraUpdateFactory
															.zoomTo(mZoom));
											}

										});
							}
							lv.setAdapter(adapter);
							lv.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									mMap.setOnCameraChangeListener(null);
									removeMarkers();

									((Button) rootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
									((LinearLayout) rootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
									((LinearLayout) rootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
									((RelativeLayout)rootView.findViewById(R.id.new_spot_name_layout)).setVisibility(View.GONE);
									((RelativeLayout)rootView.findViewById(R.id.country_layout)).setVisibility(View.GONE);
									((RelativeLayout)rootView.findViewById(R.id.region_layout)).setVisibility(View.GONE);
									String text = ((TextView) view.findViewById(R.id.name)).getText().toString();
									text = text.substring(text.indexOf(" ") + 1);
									((EditText) rootView
											.findViewById(R.id.search_bar))
											.setText(text);
									// ((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
									ListView lv = ((ListView) rootView
											.findViewById(R.id.list_view));
									List<Spot> listSpots = new ArrayList<Spot>();
									SpotAdapter adapter = new SpotAdapter(
											getActivity()
													.getApplicationContext(),
											listSpots);
									lv.setAdapter(adapter);
									
									mHasChanged = true;
									try {
										//It sets here the selected spot to the tempDive Model
										mSelectedObject = mArray.getJSONObject(position);
										((ApplicationController) getActivity().getApplicationContext()).getTempDive().setSpot(mSelectedObject);
										
										System.out.println(((ApplicationController) getActivity().getApplicationContext()).getTempDive().getSpot().toString());
										if (mMyMarker != null)
											mMyMarker.remove();
										Integer zoom = ((ApplicationController) getActivity()
												.getApplicationContext())
												.getTempDive().getSpot()
												.getZoom();
										if (zoom == null || zoom > mZoom)
											zoom = mZoom;
										Spot spot = ((ApplicationController) getActivity().getApplicationContext()).getTempDive().getSpot();
										mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(spot.getLat(), spot.getLng()),	zoom));
										mMyMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLat(),spot.getLng())).title(spot.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

										((TextView) rootView.findViewById(R.id.details_name)).setTypeface(mFaceB);
										((TextView) rootView.findViewById(R.id.details_name_content)).setTypeface(mFaceR);
										((TextView) rootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
										((TextView) rootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
										((TextView) rootView.findViewById(R.id.details_name_content)).setText(((ApplicationController) getActivity().getApplicationContext()).getTempDive().getSpot().getName());
										((TextView) rootView.findViewById(R.id.details_gps_content)).setText(getPosition());
										
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
				} else {
					((TextView) rootView.findViewById(R.id.no_spot))
							.setVisibility(View.VISIBLE);
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

	public class SpotAdapter extends BaseAdapter {
		LayoutInflater mLayoutInflater;
		List<Spot> mSpotsList;

		public SpotAdapter(Context context, List<Spot> spotsList) {
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

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater
						.inflate(R.layout.item_spots, null);

				// holder.id = (TextView)convertView.findViewById(R.id.id);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.location_country = (TextView) convertView
						.findViewById(R.id.location_country);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// holder.id.setText(Integer.toString(mSpotsList.get(position).getId()));
			holder.name.setText((position + 1) + ": "
					+ mSpotsList.get(position).getName());
			holder.name.setTypeface(mFaceR);
			holder.location_country.setText(mSpotsList.get(position)
					.getLocationName()
					+ ", "
					+ mSpotsList.get(position).getCountryName());
			holder.location_country.setTypeface(mFaceR);
			holder.name.setTextColor(getActivity().getResources().getColor(
					R.color.dark_grey));
			holder.location_country.setTextColor(getActivity().getResources()
					.getColor(R.color.dark_grey));
			return convertView;
		}

		private class ViewHolder {	
			TextView id;
			TextView name;
			TextView location_country;
		}

	}
	
	public double roundToN(double number, int n){
		
		//Round number to n decimals
		double mRounded;
		int grade = 1;
		
		if(n < 1){
			System.err.println("Number could not be rounded properly, provide a different number of decimals");
			return 0.0;
		}
		grade = (int) Math.pow(10.0, n);
		mRounded = Math.round(number * grade);
		mRounded = mRounded / grade;
		
		return mRounded;
	}

	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub
		TextView gpsCoordinates = (TextView) rootView.findViewById(R.id.details_gps_content);
		gpsCoordinates.setTypeface(mFaceR);
		
		gpsCoordinates.setText(getCoordinatesDegrees(arg0.getPosition()));
	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		
		if(manualSpotActivated)
			setManualMarker(rootView, point);
				
		System.out.println("long pressed, point=" + point);
		
	}
}
