package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Model คลาสสำหรับข้อมูลแฟ้มที่ 3 NHSO Practitioner
 * ข้อมูลผู้ให้บริการสุขภาพของหน่วยบริการที่ให้บริการผู้เข้ารับบริการ
 */
public class NHSOPractitioner implements BaseColumns {

    // ชื่อตาราง
    public static final String TABLENAME = "ffc_nhso_practitioner";

    // URI สำหรับเข้าถึงข้อมูล
    public static final Uri CONTENT_URI = Uri.parse("content://" + NHSOPractitionerProvider.AUTHORITY + "/nhso_practitioner");
    public static final Uri CONTENT_LIST_URI = Uri.parse("content://" + NHSOPractitionerProvider.AUTHORITY + "/nhso_practitioner/list");

    // MIME Types
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ffc.nhsopractitioner";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ffc.nhsopractitioner";

    // คอลัมน์ในตาราง
    public static final String ID = BaseColumns._ID; // ID สำหรับตาราง (Auto Increment)
    public static final String SEQ = "seq"; // รหัสการบริการที่กำหนดโดยนับหน่วย (Visit Number)
    public static final String HCODE = "hcode"; // รหัสสถานพยาบาล 5 หลัก หรือ 9 หลัก ตามมาตรฐานของกระทรวงสาธารณสุข
    public static final String CID = "cid"; // เลขประจำตัวประชาชน
    public static final String PROFESSION_ID = "profession_id"; // เลขใบอนุญาตประกอบวิชาชีพที่ออกให้โดยสภาวิชาชีพ
    public static final String COUNCIL = "council"; // รหัสสภาวิชาชีพ
    public static final String PROVIDERTYPE = "providertype"; // รหัสประเภทบุคลากร
    public static final String NAME_GIVEN = "name_given"; // ชื่อ
    public static final String NAME_FAMILY = "name_family"; // นามสกุล

    // คอลัมน์เพิ่มเติมที่อาจต้องการ
    public static final String CREATETIME = "createtime"; // เวลาที่สร้างข้อมูล
    public static final String UPDATETIME = "updatetime"; // เวลาที่อัพเดทข้อมูลล่าสุด
    public static final String USER_CREATE = "user_create"; // ผู้สร้างข้อมูล
    public static final String USER_UPDATE = "user_update"; // ผู้อัพเดทข้อมูลล่าสุด
    public static final String SYNC_STATUS = "sync_status"; // สถานะการซิงค์ข้อมูล

    // คำสั่ง SQL สำหรับสร้างตาราง
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SEQ + " TEXT NOT NULL, " +  // รหัสการบริการที่กำหนด (Visit Number)
                    HCODE + " TEXT NOT NULL, " +  // รหัสสถานพยาบาล
                    CID + " TEXT NOT NULL, " +  // เลขประจำตัวประชาชน
                    PROFESSION_ID + " TEXT, " +  // เลขใบอนุญาตประกอบวิชาชีพ
                    COUNCIL + " TEXT, " +  // รหัสสภาวิชาชีพ
                    PROVIDERTYPE + " TEXT, " +  // รหัสประเภทบุคลากร
                    NAME_GIVEN + " TEXT, " +  // ชื่อ
                    NAME_FAMILY + " TEXT, " +  // นามสกุล
                    CREATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    UPDATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    USER_CREATE + " TEXT, " +
                    USER_UPDATE + " TEXT, " +
                    SYNC_STATUS + " INTEGER DEFAULT 0" +
                    ");";

    // PROJECTION_MAP สำหรับการ Query
    protected static final HashMap<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(HCODE, HCODE);
        PROJECTION_MAP.put(CID, CID);
        PROJECTION_MAP.put(PROFESSION_ID, PROFESSION_ID);
        PROJECTION_MAP.put(COUNCIL, COUNCIL);
        PROJECTION_MAP.put(PROVIDERTYPE, PROVIDERTYPE);
        PROJECTION_MAP.put(NAME_GIVEN, NAME_GIVEN);
        PROJECTION_MAP.put(NAME_FAMILY, NAME_FAMILY);
        PROJECTION_MAP.put(CREATETIME, CREATETIME);
        PROJECTION_MAP.put(UPDATETIME, UPDATETIME);
        PROJECTION_MAP.put(USER_CREATE, USER_CREATE);
        PROJECTION_MAP.put(USER_UPDATE, USER_UPDATE);
        PROJECTION_MAP.put(SYNC_STATUS, SYNC_STATUS);
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
        cv.put(CID, cursor.getString(cursor.getColumnIndex(CID)));

        // ใส่ค่าเฉพาะถ้าคอลัมน์นั้นมีค่า
        if (!cursor.isNull(cursor.getColumnIndex(PROFESSION_ID))) {
            cv.put(PROFESSION_ID, cursor.getString(cursor.getColumnIndex(PROFESSION_ID)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(COUNCIL))) {
            cv.put(COUNCIL, cursor.getString(cursor.getColumnIndex(COUNCIL)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(PROVIDERTYPE))) {
            cv.put(PROVIDERTYPE, cursor.getString(cursor.getColumnIndex(PROVIDERTYPE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NAME_GIVEN))) {
            cv.put(NAME_GIVEN, cursor.getString(cursor.getColumnIndex(NAME_GIVEN)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NAME_FAMILY))) {
            cv.put(NAME_FAMILY, cursor.getString(cursor.getColumnIndex(NAME_FAMILY)));
        }

        return cv;
    }

    /**
     * คลาสสำหรับการสร้าง ContentValues สำหรับ NHSOPractitioner
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

        public Builder cid(String cid) {
            values.put(CID, cid);
            return this;
        }

        public Builder professionId(String professionId) {
            values.put(PROFESSION_ID, professionId);
            return this;
        }

        public Builder council(String council) {
            values.put(COUNCIL, council);
            return this;
        }

        public Builder providerType(String providerType) {
            values.put(PROVIDERTYPE, providerType);
            return this;
        }

        public Builder nameGiven(String nameGiven) {
            values.put(NAME_GIVEN, nameGiven);
            return this;
        }

        public Builder nameFamily(String nameFamily) {
            values.put(NAME_FAMILY, nameFamily);
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

        public Builder syncStatus(boolean syncStatus) {
            values.put(SYNC_STATUS, syncStatus ? 1 : 0);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}