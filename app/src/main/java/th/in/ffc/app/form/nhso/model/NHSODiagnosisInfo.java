package th.in.ffc.app.form.nhso.model;

import java.util.Date;

/**
 * แฟ้มที่ 5 NHSO Diagnosis - ข้อมูลวินิจฉัยโรคของผู้เข้ารับบริการ
 */
public class NHSODiagnosisInfo {

    private long id;

    // 1. รหัสการบริการที่กำหนดโดยโปรแกรม (Visit Number)
    private String seq;

    // 2. วันเดือนปีที่วินิจฉัยโรค
    private Date dateDx;

    // 3. รหัสวินิจฉัยโรค ตามรหัส ICD 10
    private String diag;

    // 4. รหัสประเภทการวินิจฉัยตาม 43plus เช่น PRINCIPLE Dx, CO-MORBIDITY, EXTERNAL CAUSE
    private String diagType;

    // 5. เลขใบอนุญาตประกอบวิชาชีพที่ออกให้โดยสภาวิชาชีพ
    private String professionId;

    // 6. แผนกที่รักษาผู้ป่วยเป็นหลัก
    private String clinic;

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

    public Date getDateDx() {
        return dateDx;
    }

    public void setDateDx(Date dateDx) {
        this.dateDx = dateDx;
    }

    public String getDiag() {
        return diag;
    }

    public void setDiag(String diag) {
        this.diag = diag;
    }

    public String getDiagType() {
        return diagType;
    }

    public void setDiagType(String diagType) {
        this.diagType = diagType;
    }

    public String getProfessionId() {
        return professionId;
    }

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    @Override
    public String toString() {
        return "NHSODiagnosisInfo{" +
                "id=" + id +
                ", seq='" + seq + '\'' +
                ", dateDx=" + dateDx +
                ", diag='" + diag + '\'' +
                ", diagType='" + diagType + '\'' +
                ", professionId='" + professionId + '\'' +
                ", clinic='" + clinic + '\'' +
                '}';
    }
}