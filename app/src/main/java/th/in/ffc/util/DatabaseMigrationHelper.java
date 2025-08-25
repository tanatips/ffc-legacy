package th.in.ffc.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import th.in.ffc.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DatabaseMigrationHelper {

    private static final String TAG = "DatabaseMigrationHelper";

    // Path เก่า (ไม่สามารถเข้าถึงได้ใน Android 14)
    private static final String OLD_USER_DB_PATH = "/sdcard/Android/data/th.in.ffc/databases/uJHCIS.db";
    private static final String OLD_DATA_DB_PATH = "/sdcard/Android/data/th.in.ffc/databases/mJHCIS.sdb";

    /**
     * ย้ายไฟล์ฐานข้อมูลจาก path เก่าไป path ใหม่ (ถ้าจำเป็น)
     */
    public static boolean migrateDatabasesToNewLocation(Context context) {
        try {
            // ตรวจสอบว่าต้องทำ migration หรือไม่
            if (!shouldMigrate(context)) {
                Log.d(TAG, "Migration not needed");
                return true;
            }

            File newDatabasesDir = new File(context.getExternalFilesDir(null), "databases");
            if (!newDatabasesDir.exists()) {
                boolean created = newDatabasesDir.mkdirs();
                Log.d(TAG, "Created new databases directory: " + created);
            }

            // ย้าย uJHCIS.db
            File oldUserDb = new File(OLD_USER_DB_PATH);
            File newUserDb = new File(newDatabasesDir, "uJHCIS.db");
            if (oldUserDb.exists() && oldUserDb.canRead()) {
                boolean userDbMigrated = copyFile(oldUserDb, newUserDb);
                Log.d(TAG, "UserDB migration success: " + userDbMigrated);
            }

            // ย้าย mJHCIS.sdb
            File oldDataDb = new File(OLD_DATA_DB_PATH);
            File newDataDb = new File(newDatabasesDir, "mJHCIS.sdb");
            if (oldDataDb.exists() && oldDataDb.canRead()) {
                boolean dataDbMigrated = copyFile(oldDataDb, newDataDb);
                Log.d(TAG, "DataDB migration success: " + dataDbMigrated);
            }

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Migration failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * ตรวจสอบว่าต้องทำ migration หรือไม่
     */
    private static boolean shouldMigrate(Context context) {
        // ถ้าไฟล์ในตำแหน่งใหม่มีอยู่แล้ว ไม่ต้อง migrate
        File newDatabasesDir = new File(context.getExternalFilesDir(null), "databases");
        File newUserDb = new File(newDatabasesDir, "uJHCIS.db");
        File newDataDb = new File(newDatabasesDir, "mJHCIS.sdb");

        if (newUserDb.exists() && newDataDb.exists()) {
            Log.d(TAG, "Files already exist in new location");
            return false;
        }

        // ถ้าไฟล์เก่ายังเข้าถึงได้ ต้อง migrate
        File oldUserDb = new File(OLD_USER_DB_PATH);
        File oldDataDb = new File(OLD_DATA_DB_PATH);

        return (oldUserDb.exists() && oldUserDb.canRead()) ||
                (oldDataDb.exists() && oldDataDb.canRead());
    }

    /**
     * คัดลอกไฟล์จาก source ไป destination
     */
    public static boolean copyFile(File source, File destination) {
        try (FileInputStream inStream = new FileInputStream(source);
             FileOutputStream outStream = new FileOutputStream(destination);
             FileChannel inChannel = inStream.getChannel();
             FileChannel outChannel = outStream.getChannel()) {

            inChannel.transferTo(0, inChannel.size(), outChannel);

            Log.d(TAG, "Successfully copied: " + source.getName() +
                    " -> " + destination.getAbsolutePath());
            return true;

        } catch (IOException e) {
            Log.e(TAG, "Failed to copy " + source.getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * ตรวจสอบสถานะไฟล์ฐานข้อมูล
     */
    public static void checkDatabaseStatus(Context context) {
        Log.d(TAG, "=== DATABASE STATUS CHECK ===");

        // ตรวจสอบตำแหน่งใหม่
        File newDatabasesDir = new File(context.getExternalFilesDir(null), "databases");
        File newUserDb = new File(newDatabasesDir, "uJHCIS.db");
        File newDataDb = new File(newDatabasesDir, "mJHCIS.sdb");

        Log.d(TAG, "NEW LOCATION:");
        Log.d(TAG, "UserDB exists: " + newUserDb.exists() + " (" + newUserDb.getAbsolutePath() + ")");
        Log.d(TAG, "DataDB exists: " + newDataDb.exists() + " (" + newDataDb.getAbsolutePath() + ")");

        // ตรวจสอบตำแหน่งเก่า
        File oldUserDb = new File(OLD_USER_DB_PATH);
        File oldDataDb = new File(OLD_DATA_DB_PATH);

        Log.d(TAG, "OLD LOCATION:");
        Log.d(TAG, "UserDB exists: " + oldUserDb.exists() + ", readable: " + oldUserDb.canRead());
        Log.d(TAG, "DataDB exists: " + oldDataDb.exists() + ", readable: " + oldDataDb.canRead());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.d(TAG, "MANAGE_EXTERNAL_STORAGE permission granted");
            } else {
                Log.w(TAG, "MANAGE_EXTERNAL_STORAGE permission NOT granted");
            }
        }
    }
}