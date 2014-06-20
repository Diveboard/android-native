package com.diveboard.mobile.editdive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.GalleryCarouselActivity;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;

public class TabEditPhotosFragment extends Fragment {

	private DiveboardModel mModel;
	private int mPhotoPos = 0;
	private DownloadImageTask mDownloadImageTask;
	//private ImageView mImageView;
	//private List<Picture> mItems;
	private Size mSizePicture;
	private ViewGroup mRootView;
	private RelativeLayout mChangeItem;
	private int nbPicture;
	ArrayList<Pair<ImageView, Picture>> arrayPair = new ArrayList<Pair<ImageView, Picture>>();
	public final int SELECT_PICTURE = 1;
	public final int TAKE_PICTURE = 2;
	private UploadPictureTask mUploadPictureTask = null;
	public int mMenuType = 0;
	public int mImageSelected;
	public ImageView mPhotoView;
	public ArrayList<Picture>		mListPictures = null;//new ArrayList<Picture>();
	public boolean isAddingPic = false;
	
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
		mModel = AC.getModel();
		mListPictures = mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures();
		System.out.println("LIST PICTURE : " + mListPictures);
//		for (int i = 0; i < EditDiveActivity.mListPictures.size(); i++)
//		{
//			System.out.println(EditDiveActivity.mListPictures.get(i).getJson());
//		}
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
						generateTableLayout();
					}
				});
			}
		}).start();
		return mRootView;
	}
	
	@Override
	public void onDestroy() {
		if (mUploadPictureTask != null)
			mUploadPictureTask.cancel(true);
		super.onDestroy();
	}

	
