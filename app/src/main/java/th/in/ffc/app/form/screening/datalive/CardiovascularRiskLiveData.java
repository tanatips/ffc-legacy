package th.in.ffc.app.form.screening.datalive;

public class CardiovascularRiskLiveData {
    private String personId;

    private String visitNo;

    private String age;
    private Integer selectedGender;
    private String bloodPressure;
    private String waistSize;
    private String height;
    private String cholesterol;
    private Boolean isSmoking;
    private Boolean hasDiabetes;
    private Double riskLevel;
    private Double riskPercentage;
    private String recommendation;

    public CardiovascularRiskLiveData() {
        // Default values
        this.isSmoking = false;
        this.hasDiabetes = false;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Integer getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(Integer selectedGender) {
        this.selectedGender = selectedGender;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getWaistSize() {
        return waistSize;
    }

    public void setWaistSize(String waistSize) {
        this.waistSize = waistSize;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(String cholesterol) {
        this.cholesterol = cholesterol;
    }

    public Boolean getIsSmoking() {
        return isSmoking;
    }

    public void setIsSmoking(Boolean isSmoking) {
        this.isSmoking = isSmoking;
    }

    public Boolean getHasDiabetes() {
        return hasDiabetes;
    }

    public void setHasDiabetes(Boolean hasDiabetes) {
        this.hasDiabetes = hasDiabetes;
    }

    public Double getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Double riskLevel) {
        this.riskLevel = riskLevel;
    }

    public double getRiskPercent() {
        return riskPercentage;
    }

    public void setRiskPercentage(double riskPercentage) {
        this.riskPercentage = riskPercentage;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getVisitNo() {
        return visitNo;
    }

    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }
}