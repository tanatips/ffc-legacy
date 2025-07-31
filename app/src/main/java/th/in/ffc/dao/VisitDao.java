package th.in.ffc.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import th.in.ffc.provider.PersonProvider.Visit;
import th.in.ffc.util.DateConverter;

/**
 * Data Access Object สำหรับจัดการข้อมูลการเยี่ยม (Visit)
 *
 * @author สร้างโดย Claude Assistant
 * @version 1.1
 * @since 1.0
 */
public class VisitDao {
    private static final String TAG = "VisitDao";
    private ContentResolver mResolver;

    public VisitDao(ContentResolver resolver) {
        this.mResolver = resolver;
    }

    /**
     * สร้าง Visit No ใหม่โดยเพิ่ม 1 จาก Visit No ล่าสุดของ PCU
     *
     * @param pcucode รหัส PCU
     * @return Visit No ใหม่
     */
    public long getNewVisitNo(String pcucode) {
        try {
            String selection = Visit.PCUCODE + "=?";
            String[] selectionArgs = {pcucode};
            String[] projection = {"MAX(" + Visit.NO + ") as max_visitno"};

            Cursor cursor = mResolver.query(Visit.CONTENT_URI, projection, selection, selectionArgs, Visit.PCUCODE);

            long maxVisitNo = 0;
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    maxVisitNo = cursor.getLong(0);
                }
                cursor.close();
            }

