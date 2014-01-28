package com.diveboard.mobile.editdive;

public class					EditOption
{
	private String				mTitle;
	private String				mValue;
	private Integer				mType;
	
	public						EditOption(String title, String value)
	{
		mTitle = title;
		mValue = value;
		mType = 0;
	}
	
	public						EditOption(String title, String value, Integer type)
	{
		mTitle = title;
		mValue = value;
		mType = type;
	}
	
	public String				getTitle()
	{
		return mTitle;
	}
	
	public String				getValue()
	{
		return mValue;
	}
	
	public void					setValue(String value)
	{
		mValue = value;
	}
	
	public Integer				getType()
	{
		return mType;
	}
}
