package th.in.ffc.util;

import android.content.Context;
import android.os.Build;
import th.in.ffc.util.Log;

import java.io.File;

/**
 * จัดการ Database Path แบบ Universal ที่ทำงานได้กับทุกเครื่อง Android 14
 */
public class UnifiedDatabaseManager {

    private static final String TAG = "UnifiedDatabaseManager";

    // Legacy paths (เครื่องเก่าที่ยังใช้ได้)
    private static final String LEGACY_USER_DB = "/sdcard/Android/data/th.in.ffc/databases/uJHCIS.db";
    private static final String LEGACY_DATA_DB = "/sdcard/Android/data/th.in.ffc/databases/mJHCIS.sdb";

    private Context context;
    private String userDbPath;
    private String dataDbPath;
    private boolean isUsingLegacyPath;

    public UnifiedDatabaseManager(Context context) {
        this.context = context;
        initializePaths();
    }

    /**
     * หา path ที่เหมาะสมสำหรับเครื่องนี้
     */
    private void initializePaths() {
        Log.d(TAG, "Initializing database paths for device...");

        // ลองใช้ modern path ก่อน (แนะนำสำหรับ Android 11+)
        File modernDir = new File(context.getExternalFilesDir(null), "databases");
        if (!modernDir.exists()) {
            modernDir.mkdirs();
        }

        File modernUserDb = new File(modernDir, "uJHCIS.db");
        File modernDataDb = new File(modernDir, "mJHCIS.sdb");

        // ตรวจสอบว่า modern path มีไฟล์หรือไม่
        if (modernUserDb.exists() && modernDataDb.exists()) {
            // ใช้ modern path
            userDbPath = modernUserDb.getAbsolutePath();
            dataDbPath = modernDataDb.getAbsolutePath();
            isUsingLegacyPath = false;
            Log.d(TAG, "Using MODERN storage paths");

        } else {
            // ตรวจสอบ legacy path
            File legacyUserDb = new File(LEGACY_USER_DB);
            File legacyDataDb = new File(LEGACY_DATA_DB);

            if (canUseLegacyPath(legacyUserDb, legacyDataDb)) {
                // ใช้ legacy path (เครื่องที่ยังเข้าถึงได้)
                userDbPath = LEGACY_USER_DB;
                dataDbPath = LEGACY_DATA_DB;
                isUsingLegacyPath = true;
                Log.d(TAG, "Using LEGACY storage paths (compatible device)");

                // ลอง migrate ไปที่ modern แต่ไม่บังคับ
                tryMigrateToModern(legacyUserDb, legacyDataDb, modernUserDb, modernDataDb);

            } else {
                // บังคับใช้ modern path
                userDbPath = modernUserDb.getAbsolutePath();
                dataDbPath = modernDataDb.getAbsolutePath();
                isUsingLegacyPath = false;
                Log.w(TAG, "FORCED to use MODERN storage paths - legacy not accessible");
            }
        }

        Log.d(TAG, "Final paths:");
        Log.d(TAG, "UserDB: " + userDbPath);
        Log.d(TAG, "DataDB: " + dataDbPath);
        Log.d(TAG, "Using Legacy: " + isUsingLegacyPath);
    }

    /**
     * ตรวจสอบว่าสามารถใช้ legacy path ได้หรือไม่
     */
    private boolean canUseLegacyPath(File legacyUserDb, File legacyDataDb) {
        try {
            // ต้องมีไฟล์อยู่ และอ่านได้
            boolean userExists = legacyUserDb.exists() && legacyUserDb.canRead();
            boolean dataExists = legacyDataDb.exists() && legacyDataDb.canRead();

            Log.d(TAG, "Legacy path check:");
            Log.d(TAG, "  UserDB exists & readable: " + userExists);
            Log.d(TAG, "  DataDB exists & readable: " + dataExists);

            return userExists && dataExists;

        } catch (Exception e) {
            Log.e(TAG, "Cannot access legacy paths: " + e.getMessage());
            return false;
        }
    }

    /**
     * ลอง migrate ไฟล์ไปที่ modern path (best effort)
     */
    private void tryMigrateToModern(File legacyUser, File legacyData, File modernUser, File modernData) {
        try {
            Log.d(TAG, "Attempting migration to modern path...");

            if (DatabaseMigrationHelper.copyFile(legacyUser, modernUser) &&
                    DatabaseMigrationHelper.copyFile(legacyData, modernData)) {

                Log.d(TAG, "Migration successful! Future app starts will use modern path.");

                // อัปเดตเป็น modern path
                userDbPath = modernUser.getAbsolutePath();
                dataDbPath = modernData.getAbsolutePath();
                isUsingLegacyPath = false;

            } else {
                Log.w(TAG, "Migration failed, continuing with legacy path");
            }

        } catch (Exception e) {
            Log.w(TAG, "Migration attempt failed: " + e.getMessage());
        }
    }

    // Getters
    public String getUserDbPath() { return userDbPath; }
    public String getDataDbPath() { return dataDbPath; }
    public boolean isUsingLegacyPath() { return isUsingLegacyPath; }

    /**
     * ตรวจสอบว่าไฟล์ DB มีอยู่และเข้าถึงได้
     */
    public boolean areDatabaseFilesReady() {
        File userDb = new File(userDbPath);
        File dataDb = new File(dataDbPath);

        boolean ready = userDb.exists() && dataDb.exists() &&
                userDb.canRead() && dataDb.canRead();

        Log.d(TAG, "Database files ready: " + ready);
        if (!ready) {
            Log.d(TAG, "UserDB exists: " + userDb.exists() + ", readable: " + userDb.canRead());
            Log.d(TAG, "DataDB exists: " + dataDb.exists() + ", readable: " + dataDb.canRead());
        }

        return ready;
    }

    /**
     * สร้าง error message แบบมีประโยชน์
     */
    public String getSetupInstructions() {
        if (isUsingLegacyPath) {
            return "แอปใช้ตำแหน่งไฟล์แบบเก่า ซึ่งอาจไม่ทำงานในอนาคต\n" +
                    "แนะนำให้ย้ายไฟล์ไปยัง: " +
                    new File(context.getExternalFilesDir(null), "databases").getAbsolutePath();
        } else {
            return "กรุณาคัดลอกไฟล์ฐานข้อมูลไปยัง:\n" +
                    new File(context.getExternalFilesDir(null), "databases").getAbsolutePath() + "\n\n" +
                    "ไฟล์ที่ต้องการ:\n• uJHCIS.db\n• mJHCIS.sdb";
        }
    }
}