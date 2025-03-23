package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Constants สำหรับแฟ้มที่ 5 NHSO Diagnosis
 */
public class NHSODiagnosis implements BaseColumns {

    public static final String TABLENAME = "ffc_nhso_diagnosis";

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsodiagnosis";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsodiagnosis";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSODiagnosisProvider.AUTHORITY + "/nhso_diagnosis");

    public static final Uri CONTENT_LIST_URI = Uri.parse("content://"
            + NHSODiagnosisProvider.AUTHORITY + "/nhso_diagnosis/list");

    // คอลัมน์ในตาราง
    public static final String ID = _ID;
    public static final String SEQ = "seq";                    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    public static final String DATEDX = "datedx";              // วันเดือนปีที่วินิจฉัยโรค
    public static final String DIAG = "diag";                  // รหัสวินิจฉัยโรค ตามรหัส ICD 10
    public static final String DIAGTYPE = "diagtype";          // รหัสประเภทการวินิจฉัยตาม 43plus
    public static final String PROFESSION_ID = "profession_id"; // เลขใบอนุญาตประกอบวิชาชีพ
    public static final String CLINIC = "clinic";              // แผนกที่รักษาผู้ป่วยเป็นหลัก
    public static final String USER_CREATE = "user_create";    // ผู้บันทึกข้อมูล
    public static final String CREATETIME = "createtime";      // เวลาที่บันทึกข้อมูล
    public static final String USER_UPDATE = "user_update";    // ผู้แก้ไขข้อมูล
    public static final String UPDATETIME = "updatetime";      // เวลาที่แก้ไขข้อมูล
    public static final String PCUCODE = "pcucode";            // รหัสหน่วยบริการ
    public static final String SYNC_STATUS = "sync_status";    // สถานะการซิงค์ข้อมูล
    public static final String UPDATE = "update_status";       // สถานะการอัพเดท

    // ตาราง NHSO Diagnosis
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLENAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEQ + " TEXT NOT NULL, "
            + DATEDX + " DATETIME, "
            + DIAG + " TEXT, "
            + DIAGTYPE + " TEXT, "
            + PROFESSION_ID + " TEXT, "
            + CLINIC + " TEXT, "
            + USER_CREATE + " TEXT, "
            + CREATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + USER_UPDATE + " TEXT, "
            + UPDATETIME + " DATETIME, "
            + PCUCODE + " TEXT, "
            + SYNC_STATUS + " INTEGER DEFAULT 0, "
            + UPDATE + " TEXT DEFAULT '0'"
            + ");";

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(DATEDX, DATEDX);
        PROJECTION_MAP.put(DIAG, DIAG);
        PROJECTION_MAP.put(DIAGTYPE, DIAGTYPE);
        PROJECTION_MAP.put(PROFESSION_ID, PROFESSION_ID);
        PROJECTION_MAP.put(CLINIC, CLINIC);
        PROJECTION_MAP.put(USER_CREATE, USER_CREATE);
        PROJECTION_MAP.put(CREATETIME, CREATETIME);
        PROJECTION_MAP.put(USER_UPDATE, USER_UPDATE);
        PROJECTION_MAP.put(UPDATETIME, UPDATETIME);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(SYNC_STATUS, SYNC_STATUS);
        PROJECTION_MAP.put(UPDATE, UPDATE);
    }

    public static Uri getContentUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}