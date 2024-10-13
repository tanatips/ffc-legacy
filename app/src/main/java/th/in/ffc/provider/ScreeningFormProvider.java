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
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class ScreeningFormProvider extends ContentProvider {
    public static String AUTHORITY = "th.in.ffc.provider.ScreeningFormProvider";

    private static final int SF_PERSON_INFO_ITEMS = 1;

    private static final int SF_PERSON_INFO_ITEM_ID = 2;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.sfpersoninfo";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.sfpersoninfo";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "sf_person_info", SF_PERSON_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_person_info/#", SF_PERSON_INFO_ITEM_ID);
    }


    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());
            mOpenHelper.getWritableDatabase().execSQL(SfPersonInfo.CREATE_TABLE);
            return true;
        }
        catch (Exception e) {
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
            case ScreeningFormProvider.SF_PERSON_INFO_ITEMS:
                builder.setTables(SfPersonInfo.TABLENAME);
                builder.setProjectionMap(SfPersonInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_PERSON_INFO_ITEM_ID:
                selection = SfPersonInfo.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(SfPersonInfo.TABLENAME);
                builder.setProjectionMap(SfPersonInfo.PROJECTION_MAP);
                break;
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
            case ScreeningFormProvider.SF_PERSON_INFO_ITEMS:
                return CONTENT_DIR_TYPE;
            case ScreeningFormProvider.SF_PERSON_INFO_ITEM_ID:
                return CONTENT_DIR_TYPE;
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
        id = db.insert(SfPersonInfo.TABLENAME, null, values);
        if (id > 0) {
            uriReturn = ContentUris.withAppendedId(SfPersonInfo.CONTENT_URI, id);
        }
        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;
        switch (mUriMatcher.match(uri)) {
            case SF_PERSON_INFO_ITEM_ID:
                rowUpdated = db.update(SfPersonInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;

    }

    public static final class SfPersonInfo implements BaseColumns {
        public static final String TABLENAME = "sf_person_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_person_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_person_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_person_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String FNAME = "fname";
        public static final String LNAME = "lname";

        public static final String BIRTHDAY = "birthday";

        public static final String GENDER = "gender";

        public static final String PHONE = "phone";

        public static final String HN = "hn";

        public static final String AUTHEN_DATE = "authen_date";

        public static final String AUTHEN_CODE = "authen_code";

        public static final String WEIGHT = "weight";

        public static final String HEIGHT = "height";
        public static final String WAIST_SIZE = "waist_size";
        public static final String BP = "bp";
        public static final String SYSTOLIC_PRESSURE = "systolic_pressure";
        public static final String DIASTOLIC_PRESSURE = "diastolic_pressure";
        public static final String CREATE_BY = "create_by";
        public static final String CREATE_DATE = "create_date";
        public static final String UPDATE_BY = "update_by";
        public static final String UPDATE_DATE = "update_date";
        public static final String SEND_TO_CLAIM = "send_to_claim";
        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfPersonInfo.ID, "id AS " + SfPersonInfo.ID);
            PROJECTION_MAP.put(SfPersonInfo.IDCARD, "idcard AS " + SfPersonInfo.IDCARD);
            PROJECTION_MAP.put(SfPersonInfo.FNAME, "fname AS " + SfPersonInfo.FNAME);
            PROJECTION_MAP.put(SfPersonInfo.LNAME, "lname AS " + SfPersonInfo.LNAME);
            PROJECTION_MAP.put(SfPersonInfo.BIRTHDAY, "birthday AS " + SfPersonInfo.BIRTHDAY);
            PROJECTION_MAP.put(SfPersonInfo.GENDER, "gender AS " + SfPersonInfo.GENDER);
            PROJECTION_MAP.put(SfPersonInfo.PHONE, "phone AS " + SfPersonInfo.PHONE);
            PROJECTION_MAP.put(SfPersonInfo.HN, "hn AS " + SfPersonInfo.HN);
            PROJECTION_MAP.put(SfPersonInfo.AUTHEN_DATE, "authen_date AS " + SfPersonInfo.AUTHEN_DATE);
            PROJECTION_MAP.put(SfPersonInfo.AUTHEN_CODE, "authen_code AS " + SfPersonInfo.AUTHEN_CODE);
            PROJECTION_MAP.put(SfPersonInfo.WEIGHT, "weight AS " + SfPersonInfo.WEIGHT);
            PROJECTION_MAP.put(SfPersonInfo.HEIGHT, "height AS " + SfPersonInfo.HEIGHT);
            PROJECTION_MAP.put(SfPersonInfo.WAIST_SIZE, "waist_size AS " + SfPersonInfo.WAIST_SIZE);
            PROJECTION_MAP.put(SfPersonInfo.BP, "bp AS " + SfPersonInfo.BP);
            PROJECTION_MAP.put(SfPersonInfo.SYSTOLIC_PRESSURE, "systolic_pressure AS " + SfPersonInfo.SYSTOLIC_PRESSURE);
            PROJECTION_MAP.put(SfPersonInfo.DIASTOLIC_PRESSURE, "diastolic_pressure AS " + SfPersonInfo.DIASTOLIC_PRESSURE);
            PROJECTION_MAP.put(SfPersonInfo.CREATE_BY, "create_by AS " + SfPersonInfo.CREATE_BY);
            PROJECTION_MAP.put(SfPersonInfo.CREATE_DATE, "create_date AS " + SfPersonInfo.CREATE_DATE);
            PROJECTION_MAP.put(SfPersonInfo.UPDATE_BY, "update_by AS " + SfPersonInfo.UPDATE_BY);
            PROJECTION_MAP.put(SfPersonInfo.UPDATE_DATE, "update_date AS " + SfPersonInfo.UPDATE_DATE);
            PROJECTION_MAP.put(SfPersonInfo.SEND_TO_CLAIM, "send_to_claim AS " + SfPersonInfo.SEND_TO_CLAIM);

        }
        public static final String CREATE_TABLE =" CREATE TABLE "+TABLENAME+" (" +
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IDCARD +" TEXT NOT NULL," +
                FNAME + " TEXT NOT NULL," +
                LNAME +" TEXT NOT NULL," +
                BIRTHDAY +"  TEXT, " +
                GENDER +" TEXT," +
                PHONE+ " TEXT," +
                HN +" TEXT," +
                AUTHEN_DATE +" DATE,  " +
                AUTHEN_CODE +" AUTHEN_CODE,  " +
                WEIGHT +" REAL," +
                HEIGHT +" REAL," +
                WAIST_SIZE +" REAL," +
                BP +" TEXT, " +
                SYSTOLIC_PRESSURE +" REAL," +
                DIASTOLIC_PRESSURE +" REAL," +
                CREATE_BY +" TEXT," +
                CREATE_DATE +" DATE," +
                UPDATE_BY +" TEXT," +
                UPDATE_DATE +" DATE," +
                SEND_TO_CLAIM + " INTEGER "+   // 0=ยังไม่ส่งไป สปสช  , 1=ส่งข้อมูลไป สปสช แล้ว
                ")";
          public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
//        public static final String SMOOKING = "smooking"; // 1B52, 1B51, 1B50
//        public static final String SMOOKING_FREQUENCY = "smooking_frequency"; // 1 = สูบนานๆ ครั้ง, 2 = สูบเป็นครั้งคราว 3 = สูบเป็นประจำ
//        public static final String SMOMOKIN_ADVICE = "smooking_advice";
//        // 1B530 (ฺฺBrief Advice)
//        // 1B531 (Counseling Advice)
//        // !B532 (Conseling Advice + MedicineX
//        public static final String DRINKING = "drinking";
//        public static final String DRINKING_FREQUENCY = "drinking_frequency";
//
//        public static final String DRINKING_POINT = "drinking_point";
//        public static final String RISK_LEVEL = "risk_level";
//        public static final String DRINKING_ADVICE = "drinking_advice";
    }
}

