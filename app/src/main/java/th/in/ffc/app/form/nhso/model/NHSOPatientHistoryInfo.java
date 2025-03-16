package th.in.ffc.app.form.nhso.model;

/**
 * โมเดลข้อมูลประวัติการส่งข้อมูลผู้ป่วย NHSO
 */
public class NHSOPatientHistoryInfo {
    private long id;
    private long patientId;
    private String cid;
    private String status;
    private String message;
    private int responseCode;
    private String responseBody;
    private String createdBy;
    private String createdDate;

    // สถานะการส่งข้อมูล
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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

    /**
     * ตรวจสอบว่าการส่งข้อมูลสำเร็จหรือไม่
     * @return true ถ้าการส่งข้อมูลสำเร็จ, false ถ้าการส่งข้อมูลไม่สำเร็จ
     */
    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }

    /**
     * ตรวจสอบว่าการส่งข้อมูลล้มเหลวหรือไม่
     * @return true ถ้าการส่งข้อมูลล้มเหลว, false ถ้าการส่งข้อมูลไม่ล้มเหลว
     */
    public boolean isFailed() {
        return STATUS_FAILED.equals(status);
    }

    /**
     * แปลงสถานะการส่งข้อมูลเป็นข้อความภาษาไทย
     * @return ข้อความสถานะการส่งข้อมูลภาษาไทย
     */
    public String getStatusText() {
        if (status == null) {
            return "ไม่ระบุ";
        }

        switch (status) {
            case STATUS_SUCCESS:
                return "ส่งข้อมูลสำเร็จ";
            case STATUS_FAILED:
                return "ส่งข้อมูลไม่สำเร็จ";
            default:
                return status;
        }
    }

    /**
     * แสดงผลรหัสการตอบกลับและข้อความ
     * @return รหัสการตอบกลับและข้อความ
     */
    public String getResponseSummary() {
        StringBuilder summary = new StringBuilder();

        if (responseCode > 0) {
            summary.append("รหัส: ").append(responseCode);
        }

        if (message != null && !message.isEmpty()) {
            if (summary.length() > 0) {
                summary.append(" - ");
            }
            summary.append(message);
        }

        return summary.length() > 0 ? summary.toString() : "ไม่มีข้อมูลการตอบกลับ";
    }

    /**
     * ฟอร์แมตเลขบัตรประชาชนให้อ่านง่าย
     * @return เลขบัตรประชาชนรูปแบบ X-XXXX-XXXXX-XX-X
     */
    public String getFormattedCid() {
        if (cid == null || cid.length() != 13) {
            return cid;
        }

        return cid.substring(0, 1) + "-" +
                cid.substring(1, 5) + "-" +
                cid.substring(5, 10) + "-" +
                cid.substring(10, 12) + "-" +
                cid.substring(12);
    }
}