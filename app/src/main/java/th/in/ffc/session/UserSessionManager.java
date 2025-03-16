package th.in.ffc.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import th.in.ffc.security.LoginActivity;

// UserSessionManager.java
public class UserSessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PCUCODE = "pcuCode";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    // Constructor
    public UserSessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    // เก็บข้อมูลผู้ใช้เมื่อ login สำเร็จ
    public void createLoginSession(String userId, String username,String pcuCode) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PCUCODE, pcuCode);
        editor.apply();
    }

    // ตรวจสอบการ login
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ดึงข้อมูลผู้ใช้
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_PCUCODE, pref.getString(KEY_PCUCODE, null));
        return user;
    }
    public String getUser(){
        return pref.getString(KEY_USERNAME, null);
    }
    public String getPcuCode(){
        return pref.getString(KEY_PCUCODE, null);
    }

    // ดึง token ของผู้ใช้
    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, null);
    }

    // ดึง username ของผู้ใช้
    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    // ล้างข้อมูล session เมื่อ logout
    public void logoutUser() {
        editor.clear();
        editor.apply();

        // หากต้องการไปที่หน้า login หลังจาก logout
        Intent i = new Intent(context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
