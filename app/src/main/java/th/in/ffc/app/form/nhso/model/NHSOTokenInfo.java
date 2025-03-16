package th.in.ffc.app.form.nhso.model;

/**
 * โมเดลข้อมูล Token สำหรับ NHSO API
 */
public class NHSOTokenInfo {
    private long id;
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private int expiresIn;
    private String createdDate;
    private String updatedDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * สร้าง Authorization Header สำหรับใช้กับ API
     * @return รูปแบบ "Bearer <token>"
     */
    public String getAuthorizationHeader() {
        if (tokenType != null && accessToken != null) {
            return tokenType + " " + accessToken;
        }
        return null;
    }

    /**
     * ตรวจสอบว่ามีข้อมูล Token ครบถ้วนหรือไม่
     * @return true ถ้ามีข้อมูลครบถ้วน, false ถ้าข้อมูลไม่ครบถ้วน
     */
    public boolean isValid() {
        return accessToken != null && !accessToken.isEmpty() &&
                tokenType != null && !tokenType.isEmpty() &&
                expiresIn > 0;
    }
}