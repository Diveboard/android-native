package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveboardLoginActivity;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditAltitudeDialogFragment.EditAltitudeDialogListener;
import com.diveboard.mobile.editdive.EditBottomTempDialogFragment.EditBottomTempDialogListener;
import com.diveboard.mobile.editdive.EditCurrentDialogFragment.EditCurrentDialogListener;
import com.diveboard.mobile.editdive.EditDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.editdive.EditDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.editdive.EditDurationDialogFragment.EditDurationDialogListener;
import com.diveboard.mobile.editdive.EditMaxDepthDialogFragment.EditMaxDepthDialogListener;
import com.diveboard.mobile.editdive.EditSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.editdive.EditTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.editdive.EditVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.editdive.EditWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.editdive.EditWeightsDialogFragment.EditWeightsDialogListener;

import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class					TabEditDetailsActivity extends FragmentActivity implements EditTripNameDialogListener,
																						   EditDiveNumberDialogListener,
																						   EditDateDialogListener,
																						   EditTimeInDialogListener,
																						   EditMaxDepthDialogListener,
																						   EditDurationDialogListener,
																						   EditSurfaceTempDialogListener,
																						   EditBottomTempDialogListener,
																						   EditWeightsDialogListener,
																						   EditVisibilityDialogListener,
																						   EditCurrentDialogListener,
																						   EditAltitudeDialogListener,
																						   EditWaterDialogListener
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
		mModel.getDives().get(mIndex).clearEditList();
	};
	
	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
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
    
    private void				_editMaxDepth()
    {
    	EditMaxDepthDialogFragment dialog = new EditMaxDepthDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditMaxDepthDialogFragment");
    }
    
    private void				_editDuration()
    {
    	EditDurationDialogFragment dialog = new EditDurationDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditDurationDialogFragment");
    }
    
    private void				_editSurfaceTemp()
    {
    	EditSurfaceTempDialogFragment dialog = new EditSurfaceTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditSurfaceTempDialogFragment");
    }
    
    private void				_editBottomTemp()
    {
    	EditBottomTempDialogFragment dialog = new EditBottomTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditBottomTempDialogFragment");
    }
    
    private void				_editWeights()
    {
    	EditWeightsDialogFragment dialog = new EditWeightsDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditWeightsDialogFragment");
    }
    
    private void				_editVisibility()
    {
    	EditVisibilityDialogFragment dialog = new EditVisibilityDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditVisibilityDialogFragment");
    }
    
    private void				_editCurrent()
    {
    	EditCurrentDialogFragment dialog = new EditCurrentDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditCurrentDialogFragment");
    }
    
    private void				_editAltitude()
    {
    	EditAltitudeDialogFragment dialog = new EditAltitudeDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditAltitudeDialogFragment");
    }
    
    private void				_editWater()
    {
    	EditWaterDialogFragment dialog = new EditWaterDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditWaterDialogFragment");
    }
    
    private void				_displayEditList()
    {
    	setContentView(R.layout.tab_edit_details);
    	
	    Typeface faceB = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Bold.otf");
    	
	    TextView title = (TextView) findViewById(R.id.title);
	    title.setTypeface(faceB);
	    title.setText(getResources().getString(R.string.tab_details_edit_title));
	    
	    Button save = (Button) findViewById(R.id.save_button);
	    save.setTypeface(faceB);
	    save.setText(getResources().getString(R.string.save_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				Dive dive = mModel.getDives().get(mIndex);
				mModel.getDataManager().save(dive);
				finish();
			}
		});
	    
    	Dive dive = mModel.getDives().get(mIndex);
    	
    	optionList = (ListView)findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Date : ", dive.getDate()));
		String[] time_in = dive.getTimeIn().split("T");
		elem.add(new EditOption("Time in : ", time_in[1]));
		elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName()));
		elem.add(new EditOption("Duration : ", Integer.toString(dive.getDuration()) + " min"));
		elem.add(new EditOption("Safety stops : ", "not implemented"));
		if (dive.getWeights() != null)
			elem.add(new EditOption("Weights : ", Double.toString(dive.getWeights().getWeight()) + " " + dive.getWeights().getSmallName()));
		else
			elem.add(new EditOption("Weights : ", "Not defined"));
		if (dive.getNumber() != null)
			elem.add(new EditOption("Dive number : ", Integer.toString(dive.getNumber())));
		else
			elem.add(new EditOption("Dive number : ", Integer.toString(0)));
		elem.add(new EditOption("Trip name : ", dive.getTripName()));
		elem.add(new EditOption("Other divers : ", "not implemented"));
		elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (dive.getVisibility() != null)
			elem.add(new EditOption("Visibility : ", dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1)));
		else
			elem.add(new EditOption("Visibility : ", "Not defined"));
		if (dive.getCurrent() != null)
			elem.add(new EditOption("Current : ", dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1)));
		else
			elem.add(new EditOption("Current : ", "Not defined"));
		if (dive.getTempSurface() != null)
			elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface().getTemperature()) + " °" + dive.getTempSurface().getSmallName()));
		else
			elem.add(new EditOption("Surface temperature : ", "Not defined"));
		if (dive.getTempBottom() != null)
			elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom().getTemperature()) + " °" + dive.getTempBottom().getSmallName()));
		else
			elem.add(new EditOption("Bottom temperature : ", "Not defined"));
		if (dive.getAltitude() != null)
			elem.add(new EditOption("Altitude : ", Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName()));
		else
			elem.add(new EditOption("Altitude : ", "Not defined"));
		if (dive.getWater() != null)
			elem.add(new EditOption("Water type : ", dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1)));
		else
			elem.add(new EditOption("Water type : ", "Not defined"));
		
		mOptionAdapter = new OptionAdapter(this, elem);
		optionList.setAdapter(mOptionAdapter);
		
		optionList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				switch (position)
				{
					case 0:
						_editDateDialog();
						break ;
					case 1:
						_editTimeInDialog();
						break ;
					case 2:
						_editMaxDepth();
						break ;
					case 3:
						_editDuration();
						break ;
					case 5:
						_editWeights();
						break ;
					case 6:
						_editDiveNumberDialog();
						break ;
					case 7:
						_editTripNameDialog();
						break ;
					case 10:
						_editVisibility();
						break ;
					case 11:
						_editCurrent();
						break ;
					case 12:
						_editSurfaceTemp();
						break ;
					case 13:
						_editBottomTemp();
						break ;
					case 14:
						_editAltitude();
						break ;
					case 15:
						_editWater();
						break ;
				}
			}
		});
    }
    
	@Override
	public void onTripNameEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(7)).setValue(dive.getTripName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
	
	@Override
	public void onDiveNumberEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		if (dive.getNumber() != null)
			((EditOption)mOptionAdapter.getItem(6)).setValue(dive.getNumber().toString());
		else
			((EditOption)mOptionAdapter.getItem(6)).setValue("0");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(0)).setValue(dive.getDate());
		String[] time_in = dive.getTimeIn().split("T");
		String new_time_in = dive.getDate() + "T" + time_in[1];
		dive.setTimeIn(new_time_in);
		((EditOption)mOptionAdapter.getItem(1)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		String[] time_in = dive.getTimeIn().split("T");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onMaxDepthEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDurationEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(3)).setValue(Integer.toString(dive.getDuration()) + " min");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onSurfaceTempEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(dive.getTempSurface().getTemperature()) + " °" + dive.getTempSurface().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onBottomTempEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(dive.getTempBottom().getTemperature()) + " °" + dive.getTempBottom().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWeightsEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(dive.getWeights().getWeight()) + " " + dive.getWeights().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onVisibilityEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(10)).setValue(dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onCurrentEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(11)).setValue(dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onAltitudeEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(14)).setValue(Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWaterEditComplete(DialogFragment dialog)
	{
		Dive dive = mModel.getDives().get(mIndex);
		((EditOption)mOptionAdapter.getItem(15)).setValue(dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
}
