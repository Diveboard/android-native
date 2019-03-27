package com.diveboard.dataaccess.datamodel;

import java.util.Date;

public class LoginResponse {
    public boolean success;
    public String token;
    public User user;
    public String message;
    public Date expiration;

    public static class User {
        public Integer id;
        public String nickname;
    }
}
