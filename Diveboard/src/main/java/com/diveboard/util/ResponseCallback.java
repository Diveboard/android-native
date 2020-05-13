package com.diveboard.util;

public interface ResponseCallback<TSuccess> {
    void success(TSuccess data);

    void error(Exception error);

    class Empty<TSuccess> implements ResponseCallback<TSuccess> {

        @Override
        public void success(TSuccess data) {
        }

        @Override
        public void error(Exception tError) {
        }
    }
}
