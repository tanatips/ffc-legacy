package th.in.ffc.app.form.screening.dao;

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

import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.provider.CounselingSignature;

public class CounselingSignatureDao {
    private static final String TAG = "CounselingSignatureDao";
    private Context context;

    public CounselingSignatureDao(Context context) {
        this.context = context;
    }

    public long saveCounseling(CounselingInfo counseling) {
        Log.d(TAG, "=== Starting saveCounseling ===");

        try {
            ContentValues values = new ContentValues();

            // ข้อมูลพื้นฐาน
            values.put(CounselingSignature.VISIT_ID, counseling.getVisitId());
            values.put(CounselingSignature.PERSON_ID, counseling.getPersonId());
            values.put(CounselingSignature.COUNSELING_TYPE, counseling.getCounselingType());
            values.put(CounselingSignature.DETAIL, counseling.getDetail());
            values.put(CounselingSignature.CREATED_BY, counseling.getCreatedBy());
            values.put(CounselingSignature.CREATED_DATE, getCurrentDateTime());
            values.put(CounselingSignature.PCUCODE, counseling.getPcuCode());
            values.put(CounselingSignature.UPDATE_STATUS, "N");

            // ข้อมูลลายเซ็น - ตรวจสอบอย่างละเอียด
            byte[] patientSignature = counseling.getPatientSignature();
            byte[] providerSignature = counseling.getProviderSignature();

            Log.d(TAG, "Patient signature: " + (patientSignature != null ? patientSignature.length + " bytes" : "null"));
            Log.d(TAG, "Provider signature: " + (providerSignature != null ? providerSignature.length + " bytes" : "null"));

            if (patientSignature != null && patientSignature.length > 0) {
                values.put(CounselingSignature.PATIENT_SIGNATURE, patientSignature);
                Log.d(TAG, "Added patient signature to ContentValues");
            } else {
                values.putNull(CounselingSignature.PATIENT_SIGNATURE);
                Log.w(TAG, "Patient signature is null or empty");
            }

            if (providerSignature != null && providerSignature.length > 0) {
                values.put(CounselingSignature.PROVIDER_SIGNATURE, providerSignature);
                Log.d(TAG, "Added provider signature to ContentValues");
            } else {
                values.putNull(CounselingSignature.PROVIDER_SIGNATURE);
                Log.w(TAG, "Provider signature is null or empty");
            }

            // Debug: แสดงข้อมูลทั้งหมดใน ContentValues
            Log.d(TAG, "ContentValues content:");
            for (String key : values.keySet()) {
                Object value = values.get(key);
                if (value instanceof byte[]) {
                    Log.d(TAG, "  " + key + ": byte[" + ((byte[])value).length + "]");
                } else {
                    Log.d(TAG, "  " + key + ": " + value);
                }
            }

            // บันทึกลง Database
            Uri uri = context.getContentResolver().insert(CounselingSignature.CONTENT_URI, values);

            if (uri != null) {
                long id = Long.parseLong(uri.getLastPathSegment());
                Log.d(TAG, "Insert successful, ID: " + id + ", URI: " + uri);

                // ตรวจสอบข้อมูลที่บันทึกแล้ว
                verifyInsertedData(id);

                return id;
            } else {
                Log.e(TAG, "Insert failed - URI is null");
                return -1;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving counseling", e);
            return -1;
        } finally {
            Log.d(TAG, "=== End saveCounseling ===");
        }
    }

    public int updateCounseling(CounselingInfo counseling) {
        Log.d(TAG, "=== Starting updateCounseling ===");
        Log.d(TAG, "Updating counseling ID: " + counseling.getId());

        try {
            ContentValues values = new ContentValues();

            // ข้อมูลพื้นฐาน
            values.put(CounselingSignature.COUNSELING_TYPE, counseling.getCounselingType());
            values.put(CounselingSignature.DETAIL, counseling.getDetail());
            values.put(CounselingSignature.UPDATED_BY, counseling.getUpdatedBy());
            values.put(CounselingSignature.UPDATED_DATE, getCurrentDateTime());
            values.put(CounselingSignature.UPDATE_STATUS, "U");

            // ข้อมูลลายเซ็น - ตรวจสอบอย่างละเอียด
            byte[] patientSignature = counseling.getPatientSignature();
            byte[] providerSignature = counseling.getProviderSignature();

            Log.d(TAG, "Patient signature: " + (patientSignature != null ? patientSignature.length + " bytes" : "null"));
            Log.d(TAG, "Provider signature: " + (providerSignature != null ? providerSignature.length + " bytes" : "null"));

            if (patientSignature != null && patientSignature.length > 0) {
                values.put(CounselingSignature.PATIENT_SIGNATURE, patientSignature);
                Log.d(TAG, "Updated patient signature in ContentValues");
            } else {
                // อย่าใส่ null ถ้าไม่มีการเปลี่ยนแปลง
                Log.d(TAG, "Patient signature not updated (null or empty)");
            }

            if (providerSignature != null && providerSignature.length > 0) {
                values.put(CounselingSignature.PROVIDER_SIGNATURE, providerSignature);
                Log.d(TAG, "Updated provider signature in ContentValues");
            } else {
                // อย่าใส่ null ถ้าไม่มีการเปลี่ยนแปลง
                Log.d(TAG, "Provider signature not updated (null or empty)");
            }

            // Debug: แสดงข้อมูลที่จะอัพเดท
            Log.d(TAG, "Update ContentValues content:");
            for (String key : values.keySet()) {
                Object value = values.get(key);
                if (value instanceof byte[]) {
                    Log.d(TAG, "  " + key + ": byte[" + ((byte[])value).length + "]");
                } else {
                    Log.d(TAG, "  " + key + ": " + value);
                }
            }

            // อัพเดทใน Database
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, String.valueOf(counseling.getId()));
            int rowsUpdated = context.getContentResolver().update(uri, values, null, null);

            Log.d(TAG, "Update result: " + rowsUpdated + " rows affected");

            if (rowsUpdated > 0) {
                // ตรวจสอบข้อมูลที่อัพเดทแล้ว
                verifyUpdatedData(counseling.getId());
            }

            return rowsUpdated;

        } catch (Exception e) {
            Log.e(TAG, "Error updating counseling", e);
            return 0;
        } finally {
            Log.d(TAG, "=== End updateCounseling ===");
        }
    }

