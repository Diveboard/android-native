package com.diveboard.mobile.editdive;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class					VisibilityAdapter extends ArrayAdapter<String>
{
	int							_resource;
	Typeface					_tf;
	
	public						VisibilityAdapter(Context context, int resource, List<String> items)
	{
		super(context, resource, items);
		_resource = resource;
		_tf = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Regular.otf");
	}
	
//	@Override
//	public View					getView(int position, View convertView, ViewGroup parent)
//	{
//		TextView spinner_text = (TextView)findViewById(R.id.text1);
//	    spinner_text.setTypeface(_tf);
//	}
}
