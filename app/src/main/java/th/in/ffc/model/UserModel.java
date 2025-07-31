package th.in.ffc.model;

/**
 * Model class สำหรับข้อมูล User
 *
 * @author Generated from UserProvider
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class UserModel {

    private String pcucode;
    private String username;
    private String idcard;
    private String password;

    // Default constructor
    public UserModel() {
    }

    // Constructor with parameters
    public UserModel(String pcucode, String username, String idcard) {
        this.pcucode = pcucode;
        this.username = username;
        this.idcard = idcard;
    }

    // Constructor with all parameters
    public UserModel(String pcucode, String username, String idcard, String password) {
        this.pcucode = pcucode;
        this.username = username;
        this.idcard = idcard;
        this.password = password;
    }

    // Getter and Setter methods

    public String getPcucode() {
        return pcucode;
    }

    public void setPcucode(String pcucode) {
        this.pcucode = pcucode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Utility methods

    @Override
    public String toString() {
        return "UserModel{" +
                "pcucode='" + pcucode + '\'' +
                ", username='" + username + '\'' +
                ", idcard='" + idcard + '\'' +
                '}'; // ไม่แสดง password เพื่อความปลอดภัย
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UserModel userModel = (UserModel) obj;

        if (username != null ? !username.equals(userModel.username) : userModel.username != null)
            return false;
        if (pcucode != null ? !pcucode.equals(userModel.pcucode) : userModel.pcucode != null)
            return false;
        return idcard != null ? idcard.equals(userModel.idcard) : userModel.idcard == null;
    }

    @Override
    public int hashCode() {
        int result = pcucode != null ? pcucode.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (idcard != null ? idcard.hashCode() : 0);
        return result;
    }

    /**
     * ตรวจสอบว่าข้อมูล user ครบถ้วนหรือไม่
     *
     * @return true ถ้าข้อมูลครบถ้วน
     */
    public boolean isValid() {
        return username != null && !username.trim().isEmpty() &&
                pcucode != null && !pcucode.trim().isEmpty();
    }

    /**
     * ตรวจสอบว่ามี password หรือไม่
     *
     * @return true ถ้ามี password
     */
    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }
}