    public List<CounselingInfo> getCounselingByVisitId(String visitId) {
        Log.d(TAG, "Getting counseling by visitId: " + visitId);

        List<CounselingInfo> counselingList = new ArrayList<>();
        Cursor cursor = null;

        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "visit/" + visitId);
            cursor = context.getContentResolver().query(uri, null, null, null,
                    CounselingSignature.ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CounselingInfo counseling = createCounselingFromCursor(cursor);
                    counselingList.add(counseling);

                    // Debug: แสดงข้อมูลที่โหลดได้
                    Log.d(TAG, "Loaded counseling ID: " + counseling.getId());
                    Log.d(TAG, "  Patient signature: " +
                            (counseling.getPatientSignature() != null ? counseling.getPatientSignature().length + " bytes" : "null"));
                    Log.d(TAG, "  Provider signature: " +
                            (counseling.getProviderSignature() != null ? counseling.getProviderSignature().length + " bytes" : "null"));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting counseling by visitId", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "Found " + counselingList.size() + " counseling records for visitId: " + visitId);
        return counselingList;
    }

    public List<CounselingInfo> getCounselingByPersonId(String personId) {
        Log.d(TAG, "Getting counseling by personId: " + personId);

        List<CounselingInfo> counselingList = new ArrayList<>();
        Cursor cursor = null;

        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "person/" + personId);
            cursor = context.getContentResolver().query(uri, null, null, null,
                    CounselingSignature.ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    CounselingInfo counseling = createCounselingFromCursor(cursor);
                    counselingList.add(counseling);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting counseling by personId", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "Found " + counselingList.size() + " counseling records for personId: " + personId);
        return counselingList;
    }

