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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class					TabEditSpotsActivity extends Activity
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private DiveboardModel				mModel;
	private int							mIndex;
	private SpotsTask 					mSpotsTask;
	
	@Override
	public void onResume()
	{
		super.onResume();
		//onSearchRequested();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ApplicationController AC = (ApplicationController)getApplicationContext();
        if (AC.handleLowMemory() == true)
			return ;
	    setContentView(R.layout.tab_edit_spots);
	    mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
	    mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
	    mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", 0);
		
		TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(mFaceB);
	    title.setText(getResources().getString(R.string.tab_spots_title));
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.save_button));
	 // Get the intent, verify the action and get the query
//	    Intent intent = getIntent();
//	    if (Intent.ACTION_SEARCH.equals(intent.getAction()))
//	    {
//	    	String query = intent.getStringExtra(SearchManager.QUERY);
//	    	doMySearch(query);
//	    }
//	    onSearchRequested();
	    
    }
    
    public void setCurrentSpot(View view)
    {

		((TextView)findViewById(R.id.search_bar)).setText("");
    	((TextView)findViewById(R.id.current_spot)).setText("Current spot: " + ((TextView)view.findViewById(R.id.name)).getText().toString());
    }
    
    public void doMySearch(View view)
    {
    	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, 0);
    	SpotsTask spots_task = new SpotsTask();
	    spots_task.execute(((TextView)findViewById(R.id.search_bar)).getText().toString());
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
			if (result != null)
			{
				try {
					System.out.println(result.toString());
					JSONArray array = result.getJSONArray("spots");
					ListView lv = ((ListView)findViewById(R.id.list_view));
					List<Spot> listSpots = new ArrayList<Spot>();
					for (int i = 0; i < array.length(); i++)
					{
						Spot spot = new Spot(array.getJSONObject(i));
						listSpots.add(spot);
					}
					SpotAdapter adapter = new SpotAdapter(TabEditSpotsActivity.this, listSpots);
					lv.setAdapter(adapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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

				holder.id = (TextView)convertView.findViewById(R.id.id);
				holder.name = (TextView)convertView.findViewById(R.id.name);
				holder.country = (TextView)convertView.findViewById(R.id.country);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.id.setText(Integer.toString(mSpotsList.get(position).getId()));
			holder.name.setText(mSpotsList.get(position).getName());
			holder.country.setText(mSpotsList.get(position).getCountryName());

			return convertView;
		}
		
		private class ViewHolder {
			TextView id;
			TextView name;
			TextView country;
		}
    	
    }
}
