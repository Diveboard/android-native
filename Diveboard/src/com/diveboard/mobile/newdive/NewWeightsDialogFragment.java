package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Units;
import com.diveboard.model.Weight;

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

public class					NewWeightsDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditWeightsDialogListener
	{
        void					onWeightsEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private EditText			mWeights;
	private EditWeightsDialogListener	mListener;
	private Spinner				weights_label;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditWeightsDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onWeightsEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_weights, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_weights_title));
		
		mWeights = (EditText) view.findViewById(R.id.weights);
		mWeights.setTypeface(faceR);
//		if (mDive.getWeights() != null)
//			mWeights.setText(Double.toString(mDive.getWeights().getWeight()));
//		else
//			mWeights.setText("");
		if (mDive.getWeights() != null)
			mWeights.setText(Double.toString(mDive.getWeights()));
		else
			mWeights.setText("");
		mWeights.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mWeights.setOnEditorActionListener(this);
        
//		TextView weights_label = (TextView) view.findViewById(R.id.weights_label);
//		weights_label.setTypeface(faceR);
//		if (mDive.getWeights() != null)
//			weights_label.setText(mDive.getWeights().getSmallName());
//		else
//		{
//			Weight temp_weight = new Weight(0.0);
//			weights_label.setText(temp_weight.getSmallName());
//		}
		weights_label = (Spinner) view.findViewById(R.id.weights_label);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		if (mDive.getWeightsUnit() == null)
		{
			if (Units.getWeightUnit() == Units.Weight.KG)
			{
				adapter.add(getResources().getString(R.string.unit_kg));
				adapter.add(getResources().getString(R.string.unit_lbs));
			}
			else
			{
				adapter.add(getResources().getString(R.string.unit_lbs));
				adapter.add(getResources().getString(R.string.unit_kg));
			}
		}
		else
		{
			if (mDive.getWeightsUnit().compareTo(getResources().getString(R.string.unit_kg)) == 0)
			{
				adapter.add(getResources().getString(R.string.unit_kg));
				adapter.add(getResources().getString(R.string.unit_lbs));
			}
			else
			{
				adapter.add(getResources().getString(R.string.unit_lbs));
				adapter.add(getResources().getString(R.string.unit_kg));
			}
		}
		weights_label.setAdapter(adapter);
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mWeights.getWindowToken(), 0);
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
				try
				{
//					mDive.setWeights(new Weight(Double.parseDouble(mWeights.getText().toString())));
					mDive.setWeights(Double.parseDouble(mWeights.getText().toString()));
					mDive.setWeightsUnit((String) weights_label.getSelectedItem());
				}
				catch (NumberFormatException e)
				{
					mDive.setWeights(null);
				}
				mListener.onWeightsEditComplete(NewWeightsDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mWeights.getWindowToken(), 0);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}

	@Override
	public boolean					onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			try
			{
//				mDive.setWeights(new Weight(Double.parseDouble(mWeights.getText().toString())));
				mDive.setWeights(Double.parseDouble(mWeights.getText().toString()));
				mDive.setWeightsUnit((String) weights_label.getSelectedItem());
			}
			catch (NumberFormatException e)
			{
				mDive.setWeights(null);
			}
			mListener.onWeightsEditComplete(NewWeightsDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}
