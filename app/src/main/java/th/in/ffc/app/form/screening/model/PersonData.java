package th.in.ffc.app.form.screening.model;

/**
 * Data class สำหรับเก็บข้อมูลบุคคลที่ใช้ในการประเมินความเสี่ยงโรคเบาหวาน
 */
public class PersonData {
    private Integer age;
    private String gender;
    private Double bmi;
    private Double waistCircumference;
    private Boolean hasHypertension;
    private Boolean hasFamilyDiabetesHistory;
    private Double fcbg; // Fasting Capillary Blood Glucose
    private Double fpg;  // Fasting Plasma Glucose

    // Constructors
    public PersonData() {}

    public PersonData(Integer age, String gender, Double bmi, Double waistCircumference,
                      Boolean hasHypertension, Boolean hasFamilyDiabetesHistory,
                      Double fcbg, Double fpg) {
        this.age = age;
        this.gender = gender;
        this.bmi = bmi;
        this.waistCircumference = waistCircumference;
        this.hasHypertension = hasHypertension;
        this.hasFamilyDiabetesHistory = hasFamilyDiabetesHistory;
        this.fcbg = fcbg;
        this.fpg = fpg;
    }

    // Getters and Setters
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public Boolean hasHypertension() {
        return hasHypertension;
    }

    public void setHasHypertension(Boolean hasHypertension) {
        this.hasHypertension = hasHypertension;
    }

    public Boolean hasFamilyDiabetesHistory() {
        return hasFamilyDiabetesHistory;
    }

    public void setHasFamilyDiabetesHistory(Boolean hasFamilyDiabetesHistory) {
        this.hasFamilyDiabetesHistory = hasFamilyDiabetesHistory;
    }

    public Double getFcbg() {
        return fcbg;
    }

    public void setFcbg(Double fcbg) {
        this.fcbg = fcbg;
    }

    public Double getFpg() {
        return fpg;
    }

    public void setFpg(Double fpg) {
        this.fpg = fpg;
    }

    @Override
    public String toString() {
        return "PersonData{" +
                "age=" + age +
                ", gender='" + gender + '\'' +
                ", bmi=" + bmi +
                ", waistCircumference=" + waistCircumference +
                ", hasHypertension=" + hasHypertension +
                ", hasFamilyDiabetesHistory=" + hasFamilyDiabetesHistory +
                ", fcbg=" + fcbg +
                ", fpg=" + fpg +
                '}';
    }
}