package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Units;
import com.diveboard.util.DiveboardSpinnerAdapter;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					EditMaxDepthDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditMaxDepthDialogListener
	{
        void					onMaxDepthEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
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
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_max_depth_title));
		
		mMaxDepth = (EditText) view.findViewById(R.id.max_depth);
		mMaxDepth.setTypeface(faceR);
		//mMaxDepth.setText(Double.toString(mModel.getDives().get(getArguments().getInt("index")).getMaxdepth().getDistance()));
		mMaxDepth.setText(Double.toString(mModel.getDives().get(getArguments().getInt("index")).getMaxdepth()));
		mMaxDepth.setSelection(mMaxDepth.getText().length());
		mMaxDepth.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mMaxDepth.setOnEditorActionListener(this);
		
		max_depth_label = (Spinner) view.findViewById(R.id.max_depth_label);
		ArrayAdapter<String> adapter = new DiveboardSpinnerAdapter(getActivity().getApplicationContext(), R.layout.units_spinner);
		String maxdepth_unit = mModel.getDives().get(getArguments().getInt("index")).getMaxdepthUnit();
		if (maxdepth_unit == null)
		{
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
		}
		else
		{
			if (maxdepth_unit.compareTo(getResources().getString(R.string.unit_m)) == 0)
			{
				adapter.add(getResources().getString(R.string.unit_m));
				adapter.add(getResources().getString(R.string.unit_ft));
			}
			else
			{
				adapter.add(getResources().getString(R.string.unit_ft));
				adapter.add(getResources().getString(R.string.unit_m));
			}
		}
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		max_depth_label.setAdapter(adapter);
//		max_depth_label.setTypeface(faceR);
//		max_depth_label.setText(mModel.getDives().get(getArguments().getInt("index")).getMaxdepth().getSmallName());
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMaxDepth.getWindowToken(), 0);
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
//				Distance new_depth = new Distance(dbl);
//				mModel.getDives().get(getArguments().getInt("index")).setMaxdepth(new_depth);
				mModel.getDives().get(getArguments().getInt("index")).setMaxdepth(dbl);
				mModel.getDives().get(getArguments().getInt("index")).setMaxdepthUnit((String) max_depth_label.getSelectedItem());
				mListener.onMaxDepthEditComplete(EditMaxDepthDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mMaxDepth.getWindowToken(), 0);
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
//			Distance new_depth = new Distance(dbl);
//			mModel.getDives().get(getArguments().getInt("index")).setMaxdepth(new_depth);
			mModel.getDives().get(getArguments().getInt("index")).setMaxdepth(dbl);
			mModel.getDives().get(getArguments().getInt("index")).setMaxdepthUnit((String) max_depth_label.getSelectedItem());
			mListener.onMaxDepthEditComplete(EditMaxDepthDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}