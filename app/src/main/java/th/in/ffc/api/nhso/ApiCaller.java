package th.in.ffc.api.nhso;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.SfToken;

public class ApiCaller {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
//    private  final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final String API_URL = "https://test.nhso.go.th/authencodeapi/api/AuthenCode";
    private static final String AUTH_TOKEN = "Bearer 34913796-e515-4b33-9656-6a2eb64ef569";
    private static Context mContext;

    // เพิ่ม constructor รับ context
    public ApiCaller(Context context) {
        this.mContext = context;
    }
    public interface ApiCallback {
        void onSuccess(ApiResponse response);
        void onError(Exception e);
    }

    public static void  getAuthenCode(AuthenCodeRequest request, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                SfTokenDao tokenDao = new SfTokenDao(mContext);
                List<SfToken> allTokens = tokenDao.getAllTokens();
                String authToken = AUTH_TOKEN; // default token
                if (!allTokens.isEmpty()) {
                    SfToken token = allTokens.get(0);
                    authToken = "Bearer " + token.getTokenAuth();
                }

                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.addRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Content-Type", "application/json");
                conn.addRequestProperty("Authorization", authToken);

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
                    throw new Exception("HTTP error code: " + responseCode);
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
}

