package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;

import java.util.ArrayList;

import com.diveboard.model.Buddy;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.ImageCache.ImageCacheParams;
import com.diveboard.util.ImageFetcher;
import com.diveboard.util.Utils;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class					TabEditBuddiesFragment extends Fragment
{
	private GridView			mGridView;
	private int					mIndex;
	private DiveboardModel		mModel;
	private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;
    private ImageAdapter mAdapter;
    
    public TabEditBuddiesFragment() {
    	
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		mModel = ((ApplicationController)getActivity().getApplicationContext()).getModel();
		mIndex = getActivity().getIntent().getIntExtra("index", -1);
		setHasOptionsMenu(false);
		mAdapter = new ImageAdapter(getActivity());
		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
		// The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_buddies, container, false);
    	mGridView = (GridView)rootView.findViewById(R.id.gridView);
    	Dive dive = mModel.getDives().get(mIndex);
    	mGridView.setAdapter(mAdapter);
    	mGridView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
    	// This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                //mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
//                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
		return rootView;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		AC.handleLowMemory();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        //private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
        private ArrayList<Buddy> mOldBuddies;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            
            ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
            mOldBuddies = AC.getModel().getOldBuddies();
            // Calculate ActionBar height
//            TypedValue tv = new TypedValue();
//            if (context.getTheme().resolveAttribute(
//                    android.R.attr.actionBarSize, tv, true)) {
//                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
//                        tv.data, context.getResources().getDisplayMetrics());
//            }
        }

        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
//            if (getNumColumns() == 0) {
//                return 0;
//            }

            // Size + number of columns for top empty row
            //return Images.imageThumbUrls.length + mNumColumns;
        	return mOldBuddies.size();
        }

//        @Override
//        public Object getItem(int position) {
//        	//return Images.imageThumbUrls[position - mNumColumns];
//        	ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
//        	try {
//				return (Bitmap)AC.getModel().getOldBuddies().get(position).getPicture().getPicture(mContext);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        	return null;
//        }

        @Override
        public long getItemId(int position) {
            return position - mNumColumns;
        }

//        @Override
//        public int getViewTypeCount() {
//            // Two types of views, the normal ImageView and the top row of empty views
//            return 1;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return 0;
//        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // First check if this is the top row
//            if (position < mNumColumns) {
//                if (convertView == null) {
//                    convertView = new View(mContext);
//                }
//                // Set empty view with height of ActionBar
//                convertView.setLayoutParams(new AbsListView.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
//                return convertView;
//            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage(mOldBuddies.get(position).getPicture()._urlDefault, imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
