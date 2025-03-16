package th.in.ffc.app.form.nhso.service;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import th.in.ffc.app.form.nhso.dao.NHSOPractitionerDao;
import th.in.ffc.app.form.nhso.dao.NHSOTokenDao;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;
import th.in.ffc.app.form.nhso.model.NHSOTokenInfo;

/**
 * Service สำหรับจัดการข้อมูลผู้ให้บริการ NHSO (แฟ้ม 3)
 */
public class NHSOPractitionerService {
    private static final String TAG = "NHSOPractitionerService";

    private Context mContext;
    private NHSOPractitionerDao practitionerDao;
    private NHSOTokenDao tokenDao;
    private SimpleDateFormat dateFormat;
    private final Executor executor;

    // URL สำหรับเชื่อมต่อกับ NHSO API
    private static final String NHSO_API_BASE_URL = "https://api.nhso.go.th/api/v1";
    private static final String NHSO_PRACTITIONER_ENDPOINT = "/practitioners";

    /**
     * คอนสตรักเตอร์
     * @param context Context ของแอปพลิเคชัน
     */
    public NHSOPractitionerService(Context context) {
        this.mContext = context;
        this.practitionerDao = new NHSOPractitionerDao(context);
        this.tokenDao = new NHSOTokenDao(context);
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * สร้างข้อมูลผู้ให้บริการใหม่แบบซิงโครนัส
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ID ของข้อมูลผู้ให้บริการที่บันทึก หรือ -1 ถ้าบันทึกไม่สำเร็จ
     */
    public long createPractitioner(NHSOPractitionerInfo practitioner, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validatePractitionerData(practitioner)) {
                Log.e(TAG, "Invalid practitioner data");
                return -1;
            }

            // สร้าง SEQ (Visit Number) ถ้ายังไม่มี
            if (practitioner.getSeq() == null || practitioner.getSeq().isEmpty()) {
                practitioner.setSeq(generateVisitNumber());
            }

            // เพิ่มข้อมูลผู้สร้างรายการ
            practitioner.setCreatedBy(username);
            practitioner.setCreatedDate(dateFormat.format(new Date()));

            // บันทึกลงฐานข้อมูล
            long practitionerId = practitionerDao.insertPractitioner(practitioner);

            if (practitionerId > 0) {
                Log.i(TAG, "Practitioner created successfully with ID: " + practitionerId);
            } else {
                Log.e(TAG, "Failed to create practitioner");
            }

            return practitionerId;
        } catch (Exception e) {
            Log.e(TAG, "Error creating practitioner", e);
            return -1;
        }
    }

    /**
     * สร้างข้อมูลผู้ให้บริการใหม่แบบอะซิงโครนัส
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการบันทึก
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void createPractitionerAsync(final NHSOPractitionerInfo practitioner, final String username, final ServiceCallback<Long> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long practitionerId = createPractitioner(practitioner, username);
                if (callback != null) {
                    if (practitionerId > 0) {
                        callback.onSuccess(practitionerId);
                    } else {
                        callback.onError("ไม่สามารถบันทึกข้อมูลผู้ให้บริการได้");
                    }
                }
            }
        });
    }

    /**
     * อัปเดตข้อมูลผู้ให้บริการแบบซิงโครนัส
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return จำนวนรายการที่อัปเดต
     */
    public int updatePractitioner(NHSOPractitionerInfo practitioner, String username) {
        try {
            // ตรวจสอบข้อมูลที่จำเป็น
            if (!validatePractitionerData(practitioner) || practitioner.getId() <= 0) {
                Log.e(TAG, "Invalid practitioner data or missing ID");
                return 0;
            }

            // เพิ่มข้อมูลผู้อัปเดตรายการ
            practitioner.setUpdatedBy(username);
            practitioner.setUpdatedDate(dateFormat.format(new Date()));

            // อัปเดตลงฐานข้อมูล
            int updatedRows = practitionerDao.updatePractitioner(practitioner);

            if (updatedRows > 0) {
                Log.i(TAG, "Practitioner updated successfully: " + practitioner.getId());
            } else {
                Log.e(TAG, "Failed to update practitioner: " + practitioner.getId());
            }

            return updatedRows;
        } catch (Exception e) {
            Log.e(TAG, "Error updating practitioner", e);
            return 0;
        }
    }

    /**
     * อัปเดตข้อมูลผู้ให้บริการแบบอะซิงโครนัส
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการอัปเดต
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void updatePractitionerAsync(final NHSOPractitionerInfo practitioner, final String username, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int updatedRows = updatePractitioner(practitioner, username);
                if (callback != null) {
                    if (updatedRows > 0) {
                        callback.onSuccess(updatedRows);
                    } else {
                        callback.onError("ไม่สามารถอัปเดตข้อมูลผู้ให้บริการได้");
                    }
                }
            }
        });
    }

    /**
     * ส่งข้อมูลผู้ให้บริการไปยัง NHSO API แบบซิงโครนัส
     * @param practitionerId ID ของผู้ให้บริการที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @return ผลลัพธ์การส่งข้อมูล
     */
    public ServiceResult sendPractitionerToNHSO(long practitionerId, String username) {
        try {
            // ดึงข้อมูลผู้ให้บริการ
            NHSOPractitionerInfo practitioner = practitionerDao.getPractitionerById(practitionerId);
            if (practitioner == null) {
                return new ServiceResult(false, "ไม่พบข้อมูลผู้ให้บริการ");
            }

            // ดึง Token สำหรับเชื่อมต่อกับ NHSO API
            NHSOTokenInfo token = tokenDao.getLatestToken();
            if (token == null || !tokenDao.isTokenValid(token)) {
                return new ServiceResult(false, "ไม่มี Token หรือ Token หมดอายุ");
            }

            // ส่งข้อมูลไปยัง NHSO API (จำลองการส่งข้อมูล)
            // ในโค้ดจริง ควรใช้ OkHttp, Retrofit หรือ HttpURLConnection สำหรับส่งข้อมูล
            boolean isSuccess = simulateSendToNHSOAPI(practitioner, token);

            if (isSuccess) {
                // อัปเดตสถานะการส่งข้อมูล
                practitioner.setSyncStatus(true);
                practitioner.setUpdatedBy(username);
                practitioner.setUpdatedDate(dateFormat.format(new Date()));
                practitionerDao.updateSyncStatus(practitionerId, true);

                return new ServiceResult(true, "ส่งข้อมูลสำเร็จ");
            } else {
                return new ServiceResult(false, "ไม่สามารถส่งข้อมูลได้");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error sending practitioner data to NHSO", e);
            return new ServiceResult(false, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    /**
     * ส่งข้อมูลผู้ให้บริการไปยัง NHSO API แบบอะซิงโครนัส
     * @param practitionerId ID ของผู้ให้บริการที่ต้องการส่งข้อมูล
     * @param username ชื่อผู้ใช้ที่ทำรายการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void sendPractitionerToNHSOAsync(final long practitionerId, final String username, final ServiceCallback<ServiceResult> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ServiceResult result = sendPractitionerToNHSO(practitionerId, username);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
        });
    }

    /**
     * ลบข้อมูลผู้ให้บริการ
     * @param practitionerId ID ของผู้ให้บริการที่ต้องการลบ
     * @return จำนวนรายการที่ลบ
     */
    public int deletePractitioner(long practitionerId) {
        try {
            // ลบข้อมูลผู้ให้บริการ
            return practitionerDao.deletePractitioner(practitionerId);
        } catch (Exception e) {
            Log.e(TAG, "Error deleting practitioner", e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลผู้ให้บริการแบบอะซิงโครนัส
     * @param practitionerId ID ของผู้ให้บริการที่ต้องการลบ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void deletePractitionerAsync(final long practitionerId, final ServiceCallback<Integer> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int deletedRows = deletePractitioner(practitionerId);
                if (callback != null) {
                    if (deletedRows > 0) {
                        callback.onSuccess(deletedRows);
                    } else {
                        callback.onError("ไม่สามารถลบข้อมูลผู้ให้บริการได้");
                    }
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ให้บริการทั้งหมดแบบอะซิงโครนัส
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getAllPractitionersAsync(final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.getAllPractitioners();
                if (callback != null) {
                    callback.onSuccess(practitioners);
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ให้บริการตามเลขบัตรประชาชนแบบอะซิงโครนัส
     * @param cid เลขบัตรประชาชน
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getPractitionerByCidAsync(final String cid, final ServiceCallback<NHSOPractitionerInfo> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                NHSOPractitionerInfo practitioner = practitionerDao.getPractitionerByCid(cid);
                if (callback != null) {
                    if (practitioner != null) {
                        callback.onSuccess(practitioner);
                    } else {
                        callback.onError("ไม่พบข้อมูลผู้ให้บริการ");
                    }
                }
            }
        });
    }

    /**
     * ค้นหาผู้ให้บริการตามชื่อแบบอะซิงโครนัส
     * @param name ชื่อหรือส่วนหนึ่งของชื่อผู้ให้บริการ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void findPractitionersByNameAsync(final String name, final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.findPractitionersByName(name);
                if (callback != null) {
                    callback.onSuccess(practitioners);
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ให้บริการตามรหัสสถานพยาบาลแบบอะซิงโครนัส
     * @param hcode รหัสสถานพยาบาล
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getPractitionersByHcodeAsync(final String hcode, final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.getPractitionersByHcode(hcode);
                if (callback != null) {
                    callback.onSuccess(practitioners);
                }
            }
        });
    }

    /**
     * ค้นหาผู้ให้บริการตามสภาวิชาชีพแบบอะซิงโครนัส
     * @param council รหัสสภาวิชาชีพ
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void findPractitionersByCouncilAsync(final String council, final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.findPractitionersByCouncil(council);
                if (callback != null) {
                    callback.onSuccess(practitioners);
                }
            }
        });
    }

    /**
     * ค้นหาผู้ให้บริการตามประเภทบุคลากรแบบอะซิงโครนัส
     * @param providerType รหัสประเภทบุคลากร
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void findPractitionersByTypeAsync(final String providerType, final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.findPractitionersByType(providerType);
                if (callback != null) {
                    callback.onSuccess(practitioners);
                }
            }
        });
    }

    /**
     * ดึงข้อมูลผู้ให้บริการที่ยังไม่ได้ซิงค์แบบอะซิงโครนัส
     * @param callback Callback เมื่อทำงานเสร็จ
     */
    public void getUnsyncedPractitionersAsync(final ServiceCallback<List<NHSOPractitionerInfo>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<NHSOPractitionerInfo> practitioners = practitionerDao.getUnsyncedPractitioners();
                if (callback != null) {
                    callback.onSuccess(practitioners);
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
     * ตรวจสอบข้อมูลผู้ให้บริการ
     * @param practitioner ข้อมูลผู้ให้บริการที่ต้องการตรวจสอบ
     * @return true ถ้าข้อมูลถูกต้อง, false ถ้าข้อมูลไม่ถูกต้อง
     */
    private boolean validatePractitionerData(NHSOPractitionerInfo practitioner) {
        // ตรวจสอบข้อมูลที่จำเป็น
        return practitioner != null
                && practitioner.getSeq() != null && !practitioner.getSeq().isEmpty()
                && practitioner.getHcode() != null && !practitioner.getHcode().isEmpty()
                && practitioner.getCid() != null && !practitioner.getCid().isEmpty();
    }

    /**
     * จำลองการส่งข้อมูลไปยัง NHSO API
     * @param practitioner ข้อมูลผู้ให้บริการ
     * @param token Token สำหรับเชื่อมต่อกับ NHSO API
     * @return ผลลัพธ์การส่งข้อมูล (true = สำเร็จ, false = ไม่สำเร็จ)
     */
    private boolean simulateSendToNHSOAPI(NHSOPractitionerInfo practitioner, NHSOTokenInfo token) {
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