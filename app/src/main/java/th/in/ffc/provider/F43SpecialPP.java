package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Model คลาสสำหรับข้อมูลการให้บริการส่งเสริม ป้องกันโรค (f43specialpp)
 * ใช้สำหรับเก็บข้อมูลบริการพิเศษทางการแพทย์
 */
public class F43SpecialPP implements BaseColumns {

    // ชื่อตาราง
    public static final String TABLENAME = "f43specialpp";

    // URI สำหรับเข้าถึงข้อมูล
    public static final Uri CONTENT_URI = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY + "/f43specialpp");
    public static final Uri CONTENT_LIST_URI = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY + "/f43specialpp/list");

    // MIME Types
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ffc.f43specialpp";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ffc.f43specialpp";

    // คอลัมน์ในตาราง
    public static final String PCUCODEPERSON = "pcucodeperson"; // รหัส PCU ของผู้รับบริการ
    public static final String PID = "pid"; // รหัสผู้รับบริการ
    public static final String DATESERV = "dateserv"; // วันที่ให้บริการ
    public static final String PPSPECIAL = "ppspecial"; // รหัสการให้บริการส่งเสริม ป้องกันโรค
    public static final String PPRESULT = "ppresult"; // ผลการให้บริการ
    public static final String PCUCODE = "pcucode"; // รหัส PCU ที่ให้บริการ
    public static final String VISITNO = "visitno"; // หมายเลขการเยี่ยมบ้าน/การให้บริการ
    public static final String SERVPLACE = "servplace"; // สถานที่ให้บริการ (1:ในสถานบริการ, 2:นอกสถานบริการ)
    public static final String PPSPLACE = "ppsplace"; // รหัสสถานบริการที่ให้บริการส่งเสริม ป้องกันโรค
    public static final String PROVIDER = "provider"; // ผู้ให้บริการ
    public static final String DATEUPDATE = "dateupdate"; // วันที่อัพเดทข้อมูล
    public static final String ISSEND2HISGATEWAY = "issend2hisgateway"; // สถานะการส่งข้อมูลไป HIS Gateway
    public static final String ISSEND2HISGATEWAYDT = "issend2hisgatewaydt"; // วันเวลาที่ส่งข้อมูลไป HIS Gateway
    public static final String ISSEND2HISGATEWAYALL = "issend2hisgatewayall"; // สถานะการส่งข้อมูลทั้งหมดไป HIS Gateway

    // ค่าคงที่สำหรับสถานะการให้บริการ
    public static final String SERVPLACE_IN = "1"; // ในสถานบริการ
    public static final String SERVPLACE_OUT = "2"; // นอกสถานบริการ

    // ค่าคงที่สำหรับสถานะการส่งข้อมูล
    public static final String SEND_STATUS_NOT_SENT = "0"; // ยังไม่ส่ง
    public static final String SEND_STATUS_SENT = "1"; // ส่งแล้ว
    public static final String SEND_STATUS_ERROR = "2"; // เกิดข้อผิดพลาด
    public static final String SEND_STATUS_RETRY = "9"; // ส่งซ้ำ

    // คำสั่ง SQL สำหรับสร้างตาราง
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                    PCUCODEPERSON + " TEXT NOT NULL, " +
                    PID + " INTEGER NOT NULL, " +
                    DATESERV + " DATE NOT NULL, " +
                    PPSPECIAL + " TEXT NOT NULL, " +
                    PPRESULT + " TEXT, " +
                    PCUCODE + " TEXT, " +
                    VISITNO + " INTEGER, " +
                    SERVPLACE + " TEXT, " +
                    PPSPLACE + " TEXT, " +
                    PROVIDER + " TEXT, " +
                    DATEUPDATE + " DATETIME, " +
                    ISSEND2HISGATEWAY + " TEXT DEFAULT '" + SEND_STATUS_NOT_SENT + "', " +
                    ISSEND2HISGATEWAYDT + " DATETIME, " +
                    ISSEND2HISGATEWAYALL + " TEXT, " +
                    // เพิ่ม constraint
                    "CONSTRAINT unique_person_service UNIQUE(" + PCUCODEPERSON + ", " + PID + ", " + DATESERV + ", " + PPSPECIAL + ")" +
                    ");";

    // คำสั่ง SQL สำหรับสร้าง Index
    public static final String CREATE_INDEX_PERSON =
            "CREATE INDEX IF NOT EXISTS idx_f43_person ON " + TABLENAME + " (" + PCUCODEPERSON + ", " + PID + ");";

    public static final String CREATE_INDEX_VISITNO =
            "CREATE INDEX IF NOT EXISTS idx_f43_visitno ON " + TABLENAME + " (" + VISITNO + ");";

    public static final String CREATE_INDEX_DATESERV =
            "CREATE INDEX IF NOT EXISTS idx_f43_dateserv ON " + TABLENAME + " (" + DATESERV + ");";

    public static final String CREATE_INDEX_PPSPECIAL =
            "CREATE INDEX IF NOT EXISTS idx_f43_ppspecial ON " + TABLENAME + " (" + PPSPECIAL + ");";

    public static final String CREATE_INDEX_SEND_STATUS =
            "CREATE INDEX IF NOT EXISTS idx_f43_send_status ON " + TABLENAME + " (" + ISSEND2HISGATEWAY + ");";

    // PROJECTION_MAP สำหรับการ Query
    protected static final HashMap<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<String, String>();

        PROJECTION_MAP.put(PCUCODEPERSON, PCUCODEPERSON);
        PROJECTION_MAP.put(PID, PID);
        PROJECTION_MAP.put(DATESERV, DATESERV);
        PROJECTION_MAP.put(PPSPECIAL, PPSPECIAL);
        PROJECTION_MAP.put(PPRESULT, PPRESULT);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(VISITNO, VISITNO);
        PROJECTION_MAP.put(SERVPLACE, SERVPLACE);
        PROJECTION_MAP.put(PPSPLACE, PPSPLACE);
        PROJECTION_MAP.put(PROVIDER, PROVIDER);
        PROJECTION_MAP.put(DATEUPDATE, DATEUPDATE);
        PROJECTION_MAP.put(ISSEND2HISGATEWAY, ISSEND2HISGATEWAY);
        PROJECTION_MAP.put(ISSEND2HISGATEWAYDT, ISSEND2HISGATEWAYDT);
        PROJECTION_MAP.put(ISSEND2HISGATEWAYALL, ISSEND2HISGATEWAYALL);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น ContentValues
     * @param cursor Cursor ที่ได้จากการ query
     * @return ContentValues ที่มีข้อมูลจาก Cursor
     */
    public static ContentValues getContentValuesFromCursor(Cursor cursor) {
        ContentValues cv = new ContentValues();

        cv.put(PCUCODEPERSON, cursor.getString(cursor.getColumnIndex(PCUCODEPERSON)));
        cv.put(PID, cursor.getInt(cursor.getColumnIndex(PID)));
        cv.put(DATESERV, cursor.getString(cursor.getColumnIndex(DATESERV)));
        cv.put(PPSPECIAL, cursor.getString(cursor.getColumnIndex(PPSPECIAL)));

        // ใส่ค่าเฉพาะถ้าคอลัมน์นั้นมีค่า
        if (!cursor.isNull(cursor.getColumnIndex(PPRESULT))) {
            cv.put(PPRESULT, cursor.getString(cursor.getColumnIndex(PPRESULT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(PCUCODE))) {
            cv.put(PCUCODE, cursor.getString(cursor.getColumnIndex(PCUCODE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(VISITNO))) {
            cv.put(VISITNO, cursor.getInt(cursor.getColumnIndex(VISITNO)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(SERVPLACE))) {
            cv.put(SERVPLACE, cursor.getString(cursor.getColumnIndex(SERVPLACE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(PPSPLACE))) {
            cv.put(PPSPLACE, cursor.getString(cursor.getColumnIndex(PPSPLACE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(PROVIDER))) {
            cv.put(PROVIDER, cursor.getString(cursor.getColumnIndex(PROVIDER)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(DATEUPDATE))) {
            cv.put(DATEUPDATE, cursor.getString(cursor.getColumnIndex(DATEUPDATE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(ISSEND2HISGATEWAY))) {
            cv.put(ISSEND2HISGATEWAY, cursor.getString(cursor.getColumnIndex(ISSEND2HISGATEWAY)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(ISSEND2HISGATEWAYDT))) {
            cv.put(ISSEND2HISGATEWAYDT, cursor.getString(cursor.getColumnIndex(ISSEND2HISGATEWAYDT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(ISSEND2HISGATEWAYALL))) {
            cv.put(ISSEND2HISGATEWAYALL, cursor.getString(cursor.getColumnIndex(ISSEND2HISGATEWAYALL)));
        }

        return cv;
    }

    /**
     * คลาสสำหรับการสร้าง ContentValues สำหรับ F43SpecialPP
     */
    public static class Builder {
        private ContentValues values = new ContentValues();

        public Builder pcucodeperson(String pcucodeperson) {
            values.put(PCUCODEPERSON, pcucodeperson);
            return this;
        }

        public Builder pid(int pid) {
            values.put(PID, pid);
            return this;
        }

        public Builder dateserv(String dateserv) {
            values.put(DATESERV, dateserv);
            return this;
        }

        public Builder ppspecial(String ppspecial) {
            values.put(PPSPECIAL, ppspecial);
            return this;
        }

        public Builder ppresult(String ppresult) {
            values.put(PPRESULT, ppresult);
            return this;
        }

        public Builder pcucode(String pcucode) {
            values.put(PCUCODE, pcucode);
            return this;
        }

        public Builder visitno(int visitno) {
            values.put(VISITNO, visitno);
            return this;
        }

        public Builder servplace(String servplace) {
            values.put(SERVPLACE, servplace);
            return this;
        }

        public Builder ppsplace(String ppsplace) {
            values.put(PPSPLACE, ppsplace);
            return this;
        }

        public Builder provider(String provider) {
            values.put(PROVIDER, provider);
            return this;
        }

        public Builder dateupdate(String dateupdate) {
            values.put(DATEUPDATE, dateupdate);
            return this;
        }

        public Builder issend2hisgateway(String issend2hisgateway) {
            values.put(ISSEND2HISGATEWAY, issend2hisgateway);
            return this;
        }

        public Builder issend2hisgatewaydt(String issend2hisgatewaydt) {
            values.put(ISSEND2HISGATEWAYDT, issend2hisgatewaydt);
            return this;
        }

        public Builder issend2hisgatewayall(String issend2hisgatewayall) {
            values.put(ISSEND2HISGATEWAYALL, issend2hisgatewayall);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }

    /**
     * Helper methods สำหรับสร้าง ContentValues สำหรับการให้บริการต่างๆ
     */
    public static class ServiceResult {

        /**
         * สร้าง ContentValues สำหรับการให้บริการส่งเสริม ป้องกันโรค
         */
        public static ContentValues createServiceRecord(String pcucodeperson, int pid, String dateserv,
                                                        String ppspecial, String ppresult, String pcucode, Integer visitno,
                                                        String servplace, String ppsplace, String provider, String userCreate) {

            return new Builder()
                    .pcucodeperson(pcucodeperson)
                    .pid(pid)
                    .dateserv(dateserv)
                    .ppspecial(ppspecial)
                    .ppresult(ppresult)
                    .pcucode(pcucode)
                    .visitno(visitno != null ? visitno : 0)
                    .servplace(servplace)
                    .ppsplace(ppsplace)
                    .provider(provider)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับการให้บริการในสถานบริการ
         */
        public static ContentValues createInServiceRecord(String pcucodeperson, int pid, String dateserv,
                                                          String ppspecial, String ppresult, String provider, String userCreate) {

            return new Builder()
                    .pcucodeperson(pcucodeperson)
                    .pid(pid)
                    .dateserv(dateserv)
                    .ppspecial(ppspecial)
                    .ppresult(ppresult)
                    .servplace(SERVPLACE_IN)
                    .provider(provider)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับการให้บริการนอกสถานบริการ
         */
        public static ContentValues createOutServiceRecord(String pcucodeperson, int pid, String dateserv,
                                                           String ppspecial, String ppresult, Integer visitno, String provider, String userCreate) {

            return new Builder()
                    .pcucodeperson(pcucodeperson)
                    .pid(pid)
                    .dateserv(dateserv)
                    .ppspecial(ppspecial)
                    .ppresult(ppresult)
                    .visitno(visitno != null ? visitno : 0)
                    .servplace(SERVPLACE_OUT)
                    .provider(provider)
                    .build();
        }
    }

    public static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}