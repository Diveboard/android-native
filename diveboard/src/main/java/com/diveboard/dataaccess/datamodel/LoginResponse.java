package com.diveboard.dataaccess.datamodel;

import java.util.Date;

//not extendable by ResponseBase, different formats of response, no "result" field
public class LoginResponse {
    public boolean success;
    public String token;
    public User user;
    //TODO:!!check that this field is returned by the API
    public String message;
    public Date expiration;
}
