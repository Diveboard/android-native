package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.Picture;
import com.diveboard.model.ScreenSetup;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * The fragment that displays the "fragment_dives.xml" layout
 */
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
	
	public DivesFragment()
	{
		//System.out.println("Entre");
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
		if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			System.out.println("CREATION FRAGMENT");
	        // Inflate the layout for this fragment
			ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dives, container, false);
			mFragment = (LinearLayout)rootView.findViewById(R.id.fragment);
			RelativeLayout.LayoutParams fragment_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentBodyHeight());
			fragment_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1() + mScreenSetup.getDiveListFragmentBannerHeight(), 0, 0);
			mFragment.setLayoutParams(fragment_params);
			// Resize the dimensions of each fragment
			RelativeLayout.LayoutParams banner_params = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentBannerHeight());
			mFragmentBannerHeight = (RelativeLayout)rootView.findViewById(R.id.fragment_banner_height);//(RelativeLayout)mFragment.findViewById(R.id.fragment_banner_height);
			banner_params.setMargins(0, mScreenSetup.getDiveListWhiteSpace1(), 0, 0);
			mFragmentBannerHeight.setLayoutParams(banner_params);
			
			mMainImage = (ImageView)mFragment.findViewById(R.id.main_image);	
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
			params1.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
			mMainImage.setLayoutParams(params1);
			
			mMainImageCache = (ImageView)mFragment.findViewById(R.id.main_image_cache);		
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentOutCircleRadius());
			params2.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace1(), 0, 0);
			mMainImageCache.setLayoutParams(params2);
			mMainImageCache.setImageBitmap(mRoundedLayer);
			
			LinearLayout.LayoutParams fragment_body_title_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentOutCircleRadius(), mScreenSetup.getDiveListFragmentBodyTitle());
			fragment_body_title_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace2(), 0, 0);
			mFragmentBodyTitle = (RelativeLayout)rootView.findViewById(R.id.fragment_body_title);
			mFragmentBodyTitle.setLayoutParams(fragment_body_title_params);
			
			LinearLayout.LayoutParams fragment_picture_circle_radius_params = new LinearLayout.LayoutParams(mScreenSetup.getDiveListFragmentWidth(), mScreenSetup.getDiveListFragmentPictureCircleRadius());
			fragment_picture_circle_radius_params.setMargins(0, mScreenSetup.getDiveListFragmentWhitespace3(), 0, 0);
			mFragmentPictureCircleRadius = (RelativeLayout)rootView.findViewById(R.id.fragment_picture_circle_radius);
			mFragmentPictureCircleRadius.setLayoutParams(fragment_picture_circle_radius_params);
			//Set the content of the fragments
			Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
			Typeface faceB = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
			if (mDive.getTripName() != null)
			{
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setText("TRIP NAME:");
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name)).setTypeface(faceR);
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setText(mDive.getTripName().toUpperCase());
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
				((TextView) mFragmentBannerHeight.findViewById(R.id.trip_name2)).setTypeface(faceB);
			}
			else
				mFragmentBannerHeight.setVisibility(View.INVISIBLE);
			((TextView) mFragment.findViewById(R.id.dive_name)).setText(mDive.getSpot().getName().toUpperCase());
			((TextView) mFragment.findViewById(R.id.dive_name)).setTypeface(faceB);
			((TextView) mFragment.findViewById(R.id.dive_name)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 35 / 100));
			((TextView) mFragment.findViewById(R.id.dive_place)).setText(mDive.getSpot().getCountryName() + " - " + mDive.getSpot().getLocationName());
			((TextView) mFragment.findViewById(R.id.dive_place)).setTypeface(faceB);
			((TextView) mFragment.findViewById(R.id.dive_place)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			((TextView) mFragment.findViewById(R.id.dive_date)).setText(mDive.getDate());
			((TextView) mFragment.findViewById(R.id.dive_date)).setTypeface(faceR);
			((TextView) mFragment.findViewById(R.id.dive_date)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			((TextView) mFragment.findViewById(R.id.dive_duration)).setText(String.valueOf(mDive.getDuration()) + "MINS");
			((TextView) mFragment.findViewById(R.id.dive_duration)).setTypeface(faceR);
			((TextView) mFragment.findViewById(R.id.dive_duration)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));
			((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setText(String.valueOf(mDive.getMaxdepth().getDistance()) + " " + mDive.getMaxdepth().getFullName().toUpperCase());
			((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTypeface(faceR);
			((TextView) mFragment.findViewById(R.id.dive_maxdepth)).setTextSize(TypedValue.COMPLEX_UNIT_PX, (mScreenSetup.getDiveListFragmentBannerHeight() * 25 / 100));	
			// Downloading the main picture in a new thread
			//mProgressBarMain = (ProgressBar)mContent.findViewById(R.id.progress_bar_main);
			//showProgress(true);
			mDownloadImageTask = new DownloadImageTask((ImageView)mFragment.findViewById(R.id.main_image));
			mDownloadImageTask.execute();
			mDownloadSmallImageTask = new DownloadSmallImageTask();
			mDownloadSmallImageTask.execute();
			return rootView;
		}
		return (ViewGroup) inflater.inflate(R.layout.fragment_dives, container, false);
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
						return mDive.getFeaturedPicture().getPicture(getActivity().getApplicationContext(), Picture.Size.MEDIUM);
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
			//showProgress(false);
			//mFragment.setAlpha(1);
			if (imageViewReference != null && result != null)
			{
				final ImageView imageView = imageViewReference.get();
				if (imageView != null)
				{
					imageView.setImageBitmap(result);
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
						//System.out.println("picture " + i);
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
						image_params.setMargins(10, 0, 10, 0);
						rounded_image_params.setMargins(10, 0, 10, 0);
						pic.setLayoutParams(image_params);
						round_pic.setLayoutParams(rounded_image_params);
						mSmallImage.add(pic);
						mSmallImage.add(round_pic);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//mFragmentPictureCircleRadius.addView(pic);
					//mFragmentPictureCircleRadius.addView(round_pic);
				}
			}
			else
				System.out.println("DownloadSmallImageTask is NULL");
			return mSmallImage;
		}
		
		protected void onPostExecute(ArrayList<ImageView> result)
		{
			if (result != null)
			{
				for (int i = 0; i < result.size(); i++)
				{
					((RelativeLayout)mFragment.findViewById(R.id.fragment_picture_circle_radius)).addView(result.get(i));
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
