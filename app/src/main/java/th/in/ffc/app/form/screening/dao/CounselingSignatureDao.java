package th.in.ffc.app.form.screening.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.provider.CounselingSignature;
import th.in.ffc.session.UserSessionManager;

/**
 * DAO สำหรับการเข้าถึงข้อมูลการให้คำปรึกษาและลายเซ็น
 */
public class CounselingSignatureDao {
    private Context context;
    private UserSessionManager session;

    public CounselingSignatureDao(Context context) {
        this.context = context;
        this.session = new UserSessionManager(context);
    }

    /**
     * บันทึกข้อมูลการให้คำปรึกษาและลายเซ็น
     * @param counseling ข้อมูลการให้คำปรึกษา
     * @return ID ของข้อมูลที่บันทึก, -1 หากบันทึกไม่สำเร็จ
     */
    public long saveCounseling(CounselingInfo counseling) {
        ContentValues values = prepareCounselingValues(counseling, true);

        Uri uri = context.getContentResolver().insert(
                CounselingSignature.CONTENT_URI, values);

        if (uri != null) {
            return Long.parseLong(uri.getLastPathSegment());
        }
        return -1;
    }

    /**
     * อัพเดทข้อมูลการให้คำปรึกษาและลายเซ็น
     * @param counseling ข้อมูลการให้คำปรึกษาที่ต้องการอัพเดท
     * @return จำนวนแถวที่อัพเดท
     */
    public int updateCounseling(CounselingInfo counseling) {
        ContentValues values = prepareCounselingValues(counseling, false);

        String selection = CounselingSignature.ID + "=?";
        String[] selectionArgs = {String.valueOf(counseling.getId())};
        Uri uri = CounselingSignature.getContentUri(counseling.getId());
        return context.getContentResolver().update(
                uri, values, selection, selectionArgs);
    }

    /**
     * ค้นหาข้อมูลการให้คำปรึกษาตาม ID
     * @param id ID ของข้อมูลที่ต้องการค้นหา
     * @return ข้อมูลการให้คำปรึกษา, null หากไม่พบ
     */
    public CounselingInfo getCounselingById(long id) {
        Uri uri = CounselingSignature.getContentUri(id);

        Cursor cursor = context.getContentResolver().query(
                uri, null, null, null, null);

        CounselingInfo counseling = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                counseling = mapCursorToCounseling(cursor);
            }
            cursor.close();
        }

        return counseling;
    }

    /**
     * ค้นหาข้อมูลการให้คำปรึกษาตาม visit ID
     * @param visitId ID ของการเข้ารับบริการ
     * @return รายการข้อมูลการให้คำปรึกษา
     */
    public List<CounselingInfo> getCounselingByVisitId(String visitId) {
        Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "visit/" + visitId);

        Cursor cursor = context.getContentResolver().query(
                uri, null, null, null, CounselingSignature.CREATED_DATE + " DESC");

        List<CounselingInfo> counselingList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                counselingList.add(mapCursorToCounseling(cursor));
            }
            cursor.close();
        }

        return counselingList;
    }

    /**
     * ค้นหาข้อมูลการให้คำปรึกษาตาม person ID
     * @param personId ID ของบุคคล
     * @return รายการข้อมูลการให้คำปรึกษา
     */
    public List<CounselingInfo> getCounselingByPersonId(String personId) {
        Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "person/" + personId);

        Cursor cursor = context.getContentResolver().query(
                uri, null, null, null, CounselingSignature.CREATED_DATE + " DESC");

        List<CounselingInfo> counselingList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                counselingList.add(mapCursorToCounseling(cursor));
            }
            cursor.close();
        }

        return counselingList;
    }

    /**
     * เตรียม ContentValues สำหรับบันทึกหรืออัพเดทข้อมูล
     * @param counseling ข้อมูลการให้คำปรึกษา
     * @param isNew true หากเป็นการบันทึกใหม่, false หากเป็นการอัพเดท
     * @return ContentValues ที่เตรียมไว้
     */
    private ContentValues prepareCounselingValues(CounselingInfo counseling, boolean isNew) {
        ContentValues values = new ContentValues();

        if (isNew) {
            values.put(CounselingSignature.VISIT_ID, counseling.getVisitId());
            values.put(CounselingSignature.PERSON_ID, counseling.getPersonId());
            values.put(CounselingSignature.CREATED_BY, counseling.getCreatedBy());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            values.put(CounselingSignature.CREATED_DATE, currentDateTime);

            values.put(CounselingSignature.PCUCODE, session.getPcuCode());
            values.put(CounselingSignature.UPDATE_STATUS, "0");
        } else {
            values.put(CounselingSignature.UPDATED_BY, counseling.getUpdatedBy());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            values.put(CounselingSignature.UPDATED_DATE, currentDateTime);

            values.put(CounselingSignature.UPDATE_STATUS, "1");
        }

        values.put(CounselingSignature.COUNSELING_TYPE, counseling.getCounselingType());
        values.put(CounselingSignature.DETAIL, counseling.getDetail());
        values.put(CounselingSignature.PATIENT_SIGNATURE, counseling.getPatientSignature());
        values.put(CounselingSignature.PROVIDER_SIGNATURE, counseling.getProviderSignature());

        return values;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น CounselingInfo
     * @param cursor Cursor ที่มีข้อมูล
     * @return ข้อมูล CounselingInfo
     */
    private CounselingInfo mapCursorToCounseling(Cursor cursor) {
        CounselingInfo counseling = new CounselingInfo();

        counseling.setId(cursor.getLong(cursor.getColumnIndex(CounselingSignature.ID)));
        counseling.setVisitId(cursor.getString(cursor.getColumnIndex(CounselingSignature.VISIT_ID)));
        counseling.setPersonId(cursor.getString(cursor.getColumnIndex(CounselingSignature.PERSON_ID)));
        counseling.setCounselingType(cursor.getInt(cursor.getColumnIndex(CounselingSignature.COUNSELING_TYPE)));
        counseling.setDetail(cursor.getString(cursor.getColumnIndex(CounselingSignature.DETAIL)));
        counseling.setPatientSignature(cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PATIENT_SIGNATURE)));
        counseling.setProviderSignature(cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PROVIDER_SIGNATURE)));
        counseling.setCreatedBy(cursor.getString(cursor.getColumnIndex(CounselingSignature.CREATED_BY)));
        counseling.setCreatedDate(cursor.getString(cursor.getColumnIndex(CounselingSignature.CREATED_DATE)));
        counseling.setUpdatedBy(cursor.getString(cursor.getColumnIndex(CounselingSignature.UPDATED_BY)));
        counseling.setUpdatedDate(cursor.getString(cursor.getColumnIndex(CounselingSignature.UPDATED_DATE)));

        return counseling;
    }
}