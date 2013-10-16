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

public class					EditDateDialogFragment extends DialogFragment
{
	public interface			EditDateDialogListener
	{
        void					onDateEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private DatePicker			mDate;
	private EditDateDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditDateDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onDateEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_date, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_date_title));
		
		mDate = (DatePicker) view.findViewById(R.id.date);
		String[] date_array = mModel.getDives().get(getArguments().getInt("index")).getDate().split("-");
		
		mDate.updateDate(Integer.parseInt(date_array[0]), Integer.parseInt(date_array[1]), Integer.parseInt(date_array[2]));
		
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
				//mModel.getDives().get(getArguments().getInt("index")).setNumber(Integer.parseInt(mDiveNumber.getText().toString()));
				mListener.onDateEditComplete(EditDateDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}
}
