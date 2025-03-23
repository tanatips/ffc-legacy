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

import th.in.ffc.app.form.nhso.model.NHSOOPDInfo;
import th.in.ffc.provider.NHSOOPD;

/**
 * Service สำหรับจัดการข้อมูลแฟ้มที่ 4 NHSO OPD
 */
public class NHSOOPDService {

    private static final String TAG = "NHSOOPDService";
    private Context context;
    private SimpleDateFormat dateFormat;

    public NHSOOPDService(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูล OPD ลงในฐานข้อมูล
     * @param opdInfo ข้อมูล OPD ที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createOPD(NHSOOPDInfo opdInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            // กำหนดค่าให้ ContentValues
            values.put(NHSOOPD.SEQ, opdInfo.getSeq());

            if (opdInfo.getDateOPD() != null) {
                values.put(NHSOOPD.DATEOPD, dateFormat.format(opdInfo.getDateOPD()));
            }

            values.put(NHSOOPD.INSCL, opdInfo.getInscl());
            values.put(NHSOOPD.PERMITNO, opdInfo.getPermitNo());
            values.put(NHSOOPD.HTYPE, opdInfo.getHtype());
            values.put(NHSOOPD.UUC, opdInfo.getUuc());
            values.put(NHSOOPD.CHIEFCOMP, opdInfo.getChiefcomp());

            if (opdInfo.getBtemp() != null) {
                values.put(NHSOOPD.BTEMP, opdInfo.getBtemp());
            }

            if (opdInfo.getSbp() != null) {
                values.put(NHSOOPD.SBP, opdInfo.getSbp());
            }

            if (opdInfo.getDbp() != null) {
                values.put(NHSOOPD.DBP, opdInfo.getDbp());
            }

            if (opdInfo.getPr() != null) {
                values.put(NHSOOPD.PR, opdInfo.getPr());
            }

            if (opdInfo.getRr() != null) {
                values.put(NHSOOPD.RR, opdInfo.getRr());
            }

            if (opdInfo.getWaistline() != null) {
                values.put(NHSOOPD.WAISTLINE, opdInfo.getWaistline());
            }

            if (opdInfo.getWeight() != null) {
                values.put(NHSOOPD.WEIGHT, opdInfo.getWeight());
            }

            if (opdInfo.getHeight() != null) {
                values.put(NHSOOPD.HEIGHT, opdInfo.getHeight());
            }

            if (opdInfo.getHeadcircum() != null) {
                values.put(NHSOOPD.HEADCIRCUM, opdInfo.getHeadcircum());
            }

            values.put(NHSOOPD.CLINIC, opdInfo.getClinic());
            values.put(NHSOOPD.USER, username);
            values.put(NHSOOPD.DATEUPDATE, System.currentTimeMillis());

            // บันทึกข้อมูล
            return context.getContentResolver().insert(NHSOOPD.CONTENT_URI, values);
        } catch (Exception e) {
            Log.e(TAG, "Error creating OPD record", e);
            return null;
        }
    }

    /**
     * ค้นหาข้อมูล OPD จากรหัส Visit
     * @param seq รหัส Visit ที่ต้องการค้นหา
     * @return ข้อมูล OPD ที่พบ หรือ null ถ้าไม่พบ
     */
    public NHSOOPDInfo getOPDBySeq(String seq) {
        NHSOOPDInfo result = null;

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOOPD.CONTENT_URI,
                    null,
                    NHSOOPD.SEQ + "=?",
                    new String[]{seq},
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    result = cursorToOPD(cursor);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying OPD record by SEQ: " + seq, e);
        }