//	@Override
//	public void onPause() {
//		if (mUploadPictureTask != null)
//			mUploadPictureTask.cancel(true);
//		super.onPause();
//	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//System.out.println(REQUEST_CODE + "=" + requestCode + ", " + Activity.RESULT_OK+"=" + resultCode);
		ConnectivityManager _connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			isAddingPic = true;
			if (resultCode == Activity.RESULT_OK)
			{
				
				if (requestCode == TAKE_PICTURE)
				{
					//					try {
					//InputStream stream = getContentResolver().openInputStream(data.getData());
					final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
					File file = new File(dir+"diveboard.jpg");
					//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
					LinearLayout parent = (LinearLayout) mPhotoView.getParent();
					ProgressBar bar = new ProgressBar(getActivity().getApplicationContext());
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
					bar.setVisibility(View.VISIBLE);
					bar.setLayoutParams(params);
					parent.addView(bar);
					mPhotoView.setVisibility(View.GONE);
					mUploadPictureTask = new UploadPictureTask(file);
					mUploadPictureTask.execute();
				}
				else if (requestCode == SELECT_PICTURE)
				{
					InputStream stream;
					try {
						stream = getActivity().getContentResolver().openInputStream(data.getData());
						System.out.println(data.getData());
						//InputStream stream = getContentResolver().openInputStream(data.getData());
						//final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
						final File file = new File(getActivity().getCacheDir(), "temp_photo.jpg");
						final OutputStream output = new FileOutputStream(file);
						final byte[] buffer = new byte[1024];
						int read;
						while ((read = stream.read(buffer)) != -1)
							output.write(buffer, 0, read);
						output.flush();
						output.close();
						stream.close();

						//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
						LinearLayout parent = (LinearLayout) mPhotoView.getParent();
						ProgressBar bar = new ProgressBar(getActivity().getApplicationContext());
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
						bar.setVisibility(View.VISIBLE);
						bar.setLayoutParams(params);
						parent.addView(bar);
						mPhotoView.setVisibility(View.GONE);
						mUploadPictureTask = new UploadPictureTask(file);
						mUploadPictureTask.execute();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			}
		}
		else
		{
			Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.check_connectivity),Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			
		}
		//		else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		//	        if (resultCode == RESULT_OK) {
		//	        	// Video captured and saved to fileUri specified in the Intent
		//	            Toast.makeText(this, "Video saved to:\n" +
		//	                     data.getData(), Toast.LENGTH_LONG).show();
		//	        } else if (resultCode == RESULT_CANCELED) {
		//	            // User cancelled the video capture
		//	        } else {
		//	            // Video capture failed, advise user
		//	        }
		//	    }
		super.onActivityResult(requestCode, resultCode, data);
	}

	
	private class UploadPictureTask extends AsyncTask<Void, Void, Picture>
	{
		private File mFile;

		public UploadPictureTask(File file)
		{
			mFile = file;
		}

		@Override
		protected Picture doInBackground(Void... arg0) {

			Picture picture = mModel.uploadPicture(mFile);
			return picture;
		}

		@Override
		protected void onPostExecute(Picture result) {
			//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.GONE);
			if(result != null){
				mListPictures.add(result);
				mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
			} else {
				Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.upload_error),Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
			
			mPhotoView.setVisibility(View.VISIBLE);
			LinearLayout rl = (LinearLayout)(mPhotoView.getParent());
			ProgressBar bar = (ProgressBar)rl.getChildAt(1);
			bar.setVisibility(View.GONE);
//			mListPictures.add(result);
//			mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
			generateTableLayout();
			isAddingPic = false;
		}

	}

	public void generateTableLayout()
	{
		try{
			Typeface mFaceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
			((TextView)mRootView.findViewById(R.id.drop_text)).setTypeface(mFaceB);
			((TextView)mRootView.findViewById(R.id.main_text)).setTypeface(mFaceB);
		} catch(NullPointerException e){
			System.out.println("Error while obtaining the font");
			e.printStackTrace();
		}
		System.out.println("il y a " + mListPictures.size() + " photos");
		
		int screenWidth;
		int screenheight;


		screenWidth = mRootView.findViewById(R.id.tablelayout).getMeasuredWidth();
		screenheight = mRootView.findViewById(R.id.tablelayout).getMeasuredHeight();
		RelativeLayout.LayoutParams dropitemparam = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
		dropitemparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		((RelativeLayout)mRootView.findViewById(R.id.drop_item)).setLayoutParams(dropitemparam);

		
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
			nbPicture = 5;
		}
		System.out.println("screenheight = " + screenheight);
		System.out.println("screenwidth = " + screenWidth);
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
		
		
		
		TableLayout tableLayout = (TableLayout)mRootView.findViewById(R.id.tablelayout);
		tableLayout.removeAllViews();
		arrayPair.clear();
		int i = 0;
		while (i < mListPictures.size())
		{
			TableRow row = new TableRow(getActivity().getApplicationContext());
			for (int j = 0; j < nbPicture && i < mListPictures.size(); j++)
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
					Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mListPictures.get(i));
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
		System.out.println("ENTRE " + mListPictures.size() % nbPicture);
		if (mListPictures.size() % nbPicture == 0)
		{

			// Add a new line
			TableRow row = new TableRow(getActivity().getApplicationContext());
			int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
			LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mPhotoView = new ImageView(getActivity().getApplicationContext());
			mPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
			mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mPhotoView.setContentDescription(String.valueOf(i));
			mPhotoView.setImageResource(R.drawable.ic_add);
			mPhotoView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					{
						goToMenuV3(v);
					}
					else
					{
						mMenuType = 1;
						goToMenuV2(v);
					}
						
				}
			});
			//imageView.setVisibility(View.GONE);
			linearLayout.addView(mPhotoView);
			//imageView.setBackgroundResource(R.drawable.item_selector_add_picture);
			row.addView(linearLayout);
			tableLayout.addView(row);
		}
		else
		{
			// Use the same row
			TableRow row = (TableRow)((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getChildAt(mListPictures.size() / nbPicture);
			int size = ((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getMeasuredWidth();
			LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mPhotoView = new ImageView(getActivity().getApplicationContext());
			//EditDiveActivity.mPhotoView.setTag("AddPicture");
			mPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
			mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mPhotoView.setContentDescription(String.valueOf(i));
			mPhotoView.setImageResource(R.drawable.ic_add);
			mPhotoView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					{
						goToMenuV3(v);
					}
					else
					{
						mMenuType = 1;
						goToMenuV2(v);
					}
						
				}
			});
			linearLayout.addView(mPhotoView);
			row.addView(linearLayout);
			//tableLayout.addView(row);
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
		{
			((RelativeLayout)(mRootView.findViewById(R.id.main_item))).setOnDragListener(new MyMainDragListener());
			((RelativeLayout)(mRootView.findViewById(R.id.drop_item))).setOnDragListener(new MyDropDragListener());
			
		}
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
				&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
		{
			mDownloadImageTask = new DownloadImageTask(arrayPair, mListPictures, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), 0);
			mDownloadImageTask.execute();
		}
	}
	
	public void goToMenuV2(View view)
	{
		// Settings floating menu
		registerForContextMenu(view);
		getActivity().openContextMenu(view);
		unregisterForContextMenu(view);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		//System.out.println("view = " + menu.);
		MenuInflater inflater = getActivity().getMenuInflater();
		if (mMenuType == 1)
			inflater.inflate(R.menu.edit_gallery, menu);
		else
			inflater.inflate(R.menu.edit_photo, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Intent intent;
		System.out.println("Entre");
		switch (item.getItemId()) {
		case R.id.take_picture:
			final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
			File newdir = new File(dir); 
			newdir.mkdirs();
			String file = dir+"test.jpg";
			File newfile = new File(file);
			try {
				newfile.createNewFile();
			} catch (IOException e) {}
			Uri outputFileUri = Uri.fromFile(newfile);
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			startActivityForResult(cameraIntent, TAKE_PICTURE);
			return true;
		case R.id.gallery:
			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(Intent.createChooser(intent,
					"Select Picture"), SELECT_PICTURE);
			return true;
		case R.id.remove:
			mListPictures.remove(mImageSelected);
			mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
			return true;
		case R.id.main:
			Collections.swap(mListPictures,0,mImageSelected);
			mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void goToMenuV3(View view)
	{
		PopupMenu popup = new PopupMenu(getActivity(), view);
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(R.menu.edit_gallery, popup.getMenu());
		popup.show();
		popup.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent;
				switch (item.getItemId()) {
				case R.id.take_picture:
					final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
					File newdir = new File(dir); 
					newdir.mkdirs();
					String file = dir+"diveboard.jpg";
					File newfile = new File(file);
					try {
						newfile.createNewFile();
					} catch (IOException e) {}
					Uri outputFileUri = Uri.fromFile(newfile);
					Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
					startActivityForResult(cameraIntent, TAKE_PICTURE);
					//					intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//			    	fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
					//			        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					//			        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
					//			        startActivityForResult(intent, TAKE_PICTURE);
					return true;
				case R.id.gallery:
					intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					startActivityForResult(Intent.createChooser(intent,
							"Select Picture"), SELECT_PICTURE);
					return true;
				default:
					return false;
				}
			}

		});

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
							mDownloadImageTask = new DownloadImageTask(mArrayPair, mListPictures, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), mPosition + 1);
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
			if (isAddingPic == false)
			{
				mImageSelected = mImagePosition;
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
					mMenuType = 2;
					goToMenuV2(view);
				}
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

				Collections.swap(mListPictures,0, mImageSelected);
				mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
				System.out.println("Set as main picture");
