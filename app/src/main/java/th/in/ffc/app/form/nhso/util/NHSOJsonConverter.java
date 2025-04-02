package th.in.ffc.app.form.nhso.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.app.form.nhso.model.NHSOCHADInfo;
import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.app.form.nhso.model.NHSODiagnosisInfo;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;
import th.in.ffc.app.form.nhso.model.NHSOOPDInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientHistoryInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;

/**
 * ยูทิลิตี้สำหรับการแปลงข้อมูลโมเดล NHSO เป็น JSON
 *
 * รายละเอียดแฟ้มข้อมูล NHSO:
 * - แฟ้มที่ 1: ข้อมูลผู้ป่วย (NHSOPatientInfo)
 * - แฟ้มที่ 2: ข้อมูลสถานพยาบาล (NHSOHospitalInfo)
 * - แฟ้มที่ 3: ข้อมูลผู้ให้บริการ (NHSOPractitionerInfo)
 * - แฟ้มที่ 4: ข้อมูลการรับบริการผู้ป่วยนอก (NHSOOPDInfo)
 * - แฟ้มที่ 5: ข้อมูลวินิจฉัยโรค (NHSODiagnosisInfo)
 * - แฟ้มที่ 7: ข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ (NHSOCHADInfo)
 * - แฟ้มที่ 8: ข้อมูลรายละเอียดทางการเงิน (NHSOCHAInfo)
 */
