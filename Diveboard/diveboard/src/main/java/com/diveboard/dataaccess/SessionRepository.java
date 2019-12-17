package com.diveboard.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;

import com.diveboard.dataaccess.datamodel.LoginResponse;
import com.diveboard.dataaccess.datamodel.StoredSession;

public class SessionRepository {
    private final SharedPreferences prefs;
    private Context context;

    //TODO: implement token refresh functionality
    public SessionRepository(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("com.diveboard.session", Context.MODE_PRIVATE);
    }

    public void saveSession(LoginResponse data) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("userId", data.user.id);
        editor.putString("token", data.token);
        editor.putLong("expiration", data.expiration.getTime());
        editor.apply();
    }

    public StoredSession getSession() {
        StoredSession result = new StoredSession();
        result.userId = prefs.getInt("userId", -1);
        result.expiration = prefs.getLong("expiration", -1);
        result.token = prefs.getString("token", null);
        if (!result.isActive()) {
            return null;
        }
        return result;
    }

    public void resetSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", null);
        editor.putInt("userId", -1);
        editor.apply();
    }
}
