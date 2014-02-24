package com.diveboard.mobile.newdive;

import java.util.ArrayList;

import org.json.JSONArray;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewSafetyStopsDialogFragment extends DialogFragment
{
	public interface			EditSafetyStopsDialogListener
	{
        void					onSafetyStopsEditComplete(DialogFragment dialog);
    }
	
	private ArrayList<SafetyStop>	mSafetyStops;
	private Dive				mDive;
	private Typeface			mFaceR;
	private View				mView;
	EditSafetyStopsDialogListener	mListener;
	private EditText			mDepthField;
	private EditText			mDurationField;
	private Integer				mIndex;
	private Spinner				mDepthLabel;
	
	 @Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditSafetyStopsDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onSafetyStopsEditComplete");
		 }
	 }
	
	private void				addNoStops(LinearLayout layout)
	{
		final float scale = getResources().getDisplayMetrics().density;
		
		TextView text = new TextView(getActivity().getApplicationContext());
		text.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		text.setTypeface(mFaceR);
		text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
		text.setTextSize(30);
		text.setText("No safety stops");
		text.setTextColor(getResources().getColor(R.color.dark_grey));
		text.setGravity(Gravity.CENTER);
		layout.addView(text);
	}
	
	private void				openSafetyStopsEdit(Integer index)
	{
		mIndex = index;
		LinearLayout safetylist = (LinearLayout) mView.findViewById(R.id.safetyfields);
		safetylist.removeAllViews();
		final float scale = getResources().getDisplayMetrics().density;
		SafetyStop safetyStop = mSafetyStops.get(index);
		
		Button add_button = (Button) mView.findViewById(R.id.add_button);
		add_button.setVisibility(Button.GONE);
		
		LinearLayout depth = new LinearLayout(getActivity().getApplicationContext());
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		depth.setLayoutParams(params);
		depth.setOrientation(LinearLayout.HORIZONTAL);
		depth.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView depth_title = new TextView(getActivity().getApplicationContext());
		depth_title.setTypeface(mFaceR);
		depth_title.setTextColor(getResources().getColor(R.color.dark_grey));
		depth_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		depth_title.setTextSize(30);
		depth_title.setText("Depth :");
		depth.addView(depth_title);
		
		mDepthField = new EditText(getActivity().getApplicationContext());
		mDepthField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDepthField.setTypeface(mFaceR);
		mDepthField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDepthField.setTextSize(30);
		mDepthField.setText(safetyStop.getDepth().toString());
		depth.addView(mDepthField);
		
		mDepthLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		String safetystop_unit = mSafetyStops.get(mIndex).getUnit();
		if (safetystop_unit == null)
		{
			if (Units.getDistanceUnit() == Units.Distance.KM)
			{
				adapter.add("m");
				adapter.add("ft");
			}
			else
			{
				adapter.add("ft");
				adapter.add("m");
			}
		}
		else
		{
			if (safetystop_unit.compareTo("m") == 0)
			{
				adapter.add("m");
				adapter.add("ft");
			}
			else
			{
				adapter.add("ft");
				adapter.add("m");
			}
		}
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		mDepthLabel.setAdapter(adapter);
		depth.addView(mDepthLabel);
		
		safetylist.addView(depth);
		
		LinearLayout duration = new LinearLayout(getActivity().getApplicationContext());
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		duration.setLayoutParams(params);
		duration.setOrientation(LinearLayout.HORIZONTAL);
		duration.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView duration_title = new TextView(getActivity().getApplicationContext());
		duration_title.setTypeface(mFaceR);
		duration_title.setTextColor(getResources().getColor(R.color.dark_grey));
		duration_title.setTextSize(30);
		duration_title.setText("Duration :");
		duration_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		duration.addView(duration_title);
		
		mDurationField = new EditText(getActivity().getApplicationContext());
		mDurationField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDurationField.setTypeface(mFaceR);
		mDurationField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDurationField.setTextSize(30);
		mDurationField.setText(safetyStop.getDuration().toString());
		duration.addView(mDurationField);
		
		TextView duration_label = new TextView(getActivity().getApplicationContext());
		duration_label.setTypeface(mFaceR);
		duration_label.setTextColor(getResources().getColor(R.color.dark_grey));
		duration_label.setTextSize(30);
		duration_label.setText("min");
		duration.addView(duration_label);
		
		safetylist.addView(duration);
		
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        cancel.setTypeface(mFaceR);
        cancel.setText(getResources().getString(R.string.cancel));
        cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mDepthField = null;
				mDepthField = null;
				mIndex = null;
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDurationField.getWindowToken(), 0);
				openSafetyStopsList();
			}
		});
        
        Button save = (Button) mView.findViewById(R.id.save);
        save.setTypeface(mFaceR);
        save.setText(getResources().getString(R.string.save));
        save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				if (mDepthField.getText().toString().isEmpty())
					mDepthField.setText("0");
				if (mDurationField.getText().toString().isEmpty())
					mDurationField.setText("0");
				mSafetyStops.set(mIndex, new SafetyStop(Integer.parseInt(mDepthField.getText().toString()), Integer.parseInt(mDurationField.getText().toString()), (String)mDepthLabel.getSelectedItem()));
				mDepthField = null;
				mDepthField = null;
				mIndex = null;
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDepthField.getWindowToken(), 0);
				openSafetyStopsList();
			}
		});
	}
	
	private void				openSafetyStopsNew()
	{
		LinearLayout safetylist = (LinearLayout) mView.findViewById(R.id.safetyfields);
		safetylist.removeAllViews();
		final float scale = getResources().getDisplayMetrics().density;
		
		Button add_button = (Button) mView.findViewById(R.id.add_button);
		add_button.setVisibility(Button.GONE);
		
		LinearLayout depth = new LinearLayout(getActivity().getApplicationContext());
		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		depth.setLayoutParams(params);
		depth.setOrientation(LinearLayout.HORIZONTAL);
		depth.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView depth_title = new TextView(getActivity().getApplicationContext());
		depth_title.setTypeface(mFaceR);
		depth_title.setTextColor(getResources().getColor(R.color.dark_grey));
		depth_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		depth_title.setTextSize(30);
		depth_title.setText("Depth :");
		depth.addView(depth_title);
		
		mDepthField = new EditText(getActivity().getApplicationContext());
		mDepthField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDepthField.setTypeface(mFaceR);
		mDepthField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDepthField.setTextSize(30);
		mDepthField.setText("3");
		depth.addView(mDepthField);
		
		mDepthLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		if (Units.getDistanceUnit() == Units.Distance.KM)
		{
			adapter.add("m");
			adapter.add("ft");
		}
		else
		{
			adapter.add("ft");
			adapter.add("m");
		}
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		mDepthLabel.setAdapter(adapter);
		depth.addView(mDepthLabel);
		
		safetylist.addView(depth);
		
		LinearLayout duration = new LinearLayout(getActivity().getApplicationContext());
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		duration.setLayoutParams(params);
		duration.setOrientation(LinearLayout.HORIZONTAL);
		duration.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView duration_title = new TextView(getActivity().getApplicationContext());
		duration_title.setTypeface(mFaceR);
		duration_title.setTextColor(getResources().getColor(R.color.dark_grey));
		duration_title.setTextSize(30);
		duration_title.setText("Duration :");
		duration_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		duration.addView(duration_title);
		
		mDurationField = new EditText(getActivity().getApplicationContext());
		mDurationField.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDurationField.setTypeface(mFaceR);
		mDurationField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDurationField.setTextSize(30);
		mDurationField.setText("3");
		duration.addView(mDurationField);
		
		TextView duration_label = new TextView(getActivity().getApplicationContext());
		duration_label.setTypeface(mFaceR);
		duration_label.setTextColor(getResources().getColor(R.color.dark_grey));
		duration_label.setTextSize(30);
		duration_label.setText("min");
		duration.addView(duration_label);
		
		safetylist.addView(duration);
		
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        cancel.setTypeface(mFaceR);
        cancel.setText(getResources().getString(R.string.cancel));
        cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mDepthField = null;
				mDepthField = null;
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDurationField.getWindowToken(), 0);
				openSafetyStopsList();
			}
		});
        
        Button save = (Button) mView.findViewById(R.id.save);
        save.setTypeface(mFaceR);
        save.setText(getResources().getString(R.string.save));
        save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				if (mDepthField.getText().toString().isEmpty())
					mDepthField.setText("0");
				if (mDurationField.getText().toString().isEmpty())
					mDurationField.setText("0");
				mSafetyStops.add(new SafetyStop(Integer.parseInt(mDepthField.getText().toString()), Integer.parseInt(mDurationField.getText().toString()), (String)mDepthLabel.getSelectedItem()));
				mDepthField = null;
				mDepthField = null;
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDurationField.getWindowToken(), 0);
				openSafetyStopsList();
			}
		});
	}
	
	private void				deleteSafetyStop(int index)
	{
		mSafetyStops.remove(index);
		openSafetyStopsList();
	}
	
	private void				openSafetyStopsList()
	{
		LinearLayout safetylist = (LinearLayout) mView.findViewById(R.id.safetyfields);
		safetylist.removeAllViews();
		if (mSafetyStops.size() == 0)
			addNoStops(safetylist);
		else
		{
			for (int i = 0, length = mSafetyStops.size(); i < length; i++)
			{
				final float scale = getResources().getDisplayMetrics().density;
				
				LinearLayout safetyElem = new LinearLayout(getActivity().getApplicationContext());
				LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				safetyElem.setTag(i);
				safetyElem.setLayoutParams(params);
				safetyElem.setOrientation(LinearLayout.HORIZONTAL);
				safetyElem.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
				safetyElem.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Edit Safety Stops
						openSafetyStopsEdit((Integer) v.getTag());
					}
				});

				TextView text = new TextView(getActivity().getApplicationContext());
				text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
				text.setTypeface(mFaceR);
				text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
				text.setTextSize(30);
				text.setText(mSafetyStops.get(i).getDepth() + mSafetyStops.get(i).getUnit() + " - " + mSafetyStops.get(i).getDuration() + "min");
				text.setTextColor(getResources().getColor(R.color.dark_grey));
				text.setGravity(Gravity.CENTER);
				
				safetyElem.addView(text);
				
				ImageView delete = new ImageView(getActivity().getApplicationContext());
				delete.setScaleType(ScaleType.FIT_CENTER);
				delete.setLayoutParams(new LinearLayout.LayoutParams((int)(50 * scale + 0.5f), LayoutParams.MATCH_PARENT));
				delete.setPadding(0, 0, (int)(5 * scale + 0.5f), 0);
				delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_recycle_bin));
				delete.setTag(i);
				delete.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Delete Safety Stops
						deleteSafetyStop((Integer) v.getTag());
					}
				});
				
				safetyElem.addView(delete);
				
				safetylist.addView(safetyElem);
			}
		}
		
		Button add_button = (Button) mView.findViewById(R.id.add_button);
		add_button.setTypeface(mFaceR);
		add_button.setText(getResources().getString(R.string.add_safetystops_button));
		add_button.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openSafetyStopsNew();
			}
		});
		add_button.setVisibility(Button.VISIBLE);
//		if (mSafetyStops.size() >= 3)
//			add_button.setVisibility(Button.GONE);
        
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        cancel.setTypeface(mFaceR);
        cancel.setText(getResources().getString(R.string.cancel));
        cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
        
        Button save = (Button) mView.findViewById(R.id.save);
        save.setTypeface(mFaceR);
        save.setText(getResources().getString(R.string.save));
        save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				mDive.setSafetyStops(mSafetyStops);
				mListener.onSafetyStopsEditComplete(NewSafetyStopsDialogFragment.this);
				dismiss();
			}
		});
	}
	 
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mFaceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		mView = inflater.inflate(R.layout.dialog_edit_safetystops, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		mSafetyStops = (ArrayList<SafetyStop>) mDive.getSafetyStops().clone();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) mView.findViewById(R.id.title);
		title.setTypeface(mFaceR);
		title.setText(getResources().getString(R.string.edit_safetystops_title));
		
		openSafetyStopsList();
		return mView;
	}
}
