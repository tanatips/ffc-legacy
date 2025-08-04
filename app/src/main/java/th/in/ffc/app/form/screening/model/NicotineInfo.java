package th.in.ffc.app.form.screening.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NicotineInfo {
    private String id;

    private String personId;
    private String idcard;
    private String Nicotine1 = "0";
    private String Nicotine2 = "0";
    private String Nicotine3 = "0";
    private String Nicotine4 = "0";
    private String Nicotine5 = "0";
    private String Nicotine6 = "0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

    private ArrayList<Integer> points;

    private Integer sum;
    private String visitNo;
    private String dateUpdate;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }
}
