package th.in.ffc.api.nhso;

import com.google.gson.annotations.SerializedName;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * คลาสสำหรับเก็บข้อมูลที่ส่งกลับมาจาก Status Tracks V2 API
 */
public class StatusTracksV2Response {

    @SerializedName("id")
    private int id;

    @SerializedName("uid")
    private String uid;

    @SerializedName("seq")
    private String seq;

    @SerializedName("hcode")
    private String hcode;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    @SerializedName("recordStatus")
    private String recordStatus;

    @SerializedName("an")
    private String an;

    @SerializedName("hn")
    private String hn;

    @SerializedName("claimResult")
    private String claimResult;

    @SerializedName("deniedCode")
    private String deniedCode;

    @SerializedName("deniedDescription")
    private String deniedDescription;

    @SerializedName("remark")
    private String remark;

    @SerializedName("runDt")
    private String runDt; // ISO 8601 date string

    @SerializedName("btchNo")
    private String btchNo;

    @SerializedName("docNo")
    private String docNo;

    @SerializedName("budgetNo")
    private String budgetNo;

    @SerializedName("bookDt")
    private String bookDt; // ISO 8601 date string

    @SerializedName("repNo")
    private String repNo;

    @SerializedName("period")
    private String period;

    @SerializedName("message")
    private String message;

    /**
     * Constructor เปล่า
     */
    public StatusTracksV2Response() {
    }

    /**
     * Constructor สำหรับสร้าง StatusTracksV2Response จากข้อมูลแยก
     */
    public StatusTracksV2Response(int id, String uid, String seq, String hcode,
                                  String paymentStatus, String recordStatus, String an, String hn,
                                  String claimResult, String deniedCode, String deniedDescription,
                                  String remark, String runDt, String btchNo, String docNo,
                                  String budgetNo, String bookDt, String repNo, String period, String message) {
        this.id = id;
        this.uid = uid;
        this.seq = seq;
        this.hcode = hcode;
        this.paymentStatus = paymentStatus;
        this.recordStatus = recordStatus;
        this.an = an;
        this.hn = hn;
        this.claimResult = claimResult;
        this.deniedCode = deniedCode;
        this.deniedDescription = deniedDescription;
        this.remark = remark;
        this.runDt = runDt;
        this.btchNo = btchNo;
        this.docNo = docNo;
        this.budgetNo = budgetNo;
        this.bookDt = bookDt;
        this.repNo = repNo;
        this.period = period;
        this.message = message;
    }

    /**
     * แปลงข้อมูล JSON Response เป็น List ของ StatusTracksV2Response
     * @param jsonResponse ข้อมูล JSON String ที่ได้จาก API
     * @return List ของ StatusTracksV2Response
     * @throws JSONException กรณีเกิดข้อผิดพลาดขณะแปลง JSON
     */
    public static List<StatusTracksV2Response> fromJsonArray(String jsonResponse) throws JSONException {
        List<StatusTracksV2Response> results = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StatusTracksV2Response response = new StatusTracksV2Response(
                    jsonObject.optInt("id", 0),
                    jsonObject.optString("uid", ""),
                    jsonObject.optString("seq", ""),
                    jsonObject.optString("hcode", ""),
                    jsonObject.optString("paymentStatus", ""),
                    jsonObject.optString("recordStatus", ""),
                    jsonObject.optString("an", ""),
                    jsonObject.optString("hn", ""),
                    jsonObject.optString("claimResult", ""),
                    jsonObject.optString("deniedCode", ""),
                    jsonObject.optString("deniedDescription", ""),
                    jsonObject.optString("remark", ""),
                    jsonObject.optString("runDt", ""),
                    jsonObject.optString("btchNo", ""),
                    jsonObject.optString("docNo", ""),
                    jsonObject.optString("budgetNo", ""),
                    jsonObject.optString("bookDt", ""),
                    jsonObject.optString("repNo", ""),
                    jsonObject.optString("period", ""),
                    jsonObject.optString("message", "")
            );
            results.add(response);
        }

        return results;
    }

    /**
     * ตรวจสอบว่าการชำระเงินสำเร็จหรือไม่
     * @return true ถ้าการชำระเงินสำเร็จ
     */
    public boolean isPaymentSuccess() {
        return "PAID".equalsIgnoreCase(paymentStatus) ||
                "SUCCESS".equalsIgnoreCase(paymentStatus);
    }

    /**
     * ตรวจสอบว่าข้อมูลถูกปฏิเสธหรือไม่
     * @return true ถ้าข้อมูลถูกปฏิเสธ
     */
    public boolean isDenied() {
        return deniedCode != null && !deniedCode.isEmpty();
    }

    /**
     * ดึงข้อมูลสรุปสถานะ
     * @return ข้อความสรุปสถานะ
     */
    public String getStatusSummary() {
        StringBuilder summary = new StringBuilder();

        if (recordStatus != null && !recordStatus.isEmpty()) {
            summary.append("สถานะบันทึก: ").append(recordStatus);
        }

        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("สถานะการชำระ: ").append(paymentStatus);
        }

        if (isDenied()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("ปฏิเสธ: ").append(deniedDescription);
        }

        if (message != null && !message.isEmpty()) {
            if (summary.length() > 0) summary.append(", ");
            summary.append("ข้อความ: ").append(message);
        }

        return summary.toString();
    }

    // Getters และ Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getAn() {
        return an;
    }

    public void setAn(String an) {
        this.an = an;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getClaimResult() {
        return claimResult;
    }

    public void setClaimResult(String claimResult) {
        this.claimResult = claimResult;
    }

    public String getDeniedCode() {
        return deniedCode;
    }

    public void setDeniedCode(String deniedCode) {
        this.deniedCode = deniedCode;
    }

    public String getDeniedDescription() {
        return deniedDescription;
    }

    public void setDeniedDescription(String deniedDescription) {
        this.deniedDescription = deniedDescription;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRunDt() {
        return runDt;
    }

    public void setRunDt(String runDt) {
        this.runDt = runDt;
    }

    public String getBtchNo() {
        return btchNo;
    }

    public void setBtchNo(String btchNo) {
        this.btchNo = btchNo;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getBudgetNo() {
        return budgetNo;
    }

    public void setBudgetNo(String budgetNo) {
        this.budgetNo = budgetNo;
    }

    public String getBookDt() {
        return bookDt;
    }

    public void setBookDt(String bookDt) {
        this.bookDt = bookDt;
    }

    public String getRepNo() {
        return repNo;
    }

    public void setRepNo(String repNo) {
        this.repNo = repNo;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StatusTracksV2Response{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", seq='" + seq + '\'' +
                ", hcode='" + hcode + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", recordStatus='" + recordStatus + '\'' +
                ", an='" + an + '\'' +
                ", hn='" + hn + '\'' +
                ", claimResult='" + claimResult + '\'' +
                ", deniedCode='" + deniedCode + '\'' +
                ", deniedDescription='" + deniedDescription + '\'' +
                ", remark='" + remark + '\'' +
                ", runDt='" + runDt + '\'' +
                ", btchNo='" + btchNo + '\'' +
                ", docNo='" + docNo + '\'' +
                ", budgetNo='" + budgetNo + '\'' +
                ", bookDt='" + bookDt + '\'' +
                ", repNo='" + repNo + '\'' +
                ", period='" + period + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}