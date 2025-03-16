package th.in.ffc.app.form.nhso.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import th.in.ffc.app.form.nhso.dao.NHSOHospitalDao;
import th.in.ffc.app.form.nhso.dao.NHSOTokenDao;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;
import th.in.ffc.app.form.nhso.model.NHSOTokenInfo;

/**
 * Service สำหรับจัดการข้อมูลสถานพยาบาล NHSO (แฟ้ม 2)
 */
public class NHSOHospitalService {
    private static final String TAG = "NHSOHospitalService";

    private Context mContext;
    private NHSOHospitalDao hospitalDao;
    private NHSOTokenDao tokenDao;
    private SimpleDateFormat dateFormat;
    private final Executor executor;

    // URL สำหรับเชื่อมต่อกับ NHSO API
    private static final String NHSO_API_BASE_URL = "https://api.nhso.go.th/api/v1";
    private static final String NHSO_HOSPITAL_ENDPOINT = "/hospitals";

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOHospitalService(Context context) {
        this.mContext = context;
        this.hospitalDao = new NHSOHospitalDao(context);
        this.tokenDao = new NHSOTokenDao(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * สร้างข้อมูลสถานพยาบาลใหม่แบบซิงโครนัส
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ID ของข้อมูลสถานพยาบาลที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long createHospital(NHSOHospitalInfo hospital, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validateHospitalData(hospital)) {
                Log.e(TAG, "Invalid hospital data");
                return -1;
            }

            // สร้าง SEQ (Visit Number) ถ้ายังไม่มี
            if (hospital.getSeq() == null || hospital.getSeq().isEmpty()) {
                hospital.setSeq(generateVisitNumber());
            }

            // เพิ่มข้อมูลผู้สร้างรายการ
            hospital.setCreatedBy(username);
            hospital.setCreatedDate(dateFormat.format(new Date()));

            // บันทึกลงฐานข้อมูล
            long hospitalId = hospitalDao.insertHospital(hospital);

            if (hospitalId > 0) {
                Log.i(TAG, "Hospital created successfully with ID: " + hospitalId);
            } else {
                Log.e(TAG, "Failed to create hospital");
            }

            return hospitalId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating hospital", e);
            return -1;
        }
    }

    /**
     * สร้างข้อมูลสถานพยาบาลใหม่แบบอะซิงโครนัส
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void createHospitalAsync(final NHSOHospitalInfo hospital, final String username, final ServiceCallback<Long> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long hospitalId = createHospital(hospital, username);
                if (callback != null) {
                    if (hospitalId > 0) {
                        callback.onSuccess(hospitalId);
                    } else {
                        callback.onError("ไม่สามารถบันทึกข้อมูลสถานพยาบาลได้");
                    }
                }
            }
        });
    }

    /**
     * อัปเดตข้อมูลสถานพยาบาลแบบซิงโครนัส
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนรายการที่อัปเดต
     */
    public int updateHospital(NHSOHospitalInfo hospital, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validateHospitalData(hospital) || hospital.getId() <= 0) {
                Log.e(TAG, "Invalid hospital data or missing ID");
                return 0;
            }

            // เพิ่มข้อมูลผู้อัปเดตรายการ
            hospital.setUpdatedBy(username);
            hospital.setUpdatedDate(dateFormat.format(new Date()));

            // อัปเดตลงฐานข้อมูล
            int updatedRows = hospitalDao.updateHospital(hospital);

            if (updatedRows > 0) {
                Log.i(TAG, "Hospital updated successfully: " + hospital.getId());
            } else {
                Log.e(TAG, "Failed to update hospital: " + hospital.getId());
            }

