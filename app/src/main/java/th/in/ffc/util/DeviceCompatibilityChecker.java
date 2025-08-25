package th.in.ffc.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Environment;
import th.in.ffc.util.Log;

import java.io.File;

public class DeviceCompatibilityChecker {

    private static final String TAG = "DeviceCompatibilityChecker";

    public static class CompatibilityInfo {
        public boolean canAccessLegacyPath;
        public boolean hasLegacyStorage;
        public boolean needsMigration;
        public String deviceInfo;
        public String recommendation;
    }

    public static CompatibilityInfo checkDeviceCompatibility(Context context) {
        CompatibilityInfo info = new CompatibilityInfo();

        // Device info
        info.deviceInfo = String.format("Brand: %s, Model: %s, Android: %d, Build: %s",
                Build.BRAND, Build.MODEL, Build.VERSION.SDK_INT, Build.TYPE);

        Log.d(TAG, "=== DEVICE COMPATIBILITY CHECK ===");
        Log.d(TAG, info.deviceInfo);

        // ตรวจสอบ Legacy Storage
        info.hasLegacyStorage = checkLegacyStorage(context);
        Log.d(TAG, "Has Legacy Storage: " + info.hasLegacyStorage);

        // ตรวจสอบการเข้าถึง legacy path
        info.canAccessLegacyPath = checkLegacyPathAccess();
        Log.d(TAG, "Can Access Legacy Path: " + info.canAccessLegacyPath);

        // ตรวจสอบว่าต้อง migrate หรือไม่
        info.needsMigration = checkNeedsMigration(context);
        Log.d(TAG, "Needs Migration: " + info.needsMigration);

        // คำแนะนำ
        info.recommendation = getRecommendation(info);
        Log.d(TAG, "Recommendation: " + info.recommendation);

        return info;
    }

    private static boolean checkLegacyStorage(Context context) {
        try {
            ApplicationInfo appInfo = context.getApplicationInfo();
            // ตรวจสอบว่ามี requestLegacyExternalStorage
            return (appInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkLegacyPathAccess() {
        try {
            File legacyPath = new File("/sdcard/Android/data/th.in.ffc/databases/");
            return legacyPath.exists() && legacyPath.canRead();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkNeedsMigration(Context context) {
        // มีไฟล์ในที่เก่าแต่ไม่มีในที่ใหม่
        File oldDir = new File("/sdcard/Android/data/th.in.ffc/databases/");
        File newDir = new File(context.getExternalFilesDir(null), "databases");

        File oldUserDb = new File(oldDir, "uJHCIS.db");
        File oldDataDb = new File(oldDir, "mJHCIS.sdb");
        File newUserDb = new File(newDir, "uJHCIS.db");
        File newDataDb = new File(newDir, "mJHCIS.sdb");

        boolean hasOldFiles = (oldUserDb.exists() && oldUserDb.canRead()) ||
                (oldDataDb.exists() && oldDataDb.canRead());
        boolean hasNewFiles = newUserDb.exists() && newDataDb.exists();

        return hasOldFiles && !hasNewFiles;
    }

    private static String getRecommendation(CompatibilityInfo info) {
        if (info.canAccessLegacyPath && info.hasLegacyStorage) {
            return "COMPATIBLE - Device can use legacy storage paths";
        } else if (info.needsMigration) {
            return "MIGRATE - Files need to be moved to new location";
        } else if (info.canAccessLegacyPath) {
            return "PARTIALLY_COMPATIBLE - Can access but may break in future updates";
        } else {
            return "INCOMPATIBLE - Must use new storage paths only";
        }
    }

    /**
     * ตรวจสอบว่าเป็น OEM ที่อาจมีพฤติกรรมต่างออกไป
     */
    public static boolean isProblemOEM() {
        String brand = Build.BRAND.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();

        // OEMs ที่มักมีปัญหา Scoped Storage
        return manufacturer.contains("google") ||  // Pixel - เข้มงวดตาม stock
                brand.contains("oneplus") ||        // OnePlus - เข้มงวด
                (Build.VERSION.SDK_INT >= 34 &&     // Android 14+ ทุกยี่ห้อเข้มงวดขึ้น
                        !manufacturer.contains("samsung") &&
                        !manufacturer.contains("xiaomi"));
    }

    /**
     * แนะนำ strategy สำหรับแต่ละ device type
     */
    public static String getStorageStrategy(Context context) {
        CompatibilityInfo info = checkDeviceCompatibility(context);

        if (Build.VERSION.SDK_INT >= 34) { // Android 14+
            return "MODERN_STORAGE_ONLY - Use getExternalFilesDir() exclusively";
        } else if (info.canAccessLegacyPath) {
            return "HYBRID - Try legacy first, fallback to modern";
        } else {
            return "MODERN_STORAGE_REQUIRED - Use getExternalFilesDir() only";
        }
    }

    /**
     * Log detailed device information
     */
    public static void logDeviceDetails(Context context) {
        Log.d(TAG, "=== DETAILED DEVICE INFO ===");
        Log.d(TAG, "Brand: " + Build.BRAND);
        Log.d(TAG, "Manufacturer: " + Build.MANUFACTURER);
        Log.d(TAG, "Model: " + Build.MODEL);
        Log.d(TAG, "Android Version: " + Build.VERSION.RELEASE);
        Log.d(TAG, "SDK Int: " + Build.VERSION.SDK_INT);
        Log.d(TAG, "Build Type: " + Build.TYPE);
        Log.d(TAG, "Build Tags: " + Build.TAGS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d(TAG, "External Storage Manager: " + Environment.isExternalStorageManager());
        }

        try {
            ApplicationInfo appInfo = context.getApplicationInfo();
            Log.d(TAG, "Target SDK: " + appInfo.targetSdkVersion);
            Log.d(TAG, "App Flags: " + appInfo.flags);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get app info: " + e.getMessage());
        }
    }
}