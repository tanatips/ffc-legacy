package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.provider.ScreeningResultCode;
import th.in.ffc.provider.ScreeningResultCodeProvider;

/**
 * Data Access Object สำหรับจัดการข้อมูลผลการคัดกรองและ Result Code
 * ให้บริการ CRUD operations และ business logic สำหรับการจัดการข้อมูลผลการประเมิน
 */
public class ScreeningResultCodeDao {
    private static final String TAG = "ScreeningResultCodeDao";

    private Context context;
    private ContentResolver contentResolver;

    public ScreeningResultCodeDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    /**
     * บันทึกผลการคัดกรอง
     */
    public Uri saveScreeningResult(ScreeningResultData screeningResult) {
        try {
            ContentValues values = convertToContentValues(screeningResult);

            // ลบข้อมูลเก่าที่มี visitno และ screening_type เดียวกันก่อน
            deleteExistingResult(screeningResult.visitno, screeningResult.screeningType);

            // Insert ข้อมูลใหม่
            Uri result = contentResolver.insert(ScreeningResultCode.CONTENT_URI, values);

            if (result != null) {
                Log.d(TAG, "บันทึกผลการคัดกรองสำเร็จ: " + result.toString());
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผลการคัดกรอง", e);
            return null;
        }
    }
    private void deleteExistingResult(int visitno, String screeningType) {
        try {
            String selection = ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=?";
            String[] selectionArgs = {String.valueOf(visitno), screeningType};

            int deletedRows = contentResolver.delete(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    selection,
                    selectionArgs
            );

            if (deletedRows > 0) {
                Log.d(TAG, "ลบข้อมูลเก่าสำเร็จ จำนวน " + deletedRows + " รายการ " +
                        "สำหรับ visitno: " + visitno + ", screening_type: " + screeningType);
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลเก่า visitno: " + visitno +
                    ", screening_type: " + screeningType, e);
        }
    }
    private void deleteExistingResultByPersonAndVisit(int personId, int visitno, String screeningType) {
        try {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=?";
            String[] selectionArgs = {String.valueOf(personId), String.valueOf(visitno), screeningType};

            int deletedRows = contentResolver.delete(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    selection,
                    selectionArgs
            );

            if (deletedRows > 0) {
                Log.d(TAG, "ลบข้อมูลเก่าสำเร็จ จำนวน " + deletedRows + " รายการ " +
                        "สำหรับ personId: " + personId + ", visitno: " + visitno +
                        ", screening_type: " + screeningType);
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลเก่า personId: " + personId +
                    ", visitno: " + visitno + ", screening_type: " + screeningType, e);
        }
    }
    private void deactivateExistingResult(int visitno, String screeningType) {
        try {
            ContentValues updateValues = new ContentValues();
            updateValues.put(ScreeningResultCode.STATUS, ScreeningResultCode.STATUS_INACTIVE);
            updateValues.put(ScreeningResultCode.UPDATETIME, "datetime('now')");

            String selection = ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(visitno), screeningType, ScreeningResultCode.STATUS_ACTIVE};

            int updatedRows = contentResolver.update(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    updateValues,
                    selection,
                    selectionArgs
            );

            if (updatedRows > 0) {
                Log.d(TAG, "ปิดการใช้งานข้อมูลเก่าสำเร็จ จำนวน " + updatedRows + " รายการ " +
                        "สำหรับ visitno: " + visitno + ", screening_type: " + screeningType);
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการปิดการใช้งานข้อมูลเก่า visitno: " + visitno +
                    ", screening_type: " + screeningType, e);
        }
    }


