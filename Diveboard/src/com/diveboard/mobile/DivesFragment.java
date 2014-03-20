package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.model.Converter;
import com.diveboard.model.Dive;
import com.diveboard.model.Picture;
import com.diveboard.model.ScreenSetup;
import com.diveboard.model.Spot;
import com.diveboard.model.Units;
import com.diveboard.model.Utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The fragment that displays the "fragment_dives.xml" layout
 */
@SuppressLint("ValidFragment")
public class DivesFragment extends Fragment {
	private Dive mDive;
	private Bitmap mRoundedLayer;
	private Bitmap mRoundedLayerSmall;
	private ImageView mMainImage;
	private ProgressBar mProgressBarMain;
	private DownloadImageTask mDownloadImageTask;
	private DownloadSmallImageTask mDownloadSmallImageTask;
	private LinearLayout mFragment;
	private RelativeLayout mFragmentBannerHeight;
	private ScreenSetup mScreenSetup;
	private ImageView mMainImageCache;
	private RelativeLayout mFragmentBodyTitle;
	private RelativeLayout mFragmentPictureCircleRadius;
	private ImageView mUserImage;
	private RelativeLayout mFragmentProfile;
	private LinearLayout mFragmentProfileInfo;
	private LinearLayout mImageLayout;
	private LinearLayout mContentLayout;
	
	public DivesFragment()
	{
	}
	
