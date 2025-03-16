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
 * ContentProvider สำหรับจัดการข้อมูลผู้ให้บริการ NHSO (แฟ้ม 3)
 */
public class NHSOPractitionerProvider extends ContentProvider {
    private static final String TAG = "NHSOPractitionerProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOPractitionerProvider";

    // NHSO Practitioner File 3
    private static final int NHSO_PRACTITIONER = 0;
    private static final int NHSO_PRACTITIONER_ITEMS = 1;
    private static final int NHSO_PRACTITIONER_ITEM_ID = 2;

    // NHSO Auth Token
    private static final int NHSO_TOKEN = 3;
    private static final int NHSO_TOKEN_ITEMS = 4;
    private static final int NHSO_TOKEN_ITEM_ID = 5;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsopractitioner";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsopractitioner";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_practitioner", NHSO_PRACTITIONER);
        mUriMatcher.addURI(AUTHORITY, "nhso_practitioner/list", NHSO_PRACTITIONER_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_practitioner/#", NHSO_PRACTITIONER_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "nhso_token", NHSO_TOKEN);
        mUriMatcher.addURI(AUTHORITY, "nhso_token/list", NHSO_TOKEN_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_token/#", NHSO_TOKEN_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOPractitioner.CREATE_TABLE);
            db.execSQL(NHSOToken.CREATE_TABLE);

            Log.i(TAG, "NHSO Practitioner Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO Practitioner Provider", e);
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
            case NHSOPractitionerProvider.NHSO_PRACTITIONER_ITEMS:
                builder.setTables(NHSOPractitioner.TABLENAME);
                builder.setProjectionMap(NHSOPractitioner.PROJECTION_MAP);
                break;

            case NHSOPractitionerProvider.NHSO_PRACTITIONER_ITEM_ID:
                selection = NHSOPractitioner.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOPractitioner.TABLENAME);
                builder.setProjectionMap(NHSOPractitioner.PROJECTION_MAP);
                break;

            case NHSOPractitionerProvider.NHSO_TOKEN_ITEMS:
                builder.setTables(NHSOToken.TABLENAME);
                builder.setProjectionMap(NHSOToken.PROJECTION_MAP);
                break;

            case NHSOPractitionerProvider.NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOToken.TABLENAME);
                builder.setProjectionMap(NHSOToken.PROJECTION_MAP);
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
            case NHSOPractitionerProvider.NHSO_PRACTITIONER_ITEMS:
                return NHSOPractitioner.CONTENT_DIR_TYPE;
            case NHSOPractitionerProvider.NHSO_PRACTITIONER_ITEM_ID:
                return NHSOPractitioner.CONTENT_ITEM_TYPE;

            case NHSOPractitionerProvider.NHSO_TOKEN_ITEMS:
                return NHSOToken.CONTENT_DIR_TYPE;
            case NHSOPractitionerProvider.NHSO_TOKEN_ITEM_ID:
                return NHSOToken.CONTENT_ITEM_TYPE;

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
            case NHSO_PRACTITIONER:
                id = db.insert(NHSOPractitioner.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOPractitioner.CONTENT_URI, id);
                break;

            case NHSO_TOKEN:
                id = db.insert(NHSOToken.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOToken.CONTENT_URI, id);
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
            case NHSO_PRACTITIONER_ITEMS:
                count = db.delete(NHSOPractitioner.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_PRACTITIONER_ITEM_ID:
                selection = NHSOPractitioner.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOPractitioner.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEMS:
                count = db.delete(NHSOToken.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOToken.TABLENAME, selection, selectionArgs);
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
            case NHSO_PRACTITIONER_ITEMS:
                rowUpdated = db.update(NHSOPractitioner.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_PRACTITIONER_ITEM_ID:
                selection = NHSOPractitioner.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOPractitioner.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEMS:
                rowUpdated = db.update(NHSOToken.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_TOKEN_ITEM_ID:
                selection = NHSOToken.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOToken.TABLENAME, values, selection, selectionArgs);
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
