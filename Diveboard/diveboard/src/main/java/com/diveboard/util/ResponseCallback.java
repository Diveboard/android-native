package com.diveboard.util;

public interface ResponseCallback<TSuccess, TError> {
    void success(TSuccess data);

    void error(TError error);

    class Empty<TSuccess, TError> implements ResponseCallback<TSuccess, TError> {

        @Override
        public void success(TSuccess data) {

        }

        @Override
        public void error(TError tError) {

        }
    }
}
