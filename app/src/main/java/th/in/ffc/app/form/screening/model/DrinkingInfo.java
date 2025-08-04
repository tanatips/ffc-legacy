package th.in.ffc.app.form.screening.model;

public class DrinkingInfo {

    private String id;
    private String personId;
    private String idcard;
    private String Drinking = "0";
    private String DrinkingFrequency = "0";
    private String DrinkingAlway = "0";

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;
    private String visitNo;
    private String dateUpdate;

    public String getDrinking() {
        return Drinking;
    }

    public void setDrinking(String drinking) {
        Drinking = drinking;
    }

    public String getDrinkingFrequency() {
        return DrinkingFrequency;
    }

    public void setDrinkingFrequency(String drinkingFrequency) {
        DrinkingFrequency = drinkingFrequency;
    }

    public String getDrinkingAlway() {
        return DrinkingAlway;
    }

    public void setDrinkingAlway(String drinkingAlway) {
        DrinkingAlway = drinkingAlway;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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
