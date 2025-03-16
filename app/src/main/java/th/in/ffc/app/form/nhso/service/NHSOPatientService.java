package th.in.ffc.app.form.nhso.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import th.in.ffc.app.form.nhso.dao.NHSOPatientDao;
import th.in.ffc.app.form.nhso.dao.NHSOPatientHistoryDao;
import th.in.ffc.app.form.nhso.dao.NHSOTokenDao;
import th.in.ffc.app.form.nhso.model.NHSOPatientHistoryInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.app.form.nhso.model.NHSOTokenInfo;

/**
 * Service สำหรับจัดการข้อมูลผู้ป่วย NHSO (แฟ้ม 1)
 */
public class NHSOPatientService {
    private static final String TAG = "NHSOPatientService";

    private Context mContext;
    private NHSOPatientDao patientDao;
    private NHSOPatientHistoryDao historyDao;
    private NHSOTokenDao tokenDao;
    private SimpleDateFormat dateFormat;
    private final Executor executor;

    // URL สำหรับเชื่อมต่อกับ NHSO API
    private static final String NHSO_API_BASE_URL = "https://api.nhso.go.th/api/v1";
    private static final String NHSO_PATIENT_ENDPOINT = "/patients";

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOPatientService(Context context) {
        this.mContext = context;
        this.patientDao = new NHSOPatientDao(context);
        this.historyDao = new NHSOPatientHistoryDao(context);
        this.tokenDao = new NHSOTokenDao(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * สร้างข้อมูลผู้ป่วยใหม่แบบซิงโครนัส
     * @param patient ข้อมูลผู้ป่วยที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ID ของข้อมูลผู้ป่วยที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long createPatient(NHSOPatientInfo patient, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validatePatientData(patient)) {
                Log.e(TAG, "Invalid patient data");
                return -1;
            }

            // สร้าง SEQ (Visit Number) ถ้ายังไม่มี
            if (patient.getSeq() == null || patient.getSeq().isEmpty()) {
                patient.setSeq(generateVisitNumber());
            }

            // เพิ่มข้อมูลผู้สร้างรายการ
            patient.setCreatedBy(username);
            patient.setCreatedDate(dateFormat.format(new Date()));

            // บันทึกลงฐานข้อมูล
            long patientId = patientDao.insertPatient(patient);

            if (patientId > 0) {
                Log.i(TAG, "Patient created successfully with ID: " + patientId);
            } else {
                Log.e(TAG, "Failed to create patient");
            }

            return patientId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating patient", e);
            return -1;
        }
    }

    /**
     * สร้างข้อมูลผู้ป่วยใหม่แบบอะซิงโครนัส
     * @param patient ข้อมูลผู้ป่วยที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void createPatientAsync(final NHSOPatientInfo patient, final String username, final ServiceCallback<Long> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long patientId = createPatient(patient, username);
                if (callback != null) {
                    if (patientId > 0) {
                        callback.onSuccess(patientId);
                    } else {
                        callback.onError("ไม่สามารถบันทึกข้อมูลผู้ป่วยได้");
                    }
                }
            }
        });
    }

    /**
     * อัปเดตข้อมูลผู้ป่วยแบบซิงโครนัส
     * @param patient ข้อมูลผู้ป่วยที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนรายการที่อัปเดต
     */
    public int updatePatient(NHSOPatientInfo patient, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validatePatientData(patient) || patient.getId() <= 0) {
                Log.e(TAG, "Invalid patient data or missing ID");
                return 0;
            }

            // เพิ่มข้อมูลผู้อัปเดตรายการ
            patient.setUpdatedBy(username);
            patient.setUpdatedDate(dateFormat.format(new Date()));

            // อัปเดตลงฐานข้อมูล
            int updatedRows = patientDao.updatePatient(patient);

            if (updatedRows > 0) {
                Log.i(TAG, "Patient updated successfully: " + patient.getId());
            } else {
                Log.e(TAG, "Failed to update patient: " + patient.getId());
            }

