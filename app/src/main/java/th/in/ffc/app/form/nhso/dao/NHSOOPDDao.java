package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.app.form.nhso.model.NHSOOPDInfo;
import th.in.ffc.provider.NHSOOPD;

/**
 * Data Access Object สำหรับจัดการข้อมูลผู้ป่วยนอก NHSO (แฟ้ม 4)
 */
public class NHSOOPDDao {
    private static final String TAG = "NHSOOPDDao";

    private ContentResolver mResolver;
    private SimpleDateFormat dateFormat;

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOOPDDao(Context context) {
        this.mResolver = context.getContentResolver();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูลผู้ป่วยนอกใหม่
     * @param opdInfo ข้อมูลผู้ป่วยนอกที่ต้องการบันทึก
     * @return ID ของข้อมูลผู้ป่วยนอกที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertOPD(NHSOOPDInfo opdInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOOPD.SEQ, opdInfo.getSeq());

            if (opdInfo.getDateOPD() != null) {
                values.put(NHSOOPD.DATEOPD, dateFormat.format(opdInfo.getDateOPD()));
            }

            if (opdInfo.getInscl() != null) {
                values.put(NHSOOPD.INSCL, opdInfo.getInscl());
            }

            if (opdInfo.getPermitNo() != null) {
                values.put(NHSOOPD.PERMITNO, opdInfo.getPermitNo());
            }

            if (opdInfo.getHtype() != null) {
                values.put(NHSOOPD.HTYPE, opdInfo.getHtype());
            }

            if (opdInfo.getUuc() != null) {
                values.put(NHSOOPD.UUC, opdInfo.getUuc());
            }

            if (opdInfo.getChiefcomp() != null) {
                values.put(NHSOOPD.CHIEFCOMP, opdInfo.getChiefcomp());
            }

            if (opdInfo.getBtemp() != null) {
                values.put(NHSOOPD.BTEMP, opdInfo.getBtemp());
            }

            if (opdInfo.getSbp() != null) {
                values.put(NHSOOPD.SBP, opdInfo.getSbp());
            }

            if (opdInfo.getDbp() != null) {
                values.put(NHSOOPD.DBP, opdInfo.getDbp());
            }

            if (opdInfo.getPr() != null) {
                values.put(NHSOOPD.PR, opdInfo.getPr());
            }

            if (opdInfo.getRr() != null) {
                values.put(NHSOOPD.RR, opdInfo.getRr());
            }

            if (opdInfo.getWaistline() != null) {
                values.put(NHSOOPD.WAISTLINE, opdInfo.getWaistline());
            }

            if (opdInfo.getWeight() != null) {
                values.put(NHSOOPD.WEIGHT, opdInfo.getWeight());
            }

            if (opdInfo.getHeight() != null) {
                values.put(NHSOOPD.HEIGHT, opdInfo.getHeight());
            }

            if (opdInfo.getHeadcircum() != null) {
                values.put(NHSOOPD.HEADCIRCUM, opdInfo.getHeadcircum());
            }

            if (opdInfo.getClinic() != null) {
                values.put(NHSOOPD.CLINIC, opdInfo.getClinic());
            }

            values.put(NHSOOPD.USER, "unknown"); // ค่าเริ่มต้น
            values.put(NHSOOPD.UPDATE, "0");
            values.put(NHSOOPD.DATEUPDATE, System.currentTimeMillis());

            Uri uri = mResolver.insert(NHSOOPD.CONTENT_URI, values);
            if (uri != null) {
                return ContentUris.parseId(uri);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting OPD", e);
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลผู้ป่วยนอก
     * @param opdInfo ข้อมูลผู้ป่วยนอกที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateOPD(NHSOOPDInfo opdInfo) {
        try {
            ContentValues values = new ContentValues();

            if (opdInfo.getDateOPD() != null) {
                values.put(NHSOOPD.DATEOPD, dateFormat.format(opdInfo.getDateOPD()));
            }

            if (opdInfo.getInscl() != null) {
                values.put(NHSOOPD.INSCL, opdInfo.getInscl());
            }

            if (opdInfo.getPermitNo() != null) {
                values.put(NHSOOPD.PERMITNO, opdInfo.getPermitNo());
            }

            if (opdInfo.getHtype() != null) {
                values.put(NHSOOPD.HTYPE, opdInfo.getHtype());
            }

            if (opdInfo.getUuc() != null) {
                values.put(NHSOOPD.UUC, opdInfo.getUuc());
            }

            if (opdInfo.getChiefcomp() != null) {
                values.put(NHSOOPD.CHIEFCOMP, opdInfo.getChiefcomp());
            }

            if (opdInfo.getBtemp() != null) {
                values.put(NHSOOPD.BTEMP, opdInfo.getBtemp());
            }

            if (opdInfo.getSbp() != null) {
                values.put(NHSOOPD.SBP, opdInfo.getSbp());
            }

            if (opdInfo.getDbp() != null) {
                values.put(NHSOOPD.DBP, opdInfo.getDbp());
            }

            if (opdInfo.getPr() != null) {
                values.put(NHSOOPD.PR, opdInfo.getPr());
            }

            if (opdInfo.getRr() != null) {
                values.put(NHSOOPD.RR, opdInfo.getRr());
            }

            if (opdInfo.getWaistline() != null) {
                values.put(NHSOOPD.WAISTLINE, opdInfo.getWaistline());
            }

            if (opdInfo.getWeight() != null) {
                values.put(NHSOOPD.WEIGHT, opdInfo.getWeight());
            }

            if (opdInfo.getHeight() != null) {
                values.put(NHSOOPD.HEIGHT, opdInfo.getHeight());
            }

            if (opdInfo.getHeadcircum() != null) {
                values.put(NHSOOPD.HEADCIRCUM, opdInfo.getHeadcircum());
            }

            if (opdInfo.getClinic() != null) {
                values.put(NHSOOPD.CLINIC, opdInfo.getClinic());
            }

            values.put(NHSOOPD.UPDATE, "1");
            values.put(NHSOOPD.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOOPD.SEQ + "=?";
            String[] selectionArgs = {opdInfo.getSeq()};

            return mResolver.update(NHSOOPD.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating OPD", e);
            return 0;
        }
    }

    /**
     * อัปเดตสถานะการซิงค์ข้อมูล
     * @param seq รหัสการรับบริการ
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSyncStatus(String seq, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOOPD.UPDATE, status);
            values.put(NHSOOPD.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOOPD.SEQ + "=?";
            String[] selectionArgs = {seq};

            return mResolver.update(NHSOOPD.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลผู้ป่วยนอก
     * @param seq รหัสการรับบริการที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteOPD(String seq) {
        try {
            String selection = NHSOOPD.SEQ + "=?";
            String[] selectionArgs = {seq};

            return mResolver.delete(NHSOOPD.CONTENT_URI, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting OPD", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลผู้ป่วยนอกตาม ID
     * @param id ID ของข้อมูลผู้ป่วยนอก
     * @return ข้อมูลผู้ป่วยนอก หรือ null ถ้าไม่พบ
     */
    public NHSOOPDInfo getOPDById(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOOPD.CONTENT_URI, id);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOOPDInfo opdInfo = cursorToOPD(cursor);
                cursor.close();
                return opdInfo;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting OPD by ID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลผู้ป่วยนอกตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ข้อมูลผู้ป่วยนอก หรือ null ถ้าไม่พบ
     */
    public NHSOOPDInfo getOPDBySeq(String seq) {
        try {
            String selection = NHSOOPD.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOOPDInfo opdInfo = cursorToOPD(cursor);
                cursor.close();
                return opdInfo;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting OPD by SEQ", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลผู้ป่วยนอกทั้งหมด
     * @return รายการข้อมูลผู้ป่วยนอกทั้งหมด
     */
    public List<NHSOOPDInfo> getAllOPDs() {
        List<NHSOOPDInfo> opds = new ArrayList<>();

        try {
            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOOPDInfo opd = cursorToOPD(cursor);
                    opds.add(opd);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all OPDs", e);
        }

        return opds;
    }

    /**
     * ค้นหาข้อมูลผู้ป่วยนอกตามช่วงวันที่
     * @param startDate วันที่เริ่มต้น
     * @param endDate วันที่สิ้นสุด
     * @return รายการข้อมูลผู้ป่วยนอกที่พบ
     */
    public List<NHSOOPDInfo> findOPDsByDateRange(Date startDate, Date endDate) {
        List<NHSOOPDInfo> opds = new ArrayList<>();

        try {
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            String selection = NHSOOPD.DATEOPD + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOOPDInfo opd = cursorToOPD(cursor);
                    opds.add(opd);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding OPDs by date range", e);
        }

        return opds;
    }

    /**
     * ค้นหาข้อมูลผู้ป่วยนอกตามสิทธิการรักษา
     * @param inscl รหัสสิทธิการรักษา
     * @return รายการข้อมูลผู้ป่วยนอกที่พบ
     */
    public List<NHSOOPDInfo> findOPDsByInsurance(String inscl) {
        List<NHSOOPDInfo> opds = new ArrayList<>();

        try {
            String selection = NHSOOPD.INSCL + "=?";
            String[] selectionArgs = {inscl};

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOOPDInfo opd = cursorToOPD(cursor);
                    opds.add(opd);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding OPDs by insurance", e);
        }

        return opds;
    }

    /**
     * ค้นหาข้อมูลผู้ป่วยนอกตามแผนก
     * @param clinic รหัสแผนก
     * @return รายการข้อมูลผู้ป่วยนอกที่พบ
     */
    public List<NHSOOPDInfo> findOPDsByClinic(String clinic) {
        List<NHSOOPDInfo> opds = new ArrayList<>();

        try {
            String selection = NHSOOPD.CLINIC + "=?";
            String[] selectionArgs = {clinic};

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOOPDInfo opd = cursorToOPD(cursor);
                    opds.add(opd);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding OPDs by clinic", e);
        }

        return opds;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOOPDInfo
     * @param cursor Cursor จากการ query
     * @return ข้อมูลผู้ป่วยนอก
     */
    private NHSOOPDInfo cursorToOPD(Cursor cursor) {
        NHSOOPDInfo opdInfo = new NHSOOPDInfo();

        int idIndex = cursor.getColumnIndex(NHSOOPD.ID);
        if (idIndex != -1) {
            opdInfo.setId(cursor.getLong(idIndex));
        }

        int seqIndex = cursor.getColumnIndex(NHSOOPD.SEQ);
        if (seqIndex != -1) {
            opdInfo.setSeq(cursor.getString(seqIndex));
        }

        int dateOPDIndex = cursor.getColumnIndex(NHSOOPD.DATEOPD);
        if (dateOPDIndex != -1 && !cursor.isNull(dateOPDIndex)) {
            String dateOPDStr = cursor.getString(dateOPDIndex);
            try {
                opdInfo.setDateOPD(dateFormat.parse(dateOPDStr));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + dateOPDStr, e);
            }
        }

        int insclIndex = cursor.getColumnIndex(NHSOOPD.INSCL);
        if (insclIndex != -1 && !cursor.isNull(insclIndex)) {
            opdInfo.setInscl(cursor.getString(insclIndex));
        }

        int permitNoIndex = cursor.getColumnIndex(NHSOOPD.PERMITNO);
        if (permitNoIndex != -1 && !cursor.isNull(permitNoIndex)) {
            opdInfo.setPermitNo(cursor.getString(permitNoIndex));
        }

        int htypeIndex = cursor.getColumnIndex(NHSOOPD.HTYPE);
        if (htypeIndex != -1 && !cursor.isNull(htypeIndex)) {
            opdInfo.setHtype(cursor.getString(htypeIndex));
        }

        int uucIndex = cursor.getColumnIndex(NHSOOPD.UUC);
        if (uucIndex != -1 && !cursor.isNull(uucIndex)) {
            opdInfo.setUuc(cursor.getString(uucIndex));
        }

        int chiefcompIndex = cursor.getColumnIndex(NHSOOPD.CHIEFCOMP);
        if (chiefcompIndex != -1 && !cursor.isNull(chiefcompIndex)) {
            opdInfo.setChiefcomp(cursor.getString(chiefcompIndex));
        }

        int btempIndex = cursor.getColumnIndex(NHSOOPD.BTEMP);
        if (btempIndex != -1 && !cursor.isNull(btempIndex)) {
            opdInfo.setBtemp(cursor.getDouble(btempIndex));
        }

        int sbpIndex = cursor.getColumnIndex(NHSOOPD.SBP);
        if (sbpIndex != -1 && !cursor.isNull(sbpIndex)) {
            opdInfo.setSbp(cursor.getInt(sbpIndex));
        }

        int dbpIndex = cursor.getColumnIndex(NHSOOPD.DBP);
        if (dbpIndex != -1 && !cursor.isNull(dbpIndex)) {
            opdInfo.setDbp(cursor.getInt(dbpIndex));
        }

        int prIndex = cursor.getColumnIndex(NHSOOPD.PR);
        if (prIndex != -1 && !cursor.isNull(prIndex)) {
            opdInfo.setPr(cursor.getInt(prIndex));
        }

        int rrIndex = cursor.getColumnIndex(NHSOOPD.RR);
        if (rrIndex != -1 && !cursor.isNull(rrIndex)) {
            opdInfo.setRr(cursor.getInt(rrIndex));
        }

        int waistlineIndex = cursor.getColumnIndex(NHSOOPD.WAISTLINE);
        if (waistlineIndex != -1 && !cursor.isNull(waistlineIndex)) {
            opdInfo.setWaistline(cursor.getInt(waistlineIndex));
        }

        int weightIndex = cursor.getColumnIndex(NHSOOPD.WEIGHT);
        if (weightIndex != -1 && !cursor.isNull(weightIndex)) {
            opdInfo.setWeight(cursor.getDouble(weightIndex));
        }

        int heightIndex = cursor.getColumnIndex(NHSOOPD.HEIGHT);
        if (heightIndex != -1 && !cursor.isNull(heightIndex)) {
            opdInfo.setHeight(cursor.getInt(heightIndex));
        }

        int headcircumIndex = cursor.getColumnIndex(NHSOOPD.HEADCIRCUM);
        if (headcircumIndex != -1 && !cursor.isNull(headcircumIndex)) {
            opdInfo.setHeadcircum(cursor.getDouble(headcircumIndex));
        }

        int clinicIndex = cursor.getColumnIndex(NHSOOPD.CLINIC);
        if (clinicIndex != -1 && !cursor.isNull(clinicIndex)) {
            opdInfo.setClinic(cursor.getString(clinicIndex));
        }

        return opdInfo;
    }

    /**
     * ตรวจสอบว่ามีข้อมูลผู้ป่วยนอกหรือไม่
     * @param seq รหัสการรับบริการ
     * @return true ถ้ามี, false ถ้าไม่มี
     */
    public boolean isOPDExists(String seq) {
        try {
            String selection = NHSOOPD.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI,
                    new String[]{NHSOOPD.ID}, selection, selectionArgs, null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if OPD exists", e);
            return false;
        }
    }

    /**
     * นับจำนวนผู้ป่วยนอกทั้งหมด
     * @return จำนวนผู้ป่วยนอกทั้งหมด
     */
    public int countAllOPDs() {
        try {
            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, null, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting OPDs", e);
            return 0;
        }
    }

    /**
     * นับจำนวนผู้ป่วยนอกที่ยังไม่ได้ซิงค์
     * @return จำนวนผู้ป่วยนอกที่ยังไม่ได้ซิงค์
     */
    public int countUnsyncedOPDs() {
        try {
            String selection = NHSOOPD.UPDATE + "='0' OR " + NHSOOPD.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, selection, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting unsynced OPDs", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลผู้ป่วยนอกที่ยังไม่ได้ซิงค์
     * @return รายการผู้ป่วยนอกที่ยังไม่ได้ซิงค์
     */
    public List<NHSOOPDInfo> getUnsyncedOPDs() {
        List<NHSOOPDInfo> opds = new ArrayList<>();

        try {
            String selection = NHSOOPD.UPDATE + "='0' OR " + NHSOOPD.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOOPD.CONTENT_URI, null, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOOPDInfo opd = cursorToOPD(cursor);
                    opds.add(opd);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting unsynced OPDs", e);
        }

        return opds;
    }
}