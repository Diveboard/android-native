package com.diveboard.mobile.editdive;

import java.util.ArrayList;
import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class					EditDiveTypeDialogFragment extends DialogFragment
{
	public interface			EditDiveTypeDialogListener
	{
        void					onDiveTypeEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private ListView				mDiveType;
	private EditDiveTypeDialogListener	mListener;
	private Typeface			mFaceR;
	private List<String>		mList;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditDiveTypeDialogListener) activity;
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
		mFaceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_divetype, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(mFaceR);
		title.setText(getResources().getString(R.string.edit_divetype_title));
		
		mDiveType = (ListView) view.findViewById(R.id.visibility);
		mList = new ArrayList<String>();
		mList.add(getResources().getString(R.string.recreational_type));
		mList.add(getResources().getString(R.string.training_type));
		mList.add(getResources().getString(R.string.nightdive_type));
		mList.add(getResources().getString(R.string.deepdive_type));
		mList.add(getResources().getString(R.string.drift_type));
		mList.add(getResources().getString(R.string.wreck_type));
		mList.add(getResources().getString(R.string.cave_type));
		mList.add(getResources().getString(R.string.reef_type));
		mList.add(getResources().getString(R.string.photo_type));
		mList.add(getResources().getString(R.string.research_type));
		//dataAdapter.setDropDownViewResource(R.layout.spinner_item);
		
		mDiveType.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mDiveType.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, mList)
				{
					@Override
				    public View getView(int position, View convertView, ViewGroup parent)
					{
				        TextView textView = (TextView) super.getView(position, convertView, parent);
						
				        textView.setTextColor(getResources().getColor(R.color.dark_grey));
				        return textView;
				    }
				});
		
		ArrayList<String> divelist = mModel.getDives().get(getArguments().getInt("index")).getDivetype(); 
		for (int i = 0, length = divelist.size(); i < length; i++)
		{
			String elem = divelist.get(i);
			for (int j = 0, length2 = mList.size(); j < length2; j++)
			{
				if (mList.get(j).toLowerCase().equals(elem))
				{
					mDiveType.setItemChecked(j, true);
					break ;
				}
			}
		}
		
		Button cancel = (Button) view.findViewById(R.id.cancel);
		cancel.setTypeface(mFaceR);
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
		save.setTypeface(mFaceR);
		save.setText(getResources().getString(R.string.save));
		save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				ArrayList<String> result = new ArrayList<String>();
				SparseBooleanArray checked = mDiveType.getCheckedItemPositions();
				for (int i = 0, len = mList.size(); i < len; i++)
				{
					if (checked.get(i) == true)
						result.add(mList.get(i).toLowerCase());
				}				
				mModel.getDives().get(getArguments().getInt("index")).setDivetype(result);
				mListener.onDiveTypeEditComplete(EditDiveTypeDialogFragment.this);
				dismiss();
			}
		});
		
		mFaceR = null;
		return view;
	}
}