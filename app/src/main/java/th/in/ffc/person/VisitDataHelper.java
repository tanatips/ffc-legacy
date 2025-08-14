package th.in.ffc.person;

import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.util.AgeCalculator;

import java.util.Calendar;

/**
 * Helper class สำหรับสร้างข้อมูลที่ใช้ในการบันทึก Visit
 * ตามเงื่อนไขการคัดกรองสุขภาพแต่ละช่วงอายุ
 */
public class VisitDataHelper {

    private PersonInfo personInfo;
    private StressDepressionInfo stressDepressionInfo;
    private HealthRiskAssessmentInfo healthRiskAssessmentInfo;
    private CardiovascularRiskInfo cardiovascularRiskInfo;
    private CounselingInfo counselingInfo;
    private boolean hasTobaccoUse;
    private boolean hasAlcoholUse;
    private boolean has2QAbnormalResult;

    /**
     * Constructor
     */
    public VisitDataHelper(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    /**
     * กำหนดข้อมูลการประเมินสุขภาพต่างๆ
     */
    public VisitDataHelper setStressDepressionInfo(StressDepressionInfo stressDepressionInfo) {
        this.stressDepressionInfo = stressDepressionInfo;
        return this;
    }

    public VisitDataHelper setHealthRiskAssessmentInfo(HealthRiskAssessmentInfo healthRiskAssessmentInfo) {
        this.healthRiskAssessmentInfo = healthRiskAssessmentInfo;
        return this;
    }

    public VisitDataHelper setCardiovascularRiskInfo(CardiovascularRiskInfo cardiovascularRiskInfo) {
        this.cardiovascularRiskInfo = cardiovascularRiskInfo;
        return this;
    }

    public VisitDataHelper setCounselingInfo(CounselingInfo counselingInfo) {
        this.counselingInfo = counselingInfo;
        return this;
    }

    public VisitDataHelper setSubstanceUse(boolean hasTobaccoUse, boolean hasAlcoholUse) {
        this.hasTobaccoUse = hasTobaccoUse;
        this.hasAlcoholUse = hasAlcoholUse;
        return this;
    }

    public VisitDataHelper setDepressionResult(boolean has2QAbnormalResult) {
        this.has2QAbnormalResult = has2QAbnormalResult;
        return this;
    }

    /**
     * สร้างข้อมูล Visit ที่สมบูรณ์
     */
    public VisitData prepareVisitData() {
        if (personInfo == null || personInfo.getBirthday() == null) {
            throw new IllegalStateException("PersonInfo และวันเกิดจำเป็นต้องมีข้อมูล");
        }

        int age = AgeCalculator.calculateAge(personInfo.getBirthday());

        VisitData visitData = new VisitData();
        visitData.symptoms = getSymptoms(age);
        visitData.symptomsco = getSymptomsco(age);
        visitData.vitalcheck = getVitalcheck(age);
        visitData.diagnote = getDiagnote(age);
        visitData.healthsuggest1 = getHealthsuggest1(age);

        return visitData;
    }

    /**
     * คำนวณปีงบประมาณ (เริ่มเดือนตุลาคม)
     */
    private String getBudgetYear() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH เริ่มจาก 0

        // ปีงบประมาณเริ่มตั้งแต่เดือนตุลาคม (เดือน 10)
        if (currentMonth >= 10) {
            return String.valueOf(currentYear + 543 + 1); // เพิ่ม 1 สำหรับปีงบประมาณ
        } else {
            return String.valueOf(currentYear + 543);
        }
    }

    /**
     * สร้างข้อความ symptoms ตามช่วงอายุ
     */
    private String getSymptoms(int age) {
        String budgetYear = getBudgetYear();

        if (age >= 15 && age <= 34) {
            return "บริการตรวจคัดกรองและประเมินปัจจัยเสี่ยงต่อสุขภาพกาย/สุขภาพจิต อายุ 15-34ปี ปีงบประมาณ " + budgetYear;
        } else if (age >= 35 && age <= 59) {
            return "บริการตรวจคัดกรองและประเมินปัจจัยเสี่ยงต่อสุขภาพกาย/สุขภาพจิต อายุ 35-59 ปี ปีงบประมาณ " + budgetYear;
        }

        return "บริการตรวจคัดกรองและประเมินปัจจัยเสี่ยงต่อสุขภาพกาย/สุขภาพจิต";
    }

