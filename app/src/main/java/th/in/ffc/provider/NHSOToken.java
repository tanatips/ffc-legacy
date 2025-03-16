package th.in.ffc.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/**
 * Token สำหรับการเชื่อมต่อกับ NHSO API
 */
public class NHSOToken implements BaseColumns {
    public static final String TABLENAME = "ffc_nhso_token";

    public static HashMap<String, String> PROJECTION_MAP;

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOPatientProvider.AUTHORITY + "/nhso_token");
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhso_token";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhso_token";

    public static final String ID = "id";
    public static final String TOKEN_TYPE = "token_type";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String CREATED_DATE = "created_date";
    public static final String UPDATED_DATE = "updated_date";

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(NHSOToken.ID, "id AS " + NHSOToken.ID);
        PROJECTION_MAP.put(NHSOToken.TOKEN_TYPE, "token_type AS " + NHSOToken.TOKEN_TYPE);
        PROJECTION_MAP.put(NHSOToken.ACCESS_TOKEN, "access_token AS " + NHSOToken.ACCESS_TOKEN);
        PROJECTION_MAP.put(NHSOToken.REFRESH_TOKEN, "refresh_token AS " + NHSOToken.REFRESH_TOKEN);
        PROJECTION_MAP.put(NHSOToken.EXPIRES_IN, "expires_in AS " + NHSOToken.EXPIRES_IN);
        PROJECTION_MAP.put(NHSOToken.CREATED_DATE, "created_date AS " + NHSOToken.CREATED_DATE);
        PROJECTION_MAP.put(NHSOToken.UPDATED_DATE, "updated_date AS " + NHSOToken.UPDATED_DATE);
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TOKEN_TYPE + " TEXT NOT NULL," +
            ACCESS_TOKEN + " TEXT NOT NULL," +
            REFRESH_TOKEN + " TEXT," +
            EXPIRES_IN + " INTEGER," +
            CREATED_DATE + " TEXT," +
            UPDATED_DATE + " TEXT" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}