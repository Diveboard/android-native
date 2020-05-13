package com.diveboard.dataaccess;

import com.diveboard.model.AuthenticationService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Cannot find UTF-8 encoder");
        }
    }

    public static String addCommonRequestArgs(String url, AuthenticationService authenticationService) {
        Map<String, String> params = RequestHelper.getCommonRequestArgs(authenticationService);
        for (String key : params.keySet()) {
            url += encode(key) + "=" + encode(params.get(key)) + "&";
        }
        return url.substring(0, url.length() - 1);
    }
}
