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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "list");
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

        // เพิ่มการดึงข้อมูล claim_total
        int claimTotalIndex = cursor.getColumnIndex(NHSOCHA.CLAIM_TOTAL);
        if (claimTotalIndex != -1 && !cursor.isNull(claimTotalIndex)) {
            chaInfo.setClaimAmount(cursor.getDouble(claimTotalIndex));
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

            // แก้ไขเพื่อใช้ URI ที่ถูกต้อง
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "sum_amount");

            Cursor cursor = mResolver.query(uri,
                    new String[]{"SUM(" + NHSOCHA.AMOUNT + ") AS total"},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total amount by SEQ: " + e.getMessage(), e);
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

            // แก้ไขเพื่อใช้ URI ที่ถูกต้อง
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "sum_total");

            Cursor cursor = mResolver.query(uri,
                    new String[]{"SUM(" + NHSOCHA.TOTAL + ") AS total"},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total sum by SEQ: " + e.getMessage(), e);
        }

        return total;
    }
    public List<Map<String, Object>> getMonthlySummaryByFiscalYear(String fiscalYear) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // วันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // สร้างรายการเปล่าสำหรับทั้ง 12 เดือน
            for (int month = 1; month <= 12; month++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("fiscalMonth", month);
                monthData.put("monthName", getFiscalMonthName(month));
                monthData.put("totalAmount", 0.0);
                monthData.put("totalCount", 0);
                result.add(monthData);
            }

            // Query ข้อมูลสรุปรายเดือน
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "summary/monthly/" + fiscalYear);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                try {
                    // ดูชื่อคอลัมน์ที่มีใน cursor
                    String[] columnNames = cursor.getColumnNames();
                    Log.d(TAG, "Column names in cursor: " + String.join(", ", columnNames));

                    while (cursor.moveToNext()) {
                        // กำหนดชื่อคอลัมน์ให้ตรงกับที่ Provider ส่งกลับมา
                        int fiscalMonthIndex = cursor.getColumnIndex("fiscal_month");
                        int totalCountIndex = cursor.getColumnIndex("total_count");
                        int totalAmountIndex = cursor.getColumnIndex("total_amount");

                        // ตรวจสอบว่าพบคอลัมน์หรือไม่
                        if (fiscalMonthIndex != -1 && totalCountIndex != -1 && totalAmountIndex != -1) {
                            String fiscalMonthStr = cursor.getString(fiscalMonthIndex);
                            int totalCount = cursor.getInt(totalCountIndex);
                            double totalAmount = cursor.getDouble(totalAmountIndex);

                            // แปลง fiscal_month เป็นตัวเลข
                            int month = Integer.parseInt(fiscalMonthStr);

                            // อัปเดตข้อมูลในผลลัพธ์
                            if (month >= 1 && month <= 12) {
                                Map<String, Object> monthData = result.get(month - 1);
                                monthData.put("totalAmount", totalAmount);
                                monthData.put("totalCount", totalCount);
                            }
                        } else {
                            Log.e(TAG, "One or more required columns not found in cursor.");
                        }
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Log.e(TAG, "Cursor is null from uri: " + uri);

                // กรณีไม่สามารถใช้ Provider ได้ ให้ใช้วิธีการคำนวณแบบเดิม
                String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
                String[] selectionArgs = {startDateStr, endDateStr};
                Cursor dataCursor = mResolver.query(
                        NHSOCHA.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        NHSOCHA.DATE + " ASC"
                );

                if (dataCursor != null) {
                    try {
                        while (dataCursor.moveToNext()) {
                            double amount = 0.0;
                            int totalIndex = dataCursor.getColumnIndex(NHSOCHA.TOTAL);
                            if (totalIndex != -1 && !dataCursor.isNull(totalIndex)) {
                                amount = dataCursor.getDouble(totalIndex);
                            }

                            Date date = null;
                            int dateIndex = dataCursor.getColumnIndex(NHSOCHA.DATE);
                            if (dateIndex != -1 && !dataCursor.isNull(dateIndex)) {
                                String dateStr = dataCursor.getString(dateIndex);
                                try {
                                    date = dateFormat.parse(dateStr);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing date: " + dateStr, e);
                                    continue;
                                }
                            }

                            if (date != null) {
                                int fiscalMonth = calculateFiscalMonth(date);
                                Map<String, Object> monthData = result.get(fiscalMonth - 1);

                                double currentAmount = (double) monthData.get("totalAmount");
                                monthData.put("totalAmount", currentAmount + amount);

                                int currentCount = (int) monthData.get("totalCount");
                                monthData.put("totalCount", currentCount + 1);
                            }
                        }
                    } finally {
                        dataCursor.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly summary", e);
        }

        return result;
    }
    /**
     * คำนวณวันที่เริ่มต้นของปีงบประมาณ
     * @param fiscalYear ปีงบประมาณ
     * @return วันที่เริ่มต้นของปีงบประมาณ (1 ตุลาคม)
     */
    private Date getFiscalYearStartDate(String fiscalYear) {
        int year = Integer.parseInt(fiscalYear) - 1; // ปีก่อนหน้าปีงบประมาณ
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.OCTOBER, 1, 0, 0, 0); // 1 ตุลาคมของปีก่อนหน้า เวลา 00:00:00
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * คำนวณวันที่สิ้นสุดของปีงบประมาณ
     * @param fiscalYear ปีงบประมาณ
     * @return วันที่สิ้นสุดของปีงบประมาณ (30 กันยายน)
     */
    private Date getFiscalYearEndDate(String fiscalYear) {
        int year = Integer.parseInt(fiscalYear); // ปีงบประมาณ
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.SEPTEMBER, 30, 23, 59, 59); // 30 กันยายนของปีงบประมาณ เวลา 23:59:59
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    private int calculateFiscalMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int month = cal.get(Calendar.MONTH); // 0-11

        // แปลงเดือนปฏิทินเป็นเดือนงบประมาณ
        if (month >= 9) { // ตุลาคม-ธันวาคม
            return month - 8; // ตุลาคม=1, พฤศจิกายน=2, ธันวาคม=3
        } else {
            return month + 4; // มกราคม=4, ..., กันยายน=12
        }
    }
    /**
     * แปลงเดือนงบประมาณเป็นชื่อเดือนภาษาไทย
     * @param fiscalMonth เดือนงบประมาณ (1-12)
     * @return ชื่อเดือนภาษาไทย
     */
    private String getFiscalMonthName(int fiscalMonth) {
        switch (fiscalMonth) {
            case 1: return "ตุลาคม";
            case 2: return "พฤศจิกายน";
            case 3: return "ธันวาคม";
            case 4: return "มกราคม";
            case 5: return "กุมภาพันธ์";
            case 6: return "มีนาคม";
            case 7: return "เมษายน";
            case 8: return "พฤษภาคม";
            case 9: return "มิถุนายน";
            case 10: return "กรกฎาคม";
            case 11: return "สิงหาคม";
            case 12: return "กันยายน";
            default: return "";
        }
    }


    /**
     * สรุปข้อมูลการเบิกจ่ายตามปีงบประมาณ (อัพเดตเพื่อรองรับ claim_total)
     */
    public Map<String, Object> getYearlySummaryByFiscalYear(String fiscalYear) {
        Map<String, Object> result = new HashMap<>();
        result.put("fiscalYear", fiscalYear);
        result.put("totalAmount", 0.0);
        result.put("totalChargeAmount", 0.0);  // เพิ่มสำหรับ total (ยอดที่ส่งเบิก)
        result.put("totalClaimAmount", 0.0);   // เพิ่มสำหรับ claim_total (ยอดที่เบิกได้)
        result.put("totalCount", 0);

        try {
            // วันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // เพิ่มข้อมูลช่วงวันที่ในผลลัพธ์
            SimpleDateFormat thaiDateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("th", "TH"));
            result.put("startDate", thaiDateFormat.format(startDate));
            result.put("endDate", thaiDateFormat.format(endDate));

            // Query ข้อมูลสรุปรายปี
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "summary/yearly/" + fiscalYear);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                try {
                    // ดูชื่อคอลัมน์ที่มีใน cursor
                    String[] columnNames = cursor.getColumnNames();
                    Log.d(TAG, "Column names in yearly summary cursor: " + String.join(", ", columnNames));

                    if (cursor.moveToFirst()) {
                        int totalCountIndex = cursor.getColumnIndex("total_count");
                        int totalAmountIndex = cursor.getColumnIndex("total_amount");
                        int totalClaimAmountIndex = cursor.getColumnIndex("total_claim_amount");

                        if (totalCountIndex != -1 && totalAmountIndex != -1) {
                            int totalCount = cursor.getInt(totalCountIndex);
                            double totalAmount = cursor.getDouble(totalAmountIndex);
                            double totalClaimAmount = 0.0;

                            if (totalClaimAmountIndex != -1) {
                                totalClaimAmount = cursor.getDouble(totalClaimAmountIndex);
                            }

                            result.put("totalCount", totalCount);
                            result.put("totalAmount", totalAmount);          // backward compatibility
                            result.put("totalChargeAmount", totalAmount);    // ยอดที่ส่งเบิก
                            result.put("totalClaimAmount", totalClaimAmount); // ยอดที่เบิกได้
                        } else {
                            Log.e(TAG, "Required columns not found in yearly summary cursor.");
                        }
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Log.e(TAG, "Yearly summary cursor is null from uri: " + uri);

                // กรณีไม่สามารถใช้ Provider ได้ ให้ใช้วิธีการคำนวณจากข้อมูลดิบ
                String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
                String[] selectionArgs = {startDateStr, endDateStr};

                Cursor dataCursor = mResolver.query(
                        NHSOCHA.CONTENT_URI,
                        new String[]{
                                "COUNT(*) AS count",
                                "SUM(" + NHSOCHA.TOTAL + ") AS charge_amount",
                                "COALESCE(SUM(" + NHSOCHA.CLAIM_TOTAL + "), 0) AS claim_amount"
                        },
                        selection,
                        selectionArgs,
                        null
                );

                if (dataCursor != null) {
                    try {
                        if (dataCursor.moveToFirst()) {
                            int count = dataCursor.getInt(dataCursor.getColumnIndex("count"));
                            double chargeAmount = dataCursor.getDouble(dataCursor.getColumnIndex("charge_amount"));
                            double claimAmount = dataCursor.getDouble(dataCursor.getColumnIndex("claim_amount"));

                            result.put("totalCount", count);
                            result.put("totalAmount", chargeAmount);        // backward compatibility
                            result.put("totalChargeAmount", chargeAmount);  // ยอดที่ส่งเบิก
                            result.put("totalClaimAmount", claimAmount);    // ยอดที่เบิกได้
                        }
                    } finally {
                        dataCursor.close();
                    }
                }
            }

            // ดึงข้อมูลสรุปตามประเภทรายการ (chrgitem)
            List<Map<String, Object>> chargeItemSummary = getSummaryByChargeItemAndFiscalYear(fiscalYear);
            result.put("chargeItemSummary", chargeItemSummary);

            // คำนวณสัดส่วนร้อยละของแต่ละประเภทรายการ (ใช้ claim_amount)
            if (!chargeItemSummary.isEmpty()) {
                double totalClaimAmount = (double) result.get("totalClaimAmount");
                if (totalClaimAmount > 0) {
                    for (Map<String, Object> item : chargeItemSummary) {
                        double itemClaimAmount = item.containsKey("claimAmount") ?
                                (double) item.get("claimAmount") : 0.0;
                        double percentage = (itemClaimAmount / totalClaimAmount) * 100;
                        item.put("percentage", percentage);
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting yearly summary", e);
        }

        return result;
    }

    /**
     * สรุปข้อมูลการเบิกจ่ายตามประเภทรายการในปีงบประมาณ (อัพเดตเพื่อรองรับ claim_total)
     */
    public List<Map<String, Object>> getSummaryByChargeItemAndFiscalYear(String fiscalYear) {
        List<Map<String, Object>> results = new ArrayList<>();

        try {
            // วันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // Query ข้อมูลสรุปตามประเภทรายการ
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "summary/by_charge/" + fiscalYear);
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                try {
                    // ดูชื่อคอลัมน์ที่มีใน cursor
                    String[] columnNames = cursor.getColumnNames();
                    Log.d(TAG, "Column names in charge item summary cursor: " + String.join(", ", columnNames));

                    while (cursor.moveToNext()) {
                        int chrgitemIndex = cursor.getColumnIndex("chrgitem");
                        int itemCountIndex = cursor.getColumnIndex("item_count");
                        int itemAmountIndex = cursor.getColumnIndex("item_amount");
                        int itemClaimAmountIndex = cursor.getColumnIndex("item_claim_amount");

                        if (chrgitemIndex != -1 && itemCountIndex != -1 && itemAmountIndex != -1) {
                            String chrgitem = cursor.getString(chrgitemIndex);
                            int count = cursor.getInt(itemCountIndex);
                            double amount = cursor.getDouble(itemAmountIndex);
                            double claimAmount = 0.0;

                            if (itemClaimAmountIndex != -1) {
                                claimAmount = cursor.getDouble(itemClaimAmountIndex);
                            }

                            Map<String, Object> itemData = new HashMap<>();
                            itemData.put("chrgitem", chrgitem);
                            itemData.put("count", count);
                            itemData.put("amount", amount);           // backward compatibility
                            itemData.put("chargeAmount", amount);     // ยอดที่ส่งเบิก
                            itemData.put("claimAmount", claimAmount); // ยอดที่เบิกได้

                            results.add(itemData);
                        } else {
                            Log.e(TAG, "Required columns not found in charge item summary cursor.");
                        }
                    }
                } finally {
                    cursor.close();
                }
            } else {
                Log.e(TAG, "Charge item summary cursor is null from uri: " + uri);

                // กรณีไม่สามารถใช้ Provider ได้ ให้ใช้วิธีการคำนวณจากข้อมูลดิบ
                String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
                String[] selectionArgs = {startDateStr, endDateStr};

                Cursor dataCursor = mResolver.query(
                        NHSOCHA.CONTENT_URI,
                        null,
                        selection,
                        selectionArgs,
                        null
                );

                if (dataCursor != null) {
                    try {
                        // สร้าง Map สำหรับเก็บข้อมูลรวมตาม chrgitem
                        Map<String, Map<String, Object>> chargeItemMap = new HashMap<>();

                        while (dataCursor.moveToNext()) {
                            String chrgitem = dataCursor.getString(dataCursor.getColumnIndex(NHSOCHA.CHRGITEM));
                            double amount = dataCursor.getDouble(dataCursor.getColumnIndex(NHSOCHA.TOTAL));

                            // ดึงข้อมูล claim_total
                            double claimAmount = 0.0;
                            int claimTotalIndex = dataCursor.getColumnIndex(NHSOCHA.CLAIM_TOTAL);
                            if (claimTotalIndex != -1 && !dataCursor.isNull(claimTotalIndex)) {
                                claimAmount = dataCursor.getDouble(claimTotalIndex);
                            }

                            // ดึงหรือสร้างข้อมูลสำหรับ chrgitem นี้
                            Map<String, Object> itemData = chargeItemMap.get(chrgitem);
                            if (itemData == null) {
                                itemData = new HashMap<>();
                                itemData.put("chrgitem", chrgitem);
                                itemData.put("count", 0);
                                itemData.put("amount", 0.0);
                                itemData.put("chargeAmount", 0.0);
                                itemData.put("claimAmount", 0.0);
                                chargeItemMap.put(chrgitem, itemData);
                            }

                            // อัปเดตข้อมูล
                            int count = (int) itemData.get("count");
                            double totalAmount = (double) itemData.get("amount");
                            double totalClaimAmount = (double) itemData.get("claimAmount");

                            itemData.put("count", count + 1);
                            itemData.put("amount", totalAmount + amount);
                            itemData.put("chargeAmount", totalAmount + amount);
                            itemData.put("claimAmount", totalClaimAmount + claimAmount);
                        }

                        // เพิ่มข้อมูลสรุปลงในผลลัพธ์
                        results.addAll(chargeItemMap.values());
                    } finally {
                        dataCursor.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting summary by charge item", e);
        }

        return results;
    }

    public Map<String, Object> getMonthSummary(String fiscalYear, int fiscalMonth) {
        Map<String, Object> monthSummary = new HashMap<>();

        Calendar calStart = Calendar.getInstance();
        int year = Integer.parseInt(fiscalYear) - 1;

        calStart.set(year, Calendar.OCTOBER, 1);
        calStart.add(Calendar.MONTH, fiscalMonth - 1);

        Calendar calEnd = (Calendar) calStart.clone();
        calEnd.add(Calendar.MONTH, 1);
        calEnd.add(Calendar.DAY_OF_MONTH, -1);

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));

        monthSummary.put("startDate", displayDateFormat.format(calStart.getTime()));
        monthSummary.put("endDate", displayDateFormat.format(calEnd.getTime()));

        // เพิ่ม URI สำหรับดึงรายละเอียดรายเดือน
        Uri uri = Uri.withAppendedPath(
                NHSOCHA.CONTENT_URI,
                "monthly_details/" + fiscalYear + "/" + String.format("%02d", fiscalMonth)
        );

        Cursor cursor = mResolver.query(uri, null, null, null, null);

        List<NHSOCHAInfo> monthDetails = new ArrayList<>();
        double totalAmount = 0.0;

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    NHSOCHAInfo claim = cursorToCHA(cursor);
                    monthDetails.add(claim);
                    totalAmount += claim.getAmount() != null ? claim.getAmount() : 0.0;
                }
            } finally {
                cursor.close();
            }
        }

        monthSummary.put("totalAmount", totalAmount);
        monthSummary.put("totalCount", monthDetails.size());
        monthSummary.put("details", monthDetails);

        return monthSummary;
    }

    /**
     * คำนวณผลรวมของจำนวนเงินที่เบิกได้ตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ผลรวมของจำนวนเงินที่เบิกได้
     */
    public double getTotalClaimAmountBySeq(String seq) {
        double total = 0.0;

        try {
            String selection = NHSOCHA.SEQ + "=?";
            String[] selectionArgs = {seq};

            // ใช้ URI สำหรับ claim_total
            Uri uri = Uri.withAppendedPath(NHSOCHA.CONTENT_URI, "sum_claim_total");

            Cursor cursor = mResolver.query(uri,
                    new String[]{"COALESCE(SUM(" + NHSOCHA.CLAIM_TOTAL + "), 0) AS total"},
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getDouble(0);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total claim amount by SEQ: " + e.getMessage(), e);
        }

        return total;
    }


    /**
     * อัพเดต claim_total สำหรับ seq ที่ระบุ
     * @param seq รหัสการรับบริการ
     * @param claimTotal จำนวนเงินที่เบิกได้
     * @return จำนวนรายการที่อัพเดต
     */
    public int updateClaimTotalBySeq(String seq, double claimTotal) {
        try {
            ContentValues values = new ContentValues();
            values.put(NHSOCHA.CLAIM_TOTAL, claimTotal);
            values.put(NHSOCHA.UPDATE, "1");
            values.put(NHSOCHA.DATEUPDATE, System.currentTimeMillis());

            String selection = NHSOCHA.SEQ + "=?";
            String[] selectionArgs = {seq};

            return mResolver.update(NHSOCHA.CONTENT_URI, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.e(TAG, "Error updating claim total by SEQ", e);
            return 0;
        }
    }
}