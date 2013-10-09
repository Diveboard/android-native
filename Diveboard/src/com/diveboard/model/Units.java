package com.diveboard.model;

import org.json.JSONException;
import org.json.JSONObject;

public class					Units
{
	public enum					Distance
	{
		KM,
		FT
	}
	
	public enum					Weight
	{
		KG,
		LBS
	}
	
	public enum					Temperature
	{
		C,
		F
	}
	
	public enum					Pressure
	{
		BAR,
		PSI
	}
	
	private	static Distance		_distanceUnit = Distance.FT;
	private static Weight		_weightUnit = Weight.KG;
	private static Temperature	_temperatureUnit = Temperature.C;
	private static Pressure		_pressureUnit = Pressure.BAR;
	
	public						Units(final JSONObject json) throws JSONException
	{
		_distanceUnit = (json.getString("distance") == "Km") ? Distance.KM : Distance.FT;
		_weightUnit = (json.getString("weight") == "Kg") ? Weight.KG : Weight.LBS;
		_temperatureUnit = (json.getString("temperature") == "C") ? Temperature.C : Temperature.F;
		_pressureUnit = (json.getString("pressure") == "bar") ? Pressure.BAR : Pressure.PSI;
	}

	public final static Distance		getDistanceUnit() {
		return _distanceUnit;
	}

	public void setDistanceUnit(Distance _distanceUnit) {
		Units._distanceUnit = _distanceUnit;
	}

	public final static Weight			getWeightUnit() {
		return _weightUnit;
	}

	public void setWeightUnit(Weight _weightUnit) {
		Units._weightUnit = _weightUnit;
	}

	public final static Temperature		getTemperatureUnit() {
		return _temperatureUnit;
	}

	public void setTemperatureUnit(Temperature _temperatureUnit) {
		Units._temperatureUnit = _temperatureUnit;
	}

	public final static Pressure		getPressureUnit() {
		return _pressureUnit;
	}

	public void setPressureUnit(Pressure _pressureUnit) {
		Units._pressureUnit = _pressureUnit;
	}
}
