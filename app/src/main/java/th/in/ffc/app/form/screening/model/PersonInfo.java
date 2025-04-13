package th.in.ffc.app.form.screening.model;

public class PersonInfo {
    private String id;
    private String idcard;
    private String fname;
    private String lname;
    private String birthday;
    private String gender;
    private String phone;
    private String hn;
    private String authen_date;
    private String authen_code;
    private double weight;
    private double height;
    private double waist_size;
    private String bp;
    private String bmi;
    private double systolic_pressure;
    private double diastolic_pressure;
    private String serviceCode;
    private String transId;
    private String sourceId;

    private String homeNo;
    private String villageNo;
    private String subDistName;
    private String subDistCode;
    private String distCode;
    private String distName;
    private String provCode;
    private String provName;
    private String postCode;

    private String created_by;
    private String created_date;
    private String updated_by;
    private String updated_date;
    private Integer send_to_claim;
    private double temperature;
    private String hcode;

    private String claim_id;
    private String claim_status;
    private String claim_message;
    private String claim_date;

    private String visitId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHn() {
        return hn;
    }

    public void setHn(String hn) {
        this.hn = hn;
    }

    public String getAuthen_date() {
        return authen_date;
    }

    public void setAuthen_date(String authen_date) {
        this.authen_date = authen_date;
    }

    public String getAuthen_code() {
        return authen_code;
    }

    public void setAuthen_code(String authen_code) {
        this.authen_code = authen_code;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWaist_size() {
        return waist_size;
    }

    public void setWaist_size(double waist_size) {
        this.waist_size = waist_size;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public double getSystolic_pressure() {
        return systolic_pressure;
    }

    public void setSystolic_pressure(double systolic_pressure) {
        this.systolic_pressure = systolic_pressure;
    }

    public double getDiastolic_pressure() {
        return diastolic_pressure;
    }

    public void setDiastolic_pressure(double diastolic_pressure) {
        this.diastolic_pressure = diastolic_pressure;
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

    public Integer getSend_to_claim() {
        return send_to_claim;
    }

    public void setSend_to_claim(Integer send_to_claim) {

        this.send_to_claim = send_to_claim;
    }

    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSubDistName() {
        return subDistName;
    }

    public void setSubDistName(String subDistName) {
        this.subDistName = subDistName;
    }

    public String getSubDistCode() {
        return subDistCode;
    }

    public void setSubDistCode(String subDistCode) {
        this.subDistCode = subDistCode;
    }

    public String getDistCode() {
        return distCode;
    }

    public void setDistCode(String distCode) {
        this.distCode = distCode;
    }

    public String getDistName() {
        return distName;
    }

    public void setDistName(String distName) {
        this.distName = distName;
    }

    public String getProvCode() {
        return provCode;
    }

    public void setProvCode(String provCode) {
        this.provCode = provCode;
    }

    public String getProvName() {
        return provName;
    }

    public void setProvName(String provName) {
        this.provName = provName;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getHomeNo() {
        return homeNo;
    }

    public void setHomeNo(String homeNo) {
        this.homeNo = homeNo;
    }

    public String getVillageNo() {
        return villageNo;
    }

    public void setVillageNo(String villageNo) {
        this.villageNo = villageNo;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    public String getHcode() {
        return hcode;
    }
    public void setHcode(String hcode) {
        this.hcode = hcode;
    }

    public String getClaim_id() {
        return claim_id;
    }

    public void setClaim_id(String claim_id) {
        this.claim_id = claim_id;
    }

    public String getClaim_status() {
        return claim_status;
    }

    public void setClaim_status(String claim_status) {
        this.claim_status = claim_status;
    }

    public String getClaim_message() {
        return claim_message;
    }

    public void setClaim_message(String claim_message) {
        this.claim_message = claim_message;
    }

    public String getClaim_date() {
        return claim_date;
    }

    public void setClaim_date(String claim_date) {
        this.claim_date = claim_date;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }
}

