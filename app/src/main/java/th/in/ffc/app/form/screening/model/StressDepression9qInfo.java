package th.in.ffc.app.form.screening.model;

import android.os.Build;

import java.util.ArrayList;

public class StressDepression9qInfo {
    private String Q1;
    private String Q2;
    private String Q3;
    private String Q4;
    private String Q5;
    private String Q6;
    private String Q7;
    private String Q8;
    private String Q9;

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

    public String getQ6() {
        return Q6;
    }

    public void setQ6(String q6) {
        Q6 = q6;
    }

    public String getQ7() {
        return Q7;
    }

    public void setQ7(String q7) {
        Q7 = q7;
    }

    public String getQ8() {
        return Q8;
    }

    public void setQ8(String q8) {
        Q8 = q8;
    }

    public String getQ9() {
        return Q9;
    }

    public void setQ9(String q9) {
        Q9 = q9;
    }

    public ArrayList<Integer> getPoint() {
        return points;
    }

    public void setPoint(ArrayList<Integer> point) {
        this.points = point;
    }

    public Integer getSum() {
        final int[] sum = {0};
        String result = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            points.forEach(num -> sum[0] += num);
        }
        setSum(sum[0]);
        return this.sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
    public void analyze(Integer age){
        if(getSum()<7) {
            setResultCode(age<=60?"1B0260":"1B0282"); //  setResultCode("1B0282"); กลุ่มผู้สูงอายุ
            setResultDescription("ไม่มีอาการของโรคซมึเศร้าหรือมีระดับน้อยมาก");
        } else if(getSum()>=7 && getSum()<=12){
            setResultCode(age<=60?"1B0261":"1B0283"); //  setResultCode("1B0283"); กลุ่มผู้สูงอายุ
            setResultDescription("เป็นโรคซึมเศร้า ระดับน้อย (Major Depression, Mild)");
        } else if(getSum()>=13 && getSum()<=18){
            setResultCode(age<=60?"1B0262":"1B0284"); //  setResultCode("1B0284"); กลุ่มผู้สูงอายุ
            setResultDescription("เป็นโรคซึมเศร้า ระดับปานกลาง (Major Depression, Moderate)");
        } else if(getSum()>=19){
            setResultCode(age<=60?"1B0263":"1B0285"); //  setResultCode("1B0285"); กลุ่มผู้สูงอายุ
            setResultDescription("เป็นโรคซึมเศร้า ระดับมาก (Major Depression, Severe)");
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
