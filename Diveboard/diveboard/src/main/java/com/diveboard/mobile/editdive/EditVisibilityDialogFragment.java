package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class					EditVisibilityDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditVisibilityDialogListener
	{
        void					onVisibilityEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel					mModel;
	private ListView						mVisibility;
	private EditVisibilityDialogListener	mListener;
	private	int								mTextSize = 20;
	
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
		
		View view = inflater.inflate(R.layout.dialog_edit_visibility, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		final Typeface faceR = mModel.getLatoR();
		
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
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list){
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(faceR);
				((TextView) v).setTextSize(mTextSize);
				return v;
			}
		};
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mVisibility.setAdapter(dataAdapter);
		mVisibility.setAdapter(dataAdapter);
		mVisibility.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
			if (position == 0)
				mModel.getDives().get(getArguments().getInt("index")).setVisibility(null);
			else
			{
				String[] visibility = ((String)mVisibility.getItemAtPosition(position)).split(" ");
				mModel.getDives().get(getArguments().getInt("index")).setVisibility(visibility[0].toLowerCase());
			}
			mListener.onVisibilityEditComplete(EditVisibilityDialogFragment.this);
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
			if (mVisibility.getSelectedItemPosition() == 0)
				mModel.getDives().get(getArguments().getInt("index")).setVisibility(null);
			else
			{
				String[] visibility = ((String)mVisibility.getSelectedItem()).split(" ");
				mModel.getDives().get(getArguments().getInt("index")).setVisibility(visibility[0].toLowerCase());
			}
			mListener.onVisibilityEditComplete(EditVisibilityDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}