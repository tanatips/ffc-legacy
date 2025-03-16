package th.in.ffc.app.form.nhso.model;

/**
 * โมเดลข้อมูลประวัติการอ่านบัตรประชาชน
 */
public class NHSOCardReadingHistoryInfo {
    private long id;
    private String readTimestamp;
    private String username;
    private String citizenId;
    private String citizenName;
    private String deviceModel;
    private String deviceBrand;
    private String cardReaderModel;
    private String appVersion;
    private String readStatus;
    private String notes;
    private String createdAt;

    // สถานะการอ่านบัตร
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_TIMEOUT = "TIMEOUT";
    public static final String STATUS_CARD_ERROR = "CARD_ERROR";
    public static final String STATUS_READER_ERROR = "READER_ERROR";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReadTimestamp() {
        return readTimestamp;
    }

    public void setReadTimestamp(String readTimestamp) {
        this.readTimestamp = readTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getCardReaderModel() {
        return cardReaderModel;
    }

    public void setCardReaderModel(String cardReaderModel) {
        this.cardReaderModel = cardReaderModel;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * ตรวจสอบว่าการอ่านบัตรสำเร็จหรือไม่
     * @return true ถ้าการอ่านบัตรสำเร็จ, false ถ้าการอ่านบัตรไม่สำเร็จ
     */
    public boolean isReadSuccess() {
        return STATUS_SUCCESS.equals(readStatus);
    }

    /**
     * แปลงสถานะการอ่านบัตรเป็นข้อความภาษาไทย
     * @return ข้อความสถานะการอ่านบัตรภาษาไทย
     */
    public String getReadStatusText() {
        if (readStatus == null) {
            return "ไม่ระบุ";
        }

        switch (readStatus) {
            case STATUS_SUCCESS:
                return "อ่านสำเร็จ";
            case STATUS_FAILED:
                return "อ่านไม่สำเร็จ";
            case STATUS_TIMEOUT:
                return "หมดเวลา";
            case STATUS_CARD_ERROR:
                return "บัตรมีปัญหา";
            case STATUS_READER_ERROR:
                return "เครื่องอ่านมีปัญหา";
            default:
                return readStatus;
        }
    }

    /**
     * รวมข้อมูลอุปกรณ์ที่ใช้อ่านบัตรเป็นข้อความ
     * @return ข้อความแสดงข้อมูลอุปกรณ์
     */
    public String getDeviceInfo() {
        StringBuilder deviceInfo = new StringBuilder();

        if (deviceBrand != null && !deviceBrand.isEmpty()) {
            deviceInfo.append(deviceBrand);
        }

        if (deviceModel != null && !deviceModel.isEmpty()) {
            if (deviceInfo.length() > 0) {
                deviceInfo.append(" ");
            }
            deviceInfo.append(deviceModel);
        }

        if (cardReaderModel != null && !cardReaderModel.isEmpty()) {
            if (deviceInfo.length() > 0) {
                deviceInfo.append(" / ");
            }
            deviceInfo.append("อุปกรณ์อ่านบัตร: ").append(cardReaderModel);
        }

        if (appVersion != null && !appVersion.isEmpty()) {
            if (deviceInfo.length() > 0) {
                deviceInfo.append(" / ");
            }
            deviceInfo.append("เวอร์ชัน: ").append(appVersion);
        }

        return deviceInfo.length() > 0 ? deviceInfo.toString() : "ไม่ระบุ";
    }

    /**
     * ฟอร์แมตเลขบัตรประชาชนให้อ่านง่าย
     * @return เลขบัตรประชาชนรูปแบบ X-XXXX-XXXXX-XX-X
     */
    public String getFormattedCitizenId() {
        if (citizenId == null || citizenId.length() != 13) {
            return citizenId;
        }

        return citizenId.substring(0, 1) + "-" +
                citizenId.substring(1, 5) + "-" +
                citizenId.substring(5, 10) + "-" +
                citizenId.substring(10, 12) + "-" +
                citizenId.substring(12);
    }
}