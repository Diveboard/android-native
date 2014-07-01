package com.diveboard.mobile.editdive;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;

public class					EditTanksDialogFragment extends DialogFragment
{
	public interface			EditTanksDialogListener
	{
        void					onTanksEditComplete(DialogFragment dialog);
    }
	
	private ArrayList<Tank>	mTanks;
	private DiveboardModel			mModel;
	private Typeface				mFaceR;
	private View					mView;
	EditTanksDialogListener			mListener;
	private EditText				mVolumeField;
//	private EditText				mDurationField;
	private EditText				mStartPressureField;
	private EditText				mEndPressureField;
	private EditText				mStartTimeField;
	private Integer					mIndex;
	private Spinner					mCylinderLabel;
	private Spinner					mVolumeLabel;
	private Spinner					mMixLabel;
	private Spinner					mPressureLabel;
	
	 @Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditTanksDialogListener) activity;
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
		text.setText(getResources().getString(R.string.no_tanks));
		text.setTextColor(getResources().getColor(R.color.dark_grey));
		text.setGravity(Gravity.CENTER);
		layout.addView(text);
	}
	
	private void				openTanksEdit(Integer index)
	{
		mIndex = index;
		LinearLayout tankslist = (LinearLayout) mView.findViewById(R.id.tanksfields);
		tankslist.removeAllViews();
		final float scale = getResources().getDisplayMetrics().density;
		Tank tank = mTanks.get(index);
		
		Button add_button = (Button) mView.findViewById(R.id.add_button);
		add_button.setVisibility(Button.GONE);

		LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		//CYLINDER ROW
		LinearLayout cylinder = new LinearLayout(getActivity().getApplicationContext());
		cylinder.setLayoutParams(params);
		cylinder.setOrientation(LinearLayout.HORIZONTAL);
		cylinder.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView cylinder_title = new TextView(getActivity().getApplicationContext());
		cylinder_title.setTypeface(mFaceR);
		cylinder_title.setTextColor(getResources().getColor(R.color.dark_grey));
		cylinder_title.setTextSize(25);
		cylinder_title.setText(getResources().getString(R.string.cylinder_label) + ":");
		cylinder_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		cylinder.addView(cylinder_title);
		
		mCylinderLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> adapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapt.add("1");
		adapt.add("2");
		adapt.setDropDownViewResource(R.layout.units_spinner_fields);
		mCylinderLabel.setAdapter(adapt);
		cylinder.addView(mCylinderLabel);
		
		tankslist.addView(cylinder);
		
		//VOLUME ROW
		LinearLayout volume = new LinearLayout(getActivity().getApplicationContext());
		volume.setLayoutParams(params);
		volume.setOrientation(LinearLayout.HORIZONTAL);
		volume.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView volume_title = new TextView(getActivity().getApplicationContext());
		volume_title.setTypeface(mFaceR);
		volume_title.setTextColor(getResources().getColor(R.color.dark_grey));
		volume_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		volume_title.setTextSize(25);
		volume_title.setText(getResources().getString(R.string.volume_label) +":");
		volume.addView(volume_title);
		
		mVolumeField = new EditText(getActivity().getApplicationContext());
		mVolumeField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mVolumeField.setTypeface(mFaceR);
		mVolumeField.setTextColor(getResources().getColor(R.color.dark_grey));
		mVolumeField.setTextSize(25);
		mVolumeField.setText(tank.getVolume().toString());
		volume.addView(mVolumeField);
		
		mVolumeLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		String volume_unit = tank.getVolumeUnit();
		if (volume_unit.equals("L"))
		{
			adapter.add(getResources().getString(R.string.unit_liter));
			adapter.add(getResources().getString(R.string.unit_cuft));

		}
		else
		{
			adapter.add(getResources().getString(R.string.unit_cuft));
			adapter.add(getResources().getString(R.string.unit_liter));
		}
		adapter.setDropDownViewResource(R.layout.units_spinner_fields);
		mVolumeLabel.setAdapter(adapter);
		volume.addView(mVolumeLabel);
		
		tankslist.addView(volume);
		
		LinearLayout gasmix = new LinearLayout(getActivity().getApplicationContext());
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		gasmix.setLayoutParams(params);
		gasmix.setOrientation(LinearLayout.HORIZONTAL);
		gasmix.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView mix_title = new TextView(getActivity().getApplicationContext());
		mix_title.setTypeface(mFaceR);
		mix_title.setTextColor(getResources().getColor(R.color.dark_grey));
		mix_title.setTextSize(25);
		mix_title.setText(getResources().getString(R.string.mix_label) + ":");
		mix_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		gasmix.addView(mix_title);
		
		mMixLabel = new Spinner(getActivity().getApplicationContext());
		
		ArrayAdapter<String> adapt_mix = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		adapt_mix.add(getResources().getString(R.string.air_mix));
		adapt_mix.add(getResources().getString(R.string.nitrox32));
		adapt_mix.add(getResources().getString(R.string.nitrox36));
		adapt_mix.add(getResources().getString(R.string.nitrox40));
		adapt_mix.add(getResources().getString(R.string.custom_mix));
		adapt_mix.setDropDownViewResource(R.layout.units_spinner_fields);
		mMixLabel.setAdapter(adapt_mix);
		gasmix.addView(mMixLabel);
		
		tankslist.addView(gasmix);
		
		
		//PRESSURE ROW
		LinearLayout pressure = new LinearLayout(getActivity().getApplicationContext());
		pressure.setLayoutParams(params);
		pressure.setOrientation(LinearLayout.HORIZONTAL);
		pressure.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView start_pressure_title = new TextView(getActivity().getApplicationContext());
		start_pressure_title.setTypeface(mFaceR);
		start_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
		start_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		start_pressure_title.setTextSize(25);
		start_pressure_title.setText(getResources().getString(R.string.start_pressure_label) +":");
		pressure.addView(start_pressure_title);
		
		mStartPressureField = new EditText(getActivity().getApplicationContext());
		mStartPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mStartPressureField.setTypeface(mFaceR);
		mStartPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
		mStartPressureField.setTextSize(25);
		mStartPressureField.setText(tank.getPStartValue().toString());
		pressure.addView(mStartPressureField);
		
		TextView end_pressure_title = new TextView(getActivity().getApplicationContext());
		end_pressure_title.setTypeface(mFaceR);
		end_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
		end_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		end_pressure_title.setTextSize(25);
		end_pressure_title.setText(getResources().getString(R.string.end_pressure_label) +":");
		pressure.addView(end_pressure_title);
		
		mEndPressureField = new EditText(getActivity().getApplicationContext());
		mEndPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mEndPressureField.setTypeface(mFaceR);
		mEndPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
		mEndPressureField.setTextSize(25);
		mEndPressureField.setText(tank.getPEndValue().toString());
		pressure.addView(mStartPressureField);
		
		mPressureLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> press_adapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
		String pressure_unit = tank.getPUnit();
		if (pressure_unit.equals("bar"))
		{
			press_adapt.add(getResources().getString(R.string.unit_bar));
			press_adapt.add(getResources().getString(R.string.unit_psi));

		}
		else
		{
			press_adapt.add(getResources().getString(R.string.unit_psi));
			press_adapt.add(getResources().getString(R.string.unit_bar));
		}
		press_adapt.setDropDownViewResource(R.layout.units_spinner_fields);
		mPressureLabel.setAdapter(press_adapt);
		pressure.addView(mPressureLabel);
		
		tankslist.addView(pressure);
		
		//Start Time ROW
		LinearLayout startTime = new LinearLayout(getActivity().getApplicationContext());
		startTime.setLayoutParams(params);
		startTime.setOrientation(LinearLayout.HORIZONTAL);
		startTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
		
		TextView startTime_title = new TextView(getActivity().getApplicationContext());
		startTime_title.setTypeface(mFaceR);
		startTime_title.setTextColor(getResources().getColor(R.color.dark_grey));
		startTime_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		startTime_title.setTextSize(25);
		startTime_title.setText(getResources().getString(R.string.volume_label) +":");
		startTime.addView(startTime_title);
		
		mStartTimeField = new EditText(getActivity().getApplicationContext());
		mStartTimeField.setRawInputType(InputType.TYPE_CLASS_NUMBER );
		mStartTimeField.setTypeface(mFaceR);
		mStartTimeField.setTextColor(getResources().getColor(R.color.dark_grey));
		mStartTimeField.setTextSize(25);
		mStartTimeField.setText(tank.getTimeStart().toString());
		startTime.addView(mStartTimeField);
		
		TextView startTime_label = new TextView(getActivity().getApplicationContext());
		startTime_label.setTypeface(mFaceR);
		startTime_label.setTextColor(getResources().getColor(R.color.dark_grey));
		startTime_label.setTextSize(25);
		startTime_label.setText(getResources().getString(R.string.unit_min));
		startTime.addView(startTime_label);
		
		
        Button cancel = (Button) mView.findViewById(R.id.cancel);
        cancel.setTypeface(mFaceR);
        cancel.setText(getResources().getString(R.string.cancel));
        cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
				mVolumeField = null;
				mStartPressureField = null;
				mEndPressureField = null;
				mStartTimeField = null;
				mIndex = null;
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
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
				if (mVolumeField.getText().toString().isEmpty())
					mVolumeField.setText("0");
				if (mStartPressureField.getText().toString().isEmpty())
					mStartPressureField.setText("0");
				if (mEndPressureField.getText().toString().isEmpty())
					mEndPressureField.setText("0");
				if (mStartTimeField.getText().toString().isEmpty())
					mStartTimeField.setText("0");
				
				mTanks.set(mIndex, new SafetyStop(Integer.parseInt(mDepthField.getText().toString()), Integer.parseInt(mDurationField.getText().toString()), (String)mDepthLabel.getSelectedItem()));
				mDepthField = null;
				mDepthField = null;
				mIndex = null;
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
		depth_title.setText(getResources().getString(R.string.depth_label) + " :");
		depth.addView(depth_title);
		
		mDepthField = new EditText(getActivity().getApplicationContext());
		mDepthField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDepthField.setTypeface(mFaceR);
		mDepthField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDepthField.setTextSize(30);
		mDepthField.setText("3");
		depth.addView(mDepthField);
		
		mDepthLabel = new Spinner(getActivity().getApplicationContext());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner);
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
		duration_title.setText(getResources().getString(R.string.duration_label) + " :");
		duration_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
		duration.addView(duration_title);
		
		mDurationField = new EditText(getActivity().getApplicationContext());
		mDurationField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		mDurationField.setTypeface(mFaceR);
		mDurationField.setTextColor(getResources().getColor(R.color.dark_grey));
		mDurationField.setTextSize(30);
		mDurationField.setText("3");
		duration.addView(mDurationField);
		
		TextView duration_label = new TextView(getActivity().getApplicationContext());
		duration_label.setTypeface(mFaceR);
		duration_label.setTextColor(getResources().getColor(R.color.dark_grey));
		duration_label.setTextSize(30);
		duration_label.setText(getResources().getString(R.string.unit_min));
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
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDurationField.getWindowToken(), 0);
				mDepthField = null;
				mDepthField = null;
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
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDurationField.getWindowToken(), 0);
				if (mDepthField.getText().toString().isEmpty())
					mDepthField.setText("0");
				if (mDurationField.getText().toString().isEmpty())
					mDurationField.setText("0");
				mTanks.add(new SafetyStop(Integer.parseInt(mDepthField.getText().toString()), Integer.parseInt(mDurationField.getText().toString()), (String)mDepthLabel.getSelectedItem()));
				mDepthField = null;
				mDepthField = null;
				openSafetyStopsList();
			}
		});
	}
	
	private void				deleteSafetyStop(int index)
	{
		mTanks.remove(index);
		openSafetyStopsList();
	}
	
	private void				openSafetyStopsList()
	{
		LinearLayout tankslist = (LinearLayout) mView.findViewById(R.id.tanksfields);
		tankslist.removeAllViews();
		if (mTanks.size() == 0)
			addNoStops(tankslist);
		else
		{
			for (int i = 0, length = mTanks.size(); i < length; i++)
			{
				final float scale = getResources().getDisplayMetrics().density;
				
				LinearLayout tankElem = new LinearLayout(getActivity().getApplicationContext());
				LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				tankElem.setTag(i);
				tankElem.setLayoutParams(params);
				tankElem.setOrientation(LinearLayout.HORIZONTAL);
				tankElem.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
				tankElem.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Edit Safety Stops
						openTanksEdit((Integer) v.getTag());
					}
				});

				TextView text = new TextView(getActivity().getApplicationContext());
				text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
				text.setTypeface(mFaceR);
				text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
				text.setTextSize(25);
				
				//Writing the summary string of the tank
				String tanksSummary = "";
				Tank tank = mTanks.get(i);
				if(tank.getMultitank() == 2)
					tanksSummary = "2x";
				tanksSummary += tank.getVolumeValue() + tank.getVolumeUnit() + "  ";
				if(tank.getGas().equals("custom"))
					tanksSummary += tank.getO2()+"O2/"+tank.getHe()+"He/"+tank.getN2()+"N2";
				else
					tanksSummary += tank.getGas();
				tanksSummary +="\n";
				tanksSummary += tank.getPStartValue() + " \u2192 " + tank.getPEndValue() + tank.getPUnit() + "  ";
				if(tank.getTimeStart() != null)
					tanksSummary += "Start:" + tank.getTimeStart()+"min";
				
				text.setText(tanksSummary);
				text.setTextColor(getResources().getColor(R.color.dark_grey));
				text.setGravity(Gravity.LEFT);
				
				tankElem.addView(text);
				
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
						deleteSafetyStop((Integer) v.getTag());+++
					}
				});
				
				tankElem.addView(delete);
				
				tankslist.addView(tankElem);
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
				openSafetyStopsNew();***
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
				mModel.getDives().get(getArguments().getInt("index")).setTanks(mTanks);
				mListener.onTanksEditComplete(EditTanksDialogFragment.this);
				dismiss();
			}
		});
	}
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mFaceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		mView = inflater.inflate(R.layout.dialog_edit_tanks, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		if (mModel.getDives().get(getArguments().getInt("index")).getTanks() != null){
			mTanks = (ArrayList<Tank>) mModel.getDives().get(getArguments().getInt("index")).getTanks().clone();
			}
		else
			mTanks = new ArrayList<Tank>();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) mView.findViewById(R.id.title);
		title.setTypeface(mFaceR);
		title.setText(getResources().getString(R.string.edit_safetystops_title));
		
		openSafetyStopsList();
		return mView;
	}
}
