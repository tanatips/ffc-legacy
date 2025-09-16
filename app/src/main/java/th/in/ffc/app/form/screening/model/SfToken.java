package th.in.ffc.app.form.screening.model;

public class SfToken {
    private int id;
    private String tokenAuth;
    private String tokenClaim;
    private String createdDate;
    private String updatedDate;

    // Constructor
    public SfToken() {
    }

    public SfToken(int id, String tokenAuth, String tokenClaim, String createdDate, String updatedDate) {
        this.id = id;
        this.tokenAuth = tokenAuth;
        this.tokenClaim = tokenClaim;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTokenAuth() {
        return tokenAuth;
    }

    public void setTokenAuth(String tokenAuth) {
        this.tokenAuth = tokenAuth;
    }

    public String getTokenClaim() {
        return tokenClaim;
    }

    public void setTokenClaim(String tokenClaim) {
        this.tokenClaim = tokenClaim;
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
}
