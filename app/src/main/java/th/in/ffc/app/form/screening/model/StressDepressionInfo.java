package th.in.ffc.app.form.screening.model;

import android.os.Build;

import java.util.ArrayList;

public class StressDepressionInfo {

    private String id;
    private String idcard;
    private String personId;
    private String Q1="0";
    private String Q2="0";
    private String Q3="0";
    private String Q4="0";
    private String Q5="0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

    private Integer sum;

    private ArrayList<Integer> points;

    private String resultCode;
    private String resultDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getQ3() {
        return Q3;
    }

    public void setQ3(String q3) {
        Q3 = q3;
    }

    public String getQ4() {
        return Q4;
    }

    public void setQ4(String q4) {
        Q4 = q4;
    }

    public String getQ5() {
        return Q5;
    }

    public void setQ5(String q5) {
        Q5 = q5;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }


    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public String getResultCode() {
        String result="";
        if(getSum()>=0 && getSum()<=4){
            setResultCode("1B132");
            setResultDescription("เครียดน้อย");
        }else if(getSum()>=5 && getSum()<=7){
            setResultCode("1B133");
            setResultDescription("เครียดปานกลาง");
        }else if(getSum()>=8 && getSum()<=9){
            setResultCode("1B134");
            setResultDescription("เครียดมาก");
        }else if(getSum()>=10 && getSum()<=15){
            setResultCode("1B134");
            setResultDescription("เครียดมากที่สุด");
        }
        resultCode = result;
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

    public Integer getSum() {
        final int[] sum = {0};
        String result = "";
        // ต้องเป็น final array เพราะใช้ใน lambda

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(getPoints()!=null) {
                getPoints().forEach(num -> sum[0] += num);
            }
        }
        setSum(sum[0]);
        return this.sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
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
