package th.in.ffc.app.form.screening.dao;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.screening.model.ClaimInfo;

public class ClaimInfoDao {
    private static final String TAG = "ClaimInfoDao";

    private Context mContext;
    private SfPersonInfoDao personInfoDao;
    private NHSOCHADao nhsochaDao;
    private SimpleDateFormat displayDateFormat;
    private SimpleDateFormat storageDateFormat;

    public ClaimInfoDao(Context context) {
        this.mContext = context;
        this.personInfoDao = new SfPersonInfoDao(context);
        this.nhsochaDao = new NHSOCHADao(context);

        // กำหนดรูปแบบวันที่สำหรับการแสดงผลและการจัดเก็บข้อมูล
        this.displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.storageDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    /**
     * ดึงข้อมูลรายการ claim ทั้งหมด
     * @return รายการข้อมูล claim ทั้งหมด
     */
    public List<ClaimInfo> getAllClaims() {
        List<ClaimInfo> claimList = new ArrayList<>();
        try {
            // ดึงข้อมูลผู้ป่วยที่มีการส่ง claim แล้ว (send_to_claim = 1)
            List<th.in.ffc.app.form.screening.model.PersonInfo> personList = personInfoDao.getSfPersonInfoAll();

            for (th.in.ffc.app.form.screening.model.PersonInfo person : personList) {
                // ตรวจสอบว่ามีการส่ง claim แล้วหรือไม่
                if (person.getSend_to_claim() != null && person.getSend_to_claim() == 1) {
                    ClaimInfo claim = new ClaimInfo();

                    // ข้อมูลบุคคล
                    claim.setId(person.getId());
                    claim.setPatientName(person.getFname() + " " + person.getLname());
                    claim.setIdCard(person.getIdcard());
                    claim.setPatientGroup("ทั่วไป"); // ค่าเริ่มต้น สามารถปรับให้ดึงจากข้อมูลจริงได้

                    // ข้อมูล claim
                    claim.setClaimId(person.getClaim_id());
                    claim.setClaimStatus(person.getClaim_status());
                    claim.setClaimMessage(person.getClaim_message());
                    claim.setClaimDate(person.getClaim_date());
                    claim.setVisitId(person.getVisitId());
                    claim.setSeq(person.getSeq());

                    // ข้อมูลวันที่รับบริการ
                    claim.setServiceDate(person.getAuthen_date());

                    // ดึงข้อมูลจำนวนเงินจาก NHSOCHADao
                    if (person.getVisitId() != null) {
                        double amount = nhsochaDao.getTotalAmountBySeq(person.getVisitId());
                        claim.setAmount(amount);
                    }

                    claimList.add(claim);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all claims", e);
        }

        return claimList;
    }

    /**
     * ค้นหารายการ claim ตามเงื่อนไข
     * @param patientName ชื่อหรือนามสกุลผู้ป่วย (ค้นหาบางส่วน)
     * @param fromDate วันที่เริ่มต้นการค้นหา
     * @param toDate วันที่สิ้นสุดการค้นหา
     * @param serviceType ประเภทบริการ
     * @param status สถานะรายการ
     * @return รายการข้อมูล claim ที่ตรงตามเงื่อนไข
     */
    public List<ClaimInfo> searchClaims(String patientName, String patientGroup, String fromDate,
                                        String toDate, String serviceType, String status) {
        List<ClaimInfo> results = new ArrayList<>();
        try {
            // ดึงข้อมูลทั้งหมดก่อน แล้วค่อยกรอง
            List<ClaimInfo> allClaims = getAllClaims();

            for (ClaimInfo claim : allClaims) {
                boolean matches = true;

                // กรองตามชื่อหรือนามสกุล
                if (patientName != null && !patientName.isEmpty()) {
                    if (claim.getPatientName() == null ||
                            !claim.getPatientName().toLowerCase().contains(patientName.toLowerCase())) {
                        matches = false;
                    }
                }

                // กรองตามกลุ่มผู้ป่วย
                if (patientGroup != null && !patientGroup.isEmpty() && !patientGroup.equals("ทั้งหมด")) {
                    if (claim.getPatientGroup() == null ||
                            !claim.getPatientGroup().equals(patientGroup)) {
                        matches = false;
                    }
                }

                // กรองตามวันที่
                if (fromDate != null && !fromDate.isEmpty()) {
                    try {
                        Date from = storageDateFormat.parse(fromDate);
                        Date claimDate = null;

                        if (claim.getClaimDate() != null && !claim.getClaimDate().isEmpty()) {
                            claimDate = storageDateFormat.parse(claim.getClaimDate());
                        }

                        if (claimDate == null || claimDate.before(from)) {
                            matches = false;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing fromDate", e);
                    }
                }

                if (toDate != null && !toDate.isEmpty()) {
                    try {
                        Date to = storageDateFormat.parse(toDate);
                        Date claimDate = null;

                        if (claim.getClaimDate() != null && !claim.getClaimDate().isEmpty()) {
                            claimDate = storageDateFormat.parse(claim.getClaimDate());
                        }

                        if (claimDate == null || claimDate.after(to)) {
                            matches = false;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing toDate", e);
                    }
                }

                // กรองตามประเภทบริการ
                if (serviceType != null && !serviceType.isEmpty() && !serviceType.equals("ทั้งหมด")) {
                    // เราต้องมีข้อมูลประเภทบริการในโมเดล ClaimInfo ด้วย
                    if (claim.getServiceType() == null ||
                            !claim.getServiceType().equals(serviceType)) {
                        matches = false;
                    }
                }

                // กรองตามสถานะรายการ
                if (status != null && !status.isEmpty() && !status.equals("ทั้งหมด")) {
                    if (claim.getClaimStatus() == null ||
                            !claim.getClaimStatus().equals(status)) {
                        matches = false;
                    }
                }

                if (matches) {
                    results.add(claim);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching claims", e);
        }

        return results;
    }

    /**
     * ดึงข้อมูล claim ตาม ID
     * @param id ID ของ claim
     * @return ข้อมูล claim หรือ null ถ้าไม่พบ
     */
    public ClaimInfo getClaimById(String id) {
        try {
            List<th.in.ffc.app.form.screening.model.PersonInfo> personList = personInfoDao.getSfPersonInfoById(Integer.valueOf(id));

            if (!personList.isEmpty()) {
                th.in.ffc.app.form.screening.model.PersonInfo person = personList.get(0);

                // ตรวจสอบว่ามีการส่ง claim แล้วหรือไม่
                if (person.getSend_to_claim() != null && person.getSend_to_claim() == 1) {
                    ClaimInfo claim = new ClaimInfo();

                    // ข้อมูลบุคคล
                    claim.setId(person.getId());
                    claim.setPatientName(person.getFname() + " " + person.getLname());
                    claim.setIdCard(person.getIdcard());
                    claim.setPatientGroup("ทั่วไป"); // ค่าเริ่มต้น สามารถปรับให้ดึงจากข้อมูลจริงได้

                    // ข้อมูล claim
                    claim.setClaimId(person.getClaim_id());
                    claim.setClaimStatus(person.getClaim_status());
                    claim.setClaimMessage(person.getClaim_message());
                    claim.setClaimDate(person.getClaim_date());
                    claim.setVisitId(person.getVisitId());
                    claim.setSeq(person.getSeq());

                    // ข้อมูลวันที่รับบริการ
                    claim.setServiceDate(person.getAuthen_date());

                    // ดึงข้อมูลจำนวนเงินจาก NHSOCHADao
                    if (person.getVisitId() != null) {
                        double amount = nhsochaDao.getTotalAmountBySeq(person.getVisitId());
                        claim.setAmount(amount);
                    }

                    return claim;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting claim by ID", e);
        }

        return null;
    }

    /**
     * ดึงรายการประเภทผู้ป่วยที่มีในระบบ
     * @return รายการประเภทผู้ป่วย
     */
    public List<String> getAvailablePatientGroups() {
        List<String> groups = new ArrayList<>();
        groups.add("ทั้งหมด");
        groups.add("ทั่วไป");
        groups.add("เบาหวาน");
        groups.add("ความดัน");
        // เพิ่มเติมกลุ่มอื่นๆ ตามความต้องการ
        return groups;
    }

    /**
     * ดึงรายการประเภทบริการที่มีในระบบ
     * @return รายการประเภทบริการ
     */
    public List<String> getAvailableServiceTypes() {
        List<String> types = new ArrayList<>();
        types.add("ทั้งหมด");
        types.add("การคัดกรองสุขภาพ");
        types.add("ตรวจรักษาโรค");
        types.add("ทันตกรรม");
        // เพิ่มเติมประเภทอื่นๆ ตามความต้องการ
        return types;
    }

    /**
     * ดึงรายการสถานะที่มีในระบบ
     * @return รายการสถานะ
     */
    public List<String> getAvailableStatuses() {
        List<String> statuses = new ArrayList<>();
        statuses.add("ทั้งหมด");
        statuses.add("รอการอนุมัติ");
        statuses.add("อนุมัติ");
        statuses.add("ไม่อนุมัติ");
        // เพิ่มเติมสถานะอื่นๆ ตามความต้องการ
        return statuses;
    }
}