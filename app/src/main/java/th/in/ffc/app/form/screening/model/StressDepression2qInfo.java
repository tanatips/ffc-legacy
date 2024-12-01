package th.in.ffc.app.form.screening.model;

import android.os.Build;

import java.util.ArrayList;

public class StressDepression2qInfo {

    private String id;
    private String personId;
    private String idcard;
    private String Q1="0";
    private String Q2="0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

    private ArrayList<Integer> points;
    private Integer sum;
    private String resultCode;
    private String resultDescription;

    public String getQ1() {
        return Q1;
    }

    public void setQ1(String q1) {
        Q1 = q1;
    }

    public String getQ2() {
        return Q2;
    }

    public void setQ2(String q2) {
        Q2 = q2;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public Integer getSum() {
        final int[] sum = {0};
        String result = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            points.forEach(num -> sum[0] += num);
        }
        setSum(sum[0]);
        return sum[0];
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public void analyze(){
        if(getSum().equals(0)){
            setResultCode("1B0210");
            setResultDescription("ปกติ");

        }else if(getSum()>0){
            setResultCode("1B0211");
            setResultDescription("ผิดปกติ และ ส่งต่อเจ้าหน้าที่");
        }
    }
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }
}
