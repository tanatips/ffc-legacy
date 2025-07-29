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
 * ContentProvider สำหรับจัดการข้อมูลการให้บริการส่งเสริม ป้องกันโรค (f43specialpp)
 * ใช้สำหรับเก็บและดึงข้อมูลบริการพิเศษทางการแพทย์
 */
public class F43SpecialPPProvider extends ContentProvider {
    private static final String TAG = "F43SpecialPPProvider";

    public static String AUTHORITY = "th.in.ffc.provider.F43SpecialPPProvider";

    // F43SpecialPP URI Types - เหลือเฉพาะที่จำเป็น
    private static final int F43SPECIALPP = 0;
    private static final int F43SPECIALPP_ITEMS = 1;
    private static final int F43SPECIALPP_BY_VISITNO = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.f43specialpp";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.f43specialpp";

    private static DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "f43specialpp", F43SPECIALPP);
        mUriMatcher.addURI(AUTHORITY, "f43specialpp/list", F43SPECIALPP_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "f43specialpp/visitno/#", F43SPECIALPP_BY_VISITNO);
    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create table
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(F43SpecialPP.CREATE_TABLE);

            Log.i(TAG, "F43SpecialPP Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating F43SpecialPP Provider", e);
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
            sortOrder = F43SpecialPP.DATESERV + " DESC, " + F43SpecialPP.VISITNO + " DESC";
        }

        switch (mUriMatcher.match(uri)) {
            case F43SPECIALPP:
            case F43SPECIALPP_ITEMS:
                builder.setTables(F43SpecialPP.TABLENAME);
                break;

            case F43SPECIALPP_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = F43SpecialPP.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                builder.setTables(F43SpecialPP.TABLENAME);
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
            case F43SPECIALPP:
            case F43SPECIALPP_ITEMS:
            case F43SPECIALPP_BY_VISITNO:
                return F43SpecialPP.CONTENT_DIR_TYPE;
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
            case F43SPECIALPP:
            case F43SPECIALPP_ITEMS:
                // ตั้งค่า dateupdate หากไม่มี
                if (values != null && !values.containsKey(F43SpecialPP.DATEUPDATE)) {
                    values.put(F43SpecialPP.DATEUPDATE, DateConverter.getCurrentWesternDateTime());
                }

                // Insert ข้อมูลใหม่
                id = db.insert(F43SpecialPP.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(F43SpecialPP.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(F43SpecialPP.CONTENT_LIST_URI, null);
            Log.d(TAG, "Insert successful, ID: " + id);
        }

        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case F43SPECIALPP_ITEMS:
                count = db.delete(F43SpecialPP.TABLENAME, selection, selectionArgs);
                break;

            case F43SPECIALPP_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = F43SpecialPP.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                count = db.delete(F43SpecialPP.TABLENAME, selection, selectionArgs);
                Log.d(TAG, "Deleted " + count + " records for visitno: " + visitno);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(F43SpecialPP.CONTENT_LIST_URI, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;

        // อัพเดท dateupdate ทุกครั้งที่มีการแก้ไข
        if (values != null) {
            values.put(F43SpecialPP.DATEUPDATE, DateConverter.getCurrentWesternDateTime());
        }

        switch (mUriMatcher.match(uri)) {
            case F43SPECIALPP_ITEMS:
                rowUpdated = db.update(F43SpecialPP.TABLENAME, values, selection, selectionArgs);
                break;

            case F43SPECIALPP_BY_VISITNO:
                String visitno = uri.getPathSegments().get(2);
                selection = F43SpecialPP.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                rowUpdated = db.update(F43SpecialPP.TABLENAME, values, selection, selectionArgs);
                Log.d(TAG, "Updated " + rowUpdated + " records for visitno: " + visitno);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(F43SpecialPP.CONTENT_LIST_URI, null);
        }

        return rowUpdated;
    }

    /**
     * รีสร้างตาราง (สำหรับ development/testing)
     */
    public static void ReCreateTable(Context context) {
        try {
            mOpenHelper = new DbOpenHelper(context);
            mOpenHelper.getWritableDatabase().execSQL(F43SpecialPP.DROP_TABLE);
            mOpenHelper.getWritableDatabase().execSQL(F43SpecialPP.CREATE_TABLE);
            Log.i("F43SpecialPPProvider", "Table recreated successfully");
        } catch (Exception e) {
            Log.e("F43SpecialPPProvider", "Error recreating table", e);
        }
    }

    /**
     * Helper method สำหรับการบันทึกข้อมูลบริการ
     */
    public static Uri insertServiceRecord(ContentResolver resolver, ContentValues values) {
        return resolver.insert(F43SpecialPP.CONTENT_URI, values);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลบริการตาม visitno
     */
    public static Cursor getServicesByVisitNo(ContentResolver resolver, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/f43specialpp/visitno/" + visitno);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการลบข้อมูลบริการตาม visitno
     */
    public static int deleteServicesByVisitNo(ContentResolver resolver, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/f43specialpp/visitno/" + visitno);
        return resolver.delete(uri, null, null);
    }

    /**
     * Helper method สำหรับการนับจำนวนบริการตาม visitno
     */
    public static int getServiceCountByVisitNo(ContentResolver resolver, int visitno) {
        Cursor cursor = getServicesByVisitNo(resolver, visitno);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }
}