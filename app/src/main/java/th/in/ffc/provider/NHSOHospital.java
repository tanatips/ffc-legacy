package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Model คลาสสำหรับข้อมูลแฟ้มที่ 2 NHSO Provider
 * ข้อมูลสถานพยาบาลที่ให้บริการผู้เข้ารับบริการ
 */
public class NHSOHospital implements BaseColumns {

    // ชื่อตาราง
    public static final String TABLENAME = "ffc_nhso_hospital";

    // URI สำหรับเข้าถึงข้อมูล
    public static final Uri CONTENT_URI = Uri.parse("content://" + NHSOHospitalProvider.AUTHORITY + "/nhso_hospital");
    public static final Uri CONTENT_LIST_URI = Uri.parse("content://" + NHSOHospitalProvider.AUTHORITY + "/nhso_hospital/list");

    // MIME Types
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ffc.nhsohospital";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ffc.nhsohospital";

    // คอลัมน์ในตาราง
    public static final String ID = BaseColumns._ID; // ID สำหรับตาราง (Auto Increment)
    public static final String SEQ = "seq"; // รหัสการบริการที่กำหนดโดยนับหน่วย (Visit Number)
    public static final String HCODE = "hcode"; // รหัสสถานพยาบาลให้บริการ 5 หลัก
    public static final String HCODE_NAME = "hcode_name"; // ชื่อสถานพยาบาลให้บริการ
    public static final String HCODE_SEND = "hcode_send"; // รหัสสถานพยาบาลส่งเบิก 5 หลัก
    public static final String HCODE_SEND_NAME = "hcode_send_name"; // ชื่อสถานพยาบาลส่งเบิก
    public static final String HMAIN = "hmain"; // รหัสสถานพยาบาลประจำ 5 หลัก
    public static final String HMAIN_NAME = "hmain_name"; // ชื่อสถานพยาบาลประจำ

    // คอลัมน์เพิ่มเติมที่อาจต้องการ
    public static final String CREATETIME = "createtime"; // เวลาที่สร้างข้อมูล
    public static final String UPDATETIME = "updatetime"; // เวลาที่อัพเดทข้อมูลล่าสุด
    public static final String USER_CREATE = "user_create"; // ผู้สร้างข้อมูล
    public static final String USER_UPDATE = "user_update"; // ผู้อัพเดทข้อมูลล่าสุด

    // คำสั่ง SQL สำหรับสร้างตาราง
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SEQ + " TEXT NOT NULL, " +  // รหัสการบริการที่กำหนด (Visit Number)
                    HCODE + " TEXT NOT NULL, " +  // รหัสสถานพยาบาลให้บริการ
                    HCODE_NAME + " TEXT, " +  // ชื่อสถานพยาบาลให้บริการ
                    HCODE_SEND + " TEXT, " +  // รหัสสถานพยาบาลส่งเบิก
                    HCODE_SEND_NAME + " TEXT, " +  // ชื่อสถานพยาบาลส่งเบิก
                    HMAIN + " TEXT, " +  // รหัสสถานพยาบาลประจำ
                    HMAIN_NAME + " TEXT, " +  // ชื่อสถานพยาบาลประจำ
                    CREATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    UPDATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    USER_CREATE + " TEXT, " +
                    USER_UPDATE + " TEXT" +
                    ");";

    // PROJECTION_MAP สำหรับการ Query
    protected static final HashMap<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(HCODE, HCODE);
        PROJECTION_MAP.put(HCODE_NAME, HCODE_NAME);
        PROJECTION_MAP.put(HCODE_SEND, HCODE_SEND);
        PROJECTION_MAP.put(HCODE_SEND_NAME, HCODE_SEND_NAME);
        PROJECTION_MAP.put(HMAIN, HMAIN);
        PROJECTION_MAP.put(HMAIN_NAME, HMAIN_NAME);
        PROJECTION_MAP.put(CREATETIME, CREATETIME);
        PROJECTION_MAP.put(UPDATETIME, UPDATETIME);
        PROJECTION_MAP.put(USER_CREATE, USER_CREATE);
        PROJECTION_MAP.put(USER_UPDATE, USER_UPDATE);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น ContentValues
     * @param cursor Cursor ที่ได้จากการ query
     * @return ContentValues ที่มีข้อมูลจาก Cursor
     */
    public static ContentValues getContentValuesFromCursor(Cursor cursor) {
        ContentValues cv = new ContentValues();

        cv.put(SEQ, cursor.getString(cursor.getColumnIndex(SEQ)));
        cv.put(HCODE, cursor.getString(cursor.getColumnIndex(HCODE)));

        // ใส่ค่าเฉพาะถ้าคอลัมน์นั้นมีค่า
        if (!cursor.isNull(cursor.getColumnIndex(HCODE_NAME))) {
            cv.put(HCODE_NAME, cursor.getString(cursor.getColumnIndex(HCODE_NAME)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(HCODE_SEND))) {
            cv.put(HCODE_SEND, cursor.getString(cursor.getColumnIndex(HCODE_SEND)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(HCODE_SEND_NAME))) {
            cv.put(HCODE_SEND_NAME, cursor.getString(cursor.getColumnIndex(HCODE_SEND_NAME)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(HMAIN))) {
            cv.put(HMAIN, cursor.getString(cursor.getColumnIndex(HMAIN)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(HMAIN_NAME))) {
            cv.put(HMAIN_NAME, cursor.getString(cursor.getColumnIndex(HMAIN_NAME)));
        }

        return cv;
    }

    /**
     * คลาสสำหรับการสร้าง ContentValues สำหรับ NHSOHospital
     */
    public static class Builder {
        private ContentValues values = new ContentValues();

        public Builder seq(String seq) {
            values.put(SEQ, seq);
            return this;
        }

        public Builder hcode(String hcode) {
            values.put(HCODE, hcode);
            return this;
        }

        public Builder hcodeName(String hcodeName) {
            values.put(HCODE_NAME, hcodeName);
            return this;
        }

        public Builder hcodeSend(String hcodeSend) {
            values.put(HCODE_SEND, hcodeSend);
            return this;
        }

        public Builder hcodeSendName(String hcodeSendName) {
            values.put(HCODE_SEND_NAME, hcodeSendName);
            return this;
        }

        public Builder hmain(String hmain) {
            values.put(HMAIN, hmain);
            return this;
        }

        public Builder hmainName(String hmainName) {
            values.put(HMAIN_NAME, hmainName);
            return this;
        }

        public Builder userCreate(String userCreate) {
            values.put(USER_CREATE, userCreate);
            return this;
        }

        public Builder userUpdate(String userUpdate) {
            values.put(USER_UPDATE, userUpdate);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
    public static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;

}