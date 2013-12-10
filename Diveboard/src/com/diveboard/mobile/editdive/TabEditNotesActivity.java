package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment.EditConfirmDialogListener;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TabEditNotesActivity extends FragmentActivity implements EditConfirmDialogListener
{
	private Typeface					mFaceR;
	private Typeface					mFaceB;
	private DiveboardModel				mModel;
	private int							mIndex;
	private EditText					mNotes;
	
	@Override
	public void onBackPressed()
	{
		if (mModel.getDives().get(mIndex).getEditList().size() > 0)
		{
			EditConfirmDialogFragment dialog = new EditConfirmDialogFragment();
	    	Bundle args = new Bundle();
	    	args.putInt("index", mIndex);
	    	dialog.setArguments(args);
	    	dialog.show(getSupportFragmentManager(), "EditConfirmDialogFragment");
		}
		else
		{
			clearEditList();
		}
	};
	
	public void clearEditList()
	{
		super.onBackPressed();
		Bundle bundle = new Bundle();
		
		// put
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		mModel.getDives().get(mIndex).clearEditList();
	}
	
	@Override
	protected void onPause()
	{
		mModel.getDives().get(mIndex).setNotes(mNotes.getText().toString());
		super.onPause();	
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ApplicationController AC = (ApplicationController)getApplicationContext();
	    setContentView(R.layout.tab_edit_notes);
	    mFaceR = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
	    mFaceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
	    mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", 0);
	    
	    TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(mFaceB);
	    title.setText(getResources().getString(R.string.tab_notes_edit_title));
	    
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(mFaceB);
	    save.setText(getResources().getString(R.string.save_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mModel.getDives().get(mIndex).setNotes(mNotes.getText().toString());
				mModel.getDataManager().save(mModel.getDives().get(mIndex));
				((ApplicationController)getApplicationContext()).setRefresh(2);
				finish();
			}
		});
	    
	    mNotes = (EditText) findViewById(R.id.notes);
	    mNotes.setTypeface(mFaceR);
	    if (mModel.getDives().get(mIndex).getNotes() != null)
	    	mNotes.setText(mModel.getDives().get(mIndex).getNotes());
	    
	    mFaceR = null;
	    mFaceB = null;
    }

	@Override
	public void onConfirmEditComplete(DialogFragment dialog) {
		clearEditList();
	}
}
