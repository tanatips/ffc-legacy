package th.in.ffc.app.form.screening.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NicotineInfo {
    private String Nicotine1;
    private String Nicotine2;
    private String Nicotine3;
    private String Nicotine4;
    private String Nicotine5;
    private String Nicotine6;

    private ArrayList<Integer> points;

    private Integer sum;

    public String getNicotine1() {
        return Nicotine1;
    }

    public void setNicotine1(String nicotine1) {
        Nicotine1 = nicotine1;
    }

    public String getNicotine2() {
        return Nicotine2;
    }

    public void setNicotine2(String nicotine2) {
        Nicotine2 = nicotine2;
    }

    public String getNicotine3() {
        return Nicotine3;
    }

    public void setNicotine3(String nicotine3) {
        Nicotine3 = nicotine3;
    }

    public String getNicotine4() {
        return Nicotine4;
    }

    public void setNicotine4(String nicotine4) {
        Nicotine4 = nicotine4;
    }

    public String getNicotine5() {
        return Nicotine5;
    }

    public void setNicotine5(String nicotine5) {
        Nicotine5 = nicotine5;
    }

    public String getNicotine6() {
        return Nicotine6;
    }

    public void setNicotine6(String nicotine6) {
        Nicotine6 = nicotine6;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Integer> points) {
        this.points = points;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }
}
