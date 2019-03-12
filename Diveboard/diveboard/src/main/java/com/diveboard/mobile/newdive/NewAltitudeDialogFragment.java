package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Distance;
import com.diveboard.model.Dive;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewAltitudeDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditAltitudeDialogListener
	{
        void					onAltitudeEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private EditText			mAltitude;
	private EditAltitudeDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditAltitudeDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onAltitudeEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_altitude, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_altitude_title));
		
		mAltitude = (EditText) view.findViewById(R.id.altitude);
		mAltitude.setTypeface(faceR);
		if (mDive.getAltitude() == null)
			mAltitude.setText("");
		else
			mAltitude.setText(Double.toString(mDive.getAltitude().getDistance()));
		mAltitude.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mAltitude.setOnEditorActionListener(this);
		
		TextView max_depth_label = (TextView) view.findViewById(R.id.altitude_label);
		Distance tmp = new Distance(0.0);
		max_depth_label.setTypeface(faceR);
		max_depth_label.setText(tmp.getSmallName());
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mAltitude.getWindowToken(), 0);
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
				Double dbl;
				try
				{
					dbl = Double.parseDouble(mAltitude.getText().toString());
				}
				catch (NumberFormatException e)
				{
					dbl = null;
				}
				Distance new_altitude = (dbl == null) ? null : new Distance(dbl);
				mDive.setAltitude(new_altitude);
				mListener.onAltitudeEditComplete(NewAltitudeDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mAltitude.getWindowToken(), 0);
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
			Double dbl;
			try
			{
				dbl = Double.parseDouble(mAltitude.getText().toString());
			}
			catch (NumberFormatException e)
			{
				dbl = 0.0;
			}
			Distance new_altitude = (dbl == null) ? null : new Distance(dbl);
			mDive.setAltitude(new_altitude);
			mListener.onAltitudeEditComplete(NewAltitudeDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}