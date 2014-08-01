package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.diveboard.mobile.editdive.DeleteConfirmDialogFragment;
import com.diveboard.mobile.editdive.DeleteConfirmDialogFragment.DeleteConfirmDialogListener;
import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.model.Buddy;
import com.diveboard.model.Converter;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveDeleteListener;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;
import com.diveboard.model.Utils;
import com.diveboard.util.EditDialog;
import com.diveboard.util.ImageCache.ImageCacheParams;
import com.diveboard.util.ImageFetcher;

public class DiveDetailsMainActivity extends FragmentActivity implements
DeleteConfirmDialogListener {
	public final static int FONT_SIZE = 13;
	private Dive mDive;
	private DownloadImageTask mDownloadImageTask;
	private DownloadShopLogoTask mDownloadShopLogoTask;
	private DownloadGraphTask mDownloadGraphTask;
	private Bitmap mRoundedLayerSmall;
	private ImageView mPic;
	private ImageView mRoundedPic;
	private ImageView mShopLogo;
	private RatingBar mOverall, mDifficulty, mFish, mLife, mWreck;

	private int mImageThumbSize;
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "thumbs";
	private LinearLayout mListBuddyPictures;
	private LinearLayout mListTanks;

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public void goToEditDive(View view) {
		ApplicationController AC = ((ApplicationController) getApplicationContext());
		Intent editDiveActivity = new Intent(this, EditDiveActivity.class);
		editDiveActivity.putExtra("index", getIntent().getIntExtra("index", 0));
		startActivity(editDiveActivity);
		finish();
	}

	public void goToDeleteDive() {
		ApplicationController AC = ((ApplicationController) getApplicationContext());
		WaitDialogFragment dialog = new WaitDialogFragment();
		dialog.show(getSupportFragmentManager(), "WaitDialogFragment");
		AC.setRefresh(3);
		AC.getModel().getDataManager().setOnDiveDeleteComplete(new DiveDeleteListener() {
			@Override
			public void onDiveDeleteComplete() {
				finish();
			}
		});
		AC.getModel().getDataManager().delete(AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)));
	}

	public void goToURL(View view) {
		if (mDive.getFullpermalink() != null) {
			String url = mDive.getFullpermalink().toString();
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
		}
	}

	public void openGraph(View view) {
		ApplicationController AC = ((ApplicationController) getApplicationContext());
		Intent graphImageActivity = new Intent(this, GraphImageActivity.class);
		graphImageActivity.putExtra("index", getIntent()
				.getIntExtra("index", 0));
		startActivity(graphImageActivity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController) getApplicationContext();

		setContentView(R.layout.activity_dive_details_main);
		// System.out.println(dpToPx(50));
		mRoundedLayerSmall = ImageHelper.getRoundedLayerSmallFix(dpToPx(35),dpToPx(35));
		Typeface faceR = Typeface.createFromAsset(getAssets(),"fonts/Lato-Light.ttf");
		Typeface faceB = Typeface.createFromAsset(getAssets(),"fonts/Lato-Regular.ttf");
		mDive = AC.getModel().getDives().get(getIntent().getIntExtra("index", 0));
		if (mDive.getNotes() != null)
			((TextView) findViewById(R.id.dive_note)).setText(mDive.getNotes());
		else
			((TextView) findViewById(R.id.dive_note))
			.setText(getResources().getString(R.string.no_note));
		if (mDive.getFullpermalink() == null || mDive.getFullpermalink() == "")
			((TextView) findViewById(R.id.dive_url)).setText("");
		((TextView) findViewById(R.id.dive_url)).setPaintFlags(((TextView) findViewById(R.id.dive_url)).getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		((TextView) findViewById(R.id.dive_note)).setTypeface(faceR);
		((TextView) findViewById(R.id.dive_note)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.logged_by)).setText(AC.getModel().getUser().getNickname());
		((TextView) findViewById(R.id.logged_by)).setTypeface(faceB);
		((TextView) findViewById(R.id.logged_by)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		if (AC.getModel().getUser().getCountryName() != null)
			((TextView) findViewById(R.id.user_country)).setText(AC.getModel().getUser().getCountryName());
		else
			((TextView) findViewById(R.id.user_country)).setText("");
		((TextView) findViewById(R.id.user_country)).setTypeface(faceR);
		((TextView) findViewById(R.id.user_country)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((Button) findViewById(R.id.deleteDiveButton)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((Button) findViewById(R.id.deleteDiveButton)).setTypeface(faceB);
		((Button) findViewById(R.id.deleteDiveButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeleteConfirmDialogFragment dialog = new DeleteConfirmDialogFragment();
				Bundle args = new Bundle();
				dialog.show(getSupportFragmentManager(),
						"DeleteConfirmDialogFragment");
			}
		});
		((Button) findViewById(R.id.goToEditButton)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((Button) findViewById(R.id.goToEditButton)).setTypeface(faceB);
		((TextView) findViewById(R.id.dive_shop)).setTypeface(faceB);
		((TextView) findViewById(R.id.dive_shop)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		if (mDive.getShopName() != null) {
			((TextView) findViewById(R.id.shop_name)).setText(mDive
					.getShopName().toUpperCase());
			((TextView) findViewById(R.id.shop_name)).setTypeface(faceB);
			((TextView) findViewById(R.id.shop_name)).setTextSize(
					TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView)findViewById(R.id.shop_review)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final ReviewDialog d = new ReviewDialog(getParent());
					d.setPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					d.setNegativeListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					d.show();
				}
			});
//			((TextView)findViewById(R.id.shop_review)).setVisibility(View.VISIBLE);
			// ((TextView)findViewById(R.id.shop_reviews)).setTypeface(faceR);
			// ((TextView)findViewById(R.id.shop_reviews)).setTextSize(TypedValue.COMPLEX_UNIT_SP,
			// FONT_SIZE);
			mShopLogo = ((ImageView) findViewById(R.id.shop_image));
			mDownloadShopLogoTask = new DownloadShopLogoTask(mShopLogo);
			mDownloadShopLogoTask.execute();
		} else {
			((TextView) findViewById(R.id.shop_name)).setText(getResources().getString(R.string.no_shop));
			((TextView) findViewById(R.id.shop_name)).setTypeface(faceR);
			((TextView) findViewById(R.id.shop_name)).setTextSize(
					TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		}
		((TextView) findViewById(R.id.depth_graph)).setTypeface(faceB);
		((TextView) findViewById(R.id.depth_graph)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.max_depth_title)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.max_depth_title)).setText(getResources().getString(R.string.max_depth_label).toUpperCase() + ": ");
		((TextView) findViewById(R.id.max_depth_title)).setTypeface(faceB);
		((TextView) findViewById(R.id.max_depth)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		// ((TextView)
		// findViewById(R.id.max_depth)).setText(String.valueOf(mDive.getMaxdepth().getDistance())
		// + " " + mDive.getMaxdepth().getFullName().toUpperCase());
		String maxdepth_unit = "";
		maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.meters).toUpperCase()
				: getResources().getString(R.string.feet).toUpperCase();

		/*
		 * if (mDive.getMaxdepthUnit() == null) maxdepth_unit =
		 * (Units.getDistanceUnit() == Units.Distance.KM) ? "METERS" : "FEET";
		 * else maxdepth_unit = (mDive.getMaxdepthUnit().compareTo("m") == 0) ?
		 * "METERS" : "FEET";
		 */
		Double maxdepth_value = 0.0;
		if (mDive.getMaxdepth() != null && mDive.getMaxdepthUnit() != null) {
			if (Units.getDistanceUnit() == Units.Distance.KM)
				maxdepth_value = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? mDive
						.getMaxdepth() : Utils.round(Converter.convert(
								mDive.getMaxdepth(), Units.Distance.FT,
								Units.Distance.KM), 2);
						else
							maxdepth_value = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_ft)) == 0) ? mDive
									.getMaxdepth() : Utils.round(Converter.convert(
											mDive.getMaxdepth(), Units.Distance.KM,
											Units.Distance.FT), 2);
		}
		((TextView) findViewById(R.id.max_depth)).setText(maxdepth_value + " "
				+ maxdepth_unit);
		((TextView) findViewById(R.id.max_depth)).setTypeface(faceR);
		mDownloadGraphTask = new DownloadGraphTask(
				((ImageView) findViewById(R.id.graph)));
		mDownloadGraphTask.execute();
		((TextView) findViewById(R.id.date)).setTypeface(faceB);
		((TextView) findViewById(R.id.date)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.water)).setTypeface(faceB);
		((TextView) findViewById(R.id.water)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.duration)).setTypeface(faceB);
		((TextView) findViewById(R.id.duration)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.visibility)).setTypeface(faceB);
		((TextView) findViewById(R.id.visibility)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.temp)).setTypeface(faceB);
		((TextView) findViewById(R.id.temp)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.dive_type)).setTypeface(faceB);
		((TextView) findViewById(R.id.dive_type)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.date_content)).setTypeface(faceR);
		((TextView) findViewById(R.id.date_content)).setText(mDive.getDate()+ " " + mDive.getTime());
		((TextView) findViewById(R.id.date_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		if (mDive.getWater() != null) {
			((TextView) findViewById(R.id.water_content)).setTypeface(faceR);
			((TextView) findViewById(R.id.water_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView) findViewById(R.id.water_content)).setText(Character.toUpperCase(mDive.getWater().charAt(0))+ mDive.getWater().substring(1));
		} else {
			((TextView) findViewById(R.id.water)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.water_content)).setVisibility(View.GONE);
		}
		((TextView) findViewById(R.id.duration_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		((TextView) findViewById(R.id.duration_content)).setTypeface(faceR);
		((TextView) findViewById(R.id.duration_content)).setText(mDive.getDuration() + getResources().getString(R.string.unit_mins));
		if (mDive.getVisibility() != null) {
			((TextView) findViewById(R.id.visibility_content)).setTypeface(faceR);
			((TextView) findViewById(R.id.visibility_content)).setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView) findViewById(R.id.visibility_content)).setText(Character.toUpperCase(mDive.getVisibility().charAt(0)) + mDive.getVisibility().substring(1));
		} else {
			((TextView) findViewById(R.id.visibility)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.visibility_content)).setVisibility(View.GONE);
		}

		String temp;
		if (mDive.getTempSurface() != null) {
			// temp = "SURF " + mDive.getTempSurface().getTemperature() + "º" +
			// mDive.getTempSurface().getSmallName();
			String tempsurface_unit = "";
			/*
			 * if (mDive.getTempSurfaceUnit() == null) tempsurface_unit =
			 * (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			 * else tempsurface_unit =
			 * (mDive.getTempSurfaceUnit().compareTo("C") == 0) ? "C" : "F";
			 */
			tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C): getResources().getString(R.string.unit_F);
			Double tempsurface_value = 0.0;
			if (mDive.getTempSurface() != null
					&& mDive.getTempSurfaceUnit() != null) {
				if (Units.getTemperatureUnit() == Units.Temperature.C)
					tempsurface_value = (mDive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) 
					? mDive.getTempSurface() 
							: Utils.round(Converter.convert(mDive.getTempSurface(),Units.Temperature.F, Units.Temperature.C),2);
					else
						tempsurface_value = (mDive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_F)) == 0) 
						? mDive.getTempSurface() 
								: Utils.round(Converter.convert(mDive.getTempSurface(),Units.Temperature.C, Units.Temperature.F),2);
			}
			if (tempsurface_unit.equals(getResources().getString(R.string.unit_C)))
				temp = getResources().getString(R.string.surface_label) + " " + tempsurface_value + getResources().getString(R.string.unit_C_symbol);
			else
				temp = getResources().getString(R.string.surface_label) + " " + tempsurface_value + getResources().getString(R.string.unit_F_symbol);
		} else
			temp = "-";
		temp += " | ";
		if (mDive.getTempBottom() != null) {
			// temp += "BOTTOM " + mDive.getTempBottom().getTemperature() + "º"
			// + mDive.getTempBottom().getSmallName();
			String tempbottom_unit = "";
			/*
			 * if (mDive.getTempBottomUnit() == null) tempbottom_unit =
			 * (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			 * else tempbottom_unit = (mDive.getTempBottomUnit().compareTo("C")
			 * == 0) ? "C" : "F";
			 */
			tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C)
					: getResources().getString(R.string.unit_F);
			Double tempbottom_value = 0.0;
			if (mDive.getTempBottom() != null
					&& mDive.getTempBottomUnit() != null) {
				if (Units.getTemperatureUnit() == Units.Temperature.C)
					tempbottom_value = (mDive.getTempBottomUnit()
							.compareTo(getResources().getString(R.string.unit_C)) == 0) ? mDive.getTempBottom()
									: Utils.round(Converter.convert(mDive.getTempBottom(), Units.Temperature.F,Units.Temperature.C), 2);
							else
								tempbottom_value = (mDive.getTempBottomUnit()
										.compareTo(getResources().getString(R.string.unit_F)) == 0) ? mDive.getTempBottom()
												: Utils.round(Converter.convert(mDive.getTempBottom(), Units.Temperature.C,Units.Temperature.F), 2);
			}
			if (tempbottom_unit.equals(getResources().getString(R.string.unit_C)))
				temp += getResources().getString(R.string.bottom_label) + " " + tempbottom_value + getResources().getString(R.string.unit_C_symbol);
			else
				temp += getResources().getString(R.string.bottom_label) + " " + tempbottom_value + getResources().getString(R.string.unit_F_symbol);
		} else
			temp += "-";
		if (temp.contentEquals("- | -")) {
			((TextView) findViewById(R.id.temp)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.temp_content)).setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.temp_content)).setTypeface(faceR);
			((TextView) findViewById(R.id.temp_content)).setTextSize(
					TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView) findViewById(R.id.temp_content)).setText(temp);
		}

		String type = "";
		if (mDive.getDivetype() == null)
			type = "";
		else
			for (int i = 0; i < mDive.getDivetype().size(); i++) {
				if (mDive.getDivetype().get(i) != null)
					type += Character.toUpperCase(mDive.getDivetype().get(i)
							.charAt(0))
							+ mDive.getDivetype().get(i).substring(1) + ", ";
			}
		if (type != "")
			type = (String) type.subSequence(0, type.length() - 2);
		else
			type = "";
		if (type.contentEquals("")) {
			((TextView) findViewById(R.id.dive_type)).setVisibility(View.GONE);
			((TextView) findViewById(R.id.dive_type_content))
			.setVisibility(View.GONE);
		} else {
			((TextView) findViewById(R.id.dive_type_content))
			.setTypeface(faceR);
			((TextView) findViewById(R.id.dive_type_content)).setTextSize(
					TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
			((TextView) findViewById(R.id.dive_type_content)).setText(type);
		}

		// Visualizing Reviews
		mOverall = (RatingBar) findViewById(R.id.overall_review);
		mDifficulty = (RatingBar) findViewById(R.id.difficulty_review);
		mFish = (RatingBar) findViewById(R.id.fish_review);
		mLife = (RatingBar) findViewById(R.id.life_review);
		mWreck = (RatingBar) findViewById(R.id.wreck_review);
		mOverall.setIsIndicator(true);
		mDifficulty.setIsIndicator(true);
		mFish.setIsIndicator(true);
		mWreck.setIsIndicator(true);
		mLife.setIsIndicator(true);
		if (mDive.getDiveReviews() != null) {
			if (mDive.getDiveReviews().getOverall() == null) {
				((TextView) findViewById(R.id.overallTV)).setVisibility(View.GONE);
				((RatingBar) findViewById(R.id.overall_review)).setVisibility(View.GONE);
			} else {
				mOverall.setRating(mDive.getDiveReviews().getOverall());
			}
			if (mDive.getDiveReviews().getDifficulty() == null) {
				((TextView) findViewById(R.id.difficultyTV)).setVisibility(View.GONE);
				((RatingBar) findViewById(R.id.difficulty_review)).setVisibility(View.GONE);
			} else {

				mDifficulty.setRating(mDive.getDiveReviews().getDifficulty());

			}
			if (mDive.getDiveReviews().getBigFish() == null) {
				((TextView) findViewById(R.id.fishTV)).setVisibility(View.GONE);
				((RatingBar) findViewById(R.id.fish_review))
				.setVisibility(View.GONE);
			} else {

				mFish.setRating(mDive.getDiveReviews().getBigFish());

			}
			if (mDive.getDiveReviews().getMarine() == null) {
				((TextView) findViewById(R.id.lifeTV)).setVisibility(View.GONE);
				((RatingBar) findViewById(R.id.life_review)).setVisibility(View.GONE);
			} else {

				mLife.setRating(mDive.getDiveReviews().getMarine());

			}
			if (mDive.getDiveReviews().getWreck() == null) {
				((TextView) findViewById(R.id.wreckTV))
				.setVisibility(View.GONE);
				((RatingBar) findViewById(R.id.wreck_review))
				.setVisibility(View.GONE);
			} else {

				mWreck.setRating(mDive.getDiveReviews().getWreck());

			}
		} else {
			((LinearLayout) findViewById(R.id.layout_review))
			.setVisibility(View.GONE);
		}

		mPic = ((ImageView) findViewById(R.id.profile_image));
		mRoundedPic = ((ImageView) findViewById(R.id.main_image_cache));
		mListBuddyPictures = (LinearLayout) findViewById(R.id.buddy_list_pictures);
		mListTanks = (LinearLayout) findViewById(R.id.tank_list);
		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

		ImageCacheParams cacheParams = new ImageCacheParams(this, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
		mImageFetcher = new ImageFetcher(this, mImageThumbSize);
		mImageFetcher.setLoadingImage(R.drawable.empty_photo);
		mImageFetcher.addImageCache(this.getSupportFragmentManager(), cacheParams);
		for (Buddy b : mDive.getBuddies())
		{
			LinearLayout layout = new LinearLayout(getApplicationContext());
			layout.setLayoutParams(new LayoutParams(250, 300));
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setGravity(Gravity.CENTER);

			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(220, 220));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setImageResource(R.drawable.no_picture);

			TextView name = new TextView(this);
			name.setText(b.getNickname());
			name.setGravity(Gravity.CENTER);
			name.setTypeface(faceR);
			layout.addView(imageView);
			layout.addView(name);
			mImageFetcher.loadImage(b.getPicture()._urlDefault, imageView);
			mListBuddyPictures.addView(layout);
		}
		TextView title = (TextView) findViewById(R.id.tank_title);
		title.setTypeface(faceB);
		title.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
		for (Tank tank : mDive.getTanks())
		{
			final float scale = getResources().getDisplayMetrics().density;
			LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LinearLayout tankElem = new LinearLayout(getApplicationContext());
			tankElem.setLayoutParams(params);
			tankElem.setOrientation(LinearLayout.HORIZONTAL);
			tankElem.setGravity(Gravity.CENTER);
			tankElem.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			TextView text = new TextView(getApplicationContext());
			text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			text.setTypeface(faceR);
			//				text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
			text.setTextSize(15);
			text.setPadding((int)(10 * scale + 0.5f),(int)(10 * scale + 0.5f),(int)(10 * scale + 0.5f),0);

			//Writing the summary string of the tank
			String tanksSummary = "";
			if(tank.getMultitank() == 2)
				tanksSummary = "2x";
			tanksSummary += tank.getVolumeValue() + tank.getVolumeUnit().toUpperCase() + "          ";
			if(tank.getGasType().equals("nitrox"))
				tanksSummary += "Nx " + tank.getO2();
			else if (tank.getGasType().equals("trimix"))
				tanksSummary += "Tx " + tank.getO2() + "/" + tank.getHe();
			else if (tank.getGasType().equals("air"))
				tanksSummary += getResources().getString(R.string.air_mix);
			else
				tanksSummary += tank.getGasType().toUpperCase();
			tanksSummary +="\n";
			tanksSummary += tank.getPStartValue() + tank.getPUnit() + " \u2192 " + tank.getPEndValue() + tank.getPUnit() + "\n";
			if(tank.getTimeStart() != null && tank.getTimeStart() != 0)
				tanksSummary += "Switched at: " + tank.getTimeStart() / 60 + "min";

			text.setText(tanksSummary);
			text.setTextColor(getResources().getColor(R.color.dark_grey));
			text.setGravity(Gravity.LEFT);

			tankElem.addView(text);
			tankElem.setBackgroundResource(R.color.white);
			mListTanks.addView(tankElem);
		}
		mDownloadImageTask = new DownloadImageTask(mPic);
		mDownloadImageTask.execute();
	}

	private class DownloadShopLogoTask extends AsyncTask<Void, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;

		public DownloadShopLogoTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(Void... voids) {
			try {
				if (mDive.getShopPicture() != null)
					return mDive.getShopPicture().getPicture(
							DiveDetailsMainActivity.this);
				// return
				// mDive.getShop().getLogo().getPicture(DiveDetailsMainActivity.this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (imageViewReference != null) {
				final ImageView imageView = imageViewReference.get();
				if (result != null && imageView != null) {
					System.out.println("Shop result = " + result);
					mShopLogo.setVisibility(View.VISIBLE);
					mShopLogo.setImageBitmap(result);
					mShopLogo.setScaleType(ScaleType.FIT_CENTER);
				}
			}
		}

		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}

	private class DownloadGraphTask extends AsyncTask<Void, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;

		public DownloadGraphTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(Void... voids) {
			try {
				if (DiveDetailsMainActivity.this != null
						&& mDive.getProfile() != null) {
					return mDive.getProfile().getPicture(
							getApplicationContext());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				return null;
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (imageViewReference != null) {
				final ImageView imageView = imageViewReference.get();
				if (result != null && imageView != null) {
					imageView.setImageBitmap(result);
				}
			}
		}

		@Override
		protected void onCancelled() {
			mDownloadGraphTask = null;
		}
	}

	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;

		public DownloadImageTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		protected Bitmap doInBackground(Void... voids) {
			try {
				if (DiveDetailsMainActivity.this != null) {
					ApplicationController AC = ((ApplicationController) getApplicationContext());
					if(AC.getModel().getUser().getPictureSmall().getPicture(getApplicationContext()) != null )
						return AC.getModel().getUser().getPictureSmall().getPicture(getApplicationContext());
					else 
						return null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (RuntimeException e){
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Bitmap result) {
			if (imageViewReference != null) {
				final ImageView imageView = imageViewReference.get();
				if (result != null && imageView != null) {
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

	@Override
	public void onDeleteConfirm(DialogFragment dialog) {
		goToDeleteDive();
	}
	
	private class ReviewDialog extends EditDialog
	{
		public ReviewDialog(Activity a) {
			super(a);
			View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_review, null);
			mTitle = getString(R.string.shop_review_title);
			
			Spinner spinner = (Spinner) v.findViewById(R.id.shop_review_service);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
			        R.array.shop_review_service, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			mContent = v;
		}
	}
}
