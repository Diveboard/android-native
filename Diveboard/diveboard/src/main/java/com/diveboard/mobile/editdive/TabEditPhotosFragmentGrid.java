package com.diveboard.mobile.editdive;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.GalleryCarouselActivity;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;

import android.os.AsyncTask;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.AbsListView;
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

public class TabEditPhotosFragmentGrid extends Fragment {

	private DiveboardModel mModel;
	private int mPhotoPos = 0;
	//private DownloadImageTask mDownloadImageTask;
	private ImageView mImageView;
	private List<Picture> mItems;
	private Size mSizePicture;
	private ViewGroup mRootView;
	private ImageAdapter mAdapter = null;

//	@Override
//	protected void onResume()
//	{
//		super.onResume();
//		ApplicationController AC = (ApplicationController)getApplicationContext();
//		AC.handleLowMemory();
//		//((ScrollView)getParent().findViewById(R.id.scroll)).smoothScrollTo(0, 0);
//	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
	{
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_photos_grid, container, false);
		mModel = AC.getModel();
		if (mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
				&& mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
		{
			mItems = mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures();
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((GridView)(mRootView.findViewById(R.id.gridview))).post(new Runnable() {
						public void run()
						{
							ArrayList<Pair<ImageView, Picture>> arrayPair = new ArrayList<Pair<ImageView, Picture>>();
							//System.out.println("il y a " + mItems.size() + " photos");
							int i = 0;
							int screenWidth;
							int screenheight;
							int nbPicture;
							screenWidth = ((GridView)(mRootView.findViewById(R.id.gridview))).getMeasuredWidth();
							screenheight = ((GridView)(mRootView.findViewById(R.id.gridview))).getMeasuredHeight();
							if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
							{
								nbPicture = 3;	
							}
							else
							{
								double temp = ((double)screenWidth / (double)screenheight);
								nbPicture = (int) (temp * 3.0);
								//System.out.println(screenWidth + " " + screenheight + " " + nbPicture);
							}
							//System.out.println("taille = " + screenWidth / nbPicture);
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
							mAdapter = new ImageAdapter(getActivity(), mItems, mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)));
							((GridView)(mRootView.findViewById(R.id.gridview))).setAdapter(mAdapter);
							((GridView)(mRootView.findViewById(R.id.gridview))).setOnScrollListener(new OnScrollListener() {
								
								@Override
								public void onScrollStateChanged(AbsListView view, int scrollState) {
									if(scrollState != SCROLL_STATE_IDLE) {
										mAdapter.setFlinging(true);
									} else {
										mAdapter.setFlinging(false);   
									}

									int first = view.getFirstVisiblePosition(); 
									int count = view.getChildCount(); 

									if (scrollState == SCROLL_STATE_IDLE || (first + count > mAdapter.getCount()) ) { 
										((GridView)(mRootView.findViewById(R.id.gridview))).invalidateViews(); 
									}
								}
								
								@Override
								public void onScroll(AbsListView view, int firstVisibleItem,
										int visibleItemCount, int totalItemCount) {
									// TODO Auto-generated method stub
									
								}
							});
//							while (i < mItems.size())
//							{
//								//TableRow row = new TableRow(getActivity().getApplicationContext());
//								for (int j = 0; j < nbPicture && i < mItems.size(); j++)
//								{
//									//int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
//									//LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
//									//TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
//									//tbparam.gravity = Gravity.CENTER;
//									//linearLayout.setGravity(Gravity.CENTER);
//									//linearLayout.setLayoutParams(tbparam);
//									ImageView imageView = new ImageView(getActivity().getApplicationContext());
//									imageView.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / nbPicture, screenWidth / nbPicture));
//									imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//									imageView.setContentDescription(String.valueOf(i));
//									imageView.setVisibility(View.GONE);
//									imageView.setOnLongClickListener(new MyTouchListener());
//									((LinearLayout)(mRootView.findViewById(R.id.drop_item))).setOnDragListener(new MyDragListener());
//									//								int shortAnimTime = getResources().getInteger(
//									//										android.R.integer.config_shortAnimTime);
//									//								imageView.setVisibility(View.VISIBLE);
//									//								imageView.animate().setDuration(shortAnimTime).alpha(1);
//									ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
//									if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
//											&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
//									{
//										Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mItems.get(i));
//										arrayPair.add(pair);
//										//imageView.setImageBitmap(mItems.get(i).getPicture(PhotosActivity.this, Size.MEDIUM));
//									}
////									imageView.setOnClickListener(new OnClickListener() {
////
////										@Override
////										public void onClick(View v) {
////											ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
////											if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
////													&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
////											{
////												//Toast.makeText(PhotosActivity.this, "" + position, Toast.LENGTH_SHORT).show();
////												Intent galleryCarousel = new Intent(getActivity().getApplicationContext(), GalleryCarouselActivity.class);
////												galleryCarousel.putExtra("index", getActivity().getIntent().getIntExtra("index", -1));
////												galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
////												startActivity(galleryCarousel);
////											}	
////
////										}
////									});
//									//								mDownloadImageTask = new DownloadImageTask(imageView, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), i);
//									//								mDownloadImageTask.execute();
//									//linearLayout.addView(imageView);
//									//ProgressBar bar = new ProgressBar(getActivity().getApplicationContext());
//									//RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//									//bar.setVisibility(View.VISIBLE);
//									//bar.setLayoutParams(params);
//									//linearLayout.addView(new ProgressBar(getActivity().getApplicationContext()));
//									//row.addView(linearLayout);
//									i++;
//								}
//								//tableLayout.addView(row);
//							}
//							ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
//							if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
//									&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
//							{
//								mDownloadImageTask = new DownloadImageTask(arrayPair, mItems, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), 0);
//								mDownloadImageTask.execute();
//							}
						}
					});
				}
			}).start();
		}
		else
		{
//			Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
//			TextView tv = new TextView(getActivity().getApplicationContext());
//			tv.setText("No Picture in your album!");
//			tv.setTypeface(face);
//			tv.setGravity(Gravity.CENTER);
//			mRootView.setContentView(tv);
		}
		return mRootView;
	}

