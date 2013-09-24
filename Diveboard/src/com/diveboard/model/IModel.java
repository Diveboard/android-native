package com.diveboard.model;

/*
 * Interface IModel
 * Interface for every Model class of Diveboard Mobile Model API
 */
public interface					IModel
{
	/*
	 * Method save
	 * Save the modifications into offline files and Diveboard server,
	 * stack into edit list if online mode isn't available
	 */
	public void						save();
	
	/*
	 * Method delete
	 * Erase the model on offlines files and send delete request to Diveboard server,
	 * stack into edit list if online mode isn't available
	 */
	public void						delete();
}
