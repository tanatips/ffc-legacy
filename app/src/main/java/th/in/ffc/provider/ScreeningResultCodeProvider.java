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

import th.in.ffc.util.DateConverter;

/**
 * ContentProvider สำหรับจัดการข้อมูลผลการคัดกรองและ Result Code
 * ใช้สำหรับเก็บและดึงข้อมูลผลการประเมินจากแบบคัดกรองต่างๆ
 */
public class ScreeningResultCodeProvider extends ContentProvider {
    private static final String TAG = "ScreeningResultCodeProvider";

    public static String AUTHORITY = "th.in.ffc.provider.ScreeningResultCodeProvider";

    // Screening Result Code
    private static final int SCREENING_RESULT = 0;
    private static final int SCREENING_RESULT_ITEMS = 1;
    private static final int SCREENING_RESULT_ITEM_ID = 2;
    private static final int SCREENING_RESULT_BY_PERSON = 3;
    private static final int SCREENING_RESULT_BY_TYPE = 4;
    private static final int SCREENING_RESULT_LATEST = 5;
    private static final int SCREENING_RESULT_BY_VISIT = 6;
    private static final int SCREENING_RESULT_BY_PERSON_VISIT = 7;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.screeningresultcode";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.screeningresultcode";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "screening_result", SCREENING_RESULT);  // เปลี่ยนกลับมาใช้ SCREENING_RESULT
        mUriMatcher.addURI(AUTHORITY, "screening_result/list", SCREENING_RESULT_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "screening_result/#", SCREENING_RESULT_ITEM_ID);
        mUriMatcher.addURI(AUTHORITY, "screening_result/person/#", SCREENING_RESULT_BY_PERSON);
        mUriMatcher.addURI(AUTHORITY, "screening_result/type/*", SCREENING_RESULT_BY_TYPE);
        mUriMatcher.addURI(AUTHORITY, "screening_result/person/*/latest", SCREENING_RESULT_LATEST);
        mUriMatcher.addURI(AUTHORITY, "screening_result/visit/#", SCREENING_RESULT_BY_VISIT);
        mUriMatcher.addURI(AUTHORITY, "screening_result/person/#/visit/#", SCREENING_RESULT_BY_PERSON_VISIT);

    }

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables and indexes
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(ScreeningResultCode.CREATE_TABLE);
//            db.execSQL(ScreeningResultCode.CREATE_INDEX_PERSON_ID);
//            db.execSQL(ScreeningResultCode.CREATE_INDEX_VISITNO);
//            db.execSQL(ScreeningResultCode.CREATE_INDEX_SCREENING_TYPE);
//            db.execSQL(ScreeningResultCode.CREATE_INDEX_SCREENING_DATE);
//            db.execSQL(ScreeningResultCode.CREATE_INDEX_PERSON_VISIT);

            Log.i(TAG, "Screening Result Code Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating Screening Result Code Provider", e);
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

        // กำหนด sortOrder เริ่มต้น
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = ScreeningResultCode.VISITNO + " DESC, " +
                    ScreeningResultCode.SCREENING_DATE + " DESC, " +
                    ScreeningResultCode.CREATETIME + " DESC";
        }

        switch (mUriMatcher.match(uri)) {
            case SCREENING_RESULT_ITEMS:
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
                break;

            case SCREENING_RESULT_ITEM_ID:
                selection = ScreeningResultCode.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
                break;

            case SCREENING_RESULT_BY_PERSON:
                String personId = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                selectionArgs = new String[]{personId, ScreeningResultCode.STATUS_ACTIVE};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
                break;

            case SCREENING_RESULT_BY_TYPE:
                String screeningType = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                selectionArgs = new String[]{screeningType, ScreeningResultCode.STATUS_ACTIVE};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
                break;

            case SCREENING_RESULT_LATEST:
                String personIdLatest = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                selectionArgs = new String[]{personIdLatest, ScreeningResultCode.STATUS_ACTIVE};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);

                // แก้ไข sortOrder เพื่อให้ได้ข้อมูลล่าสุดของแต่ละประเภท
                sortOrder = ScreeningResultCode.SCREENING_TYPE + ", " +
                        ScreeningResultCode.VISITNO + " DESC, " +
                        ScreeningResultCode.SCREENING_DATE + " DESC, " +
                        ScreeningResultCode.CREATETIME + " DESC";
                break;

            case SCREENING_RESULT_BY_VISIT:
                String visitno = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.VISITNO + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                selectionArgs = new String[]{visitno, ScreeningResultCode.STATUS_ACTIVE};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
                break;

            case SCREENING_RESULT_BY_PERSON_VISIT:
                String personIdVisit = uri.getPathSegments().get(2);
                String visitnoPersonVisit = uri.getPathSegments().get(4);
                selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                        ScreeningResultCode.VISITNO + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                selectionArgs = new String[]{personIdVisit, visitnoPersonVisit, ScreeningResultCode.STATUS_ACTIVE};
                builder.setTables(ScreeningResultCode.TABLENAME);
                builder.setProjectionMap(ScreeningResultCode.PROJECTION_MAP);
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
            case SCREENING_RESULT_ITEMS:
            case SCREENING_RESULT_BY_PERSON:
            case SCREENING_RESULT_BY_TYPE:
            case SCREENING_RESULT_LATEST:
            case SCREENING_RESULT_BY_VISIT:
            case SCREENING_RESULT_BY_PERSON_VISIT:
                return ScreeningResultCode.CONTENT_DIR_TYPE;
            case SCREENING_RESULT_ITEM_ID:
                return ScreeningResultCode.CONTENT_ITEM_TYPE;
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
            case SCREENING_RESULT:  // เพิ่ม case นี้กลับมา (แบบ SfNicotineInfoDao ใช้)
            case SCREENING_RESULT_ITEMS:  // รักษา case เดิมไว้เพื่อความปลอดภัย
                // อัพเดท updatetime ก่อนการ insert
                if (values.getAsString(ScreeningResultCode.CREATETIME) == null) {
                    values.put(ScreeningResultCode.CREATETIME, DateConverter.getCurrentWesternDate());
                }
                values.put(ScreeningResultCode.UPDATETIME, DateConverter.getCurrentWesternDate());

                // ตรวจสอบว่ามีข้อมูลเดิมหรือไม่ (same person, visitno, type, date)
                String personId = values.getAsString(ScreeningResultCode.PERSON_ID);
                String visitno = values.getAsString(ScreeningResultCode.VISITNO);
                String screeningType = values.getAsString(ScreeningResultCode.SCREENING_TYPE);
                String screeningDate = values.getAsString(ScreeningResultCode.SCREENING_DATE);

                if (screeningDate == null) {
                    screeningDate = getCurrentDate();
                    values.put(ScreeningResultCode.SCREENING_DATE, screeningDate);
                }

                // อัพเดทข้อมูลเดิมให้เป็น INACTIVE (ถ้ามี)
                ContentValues updateValues = new ContentValues();
                updateValues.put(ScreeningResultCode.STATUS, ScreeningResultCode.STATUS_INACTIVE);
                updateValues.put(ScreeningResultCode.UPDATETIME, DateConverter.getCurrentWesternDate());

                String whereClause = ScreeningResultCode.PERSON_ID + "=? AND " +
                        ScreeningResultCode.VISITNO + "=? AND " +
                        ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                        ScreeningResultCode.SCREENING_DATE + "=? AND " +
                        ScreeningResultCode.STATUS + "=?";
                String[] whereArgs = {personId, visitno, screeningType, screeningDate, ScreeningResultCode.STATUS_ACTIVE};

                db.update(ScreeningResultCode.TABLENAME, updateValues, whereClause, whereArgs);

                // Insert ข้อมูลใหม่
                id = db.insert(ScreeningResultCode.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(ScreeningResultCode.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // แจ้งเตือน URI อื่นๆ ที่เกี่ยวข้อง
            getContext().getContentResolver().notifyChange(ScreeningResultCode.CONTENT_LIST_URI, null);
        }

        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case SCREENING_RESULT_ITEMS:
                count = db.delete(ScreeningResultCode.TABLENAME, selection, selectionArgs);
                break;

            case SCREENING_RESULT_ITEM_ID:
                selection = ScreeningResultCode.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(ScreeningResultCode.TABLENAME, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_PERSON:
                String personId = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.PERSON_ID + "=?";
                selectionArgs = new String[]{personId};
                count = db.delete(ScreeningResultCode.TABLENAME, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_VISIT:
                String visitno = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                count = db.delete(ScreeningResultCode.TABLENAME, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_PERSON_VISIT:
                String personIdVisit = uri.getPathSegments().get(2);
                String visitnoPersonVisit = uri.getPathSegments().get(4);
                selection = ScreeningResultCode.PERSON_ID + "=? AND " + ScreeningResultCode.VISITNO + "=?";
                selectionArgs = new String[]{personIdVisit, visitnoPersonVisit};
                count = db.delete(ScreeningResultCode.TABLENAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(ScreeningResultCode.CONTENT_LIST_URI, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;

        // อัพเดท updatetime ทุกครั้งที่มีการแก้ไข
        if (values != null) {
            values.put(ScreeningResultCode.UPDATETIME, "datetime('now')");
        }

        switch (mUriMatcher.match(uri)) {
            case SCREENING_RESULT_ITEMS:
                rowUpdated = db.update(ScreeningResultCode.TABLENAME, values, selection, selectionArgs);
                break;

            case SCREENING_RESULT_ITEM_ID:
                selection = ScreeningResultCode.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(ScreeningResultCode.TABLENAME, values, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_PERSON:
                String personId = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.PERSON_ID + "=?";
                selectionArgs = new String[]{personId};
                rowUpdated = db.update(ScreeningResultCode.TABLENAME, values, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_VISIT:
                String visitno = uri.getPathSegments().get(2);
                selection = ScreeningResultCode.VISITNO + "=?";
                selectionArgs = new String[]{visitno};
                rowUpdated = db.update(ScreeningResultCode.TABLENAME, values, selection, selectionArgs);
                break;

            case SCREENING_RESULT_BY_PERSON_VISIT:
                String personIdVisit = uri.getPathSegments().get(2);
                String visitnoPersonVisit = uri.getPathSegments().get(4);
                selection = ScreeningResultCode.PERSON_ID + "=? AND " + ScreeningResultCode.VISITNO + "=?";
                selectionArgs = new String[]{personIdVisit, visitnoPersonVisit};
                rowUpdated = db.update(ScreeningResultCode.TABLENAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(ScreeningResultCode.CONTENT_LIST_URI, null);
        }

        return rowUpdated;
    }

    /**
     * Helper method สำหรับการบันทึกผลการคัดกรอง
     */
    public static Uri insertScreeningResult(ContentResolver resolver, ContentValues values) {
        return resolver.insert(ScreeningResultCode.CONTENT_URI, values);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลผลการคัดกรองล่าสุดของผู้รับบริการ
     */
    public static Cursor getLatestScreeningResults(ContentResolver resolver, int personId) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/screening_result/person/" + personId + "/latest");
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลผลการคัดกรองตามประเภท
     */
    public static Cursor getScreeningResultsByType(ContentResolver resolver, String screeningType) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/screening_result/type/" + screeningType);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลผลการคัดกรองทั้งหมดของผู้รับบริการ
     */
    public static Cursor getScreeningResultsByPerson(ContentResolver resolver, int personId) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/screening_result/person/" + personId);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลผลการคัดกรองตาม visitno
     */
    public static Cursor getScreeningResultsByVisit(ContentResolver resolver, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/screening_result/visit/" + visitno);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการดึงข้อมูลผลการคัดกรองตาม person และ visit
     */
    public static Cursor getScreeningResultsByPersonAndVisit(ContentResolver resolver, int personId, int visitno) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/screening_result/person/" + personId + "/visit/" + visitno);
        return resolver.query(uri, null, null, null, null);
    }

    /**
     * Helper method สำหรับการอัพเดทสถานะเป็น INACTIVE
     */
    public static int deactivateScreeningResult(ContentResolver resolver, long resultId) {
        ContentValues values = new ContentValues();
        values.put(ScreeningResultCode.STATUS, ScreeningResultCode.STATUS_INACTIVE);

        Uri uri = ContentUris.withAppendedId(ScreeningResultCode.CONTENT_URI, resultId);
        return resolver.update(uri, values, null, null);
    }

    /**
     * Helper method สำหรับการดึงวันที่ปัจจุบัน
     */
    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    /**
     * Utility methods สำหรับการบันทึกผลการคัดกรองแต่ละประเภท
     */
    public static class ScreeningResultHelper {

        /**
         * บันทึกผลการคัดกรอง 2Q
         */
        public static Uri save2QResult(ContentResolver resolver, int personId, int visitno, boolean hasPositiveAnswer, String userCreate) {
            ContentValues values = ScreeningResultCode.ScreeningResult.create2QResult(personId, visitno, hasPositiveAnswer, userCreate);
            return insertScreeningResult(resolver, values);
        }

        /**
         * บันทึกผลการคัดกรอง 9Q
         */
        public static Uri save9QResult(ContentResolver resolver, int personId, int visitno, int totalScore, String userCreate) {
            ContentValues values = ScreeningResultCode.ScreeningResult.create9QResult(personId, visitno, totalScore, userCreate);
            return insertScreeningResult(resolver, values);
        }

        /**
         * บันทึกผลการคัดกรอง 8Q
         */
        public static Uri save8QResult(ContentResolver resolver, int personId, int visitno, int totalScore, String userCreate) {
            ContentValues values = ScreeningResultCode.ScreeningResult.create8QResult(personId, visitno, totalScore, userCreate);
            return insertScreeningResult(resolver, values);
        }

        /**
         * บันทึกผลการคัดกรอง ST5
         */
        public static Uri saveST5Result(ContentResolver resolver, int personId, int visitno, int totalScore, String userCreate) {
            ContentValues values = ScreeningResultCode.ScreeningResult.createST5Result(personId, visitno, totalScore, userCreate);
            return insertScreeningResult(resolver, values);
        }

        /**
         * ดึงผลการคัดกรองล่าสุดตามประเภท
         */
        public static ScreeningResultData getLatestResultByType(ContentResolver resolver, int personId, String screeningType) {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), screeningType, ScreeningResultCode.STATUS_ACTIVE};
            String sortOrder = ScreeningResultCode.VISITNO + " DESC, " +
                    ScreeningResultCode.SCREENING_DATE + " DESC, " +
                    ScreeningResultCode.CREATETIME + " DESC LIMIT 1";

            Cursor cursor = resolver.query(ScreeningResultCode.CONTENT_LIST_URI, null, selection, selectionArgs, sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                ScreeningResultData result = new ScreeningResultData(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        }

        /**
         * ดึงผลการคัดกรองล่าสุดตามประเภทและ visitno
         */
        public static ScreeningResultData getLatestResultByTypeAndVisit(ContentResolver resolver, int personId, int visitno, String screeningType) {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), String.valueOf(visitno), screeningType, ScreeningResultCode.STATUS_ACTIVE};
            String sortOrder = ScreeningResultCode.SCREENING_DATE + " DESC, " +
                    ScreeningResultCode.CREATETIME + " DESC LIMIT 1";

            Cursor cursor = resolver.query(ScreeningResultCode.CONTENT_LIST_URI, null, selection, selectionArgs, sortOrder);

            if (cursor != null && cursor.moveToFirst()) {
                ScreeningResultData result = new ScreeningResultData(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        }

        /**
         * ตรวจสอบว่ามีผลการคัดกรองผิดปกติหรือไม่
         */
        public static boolean hasAbnormalResults(ContentResolver resolver, int personId) {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.IS_ABNORMAL + "=1 AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = resolver.query(ScreeningResultCode.CONTENT_LIST_URI,
                    new String[]{ScreeningResultCode.ID}, selection, selectionArgs, null);

            boolean hasAbnormal = false;
            if (cursor != null) {
                hasAbnormal = cursor.getCount() > 0;
                cursor.close();
            }

            return hasAbnormal;
        }

        /**
         * ตรวจสอบว่ามีผลการคัดกรองผิดปกติในการเยี่ยมนั้นๆ หรือไม่
         */
        public static boolean hasAbnormalResultsInVisit(ContentResolver resolver, int personId, int visitno) {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.IS_ABNORMAL + "=1 AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), String.valueOf(visitno), ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = resolver.query(ScreeningResultCode.CONTENT_LIST_URI,
                    new String[]{ScreeningResultCode.ID}, selection, selectionArgs, null);

            boolean hasAbnormal = false;
            if (cursor != null) {
                hasAbnormal = cursor.getCount() > 0;
                cursor.close();
            }

            return hasAbnormal;
        }
    }

    /**
     * คลาสสำหรับเก็บข้อมูลผลการคัดกรอง
     */
    public static class ScreeningResultData {
        public long id;
        public int personId;
        public int visitno;
        public String screeningType;
        public String resultCode;
        public String resultDescription;
        public int totalScore;
        public String riskLevel;
        public boolean isAbnormal;
        public String recommendation;
        public String screeningDate;
        public String status;
        public String createTime;
        public String updateTime;

        public ScreeningResultData(Cursor cursor) {
            this.id = cursor.getLong(cursor.getColumnIndex(ScreeningResultCode.ID));
            this.personId = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.PERSON_ID));
            this.visitno = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.VISITNO));
            this.screeningType = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.SCREENING_TYPE));
            this.resultCode = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RESULT_CODE));
            this.resultDescription = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RESULT_DESCRIPTION));
            this.totalScore = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.TOTAL_SCORE));
            this.riskLevel = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RISK_LEVEL));
            this.isAbnormal = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.IS_ABNORMAL)) == 1;
            this.recommendation = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RECOMMENDATION));
            this.screeningDate = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.SCREENING_DATE));
            this.status = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.STATUS));
            this.createTime = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.CREATETIME));
            this.updateTime = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.UPDATETIME));
        }

        @Override
        public String toString() {
            return "ScreeningResultData{" +
                    "id=" + id +
                    ", personId=" + personId +
                    ", visitno=" + visitno +
                    ", screeningType='" + screeningType + '\'' +
                    ", resultCode='" + resultCode + '\'' +
                    ", resultDescription='" + resultDescription + '\'' +
                    ", totalScore=" + totalScore +
                    ", riskLevel='" + riskLevel + '\'' +
                    ", isAbnormal=" + isAbnormal +
                    ", screeningDate='" + screeningDate + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }
}