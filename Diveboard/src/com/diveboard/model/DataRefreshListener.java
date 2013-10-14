package com.diveboard.model;

import java.util.EventListener;

public interface					DataRefreshListener extends EventListener
{
	void							onDataRefreshComplete();
}
