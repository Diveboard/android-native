package com.diveboard.model;

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
