package com.diveboard.dataaccess.datamodel;

import java.util.Date;

public class LoginResponse {
    public boolean success;
    public String token;
    public User user;
    public String message;
    public Date expiration;


}
