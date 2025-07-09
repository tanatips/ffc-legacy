package th.in.ffc.dao;

import static th.in.ffc.util.DataFromCursor.getDoubleFromCursor;
import static th.in.ffc.util.DataFromCursor.getIntegerFromCursor;
import static th.in.ffc.util.DataFromCursor.getStringFromCursor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.app.form.screening.model.VisitDiagInfo;
import th.in.ffc.provider.PersonProvider;
import th.in.ffc.provider.PersonProvider.VisitDiag;

/**
 * Data Access Object for VisitDiag table
 * สำหรับจัดการข้อมูลการวินิจฉัยของการเข้าเยี่ยม
 */
public class VisitDiagDao {
    private static Context mContext;

    public VisitDiagDao(Context context) {
        this.mContext = context;
    }

    /**
     * สร้าง Uri สำหรับ query VisitDiag ด้วย visitno และ diagcode
     */
    public static Uri getVisitDiagUriById(String visitno, String diagcode) {
        return VisitDiag.getContentUriId(Long.parseLong(visitno), diagcode);
    }

    /**
     * สร้าง Uri สำหรับ query VisitDiag ทั้งหมด
     */
    public static Uri getVisitDiagUri() {
        return VisitDiag.CONTENT_URI;
    }
    public static Uri getVisitDiagDirectUri() {
        return Uri.parse("content://" + PersonProvider.AUTHORITY + "/visitdiag");
    }

