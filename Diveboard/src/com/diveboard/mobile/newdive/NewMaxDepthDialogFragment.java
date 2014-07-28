package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Distance;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
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

public class					NewMaxDepthDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditMaxDepthDialogListener
	{
        void					onMaxDepthEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private EditText			mMaxDepth;
	private EditMaxDepthDialogListener	mListener;
	private Spinner				max_depth_label;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditMaxDepthDialogListener) activity;
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
		
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_max_depth, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_max_depth_title));
		
		mMaxDepth = (EditText) view.findViewById(R.id.max_depth);
		mMaxDepth.setTypeface(faceR);
//		if (mDive.getMaxdepth() != null)
//			mMaxDepth.setText(Double.toString(mDive.getMaxdepth().getDistance()));
		if (mDive.getMaxdepth() != null)
			mMaxDepth.setText(Double.toString(mDive.getMaxdepth()));
		mMaxDepth.setSelection(mMaxDepth.getText().length());
		mMaxDepth.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mMaxDepth.setOnEditorActionListener(this);
		
		max_depth_label = (Spinner) view.findViewById(R.id.max_depth_label);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		if (Units.getDistanceUnit() == Units.Distance.KM)
		{
			adapter.add(getResources().getString(R.string.unit_m));
			adapter.add(getResources().getString(R.string.unit_ft));
		}
		else
		{
			adapter.add(getResources().getString(R.string.unit_ft));
			adapter.add(getResources().getString(R.string.unit_m));
		}
		max_depth_label.setAdapter(adapter);
		
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
				Double dbl;
				try
				{
					dbl = Double.parseDouble(mMaxDepth.getText().toString());
				}
				catch (NumberFormatException e)
				{
					dbl = 0.0;
				}
				//Distance new_depth = new Distance(dbl, Units.getDistanceUnit());
				mDive.setMaxdepth(dbl);
				mDive.setMaxdepthUnit((String) max_depth_label.getSelectedItem());
				mListener.onMaxDepthEditComplete(NewMaxDepthDialogFragment.this);
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
				dbl = Double.parseDouble(mMaxDepth.getText().toString());
			}
			catch (NumberFormatException e)
			{
				dbl = 0.0;
			}
			//Distance new_depth = new Distance(dbl, Units.getDistanceUnit());
			mDive.setMaxdepth(dbl);
			mDive.setMaxdepthUnit((String) max_depth_label.getSelectedItem());
			mListener.onMaxDepthEditComplete(NewMaxDepthDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}