package com.diveboard.mobile.newdive;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.WaitDialogFragment;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment;
import com.diveboard.mobile.editdive.EditConfirmDialogFragment.EditConfirmDialogListener;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveCreateListener;
import com.diveboard.model.DiveboardModel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TabNewNotesFragment extends Fragment
{
	private Typeface					mFaceR;
	private Dive						mDive;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		
		mDive = ((ApplicationController)getActivity().getApplicationContext()).getTempDive();
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_notes, container, false);
	    
	    mFaceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
	    
	    NewDiveActivity.mNotes = (EditText) rootView.findViewById(R.id.notes);
	    NewDiveActivity.mNotes.setTypeface(mFaceR);
	    if (mDive.getNotes() != null)
	    	NewDiveActivity.mNotes.setText(mDive.getNotes());
	    
	    mFaceR = null;
	    return rootView;
    }
}
