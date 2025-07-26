package th.in.ffc.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Helper class สำหรับจัดการโหมดการตรวจสอบข้อมูล
 */
public class ValidationModeHelper {

    public static final String PREFS_NAME = "app_preferences";
    public static final String PREFS_VALIDATION_MODE = "validation_mode";
    public static final String MODE_STRICT = "strict";     // ตรวจสอบปกติ (ครบทุกแบบประเมิน)
    public static final String MODE_PARTIAL = "partial";   // ตรวจสอบบางส่วน (อย่างน้อย 1 ในแต่ละหัวข้อ)

    /**
     * ตั้งค่าโหมดการตรวจสอบ
     */
    public static void setValidationMode(Context context, String mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREFS_VALIDATION_MODE, mode).apply();
    }

    /**
     * อ่านโหมดการตรวจสอบปัจจุบัน
     */
    public static String getValidationMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREFS_VALIDATION_MODE, MODE_STRICT);
    }

    /**
     * ตรวจสอบว่าเป็นโหมดบางส่วนหรือไม่
     */
    public static boolean isPartialMode(Context context) {
        return MODE_PARTIAL.equals(getValidationMode(context));
    }

    /**
     * ตรวจสอบว่าเป็นโหมดปกติหรือไม่
     */
    public static boolean isStrictMode(Context context) {
        return MODE_STRICT.equals(getValidationMode(context));
    }

    /**
     * ดึงข้อความแสดงโหมดปัจจุบัน
     */
    public static String getDisplayText(Context context) {
        String mode = getValidationMode(context);
        if (MODE_PARTIAL.equals(mode)) {
            return "⚡ โหมดบางส่วน";
        } else {
            return "🔍 โหมดปกติ";
        }
    }

    /**
     * ดึงคำอธิบายโหมดปัจจุบัน
     */
    public static String getDescription(Context context) {
        String mode = getValidationMode(context);
        if (MODE_PARTIAL.equals(mode)) {
            return "ทำอย่างน้อย 1 แบบประเมินในแต่ละหัวข้อใหญ่";
        } else {
            return "ต้องทำแบบประเมินครบทุกรายการตามเงื่อนไข";
        }
    }

    /**
     * ดึงรายละเอียดแต่ละหมวดสำหรับโหมดบางส่วน
     */
    public static String getPartialModeDetails() {
        StringBuilder details = new StringBuilder();
        details.append("📋 โหมดบางส่วน - ทำอย่างน้อย 1 ในแต่ละหัวข้อ:\n\n");

        details.append("1️⃣ การคัดกรองสารเสพติด:\n");
        details.append("   • แบบคัดกรองการใช้สารเสพติด (จำเป็น)\n\n");

        details.append("2️⃣ ภาวะเครียด-ซึมเศร้า (เลือก 1 จาก 4):\n");
        details.append("   • ประเมินภาวะเครียด-ซึมเศร้า (ST-5)\n");
        details.append("   • คัดกรองโรคซึมเศร้าด้วย 2 คำถาม (2Q)\n");
        details.append("   • คัดกรองโรคซึมเศร้าด้วย 9 คำถาม (9Q)\n");
        details.append("   • การประเมินการฆ่าตัวตายด้วย 8 คำถาม (8Q)\n\n");

        details.append("3️⃣ ความเสี่ยงด้านสุขภาพ (เลือก 1 จาก 2):\n");
        details.append("   • แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน\n");
        details.append("   • คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด\n\n");

        details.append("4️⃣ สรุปผลการคัดกรอง:\n");
        details.append("   • ให้คำปรึกษาและแนะนำ (จำเป็น)");

        return details.toString();
    }

    /**
     * ดึงรายละเอียดโหมดปกติ
     */
    public static String getStrictModeDetails() {
        StringBuilder details = new StringBuilder();
        details.append("🔍 โหมดปกติ - ต้องทำครบทุกแบบประเมิน:\n\n");

        details.append("1️⃣ การคัดกรองสารเสพติด:\n");
        details.append("   • แบบคัดกรองการใช้สารเสพติด (จำเป็นเสมอ)\n");
        details.append("   • คัดกรองความเสี่ยงจากการสูบบุหรี่ (ถ้าเคยสูบ)\n");
        details.append("   • แบบทดสอบการติดบุหรี่ (ถ้าเคยสูบ)\n");
        details.append("   • คัดกรองความเสี่ยงจากการดื่มสุรา (ถ้าเคยดื่ม)\n\n");

        details.append("2️⃣ ภาวะเครียด-ซึมเศร้า (ทำทุกรายการ):\n");
        details.append("   • ประเมินภาวะเครียด-ซึมเศร้า (ST-5)\n");
        details.append("   • คัดกรองโรคซึมเศร้าด้วย 2 คำถาม (2Q)\n");
        details.append("   • คัดกรองโรคซึมเศร้าด้วย 9 คำถาม (9Q)\n");
        details.append("   • การประเมินการฆ่าตัวตายด้วย 8 คำถาม (8Q)\n\n");

        details.append("3️⃣ ความเสี่ยงด้านสุขภาพ (ทำทุกรายการ):\n");
        details.append("   • แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน\n");
        details.append("   • คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด\n\n");

        details.append("4️⃣ สรุปผลการคัดกรอง:\n");
        details.append("   • ให้คำปรึกษาและแนะนำ (จำเป็นเสมอ)");

        return details.toString();
    }

    /**
     * สลับโหมดการตรวจสอบ
     */
    public static String toggleMode(Context context) {
        String currentMode = getValidationMode(context);
        String newMode = MODE_STRICT.equals(currentMode) ? MODE_PARTIAL : MODE_STRICT;
        setValidationMode(context, newMode);
        return newMode;
    }

    /**
     * รีเซ็ตเป็นโหมดเริ่มต้น (ปกติ)
     */
    public static void resetToDefault(Context context) {
        setValidationMode(context, MODE_STRICT);
    }

    /**
     * ตรวจสอบความถูกต้องของโหมด
     */
    public static boolean isValidMode(String mode) {
        return MODE_STRICT.equals(mode) || MODE_PARTIAL.equals(mode);
    }
}