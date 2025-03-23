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
 * ContentProvider สำหรับจัดการข้อมูลวินิจฉัยโรค NHSO (แฟ้ม 5)
 */
public class NHSODiagnosisProvider extends ContentProvider {
    private static final String TAG = "NHSODiagnosisProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSODiagnosisProvider";

    // NHSO Diagnosis File 5
    private static final int NHSO_DIAGNOSIS = 0;
    private static final int NHSO_DIAGNOSIS_ITEMS = 1;
    private static final int NHSO_DIAGNOSIS_ITEM_ID = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsodiagnosis";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsodiagnosis";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_diagnosis", NHSO_DIAGNOSIS);
        mUriMatcher.addURI(AUTHORITY, "nhso_diagnosis/list", NHSO_DIAGNOSIS_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_diagnosis/#", NHSO_DIAGNOSIS_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSODiagnosis.CREATE_TABLE);

            Log.i(TAG, "NHSO Diagnosis Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO Diagnosis Provider", e);
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
            case NHSODiagnosisProvider.NHSO_DIAGNOSIS_ITEMS:
                builder.setTables(NHSODiagnosis.TABLENAME);
                builder.setProjectionMap(NHSODiagnosis.PROJECTION_MAP);
                break;

            case NHSODiagnosisProvider.NHSO_DIAGNOSIS_ITEM_ID:
                selection = NHSODiagnosis.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSODiagnosis.TABLENAME);
                builder.setProjectionMap(NHSODiagnosis.PROJECTION_MAP);
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
            case NHSODiagnosisProvider.NHSO_DIAGNOSIS_ITEMS:
                return NHSODiagnosis.CONTENT_DIR_TYPE;
            case NHSODiagnosisProvider.NHSO_DIAGNOSIS_ITEM_ID:
                return NHSODiagnosis.CONTENT_ITEM_TYPE;

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
            case NHSO_DIAGNOSIS:
                id = db.insert(NHSODiagnosis.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSODiagnosis.CONTENT_URI, id);
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
            case NHSO_DIAGNOSIS_ITEMS:
                count = db.delete(NHSODiagnosis.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_DIAGNOSIS_ITEM_ID:
                selection = NHSODiagnosis.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSODiagnosis.TABLENAME, selection, selectionArgs);
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
            case NHSO_DIAGNOSIS_ITEMS:
                rowUpdated = db.update(NHSODiagnosis.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_DIAGNOSIS_ITEM_ID:
                selection = NHSODiagnosis.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSODiagnosis.TABLENAME, values, selection, selectionArgs);
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