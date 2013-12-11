package com.diveboard.mobile;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
		((ScrollView)getParent().findViewById(R.id.scroll)).smoothScrollTo(0, 0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		setContentView(R.layout.activity_photos);
		if (AC.handleLowMemory() == true)
			return ;
		mModel = AC.getModel();
		mItems = mModel.getDives().get(getIntent().getIntExtra("index", 0)).getPictures();
//		GridView gridview = (GridView) findViewById(R.id.gridview);
//	    gridview.setAdapter(new ImageAdapter(getApplicationContext(), items, mModel.getDives().get(getIntent().getIntExtra("index", 0))));
//	    gridview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View v, int position,
//					long id) {
//				ApplicationController AC = (ApplicationController)getApplicationContext();
//				if (AC.getModel().getDives().get(AC.getPageIndex()).getPictures() != null
//						&& AC.getModel().getDives().get(AC.getPageIndex()).getPictures().size() != 0)
//				{
//					//Toast.makeText(PhotosActivity.this, "" + position, Toast.LENGTH_SHORT).show();
//					Intent galleryCarousel = new Intent(PhotosActivity.this, GalleryCarouselActivity.class);
//					galleryCarousel.putExtra("index", AC.getPageIndex());
//					galleryCarousel.putExtra("position", position);
//					startActivity(galleryCarousel);
//				}		
//			}
//	    });
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				((TableLayout)(findViewById(R.id.tablelayout))).post(new Runnable() {
					public void run() {
						TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout);
						System.out.println("il y a " + mItems.size() + " photos");
						int i = 0;
						while (i < mItems.size())
						{
							TableRow row = new TableRow(PhotosActivity.this);
							for (int j = 0; j < 3 && i < mItems.size(); j++)
							{
								ImageView imageView = new ImageView(PhotosActivity.this);

								int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
								System.out.println("size = " + size);
								imageView.setLayoutParams(new TableRow.LayoutParams(size / 3, size / 3));
								imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
								imageView.setContentDescription(String.valueOf(i));
								imageView.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										ApplicationController AC = (ApplicationController)getApplicationContext();
										if (AC.getModel().getDives().get(AC.getPageIndex()).getPictures() != null
												&& AC.getModel().getDives().get(AC.getPageIndex()).getPictures().size() != 0)
										{
											//Toast.makeText(PhotosActivity.this, "" + position, Toast.LENGTH_SHORT).show();
											Intent galleryCarousel = new Intent(PhotosActivity.this, GalleryCarouselActivity.class);
											galleryCarousel.putExtra("index", AC.getPageIndex());
											galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
											startActivity(galleryCarousel);
										}	
										
									}
								});
								mDownloadImageTask = new DownloadImageTask(imageView, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), i);
								mDownloadImageTask.execute();
								row.addView(imageView);
								i++;
							}
							tableLayout.addView(row);
						}
					}
				});
			}
		}).start();
		
		
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
	
	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;
		private boolean isPicture = false;
		private Context mContext;
		private Dive mDive;
		private int mPosition;
		private List<Picture> mListPictures;
		
		public DownloadImageTask(ImageView imageView, List<Picture> listPictures, Context context, Dive dive, int position)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
			mContext = context;
			mDive = dive;
			mPosition = position;
			mListPictures = listPictures;
		}
		
		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (mContext != null)
				{
					return mListPictures.get(mPosition).getPicture(mContext, Size.THUMB);
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
			mDownloadImageTask = null;
		}
	}
	
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private List<Picture> mListPhotos;
	    private DownloadImageTask mDownloadImageTask;
	    private Dive mDive;

	    public ImageAdapter(Context c, List<Picture> items, Dive dive) {
	        mContext = c;
	        mListPhotos = items;
	        mDive = dive;
	    }

	    public int getCount() {
	        return mListPhotos.size();
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            ApplicationController AC = (ApplicationController)getApplicationContext();
	            //int size = ((GridView)(findViewById(R.id.gridview))).getMeasuredWidth();
	            //System.out.println(size);
	           // imageView.setLayoutParams(new GridView.LayoutParams(size / 3, size / 3));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            //imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }

			mDownloadImageTask = new DownloadImageTask(imageView, mListPhotos, mContext,  mDive, position);
			mDownloadImageTask.execute();
	        //imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	    // references to our images
//	    private Integer[] mThumbIds = {
//	            R.drawable.sample_2, R.drawable.sample_3,
//	            R.drawable.sample_4, R.drawable.sample_5,
//	            R.drawable.sample_6, R.drawable.sample_7,
//	            R.drawable.sample_0, R.drawable.sample_1,
//	            R.drawable.sample_2, R.drawable.sample_3,
//	            R.drawable.sample_4, R.drawable.sample_5,
//	            R.drawable.sample_6, R.drawable.sample_7,
//	            R.drawable.sample_0, R.drawable.sample_1,
//	            R.drawable.sample_2, R.drawable.sample_3,
//	            R.drawable.sample_4, R.drawable.sample_5,
//	            R.drawable.sample_6, R.drawable.sample_7
//	    };
	    
	    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
		{
			private final WeakReference<ImageView> imageViewReference;
			private boolean isPicture = false;
			private Context mContext;
			private Dive mDive;
			private int mPosition;
			private List<Picture> mListPictures;
			
			public DownloadImageTask(ImageView imageView, List<Picture> listPictures, Context context, Dive dive, int position)
			{
				imageViewReference = new WeakReference<ImageView>(imageView);
				mContext = context;
				mDive = dive;
				mPosition = position;
				mListPictures = listPictures;
			}
			
			protected Bitmap doInBackground(Void... voids)
			{
				try {
					if (mContext != null)
					{
						return mListPictures.get(mPosition).getPicture(mContext, Size.THUMB);
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
				mDownloadImageTask = null;
			}
		}
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
