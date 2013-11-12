package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Temperature;

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

public class					EditCurrentDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditCurrentDialogListener
	{
        void					onCurrentEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private Spinner				mCurrent;
	private EditCurrentDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditCurrentDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onCurrentEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		View view = inflater.inflate(R.layout.dialog_edit_current, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_current_title));
		
		mCurrent = (Spinner) view.findViewById(R.id.current);
		List<String> list = new ArrayList<String>();
		list.add(getResources().getString(R.string.none_current));
		list.add(getResources().getString(R.string.light_current));
		list.add(getResources().getString(R.string.medium_current));
		list.add(getResources().getString(R.string.strong_current));
		list.add(getResources().getString(R.string.extreme_current));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mCurrent.setAdapter(dataAdapter);
		if (mModel.getDives().get(getArguments().getInt("index")).getCurrent() != null)
		{
			if (mModel.getDives().get(getArguments().getInt("index")).getCurrent().compareTo("light") == 0)
				mCurrent.setSelection(1);
			else if (mModel.getDives().get(getArguments().getInt("index")).getCurrent().compareTo("medium") == 0)
				mCurrent.setSelection(2);
			else if (mModel.getDives().get(getArguments().getInt("index")).getCurrent().compareTo("strong") == 0)
				mCurrent.setSelection(3);
			else if (mModel.getDives().get(getArguments().getInt("index")).getCurrent().compareTo("extreme") == 0)
				mCurrent.setSelection(4);
		}
		
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
				String[] current = ((String)mCurrent.getSelectedItem()).split(" ");
				mModel.getDives().get(getArguments().getInt("index")).setCurrent(current[0].toLowerCase());
				mListener.onCurrentEditComplete(EditCurrentDialogFragment.this);
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
			String[] current = ((String)mCurrent.getSelectedItem()).split(" ");
			mModel.getDives().get(getArguments().getInt("index")).setCurrent(current[0].toLowerCase());
			mListener.onCurrentEditComplete(EditCurrentDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}