        return result;
    }

    /**
     * ค้นหาข้อมูล OPD ทั้งหมด
     * @return รายการข้อมูล OPD ที่พบ
     */
    public List<NHSOOPDInfo> getAllOPD() {
        List<NHSOOPDInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOOPD.CONTENT_URI,
                    null,
                    null,
                    null,
                    NHSOOPD.DATEOPD + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToOPD(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying all OPD records", e);
        }

        return results;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOOPDInfo
     * @param cursor Cursor ที่ต้องการแปลง
     * @return NHSOOPDInfo
     */
    private NHSOOPDInfo cursorToOPD(Cursor cursor) {
        NHSOOPDInfo opdInfo = new NHSOOPDInfo();

        opdInfo.setSeq(cursor.getString(cursor.getColumnIndex(NHSOOPD.SEQ)));

        String dateOPDStr = cursor.getString(cursor.getColumnIndex(NHSOOPD.DATEOPD));
        if (dateOPDStr != null) {
            try {
                opdInfo.setDateOPD(dateFormat.parse(dateOPDStr));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + dateOPDStr, e);
            }
        }

        opdInfo.setInscl(cursor.getString(cursor.getColumnIndex(NHSOOPD.INSCL)));
        opdInfo.setPermitNo(cursor.getString(cursor.getColumnIndex(NHSOOPD.PERMITNO)));
        opdInfo.setHtype(cursor.getString(cursor.getColumnIndex(NHSOOPD.HTYPE)));
        opdInfo.setUuc(cursor.getString(cursor.getColumnIndex(NHSOOPD.UUC)));
        opdInfo.setChiefcomp(cursor.getString(cursor.getColumnIndex(NHSOOPD.CHIEFCOMP)));

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.BTEMP))) {
            opdInfo.setBtemp(cursor.getDouble(cursor.getColumnIndex(NHSOOPD.BTEMP)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.SBP))) {
            opdInfo.setSbp(cursor.getInt(cursor.getColumnIndex(NHSOOPD.SBP)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.DBP))) {
            opdInfo.setDbp(cursor.getInt(cursor.getColumnIndex(NHSOOPD.DBP)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.PR))) {
            opdInfo.setPr(cursor.getInt(cursor.getColumnIndex(NHSOOPD.PR)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.RR))) {
            opdInfo.setRr(cursor.getInt(cursor.getColumnIndex(NHSOOPD.RR)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.WAISTLINE))) {
            opdInfo.setWaistline(cursor.getInt(cursor.getColumnIndex(NHSOOPD.WAISTLINE)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.WEIGHT))) {
            opdInfo.setWeight(cursor.getDouble(cursor.getColumnIndex(NHSOOPD.WEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.HEIGHT))) {
            opdInfo.setHeight(cursor.getInt(cursor.getColumnIndex(NHSOOPD.HEIGHT)));
        }

        if (!cursor.isNull(cursor.getColumnIndex(NHSOOPD.HEADCIRCUM))) {
            opdInfo.setHeadcircum(cursor.getDouble(cursor.getColumnIndex(NHSOOPD.HEADCIRCUM)));
        }

        opdInfo.setClinic(cursor.getString(cursor.getColumnIndex(NHSOOPD.CLINIC)));

        return opdInfo;
    }

    /**
     * อัพเดทข้อมูล OPD
     * @param opdInfo ข้อมูล OPD ที่ต้องการอัพเดท
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนแถวที่ถูกอัพเดท
     */
    public int updateOPD(NHSOOPDInfo opdInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            if (opdInfo.getDateOPD() != null) {
                values.put(NHSOOPD.DATEOPD, dateFormat.format(opdInfo.getDateOPD()));
            }

            values.put(NHSOOPD.INSCL, opdInfo.getInscl());
            values.put(NHSOOPD.PERMITNO, opdInfo.getPermitNo());
            values.put(NHSOOPD.HTYPE, opdInfo.getHtype());
            values.put(NHSOOPD.UUC, opdInfo.getUuc());
            values.put(NHSOOPD.CHIEFCOMP, opdInfo.getChiefcomp());

            if (opdInfo.getBtemp() != null) {
                values.put(NHSOOPD.BTEMP, opdInfo.getBtemp());
            }

            if (opdInfo.getSbp() != null) {
                values.put(NHSOOPD.SBP, opdInfo.getSbp());
            }

            if (opdInfo.getDbp() != null) {
                values.put(NHSOOPD.DBP, opdInfo.getDbp());
            }

            if (opdInfo.getPr() != null) {
                values.put(NHSOOPD.PR, opdInfo.getPr());
            }

            if (opdInfo.getRr() != null) {
                values.put(NHSOOPD.RR, opdInfo.getRr());
            }

            if (opdInfo.getWaistline() != null) {
                values.put(NHSOOPD.WAISTLINE, opdInfo.getWaistline());
            }

            if (opdInfo.getWeight() != null) {
                values.put(NHSOOPD.WEIGHT, opdInfo.getWeight());
            }

            if (opdInfo.getHeight() != null) {
                values.put(NHSOOPD.HEIGHT, opdInfo.getHeight());
            }

            if (opdInfo.getHeadcircum() != null) {
                values.put(NHSOOPD.HEADCIRCUM, opdInfo.getHeadcircum());
            }

            values.put(NHSOOPD.CLINIC, opdInfo.getClinic());
            values.put(NHSOOPD.USER, username);
            values.put(NHSOOPD.UPDATE, "1"); // ตั้งค่าสถานะการอัพเดท
            values.put(NHSOOPD.DATEUPDATE, System.currentTimeMillis());

            // อัพเดทข้อมูล
            return context.getContentResolver().update(
                    NHSOOPD.CONTENT_URI,
                    values,
                    NHSOOPD.SEQ + "=?",
                    new String[]{opdInfo.getSeq()});
        } catch (Exception e) {
            Log.e(TAG, "Error updating OPD record: " + opdInfo.getSeq(), e);
            return 0;
        }
    }

    /**
     * ลบข้อมูล OPD
     * @param seq รหัส Visit ที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int deleteOPD(String seq) {
        try {
            return context.getContentResolver().delete(
                    NHSOOPD.CONTENT_URI,
                    NHSOOPD.SEQ + "=?",
                    new String[]{seq});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting OPD record: " + seq, e);
            return 0;
        }
    }

    /**
     * สร้างข้อมูล OPD จากข้อมูลของผู้ป่วย (อัตโนมัติ)
     * @param patientId รหัสผู้ป่วย
     * @param visitId รหัสการเข้ารับบริการ
     * @param visitDate วันที่เข้ารับบริการ
     * @param temperature อุณหภูมิร่างกาย
     * @param systolicBP ความดันโลหิตค่าบน
     * @param diastolicBP ความดันโลหิตค่าล่าง
     * @param pulseRate อัตราการเต้นของหัวใจ
     * @param respirationRate อัตราการหายใจ
     * @param waist รอบเอว
     * @param weight น้ำหนัก
     * @param height ส่วนสูง
     * @param username ชื่อผู้ใช้
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createOPDFromPatientData(
            String patientId,
            String visitId,
            Date visitDate,
            Double temperature,
            Integer systolicBP,
            Integer diastolicBP,
            Integer pulseRate,
            Integer respirationRate,
            Integer waist,
            Double weight,
            Integer height,
            String username) {

        NHSOOPDInfo opdInfo = new NHSOOPDInfo();
        opdInfo.setSeq(visitId);
        opdInfo.setDateOPD(visitDate);
        opdInfo.setInscl("UCS"); // ค่าเริ่มต้นเป็นสิทธิ UC
        opdInfo.setUuc("1"); // ใช้สิทธิ์

        opdInfo.setBtemp(temperature);
        opdInfo.setSbp(systolicBP);
        opdInfo.setDbp(diastolicBP);
        opdInfo.setPr(pulseRate);
        opdInfo.setRr(respirationRate);
        opdInfo.setWaistline(waist);
        opdInfo.setWeight(weight);
        opdInfo.setHeight(height);

        return createOPD(opdInfo, username);
    }
}