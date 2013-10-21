package com.diveboard.mobile;

import java.util.ArrayList;

import com.diveboard.mobile.EditDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.EditDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.EditTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class					TabEditDetailsActivity extends FragmentActivity implements EditTripNameDialogListener,
																						   EditDiveNumberDialogListener,
																						   EditDateDialogListener,
																						   EditTimeInDialogListener
{
	private ListView			optionList;
	private DiveboardModel		mModel;
	private int					mIndex;
	private OptionAdapter		mOptionAdapter;
	
	@Override
	public void onBackPressed()
	{
		Bundle bundle = new Bundle();
		
		// put
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);

		mModel = ((ApplicationController)getApplicationContext()).getModel();
		mIndex = getIntent().getIntExtra("index", -1);
		
		_displayEditList();
    }

    private void				_editTripNameDialog()
    {
    	EditTripNameDialogFragment dialog = new EditTripNameDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditTripNameDialogFragment");
    }
    
    private void				_editDiveNumberDialog()
    {
    	EditDiveNumberDialogFragment dialog = new EditDiveNumberDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditDiveNumberDialogFragment");
    }
    
    private void				_editDateDialog()
    {
    	EditDateDialogFragment dialog = new EditDateDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditDateDialogFragment");
    }
    
    private void				_editTimeInDialog()
    {
    	EditTimeInDialogFragment dialog = new EditTimeInDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditTimeInDialogFragment");
    }
    
    private void				_displayEditList()
    {
    	setContentView(R.layout.tab_edit_details);
    	Dive dive = mModel.getDives().get(mIndex);
    	
    	optionList = (ListView)findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Trip name : ", dive.getTripName()));
		if (dive.getNumber() != null)
			elem.add(new EditOption("Dive number : ", Integer.toString(dive.getNumber())));
		else
			elem.add(new EditOption("Dive number : ", Integer.toString(0)));
		elem.add(new EditOption("Date : ", dive.getDate()));
		String[] time_in = dive.getTimeIn().split("T");
		elem.add(new EditOption("Time in : ", time_in[1]));
		elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName()));
		elem.add(new EditOption("Duration : ", Integer.toString(dive.getDuration()) + " min"));
		elem.add(new EditOption("Safety stops : ", "not implemented"));
		elem.add(new EditOption("Other divers : ", "not implemented"));
		elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (dive.getTempSurface() != null)
			elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface())));
		else
			elem.add(new EditOption("Surface temperature : ", "Not defined"));
		if (dive.getTempBottom() != null)
			elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom())));
		else
			elem.add(new EditOption("Bottom temperature : ", "Not defined"));
		if (dive.getWeights() != null)
			elem.add(new EditOption("Weights : ", Double.toString(dive.getWeights())));
		else
			elem.add(new EditOption("Weights : ", "Not defined"));
		elem.add(new EditOption("Visibility : ", dive.getVisibility()));
		elem.add(new EditOption("Current : ", dive.getCurrent()));
		elem.add(new EditOption("Altitude : ", Double.toString(dive.getAltitude())));
		elem.add(new EditOption("Water type : ", dive.getWater()));
		
		mOptionAdapter = new OptionAdapter(this, elem);
		optionList.setAdapter(mOptionAdapter);
		
		optionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				switch (position)
				{
					case 0:
						_editTripNameDialog();
						break ;
					case 1:
						_editDiveNumberDialog();
						break ;
					case 2:
						_editDateDialog();
						break ;
					case 3:
						_editTimeInDialog();
						break ;
				}
			}
		});
    }
    
	@Override
	public void onTripNameEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(0)).setValue(dive.getTripName());
		mOptionAdapter.notifyDataSetChanged();
		mModel.getDataManager().save(mModel.getDives().get(mIndex));
	}
	
	@Override
	public void onDiveNumberEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getNumber() != null)
			((EditOption)mOptionAdapter.getItem(1)).setValue(dive.getNumber().toString());
		else
			((EditOption)mOptionAdapter.getItem(1)).setValue("0");
		mOptionAdapter.notifyDataSetChanged();
		mModel.getDataManager().save(mModel.getDives().get(mIndex));
	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(2)).setValue(dive.getDate());
		String[] time_in = dive.getTimeIn().split("T");
		String new_time_in = dive.getDate() + "T" + time_in[1];
		dive.setTimeIn(new_time_in);
		((EditOption)mOptionAdapter.getItem(3)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		mModel.getDataManager().save(mModel.getDives().get(mIndex));
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		String[] time_in = dive.getTimeIn().split("T");
		((EditOption)mOptionAdapter.getItem(3)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		mModel.getDataManager().save(mModel.getDives().get(mIndex));
	}
}
