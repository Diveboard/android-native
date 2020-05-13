package com.diveboard.util;

public interface Callback<T> {
    void execute(T data);

    class Empty<T> implements Callback<T> {
        @Override
        public void execute(T data) {

        }
    }
}
