package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.provider.NHSOHospital;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;

/**
 * Data Access Object สำหรับจัดการข้อมูลสถานพยาบาล NHSO
 */
public class NHSOHospitalDao {
    private static final String TAG = "NHSOHospitalDao";

    private ContentResolver mResolver;

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOHospitalDao(Context context) {
        this.mResolver = context.getContentResolver();
    }

    /**
     * บันทึกข้อมูลสถานพยาบาลใหม่
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการบันทึก
     * @return ID ของข้อมูลสถานพยาบาลที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertHospital(NHSOHospitalInfo hospital) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOHospital.SEQ, hospital.getSeq());
            values.put(NHSOHospital.HCODE, hospital.getHcode());
            values.put(NHSOHospital.HCODE_NAME, hospital.getHcodeName());
            values.put(NHSOHospital.HCODE_SEND, hospital.getHcodeSend());
            values.put(NHSOHospital.HCODE_SEND_NAME, hospital.getHcodeSendName());
            values.put(NHSOHospital.HMAIN, hospital.getHmain());
            values.put(NHSOHospital.HMAIN_NAME, hospital.getHmainName());
            values.put(NHSOHospital.USER_CREATE, hospital.getCreatedBy());

            Uri uri = mResolver.insert(NHSOHospital.CONTENT_URI, values);
            if (uri != null) {
                return ContentUris.parseId(uri);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting hospital", e);
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลสถานพยาบาล
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateHospital(NHSOHospitalInfo hospital) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOHospital.HCODE_NAME, hospital.getHcodeName());
            values.put(NHSOHospital.HCODE_SEND, hospital.getHcodeSend());
            values.put(NHSOHospital.HCODE_SEND_NAME, hospital.getHcodeSendName());
            values.put(NHSOHospital.HMAIN, hospital.getHmain());
            values.put(NHSOHospital.HMAIN_NAME, hospital.getHmainName());
            values.put(NHSOHospital.USER_UPDATE, hospital.getUpdatedBy());
            values.put(NHSOHospital.UPDATETIME, hospital.getUpdatedDate());

            Uri uri = ContentUris.withAppendedId(NHSOHospital.CONTENT_URI, hospital.getId());
            return mResolver.update(uri, values, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error updating hospital", e);
            return 0;
        }
    }

    /**
     * อัปเดตสถานะการซิงค์ข้อมูล
     * @param hospitalId ID ของสถานพยาบาล
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSyncStatus(long hospitalId, boolean status) {
        try {
            ContentValues values = new ContentValues();

            // สมมติว่ามี field ชื่อ sync_status ใน table
            values.put("sync_status", status ? 1 : 0);

            Uri uri = ContentUris.withAppendedId(NHSOHospital.CONTENT_URI, hospitalId);
            return mResolver.update(uri, values, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลสถานพยาบาล
     * @param hospitalId ID ของสถานพยาบาลที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteHospital(long hospitalId) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOHospital.CONTENT_URI, hospitalId);
            return mResolver.delete(uri, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting hospital", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลสถานพยาบาลตาม ID
     * @param hospitalId ID ของสถานพยาบาล
     * @return ข้อมูลสถานพยาบาล หรือ null ถ้าไม่พบ
     */
    public NHSOHospitalInfo getHospitalById(long hospitalId) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOHospital.CONTENT_URI, hospitalId);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOHospitalInfo hospital = cursorToHospital(cursor);
                cursor.close();
                return hospital;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting hospital by ID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลสถานพยาบาลตามรหัสสถานพยาบาล
     * @param hcode รหัสสถานพยาบาล
     * @return ข้อมูลสถานพยาบาล หรือ null ถ้าไม่พบ
     */
    public NHSOHospitalInfo getHospitalByHcode(String hcode) {
        try {
            String selection = NHSOHospital.HCODE + "=?";
            String[] selectionArgs = {hcode};

            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOHospitalInfo hospital = cursorToHospital(cursor);
                cursor.close();
                return hospital;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting hospital by hcode", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลสถานพยาบาลทั้งหมด
     * @return รายการข้อมูลสถานพยาบาลทั้งหมด
     */
    public List<NHSOHospitalInfo> getAllHospitals() {
        List<NHSOHospitalInfo> hospitals = new ArrayList<>();

        try {
            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOHospitalInfo hospital = cursorToHospital(cursor);
                    hospitals.add(hospital);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all hospitals", e);
        }

        return hospitals;
    }

    /**
     * ค้นหาสถานพยาบาลตามชื่อ
     * @param name ชื่อหรือส่วนหนึ่งของชื่อสถานพยาบาล
     * @return รายการข้อมูลสถานพยาบาลที่ตรงกับเงื่อนไข
     */
    public List<NHSOHospitalInfo> findHospitalsByName(String name) {
        List<NHSOHospitalInfo> hospitals = new ArrayList<>();

        try {
            String selection = NHSOHospital.HCODE_NAME + " LIKE ? OR " +
                    NHSOHospital.HCODE_SEND_NAME + " LIKE ? OR " +
                    NHSOHospital.HMAIN_NAME + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%", "%" + name + "%", "%" + name + "%"};

            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOHospitalInfo hospital = cursorToHospital(cursor);
                    hospitals.add(hospital);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding hospitals by name", e);
        }

        return hospitals;
    }

    /**
     * แปลง Cursor เป็น NHSOHospitalInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูลสถานพยาบาล
     */
    private NHSOHospitalInfo cursorToHospital(Cursor cursor) {
        NHSOHospitalInfo hospital = new NHSOHospitalInfo();

        hospital.setId(cursor.getLong(cursor.getColumnIndex(NHSOHospital.ID)));
        hospital.setSeq(cursor.getString(cursor.getColumnIndex(NHSOHospital.SEQ)));
        hospital.setHcode(cursor.getString(cursor.getColumnIndex(NHSOHospital.HCODE)));

        int hcodeNameIndex = cursor.getColumnIndex(NHSOHospital.HCODE_NAME);
        if (hcodeNameIndex != -1 && !cursor.isNull(hcodeNameIndex)) {
            hospital.setHcodeName(cursor.getString(hcodeNameIndex));
        }

        int hcodeSendIndex = cursor.getColumnIndex(NHSOHospital.HCODE_SEND);
        if (hcodeSendIndex != -1 && !cursor.isNull(hcodeSendIndex)) {
            hospital.setHcodeSend(cursor.getString(hcodeSendIndex));
        }

        int hcodeSendNameIndex = cursor.getColumnIndex(NHSOHospital.HCODE_SEND_NAME);
        if (hcodeSendNameIndex != -1 && !cursor.isNull(hcodeSendNameIndex)) {
            hospital.setHcodeSendName(cursor.getString(hcodeSendNameIndex));
        }

        int hmainIndex = cursor.getColumnIndex(NHSOHospital.HMAIN);
        if (hmainIndex != -1 && !cursor.isNull(hmainIndex)) {
            hospital.setHmain(cursor.getString(hmainIndex));
        }

        int hmainNameIndex = cursor.getColumnIndex(NHSOHospital.HMAIN_NAME);
        if (hmainNameIndex != -1 && !cursor.isNull(hmainNameIndex)) {
            hospital.setHmainName(cursor.getString(hmainNameIndex));
        }

        int syncStatusIndex = cursor.getColumnIndex("sync_status");
        if (syncStatusIndex != -1 && !cursor.isNull(syncStatusIndex)) {
            hospital.setSyncStatus(cursor.getInt(syncStatusIndex) == 1);
        }

        int createByIndex = cursor.getColumnIndex(NHSOHospital.USER_CREATE);
        if (createByIndex != -1 && !cursor.isNull(createByIndex)) {
            hospital.setCreatedBy(cursor.getString(createByIndex));
        }

        int createTimeIndex = cursor.getColumnIndex(NHSOHospital.CREATETIME);
        if (createTimeIndex != -1 && !cursor.isNull(createTimeIndex)) {
            hospital.setCreatedDate(cursor.getString(createTimeIndex));
        }

        int updateByIndex = cursor.getColumnIndex(NHSOHospital.USER_UPDATE);
        if (updateByIndex != -1 && !cursor.isNull(updateByIndex)) {
            hospital.setUpdatedBy(cursor.getString(updateByIndex));
        }

        int updateTimeIndex = cursor.getColumnIndex(NHSOHospital.UPDATETIME);
        if (updateTimeIndex != -1 && !cursor.isNull(updateTimeIndex)) {
            hospital.setUpdatedDate(cursor.getString(updateTimeIndex));
        }

        return hospital;
    }

    /**
     * ตรวจสอบว่ามีข้อมูลสถานพยาบาลอยู่หรือไม่
     * @param hcode รหัสสถานพยาบาล
     * @return true ถ้ามีข้อมูล, false ถ้าไม่มีข้อมูล
     */
    public boolean isHospitalExists(String hcode) {
        try {
            String selection = NHSOHospital.HCODE + "=?";
            String[] selectionArgs = {hcode};

            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI,
                    new String[]{NHSOHospital.ID}, selection, selectionArgs, null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if hospital exists", e);
            return false;
        }
    }

    /**
     * นับจำนวนสถานพยาบาลทั้งหมด
     * @return จำนวนสถานพยาบาลทั้งหมด
     */
    public int countAllHospitals() {
        try {
            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI,
                    new String[]{"COUNT(*) AS count"}, null, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting hospitals", e);
            return 0;
        }
    }

    /**
     * นับจำนวนสถานพยาบาลที่ยังไม่ได้ซิงค์
     * @return จำนวนสถานพยาบาลที่ยังไม่ได้ซิงค์
     */
    public int countUnsyncedHospitals() {
        try {
            String selection = "sync_status=0 OR sync_status IS NULL";

            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI,
                    new String[]{"COUNT(*) AS count"}, selection, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting unsynced hospitals", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลสถานพยาบาลที่ยังไม่ได้ซิงค์
     * @return รายการสถานพยาบาลที่ยังไม่ได้ซิงค์
     */
    public List<NHSOHospitalInfo> getUnsyncedHospitals() {
        List<NHSOHospitalInfo> hospitals = new ArrayList<>();

        try {
            String selection = "sync_status=0 OR sync_status IS NULL";

            Cursor cursor = mResolver.query(NHSOHospital.CONTENT_LIST_URI, null, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOHospitalInfo hospital = cursorToHospital(cursor);
                    hospitals.add(hospital);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting unsynced hospitals", e);
        }

        return hospitals;
    }
}