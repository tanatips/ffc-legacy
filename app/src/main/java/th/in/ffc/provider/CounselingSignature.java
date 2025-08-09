package th.in.ffc.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Constants สำหรับการจัดเก็บข้อมูลการให้คำปรึกษาและลายเซ็น
 */
public class CounselingSignature implements BaseColumns {

    public static final String TABLENAME = "ffc_sf_counseling_signature";

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.counselingsignature";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.counselingsignature";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + CounselingSignatureProvider.AUTHORITY + "/counseling_signature");

    // คอลัมน์ในตาราง
    public static final String ID = "id";
    public static final String VISIT_NO = "visitno";                 // รหัสการเข้ารับบริการ
    public static final String PERSON_ID = "person_id";               // รหัสบุคคล
    public static final String COUNSELING_TYPE = "counseling_type";   // ประเภทการให้คำปรึกษา (1=ให้คำแนะนำ, 2=ส่งต่อแพทย์)
    public static final String DETAIL = "detail";                     // รายละเอียดของคำแนะนำ
    public static final String REFERRAL_DETAIL = "referral_detail"; // เพิ่มใหม่
    public static final String PATIENT_SIGNATURE = "patient_signature"; // ลายเซ็นของผู้รับบริการ
    public static final String PROVIDER_SIGNATURE = "provider_signature"; // ลายเซ็นของผู้ให้บริการ
    public static final String CREATED_BY = "created_by";             // ผู้บันทึกข้อมูล
    public static final String CREATED_DATE = "created_date";         // วันเวลาที่บันทึก
    public static final String UPDATED_BY = "updated_by";             // ผู้แก้ไขข้อมูล
    public static final String UPDATED_DATE = "updated_date";         // วันเวลาที่แก้ไข
    public static final String PCUCODE = "pcucode";                   // รหัส PCU

    public static final String DATEUPDATE = "dateupdate";           // วันที่อัพเดทข้อมูล
    public static final String UPDATE_STATUS = "update_status";       // สถานะการอัพเดท

    // สร้างตาราง Counseling Signature
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VISIT_NO + " TEXT NOT NULL, "
            + PERSON_ID + " TEXT NOT NULL, "
            + COUNSELING_TYPE + " INTEGER NOT NULL, "
            + DETAIL + " TEXT, "
            + REFERRAL_DETAIL + " TEXT, "
            + PATIENT_SIGNATURE + " BLOB, "
            + PROVIDER_SIGNATURE + " BLOB, "
            + CREATED_BY + " TEXT, "
            + CREATED_DATE + " DATETIME, "
            + UPDATED_BY + " TEXT, "
            + UPDATED_DATE + " DATETIME, "
            + PCUCODE + " TEXT, "
            + UPDATE_STATUS + " TEXT,"
            + DATEUPDATE + " DATETIME "
            + ");";
    public static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLENAME;

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(VISIT_NO, VISIT_NO);
        PROJECTION_MAP.put(PERSON_ID, PERSON_ID);
        PROJECTION_MAP.put(COUNSELING_TYPE, COUNSELING_TYPE);
        PROJECTION_MAP.put(DETAIL, DETAIL);
        PROJECTION_MAP.put(REFERRAL_DETAIL, REFERRAL_DETAIL);
        PROJECTION_MAP.put(PATIENT_SIGNATURE, PATIENT_SIGNATURE);
        PROJECTION_MAP.put(PROVIDER_SIGNATURE, PROVIDER_SIGNATURE);
        PROJECTION_MAP.put(CREATED_BY, CREATED_BY);
        PROJECTION_MAP.put(CREATED_DATE, CREATED_DATE);
        PROJECTION_MAP.put(UPDATED_BY, UPDATED_BY);
        PROJECTION_MAP.put(UPDATED_DATE, UPDATED_DATE);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(UPDATE_STATUS, UPDATE_STATUS);
        PROJECTION_MAP.put(DATEUPDATE, DATEUPDATE);
    }

    public static Uri getContentUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}
