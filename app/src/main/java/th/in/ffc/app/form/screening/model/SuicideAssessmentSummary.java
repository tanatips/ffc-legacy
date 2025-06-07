package th.in.ffc.app.form.screening.model;

// คลาสสำหรับเก็บข้อมูลสรุปการประเมิน
public class SuicideAssessmentSummary {
    private int totalScore;
    private String resultCode;
    private String resultDescription;
    private boolean isComplete;
    private String advice;
    private SuicideAssessment8qInfo assessmentData;

    public SuicideAssessmentSummary() {
        this.totalScore = 0;
        this.resultCode = "";
        this.resultDescription = "";
        this.isComplete = false;
        this.advice = "";
        this.assessmentData = null;
    }

    public SuicideAssessmentSummary(int totalScore, String resultCode, String resultDescription,
                                    boolean isComplete, String advice, SuicideAssessment8qInfo assessmentData) {
        this.totalScore = totalScore;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.isComplete = isComplete;
        this.advice = advice;
        this.assessmentData = assessmentData;
    }

    // Getters
    public int getTotalScore() { return totalScore; }
    public String getResultCode() { return resultCode; }
    public String getResultDescription() { return resultDescription; }
    public boolean isComplete() { return isComplete; }
    public String getAdvice() { return advice; }
    public SuicideAssessment8qInfo getAssessmentData() { return assessmentData; }

    public String getRiskLevel() {
        if (totalScore == 0) return "ไม่มีความเสี่ยง";
        else if (totalScore <= 8) return "ความเสี่ยงต่ำ";
        else if (totalScore <= 16) return "ความเสี่ยงปานกลาง";
        else return "ความเสี่ยงสูง";
    }

    public boolean isHighRisk() {
        return totalScore >= 17;
    }

    public boolean isModerateRisk() {
        return totalScore >= 9 && totalScore <= 16;
    }
}