    private CounselingInfo createCounselingFromCursor(Cursor cursor) {
        CounselingInfo counseling = new CounselingInfo();

        // ข้อมูลพื้นฐาน
        counseling.setId(cursor.getLong(cursor.getColumnIndex(CounselingSignature.ID)));
        counseling.setVisitId(cursor.getString(cursor.getColumnIndex(CounselingSignature.VISIT_ID)));
        counseling.setPersonId(cursor.getString(cursor.getColumnIndex(CounselingSignature.PERSON_ID)));
        counseling.setCounselingType(cursor.getInt(cursor.getColumnIndex(CounselingSignature.COUNSELING_TYPE)));
        counseling.setDetail(cursor.getString(cursor.getColumnIndex(CounselingSignature.DETAIL)));
        counseling.setCreatedBy(cursor.getString(cursor.getColumnIndex(CounselingSignature.CREATED_BY)));
        counseling.setCreatedDate(cursor.getString(cursor.getColumnIndex(CounselingSignature.CREATED_DATE)));
        counseling.setUpdatedBy(cursor.getString(cursor.getColumnIndex(CounselingSignature.UPDATED_BY)));
        counseling.setUpdatedDate(cursor.getString(cursor.getColumnIndex(CounselingSignature.UPDATED_DATE)));
        counseling.setPcuCode(cursor.getString(cursor.getColumnIndex(CounselingSignature.PCUCODE)));
        counseling.setUpdateStatus(cursor.getString(cursor.getColumnIndex(CounselingSignature.UPDATE_STATUS)));

        // ข้อมูลลายเซ็น
        try {
            int patientSigIndex = cursor.getColumnIndex(CounselingSignature.PATIENT_SIGNATURE);
            int providerSigIndex = cursor.getColumnIndex(CounselingSignature.PROVIDER_SIGNATURE);

            if (patientSigIndex >= 0 && !cursor.isNull(patientSigIndex)) {
                byte[] patientSignature = cursor.getBlob(patientSigIndex);
                counseling.setPatientSignature(patientSignature);
                Log.d(TAG, "Loaded patient signature: " +
                        (patientSignature != null ? patientSignature.length + " bytes" : "null"));
            }

            if (providerSigIndex >= 0 && !cursor.isNull(providerSigIndex)) {
                byte[] providerSignature = cursor.getBlob(providerSigIndex);
                counseling.setProviderSignature(providerSignature);
                Log.d(TAG, "Loaded provider signature: " +
                        (providerSignature != null ? providerSignature.length + " bytes" : "null"));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading signature data from cursor", e);
        }

        return counseling;
    }

    private void verifyInsertedData(long id) {
        Log.d(TAG, "Verifying inserted data for ID: " + id);

        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, String.valueOf(id));
            cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int patientSigIndex = cursor.getColumnIndex(CounselingSignature.PATIENT_SIGNATURE);
                int providerSigIndex = cursor.getColumnIndex(CounselingSignature.PROVIDER_SIGNATURE);

                if (patientSigIndex >= 0) {
                    byte[] patientSig = cursor.getBlob(patientSigIndex);
                    Log.d(TAG, "Verification - Patient signature: " +
                            (patientSig != null ? patientSig.length + " bytes" : "null"));
                }

                if (providerSigIndex >= 0) {
                    byte[] providerSig = cursor.getBlob(providerSigIndex);
                    Log.d(TAG, "Verification - Provider signature: " +
                            (providerSig != null ? providerSig.length + " bytes" : "null"));
                }

                Log.d(TAG, "Data verification successful");
            } else {
                Log.e(TAG, "Verification failed - No record found");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error verifying inserted data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void verifyUpdatedData(long id) {
        Log.d(TAG, "Verifying updated data for ID: " + id);
        verifyInsertedData(id); // ใช้ method เดียวกัน
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // เพิ่ม method สำหรับ debug
    public void debugAllRecords() {
        Log.d(TAG, "=== DEBUG ALL RECORDS ===");

        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "list");
            cursor = context.getContentResolver().query(uri, null, null, null,
                    CounselingSignature.ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "Found " + cursor.getCount() + " total records");

                do {
                    long id = cursor.getLong(cursor.getColumnIndex(CounselingSignature.ID));
                    String visitId = cursor.getString(cursor.getColumnIndex(CounselingSignature.VISIT_ID));
                    String personId = cursor.getString(cursor.getColumnIndex(CounselingSignature.PERSON_ID));

                    byte[] patientSig = cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PATIENT_SIGNATURE));
                    byte[] providerSig = cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PROVIDER_SIGNATURE));

                    Log.d(TAG, "Record ID: " + id + ", VisitID: " + visitId + ", PersonID: " + personId);
                    Log.d(TAG, "  Patient sig: " + (patientSig != null ? patientSig.length + " bytes" : "null"));
                    Log.d(TAG, "  Provider sig: " + (providerSig != null ? providerSig.length + " bytes" : "null"));

                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No records found");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error debugging all records", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "=== END DEBUG ALL RECORDS ===");
    }
    // เพิ่ม method สำหรับลบข้อมูลตาม ID
    public int deleteCounselingById(long id) {
        Log.d(TAG, "=== Starting deleteCounselingById ===");
        Log.d(TAG, "Deleting counseling ID: " + id);

        try {
            // ตรวจสอบข้อมูลก่อนลบ
            verifyExistingData(id);

            // สร้าง URI สำหรับลบ
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, String.valueOf(id));

            // ลบข้อมูล
            int rowsDeleted = context.getContentResolver().delete(uri, null, null);

            Log.d(TAG, "Delete result: " + rowsDeleted + " rows deleted");

            if (rowsDeleted > 0) {
                Log.d(TAG, "Successfully deleted counseling with ID: " + id);

                // ตรวจสอบว่าลบจริงหรือไม่
                verifyDeletion(id);
            } else {
                Log.w(TAG, "No rows deleted - record may not exist");
            }

            return rowsDeleted;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting counseling by ID", e);
            return 0;
        } finally {
            Log.d(TAG, "=== End deleteCounselingById ===");
        }
    }

    // เพิ่ม method สำหรับลบข้อมูลตาม Visit ID
    public int deleteCounselingByVisitId(String visitId) {
        Log.d(TAG, "=== Starting deleteCounselingByVisitId ===");
        Log.d(TAG, "Deleting counseling for visitId: " + visitId);

        try {
            // สร้าง URI สำหรับลบ
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "visit/" + visitId);

            // ลบข้อมูล
            int rowsDeleted = context.getContentResolver().delete(uri, null, null);

            Log.d(TAG, "Delete result: " + rowsDeleted + " rows deleted for visitId: " + visitId);

            return rowsDeleted;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting counseling by visitId", e);
            return 0;
        } finally {
            Log.d(TAG, "=== End deleteCounselingByVisitId ===");
        }
    }

    // เพิ่ม method สำหรับลบข้อมูลตาม Person ID
    public int deleteCounselingByPersonId(String personId) {
        Log.d(TAG, "=== Starting deleteCounselingByPersonId ===");
        Log.d(TAG, "Deleting counseling for personId: " + personId);

        try {
            // สร้าง URI สำหรับลบ
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "person/" + personId);

            // ลบข้อมูล
            int rowsDeleted = context.getContentResolver().delete(uri, null, null);

            Log.d(TAG, "Delete result: " + rowsDeleted + " rows deleted for personId: " + personId);

            return rowsDeleted;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting counseling by personId", e);
            return 0;
        } finally {
            Log.d(TAG, "=== End deleteCounselingByPersonId ===");
        }
    }

    // เพิ่ม method สำหรับลบข้อมูลทั้งหมด (ใช้ระวัง!)
    public int deleteAllCounseling() {
        Log.d(TAG, "=== Starting deleteAllCounseling ===");
        Log.w(TAG, "WARNING: Deleting ALL counseling records!");

        try {
            // สร้าง URI สำหรับลบทั้งหมด
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "list");

            // ลบข้อมูลทั้งหมด
            int rowsDeleted = context.getContentResolver().delete(uri, null, null);

            Log.d(TAG, "Delete ALL result: " + rowsDeleted + " total rows deleted");

            return rowsDeleted;

        } catch (Exception e) {
            Log.e(TAG, "Error deleting all counseling records", e);
            return 0;
        } finally {
            Log.d(TAG, "=== End deleteAllCounseling ===");
        }
    }
    private void verifyExistingData(long id) {
        Log.d(TAG, "Verifying existing data for ID: " + id);

        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, String.valueOf(id));
            cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String visitId = cursor.getString(cursor.getColumnIndex(CounselingSignature.VISIT_ID));
                String personId = cursor.getString(cursor.getColumnIndex(CounselingSignature.PERSON_ID));
                int counselingType = cursor.getInt(cursor.getColumnIndex(CounselingSignature.COUNSELING_TYPE));

                Log.d(TAG, "Record exists - ID: " + id + ", VisitID: " + visitId +
                        ", PersonID: " + personId + ", Type: " + counselingType);

                // ตรวจสอบลายเซ็น
                byte[] patientSig = cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PATIENT_SIGNATURE));
                byte[] providerSig = cursor.getBlob(cursor.getColumnIndex(CounselingSignature.PROVIDER_SIGNATURE));

                Log.d(TAG, "Signatures - Patient: " + (patientSig != null ? patientSig.length + " bytes" : "null") +
                        ", Provider: " + (providerSig != null ? providerSig.length + " bytes" : "null"));

            } else {
                Log.w(TAG, "Record with ID " + id + " does not exist");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error verifying existing data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // เพิ่ม method ตรวจสอบว่าลบสำเร็จหรือไม่
    private void verifyDeletion(long id) {
        Log.d(TAG, "Verifying deletion for ID: " + id);

        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, String.valueOf(id));
            cursor = context.getContentResolver().query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Log.e(TAG, "ERROR: Record with ID " + id + " still exists after deletion!");
            } else {
                Log.d(TAG, "Deletion verified - Record with ID " + id + " no longer exists");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error verifying deletion", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    // เพิ่มใน CounselingSignatureDao.java

    /**
     * บันทึกหรืออัพเดทข้อมูล Counseling โดยตรวจสอบ visitId ก่อน
     * ถ้า visitId มีอยู่แล้วจะทำการ update โดยใช้ id เป็น key
     * ถ้า visitId ยังไม่มีจะทำการ insert ใหม่
     */
    public long saveOrUpdateCounseling(CounselingInfo counseling) {
        Log.d(TAG, "=== Starting saveOrUpdateCounseling ===");
        Log.d(TAG, "VisitId: " + counseling.getVisitId());

        try {
            // ตรวจสอบว่า visitId มีอยู่ในฐานข้อมูลแล้วหรือไม่
            CounselingInfo existingCounseling = getCounselingByVisitIdSingle(counseling.getVisitId());

            if (existingCounseling != null) {
                // มีข้อมูลอยู่แล้ว -> Update
                Log.d(TAG, "Found existing record with ID: " + existingCounseling.getId());
                Log.d(TAG, "Performing UPDATE operation");

                // คัดลอกข้อมูลใหม่ไปยัง existing record แต่เก็บ ID เดิมไว้
                existingCounseling.setCounselingType(counseling.getCounselingType());
                existingCounseling.setDetail(counseling.getDetail());
                existingCounseling.setPatientSignature(counseling.getPatientSignature());
                existingCounseling.setProviderSignature(counseling.getProviderSignature());
                existingCounseling.setUpdatedBy(counseling.getCreatedBy()); // ใช้ createdBy เป็น updatedBy
                existingCounseling.setPcuCode(counseling.getPcuCode());

                // เรียก updateCounseling
                int rowsUpdated = updateCounseling(existingCounseling);

                if (rowsUpdated > 0) {
                    Log.d(TAG, "Update successful for visitId: " + counseling.getVisitId());
                    return existingCounseling.getId(); // คืนค่า ID ของ record ที่ update
                } else {
                    Log.e(TAG, "Update failed for visitId: " + counseling.getVisitId());
                    return -1;
                }

            } else {
                // ไม่มีข้อมูล -> Insert
                Log.d(TAG, "No existing record found for visitId: " + counseling.getVisitId());
                Log.d(TAG, "Performing INSERT operation");

                // เรียก saveCounseling
                long newId = saveCounseling(counseling);

                if (newId > 0) {
                    Log.d(TAG, "Insert successful for visitId: " + counseling.getVisitId() + ", new ID: " + newId);
                    return newId;
                } else {
                    Log.e(TAG, "Insert failed for visitId: " + counseling.getVisitId());
                    return -1;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in saveOrUpdateCounseling", e);
            return -1;
        } finally {
            Log.d(TAG, "=== End saveOrUpdateCounseling ===");
        }
    }

    /**
     * ดึงข้อมูล Counseling โดย visitId (คืนค่าเพียง record เดียว)
     */
    private CounselingInfo getCounselingByVisitIdSingle(String visitId) {
        Log.d(TAG, "Getting single counseling by visitId: " + visitId);

        if (visitId == null || visitId.isEmpty()) {
            Log.w(TAG, "VisitId is null or empty");
            return null;
        }

        List<CounselingInfo> counselingList = getCounselingByVisitId(visitId);

        if (counselingList != null && !counselingList.isEmpty()) {
            CounselingInfo result = counselingList.get(0); // เอา record แรก
            Log.d(TAG, "Found existing counseling with ID: " + result.getId());
            return result;
        } else {
            Log.d(TAG, "No counseling found for visitId: " + visitId);
            return null;
        }
    }

    /**
     * ตรวจสอบว่า visitId มีอยู่ในฐานข้อมูลหรือไม่
     */
    public boolean isVisitIdExists(String visitId) {
        Log.d(TAG, "Checking if visitId exists: " + visitId);

        if (visitId == null || visitId.isEmpty()) {
            Log.w(TAG, "VisitId is null or empty");
            return false;
        }

        CounselingInfo existing = getCounselingByVisitIdSingle(visitId);
        boolean exists = (existing != null);

        Log.d(TAG, "VisitId " + visitId + " exists: " + exists);
        return exists;
    }

    /**
     * ดึงข้อมูล Counseling พร้อมข้อมูลทั้งหมดรวมถึงลายเซ็น โดย visitId
     */
    public CounselingInfo getCounselingWithSignaturesByVisitId(String visitId) {
        Log.d(TAG, "=== Getting counseling with signatures by visitId: " + visitId + " ===");

        if (visitId == null || visitId.isEmpty()) {
            Log.w(TAG, "VisitId is null or empty");
            return null;
        }

        Cursor cursor = null;
        CounselingInfo counseling = null;

        try {
            Uri uri = Uri.withAppendedPath(CounselingSignature.CONTENT_URI, "visit/" + visitId);
            cursor = context.getContentResolver().query(uri, null, null, null,
                    CounselingSignature.ID + " DESC LIMIT 1"); // เอาเฉพาะ record ล่าสุด

            if (cursor != null && cursor.moveToFirst()) {
                counseling = createCounselingFromCursor(cursor);

                // Debug: แสดงข้อมูลที่โหลดได้
                Log.d(TAG, "Loaded counseling ID: " + counseling.getId());
                Log.d(TAG, "  VisitId: " + counseling.getVisitId());
                Log.d(TAG, "  PersonId: " + counseling.getPersonId());
                Log.d(TAG, "  CounselingType: " + counseling.getCounselingType());
                Log.d(TAG, "  Detail: " + counseling.getDetail());
                Log.d(TAG, "  Patient signature: " +
                        (counseling.getPatientSignature() != null ?
                                counseling.getPatientSignature().length + " bytes" : "null"));
                Log.d(TAG, "  Provider signature: " +
                        (counseling.getProviderSignature() != null ?
                                counseling.getProviderSignature().length + " bytes" : "null"));
            } else {
                Log.d(TAG, "No counseling found for visitId: " + visitId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting counseling with signatures by visitId", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "=== End getting counseling with signatures ===");
        return counseling;
    }
}