	public DivesFragment(Dive dive, ScreenSetup screenSetup, Bitmap rounded_layer, Bitmap rounded_layer_small)
	{
		mDive = dive;
		mScreenSetup = screenSetup;
		mRoundedLayer = rounded_layer;
		mRoundedLayerSmall = rounded_layer_small;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
		// Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dives, container, false);
		mFragment = (LinearLayout)rootView.findViewById(R.id.fragment);
		mFragmentProfile = (RelativeLayout)rootView.findViewById(R.id.fragment_profile);
		//Fragment title creation
		LinearLayout.LayoutParams fragment_body_title_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentBodyTitle());
		fragment_body_title_params.gravity = Gravity.CENTER_HORIZONTAL;
		fragment_body_title_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace2(), 0, 0);
		mFragmentBodyTitle = (RelativeLayout)rootView.findViewById(R.id.fragment_body_title);
		mFragmentBodyTitle.setLayoutParams(fragment_body_title_params);
		//Fragment user profile data creation
		ApplicationController AC = ((ApplicationController)getActivity().getApplicationContext());
		((TextView) mFragmentProfile.findViewById(R.id.logged_by)).setText(getResources().getString(R.string.logged_by) + ":");
		((TextView) mFragmentProfile.findViewById(R.id.logged_by)).setTypeface(faceB);
		((TextView) mFragmentProfile.findViewById(R.id.logged_by)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListProfileBoxHeight() * 20 / 100));
		if (AC.getModel().getUser().getNickname() != null)
		{
			((TextView) mFragmentProfile.findViewById(R.id.user_name)).setText(AC.getModel().getUser().getNickname());
			((TextView) mFragmentProfile.findViewById(R.id.user_name)).setTypeface(faceR);
			((TextView) mFragmentProfile.findViewById(R.id.user_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListProfileBoxHeight() * 35 / 100));
		}
		mUserImage = (ImageView)mFragmentProfile.findViewById(R.id.profile_image);
        mFragmentProfileInfo = (LinearLayout)rootView.findViewById(R.id.profile_info);
        RelativeLayout.LayoutParams fragment_profile_info_params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        fragment_profile_info_params.setMargins(mScreenSetup.getDiveListFragmentWhitespace2(), 0, 0, 0);
        fragment_profile_info_params.addRule(RelativeLayout.RIGHT_OF, R.id.profile_image);
        fragment_profile_info_params.addRule(RelativeLayout.CENTER_VERTICAL);
        mFragmentProfileInfo.setLayoutParams(fragment_profile_info_params);
		//if we are in portrait mode - We create this condition because some content of the portrait and landscape modes cannot be factorized
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			//Setting the fragment parameters
			RelativeLayout.LayoutParams fragment_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentBodyHeight());
			fragment_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1() + mScreenSetup.getDiveListFragmentBannerHeight(), 0, 0);
			mFragment.setLayoutParams(fragment_params);
			// Resize the dimensions of each fragment
			RelativeLayout.LayoutParams banner_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentBannerHeight());
			mFragmentBannerHeight = (RelativeLayout)rootView.findViewById(R.id.fragment_banner_height);
			banner_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1(), 0, 0);
			mFragmentBannerHeight.setLayoutParams(banner_params);
			//Fragment big picture creation
			mMainImage = (ImageView)mFragment.findViewById(R.id.main_image);	
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
			params1.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
			mMainImage.setLayoutParams(params1);
			//Fragment big picture with rounded layer creation
			mMainImageCache = (ImageView)mFragment.findViewById(R.id.main_image_cache);		
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
			params2.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
			mMainImageCache.setLayoutParams(params2);
			mMainImageCache.setImageBitmap(mRoundedLayer);
			//Fragment small pictures creation
			LinearLayout.LayoutParams fragment_picture_circle_radius_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentPictureCircleRadius());
			fragment_picture_circle_radius_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace3(), 0, 0);
			mFragmentPictureCircleRadius = (RelativeLayout)rootView.findViewById(R.id.fragment_picture_circle_radius);
			mFragmentPictureCircleRadius.setLayoutParams(fragment_picture_circle_radius_params);
			//Set the logged profile
	        RelativeLayout.LayoutParams fragment_profile_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListProfileBoxWidth(), mScreenSetup.getDiveListProfileBoxHeight());
	        RelativeLayout.LayoutParams user_image_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListProfileBoxHeight(), mScreenSetup.getDiveListProfileBoxHeight());
	        fragment_profile_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	        //fragment_profile_params.setMargins(0, 0, 0, mScreenSetup.getDiveListSeekBarHeight() + mScreenSetup.getDiveListWhiteSpace3() + mScreenSetup.getDiveListWhiteSpace4());
	        mFragmentProfile.setLayoutParams(fragment_profile_params);
	        mUserImage.setLayoutParams(user_image_params);
	        DownloadProfileImageTask profile_task = new DownloadProfileImageTask();
	        profile_task.execute(AC.getModel().getDives().size() - AC.getPageIndex() - 1);	
		}
		else // Landscape mode
		{
			//Setting the fragment parameters
			RelativeLayout.LayoutParams fragment_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentHeight());
			fragment_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1(), 0, 0);//			
			mFragment.setLayoutParams(fragment_params);
			// Resize the banner
			RelativeLayout.LayoutParams banner_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth() - mScreenSetup.getDiveListFragmentHeight(), mScreenSetup.getDiveListFragmentBannerHeight());
			mFragmentBannerHeight = (RelativeLayout)rootView.findViewById(R.id.fragment_banner_height);
			banner_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1(), 0, 0);
			banner_params.leftMargin = mScreenSetup.getDiveListFragmentHeight();
			mFragmentBannerHeight.setLayoutParams(banner_params);
			// Set the main picture (left part)
			mMainImage = (ImageView)mFragment.findViewById(R.id.main_image);
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentHeight(), mScreenSetup.getDiveListFragmentHeight());//
			mMainImage.setLayoutParams(params1);
			// Set the info fragment (right part)
			mImageLayout = (LinearLayout)mFragment.findViewById(R.id.image_layout);
			mImageLayout.setLayoutParams(new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentHeight(), mScreenSetup.getDiveListFragmentHeight()));
			mContentLayout = (LinearLayout)mFragment.findViewById(R.id.content_layout);
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth() - mScreenSetup.getDiveListFragmentHeight(), mScreenSetup.getDiveListFragmentHeight() - mScreenSetup.getDiveListFragmentBannerHeight());
			params2.gravity = Gravity.BOTTOM;
			mContentLayout.setLayoutParams(params2);
			//Fragment small pictures creation
			LinearLayout.LayoutParams fragment_picture_circle_radius_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth() - mScreenSetup.getDiveListFragmentHeight(), mScreenSetup.getDiveListFragmentPictureCircleRadius());
			fragment_picture_circle_radius_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace3(), 0, mScreenSetup.getDiveListWhiteSpace2());
			mFragmentPictureCircleRadius = (RelativeLayout)rootView.findViewById(R.id.fragment_picture_circle_radius);
			mFragmentPictureCircleRadius.setLayoutParams(fragment_picture_circle_radius_params);
			//Set the logged profile
	        LinearLayout.LayoutParams fragment_profile_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListProfileBoxWidth(), mScreenSetup.getDiveListProfileBoxHeight());
	        RelativeLayout.LayoutParams user_image_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListProfileBoxHeight(), mScreenSetup.getDiveListProfileBoxHeight());
	        fragment_profile_params.gravity = Gravity.BOTTOM;
	        fragment_profile_params.setMargins(0, 0, 0, mScreenSetup.getDiveListFragmentWhitespace4());
	        mFragmentProfile.setLayoutParams(fragment_profile_params);
	        mUserImage.setLayoutParams(user_image_params);
	        DownloadProfileImageTask profile_task = new DownloadProfileImageTask();
	        profile_task.execute(AC.getModel().getDives().size() - AC.getPageIndex() - 1);
		}
		//Set the banner content
		if (mDive.getTripName() != null)
		{
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setText(getResources().getString(R.string.trip_name_label).toUpperCase() + ":");
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setTypeface(faceR);
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setText(mDive.getTripName().toUpperCase());
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setTypeface(faceB);
		}
		else
			mFragmentBannerHeight.setVisibility(View.INVISIBLE);
		
		//Set the title details content
		//For spots that already existed and had an ID differnet than 1 (no spot) and those recently created (ID not created yet)
		if (mDive.getSpot() != null && (mDive.getSpot().getId() == null || (mDive.getSpot().getId() != null && mDive.getSpot().getId() != 1)))
		{
			String mDivePlace = "";
			if(mDive.getSpot().getCountryName() != null){
				mDivePlace += mDive.getSpot().getCountryName();
			}
			if(mDive.getSpot().getLocationName() != null){
				if(mDivePlace.trim().isEmpty())
					mDivePlace += mDive.getSpot().getLocationName();
				else
					mDivePlace += " - " + mDive.getSpot().getLocationName();
			}
			((TextView) mFragment.findViewById(R.id.dive_place)).setText(mDivePlace);	
			((TextView) mFragment.findViewById(R.id.dive_place)).setTypeface(faceB);
			((TextView) mFragment.findViewById(R.id.dive_place)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			
			if(mDive.getSpot().getName()!= null)
				((TextView) mFragment.findViewById(R.id.dive_name)).setText(mDive.getSpot().getName().toUpperCase());
			((TextView) mFragment.findViewById(R.id.dive_name)).setTypeface(faceB);
			((TextView) mFragment.findViewById(R.id.dive_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 35 / 100));
		}
				
		((TextView) mFragment.findViewById(R.id.dive_date)).setText(mDive.getDate());
		((TextView) mFragment.findViewById(R.id.dive_date)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_date)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
		((TextView) mFragment.findViewById(R.id.dive_duration)).setText(String.valueOf(mDive.getDuration()) + getResources().getString(R.string.unit_mins).toUpperCase());
		((TextView) mFragment.findViewById(R.id.dive_duration)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_duration)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
		//((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setText(String.valueOf(mDive.getMaxdepth().getDistance()) + " " + mDive.getMaxdepth().getFullName().toUpperCase());
		String maxdepth_unit = "";
		maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.meters).toUpperCase() : getResources().getString(R.string.feet).toUpperCase();
		Double maxdepth_value = 0.0;
		if (mDive.getMaxdepth() != null && mDive.getMaxdepthUnit() != null)
		{
			if (Units.getDistanceUnit() == Units.Distance.KM)
				maxdepth_value = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? mDive.getMaxdepth() : Utils.round(Converter.convert(mDive.getMaxdepth(), Units.Distance.FT, Units.Distance.KM), 2);
			else
				maxdepth_value = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_ft)) == 0) ? mDive.getMaxdepth() : Utils.round(Converter.convert(mDive.getMaxdepth(), Units.Distance.KM, Units.Distance.FT), 2);
		}
