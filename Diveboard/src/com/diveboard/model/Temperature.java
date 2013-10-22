package com.diveboard.model;

public class					Temperature
{
	private Double				_value;
	private Units.Temperature	_unit;
	
	public						Temperature(Double value)
	{
		_value = value;
		_unit = Units.getTemperatureUnit();
	}
	
	public Double				getTemperature()
	{
		Double					result = 0.0;
		
		switch (_unit)
		{
			case C:
				result = _value;
				break ;
			case F:
				result = Converter.convert(_value, Units.Temperature.C, Units.Temperature.F);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public Double				getTemperature(Units.Temperature unit)
	{
		Double					result = 0.0;
		
		switch (unit)
		{
			case C:
				result = _value;
				break ;
			case F:
				result = Converter.convert(_value, Units.Temperature.C, Units.Temperature.F);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public String				getSmallName()
	{
		if (_unit == Units.Temperature.C)
			return ("C");
		if (_unit == Units.Temperature.F)
			return ("F");
		return ("Unit not defined");
	}
	
	public String				getFullName()
	{
		if (_unit == Units.Temperature.C)
			return ("celcius");
		if (_unit == Units.Temperature.F)
			return ("fahrenheit");
		return ("Unit not defined");
	}
}

