package th.in.ffc.app.form.screening.model;

public class AnswerData {
    private Boolean hasUsed;
    private String otherDrugs;

    public AnswerData(Boolean hasUsed, String otherSubstance) {
        this.hasUsed = hasUsed;
        this.otherDrugs = otherSubstance;
    }
    public AnswerData() {
        this.hasUsed = null;
        this.otherDrugs = null;
    }

    // Getters
    public Boolean isHasUsed() { return hasUsed; }
    public String getOtherDrugs() { return otherDrugs; }
}