public class NHSOJsonConverter {
    private static final String TAG = "NHSOJsonConverter";
    private static final SimpleDateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
    private static final SimpleDateFormat ISO_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * แปลงข้อมูลผู้ป่วย NHSO เป็น JSON Object (แฟ้มที่ 1)
     * @param patientInfo ข้อมูลผู้ป่วย (แฟ้มที่ 1)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject patientToJson(NHSOPatientInfo patientInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (patientInfo == null) return jsonObject;

            jsonObject.put("SEQ", patientInfo.getSeq());
            jsonObject.put("TYPE", patientInfo.getType());
            jsonObject.put("CID", patientInfo.getCid());
            jsonObject.put("PPN", patientInfo.getPpn());
            jsonObject.put("PWD", patientInfo.getPwd());
            jsonObject.put("NAME.GIVEN", patientInfo.getNameGiven());
            jsonObject.put("NAME.FAMILY", patientInfo.getNameFamily());
            jsonObject.put("BIRTHDATE", patientInfo.getBirthDate());
            jsonObject.put("GENDER", patientInfo.getGender());
            jsonObject.put("ADDRESS.LINE", patientInfo.getAddressLine());
            jsonObject.put("ADDRESS.CITY", patientInfo.getAddressCity());
            jsonObject.put("ADDRESS.DISTRICT", patientInfo.getAddressDistrict());
            jsonObject.put("ADDRESS.STATE", patientInfo.getAddressState());
            jsonObject.put("ADDRESS.POSTALCODE", patientInfo.getAddressPostalCode());
            jsonObject.put("NATIONALITY", patientInfo.getNationality());
            jsonObject.put("RACE", patientInfo.getRace());
            jsonObject.put("HN", patientInfo.getHn());
            jsonObject.put("AN", patientInfo.getAn());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting patient to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลสถานพยาบาล NHSO เป็น JSON Object (แฟ้มที่ 2)
     * @param hospitalInfo ข้อมูลสถานพยาบาล (แฟ้มที่ 2)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject hospitalToJson(NHSOHospitalInfo hospitalInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (hospitalInfo == null) return jsonObject;

            jsonObject.put("SEQ", hospitalInfo.getSeq());
            jsonObject.put("HCODE", hospitalInfo.getHcode());
            jsonObject.put("HCODE_NAME", hospitalInfo.getHcodeName());
            jsonObject.put("HCODE_SEND", hospitalInfo.getHcodeSend());
            jsonObject.put("HCODE_SEND_NAME", hospitalInfo.getHcodeSendName());
            jsonObject.put("HMAIN", hospitalInfo.getHmain());
            jsonObject.put("HMAIN_NAME", hospitalInfo.getHmainName());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting hospital to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลผู้ให้บริการ NHSO เป็น JSON Object (แฟ้มที่ 3)
     * @param practitionerInfo ข้อมูลผู้ให้บริการ (แฟ้มที่ 3)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject practitionerToJson(NHSOPractitionerInfo practitionerInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (practitionerInfo == null) return jsonObject;

            jsonObject.put("SEQ", practitionerInfo.getSeq());
            jsonObject.put("HCODE", practitionerInfo.getHcode());
            jsonObject.put("CID", practitionerInfo.getCid());
            jsonObject.put("PROFESSION_ID", practitionerInfo.getProfessionId());
            jsonObject.put("COUNCIL", practitionerInfo.getCouncil());
            jsonObject.put("PROVIDERTYPE", practitionerInfo.getProviderType());
            jsonObject.put("NAME.GIVEN", practitionerInfo.getNameGiven());
            jsonObject.put("NAME.FAMILY", practitionerInfo.getNameFamily());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting practitioner to JSON", e);
            return null;
        }
    }

    /**
     * แปลงรายการข้อมูลผู้ให้บริการ NHSO เป็น JSON Array (แฟ้มที่ 3)
     * @param practitionerInfoList รายการข้อมูลผู้ให้บริการ (แฟ้มที่ 3)
     * @return JSONArray หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONArray practitionerListToJson(List<NHSOPractitionerInfo> practitionerInfoList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (practitionerInfoList == null || practitionerInfoList.isEmpty()) return jsonArray;

            for (NHSOPractitionerInfo practitionerInfo : practitionerInfoList) {
                JSONObject jsonObject = practitionerToJson(practitionerInfo);
                if (jsonObject != null) {
                    jsonArray.put(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            Log.e(TAG, "Error converting practitioner list to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลผู้ป่วยนอก NHSO เป็น JSON Object (แฟ้มที่ 4)
     * @param opdInfo ข้อมูลผู้ป่วยนอก (แฟ้มที่ 4)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject opdToJson(NHSOOPDInfo opdInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (opdInfo == null) return jsonObject;

            jsonObject.put("SEQ", opdInfo.getSeq());

            if (opdInfo.getDateOPD() != null) {
                jsonObject.put("DATEOPD", ISO_DATE_FORMAT.format(opdInfo.getDateOPD()));
            }

            jsonObject.put("INSCL", opdInfo.getInscl());
            jsonObject.put("PERMITNO", opdInfo.getPermitNo());
            jsonObject.put("HTYPE", opdInfo.getHtype());
            jsonObject.put("UUC", opdInfo.getUuc());
            jsonObject.put("CHIEFCOMP", opdInfo.getChiefcomp());

            if (opdInfo.getBtemp() != null) {
                jsonObject.put("BTEMP", opdInfo.getBtemp());
            }

            if (opdInfo.getSbp() != null) {
                jsonObject.put("SBP", opdInfo.getSbp());
            }

            if (opdInfo.getDbp() != null) {
                jsonObject.put("DBP", opdInfo.getDbp());
            }

            if (opdInfo.getPr() != null) {
                jsonObject.put("PR", opdInfo.getPr());
            }

            if (opdInfo.getRr() != null) {
                jsonObject.put("RR", opdInfo.getRr());
            }

            if (opdInfo.getWaistline() != null) {
                jsonObject.put("WAISTLINE", opdInfo.getWaistline());
            }

            if (opdInfo.getWeight() != null) {
                jsonObject.put("WEIGHT", opdInfo.getWeight());
            }

            if (opdInfo.getHeight() != null) {
                jsonObject.put("HEIGHT", opdInfo.getHeight());
            }

            if (opdInfo.getHeadcircum() != null) {
                jsonObject.put("HEADCIRCUM", opdInfo.getHeadcircum());
            }

            jsonObject.put("CLINIC", opdInfo.getClinic());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting OPD to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลวินิจฉัยโรค NHSO เป็น JSON Object (แฟ้มที่ 5)
     * @param diagnosisInfo ข้อมูลวินิจฉัยโรค (แฟ้มที่ 5)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject diagnosisToJson(NHSODiagnosisInfo diagnosisInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (diagnosisInfo == null) return jsonObject;

            jsonObject.put("SEQ", diagnosisInfo.getSeq());

            if (diagnosisInfo.getDateDx() != null) {
                jsonObject.put("DATEDX", ISO_DATE_FORMAT.format(diagnosisInfo.getDateDx()));
            }

            jsonObject.put("DIAG", diagnosisInfo.getDiag());
            jsonObject.put("DIAGTYPE", diagnosisInfo.getDiagType());
            jsonObject.put("PROFESSION_ID", diagnosisInfo.getProfessionId());
            jsonObject.put("CLINIC", diagnosisInfo.getClinic());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting diagnosis to JSON", e);
            return null;
        }
    }

    /**
     * แปลงรายการข้อมูลวินิจฉัยโรค NHSO เป็น JSON Array (แฟ้มที่ 5)
     * @param diagnosisInfoList รายการข้อมูลวินิจฉัยโรค (แฟ้มที่ 5)
     * @return JSONArray หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONArray diagnosisListToJson(List<NHSODiagnosisInfo> diagnosisInfoList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (diagnosisInfoList == null || diagnosisInfoList.isEmpty()) return jsonArray;

            for (NHSODiagnosisInfo diagnosisInfo : diagnosisInfoList) {
                JSONObject jsonObject = diagnosisToJson(diagnosisInfo);
                if (jsonObject != null) {
                    jsonArray.put(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            Log.e(TAG, "Error converting diagnosis list to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลรายละเอียดทางการเงิน NHSO เป็น JSON Object (แฟ้มที่ 8)
     * @param chaInfo ข้อมูลรายละเอียดทางการเงิน (แฟ้มที่ 8)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject chaToJson(NHSOCHAInfo chaInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (chaInfo == null) return jsonObject;

            jsonObject.put("SEQ", chaInfo.getSeq());

            if (chaInfo.getDate() != null) {
                jsonObject.put("DATE", ISO_DATE_FORMAT.format(chaInfo.getDate()));
            }

            jsonObject.put("CHRGITEM", chaInfo.getChrgitem());
            jsonObject.put("INVOICE_NO", chaInfo.getInvoiceNo());

            if (chaInfo.getAmount() != null) {
                jsonObject.put("AMOUNT", chaInfo.getAmount());
            }

            if (chaInfo.getTotal() != null) {
                jsonObject.put("TOTAL", chaInfo.getTotal());
            }

            jsonObject.put("OPD_MEMO", chaInfo.getOpdMemo());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting CHA to JSON", e);
            return null;
        }
    }

    /**
     * แปลงรายการข้อมูลรายละเอียดทางการเงิน NHSO เป็น JSON Array (แฟ้มที่ 8)
     * @param chaInfoList รายการข้อมูลรายละเอียดทางการเงิน (แฟ้มที่ 8)
     * @return JSONArray หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONArray chaListToJson(List<NHSOCHAInfo> chaInfoList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (chaInfoList == null || chaInfoList.isEmpty()) return jsonArray;

            for (NHSOCHAInfo chaInfo : chaInfoList) {
                JSONObject jsonObject = chaToJson(chaInfo);
                if (jsonObject != null) {
                    jsonArray.put(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            Log.e(TAG, "Error converting CHA list to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ NHSO เป็น JSON Object (แฟ้มที่ 7)
     * @param chadInfo ข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ (แฟ้มที่ 7)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject chadToJson(NHSOCHADInfo chadInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (chadInfo == null) return jsonObject;

            jsonObject.put("SEQ", chadInfo.getSeq());
            jsonObject.put("STDCODE", chadInfo.getStdcode());
            jsonObject.put("INVOICE_NO", chadInfo.getInvoiceNo());

            if (chadInfo.getServdate() != null) {
                jsonObject.put("SERVDATE", ISO_DATE_FORMAT.format(chadInfo.getServdate()));
            }

            jsonObject.put("LOCALCODE", chadInfo.getLocalcode());
            jsonObject.put("DESCRIPT", chadInfo.getDescript());

            if (chadInfo.getQty() != null) {
                jsonObject.put("QTY", chadInfo.getQty());
            }

            if (chadInfo.getUnitprice() != null) {
                jsonObject.put("UNITPRICE", chadInfo.getUnitprice());
            }

            if (chadInfo.getChargeamt() != null) {
                jsonObject.put("CHARGEAMT", chadInfo.getChargeamt());
            }

            jsonObject.put("BILLGRCS", chadInfo.getBillgrcs());
            jsonObject.put("CODESYS", chadInfo.getCodesys());
            jsonObject.put("LAB_RESULT", chadInfo.getLabResult());
            jsonObject.put("UNIT", chadInfo.getUnit());

            if (chadInfo.getReimbprice() != null) {
                jsonObject.put("REIMBPRICE", chadInfo.getReimbprice());
            }

            jsonObject.put("XRAY_RESULT", chadInfo.getXrayResult());
            jsonObject.put("PATHO_RESULT", chadInfo.getPathoResult());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting CHAD to JSON", e);
            return null;
        }
    }

    /**
     * แปลงรายการข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ NHSO เป็น JSON Array (แฟ้มที่ 7)
     * @param chadInfoList รายการข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ (แฟ้มที่ 7)
     * @return JSONArray หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONArray chadListToJson(List<NHSOCHADInfo> chadInfoList) {
        try {
            JSONArray jsonArray = new JSONArray();
            if (chadInfoList == null || chadInfoList.isEmpty()) return jsonArray;

            for (NHSOCHADInfo chadInfo : chadInfoList) {
                JSONObject jsonObject = chadToJson(chadInfo);
                if (jsonObject != null) {
                    jsonArray.put(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            Log.e(TAG, "Error converting CHAD list to JSON", e);
            return null;
        }
    }

    /**
     * แปลงข้อมูลประวัติการส่งข้อมูลผู้ป่วย NHSO เป็น JSON Object
     * @param patientHistoryInfo ข้อมูลประวัติการส่งข้อมูลผู้ป่วย
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject patientHistoryToJson(NHSOPatientHistoryInfo patientHistoryInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (patientHistoryInfo == null) return jsonObject;

            jsonObject.put("id", patientHistoryInfo.getId());
            jsonObject.put("patientId", patientHistoryInfo.getPatientId());
            jsonObject.put("cid", patientHistoryInfo.getCid());
            jsonObject.put("status", patientHistoryInfo.getStatus());
            jsonObject.put("message", patientHistoryInfo.getMessage());
            jsonObject.put("responseCode", patientHistoryInfo.getResponseCode());
            jsonObject.put("responseBody", patientHistoryInfo.getResponseBody());
            jsonObject.put("createdBy", patientHistoryInfo.getCreatedBy());
            jsonObject.put("createdDate", patientHistoryInfo.getCreatedDate());

            return jsonObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error converting patient history to JSON", e);
            return null;
        }
    }

    /**
     * สร้าง JSON หลักสำหรับส่งข้อมูลไปยัง NHSO
     * @param patientInfo ข้อมูลผู้ป่วย (แฟ้มที่ 1)
     * @param hospitalInfo ข้อมูลสถานพยาบาล (แฟ้มที่ 2)
     * @param practitionerInfoList รายการข้อมูลผู้ให้บริการ (แฟ้มที่ 3)
     * @param opdInfo ข้อมูลผู้ป่วยนอก (แฟ้มที่ 4)
     * @param diagnosisInfoList รายการข้อมูลวินิจฉัยโรค (แฟ้มที่ 5)
     * @param chaInfoList รายการข้อมูลรายละเอียดทางการเงิน (แฟ้มที่ 8)
     * @param chadInfoList รายการข้อมูลรายละเอียดค่าใช้จ่ายรายรายการ (แฟ้มที่ 7)
     * @return JSONObject หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static JSONObject createNHSORequestJson(
            NHSOPatientInfo patientInfo,
            NHSOHospitalInfo hospitalInfo,
            List<NHSOPractitionerInfo> practitionerInfoList,
            NHSOOPDInfo opdInfo,
            List<NHSODiagnosisInfo> diagnosisInfoList,
            List<NHSOCHAInfo> chaInfoList,
            List<NHSOCHADInfo> chadInfoList) {

        try {
            JSONObject mainObject = new JSONObject();
            JSONArray fsDataArray = new JSONArray();
            JSONObject fsDataObject = new JSONObject();

            // เพิ่มข้อมูลผู้ป่วย
            JSONObject patientObject = patientToJson(patientInfo);
            if (patientObject != null) {
                fsDataObject.put("patient", patientObject);
            }

            // เพิ่มข้อมูลสถานพยาบาล
            JSONObject hospitalObject = hospitalToJson(hospitalInfo);
            if (hospitalObject != null) {
                fsDataObject.put("provider", hospitalObject);
            }

            // เพิ่มข้อมูลผู้ให้บริการ
            JSONArray practitionerArray = practitionerListToJson(practitionerInfoList);
            if (practitionerArray != null) {
                fsDataObject.put("practitioner", practitionerArray);
            }

            // เพิ่มข้อมูลผู้ป่วยนอก
            JSONObject opdObject = opdToJson(opdInfo);
            if (opdObject != null) {
                fsDataObject.put("opd", opdObject);
            }

            // เพิ่มข้อมูลวินิจฉัยโรค
            JSONArray diagnosisArray = diagnosisListToJson(diagnosisInfoList);
            if (diagnosisArray != null) {
                fsDataObject.put("diagnosis", diagnosisArray);
            }

            // เพิ่มข้อมูลทางการเงิน
            JSONArray chaArray = chaListToJson(chaInfoList);
            if (chaArray != null) {
                fsDataObject.put("cha", chaArray);
            }

            // เพิ่มข้อมูลรายละเอียดค่าใช้จ่าย
            JSONArray chadArray = chadListToJson(chadInfoList);
            if (chadArray != null) {
                fsDataObject.put("chad", chadArray);
            }

            fsDataArray.put(fsDataObject);
            mainObject.put("fsDatas", fsDataArray);

            return mainObject;
        } catch (JSONException e) {
            Log.e(TAG, "Error creating NHSO request JSON", e);
            return null;
        }
    }

    /**
     * แปลงวันที่จาก String เป็น Date
     * @param dateString วันที่ในรูปแบบ String (ISO 8601)
     * @return Date หรือ null ถ้าเกิดข้อผิดพลาด
     */
    public static Date parseISODate(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) {
                return null;
            }

            if (dateString.length() <= 10) {
                return ISO_DATE_ONLY_FORMAT.parse(dateString);
            } else {
                return ISO_DATE_FORMAT.parse(dateString);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            return null;
        }
    }
}