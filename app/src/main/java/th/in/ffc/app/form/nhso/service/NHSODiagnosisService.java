package th.in.ffc.app.form.nhso.service;

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

import th.in.ffc.app.form.nhso.model.NHSODiagnosisInfo;
import th.in.ffc.provider.NHSODiagnosis;

/**
 * Service สำหรับจัดการข้อมูลแฟ้มที่ 5 NHSO Diagnosis
 */
public class NHSODiagnosisService {

    private static final String TAG = "NHSODiagnosisService";
    private Context context;
    private SimpleDateFormat dateFormat;

    public NHSODiagnosisService(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูลวินิจฉัยโรคลงในฐานข้อมูล
     * @param diagnosisInfo ข้อมูลวินิจฉัยโรคที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createDiagnosis(NHSODiagnosisInfo diagnosisInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            // กำหนดค่าให้ ContentValues
            values.put(NHSODiagnosis.SEQ, diagnosisInfo.getSeq());

            if (diagnosisInfo.getDateDx() != null) {
                values.put(NHSODiagnosis.DATEDX, dateFormat.format(diagnosisInfo.getDateDx()));
            }

            values.put(NHSODiagnosis.DIAG, diagnosisInfo.getDiag());
            values.put(NHSODiagnosis.DIAGTYPE, diagnosisInfo.getDiagType());
            values.put(NHSODiagnosis.PROFESSION_ID, diagnosisInfo.getProfessionId());
            values.put(NHSODiagnosis.CLINIC, diagnosisInfo.getClinic());
            values.put(NHSODiagnosis.USER_CREATE, username);
            values.put(NHSODiagnosis.CREATETIME, System.currentTimeMillis());

            // บันทึกข้อมูล
            return context.getContentResolver().insert(NHSODiagnosis.CONTENT_URI, values);
        } catch (Exception e) {
            Log.e(TAG, "Error creating diagnosis record", e);
            return null;
        }
    }

