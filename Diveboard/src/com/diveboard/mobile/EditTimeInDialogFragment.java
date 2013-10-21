package com.diveboard.mobile;

import com.diveboard.mobile.EditDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.model.DiveboardModel;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class					EditTimeInDialogFragment extends DialogFragment
{
	public interface			EditTimeInDialogListener
	{
        void					onTimeInEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
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
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_time_in_title));
		
		mTimeIn = (TimePicker) view.findViewById(R.id.time_in);
		mTimeIn.setIs24HourView(true);
		String time_in[] = mModel.getDives().get(getArguments().getInt("index")).getTimeIn().split("T");
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
				String[] time_in = mModel.getDives().get(getArguments().getInt("index")).getTimeIn().split("T");
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
				mModel.getDives().get(getArguments().getInt("index")).setTimeIn(new_timein);
				mListener.onTimeInEditComplete(EditTimeInDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}
}
