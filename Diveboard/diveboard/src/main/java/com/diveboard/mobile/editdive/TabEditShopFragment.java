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
import android.widget.ImageView;
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
import com.diveboard.model.Shop;
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

public class					TabEditShopFragment extends Fragment
{

	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private int							mIndex;
	private ShopsTask 					mShopsTask;
	private Shop						mShop;
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
	private ViewGroup					mRootView;
	private DiveboardModel				mModel;
	private Context						mContext;
	
	public TabEditShopFragment(){}
	
	public TabEditShopFragment(Context ctx)
	{
		mContext = ctx;
	}
	
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_shops, container, false);
    	if (mContext == null)
        	mContext = getActivity();
    	//mContext = getActivity().getApplicationContext();
    	mModel = ((ApplicationController)mContext.getApplicationContext()).getModel();
	    mFaceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Light.ttf");
	    mFaceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Regular.ttf");
		mIndex = getActivity().getIntent().getIntExtra("index", 0);
		TextView no_spot_view = ((TextView)getActivity().findViewById(R.id.no_shop));
		((TextView)mRootView.findViewById(R.id.no_shop)).setTypeface(mFaceR);
		
		((Button)mRootView.findViewById(R.id.goToSearch)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goToSearch(mRootView);
			}
		});
		((Button)mRootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
		((ImageView)mRootView.findViewById(R.id.GPSImage)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				activeGPS(mRootView);
			}
		});
		
	    EditText editText = (EditText) mRootView.findViewById(R.id.search_bar);
	    editText.setTypeface(mFaceR);
	    editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
	            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	            	mMap.setOnCameraChangeListener(null);
	                doMySearch("search", ((TextView)mRootView.findViewById(R.id.search_bar)).getText().toString(), null, null, null);
	            	InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	        		imm.toggleSoftInput(0, 0);
	                handled = true;
	            }
	            return handled;
			}
	    });
		if (mMap == null) {
			FragmentManager fm = getActivity().getSupportFragmentManager();
	        Fragment fragment = fm.findFragmentById(R.id.mapfragmentshop);
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
	        	
	        	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		        mMap.getUiSettings().setAllGesturesEnabled(true);
				mMap.getUiSettings().setMyLocationButtonEnabled(true);
				mMap.getUiSettings().setZoomControlsEnabled(true);
				mMap.getUiSettings().setZoomGesturesEnabled(true);
				
				mMap.getUiSettings().setRotateGesturesEnabled(true);
				mMap.getUiSettings().setScrollGesturesEnabled(true);
				mMap.getUiSettings().setCompassEnabled(true);
				if (mModel.getDives().get(mIndex).getShop() != null)
				{
					System.out.println("shop != null");
					((LinearLayout)mRootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
					((TextView)mRootView.findViewById(R.id.details_name)).setTypeface(mFaceB);
					((TextView)mRootView.findViewById(R.id.details_name_content)).setTypeface(mFaceR);
					((TextView)mRootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
					((TextView)mRootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
					((TextView)mRootView.findViewById(R.id.details_name_content)).setText(mModel.getDives().get(mIndex).getShop().getName());
					((TextView)mRootView.findViewById(R.id.details_gps_content)).setText(getPosition());
					((Button)mRootView.findViewById(R.id.goToSearch)).setTypeface(mFaceB);
					
					if (mModel.getDives().get(mIndex).getShop().getLat() != null && mModel.getDives().get(mIndex).getShop().getLng() != null)
					{
						mMyMarker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(mModel.getDives().get(mIndex).getShop().getLat(), mModel.getDives().get(mIndex).getShop().getLng()))
						.title(mModel.getDives().get(mIndex).getShop().getName())
						.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
						//System.out.println(mModel.getDives().get(mIndex).getShop().getId());
						Integer zoom = mZoom;// = mModel.getDives().get(mIndex).getShop().getZoom();
//						if (zoom == null || zoom > mZoom)
//							zoom = mZoom;
						if (mModel.getDives().get(mIndex).getShop().getLat() != null && mModel.getDives().get(mIndex).getShop().getLng() != null)
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mModel.getDives().get(mIndex).getShop().getLat(), mModel.getDives().get(mIndex).getShop().getLng()), zoom));
					}
				}
				else
				{
					System.out.println("shop == null");
					((LinearLayout)mRootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
					activeGPS(null);
				}
	        }
		}
		return mRootView;
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
    	if (mLocationManager != null && mLocationListener != null)
    	{
    		mLocationManager.removeUpdates(mLocationListener);
    		mLocationManager = null;
    		mLocationListener = null;
    	}
		super.onDestroy();
	}
    
    public void goToSearch(View view)
    {
    	((LinearLayout)mRootView.findViewById(R.id.view_details)).setVisibility(View.GONE);
    	((LinearLayout)mRootView.findViewById(R.id.view_search)).setVisibility(View.VISIBLE);
    	InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        ((EditText) mRootView.findViewById(R.id.search_bar)).requestFocusFromTouch();
        ((EditText) mRootView.findViewById(R.id.search_bar)).setSelection(((EditText) mRootView.findViewById(R.id.search_bar)).length());
    }
    
    public void activeGPS(View view)
    {
    	InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(((EditText) mRootView.findViewById(R.id.search_bar)).getWindowToken(), 0);
		((EditText) mRootView.findViewById(R.id.search_bar)).setText("");
		if (mLocationManager != null && mLocationListener != null)
    	{
    		mLocationManager.removeUpdates(mLocationListener);
    		mLocationManager = null;
    		mLocationListener = null;
    	}
    	//removeMarkers();
    	mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
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
					//System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
					doMySearch("swipe", null, mLatitude.toString(), mLongitude.toString(), bounds);
					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					
						@Override
						public void onCameraChange(CameraPosition pos) {
							LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
							//System.out.println(Double.toString(bounds.southwest.latitude) + " " + Double.toString(bounds.northeast.latitude) + " " + Double.toString(bounds.southwest.longitude) + " " + Double.toString(bounds.northeast.longitude));
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
    
    public String getPosition()
	{
    	ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
		String pos = "";
		if (mModel.getDives().get(mIndex).getShop().getLat() == null)
		{
			pos += "0° ";
			pos += "N";
		}
		else if (mModel.getDives().get(mIndex).getShop().getLat() >= 0)
		{
			pos += String.valueOf(mModel.getDives().get(mIndex).getShop().getLat()) + "° ";
			pos += "N";
		}
		else if (mModel.getDives().get(mIndex).getShop().getLat() < 0)
		{
			pos += String.valueOf(mModel.getDives().get(mIndex).getShop().getLat() * (-1)) + "° ";
			pos += "S";
		}
		pos += ", ";
		if (mModel.getDives().get(mIndex).getShop().getLng() == null)
		{
			pos += "0° ";
			pos += "E";
		}
		else if (mModel.getDives().get(mIndex).getShop().getLng() >= 0)
		{
			pos += String.valueOf(mModel.getDives().get(mIndex).getShop().getLng()) + "° ";
			pos += "E";
		}
		else if (mModel.getDives().get(mIndex).getShop().getLng() < 0)
		{
			pos += String.valueOf(mModel.getDives().get(mIndex).getShop().getLng() * (-1)) + "° ";
			pos += "W";
		}
		if ((mModel.getDives().get(mIndex).getShop().getLat() == null || mModel.getDives().get(mIndex).getShop().getLat() == 0) && 
				(mModel.getDives().get(mIndex).getShop().getLng() == null || mModel.getDives().get(mIndex).getShop().getLng() == 0))
			pos = "";
		return (pos);
	}
    
    public void doMySearch(String swipe, String text, String latitude, String longitude, LatLngBounds bounds)
    {  	
    	ListView lv = ((ListView)mRootView.findViewById(R.id.list_view));
    	List<Shop> listShops = new ArrayList<Shop>();
    	ShopAdapter adapter = new ShopAdapter(mContext, listShops);
    	lv.setAdapter(adapter);
    	((TextView)mRootView.findViewById(R.id.no_shop)).setVisibility(View.GONE);
//    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.toggleSoftInput(0, 0);
		((ProgressBar)mRootView.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
    	ShopsTask shops_task = new ShopsTask();
    	if (bounds != null)
    		shops_task.execute(swipe, text, latitude, longitude, Double.toString(bounds.southwest.latitude), Double.toString(bounds.northeast.latitude), Double.toString(bounds.southwest.longitude), Double.toString(bounds.northeast.longitude));
    	else
    		shops_task.execute(swipe, text, latitude, longitude, null, null, null, null);
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
    
    private class ShopsTask extends AsyncTask<String, Void, JSONObject>
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
    			ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
//    			System.out.println(query[0] + " " + query[1] + " " + query[2] + " " + query[3] + " " + query[4] + " " + query[5] + " " + query[6]);
    			result = mModel.searchShopText(query[0], query[1], query[2], query[3], query[4], query[5], query[6]);
//    			System.out.println(result);
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
					return null;
//					DiveboardModel._searchtimedout = true;
//					ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
//					return mModel.offlineSearchShopText(query[1], query[2], query[3], query[4], query[5], query[6], query[7]);
				}
				return result;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
//			ApplicationController AC = (ApplicationController)getApplicationContext();
//			return mModel.searchSpotText(query[0], null, null, null, null, null, null);
		}
		
		protected void onPostExecute(JSONObject result)
		{
			if (DiveboardModel._searchtimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(mContext.getApplicationContext(), "Shop Search Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._searchtimedout = false;
			}
			if (DiveboardModel._cotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(mContext.getApplicationContext(), "Connection Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._cotimedout = false;
			}
			else if (DiveboardModel._sotimedout == true)
			{
				if (AppConfig.DEBUG_MODE == 1)
				{
					Toast toast = Toast.makeText(mContext.getApplicationContext(), "Socket Timeout", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				DiveboardModel._sotimedout = false;
			}
			((ProgressBar)mRootView.findViewById(R.id.progressBar)).setVisibility(View.GONE);
			try {
				if (result != null && result.getBoolean("success") == true)
				{
					try {
						//System.out.println(result);
						//((ListView)findViewById(R.id.list_view)).setVisibility(View.VISIBLE);
						mArray = result.getJSONArray("shops");
						final ListView lv = ((ListView)mRootView.findViewById(R.id.list_view));
						final List<Shop> listShops = new ArrayList<Shop>();
						for (int i = 0; i < mArray.length(); i++)
						{
							Shop shop = new Shop(mArray.getJSONObject(i));
							listShops.add(shop);
							
						}
						if (listShops.size() == 0)
						{
							((TextView)mRootView.findViewById(R.id.no_shop)).setVisibility(View.VISIBLE);
						}
						else
						{
							for (int i = 1; i <= listShops.size(); i++)
							{
								//System.out.println(listShops.get(i - 1).getName() + " " +listShops.get(i - 1).getLat());
								if (listShops.get(i - 1).getLat() != null && listShops.get(i - 1).getLng() != null)
								{
									Marker marker = mMap.addMarker(new MarkerOptions()
									.position(new LatLng(listShops.get(i - 1).getLat(), listShops.get(i - 1).getLng()))
									.title(i + ": " + listShops.get(i - 1).getName())
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
									mListMarkers.add(marker);
								}
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
										//System.out.println(id);
										lv.smoothScrollToPosition(id);
									}
									marker.showInfoWindow();
									return true;
								}
							});
							ShopAdapter adapter = new ShopAdapter(mContext, listShops);
							//Zoom out to show markers
							if (swipe.contentEquals("search"))
							{
								LatLngBounds.Builder builder = new LatLngBounds.Builder();
								for (Marker marker : mListMarkers) {
								    builder.include(marker.getPosition());
								}
								if (mListMarkers.size() > 0)
								{
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
							}
							lv.setAdapter(adapter);
							lv.setOnItemClickListener(new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									mMap.setOnCameraChangeListener(null);
									//System.out.println("ZOOM = " + mMap.getCameraPosition().zoom);
									removeMarkers();
									ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
									
									((LinearLayout)mRootView.findViewById(R.id.view_details)).setVisibility(View.VISIBLE);
							    	((LinearLayout)mRootView.findViewById(R.id.view_search)).setVisibility(View.GONE);
									//((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
							    	String text = ((TextView)mRootView.findViewById(R.id.name)).getText().toString();
							    	text = text.substring(text.indexOf(" ") + 1);
									((EditText)mRootView.findViewById(R.id.search_bar)).setText(text);
							    	ListView lv = ((ListView)mRootView.findViewById(R.id.list_view));
							    	List<Shop> listShops = new ArrayList<Shop>();
							    	ShopAdapter adapter = new ShopAdapter(mContext, listShops);
							    	lv.setAdapter(adapter);
//							    	ImageView remove = (ImageView) findViewById(R.id.remove_button);
//							    	remove.setVisibility(View.VISIBLE);
							    	mHasChanged = true;
							    	try {
										mSelectedObject = mArray.getJSONObject(position);
										mModel.getDives().get(mIndex).setShop(mSelectedObject);
										if (mMyMarker != null)
											mMyMarker.remove();
										Integer zoom = mZoom;// = mModel.getDives().get(mIndex).getShop().getZoom();
//										if (zoom == null || zoom > mZoom)
//											zoom = mZoom;
										Shop shop = mModel.getDives().get(mIndex).getShop();
										if (shop.getLat() != null && shop.getLng() != null)
										{
											mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(shop.getLat(), shop.getLng()), zoom));
											mMyMarker = mMap.addMarker(new MarkerOptions()
											.position(new LatLng(shop.getLat(), shop.getLng()))
											.title(shop.getName())
											.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
										}
										
										
										((TextView)mRootView.findViewById(R.id.details_name)).setTypeface(mFaceB);
										((TextView)mRootView.findViewById(R.id.details_name_content)).setTypeface(mFaceR);
										((TextView)mRootView.findViewById(R.id.details_gps)).setTypeface(mFaceB);
										((TextView)mRootView.findViewById(R.id.details_gps_content)).setTypeface(mFaceR);
										((TextView)mRootView.findViewById(R.id.details_name_content)).setText(mModel.getDives().get(mIndex).getShop().getName());
										((TextView)mRootView.findViewById(R.id.details_gps_content)).setText(getPosition());
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
					((TextView)mRootView.findViewById(R.id.no_shop)).setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onCancelled() {
			mShopsTask = null;
		}
	}
    
    public class ShopAdapter extends BaseAdapter
    {
    	LayoutInflater	mLayoutInflater;
    	List<Shop>		mShopsList;
    	
    	public ShopAdapter(Context context, List<Shop> shopsList)
    	{
    		mLayoutInflater = LayoutInflater.from(context);
    		this.mShopsList = shopsList;
    	}
    	
		@Override
		public int getCount() {
			return mShopsList.size();
		}

		@Override
		public Object getItem(int position) {
			return mShopsList.get(position);
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
			holder.name.setText((position + 1) + ": " + mShopsList.get(position).getName());
			holder.name.setTypeface(mFaceR);
			holder.location_country.setText(mShopsList.get(position).getCountryName());
			//holder.location_country.setText("");
			
			holder.location_country.setTypeface(mFaceR);
			holder.name.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
			holder.location_country.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
			return convertView;
		}
		
		private class ViewHolder {
			TextView id;
			TextView name;
			TextView location_country;
		}
    	
    }
}
