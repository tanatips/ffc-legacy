package th.in.ffc.app.form.screening.datalive;

public class StressDepression2qLiveData {

    private String personId;
    private Integer selectedQ1;
    private Integer selectedQ2;

    public Integer getSelectedQ1() {
        return selectedQ1;
    }

    public void setSelectedQ1(Integer selectedQ1) {
        this.selectedQ1 = selectedQ1;
    }

    public Integer getSelectedQ2() {
        return selectedQ2;
    }

    public void setSelectedQ2(Integer selectedQ2) {
        this.selectedQ2 = selectedQ2;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
