package com.diveboard.dataaccess;

import com.diveboard.dataaccess.datamodel.ErrorItemResponse;

public class DiveboardApiException extends Exception {
    public DiveboardApiException(ErrorItemResponse[] errors) {
        super(getMyMessage(errors));
    }

    private static String getMyMessage(ErrorItemResponse[] errors) {
        if (errors != null && errors.length > 0) {
//TODO: include all the errors not the first one only
            return errors[0].message;
        } else {
            return "Cannot save dive. Unknown error";
        }
    }
}
