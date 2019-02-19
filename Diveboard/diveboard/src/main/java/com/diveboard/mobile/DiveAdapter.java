package com.diveboard.mobile;

import java.util.ArrayList;

import com.diveboard.model.Dive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiveAdapter extends BaseAdapter {
	ArrayList<Dive> dives;
	TextView date;
	TextView time;
	
	LayoutInflater inflater;
	
	public DiveAdapter(Context context, ArrayList<Dive> dives) {
		inflater = LayoutInflater.from(context);
		this.dives = dives;
	}
	
	@Override
	public int getCount() {
		return dives.size();
	}

	@Override
	public Object getItem(int position) {
		return dives.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		convertView = inflater.inflate(R.layout.dive, null);
//		date = (TextView)convertView.findViewById(R.id.dive_date);
//		time = (TextView)convertView.findViewById(R.id.dive_time);
//		date.setText(dives.get(position).getDate());
//		time.setText(dives.get(position).getTime());
		return convertView;
	}

}
