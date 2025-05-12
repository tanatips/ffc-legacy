package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Content Provider สำหรับจัดการข้อมูล API URL
 */
public class ApiUrlProvider extends ContentProvider {

    private static final String AUTHORITY = "th.in.ffc.provider.ApiUrlProvider";
    private static final String API_URL_PATH = "api_url";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + API_URL_PATH);

    private static final int API_URLS = 1;
    private static final int API_URL_ID = 2;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, API_URL_PATH, API_URLS);
        URI_MATCHER.addURI(AUTHORITY, API_URL_PATH + "/#", API_URL_ID);
    }

    private static final String TABLE_NAME = "ffc_sf_api_url";

    private DbOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case API_URL_ID:
                queryBuilder.appendWhere("id" + "=" + uri.getLastPathSegment());
                break;
            case API_URLS:
                // ไม่ต้องเพิ่มเงื่อนไขพิเศษ
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // แจ้งให้ ContentResolver ทราบเมื่อข้อมูลมีการเปลี่ยนแปลง
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int uriType = URI_MATCHER.match(uri);
        switch (uriType) {
            case API_URLS:
                return "vnd.android.cursor.dir/vnd.ffc.api_url";
            case API_URL_ID:
                return "vnd.android.cursor.item/vnd.ffc.api_url";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id;

        switch (uriType) {
            case API_URLS:
                id = db.insert(TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        switch (uriType) {
            case API_URLS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case API_URL_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(TABLE_NAME, "id = ?", new String[] { id });
                } else {
                    rowsDeleted = db.delete(TABLE_NAME, "id = ? AND " + selection,
                            append(new String[] { id }, selectionArgs));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (uriType) {
            case API_URLS:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case API_URL_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(TABLE_NAME, values, "id = ?", new String[] { id });
                } else {
                    rowsUpdated = db.update(TABLE_NAME, values, "id = ? AND " + selection,
                            append(new String[] { id }, selectionArgs));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    /**
     * รวมอาร์เรย์สตริงสองตัวเข้าด้วยกัน
     */
    private String[] append(String[] arr1, String[] arr2) {
        if (arr2 == null) {
            return arr1;
        }

        final String[] result = new String[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, result, 0, arr1.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }
}