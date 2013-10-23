package com.diveboard.mobile.editdive;

public class					EditOption
{
	private String				mTitle;
	private String				mValue;
	
	public						EditOption(String title, String value)
	{
		mTitle = title;
		mValue = value;
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
}
