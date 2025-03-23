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

import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.provider.NHSOCHA;

/**
 * Data Access Object สำหรับจัดการข้อมูลทางการเงินของผู้เข้ารับบริการ NHSO (แฟ้ม 8)
 */
public class NHSOCHADao {
    private static final String TAG = "NHSOCHADao";

    private ContentResolver mResolver;
    private SimpleDateFormat dateFormat;

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOCHADao(Context context) {
        this.mResolver = context.getContentResolver();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูลทางการเงินใหม่
     * @param chaInfo ข้อมูลทางการเงินที่ต้องการบันทึก
     * @return ID ของข้อมูลทางการเงินที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long insertCHA(NHSOCHAInfo chaInfo) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHA.SEQ, chaInfo.getSeq());

            if (chaInfo.getDate() != null) {
                values.put(NHSOCHA.DATE, dateFormat.format(chaInfo.getDate()));
            }

            values.put(NHSOCHA.CHRGITEM, chaInfo.getChrgitem());
            values.put(NHSOCHA.INVOICE_NO, chaInfo.getInvoiceNo());

            if (chaInfo.getAmount() != null) {
                values.put(NHSOCHA.AMOUNT, chaInfo.getAmount());
            }

            if (chaInfo.getTotal() != null) {
                values.put(NHSOCHA.TOTAL, chaInfo.getTotal());
            }

            values.put(NHSOCHA.OPD_MEMO, chaInfo.getOpdMemo());

