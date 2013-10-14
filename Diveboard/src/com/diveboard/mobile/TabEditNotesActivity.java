package com.diveboard.mobile;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

public class TabEditNotesActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.tab_edit_notes);
    }
}
