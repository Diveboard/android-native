package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					EditSurfaceTempDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditSurfaceTempDialogListener
	{
        void					onSurfaceTempEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private EditText			mSurfaceTemp;
	private EditSurfaceTempDialogListener	mListener;
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
			 mListener = (EditSurfaceTempDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onSurfaceTempEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_temperature, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_surface_temp_title));
		
		mSurfaceTemp = (EditText) view.findViewById(R.id.temperature);
		mSurfaceTemp.setTypeface(faceR);
//		if (mModel.getDives().get(getArguments().getInt("index")).getTempSurface() == null)
//			mTemperature = new Temperature(0.0);
//		else
//			mTemperature = mModel.getDives().get(getArguments().getInt("index")).getTempSurface();
		if (mModel.getDives().get(getArguments().getInt("index")).getTempSurface() == null)
			mTemperature = 0.0;
		else
			mTemperature = mModel.getDives().get(getArguments().getInt("index")).getTempSurface();
//		if (mModel.getDives().get(getArguments().getInt("index")).getTempSurface() == null)
//			mSurfaceTemp.setText("");
//		else
//			mSurfaceTemp.setText(Double.toString(mTemperature.getTemperature()));
		if (mModel.getDives().get(getArguments().getInt("index")).getTempSurface() == null)
			mSurfaceTemp.setText("");
		else
			mSurfaceTemp.setText(mTemperature.toString());
		mSurfaceTemp.setHint(getResources().getString(R.string.surface_temp_hint));
		mSurfaceTemp.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mSurfaceTemp.setOnEditorActionListener(this);
		
		temp_label = (Spinner) view.findViewById(R.id.temp_label);
//		temp_label.setTypeface(faceR);
//		temp_label.setText("º" + mTemperature.getSmallName());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		if (mModel.getDives().get(getArguments().getInt("index")).getTempSurfaceUnit() == null)
		{
			if (Units.getTemperatureUnit() == Units.Temperature.C)
			{
				adapter.add(getResources().getString(R.string.unit_C_symbol));
				adapter.add(getResources().getString(R.string.unit_F_symbol));
			}
			else
			{
				adapter.add(getResources().getString(R.string.unit_F_symbol));
				adapter.add(getResources().getString(R.string.unit_C_symbol));
			}
		}
		else
		{
			if (mModel.getDives().get(getArguments().getInt("index")).getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0)
			{
				adapter.add(getResources().getString(R.string.unit_C_symbol));
				adapter.add(getResources().getString(R.string.unit_F_symbol));
			}
			else
			{
				adapter.add(getResources().getString(R.string.unit_F_symbol));
				adapter.add(getResources().getString(R.string.unit_C_symbol));
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
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mSurfaceTemp.getWindowToken(), 0);
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
//					temperature = new Temperature(Double.parseDouble(mSurfaceTemp.getText().toString()));
//				}
//				catch (NumberFormatException e)
//				{
//					temperature = null;
//				}
				Double temperature;
				
				try
				{
					temperature = Double.parseDouble(mSurfaceTemp.getText().toString());
				}
				catch (NumberFormatException e)
				{
					temperature = null;
				}
				mModel.getDives().get(getArguments().getInt("index")).setTempSurface(temperature);
				mModel.getDives().get(getArguments().getInt("index")).setTempSurfaceUnit(((String) temp_label.getSelectedItem()).substring(1));
				mListener.onSurfaceTempEditComplete(EditSurfaceTempDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mSurfaceTemp.getWindowToken(), 0);
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
//				temperature = new Temperature(Double.parseDouble(mSurfaceTemp.getText().toString()));
//			}
//			catch (NumberFormatException e)
//			{
//				temperature = null;
//			}
			Double temperature;
			
			try
			{
				temperature = Double.parseDouble(mSurfaceTemp.getText().toString());
			}
			catch (NumberFormatException e)
			{
				temperature = null;
			}
			mModel.getDives().get(getArguments().getInt("index")).setTempSurface(temperature);
			mModel.getDives().get(getArguments().getInt("index")).setTempSurfaceUnit(((String) temp_label.getSelectedItem()).substring(1));
			mListener.onSurfaceTempEditComplete(EditSurfaceTempDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}