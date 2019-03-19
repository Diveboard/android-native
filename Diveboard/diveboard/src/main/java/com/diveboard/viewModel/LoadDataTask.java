package com.diveboard.viewModel;

import android.os.AsyncTask;

import com.diveboard.model.DiveboardModel;
import com.diveboard.util.Callback;

public class LoadDataTask extends AsyncTask<Void, Void, Boolean> {

    private final DiveboardModel model;
    private Callback<Boolean> callbacks;

    public LoadDataTask(DiveboardModel model, Callback<Boolean> callbacks) {
        this.model = model;
        this.callbacks = callbacks;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (model == null) {
            return false;
        }
        model.loadData();
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (callbacks != null) {
            callbacks.execute(success);
        }
    }
}
