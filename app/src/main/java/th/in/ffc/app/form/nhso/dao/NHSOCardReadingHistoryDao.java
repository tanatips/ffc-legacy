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

import th.in.ffc.app.form.nhso.model.NHSOCardReadingHistoryInfo;
import th.in.ffc.provider.NHSOCardReadingHistory;
import th.in.ffc.provider.NHSOPatientProvider;

/**
 * Data Access Object สำหรับประวัติการอ่านบัตรประชาชน
 */
public class NHSOCardReadingHistoryDao {
    private static final String TAG = "NHSOCardReadingHistDao";
    private Context mContext;
    private SimpleDateFormat dateFormat;

    public NHSOCardReadingHistoryDao(Context context) {
        this.mContext = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    /**
     * ดึงประวัติการอ่านบัตรทั้งหมด
     * @return รายการประวัติการอ่านบัตร
     */
    public List<NHSOCardReadingHistoryInfo> getAllCardReadingHistory() {
        List<NHSOCardReadingHistoryInfo> historyList = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOCardReadingHistory.CONTENT_URI;

        String[] projection = {
                NHSOCardReadingHistory.ID,
                NHSOCardReadingHistory.READ_TIMESTAMP,
                NHSOCardReadingHistory.USERNAME,
                NHSOCardReadingHistory.CITIZEN_ID,
                NHSOCardReadingHistory.CITIZEN_NAME,
                NHSOCardReadingHistory.DEVICE_MODEL,
                NHSOCardReadingHistory.DEVICE_BRAND,
                NHSOCardReadingHistory.CARD_READER_MODEL,
                NHSOCardReadingHistory.APP_VERSION,
                NHSOCardReadingHistory.READ_STATUS,
                NHSOCardReadingHistory.NOTES,
                NHSOCardReadingHistory.CREATED_AT
        };

        String sortOrder = NHSOCardReadingHistory.READ_TIMESTAMP + " DESC";

        Cursor cursor = resolver.query(uri, projection, null, null, sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOCardReadingHistoryInfo history = cursorToHistory(cursor);
                    historyList.add(history);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing card reading history data", e);
            } finally {
                cursor.close();
            }
        }

        return historyList;
    }