            return maxVisitNo + 1;
        } catch (Exception e) {
            Log.e(TAG, "Error getting new visit number", e);
            return 1; // เริ่มต้นที่ 1 หากมีข้อผิดพลาด
        }
    }

    /**
     * บันทึกข้อมูลการเยี่ยมใหม่ (โดยกำหนด visitNo อัตโนมัติ)
     *
     * @param pcucode รหัส PCU
     * @param pcucodePerson รหัส PCU ของบุคคล
     * @param pid รหัสบุคคล
     * @param visitDate วันที่เยี่ยม
     * @param rightCode รหัสสิทธิการรักษา
     * @param rightHmain รหัสสถานพยาบาลหลัก
     * @param rightHsub รหัสสถานพยาบาลรอง
     * @param rightNo เลขที่สิทธิการรักษา
     * @param incup ค่าใช้จ่ายในโครงการ
     * @param serviceType ประเภทบริการ
     * @param username ผู้บันทึก
     * @return รหัสการเยี่ยมที่บันทึกใหม่ หรือ -1 ถ้าไม่สำเร็จ
     */
    public long saveNewVisit(String pcucode, String pcucodePerson, String pid, String visitDate,
                             String rightCode, String rightHmain, String rightHsub, String rightNo,
                             String incup, String serviceType, String username) {
        long visitNo = getNewVisitNo(pcucode);
        return saveNewVisitWithNo(visitNo, pcucode, pcucodePerson, pid, visitDate,
                rightCode, rightHmain, rightHsub, rightNo,
                incup, serviceType, username);
    }

    /**
     * บันทึกข้อมูลการเยี่ยมใหม่ (โดยกำหนด visitNo อัตโนมัติ) - เวอร์ชันย่อ
     *
     * @param pcucode รหัส PCU
     * @param pcucodePerson รหัส PCU ของบุคคล
     * @param pid รหัสบุคคล
     * @param visitDate วันที่เยี่ยม
     * @param username ผู้บันทึก
     * @return รหัสการเยี่ยมที่บันทึกใหม่ หรือ -1 ถ้าไม่สำเร็จ
     */
    public long saveNewVisit(String pcucode, String pcucodePerson, String pid, String visitDate, String username) {
        long visitNo = getNewVisitNo(pcucode);
        return saveNewVisitWithNo(visitNo, pcucode, pcucodePerson, pid, visitDate, username);
    }

    /**
     * บันทึกข้อมูลการเยี่ยมใหม่ (ระบุ visitNo)
     *
     * @param visitNo รหัสการเยี่ยม
     * @param pcucode รหัส PCU
     * @param pcucodePerson รหัส PCU ของบุคคล
     * @param pid รหัสบุคคล
     * @param visitDate วันที่เยี่ยม
     * @param rightCode รหัสสิทธิการรักษา
     * @param rightHmain รหัสสถานพยาบาลหลัก
     * @param rightHsub รหัสสถานพยาบาลรอง
     * @param rightNo เลขที่สิทธิการรักษา
     * @param incup ค่าใช้จ่ายในโครงการ
     * @param serviceType ประเภทบริการ
     * @param username ผู้บันทึก
     * @return รหัสการเยี่ยมที่บันทึกใหม่ หรือ -1 ถ้าไม่สำเร็จ
     */
    public long saveNewVisitWithNo(long visitNo, String pcucode, String pcucodePerson, String pid, String visitDate,
                                   String rightCode, String rightHmain, String rightHsub, String rightNo,
                                   String incup, String serviceType, String username) {
        ContentValues values = new ContentValues();
        values.put(Visit.NO, visitNo);
        values.put(Visit.PCUCODE, pcucode);
        values.put(Visit.PCUCODE_PERSON, pcucodePerson);
        values.put(Visit.PID, pid);
        values.put(Visit.DATE, visitDate);
        values.put(Visit.USERNAME, username);
        values.put(Visit.TIME_SERIVICE, DateConverter.getCurrentWesternDateTime());

        // ข้อมูลสิทธิการรักษา
        if (rightCode != null && !rightCode.isEmpty()) {
            values.put(Visit.RIGHT_CODE, rightCode);
        }
        if (rightHmain != null && !rightHmain.isEmpty()) {
            values.put(Visit.RIGHT_HMAIN, rightHmain);
        }
        if (rightHsub != null && !rightHsub.isEmpty()) {
            values.put(Visit.RIGHT_HSUB, rightHsub);
        }
        if (rightNo != null && !rightNo.isEmpty()) {
            values.put(Visit.RIGHT_NO, rightNo);
        }
        if (incup != null && !incup.isEmpty()) {
            values.put(Visit.INCUP, incup);
        }
        if (serviceType != null && !serviceType.isEmpty()) {
            values.put(Visit.SERVICE_TYPE, serviceType);
        }

        Uri uri = mResolver.insert(Visit.CONTENT_URI, values);
        if (uri != null) {
            return Long.parseLong(uri.getLastPathSegment());
        }
        return -1;
    }

    /**
     * บันทึกข้อมูลการเยี่ยมใหม่ (ระบุ visitNo) - เวอร์ชันย่อ
     *
     * @param visitNo รหัสการเยี่ยม
     * @param pcucode รหัส PCU
     * @param pcucodePerson รหัส PCU ของบุคคล
     * @param pid รหัสบุคคล
     * @param visitDate วันที่เยี่ยม
     * @param username ผู้บันทึก
     * @return รหัสการเยี่ยมที่บันทึกใหม่ หรือ -1 ถ้าไม่สำเร็จ
     */
    public long saveNewVisitWithNo(long visitNo, String pcucode, String pcucodePerson, String pid, String visitDate, String username) {
        return saveNewVisitWithNo(visitNo, pcucode, pcucodePerson, pid, visitDate, null, null, null, null, null, null, username);
    }

    /**
     * บันทึกข้อมูลการเยี่ยมใหม่พร้อมสัญญาณชีพ
     *
     * @param pcucode รหัส PCU
     * @param pcucodePerson รหัส PCU ของบุคคล
     * @param pid รหัสบุคคล
     * @param visitDate วันที่เยี่ยม
     * @param weight น้ำหนัก
     * @param height ส่วนสูง
     * @param pressure ความดันโลหิต
     * @param temperature อุณหภูมิ
     * @param pulse ชีพจร

     * @param waist รอบเอว
     * @param symptoms อาการ
     * @param diagnote บันทึกการวินิจฉัย

     * @param username ผู้บันทึก
     * @return รหัสการเยี่ยมที่บันทึกใหม่ หรือ -1 ถ้าไม่สำเร็จ
     */
    public long saveNewVisitWithVitalSigns(String pcucode,
                                           String pcucodePerson,
                                           String pid,
                                           String visitDate,
                                           float weight,
                                           float height,
                                           String pressure,
                                           float temperature,
                                           int pulse,
                                           float waist,
                                           String symptoms,
                                           String diagnote,
                                           String username,
                                           String healthsuggest1,
                                           String rightCode,
                                           String rightNo) {

        // สร้าง Visit ใหม่
        long visitNo = getNewVisitNo(pcucode);

        // สร้าง ContentValues สำหรับการบันทึกข้อมูลพื้นฐาน
        ContentValues basicValues = new ContentValues();
        basicValues.put(Visit.NO, visitNo);
        basicValues.put(Visit.PCUCODE, pcucode);
        basicValues.put(Visit.PCUCODE_PERSON, pcucodePerson);
        basicValues.put(Visit.PID, pid);
        basicValues.put(Visit.DATE, visitDate);
        basicValues.put(Visit.USERNAME, username);
        basicValues.put(Visit.TIME_SERIVICE, 1);


        // บันทึกข้อมูลพื้นฐาน
        Uri uri = mResolver.insert(Visit.CONTENT_URI, basicValues);
        long result = -1;
        if (uri != null) {
            result = Long.parseLong(uri.getLastPathSegment());
        } else {
            return -1; // บันทึกไม่สำเร็จ
        }

        // บันทึกสัญญาณชีพและข้อมูลเพิ่มเติม
        if (result > 0) {
            ContentValues vitalValues = new ContentValues();

            // สัญญาณชีพ
            vitalValues.put(Visit.WEIGHT, weight);
            vitalValues.put(Visit.HEIGHT, height);
            vitalValues.put(Visit.PRESSURE, pressure);
            vitalValues.put(Visit.TEMPERATURE, temperature);
            vitalValues.put(Visit.PULSE, pulse);

            vitalValues.put(Visit.WAIST, waist);
            vitalValues.put(Visit.SYMPTOMS, symptoms);
            vitalValues.put(Visit.SYMPTOMSCO, symptoms);
            vitalValues.put(Visit.DIAGNOTE, diagnote);

            vitalValues.put(Visit.VITALCHECK, symptoms);
            vitalValues.put(Visit.UPDATE, DateConverter.getCurrentWesternDateTime());
            vitalValues.put(Visit.TIME_START, DateConverter.getCurrentTime());
            vitalValues.put(Visit.TIME_END, DateConverter.getCurrentTime());
            vitalValues.put(Visit.HEALTHSUGGEST1, healthsuggest1);
            vitalValues.put(Visit.RIGHT_CODE, rightCode);
            vitalValues.put(Visit.RIGHT_NO, rightNo);


            // คำนวณค่า BMI ถ้ามีข้อมูลน้ำหนักและส่วนสูง
            if (weight > 0 && height > 0) {
                float bmi = weight / ((height/100) * (height/100));
                vitalValues.put(Visit.BMI, calculateBMILevel(bmi));
            }

            // คำนวณระดับความดันโลหิต ถ้ามีข้อมูล
//            if (pressure != null && !pressure.isEmpty()) {
//                String pressureLevel = calculatePressureLevel(pressure);
//                vitalValues.put(Visit.PRESSURE_LEVEL, pressureLevel);
//            }

            // อัพเดทข้อมูลสัญญาณชีพและข้อมูลเพิ่มเติม
            Uri updateUri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(result));
            mResolver.update(updateUri, vitalValues, null, null);
        }

        return result;
    }

    /**
     * อัพเดทข้อมูลการเยี่ยมที่มีอยู่แล้ว
     *
     * @param visitNo รหัสการเยี่ยม
     * @param weight น้ำหนัก
     * @param height ส่วนสูง
     * @param pressure ความดันโลหิต
     * @param temperature อุณหภูมิ
     * @param pulse ชีพจร
//     * @param respiratory การหายใจ
     * @param waist รอบเอว
     * @param symptoms อาการ
//     * @param symptomsco อาการร่วม
     * @param diagnote บันทึกการวินิจฉัย
//     * @param rightCode รหัสสิทธิการรักษา
//     * @param rightHmain รหัสสถานพยาบาลหลัก
//     * @param rightHsub รหัสสถานพยาบาลรอง
//     * @param rightNo เลขที่สิทธิการรักษา
//     * @param incup ค่าใช้จ่ายในโครงการ
//     * @param serviceType ประเภทบริการ
//     * @param flagService สถานะบริการ
     * @return จำนวนแถวที่อัพเดท
     */
    public int updateVisit(long visitNo, float weight, float height, String pressure,
                           float temperature, int pulse,
                           float waist,
                           String symptoms,
                           String diagnote,
                           String healthsuggest1,
                           String rightCode,
                           String rightNo,
                           String username

    ) {

        ContentValues values = new ContentValues();

        // ข้อมูลสัญญาณชีพและอาการ
        values.put(Visit.WEIGHT, weight);
        values.put(Visit.HEIGHT, height);
        values.put(Visit.PRESSURE, pressure);
        values.put(Visit.TEMPERATURE, temperature);
        values.put(Visit.PULSE, pulse);
        values.put(Visit.WAIST, waist);
        values.put(Visit.SYMPTOMS, symptoms);
        values.put(Visit.SYMPTOMSCO, symptoms);
        values.put(Visit.DIAGNOTE, diagnote);
        values.put(Visit.VITALCHECK, symptoms);
        values.put(Visit.TIME_END, DateConverter.getCurrentTime());
        values.put(Visit.HEALTHSUGGEST1, healthsuggest1);
        values.put(Visit.RIGHT_CODE, rightCode);
        values.put(Visit.RIGHT_NO, rightNo);
        values.put(Visit.USERNAME,username);

        // อัพเดทค่า BMI ถ้ามีข้อมูลน้ำหนักและส่วนสูง
        if (weight > 0 && height > 0) {
            float bmi = weight / ((height/100) * (height/100));
            values.put(Visit.BMI, calculateBMILevel(bmi));
        }

        // คำนวณระดับความดันโลหิต ถ้ามีข้อมูล
//        if (pressure != null && !pressure.isEmpty()) {
//            String pressureLevel = calculatePressureLevel(pressure);
//            values.put(Visit.PRESSURE_LEVEL, pressureLevel);
//        }

        // อัพเดทเวลาที่มีการแก้ไข
        values.put(Visit.UPDATE, DateConverter.getCurrentWesternDateTime());

        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.update(uri, values, null, null);
    }

    /**
     * อัพเดทข้อมูลสัญญาณชีพและอาการ (เวอร์ชันย่อ)
     *
     * @param visitNo รหัสการเยี่ยม
     * @param weight น้ำหนัก
     * @param height ส่วนสูง
     * @param pressure ความดันโลหิต
     * @param temperature อุณหภูมิ
     * @param pulse ชีพจร
     * @param waist รอบเอว
     * @param symptoms อาการ
     * @param diagnote บันทึกการวินิจฉัย
     * @return จำนวนแถวที่อัพเดท
     */
