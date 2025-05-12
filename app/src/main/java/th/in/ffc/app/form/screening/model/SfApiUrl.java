package th.in.ffc.app.form.screening.model;

/**
 * Model class สำหรับเก็บข้อมูล URL API
 */
public class SfApiUrl {
    public static final int ENV_TEST = 0;
    public static final int ENV_PRODUCTION = 1;

    private long id;
    private String apiCode;
    private String apiName;
    private String description;
    private String method;
    private String testUrl;
    private String prodUrl;
    private String params;
    private String requestFormat;
    private int isActive;
    private int envType;
    private long createdAt;
    private long updatedAt;

    public SfApiUrl() {
        // Default constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTestUrl() {
        return testUrl;
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public void setProdUrl(String prodUrl) {
        this.prodUrl = prodUrl;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(String requestFormat) {
        this.requestFormat = requestFormat;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getEnvType() {
        return envType;
    }

    public void setEnvType(int envType) {
        this.envType = envType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * คืนค่า URL ตามสภาพแวดล้อมที่กำหนด
     *
     * @return URL ที่ใช้งาน
     */
    public String getActiveUrl() {
        return envType == ENV_PRODUCTION ? prodUrl : testUrl;
    }

    /**
     * คืนค่า URL ตามสภาพแวดล้อมที่ระบุ
     *
     * @param envType สภาพแวดล้อม (ENV_TEST หรือ ENV_PRODUCTION)
     * @return URL ตามสภาพแวดล้อม
     */
    public String getUrlByEnvType(int envType) {
        return envType == ENV_PRODUCTION ? prodUrl : testUrl;
    }
}
