package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.SfApiUrl;
import th.in.ffc.provider.DbOpenHelper;

/**
 * Data Access Object (DAO) สำหรับจัดการข้อมูล API URL ในฐานข้อมูล SQLite
 */
public class SfApiUrlDao {

    private static final String TABLE_NAME = "ffc_sf_api_url";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_API_CODE = "api_code";
    private static final String COLUMN_API_NAME = "api_name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_METHOD = "method";
    private static final String COLUMN_TEST_URL = "test_url";
    private static final String COLUMN_PROD_URL = "prod_url";
    private static final String COLUMN_PARAMS = "params";
    private static final String COLUMN_REQUEST_FORMAT = "request_format";
    private static final String COLUMN_IS_ACTIVE = "is_active";
    private static final String COLUMN_ENV_TYPE = "env_type";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    private Context context;
    private SQLiteDatabase database;

    public SfApiUrlDao(Context context) {
        this.context = context;
        DbOpenHelper dbHelper = new DbOpenHelper(context);
        this.database = dbHelper.getWritableDatabase();

        // สร้างตารางถ้ายังไม่มี
        createTableIfNotExists();

        // เพิ่มข้อมูลเริ่มต้นถ้าตารางว่าง
        initializeDataIfEmpty();
    }

    /**
     * สร้างตาราง ffc_sf_api_url ถ้ายังไม่มีในฐานข้อมูล
     */
    private void createTableIfNotExists() {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_API_CODE + " TEXT NOT NULL, "
                + COLUMN_API_NAME + " TEXT NOT NULL, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_METHOD + " TEXT NOT NULL, "
                + COLUMN_TEST_URL + " TEXT NOT NULL, "
                + COLUMN_PROD_URL + " TEXT NOT NULL, "
                + COLUMN_PARAMS + " TEXT, "
                + COLUMN_REQUEST_FORMAT + " TEXT, "
                + COLUMN_IS_ACTIVE + " INTEGER DEFAULT 1, "
                + COLUMN_ENV_TYPE + " INTEGER DEFAULT 0, "
                + COLUMN_CREATED_AT + " INTEGER, "
                + COLUMN_UPDATED_AT + " INTEGER"
                + ")";
        database.execSQL(createTable);

        // สร้าง index
        String createIndex = "CREATE INDEX IF NOT EXISTS idx_" + TABLE_NAME + "_code ON "
                + TABLE_NAME + "(" + COLUMN_API_CODE + ")";
        database.execSQL(createIndex);
    }

