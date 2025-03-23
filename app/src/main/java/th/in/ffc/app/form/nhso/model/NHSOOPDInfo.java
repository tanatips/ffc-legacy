package th.in.ffc.app.form.nhso.model;

import java.util.Date;

/**
 * แฟ้มที่ 4 NHSO OPD - ข้อมูลการมารับบริการผู้ป่วยนอก (OPD) ของผู้เข้ารับบริการ
 */
public class NHSOOPDInfo {

    // 1. รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private long id;
    private String seq;

    // 2. วัน เวลา ที่รับบริการ บันทึก ปี ไทย เป็น ค.ศ. (รูปแบบ ISO 8601)
    private Date dateOPD;

    // 3. สิทธิการรักษาที่ใช้
    private String inscl;

    // 4. รหัส Claim Code/เลขอนุมัติ/เลข Approve code
    private String permitNo;

    // 5. ประเภทสถานพยาบาลที่รักษา (1=Main Contractor, 2=Sub Contractor, 3=Supra Contractor, 4=Excellent, 5=Super tertiary)
    private String htype;

    // 6. การใช้สิทธิ (1=ใช้สิทธิ, 2=ไม่ใช้สิทธิ ไม่ขอเบิก, 3=ผลงานบริการ)
    private String uuc;

    // 7. อาการสำคัญที่มารับบริการ
    private String chiefcomp;

    // 8. อุณหภูมิร่างกาย (องศาเซลเซียส, ไม่เกิน 2 หลัก และทศนิยม 1 ตำแหน่ง)
    private Double btemp;

    // 9. ความดันโลหิตค่าบน (mmHg, ไม่เกิน 3 หลัก)
    private Integer sbp;

    // 10. ความดันโลหิตค่าล่าง (mmHg, ไม่เกิน 3 หลัก)
    private Integer dbp;

    // 11. อัตราการเต้นหัวใจ (ครั้งต่อนาที, ไม่เกิน 3 หลัก)
    private Integer pr;

    // 12. อัตราการหายใจ (ครั้งต่อนาที, ไม่เกิน 3 หลัก)
    private Integer rr;

    // 13. รอบเอว (เซนติเมตร, ไม่เกิน 3 หลัก)
    private Integer waistline;

    // 14. น้ำหนัก (กก., ไม่เกิน 3 หลัก และทศนิยม 1 ตำแหน่ง)
    private Double weight;

    // 15. ส่วนสูง (ซม., ไม่เกิน 3 หลัก)
    private Integer height;

    // 16. เส้นรอบศีรษะ (ซม., ไม่เกิน 3 หลัก และทศนิยม 1 ตำแหน่ง)
    private Double headcircum;

    // 17. แผนกที่รักษาผู้ป่วยเป็นหลัก (01=อายุรกรรม, 02=ศัลยกรรม, 03=สูติกรรม, 04=นรีเวชกรรม, 05=กุมารเวช, ฯลฯ)
    private String clinic;

    // Getters and Setters
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public Date getDateOPD() {
        return dateOPD;
    }

    public void setDateOPD(Date dateOPD) {
        this.dateOPD = dateOPD;
    }

    public String getInscl() {
        return inscl;
    }

    public void setInscl(String inscl) {
        this.inscl = inscl;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getHtype() {
        return htype;
    }

    public void setHtype(String htype) {
        this.htype = htype;
    }

    public String getUuc() {
        return uuc;
    }

    public void setUuc(String uuc) {
        this.uuc = uuc;
    }

    public String getChiefcomp() {
        return chiefcomp;
    }

    public void setChiefcomp(String chiefcomp) {
        this.chiefcomp = chiefcomp;
    }

    public Double getBtemp() {
        return btemp;
    }

    public void setBtemp(Double btemp) {
        this.btemp = btemp;
    }

    public Integer getSbp() {
        return sbp;
    }

    public void setSbp(Integer sbp) {
        this.sbp = sbp;
    }

    public Integer getDbp() {
        return dbp;
    }

    public void setDbp(Integer dbp) {
        this.dbp = dbp;
    }

    public Integer getPr() {
        return pr;
    }

    public void setPr(Integer pr) {
        this.pr = pr;
    }

    public Integer getRr() {
        return rr;
    }

    public void setRr(Integer rr) {
        this.rr = rr;
    }

    public Integer getWaistline() {
        return waistline;
    }

    public void setWaistline(Integer waistline) {
        this.waistline = waistline;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getHeadcircum() {
        return headcircum;
    }

    public void setHeadcircum(Double headcircum) {
        this.headcircum = headcircum;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}