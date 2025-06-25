package com.facebook.react.bridge;

public interface WritableMap {
    void putString(String key, String value);
    void putInt(String key, int value);
    void putDouble(String key, double value);
    void putBoolean(String key, boolean value);
    void putMap(String key, WritableMap value);
    void putArray(String key, Object value);
    String getString(String key);
}