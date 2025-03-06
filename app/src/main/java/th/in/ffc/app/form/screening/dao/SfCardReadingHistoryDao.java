package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.SfCardReadingHistory;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfCardReadingHistoryDao {
    private Context mContext;

    public SfCardReadingHistoryDao(Context context) {
        this.mContext = context;
    }

    public static Uri getCardReadingHistoryDaoUriAppend(String name) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfCardReadingHistory.CONTENT_URI, name);
    }

    public List<SfCardReadingHistory> getAllCardReadingHistories() {
        List<SfCardReadingHistory> histories = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = getCardReadingHistoryDaoUriAppend("list");

        String[] projection = {
                ScreeningFormProvider.SfCardReadingHistory.ID,
                ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP,
                ScreeningFormProvider.SfCardReadingHistory.USERNAME,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND,
                ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.APP_VERSION,
                ScreeningFormProvider.SfCardReadingHistory.READ_STATUS,
                ScreeningFormProvider.SfCardReadingHistory.NOTES,
                ScreeningFormProvider.SfCardReadingHistory.CREATED_AT
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                histories.add(getCardReadingHistoryFromCursor(cursor));
            }
            cursor.close();
        }
        return histories;
    }

    public SfCardReadingHistory getCardReadingHistoryById(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = ScreeningFormProvider.SfCardReadingHistory.CONTENT_URI;

        String[] projection = {
                ScreeningFormProvider.SfCardReadingHistory.ID,
                ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP,
                ScreeningFormProvider.SfCardReadingHistory.USERNAME,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND,
                ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.APP_VERSION,
                ScreeningFormProvider.SfCardReadingHistory.READ_STATUS,
                ScreeningFormProvider.SfCardReadingHistory.NOTES,
                ScreeningFormProvider.SfCardReadingHistory.CREATED_AT
        };

        String selection = ScreeningFormProvider.SfCardReadingHistory.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            SfCardReadingHistory history = getCardReadingHistoryFromCursor(cursor);
            cursor.close();
            return history;
        }
        return null;
    }

    public List<SfCardReadingHistory> getCardReadingHistoriesByCitizenId(String citizenId) {
        List<SfCardReadingHistory> histories = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = getCardReadingHistoryDaoUriAppend("list");

        String[] projection = {
                ScreeningFormProvider.SfCardReadingHistory.ID,
                ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP,
                ScreeningFormProvider.SfCardReadingHistory.USERNAME,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID,
                ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND,
                ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL,
                ScreeningFormProvider.SfCardReadingHistory.APP_VERSION,
                ScreeningFormProvider.SfCardReadingHistory.READ_STATUS,
                ScreeningFormProvider.SfCardReadingHistory.NOTES,
                ScreeningFormProvider.SfCardReadingHistory.CREATED_AT
        };

        String selection = ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID + " = ?";
        String[] selectionArgs = {citizenId};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                histories.add(getCardReadingHistoryFromCursor(cursor));
            }
            cursor.close();
        }
        return histories;
    }

    public Uri insert(SfCardReadingHistory history) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP, history.getReadTimestamp());
        values.put(ScreeningFormProvider.SfCardReadingHistory.USERNAME, history.getUsername());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID, history.getCitizenId());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME, history.getCitizenName());
        values.put(ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL, history.getDeviceModel());
        values.put(ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND, history.getDeviceBrand());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL, history.getCardReaderModel());
        values.put(ScreeningFormProvider.SfCardReadingHistory.APP_VERSION, history.getAppVersion());
        values.put(ScreeningFormProvider.SfCardReadingHistory.READ_STATUS, history.getReadStatus());
        values.put(ScreeningFormProvider.SfCardReadingHistory.NOTES, history.getNotes());

        return resolver.insert(ScreeningFormProvider.SfCardReadingHistory.CONTENT_URI, values);
    }

    public int update(SfCardReadingHistory history) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP, history.getReadTimestamp());
        values.put(ScreeningFormProvider.SfCardReadingHistory.USERNAME, history.getUsername());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID, history.getCitizenId());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME, history.getCitizenName());
        values.put(ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL, history.getDeviceModel());
        values.put(ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND, history.getDeviceBrand());
        values.put(ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL, history.getCardReaderModel());
        values.put(ScreeningFormProvider.SfCardReadingHistory.APP_VERSION, history.getAppVersion());
        values.put(ScreeningFormProvider.SfCardReadingHistory.READ_STATUS, history.getReadStatus());
        values.put(ScreeningFormProvider.SfCardReadingHistory.NOTES, history.getNotes());

        String selection = ScreeningFormProvider.SfCardReadingHistory.ID + " = ?";
        String[] selectionArgs = {String.valueOf(history.getId())};

        return resolver.update(
                ScreeningFormProvider.SfCardReadingHistory.CONTENT_URI,
                values,
                selection,
                selectionArgs);
    }

    public int delete(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        String selection = ScreeningFormProvider.SfCardReadingHistory.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return resolver.delete(
                ScreeningFormProvider.SfCardReadingHistory.CONTENT_URI,
                selection,
                selectionArgs);
    }

    private SfCardReadingHistory getCardReadingHistoryFromCursor(Cursor cursor) {
        SfCardReadingHistory history = new SfCardReadingHistory();
        history.setId(cursor.getLong(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.ID)));
        history.setReadTimestamp(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.READ_TIMESTAMP)));
        history.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.USERNAME)));
        history.setCitizenId(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_ID)));
        history.setCitizenName(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.CITIZEN_NAME)));
        history.setDeviceModel(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.DEVICE_MODEL)));
        history.setDeviceBrand(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.DEVICE_BRAND)));
        history.setCardReaderModel(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.CARD_READER_MODEL)));
        history.setAppVersion(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.APP_VERSION)));
        history.setReadStatus(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.READ_STATUS)));
        history.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.NOTES)));
        history.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ScreeningFormProvider.SfCardReadingHistory.CREATED_AT)));
        return history;
    }
}