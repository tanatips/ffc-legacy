package com.facebook.react.bridge;

import java.util.HashMap;
import java.util.Map;

public class WritableMapImpl implements WritableMap {
    private Map<String, Object> map = new HashMap<>();

    @Override
    public void putString(String key, String value) {
        map.put(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        map.put(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        map.put(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        map.put(key, value);
    }

    @Override
    public void putMap(String key, WritableMap value) {
        map.put(key, value);
    }

    @Override
    public void putArray(String key, Object value) {
        map.put(key, value);
    }

    @Override
    public String getString(String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}

