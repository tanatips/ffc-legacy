package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Model คลาสสำหรับข้อมูลการส่ง claim ไปยัง สปสช (nhso_claim_data)
 * ใช้สำหรับเก็บข้อมูล JSON ที่ส่งไปยัง NHSO
 */
public class NHSOClaimData implements BaseColumns {

    // ชื่อตาราง
    public static final String TABLENAME = "ffc_sf_nhso_claim_data";

    // URI สำหรับเข้าถึงข้อมูล
    public static final Uri CONTENT_URI = Uri.parse("content://" + NHSOClaimDataProvider.AUTHORITY + "/nhso_claim_data");
    public static final Uri CONTENT_LIST_URI = Uri.parse("content://" + NHSOClaimDataProvider.AUTHORITY + "/nhso_claim_data/list");

    // MIME Types
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ffc.nhso_claim_data";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ffc.nhso_claim_data";

    // คอลัมน์ในตาราง
    public static final String ID = "_id"; // Primary key
    public static final String VISITNO = "visitno"; // หมายเลขการเยี่ยมบ้าน/การให้บริการ
    public static final String JSON_DATA = "json_data"; // ข้อมูล JSON ที่ส่งไปยัง NHSO
    public static final String SEQ = "seq"; // SEQ number ที่ได้จาก NHSO
    public static final String CLAIM_STATUS = "claim_status"; // สถานะการส่ง claim (0=ยังไม่ส่ง, 1=ส่งสำเร็จ, 2=ส่งไม่สำเร็จ)
    public static final String RESPONSE_DATA = "response_data"; // Response ที่ได้จาก NHSO API
    public static final String ERROR_MESSAGE = "error_message"; // ข้อความ error (ถ้ามี)
    public static final String CREATED_DATE = "created_date"; // วันที่สร้างข้อมูล
    public static final String UPDATED_DATE = "updated_date"; // วันที่อัพเดทข้อมูล
    public static final String SEND_DATE = "send_date"; // วันที่ส่ง claim

    // ค่าคงที่สำหรับสถานะการส่ง claim
    public static final String CLAIM_STATUS_NOT_SENT = "0"; // ยังไม่ส่ง
    public static final String CLAIM_STATUS_SUCCESS = "1"; // ส่งสำเร็จ
    public static final String CLAIM_STATUS_FAILED = "2"; // ส่งไม่สำเร็จ
    public static final String CLAIM_STATUS_RETRY = "9"; // ส่งซ้ำ

    // คำสั่ง SQL สำหรับสร้างตาราง
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VISITNO + " INTEGER NOT NULL, " +
                    JSON_DATA + " TEXT NOT NULL, " +
                    SEQ + " TEXT, " +
                    CLAIM_STATUS + " TEXT DEFAULT '" + CLAIM_STATUS_NOT_SENT + "', " +
                    RESPONSE_DATA + " TEXT, " +
                    ERROR_MESSAGE + " TEXT, " +
                    CREATED_DATE + " DATETIME NOT NULL, " +
                    UPDATED_DATE + " DATETIME, " +
                    SEND_DATE + " DATETIME" +
                    ");";

    // คำสั่ง SQL สำหรับสร้าง Index
    public static final String CREATE_INDEX_VISITNO =
            "CREATE INDEX IF NOT EXISTS idx_nhso_visitno ON " + TABLENAME + " (" + VISITNO + ");";

    public static final String CREATE_INDEX_STATUS =
            "CREATE INDEX IF NOT EXISTS idx_nhso_status ON " + TABLENAME + " (" + CLAIM_STATUS + ");";

    public static final String CREATE_INDEX_CREATED_DATE =
            "CREATE INDEX IF NOT EXISTS idx_nhso_created_date ON " + TABLENAME + " (" + CREATED_DATE + ");";

