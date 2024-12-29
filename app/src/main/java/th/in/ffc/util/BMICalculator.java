package th.in.ffc.util;

public class BMICalculator {
    private float weight; // น้ำหนักเป็นกิโลกรัม
    private float height; // ส่วนสูงเป็นเซนติเมตร

    // Constructor
    public BMICalculator(float weight, float height) {
        this.weight = weight;
        this.height = height;
    }

    // Constructor ว่างเปล่า
    public BMICalculator() {
        this.weight = 0;
        this.height = 0;
    }

    // Setters
    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    // Getters
    public float getWeight() {
        return weight;
    }

    public float getHeight() {
        return height;
    }

    // คำนวณค่า BMI
    public float calculateBMI() {
        if (weight <= 0 || height <= 0) {
            throw new IllegalArgumentException("น้ำหนักและส่วนสูงต้องมากกว่า 0");
        }
        float heightInMeters = height / 100; // แปลงเซนติเมตรเป็นเมตร
        return Math.round(weight / (heightInMeters * heightInMeters));
    }

    // ประเมินเกณฑ์ BMI
    public String getBMICategory() {
        float bmi = calculateBMI();
        if (bmi < 18.5) {
            return "น้ำหนักต่ำกว่าเกณฑ์";
        } else if (bmi < 23) {
            return "สมส่วน";
        } else if (bmi < 25) {
            return "น้ำหนักเกิน";
        } else if (bmi < 30) {
            return "อ้วน";
        } else {
            return "อ้วนมาก";
        }
    }

    // ตรวจสอบความถูกต้องของข้อมูล
    public boolean isValidInput() {
        return weight > 0 && height > 0;
    }

}
