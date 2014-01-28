package com.diveboard.model;

import com.diveboard.mobile.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class					FirstFragment extends Fragment
{
	private String				title;
    private int					page;
	
	public static FirstFragment	newInstance(int page, String title)
	{
		FirstFragment fragmentFirst = new FirstFragment();
		Bundle args = new Bundle();
		args.putInt("page", page);
		args.putString("title", title);
		fragmentFirst.setArguments(args);
		return fragmentFirst;
	}
	
	@Override
    public void					onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		page = getArguments().getInt("page", 0);
		title = getArguments().getString("title");
    }
	
    @Override
    public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	View view = inflater.inflate(R.layout.fragment_first, container, false);
    	TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
    	tvLabel.setText(page + " -- " + title);
    	return view;
    }
}