    // PROJECTION_MAP สำหรับการ Query
    protected static final HashMap<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(VISITNO, VISITNO);
        PROJECTION_MAP.put(JSON_DATA, JSON_DATA);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(CLAIM_STATUS, CLAIM_STATUS);
        PROJECTION_MAP.put(RESPONSE_DATA, RESPONSE_DATA);
        PROJECTION_MAP.put(ERROR_MESSAGE, ERROR_MESSAGE);
        PROJECTION_MAP.put(CREATED_DATE, CREATED_DATE);
        PROJECTION_MAP.put(UPDATED_DATE, UPDATED_DATE);
        PROJECTION_MAP.put(SEND_DATE, SEND_DATE);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น ContentValues
     * @param cursor Cursor ที่ได้จากการ query
     * @return ContentValues ที่มีข้อมูลจาก Cursor
     */
    public static ContentValues getContentValuesFromCursor(Cursor cursor) {
        ContentValues cv = new ContentValues();

        cv.put(ID, cursor.getLong(cursor.getColumnIndex(ID)));
        cv.put(VISITNO, cursor.getInt(cursor.getColumnIndex(VISITNO)));
        cv.put(JSON_DATA, cursor.getString(cursor.getColumnIndex(JSON_DATA)));

        // ใส่ค่าเฉพาะถ้าคอลัมน์นั้นมีค่า
        if (!cursor.isNull(cursor.getColumnIndex(SEQ))) {
            cv.put(SEQ, cursor.getString(cursor.getColumnIndex(SEQ)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(CLAIM_STATUS))) {
            cv.put(CLAIM_STATUS, cursor.getString(cursor.getColumnIndex(CLAIM_STATUS)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(RESPONSE_DATA))) {
            cv.put(RESPONSE_DATA, cursor.getString(cursor.getColumnIndex(RESPONSE_DATA)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(ERROR_MESSAGE))) {
            cv.put(ERROR_MESSAGE, cursor.getString(cursor.getColumnIndex(ERROR_MESSAGE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(CREATED_DATE))) {
            cv.put(CREATED_DATE, cursor.getString(cursor.getColumnIndex(CREATED_DATE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(UPDATED_DATE))) {
            cv.put(UPDATED_DATE, cursor.getString(cursor.getColumnIndex(UPDATED_DATE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(SEND_DATE))) {
            cv.put(SEND_DATE, cursor.getString(cursor.getColumnIndex(SEND_DATE)));
        }

        return cv;
    }

    /**
     * คลาสสำหรับการสร้าง ContentValues สำหรับ NHSOClaimData
     */
    public static class Builder {
        private ContentValues values = new ContentValues();

        public Builder visitno(int visitno) {
            values.put(VISITNO, visitno);
            return this;
        }

        public Builder jsonData(String jsonData) {
            values.put(JSON_DATA, jsonData);
            return this;
        }

        public Builder seq(String seq) {
            values.put(SEQ, seq);
            return this;
        }

        public Builder claimStatus(String claimStatus) {
            values.put(CLAIM_STATUS, claimStatus);
            return this;
        }

        public Builder responseData(String responseData) {
            values.put(RESPONSE_DATA, responseData);
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            values.put(ERROR_MESSAGE, errorMessage);
            return this;
        }

        public Builder createdDate(String createdDate) {
            values.put(CREATED_DATE, createdDate);
            return this;
        }

        public Builder updatedDate(String updatedDate) {
            values.put(UPDATED_DATE, updatedDate);
            return this;
        }

        public Builder sendDate(String sendDate) {
            values.put(SEND_DATE, sendDate);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }

    /**
     * Helper methods สำหรับสร้าง ContentValues
     */
    public static class ClaimData {

        /**
         * สร้าง ContentValues สำหรับบันทึก claim data ใหม่
         */
        public static ContentValues createClaimRecord(int visitno, String jsonData, String createdDate) {
            return new Builder()
                    .visitno(visitno)
                    .jsonData(jsonData)
                    .claimStatus(CLAIM_STATUS_NOT_SENT)
                    .createdDate(createdDate)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับอัพเดทสถานะเป็นสำเร็จ
         */
        public static ContentValues updateSuccessStatus(String seq, String responseData, String sendDate) {
            return new Builder()
                    .seq(seq)
                    .claimStatus(CLAIM_STATUS_SUCCESS)
                    .responseData(responseData)
                    .sendDate(sendDate)
                    .updatedDate(sendDate)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับอัพเดทสถานะเป็นไม่สำเร็จ
         */
        public static ContentValues updateFailedStatus(String errorMessage, String updatedDate) {
            return new Builder()
                    .claimStatus(CLAIM_STATUS_FAILED)
                    .errorMessage(errorMessage)
                    .updatedDate(updatedDate)
                    .build();
        }
    }

    public static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}