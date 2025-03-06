package th.in.ffc.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import th.in.ffc.provider.DbOpenHelper;

/**
 * ชุดฟังก์ชั่นสำหรับตรวจสอบความถูกต้องของ token
 */
public class TokenValidator {

    // Regular Expression สำหรับตรวจสอบรูปแบบของ UUID
    private static final String UUID_PATTERN = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    private static final Pattern pattern = Pattern.compile(UUID_PATTERN, Pattern.CASE_INSENSITIVE);

    /**
     * ตรวจสอบว่า token มีรูปแบบถูกต้องตามมาตรฐาน UUID หรือไม่
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @return true ถ้ารูปแบบถูกต้อง, false ถ้าไม่ถูกต้อง
     */
    public static boolean isValidUuidFormat(String token) {
        if (token == null) {
            return false;
        }

        Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }

    /**
     * ตรวจสอบว่า token สามารถแปลงเป็น UUID ได้หรือไม่
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @return true ถ้าแปลงเป็น UUID ได้, false ถ้าแปลงไม่ได้
     */
    public static boolean isValidUuid(String token) {
        try {
            UUID uuid = UUID.fromString(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * ตรวจสอบว่า token มีอยู่ในฐานข้อมูลหรือไม่
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @param context Context ของแอปพลิเคชัน
     * @return true ถ้ามีอยู่ในฐานข้อมูล, false ถ้าไม่มี
     */
    public static boolean isTokenExistsInDatabase(String token, Context context) {
        // สมมติว่ามีฐานข้อมูล SQLite
        SQLiteDatabase db = new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = null;

        try {
            // ตัวอย่างการค้นหาในตาราง token
            cursor = db.query("ffc_sf_token",
                    new String[]{"id"},
                    "token_auth = ?",
                    new String[]{token},
                    null, null, null);

            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * ตรวจสอบว่า token ยังไม่หมดอายุ
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @param context Context ของแอปพลิเคชัน
     * @return true ถ้ายังไม่หมดอายุ, false ถ้าหมดอายุแล้ว
     */
    public static boolean isTokenNotExpired(String token, Context context) {
        // สมมติว่ามีฐานข้อมูล SQLite ที่เก็บวันหมดอายุของ token
        SQLiteDatabase db = new DbOpenHelper(context).getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("ffc_sf_token",
                    new String[]{"expiration_date"},
                    "token_auth = ?",
                    new String[]{token},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                long expirationTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow("expiration_date"));
                long currentTimestamp = System.currentTimeMillis();

                return currentTimestamp < expirationTimestamp;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    /**
     * ตรวจสอบความถูกต้องของ token ด้วยการส่งคำขอไปยัง API
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @param callback callback สำหรับรับผลการตรวจสอบ
     */
    public static void validateTokenWithApi(String token, TokenValidationCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.example.com/validate-token?token=" + token);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        // อ่านผลลัพธ์จาก API
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // สมมติว่า API ส่งค่า {"valid": true} หรือ {"valid": false}
                        boolean isValid = response.toString().contains("\"valid\":true");

                        // ส่งผลลัพธ์กลับผ่าน callback
                        if (callback != null) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> callback.onResult(isValid));
                        }
                    } else {
                        if (callback != null) {
                            Handler mainHandler = new Handler(Looper.getMainLooper());
                            mainHandler.post(() -> callback.onResult(false));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> callback.onResult(false));
                    }
                }
            }
        }).start();
    }

    /**
     * ตรวจสอบ token แบบรวม
     *
     * @param token token ที่ต้องการตรวจสอบ
     * @param context Context ของแอปพลิเคชัน
     * @return ผลการตรวจสอบในรูปแบบของ TokenValidationResult
     */
    public static TokenValidationResult validateToken(String token, Context context) {
        TokenValidationResult result = new TokenValidationResult();

        // ตรวจสอบรูปแบบ
        result.isValidFormat = isValidUuidFormat(token);

        // ถ้ารูปแบบไม่ถูกต้อง ไม่ต้องตรวจสอบต่อ
        if (!result.isValidFormat) {
            result.errorMessage = "รูปแบบ token ไม่ถูกต้อง";
            return result;
        }

        // ตรวจสอบว่ามีอยู่ในฐานข้อมูล
        result.existsInDatabase = isTokenExistsInDatabase(token, context);

        if (!result.existsInDatabase) {
            result.errorMessage = "ไม่พบ token ในระบบ";
            return result;
        }

        // ตรวจสอบว่ายังไม่หมดอายุ
        result.isNotExpired = isTokenNotExpired(token, context);

        if (!result.isNotExpired) {
            result.errorMessage = "token หมดอายุแล้ว";
            return result;
        }

        // ผ่านการตรวจสอบทุกอย่าง
        result.isValid = true;
        return result;
    }

    /**
     * Interface สำหรับรับผลการตรวจสอบแบบ callback
     */
    public interface TokenValidationCallback {
        void onResult(boolean isValid);
    }

    /**
     * คลาสเก็บผลการตรวจสอบ token
     */
    public static class TokenValidationResult {
        public boolean isValid = false;
        public boolean isValidFormat = false;
        public boolean existsInDatabase = false;
        public boolean isNotExpired = false;
        public String errorMessage = "";
    }
}