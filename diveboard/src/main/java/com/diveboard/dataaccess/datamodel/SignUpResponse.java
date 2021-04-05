package com.diveboard.dataaccess.datamodel;

import java.util.List;

public class SignUpResponse {
    public transient String fatalError;
    public boolean success;
    public List<SignUpError> errors;

    public class SignUpError {
        public String params;
        public String error;
    }
}
