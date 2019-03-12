package com.diveboard.mobile.editdive;

import com.diveboard.mobile.R;
import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					EditConfirmDialogFragment extends DialogFragment implements OnEditorActionListener
{
	public interface			EditConfirmDialogListener
	{
        void					onConfirmEditComplete(DialogFragment dialog);
    }
	
	private DiveboardModel		mModel;
	private EditConfirmDialogListener	mListener;
	
	@Override
	 public void onAttach(Activity activity)
	 {
		super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditConfirmDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onWeightsEditComplete");
		 }
	 }
	
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Typeface faceB = ResourcesCompat.getFont(getActivity().getApplicationContext(), R.font.lato_regular);
		Typeface faceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
		View view = inflater.inflate(R.layout.dialog_edit_confirm, container);
		mModel = ((ApplicationController) getActivity().getApplicationContext()).getModel();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView text = (TextView) view.findViewById(R.id.exitTV);
		title.setTypeface(faceB);
		title.setText(getResources().getString(R.string.exit_title));
		
		text.setTypeface(faceR);
		text.setText(getResources().getString(R.string.edit_confirm_title));
        
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
				mListener.onConfirmEditComplete(EditConfirmDialogFragment.this);
				dismiss();
			}
		});
		
        faceR = null;
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
