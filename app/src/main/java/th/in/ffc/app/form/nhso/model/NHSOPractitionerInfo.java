package th.in.ffc.app.form.nhso.model;

import java.io.Serializable;

/**
 * Model คลาสสำหรับเก็บข้อมูลผู้ให้บริการ NHSO (แฟ้ม 3)
 * ข้อมูลผู้ให้บริการสุขภาพของหน่วยบริการที่ให้บริการผู้เข้ารับบริการ
 */
public class NHSOPractitionerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String seq; // รหัสการบริการที่กำหนด (Visit Number)
    private String hcode; // รหัสสถานพยาบาล
    private String cid; // เลขประจำตัวประชาชน
    private String professionId; // เลขใบอนุญาตประกอบวิชาชีพ
    private String council; // รหัสสภาวิชาชีพ
    private String providerType; // รหัสประเภทบุคลากร
    private String nameGiven; // ชื่อ
    private String nameFamily; // นามสกุล
    private boolean syncStatus; // สถานะการซิงค์ข้อมูล (true = ซิงค์แล้ว, false = ยังไม่ซิงค์)
    private String createdBy; // ผู้สร้างข้อมูล
    private String createdDate; // วันที่สร้างข้อมูล
    private String updatedBy; // ผู้อัปเดตข้อมูล
    private String updatedDate; // วันที่อัปเดตข้อมูล

    // Constructor
    public NHSOPractitionerInfo() {
        // Default constructor
    }

    /**
     * Constructor with required fields
     */
    public NHSOPractitionerInfo(String seq, String hcode, String cid) {
        this.seq = seq;
        this.hcode = hcode;
        this.cid = cid;
        this.syncStatus = false;
    }

    /**
     * Constructor with all fields
     */
    public NHSOPractitionerInfo(String seq, String hcode, String cid, String professionId,
                                String council, String providerType, String nameGiven, String nameFamily) {
        this.seq = seq;
        this.hcode = hcode;
        this.cid = cid;
        this.professionId = professionId;
        this.council = council;
        this.providerType = providerType;
        this.nameGiven = nameGiven;
        this.nameFamily = nameFamily;
        this.syncStatus = false;
    }

    // Getters and Setters
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

    public String getHcode() {
        return hcode;
    }

    public void setHcode(String hcode) {
        this.hcode = hcode;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getProfessionId() {
        return professionId;
    }

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public String getCouncil() {
        return council;
    }

    public void setCouncil(String council) {
        this.council = council;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
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

    public boolean isSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(boolean syncStatus) {
        this.syncStatus = syncStatus;
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

    /**
     * ดึงชื่อผู้ให้บริการฉบับเต็ม
     * @return ชื่อ-นามสกุลผู้ให้บริการ
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (nameGiven != null && !nameGiven.isEmpty()) {
            sb.append(nameGiven);
        }
        if (nameFamily != null && !nameFamily.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(nameFamily);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "NHSOPractitionerInfo{" +
                "id=" + id +
                ", seq='" + seq + '\'' +
                ", hcode='" + hcode + '\'' +
                ", cid='" + cid + '\'' +
                ", professionId='" + professionId + '\'' +
                ", council='" + council + '\'' +
                ", providerType='" + providerType + '\'' +
                ", nameGiven='" + nameGiven + '\'' +
                ", nameFamily='" + nameFamily + '\'' +
                ", syncStatus=" + syncStatus +
                '}';
    }
}