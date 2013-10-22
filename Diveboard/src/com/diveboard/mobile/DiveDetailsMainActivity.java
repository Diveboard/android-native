package com.diveboard.mobile;

import com.diveboard.model.Dive;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class DiveDetailsMainActivity extends Activity {
	private Dive mDive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dive_details_main);
		ApplicationController AC = ((ApplicationController)getApplicationContext());
		mDive = AC.getModel().getDives().get(getIntent().getIntExtra("index", -1));
		if (mDive.getNotes() != null)
			((TextView)findViewById(R.id.dive_note)).setText(mDive.getNotes());
		else
			((TextView)findViewById(R.id.dive_note)).setText("No Note for this dive");
	}

}
