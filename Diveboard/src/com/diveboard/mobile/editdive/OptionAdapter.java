package com.diveboard.mobile.editdive;

import java.util.List;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.model.Dive;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class					OptionAdapter extends BaseAdapter
{
	List<EditOption>			option;
	LayoutInflater				inflater;
	Context						mContext;
	Dive						mDive;
	View						mConvertView;
	
	public						OptionAdapter(Context context, List<EditOption> option_list, Dive dive)
	{
		inflater = LayoutInflater.from(context);
		option = option_list;
		mContext = context;
		mDive = dive;
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
		
		if (option.get(position).getType() == 0)
		{
			Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Light.ttf");
			Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Regular.ttf");
//			if (convertView == null)
//			{
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.option_list, null);
				holder.optTitle = (TextView) convertView.findViewById(R.id.optTitle);
				holder.optTitle.setTypeface(faceB);
				holder.optTitle.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
				holder.optValue = (TextView) convertView.findViewById(R.id.optValue);
				holder.optValue.setTypeface(faceR);
				holder.optValue.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
				convertView.setTag(holder);
//			}
//			else
//				holder = (ViewHolder) convertView.getTag();
			
			holder.optTitle.setText(option.get(position).getTitle().toUpperCase());
			holder.optValue.setText(option.get(position).getValue());
			mConvertView = convertView;
			return convertView;
		}
		else if (option.get(position).getType() == 1)
		{
			// Special Privacy Field
			Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Light.ttf");
			Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Regular.ttf");
			
			holder = new ViewHolder();
			
			if (Build.VERSION.SDK_INT < 14)
			{
				// Toggle Select
				convertView = inflater.inflate(R.layout.option_list_toggle, null);
				holder.optTitle = (TextView) convertView.findViewById(R.id.optTitle);
				holder.optTitle.setTypeface(faceB);
				holder.optTitle.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
				holder.optToggle = (ToggleButton) convertView.findViewById(R.id.optToggle);
				holder.optToggle.setTypeface(faceR);
				holder.optToggle.setTextOff(mContext.getResources().getString(R.string.dive_public_label));
				holder.optToggle.setTextOn(mContext.getResources().getString(R.string.dive_private_label));
				holder.optToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						int privacy = (isChecked == true) ? 1 : 0;
						mDive.setPrivacy(privacy);
					}
				});
				convertView.setTag(holder);
				
				holder.optTitle.setText(option.get(position).getTitle().toUpperCase());
				if (option.get(position).getValue().equals(mContext.getResources().getString(R.string.dive_public_label)))
					holder.optToggle.setChecked(false);
				else
					holder.optToggle.setChecked(true);
				return convertView;
			}
			else
			{
				// Switch Select
				convertView = inflater.inflate(R.layout.option_list_switch, null);
				holder.optTitle = (TextView) convertView.findViewById(R.id.optTitle);
				holder.optTitle.setTypeface(faceB);
				holder.optTitle.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
				holder.optSwitch = (Switch) convertView.findViewById(R.id.optSwitch);
				holder.optSwitch.setTypeface(faceR);
				holder.optSwitch.setTextOff(mContext.getResources().getString(R.string.dive_private_label));
				holder.optSwitch.setTextOn(mContext.getResources().getString(R.string.dive_public_label));
				holder.optSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						int privacy = (isChecked == true) ? 0 : 1;
						mDive.setPrivacy(privacy);
					}
				});
				convertView.setTag(holder);
				
				holder.optTitle.setText(option.get(position).getTitle().toUpperCase());
				if (option.get(position).getValue().equals(mContext.getResources().getString(R.string.dive_public_label)))
					holder.optSwitch.setChecked(true);
				else
					holder.optSwitch.setChecked(false);
				return convertView;
			}
		}
		return null;
	}
	
	public View getConvertView() {
		return mConvertView;
	}

	public void setConvertView(View mConvertView) {
		this.mConvertView = mConvertView;
	}

	private class				ViewHolder
	{
		TextView				optTitle;
		TextView				optValue;
		ToggleButton			optToggle;
		Switch					optSwitch;
	}
}
