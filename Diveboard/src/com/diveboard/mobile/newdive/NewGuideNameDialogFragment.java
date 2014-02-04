package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.support.v4.app.DialogFragment;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewGuideNameDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditGuideNameDialogListener
	{
        void					onGuideNameEditComplete(DialogFragment dialog);
    }
	
	private EditText			mGuideName;
	private Dive				mDive;
	EditGuideNameDialogListener	mListener;
	
	 @Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditGuideNameDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onGuideNameEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_guidename, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_guidename_title));
		
		mGuideName = (EditText) view.findViewById(R.id.guidename);
		mGuideName.setTypeface(faceR);
		mGuideName.setText(mDive.getGuide());
		mGuideName.requestFocus();
		
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mGuideName.setOnEditorActionListener(this);
        
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
				mDive.setGuide(mGuideName.getText().toString());
				mListener.onGuideNameEditComplete(NewGuideNameDialogFragment.this);
				dismiss();
			}
		});
        
        faceR = null;
		return view;
	}
	
	@Override
	public boolean				onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			mDive.setTripName(mGuideName.getText().toString());
			mListener.onGuideNameEditComplete(NewGuideNameDialogFragment.this);
			this.dismiss();
			return true;
		}
		return false;
	}
}
