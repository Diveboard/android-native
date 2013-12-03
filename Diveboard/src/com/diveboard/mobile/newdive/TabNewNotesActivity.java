package com.diveboard.mobile.newdive;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TabNewNotesActivity extends Activity
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private Dive						mDive;
	private int							mIndex;
	private EditText					mNotes;
	
	@Override
	protected void onPause()
	{
		mDive.setNotes(mNotes.getText().toString());
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
	@Override
	public void onBackPressed()
	{
		Bundle bundle = new Bundle();
		
		// put
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
		mDive = null;
		((ApplicationController)getApplicationContext()).setTempDive(null);
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ApplicationController AC = (ApplicationController)getApplicationContext();
        if (AC.handleLowMemory() == true)
			return ;
	    setContentView(R.layout.tab_edit_notes);
	    mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
	    mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
	    mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		mIndex = getIntent().getIntExtra("index", 0);
	    
	    TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(mFaceB);
	    title.setText(getResources().getString(R.string.tab_notes_edit_title));
	    
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.add_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mDive.setNotes(mNotes.getText().toString());
				ArrayList<Dive> dives = ((ApplicationController)getApplicationContext()).getModel().getDives();
				ArrayList<Pair<String, String>> editList = mDive.getEditList();
				if (editList != null && editList.size() > 0)
				{
					JSONObject edit = new JSONObject(); 
					for (int i = 0, size = editList.size(); i < size; i++)
						try {
							edit.put(editList.get(i).first, editList.get(i).second);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					try {
						mDive.applyEdit(edit);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mDive.clearEditList();
				}
				dives.add(0, mDive);
				((ApplicationController)getApplicationContext()).getModel().getDataManager().save(mDive);
				((ApplicationController)getApplicationContext()).setRefresh(1);
//				Toast toast = Toast.makeText(getApplicationContext(), "The new dive will be displayed after refreshing the page!", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
				((ApplicationController)getApplicationContext()).setTempDive(null);
				finish();
			}
		});
	    
	    mNotes = (EditText) findViewById(R.id.notes);
	    mNotes.setTypeface(mFaceR);
	    if (mDive.getNotes() != null)
	    	mNotes.setText(mDive.getNotes());
	    
	    mFaceR = null;
	    mFaceB = null;
    }
}
