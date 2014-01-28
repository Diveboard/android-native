package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Temperature;
import com.diveboard.model.Units;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
	private Double				mTemperature;
	private Spinner				temp_label;
	
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
			mTemperature = 0.0;
		else
			mTemperature = mDive.getTempBottom();
		if (mDive.getTempBottom() == null)
			mBottomTemp.setText("");
		else
			mBottomTemp.setText(Double.toString(mTemperature));
		mBottomTemp.setHint(getResources().getString(R.string.bottom_temp_hint));
		mBottomTemp.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mBottomTemp.setOnEditorActionListener(this);
		
//		TextView temp_label = (TextView) view.findViewById(R.id.temp_label);
//		temp_label.setTypeface(faceR);
//		temp_label.setText("°" + mTemperature.getSmallName());
		temp_label = (Spinner) view.findViewById(R.id.temp_label);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		if (mDive.getTempBottomUnit() == null)
		{
			if (Units.getTemperatureUnit() == Units.Temperature.C)
			{
				adapter.add("°C");
				adapter.add("°F");
			}
			else
			{
				adapter.add("°F");
				adapter.add("°C");
			}
		}
		else
		{
			if (mDive.getTempBottomUnit().compareTo("C") == 0)
			{
				adapter.add("°C");
				adapter.add("°F");
			}
			else
			{
				adapter.add("°F");
				adapter.add("°C");
			}
		}
		temp_label.setAdapter(adapter);
		
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
//				Temperature temperature;
//				
//				try
//				{
//					temperature = new Temperature(Double.parseDouble(mBottomTemp.getText().toString()));
//				}
//				catch (NumberFormatException e)
//				{
//					temperature = null;
//				}
				Double temperature;
				
				try
				{
					temperature = Double.parseDouble(mBottomTemp.getText().toString());
				}
				catch (NumberFormatException e)
				{
					temperature = null;
				}
				mDive.setTempBottom(temperature);
				mDive.setTempBottomUnit(((String) temp_label.getSelectedItem()).substring(1));
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
//			Temperature temperature;
//			
//			try
//			{
//				temperature = new Temperature(Double.parseDouble(mBottomTemp.getText().toString()));
//			}
//			catch (NumberFormatException e)
//			{
//				temperature = null;
//			}
			Double temperature;
			
			try
			{
				temperature = Double.parseDouble(mBottomTemp.getText().toString());
			}
			catch (NumberFormatException e)
			{
				temperature = null;
			}
			mDive.setTempBottom(temperature);
			mDive.setTempBottomUnit(((String) temp_label.getSelectedItem()).substring(1));
			mListener.onBottomTempEditComplete(NewBottomTempDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}