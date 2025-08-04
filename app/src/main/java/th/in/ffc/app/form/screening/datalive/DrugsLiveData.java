package th.in.ffc.app.form.screening.datalive;

import java.util.List;

import th.in.ffc.app.form.screening.model.DrugsInfo;

public class DrugsLiveData {
    private String personId;
    private String visitNo;
    private List<DrugsInfo> drugsInfo;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public List<DrugsInfo> getDrugsInfo() {
        return drugsInfo;
    }

    public void setDrugsInfo(List<DrugsInfo> drugsInfo) {
        this.drugsInfo = drugsInfo;
    }

    public String getVisitNo() {
        return visitNo;
    }
    public void setVisitNo(String visitNo) {
        this.visitNo = visitNo;
    }
}