    /**
     * ดึงข้อมูลการวินิจฉัยทั้งหมด
     */
    public static List<VisitDiagInfo> getAllVisitDiag() {
        Cursor cursor = mContext.getContentResolver().query(
                getVisitDiagDirectUri(),  // ใช้ Direct URI
                null,
                null,
                null,
                VisitDiag.DEFAULT_SORTING
        );

        List<VisitDiagInfo> diagList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, "visitno"));
                diag.setPcucode(getStringFromCursor(cursor, "pcucode"));
                diag.setDiagcode(getStringFromCursor(cursor, "diagcode"));
                diag.setDxtype(getStringFromCursor(cursor, "dxtype"));
                diag.setClinic(getStringFromCursor(cursor, "clinic"));
                diag.setConti(getStringFromCursor(cursor, "conti"));
                diag.setAppointdate(getStringFromCursor(cursor, "appointdate"));
                diag.setAppointtype(getStringFromCursor(cursor, "appointtype"));
                diag.setDoctor(getStringFromCursor(cursor, "doctordiag"));
                diag.setDateupdate(getStringFromCursor(cursor, "dateupdate"));
                diagList.add(diag);
            }
            cursor.close();
        }
        return diagList;
    }

    /**
     * ค้นหาข้อมูลการวินิจฉัยตาม visitno และ diagcode
     */
    public static VisitDiagInfo getVisitDiagById(String visitno, String diagcode) {
        String select = "visitno = ? AND diagcode = ?";
        String[] selectionArgs = new String[]{visitno, diagcode};
        Cursor cursor = mContext.getContentResolver().query(getVisitDiagUri(), null, select, selectionArgs, null);
        VisitDiagInfo diag = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, VisitDiag.NO));
                diag.setPcucode(getStringFromCursor(cursor, VisitDiag.PCUCODE));
                diag.setDiagcode(getStringFromCursor(cursor, VisitDiag.CODE));
                diag.setDxtype(getStringFromCursor(cursor, VisitDiag.TYPE));
                diag.setClinic(getStringFromCursor(cursor, VisitDiag.CLINIC));
                diag.setConti(getStringFromCursor(cursor, VisitDiag.CONTINUE));
                diag.setAppointdate(getStringFromCursor(cursor, VisitDiag.APPOINT_DATE));
                diag.setAppointtype(getStringFromCursor(cursor, VisitDiag.APPOINT_TYPE));
                diag.setDoctor(getStringFromCursor(cursor, VisitDiag.DOCTOR));
                diag.setDateupdate(getStringFromCursor(cursor, VisitDiag.DATEUPDATE));
            }
            cursor.close();
        }
        return diag;
    }
    public static VisitDiagInfo getVisitDiagByVisitNoAndPcucode(String visitno, String pcucode) {
        return getVisitDiagByVisitNoAndPcucodeSimple(visitno, pcucode);
    }
    public static VisitDiagInfo getVisitDiagByVisitNoAndPcucodeSimple(String visitno, String pcucode) {
        String select = "visitno = ? AND pcucode = ?";
        String[] selectionArgs = new String[]{visitno, pcucode};

        Cursor cursor = null;
        VisitDiagInfo diag = null;

        try {
            // ใช้ Direct URI แทน
            cursor = mContext.getContentResolver().query(
                    getVisitDiagDirectUri(),  // เปลี่ยนจาก getVisitDiagUri()
                    null,  // ดึงทุกคอลัมน์
                    select,
                    selectionArgs,
                    "visitno ASC, pcucode ASC"  // เรียงลำดับตาม visitno และ pcucode
            );

            if (cursor != null && cursor.moveToFirst()) {
                diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, "visitno"));
                diag.setPcucode(getStringFromCursor(cursor, "pcucode"));
                diag.setDiagcode(getStringFromCursor(cursor, "diagcode"));
                diag.setDxtype(getStringFromCursor(cursor, "dxtype"));
                diag.setClinic(getStringFromCursor(cursor, "clinic"));
                diag.setConti(getStringFromCursor(cursor, "conti"));
                diag.setAppointdate(getStringFromCursor(cursor, "appointdate"));
                diag.setAppointtype(getStringFromCursor(cursor, "appointtype"));
                diag.setDoctor(getStringFromCursor(cursor, "doctordiag"));
                diag.setDateupdate(getStringFromCursor(cursor, "dateupdate"));
            }
        } catch (Exception e) {
            System.out.println("Error in simple query: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return diag;
    }

    /**
     * ค้นหาข้อมูลการวินิจฉัยทั้งหมดของการเข้าเยี่ยมหนึ่งครั้ง
     */
    public static List<VisitDiagInfo> getVisitDiagByVisitNo(String visitno) {
        String select = "visitno = ?";
        String[] selectionArgs = new String[]{visitno};
        Cursor cursor = mContext.getContentResolver().query(
                getVisitDiagDirectUri(),  // ใช้ Direct URI
                null,
                select,
                selectionArgs,
                VisitDiag.DEFAULT_SORTING
        );

        List<VisitDiagInfo> diagList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, "visitno"));
                diag.setPcucode(getStringFromCursor(cursor, "pcucode"));
                diag.setDiagcode(getStringFromCursor(cursor, "diagcode"));
                diag.setDxtype(getStringFromCursor(cursor, "dxtype"));
                diag.setClinic(getStringFromCursor(cursor, "clinic"));
                diag.setConti(getStringFromCursor(cursor, "conti"));
                diag.setAppointdate(getStringFromCursor(cursor, "appointdate"));
                diag.setAppointtype(getStringFromCursor(cursor, "appointtype"));
                diag.setDoctor(getStringFromCursor(cursor, "doctordiag"));
                diag.setDateupdate(getStringFromCursor(cursor, "dateupdate"));
                diagList.add(diag);
            }
            cursor.close();
        }
        return diagList;
    }

    /**
     * เพิ่มข้อมูลการวินิจฉัย
     */
    public static String insert(VisitDiagInfo data) {
        try {
            ContentValues values = new ContentValues();
            values = getPutValueVisitDiag(data, values);
            // ใช้ URI เดิมสำหรับ insert เพราะ insert ต้องผ่าน PersonProvider
            Uri uri = mContext.getContentResolver().insert(getVisitDiagDirectUri(), values);
            String id = "";

            if (uri != null) {
                id = data.getVisitno();
            }
            return id;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * อัพเดทข้อมูลการวินิจฉัย
     */
    public static long update(VisitDiagInfo data) {
        try {
            String select = "visitno=? AND diagcode=?";
            String[] selectionArgs = {data.getVisitno(), data.getDiagcode()};
            ContentValues values = new ContentValues();
            values = getPutValueVisitDiag(data, values);
            // ใช้ URI เดิมสำหรับ update
            Uri uri = getVisitDiagUriById(data.getVisitno(), data.getDiagcode());
            mContext.getContentResolver().update(getVisitDiagDirectUri(), values, select, selectionArgs);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * ลบข้อมูลการวินิจฉัย
     */
    public static long delete(String visitno, String pcucode) {
        try {
            String select = "visitno=? AND pcucode=?";
            String[] selectionArgs = {visitno, pcucode};
            // ใช้ Direct URI สำหรับ delete เพื่อหลีกเลี่ยงปัญหา JOIN
            mContext.getContentResolver().delete(getVisitDiagDirectUri(), select, selectionArgs);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * ลบข้อมูลการวินิจฉัยทั้งหมดของการเข้าเยี่ยมหนึ่งครั้ง
     */
    public static long deleteAllByVisitNo(String visitno) {
        try {
            String select = "visitno=?";
            String[] selectionArgs = {visitno};
            int count = mContext.getContentResolver().delete(getVisitDiagDirectUri(), select, selectionArgs);
            return count;
        } catch (Exception e) {
            return 0;
        }
    }
    public static void debugVisitDiagColumns() {
        Cursor cursor = null;
        try {
            System.out.println("===== Testing Original URI =====");
            cursor = mContext.getContentResolver().query(
                    getVisitDiagUri(),
                    null, null, null, null
            );

            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                System.out.println("Original URI: " + getVisitDiagUri());
                for (int i = 0; i < columnNames.length; i++) {
                    System.out.println("Column " + i + ": " + columnNames[i]);
                }
                cursor.close();
            }

            System.out.println("===== Testing Direct URI =====");
            cursor = mContext.getContentResolver().query(
                    getVisitDiagDirectUri(),
                    null, null, null, null
            );

            if (cursor != null) {
                String[] columnNames = cursor.getColumnNames();
                System.out.println("Direct URI: " + getVisitDiagDirectUri());
                for (int i = 0; i < columnNames.length; i++) {
                    System.out.println("Column " + i + ": " + columnNames[i]);
                }
                cursor.close();
            }

        } catch (Exception e) {
            System.out.println("Error debugging columns: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    /**
     * ค้นหาข้อมูลการวินิจฉัยตามเงื่อนไขต่างๆ
     */
    public static List<VisitDiagInfo> searchVisitDiag(String visitno, String diagcode, String dxtype) {
        List<VisitDiagInfo> results = new ArrayList<>();

        // สร้าง where clause และ arguments
        List<String> whereConditions = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (visitno != null && !visitno.isEmpty()) {
            whereConditions.add("visitno = ?");
            whereArgs.add(visitno);
        }

        if (diagcode != null && !diagcode.isEmpty()) {
            whereConditions.add("diagcode LIKE ?");
            whereArgs.add("%" + diagcode + "%");
        }

        if (dxtype != null && !dxtype.isEmpty()) {
            whereConditions.add("dxtype = ?");
            whereArgs.add(dxtype);
        }

        // สร้าง where clause string
        String whereClause = null;
        if (!whereConditions.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereConditions);
        }

        // แปลง List<String> เป็น String[]
        String[] selectionArgs = whereArgs.toArray(new String[0]);

        // Query ข้อมูล
        Cursor cursor = mContext.getContentResolver().query(
                getVisitDiagUri(),
                null,
                whereClause,
                selectionArgs,
                VisitDiag.DEFAULT_SORTING
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, VisitDiag.NO));
                diag.setPcucode(getStringFromCursor(cursor, VisitDiag.PCUCODE));
                diag.setDiagcode(getStringFromCursor(cursor, VisitDiag.CODE));
                diag.setDxtype(getStringFromCursor(cursor, VisitDiag.TYPE));
                diag.setClinic(getStringFromCursor(cursor, VisitDiag.CLINIC));
                diag.setConti(getStringFromCursor(cursor, VisitDiag.CONTINUE));
                diag.setAppointdate(getStringFromCursor(cursor, VisitDiag.APPOINT_DATE));
                diag.setAppointtype(getStringFromCursor(cursor, VisitDiag.APPOINT_TYPE));
                diag.setDoctor(getStringFromCursor(cursor, VisitDiag.DOCTOR));
                diag.setDateupdate(getStringFromCursor(cursor, VisitDiag.DATEUPDATE));
                results.add(diag);
            }
            cursor.close();
        }
        return results;
    }

    /**
     * ดึงข้อมูลการวินิจฉัยตามประเภทของการวินิจฉัย
     */
    public static List<VisitDiagInfo> getVisitDiagByDxType(String dxtype) {
        String select = "dxtype = ?";
        String[] selectionArgs = {dxtype};
        Cursor cursor = mContext.getContentResolver().query(
                getVisitDiagUri(),
                null,
                select,
                selectionArgs,
                VisitDiag.DEFAULT_SORTING);

        List<VisitDiagInfo> diagList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, VisitDiag.NO));
                diag.setPcucode(getStringFromCursor(cursor, VisitDiag.PCUCODE));
                diag.setDiagcode(getStringFromCursor(cursor, VisitDiag.CODE));
                diag.setDxtype(getStringFromCursor(cursor, VisitDiag.TYPE));
                diag.setClinic(getStringFromCursor(cursor, VisitDiag.CLINIC));
                diag.setConti(getStringFromCursor(cursor, VisitDiag.CONTINUE));
                diag.setAppointdate(getStringFromCursor(cursor, VisitDiag.APPOINT_DATE));
                diag.setAppointtype(getStringFromCursor(cursor, VisitDiag.APPOINT_TYPE));
                diag.setDoctor(getStringFromCursor(cursor, VisitDiag.DOCTOR));
                diag.setDateupdate(getStringFromCursor(cursor, VisitDiag.DATEUPDATE));
                diagList.add(diag);
            }
            cursor.close();
        }
        return diagList;
    }

    /**
     * Helper method เพื่อสร้าง ContentValues สำหรับการ insert และ update
     */
    private static ContentValues getPutValueVisitDiag(VisitDiagInfo data, ContentValues values) {
        putString(values, VisitDiag.PCUCODE, data.getPcucode());
        putString(values, VisitDiag.NO, data.getVisitno());
        putString(values, VisitDiag.CODE, data.getDiagcode());
        putString(values, VisitDiag.TYPE, data.getDxtype());
        putString(values, VisitDiag.CLINIC, data.getClinic());
        putString(values, VisitDiag.CONTINUE, data.getConti());
        putString(values, VisitDiag.APPOINT_DATE, data.getAppointdate());
        putString(values, VisitDiag.APPOINT_TYPE, data.getAppointtype());
        putString(values, VisitDiag.DOCTOR, data.getDoctor());
        putString(values, VisitDiag.DATEUPDATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        return values;
    }

    /**
     * Helper methods สำหรับการใส่ค่าลงใน ContentValues
     */
    private static void putString(ContentValues values, String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    private static void putInt(ContentValues values, String key, Integer value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    private static void putDouble(ContentValues values, String key, Double value) {
        if (value != null) {
            values.put(key, value);
        }
    }

    /**
     * Model class สำหรับตาราง VisitDiag
     */

}