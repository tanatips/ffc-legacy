package th.in.ffc.app.form.screening.model;
public class CounselingInfo {
    private long id;
    private String visitId;
    private String personId;
    private int counselingType; // 1=ให้คำแนะนำ, 2=ส่งต่อแพทย์
    private String detail;
    private byte[] patientSignature;
    private byte[] providerSignature;
    private String createdBy;
    private String createdDate;
    private String updatedBy;
    private String updatedDate;
    private String pcuCode;

    private String updateStatus;

    public CounselingInfo() {
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public int getCounselingType() {
        return counselingType;
    }

    public void setCounselingType(int counselingType) {
        this.counselingType = counselingType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public byte[] getPatientSignature() {
        return patientSignature;
    }

    public void setPatientSignature(byte[] patientSignature) {
        this.patientSignature = patientSignature;
    }

    public byte[] getProviderSignature() {
        return providerSignature;
    }

    public void setProviderSignature(byte[] providerSignature) {
        this.providerSignature = providerSignature;
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

    public String getPcuCode() {
        return pcuCode;
    }

    public void setPcuCode(String pcuCode) {
        this.pcuCode = pcuCode;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }
}