//				TableRow row = (TableRow)((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getChildAt(0);
//				LinearLayout layout = (LinearLayout) row.getChildAt(0);
//				ImageView firstimage = (ImageView) layout.getChildAt(0);
//				Drawable firstdrawable = firstimage.getDrawable();
//				ImageView choosenimage = (ImageView) mChoosenImage;
//				Drawable choosedrawable = choosenimage.getDrawable();
//				firstimage.setImageDrawable(choosedrawable);
//				choosenimage.setImageDrawable(firstdrawable);
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				System.out.println("action drag ended main");
				((ImageView) v.findViewById(R.id.main_icon)).setColorFilter(null);
				((TextView) v.findViewById(R.id.main_text)).setTextColor(Color.WHITE);
				
//				((ImageView) v.findViewById(R.id.main_icon)).setColorFilter(null);
//				((TextView) v.findViewById(R.id.main_text)).setTextColor(Color.WHITE);
//				//mChangeItem.setVisibility(View.GONE);
//				
//				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
//				((ScrollView)mRootView.findViewById(R.id.scroll)).startAnimation(animation);
//				//v.setBackgroundDrawable(normalShape);
//				animation.setAnimationListener(new AnimationListener() {
//					@Override
//					public void onAnimationStart(Animation animation) {}
//
//					@Override
//					public void onAnimationRepeat(Animation animation) {}
//
//					@Override
//					public void onAnimationEnd(Animation animation) {
//						TableRow row = (TableRow)((TableLayout)(mRootView.findViewById(R.id.tablelayout))).getChildAt(0);
//						LinearLayout layout = (LinearLayout) row.getChildAt(0);
//						ImageView firstimage = (ImageView) layout.getChildAt(0);
//						Drawable firstdrawable = firstimage.getDrawable();
//						ImageView choosenimage = (ImageView) mChoosenImage;
//						Drawable choosedrawable = choosenimage.getDrawable();
//						firstimage.setImageDrawable(choosedrawable);
//						choosenimage.setImageDrawable(firstdrawable);
//						
////						System.out.println("entre");
////						Intent intent = getActivity().getIntent();
////						getActivity().finish();
//						//startActivity(intent);
//						//EditDiveActivity.adapterViewPager.notifyDataSetChanged();
//						//		        	ScrollView scroll = ((ScrollView)mRootView.findViewById(R.id.scroll));
//						//		        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//						//		        	params.addRule(RelativeLayout.BELOW, mChangeItem.getId());
//						//		        	scroll.setLayoutParams(params);
//					}
//				});
				
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
//				view = (ImageView) event.getLocalState();
//				owner = (ViewGroup) view.getParent();
//				owner.removeView(view);
//				//				RelativeLayout container = (RelativeLayout) v;
//				//				container.addView(view);
//				view.setVisibility(View.VISIBLE);

				System.out.println("drop picture");

				//ArrayList<Picture> listPictures = new ArrayList<Picture>();
				System.out.println("index =" + mImageSelected);
				mListPictures.remove(mImageSelected);
				mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)).setPictures(mListPictures);
				
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				System.out.println("action drag ended drop");
				
				//mChangeItem.setVisibility(View.GONE);
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.WHITE);
				((ImageView) v.findViewById(R.id.drop_icon)).setColorFilter(null);
				view = (ImageView) event.getLocalState();
				((ImageView)((RelativeLayout)v).getChildAt(0)).setColorFilter(null);
				mChangeItem.setVisibility(View.GONE);
				generateTableLayout();
				ScrollView scroll = ((ScrollView)mRootView.findViewById(R.id.scroll));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.BELOW, -1);
				scroll.setLayoutParams(params);
				//v.setBackgroundDrawable(normalShape);
				Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_up);
				((ScrollView)mRootView.findViewById(R.id.scroll)).startAnimation(animation);
				animation.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						
						
					}
				});
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
