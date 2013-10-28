package com.diveboard.mobile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * This Fragment manages a single background task and retains 
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {
	/**
	 * Callback interface through which the fragment will report the
	 * task's progress and results back to the Activity.
	 */
	static interface TaskCallbacks {
		void onPreExecute();
		void onCancelled();
		void onPostExecute(final Boolean success);
	}

	private TaskCallbacks mCallbacks;
	private LoadDataTask mLoadDataTask;
	private boolean mTaskEnded = false;
	
	/**
	 * Hold a reference to the parent Activity so we can report the
	 * task's current progress and results. The Android framework 
	 * will pass us a reference to the newly created Activity after 
	 * each configuration change.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//Retain this fragment across configuration changes
		setRetainInstance(true);
		
		//Create and execute the background task
		mLoadDataTask = new LoadDataTask();
		mLoadDataTask.execute();
	}
	
	/**
	 * Set the callback to null so we don't accidentally leak the 
	 * Activity instance.
	 */
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallbacks = null;
		if (mTaskEnded == false)
		{
			ApplicationController AC = (ApplicationController) getActivity().getApplicationContext();
			AC.setModel(null);
		}
	}
	
	/**
	 * Represents an asynchronous task used to load data and
	 * proxies progress updates and results back to the Activity.
	 *
	 * Note that we need to check if the callbacks are null in each
	 * method in case they are invoked after the Activity's and
	 * Fragment's onDestroy() method have been called.
	 */
	private class LoadDataTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if (getActivity() != null)
			{
				ApplicationController AC = (ApplicationController) getActivity().getApplicationContext();
				AC.getModel().loadData();
				return true;
			}
			return false;
		}

		@Override
		protected void onPreExecute()
		{
			if (mCallbacks != null)
			{
				mCallbacks.onPreExecute();
			}
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if (mCallbacks != null)
			{
				mCallbacks.onPostExecute(success);
				mTaskEnded = true;
			}
			
		}
		
		@Override
		protected void onCancelled() {
			if (mCallbacks != null)
			{
				mCallbacks.onCancelled();
			}
		}
	}
}