//	private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap>
//	{
//		private final WeakReference<ImageView> imageViewReference;
//		private boolean isPicture = false;
//		private Context mContext;
//		private Dive mDive;
//		private int mPosition;
//		private List<Picture> mListPictures;
//		private ArrayList<Pair<ImageView, Picture>> mArrayPair;
//
//		public DownloadImageTask(ArrayList<Pair<ImageView, Picture>> arrayPair, List<Picture> listPictures, Context context, Dive dive, int position)
//		{
//			imageViewReference = new WeakReference<ImageView>(arrayPair.get(position).first);
//			mContext = context;
//			mDive = dive;
//			mPosition = position;
//			mListPictures = listPictures;
//			mArrayPair = arrayPair;
//		}
//
//		protected Bitmap doInBackground(Void... voids)
//		{
//			try {
//				if (mContext != null)
//				{
//					return mListPictures.get(mPosition).getPicture(mContext, mSizePicture);
//				}
//
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		protected void onPostExecute(Bitmap result)
//		{
//			if (imageViewReference != null)
//			{
//				final ImageView imageView = imageViewReference.get();
//				if (result != null && imageView != null)
//				{	
//					imageView.setImageBitmap(result);
//					imageView.setVisibility(View.VISIBLE);
//					LinearLayout rl = (LinearLayout)(imageView.getParent());
//					ProgressBar bar = (ProgressBar)rl.getChildAt(1);
//					bar.setVisibility(View.GONE);
//					if (mPosition < mArrayPair.size() - 1 && mModel != null && mArrayPair != null && TabEditPhotosFragment.this != null && mModel.getDives() != null)
//					{
//						if (getActivity() != null)
//						{
//							mDownloadImageTask = new DownloadImageTask(mArrayPair, mItems, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), mPosition + 1);
//							mDownloadImageTask.execute();
//						}
//						System.out.println("position = " + mPosition);
//					}
//				}
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mDownloadImageTask = null;
//		}
//	}
	
	private final class MyTouchListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View view) {
			ClipData data = ClipData.newPlainText("", "");
			DragShadowBuilder shadowBuilder = new View.DragShadowBuilder((View)view.getParent());
			((View)view.getParent()).startDrag(data, shadowBuilder, view, 0);
			//((View)view.getParent()).setVisibility(View.INVISIBLE);
			return true;
		}
	}
	
	class MyDragListener implements OnDragListener {
		Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
		Drawable normalShape = getResources().getDrawable(R.drawable.shape);

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				// do nothing
				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				//v.setBackgroundDrawable(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				//v.setBackgroundDrawable(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				View view = (View) event.getLocalState();
				ViewGroup owner = (ViewGroup) view.getParent();
				owner.removeView(view);
				LinearLayout container = (LinearLayout) v;
				container.addView(view);
				view.setVisibility(View.VISIBLE);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				//v.setBackgroundDrawable(normalShape);
			default:
				break;
			}
			return true;
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private List<Picture> mListPhotos;
		private DownloadImageTask mDownloadImageTask;
		private Dive mDive;
		private LayoutInflater mInflater;
		private boolean mFlinging = false;

		public ImageAdapter(Context c, List<Picture> items, Dive dive) {
			mContext = c;
			mListPhotos = items;
			mDive = dive;
			mInflater = LayoutInflater.from(mContext);
		}

		public void setFlinging(boolean b) {
			mFlinging = b;
		}

		public int getCount() {
			return mListPhotos.size();
		}

		public Object getItem(int position) {
			return mListPhotos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder = null;
//			if (convertView == null) {  // if it's not recycled, initialize some attributes
//				convertView = mInflater.inflate(R.layout.gridview_item, null);
//				holder = new ViewHolder();
//				holder.layout = (LinearLayout)convertView.findViewById(R.id.layout);
//				holder.bar = (ProgressBar)convertView.findViewById(R.id.progress);
//				holder.imageView = (ImageView) convertView.findViewById(R.id.image);
//				int size = ((GridView)(mRootView.findViewById(R.id.gridview))).getMeasuredWidth();
//				holder.layout.setLayoutParams(new GridView.LayoutParams(size / 3, size / 3));
//				//holder.imageView.setLayoutParams(new GridView.LayoutParams(size / 3, size / 3));
//				//holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				
//				convertView.setTag(holder);
//				System.out.println("OOOOOOOOOOOOOOOO");
//				
//				//imageView.setPadding(8, 8, 8, 8);
//			} else {
//				holder = (ViewHolder)convertView.getTag();
//				//imageView = (ImageView) convertView;
//			}
//			//if (holder != null)
//			{
//				mDownloadImageTask = new DownloadImageTask(holder, mListPhotos, mContext,  mDive, position);
//				mDownloadImageTask.execute();
//				//notifyDataSetChanged();
//			}
//			//imageView.setImageResource(mThumbIds[position]);
//			return convertView;
			View v;
			if (convertView == null)
			{
				v = mInflater.inflate(R.layout.gridview_item, null);
				LinearLayout ll = (LinearLayout)v.findViewById(R.id.layout);
				int size = ((GridView)(mRootView.findViewById(R.id.gridview))).getMeasuredWidth();
				ll.setLayoutParams(new GridView.LayoutParams(size / 3, size / 3));
				mDownloadImageTask = new DownloadImageTask(v, mListPhotos, mContext,  mDive, position);
				mDownloadImageTask.execute();
			}
			else
			{
				v = convertView;
			}
			
			
			
			return v;
		}
		
		private class ViewHolder {
			LinearLayout layout;
			ImageView imageView;
			ProgressBar bar;
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
			private final WeakReference<View> convertViewReference;
			private boolean isPicture = false;
			private Context mContext;
			private Dive mDive;
			private int mPosition;
			private List<Picture> mListPictures;
			private ArrayList<Pair<ImageView, Picture>> mArrayPair;
			private ViewHolder mHolder;

			public DownloadImageTask(View holder, List<Picture> listPictures, Context context, Dive dive, int position)
			{
				convertViewReference = new WeakReference<View>(holder);
				mContext = context;
				mDive = dive;
				mPosition = position;
				mListPictures = listPictures;
				//mHolder = holder;
				//mArrayPair = arrayPair;
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
				if (convertViewReference != null)
				{
					final View convertView = convertViewReference.get();
					if (result != null  && convertView != null)
					{	
						((ProgressBar)convertView.findViewById(R.id.progress)).setVisibility(View.GONE);
						((ImageView)convertView.findViewById(R.id.image)).setVisibility(View.VISIBLE);
						((ImageView)convertView.findViewById(R.id.image)).setImageBitmap(result);
//						if (mPosition < mArrayPair.size())
//						{
//							mDownloadImageTask = new DownloadImageTask(mArrayPair, mItems, mContext, mDive, mPosition + 1);
//							mDownloadImageTask.execute();
//						}

					}
				}
			}

			@Override
			protected void onCancelled() {
				mDownloadImageTask = null;
			}
		}
	}

//	@Override
//	protected void onDestroy() {
//		if (mDownloadImageTask != null)
//			mDownloadImageTask.cancel(true);
//		super.onDestroy();
//	}
//	
//	@Override
//	public void onStart() {
//		super.onStart();
//		EasyTracker.getInstance(this).activityStart(this);
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		EasyTracker.getInstance(this).activityStop(this);
//	}

}
