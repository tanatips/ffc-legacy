package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ContentProvider สำหรับจัดการข้อมูลผู้ป่วย NHSO (แฟ้ม 1)
 */
public class NHSOPatientProvider extends ContentProvider {
    private static final String TAG = "NHSOPatientProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOPatientProvider";

    // NHSO Patient File 1
    private static final int NHSO_PATIENT = 0;
    private static final int NHSO_PATIENT_ITEMS = 1;
    private static final int NHSO_PATIENT_ITEM_ID = 2;

    // NHSO Patient History
    private static final int NHSO_PATIENT_HISTORY = 3;
    private static final int NHSO_PATIENT_HISTORY_ITEMS = 4;
    private static final int NHSO_PATIENT_HISTORY_ITEM_ID = 5;

    // NHSO Auth Token
    private static final int NHSO_TOKEN = 6;
    private static final int NHSO_TOKEN_ITEMS = 7;
    private static final int NHSO_TOKEN_ITEM_ID = 8;

    // NHSO Card Reading History
    private static final int NHSO_CARD_READING_HISTORY = 9;
    private static final int NHSO_CARD_READING_HISTORY_ITEMS = 10;
    private static final int NHSO_CARD_READING_HISTORY_ITEM_ID = 11;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsopatient";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsopatient";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_patient", NHSO_PATIENT);
        mUriMatcher.addURI(AUTHORITY, "nhso_patient/list", NHSO_PATIENT_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_patient/#", NHSO_PATIENT_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "nhso_patient_history", NHSO_PATIENT_HISTORY);
        mUriMatcher.addURI(AUTHORITY, "nhso_patient_history/list", NHSO_PATIENT_HISTORY_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_patient_history/#", NHSO_PATIENT_HISTORY_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "nhso_token", NHSO_TOKEN);
        mUriMatcher.addURI(AUTHORITY, "nhso_token/list", NHSO_TOKEN_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_token/#", NHSO_TOKEN_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "nhso_card_reading_history", NHSO_CARD_READING_HISTORY);
        mUriMatcher.addURI(AUTHORITY, "nhso_card_reading_history/list", NHSO_CARD_READING_HISTORY_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_card_reading_history/#", NHSO_CARD_READING_HISTORY_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOPatient.CREATE_TABLE);
            db.execSQL(NHSOPatientHistory.CREATE_TABLE);
            db.execSQL(NHSOToken.CREATE_TABLE);
            db.execSQL(NHSOCardReadingHistory.CREATE_TABLE);

            Log.i(TAG, "NHSO Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO Provider", e);
            return false;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String groupby = null;
        String having = null;

        switch (mUriMatcher.match(uri)) {
            case NHSOPatientProvider.NHSO_PATIENT_ITEMS:
                builder.setTables(NHSOPatient.TABLENAME);
                builder.setProjectionMap(NHSOPatient.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_PATIENT_ITEM_ID:
                selection = NHSOPatient.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOPatient.TABLENAME);
                builder.setProjectionMap(NHSOPatient.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_PATIENT_HISTORY_ITEMS:
                builder.setTables(NHSOPatientHistory.TABLENAME);
                builder.setProjectionMap(NHSOPatientHistory.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_PATIENT_HISTORY_ITEM_ID:
                selection = NHSOPatientHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOPatientHistory.TABLENAME);
                builder.setProjectionMap(NHSOPatientHistory.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_TOKEN_ITEMS:
                builder.setTables(NHSOToken.TABLENAME);
                builder.setProjectionMap(NHSOToken.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOToken.TABLENAME);
                builder.setProjectionMap(NHSOToken.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_CARD_READING_HISTORY_ITEMS:
                builder.setTables(NHSOCardReadingHistory.TABLENAME);
                builder.setProjectionMap(NHSOCardReadingHistory.PROJECTION_MAP);
                break;

            case NHSOPatientProvider.NHSO_CARD_READING_HISTORY_ITEM_ID:
                selection = NHSOCardReadingHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOCardReadingHistory.TABLENAME);
                builder.setProjectionMap(NHSOCardReadingHistory.PROJECTION_MAP);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor c = builder.query(db, projection, selection, selectionArgs,
                groupby, having, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case NHSOPatientProvider.NHSO_PATIENT_ITEMS:
                return NHSOPatient.CONTENT_DIR_TYPE;
            case NHSOPatientProvider.NHSO_PATIENT_ITEM_ID:
                return NHSOPatient.CONTENT_ITEM_TYPE;

            case NHSOPatientProvider.NHSO_PATIENT_HISTORY_ITEMS:
                return NHSOPatientHistory.CONTENT_DIR_TYPE;
            case NHSOPatientProvider.NHSO_PATIENT_HISTORY_ITEM_ID:
                return NHSOPatientHistory.CONTENT_ITEM_TYPE;

            case NHSOPatientProvider.NHSO_TOKEN_ITEMS:
                return NHSOToken.CONTENT_DIR_TYPE;
            case NHSOPatientProvider.NHSO_TOKEN_ITEM_ID:
                return NHSOToken.CONTENT_ITEM_TYPE;

            case NHSOPatientProvider.NHSO_CARD_READING_HISTORY_ITEMS:
                return NHSOCardReadingHistory.CONTENT_DIR_TYPE;
            case NHSOPatientProvider.NHSO_CARD_READING_HISTORY_ITEM_ID:
                return NHSOCardReadingHistory.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = 0;
        Uri uriReturn = null;

        switch (mUriMatcher.match(uri)) {
            case NHSO_PATIENT:
                id = db.insert(NHSOPatient.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOPatient.CONTENT_URI, id);
                break;

            case NHSO_PATIENT_HISTORY:
                id = db.insert(NHSOPatientHistory.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOPatientHistory.CONTENT_URI, id);
                break;

            case NHSO_TOKEN:
                id = db.insert(NHSOToken.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOToken.CONTENT_URI, id);
                break;

            case NHSO_CARD_READING_HISTORY:
                id = db.insert(NHSOCardReadingHistory.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOCardReadingHistory.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case NHSO_PATIENT_ITEMS:
                count = db.delete(NHSOPatient.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_PATIENT_ITEM_ID:
                selection = NHSOPatient.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOPatient.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_PATIENT_HISTORY_ITEMS:
                count = db.delete(NHSOPatientHistory.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_PATIENT_HISTORY_ITEM_ID:
                selection = NHSOPatientHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOPatientHistory.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEMS:
                count = db.delete(NHSOToken.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOToken.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CARD_READING_HISTORY_ITEMS:
                count = db.delete(NHSOCardReadingHistory.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CARD_READING_HISTORY_ITEM_ID:
                selection = NHSOCardReadingHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOCardReadingHistory.TABLENAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;

        switch (mUriMatcher.match(uri)) {
            case NHSO_PATIENT_ITEMS:
                rowUpdated = db.update(NHSOPatient.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_PATIENT_ITEM_ID:
                selection = NHSOPatient.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOPatient.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_PATIENT_HISTORY_ITEMS:
                rowUpdated = db.update(NHSOPatientHistory.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_PATIENT_HISTORY_ITEM_ID:
                selection = NHSOPatientHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOPatientHistory.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEMS:
                rowUpdated = db.update(NHSOToken.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOToken.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CARD_READING_HISTORY_ITEMS:
                rowUpdated = db.update(NHSOCardReadingHistory.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CARD_READING_HISTORY_ITEM_ID:
                selection = NHSOCardReadingHistory.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOCardReadingHistory.TABLENAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }
}