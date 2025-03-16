package th.in.ffc.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/**
 * แฟ้ม 1 NHSO Patient Data Structure
 */
public class NHSOPatient implements BaseColumns {
    public static final String TABLENAME = "ffc_nhso_patient";

    public static HashMap<String, String> PROJECTION_MAP;

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOPatientProvider.AUTHORITY + "/nhso_patient");
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhso_patient";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhso_patient";

    public static final String ID = "id";

    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    public static final String SEQ = "seq";

    // ประเภทเอกสารที่ใช้ในการยืนยันตัวตน
    public static final String TYPE = "type";

    // เลขประจำตัวประชาชน
    public static final String CID = "cid";

    // เลขหนังสือเดินทาง (Passport)
    public static final String PPN = "ppn";

    // เลขประจำตัวคนพิการ
    public static final String PWD = "pwd";

    // ชื่อ
    public static final String NAME_GIVEN = "name_given";

    // นามสกุล
    public static final String NAME_FAMILY = "name_family";

    // วันเกิด (ISO 8601 format: YYYY-MM-DD)
    public static final String BIRTHDATE = "birthdate";

    // เพศ (1=ชาย, 2=หญิง)
    public static final String GENDER = "gender";

    // ที่อยู่ บ้านเลขที่ หมู่ที่ หมู่บ้าน และซอย
    public static final String ADDRESS_LINE = "address_line";

    // รหัสตำบล/แขวง
    public static final String ADDRESS_CITY = "address_city";

    // รหัสอำเภอ/เขต
    public static final String ADDRESS_DISTRICT = "address_district";

    // รหัสจังหวัด
    public static final String ADDRESS_STATE = "address_state";

    // รหัสไปรษณีย์
    public static final String ADDRESS_POSTAL_CODE = "address_postal_code";

    // สัญชาติ
    public static final String NATIONALITY = "nationality";

    // เชื้อชาติ
    public static final String RACE = "race";

    // หมายเลขประจำตัวผู้รับบริการ
    public static final String HN = "hn";

    // หมายเลขประจำตัวผู้ป่วยใน
    public static final String AN = "an";

    // ข้อมูลการส่งข้อมูล (0=ยังไม่ส่งไป NHSO, 1=ส่งข้อมูลไป NHSO แล้ว)
    public static final String SEND_TO_NHSO = "send_to_nhso";

    // ข้อมูลผู้ที่สร้างรายการ
    public static final String CREATED_BY = "created_by";
    public static final String CREATED_DATE = "created_date";
    public static final String UPDATED_BY = "updated_by";
    public static final String UPDATED_DATE = "updated_date";

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(NHSOPatient.ID, "id AS " + NHSOPatient.ID);
        PROJECTION_MAP.put(NHSOPatient.SEQ, "seq AS " + NHSOPatient.SEQ);
        PROJECTION_MAP.put(NHSOPatient.TYPE, "type AS " + NHSOPatient.TYPE);
        PROJECTION_MAP.put(NHSOPatient.CID, "cid AS " + NHSOPatient.CID);
        PROJECTION_MAP.put(NHSOPatient.PPN, "ppn AS " + NHSOPatient.PPN);
        PROJECTION_MAP.put(NHSOPatient.PWD, "pwd AS " + NHSOPatient.PWD);
        PROJECTION_MAP.put(NHSOPatient.NAME_GIVEN, "name_given AS " + NHSOPatient.NAME_GIVEN);
        PROJECTION_MAP.put(NHSOPatient.NAME_FAMILY, "name_family AS " + NHSOPatient.NAME_FAMILY);
        PROJECTION_MAP.put(NHSOPatient.BIRTHDATE, "birthdate AS " + NHSOPatient.BIRTHDATE);
        PROJECTION_MAP.put(NHSOPatient.GENDER, "gender AS " + NHSOPatient.GENDER);
        PROJECTION_MAP.put(NHSOPatient.ADDRESS_LINE, "address_line AS " + NHSOPatient.ADDRESS_LINE);
        PROJECTION_MAP.put(NHSOPatient.ADDRESS_CITY, "address_city AS " + NHSOPatient.ADDRESS_CITY);
        PROJECTION_MAP.put(NHSOPatient.ADDRESS_DISTRICT, "address_district AS " + NHSOPatient.ADDRESS_DISTRICT);
        PROJECTION_MAP.put(NHSOPatient.ADDRESS_STATE, "address_state AS " + NHSOPatient.ADDRESS_STATE);
        PROJECTION_MAP.put(NHSOPatient.ADDRESS_POSTAL_CODE, "address_postal_code AS " + NHSOPatient.ADDRESS_POSTAL_CODE);
        PROJECTION_MAP.put(NHSOPatient.NATIONALITY, "nationality AS " + NHSOPatient.NATIONALITY);
        PROJECTION_MAP.put(NHSOPatient.RACE, "race AS " + NHSOPatient.RACE);
        PROJECTION_MAP.put(NHSOPatient.HN, "hn AS " + NHSOPatient.HN);
        PROJECTION_MAP.put(NHSOPatient.AN, "an AS " + NHSOPatient.AN);
        PROJECTION_MAP.put(NHSOPatient.SEND_TO_NHSO, "send_to_nhso AS " + NHSOPatient.SEND_TO_NHSO);
        PROJECTION_MAP.put(NHSOPatient.CREATED_BY, "created_by AS " + NHSOPatient.CREATED_BY);
        PROJECTION_MAP.put(NHSOPatient.CREATED_DATE, "created_date AS " + NHSOPatient.CREATED_DATE);
        PROJECTION_MAP.put(NHSOPatient.UPDATED_BY, "updated_by AS " + NHSOPatient.UPDATED_BY);
        PROJECTION_MAP.put(NHSOPatient.UPDATED_DATE, "updated_date AS " + NHSOPatient.UPDATED_DATE);
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            SEQ + " TEXT NOT NULL," +
            TYPE + " TEXT NOT NULL," +
            CID + " TEXT NOT NULL," +
            PPN + " TEXT," +
            PWD + " TEXT," +
            NAME_GIVEN + " TEXT NOT NULL," +
            NAME_FAMILY + " TEXT NOT NULL," +
            BIRTHDATE + " TEXT," +
            GENDER + " TEXT," +
            ADDRESS_LINE + " TEXT," +
            ADDRESS_CITY + " TEXT," +
            ADDRESS_DISTRICT + " TEXT," +
            ADDRESS_STATE + " TEXT," +
            ADDRESS_POSTAL_CODE + " TEXT," +
            NATIONALITY + " TEXT," +
            RACE + " TEXT," +
            HN + " TEXT," +
            AN + " TEXT," +
            SEND_TO_NHSO + " INTEGER DEFAULT 0," + // 0=ยังไม่ส่งไป NHSO, 1=ส่งข้อมูลไป NHSO แล้ว
            CREATED_BY + " TEXT," +
            CREATED_DATE + " TEXT," +
            UPDATED_BY + " TEXT," +
            UPDATED_DATE + " TEXT," +
            "UNIQUE (" + SEQ + ", " + TYPE + ", " + CID + ")" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}