            values.put(NHSOCHA.USER, "unknown"); // ค่าเริ่มต้น
            values.put(NHSOCHA.UPDATE, "0");
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            Uri uri = mResolver.insert(NHSOCHA.CONTENT_URI, values);
            if (uri != null) {
                return ContentUris.parseId(uri);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting CHA", e);
            return -1;
        }
    }

    /**
     * อัปเดตข้อมูลทางการเงิน
     * @param chaInfo ข้อมูลทางการเงินที่ต้องการอัปเดต
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateCHA(NHSOCHAInfo chaInfo) {
        try {
            ContentValues values = new ContentValues();

            if (chaInfo.getDate() != null) {
                values.put(NHSOCHA.DATE, dateFormat.format(chaInfo.getDate()));
            }

            values.put(NHSOCHA.CHRGITEM, chaInfo.getChrgitem());
            values.put(NHSOCHA.INVOICE_NO, chaInfo.getInvoiceNo());

            if (chaInfo.getAmount() != null) {
                values.put(NHSOCHA.AMOUNT, chaInfo.getAmount());
            }

            if (chaInfo.getTotal() != null) {
                values.put(NHSOCHA.TOTAL, chaInfo.getTotal());
            }

            values.put(NHSOCHA.OPD_MEMO, chaInfo.getOpdMemo());

            values.put(NHSOCHA.UPDATE, "1");
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOCHA.ID + "=?";
            String[] selectionArgs = {String.valueOf(chaInfo.getId())};

            return mResolver.update(NHSOCHA.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating CHA", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทางการเงิน
     * @param id ID ของข้อมูลทางการเงินที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteCHA(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHA.CONTENT_URI, id);
            return mResolver.delete(uri, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting CHA", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลทางการเงินตาม ID
     * @param id ID ของข้อมูลทางการเงิน
     * @return ข้อมูลทางการเงิน หรือ null ถ้าไม่พบ
     */
    public NHSOCHAInfo getCHAById(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHA.CONTENT_URI, id);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                NHSOCHAInfo chaInfo = cursorToCHA(cursor);
                cursor.close();
                return chaInfo;
            }

            if (cursor != null) {
                cursor.close();
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting CHA by ID", e);
            return null;
        }
    }

    /**
     * ดึงข้อมูลทางการเงินตามรหัสการรับบริการ (Visit Number)
     * @param seq รหัสการรับบริการ
     * @return รายการข้อมูลทางการเงินที่เกี่ยวข้องกับรหัสการรับบริการนั้น
     */
    public List<NHSOCHAInfo> getCHABySeq(String seq) {
        List<NHSOCHAInfo> chas = new ArrayList<>();
        try {
            String selection = NHSOCHA.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo chaInfo = cursorToCHA(cursor);
                    chas.add(chaInfo);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting CHA by SEQ", e);
        }
        return chas;
    }

    /**
     * ดึงข้อมูลทางการเงินตามเลขที่ใบแจ้งหนี้
     * @param invoiceNo เลขที่ใบแจ้งหนี้
     * @return รายการข้อมูลทางการเงินที่เกี่ยวข้องกับเลขที่ใบแจ้งหนี้นั้น
     */
    public List<NHSOCHAInfo> getCHAByInvoiceNo(String invoiceNo) {
        List<NHSOCHAInfo> chas = new ArrayList<>();
        try {
            String selection = NHSOCHA.INVOICE_NO + "=?";
            String[] selectionArgs = {invoiceNo};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo chaInfo = cursorToCHA(cursor);
                    chas.add(chaInfo);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting CHA by Invoice No", e);
        }
        return chas;
    }

    /**
     * ดึงข้อมูลทางการเงินทั้งหมด
     * @return รายการข้อมูลทางการเงินทั้งหมด
     */
    public List<NHSOCHAInfo> getAllCHAs() {
        List<NHSOCHAInfo> chas = new ArrayList<>();

        try {
            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo cha = cursorToCHA(cursor);
                    chas.add(cha);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all CHAs", e);
        }

        return chas;
    }

    /**
     * ค้นหาข้อมูลทางการเงินตามช่วงวันที่
     * @param startDate วันที่เริ่มต้น
     * @param endDate วันที่สิ้นสุด
     * @return รายการข้อมูลทางการเงินที่พบ
     */
    public List<NHSOCHAInfo> findCHAsByDateRange(Date startDate, Date endDate) {
        List<NHSOCHAInfo> chas = new ArrayList<>();

        try {
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo cha = cursorToCHA(cursor);
                    chas.add(cha);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHAs by date range", e);
        }

        return chas;
    }

    /**
     * ค้นหาข้อมูลทางการเงินตามชนิดของบริการ
     * @param chrgitem ชนิดของบริการ
     * @return รายการข้อมูลทางการเงินที่พบ
     */
    public List<NHSOCHAInfo> findCHAsByChargeItem(String chrgitem) {
        List<NHSOCHAInfo> chas = new ArrayList<>();

        try {
            String selection = NHSOCHA.CHRGITEM + "=?";
            String[] selectionArgs = {chrgitem};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo cha = cursorToCHA(cursor);
                    chas.add(cha);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHAs by charge item", e);
        }

        return chas;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOCHAInfo
     * @param cursor Cursor จากการ query
     * @return ข้อมูลทางการเงิน
     */
    private NHSOCHAInfo cursorToCHA(Cursor cursor) {
        NHSOCHAInfo chaInfo = new NHSOCHAInfo();

        int idIndex = cursor.getColumnIndex(NHSOCHA.ID);
        if (idIndex != -1) {
            chaInfo.setId(cursor.getLong(idIndex));
        }

        int seqIndex = cursor.getColumnIndex(NHSOCHA.SEQ);
        if (seqIndex != -1) {
            chaInfo.setSeq(cursor.getString(seqIndex));
        }

        int dateIndex = cursor.getColumnIndex(NHSOCHA.DATE);
        if (dateIndex != -1 && !cursor.isNull(dateIndex)) {
            String dateStr = cursor.getString(dateIndex);
            try {
                chaInfo.setDate(dateFormat.parse(dateStr));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing date: " + dateStr, e);
            }
        }

        int chrgitemIndex = cursor.getColumnIndex(NHSOCHA.CHRGITEM);
        if (chrgitemIndex != -1) {
            chaInfo.setChrgitem(cursor.getString(chrgitemIndex));
        }

        int invoiceNoIndex = cursor.getColumnIndex(NHSOCHA.INVOICE_NO);
        if (invoiceNoIndex != -1) {
            chaInfo.setInvoiceNo(cursor.getString(invoiceNoIndex));
        }

        int amountIndex = cursor.getColumnIndex(NHSOCHA.AMOUNT);
        if (amountIndex != -1 && !cursor.isNull(amountIndex)) {
            chaInfo.setAmount(cursor.getDouble(amountIndex));
        }

        int totalIndex = cursor.getColumnIndex(NHSOCHA.TOTAL);
        if (totalIndex != -1 && !cursor.isNull(totalIndex)) {
            chaInfo.setTotal(cursor.getDouble(totalIndex));
        }

        int opdMemoIndex = cursor.getColumnIndex(NHSOCHA.OPD_MEMO);
        if (opdMemoIndex != -1) {
            chaInfo.setOpdMemo(cursor.getString(opdMemoIndex));
        }

        return chaInfo;
    }

    /**
     * ตรวจสอบว่ามีข้อมูลทางการเงินหรือไม่
     * @param id ID ของข้อมูลทางการเงิน
     * @return true ถ้ามี, false ถ้าไม่มี
     */
    public boolean isCHAExists(long id) {
        try {
            Uri uri = ContentUris.withAppendedId(NHSOCHA.CONTENT_URI, id);
            Cursor cursor = mResolver.query(uri, new String[]{NHSOCHA.ID}, null, null, null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if CHA exists", e);
            return false;
        }
    }

    /**
     * นับจำนวนรายการทางการเงินทั้งหมด
     * @return จำนวนรายการทางการเงินทั้งหมด
     */
    public int countAllCHAs() {
        try {
            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, null, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting CHAs", e);
            return 0;
        }
    }

    /**
     * นับจำนวนรายการทางการเงินที่ยังไม่ได้ซิงค์
     * @return จำนวนรายการทางการเงินที่ยังไม่ได้ซิงค์
     */
    public int countUnsyncedCHAs() {
        try {
            String selection = NHSOCHA.UPDATE + "='0' OR " + NHSOCHA.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"}, selection, null, null);

            int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting unsynced CHAs", e);
            return 0;
        }
    }

    /**
     * อัปเดตสถานะการซิงค์ข้อมูล
     * @param id รหัสอ้างอิงของรายการทางการเงิน
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateSyncStatus(long id, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHA.UPDATE, status);
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOCHA.ID + "=?";
            String[] selectionArgs = {String.valueOf(id)};

            return mResolver.update(NHSOCHA.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status", e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูลทางการเงินที่ยังไม่ได้ซิงค์
     * @return รายการข้อมูลทางการเงินที่ยังไม่ได้ซิงค์
     */
    public List<NHSOCHAInfo> getUnsyncedCHAs() {
        List<NHSOCHAInfo> chas = new ArrayList<>();

        try {
            String selection = NHSOCHA.UPDATE + "='0' OR " + NHSOCHA.UPDATE + " IS NULL";

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI, null, selection, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo cha = cursorToCHA(cursor);
                    chas.add(cha);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting unsynced CHAs", e);
        }

        return chas;
    }

    /**
     * คำนวณผลรวมของจำนวนเงินตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ผลรวมของจำนวนเงิน
     */
    public double getTotalAmountBySeq(String seq) {
        double total = 0.0;

        try {
            String selection = NHSOCHA.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI,
                    new String[]{"SUM(" + NHSOCHA.AMOUNT + ") AS total"},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total amount by SEQ", e);
        }

        return total;
    }

    /**
     * คำนวณผลรวมของเงินรวมทั้งหมดตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ผลรวมของเงินรวมทั้งหมด
     */
    public double getTotalSumBySeq(String seq) {
        double total = 0.0;

        try {
            String selection = NHSOCHA.SEQ + "=?";
            String[] selectionArgs = {seq};

            Cursor cursor = mResolver.query(NHSOCHA.CONTENT_URI,
                    new String[]{"SUM(" + NHSOCHA.TOTAL + ") AS total"},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total sum by SEQ", e);
        }

        return total;
    }
}