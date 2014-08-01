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
    
    private void 				_editReviewDialog()
    {
    	NewReviewDialogFragment dialog = new NewReviewDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewReviewDialogFragment");
    	
    }
    
    private void 				_editTanksDialog()
    {
    	NewTanksDialogFragment dialog = new NewTanksDialogFragment();
    	dialog.show(getActivity().getSupportFragmentManager(), "NewTanksDialogFragment");
    	
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_details, container, false);
    	
	    Typeface faceB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
	    
    	NewDiveActivity.optionList = (ListView)rootView.findViewById(R.id.optionList);
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption(getResources().getString(R.string.date_label) + " : ", mDive.getDate()));
		String[] time_in = mDive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		elem.add(new EditOption(getResources().getString(R.string.time_in_label) + " : ", time[0] + ":" + time[1]));
		String maxdepth_unit = "";
		if (mDive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		else
			maxdepth_unit = (mDive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		if (mDive.getMaxdepth() != null)
			elem.add(new EditOption(getResources().getString(R.string.max_depth_label) + " : ", Double.toString(mDive.getMaxdepth()) + " " + maxdepth_unit));
		else
			elem.add(new EditOption(getResources().getString(R.string.max_depth_label) + " : ", ""));
		if (mDive.getDuration() != null)
			elem.add(new EditOption(getResources().getString(R.string.duration_label) + " : ", Integer.toString(mDive.getDuration()) + " " + getResources().getString(R.string.unit_min)));
		else
			elem.add(new EditOption(getResources().getString(R.string.duration_label) + " : ", ""));
		ArrayList<SafetyStop> safetystop = mDive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit() + "-" + safetystop.get(i).getDuration().toString() + getResources().getString(R.string.unit_min);
		}
		elem.add(new EditOption(getResources().getString(R.string.safety_stops_label) + " : ", safetydetails));
		if (mDive.getWeights() != null)
		{
			String weights_unit = "";
			if (mDive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			else
				weights_unit = (mDive.getWeightsUnit().compareTo(getResources().getString(R.string.unit_kg)) == 0) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			elem.add(new EditOption(getResources().getString(R.string.weights_label) + " : ", Double.toString(mDive.getWeights()) + " " + weights_unit));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.weights_label) + " : ", ""));
		if (mDive.getNumber() != null)
			elem.add(new EditOption(getResources().getString(R.string.dive_number_label) + " : ", Integer.toString(mDive.getNumber())));
		else
			elem.add(new EditOption(getResources().getString(R.string.dive_number_label) + " : ", ""));
		if (mDive.getGuide() != null)
			elem.add(new EditOption(getResources().getString(R.string.guide_name_label) + " : ", mDive.getGuide()));
		else
			elem.add(new EditOption(getResources().getString(R.string.guide_name_label) + " : ", ""));
		elem.add(new EditOption(getResources().getString(R.string.trip_name_label) + " : ", mDive.getTripName()));
		ArrayList<String> divetype = mDive.getDivetype();
		String divetype_string = "";
		for (int i = 0, length = divetype.size(); i < length; i++)
		{
			if (i != 0)
				divetype_string += ", ";
			divetype_string += divetype.get(i);
		}
		elem.add(new EditOption(getResources().getString(R.string.diving_type_label) + " : ", divetype_string));
		if (mDive.getVisibility() != null)
			elem.add(new EditOption(getResources().getString(R.string.visibility_label) + " : ", mDive.getVisibility().substring(0, 1).toUpperCase() + mDive.getVisibility().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.visibility_label) + " : ", ""));
		if (mDive.getCurrent() != null)
			elem.add(new EditOption(getResources().getString(R.string.current_label) + " : ", mDive.getCurrent().substring(0, 1).toUpperCase() + mDive.getCurrent().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.current_label) + " : ", ""));
		if (mDive.getTempSurface() != null)
		{
			String tempsurface_unit = "";
			if (mDive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempsurface_unit = (mDive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempsurface_unit.equals(getResources().getString(R.string.unit_C)))
				elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", Double.toString(mDive.getTempSurface()) + " " + getResources().getString(R.string.unit_C_symbol)));
			else
				elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", Double.toString(mDive.getTempSurface()) + " " + getResources().getString(R.string.unit_F_symbol)));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", ""));
		if (mDive.getTempBottom() != null)
		{
			String tempbotton_unit = "";
			if (mDive.getTempBottomUnit() == null)
				tempbotton_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempbotton_unit = (mDive.getTempBottomUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempbotton_unit.equals(R.string.unit_C))
				elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", Double.toString(mDive.getTempBottom()) + " " + getResources().getString(R.string.unit_C_symbol)));
			else
				elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", Double.toString(mDive.getTempBottom()) + " " + getResources().getString(R.string.unit_F_symbol)));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", ""));
		if (mDive.getAltitude() != null)
			elem.add(new EditOption(getResources().getString(R.string.altitude_label) + " : ", Double.toString(mDive.getAltitude().getDistance()) + " " + mDive.getAltitude().getSmallName()));
		else
			elem.add(new EditOption(getResources().getString(R.string.altitude_label) + " : ", ""));
		if (mDive.getWater() != null)
			elem.add(new EditOption(getResources().getString(R.string.water_type_label) + " : ", mDive.getWater().substring(0, 1).toUpperCase() + mDive.getWater().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.water_type_label) + " : ", ""));
		if (mDive.getPrivacy() == 0)
			elem.add(new EditOption(getResources().getString(R.string.dive_privacy_label) + " : ", getResources().getString(R.string.dive_public_label), 1));
		else
			elem.add(new EditOption(getResources().getString(R.string.dive_privacy_label) + " : ", getResources().getString(R.string.dive_private_label), 1));
		
		if (mDive.getTanks() != null && mDive.getTanks().size() > 0)
			elem.add(new EditOption(getResources().getString(R.string.tanks_label) + " : ", mDive.getTanks().size() + " tanks used"));
		else
			elem.add(new EditOption(getResources().getString(R.string.tanks_label) + " : ", ""));
		
		if (mDive.getDiveReviews()== null)
			elem.add(new EditOption(getResources().getString(R.string.review_label) + " : ", ""));
		else
		{
			elem.add(new EditOption(getResources().getString(R.string.review_label) + " : ",mDive.getDiveReviews().getJson().toString()));	
		}
		
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
						_editDuration();
						break ;
					case 4:
						_editSafetyStops();
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
					case 17:
						_editTanksDialog();
						break ;
					case 18:
						_editReviewDialog();
						break;
				}
			}
		});
		return rootView;
    }
}
