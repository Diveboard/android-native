package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.newdive.NewWaterDialogFragment;
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

public class					EditWaterDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditWaterDialogListener
	{
        void					onWaterEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private ListView				mWater;
	private EditWaterDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditWaterDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onWaterEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_water, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_water_title));
		
		mWater = (ListView) view.findViewById(R.id.water);
		List<String> list = new ArrayList<String>();
		list.add(getResources().getString(R.string.null_select));
		list.add(getResources().getString(R.string.salt_water));
		list.add(getResources().getString(R.string.fresh_water));
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, list);
		dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mWater.setAdapter(dataAdapter);
		mWater.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
			if (position == 0)
				mModel.getDives().get(getArguments().getInt("index")).setWater(null);
			else
			{
				String[] water = ((String)mWater.getItemAtPosition(position)).split(" ");
				mModel.getDives().get(getArguments().getInt("index")).setWater(water[0].toLowerCase());
			}
			mListener.onWaterEditComplete(EditWaterDialogFragment.this);
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
			if (mWater.getSelectedItemPosition() == 0)
				mModel.getDives().get(getArguments().getInt("index")).setWater(null);
			else
				mModel.getDives().get(getArguments().getInt("index")).setWater(((String)mWater.getSelectedItem()).toLowerCase());
			mListener.onWaterEditComplete(EditWaterDialogFragment.this);
			dismiss();
			return true;
		}
		return false;
	}
}