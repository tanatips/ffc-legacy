// ไฟล์ CounselingLiveData.java
package th.in.ffc.app.form.screening.datalive;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CounselingLiveData {
    private String personId;
    private String visitNo;
    private int counselingType;
    private String consultDetail;
    private String referralDetail;
    private byte[] patientSignature;
    private byte[] providerSignature;

    public CounselingLiveData() {
        // Default constructor
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public int getCounselingType() {
        return counselingType;
    }

    public void setCounselingType(int counselingType) {
        this.counselingType = counselingType;
    }

    public String getConsultDetail() {
        return consultDetail;
    }

    public void setConsultDetail(String consultDetail) {
        this.consultDetail = consultDetail;
    }

    public String getReferralDetail() {
        return referralDetail;
    }

    public void setReferralDetail(String referralDetail) {
        this.referralDetail = referralDetail;
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
}