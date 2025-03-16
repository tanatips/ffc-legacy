package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
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

import th.in.ffc.app.form.nhso.model.NHSOPatientHistoryInfo;
import th.in.ffc.provider.NHSOPatientHistory;
import th.in.ffc.provider.NHSOPatientProvider;

/**
 * Data Access Object สำหรับประวัติการส่งข้อมูลผู้ป่วย NHSO
 */
public class NHSOPatientHistoryDao {
    private static final String TAG = "NHSOPatientHistoryDao";
    private Context mContext;
    private SimpleDateFormat dateFormat;

    public NHSOPatientHistoryDao(Context context) {
        this.mContext = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    /**
     * ดึงประวัติการส่งข้อมูลทั้งหมด
     * @return รายการประวัติการส่งข้อมูล
     */
    public List<NHSOPatientHistoryInfo> getAllHistory() {
        List<NHSOPatientHistoryInfo> historyList = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatientHistory.CONTENT_URI;

        String[] projection = {
                NHSOPatientHistory.ID,
                NHSOPatientHistory.PATIENT_ID,
                NHSOPatientHistory.CID,
                NHSOPatientHistory.STATUS,
                NHSOPatientHistory.MESSAGE,
                NHSOPatientHistory.RESPONSE_CODE,
                NHSOPatientHistory.RESPONSE_BODY,
                NHSOPatientHistory.CREATED_BY,
                NHSOPatientHistory.CREATED_DATE
        };

        String sortOrder = NHSOPatientHistory.ID + " DESC";

        Cursor cursor = resolver.query(uri, projection, null, null, sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOPatientHistoryInfo history = cursorToHistory(cursor);
                    historyList.add(history);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient history data", e);
            } finally {
                cursor.close();
            }
        }

        return historyList;
    }

