package com.diveboard.mobile;

import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class					TabEditDetailsActivity extends Activity
{
	private ListView			optionList;
	private DiveboardModel		mModel;
	private int					mIndex;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_edit_details);

		mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", -1);
		
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
		elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface())));
		elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom())));
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
    }
}
