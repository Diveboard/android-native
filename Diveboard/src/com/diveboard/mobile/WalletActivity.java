package com.diveboard.mobile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
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
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

import com.diveboard.config.AppConfig;
import com.diveboard.model.ContentExtractor;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.diveboard.model.User;

public class WalletActivity extends NavDrawer {

	private DiveboardModel 				mModel;
	private Context						mContext;
	private ArrayList<Picture>			mListPictures;
	private ArrayList<Integer>			mPicturesIDS;
	private ArrayList <ImageView> 		mImageArray = new ArrayList<ImageView>();
	
	public final int 					SELECT_PICTURE = 1;
	public final int 					TAKE_PICTURE = 2;
	private RelativeLayout 				mChangeItem;
	public int 							mMenuType = 0;
	private int 						nbPicture;
	private int 						size;
	private Size 						mSizePicture;
	private ImageView 					mAddPhotoView;
	private DownloadImageTask			mDownloadImageTask;
	private UploadPictureTask 			mUploadPictureTask = null;
	public boolean 						isAddingPic = false;
	private int 						mImageSelected;
	public static int					mUploadProgress;
	ConnectivityManager 				_connMgr;
	NetworkInfo 						networkInfo;
	private boolean						mIsUploading = false;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_wallet);
//		setContentView(R.layout.activity_wallet); DONT CALL SET CONTENT VIEW IF CURRENT ACTIVITY IS INHERITING FROM NAVDRAWER
		ApplicationController AC = (ApplicationController) getApplicationContext();
		mModel = AC.getModel();
		mContext = getApplicationContext();
		mListPictures = new ArrayList<Picture>();
		mPicturesIDS = new ArrayList<Integer>();
		if(mModel.getUser().getWalletPictureIds() != null){
			for(int i = 0; i < mModel.getUser().getWalletPictureIds().size(); i++){
				mListPictures.add(mModel.getUser().getWalletPictures().get(i));
				mPicturesIDS.add(mModel.getUser().getWalletPictureIds().get(i));
			}
			
		}
		_connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = _connMgr.getActiveNetworkInfo();
		generateTableLayout();
