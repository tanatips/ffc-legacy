package th.in.ffc.api.nhso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * คลาสสำหรับเก็บข้อมูลที่ส่งกลับมาจาก Status Track API
 */
public class StatusTrackResponse {
    private int id;
    private String uid;
    private String seq;
    private String hcode;
    private String recordStatus;
    private String hn;
    private String message;

    /**
     * Constructor สำหรับสร้าง StatusTrackResponse จากข้อมูลแยก
     */
    public StatusTrackResponse(int id, String uid, String seq, String hcode, String recordStatus, String hn, String message) {
        this.id = id;
        this.uid = uid;
        this.seq = seq;
        this.hcode = hcode;
        this.recordStatus = recordStatus;
        this.hn = hn;
        this.message = message;
    }

    /**
     * แปลงข้อมูล JSON Response เป็น List ของ StatusTrackResponse
     * @param jsonResponse ข้อมูล JSON String ที่ได้จาก API
     * @return List ของ StatusTrackResponse
     * @throws JSONException กรณีเกิดข้อผิดพลาดขณะแปลง JSON
     */
    public static List<StatusTrackResponse> fromJsonArray(String jsonResponse) throws JSONException {
        List<StatusTrackResponse> results = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonResponse);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            StatusTrackResponse response = new StatusTrackResponse(
                    jsonObject.getInt("id"),
                    jsonObject.optString("uid", ""),
                    jsonObject.optString("seq", ""),
                    jsonObject.optString("hcode", ""),
                    jsonObject.optString("recordStatus", ""),
                    jsonObject.optString("hn", ""),
                    jsonObject.optString("message", "")
            );
            results.add(response);
        }

        return results;
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

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "StatusTrackResponse{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", seq='" + seq + '\'' +
                ", hcode='" + hcode + '\'' +
                ", recordStatus='" + recordStatus + '\'' +
                ", hn='" + hn + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}