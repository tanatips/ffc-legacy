package th.in.ffc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    public static String convertToThaiBuddhistDate(String westernDate) {
        try {
            // กำหนดรูปแบบวันที่ input (yyyy-MM-dd)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // แปลง String เป็น Date object
            Date date = inputFormat.parse(westernDate);

            // สร้าง Calendar object สำหรับคำนวณปีพุทธศักราช
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // แปลงปีคริสต์ศักราชเป็นพุทธศักราช (บวก 543)
            int yearBE = cal.get(Calendar.YEAR) + 543;

            // กำหนดรูปแบบวันที่ output (dd/MM/yyyy)
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/", Locale.US);

            // รวมวันที่และเดือนกับปีพุทธศักราช
            String result = outputFormat.format(date) + yearBE;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

    public static String convertToWesternDate(String thaiDate) {
        try {
            // แยกวันที่ออกเป็นส่วนๆ
            String[] dateParts = thaiDate.split("/");
            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int yearBE = Integer.parseInt(dateParts[2]);

            // แปลงปีพุทธศักราชเป็นคริสต์ศักราช (ลบ 543)
            int yearCE = yearBE - 543;

            // สร้าง Calendar object และกำหนดค่าวันที่
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, yearCE);
            calendar.set(Calendar.MONTH, month - 1); // เดือนใน Calendar เริ่มจาก 0
            calendar.set(Calendar.DAY_OF_MONTH, day);

            // กำหนดรูปแบบวันที่ output (yyyy-MM-dd)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // แปลงเป็น String ตามรูปแบบที่ต้องการ
            String westernDate = outputFormat.format(calendar.getTime());

            return westernDate;
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }
}