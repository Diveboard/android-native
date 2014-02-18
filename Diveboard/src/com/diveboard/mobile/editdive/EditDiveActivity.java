package com.diveboard.mobile.editdive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.astuetz.PagerSlidingTabStrip;
import com.diveboard.mobile.ApplicationController;
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
import com.diveboard.mobile.editdive.EditSafetyStopsDialogFragment.EditSafetyStopsDialogListener;
import com.diveboard.mobile.editdive.EditSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.editdive.EditTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.editdive.EditVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.editdive.EditWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.editdive.EditWeightsDialogFragment.EditWeightsDialogListener;
import com.diveboard.model.Buddy;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.FirstFragment;
import com.diveboard.model.Picture;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class					EditDiveActivity extends FragmentActivity implements EditTripNameDialogListener,
EditDiveNumberDialogListener,
EditDateDialogListener,
EditTimeInDialogListener,
EditMaxDepthDialogListener,
EditDurationDialogListener,
EditSurfaceTempDialogListener,
EditBottomTempDialogListener,
EditWeightsDialogListener,
EditVisibilityDialogListener,
EditCurrentDialogListener,
EditAltitudeDialogListener,
EditWaterDialogListener,
EditConfirmDialogListener,
EditSafetyStopsDialogListener,
EditDiveTypeDialogListener,
EditGuideNameDialogListener
{
	private int					mIndex;
	private Typeface			mFaceB;
	public static EditPagerAdapter	adapterViewPager;
	private TabEditDetailsFragment	mEditDetailsFragment = new TabEditDetailsFragment();
	public DiveboardModel		mModel;
	public static OptionAdapter		mOptionAdapter;
	private TextView			mTitle = null;
	private TabEditNotesFragment	mEditNotesFragment = new TabEditNotesFragment();
	private TabEditPhotosFragment	mEditPhotosFragment = new TabEditPhotosFragment();
	public static EditText			mNotes = null;
	private TabEditSpotsFragment	mEditSpotsFragment = new TabEditSpotsFragment(this);
	private TabEditShopFragment		mEditShopFragment = new TabEditShopFragment(this);
	private TabEditBuddiesFragment	mEditBuddiesFragment = new TabEditBuddiesFragment();
	private int		NUM_ITEMS = 6;
//	public static final int SELECT_PICTURE = 1;
//	public static final int TAKE_PICTURE = 2;
//	private Bitmap bitmap;
//	
//	public static ArrayList<Picture>		mListPictures = null;//new ArrayList<Picture>();
//	private Uri fileUri;
//	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
//	public static final int MEDIA_TYPE_IMAGE = 1;
//	public static final int MEDIA_TYPE_VIDEO = 2;
//	public static int count=0;
	//private UploadPictureTask mUploadPictureTask = null;
	
	
	public static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mIndex = getIntent().getIntExtra("index", -1);
		setContentView(R.layout.activity_edit_dive);
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
					mTitle.setText("SHOP");
					return ;
				case 3:
					mTitle.setText("BUDDIES");
					return ;
				case 4:
					mTitle.setText("PHOTOS");
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

		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
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
				mModel = ((ApplicationController)getApplicationContext()).getModel();
				Dive dive = ((ApplicationController)getApplicationContext()).getModel().getDives().get(mIndex);
				if (mNotes != null)
					dive.setNotes(mNotes.getText().toString());
				mModel.getDataManager().save(dive);
				((ApplicationController)getApplicationContext()).setRefresh(2);
				finish();
			}
		});
	}
	


//	@Override
//	protected void onPause() {
//		if (mUploadPictureTask != null)
//			mUploadPictureTask.cancel(true);
//		super.onPause();
//	}
//
//	private class UploadPictureTask extends AsyncTask<Void, Void, Picture>
//	{
//		private File mFile;
//
//		public UploadPictureTask(File file)
//		{
//			mFile = file;
//		}
//
//		@Override
//		protected Picture doInBackground(Void... arg0) {
//
//			Picture picture = mModel.uploadPicture(mFile);
//			return picture;
//		}
//
//		@Override
//		protected void onPostExecute(Picture result) {
//			System.out.println("TRUC de jean " + result.getJson());
//			//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
//			EditDiveActivity.mPhotoView.setVisibility(View.VISIBLE);
//			LinearLayout rl = (LinearLayout)(EditDiveActivity.mPhotoView.getParent());
//			ProgressBar bar = (ProgressBar)rl.getChildAt(1);
//			bar.setVisibility(View.GONE);
//			EditDiveActivity.mListPictures.add(result);
//			mModel.getDives().get(getIntent().getIntExtra("index", -1)).setPictures(EditDiveActivity.mListPictures);
//		}
//
//	}

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
//		if (mUploadPictureTask != null)
//			mUploadPictureTask.cancel(true);
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
				
//			else if (object.equals(mEditDetailsFragment))
//				return 1;
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
				return "Dive Details";
			case 1:
				return "Spot";
			case 2:
				return "Shop";
			case 3:
				return "Buddies";
			case 4:
				return "Photos";
			case 5:
				return "Notes";
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
	//	    mFaceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
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
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? "m" : "ft";
		else
			maxdepth_unit = (dive.getMaxdepthUnit().compareTo("m") == 0) ? "m" : "ft";
		((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(dive.getMaxdepth()) + " " + maxdepth_unit);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDurationEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(4)).setValue(Integer.toString(dive.getDuration()) + " min");
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
			//			((EditOption)mOptionAdapter.getItem(11)).setValue(Double.toString(dive.getTempSurface().getTemperature()) + " °" + dive.getTempSurface().getSmallName());
			String tempsurface_unit = "";
			if (dive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempsurface_unit = (dive.getTempSurfaceUnit().compareTo("C") == 0) ? "C" : "F";
			((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(dive.getTempSurface()) + " °" + tempsurface_unit);
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
				tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempbottom_unit = (dive.getTempBottomUnit().compareTo("C") == 0) ? "C" : "F";
			((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(dive.getTempBottom()) + " °" + tempbottom_unit);
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
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? "kg" : "lbs";
			else
				weights_unit = (dive.getWeightsUnit().compareTo("kg") == 0) ? "kg" : "lbs";
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
			safetydetails += safetystop.get(i).getDuration().toString() + "min" + "-" + safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit();
		}
		((EditOption)mOptionAdapter.getItem(3)).setValue(safetydetails);
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
}