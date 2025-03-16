package th.in.ffc.app.form.nhso.dao;

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

import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.provider.NHSOPatient;

/**
 * Data Access Object สำหรับข้อมูลผู้ป่วย NHSO (แฟ้ม 1)
 */
public class NHSOPatientDao {
    private static final String TAG = "NHSOPatientDao";
    private Context mContext;
    private SimpleDateFormat dateFormat;

    public NHSOPatientDao(Context context) {
        this.mContext = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    /**
     * ดึงข้อมูลผู้ป่วยทั้งหมด
     * @return รายการข้อมูลผู้ป่วย NHSO
     */
    public List<NHSOPatientInfo> getAllPatients() {
        List<NHSOPatientInfo> patients = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatient.CONTENT_URI;

        String[] projection = {
                NHSOPatient.ID,
                NHSOPatient.SEQ,
                NHSOPatient.TYPE,
                NHSOPatient.CID,
                NHSOPatient.PPN,
                NHSOPatient.PWD,
                NHSOPatient.NAME_GIVEN,
                NHSOPatient.NAME_FAMILY,
                NHSOPatient.BIRTHDATE,
                NHSOPatient.GENDER,
                NHSOPatient.ADDRESS_LINE,
                NHSOPatient.ADDRESS_CITY,
                NHSOPatient.ADDRESS_DISTRICT,
                NHSOPatient.ADDRESS_STATE,
                NHSOPatient.ADDRESS_POSTAL_CODE,
                NHSOPatient.NATIONALITY,
                NHSOPatient.RACE,
                NHSOPatient.HN,
                NHSOPatient.AN,
                NHSOPatient.SEND_TO_NHSO
        };

        Cursor cursor = resolver.query(uri, projection, null, null, NHSOPatient.ID + " DESC");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOPatientInfo patient = cursorToPatient(cursor);
                    patients.add(patient);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient data", e);
            } finally {
                cursor.close();
            }
        }

