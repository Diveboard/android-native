package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveboardLoginActivity;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditAltitudeDialogFragment;
import com.diveboard.mobile.editdive.EditAltitudeDialogFragment.EditAltitudeDialogListener;
import com.diveboard.mobile.editdive.EditBottomTempDialogFragment;
import com.diveboard.mobile.editdive.EditBottomTempDialogFragment.EditBottomTempDialogListener;
import com.diveboard.mobile.editdive.EditCurrentDialogFragment;
import com.diveboard.mobile.editdive.EditCurrentDialogFragment.EditCurrentDialogListener;
import com.diveboard.mobile.newdive.NewDateDialogFragment;
import com.diveboard.mobile.newdive.NewDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.editdive.EditDiveNumberDialogFragment;
import com.diveboard.mobile.editdive.EditDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.newdive.NewDurationDialogFragment;
import com.diveboard.mobile.newdive.NewDurationDialogFragment.EditDurationDialogListener;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment.EditMaxDepthDialogListener;
import com.diveboard.mobile.editdive.EditOption;
import com.diveboard.mobile.editdive.EditSurfaceTempDialogFragment;
import com.diveboard.mobile.editdive.EditSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment;
import com.diveboard.mobile.editdive.EditTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.editdive.EditVisibilityDialogFragment;
import com.diveboard.mobile.editdive.EditVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.editdive.EditWaterDialogFragment;
import com.diveboard.mobile.editdive.EditWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment.EditWeightsDialogListener;
import com.diveboard.mobile.editdive.OptionAdapter;

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

public class					TabNewDetailsActivity extends FragmentActivity implements EditDateDialogListener,
																						  EditTimeInDialogListener,
																						  EditMaxDepthDialogListener,
																						  EditDurationDialogListener,
																						  EditWeightsDialogListener
{
	private ListView			optionList;
	private Dive				mDive;
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
		mDive = null;
		((ApplicationController)getApplicationContext()).setTempDive(null);
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
		mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		
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
    	NewDateDialogFragment dialog = new NewDateDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewDateDialogFragment");
    }
    
    private void				_editTimeInDialog()
    {
    	NewTimeInDialogFragment dialog = new NewTimeInDialogFragment();
    	dialog.show(getSupportFragmentManager(), "EditTimeInDialogFragment");
    }
    
    private void				_editMaxDepth()
    {
    	NewMaxDepthDialogFragment dialog = new NewMaxDepthDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "EditMaxDepthDialogFragment");
    }
    
    private void				_editDuration()
    {
    	NewDurationDialogFragment dialog = new NewDurationDialogFragment();
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
    	NewWeightsDialogFragment dialog = new NewWeightsDialogFragment();
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
	    save.setText(getResources().getString(R.string.add_button));
	    save.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
//				Dive dive = mModel.getDives().get(mIndex);
//				mModel.getDataManager().save(dive);
				finish();
			}
		});
	    
    	optionList = (ListView)findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Date : ", mDive.getDate()));
		String[] time_in = mDive.getTimeIn().split("T");
		elem.add(new EditOption("Time in : ", time_in[1]));
		elem.add(new EditOption("Max depth : ", Double.toString(mDive.getMaxdepth().getDistance()) + " " + mDive.getMaxdepth().getSmallName()));
		elem.add(new EditOption("Duration : ", Integer.toString(mDive.getDuration()) + " min"));
		
		elem.add(new EditOption("Safety stops : ", "not implemented"));
		if (mDive.getWeights() != null)
			elem.add(new EditOption("Weights : ", Double.toString(mDive.getWeights().getWeight()) + " " + mDive.getWeights().getSmallName()));
		else
			elem.add(new EditOption("Weights : ", "Not defined"));
