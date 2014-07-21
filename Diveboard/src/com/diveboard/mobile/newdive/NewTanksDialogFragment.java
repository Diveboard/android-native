package com.diveboard.mobile.newdive;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.mobile.R;
import com.diveboard.mobile.editdive.EditTanksDialogFragment;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.model.Units;

import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class					NewTanksDialogFragment extends DialogFragment
{
	public interface			EditTanksDialogListener
	{
        void					onTanksEditComplete(DialogFragment dialog);
    }
	
	private ArrayList<Tank>			mTanks;
	private Dive					mDive;
	private Typeface				mFaceR;
	private View					mView;
	EditTanksDialogListener			mListener;
	private EditText				mVolumeField;
	private EditText				mStartPressureField;
	private EditText				mEndPressureField;
	private EditText				mStartTimeField;
	private TextView				he_title;
	private EditText				mHeField;
	private EditText				mO2Field;
	private EditText				mN2Field;
	private Integer					mIndex;
	private Spinner					mCylinderLabel;
	private Spinner					mMaterialLabel;
	private Spinner					mVolumeLabel;
	private Spinner					mMixLabel;
	private Spinner					mPressureLabel;
	private int						mTextSize = 15;
	
	 @Override
	 public void onAttach(Activity activity)
	 {
		 super.onAttach(activity);
		 // Verify that the host activity implements the callback interface
		 try
		 {
			 // Instantiate the NoticeDialogListener so we can send events to the host
			 mListener = (EditTanksDialogListener) activity;
		 }
		 catch (ClassCastException e)
		 {
			 // The activity doesn't implement the interface, throw exception
			 throw new ClassCastException(activity.toString() + " must implement onSafetyStopsEditComplete");
		 }
	 }
	
		private void				addNoTanks(LinearLayout layout)
		{
			final float scale = getResources().getDisplayMetrics().density;
			
			TextView text = new TextView(getActivity().getApplicationContext());
			text.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			text.setTypeface(mFaceR);
			text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
			text.setTextSize(25);
			text.setText(getResources().getString(R.string.no_tanks));
			text.setTextColor(getResources().getColor(R.color.dark_grey));
			text.setGravity(Gravity.CENTER);
			layout.addView(text);
		}
		
		private void				openTanksEdit(Integer index)
		{
			mIndex = index;
			LinearLayout tankslist = (LinearLayout) mView.findViewById(R.id.tanksfields);
			tankslist.removeAllViews();
			final float scale = getResources().getDisplayMetrics().density;
			Tank tank = mTanks.get(index);
			
			Button add_button = (Button) mView.findViewById(R.id.add_button);
			add_button.setVisibility(Button.GONE);

			LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			//CYLINDER ROW
			LinearLayout cylinder = new LinearLayout(getActivity().getApplicationContext());
			cylinder.setLayoutParams(params);
			cylinder.setOrientation(LinearLayout.HORIZONTAL);
			cylinder.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView cylinder_title = new TextView(getActivity().getApplicationContext());
			cylinder_title.setTypeface(mFaceR);
			cylinder_title.setTextColor(getResources().getColor(R.color.dark_grey));
			cylinder_title.setTextSize(mTextSize);
			cylinder_title.setText(getResources().getString(R.string.cylinder_label) + ":");
			cylinder_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			cylinder.addView(cylinder_title);
			
			mCylinderLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> cylinderAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			if(tank.getMultitank() == 2){
				cylinderAdapter.add("2");
				cylinderAdapter.add("1");
			}
			else{
				cylinderAdapter.add("1");
				cylinderAdapter.add("2");
			}
			cylinderAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mCylinderLabel.setAdapter(cylinderAdapter);
			cylinder.addView(mCylinderLabel);
			
			mMaterialLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			materialAdapter.add(getResources().getString(R.string.aluminium));
			materialAdapter.add(getResources().getString(R.string.steel));
			materialAdapter.add(getResources().getString(R.string.carbon));
			materialAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mMaterialLabel.setAdapter(materialAdapter);
			cylinder.addView(mMaterialLabel);
			
			tankslist.addView(cylinder);
			
			//VOLUME ROW
			LinearLayout volume = new LinearLayout(getActivity().getApplicationContext());
			volume.setLayoutParams(params);
			volume.setOrientation(LinearLayout.HORIZONTAL);
			volume.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView volume_title = new TextView(getActivity().getApplicationContext());
			volume_title.setTypeface(mFaceR);
			volume_title.setTextColor(getResources().getColor(R.color.dark_grey));
			volume_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			volume_title.setTextSize(mTextSize);
			volume_title.setText(getResources().getString(R.string.volume_label) +":");
			volume.addView(volume_title);
			
			mVolumeField = new EditText(getActivity().getApplicationContext());
			mVolumeField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mVolumeField.setTypeface(mFaceR);
			mVolumeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mVolumeField.setTextSize(mTextSize);
			mVolumeField.setText(tank.getVolumeValue().toString());
			volume.addView(mVolumeField);
			
			mVolumeLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> volumeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			String volume_unit = tank.getVolumeUnit();
			if (volume_unit.equals("L"))
			{
				volumeAdapter.add(getResources().getString(R.string.unit_liter));
				volumeAdapter.add(getResources().getString(R.string.cubic_ft));

			}
			else
			{
				volumeAdapter.add(getResources().getString(R.string.cubic_ft));
				volumeAdapter.add(getResources().getString(R.string.unit_liter));
			}
			volumeAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mVolumeLabel.setAdapter(volumeAdapter);
			volume.addView(mVolumeLabel);
			
			tankslist.addView(volume);
			
			
			//GAS MIX ROW
			LinearLayout gasmix = new LinearLayout(getActivity().getApplicationContext());
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			gasmix.setLayoutParams(params);
			gasmix.setOrientation(LinearLayout.VERTICAL);
			gasmix.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			LinearLayout typeofgasmix = new LinearLayout(getActivity().getApplicationContext());
			typeofgasmix.setLayoutParams(params);
			typeofgasmix.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView mix_title = new TextView(getActivity().getApplicationContext());
			mix_title.setTypeface(mFaceR);
			mix_title.setTextColor(getResources().getColor(R.color.dark_grey));
			mix_title.setTextSize(mTextSize);
			mix_title.setText(getResources().getString(R.string.mix_label) + ":");
			mix_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			typeofgasmix.addView(mix_title);
			
			mMixLabel = new Spinner(getActivity().getApplicationContext());
			
			ArrayAdapter<String> adapt_mix = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};

      adapt_mix.add(getResources().getString(R.string.air_mix));
      adapt_mix.add(getResources().getString(R.string.nitrox_mix));
      adapt_mix.add(getResources().getString(R.string.trimix_mix));
			adapt_mix.setDropDownViewResource(R.layout.units_spinner_fields);
			mMixLabel.setAdapter(adapt_mix);

			final LinearLayout custom = new LinearLayout(getActivity().getApplicationContext());
			custom.setLayoutParams(params);
			custom.setOrientation(LinearLayout.HORIZONTAL);
			custom.setVisibility(View.GONE);
			
			TextView o2_title = new TextView(getActivity().getApplicationContext());
			o2_title.setTypeface(mFaceR);
			o2_title.setTextColor(getResources().getColor(R.color.dark_grey));
			o2_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			o2_title.setTextSize(mTextSize);
			o2_title.setText(getResources().getString(R.string.unit_o2) +":");
			custom.addView(o2_title);
			
			mO2Field = new EditText(getActivity().getApplicationContext());
			mO2Field.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mO2Field.setTypeface(mFaceR);
			mO2Field.setTextColor(getResources().getColor(R.color.dark_grey));
			mO2Field.setTextSize(mTextSize);
			mO2Field.setText(tank.getO2().toString());
			custom.addView(mO2Field);
			
			he_title = new TextView(getActivity().getApplicationContext());
			he_title.setTypeface(mFaceR);
			he_title.setTextColor(getResources().getColor(R.color.dark_grey));
			he_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			he_title.setTextSize(mTextSize);
			he_title.setText(getResources().getString(R.string.unit_he) +":");
			custom.addView(he_title);
			
			mHeField = new EditText(getActivity().getApplicationContext());
			mHeField.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mHeField.setTypeface(mFaceR);
			mHeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mHeField.setTextSize(mTextSize);
			mHeField.setText(tank.getHe().toString());
			custom.addView(mHeField);
			
			TextView n2_title = new TextView(getActivity().getApplicationContext());
			n2_title.setTypeface(mFaceR);
			n2_title.setTextColor(getResources().getColor(R.color.dark_grey));
			n2_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			n2_title.setTextSize(mTextSize);
			n2_title.setText(getResources().getString(R.string.unit_n2) +":");
			//custom.addView(n2_title);
			
			mN2Field = new EditText(getActivity().getApplicationContext());
			mN2Field.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mN2Field.setTypeface(mFaceR);
			mN2Field.setTextColor(getResources().getColor(R.color.dark_grey));
			mN2Field.setTextSize(mTextSize);
			mN2Field.setText(tank.getN2().toString());
			//custom.addView(mN2Field);

      if (tank.getGasType().equals("air")) {
        mMixLabel.setSelection(0, true);
        custom.setVisibility(View.GONE);
      } else if (tank.getGasType().equals("nitrox")) {
        mMixLabel.setSelection(1, true);
        mHeField.setVisibility(View.GONE);
        he_title.setVisibility(View.GONE);
        custom.setVisibility(View.VISIBLE);
      } else {
        mMixLabel.setSelection(2, true);
        mHeField.setVisibility(View.VISIBLE);
        he_title.setVisibility(View.VISIBLE);
        custom.setVisibility(View.VISIBLE);
      }


			mMixLabel.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if(parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.air_mix))){
						custom.setVisibility(View.GONE);
					} else if(parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.nitrox_mix))){
						custom.setVisibility(View.VISIBLE);
            mHeField.setVisibility(View.GONE);
            he_title.setVisibility(View.GONE);
          } else { //trimix
						custom.setVisibility(View.VISIBLE);
            mHeField.setVisibility(View.VISIBLE);
            he_title.setVisibility(View.VISIBLE);
          }
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			typeofgasmix.addView(mMixLabel);
			gasmix.addView(typeofgasmix);
			gasmix.addView(custom);
			
			tankslist.addView(gasmix);
			
			
			//PRESSURE ROW
			LinearLayout pressure = new LinearLayout(getActivity().getApplicationContext());
			pressure.setLayoutParams(params);
			pressure.setOrientation(LinearLayout.HORIZONTAL);
			pressure.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView start_pressure_title = new TextView(getActivity().getApplicationContext());
			start_pressure_title.setTypeface(mFaceR);
			start_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
			start_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			start_pressure_title.setTextSize(mTextSize);
			start_pressure_title.setText(getResources().getString(R.string.start_pressure_label) +":");
			pressure.addView(start_pressure_title);
			
			mStartPressureField = new EditText(getActivity().getApplicationContext());
			mStartPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mStartPressureField.setTypeface(mFaceR);
			mStartPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
			mStartPressureField.setTextSize(mTextSize);
			mStartPressureField.setText(tank.getPStartValue().toString());
			pressure.addView(mStartPressureField);
			
			
			LinearLayout endpressure = new LinearLayout(getActivity().getApplicationContext());
			endpressure.setLayoutParams(params);
			endpressure.setOrientation(LinearLayout.HORIZONTAL);
			endpressure.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			TextView end_pressure_title = new TextView(getActivity().getApplicationContext());
			end_pressure_title.setTypeface(mFaceR);
			end_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
			end_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			end_pressure_title.setTextSize(mTextSize);
			end_pressure_title.setText(getResources().getString(R.string.end_pressure_label) +":");
			endpressure.addView(end_pressure_title);
			
			mEndPressureField = new EditText(getActivity().getApplicationContext());
			mEndPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mEndPressureField.setTypeface(mFaceR);
			mEndPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
			mEndPressureField.setTextSize(mTextSize);
			mEndPressureField.setText(tank.getPEndValue().toString());
			endpressure.addView(mEndPressureField);
			
			mPressureLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> press_adapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			String pressure_unit = tank.getPUnit();
			if (pressure_unit.equals("bar"))
			{
				press_adapt.add(getResources().getString(R.string.unit_bar));
				press_adapt.add(getResources().getString(R.string.unit_psi));

			}
			else
			{
				press_adapt.add(getResources().getString(R.string.unit_psi));
				press_adapt.add(getResources().getString(R.string.unit_bar));
			}
			press_adapt.setDropDownViewResource(R.layout.units_spinner_fields);
			mPressureLabel.setAdapter(press_adapt);
			endpressure.addView(mPressureLabel);
			
			tankslist.addView(pressure);
			tankslist.addView(endpressure);
			
			//Start Time ROW
			LinearLayout startTime = new LinearLayout(getActivity().getApplicationContext());
			startTime.setLayoutParams(params);
			startTime.setOrientation(LinearLayout.HORIZONTAL);
			startTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView startTime_title = new TextView(getActivity().getApplicationContext());
			startTime_title.setTypeface(mFaceR);
			startTime_title.setTextColor(getResources().getColor(R.color.dark_grey));
			startTime_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			startTime_title.setTextSize(mTextSize);
			startTime_title.setText(getResources().getString(R.string.start_time_label) +":");
			startTime.addView(startTime_title);
			
			mStartTimeField = new EditText(getActivity().getApplicationContext());
			mStartTimeField.setRawInputType(InputType.TYPE_CLASS_NUMBER );
			mStartTimeField.setTypeface(mFaceR);
			mStartTimeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mStartTimeField.setTextSize(mTextSize);
			mStartTimeField.setText(String.valueOf(tank.getTimeStart() / 60));
			startTime.addView(mStartTimeField);
			
			TextView startTime_label = new TextView(getActivity().getApplicationContext());
			startTime_label.setTypeface(mFaceR);
			startTime_label.setTextColor(getResources().getColor(R.color.dark_grey));
			startTime_label.setTextSize(mTextSize);
			startTime_label.setText(getResources().getString(R.string.mins_from_start));
			startTime.addView(startTime_label);
			if (index == 0)
				startTime.setVisibility(View.GONE);
			tankslist.addView(startTime);
			
      Button cancel = (Button) mView.findViewById(R.id.cancel);
      cancel.setTypeface(mFaceR);
      cancel.setText(getResources().getString(R.string.cancel));
      cancel.setOnClickListener(new OnClickListener()
      {
				@Override
				public void onClick(View v)
				{
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
					mVolumeField = null;
					mStartPressureField = null;
					mEndPressureField = null;
					mStartTimeField = null;
					mIndex = null;
					openTanksList();
				}
			});
	        
      Button save = (Button) mView.findViewById(R.id.save);
      save.setTypeface(mFaceR);
      save.setText(getResources().getString(R.string.save));
      save.setOnClickListener(new OnClickListener()
      {
				@Override
				public void onClick(View v)
				{
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
					if (mVolumeField.getText().toString().isEmpty())
						mVolumeField.setText("0");
					if (mStartPressureField.getText().toString().isEmpty())
						mStartPressureField.setText("0");
					if (mEndPressureField.getText().toString().isEmpty())
						mEndPressureField.setText("0");
					if (mStartTimeField.getText().toString().isEmpty())
						mStartTimeField.setText("0");
					JSONObject newtank = new JSONObject();
					try{
						newtank.put("volume_value", Double.parseDouble(mVolumeField.getText().toString()));
						newtank.put("volume_unit", mVolumeLabel.getSelectedItem().toString());
						if(mMixLabel.getSelectedItem().toString().equals(getResources().getString(R.string.nitrox_mix))){
              newtank.put("gas_type", "nitrox");
							if(!mO2Field.getText().toString().isEmpty())
								newtank.put("o2", Integer.parseInt(mO2Field.getText().toString()));
							else{
								mO2Field.setText("0");
								newtank.put("o2", 0);
							}
								
              mHeField.setText("0");
              newtank.put("he", 0);
							newtank.put("n2", 100 - Integer.parseInt(mO2Field.getText().toString()) - Integer.parseInt(mHeField.getText().toString()));
						}
						else if(mMixLabel.getSelectedItem().toString().equals(getResources().getString(R.string.trimix_mix))){
              newtank.put("gas_type", "trimix");
							if(!mO2Field.getText().toString().isEmpty())
								newtank.put("o2", Integer.parseInt(mO2Field.getText().toString()));
							else{
								mO2Field.setText("0");
								newtank.put("o2", 0);
							}
								
							if(!mHeField.getText().toString().isEmpty())
								newtank.put("he", Integer.parseInt(mHeField.getText().toString()));
							else{
								mHeField.setText("0");
								newtank.put("he", 0);
							}
							newtank.put("n2", 100 - Integer.parseInt(mO2Field.getText().toString()) - Integer.parseInt(mHeField.getText().toString()));
						}
						else{
              newtank.put("gas_type", "air");
							newtank.put("o2", 21);
							newtank.put("he", 0);
							newtank.put("n2", 79);
						}
						newtank.put("material", mMaterialLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("multitank", Integer.parseInt(mCylinderLabel.getSelectedItem().toString()));
						newtank.put("p_start_value", Double.parseDouble(mStartPressureField.getText().toString()));
						newtank.put("p_end_value", Double.parseDouble(mEndPressureField.getText().toString()));
						newtank.put("p_end_unit", mPressureLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("p_start_unit", mPressureLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("time_start", Integer.parseInt(mStartTimeField.getText().toString()) * 60 ); //needs to be expressed in secs
						mTanks.set(mIndex, new Tank(newtank));
					} catch(JSONException e){
						e.printStackTrace();
					}
					
					mVolumeField = null;
					mStartPressureField = null;
					mEndPressureField = null;
					mStartTimeField = null;
					mIndex = null;
					openTanksList();
				}
			});
		}
		
		private void				openNewTank()
		{
			LinearLayout tankslist = (LinearLayout) mView.findViewById(R.id.tanksfields);
			tankslist.removeAllViews();
			final float scale = getResources().getDisplayMetrics().density;
			
			Button add_button = (Button) mView.findViewById(R.id.add_button);
			add_button.setVisibility(Button.GONE);
			
			LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
			
			//CYLINDER ROW
			LinearLayout cylinder = new LinearLayout(getActivity().getApplicationContext());
			cylinder.setLayoutParams(params);
			cylinder.setOrientation(LinearLayout.HORIZONTAL);
			cylinder.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView cylinder_title = new TextView(getActivity().getApplicationContext());
			cylinder_title.setTypeface(mFaceR);
			cylinder_title.setTextColor(getResources().getColor(R.color.dark_grey));
			cylinder_title.setTextSize(mTextSize);
			cylinder_title.setText(getResources().getString(R.string.cylinder_label) + ":");
			cylinder_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			cylinder.addView(cylinder_title);
			
			mCylinderLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> cylinderAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			cylinderAdapter.add("1");
			cylinderAdapter.add("2");
			cylinderAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mCylinderLabel.setAdapter(cylinderAdapter);
			cylinder.addView(mCylinderLabel);
			
			mMaterialLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> materialAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			
			materialAdapter.add(getResources().getString(R.string.aluminium));
			materialAdapter.add(getResources().getString(R.string.steel));
			materialAdapter.add(getResources().getString(R.string.carbon));
			materialAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mMaterialLabel.setAdapter(materialAdapter);
			cylinder.addView(mMaterialLabel);
			
			tankslist.addView(cylinder);
			
			
			//VOLUME ROW
			LinearLayout volume = new LinearLayout(getActivity().getApplicationContext());
			volume.setLayoutParams(params);
			volume.setOrientation(LinearLayout.HORIZONTAL);
			volume.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));

			TextView volume_title = new TextView(getActivity().getApplicationContext());
			volume_title.setTypeface(mFaceR);
			volume_title.setTextColor(getResources().getColor(R.color.dark_grey));
			volume_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			volume_title.setTextSize(mTextSize);
			volume_title.setText(getResources().getString(R.string.volume_label) +":");
			volume.addView(volume_title);

			mVolumeField = new EditText(getActivity().getApplicationContext());
			mVolumeField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mVolumeField.setTypeface(mFaceR);
			mVolumeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mVolumeField.setTextSize(mTextSize);
			Double defaultVolume = 12.0;
			mVolumeField.setText(defaultVolume.toString());
			volume.addView(mVolumeField);

			mVolumeLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> volumeAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			volumeAdapter.add(getResources().getString(R.string.unit_liter));
			volumeAdapter.add(getResources().getString(R.string.cubic_ft));
			volumeAdapter.setDropDownViewResource(R.layout.units_spinner_fields);
			mVolumeLabel.setAdapter(volumeAdapter);
			volume.addView(mVolumeLabel);

			tankslist.addView(volume);
			

			//GAS MIX ROW
			LinearLayout gasmix = new LinearLayout(getActivity().getApplicationContext());
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			gasmix.setLayoutParams(params);
			gasmix.setOrientation(LinearLayout.VERTICAL);
			gasmix.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));

			LinearLayout typeofgasmix = new LinearLayout(getActivity().getApplicationContext());
			typeofgasmix.setLayoutParams(params);
			typeofgasmix.setOrientation(LinearLayout.HORIZONTAL);
			
			TextView mix_title = new TextView(getActivity().getApplicationContext());
			mix_title.setTypeface(mFaceR);
			mix_title.setTextColor(getResources().getColor(R.color.dark_grey));
			mix_title.setTextSize(mTextSize);
			mix_title.setText(getResources().getString(R.string.mix_label) + ":");
			mix_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			typeofgasmix.addView(mix_title);

			mMixLabel = new Spinner(getActivity().getApplicationContext());

			ArrayAdapter<String> adapt_mix = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			adapt_mix.add(getResources().getString(R.string.air_mix));
			adapt_mix.add(getResources().getString(R.string.nitrox_mix));
			adapt_mix.add(getResources().getString(R.string.trimix_mix));
			adapt_mix.setDropDownViewResource(R.layout.units_spinner_fields);
			mMixLabel.setAdapter(adapt_mix);


			final LinearLayout custom = new LinearLayout(getActivity().getApplicationContext());
			custom.setLayoutParams(params);
			custom.setOrientation(LinearLayout.HORIZONTAL);
			custom.setVisibility(View.GONE);

			TextView o2_title = new TextView(getActivity().getApplicationContext());
			o2_title.setTypeface(mFaceR);
			o2_title.setTextColor(getResources().getColor(R.color.dark_grey));
			o2_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			o2_title.setTextSize(mTextSize);
			o2_title.setText(getResources().getString(R.string.unit_o2) +":");
			custom.addView(o2_title);

			mO2Field = new EditText(getActivity().getApplicationContext());
			mO2Field.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mO2Field.setTypeface(mFaceR);
			mO2Field.setTextColor(getResources().getColor(R.color.dark_grey));
			mO2Field.setTextSize(mTextSize);
			mO2Field.setText("21");
			custom.addView(mO2Field);

			he_title = new TextView(getActivity().getApplicationContext());
			he_title.setTypeface(mFaceR);
			he_title.setTextColor(getResources().getColor(R.color.dark_grey));
			he_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			he_title.setTextSize(mTextSize);
			he_title.setText(getResources().getString(R.string.unit_he) +":");
			custom.addView(he_title);

			mHeField = new EditText(getActivity().getApplicationContext());
			mHeField.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mHeField.setTypeface(mFaceR);
			mHeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mHeField.setTextSize(mTextSize);
			mHeField.setText("0");
			custom.addView(mHeField);

			TextView n2_title = new TextView(getActivity().getApplicationContext());
			n2_title.setTypeface(mFaceR);
			n2_title.setTextColor(getResources().getColor(R.color.dark_grey));
			n2_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			n2_title.setTextSize(mTextSize);
			n2_title.setText(getResources().getString(R.string.unit_n2) +":");
			//custom.addView(n2_title);

			mN2Field = new EditText(getActivity().getApplicationContext());
			mN2Field.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			mN2Field.setTypeface(mFaceR);
			mN2Field.setTextColor(getResources().getColor(R.color.dark_grey));
			mN2Field.setTextSize(mTextSize);
			mN2Field.setText("79");
			mN2Field.setEnabled(false);
			//custom.addView(mN2Field);

			mMixLabel.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					if(parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.air_mix))){
						custom.setVisibility(View.GONE);
					} else if(parent.getItemAtPosition(pos).toString().equals(getResources().getString(R.string.nitrox_mix))){
						custom.setVisibility(View.VISIBLE);
            mHeField.setVisibility(View.GONE);
            he_title.setVisibility(View.GONE);
          } else { //trimix
						custom.setVisibility(View.VISIBLE);
            mHeField.setVisibility(View.VISIBLE);
            he_title.setVisibility(View.VISIBLE);
          }
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			typeofgasmix.addView(mMixLabel);
			gasmix.addView(typeofgasmix);
			gasmix.addView(custom);

			tankslist.addView(gasmix);
			
			
			//PRESSURE ROW
			LinearLayout pressure = new LinearLayout(getActivity().getApplicationContext());
			pressure.setLayoutParams(params);
			pressure.setOrientation(LinearLayout.HORIZONTAL);
			pressure.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));

			TextView start_pressure_title = new TextView(getActivity().getApplicationContext());
			start_pressure_title.setTypeface(mFaceR);
			start_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
			start_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			start_pressure_title.setTextSize(mTextSize);
			start_pressure_title.setText(getResources().getString(R.string.start_pressure_label) +":");
			pressure.addView(start_pressure_title);

			mStartPressureField = new EditText(getActivity().getApplicationContext());
			mStartPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mStartPressureField.setTypeface(mFaceR);
			mStartPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
			mStartPressureField.setTextSize(mTextSize);
			if(Units.getPressureUnit() == Units.Pressure.BAR)
				mStartPressureField.setText("200.0");
			else
				mStartPressureField.setText("3000.0");
			pressure.addView(mStartPressureField);
			
			LinearLayout endpressure = new LinearLayout(getActivity().getApplicationContext());
			endpressure.setLayoutParams(params);
			endpressure.setOrientation(LinearLayout.HORIZONTAL);
			endpressure.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
			
			TextView end_pressure_title = new TextView(getActivity().getApplicationContext());
			end_pressure_title.setTypeface(mFaceR);
			end_pressure_title.setTextColor(getResources().getColor(R.color.dark_grey));
			end_pressure_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			end_pressure_title.setTextSize(mTextSize);
			end_pressure_title.setText(getResources().getString(R.string.end_pressure_label) +":");
			endpressure.addView(end_pressure_title);

			mEndPressureField = new EditText(getActivity().getApplicationContext());
			mEndPressureField.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			mEndPressureField.setTypeface(mFaceR);
			mEndPressureField.setTextColor(getResources().getColor(R.color.dark_grey));
			mEndPressureField.setTextSize(mTextSize);
			if(Units.getPressureUnit() == Units.Pressure.BAR)
				mEndPressureField.setText("50.0");
			else
				mEndPressureField.setText("500.0");
			endpressure.addView(mEndPressureField);

			mPressureLabel = new Spinner(getActivity().getApplicationContext());
			ArrayAdapter<String> press_adapt = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.units_spinner){
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					((TextView) v).setTextSize(mTextSize);
					return v;
				}
				public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
					View v =super.getDropDownView(position, convertView, parent);
					((TextView) v).setTypeface(mFaceR);
					return v;
				}};
			String pressure_unit = (Units.getPressureUnit() == Units.Pressure.BAR) ? "bar" : "psi";
			if (pressure_unit.equals("bar"))
			{
				press_adapt.add(getResources().getString(R.string.unit_bar));
				press_adapt.add(getResources().getString(R.string.unit_psi));

			}
			else
			{
				press_adapt.add(getResources().getString(R.string.unit_psi));
				press_adapt.add(getResources().getString(R.string.unit_bar));
			}
			press_adapt.setDropDownViewResource(R.layout.units_spinner_fields);
			mPressureLabel.setAdapter(press_adapt);
			endpressure.addView(mPressureLabel);

			tankslist.addView(pressure);
			tankslist.addView(endpressure);
			
			
			//Start Time ROW
			LinearLayout startTime = new LinearLayout(getActivity().getApplicationContext());
			startTime.setLayoutParams(params);
			startTime.setOrientation(LinearLayout.HORIZONTAL);
			startTime.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));

			TextView startTime_title = new TextView(getActivity().getApplicationContext());
			startTime_title.setTypeface(mFaceR);
			startTime_title.setTextColor(getResources().getColor(R.color.dark_grey));
			startTime_title.setPadding((int)(10 * scale + 0.5f), 0, 0, 0);
			startTime_title.setTextSize(mTextSize);
			startTime_title.setText(getResources().getString(R.string.start_time_label) +":");
			startTime.addView(startTime_title);

			mStartTimeField = new EditText(getActivity().getApplicationContext());
			mStartTimeField.setRawInputType(InputType.TYPE_CLASS_NUMBER );
			mStartTimeField.setTypeface(mFaceR);
			mStartTimeField.setTextColor(getResources().getColor(R.color.dark_grey));
			mStartTimeField.setTextSize(mTextSize);
			mStartTimeField.setText("0");
			startTime.addView(mStartTimeField);

			TextView startTime_label = new TextView(getActivity().getApplicationContext());
			startTime_label.setTypeface(mFaceR);
			startTime_label.setTextColor(getResources().getColor(R.color.dark_grey));
			startTime_label.setTextSize(mTextSize);
			startTime_label.setText(getResources().getString(R.string.mins_from_start));
			startTime.addView(startTime_label);
			if (mTanks.size() == 0)
				startTime.setVisibility(View.GONE);
			
			tankslist.addView(startTime);
			
	        Button cancel = (Button) mView.findViewById(R.id.cancel);
	        cancel.setTypeface(mFaceR);
	        cancel.setText(getResources().getString(R.string.cancel));
	        cancel.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View v)
				{
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
					mVolumeField = null;
					mStartPressureField = null;
					mEndPressureField = null;
					mStartTimeField = null;
					openTanksList();
				}
			});
	        
	        Button save = (Button) mView.findViewById(R.id.save);
	        save.setTypeface(mFaceR);
	        save.setText(getResources().getString(R.string.save));
	        save.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View v)
				{
					InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(getActivity().getApplicationContext().INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mStartTimeField.getWindowToken(), 0);
					if (mVolumeField.getText().toString().isEmpty())
						mVolumeField.setText("0");
					if (mStartPressureField.getText().toString().isEmpty())
						mStartPressureField.setText("0");
					if (mEndPressureField.getText().toString().isEmpty())
						mEndPressureField.setText("0");
					if (mStartTimeField.getText().toString().isEmpty())
						mStartTimeField.setText("0");
					try{
						JSONObject newtank = new JSONObject();
						newtank.put("volume_value", Double.parseDouble(mVolumeField.getText().toString()));

            if(mMixLabel.getSelectedItem().toString().equals(getResources().getString(R.string.nitrox_mix))){
              newtank.put("gas_type", "nitrox");
              if(!mO2Field.getText().toString().isEmpty())
                newtank.put("o2", Integer.parseInt(mO2Field.getText().toString()));
              else{
                mO2Field.setText("0");
                newtank.put("o2", 0);
              }
                
              mHeField.setText("0");
              newtank.put("he", 0);
              newtank.put("n2", 100 - Integer.parseInt(mO2Field.getText().toString()) - Integer.parseInt(mHeField.getText().toString()));
            }
            else if(mMixLabel.getSelectedItem().toString().equals(getResources().getString(R.string.trimix_mix))){
              newtank.put("gas_type", "trimix");
							if(!mO2Field.getText().toString().isEmpty())
								newtank.put("o2", Integer.parseInt(mO2Field.getText().toString()));
							else{
								mO2Field.setText("0");
								newtank.put("o2", 0);
							}
								
							if(!mHeField.getText().toString().isEmpty())
								newtank.put("he", Integer.parseInt(mHeField.getText().toString()));
							else{
								mHeField.setText("0");
								newtank.put("he", 0);
							}
							newtank.put("n2", 100 - Integer.parseInt(mO2Field.getText().toString()) - Integer.parseInt(mHeField.getText().toString()));
						}
						else{
              newtank.put("gas_type", "air");
              newtank.put("o2", 21);
              newtank.put("he", 0);
              newtank.put("n2", 79);
						}
						newtank.put("material", mMaterialLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("multitank", Integer.parseInt(mCylinderLabel.getSelectedItem().toString()));
						newtank.put("p_start_value", Double.parseDouble(mStartPressureField.getText().toString()));
						newtank.put("p_end_value", Double.parseDouble(mEndPressureField.getText().toString()));
						newtank.put("p_end_unit", mPressureLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("p_start_unit", mPressureLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("volume_unit", mVolumeLabel.getSelectedItem().toString().toLowerCase());
						newtank.put("time_start", Integer.parseInt(mStartTimeField.getText().toString()) * 60 ); //needs to be expressed in secs
						mTanks.add(new Tank(newtank));
					} catch(JSONException e){
						e.printStackTrace();
					}
					
					mVolumeField = null;
					mStartPressureField = null;
					mEndPressureField = null;
					mStartTimeField = null;
					mIndex = null;
					openTanksList();
				}
			});
		}
		
		private void				deleteTank(int index)
		{
			mTanks.remove(index);
			openTanksList();
		}
		
		private void				openTanksList()
		{
			LinearLayout tankslist = (LinearLayout) mView.findViewById(R.id.tanksfields);
			tankslist.removeAllViews();
			if (mTanks.size() == 0)
				addNoTanks(tankslist);
			else
			{
				for (int i = 0, length = mTanks.size(); i < length; i++)
				{
					final float scale = getResources().getDisplayMetrics().density;
					LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
					
					RelativeLayout row = new RelativeLayout(getActivity().getApplicationContext());
					row.setLayoutParams(params);
					
					LinearLayout tankElem = new LinearLayout(getActivity().getApplicationContext());
					tankElem.setTag(i);
					tankElem.setLayoutParams(params);
					tankElem.setOrientation(LinearLayout.HORIZONTAL);
					tankElem.setGravity(Gravity.CENTER);
					tankElem.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_body_background));
					tankElem.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							// Edit Safety Stops
							openTanksEdit((Integer) v.getTag());
						}
					});

					TextView text = new TextView(getActivity().getApplicationContext());
					text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					text.setTypeface(mFaceR);
