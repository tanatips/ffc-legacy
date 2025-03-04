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
import android.content.SharedPreferences;
import android.content.Context;
public class ScreeningFormProvider extends ContentProvider {
    public static String AUTHORITY = "th.in.ffc.provider.ScreeningFormProvider";

    private static final int SF_PERSON = 0;

    private static final int SF_PERSON_INFO_ITEMS = 1;

    private static final int SF_PERSON_INFO_ITEM_ID = 2;
    private static final int SF_SMOKER = 3;

    private static final int SF_SMOKER_INFO_ITEMS = 4;
    private static final int SF_SMOKER_INFO_ITEM_ID = 5;

    private static final int SF_STRESS_DEPRESSION = 6;

    private static final int SF_STRESS_DEPRESSION_INFO_ITEMS = 7;
    private static final int SF_STRESS_DEPRESSION_INFO_ID = 8;

    private static final int SF_NICOTINE_INFO = 9;

    private static final int SF_NICOTINE_INFO_ITEMS = 10;
    private static final int SF_NICOTINE_INFO_ID = 11;

    private static final int SF_DRINKING_INFO = 12;

    private static final int SF_DRINKING_INFO_ITEMS = 13;
    private static final int SF_DRINKING_INFO_ID = 14;

    private static  final int SF_SUICIDE_ASSESSMENT_8Q_INFO = 15;
    private static  final int SF_SUICIDE_ASSESSMENT_8Q_INFO_ITEMS = 16;
    private static  final int SF_SUICIDE_ASSESSMENT_8Q_INFO_ID = 17;

    private static  final int SF_STRESS_DEPRESSION_2Q_INFO = 18;
    private static  final int SF_STRESS_DEPRESSION_2Q_INFO_ITEMS = 19;
    private static  final int SF_STRESS_DEPRESSION_2Q_INFO_ID = 20;

    private static  final int SF_STRESS_DEPRESSION_9Q_INFO = 21;
    private static  final int SF_STRESS_DEPRESSION_9Q_INFO_ITEMS = 22;
    private static  final int SF_STRESS_DEPRESSION_9Q_INFO_ID = 23;

    private static final int SF_HEALTH_RISK_ASSESSMENT_INFO = 24;
    private static final int SF_HEALTH_RISK_ASSESSMENT_INFO_ITEMS = 25;
    private static final int SF_HEALTH_RISK_ASSESSMENT_INFO_ID = 26;

    private static final int SF_TOKEN = 27;
    private static final int SF_TOKEN_ITEMS = 28;
    private static final int SF_TOKEN_ITEM_ID = 29;

