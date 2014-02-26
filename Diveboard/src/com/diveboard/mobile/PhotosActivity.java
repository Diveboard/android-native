package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosActivity extends Activity {

	private DiveboardModel mModel;
	private int mPhotoPos = 0;
	private DownloadImageTask mDownloadImageTask;
	private ImageView mImageView;
	private List<Picture> mItems;
	private Size mSizePicture;

	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
		//((ScrollView)getParent().findViewById(R.id.scroll)).smoothScrollTo(0, 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		setContentView(R.layout.activity_photos);
		if (AC.handleLowMemory() == true)
			return ;
		mModel = AC.getModel();
		if (AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures() != null
				&& AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size() != 0)
		{
			mItems = mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures();
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((TableLayout)(findViewById(R.id.tablelayout))).post(new Runnable() {
						public void run()
						{
							ArrayList<Pair<ImageView, Picture>> arrayPair = new ArrayList<Pair<ImageView, Picture>>();
							TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout);
							System.out.println("il y a " + mItems.size() + " photos");
							int i = 0;
							int screenWidth;
							int screenheight;
							int nbPicture;
							screenWidth = getParent().findViewById(R.id.root).getMeasuredWidth();
							screenheight = getParent().findViewById(R.id.root).getMeasuredHeight();
							if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
							{
								nbPicture = 3;	
							}
							else
							{
								double temp = ((double)screenWidth / (double)screenheight);
								nbPicture = (int) (temp * 3.0);
								System.out.println(screenWidth + " " + screenheight + " " + nbPicture);
							}
							System.out.println("taille = " + screenWidth / nbPicture);
							if (screenWidth / nbPicture > 240)
							{
								System.out.println("MEDIUM");
								mSizePicture = Size.MEDIUM; // medium
							}
							else
							{
								System.out.println("SMALL");
								mSizePicture = Size.SMALL; // small
							}
							while (i < mItems.size())
							{
								TableRow row = new TableRow(getApplicationContext());
								for (int j = 0; j < nbPicture && i < mItems.size(); j++)
								{
									int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
									LinearLayout linearLayout = new LinearLayout(PhotosActivity.this);
									TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
									tbparam.gravity = Gravity.CENTER;
									linearLayout.setGravity(Gravity.CENTER);
									linearLayout.setLayoutParams(tbparam);
									ImageView imageView = new ImageView(PhotosActivity.this);
									imageView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
									imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
									imageView.setContentDescription(String.valueOf(i));
									imageView.setVisibility(View.GONE);
									//								int shortAnimTime = getResources().getInteger(
									//										android.R.integer.config_shortAnimTime);
									//								imageView.setVisibility(View.VISIBLE);
									//								imageView.animate().setDuration(shortAnimTime).alpha(1);
									ApplicationController AC = (ApplicationController)getApplicationContext();
									if (AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures() != null
											&& AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size() != 0)
									{
										Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mItems.get(i));
										arrayPair.add(pair);
										//imageView.setImageBitmap(mItems.get(i).getPicture(PhotosActivity.this, Size.MEDIUM));
									}
									imageView.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											ApplicationController AC = (ApplicationController)getApplicationContext();
											if (AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures() != null
													&& AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size() != 0)
											{
												//Toast.makeText(PhotosActivity.this, "" + position, Toast.LENGTH_SHORT).show();
												Intent galleryCarousel = new Intent(getApplicationContext(), GalleryCarouselActivity.class);
												galleryCarousel.putExtra("index", getIntent().getIntExtra("index", 0));
												galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
												startActivity(galleryCarousel);
											}	

										}
									});
									//								mDownloadImageTask = new DownloadImageTask(imageView, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), i);
									//								mDownloadImageTask.execute();
									linearLayout.addView(imageView);
									ProgressBar bar = new ProgressBar(PhotosActivity.this);
									RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
									bar.setVisibility(View.VISIBLE);
									bar.setLayoutParams(params);
									linearLayout.addView(new ProgressBar(PhotosActivity.this));
									row.addView(linearLayout);
									i++;
								}
								tableLayout.addView(row);
							}
							ApplicationController AC = (ApplicationController)getApplicationContext();
							if (AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures() != null
									&& AC.getModel().getDives().get(getIntent().getIntExtra("index", 0)).getPictures().size() != 0)
							{
								mDownloadImageTask = new DownloadImageTask(arrayPair, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), 0);
								mDownloadImageTask.execute();
							}
						}
					});
				}
			}).start();
		}
		else
		{
			Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
			TextView tv = new TextView(this);
			tv.setText(getResources().getString(R.string.no_picture));
			tv.setTypeface(face);
			tv.setGravity(Gravity.CENTER);
			setContentView(tv);
		}


		//		TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout);
		//		System.out.println("il y a " + mItems.size() + " photos");
		//		int i = 0;
		//		while (i < mItems.size() - 1)
		//		{
		//			TableRow row = new TableRow(PhotosActivity.this);
		//			for (int j = 0; j < 3 && i < mItems.size() - 1; j++)
		//			{
		//				ImageView imageView = new ImageView(PhotosActivity.this);
		//
		//				int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
		//				System.out.println("size = " + size);
		//				imageView.setLayoutParams(new TableRow.LayoutParams(680 / 3, 680 / 3));
		//				imageView.getLayoutParams().width = 680 / 3;
		//				imageView.getLayoutParams().height = 680 / 3;
		//				//imageView.setLayoutParams(new TableLayout.LayoutParams(680 / 3, 680 / 3));
		//				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//				mDownloadImageTask = new DownloadImageTask(imageView, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), i);
		//				mDownloadImageTask.execute();
		//				row.addView(imageView);
		//				i++;
		//			}
		//			tableLayout.addView(row);
		//		}
	}

