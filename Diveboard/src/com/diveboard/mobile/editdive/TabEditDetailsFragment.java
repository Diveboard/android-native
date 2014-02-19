package com.diveboard.mobile.editdive;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;

import java.util.ArrayList;

import org.apache.commons.codec.digest.Md5Crypt;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Units;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
    
    private void				_editReviewType()
    {
    	EditReviewDialogFragment dialog = new EditReviewDialogFragment();
    	Bundle args = new Bundle();
    	args.putInt("index", mIndex);
    	dialog.setArguments(args);
    	dialog.show(getActivity().getSupportFragmentManager(), "EditReviewDialogFragment");
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance)
    {
    	ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.tab_edit_details, container, false);
	    
    	Dive dive = mModel.getDives().get(mIndex);
    	
    	optionList = (ListView) rootView.findViewById(R.id.optionList);
    	
		ArrayList<EditOption> elem = new ArrayList<EditOption>();
		elem.add(new EditOption("Date : ", dive.getDate()));
		String[] time_in = dive.getTimeIn().split("T");
		String[] time = time_in[1].split(":");
		elem.add(new EditOption("Time in : ", time[0] + ":" + time[1]));
		//elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth().getDistance()) + " " + dive.getMaxdepth().getSmallName()));
		String maxdepth_unit = "";
		if (dive.getMaxdepthUnit() == null)
			maxdepth_unit = (Units.getDistanceUnit() == Units.Distance.KM) ? "m" : "ft";
		else
			maxdepth_unit = (dive.getMaxdepthUnit().compareTo("m") == 0) ? "m" : "ft";
		elem.add(new EditOption("Max depth : ", Double.toString(dive.getMaxdepth()) + " " + maxdepth_unit));
		ArrayList<SafetyStop> safetystop = dive.getSafetyStops();
		String safetydetails = "";
		for (int i = 0, length = safetystop.size(); i < length; i++)
		{
			if (i != 0)
				safetydetails += ", ";
			safetydetails += safetystop.get(i).getDuration().toString() + "min" + "-" + safetystop.get(i).getDepth().toString() + safetystop.get(i).getUnit();
		}
		elem.add(new EditOption("Safety Stops : ", safetydetails));
		elem.add(new EditOption("Duration : ", Integer.toString(dive.getDuration()) + " min"));
		//elem.add(new EditOption("Safety stops : ", "not implemented"));
//		if (dive.getWeights() != null)
//			elem.add(new EditOption("Weights : ", Double.toString(dive.getWeights().getWeight()) + " " + dive.getWeights().getSmallName()));
//		else
//			elem.add(new EditOption("Weights : ", ""));
		if (dive.getWeights() != null)
		{
			String weights_unit = "";
			if (dive.getWeightsUnit() == null)
				weights_unit = (Units.getWeightUnit() == Units.Weight.KG) ? "kg" : "lbs";
			else
				weights_unit = (dive.getWeightsUnit().compareTo("kg") == 0) ? "kg" : "lbs"; 
			elem.add(new EditOption("Weights : ", Double.toString(dive.getWeights()) + " " + weights_unit));
		}
		else
			elem.add(new EditOption("Weights : ", ""));
		if (dive.getNumber() != null)
			elem.add(new EditOption("Dive number : ", Integer.toString(dive.getNumber())));
		else
			elem.add(new EditOption("Dive number : ", ""));
		if (dive.getGuide() != null)
			elem.add(new EditOption("Guide name : ", dive.getGuide()));
		else
			elem.add(new EditOption("Guide name : ", ""));
		elem.add(new EditOption("Trip name : ", dive.getTripName()));
		ArrayList<String> divetype = dive.getDivetype();
		String divetype_string = "";
		for (int i = 0, length = divetype.size(); i < length; i++)
		{
			if (i != 0)
				divetype_string += ", ";
			divetype_string += divetype.get(i);
		}
		elem.add(new EditOption("Diving type : ", divetype_string));
		//elem.add(new EditOption("Other divers : ", "not implemented"));
		//elem.add(new EditOption("Diving type & activities : ", "not implemented"));
		if (dive.getVisibility() != null)
			elem.add(new EditOption("Visibility : ", dive.getVisibility().substring(0, 1).toUpperCase() + dive.getVisibility().substring(1)));
		else
			elem.add(new EditOption("Visibility : ", ""));
		if (dive.getCurrent() != null)
			elem.add(new EditOption("Current : ", dive.getCurrent().substring(0, 1).toUpperCase() + dive.getCurrent().substring(1)));
		else
			elem.add(new EditOption("Current : ", ""));
		if (dive.getTempSurface() != null)
		{
			//elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface().getTemperature()) + " °" + dive.getTempSurface().getSmallName()));
			String tempsurface_unit = "";
			if (dive.getTempSurfaceUnit() == null)
				tempsurface_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempsurface_unit = (dive.getTempSurfaceUnit().compareTo("C") == 0) ? "C" : "F";
			elem.add(new EditOption("Surface temperature : ", Double.toString(dive.getTempSurface()) + " °" + tempsurface_unit));
		}
		else
			elem.add(new EditOption("Surface temperature : ", ""));
		if (dive.getTempBottom() != null)
		{
//			elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom().getTemperature()) + " °" + dive.getTempBottom().getSmallName()));
			String tempbottom_unit = "";
			if (dive.getTempBottomUnit() == null)
				tempbottom_unit = (Units.getTemperatureUnit() == Units.Temperature.C) ? "C" : "F";
			else
				tempbottom_unit = (dive.getTempBottomUnit().compareTo("C") == 0) ? "C" : "F";
			elem.add(new EditOption("Bottom temperature : ", Double.toString(dive.getTempBottom()) + " °" + tempbottom_unit));
		}
		else
			elem.add(new EditOption("Bottom temperature : ", ""));
		if (dive.getAltitude() != null)
			elem.add(new EditOption("Altitude : ", Double.toString(dive.getAltitude().getDistance()) + " " + dive.getAltitude().getSmallName()));
		else
			elem.add(new EditOption("Altitude : ", ""));
		if (dive.getWater() != null)
			elem.add(new EditOption("Water type : ", dive.getWater().substring(0, 1).toUpperCase() + dive.getWater().substring(1)));
		else
			elem.add(new EditOption("Water type : ", ""));
		if (dive.getPrivacy() == 0)
			elem.add(new EditOption("Dive privacy : ", "Public", 1));
		else
			elem.add(new EditOption("Dive privacy : ", "Private", 1));
		
		if (dive.getDiveReviews()== null)
			elem.add(new EditOption("Review : ", ""));
		else
		{
			
			String fullReview = "";
			Integer overall, difficulty, life, fish, wreck = 0;
			if (dive.getDiveReviews().getOverall()!= null)
				fullReview += "The Overall review was " + dive.getDiveReviews().getOverall().toString() + " ,";
			if (dive.getDiveReviews().getDifficulty()!= null)
				fullReview += "Difficulty was " + dive.getDiveReviews().getDifficulty().toString() + " ,";
			if (dive.getDiveReviews().getMarine()!= null)
				fullReview += "Marine life review was " + dive.getDiveReviews().getMarine().toString() + " ,";
			if (dive.getDiveReviews().getBigFish()!= null)
				fullReview += "Fish review was " + dive.getDiveReviews().getBigFish().toString() + " ,";
			if (dive.getDiveReviews().getOverall()!= null)
				fullReview += "Wreck review was " + dive.getDiveReviews().getWreck().toString() + " ,";
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
					case 17:
						_editReviewType();
						break;
				}
			}
		});
		return rootView;
    }
}
