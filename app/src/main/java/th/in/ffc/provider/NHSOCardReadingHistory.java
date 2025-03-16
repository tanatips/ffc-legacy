package th.in.ffc.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/**
 * ประวัติการอ่านบัตรประชาชน
 */
public class NHSOCardReadingHistory implements BaseColumns {
    public static final String TABLENAME = "ffc_nhso_card_reading_history";

    public static HashMap<String, String> PROJECTION_MAP;

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOPatientProvider.AUTHORITY + "/nhso_card_reading_history");
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhso_card_reading_history";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhso_card_reading_history";

    public static final String ID = "id";
    public static final String READ_TIMESTAMP = "read_timestamp";
    public static final String USERNAME = "username";
    public static final String CITIZEN_ID = "citizen_id";
    public static final String CITIZEN_NAME = "citizen_name";
    public static final String DEVICE_MODEL = "device_model";
    public static final String DEVICE_BRAND = "device_brand";
    public static final String CARD_READER_MODEL = "card_reader_model";
    public static final String APP_VERSION = "app_version";
    public static final String READ_STATUS = "read_status";
    public static final String NOTES = "notes";
    public static final String CREATED_AT = "created_at";

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(NHSOCardReadingHistory.ID, "id AS " + NHSOCardReadingHistory.ID);
        PROJECTION_MAP.put(NHSOCardReadingHistory.READ_TIMESTAMP, "read_timestamp AS " + NHSOCardReadingHistory.READ_TIMESTAMP);
        PROJECTION_MAP.put(NHSOCardReadingHistory.USERNAME, "username AS " + NHSOCardReadingHistory.USERNAME);
        PROJECTION_MAP.put(NHSOCardReadingHistory.CITIZEN_ID, "citizen_id AS " + NHSOCardReadingHistory.CITIZEN_ID);
        PROJECTION_MAP.put(NHSOCardReadingHistory.CITIZEN_NAME, "citizen_name AS " + NHSOCardReadingHistory.CITIZEN_NAME);
        PROJECTION_MAP.put(NHSOCardReadingHistory.DEVICE_MODEL, "device_model AS " + NHSOCardReadingHistory.DEVICE_MODEL);
        PROJECTION_MAP.put(NHSOCardReadingHistory.DEVICE_BRAND, "device_brand AS " + NHSOCardReadingHistory.DEVICE_BRAND);
        PROJECTION_MAP.put(NHSOCardReadingHistory.CARD_READER_MODEL, "card_reader_model AS " + NHSOCardReadingHistory.CARD_READER_MODEL);
        PROJECTION_MAP.put(NHSOCardReadingHistory.APP_VERSION, "app_version AS " + NHSOCardReadingHistory.APP_VERSION);
        PROJECTION_MAP.put(NHSOCardReadingHistory.READ_STATUS, "read_status AS " + NHSOCardReadingHistory.READ_STATUS);
        PROJECTION_MAP.put(NHSOCardReadingHistory.NOTES, "notes AS " + NHSOCardReadingHistory.NOTES);
        PROJECTION_MAP.put(NHSOCardReadingHistory.CREATED_AT, "created_at AS " + NHSOCardReadingHistory.CREATED_AT);
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            READ_TIMESTAMP + " DATETIME NOT NULL," +
            USERNAME + " TEXT NOT NULL," +
            CITIZEN_ID + " TEXT NOT NULL," +
            CITIZEN_NAME + " TEXT NOT NULL," +
            DEVICE_MODEL + " TEXT," +
            DEVICE_BRAND + " TEXT," +
            CARD_READER_MODEL + " TEXT," +
            APP_VERSION + " TEXT," +
            READ_STATUS + " TEXT," +
            NOTES + " TEXT," +
            CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}