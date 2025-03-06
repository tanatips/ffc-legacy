package th.in.ffc.api.nhso;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
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
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.SfToken;

public class NhsoApiCaller {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    // Constructor รับ context
    public NhsoApiCaller(Context context) {
        this.mContext = context;
    }

    // Interface สำหรับ callback
    public interface RealPersonApiCallback {
        void onSuccess(String response);
        void onError(String errorMessage);
    }

    // Interface สำหรับ callback ของ AuthenCode
    public interface AuthenCodeCallback {
        void onSuccess(ApiResponse response);
        void onError(Exception e);
    }

    /**
     * เรียก API RealPerson จาก NHSO
     *
     * @param citizenId เลขบัตรประชาชน
     * @param callback callback สำหรับรับผลลัพธ์
     */
    public void getRealPersonInfo(String citizenId, RealPersonApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึง token จากฐานข้อมูล
                SfTokenDao tokenDao = new SfTokenDao(mContext);
                List<SfToken> allTokens = tokenDao.getAllTokens();
                String authToken = "34913796-e515-4b33-9656-6a2eb64ef569"; // default token

                if (!allTokens.isEmpty()) {
                    SfToken token = allTokens.get(0);
                    authToken = token.getTokenAuth();
                }

                // สร้าง URL จาก BuildConfig
                String apiUrl = BuildConfig.API_BASE_URL +
                        BuildConfig.API_ENDPOINT_REAL_PERSON +
                        "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID +
                        "&PID=" + citizenId;

                URL url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();

                // ตั้งค่า connection
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                // เชื่อมต่อและอ่านผลลัพธ์
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // อ่านข้อมูลจาก response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    final String responseData = response.toString();

                    // ส่งผลลัพธ์กลับผ่าน callback
                    mainHandler.post(() -> callback.onSuccess(responseData));
                } else {
                    // กรณีเกิดข้อผิดพลาด
                    BufferedReader reader;
                    if (conn.getErrorStream() != null) {
                        reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    }

                    StringBuilder errorResponse = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();

                    final String errorMessage = "รหัสข้อผิดพลาด: " + responseCode + "\n" + errorResponse.toString();

                    mainHandler.post(() -> callback.onError(errorMessage));
                }
            } catch (Exception e) {
                final String errorMsg = "เกิดข้อผิดพลาด: " + e.getMessage();
                mainHandler.post(() -> callback.onError(errorMsg));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * เรียก API AuthenCode จาก NHSO
     *
     * @param request ข้อมูลสำหรับขอ AuthenCode
     * @param callback callback สำหรับรับผลลัพธ์
     */
    public void getAuthenCode(AuthenCodeRequest request, AuthenCodeCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // ดึง token จากฐานข้อมูล
                SfTokenDao tokenDao = new SfTokenDao(mContext);
                List<SfToken> allTokens = tokenDao.getAllTokens();
                String authToken = "Bearer 34913796-e515-4b33-9656-6a2eb64ef569"; // default token

                if (!allTokens.isEmpty()) {
                    SfToken token = allTokens.get(0);
                    authToken = "Bearer " + token.getTokenAuth();
                }

                // สร้าง URL จาก BuildConfig
                String apiUrl = BuildConfig.API_AUTHENCODE_URL;

                URL url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.addRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Content-Type", "application/json");
                conn.addRequestProperty("Authorization", authToken);
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                String jsonInputString = new Gson().toJson(request);
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(jsonInputString.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String response = in.lines().collect(Collectors.joining());
                        ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                        mainHandler.post(() -> callback.onSuccess(apiResponse));
                    }
                } else {
                    // กรณีเกิดข้อผิดพลาด
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                        String errorResponse = reader.lines().collect(Collectors.joining());
                        final Exception exception = new Exception("HTTP error code: " + responseCode + "\n" + errorResponse);
                        mainHandler.post(() -> callback.onError(exception));
                    }
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * เรียก API RealPerson พร้อมทดสอบการเชื่อมต่อ
     * สำหรับใช้ทดสอบ token ในหน้า settings
     *
     * @param citizenId เลขบัตรประชาชน
     * @param token token ที่ต้องการทดสอบ
     * @param callback callback สำหรับรับผลลัพธ์
     */
    public void testRealPersonApi(String citizenId, String token, RealPersonApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                // สร้าง URL จาก BuildConfig
                String apiUrl = BuildConfig.API_BASE_URL +
                        BuildConfig.API_ENDPOINT_REAL_PERSON +
                        "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID +
                        "&PID=" + citizenId;

                URL url = new URL(apiUrl);
                conn = (HttpURLConnection) url.openConnection();

                // ตั้งค่า connection
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setConnectTimeout(BuildConfig.API_TIMEOUT);
                conn.setReadTimeout(BuildConfig.API_TIMEOUT);

                // เชื่อมต่อและอ่านผลลัพธ์
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // อ่านข้อมูลจาก response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    final String responseData = response.toString();

                    // ส่งผลลัพธ์กลับผ่าน callback
                    mainHandler.post(() -> callback.onSuccess(formatResponseData(responseData)));
                } else {
                    // กรณีเกิดข้อผิดพลาด
                    BufferedReader reader;
                    if (conn.getErrorStream() != null) {
                        reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    }

                    StringBuilder errorResponse = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();

                    final String errorMessage = "รหัสข้อผิดพลาด: " + responseCode + "\n" + errorResponse.toString();

                    mainHandler.post(() -> callback.onError(errorMessage));
                }
            } catch (Exception e) {
                final String errorMsg = "เกิดข้อผิดพลาด: " + e.getMessage();
                mainHandler.post(() -> callback.onError(errorMsg));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
    private String formatResponseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            StringBuilder formattedData = new StringBuilder();

            // ข้อมูลส่วนบุคคล
            formattedData.append("ข้อมูลส่วนตัว\n");
            formattedData.append("-----------------------------------------\n");

            if (jsonObject.has("pid")) {
                formattedData.append("เลขประจำตัวประชาชน: ").append(jsonObject.getString("pid")).append("\n");
            }

            if (jsonObject.has("fullName")) {
                formattedData.append("ชื่อ-นามสกุล: ").append(jsonObject.getString("fullName")).append("\n");
            }

            if (jsonObject.has("sex")) {
                formattedData.append("เพศ: ").append(jsonObject.getString("sex")).append("\n");
            }

            if (jsonObject.has("age")) {
                formattedData.append("อายุ: ").append(jsonObject.getString("age")).append("\n");
            }

            if (jsonObject.has("birthDate")) {
                formattedData.append("วันเกิด: ").append(jsonObject.getString("birthDate")).append("\n");
            }

            if (jsonObject.has("nationDescription")) {
                formattedData.append("สัญชาติ: ").append(jsonObject.getString("nationDescription")).append("\n");
            }

            if (jsonObject.has("provinceName")) {
                formattedData.append("จังหวัด: ").append(jsonObject.getString("provinceName")).append("\n");
            }

            // ข้อมูลสิทธิ์การรักษา
            formattedData.append("ข้อมูลสิทธิ์การรักษา\n");
            formattedData.append("-----------------------------------------\n");

            if (jsonObject.has("mainInscl")) {
                formattedData.append("สิทธิหลัก: ").append(jsonObject.getString("mainInscl")).append("\n");
            }

            if (jsonObject.has("subInscl")) {
                formattedData.append("สิทธิย่อย: ").append(jsonObject.getString("subInscl")).append("\n");
            }

            // ข้อมูลสถานพยาบาล
            formattedData.append("ข้อมูลสถานพยาบาล\n");
            formattedData.append("-----------------------------------------\n");

            if (jsonObject.has("hospMain")) {
                formattedData.append("สถานพยาบาลหลัก: ").append(jsonObject.getString("hospMain")).append("\n");
            }

            if (jsonObject.has("hospSub")) {
                formattedData.append("สถานพยาบาลรอง: ").append(jsonObject.getString("hospSub")).append("\n");
            }

            if (jsonObject.has("hospMainOp")) {
                formattedData.append("สถานพยาบาลประจำ: ").append(jsonObject.getString("hospMainOp")).append("\n");
            }

            return formattedData.toString();
        } catch (JSONException e) {
            // กรณีไม่สามารถแปลงเป็น JSON ได้ ให้แสดงข้อมูลเดิม
            Log.e("API_FORMAT", "Error formatting JSON: " + e.getMessage());
            return response;
        }
    }
}