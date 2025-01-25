package th.in.ffc.api.nhso;

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
import java.util.stream.Collectors;

public class ApiCaller {

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
//    private  final Handler mainHandler = new Handler(Looper.getMainLooper());
    private  final String API_URL = "https://test.nhso.go.th/authencodeapi/api/AuthenCode";
    private  final String AUTH_TOKEN = "Bearer 34913796-e515-4b33-9656-6a2eb64ef569";

    public interface ApiCallback {
        void onSuccess(ApiResponse response);
        void onError(Exception e);
    }

    public static void getAuthenCode(AuthenCodeRequest request, ApiCallback callback) {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://test.nhso.go.th/authencodeapi/api/AuthenCode");
                conn = (HttpURLConnection) url.openConnection();

                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");

                conn.addRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Content-Type", "application/json");
                conn.addRequestProperty("Authorization", "Bearer 34913796-e515-4b33-9656-6a2eb64ef569");

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

