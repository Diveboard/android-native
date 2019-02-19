package com.diveboard.model;

import com.diveboard.mobile.R;
import android.content.res.Resources;

public class					Temperature
{
	private Double				_value;
	private Units.Temperature	_unit;
	
	public						Temperature(Double value)
	{
		_value = value;
		_unit = Units.getTemperatureUnit();
		if (_unit == Units.Temperature.F)
			_value = Converter.convert(_value, Units.Temperature.F, Units.Temperature.C);
	}
	
	public						Temperature(Double value, Units.Temperature unit)
	{
		_value = value;
		_unit = Units.getTemperatureUnit();
		if (unit == Units.Temperature.F)
			_value = Converter.convert(_value, Units.Temperature.F, Units.Temperature.C);
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
			return (DiveboardModel._context.getResources().getString(R.string.unit_C));
		if (_unit == Units.Temperature.F)
			return (DiveboardModel._context.getResources().getString(R.string.unit_F));
		return (DiveboardModel._context.getResources().getString(R.string.unit_not_defined));
	}
	
	public String				getFullName()
	{
		if (_unit == Units.Temperature.C)
			return (DiveboardModel._context.getResources().getString(R.string.celcius));
		if (_unit == Units.Temperature.F)
			return (DiveboardModel._context.getResources().getString(R.string.farenheit));
		return (DiveboardModel._context.getResources().getString(R.string.unit_not_defined));
	}
}

