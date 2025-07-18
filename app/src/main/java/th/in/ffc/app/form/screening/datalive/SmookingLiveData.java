package th.in.ffc.app.form.screening.datalive;

public class SmookingLiveData {

    private String personId;
    private String visitId;
    private Integer selectedRdoSmokerGroup;
    private Integer selectedRdoSmokerAssist;
    private Integer selectedRdoSmokerRegularly;

    public Integer getSelectedRdoSmokerGroup() {
        return selectedRdoSmokerGroup;
    }

    public void setSelectedRdoSmokerGroup(Integer selectedRdoSmokerGroup) {
        this.selectedRdoSmokerGroup = selectedRdoSmokerGroup;
    }

    public Integer getSelectedRdoSmokerAssist() {
        return selectedRdoSmokerAssist;
    }

    public void setSelectedRdoSmokerAssist(Integer selectedRdoSmokerAssist) {
        this.selectedRdoSmokerAssist = selectedRdoSmokerAssist;
    }

    public Integer getSelectedRdoSmokerRegularly() {
        return selectedRdoSmokerRegularly;
    }

    public void setSelectedRdoSmokerRegularly(Integer selectedRdoSmokerRegularly) {
        this.selectedRdoSmokerRegularly = selectedRdoSmokerRegularly;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
    public String getVisitId() {
        return visitId;
    }
    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

}