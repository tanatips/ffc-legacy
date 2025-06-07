package th.in.ffc.app.form.screening.model;

public class AssistScore {

    private String personId;
    private String nicotineScore;
    private String alcoholScore;

    public String getNicotineScore() {
        return nicotineScore;
    }

    public void setNicotineScore(String nicotineScore) {
        this.nicotineScore = nicotineScore;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAlcoholScore() {
        return alcoholScore;
    }

    public void setAlcoholScore(String alcoholScore) {
        this.alcoholScore = alcoholScore;
    }
}
