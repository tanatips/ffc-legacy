package th.in.ffc.app.form.nhso.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
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

import th.in.ffc.app.form.nhso.model.NHSOCHADInfo;
import th.in.ffc.provider.NHSOCHAD;

/**
 * Data Access Object สำหรับจัดการข้อมูลค่าใช้จ่ายรายการของผู้เข้ารับบริการ NHSO (แฟ้ม 7)
 */
public class NHSOCHADDao {
    private static final String TAG = "NHSOCHADDao";

    private ContentResolver mResolver;
    private SimpleDateFormat dateFormat;

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOCHADDao(Context context) {
        this.mResolver = context.getContentResolver();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูลค่าใช้จ่ายใหม่
     * @param chadInfo ข้อมูลค่าใช้จ่ายที่ต้องการบันทึก
     * @return ID ของข้อมูลค่าใช้จ่ายที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertCHAD(NHSOCHADInfo chadInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHAD.SEQ, chadInfo.getSeq());
            values.put(NHSOCHAD.STDCODE, chadInfo.getStdcode());
            values.put(NHSOCHAD.INVOICE_NO, chadInfo.getInvoiceNo());

            if (chadInfo.getServdate() != null) {
                values.put(NHSOCHAD.SERVDATE, dateFormat.format(chadInfo.getServdate()));
            }

            values.put(NHSOCHAD.LOCALCODE, chadInfo.getLocalcode());
            values.put(NHSOCHAD.DESCRIPT, chadInfo.getDescript());

            if (chadInfo.getQty() != null) {
                values.put(NHSOCHAD.QTY, chadInfo.getQty());
            }

            if (chadInfo.getUnitprice() != null) {
                values.put(NHSOCHAD.UNITPRICE, chadInfo.getUnitprice());
            }

            if (chadInfo.getChargeamt() != null) {
                values.put(NHSOCHAD.CHARGEAMT, chadInfo.getChargeamt());
            }

            values.put(NHSOCHAD.BILLGRCS, chadInfo.getBillgrcs());
            values.put(NHSOCHAD.CODESYS, chadInfo.getCodesys());
            values.put(NHSOCHAD.LAB_RESULT, chadInfo.getLabResult());
            values.put(NHSOCHAD.UNIT, chadInfo.getUnit());

            if (chadInfo.getReimbprice() != null) {
                values.put(NHSOCHAD.REIMBPRICE, chadInfo.getReimbprice());
            }

            values.put(NHSOCHAD.XRAY_RESULT, chadInfo.getXrayResult());
            values.put(NHSOCHAD.PATHO_RESULT, chadInfo.getPathoResult());

            values.put(NHSOCHAD.USER, "unknown"); // ค่าเริ่มต้น
            values.put(NHSOCHAD.UPDATE, "0");
            values.put(NHSOCHAD.DATEUPDATE, System.currentTimeMillis());

            Uri uri = mResolver.insert(NHSOCHAD.CONTENT_URI, values);
            if (uri != null) {
                return ContentUris.parseId(uri);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting CHAD", e);
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลค่าใช้จ่าย
     * @param chadInfo ข้อมูลค่าใช้จ่ายที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateCHAD(NHSOCHADInfo chadInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHAD.STDCODE, chadInfo.getStdcode());
            values.put(NHSOCHAD.INVOICE_NO, chadInfo.getInvoiceNo());

            if (chadInfo.getServdate() != null) {
                values.put(NHSOCHAD.SERVDATE, dateFormat.format(chadInfo.getServdate()));
            }

            values.put(NHSOCHAD.LOCALCODE, chadInfo.getLocalcode());
            values.put(NHSOCHAD.DESCRIPT, chadInfo.getDescript());

            if (chadInfo.getQty() != null) {
                values.put(NHSOCHAD.QTY, chadInfo.getQty());
            }

            if (chadInfo.getUnitprice() != null) {
                values.put(NHSOCHAD.UNITPRICE, chadInfo.getUnitprice());
            }

            if (chadInfo.getChargeamt() != null) {
                values.put(NHSOCHAD.CHARGEAMT, chadInfo.getChargeamt());
            }

            values.put(NHSOCHAD.BILLGRCS, chadInfo.getBillgrcs());
            values.put(NHSOCHAD.CODESYS, chadInfo.getCodesys());
            values.put(NHSOCHAD.LAB_RESULT, chadInfo.getLabResult());
            values.put(NHSOCHAD.UNIT, chadInfo.getUnit());

            if (chadInfo.getReimbprice() != null) {
                values.put(NHSOCHAD.REIMBPRICE, chadInfo.getReimbprice());
            }

            values.put(NHSOCHAD.XRAY_RESULT, chadInfo.getXrayResult());
            values.put(NHSOCHAD.PATHO_RESULT, chadInfo.getPathoResult());

            values.put(NHSOCHAD.UPDATE, "1");
            values.put(NHSOCHAD.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOCHAD.ID + "=?";
            String[] selectionArgs = {String.valueOf(chadInfo.getId())};

            return mResolver.update(NHSOCHAD.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating CHAD", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลค่าใช้จ่าย
     * @param id ID ของข้อมูลค่าใช้จ่ายที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteCHAD(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHAD.CONTENT_URI, id);
            return mResolver.delete(uri, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting CHAD", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลค่าใช้จ่ายตาม ID
     * @param id ID ของข้อมูลค่าใช้จ่าย
     * @return ข้อมูลค่าใช้จ่าย หรือ null ถ้าไม่พบ
     */
    public NHSOCHADInfo getCHADById(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHAD.CONTENT_URI, id);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOCHADInfo chadInfo = cursorToCHAD(cursor);
                cursor.close();
                return chadInfo;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting CHAD by ID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลค่าใช้จ่ายตามรหัสการรับบริการ (Visit Number)
     * @param seq รหัสการรับบริการ
     * @return รายการข้อมูลค่าใช้จ่ายที่เกี่ยวข้องกับรหัสการรับบริการนั้น
     */
    public List<NHSOCHADInfo> getCHADBySeq(String seq) {
        List<NHSOCHADInfo> chads = new ArrayList<>();
        try {
            String selection = NHSOCHAD.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chadInfo = cursorToCHAD(cursor);
                    chads.add(chadInfo);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting CHAD by SEQ", e);
        }
        return chads;
    }

    /**
     * ดึงข้อมูลค่าใช้จ่ายทั้งหมด
     * @return รายการข้อมูลค่าใช้จ่ายทั้งหมด
     */
    public List<NHSOCHADInfo> getAllCHADs() {
        List<NHSOCHADInfo> chads = new ArrayList<>();

        try {
            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chad = cursorToCHAD(cursor);
                    chads.add(chad);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all CHADs", e);
        }

        return chads;
    }

    /**
     * ค้นหาข้อมูลค่าใช้จ่ายตามช่วงวันที่
     * @param startDate วันที่เริ่มต้น
     * @param endDate วันที่สิ้นสุด
     * @return รายการข้อมูลค่าใช้จ่ายที่พบ
     */
    public List<NHSOCHADInfo> findCHADsByDateRange(Date startDate, Date endDate) {
        List<NHSOCHADInfo> chads = new ArrayList<>();

        try {
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            String selection = NHSOCHAD.SERVDATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chad = cursorToCHAD(cursor);
                    chads.add(chad);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHADs by date range", e);
        }

        return chads;
    }

    /**
     * ค้นหาข้อมูลค่าใช้จ่ายตามหมวดค่าใช้จ่าย
     * @param billgrcs รหัสหมวดค่าใช้จ่าย
     * @return รายการข้อมูลค่าใช้จ่ายที่พบ
     */
    public List<NHSOCHADInfo> findCHADsByBillCategory(String billgrcs) {
        List<NHSOCHADInfo> chads = new ArrayList<>();

        try {
            String selection = NHSOCHAD.BILLGRCS + "=?";
            String[] selectionArgs = {billgrcs};

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chad = cursorToCHAD(cursor);
                    chads.add(chad);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHADs by bill category", e);
        }

        return chads;
    }

    /**
     * ค้นหาข้อมูลค่าใช้จ่ายตามระบบรหัสมาตรฐาน
     * @param codesys รหัสระบบมาตรฐาน
     * @return รายการข้อมูลค่าใช้จ่ายที่พบ
     */
    public List<NHSOCHADInfo> findCHADsByCodeSystem(String codesys) {
        List<NHSOCHADInfo> chads = new ArrayList<>();

        try {
            String selection = NHSOCHAD.CODESYS + "=?";
            String[] selectionArgs = {codesys};

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chad = cursorToCHAD(cursor);
                    chads.add(chad);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHADs by code system", e);
        }

        return chads;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOCHADInfo
     * @param cursor Cursor จากการ query
     * @return ข้อมูลค่าใช้จ่าย
     */
    private NHSOCHADInfo cursorToCHAD(Cursor cursor) {
        NHSOCHADInfo chadInfo = new NHSOCHADInfo();

        int idIndex = cursor.getColumnIndex(NHSOCHAD.ID);
        if (idIndex != -1) {
            chadInfo.setId(cursor.getLong(idIndex));
        }

        int seqIndex = cursor.getColumnIndex(NHSOCHAD.SEQ);
        if (seqIndex != -1) {
            chadInfo.setSeq(cursor.getString(seqIndex));
        }

        int stdcodeIndex = cursor.getColumnIndex(NHSOCHAD.STDCODE);
        if (stdcodeIndex != -1) {
            chadInfo.setStdcode(cursor.getString(stdcodeIndex));
        }

        int invoiceNoIndex = cursor.getColumnIndex(NHSOCHAD.INVOICE_NO);
        if (invoiceNoIndex != -1) {
            chadInfo.setInvoiceNo(cursor.getString(invoiceNoIndex));
        }

        int servdateIndex = cursor.getColumnIndex(NHSOCHAD.SERVDATE);
        if (servdateIndex != -1 && !cursor.isNull(servdateIndex)) {
            String servdateStr = cursor.getString(servdateIndex);
            try {
                chadInfo.setServdate(dateFormat.parse(servdateStr));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + servdateStr, e);
            }
        }

        int localcodeIndex = cursor.getColumnIndex(NHSOCHAD.LOCALCODE);
        if (localcodeIndex != -1) {
            chadInfo.setLocalcode(cursor.getString(localcodeIndex));
        }

        int descriptIndex = cursor.getColumnIndex(NHSOCHAD.DESCRIPT);
        if (descriptIndex != -1) {
            chadInfo.setDescript(cursor.getString(descriptIndex));
        }

        int qtyIndex = cursor.getColumnIndex(NHSOCHAD.QTY);
        if (qtyIndex != -1 && !cursor.isNull(qtyIndex)) {
            chadInfo.setQty(cursor.getInt(qtyIndex));
        }

        int unitpriceIndex = cursor.getColumnIndex(NHSOCHAD.UNITPRICE);
        if (unitpriceIndex != -1 && !cursor.isNull(unitpriceIndex)) {
            chadInfo.setUnitprice(cursor.getDouble(unitpriceIndex));
        }

        int chargeamtIndex = cursor.getColumnIndex(NHSOCHAD.CHARGEAMT);
        if (chargeamtIndex != -1 && !cursor.isNull(chargeamtIndex)) {
            chadInfo.setChargeamt(cursor.getDouble(chargeamtIndex));
        }

        int billgrcsIndex = cursor.getColumnIndex(NHSOCHAD.BILLGRCS);
        if (billgrcsIndex != -1) {
            chadInfo.setBillgrcs(cursor.getString(billgrcsIndex));
        }

        int codesysIndex = cursor.getColumnIndex(NHSOCHAD.CODESYS);
        if (codesysIndex != -1) {
            chadInfo.setCodesys(cursor.getString(codesysIndex));
        }

        int labResultIndex = cursor.getColumnIndex(NHSOCHAD.LAB_RESULT);
        if (labResultIndex != -1) {
            chadInfo.setLabResult(cursor.getString(labResultIndex));
        }

        int unitIndex = cursor.getColumnIndex(NHSOCHAD.UNIT);
        if (unitIndex != -1) {
            chadInfo.setUnit(cursor.getString(unitIndex));
        }

        int reimbpriceIndex = cursor.getColumnIndex(NHSOCHAD.REIMBPRICE);
        if (reimbpriceIndex != -1 && !cursor.isNull(reimbpriceIndex)) {
            chadInfo.setReimbprice(cursor.getDouble(reimbpriceIndex));
        }

        int xrayResultIndex = cursor.getColumnIndex(NHSOCHAD.XRAY_RESULT);
        if (xrayResultIndex != -1) {
            chadInfo.setXrayResult(cursor.getString(xrayResultIndex));
        }

        int pathoResultIndex = cursor.getColumnIndex(NHSOCHAD.PATHO_RESULT);
        if (pathoResultIndex != -1) {
            chadInfo.setPathoResult(cursor.getString(pathoResultIndex));
        }

        return chadInfo;
    }

    /**
     * ตรวจสอบว่ามีข้อมูลค่าใช้จ่ายตาม ID หรือไม่
     * @param id ID ของข้อมูลค่าใช้จ่าย
     * @return true ถ้ามี, false ถ้าไม่มี
     */
    public boolean isCHADExists(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHAD.CONTENT_URI, id);
            Cursor cursor = mResolver.query(uri, new String[]{NHSOCHAD.ID}, null, null, null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if CHAD exists", e);
            return false;
        }
    }

    /**
     * นับจำนวนรายการค่าใช้จ่ายทั้งหมด
     * @return จำนวนรายการค่าใช้จ่ายทั้งหมด
     */
    public int countAllCHADs() {
        try {
            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, null, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting CHADs", e);
            return 0;
        }
    }

    /**
     * นับจำนวนรายการค่าใช้จ่ายที่ยังไม่ได้ซิงค์
     * @return จำนวนรายการค่าใช้จ่ายที่ยังไม่ได้ซิงค์
     */
    public int countUnsyncedCHADs() {
        try {
            String selection = NHSOCHAD.UPDATE + "='0' OR " + NHSOCHAD.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, selection, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting unsynced CHADs", e);
            return 0;
        }
    }

    /**
     * อัปเดตสถานะการซิงค์ข้อมูล
     * @param id รหัสอ้างอิงของรายการค่าใช้จ่าย
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSyncStatus(long id, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHAD.UPDATE, status);
            values.put(NHSOCHAD.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOCHAD.ID + "=?";
            String[] selectionArgs = {String.valueOf(id)};

            return mResolver.update(NHSOCHAD.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลค่าใช้จ่ายที่ยังไม่ได้ซิงค์
     * @return รายการข้อมูลค่าใช้จ่ายที่ยังไม่ได้ซิงค์
     */
    public List<NHSOCHADInfo> getUnsyncedCHADs() {
        List<NHSOCHADInfo> chads = new ArrayList<>();

        try {
            String selection = NHSOCHAD.UPDATE + "='0' OR " + NHSOCHAD.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOCHAD.CONTENT_URI, null, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHADInfo chad = cursorToCHAD(cursor);
                    chads.add(chad);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting unsynced CHADs", e);
        }

        return chads;
    }
}