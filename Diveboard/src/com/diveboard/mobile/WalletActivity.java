package com.diveboard.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.diveboard.model.ScreenSetup;

public class WalletActivity extends Activity {

	private DiveboardModel 				mModel;
	private Context						mContext;
	public ArrayList<Picture>			mListPictures = null;
	ArrayList <ImageView> 				mImageArray = new ArrayList<ImageView>();
	public final int 					SELECT_PICTURE = 1;
	public final int 					TAKE_PICTURE = 2;
	private RelativeLayout 				mChangeItem;
	private int 						nbPicture;
	private Size 						mSizePicture;
	public ImageView 					mPhotoView;
	private DownloadImageTask			mDownloadImageTask;
	private UploadPictureTask 			mUploadPictureTask = null;
	public boolean 						isAddingPic = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		ApplicationController AC = (ApplicationController) getApplicationContext();
		mModel = AC.getModel();
		mContext = getApplicationContext();
		mImageArray.clear();
		
		ConnectivityManager _connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			if (mModel.getUser().getWallet().isDownloaded)
				mListPictures = mModel.getUser().getWallet().getPicturesList();
			generateTableLayout();
		}
		else {
			Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet_co_wallet),Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			finish();
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//System.out.println(REQUEST_CODE + "=" + requestCode + ", " + Activity.RESULT_OK+"=" + resultCode);
		ConnectivityManager _connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
		// Test connectivity
		if (networkInfo != null && networkInfo.isConnected())
		{
			isAddingPic = true;
			if (resultCode == Activity.RESULT_OK)
			{
				if (requestCode == TAKE_PICTURE)
				{
					final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
					File file = new File(dir+"diveboard.jpg");
					//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
					LinearLayout parent = (LinearLayout) mPhotoView.getParent();
					ProgressBar bar = new ProgressBar(getApplicationContext());
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
						stream = getContentResolver().openInputStream(data.getData());
						System.out.println(data.getData());
						//InputStream stream = getContentResolver().openInputStream(data.getData());
						//final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/"; 
						final File file = new File(getCacheDir(), "temp_photo.jpg");
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
						ProgressBar bar = new ProgressBar(getApplicationContext());
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
			Toast toast = Toast.makeText(mContext, getResources().getString(R.string.check_connectivity),Toast.LENGTH_LONG);
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
			mPhotoView.setVisibility(View.VISIBLE);
			LinearLayout rl = (LinearLayout)(mPhotoView.getParent());
			ProgressBar bar = (ProgressBar)rl.getChildAt(1);
			bar.setVisibility(View.GONE);
			mListPictures.add(result);
			mModel.getUser().getWallet().setPicturesList(mListPictures);
			generateTableLayout();
			isAddingPic = false;
		}

	}
	
	
	public void generateTableLayout()
	{
		Typeface mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		TextView mTitle = (TextView) findViewById(R.id.title);
		mTitle.setTypeface(mFaceB);
		mTitle.setText(getResources().getString(R.string.title_banner_wallet));
		Button save = (Button) findViewById(R.id.save_button);
		save.setTypeface(mFaceB);
		save.setText(getResources().getString(R.string.save_button));
		System.out.println("There are " + mModel.getUser().getWallet().getSize() + " photos");
		
		int screenWidth;
		int screenHeight;

		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point mSize = new Point();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
			display.getSize(mSize);
				screenWidth = mSize.x;
				screenHeight = mSize.y;
		}else
		{
			display = wm.getDefaultDisplay();
			screenWidth = display.getWidth();
			screenHeight = display.getHeight();
		}
		((TextView) findViewById(R.id.drop_text)).setTypeface(mFaceB);
		RelativeLayout.LayoutParams dropitemparam = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
		dropitemparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		((RelativeLayout) findViewById(R.id.drop_item)).setLayoutParams(dropitemparam);

		((TextView) findViewById(R.id.share_text)).setTypeface(mFaceB);
		RelativeLayout.LayoutParams shareLP = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
		shareLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		((RelativeLayout) findViewById(R.id.share_item)).setLayoutParams(shareLP);

		mChangeItem = (RelativeLayout) findViewById(R.id.change_item);
		RelativeLayout.LayoutParams changeitemparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		mChangeItem.setLayoutParams(changeitemparams);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			nbPicture = 3;	
		}
		else
		{
			double temp = ((double)screenWidth / (double)screenHeight);
			nbPicture = (int) (temp * 3.0);
			System.out.println(screenWidth + " " + screenHeight + " " + nbPicture);
			nbPicture = 5;
		}
		System.out.println("screenheight = " + screenHeight);
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
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);
		tableLayout.removeAllViews();
		int i = 0;
		while (i < mModel.getUser().getWallet().getSize())
		{
			TableRow row = new TableRow(getApplicationContext());
			for (int j = 0; j < nbPicture && i < mModel.getUser().getWallet().getSize(); j++)
			{
				int size = screenWidth;
				LinearLayout linearLayout = new LinearLayout(getApplicationContext());
				TableRow.LayoutParams tbLP = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				tbLP.gravity = Gravity.CENTER;
				linearLayout.setGravity(Gravity.CENTER);
				linearLayout.setLayoutParams(tbLP);
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setContentDescription(String.valueOf(i));
				imageView.setVisibility(View.GONE);
//				imageView.setOnLongClickListener(new MyTouchListener(i));       /* Create this listener */
				
				
				if (mModel.getUser().getWallet().getSize() > 0)
				{
//					Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mListPictures.get(i));
					mImageArray.add(imageView);
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {    //Access to the wallet pictures of the current user
//						ApplicationController AC = (ApplicationController) getApplicationContext();
//						if (AC.getModel().getDives().get(getIntent().getIntExtra("index", -1)).getPictures() != null
//								&& AC.getModel().getDives().get(getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
//						{
//							Intent galleryCarousel = new Intent( getApplicationContext(), GalleryCarouselActivity.class);
//							galleryCarousel.putExtra("index", getIntent().getIntExtra("index", -1));
//							galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
//							startActivity(galleryCarousel);
//						}	

					}
				});
				linearLayout.addView(imageView);
				ProgressBar bar = new ProgressBar(getApplicationContext());
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (size / nbPicture), size / nbPicture );
				params.addRule(Gravity.CENTER);
				bar.setVisibility(View.VISIBLE);
				bar.setLayoutParams(params);
				linearLayout.addView(bar);
				row.addView(linearLayout);
				i++;
			}
			tableLayout.addView(row);
		}
		
		if (mModel.getUser().getWallet().getSize() % nbPicture == 0)
		{

			// Add a new line
			TableRow row = new TableRow(getApplicationContext());
			int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
			LinearLayout linearLayout = new LinearLayout(getApplicationContext());
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mPhotoView = new ImageView(getApplicationContext());
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
//					else
//					{
//						mMenuType = 1;
//						goToMenuV2(v);
//					}
						
				}
			});
			linearLayout.addView(mPhotoView);
			row.addView(linearLayout);
			tableLayout.addView(row);
		}
		else
		{
			// Use the same row
			TableRow row = (TableRow)((TableLayout)(findViewById(R.id.tablelayout))).getChildAt(mModel.getUser().getWallet().getSize() / nbPicture);
			int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
			LinearLayout linearLayout = new LinearLayout(getApplicationContext());
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mPhotoView = new ImageView(getApplicationContext());
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
//					else
//					{
//						mMenuType = 1;
//						goToMenuV2(v);
//					}
						
				}
			});
			linearLayout.addView(mPhotoView);
			row.addView(linearLayout);
		}
		
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)					//Control drag action
//		{
//			((RelativeLayout)(mRootView.findViewById(R.id.main_item))).setOnDragListener(new MyMainDragListener());
//			((RelativeLayout)(mRootView.findViewById(R.id.drop_item))).setOnDragListener(new MyDropDragListener());
//			
//		}
		
			mDownloadImageTask = new DownloadImageTask(mImageArray, mContext, 0);
			mDownloadImageTask.execute();
	}
	
	public void goToMenuV3(View view)
	{
		PopupMenu popup = new PopupMenu(this, view);
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
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
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
		private int mPosition;
		private ArrayList<ImageView> mImageViewArray;

		public DownloadImageTask(ArrayList<ImageView> imageArray, Context context, int position)
		{
			imageViewReference = new WeakReference<ImageView>(mImageArray.get(position));
			mContext = context;
			mPosition = position;
			mImageViewArray = imageArray;
		}

		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (mContext != null)
				{	
					if(mListPictures == null){
						System.out.println("Downloading URL's of wallet pictures");
						mListPictures = mModel.getUser().getWallet().downloadWalletPictures(mContext);
					}
						
					System.out.println("Trying to assign bitmap to imageview " + mPosition);
					Bitmap rs = mListPictures.get(mPosition).getPicture(mContext, mSizePicture);
					System.out.println("Wallet Picture renderized and returned properly");
					return rs;
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("There was an error downloading the picture, check connection");
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
					if (mPosition < mImageViewArray.size() - 1 && mModel != null && mImageViewArray != null)
					{
						mDownloadImageTask = new DownloadImageTask(
								mImageViewArray,
								getApplicationContext(),
								mPosition + 1);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wallet, menu);
		return true;
	}

}
