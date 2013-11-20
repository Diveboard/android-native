package com.diveboard.mobile;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.widget.TextView;

public class EmptyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
		TextView tv = ((TextView)findViewById(R.id.activity_in_progress));
		tv.setTypeface(face);
	}

}
