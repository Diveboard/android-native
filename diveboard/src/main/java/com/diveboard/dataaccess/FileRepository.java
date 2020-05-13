package com.diveboard.dataaccess;

import android.content.Context;
import android.os.AsyncTask;

import com.diveboard.util.ResponseCallback;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.diveboard.mobile.ApplicationController.getGson;

public abstract class FileRepository<T> {
    protected final File file;
    private T cached;

    FileRepository(Context context) {
        file = new File(context.getCacheDir(), getFileName());
    }

    protected abstract String getFileName();

    protected abstract Class getClazz();

    public void save(T data) {
        cached = data;
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(getGson().toJson(data).getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void getAsync(ResponseCallback<T> callback) {
        ReadAsyncTask task = new ReadAsyncTask(callback);
        task.execute();
    }

    public void cleanUp() {
        cached = null;
        if (file.exists()) {
            file.delete();
        }
    }

    private class ReadAsyncTask extends AsyncTask<Void, Void, T> {
        private ResponseCallback<T> callback;

        ReadAsyncTask(ResponseCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(T t) {
            if (t == null) {
                callback.error(new RuntimeException("No data in storage file"));
            } else {
                callback.success(t);
            }
        }

        @Override
        protected T doInBackground(Void... voids) {
            if (cached != null) {
                return cached;
            }
            if (!file.exists()) {
                return null;
            }
            FileInputStream stream = null;
            byte[] bytes = new byte[(int) file.length()];
            try {
                stream = new FileInputStream(file);
                stream.read(bytes);
                cached = (T) getGson().fromJson(new String(bytes), getClazz());
            } catch (FileNotFoundException e) {
                callback.error(e);
                return null;
            } catch (IOException e) {
                callback.error(e);
                return null;
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        callback.error(e);
                        return null;
                    }
                }
            }
            return cached;
        }
    }
}
