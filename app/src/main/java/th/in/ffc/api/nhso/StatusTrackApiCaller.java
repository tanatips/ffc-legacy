package th.in.ffc.api.nhso;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import th.in.ffc.BuildConfig;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.SfToken;

/**
 * API Caller สำหรับส่งข้อมูล Status Track ไปยัง NHSO
 */
public class StatusTrackApiCaller {

    private static final String TAG = "StatusTrackApiCaller";
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    // API endpoint URL สำหรับ status-tracks
    private static final String STATUS_TRACK_ENDPOINT = BuildConfig.API_ฺBASE_STD_DATASET+"status-tracks";

    /**
     * Constructor รับ context
     * @param context Context ของแอปพลิเคชัน
     */
    public StatusTrackApiCaller(Context context) {
        this.mContext = context;
    }

    /**
     * Interface สำหรับรับ callback จากการเรียก API
     */
    public interface StatusTrackApiCallback {
        void onSuccess(String response);
        void onSuccess(List<StatusTrackResponse> responses);
        void onError(String errorMessage, Exception e);
    }

    /**
     * ส่งข้อมูล Status Track ไปยัง NHSO
     * @param jsonData JSONObject ที่มีข้อมูลตามโครงสร้าง fsTrackDatas
     * @param callback Callback เพื่อรับผลการทำงาน
     */
    public void sendStatusTrackData(JSONObject jsonData, StatusTrackApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึง token จากฐานข้อมูล
                SfTokenDao tokenDao = new SfTokenDao(mContext);
                List<SfToken> allTokens = tokenDao.getAllTokens();
                String authToken = null;

                if (!allTokens.isEmpty()) {
                    SfToken token = allTokens.get(0);
                    authToken = token.getTokenClaim();
                }

                if (authToken == null || authToken.isEmpty()) {
                    // ถ้าไม่มี token ให้ใช้ค่า default (ถ้ามีการกำหนดใน BuildConfig)
                    try {
                        authToken = BuildConfig.API_DEFAULT_TOKEN;
                    } catch (Exception e) {
                        authToken = "34913796-e515-4b33-9656-6a2eb64ef569"; // default fallback
                    }
                }

                // สร้าง connection
                conn = (HttpURLConnection) new URL(STATUS_TRACK_ENDPOINT).openConnection();
                // กำหนดค่าต่างๆ ทั้งหมดก่อนเขียนข้อมูล
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                // กำหนด headers ทั้งหมดในคราวเดียว
                conn.setRequestProperty("Accept", "*/*");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + authToken);

                // กำหนด timeout
                try {
                    int timeout = BuildConfig.API_TIMEOUT;
                    conn.setConnectTimeout(timeout);
                    conn.setReadTimeout(timeout);
                } catch (Exception e) {
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                }

                // เตรียมข้อมูลก่อนเขียน
                String jsonInputString = jsonData.toString();
                Log.d(TAG, "Sending Status Track data: " + jsonInputString);
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                conn.setFixedLengthStreamingMode(input.length);

                // ส่วนนี้ต้องเป็นส่วนสุดท้ายที่ทำกับ connection เพราะจะเริ่มการเชื่อมต่อ
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(input);
                    wr.flush();
                }
                // อ่านผลลัพธ์
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK ||
                        responseCode == HttpURLConnection.HTTP_CREATED ||
                        responseCode == HttpURLConnection.HTTP_ACCEPTED) {

                    // กรณีสำเร็จ
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String response = in.lines().collect(Collectors.joining());
                        Log.d(TAG, "API Response: " + response);
                        try {
                            // แปลงผลลัพธ์เป็น List<StatusTrackResponse>
                            List<StatusTrackResponse> responseObjects = StatusTrackResponse.fromJsonArray(response);
                            mainHandler.post(() -> {
                                callback.onSuccess(response); // ส่งกลับ raw response string
                                callback.onSuccess(responseObjects); // ส่งกลับ Object
                            });
                        } catch (Exception parseException) {
                            Log.e(TAG, "Error parsing response", parseException);
                            // กรณีแปลงข้อมูลไม่ได้ ส่งกลับเป็น raw string อย่างเดียว
                            mainHandler.post(() -> callback.onSuccess(response));
                        }
                    }
                } else {
                    // กรณีเกิดข้อผิดพลาด
                    String errorResponse = "";
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream()))) {
                        errorResponse = reader.lines().collect(Collectors.joining());
                    }

                    final String errorMessage = "HTTP error code: " + responseCode + "\n" + errorResponse;
                    Log.e(TAG, errorMessage);
                    Exception exception = new Exception("HTTP error: " + responseCode);
                    mainHandler.post(() -> callback.onError(errorMessage, exception));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending Status Track data", e);
                final String errorMsg = "เกิดข้อผิดพลาด: " + e.getMessage();
                mainHandler.post(() -> callback.onError(errorMsg, e));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * ส่งข้อมูล Status Track จาก String JSON ไปยัง NHSO
     * @param jsonString JSON String ที่มีข้อมูลตามโครงสร้าง fsTrackDatas
     * @param callback Callback เพื่อรับผลการทำงาน
     */
    public void sendStatusTrackDataFromString(String jsonString, StatusTrackApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            sendStatusTrackData(jsonObject, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON string", e);
            callback.onError("รูปแบบ JSON ไม่ถูกต้อง: " + e.getMessage(), e);
        }
    }

    /**
     * สร้าง JSON Object สำหรับข้อมูล Status Track
     * @param id ID ของข้อมูล
     * @param seq Sequence หรือ reference ID
     * @return JSONObject ที่มีโครงสร้างตามที่ API ต้องการ
     */
    public static JSONObject createStatusTrackData(String id, String seq) {
        try {
            JSONObject trackData = new JSONObject();
            trackData.put("id", id);
            trackData.put("seq", seq);

            JSONObject jsonData = new JSONObject();
            jsonData.put("fsTrackDatas", new org.json.JSONArray().put(trackData));

            return jsonData;
        } catch (Exception e) {
            Log.e(TAG, "Error creating status track JSON", e);
            return null;
        }
    }
}