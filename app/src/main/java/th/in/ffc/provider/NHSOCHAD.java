package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Constants สำหรับแฟ้มที่ 7 NHSO CHAD
 */
public class NHSOCHAD implements BaseColumns {

    public static final String TABLENAME = "ffc_nhso_chad";

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsochad";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsochad";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOCHADProvider.AUTHORITY + "/nhso_chad");

    // คอลัมน์ในตาราง
    public static final String ID = _ID;
    public static final String SEQ = "seq";                    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    public static final String STDCODE = "stdcode";            // รหัสของรหัสมาตรฐานสากล
    public static final String INVOICE_NO = "invoice_no";      // เลขที่อ้างอิงในแจ้งหนี้ของหน่วยบริการ
    public static final String SERVDATE = "servdate";          // วันที่ ให้/ใช้ บริการ/ทรัพยากร
    public static final String LOCALCODE = "localcode";        // รหัสรายการค่าบริการที่สถานพยาบาลกำหนด
    public static final String DESCRIPT = "descript";          // ชื่อรายการที่สถานพยาบาลกำหนด
    public static final String QTY = "qty";                    // จำนวนหน่วยที่ใช้
    public static final String UNITPRICE = "unitprice";        // ราคาต่อหน่วยของ รพ.
    public static final String CHARGEAMT = "chargeamt";        // จำนวนเงินเรียกเก็บ
    public static final String BILLGRCS = "billgrcs";          // หมวดค่าใช้จ่าย
    public static final String CODESYS = "codesys";            // ระบบรหัสที่ใช้กับ STDCODE
    public static final String LAB_RESULT = "lab_result";      // ผลของการตรวจของห้องปฏิบัติการ
    public static final String UNIT = "unit";                  // หน่วยนับ
    public static final String REIMBPRICE = "reimbprice";      // ราคากลางต่อหน่วย
    public static final String XRAY_RESULT = "xray_result";    // ผลการตรวจรังสี
    public static final String PATHO_RESULT = "patho_result";  // ผลการตรวจทางพยาธิวิทยา
    public static final String USER = "user";                  // ผู้บันทึกข้อมูล
    public static final String PCUCODE = "pcucode";            // รหัส PCU
    public static final String UPDATE = "update_status";       // สถานะการอัพเดท
    public static final String DATEUPDATE = "dateupdate";      // วันเวลาที่อัพเดท

    // ตาราง NHSO CHAD
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLENAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEQ + " TEXT NOT NULL, "
            + STDCODE + " TEXT NOT NULL, "
            + INVOICE_NO + " TEXT NOT NULL, "
            + SERVDATE + " DATETIME NOT NULL, "
            + LOCALCODE + " TEXT, "
            + DESCRIPT + " TEXT, "
            + QTY + " INTEGER NOT NULL, "
            + UNITPRICE + " REAL NOT NULL, "
            + CHARGEAMT + " REAL NOT NULL, "
            + BILLGRCS + " TEXT NOT NULL, "
            + CODESYS + " TEXT NOT NULL, "
            + LAB_RESULT + " TEXT, "
            + UNIT + " TEXT, "
            + REIMBPRICE + " REAL, "
            + XRAY_RESULT + " TEXT, "
            + PATHO_RESULT + " TEXT, "
            + USER + " TEXT, "
            + PCUCODE + " TEXT, "
            + UPDATE + " TEXT, "
            + DATEUPDATE + " INTEGER"
            + ");";

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(STDCODE, STDCODE);
        PROJECTION_MAP.put(INVOICE_NO, INVOICE_NO);
        PROJECTION_MAP.put(SERVDATE, SERVDATE);
        PROJECTION_MAP.put(LOCALCODE, LOCALCODE);
        PROJECTION_MAP.put(DESCRIPT, DESCRIPT);
        PROJECTION_MAP.put(QTY, QTY);
        PROJECTION_MAP.put(UNITPRICE, UNITPRICE);
        PROJECTION_MAP.put(CHARGEAMT, CHARGEAMT);
        PROJECTION_MAP.put(BILLGRCS, BILLGRCS);
        PROJECTION_MAP.put(CODESYS, CODESYS);
        PROJECTION_MAP.put(LAB_RESULT, LAB_RESULT);
        PROJECTION_MAP.put(UNIT, UNIT);
        PROJECTION_MAP.put(REIMBPRICE, REIMBPRICE);
        PROJECTION_MAP.put(XRAY_RESULT, XRAY_RESULT);
        PROJECTION_MAP.put(PATHO_RESULT, PATHO_RESULT);
        PROJECTION_MAP.put(USER, USER);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(UPDATE, UPDATE);
        PROJECTION_MAP.put(DATEUPDATE, DATEUPDATE);
    }

    public static Uri getContentUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}