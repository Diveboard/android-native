package com.diveboard.model;

public class					Weight
{
	private Double				_value;
	private Units.Weight		_unit;
	
	public						Weight(Double value)
	{
		_value = value;
		_unit = Units.getWeightUnit();
		if (_unit == Units.Weight.LBS)
			_value = Converter.convert(_value, Units.Weight.LBS, Units.Weight.KG);
	}
	
	public						Weight(Double value, Units.Weight unit)
	{
		_value = value;
		_unit = Units.getWeightUnit();
		if (unit == Units.Weight.LBS)
			_value = Converter.convert(_value, Units.Weight.LBS, Units.Weight.KG);
	}
	
	public Double				getWeight()
	{
		Double					result = 0.0;
		
		switch (_unit)
		{
			case KG:
				result = _value;
				break ;
			case LBS:
				result = Converter.convert(_value, Units.Weight.KG, Units.Weight.LBS);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public Double				getWeight(Units.Weight unit)
	{
		Double					result = 0.0;
		
		switch (unit)
		{
			case KG:
				result = _value;
				break ;
			case LBS:
				result = Converter.convert(_value, Units.Weight.KG, Units.Weight.LBS);
				break ;
		}
		return (double) (Math.round(result * 100.0) / 100.0);
	}
	
	public String				getSmallName()
	{
		if (_unit == Units.Weight.KG)
			return ("kg");
		if (_unit == Units.Weight.LBS)
			return ("lb");
		return ("Unit not defined");
	}
	
	public String				getFullName()
	{
		if (_unit == Units.Weight.KG)
			return ("kilogram");
		if (_unit == Units.Weight.LBS)
			return ("pound");
		return ("Unit not defined");
	}
}
