package th.in.ffc.api.nhso;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * คลาสสำหรับรับผลลัพธ์จาก API create-fs-data
 */
public class FSDataResponse {

//    @SerializedName("ID")
//    private Long id; // ทำเป็น Long เพื่อรองรับกรณีที่ไม่มีค่า

    @SerializedName("SEQ")
    private String seq;

    @SerializedName("HCODE")
    private String hcode;

    @SerializedName("STATUS")
    private String status;

    @SerializedName("ERRORS")
    private List<ErrorDetail> errors;

    /**
     * คลาสสำหรับเก็บรายละเอียดข้อผิดพลาด
     */
    public static class ErrorDetail {
        @SerializedName("seq")
        private String seq;

        @SerializedName("cid")
        private String cid;

        @SerializedName("field")
        private String field;

        @SerializedName("message")
        private String message;

        // Getters and Setters
        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "ฟิลด์: " + field + ", ข้อความ: " + message;
        }
    }

    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    /**
     * ตรวจสอบว่าการส่งข้อมูลสำเร็จหรือไม่
     * @return true ถ้าสำเร็จ, false ถ้าไม่สำเร็จ
     */
    public boolean isSuccess() {
        return "SAVED".equalsIgnoreCase(status);
    }

    /**
     * รวมข้อความแสดงรายละเอียดข้อผิดพลาด
     * @return ข้อความแสดงรายละเอียดข้อผิดพลาด
     */
    public String getErrorSummary() {
        if (errors == null || errors.isEmpty()) {
            return "ไม่มีรายละเอียดข้อผิดพลาด";
        }

        StringBuilder sb = new StringBuilder();
        for (ErrorDetail error : errors) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(error.toString());
        }

        return sb.toString();
    }

    /**
     * ดึง Visit ID (ใช้ SEQ แทน visitId)
     * @return รหัสการเข้ารับบริการ
     */
    public String getVisitId() {
        return seq;
    }

    @Override
    public String toString() {
        return "FSDataResponse{" +
//                "id=" + id +
                " seq='" + seq + '\'' +
                ", hcode='" + hcode + '\'' +
                ", status='" + status + '\'' +
                ", errors=" + errors +
                '}';
    }
}