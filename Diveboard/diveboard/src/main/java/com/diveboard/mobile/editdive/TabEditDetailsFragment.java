package com.diveboard.mobile.editdive;

import java.util.ArrayList;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

public class					TabEditDetailsFragment extends Fragment
{
	private ListView			optionList;
	private int					mIndex;
	private DiveboardModel		mModel;
	
	@Override
	public void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		AC.handleLowMemory();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getActivity().getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		mModel = ((ApplicationController)getActivity().getApplicationContext()).getModel();
		mIndex = getActivity().getIntent().getIntExtra("index", -1);
    }

    private void				_editTripNameDialog()
    {
    	EditTripNameDialogFragment dialog = new EditTripNameDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditTripNameDialogFragment");
    }
    
    private void				_editDiveNumberDialog()
    {
    	EditDiveNumberDialogFragment dialog = new EditDiveNumberDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditDiveNumberDialogFragment");
    }
    
    private void				_editDateDialog()
    {
    	EditDateDialogFragment dialog = new EditDateDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditDateDialogFragment");
    }
    
    private void				_editTimeInDialog()
    {
    	EditTimeInDialogFragment dialog = new EditTimeInDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditTimeInDialogFragment");
    }
    
    private void				_editMaxDepth()
    {
    	EditMaxDepthDialogFragment dialog = new EditMaxDepthDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditMaxDepthDialogFragment");
    }
    
    private void				_editDuration()
    {
    	EditDurationDialogFragment dialog = new EditDurationDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditDurationDialogFragment");
    }
    
    private void				_editSurfaceTemp()
    {
    	EditSurfaceTempDialogFragment dialog = new EditSurfaceTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditSurfaceTempDialogFragment");
    }
    
    private void				_editBottomTemp()
    {
    	EditBottomTempDialogFragment dialog = new EditBottomTempDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditBottomTempDialogFragment");
    }
    
    private void				_editWeights()
    {
    	EditWeightsDialogFragment dialog = new EditWeightsDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditWeightsDialogFragment");
    }
    
    private void				_editVisibility()
    {
    	EditVisibilityDialogFragment dialog = new EditVisibilityDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditVisibilityDialogFragment");
    }
    
    private void				_editCurrent()
    {
    	EditCurrentDialogFragment dialog = new EditCurrentDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditCurrentDialogFragment");
    }
    
    private void				_editAltitude()
    {
    	EditAltitudeDialogFragment dialog = new EditAltitudeDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditAltitudeDialogFragment");
    }
    
    private void				_editWater()
    {
    	EditWaterDialogFragment dialog = new EditWaterDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditWaterDialogFragment");
    }
    
    private void				_editSafetyStops()
    {
    	EditSafetyStopsDialogFragment dialog = new EditSafetyStopsDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditSafetyStopsDialogFragment");
    }
    
    private void				_editDiveType()
    {
    	EditDiveTypeDialogFragment dialog = new EditDiveTypeDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditDiveTypeDialogFragment");
    }
    
    private void				_editGuideNameDialog()
    {
    	EditGuideNameDialogFragment dialog = new EditGuideNameDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditGuideNameDialogFragment");
    }
    
    private void				_editTanks()
    {
    	EditTanksDialogFragment dialog = new EditTanksDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditTanksDialogFragment");
    }
    
    private void				_editReviewType()
    {
    	EditReviewDialogFragment dialog = new EditReviewDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditReviewDialogFragment");
    }
    
	private String _getReviewHintGeneral(int rating) {
		String resul = "";
		switch (rating) {
		case 0:
			resul = "";
			break;

		case 1:
			resul = getResources().getString(R.string.hint_terrible);
			break;

		case 2:
			resul = getResources().getString(R.string.hint_poor);
			break;

		case 3:
			resul = getResources().getString(R.string.hint_average);
			break;

		case 4:
			resul = getResources().getString(R.string.hint_very_good);
			break;

		case 5:
			resul = getResources().getString(R.string.hint_excellent);
			break;

		default:
			break;
		}
		return resul;
	}
    
	private String 				_getReviewHintDifficulty(int rating) {

		String resul = "";

		switch (rating) {

		case 0:
			resul = "";
			break;

		case 1:
			resul = getResources().getString(R.string.hint_trivial);
			break;

		case 2:
			resul = getResources().getString(R.string.hint_simple);
			break;

		case 3:
			resul = getResources().getString(R.string.hint_somewhat_simple);
			break;

		case 4:
			resul = getResources().getString(R.string.hint_tricky);
			break;

		case 5:
			resul = getResources().getString(R.string.hint_hardcore);
			break;

		default:
			break;

		}

		return resul;

	}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_details, container, false);
	    
    	Dive dive = mModel.getDives().get(mIndex);
    	
    	optionList = (ListView) rootView.findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption(getResources().getString(R.string.date_label) + " : ", dive.getDate()));
		String[] time_in = dive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		elem.add(new EditOption(getResources().getString(R.string.time_in_label) + " : ", time[0] + ":" + time[1]));
		//elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName()));
		String maxdepth_unit = "";
		if (dive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		else
			maxdepth_unit = (dive.getMaxdepthUnit().compareTo(getResources().getString(R.string.unit_m)) == 0) ? getResources().getString(R.string.unit_m) : getResources().getString(R.string.unit_ft);
		elem.add(new EditOption(getResources().getString(R.string.max_depth_label) + " : ", Double.toString(dive.getMaxdepth()) + " " + maxdepth_unit));
		elem.add(new EditOption(getResources().getString(R.string.duration_label) + " : ", Integer.toString(dive.getDuration()) + " " + getResources().getString(R.string.unit_min)));

		ArrayList<SafetyStop> safetystop = dive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit() + "-" + safetystop.get(i).getDuration().toString() + getResources().getString(R.string.unit_min);
		}
		elem.add(new EditOption(getResources().getString(R.string.safety_stops_label) + " : ", safetydetails));
		
		if (dive.getWeights() != null)
		{
			String weights_unit = "";
			if (dive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs);
			else
				weights_unit = (dive.getWeightsUnit().compareTo(getResources().getString(R.string.unit_kg)) == 0) ? getResources().getString(R.string.unit_kg) : getResources().getString(R.string.unit_lbs); 
			elem.add(new EditOption(getResources().getString(R.string.weights_label) + " : ", Double.toString(dive.getWeights()) + " " + weights_unit));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.weights_label) + " : ", ""));
		if (dive.getNumber() != null)
			elem.add(new EditOption(getResources().getString(R.string.dive_number_label) + " : ", Integer.toString(dive.getNumber())));
		else
			elem.add(new EditOption(getResources().getString(R.string.dive_number_label) + " : ", ""));
		if (dive.getGuide() != null)
			elem.add(new EditOption(getResources().getString(R.string.guide_name_label) + " : ", dive.getGuide()));
		else
			elem.add(new EditOption(getResources().getString(R.string.guide_name_label) + " : ", ""));
		elem.add(new EditOption(getResources().getString(R.string.trip_name_label) + " : ", dive.getTripName()));
		ArrayList<String> divetype = dive.getDivetype();
		String divetype_string = "";
		for (int i = 0, length = divetype.size(); i < length; i++)
		{
			if (i != 0)
				divetype_string += ", ";
			divetype_string += divetype.get(i);
		}
		elem.add(new EditOption(getResources().getString(R.string.diving_type_label) + " : ", divetype_string));
		//elem.add(new EditOption("Other divers : ", "not implemented"));
		//elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (dive.getVisibility() != null)
			elem.add(new EditOption(getResources().getString(R.string.visibility_label) + " : ", dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.visibility_label) + " : ", ""));
		if (dive.getCurrent() != null)
			elem.add(new EditOption(getResources().getString(R.string.current_label) + " : ", dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.current_label) + " : ", ""));
		if (dive.getTempSurface() != null)
		{
			String tempsurface_unit = "";
			if (dive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempsurface_unit = (dive.getTempSurfaceUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempsurface_unit.equals(getResources().getString(R.string.unit_C)))
				elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", Double.toString(dive.getTempSurface()) + " " + getResources().getString(R.string.unit_C_symbol)));
			else
				elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", Double.toString(dive.getTempSurface()) + " " + getResources().getString(R.string.unit_F_symbol)));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.surface_temperature_label) + " : ", ""));
		if (dive.getTempBottom() != null)
		{
			String tempbottom_unit = "";
			if (dive.getTempBottomUnit() == null)
				tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			else
				tempbottom_unit = (dive.getTempBottomUnit().compareTo(getResources().getString(R.string.unit_C)) == 0) ? getResources().getString(R.string.unit_C) : getResources().getString(R.string.unit_F);
			if (tempbottom_unit.equals(getResources().getString(R.string.unit_C)))
				elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", Double.toString(dive.getTempBottom()) + " " + getResources().getString(R.string.unit_C_symbol)));
			else
				elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", Double.toString(dive.getTempBottom()) + " " + getResources().getString(R.string.unit_F_symbol)));
		}
		else
			elem.add(new EditOption(getResources().getString(R.string.bottom_temperature_label) + " : ", ""));
		if (dive.getAltitude() != null)
			elem.add(new EditOption(getResources().getString(R.string.altitude_label) + " : ", Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName()));
		else
			elem.add(new EditOption(getResources().getString(R.string.altitude_label) + " : ", ""));
		if (dive.getWater() != null)
			elem.add(new EditOption(getResources().getString(R.string.water_type_label) + " : ", dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1)));
		else
			elem.add(new EditOption(getResources().getString(R.string.water_type_label) + " : ", ""));
		if (dive.getPrivacy() == 0)
			elem.add(new EditOption(getResources().getString(R.string.dive_privacy_label) + " : ", getResources().getString(R.string.dive_public_label), 1));
		else
			elem.add(new EditOption(getResources().getString(R.string.dive_privacy_label) + " : ", getResources().getString(R.string.dive_private_label), 1));
		
		if (dive.getTanks() != null && dive.getTanks().size() > 0)
			elem.add(new EditOption(getResources().getString(R.string.tanks_label) + " : ", dive.getTanks().size() + " tanks used"));
		else
			elem.add(new EditOption(getResources().getString(R.string.tanks_label) + " : ", ""));
		
		if (dive.getDiveReviews()== null)
			elem.add(new EditOption(getResources().getString(R.string.review_label) + " : ", ""));
		else
		{
			
			String fullReview = "";

			if (dive.getDiveReviews().getOverall()!= null)
				fullReview += "Overall: " + _getReviewHintGeneral(dive.getDiveReviews().getOverall()).toLowerCase() + ". ";
			if (dive.getDiveReviews().getDifficulty()!= null)
				fullReview += "Dive difficulty: " + _getReviewHintDifficulty(dive.getDiveReviews().getDifficulty()).toLowerCase() + ". ";
			if (dive.getDiveReviews().getMarine()!= null)
				fullReview += "Marine life: " + _getReviewHintGeneral(dive.getDiveReviews().getMarine()).toLowerCase() + ". ";
			if (dive.getDiveReviews().getBigFish()!= null)
				fullReview += "Big fish sighted: " + _getReviewHintGeneral(dive.getDiveReviews().getBigFish()).toLowerCase() + ". ";
			if (dive.getDiveReviews().getWreck()!= null)
				fullReview += "Wrecks sighted: " + _getReviewHintGeneral(dive.getDiveReviews().getWreck()).toLowerCase() + ". ";
			elem.add(new EditOption("Review : ", fullReview));
			
		}
		
		
		EditDiveActivity.mOptionAdapter = new OptionAdapter(getActivity().getApplicationContext(), elem, mModel.getDives().get(mIndex));
		optionList.setAdapter(EditDiveActivity.mOptionAdapter);
		
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
						_editTanks();
						break;
					case 18:
						_editReviewType();
						break;
				}
			}
		});
		return rootView;
    }
}
