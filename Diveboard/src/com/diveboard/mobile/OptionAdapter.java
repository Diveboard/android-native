package com.diveboard.mobile;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class					OptionAdapter extends BaseAdapter
{
	List<EditOption>			option;
	LayoutInflater				inflater;
	Context						mContext;
	
	public						OptionAdapter(Context context, List<EditOption> option_list)
	{
		inflater = LayoutInflater.from(context);
		option = option_list;
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return option.size();
	}

	@Override
	public Object getItem(int arg0) {
		return option.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder					holder;
		
		Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Bold.otf");
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.option_list, null);
			holder.optTitle = (TextView) convertView.findViewById(R.id.optTitle);
			holder.optTitle.setTypeface(faceB);
			holder.optValue = (TextView) convertView.findViewById(R.id.optValue);
			holder.optValue.setTypeface(faceR);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.optTitle.setText(option.get(position).getTitle().toUpperCase());
		holder.optValue.setText(option.get(position).getValue());
		return convertView;
	}
	
	private class				ViewHolder
	{
		TextView				optTitle;
		TextView				optValue;
	}
}