//					text.setPadding(0, (int)(10 * scale + 0.5f), 0, (int)(10 * scale + 0.5f));
					text.setTextSize(mTextSize);
					text.setPadding((int)(10 * scale + 0.5f),(int)(10 * scale + 0.5f),(int)(10 * scale + 0.5f),0);
					
					//Writing the summary string of the tank
					String tanksSummary = "";
					Tank tank = mTanks.get(i);
					if(tank.getMultitank() == 2)
						tanksSummary = "2x";
					tanksSummary += tank.getVolumeValue() + tank.getVolumeUnit().toUpperCase() + "        ";
					if(tank.getGasType().equals("nitrox"))
            tanksSummary += "Nx " + tank.getO2();
          else if (tank.getGasType().equals("trimix"))
            tanksSummary += "Tx " + tank.getO2() + "/" + tank.getHe();
          else if (tank.getGasType().equals("air"))
						tanksSummary += getResources().getString(R.string.air_mix);
					else
						tanksSummary += tank.getGasType().toUpperCase();
					tanksSummary +="\n";
					tanksSummary += tank.getPStartValue() + tank.getPUnit() + " \u2192 " + tank.getPEndValue() + tank.getPUnit() + "\n";
					if(i > 0 && tank.getTimeStart() != null )
						tanksSummary += "Switched at: " + tank.getTimeStart() / 60 + "min"; //timestart comes expressed in secs
					
					text.setText(tanksSummary);
					text.setTextColor(getResources().getColor(R.color.dark_grey));
					text.setGravity(Gravity.LEFT);
					
					tankElem.addView(text);
					
