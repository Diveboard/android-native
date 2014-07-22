package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Spot;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TabEditSpotsFragment extends Fragment implements
		OnMapLongClickListener, OnMarkerDragListener {
	private Typeface mFaceR;
	private Typeface mFaceB;
	private int mIndex;
	private JSONObject mSelectedObject = null;
	private JSONArray mArray;
	private JSONArray mRegionsArray;
	private JSONArray mLocationsArray;
	private JSONArray mCountriesArray;
	private JSONArray mRegionsIdArray;
	private JSONArray mLocationsIdArray;
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
	private ViewGroup mRootView;
	static boolean manualSpotActivated;
	private Spinner mCountrySpinner, mRegionSpinner, mLocationSpinner;
	private String mSpotTitle = "My Spot";
	private Integer mRegionId = null;
	private Integer mLocationId = null;
	private DiveboardModel mModel = null;
	private boolean earthViewActive = false;
	private boolean goOfflineMode;
	private int toastCount = 0;
	private Context mContext;
	private ApplicationController AC;

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
		mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_spots,container, false);
		AC = (ApplicationController) getActivity().getApplicationContext();
		mContext = getActivity().getApplicationContext();
		mModel = AC.getModel();

		try{
			mFaceR = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Lato-Light.ttf");
			mFaceB = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Lato-Regular.ttf");
		}catch (NullPointerException e){
			e.printStackTrace();
			mFaceB = Typeface.DEFAULT_BOLD;
			mFaceR = Typeface.DEFAULT;
		}
		
		
		mIndex = getActivity().getIntent().getIntExtra("index", 0);
		
		((TextView) mRootView.findViewById(R.id.nameSpotTitle)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.nameSelectedSpotTV)).setTypeface(mFaceR);
		((TextView) mRootView.findViewById(R.id.countrySpotTitle)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.countrySelectedTV)).setTypeface(mFaceR);
		((Button) mRootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);

		((TextView) mRootView.findViewById(R.id.nameManualSpotTV)).setTypeface(mFaceB);
		((EditText) mRootView.findViewById(R.id.nameManualSpotET)).setTypeface(mFaceR);
		((TextView) mRootView.findViewById(R.id.details_country)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.details_region)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.details_location)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
		((TextView) mRootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);

		manualSpotActivated = false;

		WindowManager wm = (WindowManager) AC.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		final int width;
		final int height;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
			display.getSize(size);
				width = size.x;
				height = size.y;
		}else
		{
			display = wm.getDefaultDisplay();
			width = display.getWidth();
			height = display.getHeight();
		}
		
		EditDiveActivity.isNewSpot = false;
		goOfflineMode = false;

		((TextView) mRootView.findViewById(R.id.no_spot)).setTypeface(mFaceR);
