package com.diveboard.mobile.newdive;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.astuetz.PagerSlidingTabStrip;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.WaitDialogFragment;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment;
import com.diveboard.mobile.newdive.NewGuideNameDialogFragment.EditGuideNameDialogListener;
import com.diveboard.mobile.editdive.EditOption;
import com.diveboard.mobile.editdive.OptionAdapter;
import com.diveboard.mobile.editdive.TabEditSpotsFragment;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment.EditConfirmDialogListener;
import com.diveboard.mobile.newdive.NewAltitudeDialogFragment.EditAltitudeDialogListener;
import com.diveboard.mobile.newdive.NewBottomTempDialogFragment.EditBottomTempDialogListener;
import com.diveboard.mobile.newdive.NewCurrentDialogFragment.EditCurrentDialogListener;
import com.diveboard.mobile.newdive.NewDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.newdive.NewDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.newdive.NewDiveTypeDialogFragment.EditDiveTypeDialogListener;
import com.diveboard.mobile.newdive.NewDurationDialogFragment.EditDurationDialogListener;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment.EditMaxDepthDialogListener;
import com.diveboard.mobile.newdive.NewReviewDialogFragment.EditReviewDialogListener;
import com.diveboard.mobile.newdive.NewSafetyStopsDialogFragment.EditSafetyStopsDialogListener;
import com.diveboard.mobile.newdive.NewSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.newdive.NewTanksDialogFragment.EditTanksDialogListener;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.newdive.NewTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.newdive.NewVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.newdive.NewWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment.EditWeightsDialogListener;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveCreateListener;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.FirstFragment;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class					NewDiveActivity extends FragmentActivity implements EditDateDialogListener,
																					EditTimeInDialogListener,
																					EditMaxDepthDialogListener,
																					EditDurationDialogListener,
																					EditWeightsDialogListener,
																					EditDiveNumberDialogListener,
																					EditTripNameDialogListener,
																					EditVisibilityDialogListener,
																					EditCurrentDialogListener,
																					EditSurfaceTempDialogListener,
																					EditBottomTempDialogListener,
																					EditTanksDialogListener,
																					EditAltitudeDialogListener,
																					EditWaterDialogListener,
																					EditConfirmDialogListener,
																					EditSafetyStopsDialogListener,
																					EditDiveTypeDialogListener,
																					EditGuideNameDialogListener,
																					EditReviewDialogListener
{
	//public static DiveboardModel		mModel;
	private Typeface			mFaceB;
	private NewPagerAdapter		adapterViewPager;
	private int					NUM_ITEMS = 6;
	private TextView			mTitle = null;
	public static OptionAdapter	mOptionAdapter;
	private TabNewDetailsFragment	mNewDetailsFragment = new TabNewDetailsFragment();
	public static ListView		optionList;
	private boolean				mError = false;
	public static EditText		mNotes = null;
	public static boolean 		isNewSpot = false;
	private TabNewNotesFragment	mNewNotesFragment = new TabNewNotesFragment();
	private TabNewSpotsFragment	mNewSpotsFragment = new TabNewSpotsFragment();
	private TabNewPhotosFragment	mNewPhotosFragment = new TabNewPhotosFragment();
	private TabNewShopFragment mNewShopFragment = new TabNewShopFragment();
	private TabNewBuddiesFragment mNewBuddiesFragment = new TabNewBuddiesFragment();
	
	public class			NewPagerAdapter extends FragmentPagerAdapter
	{
		
		public NewPagerAdapter(android.support.v4.app.FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position)
		{
			// return FirstFragment.newInstance(1, "Page # 2");
			switch (position)
			{
				case 0:
					return mNewDetailsFragment;
				case 1:
					return mNewSpotsFragment;
				case 2:
					return mNewShopFragment;
				case 3:
					return mNewBuddiesFragment;
				case 4:
					return mNewPhotosFragment;
				case 5:
					return mNewNotesFragment;
				default:
					return null;
			}
		}

		@Override
		public int getCount()
		{
			return NUM_ITEMS;
		}
		
        @Override
        public CharSequence getPageTitle(int position)
        {
        	switch (position)
			{
				case 0:
					return getResources().getString(R.string.tab_dive_details_menutitle);
				case 1:
					return getResources().getString(R.string.tab_spot_menutitle);
				case 2:
					return getResources().getString(R.string.tab_shop_menutitle);
				case 3:
					return getResources().getString(R.string.tab_buddies_menutitle);
				case 4:
					return getResources().getString(R.string.tab_photos_menutitle);
				case 5:
					return getResources().getString(R.string.tab_notes_menutitle);
				default:
					return null;
			}
        }
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
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
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.activity_edit_dive);
	    
	    if (((ApplicationController)getApplicationContext()).getTempDive() == null)
	    {
	    	DiveboardModel model = ((ApplicationController)getApplicationContext()).getModel();
	    	ArrayList<Dive> dives = model.getDives();
	    	int id = 0;
	    	for (int i = 0, size = dives.size(); i < size; i++)
	    	{
	    		if (dives.get(i).getId() < id)
	    			id = dives.get(i).getId();
	    	}
	    	id--;
	    	Dive new_dive = new Dive();
	    	System.out.println("New dive id: " + id);
	    	new_dive.setId(id);
	    	((ApplicationController)getApplicationContext()).setTempDive(new_dive);
	    }
	
	    mFaceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
	    
	    mTitle = (TextView) findViewById(R.id.title);
	    mTitle.setTypeface(mFaceB);
	    mTitle.setText(getResources().getString(R.string.tab_details_edit_title));
	    
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.add_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
				ArrayList<Dive> dives = ((ApplicationController)getApplicationContext()).getModel().getDives();
				ArrayList<Pair<String, String>> editList = mDive.getEditList();
				if (mDive.getMaxdepth() == null || mDive.getDuration() == null || TabNewSpotsFragment.manualSpotActivated)
					mError = true;
				else
					mError = false;
				if (mDive.getMaxdepth() == null)
				{
					if (2 - optionList.getFirstVisiblePosition() >= 0)
					{
						View view = optionList.getChildAt(2 - optionList.getFirstVisiblePosition());
						((TextView)view.findViewById(R.id.optTitle)).setError(getResources().getString(R.string.empty_field_error));
						((TextView)view.findViewById(R.id.optTitle)).requestFocus();
					}
					
				}
				if (mDive.getDuration() == null)
				{
					if (4 - optionList.getFirstVisiblePosition() >= 0)
					{
						View view = optionList.getChildAt(4 - optionList.getFirstVisiblePosition());
						((TextView)view.findViewById(R.id.optTitle)).setError(getResources().getString(R.string.empty_field_error));
						((TextView)view.findViewById(R.id.optTitle)).requestFocus();
					}
					
				}
				if (mError == false)
				{
					if (mNotes != null)
						mDive.setNotes(mNotes.getText().toString());
					WaitDialogFragment dialog = new WaitDialogFragment();
					dialog.show(getSupportFragmentManager(), "WaitDialogFragment");
					if (editList != null && editList.size() > 0)
					{
						JSONObject edit = new JSONObject(); 
						for (int i = 0, size = editList.size(); i < size; i++)
							try {
								if (editList.get(i).first.equals("spot")){
									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
									System.out.println("The selected manual spot is " + editList.get(i).second );
								}
									
								else if (editList.get(i).first.equals("shop") || editList.get(i).first.equals("dive_reviews"))
									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
								else if (editList.get(i).first.equals("divetype"))
									edit.put(editList.get(i).first, new JSONArray(editList.get(i).second));
								else if (editList.get(i).first.equals("pictures"))
									edit.put(editList.get(i).first, new JSONArray(editList.get(i).second));
								else if (editList.get(i).first.equals("buddies"))
									edit.put(editList.get(i).first, new JSONArray(editList.get(i).second));
								else if (editList.get(i).first.equals("dive_reviews"))
									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
								else
									edit.put(editList.get(i).first, editList.get(i).second);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						try {
							mDive.applyEdit(edit);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mDive.clearEditList();
					}
					dives.add(0, mDive);
					((ApplicationController)getApplicationContext()).getModel().getDataManager().setOnDiveCreateComplete(new DiveCreateListener() {
						@Override
						public void onDiveCreateComplete() {
							finish();
						}
					});
					((ApplicationController)getApplicationContext()).getModel().getDataManager().save(mDive);
					((ApplicationController)getApplicationContext()).setRefresh(1);
//					Toast toast = Toast.makeText(getApplicationContext(), "The new dive will be displayed after refreshing the page!", Toast.LENGTH_LONG);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
					((ApplicationController)getApplicationContext()).setTempDive(null);
//					finish();
				}
				else
				{
					if((mDive.getMaxdepth() == null || mDive.getDuration() == null) && !TabNewSpotsFragment.manualSpotActivated)
					{
						Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.maxdepth_duration_missing), Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					if((mDive.getMaxdepth() == null || mDive.getDuration() == null) && TabNewSpotsFragment.manualSpotActivated){
						Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.maxdepth_duration_spot_missing), Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					if(TabNewSpotsFragment.manualSpotActivated){
						Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.spot_missing), Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();	
					}
				}
			}
		});
	    
	    ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
	    adapterViewPager = new NewPagerAdapter(getSupportFragmentManager());
	    vpPager.setAdapter(adapterViewPager);
        vpPager.setOffscreenPageLimit(NUM_ITEMS);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(vpPager);
        tabs.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				System.out.println("Page changed");
				if (mTitle == null)
					return ;
				switch (position)
				{
					case 0:
						mTitle.setText(getResources().getString(R.string.tab_details_edit_title));
						return ;
					case 1:
						mTitle.setText(getResources().getString(R.string.tab_spots_title));
						return ;
					case 2:
						mTitle.setText(getResources().getString(R.string.tab_shop_title));
						return ;
					case 3:
						mTitle.setText(getResources().getString(R.string.tab_buddies_title));
						return ;
					case 4:
						mTitle.setText(getResources().getString(R.string.tab_photos_title));
						return ;
					case 5:
						mTitle.setText(getResources().getString(R.string.tab_notes_edit_title));
					default:
						return ;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
        
	}
	
	@Override
	public void onBackPressed()
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getEditList()!= null && mDive.getEditList().size() > 0)
		{
			EditConfirmDialogFragment dialog = new EditConfirmDialogFragment();
	    	dialog.show(getSupportFragmentManager(), "EditConfirmDialogFragment");
		}
		else
		{
			clearEditList();
		}
	};
	
	@Override
	public void onTripNameEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		((EditOption)mOptionAdapter.getItem(8)).setValue(mDive.getTripName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
	
	@Override
	public void onDiveNumberEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getNumber() != null)
			((EditOption)mOptionAdapter.getItem(6)).setValue(mDive.getNumber().toString());
		else
			((EditOption)mOptionAdapter.getItem(6)).setValue("");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		((EditOption)mOptionAdapter.getItem(0)).setValue(mDive.getDate());
		String[] time_in = mDive.getTimeIn().split("T");
		String new_time_in = mDive.getDate() + "T" + time_in[1];
		mDive.setTimeIn(new_time_in);
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		String[] time_in = mDive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onMaxDepthEditComplete(DialogFragment dialog)
	{
		final Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		String maxdepth_unit = "";
		if (mDive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		else
			maxdepth_unit = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(mDive.getMaxdepth()) + " " + maxdepth_unit);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
		new Thread(new Runnable() {
	        public void run() {
	        	try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	if (4 - optionList.getFirstVisiblePosition() >= 0)
	        	{
	        		((TextView)optionList.getChildAt(4 - optionList.getFirstVisiblePosition()).findViewById(R.id.optTitle)).post(new Runnable() {
		                public void run() {
		                	if (mDive.getDuration() == null)
		                	{
		                		View view = optionList.getChildAt(4 - optionList.getFirstVisiblePosition());
		                		((TextView)view.findViewById(R.id.optTitle)).requestFocus();
			                	((TextView)view.findViewById(R.id.optTitle)).setError(getResources().getString(R.string.empty_field_error));
			        			mError = true;
		                	}
		                }
		            });
	        	}
	        }
	    }).start();
	}

	@Override
	public void onDurationEditComplete(DialogFragment dialog)
	{
		final Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		((EditOption)mOptionAdapter.getItem(4)).setValue(Integer.toString(mDive.getDuration()) + " " + getResources().getString(R.string.unit_min));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
		new Thread(new Runnable() {
	        public void run() {
	        	try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	if (2 - optionList.getFirstVisiblePosition() >= 0)
	        	{
	        		((TextView)optionList.getChildAt(2 - optionList.getFirstVisiblePosition()).findViewById(R.id.optTitle)).post(new Runnable() {
		                public void run() {
		                	if (mDive.getMaxdepth() == null)
		                	{
		                		View view = optionList.getChildAt(2 - optionList.getFirstVisiblePosition());
		                		((TextView)view.findViewById(R.id.optTitle)).requestFocus();
			                	((TextView)view.findViewById(R.id.optTitle)).setError(getResources().getString(R.string.empty_field_error));
			        			mError = true;
		                	}
		                	
		                }
		            });
	        	}
	        }
	    }).start();
	}

	@Override
	public void onSurfaceTempEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getTempSurface() == null)
			((EditOption)mOptionAdapter.getItem(12)).setValue("");
		else
		{
//			((EditOption)mOptionAdapter.getItem(11)).setValue(Double.toString(mDive.getTempSurface().getTemperature()) + " °" + mDive.getTempSurface().getSmallName());
			String tempsurface_unit = "";
			if (mDive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempsurface_unit = (mDive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempsurface_unit.equals(getResources().getString(R.string.unit_C)))
				((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(mDive.getTempSurface()) + " " + getResources().getString(R.string.unit_C_symbol));
			else
				((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(mDive.getTempSurface()) + " " + getResources().getString(R.string.unit_F_symbol));
		}
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onBottomTempEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getTempBottom() == null)
			((EditOption)mOptionAdapter.getItem(13)).setValue("");
		else
		{
//			((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(mDive.getTempBottom().getTemperature()) + " °" + mDive.getTempBottom().getSmallName());
			String tempbottom_unit = "";
			if (mDive.getTempBottomUnit() == null)
				tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempbottom_unit = (mDive.getTempBottomUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempbottom_unit.equals(getResources().getString(R.string.unit_C)))
				((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(mDive.getTempBottom()) + " " + getResources().getString(R.string.unit_C_symbol));
			else
				((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(mDive.getTempBottom()) + " " + getResources().getString(R.string.unit_F_symbol));
		}
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWeightsEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getWeights() == null)
			((EditOption)mOptionAdapter.getItem(5)).setValue("");
		else
		{
//			((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(mDive.getWeights().getWeight()) + " " + mDive.getWeights().getSmallName());
			String weights_unit = "";
			if (mDive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			else
				weights_unit = (mDive.getWeightsUnit().compareTo(getResources().getString(R.string.unit_kg)) == 0) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(mDive.getWeights()) + " " + weights_unit);
		}
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onVisibilityEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getVisibility() == null)
			((EditOption)mOptionAdapter.getItem(10)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(10)).setValue(mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1));
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCurrentEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getCurrent() == null)
			((EditOption)mOptionAdapter.getItem(11)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(11)).setValue(mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onAltitudeEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getAltitude() == null)
			((EditOption)mOptionAdapter.getItem(14)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(14)).setValue(Double.toString(mDive.getAltitude().getDistance()) + " " + mDive.getAltitude().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onWaterEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		if (mDive.getWater() == null)
			((EditOption)mOptionAdapter.getItem(15)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(15)).setValue(mDive.getWater().substring(0, 1).toUpperCase() + mDive.getWater().substring(1));
		mOptionAdapter.notifyDataSetChanged();
	}
	
	public void clearEditList()
	{
		Bundle bundle = new Bundle();
		
		// put
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
		((ApplicationController)getApplicationContext()).setTempDive(null);
	}
	
	@Override
	public void onConfirmEditComplete(DialogFragment dialog)
	{
		clearEditList();
	}

	@Override
	public void onSafetyStopsEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		ArrayList<SafetyStop> safetystop = mDive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit() + "-" + safetystop.get(i).getDuration().toString() + getResources().getString(R.string.unit_min);
		}
		((EditOption)mOptionAdapter.getItem(3)).setValue(safetydetails);
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDiveTypeEditComplete(DialogFragment dialog) {
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		ArrayList<String> divetype = mDive.getDivetype();
		String divetype_string = "";
		for (int i = 0, length = divetype.size(); i < length; i++)
		{
			if (i != 0)
				divetype_string += ", ";
			divetype_string += divetype.get(i);
		}
		((EditOption)mOptionAdapter.getItem(9)).setValue(divetype_string);
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGuideNameEditComplete(DialogFragment dialog)
	{
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		((EditOption)mOptionAdapter.getItem(7)).setValue(mDive.getGuide());
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onReviewEditComplete(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
		Dive mDive = ((ApplicationController) getApplicationContext()).getTempDive();
		String fullReview = "";
		if (mDive.getDiveReviews().getOverall()!= null)
			fullReview += "Overall: " + _getReviewHintGeneral(mDive.getDiveReviews().getOverall()).toLowerCase() + ". ";
		if (mDive.getDiveReviews().getDifficulty()!= null)
			fullReview += "Dive difficulty: " + _getReviewHintDifficulty(mDive.getDiveReviews().getDifficulty()).toLowerCase() + ". ";
		if (mDive.getDiveReviews().getMarine()!= null)
			fullReview += "Marine life: " + _getReviewHintGeneral(mDive.getDiveReviews().getMarine()).toLowerCase() + ". ";
		if (mDive.getDiveReviews().getBigFish()!= null)
			fullReview += "Big fish sighted:  " + _getReviewHintGeneral(mDive.getDiveReviews().getBigFish()).toLowerCase() + ". ";
		if (mDive.getDiveReviews().getWreck()!= null)
			fullReview += "Wrecks sighted: " + _getReviewHintGeneral(mDive.getDiveReviews().getWreck()).toLowerCase() + ". ";
		((EditOption)mOptionAdapter.getItem(18)).setValue(fullReview);
		mOptionAdapter.notifyDataSetChanged();
		
	}
	private String _getReviewHintGeneral(int rating) {
		String resul = "";
		switch (rating) {
		case 0:
			resul = "";
			break;

		case 1:
			resul = getResources().getString(R.string.hint_terrible);
			break;

		case 2:
			resul = getResources().getString(R.string.hint_poor);
			break;

		case 3:
			resul = getResources().getString(R.string.hint_average);
			break;

		case 4:
			resul = getResources().getString(R.string.hint_very_good);
			break;

		case 5:
			resul = getResources().getString(R.string.hint_excellent);
			break;

		default:
			break;
		}
		return resul;
	}
    
	private String 				_getReviewHintDifficulty(int rating) {

		String resul = "";

		switch (rating) {

		case 0:
			resul = "";
			break;

		case 1:
			resul = getResources().getString(R.string.hint_trivial);
			break;

		case 2:
			resul = getResources().getString(R.string.hint_simple);
			break;

		case 3:
			resul = getResources().getString(R.string.hint_somewhat_simple);
			break;

		case 4:
			resul = getResources().getString(R.string.hint_tricky);
			break;

		case 5:
			resul = getResources().getString(R.string.hint_hardcore);
			break;

		default:
			break;

		}

		return resul;

	}

	@Override
	public void onTanksEditComplete(DialogFragment dialog) {
		// TODO Auto-generated method stub
		Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		ArrayList<Tank> mTanks = (ArrayList<Tank>) mDive.getTanks().clone();
		String tankString = "";
		if (mTanks != null && mTanks.size() > 0 )
			tankString = mTanks.size() + " tanks used";
		
		((EditOption)mOptionAdapter.getItem(17)).setValue(tankString);
		mOptionAdapter.notifyDataSetChanged();
	}
}