//		if (mDive.getNumber() != null)
//			elem.add(new EditOption("Dive number : ", Integer.toString(mDive.getNumber())));
//		else
//			elem.add(new EditOption("Dive number : ", Integer.toString(0)));
//		elem.add(new EditOption("Trip name : ", mDive.getTripName()));
//		elem.add(new EditOption("Other divers : ", "not implemented"));
//		elem.add(new EditOption("Diving type & activities : ", "not implemented"));
//		if (mDive.getVisibility() != null)
//			elem.add(new EditOption("Visibility : ", mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1)));
//		else
//			elem.add(new EditOption("Visibility : ", "Not defined"));
//		if (mDive.getCurrent() != null)
//			elem.add(new EditOption("Current : ", mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1)));
//		else
//			elem.add(new EditOption("Current : ", "Not defined"));
//		if (mDive.getTempSurface() != null)
//			elem.add(new EditOption("Surface temperature : ", Double.toString(mDive.getTempSurface().getTemperature()) + " °" + mDive.getTempSurface().getSmallName()));
//		else
//			elem.add(new EditOption("Surface temperature : ", "Not defined"));
//		if (mDive.getTempBottom() != null)
//			elem.add(new EditOption("Bottom temperature : ", Double.toString(mDive.getTempBottom().getTemperature()) + " °" + mDive.getTempBottom().getSmallName()));
//		else
//			elem.add(new EditOption("Bottom temperature : ", "Not defined"));
//		if (mDive.getAltitude() != null)
//			elem.add(new EditOption("Altitude : ", Double.toString(mDive.getAltitude().getDistance()) + " " + mDive.getAltitude().getSmallName()));
//		else
//			elem.add(new EditOption("Altitude : ", "Not defined"));
//		if (mDive.getWater() != null)
//			elem.add(new EditOption("Water type : ", mDive.getWater().substring(0, 1).toUpperCase() + mDive.getWater().substring(1)));
//		else
//			elem.add(new EditOption("Water type : ", "Not defined"));
		
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
    
//	@Override
//	public void onTripNameEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(7)).setValue(dive.getTripName());
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//	
//	@Override
//	public void onDiveNumberEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		if (dive.getNumber() != null)
//			((EditOption)mOptionAdapter.getItem(6)).setValue(dive.getNumber().toString());
//		else
//			((EditOption)mOptionAdapter.getItem(6)).setValue("0");
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(0)).setValue(mDive.getDate());
		String[] time_in = mDive.getTimeIn().split("T");
		String new_time_in = mDive.getDate() + "T" + time_in[1];
		mDive.setTimeIn(new_time_in);
		((EditOption)mOptionAdapter.getItem(1)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		String[] time_in = mDive.getTimeIn().split("T");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time_in[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onMaxDepthEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(2)).setValue(Double.toString(mDive.getMaxdepth().getDistance()) + " " + mDive.getMaxdepth().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDurationEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(3)).setValue(Integer.toString(mDive.getDuration()) + " min");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
//
//	@Override
//	public void onSurfaceTempEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(12)).setValue(Double.toString(dive.getTempSurface().getTemperature()) + " °" + dive.getTempSurface().getSmallName());
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//
//	@Override
//	public void onBottomTempEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(13)).setValue(Double.toString(dive.getTempBottom().getTemperature()) + " °" + dive.getTempBottom().getSmallName());
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//
	@Override
	public void onWeightsEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(5)).setValue(Double.toString(mDive.getWeights().getWeight()) + " " + mDive.getWeights().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
//
//	@Override
//	public void onVisibilityEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(10)).setValue(dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1));
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//
//	@Override
//	public void onCurrentEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(11)).setValue(dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1));
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//
//	@Override
//	public void onAltitudeEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(14)).setValue(Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName());
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
//
//	@Override
//	public void onWaterEditComplete(DialogFragment dialog)
//	{
//		Dive dive = mModel.getDives().get(mIndex);
//		((EditOption)mOptionAdapter.getItem(15)).setValue(dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1));
//		mOptionAdapter.notifyDataSetChanged();
//		//mModel.getDataManager().save(dive);
//	}
}
