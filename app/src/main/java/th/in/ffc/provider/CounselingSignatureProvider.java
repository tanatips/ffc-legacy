package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import th.in.ffc.app.form.screening.dao.SfTokenDao;

/**
 * ContentProvider สำหรับจัดการข้อมูลการให้คำปรึกษาและลายเซ็น
 */
public class CounselingSignatureProvider extends ContentProvider {
    private static final String TAG = "CounselingSignatureProvider";

    public static String AUTHORITY = "th.in.ffc.provider.CounselingSignatureProvider";

    // URI patterns
    private static final int COUNSELING_SIGNATURE = 0;
    private static final int COUNSELING_SIGNATURE_ITEMS = 1;
    private static final int COUNSELING_SIGNATURE_ITEM_ID = 2;
    private static final int COUNSELING_SIGNATURE_BY_VISIT = 3;
    private static final int COUNSELING_SIGNATURE_BY_PERSON = 4;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.counselingsignature";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.counselingsignature";

    private static DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "counseling_signature", COUNSELING_SIGNATURE);
        mUriMatcher.addURI(AUTHORITY, "counseling_signature/list", COUNSELING_SIGNATURE_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "counseling_signature/#", COUNSELING_SIGNATURE_ITEM_ID);
        mUriMatcher.addURI(AUTHORITY, "counseling_signature/visit/*", COUNSELING_SIGNATURE_BY_VISIT);
        mUriMatcher.addURI(AUTHORITY, "counseling_signature/person/*", COUNSELING_SIGNATURE_BY_PERSON);
    }

    public static void ReCreateTable(Context context){
        mOpenHelper = new DbOpenHelper(context);
        mOpenHelper.getWritableDatabase().execSQL(CounselingSignature.DROP_TABLE);
        mOpenHelper.getWritableDatabase().execSQL(CounselingSignature.CREATE_TABLE);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(CounselingSignature.CREATE_TABLE);

            Log.i(TAG, "Counseling Signature Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating Counseling Signature Provider", e);
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
            case COUNSELING_SIGNATURE_ITEMS:
                builder.setTables(CounselingSignature.TABLENAME);
                builder.setProjectionMap(CounselingSignature.PROJECTION_MAP);
                break;

            case COUNSELING_SIGNATURE_ITEM_ID:
                selection = CounselingSignature.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(CounselingSignature.TABLENAME);
                builder.setProjectionMap(CounselingSignature.PROJECTION_MAP);
                break;

            case COUNSELING_SIGNATURE_BY_VISIT:
                String visitId = uri.getLastPathSegment();
                selection = CounselingSignature.VISIT_ID + "=?";
                selectionArgs = new String[]{visitId};
                builder.setTables(CounselingSignature.TABLENAME);
                builder.setProjectionMap(CounselingSignature.PROJECTION_MAP);
                break;

            case COUNSELING_SIGNATURE_BY_PERSON:
                String personId = uri.getLastPathSegment();
                selection = CounselingSignature.PERSON_ID + "=?";
                selectionArgs = new String[]{personId};
                builder.setTables(CounselingSignature.TABLENAME);
                builder.setProjectionMap(CounselingSignature.PROJECTION_MAP);
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
            case COUNSELING_SIGNATURE_ITEMS:
            case COUNSELING_SIGNATURE_BY_VISIT:
            case COUNSELING_SIGNATURE_BY_PERSON:
                return CounselingSignature.CONTENT_DIR_TYPE;
            case COUNSELING_SIGNATURE_ITEM_ID:
                return CounselingSignature.CONTENT_ITEM_TYPE;

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
            case COUNSELING_SIGNATURE:
                id = db.insert(CounselingSignature.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(CounselingSignature.CONTENT_URI, id);
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
            case COUNSELING_SIGNATURE_ITEMS:
                count = db.delete(CounselingSignature.TABLENAME, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_ITEM_ID:
                selection = CounselingSignature.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(CounselingSignature.TABLENAME, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_BY_VISIT:
                String visitId = uri.getLastPathSegment();
                selection = CounselingSignature.VISIT_ID + "=?";
                selectionArgs = new String[]{visitId};
                count = db.delete(CounselingSignature.TABLENAME, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_BY_PERSON:
                String personId = uri.getLastPathSegment();
                selection = CounselingSignature.PERSON_ID + "=?";
                selectionArgs = new String[]{personId};
                count = db.delete(CounselingSignature.TABLENAME, selection, selectionArgs);
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
            case COUNSELING_SIGNATURE_ITEMS:
                rowUpdated = db.update(CounselingSignature.TABLENAME, values, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_ITEM_ID:
                selection = CounselingSignature.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(CounselingSignature.TABLENAME, values, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_BY_VISIT:
                String visitId = uri.getLastPathSegment();
                selection = CounselingSignature.VISIT_ID + "=?";
                selectionArgs = new String[]{visitId};
                rowUpdated = db.update(CounselingSignature.TABLENAME, values, selection, selectionArgs);
                break;

            case COUNSELING_SIGNATURE_BY_PERSON:
                String personId = uri.getLastPathSegment();
                selection = CounselingSignature.PERSON_ID + "=?";
                selectionArgs = new String[]{personId};
                rowUpdated = db.update(CounselingSignature.TABLENAME, values, selection, selectionArgs);
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
