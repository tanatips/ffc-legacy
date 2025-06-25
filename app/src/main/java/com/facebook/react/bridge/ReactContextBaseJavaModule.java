package com.facebook.react.bridge;

public abstract class ReactContextBaseJavaModule {
    protected ReactApplicationContext reactContext;

    public ReactContextBaseJavaModule(ReactApplicationContext reactContext) {
        this.reactContext = reactContext;
    }

    public abstract String getName();
}