    /**
     * เพิ่มข้อมูล API URL เริ่มต้นถ้าตารางว่าง
     */
    private void initializeDataIfEmpty() {
        if (getCount() == 0) {
            long currentTime = System.currentTimeMillis();

            // AuthenCode API
            SfApiUrl authenCodeApi = new SfApiUrl();
            authenCodeApi.setApiCode("AUTHEN_CODE");
            authenCodeApi.setApiName("AuthenCode API");
            authenCodeApi.setDescription("API สำหรับขอ authen code");
            authenCodeApi.setMethod("POST");
            authenCodeApi.setTestUrl("https://test.nhso.go.th/authencodeapi/api/AuthenCode");
            authenCodeApi.setProdUrl("https://api.nhso.go.th/authencodeapi/api/AuthenCode");
            authenCodeApi.setParams("");
            authenCodeApi.setRequestFormat("{ \"hcode\": \"11471\", \"pid\": \"1101401424853\", \"serviceCode\": \"PG0060001\", \"sourceId\": \"BKKCC\", \"transId\": \"PHS25000010\" }");
            authenCodeApi.setIsActive(1);
            authenCodeApi.setEnvType(SfApiUrl.ENV_TEST);
            authenCodeApi.setCreatedAt(currentTime);
            authenCodeApi.setUpdatedAt(currentTime);
            insert(authenCodeApi);

            // Create-fs-data API
            SfApiUrl createFsDataApi = new SfApiUrl();
            createFsDataApi.setApiCode("CREATE_FS_DATA");
            createFsDataApi.setApiName("Create FS Data API");
            createFsDataApi.setDescription("API สำหรับสร้างข้อมูล FS");
            createFsDataApi.setMethod("POST");
            createFsDataApi.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/create-fs-data");
            createFsDataApi.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/create-fs-data");
            createFsDataApi.setParams("");
            createFsDataApi.setRequestFormat("{\"fsDatas\": [{...}]}");
            createFsDataApi.setIsActive(1);
            createFsDataApi.setEnvType(SfApiUrl.ENV_TEST);
            createFsDataApi.setCreatedAt(currentTime);
            createFsDataApi.setUpdatedAt(currentTime);
            insert(createFsDataApi);

            // RealPerson API
            SfApiUrl realPersonApi = new SfApiUrl();
            realPersonApi.setApiCode("REAL_PERSON");
            realPersonApi.setApiName("Real Person API");
            realPersonApi.setDescription("API สำหรับตรวจสอบข้อมูลบุคคล");
            realPersonApi.setMethod("GET");
            realPersonApi.setTestUrl("https://test.nhso.go.th/nhsoendpoint/api/RealPerson");
            realPersonApi.setProdUrl("https://api.nhso.go.th/nhsoendpoint/api/RealPerson");
            realPersonApi.setParams("SOURCE_ID=BKKCC&PID={PID}");
            realPersonApi.setRequestFormat("");
            realPersonApi.setIsActive(1);
            realPersonApi.setEnvType(SfApiUrl.ENV_TEST);
            realPersonApi.setCreatedAt(currentTime);
            realPersonApi.setUpdatedAt(currentTime);
            insert(realPersonApi);

            // Status-tracks API
            SfApiUrl statusTracksApi = new SfApiUrl();
            statusTracksApi.setApiCode("STATUS_TRACKS");
            statusTracksApi.setApiName("Status Tracks API");
            statusTracksApi.setDescription("API สำหรับติดตามสถานะ");
            statusTracksApi.setMethod("POST");
            statusTracksApi.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/status-tracks");
            statusTracksApi.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/status-tracks");
            statusTracksApi.setParams("");
            statusTracksApi.setRequestFormat("{\"statusTracks\": [{...}]}");
            statusTracksApi.setIsActive(1);
            statusTracksApi.setEnvType(SfApiUrl.ENV_TEST);
            statusTracksApi.setCreatedAt(currentTime);
            statusTracksApi.setUpdatedAt(currentTime);
            insert(statusTracksApi);

            // Status-tracks V2 API (New)
            SfApiUrl statusTracksV2Api = new SfApiUrl();
            statusTracksV2Api.setApiCode("STATUS_TRACKS_V2");
            statusTracksV2Api.setApiName("Status Tracks V2 API");
            statusTracksV2Api.setDescription("API สำหรับติดตามสถานะ เวอร์ชัน 2");
            statusTracksV2Api.setMethod("POST");
            statusTracksV2Api.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setParams("");
            statusTracksV2Api.setRequestFormat("{\"trackDatas\": [{\"uid\": \"2eda2961-78e2-4c58-a334-bb141b9fb5f6\"}]}");
            statusTracksV2Api.setIsActive(1);
            statusTracksV2Api.setEnvType(SfApiUrl.ENV_TEST);
            statusTracksV2Api.setCreatedAt(currentTime);
            statusTracksV2Api.setUpdatedAt(currentTime);
            insert(statusTracksV2Api);
        }
    }
    /**
     * เพิ่ม API URL ใหม่หากยังไม่มี (สำหรับ API ที่เพิ่มมาทีหลัง)
     */
    public void addMissingApis() {
        long currentTime = System.currentTimeMillis();

        // ตรวจสอบและเพิ่ม STATUS_TRACKS_V2 หากยังไม่มี
        if (findByApiCode("STATUS_TRACKS_V2") == null) {
            SfApiUrl statusTracksV2Api = new SfApiUrl();
            statusTracksV2Api.setApiCode("STATUS_TRACKS_V2");
            statusTracksV2Api.setApiName("Status Tracks V2 API");
            statusTracksV2Api.setDescription("API สำหรับติดตามสถานะ เวอร์ชัน 2");
            statusTracksV2Api.setMethod("POST");
            statusTracksV2Api.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setParams("");
            statusTracksV2Api.setRequestFormat("{\"trackDatas\": [{\"uid\": \"2eda2961-78e2-4c58-a334-bb141b9fb5f6\"}]}");
            statusTracksV2Api.setIsActive(1);
            statusTracksV2Api.setEnvType(SfApiUrl.ENV_TEST);
            statusTracksV2Api.setCreatedAt(currentTime);
            statusTracksV2Api.setUpdatedAt(currentTime);
            insert(statusTracksV2Api);
        }

        // อัปเดต request format ของ STATUS_TRACKS หากยังไม่ถูกต้อง
        SfApiUrl statusTracksApi = findByApiCode("STATUS_TRACKS");
        if (statusTracksApi != null && statusTracksApi.getRequestFormat().contains("statusTracks")) {
            statusTracksApi.setRequestFormat("{\"fsTrackDatas\": [{\"id\": \"string\", \"seq\": \"string\"}]}");
            statusTracksApi.setUpdatedAt(currentTime);
            update(statusTracksApi);
        }
    }
    /**
     * อัปเดต API URLs ที่มีอยู่แล้ว (สำหรับการแก้ไข URL หรือข้อมูลที่เปลี่ยนแปลง)
     */
    public void updateExistingApis() {
        long currentTime = System.currentTimeMillis();

        // อัปเดต CREATE_FS_DATA หากมีการเปลี่ยนแปลง URL
        SfApiUrl createFsDataApi = findByApiCode("CREATE_FS_DATA");
        if (createFsDataApi != null) {
            createFsDataApi.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/create-fs-data");
            createFsDataApi.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/create-fs-data");
            createFsDataApi.setUpdatedAt(currentTime);
            update(createFsDataApi);
        }

        // อัปเดต STATUS_TRACKS_V2 หากมีการเปลี่ยนแปลง
        SfApiUrl statusTracksV2Api = findByApiCode("STATUS_TRACKS_V2");
        if (statusTracksV2Api != null) {
            statusTracksV2Api.setTestUrl("https://testgdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setProdUrl("https://gdcc.nhso.go.th/stddataset/api/v2/status-tracks");
            statusTracksV2Api.setUpdatedAt(currentTime);
            update(statusTracksV2Api);
        }
    }
    /**
     * ตรวจสอบและเพิ่ม/อัปเดต APIs ทั้งหมด
     * เรียกใช้ใน Application.onCreate() หรือเมื่อแอปเริ่มทำงาน
     */
    public void syncApis() {
        addMissingApis();
        updateExistingApis();
    }

    /**
     * นับจำนวนรายการทั้งหมดในตาราง
     *
     * @return จำนวนรายการในตาราง
     */
    public int getCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(countQuery, null);
        int count = 0;

        try {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            cursor.close();
        }

        return count;
    }

