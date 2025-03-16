package th.in.ffc.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import th.in.ffc.app.form.screening.model.NHSOConstants;
import th.in.ffc.app.form.screening.model.NHSOPatient;

/**
 * Utility class for validating NHSO Patient data
 */
public class NHSOValidator {

    /**
     * Validates a Thai ID card number (CID)
     * @param cid Thai citizen ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidThaiID(String cid) {
        if (cid == null || !Pattern.matches(NHSOConstants.CID_PATTERN, cid)) {
            return false;
        }

        // Check digit validation algorithm for Thai ID
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (Integer.parseInt(String.valueOf(cid.charAt(i))) * (13 - i));
        }

        int checkDigit = (11 - (sum % 11)) % 10;
        return checkDigit == Integer.parseInt(String.valueOf(cid.charAt(12)));
    }

    /**
     * Validates required fields in NHSO Patient
     * @param patient The patient to validate
     * @return List of validation error messages, empty if valid
     */
    public static List<String> validatePatient(NHSOPatient patient) {
        List<String> errors = new ArrayList<>();

        // Check required fields
        if (patient.getSeq() == null || patient.getSeq().isEmpty()) {
            errors.add("SEQ (รหัสการบริการ) is required");
        }

        if (patient.getType() == null || patient.getType().isEmpty()) {
            errors.add("TYPE (ประเภทเอกสาร) is required");
        }

        if (patient.getCid() == null || patient.getCid().isEmpty()) {
            errors.add("CID (เลขประจำตัวประชาชน) is required");
        } else if (!isValidThaiID(patient.getCid())) {
            errors.add("CID (เลขประจำตัวประชาชน) is invalid");
        }

        // Check format of date fields
        if (patient.getBirthDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String formattedDate = dateFormat.format(patient.getBirthDate());
            if (!Pattern.matches(NHSOConstants.ISO_DATE_PATTERN, formattedDate)) {
                errors.add("BIRTHDATE must be in ISO 8601 format (YYYY-MM-DD)");
            }
        }

        // Check postal code format if provided
        if (patient.getAddressPostalCode() != null && !patient.getAddressPostalCode().isEmpty()) {
            if (!Pattern.matches(NHSOConstants.POSTAL_CODE_PATTERN, patient.getAddressPostalCode())) {
                errors.add("ADDRESS_POSTAL_CODE must be 5 digits");
            }
        }

        // Check gender code if provided
        if (patient.getGender() != null && !patient.getGender().isEmpty()) {
            if (!NHSOConstants.GENDER_MALE.equals(patient.getGender()) &&
                    !NHSOConstants.GENDER_FEMALE.equals(patient.getGender())) {
                errors.add("GENDER must be 1 (ชาย) or 2 (หญิง)");
            }
        }

        return errors;
    }

    /**
     * Checks if document type is valid
     * @param type Document type code
     * @return true if valid, false otherwise
     */
    public static boolean isValidDocumentType(String type) {
        return NHSOConstants.DOC_TYPE_CID.equals(type) ||
                NHSOConstants.DOC_TYPE_PPN.equals(type) ||
                NHSOConstants.DOC_TYPE_PWD.equals(type);
    }
}