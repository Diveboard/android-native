package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class					NewTimeInDialogFragment extends DialogFragment
{
	public interface			EditTimeInDialogListener
	{
        void					onTimeInEditComplete(DialogFragment dialog);
    }
	
	private Dive		mDive;
	private TimePicker			mTimeIn;
	private EditTimeInDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditTimeInDialogListener) activity;
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
		View view = inflater.inflate(R.layout.dialog_edit_time_in, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_time_in_title));
		
		mTimeIn = (TimePicker) view.findViewById(R.id.time_in);
		mTimeIn.setIs24HourView(true);
		String time_in[] = mDive.getTimeIn().split("T");
		String[] time_array = time_in[1].split(":");
		mTimeIn.setCurrentHour(Integer.parseInt(time_array[0]));
		mTimeIn.setCurrentMinute(Integer.parseInt(time_array[1]));
		
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
				mTimeIn.clearFocus();
				String[] time_in = mDive.getTimeIn().split("T");
				String new_timein = time_in[0] + "T";
				if (mTimeIn.getCurrentHour() < 10)
					new_timein += "0" + mTimeIn.getCurrentHour();
				else
					new_timein += mTimeIn.getCurrentHour();
				new_timein += ":";
				if (mTimeIn.getCurrentMinute() < 10)
					new_timein += "0" + mTimeIn.getCurrentMinute();
				else
					new_timein += mTimeIn.getCurrentMinute();
				new_timein += ":00Z";
				mDive.setTimeIn(new_timein);
				String new_time = "";
				if (mTimeIn.getCurrentHour() < 10)
					new_time += "0" + mTimeIn.getCurrentHour();
				else
					new_time += mTimeIn.getCurrentHour();
				new_time += ":";
				if (mTimeIn.getCurrentMinute() < 10)
					new_time += "0" + mTimeIn.getCurrentMinute();
				else
					new_time += mTimeIn.getCurrentMinute();
				mDive.setTime(new_time);
				mListener.onTimeInEditComplete(NewTimeInDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}
}