package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
	    
	    mFaceR = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Light.ttf");
	    
	    NewDiveActivity.mNotes = (EditText) rootView.findViewById(R.id.notes);
	    NewDiveActivity.mNotes.setTypeface(mFaceR);
	    if (mDive.getNotes() != null)
	    	NewDiveActivity.mNotes.setText(mDive.getNotes());
	    
	    mFaceR = null;
	    return rootView;
    }
}
