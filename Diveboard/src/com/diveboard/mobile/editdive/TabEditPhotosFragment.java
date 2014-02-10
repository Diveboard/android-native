package com.diveboard.mobile.editdive;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DivesActivity;
import com.diveboard.mobile.GalleryCarouselActivity;
import com.diveboard.mobile.R;
import com.diveboard.mobile.SettingsActivity;
import com.diveboard.mobile.WaitDialogFragment;
import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.google.analytics.tracking.android.EasyTracker;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class TabEditPhotosFragment extends Fragment {

	private DiveboardModel mModel;
	private int mPhotoPos = 0;
	private DownloadImageTask mDownloadImageTask;
	//private ImageView mImageView;
	//private List<Picture> mItems;
	private Size mSizePicture;
	private ViewGroup mRootView;
	private RelativeLayout mChangeItem;
	
	//private DropPictureTask mDropPictureTask = null;

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
		mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_photos, container, false);
		mModel = EditDiveActivity.mModel;
		System.out.println("LIST PICTURE : " + EditDiveActivity.mListPictures);
		EditDiveActivity.mListPictures = mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures();
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mRootView.post(new Runnable() {
					public void run()
					{
						Typeface mFaceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
						Typeface mFaceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
						ArrayList<Pair<ImageView, Picture>> arrayPair = new ArrayList<Pair<ImageView, Picture>>();
						TableLayout tableLayout = (TableLayout)mRootView.findViewById(R.id.tablelayout);
						System.out.println("il y a " + EditDiveActivity.mListPictures.size() + " photos");
						int i = 0;
						int screenWidth;
						int screenheight;
						int nbPicture;


						screenWidth = mRootView.findViewById(R.id.tablelayout).getMeasuredWidth();
						screenheight = mRootView.findViewById(R.id.tablelayout).getMeasuredHeight();
						((TextView)mRootView.findViewById(R.id.drop_text)).setTypeface(mFaceB);
						RelativeLayout.LayoutParams dropitemparam = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
						dropitemparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
						((RelativeLayout)mRootView.findViewById(R.id.drop_item)).setLayoutParams(dropitemparam);

						((TextView)mRootView.findViewById(R.id.main_text)).setTypeface(mFaceB);
						RelativeLayout.LayoutParams mainitemparam = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
						mainitemparam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
						((RelativeLayout)mRootView.findViewById(R.id.main_item)).setLayoutParams(mainitemparam);

						mChangeItem = (RelativeLayout)mRootView.findViewById(R.id.change_item);
						RelativeLayout.LayoutParams changeitemparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mRootView.getMeasuredHeight() / 5);
						mChangeItem.setLayoutParams(changeitemparams);
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
						while (i < EditDiveActivity.mListPictures.size())
						{
							TableRow row = new TableRow(getActivity().getApplicationContext());
							for (int j = 0; j < nbPicture && i < EditDiveActivity.mListPictures.size(); j++)
							{
								int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
								LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
								TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
								tbparam.gravity = Gravity.CENTER;
								linearLayout.setGravity(Gravity.CENTER);
								linearLayout.setLayoutParams(tbparam);
								ImageView imageView = new ImageView(getActivity().getApplicationContext());
								imageView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
								imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
								imageView.setContentDescription(String.valueOf(i));
								imageView.setVisibility(View.GONE);
								imageView.setOnLongClickListener(new MyTouchListener(i));

								//								int shortAnimTime = getResources().getInteger(
								//										android.R.integer.config_shortAnimTime);
								//								imageView.setVisibility(View.VISIBLE);
								//								imageView.animate().setDuration(shortAnimTime).alpha(1);
								ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
								if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
										&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
								{
									Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, EditDiveActivity.mListPictures.get(i));
									arrayPair.add(pair);
									//imageView.setImageBitmap(mItems.get(i).getPicture(PhotosActivity.this, Size.MEDIUM));
								}
								imageView.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
										if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
												&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
										{
											//Toast.makeText(PhotosActivity.this, "" + position, Toast.LENGTH_SHORT).show();
											Intent galleryCarousel = new Intent(getActivity().getApplicationContext(), GalleryCarouselActivity.class);
											galleryCarousel.putExtra("index", getActivity().getIntent().getIntExtra("index", -1));
											galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
											startActivity(galleryCarousel);
										}	

									}
								});
								//								mDownloadImageTask = new DownloadImageTask(imageView, mItems, PhotosActivity.this,  mModel.getDives().get(getIntent().getIntExtra("index", 0)), i);
								//								mDownloadImageTask.execute();
								linearLayout.addView(imageView);
								ProgressBar bar = new ProgressBar(getActivity().getApplicationContext());
								RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
								bar.setVisibility(View.VISIBLE);
								bar.setLayoutParams(params);
								linearLayout.addView(new ProgressBar(getActivity().getApplicationContext()));
								row.addView(linearLayout);
								i++;
							}
							tableLayout.addView(row);
						}
						System.out.println("ENTRE " + EditDiveActivity.mListPictures.size() % nbPicture);
						if (EditDiveActivity.mListPictures.size() % nbPicture == 0)
						{

							// Add a new line
							TableRow row = new TableRow(getActivity().getApplicationContext());
							int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
							LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
							TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
							tbparam.gravity = Gravity.CENTER;
							linearLayout.setGravity(Gravity.CENTER);
							linearLayout.setLayoutParams(tbparam);
							EditDiveActivity.mPhotoView = new ImageView(getActivity().getApplicationContext());
							EditDiveActivity.mPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
							EditDiveActivity.mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
							EditDiveActivity.mPhotoView.setContentDescription(String.valueOf(i));
							EditDiveActivity.mPhotoView.setImageResource(R.drawable.ic_add);
							EditDiveActivity.mPhotoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
									{
										((EditDiveActivity) getActivity()).goToMenuV3(v);
									}
									else
									{
										EditDiveActivity.mMenuType = 1;
										((EditDiveActivity) getActivity()).goToMenuV2(v);
									}
										
								}
							});
							//imageView.setVisibility(View.GONE);
							linearLayout.addView(EditDiveActivity.mPhotoView);
							//imageView.setBackgroundResource(R.drawable.item_selector_add_picture);
							row.addView(linearLayout);
							tableLayout.addView(row);
						}
						else
						{
							// Use the same row
							TableRow row = (TableRow)((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getChildAt(EditDiveActivity.mListPictures.size() / nbPicture);
							int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
							LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
							TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
							tbparam.gravity = Gravity.CENTER;
							linearLayout.setGravity(Gravity.CENTER);
							linearLayout.setLayoutParams(tbparam);
							EditDiveActivity.mPhotoView = new ImageView(getActivity().getApplicationContext());
							//EditDiveActivity.mPhotoView.setTag("AddPicture");
							EditDiveActivity.mPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
							EditDiveActivity.mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
							EditDiveActivity.mPhotoView.setContentDescription(String.valueOf(i));
							//imageView.setVisibility(View.GONE);
							//								StateListDrawable states = new StateListDrawable();
							//								Drawable nameDrawableSelected = getActivity().getResources().getDrawable(R.drawable.ic_add_grey);
							//								Drawable nameDrawableUnselected = getActivity().getResources().getDrawable(R.drawable.ic_add);
							//								states.addState(new int[] {android.R.attr.state_pressed},
							//										nameDrawableSelected);
							//								states.addState(new int[] { },
							//										nameDrawableUnselected);
							EditDiveActivity.mPhotoView.setImageResource(R.drawable.ic_add);
							EditDiveActivity.mPhotoView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
									{
										((EditDiveActivity) getActivity()).goToMenuV3(v);
									}
									else
									{
										EditDiveActivity.mMenuType = 1;
										((EditDiveActivity) getActivity()).goToMenuV2(v);
									}
										
								}
							});
							linearLayout.addView(EditDiveActivity.mPhotoView);
							row.addView(linearLayout);
							//tableLayout.addView(row);
						}
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
						{
							((RelativeLayout)(mRootView.findViewById(R.id.drop_item))).setOnDragListener(new MyDropDragListener());
							((RelativeLayout)(mRootView.findViewById(R.id.main_item))).setOnDragListener(new MyMainDragListener());
						}
						ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
						if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
								&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
						{
							mDownloadImageTask = new DownloadImageTask(arrayPair, EditDiveActivity.mListPictures, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), 0);
							mDownloadImageTask.execute();
						}
					}
				});
			}
		}).start();
		return mRootView;
	}

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
					if (mPosition < mArrayPair.size() - 1 && mModel != null && mArrayPair != null && TabEditPhotosFragment.this != null && mModel.getDives() != null)
					{
						if (getActivity() != null)
						{
							mDownloadImageTask = new DownloadImageTask(mArrayPair, EditDiveActivity.mListPictures, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), mPosition + 1);
							mDownloadImageTask.execute();
						}
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

	private final class MyTouchListener implements OnLongClickListener
	{
		private int mImagePosition;


		public MyTouchListener(int i)
		{
			mImagePosition = i;
		}

		@Override
		public boolean onLongClick(View view) {
			EditDiveActivity.mImageSelected = mImagePosition;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				ClipData data = ClipData.newPlainText("", "");
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder((View)view.getParent());
				((View)view.getParent()).startDrag(data, shadowBuilder, view, 0);
				//((View)view.getParent()).setVisibility(View.INVISIBLE);
				mChangeItem.setVisibility(View.VISIBLE);
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_down);
				//use this to make it longer:  animation.setDuration(1000);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						//		        	ScrollView scroll = ((ScrollView)mRootView.findViewById(R.id.scroll));
						//		        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						//		        	params.addRule(RelativeLayout.BELOW, mChangeItem.getId());
						//		        	scroll.setLayoutParams(params);
					}
				});

				((ScrollView)mRootView.findViewById(R.id.scroll)).startAnimation(animation);
				
			}
			else{
				EditDiveActivity.mMenuType = 2;
				((EditDiveActivity) getActivity()).goToMenuV2(view);
			}
			return true;
		}
	}



	//	private class DropPictureTask extends AsyncTask<Void, Void, Void>
	//	{
	//
	//		@Override
	//		protected Void doInBackground(Void... params) {
	//			EditDiveActivity.mListPictures.
	//			return null;
	//		}
	//		
	//	}

	private class MyMainDragListener implements OnDragListener {
		private int mPosition;
		Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
		Drawable normalShape = getResources().getDrawable(R.drawable.shape);
		private ImageView mChoosenImage;

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:

				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				ImageView myIcon = (ImageView) v.findViewById(R.id.main_icon); 
				((TextView) v.findViewById(R.id.main_text)).setTextColor(getActivity().getResources().getColor(R.color.yellow));
				//ColorFilter filter = new LightingColorFilter(getActivity().getResources().getColor(R.color.yellow), getActivity().getResources().getColor(R.color.yellow));
				ColorFilter filter = new PorterDuffColorFilter(getActivity().getResources().getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);
				myIcon.setColorFilter(filter);
				//v.setBackgroundDrawable(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				((ImageView) v.findViewById(R.id.main_icon)).setColorFilter(null);
				((TextView) v.findViewById(R.id.main_text)).setTextColor(Color.WHITE);
				//v.setBackgroundDrawable(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				mChoosenImage = (ImageView) event.getLocalState();
//				ViewGroup owner = (ViewGroup) mChoosenImage.getParent();
//				owner.removeView(mChoosenImage);
//				mChoosenImage.setVisibility(View.VISIBLE);
				//			RelativeLayout container = (RelativeLayout) v;
				//			container.addView(view);

				Collections.swap(EditDiveActivity.mListPictures,0, EditDiveActivity.mImageSelected);
				mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(EditDiveActivity.mListPictures);
				
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				

				System.out.println("Set as main picture");
				((ImageView) v.findViewById(R.id.main_icon)).setColorFilter(null);
				((TextView) v.findViewById(R.id.main_text)).setTextColor(Color.WHITE);
				//mChangeItem.setVisibility(View.GONE);
				
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
				((ScrollView)mRootView.findViewById(R.id.scroll)).startAnimation(animation);
				//v.setBackgroundDrawable(normalShape);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}

					@Override
					public void onAnimationRepeat(Animation animation) {}

					@Override
					public void onAnimationEnd(Animation animation) {
						TableRow row = (TableRow)((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getChildAt(0);
						LinearLayout layout = (LinearLayout) row.getChildAt(0);
						ImageView firstimage = (ImageView) layout.getChildAt(0);
						Drawable firstdrawable = firstimage.getDrawable();
						ImageView choosenimage = (ImageView) mChoosenImage;
						Drawable choosedrawable = choosenimage.getDrawable();
						firstimage.setImageDrawable(choosedrawable);
						choosenimage.setImageDrawable(firstdrawable);
						
//						System.out.println("entre");
//						Intent intent = getActivity().getIntent();
//						getActivity().finish();
						//startActivity(intent);
						//EditDiveActivity.adapterViewPager.notifyDataSetChanged();
						//		        	ScrollView scroll = ((ScrollView)mRootView.findViewById(R.id.scroll));
						//		        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						//		        	params.addRule(RelativeLayout.BELOW, mChangeItem.getId());
						//		        	scroll.setLayoutParams(params);
					}
				});
				
			default:
				break;
			}
			return true;
		}
	}

	private class MyDropDragListener implements OnDragListener {
		Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
		Drawable normalShape = getResources().getDrawable(R.drawable.shape);

		@Override
		public boolean onDrag(View v, DragEvent event) {
			int action = event.getAction();
			ImageView view;
			ViewGroup owner;
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:

				break;
			case DragEvent.ACTION_DRAG_ENTERED:
				ImageView myIcon = (ImageView) v.findViewById(R.id.drop_icon);
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.RED);
				ColorFilter filter = new LightingColorFilter(Color.RED, Color.RED);
				myIcon.setColorFilter(filter);
				//v.setBackgroundDrawable(enterShape);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.WHITE);
				((ImageView) v.findViewById(R.id.drop_icon)).setColorFilter(null);
				//v.setBackgroundDrawable(normalShape);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				view = (ImageView) event.getLocalState();
				owner = (ViewGroup) view.getParent();
				owner.removeView(view);
				//				RelativeLayout container = (RelativeLayout) v;
				//				container.addView(view);
				view.setVisibility(View.VISIBLE);

				System.out.println("drop picture");

				//ArrayList<Picture> listPictures = new ArrayList<Picture>();
				EditDiveActivity.mListPictures.remove(EditDiveActivity.mImageSelected);
				mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(EditDiveActivity.mListPictures);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.WHITE);
				((ImageView) v.findViewById(R.id.drop_icon)).setColorFilter(null);
				view = (ImageView) event.getLocalState();
				((ImageView)((RelativeLayout)v).getChildAt(0)).setColorFilter(null);
				//mChangeItem.setVisibility(View.GONE);
				ScrollView scroll = ((ScrollView)mRootView.findViewById(R.id.scroll));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.BELOW, -1);
				scroll.setLayoutParams(params);
				//v.setBackgroundDrawable(normalShape);
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
				((ScrollView)mRootView.findViewById(R.id.scroll)).startAnimation(animation);
				//	        	mDropPictureTask = new DropPictureTask();
				//	        	mDropPictureTask.execute();
			default:
				break;
			}
			return true;
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
