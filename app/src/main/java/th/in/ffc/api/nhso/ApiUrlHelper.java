package th.in.ffc.api.nhso;

import android.content.Context;
import android.util.Log;

import th.in.ffc.app.form.screening.dao.SfApiUrlDao;

/**
 * Helper class สำหรับจัดการ API URLs ในฐานข้อมูล
 * ใช้สำหรับการตั้งค่าเริ่มต้นและการจัดการ API URLs
 */
public class ApiUrlHelper {

    private static final String TAG = "ApiUrlHelper";

    /**
     * ตั้งค่า API URLs เริ่มต้นทั้งหมด
     * เรียกใช้ใน Application.onCreate() หรือ MainActivity.onCreate()
     *
     * @param context Context ของแอปพลิเคชัน
     */
    public static void initializeAllApiUrls(Context context) {
        Log.i(TAG, "Starting API URLs initialization...");

        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            // ซิงค์ APIs ทั้งหมด (เพิ่มใหม่และอัปเดตที่มีอยู่)
            apiUrlDao.syncApis();

            // ปิดการเชื่อมต่อ
            apiUrlDao.close();

            Log.i(TAG, "API URLs initialization completed successfully");

            // แสดงการตั้งค่าปัจจุบัน
            logCurrentConfiguration(context);

        } catch (Exception e) {
            Log.e(TAG, "Error during API URLs initialization", e);
        }
    }

    /**
     * เปลี่ยน environment ของ API ทั้งหมดพร้อมกัน
     *
     * @param context Context ของแอปพลิเคชัน
     * @param useProduction true สำหรับ Production, false สำหรับ Test
     */
    public static void switchAllEnvironments(Context context, boolean useProduction) {
        String envName = useProduction ? "Production" : "Test";
        Log.i(TAG, "Switching all APIs to " + envName + " environment");

        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            // เปลี่ยน environment ทั้งหมด
            int updated = apiUrlDao.updateAllEnvironment(
                    useProduction ? th.in.ffc.app.form.screening.model.SfApiUrl.ENV_PRODUCTION
                            : th.in.ffc.app.form.screening.model.SfApiUrl.ENV_TEST
            );

            apiUrlDao.close();

            Log.i(TAG, "Successfully switched " + updated + " APIs to " + envName + " environment");

        } catch (Exception e) {
            Log.e(TAG, "Error switching environments", e);
        }
    }

    /**
     * เปลี่ยน environment ของ API เฉพาะ
     *
     * @param context Context ของแอปพลิเคชัน
     * @param apiCode รหัส API ที่ต้องการเปลี่ยน
     * @param useProduction true สำหรับ Production, false สำหรับ Test
     */
    public static void switchApiEnvironment(Context context, String apiCode, boolean useProduction) {
        String envName = useProduction ? "Production" : "Test";
        Log.i(TAG, "Switching " + apiCode + " to " + envName + " environment");

        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            int updated = apiUrlDao.updateEnvironment(apiCode,
                    useProduction ? th.in.ffc.app.form.screening.model.SfApiUrl.ENV_PRODUCTION
                            : th.in.ffc.app.form.screening.model.SfApiUrl.ENV_TEST
            );

            apiUrlDao.close();

            if (updated > 0) {
                Log.i(TAG, "Successfully switched " + apiCode + " to " + envName + " environment");
            } else {
                Log.w(TAG, "No API found with code: " + apiCode);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error switching " + apiCode + " environment", e);
        }
    }

    /**
     * แสดงการตั้งค่า API URLs ปัจจุบันทั้งหมด
     *
     * @param context Context ของแอปพลิเคชัน
     */
    public static void logCurrentConfiguration(Context context) {
        Log.i(TAG, "=== Current API Configuration ===");

        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            // รายการ API codes ทั้งหมด
            String[] apiCodes = {
                    "AUTHEN_CODE",
                    "CREATE_FS_DATA",
                    "REAL_PERSON",
                    "STATUS_TRACKS",
                    "STATUS_TRACKS_V2"
            };

            for (String apiCode : apiCodes) {
                th.in.ffc.app.form.screening.model.SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
                if (apiUrl != null) {
                    String env = apiUrl.getEnvType() == th.in.ffc.app.form.screening.model.SfApiUrl.ENV_PRODUCTION ? "PROD" : "TEST";
                    Log.i(TAG, String.format("%-18s | %-4s | %s",
                            apiCode, env, apiUrl.getActiveUrl()));
                } else {
                    Log.w(TAG, String.format("%-18s | NOT FOUND", apiCode));
                }
            }

            apiUrlDao.close();
            Log.i(TAG, "================================");

        } catch (Exception e) {
            Log.e(TAG, "Error logging current configuration", e);
        }
    }

    /**
     * ตรวจสอบว่า API URLs ทั้งหมดถูกตั้งค่าแล้วหรือไม่
     *
     * @param context Context ของแอปพลิเคชัน
     * @return true ถ้า APIs ทั้งหมดพร้อมใช้งาน
     */
    public static boolean areAllApisReady(Context context) {
        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            String[] requiredApis = {
                    "AUTHEN_CODE",
                    "CREATE_FS_DATA",
                    "REAL_PERSON",
                    "STATUS_TRACKS",
                    "STATUS_TRACKS_V2"
            };

            boolean allReady = true;
            for (String apiCode : requiredApis) {
                th.in.ffc.app.form.screening.model.SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
                if (apiUrl == null) {
                    Log.w(TAG, "Missing API: " + apiCode);
                    allReady = false;
                }
            }

            apiUrlDao.close();
            return allReady;

        } catch (Exception e) {
            Log.e(TAG, "Error checking APIs readiness", e);
            return false;
        }
    }

    /**
     * รีเซ็ต API URLs ทั้งหมด (ลบและสร้างใหม่)
     * ใช้สำหรับการ debug หรือ reset ระบบ
     *
     * @param context Context ของแอปพลิเคชัน
     */
    public static void resetAllApiUrls(Context context) {
        Log.w(TAG, "Resetting all API URLs...");

        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);

            // ลบ APIs ทั้งหมด (ไม่ใช่ตารางทั้งหมด แค่ข้อมูล)
            String[] apiCodes = {
                    "AUTHEN_CODE",
                    "CREATE_FS_DATA",
                    "REAL_PERSON",
                    "STATUS_TRACKS",
                    "STATUS_TRACKS_V2"
            };

            for (String apiCode : apiCodes) {
                th.in.ffc.app.form.screening.model.SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
                if (apiUrl != null) {
                    apiUrlDao.delete(apiUrl.getId());
                    Log.i(TAG, "Deleted API: " + apiCode);
                }
            }

            apiUrlDao.close();

            // สร้างใหม่
            initializeAllApiUrls(context);

            Log.i(TAG, "All API URLs have been reset successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error resetting API URLs", e);
        }
    }

    /**
     * ได้รับ URL ที่ใช้งานปัจจุบันของ API
     *
     * @param context Context ของแอปพลิเคชัน
     * @param apiCode รหัส API
     * @return URL ที่ใช้งานปัจจุบัน หรือ null ถ้าไม่พบ
     */
    public static String getCurrentApiUrl(Context context, String apiCode) {
        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);
            th.in.ffc.app.form.screening.model.SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
            apiUrlDao.close();

            return apiUrl != null ? apiUrl.getActiveUrl() : null;

        } catch (Exception e) {
            Log.e(TAG, "Error getting current URL for " + apiCode, e);
            return null;
        }
    }

    /**
     * ตรวจสอบว่า API อยู่ใน Production environment หรือไม่
     *
     * @param context Context ของแอปพลิเคชัน
     * @param apiCode รหัส API
     * @return true ถ้าอยู่ใน Production environment
     */
    public static boolean isProductionEnvironment(Context context, String apiCode) {
        try {
            SfApiUrlDao apiUrlDao = new SfApiUrlDao(context);
            th.in.ffc.app.form.screening.model.SfApiUrl apiUrl = apiUrlDao.findByApiCode(apiCode);
            apiUrlDao.close();

            return apiUrl != null &&
                    apiUrl.getEnvType() == th.in.ffc.app.form.screening.model.SfApiUrl.ENV_PRODUCTION;

        } catch (Exception e) {
            Log.e(TAG, "Error checking environment for " + apiCode, e);
            return false;
        }
    }
}