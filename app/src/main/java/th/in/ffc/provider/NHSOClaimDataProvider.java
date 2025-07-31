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

import th.in.ffc.util.DateConverter;

/**
 * ContentProvider สำหรับจัดการข้อมูลการส่ง claim ไปยัง สปสช (nhso_claim_data)
 * ใช้สำหรับเก็บและดึงข้อมูล JSON ที่ส่งไปยัง NHSO
 */
public class NHSOClaimDataProvider extends ContentProvider {
    private static final String TAG = "NHSOClaimDataProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOClaimDataProvider";

    // URI Types
    private static final int NHSO_CLAIM_DATA = 0;
    private static final int NHSO_CLAIM_DATA_ITEMS = 1;
    private static final int NHSO_CLAIM_DATA_BY_VISITNO = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhso_claim_data";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhso_claim_data";

    private static DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "nhso_claim_data", NHSO_CLAIM_DATA);
        mUriMatcher.addURI(AUTHORITY, "nhso_claim_data/list", NHSO_CLAIM_DATA_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_claim_data/visitno/#", NHSO_CLAIM_DATA_BY_VISITNO);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create table and indexes
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOClaimData.CREATE_TABLE);
            db.execSQL(NHSOClaimData.CREATE_INDEX_VISITNO);
            db.execSQL(NHSOClaimData.CREATE_INDEX_STATUS);
            db.execSQL(NHSOClaimData.CREATE_INDEX_CREATED_DATE);

            Log.i(TAG, "NHSOClaimData Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSOClaimData Provider", e);
            return false;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        // กำหนด sortOrder เริ่มต้น
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = NHSOClaimData.CREATED_DATE + " DESC";
        }

        switch (mUriMatcher.match(uri)) {
            case NHSO_CLAIM_DATA:
            case NHSO_CLAIM_DATA_ITEMS:
                builder.setTables(NHSOClaimData.TABLENAME);
                break;

            case NHSO_CLAIM_DATA_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = NHSOClaimData.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                builder.setTables(NHSOClaimData.TABLENAME);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor c = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case NHSO_CLAIM_DATA:
            case NHSO_CLAIM_DATA_ITEMS:
            case NHSO_CLAIM_DATA_BY_VISITNO:
                return NHSOClaimData.CONTENT_DIR_TYPE;
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
            case NHSO_CLAIM_DATA:
            case NHSO_CLAIM_DATA_ITEMS:
                // ตั้งค่า created_date หากไม่มี
                if (values != null && !values.containsKey(NHSOClaimData.CREATED_DATE)) {
                    values.put(NHSOClaimData.CREATED_DATE, DateConverter.getCurrentWesternDateTime());
                }

                try {
                    // Insert ข้อมูลใหม่
                    id = db.insert(NHSOClaimData.TABLENAME, null, values);
                    uriReturn = ContentUris.withAppendedId(NHSOClaimData.CONTENT_URI, id);
                    Log.d(TAG, "Insert successful, ID: " + id + ", VISITNO: " +
                            (values != null ? values.getAsInteger(NHSOClaimData.VISITNO) : "null"));
                } catch (Exception e) {
                    Log.e(TAG, "Error inserting claim data", e);
                    // อาจจะเป็น duplicate visitno - ลองอัพเดทแทน
                    if (values != null && values.containsKey(NHSOClaimData.VISITNO)) {
                        String visitno = String.valueOf(values.getAsInteger(NHSOClaimData.VISITNO));
                        values.put(NHSOClaimData.UPDATED_DATE, DateConverter.getCurrentWesternDateTime());
                        int updated = db.update(NHSOClaimData.TABLENAME, values,
                                NHSOClaimData.VISITNO + "=?", new String[]{visitno});
                        if (updated > 0) {
                            uriReturn = Uri.parse("content://" + AUTHORITY + "/nhso_claim_data/visitno/" + visitno);
                            Log.d(TAG, "Updated existing record for VISITNO: " + visitno);
                        }
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (uriReturn != null) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(NHSOClaimData.CONTENT_LIST_URI, null);
        }

        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case NHSO_CLAIM_DATA_ITEMS:
                count = db.delete(NHSOClaimData.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CLAIM_DATA_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = NHSOClaimData.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                count = db.delete(NHSOClaimData.TABLENAME, selection, selectionArgs);
                Log.d(TAG, "Deleted " + count + " records for visitno: " + visitno);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(NHSOClaimData.CONTENT_LIST_URI, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;

        // อัพเดท updated_date ทุกครั้งที่มีการแก้ไข
        if (values != null) {
            values.put(NHSOClaimData.UPDATED_DATE, DateConverter.getCurrentWesternDateTime());
        }

        switch (mUriMatcher.match(uri)) {
            case NHSO_CLAIM_DATA_ITEMS:
                rowUpdated = db.update(NHSOClaimData.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CLAIM_DATA_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = NHSOClaimData.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                rowUpdated = db.update(NHSOClaimData.TABLENAME, values, selection, selectionArgs);
                Log.d(TAG, "Updated " + rowUpdated + " records for visitno: " + visitno);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(NHSOClaimData.CONTENT_LIST_URI, null);
        }

        return rowUpdated;
    }

    /**
     * รีสร้างตาราง (สำหรับ development/testing)
     */
    public static void ReCreateTable(Context context) {
        try {
            mOpenHelper = new DbOpenHelper(context);
            mOpenHelper.getWritableDatabase().execSQL(NHSOClaimData.DROP_TABLE);
            mOpenHelper.getWritableDatabase().execSQL(NHSOClaimData.CREATE_TABLE);
            mOpenHelper.getWritableDatabase().execSQL(NHSOClaimData.CREATE_INDEX_VISITNO);
            mOpenHelper.getWritableDatabase().execSQL(NHSOClaimData.CREATE_INDEX_STATUS);
            mOpenHelper.getWritableDatabase().execSQL(NHSOClaimData.CREATE_INDEX_CREATED_DATE);
            Log.i(TAG, "Table recreated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error recreating table", e);
        }
    }

    /**
     * Helper method สำหรับการบันทึกข้อมูล claim
     */
    public static Uri insertClaimData(ContentResolver resolver, ContentValues values) {
        return resolver.insert(NHSOClaimData.CONTENT_URI, values);
    }

    /**
     * Helper method สำหรับการดึงข้อมูล claim ตาม visitno
     */
    public static Cursor getClaimDataByVisitNo(ContentResolver resolver, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/nhso_claim_data/visitno/" + visitno);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการอัพเดทสถานะ claim ตาม visitno
     */
    public static int updateClaimStatusByVisitNo(ContentResolver resolver, int visitno, ContentValues values) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/nhso_claim_data/visitno/" + visitno);
        return resolver.update(uri, values, null, null);
    }

    /**
     * Helper method สำหรับการลบข้อมูล claim ตาม visitno
     */
    public static int deleteClaimDataByVisitNo(ContentResolver resolver, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/nhso_claim_data/visitno/" + visitno);
        return resolver.delete(uri, null, null);
    }
}