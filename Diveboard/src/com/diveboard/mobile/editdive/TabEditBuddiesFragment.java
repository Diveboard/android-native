package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.Md5Crypt;

import com.diveboard.model.Buddy;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.ExpandableHeightGridView;
import com.diveboard.util.ImageCache.ImageCacheParams;
import com.diveboard.util.ImageFetcher;
import com.diveboard.util.Utils;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					TabEditBuddiesFragment extends Fragment
{
	private ExpandableHeightGridView			mGridView;
	private ExpandableHeightGridView			mGridViewBuddies;
	private int					mIndex;
	private DiveboardModel		mModel;
	private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageFetcher mImageFetcher;
    private ImageAdapter mAdapter;
    private ImageAdapterBuddies mAdapterBuddies;
    ArrayList<Buddy> mOldBuddies;
    ArrayList<Buddy> mBuddies;
    ArrayList<String> mListString = new  ArrayList<String>();
    private ViewGroup mRootView;
    
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
    	ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
    	Dive dive = mModel.getDives().get(mIndex);
    	mOldBuddies = AC.getModel().getOldBuddies();
    	mBuddies = dive.getBuddies();
    	mAdapter = new ImageAdapter(getActivity());
		mAdapterBuddies = new ImageAdapterBuddies(getActivity());
//    	for (int i = 0; i < mOldBuddies.size(); i++)
//    	{
//    		System.out.println("Add String " + mOldBuddies.get(i).getPicture()._urlDefault);
//    		mListString.add(mOldBuddies.get(i).getPicture()._urlDefault);
//    	}
//    	for (int i = 0; i < mOldBuddies.size(); i++)
//        {
//        	System.out.println("TEST: "+ mOldBuddies.get(i).getNickname() + " " + mOldBuddies.get(i).getId());
//        }
    	mRootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_buddies, container, false);
    	mGridView = (ExpandableHeightGridView)mRootView.findViewById(R.id.gridView);
    	mGridViewBuddies = (ExpandableHeightGridView)mRootView.findViewById(R.id.gridViewBuddies);
    	mGridView.setAdapter(mAdapter);
    	mGridView.setExpanded(true);
    	mGridViewBuddies.setExpanded(true);
    	mGridViewBuddies.setAdapter(mAdapterBuddies);
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
    	mGridViewBuddies.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (mBuddies.get(position).getId() != null && !mOldBuddies.contains(mBuddies.get(position)))
					mOldBuddies.add(mBuddies.get(position));
				mBuddies.remove(position);
				mModel.getDives().get(mIndex).setBuddies(mBuddies);
				mAdapter.notifyDataSetChanged();
				mAdapterBuddies.notifyDataSetChanged();
				
			}
		});
    	mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				//System.out.println(position);
				if (!mBuddies.contains(mOldBuddies.get(position)))
				{
					mBuddies.add(mOldBuddies.get(position));
					System.out.println("Click on " + mOldBuddies.get(position).getNickname());
				}
					
				mOldBuddies.remove(position);
				mModel.getDives().get(mIndex).setBuddies(mBuddies);
				mAdapter.notifyDataSetChanged();
				mAdapterBuddies.notifyDataSetChanged();
				
			}
		});
    	mGridViewBuddies.setOnScrollListener(new OnScrollListener() {
			
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
                    public void onGlobalLayout()
                    {
                    	
                        if (mAdapter.getNumColumns() == 0)
                        {
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
        mGridViewBuddies.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout()
                    {
                    	
                        if (mAdapter.getNumColumns() == 0)
                        {
                            final int numColumns = (int) Math.floor(
                            		mGridViewBuddies.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridViewBuddies.getWidth() / numColumns) - mImageThumbSpacing;
                                //mAdapter.setNumColumns(numColumns);
                                mAdapterBuddies.setItemHeight(columnWidth);
//                                if (BuildConfig.DEBUG) {
//                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
//                                }
                                if (Utils.hasJellyBean()) {
                                	mGridViewBuddies.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                	mGridViewBuddies.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });
        Typeface faceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
		Typeface faceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        ((TextView)mRootView.findViewById(R.id.myBuddies)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.myOldBuddies)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.search)).setTypeface(faceB);
        ((TextView)mRootView.findViewById(R.id.search_diveboard_text)).setTypeface(faceB);
        ((AutoCompleteTextView)mRootView.findViewById(R.id.search_diveboard_edit)).setTypeface(faceR);
        ((AutoCompleteTextView)mRootView.findViewById(R.id.search_diveboard_edit)).setAdapter(new AutoCompleteAdapter(getActivity()));
        ((AutoCompleteTextView)mRootView.findViewById(R.id.search_diveboard_edit)).setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				((AutoCompleteTextView)mRootView.findViewById(R.id.search_diveboard_edit)).setText("");
				Buddy buddy = (Buddy) parent.getItemAtPosition(pos);
				buddy.setNotify(((CheckBox)mRootView.findViewById(R.id.notify_diveboard_checkbox)).isChecked());
				if (!mBuddies.contains(buddy))
					mBuddies.add(buddy);
				mModel.getDives().get(mIndex).setBuddies(mBuddies);
				mAdapter.notifyDataSetChanged();
			}
		});
        ((TextView)mRootView.findViewById(R.id.notify_diveboard_text)).setTypeface(faceB);
        ((CheckBox)mRootView.findViewById(R.id.notify_diveboard_checkbox)).setChecked(true);
        ((TextView)mRootView.findViewById(R.id.search_facebook_text)).setTypeface(faceB);
        ((EditText)mRootView.findViewById(R.id.search_facebook_edit)).setTypeface(faceR);
        ((TextView)mRootView.findViewById(R.id.search_name_text)).setTypeface(faceB);
        ((EditText)mRootView.findViewById(R.id.search_name_edit)).setTypeface(faceR);
        ((TextView)mRootView.findViewById(R.id.search_email_text)).setTypeface(faceB);
        ((EditText)mRootView.findViewById(R.id.search_email_edit)).setTypeface(faceR);
        ((EditText)mRootView.findViewById(R.id.search_email_edit)).addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().contains("@") && s.toString().contains(".")) {
					((CheckBox)mRootView.findViewById(R.id.notify_email_checkbox)).setEnabled(true);
					((CheckBox)mRootView.findViewById(R.id.notify_email_checkbox)).setChecked(true);
				}
				else
				{
					((CheckBox)mRootView.findViewById(R.id.notify_email_checkbox)).setEnabled(false);
					((CheckBox)mRootView.findViewById(R.id.notify_email_checkbox)).setChecked(false);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
        ((TextView)mRootView.findViewById(R.id.notify_email_text)).setTypeface(faceB);
        ((Button)mRootView.findViewById(R.id.add_email_checkbox)).setTypeface(faceB);
        ((Button)mRootView.findViewById(R.id.add_email_checkbox)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((EditText)mRootView.findViewById(R.id.search_name_edit)).setText("");
				((EditText)mRootView.findViewById(R.id.search_email_edit)).setText("");
				((EditText)mRootView.findViewById(R.id.search_email_edit)).setError(null);
				if (((EditText)mRootView.findViewById(R.id.search_name_edit)).getText().toString().isEmpty())
				{
					((EditText)mRootView.findViewById(R.id.search_name_edit)).setError("The field cannot be empty");
				}
				else
				{
					String email = ((EditText)mRootView.findViewById(R.id.search_email_edit)).getText().toString();
					Buddy buddy = new Buddy();
					buddy.setNickname(((EditText)mRootView.findViewById(R.id.search_name_edit)).getText().toString());
					if (TextUtils.isEmpty(email) || !email.contains("@") || !email.contains(".")) {
					}
					else
						buddy.setEmail(email);
					buddy.setNotify(((CheckBox)mRootView.findViewById(R.id.notify_email_checkbox)).isChecked());
					if (!mBuddies.contains(buddy))
						mBuddies.add(buddy);
					mModel.getDives().get(mIndex).setBuddies(mBuddies);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.buddy_short_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = ((Spinner)mRootView.findViewById(R.id.spinner));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == 0)
				{
					((LinearLayout)mRootView.findViewById(R.id.search_global_diveboard)).setVisibility(View.VISIBLE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_email)).setVisibility(View.GONE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_facebook)).setVisibility(View.GONE);
					//EditText editText = new EditText(getActivity());
//					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//					params.addRule(RelativeLayout.BELOW, arg0.getId());
//					editText.setLayoutParams(params);
//					((RelativeLayout)mRootView.findViewById(R.id.parent_rl)).addView(editText);
				}
				else if (position == 1)
				{
					((LinearLayout)mRootView.findViewById(R.id.search_global_diveboard)).setVisibility(View.GONE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_facebook)).setVisibility(View.VISIBLE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_email)).setVisibility(View.GONE);
				}
				else if (position == 2)
				{
					((LinearLayout)mRootView.findViewById(R.id.search_global_diveboard)).setVisibility(View.GONE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_facebook)).setVisibility(View.GONE);
					((LinearLayout)mRootView.findViewById(R.id.search_global_email)).setVisibility(View.VISIBLE);
					
					
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        return mRootView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
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
    
    
    private class AutoCompleteAdapter extends ArrayAdapter<Buddy> implements Filterable
    {
    	private LayoutInflater mInflater;
    	private StringBuilder mSB = new StringBuilder();
    	
    	public AutoCompleteAdapter(Context context)
    	{
    		super(context, -1);
    		mInflater = LayoutInflater.from(context);
    	}
    	
    	@Override
    	public View getView(final int position, final View convertView, final ViewGroup parent)
    	{
    		final TextView tv;
    		
    		if (convertView != null)
    		{
    			tv = (TextView) convertView;
    		}
    		else
    			tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
    		tv.setText(createFormattedBuddyFromBuddy(getItem(position)));
    		return tv;
    	}
    	
    	private String createFormattedBuddyFromBuddy(final Buddy buddy)
    	{
    		return buddy.getNickname();
//    		//System.out.println(buddy.getNickname());
//    		mSB.setLength(0);
//    		final int size = buddy.getNickname().length();
//    		for (int i = 0; i < size; i++)
//    		{
//    			mSB.append(buddy.getNickname(i))
//    		}
//    		mSB.append(buddy.getNickname());
//    		return mSB.toString();
    	}

		@Override
    	public Filter getFilter()
    	{
    		Filter myFilter = new Filter()
    		{
    			@Override
    			protected FilterResults performFiltering(final CharSequence constraint)
    			{
    				List<Buddy> buddyList = null;
    				if (constraint != null && constraint.length() >= 2)
    				{
    					buddyList = mModel.searchBuddy(constraint.toString());
    				}
    				if (buddyList == null)
    				{
    					buddyList = new ArrayList<Buddy>();
    				}
    				final FilterResults filterResults = new FilterResults();
    				filterResults.values = buddyList;
    				filterResults.count = buddyList.size();
    				return filterResults;
    			}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					clear();
					for (Buddy buddy : (List<Buddy>) results.values)
					{
						System.out.println(buddy.getNickname());
						add(buddy);
					}
					if (results.count > 0)
						notifyDataSetChanged();
					else
						notifyDataSetInvalidated();
				}
				
				@Override
				public CharSequence convertResultToString(final Object resultValue)
				{
					return resultValue == null ? "" : ((Buddy)resultValue).getNickname();
				}
    		};
    		return myFilter;
    	}
    	
    }
    
    
    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    // Old Buddies
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        //private int mActionBarHeight = 0;
        private LinearLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        @Override
        public int getCount() {
        	return mOldBuddies.size();
        }

        @Override
        public long getItemId(int position) {
            return position - mNumColumns;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
        	ImageView imageView;
        	TextView tv;
        	LinearLayout rl;
        	Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
			Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Bold.otf");
        	//if (convertView == null) { // if it's not recycled, instantiate and initialize
        		imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
                //imageView.setId(position + 1000);
                
                rl = new LinearLayout(mContext);
                rl.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	rl.setLayoutParams(params);
            	tv = new TextView(mContext);
            	LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	//tvParams.addRule(RelativeLayout.BELOW, imageView.getId());
            	tv.setLayoutParams(tvParams);
            	tv.setText(mOldBuddies.get(position).getNickname());
            	tv.setTypeface(faceR);
                
                
            	
            	rl.addView(imageView);
            	rl.addView(tv);
//            } else { // Otherwise re-use the converted view
//            	//System.out.println("convertview != null");
//            	rl = (LinearLayout) convertView;
//            }

        	// Check the height matches our calculated column width
            if (rl.getChildAt(0).getLayoutParams().height != mItemHeight) {
            	rl.getChildAt(0).setLayoutParams(mImageViewLayoutParams);
            }
            //System.out.println("Old Buddies " + mOldBuddies.get(position).get_class());
            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
            if (mOldBuddies.get(position).getPicture() == null || mOldBuddies.get(position).getPicture()._urlDefault.contains("no_picture"))
            {
            	((ImageView)rl.getChildAt(0)).setImageResource(R.drawable.no_picture);
            }
            else
            	mImageFetcher.loadImage(mOldBuddies.get(position).getPicture()._urlDefault, (ImageView)rl.getChildAt(0));
            return rl;
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
                    new LinearLayout.LayoutParams(mItemHeight, mItemHeight);
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
			return mOldBuddies.get(position).getPicture()._urlDefault;
		}
    }
    
    // New Buddies 
    private class ImageAdapterBuddies extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private LinearLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapterBuddies(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        @Override
        public int getCount() {
        	return mBuddies.size();
        }

        @Override
        public long getItemId(int position) {
            return position - mNumColumns;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            // Now handle the main ImageView thumbnails
        	ImageView imageView;
        	TextView tv;
        	LinearLayout rl;
        	Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
			Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Bold.otf");
            //if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
                //imageView.setId(position + 1000);
                
                rl = new LinearLayout(mContext);
                rl.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	rl.setLayoutParams(params);
            	tv = new TextView(mContext);
            	LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            	//tvParams.addRule(RelativeLayout.BELOW, imageView.getId());
            	tv.setLayoutParams(tvParams);
            	System.out.println("Refresh buddies " + position + " " + mBuddies.get(position).getNickname());
            	tv.setText(mBuddies.get(position).getNickname());
            	tv.setTypeface(faceR);
                
                
            	
            	rl.addView(imageView);
            	rl.addView(tv);
                
                
            	
            	//System.out.println("convertview = null");
//            } else { // Otherwise re-use the converted view
//            	//System.out.println("convertview != null");
//            	rl = (LinearLayout) convertView;
//            }

            // Check the height matches our calculated column width
            if (rl.getChildAt(0).getLayoutParams().height != mItemHeight) {
            	rl.getChildAt(0).setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            ApplicationController AC = (ApplicationController)mContext.getApplicationContext();
            if (mBuddies.get(position).getPicture() == null || mBuddies.get(position).getPicture()._urlDefault.contains("no_picture"))
            {
            	((ImageView)rl.getChildAt(0)).setImageResource(R.drawable.no_picture);
            }
            else
            	mImageFetcher.loadImage(mBuddies.get(position).getPicture()._urlDefault, (ImageView)rl.getChildAt(0));
            return rl;
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
                    new LinearLayout.LayoutParams(mItemHeight, mItemHeight);
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
			return mBuddies.get(position).getPicture()._urlDefault;
		}
    }
}
