package th.in.ffc.app.form.nhso.model;

import java.io.Serializable;

/**
 * Model คลาสสำหรับเก็บข้อมูลสถานพยาบาล NHSO (แฟ้ม 2)
 */
public class NHSOHospitalInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String seq; // รหัสการบริการที่กำหนด (Visit Number)
    private String hcode; // รหัสสถานพยาบาลให้บริการ
    private String hcodeName; // ชื่อสถานพยาบาลให้บริการ
    private String hcodeSend; // รหัสสถานพยาบาลส่งเบิก
    private String hcodeSendName; // ชื่อสถานพยาบาลส่งเบิก
    private String hmain; // รหัสสถานพยาบาลประจำ
    private String hmainName; // ชื่อสถานพยาบาลประจำ
    private boolean syncStatus; // สถานะการซิงค์ข้อมูล (true = ซิงค์แล้ว, false = ยังไม่ซิงค์)
    private String createdBy; // ผู้สร้างข้อมูล
    private String createdDate; // วันที่สร้างข้อมูล
    private String updatedBy; // ผู้อัปเดตข้อมูล
    private String updatedDate; // วันที่อัปเดตข้อมูล

    // Constructor
    public NHSOHospitalInfo() {
        // Default constructor
    }

    public NHSOHospitalInfo(String seq, String hcode, String hcodeName, String hcodeSend,
                            String hcodeSendName, String hmain, String hmainName) {
        this.seq = seq;
        this.hcode = hcode;
        this.hcodeName = hcodeName;
        this.hcodeSend = hcodeSend;
        this.hcodeSendName = hcodeSendName;
        this.hmain = hmain;
        this.hmainName = hmainName;
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

    public String getHcodeName() {
        return hcodeName;
    }

    public void setHcodeName(String hcodeName) {
        this.hcodeName = hcodeName;
    }

    public String getHcodeSend() {
        return hcodeSend;
    }

    public void setHcodeSend(String hcodeSend) {
        this.hcodeSend = hcodeSend;
    }

    public String getHcodeSendName() {
        return hcodeSendName;
    }

    public void setHcodeSendName(String hcodeSendName) {
        this.hcodeSendName = hcodeSendName;
    }

    public String getHmain() {
        return hmain;
    }

    public void setHmain(String hmain) {
        this.hmain = hmain;
    }

    public String getHmainName() {
        return hmainName;
    }

    public void setHmainName(String hmainName) {
        this.hmainName = hmainName;
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

    @Override
    public String toString() {
        return "NHSOHospitalInfo{" +
                "id=" + id +
                ", seq='" + seq + '\'' +
                ", hcode='" + hcode + '\'' +
                ", hcodeName='" + hcodeName + '\'' +
                ", hcodeSend='" + hcodeSend + '\'' +
                ", hcodeSendName='" + hcodeSendName + '\'' +
                ", hmain='" + hmain + '\'' +
                ", hmainName='" + hmainName + '\'' +
                ", syncStatus=" + syncStatus +
                '}';
    }
}