    /**
     * สร้างข้อความ symptomsco ตามช่วงอายุ
     */
    private String getSymptomsco(int age) {
        if (age >= 15 && age <= 34) {
            return "บริการตรวจประเมินดัชนีมวลกาย ชั่งน้ำหนัก วัดส่วนสูง และวัดรอบเอว วัดความโลหิต ประเมินความเสี่ยงจากสูบบุหรี่ ดื่มแอลกอฮอล์ และการใช้สารเสพติด ประเมินภาวะซึมเศร้า (2Q) ความเครียด(ST-5) และให้ปรึกษาและคำแนะนำปรับเปลี่ยนพฤติกรรมรายบุคคล";
        } else if (age >= 35 && age <= 59) {
            return "บริการเจาะเลือดตรวจค่าน้ำตาล FCG หลังงดน้ำงดอาหาร 8 ชั่วโมง  ประเมินดัชนีมวลกาย ชั่งน้ำหนัก วัดส่วนสูง และวัดรอบเอว วัดความโลหิต ประเมินความเสี่ยงจากสูบบุหรี่ ดื่มแอลกอฮอล์ และการใช้สารเสพติด ประเมินภาวะซึมเศร้า (2Q) ความเครียด(ST-5) ประเมินความเสี่ยงต่อโรคเบาหวาน โรคหัวใจและหลอดเลือด  ให้ปรึกษาและคำแนะนำปรับเปลี่ยนพฤติกรรมรายบุคคล";
        }

        return "บริการประเมินสุขภาพรายบุคคล";
    }

    /**
     * สร้างข้อความ vitalcheck ตามผลการประเมิน
     */
    private String getVitalcheck(int age) {
        StringBuilder vitalcheck = new StringBuilder();

        // ดัชนีมวลกาย (BMI)
        String bmiStatus = getBMIStatus();
        vitalcheck.append("ค่าดัชนีมวลกายอยู่ในเกณฑ์=").append(bmiStatus);

        // รอบเอว
        String waistStatus = getWaistStatus();
        vitalcheck.append(" เส้นรอบเอว=").append(waistStatus);

        // ความดันโลหิต
        String bpStatus = getBloodPressureStatus();
        vitalcheck.append(" ความดันโลหิต=").append(bpStatus);

        // การสูบบุหรี่
        String smokingStatus = getSmokingStatus();
        vitalcheck.append(" ").append(smokingStatus);

        // การดื่มแอลกอฮอล์
        String alcoholStatus = getAlcoholStatus();
        vitalcheck.append(" ").append(alcoholStatus);

        // ภาวะซึมเศร้า
        String depressionStatus = getDepressionStatus();
        vitalcheck.append(" ").append(depressionStatus);

        // ความเครียด
        String stressStatus = getStressStatus();
        vitalcheck.append(" ").append(stressStatus);

        // สำหรับอายุ 35-59 ปี เพิ่มข้อมูลน้ำตาลและความเสี่ยงโรคหัวใจ
        if (age >= 35 && age <= 59) {
            String fcgValue = getFCGValue();
            vitalcheck.append(" ระดับน้ำตาล (FCG)= ").append(fcgValue).append(" mg/dl");

            String heartRiskStatus = getCardiovascularRiskStatus();
            vitalcheck.append(" ").append(heartRiskStatus);
        }

        return vitalcheck.toString();
    }

    /**
     * สร้างข้อความ diagnote (เหมือนกับ symptoms)
     */
    private String getDiagnote(int age) {
        return getSymptoms(age);
    }

    /**
     * สร้างข้อความ healthsuggest1 ตามช่วงอายุ
     */
    private String getHealthsuggest1(int age) {
        if (age >= 15 && age <= 34) {
            return "ออกกำลังกายสม่ำเสมออย่างน้อย30นาทีต่อวัน ควบคุมน้ำหนักไม่ให้อ้วน งดบุหรี่ ลดแอลกอฮอล์";
        } else if (age >= 35 && age <= 59) {
            return "หลีกเลี่ยงอาหาร หวาน มัน เค็ม  ออกกำลังกายสม่ำเสมออย่างน้อย30นาทีต่อวัน ควบคุมน้ำหนักไม่ให้อ้วน งดบุหรี่ ลดแอลกอฮอล์ ฝึกหายใจ เพื่อคลายความเครียด";
        }

        return "ออกกำลังกายสม่ำเสมอและดูแลสุขภาพ";
    }

