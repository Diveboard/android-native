package com.diveboard.mobile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Picture.Size;
import com.diveboard.model.Wallet;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class WalletActivity extends Activity {

	private DiveboardModel 				mModel;
	public ArrayList<Picture>			mListPictures = null;
	ArrayList<Pair<ImageView, Picture>> arrayPair = new ArrayList<Pair<ImageView, Picture>>();
	public final int 					SELECT_PICTURE = 1;
	public final int 					TAKE_PICTURE = 2;
	private RelativeLayout 				mChangeItem;
	private int 						nbPicture;
	private Size 						mSizePicture;
	public ImageView 					mPhotoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallet);
		ApplicationController AC = (ApplicationController) getApplicationContext();
		mModel = AC.getModel();
		mModel.getUser().getId();
		generateTableLayout();
	}
	
	public void generateTableLayout()
	{
		Typeface mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
		
		
		System.out.println("There are " + mListPictures.size() + " photos");
		
		int screenWidth;
		int screenheight;


		screenWidth = findViewById(R.id.tablelayout).getMeasuredWidth();
		screenheight = findViewById(R.id.tablelayout).getMeasuredHeight();
		((TextView) findViewById(R.id.drop_text)).setTypeface(mFaceB);
		RelativeLayout.LayoutParams dropitemparam = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
		dropitemparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		((RelativeLayout) findViewById(R.id.drop_item)).setLayoutParams(dropitemparam);

		((TextView) findViewById(R.id.share_text)).setTypeface(mFaceB);
		RelativeLayout.LayoutParams shareLP = new RelativeLayout.LayoutParams(screenWidth / 2, RelativeLayout.LayoutParams.MATCH_PARENT);
		shareLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		((RelativeLayout) findViewById(R.id.share_item)).setLayoutParams(shareLP);

		mChangeItem = (RelativeLayout) findViewById(R.id.change_item);
		RelativeLayout.LayoutParams changeitemparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, screenheight / 5);
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
		
		
		
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tablelayout);
		tableLayout.removeAllViews();
		arrayPair.clear();
		int i = 0;
		while (i < mListPictures.size())
		{
			TableRow row = new TableRow(getApplicationContext());
			for (int j = 0; j < nbPicture && i < mListPictures.size(); j++)
			{
				int size = ((TableLayout)( findViewById(R.id.tablelayout))).getMeasuredWidth();
				LinearLayout linearLayout = new LinearLayout(getApplicationContext());
				TableRow.LayoutParams tbLP = new TableRow.LayoutParams(size / nbPicture, size / nbPicture);
				tbLP.gravity = Gravity.CENTER;
				linearLayout.setGravity(Gravity.CENTER);
				linearLayout.setLayoutParams(tbLP);
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(new RelativeLayout.LayoutParams(size / nbPicture, size / nbPicture));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setContentDescription(String.valueOf(i));
				imageView.setVisibility(View.GONE);
//				imageView.setOnLongClickListener(new MyTouchListener(i));       /* Create this listener */
				
				Wallet myWallet = new Wallet();
				
				ApplicationController AC = (ApplicationController) getApplicationContext();
				if (myWallet.getPictures() != null)
				{
					Pair<ImageView, Picture> pair = new Pair<ImageView, Picture>(imageView, mListPictures.get(i));
					arrayPair.add(pair);
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
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				bar.setVisibility(View.VISIBLE);
				bar.setLayoutParams(params);
				linearLayout.addView(new ProgressBar(getApplicationContext()));
				row.addView(linearLayout);
				i++;
			}
			tableLayout.addView(row);
		}
		
		if (mListPictures.size() % nbPicture == 0)
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
			TableRow row = (TableRow)((TableLayout)(findViewById(R.id.tablelayout))).getChildAt(mListPictures.size() / nbPicture);
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
//		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
//		if (AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures() != null
//				&& AC.getModel().getDives().get(getActivity().getIntent().getIntExtra("index", -1)).getPictures().size() != 0)
//		{
//			mDownloadImageTask = new DownloadImageTask(arrayPair, mListPictures, getActivity().getApplicationContext(),  mModel.getDives().get(getActivity().getIntent().getIntExtra("index", -1)), 0);
//			mDownloadImageTask.execute();
//		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wallet, menu);
		return true;
	}

}
