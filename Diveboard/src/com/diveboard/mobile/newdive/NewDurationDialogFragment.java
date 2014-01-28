package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewDurationDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditDurationDialogListener
	{
        void					onDurationEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private EditText			mDuration;
	private EditDurationDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditDurationDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onTimeInEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_duration, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_duration_title));
		
		mDuration = (EditText) view.findViewById(R.id.duration);
		mDuration.setTypeface(faceR);
		if (mDive.getDuration() != null)
			mDuration.setText(Integer.toString(mDive.getDuration()));
		mDuration.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mDuration.setOnEditorActionListener(this);
		
		TextView min_label = (TextView) view.findViewById(R.id.min_label);
		min_label.setTypeface(faceR);
		min_label.setText(getResources().getString(R.string.duration_label));
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		
		Button save = (Button) view.findViewById(R.id.save);
		save.setTypeface(faceR);
		save.setText(getResources().getString(R.string.save));
		save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				Integer duration;
				
				try
				{
					duration = Integer.parseInt(mDuration.getText().toString());
				}
				catch (NumberFormatException e)
				{
					duration = 0;
				}
				mDive.setDuration(duration);
				mListener.onDurationEditComplete(NewDurationDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			Integer duration;
			
			try
			{
				duration = Integer.parseInt(mDuration.getText().toString());
			}
			catch (NumberFormatException e)
			{
				duration = 0;
			}
			mDive.setDuration(duration);
			mListener.onDurationEditComplete(NewDurationDialogFragment.this);
			dismiss();
		}
		return false;
	}
}