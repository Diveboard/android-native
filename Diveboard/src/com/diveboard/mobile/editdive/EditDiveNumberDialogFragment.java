package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					EditDiveNumberDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditDiveNumberDialogListener
	{
        void					onDiveNumberEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private EditText			mDiveNumber;
	private EditDiveNumberDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditDiveNumberDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onDiveNumberEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_dive_number, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_divenumber_title));
		
		mDiveNumber = (EditText) view.findViewById(R.id.divenumber);
		mDiveNumber.setTypeface(faceR);
		if (mModel.getDives().get(getArguments().getInt("index")).getNumber() == null)
			mDiveNumber.setText("");
		else
			mDiveNumber.setText(Integer.toString(mModel.getDives().get(getArguments().getInt("index")).getNumber()));
		mDiveNumber.requestFocus();
		
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		mDiveNumber.setOnEditorActionListener(this);
        
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(faceR);
		cancel.setText(getResources().getString(R.string.cancel));
		cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDiveNumber.getWindowToken(), 0);
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
				if (mDiveNumber.getText().toString().equals(""))
					mModel.getDives().get(getArguments().getInt("index")).setNumber(null);
				else
					mModel.getDives().get(getArguments().getInt("index")).setNumber(Integer.parseInt(mDiveNumber.getText().toString()));
				mListener.onDiveNumberEditComplete(EditDiveNumberDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mDiveNumber.getWindowToken(), 0);
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
			if (mDiveNumber.getText().toString().equals(""))
				mModel.getDives().get(getArguments().getInt("index")).setNumber(null);
			else
				mModel.getDives().get(getArguments().getInt("index")).setNumber(Integer.parseInt(mDiveNumber.getText().toString()));
			mListener.onDiveNumberEditComplete(EditDiveNumberDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}