    /**
     * เพิ่มข้อมูล API URL ใหม่ลงในฐานข้อมูล
     *
     * @param apiUrl ข้อมูล API URL ที่ต้องการบันทึก
     * @return ID ของข้อมูลที่ถูกบันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insert(SfApiUrl apiUrl) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_API_CODE, apiUrl.getApiCode());
        values.put(COLUMN_API_NAME, apiUrl.getApiName());
        values.put(COLUMN_DESCRIPTION, apiUrl.getDescription());
        values.put(COLUMN_METHOD, apiUrl.getMethod());
        values.put(COLUMN_TEST_URL, apiUrl.getTestUrl());
        values.put(COLUMN_PROD_URL, apiUrl.getProdUrl());
        values.put(COLUMN_PARAMS, apiUrl.getParams());
        values.put(COLUMN_REQUEST_FORMAT, apiUrl.getRequestFormat());
        values.put(COLUMN_IS_ACTIVE, apiUrl.getIsActive());
        values.put(COLUMN_ENV_TYPE, apiUrl.getEnvType());
        values.put(COLUMN_CREATED_AT, apiUrl.getCreatedAt());
        values.put(COLUMN_UPDATED_AT, apiUrl.getUpdatedAt());

        return database.insert(TABLE_NAME, null, values);
    }

    /**
     * อัปเดตข้อมูล API URL ที่มีอยู่แล้วในฐานข้อมูล
     *
     * @param apiUrl ข้อมูล API URL ที่ต้องการอัปเดต
     * @return จำนวนแถวที่ถูกอัปเดต
     */
    public int update(SfApiUrl apiUrl) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_API_CODE, apiUrl.getApiCode());
        values.put(COLUMN_API_NAME, apiUrl.getApiName());
        values.put(COLUMN_DESCRIPTION, apiUrl.getDescription());
        values.put(COLUMN_METHOD, apiUrl.getMethod());
        values.put(COLUMN_TEST_URL, apiUrl.getTestUrl());
        values.put(COLUMN_PROD_URL, apiUrl.getProdUrl());
        values.put(COLUMN_PARAMS, apiUrl.getParams());
        values.put(COLUMN_REQUEST_FORMAT, apiUrl.getRequestFormat());
        values.put(COLUMN_IS_ACTIVE, apiUrl.getIsActive());
        values.put(COLUMN_ENV_TYPE, apiUrl.getEnvType());
        values.put(COLUMN_UPDATED_AT, apiUrl.getUpdatedAt());

        return database.update(TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[] { String.valueOf(apiUrl.getId()) });
    }

    /**
     * ลบข้อมูล API URL จากฐานข้อมูล
     *
     * @param id ID ของ API URL ที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int delete(long id) {
        return database.delete(TABLE_NAME, COLUMN_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * อัปเดตสภาพแวดล้อม (TEST/PRODUCTION) สำหรับ API ที่ระบุ
     *
     * @param apiCode รหัส API
     * @param envType สภาพแวดล้อม (ENV_TEST หรือ ENV_PRODUCTION)
     * @return จำนวนแถวที่ถูกอัปเดต
     */
    public int updateEnvironment(String apiCode, int envType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENV_TYPE, envType);
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        return database.update(TABLE_NAME, values, COLUMN_API_CODE + " = ?",
                new String[] { apiCode });
    }

    /**
     * อัปเดตสภาพแวดล้อมของ API ทั้งหมด
     *
     * @param envType สภาพแวดล้อม (ENV_TEST หรือ ENV_PRODUCTION)
     * @return จำนวนแถวที่ถูกอัปเดต
     */
    public int updateAllEnvironment(int envType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENV_TYPE, envType);
        values.put(COLUMN_UPDATED_AT, System.currentTimeMillis());

        return database.update(TABLE_NAME, values, null, null);
    }

    /**
     * ดึงข้อมูล API URL ทั้งหมดจากฐานข้อมูล
     *
     * @return รายการข้อมูล API URL ทั้งหมด
     */
    public List<SfApiUrl> getAllApiUrls() {
        List<SfApiUrl> apiUrlList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_API_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    SfApiUrl apiUrl = new SfApiUrl();
                    apiUrl.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                    apiUrl.setApiCode(cursor.getString(cursor.getColumnIndex(COLUMN_API_CODE)));
                    apiUrl.setApiName(cursor.getString(cursor.getColumnIndex(COLUMN_API_NAME)));
                    apiUrl.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    apiUrl.setMethod(cursor.getString(cursor.getColumnIndex(COLUMN_METHOD)));
                    apiUrl.setTestUrl(cursor.getString(cursor.getColumnIndex(COLUMN_TEST_URL)));
                    apiUrl.setProdUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PROD_URL)));
                    apiUrl.setParams(cursor.getString(cursor.getColumnIndex(COLUMN_PARAMS)));
                    apiUrl.setRequestFormat(cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_FORMAT)));
                    apiUrl.setIsActive(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ACTIVE)));
                    apiUrl.setEnvType(cursor.getInt(cursor.getColumnIndex(COLUMN_ENV_TYPE)));
                    apiUrl.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                    apiUrl.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)));

                    apiUrlList.add(apiUrl);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return apiUrlList;
    }

    /**
     * ดึงข้อมูล API URL จากรหัส API
     *
     * @param apiCode รหัส API
     * @return ข้อมูล API URL ที่พบ หรือ null ถ้าไม่พบ
     */
    public SfApiUrl findByApiCode(String apiCode) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_API_CODE + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[] { apiCode });

        SfApiUrl apiUrl = null;

        try {
            if (cursor.moveToFirst()) {
                apiUrl = new SfApiUrl();
                apiUrl.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                apiUrl.setApiCode(cursor.getString(cursor.getColumnIndex(COLUMN_API_CODE)));
                apiUrl.setApiName(cursor.getString(cursor.getColumnIndex(COLUMN_API_NAME)));
                apiUrl.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                apiUrl.setMethod(cursor.getString(cursor.getColumnIndex(COLUMN_METHOD)));
                apiUrl.setTestUrl(cursor.getString(cursor.getColumnIndex(COLUMN_TEST_URL)));
                apiUrl.setProdUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PROD_URL)));
                apiUrl.setParams(cursor.getString(cursor.getColumnIndex(COLUMN_PARAMS)));
                apiUrl.setRequestFormat(cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_FORMAT)));
                apiUrl.setIsActive(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ACTIVE)));
                apiUrl.setEnvType(cursor.getInt(cursor.getColumnIndex(COLUMN_ENV_TYPE)));
                apiUrl.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                apiUrl.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
            }
        } finally {
            cursor.close();
        }

        return apiUrl;
    }

    /**
     * ดึงข้อมูล API URL จาก ID
     *
     * @param id ID ของ API URL
     * @return ข้อมูล API URL ที่พบ หรือ null ถ้าไม่พบ
     */
    public SfApiUrl findById(long id) {
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[] { String.valueOf(id) });

        SfApiUrl apiUrl = null;

        try {
            if (cursor.moveToFirst()) {
                apiUrl = new SfApiUrl();
                apiUrl.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                apiUrl.setApiCode(cursor.getString(cursor.getColumnIndex(COLUMN_API_CODE)));
                apiUrl.setApiName(cursor.getString(cursor.getColumnIndex(COLUMN_API_NAME)));
                apiUrl.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                apiUrl.setMethod(cursor.getString(cursor.getColumnIndex(COLUMN_METHOD)));
                apiUrl.setTestUrl(cursor.getString(cursor.getColumnIndex(COLUMN_TEST_URL)));
                apiUrl.setProdUrl(cursor.getString(cursor.getColumnIndex(COLUMN_PROD_URL)));
                apiUrl.setParams(cursor.getString(cursor.getColumnIndex(COLUMN_PARAMS)));
                apiUrl.setRequestFormat(cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST_FORMAT)));
                apiUrl.setIsActive(cursor.getInt(cursor.getColumnIndex(COLUMN_IS_ACTIVE)));
                apiUrl.setEnvType(cursor.getInt(cursor.getColumnIndex(COLUMN_ENV_TYPE)));
                apiUrl.setCreatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_AT)));
                apiUrl.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(COLUMN_UPDATED_AT)));
            }
        } finally {
            cursor.close();
        }

        return apiUrl;
    }

    /**
     * ปิดการเชื่อมต่อกับฐานข้อมูล
     */
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}