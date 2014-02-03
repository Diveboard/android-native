package com.diveboard.model;

public class					SafetyStop
{
	private Integer				_duration;
	private Integer				_depth;
	private String				_unit;
	
	public						SafetyStop(Integer depth, Integer duration, String unit)
	{
		this._duration = duration;
		this._depth = depth;
		this._unit = unit;
	}
	
	public void					setDuration(Integer duration)
	{
		this._duration = duration; 
	}
	
	public Integer				getDuration()
	{
		return this._duration;
	}
	
	public void					setDepth(Integer depth)
	{
		this._depth = depth;
	}
	
	public Integer				getDepth()
	{
		return this._depth;
	}
	
	public void					setUnit(String unit)
	{
		this._unit = unit;
	}
	
	public String				getUnit()
	{
		return this._unit;
	}
}
