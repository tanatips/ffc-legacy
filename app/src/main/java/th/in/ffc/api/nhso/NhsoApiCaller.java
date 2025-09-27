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
import th.in.ffc.app.form.screening.dao.SfApiUrlDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.SfApiUrl;
import th.in.ffc.app.form.screening.model.SfToken;

public class NhsoApiCaller {

    private static final String TAG = "NhsoApiCaller";
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context mContext;

    // API Code สำหรับเรียกใช้ API
    private static final String API_CODE_REAL_PERSON = "REAL_PERSON";
    private static final String API_CODE_AUTHEN_CODE = "AUTHEN_CODE";

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
        // ใช้ ApiManager เพื่อเรียก API ตาม API Code
        ApiManager.callGetApi(mContext, API_CODE_REAL_PERSON, getLatestToken(true),
                citizenId, new ApiManager.ApiCallback() {
                    @Override
                    public void onResult(boolean success, String message) {
                        if (success) {
                            callback.onSuccess(message);
                        } else {
                            callback.onError(message);
                        }
                    }
                });
    }

    /**
     * เรียก API AuthenCode จาก NHSO
     *
     * @param request ข้อมูลสำหรับขอ AuthenCode
     * @param callback callback สำหรับรับผลลัพธ์
     */
    public void getAuthenCode(AuthenCodeRequest request, AuthenCodeCallback callback) {
        // แปลง request เป็น JSON String
        String jsonBody = new Gson().toJson(request);

        // ใช้ ApiManager เพื่อเรียก API ตาม API Code
        ApiManager.callPostApi(mContext, API_CODE_AUTHEN_CODE, getLatestToken(true),
                jsonBody, new ApiManager.ApiCallback() {
                    @Override
                    public void onResult(boolean success, String message) {
                        if (success) {
                            try {
                                ApiResponse apiResponse = new Gson().fromJson(message, ApiResponse.class);
                                callback.onSuccess(apiResponse);
                            } catch (Exception e) {
                                callback.onError(new Exception("การแปลงข้อมูลล้มเหลว: " + e.getMessage()));
                            }
                        } else {
                            callback.onError(new Exception(message));
                        }
                    }
                });
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
                // ดึงข้อมูล API URL จากฐานข้อมูล
                SfApiUrlDao apiUrlDao = new SfApiUrlDao(mContext);
                SfApiUrl apiUrl = apiUrlDao.findByApiCode(API_CODE_REAL_PERSON);
                apiUrlDao.close();

                if (apiUrl == null) {
                    // ถ้าไม่พบ API URL ในฐานข้อมูล ใช้ค่าจาก BuildConfig
                    mainHandler.post(() -> callback.onError("ไม่พบ API URL สำหรับ REAL_PERSON ในฐานข้อมูล"));
                    return;
                }

                // สร้าง URL จากข้อมูลในฐานข้อมูล
                String apiUrlStr = apiUrl.getActiveUrl();
                if (apiUrl.getParams() != null && !apiUrl.getParams().isEmpty()) {
                    apiUrlStr += (apiUrlStr.contains("?") ? "&" : "?") + apiUrl.getParams();
                } else {
                    // ถ้าไม่มีพารามิเตอร์ในฐานข้อมูล ใช้ค่า default
                    apiUrlStr += "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID + "&PID=" + citizenId;
                }

                // ตรวจสอบและแทนที่ PID ด้วยค่าจริง
                apiUrlStr = apiUrlStr.replace("{PID}", citizenId);

                URL url = new URL(apiUrlStr);
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
            return  null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting token", e);
            return null;
        }
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

    public String extractInsuranceCode(String data) {
        String insuranceCode = null;

        // ค้นหาบรรทัดที่มีคำว่า "สิทธิหลัก:"
        String[] lines = data.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("สิทธิหลัก:")) {
                // แยกข้อความในวงเล็บ (UCS)
                int startIndex = line.indexOf("(") + 1;
                int endIndex = line.indexOf(")");

                if (startIndex > 0 && endIndex > startIndex) {
                    insuranceCode = line.substring(startIndex, endIndex);
                }
                break;
            }
        }

        return insuranceCode; // จะได้ค่า "UCS"
    }
}