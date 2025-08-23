package th.in.ffc.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import th.in.ffc.api.nhso.FSDataResponse;
import th.in.ffc.api.nhso.NHSOFSDataApiCaller;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.nhso.model.NHSOCHADInfo;
import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.app.form.nhso.model.NHSODiagnosisInfo;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;
import th.in.ffc.app.form.nhso.model.NHSOOPDInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;
import th.in.ffc.app.form.nhso.service.NHSOCHADService;
import th.in.ffc.app.form.nhso.service.NHSOCHAService;
import th.in.ffc.app.form.nhso.service.NHSODiagnosisService;
import th.in.ffc.app.form.nhso.service.NHSOHospitalService;
import th.in.ffc.app.form.nhso.service.NHSOOPDService;
import th.in.ffc.app.form.nhso.service.NHSOPatientService;
import th.in.ffc.app.form.nhso.service.NHSOPractitionerService;
import th.in.ffc.app.form.nhso.util.NHSOJsonConverter;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.app.form.screening.model.VisitDiagInfo;
import th.in.ffc.dao.NHSOClaimDataDao;
import th.in.ffc.dao.VisitDiagDao;
import th.in.ffc.provider.NHSOCHA;
import th.in.ffc.security.LoginActivity;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.InvoiceNumberGenerator;
import th.in.ffc.util.Log;

/**
 * Service class สำหรับการส่งเคลมไปยัง NHSO
 * แยกออกมาจาก PersonAdapter เพื่อใช้งานในส่วนอื่นๆ ได้
 */
public class ClaimSubmissionService {
    private static final String TAG = "ClaimSubmissionService";
    public static final String EXTRA_PCUCODE = "pcucode";

    private Context context;
    private SimpleDateFormat dateFormat;
    private ClaimSubmissionListener listener;

    /**
     * Interface สำหรับรับ callback ผลการส่งเคลม
     */
    public interface ClaimSubmissionListener {
        void onClaimSubmissionSuccess(String message, String seqNo);
        void onClaimSubmissionError(String errorMessage);
        void onClaimSubmissionProgress(String message);
    }

    /**
     * Constructor
     * @param context Context ของแอปพลิเคชัน
     */
    public ClaimSubmissionService(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss", Locale.US);
    }

    /**
     * ตั้งค่า Listener สำหรับรับ callback
     * @param listener ClaimSubmissionListener
     */
    public void setClaimSubmissionListener(ClaimSubmissionListener listener) {
        this.listener = listener;
    }