    /**
     * ดึงประวัติการส่งข้อมูลตามเลขบัตรประชาชน
     * @param cid เลขบัตรประชาชน
     * @return รายการประวัติการส่งข้อมูล
     */
    public List<NHSOPatientHistoryInfo> getHistoryByCid(String cid) {
        List<NHSOPatientHistoryInfo> historyList = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatientHistory.CONTENT_URI;

        String[] projection = {
                NHSOPatientHistory.ID,
                NHSOPatientHistory.PATIENT_ID,
                NHSOPatientHistory.CID,
                NHSOPatientHistory.STATUS,
                NHSOPatientHistory.MESSAGE,
                NHSOPatientHistory.RESPONSE_CODE,
                NHSOPatientHistory.RESPONSE_BODY,
                NHSOPatientHistory.CREATED_BY,
                NHSOPatientHistory.CREATED_DATE
        };

        String selection = NHSOPatientHistory.CID + " = ?";
        String[] selectionArgs = {cid};
        String sortOrder = NHSOPatientHistory.ID + " DESC";

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOPatientHistoryInfo history = cursorToHistory(cursor);
                    historyList.add(history);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient history data", e);
            } finally {
                cursor.close();
            }
        }

        return historyList;
    }

    /**
     * ดึงประวัติการส่งข้อมูลตาม ID ผู้ป่วย
     * @param patientId ID ผู้ป่วย
     * @return รายการประวัติการส่งข้อมูล
     */
    public List<NHSOPatientHistoryInfo> getHistoryByPatientId(long patientId) {
        List<NHSOPatientHistoryInfo> historyList = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatientHistory.CONTENT_URI;

        String[] projection = {
                NHSOPatientHistory.ID,
                NHSOPatientHistory.PATIENT_ID,
                NHSOPatientHistory.CID,
                NHSOPatientHistory.STATUS,
                NHSOPatientHistory.MESSAGE,
                NHSOPatientHistory.RESPONSE_CODE,
                NHSOPatientHistory.RESPONSE_BODY,
                NHSOPatientHistory.CREATED_BY,
                NHSOPatientHistory.CREATED_DATE
        };

        String selection = NHSOPatientHistory.PATIENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(patientId)};
        String sortOrder = NHSOPatientHistory.ID + " DESC";

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOPatientHistoryInfo history = cursorToHistory(cursor);
                    historyList.add(history);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient history data", e);
            } finally {
                cursor.close();
            }
        }

        return historyList;
    }

    /**
     * บันทึกประวัติการส่งข้อมูลใหม่
     * @param history ข้อมูลประวัติการส่งข้อมูล
     * @return รหัส ID ที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertHistory(NHSOPatientHistoryInfo history) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = historyToContentValues(history);

        // เพิ่มข้อมูลวันที่บันทึก
        if (history.getCreatedDate() == null) {
            values.put(NHSOPatientHistory.CREATED_DATE, dateFormat.format(new Date()));
        }

        Uri resultUri = resolver.insert(NHSOPatientHistory.CONTENT_URI, values);
        if (resultUri != null) {
            return Long.parseLong(resultUri.getLastPathSegment());
        } else {
            return -1;
        }
    }

    /**
     * ลบประวัติการส่งข้อมูลตาม ID
     * @param id รหัส ID ที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteHistory(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(NHSOPatientHistory.CONTENT_URI, String.valueOf(id));

        return resolver.delete(uri, null, null);
    }

    /**
     * ลบประวัติการส่งข้อมูลของผู้ป่วยทั้งหมด
     * @param patientId ID ผู้ป่วย
     * @return จำนวนรายการที่ลบ
     */
    public int deleteHistoryByPatientId(long patientId) {
        ContentResolver resolver = mContext.getContentResolver();

        String selection = NHSOPatientHistory.PATIENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(patientId)};

        return resolver.delete(NHSOPatientHistory.CONTENT_URI, selection, selectionArgs);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOPatientHistoryInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูลประวัติการส่งข้อมูล
     */
    private NHSOPatientHistoryInfo cursorToHistory(Cursor cursor) {
        NHSOPatientHistoryInfo history = new NHSOPatientHistoryInfo();

        history.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NHSOPatientHistory.ID)));
        history.setPatientId(cursor.getLong(cursor.getColumnIndexOrThrow(NHSOPatientHistory.PATIENT_ID)));
        history.setCid(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatientHistory.CID)));
        history.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatientHistory.STATUS)));

        int messageIndex = cursor.getColumnIndexOrThrow(NHSOPatientHistory.MESSAGE);
        if (!cursor.isNull(messageIndex)) {
            history.setMessage(cursor.getString(messageIndex));
        }

        int responseCodeIndex = cursor.getColumnIndexOrThrow(NHSOPatientHistory.RESPONSE_CODE);
        if (!cursor.isNull(responseCodeIndex)) {
            history.setResponseCode(cursor.getInt(responseCodeIndex));
        }

        int responseBodyIndex = cursor.getColumnIndexOrThrow(NHSOPatientHistory.RESPONSE_BODY);
        if (!cursor.isNull(responseBodyIndex)) {
            history.setResponseBody(cursor.getString(responseBodyIndex));
        }

        int createdByIndex = cursor.getColumnIndexOrThrow(NHSOPatientHistory.CREATED_BY);
        if (!cursor.isNull(createdByIndex)) {
            history.setCreatedBy(cursor.getString(createdByIndex));
        }

        int createdDateIndex = cursor.getColumnIndexOrThrow(NHSOPatientHistory.CREATED_DATE);
        if (!cursor.isNull(createdDateIndex)) {
            history.setCreatedDate(cursor.getString(createdDateIndex));
        }

        return history;
    }

    /**
     * แปลงข้อมูลจาก NHSOPatientHistoryInfo เป็น ContentValues
     * @param history ข้อมูลประวัติการส่งข้อมูล
     * @return ContentValues สำหรับบันทึกลงฐานข้อมูล
     */
    private ContentValues historyToContentValues(NHSOPatientHistoryInfo history) {
        ContentValues values = new ContentValues();

        values.put(NHSOPatientHistory.PATIENT_ID, history.getPatientId());
        values.put(NHSOPatientHistory.CID, history.getCid());
        values.put(NHSOPatientHistory.STATUS, history.getStatus());

        if (history.getMessage() != null) {
            values.put(NHSOPatientHistory.MESSAGE, history.getMessage());
        }

        if (history.getResponseCode() != 0) {
            values.put(NHSOPatientHistory.RESPONSE_CODE, history.getResponseCode());
        }

        if (history.getResponseBody() != null) {
            values.put(NHSOPatientHistory.RESPONSE_BODY, history.getResponseBody());
        }

        if (history.getCreatedBy() != null) {
            values.put(NHSOPatientHistory.CREATED_BY, history.getCreatedBy());
        }

        if (history.getCreatedDate() != null) {
            values.put(NHSOPatientHistory.CREATED_DATE, history.getCreatedDate());
        }

        return values;
    }
}