package th.in.ffc.app.form.nhso.model;
/**
 * โมเดลข้อมูลผู้ป่วย NHSO
 */
public class NHSOPatientInfo {
    private long id;

    // รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private String seq;

    // ประเภทเอกสารที่ใช้ในการยืนยันตัวตน
    private String type;

    // เลขประจำตัวประชาชน
    private String cid;

    // เลขหนังสือเดินทาง (Passport)
    private String ppn;

    // เลขประจำตัวคนพิการ
    private String pwd;

    // ชื่อ
    private String nameGiven;

    // นามสกุล
    private String nameFamily;

    // วันเกิด (ISO 8601 format: YYYY-MM-DD)
    private String birthDate;

    // เพศ (1=ชาย, 2=หญิง)
    private String gender;

    // ที่อยู่ บ้านเลขที่ หมู่ที่ หมู่บ้าน และซอย
    private String addressLine;

    // รหัสตำบล/แขวง
    private String addressCity;

    // รหัสอำเภอ/เขต
    private String addressDistrict;

    // รหัสจังหวัด
    private String addressState;

    // รหัสไปรษณีย์
    private String addressPostalCode;

    // สัญชาติ
    private String nationality;

    // เชื้อชาติ
    private String race;

    // หมายเลขประจำตัวผู้รับบริการ
    private String hn;

    // หมายเลขประจำตัวผู้ป่วยใน
    private String an;

    // ข้อมูลการส่งข้อมูล (false=ยังไม่ส่งไป NHSO, true=ส่งข้อมูลไป NHSO แล้ว)
    private boolean sentToNHSO;

    // ข้อมูลผู้ที่สร้างรายการ
    private String createdBy;
    private String createdDate;
    private String updatedBy;
    private String updatedDate;

    // Getters และ Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
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

    public boolean isSentToNHSO() {
        return sentToNHSO;
    }

    public void setSentToNHSO(boolean sentToNHSO) {
        this.sentToNHSO = sentToNHSO;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    // ชื่อเต็ม (firstname + lastname)
    public String getFullName() {
        return nameGiven + " " + nameFamily;
    }

    // แปลงค่าเพศเป็นข้อความ
    public String getGenderText() {
        if ("1".equals(gender)) {
            return "ชาย";
        } else if ("2".equals(gender)) {
            return "หญิง";
        } else {
            return "ไม่ระบุ";
        }
    }

    // ที่อยู่เต็มรูปแบบ
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();

        if (addressLine != null && !addressLine.isEmpty()) {
            address.append(addressLine);
        }

        if (addressCity != null && !addressCity.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append("ตำบล/แขวง ").append(addressCity);
        }

        if (addressDistrict != null && !addressDistrict.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append("อำเภอ/เขต ").append(addressDistrict);
        }

        if (addressState != null && !addressState.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append("จังหวัด ").append(addressState);
        }

        if (addressPostalCode != null && !addressPostalCode.isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(addressPostalCode);
        }

        return address.toString();
    }
}
