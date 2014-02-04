package com.diveboard.mobile.newdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.newdive.NewAltitudeDialogFragment;
import com.diveboard.mobile.newdive.NewBottomTempDialogFragment;
import com.diveboard.mobile.newdive.NewCurrentDialogFragment;
import com.diveboard.mobile.newdive.NewDateDialogFragment;
import com.diveboard.mobile.newdive.NewDiveNumberDialogFragment;
import com.diveboard.mobile.newdive.NewDurationDialogFragment;
import com.diveboard.mobile.newdive.NewMaxDepthDialogFragment;
import com.diveboard.mobile.editdive.EditGuideNameDialogFragment;
import com.diveboard.mobile.editdive.EditOption;
import com.diveboard.mobile.newdive.NewSurfaceTempDialogFragment;
import com.diveboard.mobile.newdive.NewTimeInDialogFragment;
import com.diveboard.mobile.newdive.NewTripNameDialogFragment;
import com.diveboard.mobile.newdive.NewVisibilityDialogFragment;
import com.diveboard.mobile.newdive.NewWaterDialogFragment;
import com.diveboard.mobile.newdive.NewWeightsDialogFragment;
import com.diveboard.mobile.editdive.OptionAdapter;

import java.util.ArrayList;

import com.diveboard.model.Dive;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class					TabNewDetailsFragment extends Fragment
{
	private Dive				mDive;
	private int					mIndex;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		
		mDive = ((ApplicationController)getActivity().getApplicationContext()).getTempDive();
    }

    private void				_editTripNameDialog()
    {
    	NewTripNameDialogFragment dialog = new NewTripNameDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewTripNameDialogFragment");
    }
    
    private void				_editDiveNumberDialog()
    {
    	NewDiveNumberDialogFragment dialog = new NewDiveNumberDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewDiveNumberDialogFragment");
    }
    
    private void				_editDateDialog()
    {
    	NewDateDialogFragment dialog = new NewDateDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewDateDialogFragment");
    }
    
    private void				_editTimeInDialog()
    {
    	NewTimeInDialogFragment dialog = new NewTimeInDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "EditTimeInDialogFragment");
    }
    
    private void				_editMaxDepth()
    {
    	NewMaxDepthDialogFragment dialog = new NewMaxDepthDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditMaxDepthDialogFragment");
    }
    
    private void				_editDuration()
    {
    	NewDurationDialogFragment dialog = new NewDurationDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditDurationDialogFragment");
    }
    
    private void				_editSurfaceTemp()
    {
    	NewSurfaceTempDialogFragment dialog = new NewSurfaceTempDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewSurfaceTempDialogFragment");
    }
    
    private void				_editBottomTemp()
    {
    	NewBottomTempDialogFragment dialog = new NewBottomTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "NewBottomTempDialogFragment");
    }
    
    private void				_editWeights()
    {
    	NewWeightsDialogFragment dialog = new NewWeightsDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditWeightsDialogFragment");
    }
    
    private void				_editVisibility()
    {
    	NewVisibilityDialogFragment dialog = new NewVisibilityDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewVisibilityDialogFragment");
    }
    
    private void				_editCurrent()
    {
    	NewCurrentDialogFragment dialog = new NewCurrentDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewCurrentDialogFragment");
    }
    
    private void				_editAltitude()
    {
    	NewAltitudeDialogFragment dialog = new NewAltitudeDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewAltitudeDialogFragment");
    }
    
    private void				_editWater()
    {
    	NewWaterDialogFragment dialog = new NewWaterDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewWaterDialogFragment");
    }
    
    private void				_editSafetyStops()
    {
    	NewSafetyStopsDialogFragment dialog = new NewSafetyStopsDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewSafetyStopsDialogFragment");
    }
    
    private void				_editDiveType()
    {
    	NewDiveTypeDialogFragment dialog = new NewDiveTypeDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewDiveTypeDialogFragment");
    }
    
    private void				_editGuideNameDialog()
    {
    	NewGuideNameDialogFragment dialog = new NewGuideNameDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewGuideNameDialogFragment");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_details, container, false);
    	
	    Typeface faceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Bold.otf");
	    
//	    Button save = (Button) findViewById(R.id.save_button);
//	    save.setTypeface(faceB);
//	    save.setText(getResources().getString(R.string.add_button));
//	    save.setOnClickListener(new OnClickListener()
//        {
//			@Override
//			public void onClick(View v)
//			{
//				ArrayList<Dive> dives = ((ApplicationController)getApplicationContext()).getModel().getDives();
//				ArrayList<Pair<String, String>> editList = mDive.getEditList();
//				if (mDive.getMaxdepth() == null || mDive.getDuration() == null)
//					mError = true;
//				else
//					mError = false;
//				if (mDive.getMaxdepth() == null)
//				{
//					if (2 - optionList.getFirstVisiblePosition() >= 0)
//					{
//						View view = optionList.getChildAt(2 - optionList.getFirstVisiblePosition());
//						((TextView)view.findViewById(R.id.optTitle)).setError("This field must be filled");
//						((TextView)view.findViewById(R.id.optTitle)).requestFocus();
//					}
//					
//				}
//				if (mDive.getDuration() == null)
//				{
//					if (4 - optionList.getFirstVisiblePosition() >= 0)
//					{
//						View view = optionList.getChildAt(4 - optionList.getFirstVisiblePosition());
//						((TextView)view.findViewById(R.id.optTitle)).setError("This field must be filled");
//						((TextView)view.findViewById(R.id.optTitle)).requestFocus();
//					}
//					
//				}
//				if (mError == false)
//				{
//					WaitDialogFragment dialog = new WaitDialogFragment();
//					dialog.show(getSupportFragmentManager(), "WaitDialogFragment");
//					if (editList != null && editList.size() > 0)
//					{
//						JSONObject edit = new JSONObject(); 
//						for (int i = 0, size = editList.size(); i < size; i++)
//							try {
//								if (editList.get(i).first.equals("spot"))
//									edit.put(editList.get(i).first, new JSONObject(editList.get(i).second));
//								else
//									edit.put(editList.get(i).first, editList.get(i).second);
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//						try {
//							mDive.applyEdit(edit);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						mDive.clearEditList();
//					}
//					dives.add(0, mDive);
//					((ApplicationController)getApplicationContext()).getModel().getDataManager().setOnDiveCreateComplete(new DiveCreateListener() {
//						@Override
//						public void onDiveCreateComplete() {
//							finish();
//						}
//					});
//					((ApplicationController)getApplicationContext()).getModel().getDataManager().save(mDive);
//					((ApplicationController)getApplicationContext()).setRefresh(1);
//					((ApplicationController)getApplicationContext()).setTempDive(null);
//				}
//				else
//				{
//					Toast toast = Toast.makeText(getApplicationContext(), "Max Depth or Duration fields are missing", Toast.LENGTH_LONG);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					toast.show();
//				}
//			}
//		});
	    
    	NewDiveActivity.optionList = (ListView)rootView.findViewById(R.id.optionList);
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Date : ", mDive.getDate()));
		String[] time_in = mDive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		elem.add(new EditOption("Time in : ", time[0] + ":" + time[1]));
		String maxdepth_unit = "";
		if (mDive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? "m" : "ft";
		else
			maxdepth_unit = (mDive.getMaxdepthUnit().compareTo("m") == 0) ? "m" : "ft";
		if (mDive.getMaxdepth() != null)
			elem.add(new EditOption("Max depth : ", Double.toString(mDive.getMaxdepth()) + " " + maxdepth_unit));
		else
			elem.add(new EditOption("Max depth : ", ""));
		ArrayList<SafetyStop> safetystop = mDive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDuration().toString() + "min" + "-" + safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit();
		}
		elem.add(new EditOption("Safety Stops : ", safetydetails));
		if (mDive.getDuration() != null)
			elem.add(new EditOption("Duration : ", Integer.toString(mDive.getDuration()) + " min"));
		else
			elem.add(new EditOption("Duration : ", ""));
		if (mDive.getWeights() != null)
		{
			String weights_unit = "";
			if (mDive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? "kg" : "lbs";
			else
				weights_unit = (mDive.getWeightsUnit().compareTo("kg") == 0) ? "kg" : "lbs";
			elem.add(new EditOption("Weights : ", Double.toString(mDive.getWeights()) + " " + weights_unit));
		}
		else
			elem.add(new EditOption("Weights : ", ""));
		if (mDive.getNumber() != null)
			elem.add(new EditOption("Dive number : ", Integer.toString(mDive.getNumber())));
		else
			elem.add(new EditOption("Dive number : ", ""));
		if (mDive.getGuide() != null)
			elem.add(new EditOption("Guide name : ", mDive.getGuide()));
		else
			elem.add(new EditOption("Guide name : ", ""));
		elem.add(new EditOption("Trip name : ", mDive.getTripName()));
		ArrayList<String> divetype = mDive.getDivetype();
		String divetype_string = "";
		for (int i = 0, length = divetype.size(); i < length; i++)
		{
			if (i != 0)
				divetype_string += ", ";
			divetype_string += divetype.get(i);
		}
		elem.add(new EditOption("Diving type : ", divetype_string));
		if (mDive.getVisibility() != null)
			elem.add(new EditOption("Visibility : ", mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1)));
		else
			elem.add(new EditOption("Visibility : ", ""));
		if (mDive.getCurrent() != null)
			elem.add(new EditOption("Current : ", mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1)));
		else
			elem.add(new EditOption("Current : ", ""));
		if (mDive.getTempSurface() != null)
		{
			String tempsurface_unit = "";
			if (mDive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempsurface_unit = (mDive.getTempSurfaceUnit().compareTo("C") == 0) ? "C" : "F";
			elem.add(new EditOption("Surface temperature : ", Double.toString(mDive.getTempSurface()) + " °" + tempsurface_unit));
		}
		else
			elem.add(new EditOption("Surface temperature : ", ""));
		if (mDive.getTempBottom() != null)
		{
			String tempbotton_unit = "";
			if (mDive.getTempBottomUnit() == null)
				tempbotton_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempbotton_unit = (mDive.getTempBottomUnit().compareTo("C") == 0) ? "C" : "F";
			elem.add(new EditOption("Bottom temperature : ", Double.toString(mDive.getTempBottom()) + " °" + tempbotton_unit));
		}
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
		
		NewDiveActivity.mOptionAdapter = new OptionAdapter(getActivity().getApplicationContext(), elem, mDive);
		NewDiveActivity.optionList.setAdapter(NewDiveActivity.mOptionAdapter);
		
		NewDiveActivity.optionList.setOnItemClickListener(new OnItemClickListener() {
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
						_editSafetyStops();
						break ;
					case 4:
						_editDuration();
						break ;
					case 5:
						_editWeights();
						break ;
					case 6:
						_editDiveNumberDialog();
						break ;
					case 7:
						_editGuideNameDialog();
						break ;	
					case 8:
						_editTripNameDialog();
						break ;
					case 9:
						_editDiveType();
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
		return rootView;
    }
}
