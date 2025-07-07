package th.in.ffc.model;

import java.io.Serializable;

/**
 * Person Model Class
 * สำหรับเก็บข้อมูลบุคคลในระบบ FFC
 *
 * @author Generated from PersonProvider
 * @version 1.0
 */
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    // Primary Keys
    private String pid;
    private String pcuCodePerson;

    // Personal Information
    private String idCard;           // เลขบัตรประชาชน
    private String prename;          // คำนำหน้าชื่อ
    private String firstName;        // ชื่อ
    private String lastName;         // นามสกุล
    private String nickname;         // ชื่อเล่น
    private String birth;            // วันเกิด
    private String sex;              // เพศ
    private String bloodGroup;       // หมู่เลือด
    private String bloodRh;          // RH
    private String allergic;         // ประวัติแพ้ยา
    private String occupa;           // อาชีพ
    private String education;        // การศึกษา
    private String nation;           // เชื้อชาติ
    private String origin;           // สัญชาติ
    private String religion;         // ศาสนา
    private String income;           // รายได้

    // Address & House
    private String hcode;            // รหัสบ้าน

    // Family Information
    private String marryStatus;      // สถานะสมรส
    private String familyNo;         // หมายเลขครอบครัว
    private String familyPosition;   // ตำแหน่งในครอบครัว

    // Rights Information
    private String rightCode;        // รหัสสิทธิ
    private String rightNo;          // เลขที่สิทธิ
    private String rightHmain;       // รพ.หลัก
    private String rightHsub;        // รพ.ย่อย

    // Contact
    private String tel;              // เบอร์โทรศัพท์

    // System Fields
    private String dateUpdate;       // วันที่ปรับปรุงข้อมูล

    // Constructors
    public Person() {
    }

    public Person(String pid, String idCard, String firstName, String lastName) {
        this.pid = pid;
        this.idCard = idCard;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getter and Setter methods

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPcuCodePerson() {
        return pcuCodePerson;
    }

    public void setPcuCodePerson(String pcuCodePerson) {
        this.pcuCodePerson = pcuCodePerson;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getBloodRh() {
        return bloodRh;
    }

    public void setBloodRh(String bloodRh) {
        this.bloodRh = bloodRh;
    }

    public String getAllergic() {
        return allergic;
    }

    public void setAllergic(String allergic) {
        this.allergic = allergic;
    }

    public String getOccupa() {
        return occupa;
    }

    public void setOccupa(String occupa) {
        this.occupa = occupa;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getHcode() {
        return hcode;
    }

    public void setHcode(String hcode) {
        this.hcode = hcode;
    }

    public String getMarryStatus() {
        return marryStatus;
    }

    public void setMarryStatus(String marryStatus) {
        this.marryStatus = marryStatus;
    }

    public String getFamilyNo() {
        return familyNo;
    }

    public void setFamilyNo(String familyNo) {
        this.familyNo = familyNo;
    }

    public String getFamilyPosition() {
        return familyPosition;
    }

    public void setFamilyPosition(String familyPosition) {
        this.familyPosition = familyPosition;
    }

    public String getRightCode() {
        return rightCode;
    }

    public void setRightCode(String rightCode) {
        this.rightCode = rightCode;
    }

    public String getRightNo() {
        return rightNo;
    }

    public void setRightNo(String rightNo) {
        this.rightNo = rightNo;
    }

    public String getRightHmain() {
        return rightHmain;
    }

    public void setRightHmain(String rightHmain) {
        this.rightHmain = rightHmain;
    }

    public String getRightHsub() {
        return rightHsub;
    }

    public void setRightHsub(String rightHsub) {
        this.rightHsub = rightHsub;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    // Utility methods

    /**
     * ได้ชื่อเต็ม (คำนำหน้า + ชื่อ + นามสกุล)
     * @return ชื่อเต็ม
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();

        if (prename != null && !prename.trim().isEmpty()) {
            fullName.append(prename.trim()).append(" ");
        }

        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }

        return fullName.toString().trim();
    }

    /**
     * ได้ชื่อ-นามสกุล (ไม่มีคำนำหน้า)
     * @return ชื่อ-นามสกุล
     */
    public String getName() {
        StringBuilder name = new StringBuilder();

        if (firstName != null && !firstName.trim().isEmpty()) {
            name.append(firstName.trim());
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (name.length() > 0) {
                name.append(" ");
            }
            name.append(lastName.trim());
        }

        return name.toString().trim();
    }

    /**
     * ตรวจสอบว่าเป็นเพศชายหรือไม่
     * @return true หากเป็นเพศชาย
     */
    public boolean isMale() {
        return "1".equals(sex) || "M".equalsIgnoreCase(sex) || "Male".equalsIgnoreCase(sex);
    }

    /**
     * ตรวจสอบว่าเป็นเพศหญิงหรือไม่
     * @return true หากเป็นเพศหญิง
     */
    public boolean isFemale() {
        return "2".equals(sex) || "F".equalsIgnoreCase(sex) || "Female".equalsIgnoreCase(sex);
    }

    /**
     * ตรวจสอบว่ามีเลขบัตรประชาชนหรือไม่
     * @return true หากมีเลขบัตรประชาชน
     */
    public boolean hasIdCard() {
        return idCard != null && idCard.trim().length() == 13 && idCard.matches("\\d{13}");
    }

    /**
     * แสดงข้อมูลแบบ formatted สำหรับ display
     * @return ข้อมูลสำหรับแสดง
     */
    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();

        // ชื่อ-นามสกุล
        info.append(getFullName());

        // เลขบัตรประชาชน
        if (hasIdCard()) {
            info.append(" (").append(idCard).append(")");
        }

        // PID
        if (pid != null && !pid.trim().isEmpty()) {
            info.append(" PID: ").append(pid);
        }

        return info.toString();
    }

    @Override
    public String toString() {
        return "Person{" +
                "pid='" + pid + '\'' +
                ", idCard='" + idCard + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", sex='" + sex + '\'' +
                ", birth='" + birth + '\'' +
                ", hcode='" + hcode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        // เปรียบเทียบด้วย PID เป็นหลัก
        if (pid != null && person.pid != null) {
            return pid.equals(person.pid);
        }

        // หากไม่มี PID ให้เปรียบเทียบด้วยเลขบัตรประชาชน
        if (idCard != null && person.idCard != null) {
            return idCard.equals(person.idCard);
        }

        return false;
    }

    @Override
    public int hashCode() {
        // ใช้ PID เป็นหลักในการสร้าง hashCode
        if (pid != null) {
            return pid.hashCode();
        }

        // หากไม่มี PID ให้ใช้เลขบัตรประชาชน
        if (idCard != null) {
            return idCard.hashCode();
        }

        return super.hashCode();
    }
}