    /**
     * ค้นหาข้อมูลวินิจฉัยโรคจากรหัส Visit
     * @param seq รหัส Visit ที่ต้องการค้นหา
     * @return รายการข้อมูลวินิจฉัยโรคที่พบ
     */
    public List<NHSODiagnosisInfo> getDiagnosisBySeq(String seq) {
        List<NHSODiagnosisInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSODiagnosis.CONTENT_LIST_URI,
                    null,
                    NHSODiagnosis.SEQ + "=?",
                    new String[]{seq},
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(cursorToDiagnosis(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying diagnosis by SEQ: " + seq, e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูลวินิจฉัยโรคทั้งหมด
     * @return รายการข้อมูลวินิจฉัยโรคทั้งหมด
     */
    public List<NHSODiagnosisInfo> getAllDiagnosis() {
        List<NHSODiagnosisInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSODiagnosis.CONTENT_LIST_URI,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(cursorToDiagnosis(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying all diagnosis records", e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูลวินิจฉัยโรคตามรหัสโรค
     * @param diagCode รหัสโรคที่ต้องการค้นหา
     * @return รายการข้อมูลวินิจฉัยโรคที่พบ
     */
    public List<NHSODiagnosisInfo> getDiagnosisByDiagCode(String diagCode) {
        List<NHSODiagnosisInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSODiagnosis.CONTENT_LIST_URI,
                    null,
                    NHSODiagnosis.DIAG + "=?",
                    new String[]{diagCode},
                    null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    results.add(cursorToDiagnosis(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying diagnosis by code: " + diagCode, e);
        }

        return results;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSODiagnosisInfo
     * @param cursor Cursor ที่ต้องการแปลง
     * @return NHSODiagnosisInfo
     */
    private NHSODiagnosisInfo cursorToDiagnosis(Cursor cursor) {
        NHSODiagnosisInfo diagnosisInfo = new NHSODiagnosisInfo();

        int idIndex = cursor.getColumnIndex(NHSODiagnosis.ID);
        if (idIndex != -1) {
            diagnosisInfo.setId(cursor.getLong(idIndex));
        }

        int seqIndex = cursor.getColumnIndex(NHSODiagnosis.SEQ);
        if (seqIndex != -1) {
            diagnosisInfo.setSeq(cursor.getString(seqIndex));
        }

        int dateDxIndex = cursor.getColumnIndex(NHSODiagnosis.DATEDX);
        if (dateDxIndex != -1 && !cursor.isNull(dateDxIndex)) {
            String dateDxStr = cursor.getString(dateDxIndex);
            try {
                diagnosisInfo.setDateDx(dateFormat.parse(dateDxStr));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + dateDxStr, e);
            }
        }

        int diagIndex = cursor.getColumnIndex(NHSODiagnosis.DIAG);
        if (diagIndex != -1) {
            diagnosisInfo.setDiag(cursor.getString(diagIndex));
        }

        int diagTypeIndex = cursor.getColumnIndex(NHSODiagnosis.DIAGTYPE);
        if (diagTypeIndex != -1) {
            diagnosisInfo.setDiagType(cursor.getString(diagTypeIndex));
        }

        int professionIdIndex = cursor.getColumnIndex(NHSODiagnosis.PROFESSION_ID);
        if (professionIdIndex != -1) {
            diagnosisInfo.setProfessionId(cursor.getString(professionIdIndex));
        }

        int clinicIndex = cursor.getColumnIndex(NHSODiagnosis.CLINIC);
        if (clinicIndex != -1) {
            diagnosisInfo.setClinic(cursor.getString(clinicIndex));
        }

        return diagnosisInfo;
    }

    /**
     * อัพเดทข้อมูลวินิจฉัยโรค
     * @param diagnosisInfo ข้อมูลวินิจฉัยโรคที่ต้องการอัพเดท
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนแถวที่ถูกอัพเดท
     */
    public int updateDiagnosis(NHSODiagnosisInfo diagnosisInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            if (diagnosisInfo.getDateDx() != null) {
                values.put(NHSODiagnosis.DATEDX, dateFormat.format(diagnosisInfo.getDateDx()));
            }

            values.put(NHSODiagnosis.DIAG, diagnosisInfo.getDiag());
            values.put(NHSODiagnosis.DIAGTYPE, diagnosisInfo.getDiagType());
            values.put(NHSODiagnosis.PROFESSION_ID, diagnosisInfo.getProfessionId());
            values.put(NHSODiagnosis.CLINIC, diagnosisInfo.getClinic());
            values.put(NHSODiagnosis.USER_UPDATE, username);
            values.put(NHSODiagnosis.UPDATETIME, System.currentTimeMillis());
            values.put(NHSODiagnosis.UPDATE, "1");

            // อัพเดทข้อมูล
            String whereClause = NHSODiagnosis.ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(diagnosisInfo.getId())};

            return context.getContentResolver().update(
                    NHSODiagnosis.CONTENT_URI,
                    values,
                    whereClause,
                    whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating diagnosis record", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลวินิจฉัยโรค
     * @param id ID ของข้อมูลวินิจฉัยโรคที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int deleteDiagnosis(long id) {
        try {
            Uri uri = NHSODiagnosis.getContentUri(id);
            return context.getContentResolver().delete(uri, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting diagnosis record", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลวินิจฉัยโรคตามรหัส Visit
     * @param seq รหัส Visit ที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int deleteDiagnosisBySeq(String seq) {
        try {
            String whereClause = NHSODiagnosis.SEQ + "=?";
            String[] whereArgs = new String[]{seq};

            return context.getContentResolver().delete(
                    NHSODiagnosis.CONTENT_URI,
                    whereClause,
                    whereArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting diagnosis records by SEQ", e);
            return 0;
        }
    }

    /**
     * สร้างข้อมูลวินิจฉัยโรค (ข้อมูลพื้นฐาน)
     * @param seq รหัส Visit
     * @param visitDate วันที่รับบริการ
     * @param diagCode รหัสวินิจฉัยโรค
     * @param diagType ประเภทการวินิจฉัย
     * @param username ชื่อผู้ใช้
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createBasicDiagnosis(String seq, Date visitDate, String diagCode, String diagType, String username) {
        NHSODiagnosisInfo diagnosisInfo = new NHSODiagnosisInfo();
        diagnosisInfo.setSeq(seq);
        diagnosisInfo.setDateDx(visitDate);
        diagnosisInfo.setDiag(diagCode);
        diagnosisInfo.setDiagType(diagType);

        return createDiagnosis(diagnosisInfo, username);
    }
}