    /**
     * บันทึกผลการคัดกรอง 2Q
     */
    public Uri save2QResult(int personId, int visitno, boolean hasPositiveAnswer, String userCreate) {
        try {
            String resultCode = hasPositiveAnswer ? "1B0211" : "1B0210";
            String resultDescription = hasPositiveAnswer ?
                    "ผิดปกติ และส่งต่อเจ้าหน้าที่" : "ปกติ";
            String riskLevel = hasPositiveAnswer ?
                    ScreeningResultCode.RISK_HIGH : ScreeningResultCode.RISK_NORMAL;
            String recommendation = hasPositiveAnswer ?
                    "แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติม และพิจารณาปรึกษาแพทย์" :
                    "ไม่พบความเสี่ยงต่อภาวะซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";

            ScreeningResultData data = new ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_STRESS_DEPRESSION_2Q;
            data.resultCode = resultCode;
            data.resultDescription = resultDescription;
            data.totalScore = hasPositiveAnswer ? 1 : 0;
            data.riskLevel = riskLevel;
            data.isAbnormal = hasPositiveAnswer;
            data.recommendation = recommendation;
            data.screeningDate = getCurrentDate();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;

            return saveScreeningResult(data);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผล 2Q", e);
            return null;
        }
    }
    /**
     * บันทึกผลการคัดกรอง 9Q - เวอร์ชันปรับปรุง
     */
    public Uri save9QResult(int personId, int visitno, int totalScore, String userCreate) {
        try {
            String resultCode, resultDescription, riskLevel;
            boolean isAbnormal = totalScore >= 7;

            if (totalScore < 7) {
                resultCode = "1B0260";
                resultDescription = "ไม่มีอาการของโรคซึมเศร้า";
                riskLevel = ScreeningResultCode.RISK_NORMAL;
            } else if (totalScore <= 12) {
                resultCode = "1B0261";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับน้อย";
                riskLevel = ScreeningResultCode.RISK_LOW;
            } else if (totalScore <= 18) {
                resultCode = "1B0262";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับปานกลาง";
                riskLevel = ScreeningResultCode.RISK_MODERATE;
            } else {
                resultCode = "1B0263";
                resultDescription = "มีอาการของโรคซึมเศร้าระดับรุนแรง";
                riskLevel = ScreeningResultCode.RISK_HIGH;
            }

            // กำหนดคำแนะนำ
            String recommendation;
            if (totalScore < 7) {
                recommendation = "ไม่มีอาการของโรคซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";
            } else if (totalScore <= 12) {
                recommendation = "มีอาการซึมเศร้าระดับน้อย ควรพักผ่อนให้เพียงพอ ออกกำลังกาย และทำกิจกรรมที่ชื่นชอบ";
            } else if (totalScore <= 18) {
                recommendation = "มีอาการซึมเศร้าระดับปานกลาง ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิต";
            } else {
                recommendation = "มีอาการซึมเศร้าระดับรุนแรง ควรพบแพทย์เพื่อรับการรักษาโดยเร็ว";
            }

            ScreeningResultData data = new ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_STRESS_DEPRESSION_9Q;
            data.resultCode = resultCode;
            data.resultDescription = resultDescription;
            data.totalScore = totalScore;
            data.riskLevel = riskLevel;
            data.isAbnormal = isAbnormal;
            data.recommendation = recommendation;
            data.screeningDate = getCurrentDate();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // เพิ่มข้อมูลสำหรับ 9Q
            data.hasHighRisk = totalScore >= 19;
            if (totalScore < 7) {
                data.severityLevel = "NORMAL";
            } else if (totalScore <= 12) {
                data.severityLevel = "MILD";
            } else if (totalScore <= 18) {
                data.severityLevel = "MODERATE";
            } else {
                data.severityLevel = "SEVERE";
            }

            return saveScreeningResult(data);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผล 9Q", e);
            return null;
        }
    }
    /**
     * บันทึกผลการคัดกรอง 8Q
     */
    public Uri save8QResult(int personId, int visitno, int totalScore, String userCreate) {
        try
        {
        String resultCode, resultDescription, riskLevel;
        boolean isAbnormal = totalScore > 0;

        if (totalScore == 0) {
            resultCode = "1B0270";
            resultDescription = "ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
            riskLevel = ScreeningResultCode.RISK_NORMAL;
        } else if (totalScore <= 8) {
            resultCode = "1B0271";
            resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
            riskLevel = ScreeningResultCode.RISK_LOW;
        } else if (totalScore <= 16) {
            resultCode = "1B0272";
            resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
            riskLevel = ScreeningResultCode.RISK_MODERATE;
        } else {
            resultCode = "1B0273";
            resultDescription = "มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
            riskLevel = ScreeningResultCode.RISK_VERY_HIGH;
        }

        // กำหนดคำแนะนำ
        String recommendation;
        if (totalScore >= 17) {
            recommendation = "⚠️ ความเสี่ยงสูงมาก! ต้องดำเนินการแทรกแซงทันที และส่งต่อผู้เชี่ยวชาญโดยด่วน";
        } else if (totalScore >= 9) {
            recommendation = "🚨 ความเสี่ยงปานกลาง ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด พิจารณาส่งต่อผู้เชี่ยวชาญ";
        } else if (totalScore >= 1) {
            recommendation = "⚠️ ความเสี่ยงต่ำ ควรให้การสนับสนุนและคำแนะนำ ติดตามสถานการณ์";
        } else {
            recommendation = "✅ ไม่มีความเสี่ยง ควรส่งเสริมสุขภาพจิตต่อไป";
        }

        ScreeningResultData data = new ScreeningResultData();
        data.personId = personId;
        data.visitno = visitno;
        data.screeningType = ScreeningResultCode.TYPE_SUICIDE_ASSESSMENT_8Q;
        data.resultCode = resultCode;
        data.resultDescription = resultDescription;
        data.totalScore = totalScore;
        data.riskLevel = riskLevel;
        data.isAbnormal = isAbnormal;
        data.recommendation = recommendation;
        data.screeningDate = getCurrentDate();
        data.status = ScreeningResultCode.STATUS_ACTIVE;
        data.userCreate = userCreate;
        data.userUpdate = userCreate;

        // เพิ่มข้อมูลสำหรับ 8Q
        data.hasHighRisk = totalScore >= 17;
        data.requiresFollowUp = totalScore >= 1;

        if (totalScore >= 17) {
            data.followUpType = "IMMEDIATE_INTERVENTION";
            data.severityLevel = "HIGH_RISK";
        } else if (totalScore >= 9) {
            data.followUpType = "CLOSE_MONITORING";
            data.severityLevel = "MODERATE_RISK";
        } else if (totalScore >= 1) {
            data.followUpType = "SUPPORT_COUNSELING";
            data.severityLevel = "LOW_RISK";
        } else {
            data.severityLevel = "NO_RISK";
        }

        return saveScreeningResult(data);
    } catch (Exception e) {
        Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผล 8Q", e);
        return null;
    }

    }

