package th.in.ffc.app.form.screening.model;

import java.util.Date;

/**
 * Model class for NHSO Patient data
 * Based on NHSO Patient specification
 */
public class NHSOPatient { // แฟ้ม 1
    // Field 1: SEQ - รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private String seq;

    // Field 2: TYPE - ประเภทเอกสารที่ใช้ในการยืนยันตัวตน
    private String type;

    // Field 3: CID - เลขประจำตัวประชาชน
    private String cid;

    // Field 4: PPN - เลขหนังสือเดินทาง (Passport)
    private String ppn;

    // Field 5: PWD - เลขประจำตัวคนพิการ
    private String pwd;

    // Field 6: NAME_GIVEN - ชื่อ
    private String nameGiven;

    // Field 7: NAME_FAMILY - นามสกุล
    private String nameFamily;

    // Field 8: BIRTHDATE - วันเกิด (ISO 8601 format: YYYY-MM-DD)
    private Date birthDate;

    // Field 9: GENDER - เพศ (1=ชาย, 2=หญิง)
    private String gender;

    // Field 10: ADDRESS_LINE - ที่อยู่ บ้านเลขที่ หมู่ที่ หมู่บ้าน และซอย
    private String addressLine;

    // Field 11: ADDRESS_CITY - รหัสตำบล/แขวง
    private String addressCity;

    // Field 12: ADDRESS_DISTRICT - รหัสอำเภอ/เขต
    private String addressDistrict;

    // Field 13: ADDRESS_STATE - รหัสจังหวัด
    private String addressState;

    // Field 14: ADDRESS_POSTAL_CODE - รหัสไปรษณีย์
    private String addressPostalCode;

    // Field 15: NATIONALITY - สัญชาติ
    private String nationality;

    // Field 16: RACE - เชื้อชาติ
    private String race;

    // Field 17: HN - หมายเลขประจำตัวผู้รับบริการ
    private String hn;

    // Field 18: AN - หมายเลขประจำตัวผู้ป่วยใน
    private String an;

    // Constructor
    public NHSOPatient() {
        // Default constructor
    }

    // Getters and Setters
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNameGiven() {
        return nameGiven;
    }

    public void setNameGiven(String nameGiven) {
        this.nameGiven = nameGiven;
    }

    public String getNameFamily() {
        return nameFamily;
    }

    public void setNameFamily(String nameFamily) {
        this.nameFamily = nameFamily;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressDistrict() {
        return addressDistrict;
    }

    public void setAddressDistrict(String addressDistrict) {
        this.addressDistrict = addressDistrict;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressPostalCode() {
        return addressPostalCode;
    }

    public void setAddressPostalCode(String addressPostalCode) {
        this.addressPostalCode = addressPostalCode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    @Override
    public String toString() {
        return "NHSOPatient{" +
                "seq='" + seq + '\'' +
                ", type='" + type + '\'' +
                ", cid='" + cid + '\'' +
                ", nameGiven='" + nameGiven + '\'' +
                ", nameFamily='" + nameFamily + '\'' +
                ", birthDate=" + birthDate +
                ", gender='" + gender + '\'' +
                '}';
    }
}
