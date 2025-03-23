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

import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.provider.NHSOCHA;

/**
 * Service สำหรับจัดการข้อมูลแฟ้มที่ 8 NHSO CHA
 */
public class NHSOCHAService {

    private static final String TAG = "NHSOCHAService";
    private Context context;
    private SimpleDateFormat dateFormat;

    public NHSOCHAService(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูล CHA ลงในฐานข้อมูล
     * @param chaInfo ข้อมูล CHA ที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createCHA(NHSOCHAInfo chaInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            // กำหนดค่าให้ ContentValues
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

            values.put(NHSOCHA.USER, username);
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            // บันทึกข้อมูล
            return context.getContentResolver().insert(NHSOCHA.CONTENT_URI, values);
        } catch (Exception e) {
            Log.e(TAG, "Error creating CHA record", e);
            return null;
        }
    }

    /**
     * ค้นหาข้อมูล CHA ทั้งหมด
     * @return รายการข้อมูล CHA ที่พบ
     */
    public List<NHSOCHAInfo> getAllCHA() {
        List<NHSOCHAInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    null,
                    null,
                    null,
                    NHSOCHA.DATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHA(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying all CHA records", e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHA ตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการที่ต้องการค้นหา
     * @return รายการข้อมูล CHA ที่พบ
     */
    public List<NHSOCHAInfo> getCHABySeq(String seq) {
        List<NHSOCHAInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    null,
                    NHSOCHA.SEQ + "=?",
                    new String[]{seq},
                    NHSOCHA.DATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHA(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying CHA records by SEQ: " + seq, e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHA ตามเลขที่ใบแจ้งหนี้
     * @param invoiceNo เลขที่ใบแจ้งหนี้ที่ต้องการค้นหา
     * @return รายการข้อมูล CHA ที่พบ
     */
    public List<NHSOCHAInfo> getCHAByInvoiceNo(String invoiceNo) {
        List<NHSOCHAInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    null,
                    NHSOCHA.INVOICE_NO + "=?",
                    new String[]{invoiceNo},
                    NHSOCHA.DATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHA(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying CHA records by Invoice No: " + invoiceNo, e);
        }

        return results;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOCHAInfo
     * @param cursor Cursor ที่ต้องการแปลง
     * @return NHSOCHAInfo
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
     * อัพเดทข้อมูล CHA
     * @param chaInfo ข้อมูล CHA ที่ต้องการอัพเดท
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนแถวที่ถูกอัพเดท
     */
    public int updateCHA(NHSOCHAInfo chaInfo, String username) {
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

            values.put(NHSOCHA.USER, username);
            values.put(NHSOCHA.UPDATE, "1"); // ตั้งค่าสถานะการอัพเดท
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            // อัพเดทข้อมูล
            return context.getContentResolver().update(
                    NHSOCHA.CONTENT_URI,
                    values,
                    NHSOCHA.ID + "=?",
                    new String[]{String.valueOf(chaInfo.getId())});
        } catch (Exception e) {
            Log.e(TAG, "Error updating CHA record: " + chaInfo.getId(), e);
            return 0;
        }
    }

    /**
     * ลบข้อมูล CHA
     * @param id ID ของข้อมูลที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int deleteCHA(long id) {
        try {
            return context.getContentResolver().delete(
                    NHSOCHA.CONTENT_URI,
                    NHSOCHA.ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting CHA record: " + id, e);
            return 0;
        }
    }

    /**
     * สร้างข้อมูล CHA จากข้อมูลของการเข้ารับบริการ (อัตโนมัติ)
     * @param visitId รหัสการเข้ารับบริการ
     * @param visitDate วันที่รับบริการ
     * @param chargeItem ชนิดของบริการ
     * @param invoiceNo เลขที่ใบแจ้งหนี้
     * @param amount จำนวนเงิน
     * @param total เงินรวม
     * @param memo รายละเอียดเพิ่มเติม
     * @param username ชื่อผู้ใช้
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createCHAFromServiceData(
            String visitId,
            Date visitDate,
            String chargeItem,
            String invoiceNo,
            double amount,
            double total,
            String memo,
            String username) {

        NHSOCHAInfo chaInfo = new NHSOCHAInfo();
        chaInfo.setSeq(visitId);
        chaInfo.setDate(visitDate);
        chaInfo.setChrgitem(chargeItem);
        chaInfo.setInvoiceNo(invoiceNo);
        chaInfo.setAmount(amount);
        chaInfo.setTotal(total);
        chaInfo.setOpdMemo(memo);

        return createCHA(chaInfo, username);
    }

    /**
     * คำนวณยอดค่าใช้จ่ายรวมของรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ยอดค่าใช้จ่ายรวม
     */
    public double calculateTotalAmountBySeq(String seq) {
        double total = 0.0;

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    new String[]{"SUM(" + NHSOCHA.AMOUNT + ") AS total"},
                    NHSOCHA.SEQ + "=?",
                    new String[]{seq},
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    total = cursor.getDouble(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total amount for SEQ: " + seq, e);
        }

        return total;
    }

    /**
     * ค้นหาข้อมูล CHA ตามช่วงวันที่
     * @param startDate วันที่เริ่มต้น
     * @param endDate วันที่สิ้นสุด
     * @return รายการข้อมูล CHA ที่พบ
     */
    public List<NHSOCHAInfo> findCHAsByDateRange(Date startDate, Date endDate) {
        List<NHSOCHAInfo> results = new ArrayList<>();

        try {
            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};

            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    NHSOCHA.DATE + " ASC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHA(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHAs by date range", e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHA ตามชนิดของบริการ
     * @param chrgitem ชนิดของบริการที่ต้องการค้นหา
     * @return รายการข้อมูล CHA ที่พบ
     */
    public List<NHSOCHAInfo> findCHAsByChargeItem(String chrgitem) {
        List<NHSOCHAInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    null,
                    NHSOCHA.CHRGITEM + "=?",
                    new String[]{chrgitem},
                    NHSOCHA.DATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHA(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding CHAs by charge item: " + chrgitem, e);
        }

        return results;
    }

    /**
     * จัดกลุ่มข้อมูล CHA ตามรหัสการรับบริการและคำนวณยอดรวม
     * @return รายการข้อมูลสรุปยอดรวมตามรหัสการรับบริการ
     */
    public List<Object[]> summarizeChargesBySeq() {
        List<Object[]> summary = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    new String[]{
                            NHSOCHA.SEQ,
                            "SUM(" + NHSOCHA.AMOUNT + ") AS total_amount",
                            "SUM(" + NHSOCHA.TOTAL + ") AS total_sum",
                            "COUNT(*) AS count"
                    },
                    null,
                    null,
                    NHSOCHA.SEQ + " ASC GROUP BY seq");
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Object[] row = new Object[4];
                        row[0] = cursor.getString(cursor.getColumnIndex(NHSOCHA.SEQ));
                        row[1] = cursor.getDouble(cursor.getColumnIndex("total_amount"));
                        row[2] = cursor.getDouble(cursor.getColumnIndex("total_sum"));
                        row[3] = cursor.getInt(cursor.getColumnIndex("count"));
                        summary.add(row);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error summarizing charges by SEQ", e);
        }

        return summary;
    }

    /**
     * ตรวจสอบว่ามีข้อมูล CHA สำหรับรหัสการรับบริการหรือไม่
     * @param seq รหัสการรับบริการ
     * @return true ถ้ามี, false ถ้าไม่มี
     */
    public boolean hasCHAForSeq(String seq) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    new String[]{NHSOCHA.ID},
                    NHSOCHA.SEQ + "=?",
                    new String[]{seq},
                    null,
                    null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if CHA exists for SEQ: " + seq, e);
            return false;
        }
    }

    /**
     * อัพเดตสถานะการซิงค์ข้อมูล
     * @param id รหัสอ้างอิงของรายการทางการเงิน
     * @param status สถานะการซิงค์ข้อมูล
     * @return จำนวนรายการที่อัพเดต
     */
    public int updateSyncStatus(long id, String status) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHA.UPDATE, status);
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            return context.getContentResolver().update(
                    NHSOCHA.CONTENT_URI,
                    values,
                    NHSOCHA.ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status for CHA: " + id, e);
            return 0;
        }
    }
}