        return patients;
    }

    /**
     * ดึงข้อมูลผู้ป่วยตามรหัสบัตรประชาชน
     * @param cid รหัสบัตรประชาชน
     * @return ข้อมูลผู้ป่วย หรือ null ถ้าไม่พบ
     */
    public NHSOPatientInfo getPatientByCid(String cid) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatient.CONTENT_URI;

        String[] projection = {
                NHSOPatient.ID,
                NHSOPatient.SEQ,
                NHSOPatient.TYPE,
                NHSOPatient.CID,
                NHSOPatient.PPN,
                NHSOPatient.PWD,
                NHSOPatient.NAME_GIVEN,
                NHSOPatient.NAME_FAMILY,
                NHSOPatient.BIRTHDATE,
                NHSOPatient.GENDER,
                NHSOPatient.ADDRESS_LINE,
                NHSOPatient.ADDRESS_CITY,
                NHSOPatient.ADDRESS_DISTRICT,
                NHSOPatient.ADDRESS_STATE,
                NHSOPatient.ADDRESS_POSTAL_CODE,
                NHSOPatient.NATIONALITY,
                NHSOPatient.RACE,
                NHSOPatient.HN,
                NHSOPatient.AN,
                NHSOPatient.SEND_TO_NHSO
        };

        String selection = NHSOPatient.CID + " = ?";
        String[] selectionArgs = {cid};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        NHSOPatientInfo patient = null;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    patient = cursorToPatient(cursor);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient data", e);
            } finally {
                cursor.close();
            }
        }

        return patient;
    }

    /**
     * ดึงข้อมูลผู้ป่วยตาม ID
     * @param id รหัส ID ในฐานข้อมูล
     * @return ข้อมูลผู้ป่วย หรือ null ถ้าไม่พบ
     */
    public NHSOPatientInfo getPatientById(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(NHSOPatient.CONTENT_URI, String.valueOf(id));

        String[] projection = {
                NHSOPatient.ID,
                NHSOPatient.SEQ,
                NHSOPatient.TYPE,
                NHSOPatient.CID,
                NHSOPatient.PPN,
                NHSOPatient.PWD,
                NHSOPatient.NAME_GIVEN,
                NHSOPatient.NAME_FAMILY,
                NHSOPatient.BIRTHDATE,
                NHSOPatient.GENDER,
                NHSOPatient.ADDRESS_LINE,
                NHSOPatient.ADDRESS_CITY,
                NHSOPatient.ADDRESS_DISTRICT,
                NHSOPatient.ADDRESS_STATE,
                NHSOPatient.ADDRESS_POSTAL_CODE,
                NHSOPatient.NATIONALITY,
                NHSOPatient.RACE,
                NHSOPatient.HN,
                NHSOPatient.AN,
                NHSOPatient.SEND_TO_NHSO
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);
        NHSOPatientInfo patient = null;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    patient = cursorToPatient(cursor);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient data", e);
            } finally {
                cursor.close();
            }
        }

        return patient;
    }

    /**
     * บันทึกข้อมูลผู้ป่วยใหม่
     * @param patient ข้อมูลผู้ป่วยที่ต้องการบันทึก
     * @return รหัส ID ที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertPatient(NHSOPatientInfo patient) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = patientToContentValues(patient);

        // เพิ่มข้อมูลวันที่บันทึก
        values.put(NHSOPatient.CREATED_DATE, dateFormat.format(new Date()));

        Uri resultUri = resolver.insert(NHSOPatient.CONTENT_URI, values);
        if (resultUri != null) {
            return Long.parseLong(resultUri.getLastPathSegment());
        } else {
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลผู้ป่วย
     * @param patient ข้อมูลผู้ป่วยที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updatePatient(NHSOPatientInfo patient) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = patientToContentValues(patient);

        // เพิ่มข้อมูลวันที่อัปเดต
        values.put(NHSOPatient.UPDATED_DATE, dateFormat.format(new Date()));

        String selection = NHSOPatient.ID + " = ?";
        String[] selectionArgs = {String.valueOf(patient.getId())};

        return resolver.update(NHSOPatient.CONTENT_URI, values, selection, selectionArgs);
    }

    /**
     * ลบข้อมูลผู้ป่วย
     * @param id รหัส ID ที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deletePatient(long id) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.withAppendedPath(NHSOPatient.CONTENT_URI, String.valueOf(id));

        return resolver.delete(uri, null, null);
    }

    /**
     * อัปเดตสถานะการส่งข้อมูลไป NHSO
     * @param id รหัส ID ที่ต้องการอัปเดต
     * @param sent สถานะการส่ง (true = ส่งแล้ว, false = ยังไม่ส่ง)
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSentStatus(long id, boolean sent) {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(NHSOPatient.SEND_TO_NHSO, sent ? 1 : 0);
        values.put(NHSOPatient.UPDATED_DATE, dateFormat.format(new Date()));

        String selection = NHSOPatient.ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return resolver.update(NHSOPatient.CONTENT_URI, values, selection, selectionArgs);
    }

    /**
     * ดึงข้อมูลผู้ป่วยที่ยังไม่ได้ส่งไป NHSO
     * @return รายการข้อมูลผู้ป่วยที่ยังไม่ได้ส่ง
     */
    public List<NHSOPatientInfo> getUnsentPatients() {
        List<NHSOPatientInfo> patients = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = NHSOPatient.CONTENT_URI;

        String[] projection = {
                NHSOPatient.ID,
                NHSOPatient.SEQ,
                NHSOPatient.TYPE,
                NHSOPatient.CID,
                NHSOPatient.PPN,
                NHSOPatient.PWD,
                NHSOPatient.NAME_GIVEN,
                NHSOPatient.NAME_FAMILY,
                NHSOPatient.BIRTHDATE,
                NHSOPatient.GENDER,
                NHSOPatient.ADDRESS_LINE,
                NHSOPatient.ADDRESS_CITY,
                NHSOPatient.ADDRESS_DISTRICT,
                NHSOPatient.ADDRESS_STATE,
                NHSOPatient.ADDRESS_POSTAL_CODE,
                NHSOPatient.NATIONALITY,
                NHSOPatient.RACE,
                NHSOPatient.HN,
                NHSOPatient.AN,
                NHSOPatient.SEND_TO_NHSO
        };

        String selection = NHSOPatient.SEND_TO_NHSO + " = ?";
        String[] selectionArgs = {"0"};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, NHSOPatient.ID + " DESC");
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOPatientInfo patient = cursorToPatient(cursor);
                    patients.add(patient);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing patient data", e);
            } finally {
                cursor.close();
            }
        }

        return patients;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOPatientInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูลผู้ป่วย NHSO
     */
    private NHSOPatientInfo cursorToPatient(Cursor cursor) {
        NHSOPatientInfo patient = new NHSOPatientInfo();

        patient.setId(cursor.getLong(cursor.getColumnIndexOrThrow(NHSOPatient.ID)));
        patient.setSeq(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatient.SEQ)));
        patient.setType(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatient.TYPE)));
        patient.setCid(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatient.CID)));

        // ตรวจสอบฟิลด์ที่อาจเป็น null
        int ppnIndex = cursor.getColumnIndexOrThrow(NHSOPatient.PPN);
        if (!cursor.isNull(ppnIndex)) {
            patient.setPpn(cursor.getString(ppnIndex));
        }

        int pwdIndex = cursor.getColumnIndexOrThrow(NHSOPatient.PWD);
        if (!cursor.isNull(pwdIndex)) {
            patient.setPwd(cursor.getString(pwdIndex));
        }

        patient.setNameGiven(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatient.NAME_GIVEN)));
        patient.setNameFamily(cursor.getString(cursor.getColumnIndexOrThrow(NHSOPatient.NAME_FAMILY)));

        int birthDateIndex = cursor.getColumnIndexOrThrow(NHSOPatient.BIRTHDATE);
        if (!cursor.isNull(birthDateIndex)) {
            patient.setBirthDate(cursor.getString(birthDateIndex));
        }

        int genderIndex = cursor.getColumnIndexOrThrow(NHSOPatient.GENDER);
        if (!cursor.isNull(genderIndex)) {
            patient.setGender(cursor.getString(genderIndex));
        }

        int addressLineIndex = cursor.getColumnIndexOrThrow(NHSOPatient.ADDRESS_LINE);
        if (!cursor.isNull(addressLineIndex)) {
            patient.setAddressLine(cursor.getString(addressLineIndex));
        }

        int addressCityIndex = cursor.getColumnIndexOrThrow(NHSOPatient.ADDRESS_CITY);
        if (!cursor.isNull(addressCityIndex)) {
            patient.setAddressCity(cursor.getString(addressCityIndex));
        }

        int addressDistrictIndex = cursor.getColumnIndexOrThrow(NHSOPatient.ADDRESS_DISTRICT);
        if (!cursor.isNull(addressDistrictIndex)) {
            patient.setAddressDistrict(cursor.getString(addressDistrictIndex));
        }

        int addressStateIndex = cursor.getColumnIndexOrThrow(NHSOPatient.ADDRESS_STATE);
        if (!cursor.isNull(addressStateIndex)) {
            patient.setAddressState(cursor.getString(addressStateIndex));
        }

        int addressPostalCodeIndex = cursor.getColumnIndexOrThrow(NHSOPatient.ADDRESS_POSTAL_CODE);
        if (!cursor.isNull(addressPostalCodeIndex)) {
            patient.setAddressPostalCode(cursor.getString(addressPostalCodeIndex));
        }

        int nationalityIndex = cursor.getColumnIndexOrThrow(NHSOPatient.NATIONALITY);
        if (!cursor.isNull(nationalityIndex)) {
            patient.setNationality(cursor.getString(nationalityIndex));
        }

        int raceIndex = cursor.getColumnIndexOrThrow(NHSOPatient.RACE);
        if (!cursor.isNull(raceIndex)) {
            patient.setRace(cursor.getString(raceIndex));
        }

        int hnIndex = cursor.getColumnIndexOrThrow(NHSOPatient.HN);
        if (!cursor.isNull(hnIndex)) {
            patient.setHn(cursor.getString(hnIndex));
        }

        int anIndex = cursor.getColumnIndexOrThrow(NHSOPatient.AN);
        if (!cursor.isNull(anIndex)) {
            patient.setAn(cursor.getString(anIndex));
        }

        patient.setSentToNHSO(cursor.getInt(cursor.getColumnIndexOrThrow(NHSOPatient.SEND_TO_NHSO)) == 1);

        return patient;
    }

    /**
     * แปลงข้อมูลจาก NHSOPatientInfo เป็น ContentValues
     * @param patient ข้อมูลผู้ป่วย NHSO
     * @return ContentValues สำหรับบันทึกลงฐานข้อมูล
     */
    private ContentValues patientToContentValues(NHSOPatientInfo patient) {
        ContentValues values = new ContentValues();

        values.put(NHSOPatient.SEQ, patient.getSeq());
        values.put(NHSOPatient.TYPE, patient.getType());
        values.put(NHSOPatient.CID, patient.getCid());

        if (patient.getPpn() != null) {
            values.put(NHSOPatient.PPN, patient.getPpn());
        }

        if (patient.getPwd() != null) {
            values.put(NHSOPatient.PWD, patient.getPwd());
        }

        values.put(NHSOPatient.NAME_GIVEN, patient.getNameGiven());
        values.put(NHSOPatient.NAME_FAMILY, patient.getNameFamily());

        if (patient.getBirthDate() != null) {
            values.put(NHSOPatient.BIRTHDATE, patient.getBirthDate());
        }

        if (patient.getGender() != null) {
            values.put(NHSOPatient.GENDER, patient.getGender());
        }

        if (patient.getAddressLine() != null) {
            values.put(NHSOPatient.ADDRESS_LINE, patient.getAddressLine());
        }

        if (patient.getAddressCity() != null) {
            values.put(NHSOPatient.ADDRESS_CITY, patient.getAddressCity());
        }

        if (patient.getAddressDistrict() != null) {
            values.put(NHSOPatient.ADDRESS_DISTRICT, patient.getAddressDistrict());
        }

        if (patient.getAddressState() != null) {
            values.put(NHSOPatient.ADDRESS_STATE, patient.getAddressState());
        }

        if (patient.getAddressPostalCode() != null) {
            values.put(NHSOPatient.ADDRESS_POSTAL_CODE, patient.getAddressPostalCode());
        }

        if (patient.getNationality() != null) {
            values.put(NHSOPatient.NATIONALITY, patient.getNationality());
        }

        if (patient.getRace() != null) {
            values.put(NHSOPatient.RACE, patient.getRace());
        }

        if (patient.getHn() != null) {
            values.put(NHSOPatient.HN, patient.getHn());
        }

        if (patient.getAn() != null) {
            values.put(NHSOPatient.AN, patient.getAn());
        }

        values.put(NHSOPatient.SEND_TO_NHSO, patient.isSentToNHSO() ? 1 : 0);

        return values;
    }
}