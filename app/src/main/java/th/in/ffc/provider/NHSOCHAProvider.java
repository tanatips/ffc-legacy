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
 * ContentProvider สำหรับจัดการข้อมูลทางการเงินของผู้เข้ารับบริการ NHSO CHA (แฟ้ม 8)
 */
public class NHSOCHAProvider extends ContentProvider {
    private static final String TAG = "NHSOCHAProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOCHAProvider";

    // NHSO CHA File 8
    private static final int NHSO_CHA = 0;
    private static final int NHSO_CHA_ITEMS = 1;
    private static final int NHSO_CHA_ITEM_ID = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsocha";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsocha";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_cha", NHSO_CHA);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/list", NHSO_CHA_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/#", NHSO_CHA_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOCHA.CREATE_TABLE);

            Log.i(TAG, "NHSO CHA Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO CHA Provider", e);
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
            case NHSOCHAProvider.NHSO_CHA_ITEMS:
                builder.setTables(NHSOCHA.TABLENAME);
                builder.setProjectionMap(NHSOCHA.PROJECTION_MAP);
                break;

            case NHSOCHAProvider.NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOCHA.TABLENAME);
                builder.setProjectionMap(NHSOCHA.PROJECTION_MAP);
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
            case NHSOCHAProvider.NHSO_CHA_ITEMS:
                return NHSOCHA.CONTENT_DIR_TYPE;
            case NHSOCHAProvider.NHSO_CHA_ITEM_ID:
                return NHSOCHA.CONTENT_ITEM_TYPE;

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
            case NHSO_CHA:
                id = db.insert(NHSOCHA.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOCHA.CONTENT_URI, id);
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
            case NHSO_CHA_ITEMS:
                count = db.delete(NHSOCHA.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOCHA.TABLENAME, selection, selectionArgs);
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
            case NHSO_CHA_ITEMS:
                rowUpdated = db.update(NHSOCHA.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOCHA.TABLENAME, values, selection, selectionArgs);
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