    /**
     * บันทึกผลการคัดกรอง ST5
     */
    public Uri saveST5Result(int personId, int visitno, int totalScore, String userCreate) {
        try {
            String resultCode, resultDescription, riskLevel;
            boolean isAbnormal = totalScore >= 5;

            if (totalScore <= 4) {
                resultCode = "1B132";
                resultDescription = "เครียดน้อย";
                riskLevel = ScreeningResultCode.RISK_NORMAL;
            } else if (totalScore <= 7) {
                resultCode = "1B133";
                resultDescription = "เครียดปานกลาง";
                riskLevel = ScreeningResultCode.RISK_LOW;
            } else if (totalScore <= 9) {
                resultCode = "1B134";
                resultDescription = "เครียดมาก";
                riskLevel = ScreeningResultCode.RISK_MODERATE;
            } else {
                resultCode = "1B134";
                resultDescription = "เครียดมากที่สุด";
                riskLevel = ScreeningResultCode.RISK_HIGH;
            }

            ScreeningResultData data = new ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_STRESS_DEPRESSION_ST5;
            data.resultCode = resultCode;
            data.resultDescription = resultDescription;
            data.totalScore = totalScore;
            data.riskLevel = riskLevel;
            data.isAbnormal = isAbnormal;
            data.screeningDate = getCurrentDate();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;

            return saveScreeningResult(data);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผล ST5", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลผลการคัดกรองทั้งหมด
     */
    public List<ScreeningResultData> getAllResults() {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    null, null, null,
                    ScreeningResultCode.VISITNO + " DESC, " + ScreeningResultCode.SCREENING_DATE + " DESC"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลทั้งหมด", e);
        }

        return results;
    }

    /**
     * ดึงข้อมูลผลการคัดกรองตาม Person ID
     */
    public List<ScreeningResultData> getResultsByPersonId(int personId) {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/person/" + personId);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Person ID: " + personId, e);
        }

