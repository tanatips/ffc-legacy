package th.in.ffc.app.form.screening.model;

public class AnswerData {
    private boolean hasUsed;
    private String otherDrugs;

    public AnswerData(boolean hasUsed, String otherSubstance) {
        this.hasUsed = hasUsed;
        this.otherDrugs = otherSubstance;
    }

    // Getters
    public boolean isHasUsed() { return hasUsed; }
    public String getOtherDrugs() { return otherDrugs; }
}
