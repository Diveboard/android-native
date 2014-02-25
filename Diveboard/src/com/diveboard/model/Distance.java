package com.diveboard.model;

import com.diveboard.mobile.R;

import android.content.res.Resources;

public class					Distance
{
	private Double				_value;
	private Units.Distance		_unit;
	
	public						Distance(Double value)
	{
		_value = value;
		_unit = Units.getDistanceUnit();
		if (_unit == Units.Distance.FT)
			_value = Converter.convert(_value, Units.Distance.FT, Units.Distance.KM);
	}
	
	public						Distance(Double value, Units.Distance unit)
	{
		_value = value;
		_unit = Units.getDistanceUnit();
		if (unit == Units.Distance.FT)
			_value = Converter.convert(_value, Units.Distance.FT, Units.Distance.KM);
	}
	
	public Double				getDistance()
	{
		Double					result = 0.0;
		
		switch (_unit)
		{
			case KM:
				result = _value;
				break ;
			case FT:
				result = Converter.convert(_value, Units.Distance.KM, Units.Distance.FT);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public Double				getDistance(Units.Distance unit)
	{
		Double					result = 0.0;
		
		switch (unit)
		{
			case KM:
				result = _value;
				break ;
			case FT:
				result = Converter.convert(_value, Units.Distance.KM, Units.Distance.FT);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public String				getSmallName()
	{
		if (_unit == Units.Distance.KM)
			return (Resources.getSystem().getString(R.string.unit_m));
		if (_unit == Units.Distance.FT)
			return (Resources.getSystem().getString(R.string.unit_ft));
		return (Resources.getSystem().getString(R.string.unit_not_defined));
	}
	
	public String				getFullName()
	{
		if (_unit == Units.Distance.KM)
			return (Resources.getSystem().getString(R.string.meters));
		if (_unit == Units.Distance.FT)
			return (Resources.getSystem().getString(R.string.feet));
		return (Resources.getSystem().getString(R.string.unit_not_defined));
	}
}