//		else {
//			Toast toast = Toast.makeText(mContext, getResources().getString(R.string.no_internet_co_wallet),Toast.LENGTH_LONG);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
//			finish();
//		}
		
		Button save = (Button) findViewById(R.id.save_button);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(!mIsUploading){
					User user = mModel.getUser();
					mModel.getDataManager().save(user);
//					mModel.updateUser();
					finish();
				}
				else{
					Toast toast = Toast.makeText(mContext, getResources().getString(R.string.upload_not_finished),Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
					
			}
		});
		
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
					final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DiveboardPictures/"; 
					File file = new File(dir+"diveboard.jpg");
					//((ProgressBar)findViewById(R.id.progress)).setVisibility(View.VISIBLE);
					LinearLayout parent = (LinearLayout) mAddPhotoView.getParent();
					ProgressBar bar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
					RelativeLayout newObj = new RelativeLayout(mContext);
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.CENTER_IN_PARENT);
					bar.setVisibility(View.VISIBLE);
					bar.setLayoutParams(params);
					bar.setId(1000);
					newObj.setGravity(Gravity.CENTER);
					mAddPhotoView.setVisibility(View.GONE);
					ImageView newPic = new ImageView(mContext);
					newPic.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
					newPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
					newPic.setAlpha((float)(0.5));
					Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
					newPic.setImageBitmap(b);
					newObj.addView(newPic);
					newObj.addView(bar);
					parent.addView(newObj);
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
						LinearLayout parent = (LinearLayout) mAddPhotoView.getParent();
						ProgressBar bar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleHorizontal);
						RelativeLayout newObj = new RelativeLayout(mContext);
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
						params.addRule(RelativeLayout.CENTER_IN_PARENT);
						bar.setVisibility(View.VISIBLE);
						bar.setLayoutParams(params);
						ImageView newPic = new ImageView(mContext);
						newPic.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
						newPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
						bar.setId(1000);
						newObj.setGravity(Gravity.CENTER);
						newObj.setLayoutParams(params);
						newPic.setAlpha((float)(0.5));
						Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
						newPic.setImageBitmap(b);
						newObj.addView(newPic);
						newObj.addView(bar);
						parent.addView(newObj);
						mAddPhotoView.setVisibility(View.GONE);
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
	
	public void generateTableLayout()
	{
		Typeface mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		TextView mTitle = (TextView) findViewById(R.id.title);
		mTitle.setTypeface(mFaceB);
		mTitle.setText(getResources().getString(R.string.title_banner_wallet));
		Button save = (Button) findViewById(R.id.save_button);
		save.setTypeface(mFaceB);
		save.setText(getResources().getString(R.string.save_button));
		System.out.println("There are " + mListPictures.size() + " photos");
		
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
		RelativeLayout.LayoutParams changeitemparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight / 5);
		mChangeItem.setLayoutParams(changeitemparams);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			nbPicture = 3;	
		}
		else
		{
//			double temp = ((double)screenWidth / (double)screenHeight);
//			nbPicture = (int) (temp * 3.0);
//			System.out.println(screenWidth + " " + screenHeight + " " + nbPicture);
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
		mImageArray.clear();
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);
		tableLayout.removeAllViews();
		int i = 0;
		while (i < mListPictures.size())
		{
			TableRow row = new TableRow(mContext);
			for (int j = 0; j < nbPicture && i < mListPictures.size(); j++)
			{
				size = screenWidth;
				LinearLayout linearLayout = new LinearLayout(mContext);
				TableRow.LayoutParams tbLP = new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				tbLP.gravity = Gravity.CENTER;
//				tbLP.setMargins(2, 2, 2, 2);
				linearLayout.setGravity(Gravity.CENTER);
				linearLayout.setLayoutParams(tbLP);
				ImageView imageView = new ImageView(mContext);
				imageView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setContentDescription(String.valueOf(i));
				imageView.setVisibility(View.GONE);
//				imageView.setPadding(6, 6, 6, 6);
				imageView.setOnLongClickListener(new MyTouchListener(i));       /* Create this listener */
				
				if (mListPictures.size() > 0)
				{
//					Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mListPictures.get(i));
					mImageArray.add(imageView);
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {    //Access to the wallet pictures of the current user
						ApplicationController AC = (ApplicationController) getApplicationContext();
						if (mModel.getUser().getWalletPictures() != null && mModel.getUser().getWalletPictures().size() != 0)
						{
							Intent galleryCarousel = new Intent( getApplicationContext(), GalleryCarouselActivity.class);
							galleryCarousel.putExtra("activity", "wallet");
							galleryCarousel.putExtra("position", Integer.valueOf(v.getContentDescription().toString()));
							startActivity(galleryCarousel);
						}	
					}
				});
				linearLayout.addView(imageView);
				LinearLayout parent = (LinearLayout) imageView.getParent();
				ProgressBar bar = new ProgressBar(mContext);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				bar.setVisibility(View.VISIBLE);
				bar.setLayoutParams(params);
				bar.setIndeterminate(false);
//				linearLayout.addView(bar);
				parent.addView(bar);
				row.addView(linearLayout);
				i++;
			}
			tableLayout.addView(row);
		}
		//We add the + icon
		if (mListPictures.size() % nbPicture == 0)
		{

			// Add a new line
			TableRow row = new TableRow(mContext);
//			int size = ((TableLayout)(findViewById(R.id.tablelayout))).getMeasuredWidth();
			int size = screenWidth;
			LinearLayout linearLayout = new LinearLayout(mContext);
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mAddPhotoView = new ImageView(mContext);
			mAddPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
			mAddPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mAddPhotoView.setContentDescription(String.valueOf(i));
			mAddPhotoView.setImageResource(R.drawable.ic_add);
			mAddPhotoView.setOnClickListener(new OnClickListener() {

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
			linearLayout.addView(mAddPhotoView);
			row.addView(linearLayout);
			tableLayout.addView(row);
		}
		else
		{
			// Use the same row
			TableRow row = (TableRow)((TableLayout)(findViewById(R.id.tablelayout))).getChildAt(mListPictures.size() / nbPicture);
			int size = screenWidth;
			LinearLayout linearLayout = new LinearLayout(mContext);
			TableRow.LayoutParams tbparam = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
			tbparam.gravity = Gravity.CENTER;
			linearLayout.setGravity(Gravity.CENTER);
			linearLayout.setLayoutParams(tbparam);
			mAddPhotoView = new ImageView(mContext);
			mAddPhotoView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
			mAddPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			mAddPhotoView.setContentDescription(String.valueOf(i));
			mAddPhotoView.setImageResource(R.drawable.ic_add);
			mAddPhotoView.setOnClickListener(new OnClickListener() {

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
			linearLayout.addView(mAddPhotoView);
			row.addView(linearLayout);
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)					//Control drag action
		{
			((RelativeLayout)(findViewById(R.id.share_item))).setOnDragListener(new MyShareDragListener());
			((RelativeLayout)(findViewById(R.id.drop_item))).setOnDragListener(new MyDropDragListener());
			
		}
		
		if(mListPictures.size() > 0){
			mDownloadImageTask = new DownloadImageTask(mImageArray, mContext, 0);
			mDownloadImageTask.execute();
		}
			
	}
	
	public void goToMenuV2(View view)
	{
		// Settings floating menu
		registerForContextMenu(view);
		openContextMenu(view);
		unregisterForContextMenu(view);
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
					final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/DiveboardPictures/"; 
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
			_connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = _connMgr.getActiveNetworkInfo();
		}

		protected Bitmap doInBackground(Void... voids)
		{
			try {
				if (mContext != null){	
//					if(mListPictures == null && networkInfo != null && networkInfo.isConnected()){
//						System.out.println("Downloading URL's of wallet pictures");
//						mListPictures = mModel.getUser().getWallet().downloadWalletPictures(mContext);
//						if(mListPictures != null){
//							mModel.getUser().setWalletPictures(mListPictures);
//						}
//							
//						else
//							mListPictures = mModel.getUser().getWalletPictures();
//					}
//					if(mListPictures== null){
//						System.out.println("mListPictures is null");
//					}
//					else
//						System.out.println("mListPictures is NOT null");
					if(mListPictures.size() == mPicturesIDS.size() && mListPictures.size() > 0){
						System.out.println("Trying to assign bitmap to imageview " + mPosition);
						Bitmap rs = mListPictures.get(mPosition).getPicture(mContext, mSizePicture);
						System.out.println("Wallet Picture renderized and returned properly");
						return rs;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println("There was an error downloading the picture, check connection");
			return null;
		}

		protected void onPostExecute(Bitmap result)
		{
			if (imageViewReference != null && result != null)
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
						mDownloadImageTask = new DownloadImageTask(mImageViewArray, mContext, mPosition + 1);
						mDownloadImageTask.execute();
						System.out.println("position = " + mPosition);
					}
				}
			}else{
				Toast toast = Toast.makeText(mContext, getResources().getString(R.string.no_internet_co_wallet),Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				finish();
			}
		}

		@Override
		protected void onCancelled() {
			mDownloadImageTask = null;
		}
	}
	
	private class UploadPictureTask extends AsyncTask<Void, Integer, Void>
	{
		private File mFile;
		private Picture picture = null;
		private Integer pictureId = null;
		LinearLayout rl = (LinearLayout)(mAddPhotoView.getParent());
		ProgressBar bar = (ProgressBar)findViewById(1000);
		JSONObject result = new JSONObject();
		
		
		public UploadPictureTask(File file)
		{
			mUploadProgress = 0;
			mFile = file;
//			bar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_circle));
			bar.setIndeterminate(false);
			bar.setProgress(0);
			bar.setMax(100);
			mIsUploading = false;
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			System.out.println("Uploading picture to the server ");
//			JSONObject result = mModel.uploadWalletPicture(mFile);
			
			
			File picture_file = mFile;
			HttpClient							httpClient = new DefaultHttpClient();
			HttpContext							localContext = new BasicHttpContext();
			HttpPost							httpPost = new HttpPost(AppConfig.SERVER_URL + "/api/picture/upload");
			
			NetworkInfo networkInfo = _connMgr.getActiveNetworkInfo();
			// Test connectivity
			if (networkInfo != null && networkInfo.isConnected())
			{
				try {
					mIsUploading = true;
					MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
					Bitmap bm = BitmapFactory.decodeFile(picture_file.getPath());
					mUploadProgress += 5;
					publishProgress(mUploadProgress);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					mUploadProgress += 5;
					publishProgress(mUploadProgress);
					bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
					mUploadProgress += 10;
					publishProgress(mUploadProgress);
					byte[] b = baos.toByteArray();
					//String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
					entity.addPart("qqfile", new ByteArrayBody(b, "file.jpg"));
					entity.addPart("auth_token", new StringBody(mModel.getToken()));
					entity.addPart("apikey", new StringBody("xJ9GunZaNwLjP4Dz2jy3rdF"));
					entity.addPart("flavour", new StringBody("private"));
//					entity.addPart("album", new StringBody("wallet"));
					httpPost.setEntity(entity);
					mUploadProgress += 20;
					publishProgress(mUploadProgress);
					HttpResponse response = httpClient.execute(httpPost, localContext);
					mUploadProgress += 20;
					publishProgress(mUploadProgress);
					HttpEntity entity_response = response.getEntity();
					String res = ContentExtractor.getASCII(entity_response);
					System.out.println("WALLET PICTURE UPLOADED SUCCESSFULLY!\n" + res);
					mUploadProgress += 10;
					publishProgress(mUploadProgress);
					JSONObject json = new JSONObject(res);
					if (json.getBoolean("success") == false)
						result = null;
					result = json;
//					return (json);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					result = null;
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					result = null;
				} catch (IOException e) {
					e.printStackTrace();
					result = null;
				} catch (JSONException e) {
					e.printStackTrace();
					result = null;
				}finally{
					
				}
			}
			else
				result = null;
			
			
			
			try{
				if(result != null && result.getBoolean("success")){
//					Wallet tmp = mModel.getUser().getWallet();
					picture = new Picture(result.getJSONObject("result"));
					picture.storePicture(mContext);
					mUploadProgress += 20;
					publishProgress(mUploadProgress);
					pictureId = result.getJSONObject("picture").getInt("id");
					mListPictures.add(picture);
					mPicturesIDS.add(pictureId);
					System.out.println("Picture " + pictureId + " was added to the wallet");
					mModel.getUser().setWalletPictures(mListPictures);
					mModel.getUser().setWalletPictureIds(mPicturesIDS);
					mUploadProgress += 10;
					publishProgress(mUploadProgress);
				}
			}catch (JSONException e){
				e.printStackTrace();
			}catch (IOException e){
				e.printStackTrace();
			}finally{
				mIsUploading = false;
			}
			
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			bar.setIndeterminate(false);
			bar.setProgress(values[0]);
//			bar.incrementProgressBy(values[0]);
			System.out.println("Progress of " + bar.getProgress() + "%");
		}
		
		@Override
		protected void onPostExecute(Void res) {
				mAddPhotoView.setVisibility(View.VISIBLE);
				bar.setVisibility(View.GONE);
				generateTableLayout();
				isAddingPic = false;
				if(result == null){
					Toast toast = Toast.makeText(mContext, getResources().getString(R.string.upload_error),Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
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
					Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_down);
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

					((ScrollView)findViewById(R.id.scroll)).startAnimation(animation);
					
				}
				else{
					mMenuType = 2;
					goToMenuV2(view);
				}
			}
			
			return true;
		}
	}
	
	private class MyShareDragListener implements OnDragListener {
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
				ImageView myIcon = (ImageView) v.findViewById(R.id.share_icon); 
				((TextView) v.findViewById(R.id.share_text)).setTextColor(getResources().getColor(R.color.yellow));
				ColorFilter filter = new PorterDuffColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.MULTIPLY);
				myIcon.setColorFilter(filter);
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				((ImageView) v.findViewById(R.id.share_icon)).setColorFilter(null);
				((TextView) v.findViewById(R.id.share_text)).setTextColor(Color.WHITE);
				break;
			case DragEvent.ACTION_DROP:
				mChoosenImage = (ImageView) event.getLocalState();
//				Intent share = new Intent(Intent.ACTION_SEND);
//				startActivity(Intent.createChooser(share, "Share Image"));
//				System.out.println(Integer.valueOf(v.getContentDescription().toString()));

				Bitmap icon = null;
				try {
					icon = mListPictures.get(Integer.valueOf(mChoosenImage.getContentDescription().toString()))
							.getPicture(mContext);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Intent share = new Intent(Intent.ACTION_SEND);
				share.setType("image/jpeg");
				ByteArrayOutputStream bytes = new ByteArrayOutputStream();
				icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
				File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
				try {
					f.createNewFile();
					FileOutputStream fo = new FileOutputStream(f);
					fo.write(bytes.toByteArray());
					fo.flush();
					fo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				share.putExtra(Intent.EXTRA_STREAM,Uri.parse("file:///sdcard/temporary_file.jpg"));
				startActivity(Intent.createChooser(share, "Share Image"));
				
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				System.out.println("Share action ended");
				((ImageView) v.findViewById(R.id.share_icon)).setColorFilter(null);
				((TextView) v.findViewById(R.id.share_text)).setTextColor(Color.WHITE);
				
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
				break;
			case DragEvent.ACTION_DRAG_EXITED:
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.WHITE);
				((ImageView) v.findViewById(R.id.drop_icon)).setColorFilter(null);
				break;
			case DragEvent.ACTION_DROP:
				// Dropped, reassign View to ViewGroup
				System.out.println("drop picture");
				System.out.println("index =" + mImageSelected);
//				Wallet tmp = mModel.getUser().getWallet();
				mListPictures.remove(mImageSelected);
				mPicturesIDS.remove(mImageSelected);
				mModel.getUser().setWalletPictures(mListPictures);
				mModel.getUser().setWalletPictureIds(mPicturesIDS);
				
				break;
			case DragEvent.ACTION_DRAG_ENDED:
				System.out.println("action drag ended drop");
				
				((TextView) v.findViewById(R.id.drop_text)).setTextColor(Color.WHITE);
				((ImageView) v.findViewById(R.id.drop_icon)).setColorFilter(null);
				view = (ImageView) event.getLocalState();
				((ImageView)((RelativeLayout)v).getChildAt(0)).setColorFilter(null);
				mChangeItem.setVisibility(View.GONE);
				generateTableLayout();
				ScrollView scroll = ((ScrollView) findViewById(R.id.scroll));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.BELOW, -1);
				scroll.setLayoutParams(params);
				//v.setBackgroundDrawable(normalShape);
				Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_up);
				scroll.startAnimation(animation);
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
			default:
				break;
			}
			return true;
		}
	}
	
	@Override
	public void onBackPressed()
	{
		if (mModel.getUser().getEditList().size() > 0)
		{
			SaveChangesDialog dialog = new SaveChangesDialog(this);
			dialog.show();
		}
		else
		{
			finish();
		}
	};
	
	public class SaveChangesDialog extends Dialog implements android.view.View.OnClickListener{
		public SaveChangesDialog(Activity a) {
			super(a);
		}
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Typeface faceR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
//			View view = findViewById(R.layout.dialog_edit_confirm);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_edit_confirm);
			TextView title = (TextView) findViewById(R.id.title);
			title.setTypeface(faceR);
			title.setText(getResources().getString(R.string.edit_confirm_title));
	        
			Button cancel = (Button) findViewById(R.id.cancel);
			cancel.setTypeface(faceR);
			cancel.setText(getResources().getString(R.string.cancel));
			cancel.setOnClickListener(this);
			Button save = (Button) findViewById(R.id.save);
			save.setTypeface(faceR);
			save.setText(getResources().getString(R.string.save));
			save.setOnClickListener(this);
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    case R.id.cancel:
		      break;
		    case R.id.save:
		    	mModel.getUser().clearEditList();
//				Intent intent = new Intent(mContext, DivesActivity.class);
//				startActivity(intent);
				finish();
		      break;
		    default:
		      break;
		    }
		    dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wallet, menu);
		return true;
	}

}