    /**
     * ดึงประวัติการอ่านบัตรตามเลขบัตรประชาชน
     * @param citizenId เลขบัตรประชาชน
     * @return รายการประวัติการอ่านบัตร
     */
    public List<NHSOCardReadingHistoryInfo> getCardReadingHistoryByCitizenId(String citizenId) {
        List<NHSOCardReadingHistoryInfo> historyList = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOCardReadingHistory.CONTENT_URI;

        String[] projection = {
                NHSOCardReadingHistory.ID,
                NHSOCardReadingHistory.READ_TIMESTAMP,
                NHSOCardReadingHistory.USERNAME,
                NHSOCardReadingHistory.CITIZEN_ID,
                NHSOCardReadingHistory.CITIZEN_NAME,
                NHSOCardReadingHistory.DEVICE_MODEL,
                NHSOCardReadingHistory.DEVICE_BRAND,
                NHSOCardReadingHistory.CARD_READER_MODEL,
                NHSOCardReadingHistory.APP_VERSION,
                NHSOCardReadingHistory.READ_STATUS,
                NHSOCardReadingHistory.NOTES,
                NHSOCardReadingHistory.CREATED_AT
        };

        String selection = NHSOCardReadingHistory.CITIZEN_ID + " = ?";
        String[] selectionArgs = {citizenId};
        String sortOrder = NHSOCardReadingHistory.READ_TIMESTAMP + " DESC";

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOCardReadingHistoryInfo history = cursorToHistory(cursor);
                    historyList.add(history);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing card reading history data", e);
            } finally {
                cursor.close();
            }
        }

        return historyList;
    }

    /**
     * บันทึกประวัติการอ่านบัตรใหม่
     * @param history ข้อมูลประวัติการอ่านบัตร
     * @return รหัส ID ที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertCardReadingHistory(NHSOCardReadingHistoryInfo history) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = historyToContentValues(history);

        Uri resultUri = resolver.insert(NHSOCardReadingHistory.CONTENT_URI, values);
        if (resultUri != null) {
            return Long.parseLong(resultUri.getLastPathSegment());
        } else {
            return -1;
        }
    }

    /**
     * ลบประวัติการอ่านบัตรเก่ากว่าจำนวนวันที่กำหนด
     * @param days จำนวนวันที่ต้องการเก็บข้อมูล
     * @return จำนวนรายการที่ลบ
     */
    public int deleteOldCardReadingHistory(int days) {
        ContentResolver resolver = mContext.getContentResolver();

        // คำนวณวันที่ย้อนไป days วัน
        long cutoffTimeMillis = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L);
        String cutoffDate = dateFormat.format(new Date(cutoffTimeMillis));

        String selection = NHSOCardReadingHistory.READ_TIMESTAMP + " < ?";
        String[] selectionArgs = {cutoffDate};

        return resolver.delete(NHSOCardReadingHistory.CONTENT_URI, selection, selectionArgs);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOCardReadingHistoryInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูลประวัติการอ่านบัตร
     */
    private NHSOCardReadingHistoryInfo cursorToHistory(Cursor cursor) {
        NHSOCardReadingHistoryInfo history = new NHSOCardReadingHistoryInfo();

        history.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.ID)));
        history.setReadTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.READ_TIMESTAMP)));
        history.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.USERNAME)));
        history.setCitizenId(cursor.getString(cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.CITIZEN_ID)));
        history.setCitizenName(cursor.getString(cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.CITIZEN_NAME)));

        int deviceModelIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.DEVICE_MODEL);
        if (!cursor.isNull(deviceModelIndex)) {
            history.setDeviceModel(cursor.getString(deviceModelIndex));
        }

        int deviceBrandIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.DEVICE_BRAND);
        if (!cursor.isNull(deviceBrandIndex)) {
            history.setDeviceBrand(cursor.getString(deviceBrandIndex));
        }

        int cardReaderModelIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.CARD_READER_MODEL);
        if (!cursor.isNull(cardReaderModelIndex)) {
            history.setCardReaderModel(cursor.getString(cardReaderModelIndex));
        }

        int appVersionIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.APP_VERSION);
        if (!cursor.isNull(appVersionIndex)) {
            history.setAppVersion(cursor.getString(appVersionIndex));
        }

        int readStatusIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.READ_STATUS);
        if (!cursor.isNull(readStatusIndex)) {
            history.setReadStatus(cursor.getString(readStatusIndex));
        }

        int notesIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.NOTES);
        if (!cursor.isNull(notesIndex)) {
            history.setNotes(cursor.getString(notesIndex));
        }

        int createdAtIndex = cursor.getColumnIndexOrThrow(NHSOCardReadingHistory.CREATED_AT);
        if (!cursor.isNull(createdAtIndex)) {
            history.setCreatedAt(cursor.getString(createdAtIndex));
        }

        return history;
    }

    /**
     * แปลงข้อมูลจาก NHSOCardReadingHistoryInfo เป็น ContentValues
     * @param history ข้อมูลประวัติการอ่านบัตร
     * @return ContentValues สำหรับบันทึกลงฐานข้อมูล
     */
    private ContentValues historyToContentValues(NHSOCardReadingHistoryInfo history) {
        ContentValues values = new ContentValues();

        values.put(NHSOCardReadingHistory.READ_TIMESTAMP, history.getReadTimestamp());
        values.put(NHSOCardReadingHistory.USERNAME, history.getUsername());
        values.put(NHSOCardReadingHistory.CITIZEN_ID, history.getCitizenId());
        values.put(NHSOCardReadingHistory.CITIZEN_NAME, history.getCitizenName());

        if (history.getDeviceModel() != null) {
            values.put(NHSOCardReadingHistory.DEVICE_MODEL, history.getDeviceModel());
        }

        if (history.getDeviceBrand() != null) {
            values.put(NHSOCardReadingHistory.DEVICE_BRAND, history.getDeviceBrand());
        }

        if (history.getCardReaderModel() != null) {
            values.put(NHSOCardReadingHistory.CARD_READER_MODEL, history.getCardReaderModel());
        }

        if (history.getAppVersion() != null) {
            values.put(NHSOCardReadingHistory.APP_VERSION, history.getAppVersion());
        }

        if (history.getReadStatus() != null) {
            values.put(NHSOCardReadingHistory.READ_STATUS, history.getReadStatus());
        }

        if (history.getNotes() != null) {
            values.put(NHSOCardReadingHistory.NOTES, history.getNotes());
        }

        // บันทึกเวลาปัจจุบันเป็น CREATED_AT ถ้าไม่ได้ระบุมา
        if (history.getCreatedAt() == null) {
            values.put(NHSOCardReadingHistory.CREATED_AT, dateFormat.format(new Date()));
        } else {
            values.put(NHSOCardReadingHistory.CREATED_AT, history.getCreatedAt());
        }

        return values;
    }
}