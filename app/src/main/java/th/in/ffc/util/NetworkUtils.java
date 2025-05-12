package th.in.ffc.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * คลาสสำหรับตรวจสอบสถานะการเชื่อมต่อเครือข่ายในแอปพลิเคชัน
 */
public class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    /**
     * ตรวจสอบว่าอุปกรณ์เชื่อมต่อกับอินเทอร์เน็ตหรือไม่
     *
     * @param context Context ของแอปพลิเคชัน
     * @return true หากเชื่อมต่ออินเทอร์เน็ตได้, false หากไม่มีการเชื่อมต่อ
     */
    public static boolean isInternetAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        // สำหรับ Android 10 (API 29) ขึ้นไป
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
        }

        // สำหรับ Android 9 (API 28) และต่ำกว่า
        else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    /**
     * แสดง Dialog เมื่อไม่มีการเชื่อมต่ออินเทอร์เน็ต
     *
     * @param context Context ของแอปพลิเคชัน
     */
    public static void showNoInternetDialog(Context context) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("ไม่มีการเชื่อมต่ออินเทอร์เน็ต");
        builder.setMessage("กรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ตของท่านแล้วลองใหม่อีกครั้ง");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("ตกลง", null);
        builder.setNeutralButton("ตั้งค่าเครือข่าย", (dialog, which) -> {
            // เปิดหน้าตั้งค่าเครือข่าย
            context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        });
        builder.show();
    }



    /**
     * ตรวจสอบประเภทการเชื่อมต่อที่ใช้งานอยู่
     *
     * @param context Context ของแอปพลิเคชัน
     * @return ประเภทการเชื่อมต่อ (WIFI, MOBILE, ETHERNET, VPN, BLUETOOTH, หรือ NOT_CONNECTED)
     */
    public static ConnectionType getConnectionType(Context context) {
        if (context == null) return ConnectionType.NOT_CONNECTED;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return ConnectionType.NOT_CONNECTED;

        // สำหรับ Android 10 (API 29) ขึ้นไป
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return ConnectionType.NOT_CONNECTED;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) return ConnectionType.NOT_CONNECTED;

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return ConnectionType.WIFI;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return ConnectionType.MOBILE;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return ConnectionType.ETHERNET;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return ConnectionType.VPN;
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
                return ConnectionType.BLUETOOTH;
            }

            return ConnectionType.OTHER;
        }
        // สำหรับ Android 9 (API 28) และต่ำกว่า
        else {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isConnected()) {
                return ConnectionType.NOT_CONNECTED;
            }

            int type = activeNetwork.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return ConnectionType.WIFI;
            }

            if (type == ConnectivityManager.TYPE_MOBILE) {
                return ConnectionType.MOBILE;
            }

            if (type == ConnectivityManager.TYPE_ETHERNET) {
                return ConnectionType.ETHERNET;
            }

            if (type == ConnectivityManager.TYPE_VPN) {
                return ConnectionType.VPN;
            }

            return ConnectionType.OTHER;
        }
    }

    /**
     * ตรวจสอบว่าสามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้จริงหรือไม่
     *
     * @param serverUrl URL ของเซิร์ฟเวอร์ที่ต้องการตรวจสอบ
     * @param timeout ระยะเวลาหมดเวลาการเชื่อมต่อในมิลลิวินาที
     * @return true หากเชื่อมต่อเซิร์ฟเวอร์ได้, false หากไม่สามารถเชื่อมต่อได้
     */
    public static boolean checkServerConnection(String serverUrl, int timeout) {
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            connection.disconnect();
            return (responseCode >= 200 && responseCode < 400);
        } catch (IOException e) {
            Log.e(TAG, "Error checking server connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * ตรวจสอบการเชื่อมต่อเซิร์ฟเวอร์แบบ Async (สำหรับ Android 8 ขึ้นไป)
     *
     * @param context Context ของแอปพลิเคชัน
     * @param serverUrl URL ของเซิร์ฟเวอร์ที่ต้องการตรวจสอบ
     * @param timeout ระยะเวลาหมดเวลาการเชื่อมต่อในมิลลิวินาที
     * @param callback Callback ที่จะถูกเรียกเมื่อตรวจสอบเสร็จสิ้น
     */
    public static void checkServerConnectionAsync(Context context, String serverUrl, int timeout, Consumer<Boolean> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CompletableFuture.supplyAsync(() -> checkServerConnection(serverUrl, timeout))
                    .thenAccept(isConnected -> {
                        new Handler(Looper.getMainLooper()).post(() -> callback.accept(isConnected));
                    });
        } else {
            // สำหรับ Android 7 และต่ำกว่า
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                boolean isConnected = checkServerConnection(serverUrl, timeout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    handler.post(() -> callback.accept(isConnected));
                }
            });
        }
    }

    /**
     * แสดง Dialog เมื่อไม่มีการเชื่อมต่ออินเทอร์เน็ต
     *
     * @param context Context ของแอปพลิเคชัน
     * @param title หัวข้อของ Dialog
     * @param message ข้อความของ Dialog
     * @param showSettingsButton true เพื่อแสดงปุ่มไปยังการตั้งค่าเครือข่าย
     */
    public static void showNoInternetDialog(Context context, String title, String message, boolean showSettingsButton) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("ตกลง", null);

        if (showSettingsButton) {
            builder.setNeutralButton("ตั้งค่าเครือข่าย", (dialog, which) -> {
                context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            });
        }

        builder.show();
    }

    /**
     * ตรวจสอบการเชื่อมต่ออินเทอร์เน็ตและแสดง Dialog ถ้าไม่มีการเชื่อมต่อ
     *
     * @param context Context ของแอปพลิเคชัน
     * @return true หากเชื่อมต่ออินเทอร์เน็ตได้, false หากไม่มีการเชื่อมต่อ
     */
    public static boolean checkInternetAndShowDialog(Context context) {
        if (!isInternetAvailable(context)) {
            showNoInternetDialog(context);
            return false;
        }
        return true;
    }

    /**
     * ตรวจสอบการเชื่อมต่ออินเทอร์เน็ตและแสดง Toast ถ้าไม่มีการเชื่อมต่อ
     *
     * @param context Context ของแอปพลิเคชัน
     * @param message ข้อความที่ต้องการแสดงใน Toast
     * @return true หากเชื่อมต่ออินเทอร์เน็ตได้, false หากไม่มีการเชื่อมต่อ
     */
    public static boolean checkInternetAndShowToast(Context context, String message) {
        if (!isInternetAvailable(context)) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * ประเภทการเชื่อมต่อเครือข่าย
     */
    public enum ConnectionType {
        WIFI,
        MOBILE,
        ETHERNET,
        VPN,
        BLUETOOTH,
        OTHER,
        NOT_CONNECTED;

        /**
         * แปลงประเภทการเชื่อมต่อเป็นข้อความที่มนุษย์อ่านได้
         */
        public String getDisplayName() {
            switch (this) {
                case WIFI: return "Wi-Fi";
                case MOBILE: return "เครือข่ายมือถือ";
                case ETHERNET: return "อีเธอร์เน็ต";
                case VPN: return "VPN";
                case BLUETOOTH: return "บลูทูธ";
                case OTHER: return "การเชื่อมต่ออื่นๆ";
                case NOT_CONNECTED: default: return "ไม่มีการเชื่อมต่อ";
            }
        }
    }
}