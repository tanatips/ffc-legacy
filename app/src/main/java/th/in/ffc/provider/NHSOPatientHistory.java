package th.in.ffc.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.HashMap;

/**
 * ประวัติการส่งข้อมูลผู้ป่วย NHSO
 */
public class NHSOPatientHistory implements BaseColumns {
    public static final String TABLENAME = "ffc_nhso_patient_history";

    public static HashMap<String, String> PROJECTION_MAP;

    public static final Uri CONTENT_URI = Uri.parse("content://"
            + NHSOPatientProvider.AUTHORITY + "/nhso_patient_history");
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhso_patient_history";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhso_patient_history";

    public static final String ID = "id";
    public static final String PATIENT_ID = "patient_id";
    public static final String CID = "cid";
    public static final String STATUS = "status";  // SUCCESS, FAILED
    public static final String MESSAGE = "message";
    public static final String RESPONSE_CODE = "response_code";
    public static final String RESPONSE_BODY = "response_body";
    public static final String CREATED_BY = "created_by";
    public static final String CREATED_DATE = "created_date";

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(NHSOPatientHistory.ID, "id AS " + NHSOPatientHistory.ID);
        PROJECTION_MAP.put(NHSOPatientHistory.PATIENT_ID, "patient_id AS " + NHSOPatientHistory.PATIENT_ID);
        PROJECTION_MAP.put(NHSOPatientHistory.CID, "cid AS " + NHSOPatientHistory.CID);
        PROJECTION_MAP.put(NHSOPatientHistory.STATUS, "status AS " + NHSOPatientHistory.STATUS);
        PROJECTION_MAP.put(NHSOPatientHistory.MESSAGE, "message AS " + NHSOPatientHistory.MESSAGE);
        PROJECTION_MAP.put(NHSOPatientHistory.RESPONSE_CODE, "response_code AS " + NHSOPatientHistory.RESPONSE_CODE);
        PROJECTION_MAP.put(NHSOPatientHistory.RESPONSE_BODY, "response_body AS " + NHSOPatientHistory.RESPONSE_BODY);
        PROJECTION_MAP.put(NHSOPatientHistory.CREATED_BY, "created_by AS " + NHSOPatientHistory.CREATED_BY);
        PROJECTION_MAP.put(NHSOPatientHistory.CREATED_DATE, "created_date AS " + NHSOPatientHistory.CREATED_DATE);
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PATIENT_ID + " INTEGER NOT NULL," +
            CID + " TEXT NOT NULL," +
            STATUS + " TEXT NOT NULL," +
            MESSAGE + " TEXT," +
            RESPONSE_CODE + " INTEGER," +
            RESPONSE_BODY + " TEXT," +
            CREATED_BY + " TEXT," +
            CREATED_DATE + " TEXT" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}