            return updatedRows;
        } catch (Exception e) {
            Log.e(TAG, "Error updating hospital", e);
            return 0;
        }
    }

    /**
     * อัปเดตข้อมูลสถานพยาบาลแบบอะซิงโครนัส
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void updateHospitalAsync(final NHSOHospitalInfo hospital, final String username, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int updatedRows = updateHospital(hospital, username);
                if (callback != null) {
                    if (updatedRows > 0) {
                        callback.onSuccess(updatedRows);
                    } else {
                        callback.onError("ไม่สามารถอัปเดตข้อมูลสถานพยาบาลได้");
                    }
                }
            }
        });
    }

    /**
     * ส่งข้อมูลสถานพยาบาลไปยัง NHSO API แบบซิงโครนัส
     * @param hospitalId ID ของสถานพยาบาลที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ผลลัพธ์การส่งข้อมูล
     */
    public ServiceResult sendHospitalToNHSO(long hospitalId, String username) {
        try {
            // ดึงข้อมูลสถานพยาบาล
            NHSOHospitalInfo hospital = hospitalDao.getHospitalById(hospitalId);
            if (hospital == null) {
                return new ServiceResult(false, "ไม่พบข้อมูลสถานพยาบาล");
            }

            // ดึง Token สำหรับเชื่อมต่อกับ NHSO API
            NHSOTokenInfo token = tokenDao.getLatestToken();
            if (token == null || !tokenDao.isTokenValid(token)) {
                return new ServiceResult(false, "ไม่มี Token หรือ Token หมดอายุ");
            }

            // ส่งข้อมูลไปยัง NHSO API (จำลองการส่งข้อมูล)
            // ในโค้ดจริง ควรใช้ OkHttp, Retrofit หรือ HttpURLConnection สำหรับส่งข้อมูล
            boolean isSuccess = simulateSendToNHSOAPI(hospital, token);

            if (isSuccess) {
                // อัปเดตสถานะการส่งข้อมูล
                hospital.setSyncStatus(true);
                hospital.setUpdatedBy(username);
                hospital.setUpdatedDate(dateFormat.format(new Date()));
                hospitalDao.updateSyncStatus(hospitalId, true);

                return new ServiceResult(true, "ส่งข้อมูลสำเร็จ");
            } else {
                return new ServiceResult(false, "ไม่สามารถส่งข้อมูลได้");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending hospital data to NHSO", e);
            return new ServiceResult(false, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    /**
     * ส่งข้อมูลสถานพยาบาลไปยัง NHSO API แบบอะซิงโครนัส
     * @param hospitalId ID ของสถานพยาบาลที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void sendHospitalToNHSOAsync(final long hospitalId, final String username, final ServiceCallback<ServiceResult> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServiceResult result = sendHospitalToNHSO(hospitalId, username);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        });
    }

    /**
     * ลบข้อมูลสถานพยาบาล
     * @param hospitalId ID ของสถานพยาบาลที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deleteHospital(long hospitalId) {
        try {
            // ลบข้อมูลสถานพยาบาล
            return hospitalDao.deleteHospital(hospitalId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting hospital", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลสถานพยาบาลแบบอะซิงโครนัส
     * @param hospitalId ID ของสถานพยาบาลที่ต้องการลบ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void deleteHospitalAsync(final long hospitalId, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int deletedRows = deleteHospital(hospitalId);
                if (callback != null) {
                    if (deletedRows > 0) {
                        callback.onSuccess(deletedRows);
                    } else {
                        callback.onError("ไม่สามารถลบข้อมูลสถานพยาบาลได้");
                    }
                }
            }
        });
    }

    /**
     * ดึงข้อมูลสถานพยาบาลทั้งหมดแบบอะซิงโครนัส
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getAllHospitalsAsync(final ServiceCallback<List<NHSOHospitalInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOHospitalInfo> hospitals = hospitalDao.getAllHospitals();
                if (callback != null) {
                    callback.onSuccess(hospitals);
                }
            }
        });
    }

    /**
     * ดึงข้อมูลสถานพยาบาลตามรหัสแบบอะซิงโครนัส
     * @param hcode รหัสสถานพยาบาล
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getHospitalByHcodeAsync(final String hcode, final ServiceCallback<NHSOHospitalInfo> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NHSOHospitalInfo hospital = hospitalDao.getHospitalByHcode(hcode);
                if (callback != null) {
                    if (hospital != null) {
                        callback.onSuccess(hospital);
                    } else {
                        callback.onError("ไม่พบข้อมูลสถานพยาบาล");
                    }
                }
            }
        });
    }

    /**
     * ค้นหาสถานพยาบาลตามชื่อแบบอะซิงโครนัส
     * @param name ชื่อหรือส่วนหนึ่งของชื่อสถานพยาบาล
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void findHospitalsByNameAsync(final String name, final ServiceCallback<List<NHSOHospitalInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOHospitalInfo> hospitals = hospitalDao.findHospitalsByName(name);
                if (callback != null) {
                    callback.onSuccess(hospitals);
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
     * ตรวจสอบข้อมูลสถานพยาบาล
     * @param hospital ข้อมูลสถานพยาบาลที่ต้องการตรวจสอบ
     * @return true ถ้าข้อมูลถูกต้อง, false ถ้าข้อมูลไม่ถูกต้อง
     */
    private boolean validateHospitalData(NHSOHospitalInfo hospital) {
        // ตรวจสอบข้อมูลที่จำเป็น
        return hospital != null
                && hospital.getHcode() != null && !hospital.getHcode().isEmpty()
                    && hospital.getSeq() != null && !hospital.getSeq().isEmpty();
//                && hospital.getHcodeName() != null && !hospital.getHcodeName().isEmpty();
    }

    /**
     * จำลองการส่งข้อมูลไปยัง NHSO API
     * @param hospital ข้อมูลสถานพยาบาล
     * @param token Token สำหรับเชื่อมต่อกับ NHSO API
     * @return ผลลัพธ์การส่งข้อมูล (true = สำเร็จ, false = ไม่สำเร็จ)
     */
    private boolean simulateSendToNHSOAPI(NHSOHospitalInfo hospital, NHSOTokenInfo token) {
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