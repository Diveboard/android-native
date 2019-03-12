package com.diveboard.mobile.newdive;

import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewCurrentDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditCurrentDialogListener
	{
        void					onCurrentEditComplete(DialogFragment dialog);
    }
	private DiveboardModel				mModel;
	private Dive						mDive;
	private ListView					mCurrent;
	private EditCurrentDialogListener	mListener;
	private int							mTextSize = 20;
	
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
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		final Typeface faceR = mModel.getLatoR();
		View view = inflater.inflate(R.layout.dialog_edit_current, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_current_title));
		
		mCurrent = (ListView) view.findViewById(R.id.current);
		List<String> list = new ArrayList<String>();
		list.add(getResources().getString(R.string.null_select));
		list.add(getResources().getString(R.string.none_current));
		list.add(getResources().getString(R.string.light_current));
		list.add(getResources().getString(R.string.medium_current));
		list.add(getResources().getString(R.string.strong_current));
		list.add(getResources().getString(R.string.extreme_current));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list){
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(faceR);
				((TextView) v).setTextSize(mTextSize);
				return v;
			}
		};
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mCurrent.setAdapter(dataAdapter);
		mCurrent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
			if (position == 0)
				mDive.setCurrent(null);
			else
			{
				String[] current = ((String)mCurrent.getItemAtPosition(position)).split(" ");
				mDive.setCurrent(current[0].toLowerCase());
			}
			mListener.onCurrentEditComplete(NewCurrentDialogFragment.this);
			dismiss();
				
			}
		});
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			if (mCurrent.getSelectedItemPosition() == 0)
				mDive.setCurrent(null);
			else
			{
				String[] current = ((String)mCurrent.getSelectedItem()).split(" ");
				mDive.setCurrent(current[0].toLowerCase());
			}
			mListener.onCurrentEditComplete(NewCurrentDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}