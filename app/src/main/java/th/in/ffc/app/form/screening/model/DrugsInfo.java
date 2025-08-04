package th.in.ffc.app.form.screening.model;

public class DrugsInfo {
    private String id;
    private String personInfoId;
    private String question;
    private String subquestion;

    private String otherDrugs;
    private String answer;
    private String createdBy;
    private String createdDate;
    private String updatedBy;
    private String updatedDate;
    private String idcard;
    private String visitNo;
    private String dateUpdate;

    // Constructors
    public DrugsInfo() {
    }

    public DrugsInfo(String id, String personInfoId, String question, String subquestion,
                     String answer,String otherDrugs, String createdBy, String createdDate,
                     String updatedBy, String updatedDate, String idcard, String visitNo, String dateUpdate) {
        this.id = id;
        this.personInfoId = personInfoId;
        this.question = question;
        this.subquestion = subquestion;
        this.answer = answer;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
        this.otherDrugs = otherDrugs;
        this.idcard = idcard;
        this.visitNo = visitNo;
        this.dateUpdate = dateUpdate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonInfoId() {
        return personInfoId;
    }

    public void setPersonInfoId(String personInfoId) {
        this.personInfoId = personInfoId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSubquestion() {
        return subquestion;
    }

    public void setSubquestion(String subquestion) {
        this.subquestion = subquestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getOtherDrugs() {
        return otherDrugs;
    }

    public void setOtherDrugs(String otherDrugs) {
        this.otherDrugs = otherDrugs;
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

    @Override
    public String toString() {
        return "Drugs{" +
                "id='" + id + '\'' +
                ", personInfoId='" + personInfoId + '\'' +
                ", question='" + question + '\'' +
                ", subquestion='" + subquestion + '\'' +
                ", answer='" + answer + '\'' +
                ", otherDrugs='" + otherDrugs + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", updatedDate='" + updatedDate + '\'' +
                ", idcard='" + idcard + '\'' +
                '}';
    }
}
