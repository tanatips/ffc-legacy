package th.in.ffc.app.form.screening.model;

import android.os.Build;

import java.util.ArrayList;

public class StressDepressionInfo {
    private String Q1;
    private String Q2;
    private String Q3;
    private String Q4;
    private String Q5;

    private Integer sum;

    private ArrayList<Integer> points;

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
            getPoints().forEach(num -> sum[0] += num);
        }
        setSum(sum[0]);
        return this.sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
