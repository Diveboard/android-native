package com.diveboard.mobile.newdive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class					NewVisibilityDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditVisibilityDialogListener
	{
        void					onVisibilityEditComplete(DialogFragment dialog);
    }
	
	private Dive				mDive;
	private ListView				mVisibility;
	private EditVisibilityDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditVisibilityDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onVisibilityEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_visibility, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_visibility_title));
		
		mVisibility = (ListView) view.findViewById(R.id.visibility);
		List<String> list = new ArrayList<String>();
		list.add(getResources().getString(R.string.null_select));
		list.add(getResources().getString(R.string.bad_visibility));
		list.add(getResources().getString(R.string.average_visibility));
		list.add(getResources().getString(R.string.good_visibility));
		list.add(getResources().getString(R.string.excellent_visibility));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mVisibility.setAdapter(dataAdapter);
		mVisibility.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
			if (position == 0)
				mDive.setVisibility(null);
			else
			{
				String[] visibility = ((String)mVisibility.getItemAtPosition(position)).split(" ");
				mDive.setVisibility(visibility[0].toLowerCase());
			}
			mListener.onVisibilityEditComplete(NewVisibilityDialogFragment.this);
			dismiss();
				
			}
		});
//		if (mDive.getVisibility() == null)
//			mVisibility.setSelection(0);
//		else if (mDive.getVisibility().compareTo("bad") == 0)
//			mVisibility.setSelection(1);
//		else if (mDive.getVisibility().compareTo("average") == 0)
//			mVisibility.setSelection(2);
//		else if (mDive.getVisibility().compareTo("good") == 0)
//			mVisibility.setSelection(3);
//		else if (mDive.getVisibility().compareTo("excellent") == 0)
//			mVisibility.setSelection(4);
//		
//		Button cancel = (Button) view.findViewById(R.id.cancel);
//		cancel.setTypeface(faceR);
//		cancel.setText(getResources().getString(R.string.cancel));
//		cancel.setOnClickListener(new OnClickListener()
//        {
//			@Override
//			public void onClick(View v)
//			{
//				dismiss();
//			}
//		});
//		
//		Button save = (Button) view.findViewById(R.id.save);
//		save.setTypeface(faceR);
//		save.setText(getResources().getString(R.string.save));
//		save.setOnClickListener(new OnClickListener()
//        {
//			@Override
//			public void onClick(View v)
//			{
//				if (mVisibility.getSelectedItemPosition() == 0)
//					mDive.setVisibility(null);
//				else
//				{
//					String[] visibility = ((String)mVisibility.getSelectedItem()).split(" ");
//					mDive.setVisibility(visibility[0].toLowerCase());
//				}
//				mListener.onVisibilityEditComplete(NewVisibilityDialogFragment.this);
//				dismiss();
//			}
//		});
		
        faceR = null;
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			if (mVisibility.getSelectedItemPosition() == 0)
				mDive.setVisibility(null);
			else
			{
				String[] visibility = ((String)mVisibility.getSelectedItem()).split(" ");
				mDive.setVisibility(visibility[0].toLowerCase());
			}
			mListener.onVisibilityEditComplete(NewVisibilityDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}