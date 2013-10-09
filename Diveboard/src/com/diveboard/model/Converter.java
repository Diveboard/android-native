package com.diveboard.model;

public class				Converter
{
	private static Double	_KmFt = 3.2808;
	private static Double	_KgLbs = 2.2046;
	private static Double	_BarPsi = 14.5137;
	
	public static Integer	convert(final Integer value, final Units.Distance from, final Units.Distance to)
	{
		switch (from)
		{
			case KM:
				if (to == Units.Distance.FT)
					return (int) (value * _KmFt);
				break ;
			case FT:
				if (to == Units.Distance.KM)
					return (int) (value / _KmFt);
				break ;
		}
		return 1;
	}
	
	public static Double	convert(final Double value, final Units.Distance from, final Units.Distance to)
	{
		switch (from)
		{
			case KM:
				if (to == Units.Distance.FT)
					return (Double) (value * _KmFt);
				break ;
			case FT:
				if (to == Units.Distance.KM)
					return (Double) (value / _KmFt);
				break ;
		}
		return 1.0;
	}
	
	public static Integer	convert(final Integer value, final Units.Weight from, final Units.Weight to)
	{
		switch (from)
		{
			case KG:
				if (to == Units.Weight.LBS)
					return (int) (value * _KgLbs);
				break ;
			case LBS:
				if (to == Units.Weight.KG)
					return (int) (value / _KgLbs);
				break ;
		}
		return 1;
	}
	
	public static Double	convert(final Double value, final Units.Weight from, final Units.Weight to)
	{
		switch (from)
		{
			case KG:
				if (to == Units.Weight.LBS)
					return (Double) (value * _KgLbs);
				break ;
			case LBS:
				if (to == Units.Weight.KG)
					return (Double) (value / _KgLbs);
				break ;
		}
		return 1.0;
	}
	
	public static Integer	convert(final Integer value, final Units.Temperature from, final Units.Temperature to)
	{
		switch (from)
		{
			case C:
				if (to == Units.Temperature.F)
					return (int) ((value * 1.8) + 32);
				break ;
			case F:
				if (to == Units.Temperature.C)
					return (int) ((value - 32) / 1.8);
				break ;
		}
		return 1;
	}
	
	public static Double	convert(final Double value, final Units.Temperature from, final Units.Temperature to)
	{
		switch (from)
		{
			case C:
				if (to == Units.Temperature.F)
					return (Double) ((value * 1.8) + 32);
				break ;
			case F:
				if (to == Units.Temperature.C)
					return (Double) ((value - 32) / 1.8);
				break ;
		}
		return 1.0;
	}
	
	public static Integer	convert(final Integer value, final Units.Pressure from, final Units.Pressure to)
	{
		switch (from)
		{
			case BAR:
				if (to == Units.Pressure.PSI)
					return (int) (value * _BarPsi);
				break ;
			case PSI:
				if (to == Units.Pressure.BAR)
					return (int) (value / _BarPsi);
				break ;
		}
		return 1;
	}
	
	public static Double	convert(final Double value, final Units.Pressure from, final Units.Pressure to)
	{
		switch (from)
		{
			case BAR:
				if (to == Units.Pressure.PSI)
					return (Double) (value * _BarPsi);
				break ;
			case PSI:
				if (to == Units.Pressure.BAR)
					return (Double) (value / _BarPsi);
				break ;
		}
		return 1.0;
	}
}
