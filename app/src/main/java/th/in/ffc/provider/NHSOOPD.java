package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Constants สำหรับแฟ้มที่ 4 NHSO OPD
 */
public class NHSOOPD implements BaseColumns {

    public static final String TABLENAME = "ffc_nhso_opd";

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsoopd";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsoopd";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOOPDProvider.AUTHORITY + "/nhso_opd");

    // คอลัมน์ในตาราง
    public static final String ID = _ID;
    public static final String SEQ = "seq";                    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    public static final String DATEOPD = "dateopd";            // วัน เวลา ที่รับบริการ
    public static final String INSCL = "inscl";                // สิทธิการรักษาที่ใช้
    public static final String PERMITNO = "permitno";          // รหัส Claim Code/เลขอนุมัติ/เลข Approve code
    public static final String HTYPE = "htype";                // ประเภทสถานพยาบาลที่รักษา
    public static final String UUC = "uuc";                    // การใช้สิทธิ
    public static final String CHIEFCOMP = "chiefcomp";        // อาการสำคัญที่มารับบริการ
    public static final String BTEMP = "btemp";                // อุณหภูมิร่างกาย
    public static final String SBP = "sbp";                    // ความดันโลหิตค่าบน
    public static final String DBP = "dbp";                    // ความดันโลหิตค่าล่าง
    public static final String PR = "pr";                      // อัตราการเต้นหัวใจ
    public static final String RR = "rr";                      // อัตราการหายใจ
    public static final String WAISTLINE = "waistline";        // รอบเอว
    public static final String WEIGHT = "weight";              // น้ำหนัก
    public static final String HEIGHT = "height";              // ส่วนสูง
    public static final String HEADCIRCUM = "headcircum";      // เส้นรอบศีรษะ
    public static final String CLINIC = "clinic";              // แผนกที่รักษาผู้ป่วยเป็นหลัก
    public static final String USER = "user";                  // ผู้บันทึกข้อมูล
    public static final String PCUCODE = "pcucode";            // รหัส PCU
    public static final String UPDATE = "update_status";       // สถานะการอัพเดท
    public static final String DATEUPDATE = "dateupdate";      // วันเวลาที่อัพเดท

    // ตาราง NHSO OPD
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEQ + " TEXT NOT NULL, "
            + DATEOPD + " DATETIME, "
            + INSCL + " TEXT, "
            + PERMITNO + " TEXT, "
            + HTYPE + " TEXT, "
            + UUC + " TEXT, "
            + CHIEFCOMP + " TEXT, "
            + BTEMP + " REAL, "
            + SBP + " INTEGER, "
            + DBP + " INTEGER, "
            + PR + " INTEGER, "
            + RR + " INTEGER, "
            + WAISTLINE + " INTEGER, "
            + WEIGHT + " REAL, "
            + HEIGHT + " INTEGER, "
            + HEADCIRCUM + " REAL, "
            + CLINIC + " TEXT, "
            + USER + " TEXT, "
            + PCUCODE + " TEXT, "
            + UPDATE + " TEXT, "
            + DATEUPDATE + " INTEGER"
            + ");";

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(DATEOPD, DATEOPD);
        PROJECTION_MAP.put(INSCL, INSCL);
        PROJECTION_MAP.put(PERMITNO, PERMITNO);
        PROJECTION_MAP.put(HTYPE, HTYPE);
        PROJECTION_MAP.put(UUC, UUC);
        PROJECTION_MAP.put(CHIEFCOMP, CHIEFCOMP);
        PROJECTION_MAP.put(BTEMP, BTEMP);
        PROJECTION_MAP.put(SBP, SBP);
        PROJECTION_MAP.put(DBP, DBP);
        PROJECTION_MAP.put(PR, PR);
        PROJECTION_MAP.put(RR, RR);
        PROJECTION_MAP.put(WAISTLINE, WAISTLINE);
        PROJECTION_MAP.put(WEIGHT, WEIGHT);
        PROJECTION_MAP.put(HEIGHT, HEIGHT);
        PROJECTION_MAP.put(HEADCIRCUM, HEADCIRCUM);
        PROJECTION_MAP.put(CLINIC, CLINIC);
        PROJECTION_MAP.put(USER, USER);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(UPDATE, UPDATE);
        PROJECTION_MAP.put(DATEUPDATE, DATEUPDATE);
    }

    public static Uri getContentUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}