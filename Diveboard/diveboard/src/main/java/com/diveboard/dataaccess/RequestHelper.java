package com.diveboard.dataaccess;

import com.diveboard.model.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

public class RequestHelper {
    public static Map<String, String> getCommonRequestArgs(AuthenticationService authenticationService) {
        Map<String, String> args = new HashMap<>();
        args.put("auth_token", authenticationService.getSession().token);
        args.put("apikey", "xJ9GunZaNwLjP4Dz2jy3rdF");
        args.put("flavour", "mobile");
        return args;
    }
}
