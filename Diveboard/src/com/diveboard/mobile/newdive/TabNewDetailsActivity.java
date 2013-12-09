package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.DiveboardLoginActivity;
import com.diveboard.mobile.R;
import com.diveboard.mobile.newdive.NewAltitudeDialogFragment;
import com.diveboard.mobile.newdive.NewAltitudeDialogFragment.EditAltitudeDialogListener;
import com.diveboard.mobile.newdive.NewBottomTempDialogFragment;
import com.diveboard.mobile.newdive.NewBottomTempDialogFragment.EditBottomTempDialogListener;
import com.diveboard.mobile.newdive.NewCurrentDialogFragment;
import com.diveboard.mobile.newdive.NewCurrentDialogFragment.EditCurrentDialogListener;
import com.diveboard.mobile.newdive.NewDateDialogFragment;
import com.diveboard.mobile.newdive.NewDateDialogFragment.EditDateDialogListener;
import com.diveboard.mobile.newdive.NewDiveNumberDialogFragment;
import com.diveboard.mobile.newdive.NewDiveNumberDialogFragment.EditDiveNumberDialogListener;
import com.diveboard.mobile.newdive.NewDurationDialogFragment;
import com.diveboard.mobile.newdive.NewDurationDialogFragment.EditDurationDialogListener;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment.EditMaxDepthDialogListener;
import com.diveboard.mobile.editdive.EditOption;
import com.diveboard.mobile.newdive.NewSurfaceTempDialogFragment;
import com.diveboard.mobile.newdive.NewSurfaceTempDialogFragment.EditSurfaceTempDialogListener;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment.EditTimeInDialogListener;
import com.diveboard.mobile.newdive.NewTripNameDialogFragment;
import com.diveboard.mobile.newdive.NewTripNameDialogFragment.EditTripNameDialogListener;
import com.diveboard.mobile.newdive.NewVisibilityDialogFragment;
import com.diveboard.mobile.newdive.NewVisibilityDialogFragment.EditVisibilityDialogListener;
import com.diveboard.mobile.newdive.NewWaterDialogFragment;
import com.diveboard.mobile.newdive.NewWaterDialogFragment.EditWaterDialogListener;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment.EditWeightsDialogListener;
import com.diveboard.mobile.editdive.OptionAdapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveCreateListener;
import com.diveboard.model.DiveboardModel;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class					TabNewDetailsActivity extends FragmentActivity implements EditDateDialogListener,
																						  EditTimeInDialogListener,
																						  EditMaxDepthDialogListener,
																						  EditDurationDialogListener,
																						  EditWeightsDialogListener,
																						  EditDiveNumberDialogListener,
																						  EditTripNameDialogListener,
																						  EditVisibilityDialogListener,
																						  EditCurrentDialogListener,
																						  EditSurfaceTempDialogListener,
																						  EditBottomTempDialogListener,
																						  EditAltitudeDialogListener,
																						  EditWaterDialogListener
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
    	NewTripNameDialogFragment dialog = new NewTripNameDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewTripNameDialogFragment");
    }
    
    private void				_editDiveNumberDialog()
    {
    	NewDiveNumberDialogFragment dialog = new NewDiveNumberDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewDiveNumberDialogFragment");
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
    	NewSurfaceTempDialogFragment dialog = new NewSurfaceTempDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewSurfaceTempDialogFragment");
    }
    
    private void				_editBottomTemp()
    {
    	NewBottomTempDialogFragment dialog = new NewBottomTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getSupportFragmentManager(), "NewBottomTempDialogFragment");
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
    	NewVisibilityDialogFragment dialog = new NewVisibilityDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewVisibilityDialogFragment");
    }
    
    private void				_editCurrent()
    {
    	NewCurrentDialogFragment dialog = new NewCurrentDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewCurrentDialogFragment");
    }
    
    private void				_editAltitude()
    {
    	NewAltitudeDialogFragment dialog = new NewAltitudeDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewAltitudeDialogFragment");
    }
    
    private void				_editWater()
    {
    	NewWaterDialogFragment dialog = new NewWaterDialogFragment();
    	dialog.show(getSupportFragmentManager(), "NewWaterDialogFragment");
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
				ArrayList<Dive> dives = ((ApplicationController)getApplicationContext()).getModel().getDives();
				ArrayList<Pair<String, String>> editList = mDive.getEditList();
				if (editList != null && editList.size() > 0)
				{
					JSONObject edit = new JSONObject(); 
					for (int i = 0, size = editList.size(); i < size; i++)
						try {
							edit.put(editList.get(i).first, editList.get(i).second);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					try {
						mDive.applyEdit(edit);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					mDive.clearEditList();
				}
				dives.add(0, mDive);
				((ApplicationController)getApplicationContext()).getModel().getDataManager().setOnDiveCreateComplete(new DiveCreateListener() {
					@Override
					public void onDiveCreateComplete() {
						finish();
					}
				});
				((ApplicationController)getApplicationContext()).getModel().getDataManager().save(mDive);
				((ApplicationController)getApplicationContext()).setRefresh(1);
//				Toast toast = Toast.makeText(getApplicationContext(), "The new dive will be displayed after refreshing the page!", Toast.LENGTH_LONG);
//				toast.setGravity(Gravity.CENTER, 0, 0);
//				toast.show();
				((ApplicationController)getApplicationContext()).setTempDive(null);
			}
		});
	    
    	optionList = (ListView)findViewById(R.id.optionList);
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Date : ", mDive.getDate()));
		String[] time_in = mDive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		elem.add(new EditOption("Time in : ", time[0] + ":" + time[1]));
		elem.add(new EditOption("Max depth : ", Double.toString(mDive.getMaxdepth().getDistance()) + " " + mDive.getMaxdepth().getSmallName()));
		elem.add(new EditOption("Duration : ", Integer.toString(mDive.getDuration()) + " min"));
		
		//elem.add(new EditOption("Safety stops : ", "not implemented"));
		if (mDive.getWeights() != null)
			elem.add(new EditOption("Weights : ", Double.toString(mDive.getWeights().getWeight()) + " " + mDive.getWeights().getSmallName()));
		else
			elem.add(new EditOption("Weights : ", ""));
		if (mDive.getNumber() != null)
			elem.add(new EditOption("Dive number : ", Integer.toString(mDive.getNumber())));
		else
			elem.add(new EditOption("Dive number : ", ""));
		elem.add(new EditOption("Trip name : ", mDive.getTripName()));
		//elem.add(new EditOption("Other divers : ", "not implemented"));
		//elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (mDive.getVisibility() != null)
			elem.add(new EditOption("Visibility : ", mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1)));
		else
			elem.add(new EditOption("Visibility : ", ""));
		if (mDive.getCurrent() != null)
			elem.add(new EditOption("Current : ", mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1)));
		else
			elem.add(new EditOption("Current : ", ""));
		if (mDive.getTempSurface() != null)
			elem.add(new EditOption("Surface temperature : ", Double.toString(mDive.getTempSurface().getTemperature()) + " °" + mDive.getTempSurface().getSmallName()));
		else
			elem.add(new EditOption("Surface temperature : ", ""));
		if (mDive.getTempBottom() != null)
			elem.add(new EditOption("Bottom temperature : ", Double.toString(mDive.getTempBottom().getTemperature()) + " °" + mDive.getTempBottom().getSmallName()));
		else
			elem.add(new EditOption("Bottom temperature : ", ""));
		if (mDive.getAltitude() != null)
			elem.add(new EditOption("Altitude : ", Double.toString(mDive.getAltitude().getDistance()) + " " + mDive.getAltitude().getSmallName()));
		else
			elem.add(new EditOption("Altitude : ", ""));
		if (mDive.getWater() != null)
			elem.add(new EditOption("Water type : ", mDive.getWater().substring(0, 1).toUpperCase() + mDive.getWater().substring(1)));
		else
			elem.add(new EditOption("Water type : ", ""));
		if (mDive.getPrivacy() == 0)
			elem.add(new EditOption("Dive privacy : ", "Public", 1));
		else
			elem.add(new EditOption("Dive privacy : ", "Private", 1));
		
		mOptionAdapter = new OptionAdapter(this, elem, mDive);
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
					case 4:
						_editWeights();
						break ;
					case 5:
						_editDiveNumberDialog();
						break ;
					case 6:
						_editTripNameDialog();
						break ;
					case 7:
						_editVisibility();
						break ;
					case 8:
						_editCurrent();
						break ;
					case 9:
						_editSurfaceTemp();
						break ;
					case 10:
						_editBottomTemp();
						break ;
					case 11:
						_editAltitude();
						break ;
					case 12:
						_editWater();
						break ;
				}
			}
		});
    }
    
	@Override
	public void onTripNameEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(6)).setValue(mDive.getTripName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
	
	@Override
	public void onDiveNumberEditComplete(DialogFragment dialog)
	{
		if (mDive.getNumber() != null)
			((EditOption)mOptionAdapter.getItem(5)).setValue(mDive.getNumber().toString());
		else
			((EditOption)mOptionAdapter.getItem(5)).setValue("");
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onDateEditComplete(DialogFragment dialog)
	{
		((EditOption)mOptionAdapter.getItem(0)).setValue(mDive.getDate());
		String[] time_in = mDive.getTimeIn().split("T");
		String new_time_in = mDive.getDate() + "T" + time_in[1];
		mDive.setTimeIn(new_time_in);
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onTimeInEditComplete(DialogFragment dialog)
	{
		String[] time_in = mDive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		((EditOption)mOptionAdapter.getItem(1)).setValue(time[0] + ":" + time[1]);
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

	@Override
	public void onSurfaceTempEditComplete(DialogFragment dialog)
	{
		if (mDive.getTempSurface() == null)
			((EditOption)mOptionAdapter.getItem(9)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(9)).setValue(Double.toString(mDive.getTempSurface().getTemperature()) + " °" + mDive.getTempSurface().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onBottomTempEditComplete(DialogFragment dialog)
	{
		if (mDive.getTempBottom() == null)
			((EditOption)mOptionAdapter.getItem(10)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(10)).setValue(Double.toString(mDive.getTempBottom().getTemperature()) + " °" + mDive.getTempBottom().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWeightsEditComplete(DialogFragment dialog)
	{
		if (mDive.getWeights() == null)
			((EditOption)mOptionAdapter.getItem(4)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(4)).setValue(Double.toString(mDive.getWeights().getWeight()) + " " + mDive.getWeights().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onVisibilityEditComplete(DialogFragment dialog)
	{
		if (mDive.getVisibility() == null)
			((EditOption)mOptionAdapter.getItem(7)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(7)).setValue(mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onCurrentEditComplete(DialogFragment dialog)
	{
		if (mDive.getCurrent() == null)
			((EditOption)mOptionAdapter.getItem(8)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(8)).setValue(mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onAltitudeEditComplete(DialogFragment dialog)
	{
		if (mDive.getAltitude() == null)
			((EditOption)mOptionAdapter.getItem(11)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(11)).setValue(Double.toString(mDive.getAltitude().getDistance()) + " " + mDive.getAltitude().getSmallName());
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}

	@Override
	public void onWaterEditComplete(DialogFragment dialog)
	{
		if (mDive.getWater() == null)
			((EditOption)mOptionAdapter.getItem(12)).setValue("");
		else
			((EditOption)mOptionAdapter.getItem(12)).setValue(mDive.getWater().substring(0, 1).toUpperCase() + mDive.getWater().substring(1));
		mOptionAdapter.notifyDataSetChanged();
		//mModel.getDataManager().save(dive);
	}
}
