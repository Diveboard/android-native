package com.diveboard.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import com.diveboard.config.AppConfig;
import com.diveboard.mobile.R;
import com.diveboard.viewModel.AsyncTaskCallback;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import static com.diveboard.util.Utils.copyInputStreamToFile;

public class SpotsDbUpdater implements AsyncTaskCallback {
    private Context context;
    private final String DB_PATH;
    private final String DB_NAME = "spots.db";
    private AsyncTaskCallback callback;

    public SpotsDbUpdater(Context context) {
        this.context = context;
        DB_PATH = this.context.getApplicationInfo().dataDir + "/databases/";
    }

    public void launchUpdate(AsyncTaskCallback callback) {
        DownloadSpotsAsyncTask task = new DownloadSpotsAsyncTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        this.callback = callback;
    }

    public File getSpotsFile() {
        File outputFile = new File(DB_PATH, DB_NAME);
        if (outputFile.exists()) {
            return outputFile;
        }
        return null;
    }

    @Override
    public void onPostExecute(Boolean success) {
        if (callback != null) {
            callback.onPostExecute(success);
        }
        if (success) {
            Toast toast = Toast.makeText(context, R.string.spots_db_updated, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(context, R.string.spots_db_update_error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private class DownloadSpotsAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private AsyncTaskCallback callback;

        public DownloadSpotsAsyncTask(AsyncTaskCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (callback != null) {
                callback.onPostExecute(success);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(AppConfig.SERVER_URL + "/assets/mobilespots.db.gz");
                InputStream mInput = url.openConnection().getInputStream();
                File folder = new File(DB_PATH);
                folder.mkdirs();

                File outputFile = new File(folder, DB_NAME);
                GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(mInput));
                copyInputStreamToFile(zis, outputFile);

                System.out.println("Spots downloaded successfully");
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}