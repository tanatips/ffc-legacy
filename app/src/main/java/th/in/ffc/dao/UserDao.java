package th.in.ffc.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import th.in.ffc.model.UserModel;
import th.in.ffc.provider.UserDataProvider;

/**
 * Data Access Object for User operations
 *
 * @author Generated from UserProvider
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class UserDao {

    private Context context;
    private ContentResolver contentResolver;

    public UserDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    /**
     * ค้นหาข้อมูล user ด้วย username
     *
     * @param username ชื่อผู้ใช้ที่ต้องการค้นหา
     * @return UserModel object หรือ null ถ้าไม่พบข้อมูล
     */
    public UserModel findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        Uri uri = Uri.withAppendedPath(UserDataProvider.User.CONTENT_URI, username);
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                    uri,
                    null, // projection - ดึงข้อมูลทุกคอลัมน์
                    null, // selection
                    null, // selectionArgs
                    null  // sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * ตรวจสอบการ login ด้วย username และ password
     *
     * @param username ชื่อผู้ใช้
     * @param password รหัสผ่าน
     * @return UserModel object ถ้า login สำเร็จ หรือ null ถ้าไม่สำเร็จ
     */
    public UserModel login(String username, String password) {
        if (username == null || password == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                    UserDataProvider.User.CONTENT_URI,
                    null, // projection
                    UserDataProvider.User.SELECTION_LOGIN, // selection
                    new String[]{username, password}, // selectionArgs
                    null // sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * ดึงข้อมูล user ทั้งหมด
     *
     * @return Cursor ที่มีข้อมูล user ทั้งหมด
     */
    public Cursor getAllUsers() {
        try {
            return contentResolver.query(
                    UserDataProvider.User.CONTENT_URI,
                    null, // projection
                    null, // selection
                    null, // selectionArgs
                    UserDataProvider.User.USERNAME + " ASC" // sortOrder
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ตรวจสอบว่า username มีอยู่ในระบบหรือไม่
     *
     * @param username ชื่อผู้ใช้ที่ต้องการตรวจสอบ
     * @return true ถ้ามี username อยู่ในระบบ, false ถ้าไม่มี
     */
    public boolean isUsernameExists(String username) {
        UserModel user = findByUsername(username);
        return user != null;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น UserModel
     *
     * @param cursor Cursor ที่มีข้อมูล user
     * @return UserModel object
     */
    private UserModel cursorToUser(Cursor cursor) {
        UserModel user = new UserModel();

        try {
            // ตรวจสอบว่าคอลัมน์มีอยู่จริงหรือไม่ก่อนดึงข้อมูล
            int pcucodeIndex = cursor.getColumnIndex(UserDataProvider.User.PCUCODE);
            if (pcucodeIndex >= 0) {
                user.setPcucode(cursor.getString(pcucodeIndex));
            }

            int usernameIndex = cursor.getColumnIndex(UserDataProvider.User.USERNAME);
            if (usernameIndex >= 0) {
                user.setUsername(cursor.getString(usernameIndex));
            }

            int idcardIndex = cursor.getColumnIndex(UserDataProvider.User.IDCARD);
            if (idcardIndex >= 0) {
                user.setIdcard(cursor.getString(idcardIndex));
            }

            // หมายเหตุ: PASSWORD เป็น private field ใน UserProvider
            // ถ้าต้องการใช้งาน อาจต้องเปลี่ยนเป็น public หรือสร้าง getter method

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }
}