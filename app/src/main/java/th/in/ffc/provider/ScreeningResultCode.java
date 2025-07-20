package th.in.ffc.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

/**
 * Model คลาสสำหรับข้อมูลผลการคัดกรองและ Result Code
 * ใช้สำหรับเก็บข้อมูลผลการประเมินจากแบบคัดกรองต่างๆ
 */
public class ScreeningResultCode implements BaseColumns {

    // ชื่อตาราง
    public static final String TABLENAME = "ffc_sf_screening_result_code";

    // URI สำหรับเข้าถึงข้อมูล
    public static final Uri CONTENT_URI = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY + "/screening_result");
    public static final Uri CONTENT_LIST_URI = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY + "/screening_result/list");

    // MIME Types
    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.ffc.screeningresultcode";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.ffc.screeningresultcode";

    // คอลัมน์ในตาราง
    public static final String ID = BaseColumns._ID; // ID สำหรับตาราง (Auto Increment)
    public static final String PERSON_ID = "person_id"; // รหัสผู้รับบริการ
    public static final String VISITNO = "visitno"; // หมายเลขการเยี่ยมบ้าน/การให้บริการ
    public static final String SCREENING_TYPE = "screening_type"; // ประเภทการคัดกรอง (2Q, 9Q, 8Q, ST5, SMOKING)
    public static final String RESULT_CODE = "result_code"; // รหัสผลการประเมิน (เช่น 1B0210, 1B0211)
    public static final String RESULT_DESCRIPTION = "result_description"; // คำอธิบายผลการประเมิน
    public static final String TOTAL_SCORE = "total_score"; // คะแนนรวม
    public static final String RISK_LEVEL = "risk_level"; // ระดับความเสี่ยง (NORMAL, LOW, MODERATE, HIGH)
    public static final String IS_ABNORMAL = "is_abnormal"; // ผิดปกติหรือไม่ (0=ปกติ, 1=ผิดปกติ)
    public static final String RECOMMENDATION = "recommendation"; // คำแนะนำ
    public static final String SCREENING_DATE = "screening_date"; // วันที่ทำการคัดกรอง
    public static final String STATUS = "status"; // สถานะ (ACTIVE, INACTIVE)

    // คอลัมน์เพิ่มเติมสำหรับการจัดการข้อมูล
    public static final String CREATETIME = "createtime"; // เวลาที่สร้างข้อมูล
    public static final String UPDATETIME = "updatetime"; // เวลาที่อัพเดทข้อมูลล่าสุด
    public static final String USER_CREATE = "user_create"; // ผู้สร้างข้อมูล
    public static final String USER_UPDATE = "user_update"; // ผู้อัพเดทข้อมูลล่าสุด

    // ค่าคงที่สำหรับประเภทการคัดกรอง
    public static final String TYPE_STRESS_DEPRESSION_2Q = "2Q";
    public static final String TYPE_STRESS_DEPRESSION_9Q = "9Q";
    public static final String TYPE_SUICIDE_ASSESSMENT_8Q = "8Q";
    public static final String TYPE_STRESS_DEPRESSION_ST5 = "ST5";
    public static final String TYPE_ALCOHOL_SCREENING = "ALCOHOL";
    public static final String TYPE_SMOKING_ASSESSMENT = "SMOKING";

    public static final String TYPE_SMOKING_RISK = "SMOKING";
    public static final String TYPE_SMOKING_STATUS = "SMOKING_STATUS";
    public static final String TYPE_SMOKING_ADVICE = "SMOKING_ADVICE";

    // ค่าคงที่สำหรับระดับความเสี่ยง
    public static final String RISK_NONE = "NONE";
    public static final String RISK_NORMAL = "NORMAL";

    public static final String RISK_MEDIUM = "MEDIUM";
    public static final String RISK_LOW = "LOW";
    public static final String RISK_MODERATE = "MODERATE";
    public static final String RISK_HIGH = "HIGH";
    public static final String RISK_VERY_HIGH = "VERY_HIGH";

    // ค่าคงที่สำหรับสถานะ
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    public static final String ALCOHOL_RESULT_NO_DRINKING = "1B600";
    public static final String ALCOHOL_RESULT_BRIEF_ADVICE = "1B610";
    public static final String ALCOHOL_RESULT_BRIEF_COUNSELING = "1B611";
    public static final String ALCOHOL_RESULT_INTENSIVE_TREATMENT = "1B612";

    public static final String ALCOHOL_DESC_NO_DRINKING = "ไม่ดื่ม";
    public static final String ALCOHOL_DESC_BRIEF_ADVICE = "ไม่ต้องบำบัด - การให้คำแนะนำ (brief advice)";
    public static final String ALCOHOL_DESC_BRIEF_COUNSELING = "บำบัดอย่างย่อ - การให้คำปรึกษาแบบสั้น (brief counseling)";
    public static final String ALCOHOL_DESC_INTENSIVE_TREATMENT = "บำบัดเข้มข้น - การส่งต่อเพื่อรับการประเมินและการบำบัดโดยผู้เชี่ยวชาญ (refer)";

    public static final int ALCOHOL_SCORE_NO_DRINKING = 0;
    public static final int ALCOHOL_SCORE_LOW_RISK_MIN = 1;
    public static final int ALCOHOL_SCORE_LOW_RISK_MAX = 10;
    public static final int ALCOHOL_SCORE_MEDIUM_RISK_MIN = 11;
    public static final int ALCOHOL_SCORE_MEDIUM_RISK_MAX = 26;
    public static final int ALCOHOL_SCORE_HIGH_RISK_MIN = 27;

    public static final String ALCOHOL_RECOMMENDATION_NO_DRINKING =
            "ไม่ดื่มเครื่องดื่มแอลกอฮอล์ ให้คงสภาพปัจจุบัน";
    public static final String ALCOHOL_RECOMMENDATION_LOW_RISK =
            "ความเสี่ยงต่ำ - ให้คำแนะนำเกี่ยวกับการดื่มอย่างปลอดภัย";
    public static final String ALCOHOL_RECOMMENDATION_MEDIUM_RISK =
            "ความเสี่ยงปานกลาง - แนะนำให้ได้รับการปรึกษาแบบสั้นและลดการดื่ม";
    public static final String ALCOHOL_RECOMMENDATION_HIGH_RISK =
            "ความเสี่ยงสูง - แนะนำให้ได้รับการบำบัดเข้มข้นจากผู้เชี่ยวชาญ";
    public static String getAlcoholResultCode(int score) {
        if (score == ALCOHOL_SCORE_NO_DRINKING) {
            return ALCOHOL_RESULT_NO_DRINKING;
        } else if (score >= ALCOHOL_SCORE_LOW_RISK_MIN && score <= ALCOHOL_SCORE_LOW_RISK_MAX) {
            return ALCOHOL_RESULT_BRIEF_ADVICE;
        } else if (score >= ALCOHOL_SCORE_MEDIUM_RISK_MIN && score <= ALCOHOL_SCORE_MEDIUM_RISK_MAX) {
            return ALCOHOL_RESULT_BRIEF_COUNSELING;
        } else if (score >= ALCOHOL_SCORE_HIGH_RISK_MIN) {
            return ALCOHOL_RESULT_INTENSIVE_TREATMENT;
        }
        return ALCOHOL_RESULT_NO_DRINKING;
    }

    /**
     * ดึงคำอธิบายผลการประเมินตามคะแนนแอลกอฮอล์
     */
    public static String getAlcoholResultDescription(int score) {
        if (score == ALCOHOL_SCORE_NO_DRINKING) {
            return ALCOHOL_DESC_NO_DRINKING;
        } else if (score >= ALCOHOL_SCORE_LOW_RISK_MIN && score <= ALCOHOL_SCORE_LOW_RISK_MAX) {
            return ALCOHOL_DESC_BRIEF_ADVICE;
        } else if (score >= ALCOHOL_SCORE_MEDIUM_RISK_MIN && score <= ALCOHOL_SCORE_MEDIUM_RISK_MAX) {
            return ALCOHOL_DESC_BRIEF_COUNSELING;
        } else if (score >= ALCOHOL_SCORE_HIGH_RISK_MIN) {
            return ALCOHOL_DESC_INTENSIVE_TREATMENT;
        }
        return ALCOHOL_DESC_NO_DRINKING;
    }

    /**
     * ดึงระดับความเสี่ยงตามคะแนนแอลกอฮอล์
     */
    public static String getAlcoholRiskLevel(int score) {
        if (score == ALCOHOL_SCORE_NO_DRINKING) {
            return RISK_NONE;
        } else if (score >= ALCOHOL_SCORE_LOW_RISK_MIN && score <= ALCOHOL_SCORE_LOW_RISK_MAX) {
            return RISK_LOW;
        } else if (score >= ALCOHOL_SCORE_MEDIUM_RISK_MIN && score <= ALCOHOL_SCORE_MEDIUM_RISK_MAX) {
            return RISK_MEDIUM;
        } else if (score >= ALCOHOL_SCORE_HIGH_RISK_MIN) {
            return RISK_HIGH;
        }
        return RISK_NONE;
    }

    /**
     * ดึงคำแนะนำตามคะแนนแอลกอฮอล์
     */
    public static String getAlcoholRecommendation(int score) {
        if (score == ALCOHOL_SCORE_NO_DRINKING) {
            return ALCOHOL_RECOMMENDATION_NO_DRINKING;
        } else if (score >= ALCOHOL_SCORE_LOW_RISK_MIN && score <= ALCOHOL_SCORE_LOW_RISK_MAX) {
            return ALCOHOL_RECOMMENDATION_LOW_RISK;
        } else if (score >= ALCOHOL_SCORE_MEDIUM_RISK_MIN && score <= ALCOHOL_SCORE_MEDIUM_RISK_MAX) {
            return ALCOHOL_RECOMMENDATION_MEDIUM_RISK;
        } else if (score >= ALCOHOL_SCORE_HIGH_RISK_MIN) {
            return ALCOHOL_RECOMMENDATION_HIGH_RISK;
        }
        return ALCOHOL_RECOMMENDATION_NO_DRINKING;
    }

    /**
     * ตรวจสอบว่าเป็นผลผิดปกติหรือไม่
     */
    public static boolean isAlcoholAbnormal(int score) {
        return score >= ALCOHOL_SCORE_MEDIUM_RISK_MIN;
    }

    /**
     * ตรวจสอบว่าเป็นความเสี่ยงสูงหรือไม่
     */
    public static boolean isAlcoholHighRisk(int score) {
        return score >= ALCOHOL_SCORE_HIGH_RISK_MIN;
    }

    // คำสั่ง SQL สำหรับสร้างตาราง
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLENAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PERSON_ID + " INTEGER NOT NULL, " +  // รหัสผู้รับบริการ
                    VISITNO + " INTEGER NOT NULL, " +  // หมายเลขการเยี่ยมบ้าน/การให้บริการ
                    SCREENING_TYPE + " TEXT NOT NULL, " +  // ประเภทการคัดกรอง
                    RESULT_CODE + " TEXT NOT NULL, " +  // รหัสผลการประเมิน
                    RESULT_DESCRIPTION + " TEXT, " +  // คำอธิบายผลการประเมิน
                    TOTAL_SCORE + " INTEGER DEFAULT 0, " +  // คะแนนรวม
                    RISK_LEVEL + " TEXT DEFAULT '" + RISK_NORMAL + "', " +  // ระดับความเสี่ยง
                    IS_ABNORMAL + " INTEGER DEFAULT 0, " +  // ผิดปกติหรือไม่
                    RECOMMENDATION + " TEXT, " +  // คำแนะนำ
                    SCREENING_DATE + " DATE DEFAULT CURRENT_DATE, " +  // วันที่ทำการคัดกรอง
                    STATUS + " TEXT DEFAULT '" + STATUS_ACTIVE + "', " +  // สถานะ
                    CREATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    UPDATETIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    USER_CREATE + " TEXT, " +
                    USER_UPDATE + " TEXT, " +
                    // เพิ่ม constraint
                    "CONSTRAINT unique_person_visit_screening UNIQUE(" + PERSON_ID + ", " + VISITNO + ", " + SCREENING_TYPE + ", " + SCREENING_DATE + ")" +
                    ");";

    // คำสั่ง SQL สำหรับสร้าง Index
    public static final String CREATE_INDEX_PERSON_ID =
            "CREATE INDEX IF NOT EXISTS idx_screening_person_id ON " + TABLENAME + " (" + PERSON_ID + ");";

    public static final String CREATE_INDEX_VISITNO =
            "CREATE INDEX IF NOT EXISTS idx_screening_visitno ON " + TABLENAME + " (" + VISITNO + ");";

    public static final String CREATE_INDEX_SCREENING_TYPE =
            "CREATE INDEX IF NOT EXISTS idx_screening_type ON " + TABLENAME + " (" + SCREENING_TYPE + ");";

    public static final String CREATE_INDEX_SCREENING_DATE =
            "CREATE INDEX IF NOT EXISTS idx_screening_date ON " + TABLENAME + " (" + SCREENING_DATE + ");";

    public static final String CREATE_INDEX_PERSON_VISIT =
            "CREATE INDEX IF NOT EXISTS idx_person_visit ON " + TABLENAME + " (" + PERSON_ID + ", " + VISITNO + ");";

    // PROJECTION_MAP สำหรับการ Query
    protected static final HashMap<String, String> PROJECTION_MAP;

    static {
        PROJECTION_MAP = new HashMap<String, String>();
        PROJECTION_MAP.put(ID, ID);
        PROJECTION_MAP.put(PERSON_ID, PERSON_ID);
        PROJECTION_MAP.put(VISITNO, VISITNO);
        PROJECTION_MAP.put(SCREENING_TYPE, SCREENING_TYPE);
        PROJECTION_MAP.put(RESULT_CODE, RESULT_CODE);
        PROJECTION_MAP.put(RESULT_DESCRIPTION, RESULT_DESCRIPTION);
        PROJECTION_MAP.put(TOTAL_SCORE, TOTAL_SCORE);
        PROJECTION_MAP.put(RISK_LEVEL, RISK_LEVEL);
        PROJECTION_MAP.put(IS_ABNORMAL, IS_ABNORMAL);
        PROJECTION_MAP.put(RECOMMENDATION, RECOMMENDATION);
        PROJECTION_MAP.put(SCREENING_DATE, SCREENING_DATE);
        PROJECTION_MAP.put(STATUS, STATUS);
        PROJECTION_MAP.put(CREATETIME, CREATETIME);
        PROJECTION_MAP.put(UPDATETIME, UPDATETIME);
        PROJECTION_MAP.put(USER_CREATE, USER_CREATE);
        PROJECTION_MAP.put(USER_UPDATE, USER_UPDATE);
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น ContentValues
     * @param cursor Cursor ที่ได้จากการ query
     * @return ContentValues ที่มีข้อมูลจาก Cursor
     */
    public static ContentValues getContentValuesFromCursor(Cursor cursor) {
        ContentValues cv = new ContentValues();

        cv.put(PERSON_ID, cursor.getInt(cursor.getColumnIndex(PERSON_ID)));
        cv.put(VISITNO, cursor.getInt(cursor.getColumnIndex(VISITNO)));
        cv.put(SCREENING_TYPE, cursor.getString(cursor.getColumnIndex(SCREENING_TYPE)));
        cv.put(RESULT_CODE, cursor.getString(cursor.getColumnIndex(RESULT_CODE)));

        // ใส่ค่าเฉพาะถ้าคอลัมน์นั้นมีค่า
        if (!cursor.isNull(cursor.getColumnIndex(RESULT_DESCRIPTION))) {
            cv.put(RESULT_DESCRIPTION, cursor.getString(cursor.getColumnIndex(RESULT_DESCRIPTION)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(TOTAL_SCORE))) {
            cv.put(TOTAL_SCORE, cursor.getInt(cursor.getColumnIndex(TOTAL_SCORE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(RISK_LEVEL))) {
            cv.put(RISK_LEVEL, cursor.getString(cursor.getColumnIndex(RISK_LEVEL)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(IS_ABNORMAL))) {
            cv.put(IS_ABNORMAL, cursor.getInt(cursor.getColumnIndex(IS_ABNORMAL)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(RECOMMENDATION))) {
            cv.put(RECOMMENDATION, cursor.getString(cursor.getColumnIndex(RECOMMENDATION)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(SCREENING_DATE))) {
            cv.put(SCREENING_DATE, cursor.getString(cursor.getColumnIndex(SCREENING_DATE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(STATUS))) {
            cv.put(STATUS, cursor.getString(cursor.getColumnIndex(STATUS)));
        }

        return cv;
    }

    /**
     * คลาสสำหรับการสร้าง ContentValues สำหรับ ScreeningResultCode
     */
    public static class Builder {
        private ContentValues values = new ContentValues();

        public Builder personId(int personId) {
            values.put(PERSON_ID, personId);
            return this;
        }

        public Builder visitno(int visitno) {
            values.put(VISITNO, visitno);
            return this;
        }

        public Builder screeningType(String screeningType) {
            values.put(SCREENING_TYPE, screeningType);
            return this;
        }

        public Builder resultCode(String resultCode) {
            values.put(RESULT_CODE, resultCode);
            return this;
        }

        public Builder resultDescription(String resultDescription) {
            values.put(RESULT_DESCRIPTION, resultDescription);
            return this;
        }

        public Builder totalScore(int totalScore) {
            values.put(TOTAL_SCORE, totalScore);
            return this;
        }

        public Builder riskLevel(String riskLevel) {
            values.put(RISK_LEVEL, riskLevel);
            return this;
        }

        public Builder isAbnormal(boolean isAbnormal) {
            values.put(IS_ABNORMAL, isAbnormal ? 1 : 0);
            return this;
        }

        public Builder recommendation(String recommendation) {
            values.put(RECOMMENDATION, recommendation);
            return this;
        }

        public Builder screeningDate(String screeningDate) {
            values.put(SCREENING_DATE, screeningDate);
            return this;
        }

        public Builder status(String status) {
            values.put(STATUS, status);
            return this;
        }

        public Builder userCreate(String userCreate) {
            values.put(USER_CREATE, userCreate);
            return this;
        }

        public Builder userUpdate(String userUpdate) {
            values.put(USER_UPDATE, userUpdate);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }

    /**
     * Helper methods สำหรับสร้าง ContentValues สำหรับแต่ละประเภทการคัดกรอง
     */
    public static class ScreeningResult {

        /**
         * สร้าง ContentValues สำหรับผลการคัดกรอง 2Q
         */
        public static ContentValues create2QResult(int personId, int visitno, boolean hasPositiveAnswer, String userCreate) {
            String resultCode = hasPositiveAnswer ? "1B0211" : "1B0210";
            String resultDescription = hasPositiveAnswer ?
                    "ผิดปกติ และส่งต่อเจ้าหน้าที่" : "ปกติ";
            String riskLevel = hasPositiveAnswer ? RISK_HIGH : RISK_NORMAL;

            return new Builder()
                    .personId(personId)
                    .visitno(visitno)
                    .screeningType(TYPE_STRESS_DEPRESSION_2Q)
                    .resultCode(resultCode)
                    .resultDescription(resultDescription)
                    .totalScore(hasPositiveAnswer ? 1 : 0)
                    .riskLevel(riskLevel)
                    .isAbnormal(hasPositiveAnswer)
                    .recommendation(hasPositiveAnswer ?
                            "แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติม และพิจารณาปรึกษาแพทย์" :
                            "ไม่พบความเสี่ยงต่อภาวะซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป")
                    .userCreate(userCreate)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับผลการคัดกรอง 9Q
         */
        public static ContentValues create9QResult(int personId, int visitno, int totalScore, String userCreate) {
            String resultCode, resultDescription, riskLevel;
            boolean isAbnormal = totalScore >= 7;

            if (totalScore < 7) {
                resultCode = "1B0260|1B0282";
                resultDescription = "ไม่มีอาการของโรคซึมเศร้า";
                riskLevel = RISK_NORMAL;
            } else if (totalScore <= 12) {
                resultCode = "1B0261|1B0283";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับน้อย";
                riskLevel = RISK_LOW;
            } else if (totalScore <= 18) {
                resultCode = "1B0262|1B0284";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับปานกลาง";
                riskLevel = RISK_MODERATE;
            } else {
                resultCode = "1B0263|1B0285";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับรุนแรง";
                riskLevel = RISK_HIGH;
            }

            return new Builder()
                    .personId(personId)
                    .visitno(visitno)
                    .screeningType(TYPE_STRESS_DEPRESSION_9Q)
                    .resultCode(resultCode)
                    .resultDescription(resultDescription)
                    .totalScore(totalScore)
                    .riskLevel(riskLevel)
                    .isAbnormal(isAbnormal)
                    .userCreate(userCreate)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับผลการคัดกรอง 8Q
         */
        public static ContentValues create8QResult(int personId, int visitno, int totalScore, String userCreate) {
            String resultCode, resultDescription, riskLevel;
            boolean isAbnormal = totalScore > 0;

            if (totalScore == 0) {
                resultCode = "1B0270";
                resultDescription = "ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
                riskLevel = RISK_NORMAL;
            } else if (totalScore <= 8) {
                resultCode = "1B0271";
                resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
                riskLevel = RISK_LOW;
            } else if (totalScore <= 16) {
                resultCode = "1B0272";
                resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
                riskLevel = RISK_MODERATE;
            } else {
                resultCode = "1B0273";
                resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
                riskLevel = RISK_VERY_HIGH;
            }

            return new Builder()
                    .personId(personId)
                    .visitno(visitno)
                    .screeningType(TYPE_SUICIDE_ASSESSMENT_8Q)
                    .resultCode(resultCode)
                    .resultDescription(resultDescription)
                    .totalScore(totalScore)
                    .riskLevel(riskLevel)
                    .isAbnormal(isAbnormal)
                    .userCreate(userCreate)
                    .build();
        }

        /**
         * สร้าง ContentValues สำหรับผลการคัดกรอง ST5
         */
        public static ContentValues createST5Result(int personId, int visitno, int totalScore, String userCreate) {
            String resultCode, resultDescription, riskLevel;
            boolean isAbnormal = totalScore >= 5;

            if (totalScore <= 4) {
                resultCode = "1B132";
                resultDescription = "เครียดน้อย";
                riskLevel = RISK_NORMAL;
            } else if (totalScore <= 7) {
                resultCode = "1B133";
                resultDescription = "เครียดปานกลาง";
                riskLevel = RISK_LOW;
            } else if (totalScore <= 9) {
                resultCode = "1B134";
                resultDescription = "เครียดมาก";
                riskLevel = RISK_MODERATE;
            } else {
                resultCode = "1B135";
                resultDescription = "เครียดมากที่สุด";
                riskLevel = RISK_HIGH;
            }

            return new Builder()
                    .personId(personId)
                    .visitno(visitno)
                    .screeningType(TYPE_STRESS_DEPRESSION_ST5)
                    .resultCode(resultCode)
                    .resultDescription(resultDescription)
                    .totalScore(totalScore)
                    .riskLevel(riskLevel)
                    .isAbnormal(isAbnormal)
                    .userCreate(userCreate)
                    .build();
        }
    }

    public static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLENAME;
}