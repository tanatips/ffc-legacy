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
 * ContentProvider สำหรับจัดการข้อมูลค่าใช้จ่ายรายการของผู้เข้ารับบริการ NHSO CHAD (แฟ้ม 7)
 */
public class NHSOCHADProvider extends ContentProvider {
    private static final String TAG = "NHSOCHADProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOCHADProvider";

    // NHSO CHAD File 7
    private static final int NHSO_CHAD = 0;
    private static final int NHSO_CHAD_ITEMS = 1;
    private static final int NHSO_CHAD_ITEM_ID = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsochad";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsochad";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_chad", NHSO_CHAD);
        mUriMatcher.addURI(AUTHORITY, "nhso_chad/list", NHSO_CHAD_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_chad/#", NHSO_CHAD_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOCHAD.CREATE_TABLE);

            Log.i(TAG, "NHSO CHAD Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO CHAD Provider", e);
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
            case NHSOCHADProvider.NHSO_CHAD_ITEMS:
                builder.setTables(NHSOCHAD.TABLENAME);
                builder.setProjectionMap(NHSOCHAD.PROJECTION_MAP);
                break;

            case NHSOCHADProvider.NHSO_CHAD_ITEM_ID:
                selection = NHSOCHAD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOCHAD.TABLENAME);
                builder.setProjectionMap(NHSOCHAD.PROJECTION_MAP);
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
            case NHSOCHADProvider.NHSO_CHAD_ITEMS:
                return NHSOCHAD.CONTENT_DIR_TYPE;
            case NHSOCHADProvider.NHSO_CHAD_ITEM_ID:
                return NHSOCHAD.CONTENT_ITEM_TYPE;

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
            case NHSO_CHAD:
                id = db.insert(NHSOCHAD.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOCHAD.CONTENT_URI, id);
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
            case NHSO_CHAD_ITEMS:
                count = db.delete(NHSOCHAD.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CHAD_ITEM_ID:
                selection = NHSOCHAD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOCHAD.TABLENAME, selection, selectionArgs);
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
            case NHSO_CHAD_ITEMS:
                rowUpdated = db.update(NHSOCHAD.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CHAD_ITEM_ID:
                selection = NHSOCHAD.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOCHAD.TABLENAME, values, selection, selectionArgs);
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