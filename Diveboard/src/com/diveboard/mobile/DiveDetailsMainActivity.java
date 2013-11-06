package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.Picture;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class DiveDetailsMainActivity extends Activity {
	public final static int FONT_SIZE = 13;
	private Dive mDive;
	private DownloadImageTask mDownloadImageTask;
	private DownloadShopLogoTask mDownloadShopLogoTask;
	private DownloadGraphTask mDownloadGraphTask;
	private Bitmap mRoundedLayerSmall;
	private ImageView mPic;
	private ImageView mRoundedPic;
	private ImageView mShopLogo;
	
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	public void goToEditDive(View view)
	{
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		Intent editDiveActivity = new Intent(this, EditDiveActivity.class);
		editDiveActivity.putExtra("index", AC.getPageIndex());
	    startActivity(editDiveActivity);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dive_details_main);
		System.out.println(dpToPx(50));
		mRoundedLayerSmall = ImageHelper.getRoundedLayerSmallFix(dpToPx(35), dpToPx(35));
		Typeface faceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		mDive = AC.getModel().getDives().get(getIntent().getIntExtra("index", 0));
		if (mDive.getNotes() != null)
			((TextView)findViewById(R.id.dive_note)).setText(mDive.getNotes());
		else
			((TextView)findViewById(R.id.dive_note)).setText("No Note for this dive");
		((TextView)findViewById(R.id.dive_note)).setTypeface(faceR);
		((TextView)findViewById(R.id.dive_note)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.logged_by)).setText(AC.getModel().getUser().getNickname());
		((TextView)findViewById(R.id.logged_by)).setTypeface(faceB);
		((TextView)findViewById(R.id.logged_by)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		if (AC.getModel().getUser().getCountryName() != null)
			((TextView)findViewById(R.id.user_country)).setText(AC.getModel().getUser().getCountryName());
		else
			((TextView)findViewById(R.id.user_country)).setText("");
		((TextView)findViewById(R.id.user_country)).setTypeface(faceR);
		((TextView)findViewById(R.id.user_country)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((Button)findViewById(R.id.goToEditButton)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.dive_shop)).setTypeface(faceB);
		((TextView)findViewById(R.id.dive_shop)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		if (mDive.getShopName() != null)
		{
			((TextView)findViewById(R.id.shop_name)).setText(mDive.getShopName().toUpperCase());
			((TextView)findViewById(R.id.shop_name)).setTypeface(faceB);
			((TextView)findViewById(R.id.shop_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView)findViewById(R.id.shop_reviews)).setText("0 REVIEW");
			((TextView)findViewById(R.id.shop_reviews)).setTypeface(faceR);
			((TextView)findViewById(R.id.shop_reviews)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			mShopLogo = ((ImageView)findViewById(R.id.shop_image));
			mDownloadShopLogoTask = new DownloadShopLogoTask(mShopLogo);
			mDownloadShopLogoTask.execute();
		}	
		else
		{
			((TextView)findViewById(R.id.shop_name)).setText("No shop");
			((TextView)findViewById(R.id.shop_name)).setTypeface(faceR);
			((TextView)findViewById(R.id.shop_name)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		}
		((TextView)findViewById(R.id.depth_graph)).setTypeface(faceB);
		((TextView)findViewById(R.id.depth_graph)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		mDownloadGraphTask = new DownloadGraphTask(((ImageView)findViewById(R.id.graph)));
		mDownloadGraphTask.execute();
		((TextView)findViewById(R.id.date)).setTypeface(faceB);
		((TextView)findViewById(R.id.date)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.water)).setTypeface(faceB);
		((TextView)findViewById(R.id.water)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.duration)).setTypeface(faceB);
		((TextView)findViewById(R.id.duration)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.visibility)).setTypeface(faceB);
		((TextView)findViewById(R.id.visibility)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.temp)).setTypeface(faceB);
		((TextView)findViewById(R.id.temp)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.dive_type)).setTypeface(faceB);
		((TextView)findViewById(R.id.dive_type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.date_content)).setTypeface(faceR);
		((TextView)findViewById(R.id.date_content)).setText(mDive.getDate() + " " + mDive.getTime());
		((TextView)findViewById(R.id.date_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.water_content)).setTypeface(faceR);
		((TextView)findViewById(R.id.water_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.water_content)).setText(mDive.getWater());
		((TextView)findViewById(R.id.duration_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.duration_content)).setTypeface(faceR);
		((TextView)findViewById(R.id.duration_content)).setText(mDive.getDuration() + "mins");
		((TextView)findViewById(R.id.visibility_content)).setTypeface(faceR);
		((TextView)findViewById(R.id.visibility_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.visibility_content)).setText(mDive.getVisibility());
		((TextView)findViewById(R.id.temp_content)).setTypeface(faceR);
		((TextView)findViewById(R.id.temp_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		String temp;
		if (mDive.getTempSurface() != null)
			temp = "SURF " + mDive.getTempSurface().getTemperature() + "°" + mDive.getTempSurface().getSmallName();
		else
			temp = "null";
		temp += " | ";
		if (mDive.getTempBottom() != null)
			temp += "BOTTOM " + mDive.getTempBottom().getTemperature() + "°" + mDive.getTempBottom().getSmallName();
		else
			temp += "null";
		((TextView)findViewById(R.id.temp_content)).setText(temp);
		((TextView)findViewById(R.id.dive_type_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView)findViewById(R.id.dive_type_content)).setTypeface(faceR);
		String type = "";
		for (int i = 0; i < mDive.getDivetype().size(); i++)
		{	
			if (mDive.getDivetype().get(i) != null)
				type += mDive.getDivetype().get(i) + ", ";
		}
		if (type != "")
			type = (String) type.subSequence(0, type.length() - 2);
		else
			type = "Null";
		((TextView)findViewById(R.id.dive_type_content)).setText(type);
		mPic = ((ImageView)findViewById(R.id.profile_image));
		mRoundedPic = ((ImageView)findViewById(R.id.main_image_cache));
		mDownloadImageTask = new DownloadImageTask(mPic);
		mDownloadImageTask.execute();	
	}
	
	private class DownloadShopLogoTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;
		
		public DownloadShopLogoTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (mDive.getShopPicture() != null)
					return mDive.getShopPicture().getPicture(DiveDetailsMainActivity.this);
				//return mDive.getShop().getLogo().getPicture(DiveDetailsMainActivity.this);
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
					mShopLogo.setImageBitmap(result);
					mShopLogo.setScaleType(ScaleType.CENTER_CROP);
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}
	
	private class DownloadGraphTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		
		public DownloadGraphTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (DiveDetailsMainActivity.this != null)
				{
					return mDive.getProfile().getPicture(getApplicationContext());
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
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadGraphTask = null;
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
				if (DiveDetailsMainActivity.this != null)
				{
					ApplicationController AC = ((ApplicationController)getApplicationContext());
					return AC.getModel().getUser().getPictureSmall().getPicture(getApplicationContext());
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
					mRoundedPic.setImageBitmap(mRoundedLayerSmall);
					imageView.setScaleType(ScaleType.CENTER_CROP);
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}

}