//	private class ImageThread extends Thread
//	{
//		private Picture mPicture;
//		private Context mContext;
//		private Bitmap mBitmap;
//		public ImageThread(Context context, Picture picture, Bitmap bitmap)
//		{
//			mPicture = picture;
//			mContext = context;
//			setBitmap(bitmap);
//		}
//
//		@Override
//		public void run() {
//			try {
//				setBitmap(mPicture.getPicture(mContext, mSizePicture));
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		public Bitmap getBitmap() {
//			return mBitmap;
//		}
//
//		public void setBitmap(Bitmap mBitmap) {
//			this.mBitmap = mBitmap;
//		}
//	}

	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;
		private Context mContext;
		private Dive mDive;
		private int mPosition;
		private List<Picture> mListPictures;
		private ArrayList<Pair<ImageView, Picture>> mArrayPair;

		public DownloadImageTask(ArrayList<Pair<ImageView, Picture>> arrayPair, List<Picture> listPictures, Context context, Dive dive, int position)
		{
			imageViewReference = new WeakReference<ImageView>(arrayPair.get(position).first);
			mContext = context;
			mDive = dive;
			mPosition = position;
			mListPictures = listPictures;
			mArrayPair = arrayPair;
		}

		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (mContext != null)
				{
					return mListPictures.get(mPosition).getPicture(mContext, mSizePicture);
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
					imageView.setVisibility(View.VISIBLE);
					LinearLayout rl = (LinearLayout)(imageView.getParent());
					ProgressBar bar = (ProgressBar)rl.getChildAt(1);
					bar.setVisibility(View.GONE);
					if (mPosition < mArrayPair.size() - 1 && mModel != null && mArrayPair != null && PhotosActivity.this != null && mModel.getDives() != null)
					{
						mDownloadImageTask = new DownloadImageTask(mArrayPair, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), mPosition + 1);
						mDownloadImageTask.execute();
						System.out.println("position = " + mPosition);
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}

	//	public class ImageAdapter extends BaseAdapter {
	//	    private Context mContext;
	//	    private List<Picture> mListPhotos;
	//	    private DownloadImageTask mDownloadImageTask;
	//	    private Dive mDive;
	//
	//	    public ImageAdapter(Context c, List<Picture> items, Dive dive) {
	//	        mContext = c;
	//	        mListPhotos = items;
	//	        mDive = dive;
	//	    }
	//
	//	    public int getCount() {
	//	        return mListPhotos.size();
	//	    }
	//
	//	    public Object getItem(int position) {
	//	        return null;
	//	    }
	//
	//	    public long getItemId(int position) {
	//	        return 0;
	//	    }
	//
	//	    // create a new ImageView for each item referenced by the Adapter
	//	    public View getView(int position, View convertView, ViewGroup parent) {
	//	        ImageView imageView;
	//	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	//	            imageView = new ImageView(mContext);
	//	            ApplicationController AC = (ApplicationController)getApplicationContext();
	//	            //int size = ((GridView)(findViewById(R.id.gridview))).getMeasuredWidth();
	//	            //System.out.println(size);
	//	           // imageView.setLayoutParams(new GridView.LayoutParams(size / 3, size / 3));
	//	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	//	            //imageView.setPadding(8, 8, 8, 8);
	//	        } else {
	//	            imageView = (ImageView) convertView;
	//	        }
	//
	//			mDownloadImageTask = new DownloadImageTask(imageView, mListPhotos, mContext,  mDive, position);
	//			mDownloadImageTask.execute();
	//	        //imageView.setImageResource(mThumbIds[position]);
	//	        return imageView;
	//	    }
	//
	//	    // references to our images
	////	    private Integer[] mThumbIds = {
	////	            R.drawable.sample_2, R.drawable.sample_3,
	////	            R.drawable.sample_4, R.drawable.sample_5,
	////	            R.drawable.sample_6, R.drawable.sample_7,
	////	            R.drawable.sample_0, R.drawable.sample_1,
	////	            R.drawable.sample_2, R.drawable.sample_3,
	////	            R.drawable.sample_4, R.drawable.sample_5,
	////	            R.drawable.sample_6, R.drawable.sample_7,
	////	            R.drawable.sample_0, R.drawable.sample_1,
	////	            R.drawable.sample_2, R.drawable.sample_3,
	////	            R.drawable.sample_4, R.drawable.sample_5,
	////	            R.drawable.sample_6, R.drawable.sample_7
	////	    };
	//	    
	//	    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	//		{
	//			private final WeakReference<ImageView> imageViewReference;
	//			private boolean isPicture = false;
	//			private Context mContext;
	//			private Dive mDive;
	//			private int mPosition;
	//			private List<Picture> mListPictures;
	//			private ArrayList<Pair<ImageView, Picture>> mArrayPair;
	//			
	//			public DownloadImageTask(ArrayList<Pair<ImageView, Picture>> arrayPair, List<Picture> listPictures, Context context, Dive dive, int position)
	//			{
	//				imageViewReference = new WeakReference<ImageView>(arrayPair.get(position).first);
	//				mContext = context;
	//				mDive = dive;
	//				mPosition = position;
	//				mListPictures = listPictures;
	//				mArrayPair = arrayPair;
	//			}
	//			
	//			protected Bitmap doInBackground(Void... voids)
	//			{
	//				try {
	//					if (mContext != null)
	//					{
	//						return mListPictures.get(mPosition).getPicture(mContext, Size.THUMB);
	//					}
	//						
	//				} catch (IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//				return null;
	//			}
	//			
	//			protected void onPostExecute(Bitmap result)
	//			{
	//				if (imageViewReference != null)
	//				{
	//					final ImageView imageView = imageViewReference.get();
	//					if (result != null && imageView != null)
	//					{	
	//						imageView.setImageBitmap(result);
	//						if (mPosition < mArrayPair.size())
	//						{
	//							mDownloadImageTask = new DownloadImageTask(mArrayPair, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), mPosition + 1);
	//							mDownloadImageTask.execute();
	//						}
	//						
	//					}
	//				}
	//			}
	//			
	//			@Override
	//			protected void onCancelled() {
	//				mDownloadImageTask = null;
	//			}
	//		}
	//	}

	@Override
	protected void onDestroy() {
		if (mDownloadImageTask != null)
			mDownloadImageTask.cancel(true);
		super.onDestroy();
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

}