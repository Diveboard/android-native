package com.diveboard.dataaccess.datamodel;

import java.util.Date;

public class StoredSession {
    public int userId;
    public String token;
    public long expiration;

    public boolean isActive() {
        return userId != -1 && token != null && expiration > new Date().getTime();
    }
}