//    public int updateVisit(long visitNo, float weight, float height, String pressure,
//                           float temperature, int pulse,
//                           float waist,
//                           String symptoms, String diagnote) {
//
//        return updateVisit(visitNo, weight, height, pressure, temperature, pulse,
//                waist, symptoms, diagnote
//             );
//    }

    /**
     * ลบข้อมูลการเยี่ยม
     *
     * @param visitNo รหัสการเยี่ยม
     * @return จำนวนแถวที่ลบ
     */
    public int deleteVisit(long visitNo) {
        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.delete(uri, null, null);
    }

    /**
     * ดึงข้อมูลการเยี่ยมตามรหัส
     *
     * @param visitNo รหัสการเยี่ยม
     * @return Cursor ที่มีข้อมูลการเยี่ยม
     */
    public Cursor getVisitById(long visitNo) {
        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.query(uri, null, null, null, null);
    }

    /**
     * ดึงข้อมูลการเยี่ยมทั้งหมดของบุคคล
     *
     * @param pid รหัสบุคคล
     * @return Cursor ที่มีข้อมูลการเยี่ยมทั้งหมดของบุคคล
     */
    public Cursor getVisitsByPid(String pid) {
        String selection = Visit.PID + "=?";
        String[] selectionArgs = {pid};
        return mResolver.query(Visit.CONTENT_URI, null, selection, selectionArgs, Visit.DEFAULT_SORTING);
    }

    /**
     * ดึงข้อมูลการเยี่ยมล่าสุดของบุคคล
     *
     * @param pid รหัสบุคคล
     * @return Cursor ที่มีข้อมูลการเยี่ยมล่าสุด
     */
    public Cursor getLatestVisitByPid(String pid) {
        String selection = Visit.PID + "=?";
        String[] selectionArgs = {pid};
        return mResolver.query(Visit.CONTENT_URI, null, selection, selectionArgs, Visit.NO + " DESC LIMIT 1");
    }

    /**
     * ดึงรหัสการเยี่ยมล่าสุดของบุคคล
     *
     * @param pid รหัสบุคคล
     * @return รหัสการเยี่ยมล่าสุด หรือ 0 ถ้าไม่พบข้อมูล
     */
    public long getLatestVisitNoByPid(String pid) {
        Cursor cursor = getLatestVisitByPid(pid);
        long visitNo = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(Visit.NO);
                if (index != -1) {
                    visitNo = cursor.getLong(index);
                }
            }
            cursor.close();
        }

        return visitNo;
    }

    /**
     * บันทึกข้อมูลสัญญาณชีพ
     *
     * @param visitNo รหัสการเยี่ยม
     * @param weight น้ำหนัก
     * @param height ส่วนสูง
     * @param pressure ความดันโลหิต
     * @param temperature อุณหภูมิ
     * @param pulse ชีพจร
     * @param respiratory การหายใจ
     * @param waist รอบเอว
     * @return จำนวนแถวที่อัพเดท
     */
    public int saveVitalSign(long visitNo, float weight, float height, String pressure,
                             float temperature, int pulse, int respiratory, float waist) {

        ContentValues values = new ContentValues();
        values.put(Visit.WEIGHT, weight);
        values.put(Visit.HEIGHT, height);
        values.put(Visit.PRESSURE, pressure);
        values.put(Visit.TEMPERATURE, temperature);
        values.put(Visit.PULSE, pulse);
        values.put(Visit.RESPI, respiratory);
        values.put(Visit.WAIST, waist);
        values.put(Visit.VITAL, "1"); // มีการบันทึกสัญญาณชีพ

        // คำนวณค่า BMI ถ้ามีข้อมูลน้ำหนักและส่วนสูง
        if (weight > 0 && height > 0) {
            float bmi = weight / ((height/100) * (height/100));
            values.put(Visit.BMI, calculateBMILevel(bmi));
        }

        // คำนวณระดับความดันโลหิต ถ้ามีข้อมูล
        if (pressure != null && !pressure.isEmpty()) {
            String pressureLevel = calculatePressureLevel(pressure);
            values.put(Visit.PRESSURE_LEVEL, pressureLevel);
        }

        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.update(uri, values, null, null);
    }

    /**
     * บันทึกอาการและบันทึกวินิจฉัย
     *
     * @param visitNo รหัสการเยี่ยม
     * @param symptoms อาการ
     * @param diagnote บันทึกการวินิจฉัย
     * @return จำนวนแถวที่อัพเดท
     */
    public int saveSymptomsAndDiagnote(long visitNo, String symptoms, String diagnote) {
        ContentValues values = new ContentValues();
        values.put(Visit.SYMPTOMS, symptoms);
        values.put(Visit.DIAGNOTE, diagnote);
        values.put(Visit.UPDATE,  DateConverter.getCurrentWesternDateTime());

        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.update(uri, values, null, null);
    }

    /**
     * บันทึกข้อมูลการส่งต่อผู้ป่วย
     *
     * @param visitNo รหัสการเยี่ยม
     * @param referTo รหัสสถานพยาบาลที่ส่งต่อ
     * @param referBack มีการส่งกลับหรือไม่
     * @return จำนวนแถวที่อัพเดท
     */
    public int saveReferData(long visitNo, String referTo, boolean referBack) {
        ContentValues values = new ContentValues();
        values.put(Visit.REFER_PATIENT, "1"); // มีการส่งต่อผู้ป่วย
        values.put(Visit.REFER_TO_HOS, referTo);
        values.put(Visit.REFER_BACK, referBack ? "1" : "0");
        values.put(Visit.UPDATE, DateConverter.getCurrentWesternDateTime());

        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.update(uri, values, null, null);
    }

    /**
     * บันทึกข้อมูลการรับผู้ป่วย
     *
     * @param visitNo รหัสการเยี่ยม
     * @param receiveFrom รหัสสถานพยาบาลที่ส่งมา
     * @return จำนวนแถวที่อัพเดท
     */
    public int saveReceivePatientData(long visitNo, String receiveFrom) {
        ContentValues values = new ContentValues();
        values.put(Visit.RECEIVE_PATIENT, "1"); // มีการรับผู้ป่วย
        values.put(Visit.RECEIVE_FROM, receiveFrom);
        values.put(Visit.UPDATE,DateConverter.getCurrentWesternDateTime());

        Uri uri = Uri.withAppendedPath(Visit.CONTENT_URI, String.valueOf(visitNo));
        return mResolver.update(uri, values, null, null);
    }

    /**
     * นับจำนวนการเยี่ยมของบุคคล
     *
     * @param pid รหัสบุคคล
     * @return จำนวนการเยี่ยม
     */
    public int countVisitsByPid(String pid) {
        String selection = Visit.PID + "=?";
        String[] selectionArgs = {pid};
        Cursor cursor = mResolver.query(Visit.CONTENT_URI, new String[]{"COUNT(*) AS count"}, selection, selectionArgs, null);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        return count;
    }

    /**
     * ค้นหาการเยี่ยมตามวันที่
     *
     * @param date วันที่ (ในรูปแบบ yyyy-MM-dd)
     * @return Cursor ที่มีข้อมูลการเยี่ยม
     */
    public Cursor searchVisitsByDate(String date) {
        String selection = Visit.DATE + "=?";
        String[] selectionArgs = {date};
        return mResolver.query(Visit.CONTENT_URI, null, selection, selectionArgs, Visit.DEFAULT_SORTING);
    }

    /**
     * ค้นหาการเยี่ยมในช่วงวันที่
     *
     * @param startDate วันที่เริ่มต้น (ในรูปแบบ yyyy-MM-dd)
     * @param endDate วันที่สิ้นสุด (ในรูปแบบ yyyy-MM-dd)
     * @return Cursor ที่มีข้อมูลการเยี่ยม
     */
    public Cursor searchVisitsByDateRange(String startDate, String endDate) {
        String selection = Visit.DATE + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};
        return mResolver.query(Visit.CONTENT_URI, null, selection, selectionArgs, Visit.DEFAULT_SORTING);
    }

    /**
     * คำนวณระดับ BMI
     *
     * @param bmi ค่า BMI
     * @return ระดับ BMI
     */
    private String calculateBMILevel(float bmi) {
        if (bmi < 18.5) {
            return "1"; // ผอม
        } else if (bmi < 23.0) {
            return "2"; // ปกติ
        } else if (bmi < 25.0) {
            return "3"; // ท้วม
        } else if (bmi < 30.0) {
            return "4"; // อ้วน
        } else {
            return "5"; // อ้วนมาก
        }
    }

    /**
     * คำนวณระดับความดันโลหิต
     *
     * @param pressure ความดันโลหิต (format: systolic/diastolic)
     * @return ระดับความดันโลหิต
     */
    private String calculatePressureLevel(String pressure) {
        try {
            String[] parts = pressure.split("/");
            if (parts.length == 2) {
                int systolic = Integer.parseInt(parts[0].trim());
                int diastolic = Integer.parseInt(parts[1].trim());

                if (systolic < 120 && diastolic < 80) {
                    return "1"; // ปกติ
                } else if (systolic < 130 && diastolic < 85) {
                    return "2"; // ปกติสูง
                } else if (systolic < 140 && diastolic < 90) {
                    return "3"; // สูงปานกลาง
                } else if (systolic < 160 && diastolic < 100) {
                    return "4"; // สูง
                } else if (systolic < 180 && diastolic < 110) {
                    return "5"; // สูงมาก
                } else {
                    return "6"; // สูงรุนแรง
                }
            }
        } catch (NumberFormatException e) {
            // ไม่สามารถแปลงข้อมูลได้
        }
        return "0"; // ไม่สามารถคำนวณได้
    }


}