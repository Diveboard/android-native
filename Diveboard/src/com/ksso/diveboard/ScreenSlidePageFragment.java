package com.ksso.diveboard;

import com.ksso.model.Dive;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScreenSlidePageFragment extends Fragment {
	TextView date;
	TextView time;
	TextView id;
	TextView duration;
	Dive mDive;
	
	public ScreenSlidePageFragment()
	{
		System.out.println("Entre");
	}
	
	public ScreenSlidePageFragment(Dive dive)
	{
		mDive = dive;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
		id = (TextView)rootView.findViewById(R.id.dive_id);
		date = (TextView)rootView.findViewById(R.id.dive_date);
		time = (TextView)rootView.findViewById(R.id.dive_time);
		duration = (TextView)rootView.findViewById(R.id.dive_duration);
		if (mDive != null)
		{
			date.setText(mDive.getDate());
			time.setText(mDive.getTime());
			id.setText(Integer.toString(mDive.getId()));
			duration.setText(Integer.toString(mDive.getDuration()));
		}
        return rootView;
    }
}
