package th.in.ffc.app.form.screening.model;

public class SuicideAssessment8qInfo {

    private String id;
    private String personId;
    private String idcard;
    private String Q1="0";
    private String Q2="0";
    private String Q3="0";
    private String Q3_2_1="0";
    private String Q4="0";
    private String Q5="0";
    private String Q6="0";
    private String Q7="0";
    private String Q8="0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

    private String visitNo;
    private String dateUpdate;

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

    public String getQ3_2_1() {
        return Q3_2_1;
    }

    public void setQ3_2_1(String q3_2_1) {
        Q3_2_1 = q3_2_1;
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
