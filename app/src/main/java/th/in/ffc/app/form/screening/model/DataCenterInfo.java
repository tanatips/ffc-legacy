package th.in.ffc.app.form.screening.model;

import th.in.ffc.person.genogram.V1.Person;

public class DataCenterInfo {
    PersonInfo personInfo;
    SmokerInfo smokerInfo;

    public PersonInfo getPersonInfo() {
        return personInfo;
    }

    public void setPersonInfo(PersonInfo personInfo) {
        this.personInfo = personInfo;
    }

    public SmokerInfo getSmokerInfo() {
        return smokerInfo;
    }

    public void setSmokerInfo(SmokerInfo smokerInfo) {
        this.smokerInfo = smokerInfo;
    }
}
