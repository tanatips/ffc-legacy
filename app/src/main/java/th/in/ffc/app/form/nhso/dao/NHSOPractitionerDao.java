package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.provider.NHSOPractitioner;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;

/**
 * Data Access Object สำหรับจัดการข้อมูลผู้ให้บริการ NHSO
 */
public class NHSOPractitionerDao {
    private static final String TAG = "NHSOPractitionerDao";

    private ContentResolver mResolver;

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOPractitionerDao(Context context) {
        this.mResolver = context.getContentResolver();
    }

    /**
     * บันทึกข้อมูลผู้ให้บริการใหม่
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการบันทึก
     * @return ID ของข้อมูลผู้ให้บริการที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertPractitioner(NHSOPractitionerInfo practitioner) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOPractitioner.SEQ, practitioner.getSeq());
            values.put(NHSOPractitioner.HCODE, practitioner.getHcode());
            values.put(NHSOPractitioner.CID, practitioner.getCid());

            if (practitioner.getProfessionId() != null) {
                values.put(NHSOPractitioner.PROFESSION_ID, practitioner.getProfessionId());
            }

            if (practitioner.getCouncil() != null) {
                values.put(NHSOPractitioner.COUNCIL, practitioner.getCouncil());
            }

            if (practitioner.getProviderType() != null) {
                values.put(NHSOPractitioner.PROVIDERTYPE, practitioner.getProviderType());
            }

            if (practitioner.getNameGiven() != null) {
                values.put(NHSOPractitioner.NAME_GIVEN, practitioner.getNameGiven());
            }

            if (practitioner.getNameFamily() != null) {
                values.put(NHSOPractitioner.NAME_FAMILY, practitioner.getNameFamily());
            }

            values.put(NHSOPractitioner.USER_CREATE, practitioner.getCreatedBy());
            values.put(NHSOPractitioner.SYNC_STATUS, practitioner.isSyncStatus() ? 1 : 0);

            Uri uri = mResolver.insert(NHSOPractitioner.CONTENT_URI, values);
            if (uri != null) {
                return ContentUris.parseId(uri);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting practitioner", e);
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลผู้ให้บริการ
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updatePractitioner(NHSOPractitionerInfo practitioner) {
        try {
            ContentValues values = new ContentValues();

            if (practitioner.getHcode() != null) {
                values.put(NHSOPractitioner.HCODE, practitioner.getHcode());
            }

            if (practitioner.getProfessionId() != null) {
                values.put(NHSOPractitioner.PROFESSION_ID, practitioner.getProfessionId());
            }

            if (practitioner.getCouncil() != null) {
                values.put(NHSOPractitioner.COUNCIL, practitioner.getCouncil());
            }

            if (practitioner.getProviderType() != null) {
                values.put(NHSOPractitioner.PROVIDERTYPE, practitioner.getProviderType());
            }

            if (practitioner.getNameGiven() != null) {
                values.put(NHSOPractitioner.NAME_GIVEN, practitioner.getNameGiven());
            }

            if (practitioner.getNameFamily() != null) {
                values.put(NHSOPractitioner.NAME_FAMILY, practitioner.getNameFamily());
            }

            values.put(NHSOPractitioner.USER_UPDATE, practitioner.getUpdatedBy());
            values.put(NHSOPractitioner.UPDATETIME, practitioner.getUpdatedDate());

            Uri uri = ContentUris.withAppendedId(NHSOPractitioner.CONTENT_URI, practitioner.getId());
            return mResolver.update(uri, values, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error updating practitioner", e);
            return 0;
        }
    }

    /**
     * อัปเดตสถานะการซิงค์ข้อมูล
     * @param practitionerId ID ของผู้ให้บริการ
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSyncStatus(long practitionerId, boolean status) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOPractitioner.SYNC_STATUS, status ? 1 : 0);

            Uri uri = ContentUris.withAppendedId(NHSOPractitioner.CONTENT_URI, practitionerId);
            return mResolver.update(uri, values, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลผู้ให้บริการ
     * @param practitionerId ID ของผู้ให้บริการที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deletePractitioner(long practitionerId) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOPractitioner.CONTENT_URI, practitionerId);
            return mResolver.delete(uri, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting practitioner", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลผู้ให้บริการตาม ID
     * @param practitionerId ID ของผู้ให้บริการ
     * @return ข้อมูลผู้ให้บริการ หรือ null ถ้าไม่พบ
     */
    public NHSOPractitionerInfo getPractitionerById(long practitionerId) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOPractitioner.CONTENT_URI, practitionerId);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                cursor.close();
                return practitioner;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting practitioner by ID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลผู้ให้บริการตามเลขบัตรประชาชน
     * @param cid เลขบัตรประชาชน
     * @return ข้อมูลผู้ให้บริการ หรือ null ถ้าไม่พบ
     */
    public NHSOPractitionerInfo getPractitionerByCid(String cid) {
        try {
            String selection = NHSOPractitioner.CID + "=?";
            String[] selectionArgs = {cid};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                cursor.close();
                return practitioner;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting practitioner by CID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลผู้ให้บริการตามรหัสสถานพยาบาล
     * @param hcode รหัสสถานพยาบาล
     * @return รายการข้อมูลผู้ให้บริการ
     */
    public List<NHSOPractitionerInfo> getPractitionersByHcode(String hcode) {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            String selection = NHSOPractitioner.HCODE + "=?";
            String[] selectionArgs = {hcode};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting practitioners by HCODE", e);
        }

        return practitioners;
    }

    /**
     * ดึงข้อมูลผู้ให้บริการทั้งหมด
     * @return รายการข้อมูลผู้ให้บริการทั้งหมด
     */
    public List<NHSOPractitionerInfo> getAllPractitioners() {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all practitioners", e);
        }

        return practitioners;
    }

    /**
     * ค้นหาผู้ให้บริการตามชื่อ
     * @param name ชื่อหรือส่วนหนึ่งของชื่อผู้ให้บริการ
     * @return รายการข้อมูลผู้ให้บริการที่ตรงกับเงื่อนไข
     */
    public List<NHSOPractitionerInfo> findPractitionersByName(String name) {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            String selection = NHSOPractitioner.NAME_GIVEN + " LIKE ? OR " + NHSOPractitioner.NAME_FAMILY + " LIKE ?";
            String[] selectionArgs = {"%" + name + "%", "%" + name + "%"};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding practitioners by name", e);
        }

        return practitioners;
    }

    /**
     * ค้นหาผู้ให้บริการตามประเภทบุคลากร
     * @param providerType รหัสประเภทบุคลากร
     * @return รายการข้อมูลผู้ให้บริการที่ตรงกับเงื่อนไข
     */
    public List<NHSOPractitionerInfo> findPractitionersByType(String providerType) {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            String selection = NHSOPractitioner.PROVIDERTYPE + "=?";
            String[] selectionArgs = {providerType};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding practitioners by type", e);
        }

        return practitioners;
    }

    /**
     * ค้นหาผู้ให้บริการตามสภาวิชาชีพ
     * @param council รหัสสภาวิชาชีพ
     * @return รายการข้อมูลผู้ให้บริการที่ตรงกับเงื่อนไข
     */
    public List<NHSOPractitionerInfo> findPractitionersByCouncil(String council) {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            String selection = NHSOPractitioner.COUNCIL + "=?";
            String[] selectionArgs = {council};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding practitioners by council", e);
        }

        return practitioners;
    }

    /**
     * แปลง Cursor เป็น NHSOPractitionerInfo
     * @param cursor Cursor ที่ได้จากการ query
     * @return ข้อมูลผู้ให้บริการ
     */
    private NHSOPractitionerInfo cursorToPractitioner(Cursor cursor) {
        NHSOPractitionerInfo practitioner = new NHSOPractitionerInfo();

        int idIndex = cursor.getColumnIndex(NHSOPractitioner.ID);
        if (idIndex != -1) {
            practitioner.setId(cursor.getLong(idIndex));
        }

        int seqIndex = cursor.getColumnIndex(NHSOPractitioner.SEQ);
        if (seqIndex != -1) {
            practitioner.setSeq(cursor.getString(seqIndex));
        }

        int hcodeIndex = cursor.getColumnIndex(NHSOPractitioner.HCODE);
        if (hcodeIndex != -1) {
            practitioner.setHcode(cursor.getString(hcodeIndex));
        }

        int cidIndex = cursor.getColumnIndex(NHSOPractitioner.CID);
        if (cidIndex != -1) {
            practitioner.setCid(cursor.getString(cidIndex));
        }

        int professionIdIndex = cursor.getColumnIndex(NHSOPractitioner.PROFESSION_ID);
        if (professionIdIndex != -1 && !cursor.isNull(professionIdIndex)) {
            practitioner.setProfessionId(cursor.getString(professionIdIndex));
        }

        int councilIndex = cursor.getColumnIndex(NHSOPractitioner.COUNCIL);
        if (councilIndex != -1 && !cursor.isNull(councilIndex)) {
            practitioner.setCouncil(cursor.getString(councilIndex));
        }

        int providerTypeIndex = cursor.getColumnIndex(NHSOPractitioner.PROVIDERTYPE);
        if (providerTypeIndex != -1 && !cursor.isNull(providerTypeIndex)) {
            practitioner.setProviderType(cursor.getString(providerTypeIndex));
        }

        int nameGivenIndex = cursor.getColumnIndex(NHSOPractitioner.NAME_GIVEN);
        if (nameGivenIndex != -1 && !cursor.isNull(nameGivenIndex)) {
            practitioner.setNameGiven(cursor.getString(nameGivenIndex));
        }

        int nameFamilyIndex = cursor.getColumnIndex(NHSOPractitioner.NAME_FAMILY);
        if (nameFamilyIndex != -1 && !cursor.isNull(nameFamilyIndex)) {
            practitioner.setNameFamily(cursor.getString(nameFamilyIndex));
        }

        int syncStatusIndex = cursor.getColumnIndex(NHSOPractitioner.SYNC_STATUS);
        if (syncStatusIndex != -1 && !cursor.isNull(syncStatusIndex)) {
            practitioner.setSyncStatus(cursor.getInt(syncStatusIndex) == 1);
        }

        int createByIndex = cursor.getColumnIndex(NHSOPractitioner.USER_CREATE);
        if (createByIndex != -1 && !cursor.isNull(createByIndex)) {
            practitioner.setCreatedBy(cursor.getString(createByIndex));
        }

        int createTimeIndex = cursor.getColumnIndex(NHSOPractitioner.CREATETIME);
        if (createTimeIndex != -1 && !cursor.isNull(createTimeIndex)) {
            practitioner.setCreatedDate(cursor.getString(createTimeIndex));
        }

        int updateByIndex = cursor.getColumnIndex(NHSOPractitioner.USER_UPDATE);
        if (updateByIndex != -1 && !cursor.isNull(updateByIndex)) {
            practitioner.setUpdatedBy(cursor.getString(updateByIndex));
        }

        int updateTimeIndex = cursor.getColumnIndex(NHSOPractitioner.UPDATETIME);
        if (updateTimeIndex != -1 && !cursor.isNull(updateTimeIndex)) {
            practitioner.setUpdatedDate(cursor.getString(updateTimeIndex));
        }

        return practitioner;
    }

    /**
     * ตรวจสอบว่ามีข้อมูลผู้ให้บริการอยู่หรือไม่
     * @param cid เลขบัตรประชาชน
     * @return true ถ้ามีข้อมูล, false ถ้าไม่มีข้อมูล
     */
    public boolean isPractitionerExists(String cid) {
        try {
            String selection = NHSOPractitioner.CID + "=?";
            String[] selectionArgs = {cid};

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI,
                    new String[]{NHSOPractitioner.ID}, selection, selectionArgs, null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if practitioner exists", e);
            return false;
        }
    }

    /**
     * นับจำนวนผู้ให้บริการทั้งหมด
     * @return จำนวนผู้ให้บริการทั้งหมด
     */
    public int countAllPractitioners() {
        try {
            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI,
                    new String[]{"COUNT(*) AS count"}, null, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting practitioners", e);
            return 0;
        }
    }

    /**
     * นับจำนวนผู้ให้บริการที่ยังไม่ได้ซิงค์
     * @return จำนวนผู้ให้บริการที่ยังไม่ได้ซิงค์
     */
    public int countUnsyncedPractitioners() {
        try {
            String selection = NHSOPractitioner.SYNC_STATUS + "=0 OR " + NHSOPractitioner.SYNC_STATUS + " IS NULL";

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI,
                    new String[]{"COUNT(*) AS count"}, selection, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting unsynced practitioners", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลผู้ให้บริการที่ยังไม่ได้ซิงค์
     * @return รายการผู้ให้บริการที่ยังไม่ได้ซิงค์
     */
    public List<NHSOPractitionerInfo> getUnsyncedPractitioners() {
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        try {
            String selection = NHSOPractitioner.SYNC_STATUS + "=0 OR " + NHSOPractitioner.SYNC_STATUS + " IS NULL";

            Cursor cursor = mResolver.query(NHSOPractitioner.CONTENT_LIST_URI, null, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOPractitionerInfo practitioner = cursorToPractitioner(cursor);
                    practitioners.add(practitioner);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting unsynced practitioners", e);
        }

        return practitioners;
    }
}