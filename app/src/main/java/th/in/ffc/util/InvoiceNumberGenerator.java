package th.in.ffc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * ตัวสร้างเลขที่อ้างอิงในแจ้งหนี้ (Invoice Number)
 */
public class InvoiceNumberGenerator {
    private static final String TAG = "InvoiceNumberGenerator";

    // Prefix สำหรับเลข invoice
    private static final String INVOICE_PREFIX = "I";

    // Key สำหรับเก็บค่า counter ใน SharedPreferences
    private static final String PREF_NAME = "invoice_number_prefs";
    private static final String LAST_COUNTER_KEY = "last_counter";
    private static final String LAST_YEAR_MONTH_KEY = "last_year_month";

    private Context context;

    public InvoiceNumberGenerator(Context context) {
        this.context = context;
    }

    /**
     * สร้างเลข Invoice Number ตามรูปแบบ I+ปี+เดือน+เลขลำดับ (เช่น I056400272662)
     *
     * รูปแบบ I + YY + MM + random/sequence (8 หลัก)
     *
     * @return เลข invoice number ที่สร้างขึ้น
     */
    public String generateInvoiceNumber() {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US); // 2 หลัก (เช่น 64)
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US); // 2 หลัก (เช่น 05)

        Date currentDate = new Date();
        String year = yearFormat.format(currentDate);
        String month = monthFormat.format(currentDate);

        // สร้างเลขที่ต่อเนื่องจากค่าที่เก็บไว้
        int sequenceNumber = getNextSequenceNumber(year + month);

        // สร้างเลข invoice จากส่วนประกอบ
        return INVOICE_PREFIX + year + month + String.format(Locale.US, "%08d", sequenceNumber);
    }

    /**
     * สร้างเลข Invoice Number ตามรูปแบบ แต่ใช้ค่าสุ่มแทนเลขลำดับ
     *
     * @return เลข invoice number ที่สร้างขึ้น
     */
    public String generateRandomInvoiceNumber() {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US);

        Date currentDate = new Date();
        String year = yearFormat.format(currentDate);
        String month = monthFormat.format(currentDate);

        // สร้างเลขสุ่ม 8 หลัก
        Random random = new Random();
        int randomNumber = random.nextInt(90000000) + 10000000; // สุ่มเลข 8 หลักระหว่าง 10000000-99999999

        // สร้างเลข invoice จากส่วนประกอบ
        return INVOICE_PREFIX + year + month + randomNumber;
    }

    /**
     * สร้างเลข Invoice Number สำหรับวันที่ที่กำหนด
     *
     * @param date วันที่ที่ต้องการสร้างเลข invoice
     * @return เลข invoice number ที่สร้างขึ้น
     */
    public String generateInvoiceNumberForDate(Date date) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.US);

        String year = yearFormat.format(date);
        String month = monthFormat.format(date);

        // สร้างเลขที่ต่อเนื่องจากค่าที่เก็บไว้
        int sequenceNumber = getNextSequenceNumber(year + month);

        // สร้างเลข invoice จากส่วนประกอบ
        return INVOICE_PREFIX + year + month + String.format(Locale.US, "%08d", sequenceNumber);
    }

    /**
     * ตรวจสอบว่าเลข invoice ถูกต้องตามรูปแบบหรือไม่
     *
     * @param invoiceNumber เลข invoice ที่ต้องการตรวจสอบ
     * @return true ถ้าถูกต้อง, false ถ้าไม่ถูกต้อง
     */
    public boolean isValidInvoiceNumber(String invoiceNumber) {
        // ต้องขึ้นต้นด้วย I ตามด้วยตัวเลข 12 หลัก
        return invoiceNumber != null && invoiceNumber.matches("^I\\d{12}$");
    }

    /**
     * ดึงวันที่จากเลข invoice
     *
     * @param invoiceNumber เลข invoice
     * @return วันที่ที่สร้างเลข invoice หรือ null ถ้าไม่สามารถแปลงเป็นวันที่ได้
     */
    public Date getDateFromInvoiceNumber(String invoiceNumber) {
        if (isValidInvoiceNumber(invoiceNumber)) {
            try {
                // ดึงปีและเดือนจากเลข invoice (เช่น I056400272662 -> 6 = ปี, 4 = เดือน)
                String yearPart = invoiceNumber.substring(1, 3);  // 2 หลักหลัง I
                String monthPart = invoiceNumber.substring(3, 5); // 2 หลักถัดไป

                int year = Integer.parseInt(yearPart) + 2000; // แปลงเป็นปี 4 หลัก (เช่น 64 -> 2564)
                int month = Integer.parseInt(monthPart) - 1;  // เดือนใน Calendar เริ่มที่ 0-11

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                return calendar.getTime();
            } catch (Exception e) {
                Log.e(TAG, "Error extracting date from invoice number: " + invoiceNumber, e);
                return null;
            }
        }
        return null;
    }

    /**
     * ดึงค่าลำดับถัดไปสำหรับเดือนและปีที่กำหนด
     *
     * @param yearMonth ปีและเดือนในรูปแบบ YYMM
     * @return เลขลำดับถัดไป
     */
    private int getNextSequenceNumber(String yearMonth) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lastYearMonth = prefs.getString(LAST_YEAR_MONTH_KEY, "");

        // ถ้าปีและเดือนเปลี่ยน ให้เริ่มต้นนับใหม่
        if (!yearMonth.equals(lastYearMonth)) {
            prefs.edit()
                    .putString(LAST_YEAR_MONTH_KEY, yearMonth)
                    .putInt(LAST_COUNTER_KEY, 1)
                    .apply();
            return 1;
        }

        // เพิ่มค่า counter ขึ้น 1
        int lastCounter = prefs.getInt(LAST_COUNTER_KEY, 0);
        int newCounter = lastCounter + 1;

        prefs.edit().putInt(LAST_COUNTER_KEY, newCounter).apply();

        return newCounter;
    }
}