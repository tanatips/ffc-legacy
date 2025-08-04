package th.in.ffc.app.form.screening;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.screening.dao.ClaimInfoDao;
import th.in.ffc.app.form.screening.model.ClaimInfo;

/**
 * กิจกรรมแสดงรายละเอียดของรายการเบิก (Claim)
 */
public class ClaimDetailActivity extends AppCompatActivity {

    private TextView tvDetailClaimId;
    private TextView tvDetailStatus;
    private TextView tvDetailPatientName;
    private TextView tvDetailIdCard;
    private TextView tvDetailPatientGroup;
    private TextView tvDetailServiceDate;
    private TextView tvDetailServiceType;
    private TextView tvDetailVisitId;
    private TextView tvDetailAmount;
    private TextView tvDetailClaimDate;
    private TextView tvDetailStatusMessage;
    private Button btnPrintReceipt;
    private Button btnBack;

    private ClaimInfoDao claimInfoDao;
    private SimpleDateFormat displayDateFormat;
    private DecimalFormat decimalFormat;

    private NHSOCHADao nhsochaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_detail);

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("รายละเอียดรายการเบิก");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        nhsochaDao = new NHSOCHADao(getApplicationContext());
        // Initialize date format
        displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        decimalFormat = new DecimalFormat("#,##0.00");

        // Initialize DAO
        claimInfoDao = new ClaimInfoDao(this);

        // Initialize views
        initViews();

        // Set up buttons
        setupButtons();

        // Load claim data
        String claimId = getIntent().getStringExtra("CLAIM_ID");
        if (claimId != null) {
            loadClaimData(claimId);
        } else {
            Toast.makeText(this, "ไม่พบข้อมูลรายการเบิก", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Initialize all views
     */
    private void initViews() {
        tvDetailClaimId = findViewById(R.id.tvDetailClaimId);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailPatientName = findViewById(R.id.tvDetailPatientName);
        tvDetailIdCard = findViewById(R.id.tvDetailIdCard);
        tvDetailPatientGroup = findViewById(R.id.tvDetailPatientGroup);
        tvDetailServiceDate = findViewById(R.id.tvDetailServiceDate);
        tvDetailServiceType = findViewById(R.id.tvDetailServiceType);
        tvDetailVisitId = findViewById(R.id.tvDetailVisitId);
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailClaimDate = findViewById(R.id.tvDetailClaimDate);
        tvDetailStatusMessage = findViewById(R.id.tvDetailStatusMessage);
        btnPrintReceipt = findViewById(R.id.btnPrintReceipt);
        btnBack = findViewById(R.id.btnBack);
    }

    /**
     * Set up button click listeners
     */
    private void setupButtons() {
        btnPrintReceipt.setOnClickListener(v -> {
            Toast.makeText(this, "กำลังพิมพ์ใบเสร็จ...", Toast.LENGTH_SHORT).show();
            // โค้ดสำหรับการพิมพ์ใบเสร็จจะถูกเพิ่มที่นี่
        });

        btnBack.setOnClickListener(v -> finish());
    }

    /**
     * โหลดข้อมูลรายการเบิก
     * @param claimId รหัสรายการเบิก
     */
    private void loadClaimData(String claimId) {
        ClaimInfo claim = claimInfoDao.getClaimById(claimId);

        if (claim == null) {
            Toast.makeText(this, "ไม่พบข้อมูลรายการเบิก", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // แสดงข้อมูลรายการเบิก
        tvDetailClaimId.setText("รหัสรายการเบิก: " + (claim.getClaimId() != null ? claim.getClaimId() : "-"));

        // กำหนดข้อความและสีตามสถานะ
        String status = claim.getClaimStatus() != null ? claim.getClaimStatus() : "รอดำเนินการ";
        tvDetailStatus.setText("สถานะ: " + status);

        int textColor;
        if ("อนุมัติ".equals(status)) {
            textColor = getResources().getColor(android.R.color.holo_green_dark);
        } else if ("ไม่อนุมัติ".equals(status)) {
            textColor = getResources().getColor(android.R.color.holo_red_dark);
        } else {
            textColor = getResources().getColor(android.R.color.holo_orange_dark);
        }
        tvDetailStatus.setTextColor(textColor);

        // ข้อมูลผู้ป่วย
        tvDetailPatientName.setText("ชื่อ-นามสกุล: " + claim.getPatientName());
        tvDetailIdCard.setText("เลขบัตรประชาชน: " + formatIdCard(claim.getIdCard()));
        tvDetailPatientGroup.setText("กลุ่มผู้ป่วย: " + claim.getPatientGroup());

        // ข้อมูลการรับบริการ
        String serviceDate = claim.getServiceDate() != null ? formatDate(claim.getServiceDate()) : "-";
        tvDetailServiceDate.setText("วันที่รับบริการ: " + serviceDate);

        String serviceType = claim.getServiceType() != null ? claim.getServiceType() : "การคัดกรองสุขภาพ";
        tvDetailServiceType.setText("ประเภทบริการ: " + serviceType);

        tvDetailVisitId.setText("รหัสการเข้ารับบริการ: " + (claim.getVisitNo() != null ? claim.getVisitNo() : "-"));

        Double total =  nhsochaDao.getTotalAmountBySeq(claim.getSeq());
        String amountText = decimalFormat.format(total) + " บาท";
        tvDetailAmount.setText("จำนวนเงิน: " + amountText);

        // ข้อมูลการเบิก
        String claimDate = claim.getClaimDate() != null ? formatDate(claim.getClaimDate()) : "-";
        tvDetailClaimDate.setText("วันที่ทำรายการ: " + claimDate);

        String statusMessage = claim.getClaimMessage() != null ? claim.getClaimMessage() :
                ("อนุมัติ".equals(status) ? "รายการเบิกได้รับการอนุมัติเรียบร้อยแล้ว" :
                        ("ไม่อนุมัติ".equals(status) ? "รายการเบิกไม่ได้รับการอนุมัติ" :
                                "รายการเบิกอยู่ระหว่างการดำเนินการ"));
        tvDetailStatusMessage.setText(statusMessage);
    }

    /**
     * จัดรูปแบบเลขบัตรประชาชน (X-XXXX-XXXXX-XX-X)
     */
    private String formatIdCard(String idcard) {
        if (idcard == null || idcard.length() != 13) {
            return idcard;
        }
        return idcard.substring(0, 1) + "-" +
                idcard.substring(1, 5) + "-" +
                idcard.substring(5, 10) + "-" +
                idcard.substring(10, 12) + "-" +
                idcard.substring(12);
    }

    /**
     * จัดรูปแบบวันที่
     */
    private String formatDate(String dateStr) {
        try {
            // จัดการกับรูปแบบวันที่ที่อาจมีหลายรูปแบบ
            SimpleDateFormat[] formats = {
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            };

            Date date = null;
            for (SimpleDateFormat format : formats) {
                try {
                    date = format.parse(dateStr);
                    if (date != null) break;
                } catch (Exception ignored) {}
            }

            if (date != null) {
                return displayDateFormat.format(date);
            }
        } catch (Exception e) {
            // กรณีมีข้อผิดพลาด ให้แสดงข้อมูลเดิม
        }

        return dateStr;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}