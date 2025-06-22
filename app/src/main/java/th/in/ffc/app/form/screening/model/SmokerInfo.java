package th.in.ffc.app.form.screening.model;

public class SmokerInfo {

    private String id;
    private String personId;
    private String idcard;
    private String SmokerGroup = null;
    private String SmokerAssist = null;
    private String SmokerRegularly = null;
    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;

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

    public String getSmokerGroup() {
        return SmokerGroup;
    }

    public void setSmokerGroup(String smokerGroup) {
        SmokerGroup = smokerGroup;
    }

    public String getSmokerAssist() {
        return SmokerAssist;
    }

    public void setSmokerAssist(String smokerAssist) {
        SmokerAssist = smokerAssist;
    }

    public String getSmokerRegularly() {
        return SmokerRegularly;
    }

    public void setSmokerRegularly(String smokerRegularly) {
        SmokerRegularly = smokerRegularly;
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
