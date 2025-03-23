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

import th.in.ffc.app.form.nhso.model.NHSOCHADInfo;
import th.in.ffc.provider.NHSOCHAD;

/**
 * Service สำหรับจัดการข้อมูลแฟ้มที่ 7 NHSO CHAD
 */
public class NHSOCHADService {

    private static final String TAG = "NHSOCHADService";
    private Context context;
    private SimpleDateFormat dateFormat;

    public NHSOCHADService(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    }

    /**
     * บันทึกข้อมูล CHAD ลงในฐานข้อมูล
     * @param chadInfo ข้อมูล CHAD ที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createCHAD(NHSOCHADInfo chadInfo, String username) {
        try {
            ContentValues values = new ContentValues();

            // กำหนดค่าให้ ContentValues
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

            values.put(NHSOCHAD.USER, username);
            values.put(NHSOCHAD.DATEUPDATE, System.currentTimeMillis());

            // บันทึกข้อมูล
            return context.getContentResolver().insert(NHSOCHAD.CONTENT_URI, values);
        } catch (Exception e) {
            Log.e(TAG, "Error creating CHAD record", e);
            return null;
        }
    }

    /**
     * ค้นหาข้อมูล CHAD ทั้งหมด
     * @return รายการข้อมูล CHAD ที่พบ
     */
    public List<NHSOCHADInfo> getAllCHAD() {
        List<NHSOCHADInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    null,
                    null,
                    null,
                    NHSOCHAD.SERVDATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHAD(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying all CHAD records", e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHAD ตามรหัสการรับบริการ
     * @param seq รหัสการรับบริการที่ต้องการค้นหา
     * @return รายการข้อมูล CHAD ที่พบ
     */
    public List<NHSOCHADInfo> getCHADBySeq(String seq) {
        List<NHSOCHADInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    null,
                    NHSOCHAD.SEQ + "=?",
                    new String[]{seq},
                    NHSOCHAD.SERVDATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHAD(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying CHAD records by SEQ: " + seq, e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHAD ตามหมวดค่าใช้จ่าย
     * @param billgrcs รหัสหมวดค่าใช้จ่าย
     * @return รายการข้อมูล CHAD ที่พบ
     */
    public List<NHSOCHADInfo> getCHADByBillCategory(String billgrcs) {
        List<NHSOCHADInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    null,
                    NHSOCHAD.BILLGRCS + "=?",
                    new String[]{billgrcs},
                    NHSOCHAD.SERVDATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHAD(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying CHAD records by bill category: " + billgrcs, e);
        }

        return results;
    }

    /**
     * ค้นหาข้อมูล CHAD ตามระบบรหัสที่ใช้
     * @param codesys รหัสระบบที่ใช้
     * @return รายการข้อมูล CHAD ที่พบ
     */
    public List<NHSOCHADInfo> getCHADByCodeSystem(String codesys) {
        List<NHSOCHADInfo> results = new ArrayList<>();

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    null,
                    NHSOCHAD.CODESYS + "=?",
                    new String[]{codesys},
                    NHSOCHAD.SERVDATE + " DESC");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        results.add(cursorToCHAD(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error querying CHAD records by code system: " + codesys, e);
        }

        return results;
    }

    /**
     * แปลงข้อมูลจาก Cursor เป็น NHSOCHADInfo
     * @param cursor Cursor ที่ต้องการแปลง
     * @return NHSOCHADInfo
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
     * อัพเดทข้อมูล CHAD
     * @param chadInfo ข้อมูล CHAD ที่ต้องการอัพเดท
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนแถวที่ถูกอัพเดท
     */
    public int updateCHAD(NHSOCHADInfo chadInfo, String username) {
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

            values.put(NHSOCHAD.USER, username);
            values.put(NHSOCHAD.UPDATE, "1"); // ตั้งค่าสถานะการอัพเดท
            values.put(NHSOCHAD.DATEUPDATE, System.currentTimeMillis());

            // อัพเดทข้อมูล
            return context.getContentResolver().update(
                    NHSOCHAD.CONTENT_URI,
                    values,
                    NHSOCHAD.ID + "=?",
                    new String[]{String.valueOf(chadInfo.getId())});
        } catch (Exception e) {
            Log.e(TAG, "Error updating CHAD record: " + chadInfo.getId(), e);
            return 0;
        }
    }

    /**
     * ลบข้อมูล CHAD
     * @param id ID ของข้อมูลที่ต้องการลบ
     * @return จำนวนแถวที่ถูกลบ
     */
    public int deleteCHAD(long id) {
        try {
            return context.getContentResolver().delete(
                    NHSOCHAD.CONTENT_URI,
                    NHSOCHAD.ID + "=?",
                    new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting CHAD record: " + id, e);
            return 0;
        }
    }

    /**
     * สร้างข้อมูล CHAD จากข้อมูลของการเข้ารับบริการ (อัตโนมัติ)
     * @param visitId รหัสการเข้ารับบริการ
     * @param invoiceNo เลขที่ใบแจ้งหนี้
     * @param serviceDate วันที่รับบริการ
     * @param stdCode รหัสมาตรฐาน
     * @param codeSystem ระบบรหัสที่ใช้
     * @param billCategory หมวดค่าใช้จ่าย
     * @param description รายละเอียดบริการ
     * @param quantity จำนวน
     * @param unitPrice ราคาต่อหน่วย
     * @param username ชื่อผู้ใช้
     * @return Uri ของข้อมูลที่บันทึก
     */
    public Uri createCHADFromServiceData(
            String visitId,
            String invoiceNo,
            Date serviceDate,
            String stdCode,
            String codeSystem,
            String billCategory,
            String description,
            int quantity,
            double unitPrice,
            String username) {

        NHSOCHADInfo chadInfo = new NHSOCHADInfo();
        chadInfo.setSeq(visitId);
        chadInfo.setInvoiceNo(invoiceNo);
        chadInfo.setServdate(serviceDate);
        chadInfo.setStdcode(stdCode);
        chadInfo.setCodesys(codeSystem);
        chadInfo.setBillgrcs(billCategory);
        chadInfo.setDescript(description);
        chadInfo.setQty(quantity);
        chadInfo.setUnitprice(unitPrice);
        chadInfo.setChargeamt(quantity * unitPrice);

        return createCHAD(chadInfo, username);
    }

    /**
     * คำนวณยอดค่าใช้จ่ายรวมของรหัสการรับบริการ
     * @param seq รหัสการรับบริการ
     * @return ยอดค่าใช้จ่ายรวม
     */
    public double calculateTotalChargeBySeq(String seq) {
        double total = 0.0;

        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    new String[]{"SUM(" + NHSOCHAD.CHARGEAMT + ") AS total"},
                    NHSOCHAD.SEQ + "=?",
                    new String[]{seq},
                    null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    total = cursor.getDouble(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating total charge for SEQ: " + seq, e);
        }

        return total;
    }

    /**
     * ตรวจสอบว่ามีข้อมูล CHAD สำหรับรหัสการรับบริการหรือไม่
     * @param seq รหัสการรับบริการ
     * @return true ถ้ามี, false ถ้าไม่มี
     */
    public boolean hasCHADForSeq(String seq) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    new String[]{NHSOCHAD.ID},
                    NHSOCHAD.SEQ + "=?",
                    new String[]{seq},
                    null,
                    null);

            boolean exists = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return exists;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if CHAD exists for SEQ: " + seq, e);
            return false;
        }
    }

    /**
     * นับจำนวนรายการค่าใช้จ่ายตามหมวดค่าใช้จ่าย
     * @param billgrcs รหัสหมวดค่าใช้จ่าย
     * @return จำนวนรายการ
     */
    public int countCHADsByBillCategory(String billgrcs) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHAD.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"},
                    NHSOCHAD.BILLGRCS + "=?",
                    new String[]{billgrcs},
                    null);

            int count = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
            }

            return count;
        } catch (Exception e) {
            Log.e(TAG, "Error counting CHADs by bill category: " + billgrcs, e);
            return 0;
        }
    }
}