//		if (mDive.getMaxdepthUnit() == null)
//			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? "METERS" : "FEET";
//		else
//			maxdepth_unit = (mDive.getMaxdepthUnit().compareTo("m") == 0) ? "METERS" : "FEET";
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setText(maxdepth_value + " " + maxdepth_unit);
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTypeface(faceR);
		((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
		//Threads for the pictures
		mDownloadImageTask = new DownloadImageTask(mMainImage);
		mDownloadImageTask.execute();
		mDownloadSmallImageTask = new DownloadSmallImageTask();
		mDownloadSmallImageTask.execute();
		return rootView;
    }
	
	/**
	 * Represents an asynchronous task used to load user profile image
	 */
	private class DownloadProfileImageTask extends AsyncTask<Integer, Void, Bitmap> {
		private Integer mItemNb;
		@Override
		protected Bitmap doInBackground(Integer... params) {
			mItemNb = params[0];
			Bitmap result = null;
			try {
				if (getActivity() != null)
				{
					ApplicationController AC = ((ApplicationController)getActivity().getApplicationContext());
					result = AC.getModel()
							.getUser()
							.getPictureSmall()
							.getPicture(getActivity()
									.getApplicationContext());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result != null)
			{
				return result;
			}
			return null;
		}

		@Override
		protected void onPostExecute(final Bitmap result) {
			mUserImage.setImageBitmap(result);
		}
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mProgressBarMain.setVisibility(View.VISIBLE);
			mProgressBarMain.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressBarMain.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mMainImage.setVisibility(View.VISIBLE);
			mMainImage.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mMainImage.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressBarMain.setVisibility(show ? View.VISIBLE : View.GONE);
			mMainImage.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;
		
		public DownloadImageTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (getActivity() != null)
				{
					if (mDive.getFeaturedPicture() != null)
					{
						isPicture = true;
						return mDive.getFeaturedPicture().getPicture(getActivity().getApplicationContext(), Picture.Size.MEDIUM);
					}
					isPicture = false;
					if (mDive.getThumbnailImageUrl() != null)
						return mDive.getThumbnailImageUrl().getPicture(getActivity().getApplicationContext());
				}
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Bitmap result)
		{
			if (imageViewReference != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (result != null && imageView != null)
				{	
					imageView.setImageBitmap(result);
					imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
							if (AC.getModel().getDives().get(AC.getModel().getDives().size() - AC.getPageIndex() - 1).getPictures().size() != 0)
							{
								Intent galleryCarousel = new Intent(getActivity(), GalleryCarouselActivity.class);
								galleryCarousel.putExtra("index", AC.getModel().getDives().size() - AC.getPageIndex() - 1);
								startActivity(galleryCarousel);
							}
							else
							{
								Intent diveDetailsActivity = new Intent(getActivity(), DiveDetailsActivity.class);
								diveDetailsActivity.putExtra("index", AC.getModel().getDives().size() - AC.getPageIndex() - 1);
								startActivity(diveDetailsActivity);
							}
						}
					});
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
			showProgress(false);
		}
	}
	
	private class DownloadSmallImageTask extends AsyncTask<Void, Void, ArrayList<ImageView>>
	{
		private ArrayList<ImageView> mSmallImage = new ArrayList<ImageView>();
		
		protected ArrayList<ImageView> doInBackground(Void... voids)
		{
			//Adding small pictures
			if (mDive.getPictures() != null)
			{
				for (int i = 1; getActivity() != null && i < mDive.getPictures().size() && i < 6; i++)
				{
					try {
						ImageView pic = new ImageView(getActivity().getApplicationContext());
						ImageView round_pic = new ImageView(getActivity().getApplicationContext());
						pic.setId(i);
						pic.setImageBitmap(mDive.getPictures().get(i).getPicture(getActivity().getApplicationContext(), Picture.Size.SMALL));
						round_pic.setImageBitmap(mRoundedLayerSmall);
						pic.setScaleType(ScaleType.CENTER_CROP);
						RelativeLayout.LayoutParams image_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentPictureCircleRadius(), mScreenSetup.getDiveListFragmentPictureCircleRadius());
						RelativeLayout.LayoutParams rounded_image_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentPictureCircleRadius(), mScreenSetup.getDiveListFragmentPictureCircleRadius());
						if (i > 1)
						{
							image_params.addRule(RelativeLayout.RIGHT_OF, i - 1);
							rounded_image_params.addRule(RelativeLayout.RIGHT_OF, i - 1);
						}
						image_params.setMargins(mScreenSetup.getDiveListSmallPictureMargin(), 0, mScreenSetup.getDiveListSmallPictureMargin(), 0);
						rounded_image_params.setMargins(mScreenSetup.getDiveListSmallPictureMargin(), 0, mScreenSetup.getDiveListSmallPictureMargin(), 0);
						pic.setLayoutParams(image_params);
						round_pic.setLayoutParams(rounded_image_params);
						mSmallImage.add(pic);
						mSmallImage.add(round_pic);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return mSmallImage;
		}
		
		protected void onPostExecute(ArrayList<ImageView> result)
		{
			if (result != null)
			{
				for (int i = 0; i < result.size(); i++)
				{
					((RelativeLayout)mFragment.findViewById(R.id.fragment_picture_circle_radius)).addView(result.get(i));
					//System.out.println(result.size());
//					ApplicationController AC = ((ApplicationController)getActivity().getApplicationContext());
//					Intent diveDetailsActivity = new Intent(getActivity(), DiveDetailsActivity.class);
//					diveDetailsActivity.putExtra("index", AC.getPageIndex());
//					startActivity(diveDetailsActivity);
//					if (getActivity() != null && (result.size() == 0 || getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE))
//					{
//						((RelativeLayout)mFragment.findViewById(R.id.fragment_picture_circle_radius)).setOnClickListener(new OnClickListener()
//						{
//							@Override
//							public void onClick(View v) {
//								ApplicationController AC = ((ApplicationController)getActivity().getApplicationContext());
//								Intent diveDetailsActivity = new Intent(getActivity(), DiveDetailsActivity.class);
//								diveDetailsActivity.putExtra("index", AC.getPageIndex());
//								startActivity(diveDetailsActivity);
//							}
//						});
//					}
//					else
//					{
//						((RelativeLayout)mFragment.findViewById(R.id.fragment_picture_circle_radius)).setOnClickListener(new OnClickListener()
//						{
//							@Override
//							public void onClick(View v) {
//								ApplicationController AC = ((ApplicationController)getActivity().getApplicationContext());
//								Intent galleryCarousel = new Intent(getActivity(), GalleryCarouselActivity.class);
//								galleryCarousel.putExtra("index", AC.getPageIndex());
//								startActivity(galleryCarousel);
//							}
//						});
//					}
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
			showProgress(false);
		}
	}
}