//		if (mModel.getDives().get(mIndex).getSpot().getId() != 1) {
//			((EditText) mRootView.findViewById(R.id.search_bar)).setText(mModel.getDives().get(mIndex).getSpot().getName());
//		}
		((TextView) mRootView.findViewById(R.id.search_bar)).setTypeface(mFaceR);

		((Button) mRootView.findViewById(R.id.goToSearch)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// If I come from set manual Spot i need to reset the
						// listView layout
						if (manualSpotActivated) {
							manualSpotActivated = false;

							((ListView) mRootView.findViewById(R.id.list_view)).setVisibility(View.VISIBLE);

						}
						goToSearch(mRootView);
					}
				});

		((ImageView) mRootView.findViewById(R.id.GPSImage)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						activeGPS(mRootView);
					}
				});

		OnEditorActionListener searchListener = new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mLatitude = mMap.getCameraPosition().target.latitude;
					mLongitude = mMap.getCameraPosition().target.longitude;
					LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
					String stringToSearch = ((TextView) mRootView.findViewById(R.id.search_bar)).getText().toString();

					if (!stringToSearch.trim().isEmpty() && stringToSearch.trim().length() > 2) {
						doMySearch("search", stringToSearch, null, null, null);
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(0, 0);
						handled = true;
					} else {

						doMySearch("swipe", null, mLatitude.toString(),mLongitude.toString(), bounds);
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(0, 0);
						handled = true;
					}

				}
				return handled;
			}
		};
		EditText editText = (EditText) mRootView.findViewById(R.id.search_bar);
		editText.setOnEditorActionListener(searchListener);

		ImageView setManualSpot = (ImageView) mRootView
				.findViewById(R.id.setManualSpot);

		setManualSpot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				manualSpotActivated = true;
				removeMarkers();
				mMap.setOnCameraChangeListener(null);
				((ListView) mRootView.findViewById(R.id.list_view)).setVisibility(View.GONE);
				goToSetManualSpot(mRootView);
			}
		});

		mCountrySpinner = (Spinner) mRootView.findViewById(R.id.details_country_content);
		mRegionSpinner = (Spinner) mRootView.findViewById(R.id.details_region_content);
		mLocationSpinner = (Spinner) mRootView.findViewById(R.id.details_location_content);

		// We initialize the spinners
		ArrayAdapter<CharSequence> countriesAdapter = ArrayAdapter.createFromResource(mContext, R.array.countries,R.layout.custom_spinner_layout);
		mCountrySpinner.setAdapter(countriesAdapter);
		mRegionSpinner.setAdapter(countriesAdapter);
		mLocationSpinner.setAdapter(countriesAdapter);

		if (mMap == null) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			Fragment fragment = fm.findFragmentById(R.id.mapfragmentspot);
			LinearLayout mapLayout = (LinearLayout) mRootView
					.findViewById(R.id.mapLayout);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mapLayout
					.getLayoutParams();

			int heightAdjusted = 0;
			//System.out.println("Dimensions are " + height + "x" + width);
			// If Portrait mode Map 40% of the screen height
			if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				heightAdjusted = (int) Math.round(height * 0.38);
			}
			// Else --> Landscape mode
			else {
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

				mMap.getUiSettings().setRotateGesturesEnabled(false);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);

				mMap.setOnMapLongClickListener(this);
				mMap.setOnMarkerDragListener(this);
				
				((ImageView) mRootView.findViewById(R.id.ic_map_change)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!earthViewActive){
							((ImageView) mRootView.findViewById(R.id.ic_map_change)).setImageResource(R.drawable.ic_map_view);
							earthViewActive = true;
							System.out.println(String.valueOf(earthViewActive));
							mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						}
						else{
							((ImageView) mRootView.findViewById(R.id.ic_map_change)).setImageResource(R.drawable.ic_earth_view);
							earthViewActive = false;
							System.out.println(String.valueOf(earthViewActive));
							mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
						}				
					}
				});
				
				// //Load the previously selected spot if so, and if it is a new spot whose spotID is null we load it too
				if (mModel.getDives().get(mIndex).getSpot().getId() == null || mModel.getDives().get(mIndex).getSpot().getId() != 1) {
					
					goToSpotSelected(mRootView, mModel.getDives().get(mIndex).getSpot().getJson());

				} else {
					goToSearch(mRootView);
					activeGPS(mRootView);
				}
					//if (!manualSpotActivated) {
