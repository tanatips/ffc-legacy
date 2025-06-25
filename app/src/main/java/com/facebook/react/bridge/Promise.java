package com.facebook.react.bridge;

public interface Promise {
    void resolve(Object value);
    void reject(String code, String message);
    void reject(String code, String message, Throwable throwable);
    void reject(String code, WritableMap userInfo);
    void reject(String code, Throwable throwable);
    void reject(String code, String message, WritableMap userInfo);
    void reject(String code, Throwable throwable, WritableMap userInfo);
    void reject(Throwable throwable);
    void reject(Throwable throwable, WritableMap userInfo);
    void reject(String message);
}
