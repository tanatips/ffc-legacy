package th.in.ffc.app.form.screening.model;
import th.in.ffc.R;
import java.io.Serializable;

/**
 * Model class for representing claim information
 */
public class ClaimInfo implements Serializable {

    private String id;
    private String patientId;
    private String patientName;
    private String idCard;
    private String patientGroup;
    private String serviceDate;
    private String serviceType;
    private double amount;
    private String status;
    private String statusMessage;
    private String claimDate;

    private String claimId;
    private String claimStatus;
    private String claimMessage;
    private String visitNo;

    private String seq;

    // Default constructor
    public ClaimInfo() {
    }

    // Constructor with all fields
    public ClaimInfo(String id, String patientId, String patientName, String idCard,
                     String patientGroup, String serviceDate, String serviceType,
                     double amount, String status, String statusMessage,
                     String claimDate, String visitNo,String seq) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.idCard = idCard;
        this.patientGroup = patientGroup;
        this.serviceDate = serviceDate;
        this.serviceType = serviceType;
        this.amount = amount;
        this.status = status;
        this.statusMessage = statusMessage;
        this.claimDate = claimDate;
        this.visitNo = visitNo;
        this.seq = seq;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPatientGroup() {
        return patientGroup;
    }

    public void setPatientGroup(String patientGroup) {
        this.patientGroup = patientGroup;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getSeq() {
        return seq;
    }
    public void setSeq(String seq) {
        this.seq = seq;
    }

    /**
     * Get color code based on claim status
     * @return Color code in string format (#RRGGBB)
     */
    public String getStatusColor() {
        switch (status.toUpperCase()) {
            case "APPROVED":
                return "#4CAF50"; // Green
            case "PENDING":
                return "#FFC107"; // Amber
            case "REJECTED":
                return "#F44336"; // Red
            case "PROCESSING":
                return "#2196F3"; // Blue
            case "FAILED":
            case "ERROR":
            case "API_ERROR":
            case "PERSON_API_ERROR":
                return "#F44336"; // Red
            default:
                return "#757575"; // Grey
        }
    }

    /**
     * Get translated status text in Thai
     * @return Status text in Thai
     */
    public String getStatusThai() {
        switch (status.toUpperCase()) {
            case "APPROVED":
                return "อนุมัติ";
            case "PENDING":
                return "รอดำเนินการ";
            case "REJECTED":
                return "ปฏิเสธ";
            case "PROCESSING":
                return "กำลังดำเนินการ";
            case "FAILED":
                return "ล้มเหลว";
            case "ERROR":
            case "API_ERROR":
            case "PERSON_API_ERROR":
                return "เกิดข้อผิดพลาด";
            default:
                return status;
        }
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(String claimStatus) {
        this.claimStatus = claimStatus;
    }

    public String getClaimMessage() {
        return claimMessage;
    }

    public void setClaimMessage(String claimMessage) {
        this.claimMessage = claimMessage;
    }

    /**
     * Format amount to display with Thai Baht
     * @return Formatted amount string
     */
    public String getFormattedAmount() {
        return String.format("%.2f บาท", amount);
    }
}