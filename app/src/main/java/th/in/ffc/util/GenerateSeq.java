package th.in.ffc.util;


import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;

public class  GenerateSeq {
    /**
     * สร้างเลข SEQ ตามรูปแบบ PCUCODE-YYYYMMDDhhmmss.sss
     * โดยมีความยาวเท่ากับ 20 ตัวอักษร
     *
     * @param pcuCode รหัส PCU
     * @return SEQ ในรูปแบบ PCUCODE-YYYYMMDDhhmmss.sss
     */
    public static String generateSeq(String pcuCode) {
        // ตรวจสอบ pcuCode ไม่ให้เป็น null
        if (pcuCode == null) {
            pcuCode = "";
        }

        // สร้างรูปแบบวันที่และเวลาปัจจุบัน
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
        String timestamp = dateFormat.format(new Date());

        // สร้าง SEQ โดยรวม pcuCode กับ timestamp
        String seq = pcuCode + timestamp;

        // ตรวจสอบและปรับความยาวให้เป็น 20 ตัวอักษร
        if (seq.length() > 20) {
            // ถ้ายาวเกินให้ตัดให้เหลือ 20 ตัวอักษรจากด้านขวา (เพื่อรักษา timestamp ไว้ให้มากที่สุด)
            seq = seq.substring(0,20);
        } else if (seq.length() < 20) {
            // ถ้าสั้นเกินไปให้เติม 0 ด้านหน้า pcuCode จนกว่าจะได้ความยาว 20
            StringBuilder padding = new StringBuilder();
            for (int i = 0; i < 20 - seq.length(); i++) {
                padding.append("0");
            }
            seq = padding.toString() + seq;
        }

        return seq;
    }

    /**
     * สร้างเลข SEQ ตามรูปแบบ PCUCODE-YYYYMMDDhhmmss.sss
     * โดยมีความยาวเท่ากับ 20 ตัวอักษร
     * กรณีที่ไม่ต้องการระบุ pcuCode
     *
     * @return SEQ ในรูปแบบ PCUCODE-YYYYMMDDhhmmss.sss
     */

}
