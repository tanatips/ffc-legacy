package th.in.ffc.app.form.screening.model;
/**
 * Constants for NHSO Patient data
 * Contains values for identification document types, gender codes, etc.
 */
public class NHSOConstants {
    // Document Types (TYPE field)
    public static final String DOC_TYPE_CID = "CID"; // บัตรประชาชน
    public static final String DOC_TYPE_PPN = "PPN"; // หนังสือเดินทาง
    public static final String DOC_TYPE_PWD = "PWD"; // บัตรประจำตัวคนพิการ

    // Gender Codes
    public static final String GENDER_MALE = "1";   // ชาย
    public static final String GENDER_FEMALE = "2"; // หญิง

    // Primary Key fields
    public static final String[] PRIMARY_KEYS = {"SEQ", "TYPE", "CID"};

    // Required fields
    public static final String[] REQUIRED_FIELDS = {"SEQ", "TYPE", "CID"};

    // Field validation patterns
    public static final String CID_PATTERN = "^[0-9]{13}$";                    // 13 digits for Thai ID
    public static final String PASSPORT_PATTERN = "^[A-Z0-9]{6,9}$";           // 6-9 alphanumeric chars for passport
    public static final String POSTAL_CODE_PATTERN = "^[0-9]{5}$";             // 5 digits for Thai postal code
    public static final String ISO_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";    // YYYY-MM-DD format

    // Convert Gender code to text
    public static String getGenderText(String genderCode) {
        if (GENDER_MALE.equals(genderCode)) {
            return "ชาย";
        } else if (GENDER_FEMALE.equals(genderCode)) {
            return "หญิง";
        } else {
            return "ไม่ระบุ";
        }
    }

    // Convert document type to text
    public static String getDocumentTypeText(String typeCode) {
        switch (typeCode) {
            case DOC_TYPE_CID:
                return "บัตรประชาชน";
            case DOC_TYPE_PPN:
                return "หนังสือเดินทาง";
            case DOC_TYPE_PWD:
                return "บัตรประจำตัวคนพิการ";
            default:
                return "ไม่ระบุ";
        }
    }
}