    private static final int SF_DRUGS = 30;
    private static final int SF_DRUGS_ITEMS = 31;
    private static final int SF_DRUGS_ITEM_ID = 32;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.sfpersoninfo";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.sfpersoninfo";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;
    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "sf_person_info", SF_PERSON);
        mUriMatcher.addURI(AUTHORITY, "sf_person_info/list", SF_PERSON_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_person_info/#", SF_PERSON_INFO_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_smoker_info", SF_SMOKER);
        mUriMatcher.addURI(AUTHORITY, "sf_smoker_info/list", SF_SMOKER_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_smoker_info/#", SF_SMOKER_INFO_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_info", SF_STRESS_DEPRESSION);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_info/list", SF_STRESS_DEPRESSION_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_info/#", SF_STRESS_DEPRESSION_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_nicotine_info", SF_NICOTINE_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_nicotine_info/list", SF_NICOTINE_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_nicotine_info/#", SF_NICOTINE_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_drinking_info", SF_DRINKING_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_drinking_info/list", SF_DRINKING_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_drinking_info/#", SF_DRINKING_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_suicide_assessment_8q_info", SF_SUICIDE_ASSESSMENT_8Q_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_suicide_assessment_8q_info/list", SF_SUICIDE_ASSESSMENT_8Q_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_suicide_assessment_8q_info/#", SF_SUICIDE_ASSESSMENT_8Q_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_9q_info", SF_STRESS_DEPRESSION_9Q_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_9q_info/list", SF_STRESS_DEPRESSION_9Q_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_9q_info/#", SF_STRESS_DEPRESSION_9Q_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_2q_info", SF_STRESS_DEPRESSION_2Q_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_2q_info/list", SF_STRESS_DEPRESSION_2Q_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_stress_depression_2q_info/#", SF_STRESS_DEPRESSION_2Q_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_health_risk_assessment_info", SF_HEALTH_RISK_ASSESSMENT_INFO);
        mUriMatcher.addURI(AUTHORITY, "sf_health_risk_assessment_info/list", SF_HEALTH_RISK_ASSESSMENT_INFO_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_health_risk_assessment_info/#", SF_HEALTH_RISK_ASSESSMENT_INFO_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_token", SF_TOKEN);
        mUriMatcher.addURI(AUTHORITY, "sf_token/list", SF_TOKEN_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_token/#", SF_TOKEN_ITEM_ID);

        mUriMatcher.addURI(AUTHORITY, "sf_drugs", SF_DRUGS);
        mUriMatcher.addURI(AUTHORITY, "sf_drugs/list", SF_DRUGS_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "sf_drugs/#", SF_DRUGS_ITEM_ID);
    }


    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());
            // เพิ่มการตรวจสอบการใช้งานครั้งแรก
//            SharedPreferences prefs = getContext().getSharedPreferences("DatabasePrefs", Context.MODE_PRIVATE);
//            boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
//            if(isFirstRun) {
//                mOpenHelper.getWritableDatabase().execSQL(SfPersonInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfSmokerInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepressionInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfNicotineInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfDrinkingInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepression2qInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepression9qInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfSuicideAssessment8qInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfHealthRiskAssessmentInfo.DROP_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfPersonInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfSmokerInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepressionInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfNicotineInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfDrinkingInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepression2qInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfStressDepression9qInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfSuicideAssessment8qInfo.CREATE_TABLE);
//                mOpenHelper.getWritableDatabase().execSQL(SfHealthRiskAssessmentInfo.CREATE_TABLE);
//            }
//            for (String alterStatement : SfPersonInfo.ALTER_TABLE) {
//                mOpenHelper.getWritableDatabase().execSQL(alterStatement);
//            }
//            mOpenHelper.getWritableDatabase().execSQL(SfDrugs.DROP_TABLE);
//            mOpenHelper.getWritableDatabase().execSQL(SfDrugs.CREATE_TABLE);
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
            case ScreeningFormProvider.SF_SMOKER_INFO_ITEMS:
                builder.setTables(SfSmokerInfo.TABLENAME);
                builder.setProjectionMap(SfPersonInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_SMOKER_INFO_ITEM_ID:
//                selection = SfSmokerInfo.ID + "=?";
                builder.setTables(SfSmokerInfo.TABLENAME);
                builder.setProjectionMap(SfSmokerInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_STRESS_DEPRESSION_INFO_ITEMS:
                builder.setTables(SfStressDepressionInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepressionInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_STRESS_DEPRESSION_INFO_ID:
//                selection = SfStressDepressionInfo.ID + "=?";
                builder.setTables(SfStressDepressionInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepressionInfo.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_NICOTINE_INFO_ITEMS:
                builder.setTables(SfNicotineInfo.TABLENAME);
                builder.setProjectionMap(SfNicotineInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_NICOTINE_INFO_ID:
//                selection = SfNicotineInfo.ID + "=?";
                builder.setTables(SfNicotineInfo.TABLENAME);
                builder.setProjectionMap(SfNicotineInfo.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_DRINKING_INFO_ITEMS:
                builder.setTables(SfDrinkingInfo.TABLENAME);
                builder.setProjectionMap(SfDrinkingInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_DRINKING_INFO_ID:
//                selection = SfDrinkingInfo.ID + "=?";
                builder.setTables(SfDrinkingInfo.TABLENAME);
                builder.setProjectionMap(SfDrinkingInfo.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_SUICIDE_ASSESSMENT_8Q_INFO_ITEMS:
                builder.setTables(SfSuicideAssessment8qInfo.TABLENAME);
                builder.setProjectionMap(SfSuicideAssessment8qInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_SUICIDE_ASSESSMENT_8Q_INFO_ID:
//                selection = SfSuicideAssessment8qInfo.ID + "=?";
                builder.setTables(SfSuicideAssessment8qInfo.TABLENAME);
                builder.setProjectionMap(SfSuicideAssessment8qInfo.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_STRESS_DEPRESSION_2Q_INFO_ITEMS:
                builder.setTables(SfStressDepression2qInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepression2qInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_STRESS_DEPRESSION_2Q_INFO_ID:
//                selection = SfStressDepression2qInfo.ID + "=?";
                builder.setTables(SfStressDepression2qInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepression2qInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_STRESS_DEPRESSION_9Q_INFO_ITEMS:
                builder.setTables(SfStressDepression9qInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepression9qInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_STRESS_DEPRESSION_9Q_INFO_ID:
//                selection = SfStressDepression9qInfo.ID + "=?";
                builder.setTables(SfStressDepression9qInfo.TABLENAME);
                builder.setProjectionMap(SfStressDepression9qInfo.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_HEALTH_RISK_ASSESSMENT_INFO_ITEMS:
                builder.setTables(SfHealthRiskAssessmentInfo.TABLENAME);
                builder.setProjectionMap(SfHealthRiskAssessmentInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_HEALTH_RISK_ASSESSMENT_INFO_ID:
//                selection = SfHealthRiskAssessmentInfo.ID + "=?";
                builder.setTables(SfHealthRiskAssessmentInfo.TABLENAME);
                builder.setProjectionMap(SfHealthRiskAssessmentInfo.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_TOKEN_ITEMS:
                builder.setTables(SfToken.TABLENAME);
                builder.setProjectionMap(SfToken.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_TOKEN_ITEM_ID:
                builder.setTables(SfToken.TABLENAME);
                builder.setProjectionMap(SfToken.PROJECTION_MAP);
                break;

            case ScreeningFormProvider.SF_DRUGS_ITEMS:
                builder.setTables(SfDrugs.TABLENAME);
                builder.setProjectionMap(SfDrugs.PROJECTION_MAP);
                break;
            case ScreeningFormProvider.SF_DRUGS_ITEM_ID:
                builder.setTables(SfDrugs.TABLENAME);
                builder.setProjectionMap(SfDrugs.PROJECTION_MAP);
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
            case SF_DRUGS_ITEMS:
                return SfDrugs.CONTENT_DIR_TYPE;
            case SF_DRUGS_ITEM_ID:
                return SfDrugs.CONTENT_ITEM_TYPE;
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
            case SF_PERSON:
                id = db.insert(SfPersonInfo.TABLENAME, null, values);
                break;
            case SF_SMOKER:
                id = db.insert(SfSmokerInfo.TABLENAME, null, values);
                break;
            case SF_STRESS_DEPRESSION:
                id = db.insert(SfStressDepressionInfo.TABLENAME, null, values);
                break;
            case SF_NICOTINE_INFO:
                id = db.insert(SfNicotineInfo.TABLENAME, null, values);
                break;
            case SF_DRINKING_INFO:
                id = db.insert(SfDrinkingInfo.TABLENAME, null, values);
                break;
            case SF_STRESS_DEPRESSION_2Q_INFO:
                id = db.insert(SfStressDepression2qInfo.TABLENAME, null, values);
                break;
            case SF_STRESS_DEPRESSION_9Q_INFO:
                id = db.insert(SfStressDepression9qInfo.TABLENAME, null, values);
                break;
            case SF_SUICIDE_ASSESSMENT_8Q_INFO:
                id = db.insert(SfSuicideAssessment8qInfo.TABLENAME, null, values);
                break;
            case SF_HEALTH_RISK_ASSESSMENT_INFO:
                id = db.insert(SfHealthRiskAssessmentInfo.TABLENAME, null, values);
                break;
            case SF_TOKEN:
                id = db.insert(SfToken.TABLENAME, null, values);
                break;
            case SF_DRUGS:
                id = db.insert(SfDrugs.TABLENAME, null, values);
                break;

        }
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
            case SF_SMOKER_INFO_ITEM_ID:
                rowUpdated = db.update(SfSmokerInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_NICOTINE_INFO_ID:
                rowUpdated = db.update(SfNicotineInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_STRESS_DEPRESSION_INFO_ID:
                rowUpdated = db.update(SfStressDepressionInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_DRINKING_INFO_ID:
                rowUpdated = db.update(SfDrinkingInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_STRESS_DEPRESSION_2Q_INFO_ID:
                rowUpdated = db.update(SfStressDepression2qInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_STRESS_DEPRESSION_9Q_INFO_ID:
                rowUpdated = db.update(SfStressDepression9qInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_SUICIDE_ASSESSMENT_8Q_INFO_ID:
                rowUpdated = db.update(SfSuicideAssessment8qInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_HEALTH_RISK_ASSESSMENT_INFO_ID:
                rowUpdated = db.update(SfHealthRiskAssessmentInfo.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_TOKEN_ITEM_ID:
                rowUpdated = db.update(SfToken.TABLENAME, contentValues, selection, selectionArgs);
                break;
            case SF_DRUGS_ITEM_ID:
                rowUpdated = db.update(SfDrugs.TABLENAME, contentValues, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;

    }

    public static final class SfPersonInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_person_info";

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
        public static final String BMI = "bmi";
        public static final String SYSTOLIC_PRESSURE = "systolic_pressure";
        public static final String DIASTOLIC_PRESSURE = "diastolic_pressure";

        public static final String SERVICE_CODE = "serviceCode";
        public static final String TRANS_ID = "transId";
        public static final String SOURCE_ID = "sourceId";
        public static final String SUB_DIST_NAME = "subDistName";
        public static final String SUB_DIST_CODE = "subDistCode";
        public static final String DIST_CODE = "distCode";
        public static final String DIST_NAME = "distName";
        public static final String PROV_CODE = "provCode";
        public static final String PROV_NAME = "provName";
        public static final String POST_CODE = "postCode";
        public static final String HOME_NO = "homeNo";

        public static final String VILLAGE_NO = "villageNo";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
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
            PROJECTION_MAP.put(SfPersonInfo.BMI, "bp AS " + SfPersonInfo.BMI);
            PROJECTION_MAP.put(SfPersonInfo.SYSTOLIC_PRESSURE, "systolic_pressure AS " + SfPersonInfo.SYSTOLIC_PRESSURE);
            PROJECTION_MAP.put(SfPersonInfo.DIASTOLIC_PRESSURE, "diastolic_pressure AS " + SfPersonInfo.DIASTOLIC_PRESSURE);
            PROJECTION_MAP.put(SfPersonInfo.CREATED_BY, "created_by AS " + SfPersonInfo.CREATED_BY);
            PROJECTION_MAP.put(SfPersonInfo.CREATED_DATE, "created_date AS " + SfPersonInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfPersonInfo.UPDATED_BY, "updated_by AS " + SfPersonInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfPersonInfo.UPDATED_DATE, "updated_date AS " + SfPersonInfo.UPDATED_DATE);
            PROJECTION_MAP.put(SfPersonInfo.SEND_TO_CLAIM, "send_to_claim AS " + SfPersonInfo.SEND_TO_CLAIM);
            PROJECTION_MAP.put(SfPersonInfo.SERVICE_CODE, "serviceCode AS " + SfPersonInfo.SERVICE_CODE);
            PROJECTION_MAP.put(SfPersonInfo.TRANS_ID, "transId AS " + SfPersonInfo.TRANS_ID);
            PROJECTION_MAP.put(SfPersonInfo.SOURCE_ID, "sourceId AS " + SfPersonInfo.SOURCE_ID);
            PROJECTION_MAP.put(SfPersonInfo.SUB_DIST_NAME, "subDistName AS " + SfPersonInfo.SUB_DIST_NAME);
            PROJECTION_MAP.put(SfPersonInfo.SUB_DIST_CODE, "subDistCode AS " + SfPersonInfo.SUB_DIST_CODE);
            PROJECTION_MAP.put(SfPersonInfo.DIST_CODE, "distCode AS " + SfPersonInfo.DIST_CODE);
            PROJECTION_MAP.put(SfPersonInfo.DIST_NAME, "distName AS " + SfPersonInfo.DIST_NAME);
            PROJECTION_MAP.put(SfPersonInfo.PROV_CODE, "provCode AS " + SfPersonInfo.PROV_CODE);
            PROJECTION_MAP.put(SfPersonInfo.PROV_NAME, "provName AS " + SfPersonInfo.PROV_NAME);
            PROJECTION_MAP.put(SfPersonInfo.POST_CODE, "postCode AS " + SfPersonInfo.POST_CODE);
            PROJECTION_MAP.put(SfPersonInfo.HOME_NO, "homeNo AS " + SfPersonInfo.HOME_NO);
            PROJECTION_MAP.put(SfPersonInfo.VILLAGE_NO, "villageNo AS " + SfPersonInfo.VILLAGE_NO);

        }
//        public static final String CREATE_TABLE =" CREATE TABLE IF NOT EXISTS "+TABLENAME+" (" +
//                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                IDCARD +" TEXT NOT NULL," +
//                FNAME + " TEXT NOT NULL," +
//                LNAME +" TEXT NOT NULL," +
//                BIRTHDAY +"  TEXT, " +
//                GENDER +" TEXT," +
//                PHONE+ " TEXT," +
//                HN +" TEXT," +
//                AUTHEN_DATE +" DATE,  " +
//                AUTHEN_CODE +" AUTHEN_CODE,  " +
//                WEIGHT +" REAL," +
//                HEIGHT +" REAL," +
//                WAIST_SIZE +" REAL," +
//                BP +" TEXT, " +
//                BMI +" TEXT, " +
//                SYSTOLIC_PRESSURE +" REAL," +
//                DIASTOLIC_PRESSURE +" REAL," +
//                CREATED_BY +" TEXT," +
//                CREATED_DATE +" DATE," +
//                UPDATED_BY +" TEXT," +
//                UPDATED_DATE +" DATE," +
//                SEND_TO_CLAIM + " INTEGER "+   // 0=ยังไม่ส่งไป สปสช  , 1=ส่งข้อมูลไป สปสช แล้ว
//                ")";
public static final String CREATE_TABLE =" CREATE TABLE IF NOT EXISTS "+TABLENAME+" (" +
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
        BMI +" TEXT, " +
        SYSTOLIC_PRESSURE +" REAL," +
        DIASTOLIC_PRESSURE +" REAL," +
        SERVICE_CODE + " TEXT, " +
        TRANS_ID + " TEXT, " +
        SOURCE_ID + " TEXT, " +
        SUB_DIST_NAME + " TEXT, " +
        SUB_DIST_CODE + " TEXT, " +
        DIST_CODE + " TEXT, " +
        DIST_NAME + " TEXT, " +
        PROV_CODE + " TEXT, " +
        PROV_NAME + " TEXT, " +
        POST_CODE + " TEXT, " +
        HOME_NO + " TEXT, " +
        VILLAGE_NO + " TEXT, " +
        CREATED_BY +" TEXT," +
        CREATED_DATE +" DATE," +
        UPDATED_BY +" TEXT," +
        UPDATED_DATE +" DATE," +
        SEND_TO_CLAIM + " INTEGER "+   // 0=ยังไม่ส่งไป สปสช  , 1=ส่งข้อมูลไป สปสช แล้ว
        ")";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;

        public static final String[] ALTER_TABLE = {
//                "ALTER TABLE ffc_sf_person_info ADD COLUMN serviceCode TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN transId TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN sourceId TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN subDistName TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN subDistCode TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN distCode TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN distName TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN provCode TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN provName TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN postCode TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN homeNo TEXT;",
                "ALTER TABLE ffc_sf_person_info ADD COLUMN villageNo TEXT;"
        };
    }

    public static final  class SfDrinkingInfo implements  BaseColumns {
        public static final String TABLENAME = "ffc_sf_drinking_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_drinking_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_drinking_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_drinking_info";

        public static final String ID = "id";

        public static final String ID_CARD = "idcard";

        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String DRINKING  = "drinking"; // 1B600, 1B601, 3
        public static final String DRINKING_FREQUENCY  = "drinking_frequency"; // 1B600, 1B601, 3
        public static final String DRINKING_ALWAY  = "drinking_alway"; // 1B600, 1B601, 3

        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String CREATE_TABLE =" CREATE TABLE IF NOT EXISTS "+TABLENAME+" (" +
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ID_CARD +" TEXT NOT NULL," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                DRINKING +" TEXT NOT NULL," +
                DRINKING_FREQUENCY +" TEXT NOT NULL," +
                DRINKING_ALWAY +" TEXT NOT NULL," +
                CREATED_BY +" TEXT," +
                CREATED_DATE +" DATE," +
                UPDATED_BY +" TEXT," +
                UPDATED_DATE +" DATE"+
                ")";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
    }

    public static final class SfSmokerInfo implements  BaseColumns {
        public static final String TABLENAME = "ffc_sf_smoker_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_smoker_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_smoker_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_smoker_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String SMOKER_GROUP  = "smoker_group";

        public static final String SMOKER_ASSIST  = "smoker_assist";
        public static final String SMOKER_REGULARLY  = "smoker_regularly";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String CREATE_TABLE =" CREATE TABLE IF NOT EXISTS "+TABLENAME+" (" +
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IDCARD +" TEXT NOT NULL," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                SMOKER_GROUP +" TEXT NOT NULL," +
                SMOKER_ASSIST +" TEXT NOT NULL," +
                SMOKER_REGULARLY +" TEXT NOT NULL," +
                CREATED_BY +" TEXT," +
                CREATED_DATE +" DATE," +
                UPDATED_BY +" TEXT," +
                UPDATED_DATE +" DATE " + ")";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfSmokerInfo.ID, "id AS " + SfSmokerInfo.ID);
            PROJECTION_MAP.put(SfSmokerInfo.IDCARD, "idcard AS " + SfSmokerInfo.IDCARD);
            PROJECTION_MAP.put(SfSmokerInfo.PERSON_INFO_ID, "person_info_id AS " + SfSmokerInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfSmokerInfo.SMOKER_GROUP, "smoker_group AS " + SfSmokerInfo.SMOKER_GROUP);
            PROJECTION_MAP.put(SfSmokerInfo.SMOKER_ASSIST, "smoker_assist AS " + SfSmokerInfo.SMOKER_ASSIST);
            PROJECTION_MAP.put(SfSmokerInfo.SMOKER_REGULARLY, "smoker_regularly AS " + SfSmokerInfo.SMOKER_REGULARLY);
            PROJECTION_MAP.put(SfSmokerInfo.CREATED_BY, "created_by AS " + SfSmokerInfo.CREATED_BY);
            PROJECTION_MAP.put(SfSmokerInfo.CREATED_DATE, "created_date AS " + SfSmokerInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfSmokerInfo.UPDATED_BY, "updated_by AS " + SfSmokerInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfSmokerInfo.UPDATED_DATE, "updated_date AS " + SfSmokerInfo.UPDATED_DATE);
        }
    }

    public static final class SfStressDepressionInfo implements  BaseColumns {
        public static final String TABLENAME = "ffc_sf_stress_depression_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_stress_depression_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_info";
        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";

        public static final String Q1 = "q1";
        public static final String Q2 = "q2";
        public static final String Q3 = "q3";
        public static final String Q4 = "q4";
        public static final String Q5 = "q5";

        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";

        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
        public static final String CREATE_TABLE =" CREATE TABLE IF NOT EXISTS "+TABLENAME+" (" +
                ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                IDCARD +" TEXT NOT NULL," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                Q1 +" TEXT NOT NULL," +
                Q2 +" TEXT NOT NULL," +
                Q3 +" TEXT NOT NULL," +
                Q4 +" TEXT NOT NULL," +
                Q5 +" TEXT NOT NULL," +
                CREATED_BY +" TEXT," +
                CREATED_DATE +" DATE," +
                UPDATED_BY +" TEXT," +
                UPDATED_DATE +" DATE " + ")";
        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfStressDepressionInfo.ID, "id AS " + SfStressDepressionInfo.ID);
            PROJECTION_MAP.put(SfStressDepressionInfo.IDCARD, "idcard AS " + SfStressDepressionInfo.IDCARD);
            PROJECTION_MAP.put(SfStressDepressionInfo.PERSON_INFO_ID, "person_info_id AS " + SfStressDepressionInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfStressDepressionInfo.Q1, "q1 AS " + SfStressDepressionInfo.Q1);
            PROJECTION_MAP.put(SfStressDepressionInfo.Q2, "q2 AS " + SfStressDepressionInfo.Q2);
            PROJECTION_MAP.put(SfStressDepressionInfo.Q3, "q3 AS " + SfStressDepressionInfo.Q3);
            PROJECTION_MAP.put(SfStressDepressionInfo.Q4, "q4 AS " + SfStressDepressionInfo.Q4);
            PROJECTION_MAP.put(SfStressDepressionInfo.Q5, "q5 AS " + SfStressDepressionInfo.Q5);
            PROJECTION_MAP.put(SfStressDepressionInfo.CREATED_BY, "created_by AS " + SfStressDepressionInfo.CREATED_BY);
            PROJECTION_MAP.put(SfStressDepressionInfo.CREATED_DATE, "created_date AS " + SfStressDepressionInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfStressDepressionInfo.UPDATED_BY, "updated_by AS " + SfStressDepressionInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfStressDepressionInfo.UPDATED_DATE, "updated_date AS " + SfStressDepressionInfo.UPDATED_DATE);
        }
    }
    public static final class SfNicotineInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_nicotine_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_nicotine_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_nicotine_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_nicotine_info";

        public static final String ID = "id";

        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String NICOTINE1 = "nicotine1";
        public static final String NICOTINE2 = "nicotine2";
        public static final String NICOTINE3 = "nicotine3";
        public static final String NICOTINE4 = "nicotine4";
        public static final String NICOTINE5 = "nicotine5";
        public static final String NICOTINE6 = "nicotine6";
        public static final String POINTS = "points";
        public static final String SUM = "sum";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                IDCARD + " TEXT NOT NULL," +
                NICOTINE1 + " TEXT NOT NULL," +
                NICOTINE2 + " TEXT NOT NULL," +
                NICOTINE3 + " TEXT NOT NULL," +
                NICOTINE4 + " TEXT NOT NULL," +
                NICOTINE5 + " TEXT NOT NULL," +
                NICOTINE6 + " TEXT NOT NULL," +
                POINTS + " TEXT NOT NULL," +
                SUM + " INTEGER NOT NULL," +
                CREATED_BY + " TEXT," +
                CREATED_DATE + " DATE," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " DATE " + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfNicotineInfo.ID, "id AS " + SfNicotineInfo.ID);
            PROJECTION_MAP.put(SfNicotineInfo.IDCARD, "idcard AS " + SfNicotineInfo.IDCARD);
            PROJECTION_MAP.put(SfNicotineInfo.PERSON_INFO_ID, "person_info_id AS " + SfNicotineInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE1, "nicotine1 AS " + SfNicotineInfo.NICOTINE1);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE2, "nicotine2 AS " + SfNicotineInfo.NICOTINE2);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE3, "nicotine3 AS " + SfNicotineInfo.NICOTINE3);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE4, "nicotine4 AS " + SfNicotineInfo.NICOTINE4);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE5, "nicotine5 AS " + SfNicotineInfo.NICOTINE5);
            PROJECTION_MAP.put(SfNicotineInfo.NICOTINE6, "nicotine6 AS " + SfNicotineInfo.NICOTINE6);
            PROJECTION_MAP.put(SfNicotineInfo.POINTS, "points AS " + SfNicotineInfo.POINTS);
            PROJECTION_MAP.put(SfNicotineInfo.SUM, "sum AS " + SfNicotineInfo.SUM);
            PROJECTION_MAP.put(SfNicotineInfo.CREATED_BY, "created_by AS " + SfNicotineInfo.CREATED_BY);
            PROJECTION_MAP.put(SfNicotineInfo.CREATED_DATE, "created_date AS " + SfNicotineInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfNicotineInfo.UPDATED_BY, "updated_by AS " + SfNicotineInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfNicotineInfo.UPDATED_DATE, "updated_date AS " + SfNicotineInfo.UPDATED_DATE);
        }
    }

    public static final class SfStressDepression2qInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_stress_depression_2q_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_stress_depression_2q_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_2q_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_2q_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String Q1 = "q1";
        public static final String Q2 = "q2";
        public static final String POINTS = "points";
        public static final String SUM = "sum";
//        public static final String RESULT_CODE = "result_code";
//        public static final String RESULT_DESCRIPTION = "result_description";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;

        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                IDCARD + " TEXT NOT NULL," +
                Q1 + " TEXT NOT NULL," +
                Q2 + " TEXT NOT NULL," +
                POINTS + " TEXT NOT NULL," +
                SUM + " INTEGER NOT NULL," +
//                RESULT_CODE + " TEXT NOT NULL," +
//                RESULT_DESCRIPTION + " TEXT NOT NULL," +
                CREATED_BY + " TEXT," +
                CREATED_DATE + " DATE," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " DATE " + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfStressDepression2qInfo.ID, "id AS " + SfStressDepression2qInfo.ID);
            PROJECTION_MAP.put(SfStressDepression2qInfo.IDCARD, "idcard AS " + SfStressDepression2qInfo.IDCARD);
            PROJECTION_MAP.put(SfStressDepression2qInfo.PERSON_INFO_ID, "person_info_id AS " + SfStressDepression2qInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfStressDepression2qInfo.Q1, "q1 AS " + SfStressDepression2qInfo.Q1);
            PROJECTION_MAP.put(SfStressDepression2qInfo.Q2, "q2 AS " + SfStressDepression2qInfo.Q2);
            PROJECTION_MAP.put(SfStressDepression2qInfo.POINTS, "points AS " + SfStressDepression2qInfo.POINTS);
            PROJECTION_MAP.put(SfStressDepression2qInfo.SUM, "sum AS " + SfStressDepression2qInfo.SUM);
//            PROJECTION_MAP.put(SfStressDepression2qInfo.RESULT_CODE, "result_code AS " + SfStressDepression2qInfo.RESULT_CODE);
//            PROJECTION_MAP.put(SfStressDepression2qInfo.RESULT_DESCRIPTION, "result_description AS " + SfStressDepression2qInfo.RESULT_DESCRIPTION);
            PROJECTION_MAP.put(SfStressDepression2qInfo.CREATED_BY, "created_by AS " + SfStressDepression2qInfo.CREATED_BY);
            PROJECTION_MAP.put(SfStressDepression2qInfo.CREATED_DATE, "created_date AS " + SfStressDepression2qInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfStressDepression2qInfo.UPDATED_BY, "updated_by AS " + SfStressDepression2qInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfStressDepression2qInfo.UPDATED_DATE, "updated_date AS " + SfStressDepression2qInfo.UPDATED_DATE);
        }
    }
    public static final class SfStressDepression9qInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_stress_depression_9q_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_stress_depression_9q_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_9q_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_stress_depression_9q_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String Q1 = "q1";
        public static final String Q2 = "q2";
        public static final String Q3 = "q3";
        public static final String Q4 = "q4";
        public static final String Q5 = "q5";
        public static final String Q6 = "q6";
        public static final String Q7 = "q7";
        public static final String Q8 = "q8";
        public static final String Q9 = "q9";
        public static final String POINTS = "points";
        public static final String SUM = "sum";
//        public static final String RESULT_CODE = "result_code";
//        public static final String RESULT_DESCRIPTION = "result_description";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                IDCARD + " TEXT NOT NULL," +
                Q1 + " TEXT NOT NULL," +
                Q2 + " TEXT NOT NULL," +
                Q3 + " TEXT NOT NULL," +
                Q4 + " TEXT NOT NULL," +
                Q5 + " TEXT NOT NULL," +
                Q6 + " TEXT NOT NULL," +
                Q7 + " TEXT NOT NULL," +
                Q8 + " TEXT NOT NULL," +
                Q9 + " TEXT NOT NULL," +
                POINTS + " TEXT NOT NULL," +
                SUM + " INTEGER NOT NULL," +
//                RESULT_CODE + " TEXT NOT NULL," +
//                RESULT_DESCRIPTION + " TEXT NOT NULL," +
                CREATED_BY + " TEXT," +
                CREATED_DATE + " DATE," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " DATE " + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfStressDepression9qInfo.ID, "id AS " + SfStressDepression9qInfo.ID);
            PROJECTION_MAP.put(SfStressDepression9qInfo.IDCARD, "idcard AS " + SfStressDepression9qInfo.IDCARD);
            PROJECTION_MAP.put(SfStressDepression9qInfo.PERSON_INFO_ID, "person_info_id AS " + SfStressDepression9qInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q1, "q1 AS " + SfStressDepression9qInfo.Q1);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q2, "q2 AS " + SfStressDepression9qInfo.Q2);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q3, "q3 AS " + SfStressDepression9qInfo.Q3);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q4, "q4 AS " + SfStressDepression9qInfo.Q4);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q5, "q5 AS " + SfStressDepression9qInfo.Q5);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q6, "q6 AS " + SfStressDepression9qInfo.Q6);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q7, "q7 AS " + SfStressDepression9qInfo.Q7);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q8, "q8 AS " + SfStressDepression9qInfo.Q8);
            PROJECTION_MAP.put(SfStressDepression9qInfo.Q9, "q9 AS " + SfStressDepression9qInfo.Q9);
            PROJECTION_MAP.put(SfStressDepression9qInfo.POINTS, "points AS " + SfStressDepression9qInfo.POINTS);
            PROJECTION_MAP.put(SfStressDepression9qInfo.SUM, "sum AS " + SfStressDepression9qInfo.SUM);
//            PROJECTION_MAP.put(SfStressDepression9qInfo.RESULT_CODE, "result_code AS " + SfStressDepression9qInfo.RESULT_CODE);
//            PROJECTION_MAP.put(SfStressDepression9qInfo.RESULT_DESCRIPTION, "result_description AS " + SfStressDepression9qInfo.RESULT_DESCRIPTION);
            PROJECTION_MAP.put(SfStressDepression9qInfo.CREATED_BY, "created_by AS " + SfStressDepression9qInfo.CREATED_BY);
            PROJECTION_MAP.put(SfStressDepression9qInfo.CREATED_DATE, "created_date AS " + SfStressDepression9qInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfStressDepression9qInfo.UPDATED_BY, "updated_by AS " + SfStressDepression9qInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfStressDepression9qInfo.UPDATED_DATE, "updated_date AS " + SfStressDepression9qInfo.UPDATED_DATE);
        }
    }

    public static final class SfSuicideAssessment8qInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_suicide_assessment_8q_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_suicide_assessment_8q_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_suicide_assessment_8q_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_suicide_assessment_8q_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String Q1 = "q1";
        public static final String Q2 = "q2";
        public static final String Q3 = "q3";
        public static final String Q3_2_1 = "q3_2_1";
        public static final String Q4 = "q4";
        public static final String Q5 = "q5";
        public static final String Q6 = "q6";
        public static final String Q7 = "q7";
        public static final String Q8 = "q8";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;
        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                IDCARD + " TEXT NOT NULL," +
                Q1 + " TEXT NOT NULL," +
                Q2 + " TEXT NOT NULL," +
                Q3 + " TEXT NOT NULL," +
                Q3_2_1 + " TEXT NOT NULL," +
                Q4 + " TEXT NOT NULL," +
                Q5 + " TEXT NOT NULL," +
                Q6 + " TEXT NOT NULL," +
                Q7 + " TEXT NOT NULL," +
                Q8 + " TEXT NOT NULL," +
                CREATED_BY + " TEXT," +
                CREATED_DATE + " DATE," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " DATE " + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.ID, "id AS " + SfSuicideAssessment8qInfo.ID);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.IDCARD, "idcard AS " + SfSuicideAssessment8qInfo.IDCARD);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.PERSON_INFO_ID, "person_info_id AS " + SfSuicideAssessment8qInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q1, "q1 AS " + SfSuicideAssessment8qInfo.Q1);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q2, "q2 AS " + SfSuicideAssessment8qInfo.Q2);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q3, "q3 AS " + SfSuicideAssessment8qInfo.Q3);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q3_2_1, "q3_2_1 AS " + SfSuicideAssessment8qInfo.Q3_2_1);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q4, "q4 AS " + SfSuicideAssessment8qInfo.Q4);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q5, "q5 AS " + SfSuicideAssessment8qInfo.Q5);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q6, "q6 AS " + SfSuicideAssessment8qInfo.Q6);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q7, "q7 AS " + SfSuicideAssessment8qInfo.Q7);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.Q8, "q8 AS " + SfSuicideAssessment8qInfo.Q8);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.CREATED_BY, "created_by AS " + SfSuicideAssessment8qInfo.CREATED_BY);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.CREATED_DATE, "created_date AS " + SfSuicideAssessment8qInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.UPDATED_BY, "updated_by AS " + SfSuicideAssessment8qInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfSuicideAssessment8qInfo.UPDATED_DATE, "updated_date AS " + SfSuicideAssessment8qInfo.UPDATED_DATE);
        }
    }

    public static final class SfHealthRiskAssessmentInfo implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_health_risk_assessment_info";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_health_risk_assessment_info");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_health_risk_assessment_info";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_health_risk_assessment_info";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String HEALTH_RISK_Q1 = "health_risk_q1";
        public static final String HEALTH_RISK_Q2 = "health_risk_q2";
        public static final String HEALTH_RISK_Q3 = "health_risk_q3";
        public static final String HEALTH_RISK_Q4 = "health_risk_q4";
        public static final String HEALTH_RISK_Q5 = "health_risk_q5";
        public static final String HEALTH_RISK_Q6 = "health_risk_q6";

        public static final String FCBG = "fcbg";

        public static final String FPG = "fpg";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";

        public static final String DROP_TABLE = " DROP TABLE IF EXISTS "+TABLENAME;

        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                IDCARD + " TEXT NOT NULL," +
                HEALTH_RISK_Q1 + " TEXT NOT NULL," +
                HEALTH_RISK_Q2 + " TEXT NOT NULL," +
                HEALTH_RISK_Q3 + " TEXT NOT NULL," +
                HEALTH_RISK_Q4 + " TEXT NOT NULL," +
                HEALTH_RISK_Q5 + " TEXT NOT NULL," +
                HEALTH_RISK_Q6 + " TEXT NOT NULL," +
                FCBG + " TEXT NOT NULL," +
                FPG + " TEXT NOT NULL," +
                CREATED_BY + " TEXT," +
                CREATED_DATE + " DATE," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " DATE " + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.ID, "id AS " + SfHealthRiskAssessmentInfo.ID);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.IDCARD, "idcard AS " + SfHealthRiskAssessmentInfo.IDCARD);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.PERSON_INFO_ID, "person_info_id AS " + SfHealthRiskAssessmentInfo.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q1, "health_risk_q1 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q1);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q2, "health_risk_q2 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q2);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q3, "health_risk_q3 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q3);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q4, "health_risk_q4 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q4);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q5, "health_risk_q5 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q5);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.HEALTH_RISK_Q6, "health_risk_q6 AS " + SfHealthRiskAssessmentInfo.HEALTH_RISK_Q6);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.FCBG, "fcbg AS " + SfHealthRiskAssessmentInfo.FCBG);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.FPG, "fpg AS " + SfHealthRiskAssessmentInfo.FPG);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.CREATED_BY, "created_by AS " + SfHealthRiskAssessmentInfo.CREATED_BY);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.CREATED_DATE, "created_date AS " + SfHealthRiskAssessmentInfo.CREATED_DATE);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.UPDATED_BY, "updated_by AS " + SfHealthRiskAssessmentInfo.UPDATED_BY);
            PROJECTION_MAP.put(SfHealthRiskAssessmentInfo.UPDATED_DATE, "updated_date AS " + SfHealthRiskAssessmentInfo.UPDATED_DATE);
        }
    }

    public static final class SfDrugs implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_drugs";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_drugs");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_drugs";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_drugs";

        public static final String ID = "id";
        public static final String PERSON_INFO_ID = "person_info_id";
        public static final String QUESTION = "question";
        public static final String SUBQUESTION = "subquestion";
        public static final String ANSWER = "answer";
        public static final String OTHER_DRUGS = "other_drugs";
        public static final String CREATED_BY = "created_by";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_BY = "updated_by";
        public static final String UPDATED_DATE = "updated_date";
        public static final String IDCARD = "idcard";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PERSON_INFO_ID + " TEXT NOT NULL," +
                QUESTION + " TEXT NOT NULL," +
                SUBQUESTION + " TEXT," +
                OTHER_DRUGS + " TEXT," +
                ANSWER + " TEXT," +
                CREATED_BY + " TEXT NOT NULL," +
                CREATED_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                UPDATED_BY + " TEXT," +
                UPDATED_DATE + " TIMESTAMP," +
                IDCARD + " TEXT" + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfDrugs.ID, "id AS " + SfDrugs.ID);
            PROJECTION_MAP.put(SfDrugs.PERSON_INFO_ID, "person_info_id AS " + SfDrugs.PERSON_INFO_ID);
            PROJECTION_MAP.put(SfDrugs.QUESTION, "question AS " + SfDrugs.QUESTION);
            PROJECTION_MAP.put(SfDrugs.SUBQUESTION, "subquestion AS " + SfDrugs.SUBQUESTION);
            PROJECTION_MAP.put(SfDrugs.ANSWER, "answer AS " + SfDrugs.ANSWER);
            PROJECTION_MAP.put(SfDrugs.OTHER_DRUGS, "other_drugs AS " + SfDrugs.OTHER_DRUGS);
            PROJECTION_MAP.put(SfDrugs.CREATED_BY, "created_by AS " + SfDrugs.CREATED_BY);
            PROJECTION_MAP.put(SfDrugs.CREATED_DATE, "created_date AS " + SfDrugs.CREATED_DATE);
            PROJECTION_MAP.put(SfDrugs.UPDATED_BY, "updated_by AS " + SfDrugs.UPDATED_BY);
            PROJECTION_MAP.put(SfDrugs.UPDATED_DATE, "updated_date AS " + SfDrugs.UPDATED_DATE);
            PROJECTION_MAP.put(SfDrugs.IDCARD, "idcard AS " + SfDrugs.IDCARD);
        }
    }


    public static final class SfToken implements BaseColumns {
        public static final String TABLENAME = "ffc_sf_token";

        public static HashMap<String, String> PROJECTION_MAP;

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + ScreeningFormProvider.AUTHORITY + "/sf_token");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.sf_token";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.sf_token";

        public static final String ID = "id";
        public static final String TOKEN_AUTH = "token_auth";
        public static final String TOKEN_CLAIM = "token_claim";
        public static final String CREATED_DATE = "created_date";
        public static final String UPDATED_DATE = "updated_date";

        public static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLENAME;

        public static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TOKEN_AUTH + " TEXT NOT NULL," +
                TOKEN_CLAIM + " TEXT NOT NULL," +
                CREATED_DATE + " NUMERIC," +
                UPDATED_DATE + " NUMERIC" + ")";

        static {
            PROJECTION_MAP = new HashMap<String, String>();
            PROJECTION_MAP.put(SfToken.ID, "id AS " + SfToken.ID);
            PROJECTION_MAP.put(SfToken.TOKEN_AUTH, "token_auth AS " + SfToken.TOKEN_AUTH);
            PROJECTION_MAP.put(SfToken.TOKEN_CLAIM, "token_claim AS " + SfToken.TOKEN_CLAIM);
            PROJECTION_MAP.put(SfToken.CREATED_DATE, "created_date AS " + SfToken.CREATED_DATE);
            PROJECTION_MAP.put(SfToken.UPDATED_DATE, "updated_date AS " + SfToken.UPDATED_DATE);
        }
    }
}

