package com.diveboard.mobile.editdive;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.NavDrawer;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditAltitudeDialogFragment.EditAltitudeDialogListener;
import com.diveboard.mobile.editdive.EditBottomTempDialogFragment.EditBottomTempDialogListener;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment.EditConfirmDialogListener;
import com.diveboard.mobile.editdive.EditCurrentDialogFragment.EditCurrentDialogListener;
import com.diveboard.mobile.editdive.EditDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.editdive.EditDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.editdive.EditDiveTypeDialogFragment.EditDiveTypeDialogListener;
import com.diveboard.mobile.editdive.EditDurationDialogFragment.EditDurationDialogListener;
import com.diveboard.mobile.editdive.EditGuideNameDialogFragment.EditGuideNameDialogListener;
import com.diveboard.mobile.editdive.EditMaxDepthDialogFragment.EditMaxDepthDialogListener;
import com.diveboard.mobile.editdive.EditReviewDialogFragment.EditReviewDialogListener;
import com.diveboard.mobile.editdive.EditSafetyStopsDialogFragment.EditSafetyStopsDialogListener;
import com.diveboard.mobile.editdive.EditSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.editdive.EditTanksDialogFragment.EditTanksDialogListener;
import com.diveboard.mobile.editdive.EditTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.editdive.EditVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.editdive.EditWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.editdive.EditWeightsDialogFragment.EditWeightsDialogListener;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.google.analytics.tracking.android.EasyTracker;

public class EditDiveActivity extends NavDrawer implements
		EditTripNameDialogListener, EditDiveNumberDialogListener,
		EditDateDialogListener, EditTimeInDialogListener,
		EditMaxDepthDialogListener, EditDurationDialogListener,
		EditSurfaceTempDialogListener, EditBottomTempDialogListener,
		EditWeightsDialogListener, EditVisibilityDialogListener,
		EditCurrentDialogListener, EditAltitudeDialogListener,
		EditWaterDialogListener, EditConfirmDialogListener,
		EditTanksDialogListener, EditSafetyStopsDialogListener,
		EditDiveTypeDialogListener, EditGuideNameDialogListener,
		EditReviewDialogListener

{
	private int						mIndex;
	private Typeface				mFaceB;
	public static EditPagerAdapter	adapterViewPager;
	private TabEditDetailsFragment	mEditDetailsFragment = new TabEditDetailsFragment();
	public DiveboardModel			mModel;
	public static OptionAdapter		mOptionAdapter;
	private TextView				mTitle = null;
	private TabEditNotesFragment	mEditNotesFragment = new TabEditNotesFragment();
	private TabEditPhotosFragment	mEditPhotosFragment = new TabEditPhotosFragment();
	public static EditText			mNotes = null;
	private TabEditSpotsFragment	mEditSpotsFragment = new TabEditSpotsFragment();
	private TabEditShopFragment		mEditShopFragment = new TabEditShopFragment(this);
	private TabEditBuddiesFragment	mEditBuddiesFragment = new TabEditBuddiesFragment();
	public static boolean 			isNewSpot = false;
	private boolean					mError = false;
	private int						NUM_ITEMS = 6;
	
	
	public static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance, R.layout.activity_edit_dive);
		mIndex = getIntent().getIntExtra("index", -1);
