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
import java.util.List;
import java.util.stream.Collectors;

import th.in.ffc.BuildConfig;
import th.in.ffc.app.form.screening.dao.SfApiUrlDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.SfApiUrl;
import th.in.ffc.app.form.screening.model.SfToken;

/**
 * API Caller สำหรับส่งข้อมูล FS Data ไปยัง NHSO
 */
public class NHSOFSDataApiCaller {

    private static final String TAG = "NHSOFSDataApiCaller";
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    // API Code สำหรับเรียกใช้ API
    private static final String API_CODE_CREATE_FS_DATA = "CREATE_FS_DATA";

    /**
     * Constructor รับ context
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOFSDataApiCaller(Context context) {
        this.mContext = context;
    }

    /**
     * Interface สำหรับรับ callback จากการเรียก API
     */
    public interface FSDataApiCallback {
        void onSuccess(String response);
        void onError(String errorMessage, Exception e);
    }

    /**
     * ส่งข้อมูล FS Data ไปยัง NHSO โดยใช้ ApiManager
     * @param jsonData JSONObject ที่มีข้อมูลตามโครงสร้าง fsDatas
     * @param callback Callback เพื่อรับผลการทำงาน
     */
    public void sendFSData(JSONObject jsonData, FSDataApiCallback callback) {
        // ใช้ ApiManager เพื่อเรียก API ตาม API Code
        ApiManager.callPostApi(mContext, API_CODE_CREATE_FS_DATA, getLatestToken(false),
                jsonData.toString(), new ApiManager.ApiCallback() {
                    @Override
                    public void onResult(boolean success, String message) {
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message, new Exception(message));
                        }
                    }
                });
    }

    /**
     * ส่งข้อมูล FS Data จาก String JSON ไปยัง NHSO
     * @param jsonString JSON String ที่มีข้อมูลตามโครงสร้าง fsDatas
     * @param callback Callback เพื่อรับผลการทำงาน
     */
    public void sendFSDataFromString(String jsonString, FSDataApiCallback callback) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            sendFSData(jsonObject, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing JSON string", e);
            callback.onError("รูปแบบ JSON ไม่ถูกต้อง: " + e.getMessage(), e);
        }
    }

    /**
     * ส่งข้อมูล FS Data แบบดั้งเดิม (ไม่ใช้ ApiManager) - สำรองไว้ในกรณีที่ ApiManager มีปัญหา
     * @param jsonData JSONObject ที่มีข้อมูลตามโครงสร้าง fsDatas
     * @param callback Callback เพื่อรับผลการทำงาน
     */
    public void sendFSDataLegacy(JSONObject jsonData, FSDataApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึง URL API จากฐานข้อมูล
                SfApiUrlDao apiUrlDao = new SfApiUrlDao(mContext);
                SfApiUrl apiUrl = apiUrlDao.findByApiCode(API_CODE_CREATE_FS_DATA);
                apiUrlDao.close();

                if (apiUrl == null) {
                    mainHandler.post(() -> callback.onError(
                            "ไม่พบ API URL สำหรับ CREATE_FS_DATA ในฐานข้อมูล",
                            new Exception("API URL not found")));
                    return;
                }

                String apiUrlStr = apiUrl.getActiveUrl();

                // ดึง token จากฐานข้อมูล
                String authToken = getLatestToken(false);

                // สร้าง connection
                conn = (HttpURLConnection) new URL(apiUrlStr).openConnection();

                // กำหนดค่าต่างๆ
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);

                // กำหนด headers
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + authToken);

                // กำหนด timeout
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                // เตรียมข้อมูลและส่ง
                String jsonInputString = jsonData.toString();
                Log.d(TAG, "Sending JSON data: " + jsonInputString);
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                conn.setFixedLengthStreamingMode(input.length);

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
                        mainHandler.post(() -> callback.onSuccess(response));
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
                Log.e(TAG, "Error sending FS Data", e);
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
     * ดึง token ล่าสุดจากฐานข้อมูล
     *
     * @param isAuthToken true หากต้องการ token_auth, false หากต้องการ token_claim
     * @return token ล่าสุด
     */
    private String getLatestToken(boolean isAuthToken) {
        try {
            SfTokenDao tokenDao = new SfTokenDao(mContext);
            List<SfToken> tokens = tokenDao.getAllTokens();

            if (!tokens.isEmpty()) {
                SfToken token = tokens.get(0);
                return isAuthToken ? token.getTokenAuth() : token.getTokenClaim();
            }

            // ถ้าไม่พบ token ให้ใช้ค่า default
            return "34913796-e515-4b33-9656-6a2eb64ef569";
        } catch (Exception e) {
            Log.e(TAG, "Error getting token", e);
            return "34913796-e515-4b33-9656-6a2eb64ef569";
        }
    }
}