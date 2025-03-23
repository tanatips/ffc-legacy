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
 * ContentProvider สำหรับจัดการข้อมูลผู้ป่วยนอก NHSO (แฟ้ม 4)
 */
public class NHSOOPDProvider extends ContentProvider {
    private static final String TAG = "NHSOOPDProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOOPDProvider";

    // NHSO OPD File 4
    private static final int NHSO_OPD = 0;
    private static final int NHSO_OPD_ITEMS = 1;
    private static final int NHSO_OPD_ITEM_ID = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsoopd";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsoopd";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_opd", NHSO_OPD);
        mUriMatcher.addURI(AUTHORITY, "nhso_opd/list", NHSO_OPD_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_opd/#", NHSO_OPD_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOOPD.CREATE_TABLE);

            Log.i(TAG, "NHSO OPD Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO OPD Provider", e);
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
            case NHSOOPDProvider.NHSO_OPD_ITEMS:
                builder.setTables(NHSOOPD.TABLENAME);
                builder.setProjectionMap(NHSOOPD.PROJECTION_MAP);
                break;

            case NHSOOPDProvider.NHSO_OPD_ITEM_ID:
                selection = NHSOOPD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOOPD.TABLENAME);
                builder.setProjectionMap(NHSOOPD.PROJECTION_MAP);
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
            case NHSOOPDProvider.NHSO_OPD_ITEMS:
                return NHSOOPD.CONTENT_DIR_TYPE;
            case NHSOOPDProvider.NHSO_OPD_ITEM_ID:
                return NHSOOPD.CONTENT_ITEM_TYPE;

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
            case NHSO_OPD:
                id = db.insert(NHSOOPD.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOOPD.CONTENT_URI, id);
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
            case NHSO_OPD_ITEMS:
                count = db.delete(NHSOOPD.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_OPD_ITEM_ID:
                selection = NHSOOPD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOOPD.TABLENAME, selection, selectionArgs);
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
            case NHSO_OPD_ITEMS:
                rowUpdated = db.update(NHSOOPD.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_OPD_ITEM_ID:
                selection = NHSOOPD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOOPD.TABLENAME, values, selection, selectionArgs);
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