//		setContentView(R.layout.activity_edit_dive);
		mViewPager = (ViewPager) findViewById(R.id.vpPager);
		adapterViewPager = new EditPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(adapterViewPager);
		mViewPager.setOffscreenPageLimit(NUM_ITEMS);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
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
		mModel = ((ApplicationController)getApplicationContext()).getModel();

		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setTypeface(faceB);
		mTitle.setText(getResources().getString(R.string.tab_details_edit_title));

		Button save = (Button) findViewById(R.id.save_button);
		save.setTypeface(faceB);
		save.setText(getResources().getString(R.string.save_button));
		save.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{	
				mError = false;
				mModel = ((ApplicationController)getApplicationContext()).getModel();
				Dive dive = ((ApplicationController)getApplicationContext()).getModel().getDives().get(mIndex);
				ArrayList<Dive> dives = ((ApplicationController)getApplicationContext()).getModel().getDives();
				ArrayList<Pair<String, String>> editList = dive.getEditList();
				if (mNotes != null)
					dive.setNotes(mNotes.getText().toString());
				if(TabEditSpotsFragment.manualSpotActivated){
					Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.spot_missing), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();	
					mError = true;
				}
				if(!mError){
			
					if (editList != null && editList.size() > 0)
					{
						JSONObject edit = new JSONObject(); 
						for (int i = 0, size = editList.size(); i < size; i++)
							try {
								if (editList.get(i).first.equals("spot")){
									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
								}
									
								else if (editList.get(i).first.equals("shop"))
									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
								else
									edit.put(editList.get(i).first, editList.get(i).second);
							} catch (JSONException e) {
								e.printStackTrace();
							}
					}
					
					mModel.getDataManager().save(dive);
					((ApplicationController)getApplicationContext()).setRefresh(2);
					finish();
				}
			}
		}); 
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

	public class			EditPagerAdapter extends FragmentPagerAdapter
	{
		final ArrayList<String> titles = new ArrayList<String>();
		
		public EditPagerAdapter(android.support.v4.app.FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position)
		{
			switch (position)
			{
			case 0:
				return mEditDetailsFragment;
			case 1:
				return mEditSpotsFragment;
			case 2:
				return mEditShopFragment;
			case 3:
				return mEditBuddiesFragment;
			case 4:
				return mEditPhotosFragment;
			case 5:
				return mEditNotesFragment;
			default:
				return null;
			}
		}
		
		@Override
		public int getItemPosition(Object object) {
			Fragment fragment = (Fragment)object;
			
			if (object.equals(mEditDetailsFragment))
			{
				System.out.println("0");
				return 0;
			}
			else if (object.equals(mEditPhotosFragment))
			{
				System.out.println("2");
				return POSITION_NONE;
			}
			else if (object.equals(mEditNotesFragment))
			{
				System.out.println("3");
				return 3;
			}
			else if (object.equals(mEditSpotsFragment))
			{
				System.out.println("4");
				return 4;
			}
			return POSITION_NONE;	
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
	public void onStop() {

		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	//	
	//	/** Called when the activity is first created. */
	//	@Override
	//	public void onCreate(Bundle savedInstanceState)
	//	{
	//	    super.onCreate(savedInstanceState);
	//	    ApplicationController AC = (ApplicationController)getApplicationContext();
	//	    if (AC.handleLowMemory() == true)
	//			return ;
	//	    setContentView(R.layout.activity_edit_dive);
	//	
	//	    mFaceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
	//	    
	//	    mIndex = getIntent().getIntExtra("index", -1);
	//	
	//	    System.out.println("index " + Integer.toString(mIndex));
	//	    // create the TabHost that will contain the Tabs
	//	    mTabHost = (TabHost)findViewById(android.R.id.tabhost);
	//	
	//	    Intent intent = new Intent(this, TabEditDetailsActivity.class);
	//	    intent.putExtra("index", mIndex);
	//	    setupTab(new TextView(this), getResources().getString(R.string.tab_details_label), intent);
	//	    	    
	//	    intent = new Intent(this,TabEditNotesActivity.class);
	//	    intent.putExtra("index", mIndex);
	//	    setupTab(new TextView(this), getResources().getString(R.string.tab_notes_label), intent);
	//	
	//	    intent = new Intent(this,TabEditSpotsActivity.class);
	//	    intent.putExtra("index", mIndex);
	//	    setupTab(new TextView(this), getResources().getString(R.string.tab_spots_label), intent);
	//	}
	//	
	//	private void					setupTab(final View view, final String tag, final Intent content)
	//	{
	//		View tabview = createTabView(mTabHost.getContext(), tag);
	//		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
	//		mTabHost.addTab(setContent);
	//	}
	//	
	//	private View				createTabView(final Context context, final String text)
	//	{
	//		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
	//		TextView tv = (TextView) view.findViewById(R.id.tabsText);
	//		tv.setTypeface(mFaceB);
	//		tv.setTextSize(12);
	//		tv.setText(text);
	//		return view;
	//	}
	@Override
	public void onTripNameEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(8)).setValue(dive.getTripName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDiveNumberEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getNumber() != null)
			((EditOption)mOptionAdapter.getItem(6)).setValue(dive.getNumber().toString());
		else
			((EditOption)mOptionAdapter.getItem(6)).setValue("");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(0)).setValue(dive.getDate());
		String[] time_in = dive.getTimeIn().split("T");
		String new_time_in = dive.getDate() + "T" + time_in[1];
		dive.setTimeIn(new_time_in);
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		String[] time_in = dive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onMaxDepthEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		//((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName());
		String maxdepth_unit = "";
		if (dive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		else
			maxdepth_unit = (dive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(dive.getMaxdepth()) + " " + maxdepth_unit);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDurationEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(3)).setValue(Integer.toString(dive.getDuration()) + " " + getResources().getString(R.string.unit_min));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onSurfaceTempEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getTempSurface() == null)
			((EditOption)mOptionAdapter.getItem(11)).setValue("");
		else
		{
			//			((EditOption)mOptionAdapter.getItem(11)).setValue(Double.toString(dive.getTempSurface().getTemperature()) + " º" + dive.getTempSurface().getSmallName());
			String tempsurface_unit = "";
			if (dive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempsurface_unit = (dive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempsurface_unit.equals(getResources().getString(R.string.unit_C)))
				((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(dive.getTempSurface()) + " " + getResources().getString(R.string.unit_C_symbol));
			else
				((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(dive.getTempSurface()) + " " + getResources().getString(R.string.unit_F_symbol));
		}
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onBottomTempEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getTempBottom() == null)
			((EditOption)mOptionAdapter.getItem(13)).setValue("");
		else
		{
			String tempbottom_unit = "";
			if (dive.getTempBottomUnit() == null)
				tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempbottom_unit = (dive.getTempBottomUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempbottom_unit.equals(getResources().getString(R.string.unit_C)))
				((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(dive.getTempBottom()) + " " + getResources().getString(R.string.unit_C_symbol));
			else
				((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(dive.getTempBottom()) + " " + getResources().getString(R.string.unit_F_symbol));
		}
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWeightsEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getWeights() == null)
			((EditOption)mOptionAdapter.getItem(5)).setValue("");
		else
		{
			//			((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(dive.getWeights().getWeight()) + " " + dive.getWeights().getSmallName());
			String weights_unit = "";
			if (dive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			else
				weights_unit = (dive.getWeightsUnit().compareTo(getResources().getString(R.string.unit_kg)) == 0) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(dive.getWeights()) + " " + weights_unit);
		}
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onConfirmEditComplete(DialogFragment dialog)
	{
		clearEditList();
	}

	@Override
	public void onVisibilityEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getVisibility() == null)
			((EditOption)mOptionAdapter.getItem(10)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(10)).setValue(dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onCurrentEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getCurrent() == null)
			((EditOption)mOptionAdapter.getItem(11)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(11)).setValue(dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onAltitudeEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getAltitude() == null)
			((EditOption)mOptionAdapter.getItem(14)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(14)).setValue(Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWaterEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getWater() == null)
			((EditOption)mOptionAdapter.getItem(15)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(15)).setValue(dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onSafetyStopsEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		ArrayList<SafetyStop> safetystop = dive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit() + "-" + safetystop.get(i).getDuration().toString() + getResources().getString(R.string.unit_min);
		}
		((EditOption)mOptionAdapter.getItem(4)).setValue(safetydetails);
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDiveTypeEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		ArrayList<String> divetype = dive.getDivetype();
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
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(7)).setValue(dive.getGuide());
		mOptionAdapter.notifyDataSetChanged();
	}

	@Override
	public void onTanksEditComplete(DialogFragment dialog) {
		// TODO Auto-generated method stub
		ArrayList<Tank> mTanks = (ArrayList<Tank>) mModel.getDives().get(mIndex).getTanks().clone();
		String tankString = "";
		
		if (mTanks != null && mTanks.size() > 0 )
			tankString = mTanks.size() + " tanks used";
		
		((EditOption)mOptionAdapter.getItem(17)).setValue(tankString);
		mOptionAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onReviewEditComplete(DialogFragment dialog) {
		// TODO Auto-generated method stub
		Dive dive = mModel.getDives().get(mIndex);
		String fullReview = "";
		if (dive.getDiveReviews().getOverall()!= null)
			fullReview += "Overall: " + _getReviewHintGeneral(dive.getDiveReviews().getOverall()) + ". ";
		if (dive.getDiveReviews().getDifficulty()!= null)
			fullReview += "Dive difficulty: " + _getReviewHintDifficulty(dive.getDiveReviews().getDifficulty()).toLowerCase() + ". ";
		if (dive.getDiveReviews().getMarine()!= null)
			fullReview += "Marine life: " +_getReviewHintGeneral(dive.getDiveReviews().getMarine()).toLowerCase() + ". ";
		if (dive.getDiveReviews().getBigFish()!= null)
			fullReview += "Big fish sighted: " + _getReviewHintGeneral(dive.getDiveReviews().getBigFish()).toLowerCase() + ". ";
		if (dive.getDiveReviews().getWreck()!= null)
			fullReview += "Wrecks sighted: " + _getReviewHintGeneral(dive.getDiveReviews().getWreck()).toLowerCase() + ". ";
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

	
}