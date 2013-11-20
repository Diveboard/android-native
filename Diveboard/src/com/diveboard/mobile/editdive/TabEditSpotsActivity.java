package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveDetailsActivity;
import com.diveboard.mobile.GalleryCarouselActivity;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Picture;
import com.diveboard.model.Spot;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView.OnEditorActionListener;

public class					TabEditSpotsActivity extends Activity
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private DiveboardModel				mModel;
	private int							mIndex;
	private SpotsTask 					mSpotsTask;
	private Spot						mSpot;
	private JSONObject					mSelectedObject;
	private JSONArray					mArray;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ApplicationController AC = (ApplicationController)getApplicationContext();
	    setContentView(R.layout.tab_edit_spots);
	    mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
	    mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
	    mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", 0);
		
		TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(mFaceB);
	    title.setText(getResources().getString(R.string.tab_spots_title));
	    ((TextView)findViewById(R.id.current_spot_title)).setTypeface(mFaceB);
	    ((TextView)findViewById(R.id.current_spot)).setTypeface(mFaceR);
	    ((TextView)findViewById(R.id.no_spot)).setTypeface(mFaceR);
	    ((TextView)findViewById(R.id.current_spot)).setText(mModel.getDives().get(mIndex).getSpot().getName());
	    ((TextView)findViewById(R.id.search_bar)).setTypeface(mFaceR);
	    //((Button)findViewById(R.id.ok_search)).setTypeface(mFaceR);
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.save_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
//				mModel.getDives().get(mIndex).setNotes(mNotes.getText().toString());
//				mModel.getDataManager().save(mModel.getDives().get(mIndex));
				mModel.getDives().get(mIndex).setSpot(mSelectedObject);
				mModel.getDataManager().save(mModel.getDives().get(mIndex));
				finish();
			}
		});
	    EditText editText = (EditText) findViewById(R.id.search_bar);
	    editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				boolean handled = false;
	            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	                doMySearch();
	                handled = true;
	            }
	            return handled;
			}
	    });
    }
    
//    public void setCurrentSpot(View view)
//    {  	
//    	((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
//    	ListView lv = ((ListView)findViewById(R.id.list_view));
//    	List<Spot> listSpots = new ArrayList<Spot>();
//    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
//    	lv.setAdapter(adapter);
////    	SpotAdapter adapter = ((SpotAdapter)lv.getAdapter());
////    	for (int i = 0; i < adapter.getCount(); i++)
////    	{
////    		adapter.
////    	}
//    }
    
    public void doMySearch()
    {  	
    	ListView lv = ((ListView)findViewById(R.id.list_view));
    	List<Spot> listSpots = new ArrayList<Spot>();
    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
    	lv.setAdapter(adapter);
    	((TextView)findViewById(R.id.no_spot)).setVisibility(View.GONE);
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
    	SpotsTask spots_task = new SpotsTask();
	    spots_task.execute(((TextView)findViewById(R.id.search_bar)).getText().toString());
	    ((TextView)findViewById(R.id.search_bar)).setText("");
    }
    
    private class SpotsTask extends AsyncTask<String, Void, JSONObject>
	{
		protected JSONObject doInBackground(String... query)
		{
			ApplicationController AC = (ApplicationController)getApplicationContext();
			return AC.getModel().searchSpotText(query[0], null, null);
		}
		
		protected void onPostExecute(JSONObject result)
		{
			try {
				if (result.getBoolean("success") == true)
				{
					try {
						mArray = result.getJSONArray("spots");
						ListView lv = ((ListView)findViewById(R.id.list_view));
						List<Spot> listSpots = new ArrayList<Spot>();
						for (int i = 0; i < mArray.length(); i++)
						{
							Spot spot = new Spot(mArray.getJSONObject(i));
							listSpots.add(spot);
						}
						if (listSpots.size() == 0)
						{
							((TextView)findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
						}
						else
						{
							System.out.println(result.toString());
							SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
							lv.setAdapter(adapter);
							lv.setOnItemClickListener(new OnItemClickListener()
							{
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									((TextView)findViewById(R.id.current_spot)).setText(((TextView)view.findViewById(R.id.name)).getText().toString());
							    	ListView lv = ((ListView)findViewById(R.id.list_view));
							    	List<Spot> listSpots = new ArrayList<Spot>();
							    	SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
							    	lv.setAdapter(adapter);
							    	try {
										mSelectedObject = mArray.getJSONObject(position);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					((TextView)findViewById(R.id.no_spot)).setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected void onCancelled() {
			mSpotsTask = null;
		}
	}
    
    public class SpotAdapter extends BaseAdapter
    {
    	LayoutInflater	mLayoutInflater;
    	List<Spot>		mSpotsList;
    	
    	public SpotAdapter(Context context, List<Spot> spotsList)
    	{
    		mLayoutInflater = LayoutInflater.from(context);
    		this.mSpotsList = spotsList;
    	}
    	
		@Override
		public int getCount() {
			return mSpotsList.size();
		}

		@Override
		public Object getItem(int position) {
			return mSpotsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = mLayoutInflater.inflate(R.layout.item_spots, null);

				//holder.id = (TextView)convertView.findViewById(R.id.id);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				holder.location_country = (TextView)convertView.findViewById(R.id.location_country);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			//holder.id.setText(Integer.toString(mSpotsList.get(position).getId()));
			holder.name.setText(mSpotsList.get(position).getName());
			holder.name.setTypeface(mFaceR);
			holder.location_country.setText(mSpotsList.get(position).getLocationName() + ", " + mSpotsList.get(position).getCountryName());
			holder.location_country.setTypeface(mFaceR);
			return convertView;
		}
		
		private class ViewHolder {
			TextView id;
			TextView name;
			TextView location_country;
		}
    	
    }
}