            return updatedRows;
        } catch (Exception e) {
            Log.e(TAG, "Error updating patient", e);
            return 0;
        }
    }

    /**
     * อัปเดตข้อมูลผู้ป่วยแบบอะซิงโครนัส
     * @param patient ข้อมูลผู้ป่วยที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void updatePatientAsync(final NHSOPatientInfo patient, final String username, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int updatedRows = updatePatient(patient, username);
                if (callback != null) {
                    if (updatedRows > 0) {
                        callback.onSuccess(updatedRows);
                    } else {
                        callback.onError("ไม่สามารถอัปเดตข้อมูลผู้ป่วยได้");
                    }
                }
            }
        });
    }

    /**
     * ส่งข้อมูลผู้ป่วยไปยัง NHSO API แบบซิงโครนัส
     * @param patientId ID ของผู้ป่วยที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ผลลัพธ์การส่งข้อมูล
     */
    public ServiceResult sendPatientToNHSO(long patientId, String username) {
        try {
            // ดึงข้อมูลผู้ป่วย
            NHSOPatientInfo patient = patientDao.getPatientById(patientId);
            if (patient == null) {
                return new ServiceResult(false, "ไม่พบข้อมูลผู้ป่วย");
            }

            // ดึง Token สำหรับเชื่อมต่อกับ NHSO API
            NHSOTokenInfo token = tokenDao.getLatestToken();
            if (token == null || !tokenDao.isTokenValid(token)) {
                return new ServiceResult(false, "ไม่มี Token หรือ Token หมดอายุ");
            }

            // สร้างประวัติการส่งข้อมูล
            NHSOPatientHistoryInfo history = new NHSOPatientHistoryInfo();
            history.setPatientId(patientId);
            history.setCid(patient.getCid());
            history.setCreatedBy(username);
            history.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));

            // ส่งข้อมูลไปยัง NHSO API (จำลองการส่งข้อมูล)
            // ในโค้ดจริง ควรใช้ OkHttp, Retrofit หรือ HttpURLConnection สำหรับส่งข้อมูล
            boolean isSuccess = simulateSendToNHSOAPI(patient, token);

            if (isSuccess) {
                // อัปเดตสถานะการส่งข้อมูล
                patient.setSentToNHSO(true);
                patient.setUpdatedBy(username);
                patient.setUpdatedDate(dateFormat.format(new Date()));
                patientDao.updateSentStatus(patientId, true);

                // บันทึกประวัติการส่งข้อมูลสำเร็จ
                history.setStatus(NHSOPatientHistoryInfo.STATUS_SUCCESS);
                history.setMessage("ส่งข้อมูลสำเร็จ");
                history.setResponseCode(200);
                history.setResponseBody("{\"success\": true, \"message\": \"Data sent successfully\"}");
                historyDao.insertHistory(history);

                return new ServiceResult(true, "ส่งข้อมูลสำเร็จ");
            } else {
                // บันทึกประวัติการส่งข้อมูลไม่สำเร็จ
                history.setStatus(NHSOPatientHistoryInfo.STATUS_FAILED);
                history.setMessage("ไม่สามารถส่งข้อมูลได้");
                history.setResponseCode(500);
                history.setResponseBody("{\"success\": false, \"message\": \"Failed to send data\"}");
                historyDao.insertHistory(history);

                return new ServiceResult(false, "ไม่สามารถส่งข้อมูลได้");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending patient data to NHSO", e);
            return new ServiceResult(false, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    /**
     * ส่งข้อมูลผู้ป่วยไปยัง NHSO API แบบอะซิงโครนัส
     * @param patientId ID ของผู้ป่วยที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void sendPatientToNHSOAsync(final long patientId, final String username, final ServiceCallback<ServiceResult> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServiceResult result = sendPatientToNHSO(patientId, username);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        });
    }

    /**
     * ลบข้อมูลผู้ป่วย
     * @param patientId ID ของผู้ป่วยที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deletePatient(long patientId) {
        try {
            // ลบประวัติการส่งข้อมูลของผู้ป่วย
            historyDao.deleteHistoryByPatientId(patientId);

            // ลบข้อมูลผู้ป่วย
            return patientDao.deletePatient(patientId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting patient", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลผู้ป่วยแบบอะซิงโครนัส
     * @param patientId ID ของผู้ป่วยที่ต้องการลบ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void deletePatientAsync(final long patientId, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int deletedRows = deletePatient(patientId);
                if (callback != null) {
                    if (deletedRows > 0) {
                        callback.onSuccess(deletedRows);
                    } else {
                        callback.onError("ไม่สามารถลบข้อมูลผู้ป่วยได้");
                    }
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ป่วยทั้งหมดแบบอะซิงโครนัส
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getAllPatientsAsync(final ServiceCallback<List<NHSOPatientInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPatientInfo> patients = patientDao.getAllPatients();
                if (callback != null) {
                    callback.onSuccess(patients);
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ป่วยตามเลขบัตรประชาชนแบบอะซิงโครนัส
     * @param cid เลขบัตรประชาชน
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getPatientByCidAsync(final String cid, final ServiceCallback<NHSOPatientInfo> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NHSOPatientInfo patient = patientDao.getPatientByCid(cid);
                if (callback != null) {
                    if (patient != null) {
                        callback.onSuccess(patient);
                    } else {
                        callback.onError("ไม่พบข้อมูลผู้ป่วย");
                    }
                }
            }
        });
    }

    /**
     * ดึงประวัติการส่งข้อมูลผู้ป่วยแบบอะซิงโครนัส
     * @param patientId ID ของผู้ป่วย
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getPatientHistoryAsync(final long patientId, final ServiceCallback<List<NHSOPatientHistoryInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPatientHistoryInfo> history = historyDao.getHistoryByPatientId(patientId);
                if (callback != null) {
                    callback.onSuccess(history);
                }
            }
        });
    }

    /**
     * สร้าง Visit Number ใหม่
     * @return Visit Number
     */
    private String generateVisitNumber() {
        // ใช้ UUID หรือวิธีอื่นๆ ในการสร้าง Visit Number
        return UUID.randomUUID().toString().substring(0, 16);
    }

    /**
     * ตรวจสอบข้อมูลผู้ป่วย
     * @param patient ข้อมูลผู้ป่วยที่ต้องการตรวจสอบ
     * @return true ถ้าข้อมูลถูกต้อง, false ถ้าข้อมูลไม่ถูกต้อง
     */
    private boolean validatePatientData(NHSOPatientInfo patient) {
        // ตรวจสอบข้อมูลที่จำเป็น
        return patient != null
                && patient.getType() != null && !patient.getType().isEmpty()
                && patient.getCid() != null && !patient.getCid().isEmpty()
                && patient.getNameGiven() != null && !patient.getNameGiven().isEmpty()
                && patient.getNameFamily() != null && !patient.getNameFamily().isEmpty();
    }

    /**
     * จำลองการส่งข้อมูลไปยัง NHSO API
     * @param patient ข้อมูลผู้ป่วย
     * @param token Token สำหรับเชื่อมต่อกับ NHSO API
     * @return ผลลัพธ์การส่งข้อมูล (true = สำเร็จ, false = ไม่สำเร็จ)
     */
    private boolean simulateSendToNHSOAPI(NHSOPatientInfo patient, NHSOTokenInfo token) {
        try {
            // จำลองการส่งข้อมูล (ใช้ในการทดสอบ)
            // ในโค้ดจริง ควรใช้ HTTP Client ในการส่งข้อมูล

            // จำลองความสำเร็จ 80% ของเวลา
            return Math.random() < 0.8;
        } catch (Exception e) {
            Log.e(TAG, "Error simulating send to NHSO API", e);
            return false;
        }
    }

    /**
     * Interface สำหรับ Callback
     * @param <T> ประเภทข้อมูลที่ส่งกลับ
     */
    public interface ServiceCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    /**
     * คลาสผลลัพธ์การทำงาน
     */
    public static class ServiceResult {
        private boolean success;
        private String message;
        private Object data;

        public ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public ServiceResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}