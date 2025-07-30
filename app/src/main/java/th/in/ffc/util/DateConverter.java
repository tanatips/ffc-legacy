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
            return null;
        }
    }
    /**
     * แปลงวันที่และเวลาจากรูปแบบคริสต์ศักราช (yyyy-MM-dd HH:mm:ss) เป็นรูปแบบพุทธศักราช (dd/MM/yyyy HH:mm:ss)
     *
     * @param westernDateTime วันที่และเวลาในรูปแบบ yyyy-MM-dd HH:mm:ss
     * @return วันที่และเวลาในรูปแบบ dd/MM/yyyy HH:mm:ss โดยปีเป็นพุทธศักราช
     */
    public static String convertToThaiBuddhistDateTime(String westernDateTime) {
        try {
            // กำหนดรูปแบบวันที่และเวลา input (yyyy-MM-dd HH:mm:ss)
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            // แปลง String เป็น Date object
            Date date = inputFormat.parse(westernDateTime);

            // สร้าง Calendar object สำหรับคำนวณปีพุทธศักราช
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            // แปลงปีคริสต์ศักราชเป็นพุทธศักราช (บวก 543)
            int yearBE = cal.get(Calendar.YEAR) + 543;

            // กำหนดรูปแบบวันที่ output (dd/MM/)
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/", Locale.US);

            // กำหนดรูปแบบเวลา output (HH:mm:ss)
            SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm:ss", Locale.US);

            // รวมวันที่, ปีพุทธศักราช และเวลา
            String result = dateFormat.format(date) + yearBE + timeFormat.format(date);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * แปลงวันที่ปัจจุบันและเวลาปัจจุบันเป็นรูปแบบพุทธศักราช (dd/MM/yyyy HH:mm:ss)
     *
     * @return วันที่และเวลาปัจจุบันในรูปแบบ dd/MM/yyyy HH:mm:ss โดยปีเป็นพุทธศักราช
     */
    public static String getCurrentThaiBuddhistDateTime() {
        try {
            // รับวันที่และเวลาปัจจุบัน
            Calendar now = Calendar.getInstance();

            // แปลงปีคริสต์ศักราชเป็นพุทธศักราช (บวก 543)
            int yearBE = now.get(Calendar.YEAR) + 543;

            // กำหนดรูปแบบวันที่ (dd/MM/)
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/", Locale.US);

            // กำหนดรูปแบบเวลา (HH:mm:ss)
            SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm:ss", Locale.US);

            // รวมวันที่, ปีพุทธศักราช และเวลา
            String result = dateFormat.format(now.getTime()) + yearBE + timeFormat.format(now.getTime());

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * แปลงวันที่และเวลาจากรูปแบบพุทธศักราช (dd/MM/yyyy HH:mm:ss) เป็นรูปแบบคริสต์ศักราช (yyyy-MM-dd HH:mm:ss)
     *
     * @param thaiDateTime วันที่และเวลาในรูปแบบ dd/MM/yyyy HH:mm:ss โดยปีเป็นพุทธศักราช
     * @return วันที่และเวลาในรูปแบบ yyyy-MM-dd HH:mm:ss
     */
    public static String convertToWesternDateTime(String thaiDateTime) {
        try {
            // แยกวันที่และเวลา
            String[] parts = thaiDateTime.split(" ");
            if (parts.length < 2) {
                return null; // ข้อมูลไม่ครบถ้วน
            }

            String thaiDate = parts[0];
            String time = parts[1];

            // แยกวันที่
            String[] dateParts = thaiDate.split("/");
            if (dateParts.length != 3) {
                return null; // รูปแบบวันที่ไม่ถูกต้อง
            }

            int day = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int yearBE = Integer.parseInt(dateParts[2]);

            // แปลงปีพุทธศักราชเป็นคริสต์ศักราช (ลบ 543)
            int yearCE = yearBE - 543;

            // แยกเวลา
            String[] timeParts = time.split(":");
            if (timeParts.length != 3) {
                return null; // รูปแบบเวลาไม่ถูกต้อง
            }

            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int second = Integer.parseInt(timeParts[2]);

            // สร้าง Calendar
            Calendar calendar = Calendar.getInstance();
            calendar.set(yearCE, month - 1, day, hour, minute, second);

            // กำหนดรูปแบบวันที่และเวลา output (yyyy-MM-dd HH:mm:ss)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            // แปลงเป็น String ตามรูปแบบที่ต้องการ
            return outputFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
            return null;
        }
    }

    public static String getCurrentWesternDate() {
        try {
            // รับวันที่ปัจจุบัน
            Calendar now = Calendar.getInstance();

            // กำหนดรูปแบบวันที่ output (yyyy-MM-dd)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // แปลงเป็น String ตามรูปแบบที่ต้องการ
            return outputFormat.format(now.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * รับวันที่และเวลาปัจจุบันในรูปแบบคริสต์ศักราช (yyyy-MM-dd HH:mm:ss)
     *
     * @return วันที่และเวลาปัจจุบันในรูปแบบ yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentWesternDateTime() {
        try {
            // รับวันที่และเวลาปัจจุบัน
            Calendar now = Calendar.getInstance();
            // กำหนดรูปแบบวันที่และเวลา output (yyyy-MM-dd HH:mm:ss)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.US);
            // แปลงเป็น String ตามรูปแบบที่ต้องการ
            return outputFormat.format(now.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}