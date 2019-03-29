package com.diveboard.dataaccess;

import android.content.Context;

import com.diveboard.util.AsyncTask;
import com.diveboard.util.ResponseCallback;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
            Gson gson = new Gson();
            stream.write(gson.toJson(data).getBytes());
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

    public void getAsync(ResponseCallback<T, Exception> callback) {
        ReadAsyncTask task = new ReadAsyncTask(callback);
        task.execute();
    }

    private class ReadAsyncTask extends AsyncTask<Void, Void, T> {
        private ResponseCallback<T, Exception> callback;

        ReadAsyncTask(ResponseCallback<T, Exception> callback) {
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(T t) {
            callback.success(t);
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
                Gson gson = new Gson();
                cached = (T) gson.fromJson(new String(bytes), getClazz());
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
