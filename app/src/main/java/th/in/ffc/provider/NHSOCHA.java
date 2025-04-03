package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Constants สำหรับแฟ้มที่ 8 NHSO CHA
 */
public class NHSOCHA implements BaseColumns {

    public static final String TABLENAME = "ffc_nhso_cha";

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsocha";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsocha";

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOCHAProvider.AUTHORITY + "/nhso_cha");

    // คอลัมน์ในตาราง
    public static final String ID = _ID;
    public static final String SEQ = "seq";                    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    public static final String DATE = "date";                  // วันที่ค่ารักษา
    public static final String CHRGITEM = "chrgitem";          // ชนิดของบริการที่คิดค่ารักษา
    public static final String INVOICE_NO = "invoice_no";      // เลขที่อ้างอิงในแจ้งหนี้ของหน่วยบริการ
    public static final String AMOUNT = "amount";              // จำนวนเงิน ค่ารักษาของบริการรายการนั้น
    public static final String TOTAL = "total";                // จำนวนเงินค่ารักษารวมหน่วยเป็นบาท
    public static final String OPD_MEMO = "opd_memo";          // รายละเอียดค่าบริการและการรักษาเพิ่มเติม
    public static final String USER = "user";                  // ผู้บันทึกข้อมูล
    public static final String PCUCODE = "pcucode";            // รหัส PCU
    public static final String UPDATE = "update_status";       // สถานะการอัพเดท
    public static final String DATEUPDATE = "dateupdate";      // วันเวลาที่อัพเดท

    // ตาราง NHSO CHA
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS  " + TABLENAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SEQ + " TEXT NOT NULL, "
            + DATE + " DATETIME NOT NULL, "
            + CHRGITEM + " TEXT NOT NULL, "
            + INVOICE_NO + " TEXT NOT NULL, "
            + AMOUNT + " REAL NOT NULL, "
            + TOTAL + " REAL NOT NULL, "
            + OPD_MEMO + " TEXT, "
            + USER + " TEXT, "
            + PCUCODE + " TEXT, "
            + UPDATE + " TEXT, "
            + DATEUPDATE + " INTEGER"
            + ");";

    public static final HashMap<String, String> PROJECTION_MAP = new HashMap<>();

    static {
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(SEQ, SEQ);
        PROJECTION_MAP.put(DATE, DATE);
        PROJECTION_MAP.put(CHRGITEM, CHRGITEM);
        PROJECTION_MAP.put(INVOICE_NO, INVOICE_NO);
        PROJECTION_MAP.put(AMOUNT, AMOUNT);
        PROJECTION_MAP.put(TOTAL, TOTAL);
        PROJECTION_MAP.put(OPD_MEMO, OPD_MEMO);
        PROJECTION_MAP.put(USER, USER);
        PROJECTION_MAP.put(PCUCODE, PCUCODE);
        PROJECTION_MAP.put(UPDATE, UPDATE);
        PROJECTION_MAP.put(DATEUPDATE, DATEUPDATE);
    }

    public static Uri getContentUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }
}