    /**
     * ส่งเคลมสำหรับผู้ป่วยคนหนึ่ง
     * @param personInfo ข้อมูลผู้ป่วย
     */
    public void submitClaim(PersonInfo personInfo) {
        if (personInfo == null) {
            notifyError("ข้อมูลผู้ป่วยไม่ครบถ้วน");
            return;
        }

        try {
            notifyProgress("เริ่มต้นการส่งเคลม...");

            // เตรียมข้อมูลพื้นฐาน
            UserSessionManager userSessionManager = new UserSessionManager(context);
            String user = userSessionManager.getUser();
            String[] names = (personInfo.getFname() + " " + personInfo.getLname()).split(" ");

            List<PersonInfo> personInfos = SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(personInfo.getId()));
            PersonInfo updatedPersonInfo = new PersonInfo();

            SharedPreferences prefs = context.getSharedPreferences(LoginActivity.PREFS_FILE, Context.MODE_PRIVATE);
            String pcuCode = prefs.getString(EXTRA_PCUCODE, "");

            if (!personInfos.isEmpty()) {
                updatedPersonInfo = personInfos.get(0);
                updatedPersonInfo.setHcode(pcuCode);
            }

            String seq = updatedPersonInfo.getSeq();

            notifyProgress("สร้างข้อมูลผู้ป่วย...");

            // สร้างข้อมูลแต่ละแฟ้ม
            NHSOPatientInfo patient = createPatientInfo(updatedPersonInfo, names, seq, userSessionManager);
            NHSOHospitalInfo hospital = createHospitalInfo(seq, updatedPersonInfo.getHcode(), userSessionManager);
            List<NHSOPractitionerInfo> practitioners = createPractitionerInfo(seq, updatedPersonInfo.getHcode(), userSessionManager);

            // ตรวจสอบ Token และสร้าง OPD Info
            createOPDInfoWithToken(updatedPersonInfo, seq, userSessionManager,
                    patient, hospital, practitioners);

        } catch (Exception e) {
            Log.e(TAG, "Error in submitClaim: " + e.getMessage());
            notifyError("เกิดข้อผิดพลาดในการส่งเคลม: " + e.getMessage());
        }
    }

    /**
     * สร้างข้อมูลผู้ป่วย (แฟ้ม 1)
     */
    private NHSOPatientInfo createPatientInfo(PersonInfo personInfo, String[] names, String seq, UserSessionManager userSessionManager) {
        NHSOPatientService nhsoPatientService = new NHSOPatientService(context);
        NHSOPatientInfo patient = new NHSOPatientInfo();

        patient.setType("CID");
        patient.setCid(personInfo.getIdcard());
        patient.setNameGiven(names[0]);
        patient.setNameFamily(names.length > 1 ? names[1] : "");
        patient.setSeq(seq);
        patient.setBirthDate(personInfo.getBirthday());
        patient.setGender(personInfo.getGender().equals("M") ? "1" : "2");
        patient.setAddressLine(personInfo.getHomeNo() + " หมู่ที่ " + personInfo.getVillageNo());
        patient.setAddressCity(personInfo.getSubDistCode());
        patient.setAddressDistrict(personInfo.getDistCode());
        patient.setAddressState(personInfo.getProvCode());
        patient.setAddressPostalCode(personInfo.getPostCode());
        patient.setHn(personInfo.getHn());

        // ตรวจสอบว่าข้อมูลมีอยู่แล้วหรือไม่ก่อนสร้างใหม่
        try {
            nhsoPatientService.createPatient(patient, userSessionManager.getUser());
        } catch (Exception e) {
            Log.w(TAG, "Patient data may already exist: " + e.getMessage());
            // ถ้าข้อมูลมีอยู่แล้ว ให้ใช้ข้อมูลเดิม
        }
        return patient;
    }

    /**
     * สร้างข้อมูลสถานพยาบาล (แฟ้ม 2)
     */
    private NHSOHospitalInfo createHospitalInfo(String seq, String hcode, UserSessionManager userSessionManager) {
        NHSOHospitalService nhsoHospitalService = new NHSOHospitalService(context);
        NHSOHospitalInfo hospital = new NHSOHospitalInfo();

        hospital.setSeq(seq);
        hospital.setHcode(hcode);

        try {
            nhsoHospitalService.createHospital(hospital, userSessionManager.getUser());
        } catch (Exception e) {
            Log.w(TAG, "Hospital data may already exist: " + e.getMessage());
        }
        return hospital;
    }

    /**
     * สร้างข้อมูลผู้ให้บริการ (แฟ้ม 3)
     */
    private List<NHSOPractitionerInfo> createPractitionerInfo(String seq, String hcode, UserSessionManager userSessionManager) {
        NHSOPractitionerService nhsoPractitionerService = new NHSOPractitionerService(context);
        NHSOPractitionerInfo practitioner = new NHSOPractitionerInfo();
        List<NHSOPractitionerInfo> practitioners = new ArrayList<>();

        practitioner.setSeq(seq);
        practitioner.setHcode(hcode);
        // ใช้ idcard ของ practitioner แทนที่จะเป็นของผู้ป่วย
        practitioner.setCid(userSessionManager.getIdcard());

        practitioners.add(practitioner);

        try {
            nhsoPractitionerService.createPractitioner(practitioner, userSessionManager.getUser());
        } catch (Exception e) {
            Log.w(TAG, "Practitioner data may already exist: " + e.getMessage());
        }

        return practitioners;
    }

    /**
     * สร้างข้อมูล OPD พร้อมตรวจสอบ Token
     */
    private void createOPDInfoWithToken(PersonInfo personInfo, String seq, UserSessionManager userSessionManager,
                                        NHSOPatientInfo patient, NHSOHospitalInfo hospital,
                                        List<NHSOPractitionerInfo> practitioners) {

        notifyProgress("ตรวจสอบสิทธิการรักษา...");

        NHSOOPDService nhsoopdService = new NHSOOPDService(context);
        NHSOOPDInfo nhsoopdInfo = new NHSOOPDInfo();

        nhsoopdInfo.setSeq(seq);
        nhsoopdInfo.setHtype("1");
        nhsoopdInfo.setUuc("1");

        try {
            nhsoopdInfo.setDateOPD(dateFormat.parse(personInfo.getCreated_date()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // ตรวจสอบ Token และเรียก API
        SfTokenDao sfTokenDao = new SfTokenDao(context);
        NhsoApiCaller nhsoApiCaller = new NhsoApiCaller(context);
        List<SfToken> sfTokens = sfTokenDao.getAllTokens();
        String token = "";

        if (sfTokens.size() > 0) {
            token = sfTokens.get(0).getTokenAuth();
        }

        nhsoApiCaller.testRealPersonApi(personInfo.getIdcard(), token, new NhsoApiCaller.RealPersonApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    String inscl = nhsoApiCaller.extractInsuranceCode(response);
                    nhsoopdInfo.setInscl(inscl);
                    Log.d("NHSO: inscl", inscl);

                    nhsoopdService.createOPD(nhsoopdInfo, userSessionManager.getUser());

                    // ดำเนินการขั้นตอนถัดไป
                    processDiagnosisAndClaim(personInfo, seq, userSessionManager, patient, hospital,
                            practitioners, nhsoopdInfo);

                } catch (Exception e) {
                    Log.e(TAG, "Error processing OPD info: " + e.getMessage());
                    notifyError("เกิดข้อผิดพลาดในการประมวลผลข้อมูล OPD: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "NHSO API Error: " + errorMessage);
                notifyError("ข้อผิดพลาดในการตรวจสอบสิทธิ: " + errorMessage);
            }
        });
    }

    /**
     * ประมวลผลข้อมูลการวินิจฉัยและสร้างเคลม
     */
    private void processDiagnosisAndClaim(PersonInfo personInfo, String seq, UserSessionManager userSessionManager,
                                          NHSOPatientInfo patient, NHSOHospitalInfo hospital,
                                          List<NHSOPractitionerInfo> practitioners, NHSOOPDInfo nhsoopdInfo) {

        notifyProgress("สร้างข้อมูลการวินิจฉัย...");

        // แฟ้ม 5 - ข้อมูลการวินิจฉัย
        VisitDiagDao visitDiagDao = new VisitDiagDao(context);
        List<VisitDiagInfo> visitDiagInfos = visitDiagDao.getVisitDiagByVisitNo(personInfo.getVisitNo());
        List<NHSODiagnosisInfo> nhsoDiagnosisInfos = new ArrayList<>();
        NHSODiagnosisService nhsoDiagnosisService = new NHSODiagnosisService(context);

        for (VisitDiagInfo visitDiagInfo : visitDiagInfos) {
            NHSODiagnosisInfo nhsoDiagnosisInfo = new NHSODiagnosisInfo();
            nhsoDiagnosisInfo.setSeq(seq);
            nhsoDiagnosisInfo.setDiag(visitDiagInfo.getDiagcode().replace(".", ""));
            nhsoDiagnosisInfo.setDiagType(visitDiagInfo.getDxtype());

            try {
                nhsoDiagnosisInfo.setDateDx(dateFormat.parse(personInfo.getCreated_date()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            nhsoDiagnosisInfos.add(nhsoDiagnosisInfo);

            try {
                nhsoDiagnosisService.createDiagnosis(nhsoDiagnosisInfo, userSessionManager.getUser());
            } catch (Exception e) {
                Log.w(TAG, "Diagnosis data may already exist: " + e.getMessage());
            }
        }

        // สร้างข้อมูล CHAD และ CHA
        createCHADAndCHAInfo(personInfo, seq, userSessionManager, patient, hospital,
                practitioners, nhsoopdInfo, nhsoDiagnosisInfos);
    }

    /**
     * สร้างข้อมูล CHAD (แฟ้ม 7) และ CHA (แฟ้ม 8)
     */
    /**
     * สร้างข้อมูล CHAD (แฟ้ม 7) และ CHA (แฟ้ม 8) - ปรับปรุงให้ลบข้อมูลเก่าก่อน
     */
    private void createCHADAndCHAInfo(PersonInfo personInfo, String seq, UserSessionManager userSessionManager,
                                      NHSOPatientInfo patient, NHSOHospitalInfo hospital,
                                      List<NHSOPractitionerInfo> practitioners, NHSOOPDInfo nhsoopdInfo,
                                      List<NHSODiagnosisInfo> nhsoDiagnosisInfos) {

        notifyProgress("คำนวดค่าใช้จ่าย...");

        // คำนวดค่าใช้จ่าย
        double totalCost = calculateServiceCost(personInfo);

        // สร้าง Invoice Number
        InvoiceNumberGenerator invoiceNumberGenerator = new InvoiceNumberGenerator(context);
        String invoiceNumber = invoiceNumberGenerator.generateInvoiceNumber();

        // **เพิ่ม: ลบข้อมูล CHA เก่าก่อนที่จะ insert ใหม่**
        notifyProgress("ลบข้อมูลเก่า...");
        deletePreviousCHAData(seq);

        // แฟ้ม 7 - CHAD
        NHSOCHADService chadService = new NHSOCHADService(context);
        NHSOCHADInfo nhsochadInfo = new NHSOCHADInfo();
        List<NHSOCHADInfo> nhsochadInfos = new ArrayList<>();

        nhsochadInfo.setSeq(seq);
        nhsochadInfo.setStdcode("1170884");
        nhsochadInfo.setInvoiceNo(invoiceNumber);

        try {
            nhsochadInfo.setServdate(dateFormat.parse(personInfo.getCreated_date()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        nhsochadInfo.setCodesys("002");
        nhsochadInfo.setBillgrcs("04");
        nhsochadInfo.setQty(1);
        nhsochadInfo.setUnitprice(totalCost);
        nhsochadInfo.setChargeamt(totalCost);

        nhsochadInfos.add(nhsochadInfo);

        try {
            chadService.createCHAD(nhsochadInfo, userSessionManager.getUser());
        } catch (Exception e) {
            Log.w(TAG, "CHAD data may already exist: " + e.getMessage());
        }

        // แฟ้ม 8 - CHA
        NHSOCHAService chaService = new NHSOCHAService(context);
        NHSOCHAInfo chaInfo = new NHSOCHAInfo();
        List<NHSOCHAInfo> chaInfos = new ArrayList<>();

        chaInfo.setSeq(seq);
        try {
            chaInfo.setDate(dateFormat.parse(personInfo.getCreated_date()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        chaInfo.setChrgitem("I1");
        chaInfo.setInvoiceNo(invoiceNumber);
        chaInfo.setAmount(totalCost);
        chaInfo.setTotal(totalCost);
        // เซ็ตค่า claim_total เป็นค่าเดียวกับ total ในตอนแรก
        chaInfo.setClaimAmount(totalCost);

        chaInfos.add(chaInfo);

        try {
            chaService.createCHA(chaInfo, userSessionManager.getUser());
            Log.d(TAG, "Successfully created CHA data for seq: " + seq + ", amount: " + totalCost);
        } catch (Exception e) {
            Log.e(TAG, "Error creating CHA data: " + e.getMessage());
            notifyError("เกิดข้อผิดพลาดในการสร้างข้อมูล CHA: " + e.getMessage());
            return;
        }

        // ส่งข้อมูลไปยัง API
        sendDataToAPI(personInfo, patient, hospital, practitioners, nhsoopdInfo,
                nhsoDiagnosisInfos, chaInfos, nhsochadInfos, userSessionManager);
    }

    /**
     * ตรวจสอบว่ามีข้อมูล CHA สำหรับ seq นี้อยู่หรือไม่
     * @param seq รหัสการรับบริการ
     * @return จำนวนระเบียนที่พบ
     */
    private int checkExistingCHAData(String seq) {
        try {
            Cursor cursor = context.getContentResolver().query(
                    NHSOCHA.CONTENT_URI,
                    new String[]{"COUNT(*) AS count"},
                    NHSOCHA.SEQ + "=?",
                    new String[]{seq},
                    null
            );

            int count = 0;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
                cursor.close();
            }

            Log.d(TAG, "Found " + count + " existing CHA records for seq: " + seq);
            return count;

        } catch (Exception e) {
            Log.e(TAG, "Error checking existing CHA data for seq: " + seq);
            return -1; // return -1 เพื่อบอกว่าเกิดข้อผิดพลาด
        }
    }

    /**
     * ลบข้อมูล CHA เก่าสำหรับ seq ที่ระบุ - ปรับปรุงแล้ว
     * @param seq รหัสการรับบริการที่ต้องการลบ
     */
    private void deletePreviousCHAData(String seq) {
        try {
            Log.d(TAG, "Starting deletion of previous CHA data for seq: " + seq);

            // ตรวจสอบก่อนว่ามีข้อมูลที่ต้องลบหรือไม่
            int existingRecords = checkExistingCHAData(seq);

            if (existingRecords < 0) {
                Log.w(TAG, "Cannot check existing records - proceeding with deletion attempt");
            } else if (existingRecords == 0) {
                Log.d(TAG, "No existing CHA records found for seq: " + seq + " - skipping deletion");
                return;
            }

            // ลบข้อมูลผ่าน ContentResolver
            int deletedRows = context.getContentResolver().delete(
                    NHSOCHA.CONTENT_URI,
                    NHSOCHA.SEQ + "=?",
                    new String[]{seq}
            );

            Log.d(TAG, "Successfully deleted " + deletedRows + " CHA records for seq: " + seq);

            // ตรวจสอบว่าลบหมดแล้วหรือไม่
            if (existingRecords > 0 && deletedRows != existingRecords) {
                Log.w(TAG, "Warning: Expected to delete " + existingRecords + " records but actually deleted " + deletedRows);
            }

            // ตรวจสอบอีกครั้งว่าลบหมดแล้วจริงหรือไม่
            int remainingRecords = checkExistingCHAData(seq);
            if (remainingRecords > 0) {
                Log.w(TAG, "Warning: " + remainingRecords + " CHA records still remain after deletion for seq: " + seq);
            } else {
                Log.d(TAG, "Confirmed: All CHA records deleted successfully for seq: " + seq);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error deleting previous CHA data for seq: " + seq + " - " + e.getMessage());
            // ไม่ throw exception เพราะไม่ควรหยุดกระบวนการทั้งหมดเพียงเพราะลบข้อมูลเก่าไม่ได้
            // แต่จะ log ไว้เพื่อ debug

            // แสดง Toast เตือนผู้ใช้ (ถ้าจำเป็น)
            notifyProgress("ไม่สามารถลบข้อมูลเก่าได้ แต่จะดำเนินการต่อ...");
        }
    }
    /**
     * คำนวณค่าใช้จ่ายสำหรับการบริการ
     */
    private double calculateServiceCost(PersonInfo personInfo) {
        try {
            int personId = Integer.parseInt(personInfo.getId());

            SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(context);
            List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos = sfHealthRiskAssessmentInfoDao.getByPersonId(personId);

            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(context);
            List<CardiovascularRiskInfo> cardiovascularRiskInfos = sfCardiovascularRiskInfoDao.getByPersonId(personId);

            double fpg = 0.0;
            double cholesterol = 0.0;

            if (!healthRiskAssessmentInfos.isEmpty()) {
                HealthRiskAssessmentInfo healthInfo = healthRiskAssessmentInfos.get(0);
                if (healthInfo.getFpg() != null && !healthInfo.getFpg().isEmpty() &&
                        !Objects.equals(healthInfo.getFpg(), "")) {
                    fpg = Double.parseDouble(healthInfo.getFpg());
                }
            }

            if (!cardiovascularRiskInfos.isEmpty()) {
                CardiovascularRiskInfo cardioInfo = cardiovascularRiskInfos.get(0);
                if (cardioInfo.getCholesterol() != null && !cardioInfo.getCholesterol().isEmpty() &&
                        !Objects.equals(cardioInfo.getCholesterol(), "")) {
                    cholesterol = Double.parseDouble(cardioInfo.getCholesterol());
                }
            }

            int age = AgeCalculator.calculateAge(personInfo.getBirthday());
            double cost13 = AgeCalculator.calculateServiceCost(age, 0, 0);
            double costFpg = 0;
            double costCholesterol = 0;
            if (age >= 35 && age <= 59) {
                costFpg = AgeCalculator.calculateServiceCost(age, fpg, 0);
                costCholesterol = AgeCalculator.calculateServiceCost(age, 0, cholesterol);
            } else if (age >= 60) {
                costFpg = AgeCalculator.calculateServiceCost(age, fpg, 0);
                costCholesterol = AgeCalculator.calculateServiceCost(age, 0, cholesterol);
            }

            return cost13 + costFpg + costCholesterol;

        } catch (Exception e) {
            Log.e(TAG, "Error calculating service cost: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * ส่งข้อมูลไปยัง NHSO API
     */
    private void sendDataToAPI(PersonInfo personInfo, NHSOPatientInfo patient, NHSOHospitalInfo hospital,
                               List<NHSOPractitionerInfo> practitioners, NHSOOPDInfo nhsoopdInfo,
                               List<NHSODiagnosisInfo> nhsoDiagnosisInfos, List<NHSOCHAInfo> chaInfos,
                               List<NHSOCHADInfo> nhsochadInfos, UserSessionManager userSessionManager) {

        notifyProgress("ส่งข้อมูลไปยัง NHSO...");

        try {
            // สร้าง JSON
             JSONObject jsonObject = NHSOJsonConverter.createNHSORequestJson(
                    patient, hospital, practitioners, nhsoopdInfo,
                    nhsoDiagnosisInfos, chaInfos, nhsochadInfos
            );

            Log.d(TAG, "NHSO JSON: " + jsonObject.toString());

            if (jsonObject == null) {
                notifyError("ไม่สามารถสร้างข้อมูล JSON ได้");
                return;
            }

            // บันทึกข้อมูลเคลมในฐานข้อมูลท้องถิ่น
            NHSOClaimDataDao claimDataDao = new NHSOClaimDataDao(context);
            claimDataDao.saveClaimData(Integer.parseInt(personInfo.getVisitNo()), jsonObject);

            // ส่งข้อมูลไปยัง API
            NHSOFSDataApiCaller apiCaller = new NHSOFSDataApiCaller(context);
            apiCaller.sendFSData(jsonObject, new NHSOFSDataApiCaller.FSDataApiCallback() {
                @Override
                public void onSuccess(String response) {
                    handleAPISuccess(response, personInfo, claimDataDao, userSessionManager);
                }

                @Override
                public void onError(String errorMessage, Exception e) {
                    handleAPIError(errorMessage, e, personInfo, claimDataDao);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error sending data to API: " + e.getMessage());
            notifyError("เกิดข้อผิดพลาดในการส่งข้อมูล: " + e.getMessage());
        }
    }

    /**
     * จัดการผลลัพธ์เมื่อ API สำเร็จ
     */
    private void handleAPISuccess(String response, PersonInfo personInfo, NHSOClaimDataDao claimDataDao,
                                  UserSessionManager userSessionManager) {
        try {
            Log.d(TAG, "API Response: " + response);

            FSDataResponse[] fsResponses = new Gson().fromJson(response, FSDataResponse[].class);
            FSDataResponse fsResponse = fsResponses[0];

            if (fsResponse.isSuccess()) {
                // อัพเดทสถานะการส่งข้อมูลในฐานข้อมูล
                updateSyncStatus(personInfo.getVisitNo(), true);

                // อัพเดทข้อมูล claim ในตาราง ffc_sf_person_info
                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new java.util.Date());

                SfPersonInfoDao.updateClaimInfo(
                        personInfo.getId(),
                        fsResponse.getId(),
                        "",
                        "",
                        currentDateTime,
                        personInfo.getVisitNo()
                );

                claimDataDao.updateSuccessStatus(Integer.parseInt(personInfo.getVisitNo()),
                        fsResponse.getSeq(), response);

                Log.d(TAG, "Updated claim information for person ID: " + personInfo.getId());
                notifySuccess("ส่งข้อมูลสำเร็จ! seq no: " + fsResponse.getSeq(), fsResponse.getSeq());

            } else {
                notifyError("ส่งข้อมูลไม่สำเร็จ: " + fsResponse.getErrorSummary());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error processing API success response: " + e.getMessage());
            notifyError("ส่งข้อมูลสำเร็จ แต่ไม่สามารถประมวลผลการตอบกลับได้: " + e.getMessage());
        }
    }

    /**
     * จัดการผลลัพธ์เมื่อ API ผิดพลาด
     */
    private void handleAPIError(String errorMessage, Exception e, PersonInfo personInfo, NHSOClaimDataDao claimDataDao) {
        Log.e(TAG, "API Error: " + errorMessage + " " + (e != null ? e.getMessage() : ""));
        claimDataDao.updateFailedStatus(Integer.parseInt(personInfo.getVisitNo()), errorMessage);
        notifyError("เกิดข้อผิดพลาด: " + errorMessage);
    }

    /**
     * อัพเดทสถานะการ sync
     */
    private void updateSyncStatus(String visitNumber, boolean synced) {
        try {
            NHSOCHADao chaDao = new NHSOCHADao(context);
            List<NHSOCHAInfo> chaList = chaDao.getCHABySeq(visitNumber);

            for (NHSOCHAInfo cha : chaList) {
                chaDao.updateSyncStatus(cha.getId(), synced ? "1" : "0");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating sync status: " + e.getMessage());
        }
    }

    /**
     * แจ้งเตือนความสำเร็จ
     */
    private void notifySuccess(String message, String seqNo) {
        Log.d(TAG, "Success: " + message);
        if (listener != null) {
            listener.onClaimSubmissionSuccess(message, seqNo);
        }
    }

    /**
     * แจ้งเตือนข้อผิดพลาด
     */
    private void notifyError(String errorMessage) {
        Log.e(TAG, "Error: " + errorMessage);
        if (listener != null) {
            listener.onClaimSubmissionError(errorMessage);
        }
    }

    /**
     * แจ้งเตือนความคืบหนา้
     */
    private void notifyProgress(String message) {
        Log.d(TAG, "Progress: " + message);
        if (listener != null) {
            listener.onClaimSubmissionProgress(message);
        }
    }
}