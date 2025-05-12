package th.in.ffc.app.form.screening.model;

public  class VisitDiagInfo {
    private String pcucode;
    private String visitno;
    private String diagcode;
    private String dxtype;
    private String clinic;
    private String continue_field;  // ใช้ continue_field แทน continue เนื่องจาก continue เป็น keyword
    private String appointdate;
    private String appointtype;
    private String doctor;
    private String dateupdate;

    public String getPcucode() {
        return pcucode;
    }

    public void setPcucode(String pcucode) {
        this.pcucode = pcucode;
    }

    public String getVisitno() {
        return visitno;
    }

    public void setVisitno(String visitno) {
        this.visitno = visitno;
    }

    public String getDiagcode() {
        return diagcode;
    }

    public void setDiagcode(String diagcode) {
        this.diagcode = diagcode;
    }

    public String getDxtype() {
        return dxtype;
    }

    public void setDxtype(String dxtype) {
        this.dxtype = dxtype;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getContinue() {
        return continue_field;
    }

    public void setContinue(String continue_field) {
        this.continue_field = continue_field;
    }

    public String getAppointdate() {
        return appointdate;
    }

    public void setAppointdate(String appointdate) {
        this.appointdate = appointdate;
    }

    public String getAppointtype() {
        return appointtype;
    }

    public void setAppointtype(String appointtype) {
        this.appointtype = appointtype;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getDateupdate() {
        return dateupdate;
    }

    public void setDateupdate(String dateupdate) {
        this.dateupdate = dateupdate;
    }
}