//					LinearLayout img = new LinearLayout(getActivity().getApplicationContext());
//					img.setLayoutParams(new LinearLayout.LayoutParams(params));
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int)(50 * scale + 0.5f), LayoutParams.MATCH_PARENT);
					lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					ImageView delete = new ImageView(getActivity().getApplicationContext());
					delete.setScaleType(ScaleType.FIT_CENTER);
					delete.setLayoutParams(lp);
					delete.setPadding((int)(5 * scale + 0.5f), 0, (int)(5 * scale + 0.5f), 0);
					delete.setImageDrawable(getResources().getDrawable(R.drawable.ic_recycle_bin));
					delete.setTag(i);
					delete.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							// Delete Safety Stops
							deleteTank((Integer) v.getTag());
						}
					});
					
					tankElem.addView(delete);
					row.addView(tankElem);
					tankslist.addView(row);
				}
			}
			
			Button add_button = (Button) mView.findViewById(R.id.add_button);
			add_button.setTypeface(mFaceR);
			add_button.setText(getResources().getString(R.string.add_tank_button));
			add_button.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					openNewTank();
				}
			});
			add_button.setVisibility(Button.VISIBLE);
//			if (mSafetyStops.size() >= 3)
//				add_button.setVisibility(Button.GONE);
	        
	        Button cancel = (Button) mView.findViewById(R.id.cancel);
	        cancel.setTypeface(mFaceR);
	        cancel.setText(getResources().getString(R.string.cancel));
	        cancel.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View v)
				{
					dismiss();
				}
			});
	        
	        Button save = (Button) mView.findViewById(R.id.save);
	        save.setTypeface(mFaceR);
	        save.setText(getResources().getString(R.string.save));
	        save.setOnClickListener(new OnClickListener()
	        {
				@Override
				public void onClick(View v)
				{
					mDive.setTanks(mTanks);
					mListener.onTanksEditComplete(NewTanksDialogFragment.this);
					dismiss();
				}
			});
		}
	 
	@Override
	public View					onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		mFaceR = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
		mView = inflater.inflate(R.layout.dialog_edit_tanks, container);
		mDive = ((ApplicationController) getActivity().getApplicationContext()).getTempDive();
		if (mDive.getTanks() != null && !mDive.getTanks().isEmpty()){
			mTanks = (ArrayList<Tank>) mDive.getTanks().clone();
			}
		else
			mTanks = new ArrayList<Tank>();
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		TextView title = (TextView) mView.findViewById(R.id.title);
		title.setTypeface(mFaceR);
		title.setText(getResources().getString(R.string.edit_tanks_title));
		
		openTanksList();
		return mView;
		
	}
	
}
