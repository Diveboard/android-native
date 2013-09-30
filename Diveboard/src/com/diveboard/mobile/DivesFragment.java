package com.diveboard.mobile;

import com.diveboard.model.Dive;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The fragment that displays the "fragment_dives.xml" layout
 */
public class DivesFragment extends Fragment {
	TextView date;
	TextView time;
	TextView id;
	TextView duration;
	Dive mDive;
	int mScreenheight;
	
	public DivesFragment()
	{
		System.out.println("Entre");
	}
	
	public DivesFragment(Dive dive, int screenheight)
	{
		mDive = dive;
		mScreenheight = screenheight;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_dives, container, false);
//		id = (TextView)rootView.findViewById(R.id.dive_id);
//		date = (TextView)rootView.findViewById(R.id.dive_date);
//		time = (TextView)rootView.findViewById(R.id.dive_time);
//		duration = (TextView)rootView.findViewById(R.id.dive_duration);
//		if (mDive != null)
//		{
//			date.setText(mDive.getDate());
//			time.setText(mDive.getTime());
//			id.setText(Integer.toString(mDive.getId()));
//			duration.setText(Integer.toString(mDive.getDuration()));
//		}
		RelativeLayout content = (RelativeLayout)rootView.findViewById(R.id.content);
		int contentheight = mScreenheight * 74 / 100;
		//System.out.println(mScreenheight);
		int size = contentheight * 60 / 100;
		content.setLayoutParams(new RelativeLayout.LayoutParams((int)((contentheight * 10) / 13), contentheight));
		ImageView rounded_image = (ImageView)content.findViewById(R.id.rounded_image);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(size, size);
		params1.setMargins(0, contentheight * 154 / 1000, 0, 0);
		rounded_image.setLayoutParams(params1);
		ImageView rounded_layer = (ImageView)content.findViewById(R.id.rounded_layer);
		
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(size, size);
		params2.setMargins(0, contentheight * 154 / 1000, 0, 0);
		
		rounded_layer.setLayoutParams(params2);
		//((ImageView)content.findViewById(R.id.rounded_layer)).setLayoutParams(params);
		//ImageView ring = (ImageView)content.findViewById(R.id.ring);
		//ring.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
		//ShapeDrawable ringRadius = (ShapeDrawable)ring.getBackground();
		//ringRadius.
		Bitmap bitmap = ImageHelper.getRoundedCornerBitmap(size, contentheight, 0);
		rounded_layer.setImageBitmap(bitmap);
        return rootView;
    }
}
