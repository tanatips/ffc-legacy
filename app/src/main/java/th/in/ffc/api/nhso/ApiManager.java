package th.in.ffc.api.nhso;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import th.in.ffc.BuildConfig;
import th.in.ffc.app.form.screening.dao.SfApiUrlDao;
import th.in.ffc.app.form.screening.model.SfApiUrl;

/**
 * ตัวช่วยในการเรียกใช้ API ผ่าน URL ที่กำหนดไว้ในฐานข้อมูล
 */
public class ApiManager {

    private static final String TAG = "ApiManager";
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * เรียกใช้ API แบบ GET
     *
     * @param context Context
     * @param apiCode รหัส API ที่ต้องการเรียกใช้
     * @param token Token สำหรับ Authentication
     * @param citizenId เลขบัตรประชาชน (ถ้ามี)
     * @param callback Callback สำหรับรับผลลัพธ์
     */
    public static void callGetApi(final Context context, final String apiCode, final String token,
                                  final String citizenId, final ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึงข้อมูล API URL จาก Code
                SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);
                SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
                apiUrlDao.close();

                if (apiUrl == null) {
                    callbackOnMainThread(callback, false, "ไม่พบข้อมูล API URL สำหรับรหัส: " + apiCode);
                    return;
                }

                if (!"GET".equals(apiUrl.getMethod())) {
                    callbackOnMainThread(callback, false, "API นี้ไม่ใช่ประเภท GET");
                    return;
                }

                // สร้าง URL
                String apiUrlStr = apiUrl.getActiveUrl();
                if (apiUrl.getParams() != null && !apiUrl.getParams().isEmpty()) {
                    apiUrlStr += (apiUrlStr.contains("?") ? "&" : "?") + apiUrl.getParams();
                }

                // แทนที่ตัวแปรในพารามิเตอร์ถ้ามี
                if (citizenId != null && !citizenId.isEmpty()) {
                    apiUrlStr = apiUrlStr.replace("{PID}", citizenId);
                }

                Log.d(TAG, "Calling GET API: " + apiUrlStr);

                URL url = new URL(apiUrlStr);

                // เปิดการเชื่อมต่อ
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // กำหนด timeout
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                // กำหนด header สำหรับ Bearer Token ถ้ามี
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                // เชื่อมต่อ
                conn.connect();

                // อ่านผลลัพธ์
                int responseCode = conn.getResponseCode();
                processResponse(conn, responseCode, callback);

            } catch (Exception e) {
                Log.e(TAG, "Error calling GET API: " + apiCode, e);
                callbackOnMainThread(callback, false, "เกิดข้อผิดพลาด: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * เรียกใช้ API แบบ POST
     *
     * @param context Context
     * @param apiCode รหัส API ที่ต้องการเรียกใช้
     * @param token Token สำหรับ Authentication
     * @param jsonBody ข้อมูล JSON ที่ต้องการส่ง
     * @param callback Callback สำหรับรับผลลัพธ์
     */
    public static void callPostApi(final Context context, final String apiCode, final String token,
                                   final String jsonBody, final ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึงข้อมูล API URL จาก Code
                SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);
                SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
                apiUrlDao.close();

                if (apiUrl == null) {
                    callbackOnMainThread(callback, false, "ไม่พบข้อมูล API URL สำหรับรหัส: " + apiCode);
                    return;
                }

                if (!"POST".equals(apiUrl.getMethod())) {
                    callbackOnMainThread(callback, false, "API นี้ไม่ใช่ประเภท POST");
                    return;
                }

                // สร้าง URL
                String apiUrlStr = apiUrl.getActiveUrl();
                if (apiUrl.getParams() != null && !apiUrl.getParams().isEmpty()) {
                    apiUrlStr += (apiUrlStr.contains("?") ? "&" : "?") + apiUrl.getParams();
                }

                Log.d(TAG, "Calling POST API: " + apiUrlStr);
                Log.d(TAG, "Request Body: " + jsonBody);

                URL url = new URL(apiUrlStr);

                // เปิดการเชื่อมต่อ
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                // กำหนด timeout
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                // กำหนด header สำหรับ Bearer Token ถ้ามี
                if (token != null && !token.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

                // ส่งข้อมูล JSON
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // อ่านผลลัพธ์
                int responseCode = conn.getResponseCode();
                processResponse(conn, responseCode, callback);

            } catch (Exception e) {
                Log.e(TAG, "Error calling POST API: " + apiCode, e);
                callbackOnMainThread(callback, false, "เกิดข้อผิดพลาด: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * เปลี่ยนสภาพแวดล้อมของ API ทั้งหมด
     *
     * @param context Context
     * @param isProduction true หากต้องการใช้ Production, false หากต้องการใช้ Test
     * @return จำนวนรายการที่ถูกอัปเดต
     */
    public static int setAllEnvironment(Context context, boolean isProduction) {
        SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);
        int updated = apiUrlDao.updateAllEnvironment(isProduction ? SfApiUrl.ENV_PRODUCTION : SfApiUrl.ENV_TEST);
        apiUrlDao.close();
        return updated;
    }

    /**
     * ประมวลผลการตอบกลับจาก HTTP Connection
     */
    private static void processResponse(HttpURLConnection conn, int responseCode, ApiCallback callback) {
        try {
            if (responseCode >= 200 && responseCode < 300) {
                // อ่านข้อมูลจาก response
                String responseData = readInputStream(conn.getInputStream());

                // ส่งผลลัพธ์กลับไปที่ UI thread
                callbackOnMainThread(callback, true, responseData);
            } else {
                // กรณีเกิดข้อผิดพลาด
                InputStream errorStream = conn.getErrorStream();
                String errorResponse = errorStream != null ?
                        readInputStream(errorStream) : "HTTP Error: " + responseCode;

                callbackOnMainThread(callback, false, "HTTP " + responseCode + ": " + errorResponse);
            }
        } catch (Exception e) {
            callbackOnMainThread(callback, false, "เกิดข้อผิดพลาดในการอ่านผลลัพธ์: " + e.getMessage());
        }
    }

    /**
     * อ่านข้อมูลจาก InputStream เป็น String
     */
    private static String readInputStream(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
            response.append("\n");
        }
        reader.close();

        return response.toString().trim();
    }

    /**
     * ส่ง callback กลับไปที่ UI thread
     */
    private static void callbackOnMainThread(final ApiCallback callback, final boolean success, final String message) {
        mainHandler.post(() -> callback.onResult(success, message));
    }

    /**
     * Interface สำหรับรับผลลัพธ์การเรียกใช้ API
     */
    public interface ApiCallback {
        void onResult(boolean success, String message);
    }
}