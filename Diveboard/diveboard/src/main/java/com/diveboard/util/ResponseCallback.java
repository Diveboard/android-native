package com.diveboard.util;

public interface ResponseCallback<TSuccess, TError> {
    void success(TSuccess data);

    void error(TError error);
}
