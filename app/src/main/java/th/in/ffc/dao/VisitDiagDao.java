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

    /**
     * ดึงข้อมูลการวินิจฉัยทั้งหมด
     */
    public static List<VisitDiagInfo> getAllVisitDiag() {
        Cursor cursor = mContext.getContentResolver().query(getVisitDiagUri(), null, null, null, VisitDiag.DEFAULT_SORTING);
        List<VisitDiagInfo> diagList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, VisitDiag.NO));
                diag.setPcucode(getStringFromCursor(cursor, VisitDiag.PCUCODE));
                diag.setDiagcode(getStringFromCursor(cursor, VisitDiag.CODE));
                diag.setDxtype(getStringFromCursor(cursor, VisitDiag.TYPE));
                diag.setClinic(getStringFromCursor(cursor, VisitDiag.CLINIC));
                diag.setContinue(getStringFromCursor(cursor, VisitDiag.CONTINUE));
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
                diag.setContinue(getStringFromCursor(cursor, VisitDiag.CONTINUE));
                diag.setAppointdate(getStringFromCursor(cursor, VisitDiag.APPOINT_DATE));
                diag.setAppointtype(getStringFromCursor(cursor, VisitDiag.APPOINT_TYPE));
                diag.setDoctor(getStringFromCursor(cursor, VisitDiag.DOCTOR));
                diag.setDateupdate(getStringFromCursor(cursor, VisitDiag.DATEUPDATE));
            }
            cursor.close();
        }
        return diag;
    }

    /**
     * ค้นหาข้อมูลการวินิจฉัยทั้งหมดของการเข้าเยี่ยมหนึ่งครั้ง
     */
    public static List<VisitDiagInfo> getVisitDiagByVisitNo(String visitno) {
        String select = "visitno = ?";
        String[] selectionArgs = new String[]{visitno};
        Cursor cursor = mContext.getContentResolver().query(getVisitDiagUri(), null, select, selectionArgs, VisitDiag.DEFAULT_SORTING);
        List<VisitDiagInfo> diagList = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                VisitDiagInfo diag = new VisitDiagInfo();
                diag.setVisitno(getStringFromCursor(cursor, VisitDiag.NO));
                diag.setPcucode(getStringFromCursor(cursor, VisitDiag.PCUCODE));
                diag.setDiagcode(getStringFromCursor(cursor, VisitDiag.CODE));
                diag.setDxtype(getStringFromCursor(cursor, VisitDiag.TYPE));
                diag.setClinic(getStringFromCursor(cursor, VisitDiag.CLINIC));
                diag.setContinue(getStringFromCursor(cursor, VisitDiag.CONTINUE));
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
     * เพิ่มข้อมูลการวินิจฉัย
     */
    public static String insert(VisitDiagInfo data) {
        try {
            ContentValues values = new ContentValues();
            values = getPutValueVisitDiag(data, values);
            Uri uri = mContext.getContentResolver().insert(VisitDiag.CONTENT_URI, values);
            String id = "";

            // ดึง ID ที่ได้จากการ insert (ในกรณีนี้คือ visitno)
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
            Uri uri = getVisitDiagUriById(data.getVisitno(), data.getDiagcode());
            mContext.getContentResolver().update(uri, values, select, selectionArgs);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * ลบข้อมูลการวินิจฉัย
     */
    public static long delete(String visitno, String diagcode) {
        try {
            String select = "visitno=? AND diagcode=?";
            String[] selectionArgs = {visitno, diagcode};
            mContext.getContentResolver().delete(getVisitDiagUri(), select, selectionArgs);
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
            int count = mContext.getContentResolver().delete(getVisitDiagUri(), select, selectionArgs);
            return count;
        } catch (Exception e) {
            return 0;
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
                diag.setContinue(getStringFromCursor(cursor, VisitDiag.CONTINUE));
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
                diag.setContinue(getStringFromCursor(cursor, VisitDiag.CONTINUE));
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
        putString(values, VisitDiag.CONTINUE, data.getContinue());
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