        return results;
    }

    /**
     * ดึงข้อมูลผลการคัดกรองตาม Visit Number
     */
    public List<ScreeningResultData> getResultsByVisitNo(int visitno) {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/visit/" + visitno);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Visit No: " + visitno, e);
        }

        return results;
    }

    /**
     * ดึงข้อมูลผลการคัดกรองตาม Person ID และ Visit Number
     */
    public List<ScreeningResultData> getResultsByPersonAndVisit(int personId, int visitno) {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/person/" + personId + "/visit/" + visitno);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Person ID: " + personId +
                    " และ Visit No: " + visitno, e);
        }

        return results;
    }

    /**
     * ดึงข้อมูลผลการคัดกรองตามประเภท
     */
    public List<ScreeningResultData> getResultsByType(String screeningType) {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/type/" + screeningType);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตามประเภท: " + screeningType, e);
        }

        return results;
    }

    /**
     * ดึงข้อมูลผลการคัดกรองล่าสุดของผู้รับบริการ
     */
    public List<ScreeningResultData> getLatestResultsByPersonId(int personId) {
        List<ScreeningResultData> results = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/person/" + personId + "/latest");

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลล่าสุดของ Person ID: " + personId, e);
        }

        return results;
    }

    /**
     * ดึงผลการคัดกรองล่าสุดตามประเภทและ Person ID
     */
    public ScreeningResultData getLatestResultByTypeAndPersonId(int personId, String screeningType) {
        try {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), screeningType, ScreeningResultCode.STATUS_ACTIVE};
            String sortOrder = ScreeningResultCode.VISITNO + " DESC, " +
                    ScreeningResultCode.SCREENING_DATE + " DESC, " +
                    ScreeningResultCode.CREATETIME + " DESC LIMIT 1";

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    null, selection, selectionArgs, sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                ScreeningResultData result = convertFromCursor(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลล่าสุดตามประเภท: " + screeningType +
                    " สำหรับ Person ID: " + personId, e);
        }

        return null;
    }

    /**
     * ดึงผลการคัดกรองตามประเภท Person ID และ Visit Number
     */
    public ScreeningResultData getResultByTypePersonAndVisit(int personId, int visitno, String screeningType) {
        try {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), String.valueOf(visitno),
                    screeningType, ScreeningResultCode.STATUS_ACTIVE};
            String sortOrder = ScreeningResultCode.SCREENING_DATE + " DESC, " +
                    ScreeningResultCode.CREATETIME + " DESC LIMIT 1";

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    null, selection, selectionArgs, sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                ScreeningResultData result = convertFromCursor(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตามประเภท: " + screeningType +
                    " สำหรับ Person ID: " + personId + " Visit No: " + visitno, e);
        }

        return null;
    }

    /**
     * ตรวจสอบว่ามีผลการคัดกรองผิดปกติหรือไม่
     */
    public boolean hasAbnormalResults(int personId) {
        try {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.IS_ABNORMAL + "=1 AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    new String[]{ScreeningResultCode.ID},
                    selection, selectionArgs, null
            );

            boolean hasAbnormal = false;
            if (cursor != null) {
                hasAbnormal = cursor.getCount() > 0;
                cursor.close();
            }

            return hasAbnormal;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบผลผิดปกติสำหรับ Person ID: " + personId, e);
            return false;
        }
    }

    /**
     * ตรวจสอบว่ามีผลการคัดกรองผิดปกติในการเยี่ยมนั้นๆ หรือไม่
     */
    public boolean hasAbnormalResultsInVisit(int personId, int visitno) {
        try {
            String selection = ScreeningResultCode.PERSON_ID + "=? AND " +
                    ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.IS_ABNORMAL + "=1 AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(personId), String.valueOf(visitno),
                    ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    new String[]{ScreeningResultCode.ID},
                    selection, selectionArgs, null
            );

            boolean hasAbnormal = false;
            if (cursor != null) {
                hasAbnormal = cursor.getCount() > 0;
                cursor.close();
            }

            return hasAbnormal;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบผลผิดปกติสำหรับ Person ID: " + personId +
                    " Visit No: " + visitno, e);
            return false;
        }
    }

    /**
     * อัพเดทข้อมูลผลการคัดกรอง
     */
    public int updateResult(long resultId, ScreeningResultData newData) {
        try {
            ContentValues values = convertToContentValues(newData);
            values.put(ScreeningResultCode.UPDATETIME, "datetime('now')");

            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/" + resultId);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Log.d(TAG, "อัพเดทข้อมูลสำเร็จ: " + resultId);
            }

            return rowsUpdated;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการอัพเดทข้อมูล ID: " + resultId, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลผลการคัดกรอง (Hard Delete)
     */
    public int deleteResult(long resultId) {
        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/" + resultId);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลสำเร็จ: " + resultId);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูล ID: " + resultId, e);
            return 0;
        }
    }

    /**
     * ปิดการใช้งานผลการคัดกรอง (Soft Delete)
     */
    public int deactivateResult(long resultId) {
        try {
            ContentValues values = new ContentValues();
            values.put(ScreeningResultCode.STATUS, ScreeningResultCode.STATUS_INACTIVE);
            values.put(ScreeningResultCode.UPDATETIME, "datetime('now')");

            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/" + resultId);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Log.d(TAG, "ปิดการใช้งานข้อมูลสำเร็จ: " + resultId);
            }

            return rowsUpdated;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการปิดการใช้งานข้อมูล ID: " + resultId, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของผู้รับบริการ
     */
    public int deleteAllResultsByPersonId(int personId) {
        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/person/" + personId);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Person ID สำเร็จ: " + personId);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Person ID: " + personId, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของการเยี่ยมนั้นๆ
     */
    public int deleteAllResultsByVisitNo(int visitno) {
        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/visit/" + visitno);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Visit No สำเร็จ: " + visitno);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Visit No: " + visitno, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของผู้รับบริการในการเยี่ยมนั้นๆ
     */
    public int deleteAllResultsByPersonAndVisit(int personId, int visitno) {
        try {
            Uri uri = Uri.parse("content://" + ScreeningResultCodeProvider.AUTHORITY +
                    "/screening_result/person/" + personId + "/visit/" + visitno);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Person ID: " + personId +
                        " Visit No: " + visitno + " สำเร็จ");
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Person ID: " + personId +
                    " Visit No: " + visitno, e);
            return 0;
        }
    }

    /**
     * ดึงสถิติผลการคัดกรองตามประเภท
     */
    public ScreeningStatistics getStatisticsByType(String screeningType) {
        ScreeningStatistics stats = new ScreeningStatistics();
        stats.screeningType = screeningType;

        try {
            String selection = ScreeningResultCode.SCREENING_TYPE + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {screeningType, ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null) {
                stats.totalCount = cursor.getCount();

                while (cursor.moveToNext()) {
                    boolean isAbnormal = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.IS_ABNORMAL)) == 1;
                    int score = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.TOTAL_SCORE));

                    if (isAbnormal) {
                        stats.abnormalCount++;
                    } else {
                        stats.normalCount++;
                    }

                    stats.totalScore += score;

                    if (score > stats.maxScore) {
                        stats.maxScore = score;
                    }

                    if (score < stats.minScore || stats.minScore == 0) {
                        stats.minScore = score;
                    }
                }

                if (stats.totalCount > 0) {
                    stats.averageScore = (double) stats.totalScore / stats.totalCount;
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติสำหรับประเภท: " + screeningType, e);
        }

        return stats;
    }

    /**
     * ดึงสถิติผลการคัดกรองตามการเยี่ยม
     */
    public ScreeningStatistics getStatisticsByVisit(int visitno) {
        ScreeningStatistics stats = new ScreeningStatistics();
        stats.visitno = visitno;

        try {
            String selection = ScreeningResultCode.VISITNO + "=? AND " +
                    ScreeningResultCode.STATUS + "=?";
            String[] selectionArgs = {String.valueOf(visitno), ScreeningResultCode.STATUS_ACTIVE};

            Cursor cursor = contentResolver.query(
                    ScreeningResultCode.CONTENT_LIST_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null) {
                stats.totalCount = cursor.getCount();

                while (cursor.moveToNext()) {
                    boolean isAbnormal = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.IS_ABNORMAL)) == 1;
                    int score = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.TOTAL_SCORE));

                    if (isAbnormal) {
                        stats.abnormalCount++;
                    } else {
                        stats.normalCount++;
                    }

                    stats.totalScore += score;

                    if (score > stats.maxScore) {
                        stats.maxScore = score;
                    }

                    if (score < stats.minScore || stats.minScore == 0) {
                        stats.minScore = score;
                    }
                }

                if (stats.totalCount > 0) {
                    stats.averageScore = (double) stats.totalScore / stats.totalCount;
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติสำหรับ Visit No: " + visitno, e);
        }

        return stats;
    }

    // Helper Methods

    /**
     * แปลง ScreeningResultData เป็น ContentValues
     */
    private ContentValues convertToContentValues(ScreeningResultData data) {
        ContentValues values = new ContentValues();

        values.put(ScreeningResultCode.PERSON_ID, data.personId);
        values.put(ScreeningResultCode.VISITNO, data.visitno);
        values.put(ScreeningResultCode.SCREENING_TYPE, data.screeningType);
        values.put(ScreeningResultCode.RESULT_CODE, data.resultCode);
        values.put(ScreeningResultCode.RESULT_DESCRIPTION, data.resultDescription);
        values.put(ScreeningResultCode.TOTAL_SCORE, data.totalScore);
        values.put(ScreeningResultCode.RISK_LEVEL, data.riskLevel);
        values.put(ScreeningResultCode.IS_ABNORMAL, data.isAbnormal ? 1 : 0);
        values.put(ScreeningResultCode.RECOMMENDATION, data.recommendation);
        values.put(ScreeningResultCode.SCREENING_DATE, data.screeningDate);
        values.put(ScreeningResultCode.STATUS, data.status);
        values.put(ScreeningResultCode.USER_CREATE, data.userCreate);
        values.put(ScreeningResultCode.USER_UPDATE, data.userUpdate);

        return values;
    }

    /**
     * แปลง Cursor เป็น ScreeningResultData
     */
    private ScreeningResultData convertFromCursor(Cursor cursor) {
        ScreeningResultData data = new ScreeningResultData();

        data.id = cursor.getLong(cursor.getColumnIndex(ScreeningResultCode.ID));
        data.personId = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.PERSON_ID));
        data.visitno = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.VISITNO));
        data.screeningType = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.SCREENING_TYPE));
        data.resultCode = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RESULT_CODE));
        data.resultDescription = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RESULT_DESCRIPTION));
        data.totalScore = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.TOTAL_SCORE));
        data.riskLevel = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RISK_LEVEL));
        data.isAbnormal = cursor.getInt(cursor.getColumnIndex(ScreeningResultCode.IS_ABNORMAL)) == 1;
        data.recommendation = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.RECOMMENDATION));
        data.screeningDate = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.SCREENING_DATE));
        data.status = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.STATUS));
        data.createTime = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.CREATETIME));
        data.updateTime = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.UPDATETIME));
        data.userCreate = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.USER_CREATE));
        data.userUpdate = cursor.getString(cursor.getColumnIndex(ScreeningResultCode.USER_UPDATE));

        return data;
    }

    /**
     * ดึงวันที่ปัจจุบันในรูปแบบ yyyy-MM-dd
     */
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // Data Classes

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
        public String userCreate;
        public String userUpdate;

        public Boolean hasHighRisk;
        public String additionalInfo;
        public Boolean requiresFollowUp;
        public String followUpType;
        public String severityLevel;

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
                    ", hasHighRisk=" + hasHighRisk +
                    ", requiresFollowUp=" + requiresFollowUp +
                    ", severityLevel='" + severityLevel + '\'' +
                    ", screeningDate='" + screeningDate + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    /**
     * คลาสสำหรับเก็บสถิติการคัดกรอง
     */
    public static class ScreeningStatistics {
        public String screeningType;
        public int visitno;
        public int totalCount;
        public int normalCount;
        public int abnormalCount;
        public int totalScore;
        public double averageScore;
        public int maxScore;
        public int minScore;

        @Override
        public String toString() {
            return "ScreeningStatistics{" +
                    "screeningType='" + screeningType + '\'' +
                    ", visitno=" + visitno +
                    ", totalCount=" + totalCount +
                    ", normalCount=" + normalCount +
                    ", abnormalCount=" + abnormalCount +
                    ", averageScore=" + averageScore +
                    ", maxScore=" + maxScore +
                    ", minScore=" + minScore +
                    '}';
        }
    }
}