    // ========== Helper Methods สำหรับประเมินสถานะต่างๆ ==========

    private String getBMIStatus() {
        if (personInfo != null && personInfo.getWeight() > 0 && personInfo.getHeight() > 0) {
            double heightInMeters = personInfo.getHeight() / 100.0;
            double bmi = personInfo.getWeight() / (heightInMeters * heightInMeters);

            if (bmi < 18.5) {
                return "น้ำหนักน้อย";
            } else if (bmi >= 18.5 && bmi < 25) {
                return "ปกติ";
            } else if (bmi >= 25 && bmi < 30) {
                return "น้ำหนักเกิน";
            } else {
                return "อ้วน";
            }
        }
        return "ปกติ";
    }

    private String getWaistStatus() {
        if (personInfo != null && personInfo.getWaist_size() > 0) {
            boolean isMale = "M".equals(personInfo.getGender());
            double waistSize = personInfo.getWaist_size();

            if ((isMale && waistSize < 90) || (!isMale && waistSize < 80)) {
                return "ปกติ";
            } else {
                return "เกินเกณฑ์";
            }
        }
        return "ปกติ";
    }

    private String getBloodPressureStatus() {
        if (personInfo != null && personInfo.getSystolic_pressure() > 0 && personInfo.getDiastolic_pressure() > 0) {
            double systolic = personInfo.getSystolic_pressure();
            double diastolic = personInfo.getDiastolic_pressure();

            if (systolic >= 140 || diastolic >= 90) {
                return "สูง";
            } else if (systolic >= 120 || diastolic >= 80) {
                return "เสี่ยงสูง";
            } else {
                return "ปกติ";
            }
        }
        return "ปกติ";
    }

    private String getSmokingStatus() {
        if (hasTobaccoUse) {
            return "สูบบุหรี่";
        }
        return "ไม่สูบบุหรี่";
    }

    private String getAlcoholStatus() {
        if (hasAlcoholUse) {
            return "ดื่มสุรา";
        }
        return "ไม่ดื่มสุรา";
    }

    private String getDepressionStatus() {
        if (has2QAbnormalResult) {
            return "มีภาวะซึมเศร้า";
        }
        return "ไม่มีภาวะซึมเศร้า";
    }

    private String getStressStatus() {
        if (stressDepressionInfo != null) {
            int stressScore = stressDepressionInfo.getSum();
            if (stressScore >= 10) {
                return "มีความเครียด";
            }
        }
        return "ไม่มีความเครียด";
    }

    private String getFCGValue() {
        if (healthRiskAssessmentInfo != null &&
                healthRiskAssessmentInfo.getFcbg() != null &&
                !healthRiskAssessmentInfo.getFcbg().isEmpty()) {
            return healthRiskAssessmentInfo.getFcbg();
        }
        return "ไม่ระบุ";
    }

    private String getCardiovascularRiskStatus() {
        if (cardiovascularRiskInfo != null &&
                cardiovascularRiskInfo.getRiskLevel() != null &&
                !cardiovascularRiskInfo.getRiskLevel().isEmpty()) {
            String riskLevel = cardiovascularRiskInfo.getRiskLevel();
            if (riskLevel.contains("สูง")) {
                return "มีความเสี่ยงต่อการเกิดโรคหัวใจและหลอดเลือด";
            } else if (riskLevel.contains("ปานกลาง")) {
                return "มีความเสี่ยงปานกลางต่อการเกิดโรคหัวใจและหลอดเลือด";
            }
        }
        return "ไม่พบความเสี่ยงต่อการเกิดโรคหัวใจและหลอดเลือด";
    }

    /**
     * Inner class สำหรับเก็บข้อมูล Visit
     */
    public static class VisitData {
        public String symptoms;
        public String symptomsco;
        public String vitalcheck;
        public String diagnote;
        public String healthsuggest1;

        @Override
        public String toString() {
            return "VisitData{" +
                    "symptoms='" + symptoms + '\'' +
                    ", symptomsco='" + symptomsco + '\'' +
                    ", vitalcheck='" + vitalcheck + '\'' +
                    ", diagnote='" + diagnote + '\'' +
                    ", healthsuggest1='" + healthsuggest1 + '\'' +
                    '}';
        }
    }
}