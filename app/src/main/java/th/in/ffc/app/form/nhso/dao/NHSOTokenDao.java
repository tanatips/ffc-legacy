package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import th.in.ffc.app.form.nhso.model.NHSOTokenInfo;
import th.in.ffc.provider.NHSOPatientProvider;
import th.in.ffc.provider.NHSOToken;

/**
 * Data Access Object สำหรับ Token การเข้าถึง NHSO API
 */
public class NHSOTokenDao {
    private static final String TAG = "NHSOTokenDao";
    private Context mContext;
    private SimpleDateFormat dateFormat;

    public NHSOTokenDao(Context context) {
        this.mContext = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    /**
     * ดึงข้อมูล Token ล่าสุด
     * @return ข้อมูล Token หรือ null ถ้าไม่พบ
     */
    public NHSOTokenInfo getLatestToken() {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOToken.CONTENT_URI;

        String[] projection = {
                NHSOToken.ID,
                NHSOToken.TOKEN_TYPE,
                NHSOToken.ACCESS_TOKEN,
                NHSOToken.REFRESH_TOKEN,
                NHSOToken.EXPIRES_IN,
                NHSOToken.CREATED_DATE,
                NHSOToken.UPDATED_DATE
        };

        String sortOrder = NHSOToken.ID + " DESC";
        Cursor cursor = resolver.query(uri, projection, null, null, sortOrder);
        NHSOTokenInfo token = null;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    token = cursorToToken(cursor);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing token data", e);
            } finally {
                cursor.close();
            }
        }

        return token;
    }

    /**
     * บันทึกข้อมูล Token ใหม่
     * @param token ข้อมูล Token ที่ต้องการบันทึก
     * @return รหัส ID ที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertToken(NHSOTokenInfo token) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = tokenToContentValues(token);

        // เพิ่มข้อมูลวันที่บันทึก
        values.put(NHSOToken.CREATED_DATE, dateFormat.format(new Date()));

        Uri resultUri = resolver.insert(NHSOToken.CONTENT_URI, values);
        if (resultUri != null) {
            return Long.parseLong(resultUri.getLastPathSegment());
        } else {
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูล Token
     * @param token ข้อมูล Token ที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateToken(NHSOTokenInfo token) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = tokenToContentValues(token);

        // เพิ่มข้อมูลวันที่อัปเดต
        values.put(NHSOToken.UPDATED_DATE, dateFormat.format(new Date()));

        String selection = NHSOToken.ID + " = ?";
        String[] selectionArgs = {String.valueOf(token.getId())};

        return resolver.update(NHSOToken.CONTENT_URI, values, selection, selectionArgs);
    }

    /**
     * ลบข้อมูล Token
     * @param id รหัส ID ที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteToken(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(NHSOToken.CONTENT_URI, String.valueOf(id));

        return resolver.delete(uri, null, null);
    }

    /**
     * ลบข้อมูล Token ทั้งหมด
     * @return จำนวนรายการที่ลบ
     */
    public int deleteAllTokens() {
        ContentResolver resolver = mContext.getContentResolver();
        return resolver.delete(NHSOToken.CONTENT_URI, null, null);
    }

    /**
     * ตรวจสอบว่า Token ยังไม่หมดอายุ
     * @param token ข้อมูล Token ที่ต้องการตรวจสอบ
     * @return true ถ้า Token ยังไม่หมดอายุ, false ถ้า Token หมดอายุแล้ว
     */
    public boolean isTokenValid(NHSOTokenInfo token) {
        if (token == null || token.getAccessToken() == null || token.getCreatedDate() == null) {
            return false;
        }

        try {
            Date createdDate = dateFormat.parse(token.getCreatedDate());
            if (createdDate == null) {
                return false;
            }

            // คำนวณเวลาหมดอายุ (created_date + expires_in)
            long expiryTimeMillis = createdDate.getTime() + (token.getExpiresIn() * 1000L);
            long currentTimeMillis = System.currentTimeMillis();

            // ตรวจสอบว่าเวลาปัจจุบันยังไม่เกินเวลาหมดอายุ
            return currentTimeMillis < expiryTimeMillis;
        } catch (Exception e) {
            Log.e(TAG, "Error checking token validity", e);
            return false;
        }
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOTokenInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูล Token
     */
    private NHSOTokenInfo cursorToToken(Cursor cursor) {
        NHSOTokenInfo token = new NHSOTokenInfo();

        token.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NHSOToken.ID)));
        token.setTokenType(cursor.getString(cursor.getColumnIndexOrThrow(NHSOToken.TOKEN_TYPE)));
        token.setAccessToken(cursor.getString(cursor.getColumnIndexOrThrow(NHSOToken.ACCESS_TOKEN)));

        int refreshTokenIndex = cursor.getColumnIndexOrThrow(NHSOToken.REFRESH_TOKEN);
        if (!cursor.isNull(refreshTokenIndex)) {
            token.setRefreshToken(cursor.getString(refreshTokenIndex));
        }

        token.setExpiresIn(cursor.getInt(cursor.getColumnIndexOrThrow(NHSOToken.EXPIRES_IN)));

        int createdDateIndex = cursor.getColumnIndexOrThrow(NHSOToken.CREATED_DATE);
        if (!cursor.isNull(createdDateIndex)) {
            token.setCreatedDate(cursor.getString(createdDateIndex));
        }

        int updatedDateIndex = cursor.getColumnIndexOrThrow(NHSOToken.UPDATED_DATE);
        if (!cursor.isNull(updatedDateIndex)) {
            token.setUpdatedDate(cursor.getString(updatedDateIndex));
        }

        return token;
    }

    /**
     * แปลงข้อมูลจาก NHSOTokenInfo เป็น ContentValues
     * @param token ข้อมูล Token
     * @return ContentValues สำหรับบันทึกลงฐานข้อมูล
     */
    private ContentValues tokenToContentValues(NHSOTokenInfo token) {
        ContentValues values = new ContentValues();

        values.put(NHSOToken.TOKEN_TYPE, token.getTokenType());
        values.put(NHSOToken.ACCESS_TOKEN, token.getAccessToken());

        if (token.getRefreshToken() != null) {
            values.put(NHSOToken.REFRESH_TOKEN, token.getRefreshToken());
        }

        values.put(NHSOToken.EXPIRES_IN, token.getExpiresIn());

        return values;
    }
}