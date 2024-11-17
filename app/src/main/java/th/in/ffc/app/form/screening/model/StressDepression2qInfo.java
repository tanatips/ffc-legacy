package th.in.ffc.app.form.screening.model;

import android.os.Build;

import java.util.ArrayList;

public class StressDepression2qInfo {
    private String Q1;
    private String Q2;

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
}
