package com.diveboard.mobile.newdive;

import java.util.ArrayList;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;

import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewTripNameDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditTripNameDialogListener
	{
        void					onTripNameEditComplete(DialogFragment dialog);
    }
	
	private EditText				mTripName;
	private Dive					mDive;
	EditTripNameDialogListener		mListener;
	private ArrayList<String>		mHints = new ArrayList<String>();
	private ArrayAdapter<String> 	mAdapter;
	
	 @Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditTripNameDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onTripNameEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		final Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_tripname, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(faceR);
		title.setText(getResources().getString(R.string.edit_tripname_title));
		
		mHints.addAll(((ApplicationController) getActivity().getApplicationContext()).getModel().getUser().getTripNames());		
	    final ListView lv;
	    lv = (ListView) view.findViewById(R.id.hintsList);
	    mAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, mHints){
	    	public View getView(int position, View convertView, ViewGroup parent) {
	   		 View v = super.getView(position, convertView, parent);
	   		 ((TextView) v).setTypeface(faceR);
	   		 ((TextView) v).setTextSize(25);
	   		 return v;
	   	 }
	    };
	    lv.setAdapter(mAdapter);
	    lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				mTripName.setText(((TextView) v.findViewById(R.id.itemTV)).getText());
				lv.setVisibility(View.GONE);
//				System.out.println("Prueba");
				
			}
		});
		
		mTripName = (EditText) view.findViewById(R.id.tripname);
		mTripName.setTypeface(faceR);
		mTripName.setText(mDive.getTripName());
		mTripName.requestFocus();
		
        
        mTripName.setOnEditorActionListener(this);
        mTripName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				System.out.println("start " + start + " before " + before + " count " +count);
				if(count > 1){
					lv.setVisibility(View.GONE);
					mAdapter.getFilter().filter(s);
					lv.setVisibility(View.VISIBLE);
				}
				else
					lv.setVisibility(View.GONE);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setTypeface(faceR);
        cancel.setText(getResources().getString(R.string.cancel));
        cancel.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTripName.getWindowToken(), 0);
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
				mDive.setTripName(mTripName.getText().toString());
				mListener.onTripNameEditComplete(NewTripNameDialogFragment.this);
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTripName.getWindowToken(), 0);
				dismiss();
			}
		});
		return view;
	}
	
	@Override
	public boolean				onEditorAction(TextView v, int actionId, KeyEvent event)
	{
		if (EditorInfo.IME_ACTION_DONE == actionId)
		{
			mDive.setTripName(mTripName.getText().toString());
			mListener.onTripNameEditComplete(NewTripNameDialogFragment.this);
			this.dismiss();
			return true;
		}
		return false;
	}
}
