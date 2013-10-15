package com.diveboard.mobile;

import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class					TabEditDetailsActivity extends Activity
{
	private ListView			optionList;
	private DiveboardModel		mModel;
	private int					mIndex;
	private int					mPage = 0;
	private TextView			mLengthCount;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

		mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", -1);
		
		_displayEditList();
    }
    
    @Override
    public void onBackPressed()
    {
    	System.out.println("Back");
    	if (mPage > 0)
    	{
    		_displayEditList();
    		mPage--;
    	}
    	else
    	{
    		Intent divesActivity = new Intent(TabEditDetailsActivity.this, DivesActivity.class);
    	    startActivity(divesActivity);
    	}
    }
    
    private void				editTitle()
    {
    	setContentView(R.layout.edit_title);
    	Typeface FaceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Bold.otf");
    	Typeface FaceR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");

    	Button backButton = (Button) findViewById(R.id.back_button);
    	backButton.setTypeface(FaceR);
    	backButton.setText("<");
    	backButton.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View v)
			{
		    	InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
		    	if (inputMethodManager != null)
		    		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
				_displayEditList();
				mPage--;
			}
		});

    	TextView title = (TextView) findViewById(R.id.title);
    	title.setTypeface(FaceB);
    	title.setText(getResources().getString(R.string.edit_title_title));
    	
    	Button saveButton = (Button) findViewById(R.id.save_button);
    	saveButton.setTypeface(FaceB);
    	saveButton.setText(getResources().getString(R.string.save_button));
    	    	
    	TextView edit_title = (TextView) findViewById(R.id.edit_title);
    	edit_title.setTypeface(FaceR);
    	edit_title.setText(mModel.getDives().get(mIndex).getSpot().getName());
    	edit_title.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View v)
			{
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
		    	if (inputMethodManager != null)
		    		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		});
    	
    	mLengthCount = (TextView) findViewById(R.id.length_count);
    	mLengthCount.setTypeface(FaceR);
    	mLengthCount.setText(50 - edit_title.length() + " " + getResources().getString(R.string.characters_left));
    	edit_title.addTextChangedListener(new TextWatcher()
    	{
			@Override
			public void onTextChanged(CharSequence s, int start, int arg2, int arg3) {
				mLengthCount.setText(50 - s.length() + " " + getResources().getString(R.string.characters_left));
			}

			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
		});
    	InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
    	if (inputMethodManager != null)
    		inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }
    
    private void				_displayEditList()
    {
    	setContentView(R.layout.tab_edit_details);
    	Dive dive = mModel.getDives().get(mIndex);
    	
    	optionList = (ListView)findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Title : ", dive.getSpot().getName()));
		elem.add(new EditOption("Dive number : ", Integer.toString(dive.getId())));
		elem.add(new EditOption("Date : ", dive.getDate()));
		elem.add(new EditOption("Time in : ", dive.getTimeIn()));
		elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth().getDistance())));
		elem.add(new EditOption("Duration : ", Integer.toString(dive.getDuration())));
		elem.add(new EditOption("Safety stops : ", "not implemented"));
		elem.add(new EditOption("Other divers : ", "not implemented"));
		elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (dive.getTempSurface() != null)
			elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface())));
		else
			elem.add(new EditOption("Surface temperature : ", "Not defined"));
		if (dive.getTempBottom() != null)
			elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom())));
		else
			elem.add(new EditOption("Bottom temperature : ", "Not defined"));
		if (dive.getWeights() != null)
			elem.add(new EditOption("Weights : ", Double.toString(dive.getWeights())));
		else
			elem.add(new EditOption("Weights : ", "Not defined"));
		elem.add(new EditOption("Visibility : ", dive.getVisibility()));
		elem.add(new EditOption("Current : ", dive.getCurrent()));
		elem.add(new EditOption("Altitude : ", Double.toString(dive.getAltitude())));
		elem.add(new EditOption("Water type : ", dive.getWater()));
		
		OptionAdapter adapter = new OptionAdapter(this, elem);
		optionList.setAdapter(adapter);
		
		optionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				editTitle();
				mPage++;
			}
		});
    }
}