//					System.out.println("activeGPS");
//					((LinearLayout) mRootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
//					activeGPS(null);
//				}
//				if (manualSpotActivated) {
//					// Then we show the manual spot controls
//
//					System.out.println("Manual Spot mode on");
//				}
			}

		}

		return mRootView;
	}

	public String getPosition() {
		String pos = "";
		if (mModel.getDives().get(mIndex).getSpot().getLat() == null) {
			pos += "0° ";
			pos += "N";
		} else if (mModel.getDives().get(mIndex).getSpot().getLat() >= 0) {
			pos += String.valueOf(mModel.getDives().get(mIndex).getSpot().getLat()) + "° ";
			pos += "N";
		} else if (mModel.getDives().get(mIndex).getSpot().getLat() < 0) {
			pos += String.valueOf(mModel.getDives().get(mIndex).getSpot().getLat() * (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (mModel.getDives().get(mIndex).getSpot().getLng() == null) {
			pos += "0° ";
			pos += "E";
		} else if (mModel.getDives().get(mIndex).getSpot().getLng() >= 0) {
			pos += String.valueOf(mModel.getDives().get(mIndex).getSpot().getLng()) + "° ";
			pos += "E";
		} else if (mModel.getDives().get(mIndex).getSpot().getLng() < 0) {
			pos += String.valueOf(mModel.getDives().get(mIndex).getSpot().getLng() * (-1)) + "° ";
			pos += "W";
		}
		if ((mModel.getDives().get(mIndex).getSpot().getLat() == null || mModel.getDives().get(mIndex).getSpot().getLat() == 0)
				&& (mModel.getDives().get(mIndex).getSpot().getLng() == null || mModel.getDives().get(mIndex).getSpot().getLng() == 0))
			pos = "";
		return (pos);
	}

	public String getCoordinatesDegrees(LatLng position) {
		// returns the position formatted on degrees and with 5 decimals

		String mCoordinates = "";
		if (position.latitude > 0.0)
			mCoordinates += String.valueOf(roundToN(position.latitude, 5)) + "°N, ";
		else
			mCoordinates += String.valueOf(roundToN((position.latitude * -1), 5)) + "°S, ";

		if (position.longitude > 0.0)
			mCoordinates += String.valueOf(roundToN(position.longitude, 5)) + "°E";
		else
			mCoordinates += String.valueOf(roundToN((position.longitude * -1), 5)) + "°W";

		return mCoordinates;
	}

	public void goToSearch(View view) {

		EditDiveActivity.isNewSpot = false;

		((LinearLayout) mRootView.findViewById(R.id.manual_spot_layout)).setVisibility(View.GONE);
		((LinearLayout) mRootView.findViewById(R.id.on_spot_selected_layout)).setVisibility(View.GONE);
		((LinearLayout) mRootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
		((ListView) mRootView.findViewById(R.id.list_view)).setVisibility(View.VISIBLE);
		removeMarkers();
		if (mManualMarker != null)
			mManualMarker.remove();

		mMap.clear();

		if (manualSpotActivated) {
			manualSpotActivated = false;
		}

//		mLatitude = mMap.getCameraPosition().target.latitude;
//		mLongitude = mMap.getCameraPosition().target.longitude;
//		LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
		//doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(),bounds);
		mMap.setOnCameraChangeListener(null);
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition pos) {
				LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
				System.out.println(Double.toString(bounds.southwest.latitude)
						+ " " + Double.toString(bounds.northeast.latitude)
						+ " " + Double.toString(bounds.southwest.longitude)
						+ " " + Double.toString(bounds.northeast.longitude));
				mLatitude = mMap.getCameraPosition().target.latitude;
				mLongitude = mMap.getCameraPosition().target.longitude;
				String stringToSearch = ((TextView) mRootView.findViewById(R.id.search_bar)).getText().toString();
				if(stringToSearch.trim().isEmpty()){
					doMySearch("swipe", null, mLatitude.toString(),mLongitude.toString(), bounds);
					System.out.println("doMySearch with NO term in goToSearch");
				}
			}
		});
		
		JSONObject no_spot = new JSONObject();
		try {
			no_spot.put("id", 1);
			mModel.getDives().get(mIndex).setSpot(no_spot);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public void goToSetManualSpot(View view) {

		final JSONObject mSpot = new JSONObject();
		EditDiveActivity.isNewSpot = true;

		// We load the necessary layouts for setting a manual marker
		((LinearLayout) mRootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
		((LinearLayout) mRootView.findViewById(R.id.on_spot_selected_layout)).setVisibility(View.GONE);
		((LinearLayout) mRootView.findViewById(R.id.manual_spot_layout)).setVisibility(View.VISIBLE);

		Button cancel = (Button) mRootView.findViewById(R.id.cancel_button);
		Button confirm = (Button) mRootView.findViewById(R.id.confirm_button);

		if(toastCount<1){
			Toast toast = Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.new_spot_marker_toast),Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			toastCount++;
		}
		

		EditText editText = (EditText) mRootView.findViewById(R.id.nameManualSpotET);
		editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_NULL&& event.getAction() == KeyEvent.ACTION_DOWN) {
					mSpotTitle = ((TextView) mRootView.findViewById(R.id.nameManualSpotET)).getText().toString();

					if (!mSpotTitle.isEmpty()) {
						handled = true;
					}
				}
				return handled;
			}
		});

		// we add the custom marker and launch the search of nearby spots on the
		// background
		mManualMarker = mMap.addMarker(new MarkerOptions()
				.position(mMap.getCameraPosition().target)
				.title(((TextView) mRootView.findViewById(R.id.nameManualSpotET)).getText().toString())
				.draggable(true)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
		
		mLatitude = mMap.getCameraPosition().target.latitude;
		mLongitude = mMap.getCameraPosition().target.longitude;
		RegionLocationTask regionLocation_task = new RegionLocationTask();
		regionLocation_task.execute(mLatitude.toString(),mLongitude.toString());
		mMap.setOnCameraChangeListener(null);
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition pos) {
				LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
				System.out.println(Double.toString(bounds.southwest.latitude)
						+ " " + Double.toString(bounds.northeast.latitude)
						+ " " + Double.toString(bounds.southwest.longitude)
						+ " " + Double.toString(bounds.northeast.longitude));
				mLatitude = mMap.getCameraPosition().target.latitude;
				mLongitude = mMap.getCameraPosition().target.longitude;
				doMySearch("swipe", null, mLatitude.toString(),
						mLongitude.toString(), bounds);
			}
		});

		Spinner countrySpinner = (Spinner) mRootView.findViewById(R.id.details_country_content);
		Spinner regionSpinner = (Spinner) mRootView.findViewById(R.id.details_region_content);
		Spinner locationSpinner = (Spinner) mRootView.findViewById(R.id.details_location_content);

		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				try {
					switch (parent.getId()) {

					case R.id.details_country_content:
						mSpot.put("cname", parent.getItemAtPosition(pos)
								.toString());
						break;

					case R.id.details_region_content:
						mSpot.put("region", parent.getItemAtPosition(pos)
								.toString());
						break;

					case R.id.details_location_content:
						mSpot.put("location", parent.getItemAtPosition(pos)
								.toString());
						break;

					default:
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		};

		countrySpinner.setOnItemSelectedListener(spinnerListener);
		regionSpinner.setOnItemSelectedListener(spinnerListener);
		locationSpinner.setOnItemSelectedListener(spinnerListener);
		
		((RelativeLayout) mRootView.findViewById(R.id.new_spot_name_layout)).setVisibility(View.GONE);
		((RelativeLayout) mRootView.findViewById(R.id.country_layout)).setVisibility(View.GONE);
		((RelativeLayout) mRootView.findViewById(R.id.region_layout)).setVisibility(View.GONE);
		((RelativeLayout) mRootView.findViewById(R.id.location_layout)).setVisibility(View.GONE);
		((RelativeLayout) mRootView.findViewById(R.id.buttons_layout)).setVisibility(View.GONE);
		((ProgressBar) mRootView.findViewById(R.id.progressBarManualSpot)).setVisibility(View.VISIBLE);

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				goToSearch(mRootView);
			}
		});

		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = ((TextView) mRootView.findViewById(R.id.nameManualSpotET)).getText().toString();
				name = (name.trim().isEmpty()) ? mSpotTitle : ((TextView) mRootView.findViewById(R.id.nameManualSpotET)).getText().toString();

				if (!mSpot.isNull("location")){
					try {
						System.out.println("1 " + mSpot.get("region").toString());
						System.out.println("2 " + mSpot.get("location").toString());
						
						for (int i = 0; i < mRegionsIdArray.length(); i++) {
							
							if (mRegionsIdArray.getJSONObject(i).getString("region_name").equals(mSpot.get("region"))) {
								mRegionId = mRegionsIdArray.getJSONObject(i).getInt("region_id");
							}
	
						}
						for (int i = 0; i < mLocationsIdArray.length(); i++) {
							
							if (mLocationsIdArray.getJSONObject(i).getString("location_name").equals(mSpot.get("location"))) {
								mLocationId = mLocationsIdArray.getJSONObject(i).getInt("location_id");
	
							}
	
						}
	
						mSpot.put("lat", mManualMarker.getPosition().latitude);
						mSpot.put("lng", mManualMarker.getPosition().longitude);
						mSpot.put("name", name);
//						if(mModel.getDives().get(mIndex).getSpot().getId() != 1)
//							mSpot.put("id", mModel.getDives().get(mIndex).getSpot().getId());
						if (mRegionId != null && mLocationId != null) {
							mSpot.put("region_id", mRegionId);
							mSpot.put("location_id", mLocationId);
						}
						
						// It sets here the selected spot to the Dive Model
						mModel.getDives().get(mIndex).setSpot(mSpot);
						System.out.println("~~~SetSpot with " + mSpot.toString());
						goToSpotSelected(mRootView, mSpot);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					Toast toast = Toast.makeText(mContext, getResources().getString((R.string.no_spots_around)),Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

				
			}
		});

	}

	public void activeGPS(View view) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText) mRootView
				.findViewById(R.id.search_bar)).getWindowToken(), 0);
		((EditText) mRootView.findViewById(R.id.search_bar)).setText("");
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
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, mLocationListener);

		Location lastKnownLocation = mLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation == null)
			lastKnownLocation = mLocationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		if (lastKnownLocation != null) {
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
					new LatLng(lastKnownLocation.getLatitude(),
							lastKnownLocation.getLongitude()), mZoom);
			mMap.animateCamera(update, new CancelableCallback() {

				@Override
				public void onFinish() {
					
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
		ApplicationController AC = (ApplicationController) getActivity()
				.getApplicationContext();
		Vibrator v = (Vibrator) AC.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(500);

		// Remove the previous one and set the new one
		if (mManualMarker != null)
			mManualMarker.remove();

		mManualMarker = mMap.addMarker(new MarkerOptions()
				.position(mMarker)
				.title(mSpotTitle)
				.draggable(true)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

		RegionLocationTask regionLocation_task = new RegionLocationTask();
		regionLocation_task.execute(String.valueOf(mMarker.latitude),String.valueOf(mMarker.longitude));
	}

	public void doMySearch(String swipe, String text, String latitude,
			String longitude, LatLngBounds bounds) {
		ListView lv = ((ListView) mRootView.findViewById(R.id.list_view));
		List<Spot> listSpots = new ArrayList<Spot>();
		SpotAdapter adapter = new SpotAdapter(mContext, listSpots);
		lv.setAdapter(adapter);
		((ProgressBar) mRootView.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
		((TextView) mRootView.findViewById(R.id.no_spot)).setVisibility(View.GONE);
		SpotsTask spots_task = new SpotsTask();
		if (bounds != null)
			spots_task.execute(swipe, text, latitude, longitude,
					Double.toString(bounds.southwest.latitude),
					Double.toString(bounds.northeast.latitude),
					Double.toString(bounds.southwest.longitude),
					Double.toString(bounds.northeast.longitude));
		else
			spots_task.execute(swipe, text, latitude, longitude, null, null,null, null);
		
		if (mMyMarker != null)
			mMyMarker.remove();
	}

	public void removeMarkers() {
		for (int i = 0; i < mListMarkers.size(); i++) {
			mListMarkers.get(i).remove();
		}
		mListMarkers.clear();
	}

	private class RegionLocationTask extends
			AsyncTask<String, Void, JSONObject> {
		private JSONObject result;
		private boolean searchDone = false;

		private class SearchTimer extends Thread {
			private String[] query;

			public SearchTimer(String... query) {
				this.query = query;
			}

			@Override
			public void run() {
				if(!goOfflineMode){
					System.out.println("Region-location API call" + " " + query[0]+ " " + query[1]);
					result = mModel.searchRegionLocationText(query[0],query[1]);
					searchDone = true;
				}
				else{
					result = mModel.offlineSearchRegionLocationText(query[0], query[1], String.valueOf(2.0));
					searchDone = true;
				}
			}
		}

		protected JSONObject doInBackground(String... query) {

			SearchTimer task = new SearchTimer(query[0], query[1]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false) {
					DiveboardModel._searchtimedout = true;
					return mModel.offlineSearchRegionLocationText(query[0], query[1], String.valueOf(2.0));
				}
				else
					return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(JSONObject result) {
			if (DiveboardModel._searchtimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Spot Search Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
				goOfflineMode = true;
			}
			if (DiveboardModel._cotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Connection Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
				goOfflineMode = true;
			} else if (DiveboardModel._sotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Socket Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
				goOfflineMode = true;
			}

			try {
				if (result != null && result.getBoolean("success") == true) {

					mCountriesArray = result.getJSONArray("countries");
					mRegionsArray = result.getJSONArray("regions");
					mLocationsArray = result.getJSONArray("locations");
					mRegionsIdArray = new JSONArray();
					mLocationsIdArray = new JSONArray();
					List<String> countriesList = new ArrayList<String>();
					List<String> regionsList = new ArrayList<String>();
					List<String> locationsList = new ArrayList<String>();
					JSONObject tempJO = new JSONObject();

					for (int i = 0; i < mCountriesArray.length(); i++) {
						tempJO = mCountriesArray.getJSONObject(i);
						countriesList.add(tempJO.getString("name"));

					}

					for (int i = 0; i < mRegionsArray.length(); i++) {
						JSONObject region = new JSONObject();
						tempJO = mRegionsArray.getJSONObject(i);
						regionsList.add(tempJO.getString("name"));
						String reg = tempJO.getString("name");
						int id = tempJO.getInt("id");
						region.put("region_name", reg);
						region.put("region_id", id);
						mRegionsIdArray.put(region);
						
					}
					System.out.println(mRegionsIdArray.toString());

					for (int i = 0; i < mLocationsArray.length(); i++) {
						JSONObject location = new JSONObject();
						tempJO = mLocationsArray.getJSONObject(i);
						locationsList.add(tempJO.getString("name"));
						String reg = tempJO.getString("name");
						int id = tempJO.getInt("id");
						location.put("location_name", reg);
						location.put("location_id", id);
						mLocationsIdArray.put(location);
					}
					System.out.println(mLocationsIdArray.toString());

					ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_layout, countriesList);
					mCountrySpinner.setAdapter(countryAdapter);

					ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_layout, regionsList);
					mRegionSpinner.setAdapter(regionAdapter);

					ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_layout, locationsList);
					mLocationSpinner.setAdapter(locationAdapter);
					
					((RelativeLayout) mRootView.findViewById(R.id.new_spot_name_layout)).setVisibility(View.VISIBLE);
					((RelativeLayout) mRootView.findViewById(R.id.country_layout)).setVisibility(View.VISIBLE);
					((RelativeLayout) mRootView.findViewById(R.id.region_layout)).setVisibility(View.VISIBLE);
					((RelativeLayout) mRootView.findViewById(R.id.location_layout)).setVisibility(View.VISIBLE);
					((RelativeLayout) mRootView.findViewById(R.id.buttons_layout)).setVisibility(View.VISIBLE);
					((ProgressBar) mRootView.findViewById(R.id.progressBarManualSpot)).setVisibility(View.GONE);

				}else{
					goToSearch(mRootView);
					if(result!= null && !result.isNull("error") && result.getString("error").contains("DB")){ 
						Toast toast = Toast.makeText(getActivity()
								.getApplicationContext(), getResources().getString(R.string.no_db),
								Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
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
				if (!goOfflineMode) {
					result = mModel.searchSpotText(query[0], query[1],query[2], query[3], query[4], query[5], query[6]);
					searchDone = true;
				}
				else {
					result = mModel.offlineSearchSpotText(query[0], query[1],query[2], query[3], query[4], query[5], query[6]);
					searchDone = true;
				}
			}
		}

		protected JSONObject doInBackground(String... query) {
			swipe = query[0];
			SearchTimer task = new SearchTimer(query[1], query[2], query[3],query[4], query[5], query[6], query[7]);
			task.start();
			try {
				task.join(DiveboardModel._searchTimeout);
				if (searchDone == false) {
					goOfflineMode = true;
					DiveboardModel._searchtimedout = true;
					return mModel.offlineSearchSpotText(query[1],query[2], query[3], query[4], query[5], query[6],query[7]);
				}else
					return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(JSONObject result) {
			if (DiveboardModel._searchtimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Spot Search Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
				goOfflineMode = true;
			}
			if (DiveboardModel._cotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Connection Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
				goOfflineMode = true;
				
			} else if (DiveboardModel._sotimedout == true) {
				if (AppConfig.DEBUG_MODE == 1) {
					Toast toast = Toast.makeText(mContext, "Socket Timeout",
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
				goOfflineMode = true;
			}
			
			((ProgressBar) mRootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
			try {
				if (result != null && result.getBoolean("success") == true) {
					try {
						mArray = result.getJSONArray("spots");
						final ListView lv = ((ListView) mRootView
								.findViewById(R.id.list_view));
						final List<Spot> listSpots = new ArrayList<Spot>();
						for (int i = 0; i < mArray.length(); i++) {
							Spot spot = new Spot(mArray.getJSONObject(i));
							listSpots.add(spot);
						}
						removeMarkers();
						if (listSpots.size() == 0) {
							((TextView) mRootView.findViewById(R.id.no_spot))
									.setVisibility(View.VISIBLE);
						} else {
							for (int i = 1; i <= listSpots.size(); i++) {
								if (!manualSpotActivated) {
									Marker marker = mMap
											.addMarker(new MarkerOptions()
													.position(new LatLng(listSpots.get(i - 1).getLat(),listSpots.get(i - 1).getLng()))
													.title(i + ": " + listSpots.get(i - 1).getName())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
									mListMarkers.add(marker);
								} else {
									Marker marker = mMap
											.addMarker(new MarkerOptions()
													.position(new LatLng(listSpots.get(i - 1).getLat(),listSpots.get(i - 1).getLng()))
													.title(i+ ": "+ listSpots.get(i - 1).getName())
													.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey)));
									mListMarkers.add(marker);
								}
							}
							mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

								@Override
								public boolean onMarkerClick(Marker marker) {
									if (!manualSpotActivated) {
										int id = Integer.valueOf(marker.getTitle().substring(0,marker.getTitle().indexOf(":")));
										System.out.println(id);
										lv.smoothScrollToPosition(id);
									}
									marker.showInfoWindow();
									return true;
								}
							});
							System.out.println(result.toString());
							SpotAdapter adapter = new SpotAdapter(mContext,
									listSpots);
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

//									String text = ((TextView) view.findViewById(R.id.name)).getText().toString();
//									text = text.substring(text.indexOf(" ") + 1);
									// ((EditText)
									// rootView.findViewById(R.id.search_bar)).setText(text);
									ListView lv = ((ListView) mRootView.findViewById(R.id.list_view));
									List<Spot> listSpots = new ArrayList<Spot>();
									SpotAdapter adapter = new SpotAdapter(mContext, listSpots); 
									lv.setAdapter(adapter);

									try {

										mSelectedObject = mArray.getJSONObject(position);

									} catch (JSONException e1) {
										// TODO Auto-generated catch block

										e1.printStackTrace();
									}
									// It sets here the selected spot to the Dive Model
									mModel.getDives().get(mIndex).setSpot(mSelectedObject);
									System.out.println("~~~SetSpot with " + mSelectedObject.toString());
									goToSpotSelected(mRootView, mSelectedObject);

								}
							});
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					((TextView) mRootView.findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
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

	public void goToSpotSelected(View view, JSONObject mSpotSelected) {

		mMap.setOnCameraChangeListener(null);
		removeMarkers();
		
		if (mManualMarker != null)
			mManualMarker.remove();

		((LinearLayout) mRootView.findViewById(R.id.on_spot_selected_layout)).setVisibility(View.VISIBLE);
		((LinearLayout) mRootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
		((LinearLayout) mRootView.findViewById(R.id.manual_spot_layout)).setVisibility(View.GONE);

		LatLng mPosition = null;
		String mCountry = "";
		String mSpotName = "";
		String mLocation = "";
		String mRegion = "";

		try {
			Double mLong;
			if(mSpotSelected.isNull("cname"))
				mCountry = mSpotSelected.getString("country_name");
			else
				mCountry = mSpotSelected.getString("cname");
			
			if (mSpotSelected.isNull("long"))
				mLong = mSpotSelected.getDouble("lng");
			else
				mLong = mSpotSelected.getDouble("long");
				
			mPosition = new LatLng((Double) (mSpotSelected.get("lat")),mLong);
			mSpotName = mSpotSelected.getString("name");
			
//			mRegion = mSpotSelected.getString("region");
//			mLocation = mSpotSelected.getString("location");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mPosition != null)
			((TextView) mRootView.findViewById(R.id.details_gps_content)).setText(getCoordinatesDegrees(mPosition));

		else
			((TextView) mRootView.findViewById(R.id.details_gps_content)).setText(getResources().getString(R.string.no_coordinates));

		((TextView) mRootView.findViewById(R.id.nameSelectedSpotTV)).setText(mSpotName);
		((TextView) mRootView.findViewById(R.id.countrySelectedTV)).setText(mCountry);

		Integer zoom = mModel.getDives().get(mIndex).getSpot().getZoom();
		if (zoom == null || zoom > mZoom)
			zoom = mZoom;

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mPosition.latitude, mPosition.longitude), zoom));
		mMyMarker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(mPosition.latitude, mPosition.longitude))
				.title(mSpotName)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

		manualSpotActivated = false;

	}

	public double roundToN(double number, int n) {

		// Round number to n decimals
		double mRounded;
		int grade = 1;

		if (n < 1) {
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

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		if (arg0 != null && manualSpotActivated)
			setManualMarker(mRootView, arg0.getPosition());
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub

		if (manualSpotActivated)
			setManualMarker(mRootView, point);

		System.out.println("long pressed, point=" + point);

	}
}
