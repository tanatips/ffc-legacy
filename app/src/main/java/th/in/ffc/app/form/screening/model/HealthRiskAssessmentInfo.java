package th.in.ffc.app.form.screening.model;

public class HealthRiskAssessmentInfo {

    private String id;

    private String personId;
    private String idcard;
    private String HealthRiskQ1="0";
    private String HealthRiskQ2="0";
    private String HealthRiskQ3="0";
    private String HealthRiskQ4="0";
    private String HealthRiskQ5="0";
    private String HealthRiskQ6="0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

    public String getHealthRiskQ1() {
        return HealthRiskQ1;
    }

    public void setHealthRiskQ1(String healthRiskQ1) {
        HealthRiskQ1 = healthRiskQ1;
    }

    public String getHealthRiskQ2() {
        return HealthRiskQ2;
    }

    public void setHealthRiskQ2(String healthRiskQ2) {
        HealthRiskQ2 = healthRiskQ2;
    }

    public String getHealthRiskQ3() {
        return HealthRiskQ3;
    }

    public void setHealthRiskQ3(String healthRiskQ3) {
        HealthRiskQ3 = healthRiskQ3;
    }

    public String getHealthRiskQ4() {
        return HealthRiskQ4;
    }

    public void setHealthRiskQ4(String healthRiskQ4) {
        HealthRiskQ4 = healthRiskQ4;
    }

    public String getHealthRiskQ5() {
        return HealthRiskQ5;
    }

    public void setHealthRiskQ5(String healthRiskQ5) {
        HealthRiskQ5 = healthRiskQ5;
    }

    public String getHealthRiskQ6() {
        return HealthRiskQ6;
    }

    public void setHealthRiskQ6(String healthRiskQ6) {
        HealthRiskQ6 = healthRiskQ6;
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
