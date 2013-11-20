package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Temperature;

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

public class					NewBottomTempDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditBottomTempDialogListener
	{
        void					onBottomTempEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private EditText			mBottomTemp;
	private EditBottomTempDialogListener	mListener;
	private Temperature			mTemperature;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditBottomTempDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onBottomTempEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_temperature, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_bottom_temp_title));
		
		mBottomTemp = (EditText) view.findViewById(R.id.temperature);
		mBottomTemp.setTypeface(faceR);
		if (mDive.getTempBottom() == null)
			mTemperature = new Temperature(0.0);
		else
			mTemperature = mDive.getTempBottom();
		mBottomTemp.setText(Double.toString(mTemperature.getTemperature()));
		mBottomTemp.setHint(getResources().getString(R.string.bottom_temp_hint));
		mBottomTemp.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mBottomTemp.setOnEditorActionListener(this);
		
		TextView temp_label = (TextView) view.findViewById(R.id.temp_label);
		temp_label.setTypeface(faceR);
		temp_label.setText("°" + mTemperature.getSmallName());
		
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
				Temperature temperature;
				
				try
				{
					temperature = new Temperature(Double.parseDouble(mBottomTemp.getText().toString()));
				}
				catch (NumberFormatException e)
				{
					temperature = new Temperature(0.0);
				}
				mDive.setTempBottom(temperature);
				mListener.onBottomTempEditComplete(NewBottomTempDialogFragment.this);
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
			Temperature temperature;
			
			try
			{
				temperature = new Temperature(Double.parseDouble(mBottomTemp.getText().toString()));
			}
			catch (NumberFormatException e)
			{
				temperature = new Temperature(0.0);
			}
			mDive.setTempBottom(temperature);
			mListener.onBottomTempEditComplete(NewBottomTempDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}