package com.diveboard.model;

public class					Distance
{
	private Double				_value;
	private Units.Distance		_unit;
	
	public						Distance(Double value)
	{
		_value = value;
		_unit = Units.getDistanceUnit();
	}
	
	public Double				getDistance()
	{
		return _value;
	}
	
	public Double				getDistance(Units.Distance unit)
	{
		switch (unit)
		{
			case KM:
				return _value;
			case FT:
				return Converter.convert(_value, Units.Distance.KM, Units.Distance.FT);
		}
		return 0.0;
	}
	
	public String				getSmallName()
	{
		if (_unit == Units.Distance.KM)
			return ("m");
		if (_unit == Units.Distance.FT)
			return ("ft");
		return ("Unit not defined");
	}
	
	public String				getFullName()
	{
		if (_unit == Units.Distance.KM)
			return ("meters");
		if (_unit == Units.Distance.FT)
			return ("feet");
		return ("Unit not defined");
	}
}
