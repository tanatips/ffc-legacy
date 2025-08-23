package th.in.ffc.app.form.screening;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.PersonData;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.view.BloodSugarGaugeView;
import th.in.ffc.app.form.screening.view.DiabetesRiskGaugeView;
import th.in.ffc.util.Log;

public class HealthRiskAssessmentFragment extends Fragment {

    SharedViewModel shareViewModel;
    HealthRiskAssessmentLiveData healthRiskAssessmentLiveData;
    private OnDataPass dataPasser;
    private HealthRiskAssessmentInfo healthRiskAssessmentInfo;
    private EditText editFcbg;
    private EditText editFpg;

    // เพิ่มตัวแปรสำหรับแสดงผลแบบใหม่
    private TextView tvHealthRiskScore;
    private TextView tvHealthRiskLevel;

    // ตัวแปรป้องกัน infinite loop
    private boolean isAutoSelecting = false;
    private boolean isLoadingExistingData = false;
    private boolean isUpdatingFromTextWatcher = false;

    // เพิ่มตัวแปรสำหรับ RadioGroups
    private RadioGroup rdoHealthRiskQ1;
    private RadioGroup rdoHealthRiskQ2;
    private RadioGroup rdoHealthRiskQ3;
    private RadioGroup rdoHealthRiskQ4;
    private RadioGroup rdoHealthRiskQ5;
    private RadioGroup rdoHealthRiskQ6;

    // เพิ่มตัวแปรสำหรับติดตามการเลือกของผู้ใช้
    private boolean userHasSelectedQ1 = false;
    private boolean userHasSelectedQ2 = false;
    private boolean userHasSelectedQ3 = false;
    private boolean userHasSelectedQ4 = false;
    private boolean userHasSelectedQ5 = false;
    private boolean userHasSelectedQ6 = false;
    private boolean isRestoringData = false;

    private boolean isUpdating = false;
    private boolean hasAutoSelectedOnce = false;

    private boolean fisrtTime = true;
    private DiabetesRiskGaugeView diabetesRiskGauge;
    private TextView tvGaugeEmoji;
    private TextView tvGaugeScore;
    private TextView tvGaugeLevel;
    private TextView tvGaugeCode;
    private TextView tvGaugeRecommendation;
    private ImageView ivDiabetesInfoButton;
    private SeekBar seekBarGaugeTest;

    private BloodSugarGaugeView bloodSugarGauge;
    private TextView tvBloodSugarEmoji;
    private TextView tvBloodSugarLevel;
    private TextView tvBloodSugarValue;
    private TextView tvBloodSugarUnit;
    private TextView tvBloodSugarRecommendation;
    private ImageView ivBloodSugarInfoButton;

    public HealthRiskAssessmentFragment() {
        // Required empty public constructor
    }

    public static HealthRiskAssessmentFragment newInstance(String param1, String param2) {
        HealthRiskAssessmentFragment fragment = new HealthRiskAssessmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class); // เปลี่ยนจาก this
//
//        // แก้ไข: ตรวจสอบและสร้างให้แน่นอน
//        healthRiskAssessmentLiveData = shareViewModel.getHealthRiskAssessmentLiveDataMutableLiveData().getValue();
//        if (healthRiskAssessmentLiveData == null) {
//            healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
//            shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
//        }

        // แก้ไข: ตรวจสอบว่ามี cardiovascularRiskLiveData อยู่แล้วหรือไม่
        if (shareViewModel.getHealthRiskAssessmentLiveDataMutableLiveData() != null &&
                shareViewModel.getHealthRiskAssessmentLiveDataMutableLiveData().getValue() != null) {
            // ใช้ข้อมูลที่มีอยู่แล้ว
            healthRiskAssessmentLiveData = shareViewModel.getHealthRiskAssessmentLiveDataMutableLiveData().getValue();
            android.util.Log.d("healthRiskAssessmentLiveData", "Using existing LiveData with personId: " +
                    (healthRiskAssessmentLiveData.getPersonId() != null ? healthRiskAssessmentLiveData.getPersonId() : "null"));
        } else {
            // สร้างใหม่เฉพาะเมื่อไม่มีข้อมูล
            healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
            shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
            android.util.Log.d("CardiovascularRiskFragment", "Created new LiveData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_risk_assessment, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();

        if (healthRiskAssessmentLiveData == null) {
            healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
            if (shareViewModel != null) {
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
            }
        }
        // Initialize UI components

        setupRadioGroups(view);
        editFcbg = view.findViewById(R.id.edtFCBG);
        editFpg = view.findViewById(R.id.edtFPG);

        // เชื่อมโยง TextView สำหรับแสดงผลแบบใหม่
        tvHealthRiskScore = view.findViewById(R.id.tvHealthRiskScore);
        tvHealthRiskLevel = view.findViewById(R.id.tvHealthRiskLevel);
        loadData();
        initializeGaugeViews(view);
        initializeBloodSugarGaugeViews(view);
        // Setup listeners and observers

        setupGlucoseInputListeners();
        setupFocusListeners();
        setupPersonDataObserver();

        // Check for bundle arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            setPersonDataFromOtherScreens(arguments);
        }
    }
    private void initializeGaugeViews(View view) {
        diabetesRiskGauge = view.findViewById(R.id.diabetesRiskGauge);
        tvGaugeEmoji = view.findViewById(R.id.tvGaugeEmoji);
        tvGaugeScore = view.findViewById(R.id.tvGaugeScore);
        tvGaugeLevel = view.findViewById(R.id.tvGaugeLevel);
        tvGaugeCode = view.findViewById(R.id.tvGaugeCode);
        tvGaugeRecommendation = view.findViewById(R.id.tvGaugeRecommendation);
        ivDiabetesInfoButton = view.findViewById(R.id.ivDiabetesInfoButton);

        // สำหรับทดสอบ (สามารถลบออกได้)
        seekBarGaugeTest = view.findViewById(R.id.seekBarGaugeTest);
        setupGaugeTestControls();

        // ตั้งค่าปุ่ม info
        setupInfoButtonListener();

        // อัปเดต Gauge ครั้งแรก
        updateGaugeDisplay();
    }
    private void setupGaugeTestControls() {
        if (seekBarGaugeTest != null) {
            seekBarGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && diabetesRiskGauge != null) {
                        diabetesRiskGauge.setScore(progress);
                        DiabetesRiskGaugeView.RiskLevel level = getCurrentRiskLevelFromScore(progress);

                        // อัปเดตข้อความทดสอบ
                        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(level.emoji);
                        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + progress);
                        if (tvGaugeLevel != null) {
                            tvGaugeLevel.setText(level.label);
                            tvGaugeLevel.setTextColor(Color.parseColor(level.color));
                        }
                        if (tvGaugeCode != null) tvGaugeCode.setText(level.code);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }
    private void initializeBloodSugarGaugeViews(View view) {
        bloodSugarGauge = view.findViewById(R.id.bloodSugarGauge);
        tvBloodSugarEmoji = view.findViewById(R.id.tvBloodSugarEmoji);
        tvBloodSugarLevel = view.findViewById(R.id.tvBloodSugarLevel);
        tvBloodSugarValue = view.findViewById(R.id.tvBloodSugarValue);
        tvBloodSugarUnit = view.findViewById(R.id.tvBloodSugarUnit);
        tvBloodSugarRecommendation = view.findViewById(R.id.tvBloodSugarRecommendation);
        ivBloodSugarInfoButton = view.findViewById(R.id.ivBloodSugarInfoButton);

        // ตั้งค่าปุ่ม info
        setupBloodSugarInfoButtonListener();

        // อัปเดต Gauge ครั้งแรก
        updateBloodSugarGaugeDisplay();
    }

    private void setupBloodSugarInfoButtonListener() {
        if (ivBloodSugarInfoButton != null) {
            ivBloodSugarInfoButton.setOnClickListener(v -> showBloodSugarCriteriaDialog());
        }
    }

    private void showBloodSugarCriteriaDialog() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_blood_sugar_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("BloodSugarGauge", "แสดง Dialog เกณฑ์ระดับน้ำตาลในเลือดสำเร็จ");

        } catch (Exception e) {
            Log.e("BloodSugarGauge", "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());
            showSimpleBloodSugarCriteriaDialog();
        }
    }
    private void showSimpleBloodSugarCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "🩸 เกณฑ์ระดับน้ำตาลในเลือด (มก/ดล)\n\n" +
                "😊 น้อยกว่า 100: ปกติ\n" +
                "🔶 รักษาสุขภาพให้ดีต่อไป\n\n" +

                "😐 100-125: เสี่ยงต่อการเป็นโรคเบาหวาน\n" +
                "🔶 ควบคุมอาหาร ออกกำลังกาย\n" +
                "🔶 ตรวจติดตามเป็นประจำ\n\n" +

                "😟 ตั้งแต่ 126 ขึ้นไป: เป็นโรคเบาหวาน\n" +
                "🔶 ควรพบแพทย์เพื่อรับการรักษา\n" +
                "🔶 ควบคุมระดับน้ำตาลอย่างเข้มงวด\n\n";

        builder.setTitle("📊 เกณฑ์ระดับน้ำตาลในเลือด")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateBloodSugarGaugeDisplay() {
        if (bloodSugarGauge == null) return;

        // ดึงค่าน้ำตาลจาก FPG (ให้ความสำคัญกับ FPG ก่อน)
        double glucoseValue = getCurrentGlucoseValue();

        if (glucoseValue > 0) {
            // อัปเดต Gauge
            bloodSugarGauge.setGlucoseLevel(glucoseValue);
            BloodSugarGaugeView.BloodSugarLevel currentLevel = bloodSugarGauge.getCurrentBloodSugarLevel();

            // อัปเดตข้อความ
            if (tvBloodSugarEmoji != null) tvBloodSugarEmoji.setText(currentLevel.emoji);
            if (tvBloodSugarValue != null) tvBloodSugarValue.setText(String.format("%.0f", glucoseValue));
            if (tvBloodSugarUnit != null) tvBloodSugarUnit.setText("มก/ดล");
            if (tvBloodSugarLevel != null) {
                tvBloodSugarLevel.setText(currentLevel.label);
                tvBloodSugarLevel.setTextColor(Color.parseColor(currentLevel.color));
            }
            if (tvBloodSugarRecommendation != null) {
                tvBloodSugarRecommendation.setText(bloodSugarGauge.getRecommendation());
//                tvBloodSugarRecommendation.setVisibility(View.VISIBLE);
            }

            Log.d("BloodSugarGauge", "Updated - Glucose: " + glucoseValue + ", Level: " + currentLevel.label);
        } else {
            // รีเซ็ตเมื่อไม่มีค่า
            resetBloodSugarGauge();
        }
    }
    private double getCurrentGlucoseValue() {
        double glucoseValue = 0.0;

        // ลองดึงค่าจาก FPG ก่อน (ให้ความสำคัญกับ FPG)
        if (editFpg != null && !editFpg.getText().toString().trim().isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(editFpg.getText().toString().trim());
            } catch (NumberFormatException e) {
                // ไม่สามารถแปลงได้
            }
        }

        // ถ้าไม่มี FPG ให้ลองดึงจาก FCBG
        if (glucoseValue == 0.0 && editFcbg != null && !editFcbg.getText().toString().trim().isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(editFcbg.getText().toString().trim());
            } catch (NumberFormatException e) {
                // ไม่สามารถแปลงได้
            }
        }

        return glucoseValue;
    }

    public void resetBloodSugarGauge() {
        if (bloodSugarGauge != null) {
            bloodSugarGauge.resetGauge();

            if (tvBloodSugarEmoji != null) tvBloodSugarEmoji.setText("❓");
            if (tvBloodSugarValue != null) tvBloodSugarValue.setText("-");
            if (tvBloodSugarUnit != null) tvBloodSugarUnit.setText("มก/ดล");
            if (tvBloodSugarLevel != null) {
                tvBloodSugarLevel.setText("ยังไม่ได้ตรวจ");
                tvBloodSugarLevel.setTextColor(getResources().getColor(R.color.text_secondary));
            }
            if (tvBloodSugarRecommendation != null) {
                tvBloodSugarRecommendation.setVisibility(View.GONE);
            }
        }
    }


    private void setupInfoButtonListener() {
        if (ivDiabetesInfoButton != null) {
            ivDiabetesInfoButton.setOnClickListener(v -> showDiabetesCriteriaDialog());
        }
    }
    private void showDiabetesCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_diabetes_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("HealthRiskAssessment", "แสดง Dialog เกณฑ์การประเมินเบาหวานสำเร็จ");

        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());
            showSimpleDiabetesCriteriaDialog();
        }
    }
    private void showSimpleDiabetesCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "🩺 เกณฑ์การประเมินความเสี่ยงโรคเบาหวาน\n\n" +
                "😊 ≤ 2 คะแนน: เสี่ยงน้อย (< 5%)\n" +
                "🔶 ตรวจสุขภาพประจำปี\n\n" +

                "😐 3-5 คะแนน: เสี่ยงปานกลาง (5-10%)\n" +
                "🔶 ปรับพฤติกรรม ตรวจน้ำตาลทุก 1-3 ปี\n\n" +

                "😟 6-8 คะแนน: เสี่ยงสูง (11-20%)\n" +
                "🔶 ตรวจน้ำตาลด่วน ปรับพฤติกรรมจริงจัง\n\n" +

                "😰 > 8 คะแนน: เสี่ยงสูงมาก (> 20%)\n" +
                "🔶 พบแพทย์ด่วน ตรวจวินิจฉัยและรักษา\n\n";

        builder.setTitle("📊 เกณฑ์การประเมินเบาหวาน")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private DiabetesRiskGaugeView.RiskLevel getCurrentRiskLevelFromScore(int score) {
        if (score >= 0 && score <= 2) {
            return new DiabetesRiskGaugeView.RiskLevel(0, 2, "เสี่ยงน้อย (< 5%)", "#27AE60", "😊", "DB01");
        } else if (score >= 3 && score <= 5) {
            return new DiabetesRiskGaugeView.RiskLevel(3, 5, "เสี่ยงปานกลาง (5-10%)", "#F39C12", "😐", "DB02");
        } else if (score >= 6 && score <= 8) {
            return new DiabetesRiskGaugeView.RiskLevel(6, 8, "เสี่ยงสูง (11-20%)", "#FF9800", "😟", "DB03");
        } else {
            return new DiabetesRiskGaugeView.RiskLevel(9, 16, "เสี่ยงสูงมาก (> 20%)", "#E74C3C", "😰", "DB04");
        }
    }
    private void updateGaugeDisplay() {
        if (diabetesRiskGauge == null) return;

        int totalScore = calculateTotalScore();
        DiabetesRiskGaugeView.RiskLevel currentLevel = getCurrentRiskLevelFromScore(totalScore);

        // อัปเดต Gauge
        diabetesRiskGauge.setScore(totalScore);
        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + totalScore);
        // อัปเดตข้อความ
        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(currentLevel.emoji);
        if (tvGaugeLevel != null) {
            tvGaugeLevel.setText(currentLevel.label);
            tvGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvGaugeCode != null) tvGaugeCode.setText(currentLevel.code);

        Log.d("HealthRiskAssessment", "Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label);
    }
    public void updateGaugeWithScore(int score) {
        if (diabetesRiskGauge != null) {
            diabetesRiskGauge.setScore(score);
            updateGaugeDisplay();
        }
    }
    public void resetGauge() {
        if (diabetesRiskGauge != null) {
            diabetesRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }
    public void showGaugeTestControls(boolean show) {
        View layoutGaugeControl = getView().findViewById(R.id.layoutGaugeControl);
        if (layoutGaugeControl != null) {
            layoutGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    public DiabetesRiskGaugeView.RiskLevel getCurrentGaugeLevel() {
        if (diabetesRiskGauge != null) {
            return diabetesRiskGauge.getCurrentRiskLevel();
        }
        return getCurrentRiskLevelFromScore(0);
    }
    private void setupRadioGroups(View view) {
        rdoHealthRiskQ1 = view.findViewById(R.id.rdoHealthRiskQ1);
        rdoHealthRiskQ2 = view.findViewById(R.id.rdoHealthRiskQ2);
        rdoHealthRiskQ3 = view.findViewById(R.id.rdoHealthRiskQ3);
        rdoHealthRiskQ4 = view.findViewById(R.id.rdoHealthRiskQ4);
        rdoHealthRiskQ5 = view.findViewById(R.id.rdoHealthRiskQ5);
        rdoHealthRiskQ6 = view.findViewById(R.id.rdoHealthRiskQ6);

        // Q1 - Age
        rdoHealthRiskQ1.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ1_");
            if (!data.isEmpty()) {
                userHasSelectedQ1 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ1(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ1(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q1 selected by user: " + data);
            }
        });

        // Q2 - Gender
        rdoHealthRiskQ2.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ2_");
            if (!data.isEmpty()) {
                userHasSelectedQ2 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ2(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ2(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q2 selected by user: " + data);
            }
        });

        // Q3 - BMI
        rdoHealthRiskQ3.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ3_");
            if (!data.isEmpty()) {
                userHasSelectedQ3 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ3(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ3(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q3 selected by user: " + data);
            }
        });

        // Q4 - Waist
        rdoHealthRiskQ4.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ4_");
            if (!data.isEmpty()) {
                userHasSelectedQ4 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ4(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ4(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q4 selected by user: " + data);
            }
        });

        // Q5 - Blood Pressure
        rdoHealthRiskQ5.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ5_");
            if (!data.isEmpty()) {
                userHasSelectedQ5 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ5(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ5(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q5 selected by user: " + data);
            }
        });

        // Q6 - Family History
        rdoHealthRiskQ6.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isUpdating || checkedId == -1) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ6_");
            if (!data.isEmpty()) {
                userHasSelectedQ6 = true; // ผู้ใช้เลือกเอง
                if (healthRiskAssessmentLiveData != null) {
                    healthRiskAssessmentLiveData.setSelectHealthRiskQ6(checkedId);
                }
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ6(data);
                notifyDataPasser();
                updateScoreAndHighlight();
                Log.d("HealthRiskAssessment", "Q6 selected by user: " + data);
            }
        });
    }

    private String getSelectedValue(int checkedId, String prefix) {
        try {
            String resourceName = getResources().getResourceEntryName(checkedId);
            if (resourceName.startsWith(prefix)) {
                return resourceName.substring(prefix.length());
            }
        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error getting selected value: " + e.getMessage());
        }
        return "";
    }

    private void updateSharedViewModel() {
        if (isUpdating) return; // เพิ่มการป้องกัน
        if (shareViewModel != null && healthRiskAssessmentLiveData != null) {
            HealthRiskAssessmentLiveData current = shareViewModel.getHealthRiskAssessmentLiveDataMutableLiveData().getValue();
            if (current != healthRiskAssessmentLiveData) {
                isUpdating = true;
                try {
                    shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
                } finally {
                    isUpdating = false;
                }
            }
        }
    }

    private void notifyDataPasser() {
        if (dataPasser != null && !isUpdating) {
            Log.d("HealthRiskAssessment", "Notifying DataPasser - ID: " +
                    (healthRiskAssessmentInfo != null ? healthRiskAssessmentInfo.getId() : "null"));
            dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
        }
    }

    private boolean hasExistingData() {
        if (healthRiskAssessmentInfo == null) return false;

        return (healthRiskAssessmentInfo.getHealthRiskQ1() != null && !healthRiskAssessmentInfo.getHealthRiskQ1().equals("0")) ||
                (healthRiskAssessmentInfo.getHealthRiskQ2() != null && !healthRiskAssessmentInfo.getHealthRiskQ2().equals("0")) ||
                (healthRiskAssessmentInfo.getHealthRiskQ3() != null && !healthRiskAssessmentInfo.getHealthRiskQ3().equals("0")) ||
                (healthRiskAssessmentInfo.getHealthRiskQ4() != null && !healthRiskAssessmentInfo.getHealthRiskQ4().equals("0")) ||
                (healthRiskAssessmentInfo.getHealthRiskQ5() != null && !healthRiskAssessmentInfo.getHealthRiskQ5().equals("0")) ||
                (healthRiskAssessmentInfo.getHealthRiskQ6() != null && !healthRiskAssessmentInfo.getHealthRiskQ6().equals("0"));
    }

    private void setupPersonDataObserver() {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonDataLiveData().observe(getViewLifecycleOwner(), personData -> {
            if (personData != null && !isLoadingExistingData) {
                Log.d("HealthRiskAssessment", "Received PersonData: " + personData.toString());
                // แสดงความคิดเห็น: ไม่ auto-select หากผู้ใช้เลือกแล้ว
                hasAutoSelectedOnce = true;
                if(fisrtTime) {
                    autoSelectFromPersonData(personData);
                    fisrtTime = false;
                }
            }
        });
    }

    public void resetAutoSelection() {
        hasAutoSelectedOnce = false;
        Log.d("HealthRiskAssessment", "Reset auto-selection flag");
    }

    private void loadData() {
        SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getHealthRiskAssessmentLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null && !isUpdating) {
                List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos =
                        sfHealthRiskAssessmentInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));

                isUpdating = true;
                try {
                    if (!healthRiskAssessmentInfos.isEmpty()) {
                        // มีข้อมูลเดิม
                        for (HealthRiskAssessmentInfo healthRiskAssessmentInfo1 : healthRiskAssessmentInfos) {
                            Log.d("healthRiskAssessmentInfo1", "Found existing data with ID: " + healthRiskAssessmentInfo1.getId());
                            setHealthRiskInfo(healthRiskAssessmentInfo1);
//                            editFcbg.setText(healthRiskAssessmentInfo1.getFcbg());
//                            editFpg.setText(healthRiskAssessmentInfo1.getFpg());
                        }
                    } else {
                        // ไม่มีข้อมูลเดิม - สร้างใหม่
                        Log.d("HealthRiskAssessment", "No existing data found, creating new");
                        if (healthRiskAssessmentInfo == null) {
                            healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();
                        }
                    }
                } finally {
                    isUpdating = false;
                }
            }
        });
    }

    public void setHealthRiskInfo(HealthRiskAssessmentInfo info) {
        if (info != null) {
            this.healthRiskAssessmentInfo = info;
            Log.d("HealthRiskAssessment", "Set HealthRiskInfo with ID: " + info.getId());
        } else {
            // สร้างใหม่เฉพาะเมื่อไม่มีข้อมูล
            this.healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();
            Log.d("HealthRiskAssessment", "Created new HealthRiskInfo");
        }
        loadExistingData();
    }

    private void loadExistingData() {
        if (healthRiskAssessmentInfo == null) return;

        isUpdating = true;

        try {
            // Clear all selections first ก่อนที่จะ load ข้อมูลใหม่
            clearAllRadioSelections();

            // Load all questions silently
            loadQuestionSilently("rdoHealthRiskQ1_", healthRiskAssessmentInfo.getHealthRiskQ1());
            loadQuestionSilently("rdoHealthRiskQ2_", healthRiskAssessmentInfo.getHealthRiskQ2());
            loadQuestionSilently("rdoHealthRiskQ3_", healthRiskAssessmentInfo.getHealthRiskQ3());
            loadQuestionSilently("rdoHealthRiskQ4_", healthRiskAssessmentInfo.getHealthRiskQ4());
            loadQuestionSilently("rdoHealthRiskQ5_", healthRiskAssessmentInfo.getHealthRiskQ5());
            loadQuestionSilently("rdoHealthRiskQ6_", healthRiskAssessmentInfo.getHealthRiskQ6());

            // Load glucose values
//            setTextSilently(editFcbg, healthRiskAssessmentInfo.getFcbg());
//            setTextSilently(editFpg, healthRiskAssessmentInfo.getFpg());


            updateScoreAndHighlight();
            updateGlucoseHighlightFromCurrentData();
            updateBloodSugarGaugeDisplay();

            editFcbg.setText(healthRiskAssessmentInfo.getFcbg());
            editFpg.setText(healthRiskAssessmentInfo.getFpg());


        } finally {
            isUpdating = false;
        }
    }
    public BloodSugarGaugeView.BloodSugarLevel getCurrentBloodSugarStatus() {
        if (bloodSugarGauge != null) {
            return bloodSugarGauge.getCurrentBloodSugarLevel();
        }
        return null;
    }
    public boolean hasGlucoseData() {
        double glucose = getCurrentGlucoseValue();
        return glucose > 0;
    }
    public double getDisplayedGlucoseValue() {
        return getCurrentGlucoseValue();
    }
    private void clearAllRadioSelections() {
        // รีเซ็ต user selection flags
        userHasSelectedQ1 = false;
        userHasSelectedQ2 = false;
        userHasSelectedQ3 = false;
        userHasSelectedQ4 = false;
        userHasSelectedQ5 = false;
        userHasSelectedQ6 = false;

        if (rdoHealthRiskQ1 != null) rdoHealthRiskQ1.clearCheck();
        if (rdoHealthRiskQ2 != null) rdoHealthRiskQ2.clearCheck();
        if (rdoHealthRiskQ3 != null) rdoHealthRiskQ3.clearCheck();
        if (rdoHealthRiskQ4 != null) rdoHealthRiskQ4.clearCheck();
        if (rdoHealthRiskQ5 != null) rdoHealthRiskQ5.clearCheck();
        if (rdoHealthRiskQ6 != null) rdoHealthRiskQ6.clearCheck();
    }

    private void loadQuestionSilently(String prefix, String value) {
        if (value != null && !value.equals("0") && !value.isEmpty()) {
            selectRadioButtonSilently(prefix + value);
        }
    }

    private void setTextSilently(EditText editText, String value) {
        if (editText == null) return;

        isUpdating = true;
        try {
            int cursorPosition = editText.getSelectionStart();
            String currentText = editText.getText().toString();
            String newText = (value != null) ? value : "";

            if (!currentText.equals(newText)) {
                editText.setText(newText);
                int newCursorPosition = Math.min(cursorPosition, newText.length());
                if (newCursorPosition >= 0 && newCursorPosition <= newText.length()) {
                    editText.setSelection(newCursorPosition);
                }
            }
        } catch (Exception e) {
            String newText = (value != null) ? value : "";
            editText.setText(newText);
            editText.setSelection(newText.length());
        } finally {
            isUpdating = false;
        }
    }

    private void autoSelectFromPersonData(PersonData personData) {
        if (personData == null || isLoadingExistingData) return;

        isAutoSelecting = true;

        try {
            Log.d("HealthRiskAssessment", "Starting auto-selection from PersonData");

            // Auto-select แต่ละข้อ
            autoSelectAge(personData);
            autoSelectGender(personData);
            autoSelectBMI(personData);
            autoSelectWaist(personData);
            autoSelectHypertension(personData);
            autoSelectFamilyHistory(personData);
            autoFillGlucoseValues(personData);

            // อัพเดตคะแนนและ highlight
            updateScoreAndHighlight();

            // แจ้ง DataPasser หลังจาก auto-select เสร็จ
            if (dataPasser != null) {
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }

        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error in autoSelectFromPersonData: " + e.getMessage());
        } finally {
            isAutoSelecting = false;
        }
    }

    private void autoSelectAge(PersonData personData) {
        if (personData.getAge() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ1 || (healthRiskAssessmentInfo.getHealthRiskQ1() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ1().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select age: User has already selected");
                return;
            }

            int age = personData.getAge();
            String ageCategory = "";

            if (age >= 34 && age <= 39) ageCategory = "1";
            else if (age >= 40 && age <= 44) ageCategory = "2";
            else if (age >= 45 && age <= 49) ageCategory = "3";
            else if (age >= 50) ageCategory = "4";

            if (!ageCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ1(ageCategory);
                selectRadioButtonSilently("rdoHealthRiskQ1_" + ageCategory);
                Log.d("HealthRiskAssessment", "Auto-selected age: " + ageCategory + " for age: " + age);
            }
        }
    }

    private void autoSelectGender(PersonData personData) {
        if (personData.getGender() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ2 || (healthRiskAssessmentInfo.getHealthRiskQ2() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ2().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select gender: User has already selected");
                return;
            }

            String gender = personData.getGender().toLowerCase();
            String genderCategory = "";

            if (gender.equals("female") || gender.equals("หญิง") || gender.equals("f") || gender.equals("2")) {
                genderCategory = "1";
            } else if (gender.equals("male") || gender.equals("ชาย") || gender.equals("m") || gender.equals("1")) {
                genderCategory = "2";
            }

            if (!genderCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ2(genderCategory);
                selectRadioButtonSilently("rdoHealthRiskQ2_" + genderCategory);
                Log.d("HealthRiskAssessment", "Auto-selected gender: " + genderCategory + " for: " + gender);
            }
        }
    }

    private void autoSelectBMI(PersonData personData) {
        if (personData.getBmi() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ3 || (healthRiskAssessmentInfo.getHealthRiskQ3() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ3().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select BMI: User has already selected");
                return;
            }

            double bmi = personData.getBmi();
            String bmiCategory = "";

            if (bmi < 23) bmiCategory = "1";
            else if (bmi >= 23 && bmi < 27.5) bmiCategory = "2";
            else if (bmi >= 27.5) bmiCategory = "3";

            if (!bmiCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ3(bmiCategory);
                selectRadioButtonSilently("rdoHealthRiskQ3_" + bmiCategory);
                Log.d("HealthRiskAssessment", "Auto-selected BMI: " + bmiCategory + " for BMI: " + bmi);
            }
        }
    }

    private void autoSelectWaist(PersonData personData) {
        if (personData.getWaistCircumference() != null && personData.getGender() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ4 || (healthRiskAssessmentInfo.getHealthRiskQ4() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ4().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select waist: User has already selected");
                return;
            }

            double waist = personData.getWaistCircumference();
            String gender = personData.getGender().toLowerCase();
            String waistCategory = "";

            if (gender.equals("male") || gender.equals("ชาย") || gender.equals("m") || gender.equals("1")) {
                waistCategory = waist < 90 ? "1" : "2";
            } else if (gender.equals("female") || gender.equals("หญิง") || gender.equals("f") || gender.equals("2")) {
                waistCategory = waist < 80 ? "1" : "2";
            }

            if (!waistCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ4(waistCategory);
                selectRadioButtonSilently("rdoHealthRiskQ4_" + waistCategory);
                Log.d("HealthRiskAssessment", "Auto-selected waist: " + waistCategory + " for waist: " + waist + " cm");
            }
        }
    }

    private void autoSelectHypertension(PersonData personData) {
        if (personData.hasHypertension() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ5 || (healthRiskAssessmentInfo.getHealthRiskQ5() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ5().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select hypertension: User has already selected");
                return;
            }

            String bpCategory = personData.hasHypertension() ? "2" : "1";
            healthRiskAssessmentInfo.setHealthRiskQ5(bpCategory);
            selectRadioButtonSilently("rdoHealthRiskQ5_" + bpCategory);
            Log.d("HealthRiskAssessment", "Auto-selected hypertension: " + bpCategory);
        }
    }

    private void autoSelectFamilyHistory(PersonData personData) {
        if (personData.hasFamilyDiabetesHistory() != null) {
            // ตรวจสอบว่ามีข้อมูลเดิมหรือผู้ใช้เลือกแล้วหรือไม่
            if (userHasSelectedQ6 || (healthRiskAssessmentInfo.getHealthRiskQ6() != null &&
                    !healthRiskAssessmentInfo.getHealthRiskQ6().equals("-1"))) {
                Log.d("HealthRiskAssessment", "Skip auto-select family history: User has already selected");
                return;
            }

            String familyHistoryCategory = personData.hasFamilyDiabetesHistory() ? "2" : "1";
            healthRiskAssessmentInfo.setHealthRiskQ6(familyHistoryCategory);
            selectRadioButtonSilently("rdoHealthRiskQ6_" + familyHistoryCategory);
            Log.d("HealthRiskAssessment", "Auto-selected family diabetes history: " + familyHistoryCategory);
        }
    }

    public void refreshWithPersonData(PersonData personData) {
        if (personData != null && !hasExistingData()) {
            // ถ้ายังไม่มีข้อมูลเดิม ให้ auto-select จาก PersonData
            autoSelectFromPersonData(personData);
        }
    }

    private void autoFillGlucoseValues(PersonData personData) {
        boolean fcbgHasFocus = editFcbg.hasFocus();
        boolean fpgHasFocus = editFpg.hasFocus();

        if (!fcbgHasFocus) {
            if (personData.getFcbg() != null) {
                String fcbgValue = String.valueOf(personData.getFcbg());
                setTextSilently(editFcbg, fcbgValue);
                healthRiskAssessmentInfo.setFcbg(fcbgValue);
                Log.d("HealthRiskAssessment", "Auto-filled FCBG: " + fcbgValue);
            } else {
                setTextSilently(editFcbg, "");
                healthRiskAssessmentInfo.setFcbg("");
            }
        }

        if (!fpgHasFocus) {
            if (personData.getFpg() != null) {
                String fpgValue = String.valueOf(personData.getFpg());
                setTextSilently(editFpg, fpgValue);
                healthRiskAssessmentInfo.setFpg(fpgValue);
                Log.d("HealthRiskAssessment", "Auto-filled FPG: " + fpgValue);
            } else {
                setTextSilently(editFpg, "");
                healthRiskAssessmentInfo.setFpg("");
            }
        }
        updateGlucoseHighlightFromCurrentData();
        updateBloodSugarGaugeDisplay();
    }

    private String validateAndFormatNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            String cleaned = input.replaceAll("[^0-9.]", "");
            String[] parts = cleaned.split("\\.");
            if (parts.length > 2) {
                cleaned = parts[0] + "." + parts[1];
            }

            if (!cleaned.isEmpty()) {
                Double.parseDouble(cleaned);
            }

            return cleaned;
        } catch (NumberFormatException e) {
            return input;
        }
    }

    private void setupFocusListeners() {
        editFcbg.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = editFcbg.getText().toString().trim();
                String validatedText = validateAndFormatNumber(text);

                if (!text.equals(validatedText)) {
                    setTextSilently(editFcbg, validatedText);
                    healthRiskAssessmentInfo.setFcbg(validatedText);
                }
            }
        });

        editFpg.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String text = editFpg.getText().toString().trim();
                String validatedText = validateAndFormatNumber(text);

                if (!text.equals(validatedText)) {
                    setTextSilently(editFpg, validatedText);
                    healthRiskAssessmentInfo.setFpg(validatedText);
                }
            }
        });
    }

    private void updateGlucoseHighlightFromCurrentData() {
        String fcbgStr = healthRiskAssessmentInfo.getFcbg();
        String fpgStr = healthRiskAssessmentInfo.getFpg();

        Double glucoseValue = null;

        if (fpgStr != null && !fpgStr.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fpgStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        if (glucoseValue == null && fcbgStr != null && !fcbgStr.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fcbgStr);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
//
//        if (glucoseValue != null) {
//            highlightGlucoseRow(glucoseValue);
//        } else {
//            clearGlucoseHighlight();
//        }
    }

    private void selectRadioButtonSilently(String radioButtonName) {
        try {
            int radioButtonId = getResources().getIdentifier(
                    radioButtonName, "id", requireContext().getPackageName());

            if (radioButtonId != 0) {
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                    Log.d("HealthRiskAssessment", "Silently selected: " + radioButtonName);
                } else {
                    Log.w("HealthRiskAssessment", "RadioButton not found: " + radioButtonName);
                }
            } else {
                Log.w("HealthRiskAssessment", "RadioButton ID not found for: " + radioButtonName);
            }
        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error selecting radio button silently: " + radioButtonName);
        }
    }

    public void setPersonDataFromOtherScreens(Bundle personDataBundle) {
        PersonData personData = new PersonData();

        try {
            if (personDataBundle.containsKey("age")) {
                personData.setAge(personDataBundle.getInt("age"));
            }
            if (personDataBundle.containsKey("gender")) {
                personData.setGender(personDataBundle.getString("gender"));
            }
            if (personDataBundle.containsKey("bmi")) {
                personData.setBmi(personDataBundle.getDouble("bmi"));
            }
            if (personDataBundle.containsKey("waist_circumference")) {
                personData.setWaistCircumference(personDataBundle.getDouble("waist_circumference"));
            }
            if (personDataBundle.containsKey("has_hypertension")) {
                personData.setHasHypertension(personDataBundle.getBoolean("has_hypertension"));
            }
            if (personDataBundle.containsKey("family_diabetes_history")) {
                personData.setHasFamilyDiabetesHistory(personDataBundle.getBoolean("family_diabetes_history"));
            }
            if (personDataBundle.containsKey("fcbg")) {
                personData.setFcbg(personDataBundle.getDouble("fcbg"));
            }
            if (personDataBundle.containsKey("fpg")) {
                personData.setFpg(personDataBundle.getDouble("fpg"));
            }

            autoSelectFromPersonData(personData);

        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error in setPersonDataFromOtherScreens: " + e.getMessage());
        }
    }

    public HealthRiskAssessmentInfo getHealthRiskAssessmentInfo() {
        Log.d("HealthRiskAssessment", "Getting HealthRiskInfo - ID: " +
                (healthRiskAssessmentInfo != null ? healthRiskAssessmentInfo.getId() : "null"));
        return healthRiskAssessmentInfo;
    }

    private int calculateTotalScore() {
        if (healthRiskAssessmentInfo == null) {
            Log.d("HealthRiskAssessment", "healthRiskAssessmentInfo is null, returning 0 score");
            return 0;
        }
        int totalScore = 0;

        // คะแนนอายุ
        if ("3".equals(healthRiskAssessmentInfo.getHealthRiskQ1())) totalScore += 1;  // 45-49 ปี
        if ("4".equals(healthRiskAssessmentInfo.getHealthRiskQ1())) totalScore += 2;  // 50 ปีขึ้นไป

        // คะแนนเพศ (ชาย = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ2())) totalScore += 2;

        // คะแนน BMI
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ3())) totalScore += 3;  // 23-27.5
        if ("3".equals(healthRiskAssessmentInfo.getHealthRiskQ3())) totalScore += 5;  // >= 27.5

        // คะแนนรอบเอว (เกิน = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ4())) totalScore += 2;

        // คะแนนความดัน (มี = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ5())) totalScore += 2;

        // คะแนนประวัติครอบครัว (มี = 4 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ6())) totalScore += 4;

        return totalScore;
    }

    /**
     * อัปเดตการแสดงคะแนนและระดับความเสี่ยงแบบใหม่ (คล้าย Nicotine)
     */
    private void updateHealthRiskScore(int score) {
        if (tvHealthRiskScore != null) {
            tvHealthRiskScore.setText(String.valueOf(score));

            // เปลี่ยนสี background และ text color ของ tvHealthRiskScore ตามระดับคะแนน
            if (score <= 2) {
                // เสี่ยงน้อย - สีเขียว
                tvHealthRiskScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvHealthRiskScore.setTextColor(Color.WHITE);
            } else if (score >= 3 && score <= 5) {
                // เสี่ยงปานกลาง - สีเหลือง
                tvHealthRiskScore.setBackground(createGradientDrawable("#F1C40F", "#F39C12"));
                tvHealthRiskScore.setTextColor(Color.WHITE);
            } else if (score >= 6 && score <= 8) {
                // เสี่ยงสูง - สีส้ม
                tvHealthRiskScore.setBackground(createGradientDrawable("#FF9800", "#FF6B35"));
                tvHealthRiskScore.setTextColor(Color.WHITE);
            } else if (score > 8) {
                // เสี่ยงสูงมาก - สีแดง
                tvHealthRiskScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvHealthRiskScore.setTextColor(Color.WHITE);
            }
        }

        if (tvHealthRiskLevel != null) {
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนน
            if (score <= 2) {
                riskLevel = "เสี่ยงน้อย (น้อยกว่าร้อยละ 5)";
                tvHealthRiskLevel.setBackgroundResource(R.color.light_green);
                tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 3 && score <= 5) {
                riskLevel = "เสี่ยงปานกลาง (ร้อยละ 5-10)";
                tvHealthRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 6 && score <= 8) {
                riskLevel = "เสี่ยงสูง (ร้อยละ 11-20)";
                tvHealthRiskLevel.setBackgroundResource(R.color.light_orange);
                tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.dark_orange));
            } else if (score > 8) {
                riskLevel = "เสี่ยงสูงมาก (มากกว่าร้อยละ 20)";
                tvHealthRiskLevel.setBackgroundResource(R.color.light_red);
                tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "ยังไม่ได้ประเมิน";
                tvHealthRiskLevel.setBackgroundResource(R.color.light_gray);
                tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvHealthRiskLevel.setText(riskLevel);
        }
        updateGaugeWithScore(score);
    }

    private android.graphics.drawable.GradientDrawable createGradientDrawable(String startColor, String endColor) {
        android.graphics.drawable.GradientDrawable gradient = new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        Color.parseColor(startColor),
                        Color.parseColor(endColor)
                }
        );

        // ตั้งค่ามุมโค้ง
        gradient.setCornerRadius(20f);

        return gradient;
    }

    private void highlightScoreRow(int totalScore) {
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        int highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);

        TableRow row1 = getView().findViewById(R.id.scoreRow1);
        TableRow row2 = getView().findViewById(R.id.scoreRow2);
        TableRow row3 = getView().findViewById(R.id.scoreRow3);
        TableRow row4 = getView().findViewById(R.id.scoreRow4);

        // รีเซ็ตสีพื้นหลัง
        if (row1 != null) row1.setBackgroundColor(white);
        if (row2 != null) row2.setBackgroundColor(light_gray);
        if (row3 != null) row3.setBackgroundColor(white);
        if (row4 != null) row4.setBackgroundColor(light_gray);

        // ไฮไลท์แถวตามคะแนน
        if (totalScore <= 2) {
            if (row1 != null) row1.setBackgroundColor(highlightColor);
        } else if (totalScore >= 3 && totalScore <= 5) {
            if (row2 != null) row2.setBackgroundColor(highlightColor);
        } else if (totalScore >= 6 && totalScore <= 8) {
            if (row3 != null) row3.setBackgroundColor(highlightColor);
        } else if (totalScore > 8) {
            if (row4 != null) row4.setBackgroundColor(highlightColor);
        }

        // อัพเดตการแสดงผลเก่า (รักษาไว้สำหรับ backward compatibility)
        TextView resultTextView = getView() != null ? getView().findViewById(R.id.resultHealthRiskScrollView) : null;
        if (resultTextView != null) {
            resultTextView.setText(String.format("คะแนนที่ได้: %d คะแนน", totalScore));
        }
    }

    private void updateScoreAndHighlight() {
        int totalScore = calculateTotalScore();

        // อัพเดตการแสดงผลแบบใหม่ (คล้าย Nicotine)
        updateHealthRiskScore(totalScore);

        // อัพเดตการแสดงผลแบบเก่า (ตารางไฮไลท์)
        highlightScoreRow(totalScore);
    }

//    private void highlightGlucoseRow(double glucoseValue) {
//        TableRow row1 = getView().findViewById(R.id.glucoseRow1);
//        TableRow row2 = getView().findViewById(R.id.glucoseRow2);
//        TableRow row3 = getView().findViewById(R.id.glucoseRow3);
//
//        int white = ContextCompat.getColor(requireContext(), R.color.white);
//        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
//        int highlight = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
//
//        if (row1 != null) row1.setBackgroundColor(white);
//        if (row2 != null) row2.setBackgroundColor(light_gray);
//        if (row3 != null) row3.setBackgroundColor(white);
//
//        if (glucoseValue < 100) {
//            if (row1 != null) row1.setBackgroundColor(highlight);
//        } else if (glucoseValue >= 100 && glucoseValue <= 125) {
//            if (row2 != null) row2.setBackgroundColor(highlight);
//        } else if (glucoseValue >= 126) {
//            if (row3 != null) row3.setBackgroundColor(highlight);
//        }
//    }

    private void setupGlucoseInputListeners() {
        // FCBG Listener
        editFcbg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    return;
                }


                String text = s.toString().trim();

                if (text.isEmpty()) {
                    healthRiskAssessmentInfo.setFcbg("");
                    healthRiskAssessmentLiveData.setFcbg("");
//                    clearGlucoseHighlight();
                    updateBloodSugarGaugeDisplay();
                    notifyDataPasser();
                } else {
                    try {
                        double fcbgValue = Double.parseDouble(text);
                        healthRiskAssessmentInfo.setFcbg(text);
                        healthRiskAssessmentLiveData.setFcbg(text);
//                        highlightGlucoseRow(fcbgValue);
                        updateBloodSugarGaugeDisplay();
                        notifyDataPasser();
                    } catch (NumberFormatException e) {
                        healthRiskAssessmentInfo.setFcbg(text);
                        healthRiskAssessmentLiveData.setFcbg(text);
//                        clearGlucoseHighlight();
                        resetBloodSugarGauge();
                        notifyDataPasser();
                    }
                }
                shareViewModel.getPersonDataLiveData()
                        .observe(getViewLifecycleOwner(), personData -> {
                            if (personData != null) {
                                if(text.isEmpty()) {
                                    personData.setFcbg(null);
                                } else {
                                    // แปลงเป็น Double และตั้งค่าให้กับ PersonData
                                    if (text.equals("-")) {
                                        personData.setFcbg(null);
                                    } else if (text.equals(".")) {
                                        personData.setFcbg(0.0);
                                    } else {
                                        personData.setFcbg(Double.parseDouble(text));
                                    }
                                }
//                                personData.setFcbg(Double.parseDouble(text));
                                autoFillGlucoseValues(personData);
                            }
                        });
            }
        });

        // FPG Listener
        editFpg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    return;
                }

                String text = s.toString().trim();

                if (text.isEmpty()) {
                    healthRiskAssessmentInfo.setFpg("");
                    healthRiskAssessmentLiveData.setFpg("");
//                    clearGlucoseHighlight();
                    updateBloodSugarGaugeDisplay();
                    notifyDataPasser();
                } else {
                    try {
                        double fpgValue = Double.parseDouble(text);
                        healthRiskAssessmentInfo.setFpg(text);
                        healthRiskAssessmentLiveData.setFpg(text);
//                        highlightGlucoseRow(fpgValue);
                        updateBloodSugarGaugeDisplay();
                        notifyDataPasser();
                    } catch (NumberFormatException e) {
                        healthRiskAssessmentInfo.setFpg(text);
                        healthRiskAssessmentLiveData.setFpg(text);
//                        clearGlucoseHighlight();
                        resetBloodSugarGauge();
                        notifyDataPasser();
                    }
                    shareViewModel.getPersonDataLiveData()
                            .observe(getViewLifecycleOwner(), personData -> {
                                if (personData != null) {
                                    if(text.isEmpty()) {
                                        personData.setFpg(null);
                                    } else {
                                        // แปลงเป็น Double และตั้งค่าให้กับ PersonData
                                        if (text.equals("-")) {
                                            personData.setFpg(null);
                                        } else if (text.equals(".")) {
                                            personData.setFpg(0.0);
                                        } else {
                                            personData.setFpg(Double.parseDouble(text));
                                        }
                                    }
                                    autoFillGlucoseValues(personData);
                                }
                            });
                }
            }
        });
    }

//    private void clearGlucoseHighlight() {
//        TableRow row1 = getView() != null ? getView().findViewById(R.id.glucoseRow1) : null;
//        TableRow row2 = getView() != null ? getView().findViewById(R.id.glucoseRow2) : null;
//        TableRow row3 = getView() != null ? getView().findViewById(R.id.glucoseRow3) : null;
//
//        int white = ContextCompat.getColor(requireContext(), R.color.white);
//        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
//
//        if (row1 != null) row1.setBackgroundColor(white);
//        if (row2 != null) row2.setBackgroundColor(light_gray);
//        if (row3 != null) row3.setBackgroundColor(white);
//    }

    // เพิ่มเมธอด validation ใน HealthRiskAssessmentFragment class

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
     */
    public boolean isFormComplete() {
        if (healthRiskAssessmentInfo == null) {
            return false;
        }

        // ตรวจสอบคำถามทั้ง 6 ข้อ
        boolean q1Complete = healthRiskAssessmentInfo.getHealthRiskQ1() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().isEmpty();

        boolean q2Complete = healthRiskAssessmentInfo.getHealthRiskQ2() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().isEmpty();

        boolean q3Complete = healthRiskAssessmentInfo.getHealthRiskQ3() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().isEmpty();

        boolean q4Complete = healthRiskAssessmentInfo.getHealthRiskQ4() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().isEmpty();

        boolean q5Complete = healthRiskAssessmentInfo.getHealthRiskQ5() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().isEmpty();

        boolean q6Complete = healthRiskAssessmentInfo.getHealthRiskQ6() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().isEmpty();

        // ตรวจสอบว่ามีค่าน้ำตาล FCBG หรือ FPG หรือไม่
        boolean fcbgComplete = healthRiskAssessmentInfo.getFcbg() != null &&
                !healthRiskAssessmentInfo.getFcbg().isEmpty();
        boolean fpgComplete = healthRiskAssessmentInfo.getFpg() != null &&
                !healthRiskAssessmentInfo.getFpg().isEmpty();

        // ตรวจสอบว่าตอบครบทุกข้อหรือไม่
        return q1Complete && q2Complete && q3Complete && q4Complete && q5Complete && q6Complete
                && (fcbgComplete || fpgComplete);
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าตอบคำถามครบหรือไม่
        ArrayList<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("แบบประเมินความเสี่ยงโรคเบาหวาน: ยังไม่ได้ตอบข้อ ");

            // แสดงรายการข้อที่ยังไม่ได้ตอบ
            for (int i = 0; i < unansweredQuestions.size(); i++) {
                if (i > 0) {
                    message.append(", ");
                }
                message.append(unansweredQuestions.get(i));
            }
        }

        return message.toString();
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        if (healthRiskAssessmentInfo == null) {
            return "แบบประเมินความเสี่ยงโรคเบาหวาน:\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingQuestions = new ArrayList<>();

        if (healthRiskAssessmentInfo.getHealthRiskQ1() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ1().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ1().isEmpty()) {
            missingQuestions.add("ข้อ 1: อายุ");
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ2() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ2().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ2().isEmpty()) {
            missingQuestions.add("ข้อ 2: เพศ");
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ3() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ3().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ3().isEmpty()) {
            missingQuestions.add("ข้อ 3: ดัชนีมวลกาย (BMI)");
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ4() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ4().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ4().isEmpty()) {
            missingQuestions.add("ข้อ 4: รอบเอว");
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ5() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ5().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ5().isEmpty()) {
            missingQuestions.add("ข้อ 5: ความดันโลหิตสูง");
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ6() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ6().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ6().isEmpty()) {
            missingQuestions.add("ข้อ 6: ประวัติเบาหวานในครอบครัว");
        }
        // ตรวจสอบค่าน้ำตาล FCBG และ FPG
        if (healthRiskAssessmentInfo.getFcbg() == null || healthRiskAssessmentInfo.getFcbg().isEmpty()) {
            missingQuestions.add("ค่าน้ำตาลในเลือด (FCBG)");
        }
        if (healthRiskAssessmentInfo.getFpg() == null || healthRiskAssessmentInfo.getFpg().isEmpty()) {
            missingQuestions.add("ค่าน้ำตาลในเลือด (FPG)");
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("แบบประเมินความเสี่ยงโรคเบาหวาน:\n");
            message.append("กรุณาตอบคำถามที่ยังไม่ได้ตอบ:\n");
            for (String question : missingQuestions) {
                message.append("• ").append(question).append("\n");
            }

            // เพิ่มการแจ้งเตือนเกี่ยวกับค่าน้ำตาล (ถ้าต้องการ)
            boolean hasFCBG = healthRiskAssessmentInfo.getFcbg() != null && !healthRiskAssessmentInfo.getFcbg().isEmpty();
            boolean hasFPG = healthRiskAssessmentInfo.getFpg() != null && !healthRiskAssessmentInfo.getFpg().isEmpty();

            if (!hasFCBG && !hasFPG) {
                message.append("• กรุณากรอกค่าน้ำตาลในเลือด (FCBG หรือ FPG)");
            }
//            else if (!hasFCBG) {
//                message.append("• กรุณากรอกค่าน้ำตาลในเลือด (FCBG) หรือ กรอกค่า 0 ถ้าไม่มีข้อมูล");
//            } else if (!hasFPG) {
//                message.append("• กรุณากรอกค่าน้ำตาลในเลือด (FPG) หรือ กรอกค่า 0 ถ้าไม่มีข้อมูล");
//            }

            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * รีเซ็ตฟอร์มกลับเป็นค่าเริ่มต้น
     */
    public void resetForm() {
        // ล้างการเลือกทั้งหมด
        resetAutoSelection();
        clearAllRadioSelections();

        // ล้างค่าน้ำตาล
        if (editFcbg != null) {
            setTextSilently(editFcbg, "");
        }
        if (editFpg != null) {
            setTextSilently(editFpg, "");
        }

        // รีเซ็ต healthRiskAssessmentInfo
        healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();

        // รีเซ็ต LiveData
        healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();

        // ล้าง highlight
//        clearGlucoseHighlight();

        // รีเซ็ตการแสดงคะแนนแบบใหม่
        if (tvHealthRiskScore != null) {
            tvHealthRiskScore.setText("-");
            tvHealthRiskScore.setBackgroundResource(R.color.light_gray);
            tvHealthRiskScore.setTextColor(getResources().getColor(R.color.darker_gray));
        }

        if (tvHealthRiskLevel != null) {
            tvHealthRiskLevel.setText("ยังไม่ได้ประเมิน");
            tvHealthRiskLevel.setBackgroundResource(R.color.light_gray);
            tvHealthRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
        }

        // รีเซ็ตการแสดงคะแนนแบบเก่า
        TextView resultTextView = getView() != null ? getView().findViewById(R.id.resultHealthRiskScrollView) : null;
        if (resultTextView != null) {
            resultTextView.setText("คะแนนที่ได้: - คะแนน");
        }

        // รีเซ็ต highlight คะแนน
        clearScoreHighlight();
    }

    public void forceRefreshFromPersonData() {
        resetAutoSelection();
        // PersonData จะถูกส่งมาใหม่อัตโนมัติจาก Observer
    }

    /**
     * ล้าง highlight คะแนน
     */
    private void clearScoreHighlight() {
        if (getView() == null) return;

        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);

        TableRow row1 = getView().findViewById(R.id.scoreRow1);
        TableRow row2 = getView().findViewById(R.id.scoreRow2);
        TableRow row3 = getView().findViewById(R.id.scoreRow3);
        TableRow row4 = getView().findViewById(R.id.scoreRow4);

        if (row1 != null) row1.setBackgroundColor(white);
        if (row2 != null) row2.setBackgroundColor(light_gray);
        if (row3 != null) row3.setBackgroundColor(white);
        if (row4 != null) row4.setBackgroundColor(light_gray);
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (healthRiskAssessmentInfo == null) {
            return false;
        }

        // ตรวจสอบว่ามีการตอบคำถามอย่างน้อย 1 ข้อหรือไม่
        boolean hasAnswer = false;

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ1() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().isEmpty());

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ2() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().isEmpty());

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ3() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().isEmpty());

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ4() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().isEmpty());

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ5() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().isEmpty());

        hasAnswer |= (healthRiskAssessmentInfo.getHealthRiskQ6() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().isEmpty());

        // ตรวจสอบค่าน้ำตาลด้วย
        hasAnswer |= (healthRiskAssessmentInfo.getFcbg() != null && !healthRiskAssessmentInfo.getFcbg().isEmpty());
        hasAnswer |= (healthRiskAssessmentInfo.getFpg() != null && !healthRiskAssessmentInfo.getFpg().isEmpty());

        return hasAnswer;
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (healthRiskAssessmentInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 6;

        if (healthRiskAssessmentInfo.getHealthRiskQ1() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ1().isEmpty()) completedQuestions++;

        if (healthRiskAssessmentInfo.getHealthRiskQ2() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ2().isEmpty()) completedQuestions++;

        if (healthRiskAssessmentInfo.getHealthRiskQ3() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ3().isEmpty()) completedQuestions++;

        if (healthRiskAssessmentInfo.getHealthRiskQ4() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ4().isEmpty()) completedQuestions++;

        if (healthRiskAssessmentInfo.getHealthRiskQ5() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ5().isEmpty()) completedQuestions++;

        if (healthRiskAssessmentInfo.getHealthRiskQ6() != null &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().equals("0") &&
                !healthRiskAssessmentInfo.getHealthRiskQ6().isEmpty()) completedQuestions++;

        return (completedQuestions * 100) / totalQuestions;
    }

    /**
     * แสดงสถานะการกรอกข้อมูล
     */
    public void showCompletionStatus() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%)";

            // แสดงระดับความเสี่ยงด้วย
            String riskLevel = getRiskLevel();
            message += " - " + riskLevel;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("HealthRiskAssessment", "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ดึงรายชื่อคำถามที่ยังไม่ได้ตอบ
     */
    public ArrayList<Integer> getUnansweredQuestions() {
        ArrayList<Integer> unanswered = new ArrayList<>();

        if (healthRiskAssessmentInfo == null) {
            for (int i = 1; i <= 6; i++) {
                unanswered.add(i);
            }
            return unanswered;
        }

        if (healthRiskAssessmentInfo.getHealthRiskQ1() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ1().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ1().isEmpty()) unanswered.add(1);

        if (healthRiskAssessmentInfo.getHealthRiskQ2() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ2().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ2().isEmpty()) unanswered.add(2);

        if (healthRiskAssessmentInfo.getHealthRiskQ3() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ3().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ3().isEmpty()) unanswered.add(3);

        if (healthRiskAssessmentInfo.getHealthRiskQ4() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ4().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ4().isEmpty()) unanswered.add(4);

        if (healthRiskAssessmentInfo.getHealthRiskQ5() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ5().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ5().isEmpty()) unanswered.add(5);

        if (healthRiskAssessmentInfo.getHealthRiskQ6() == null ||
                healthRiskAssessmentInfo.getHealthRiskQ6().equals("0") ||
                healthRiskAssessmentInfo.getHealthRiskQ6().isEmpty()) unanswered.add(6);

        return unanswered;
    }

    /**
     * ดึงคำอธิบายของคำถามแต่ละข้อ
     */
    private String getQuestionDescription(int questionNumber) {
        switch (questionNumber) {
            case 1:
                return "อายุ";
            case 2:
                return "เพศ";
            case 3:
                return "ดัชนีมวลกาย (BMI)";
            case 4:
                return "รอบเอว";
            case 5:
                return "ความดันโลหิตสูง";
            case 6:
                return "ประวัติเบาหวานในครอบครัว";
            default:
                return "คำถามที่ " + questionNumber;
        }
    }

    /**
     * ตรวจสอบและเลื่อนไปยังคำถามแรกที่ยังไม่ได้ตอบ
     */
    public void scrollToFirstUnansweredQuestion() {
        ArrayList<Integer> unanswered = getUnansweredQuestions();
        if (!unanswered.isEmpty()) {
            int firstUnanswered = unanswered.get(0);
            RadioGroup targetGroup = null;

            switch (firstUnanswered) {
                case 1:
                    targetGroup = rdoHealthRiskQ1;
                    break;
                case 2:
                    targetGroup = rdoHealthRiskQ2;
                    break;
                case 3:
                    targetGroup = rdoHealthRiskQ3;
                    break;
                case 4:
                    targetGroup = rdoHealthRiskQ4;
                    break;
                case 5:
                    targetGroup = rdoHealthRiskQ5;
                    break;
                case 6:
                    targetGroup = rdoHealthRiskQ6;
                    break;
            }

            if (targetGroup != null) {
                targetGroup.requestFocus();
                // สามารถเพิ่มการ scroll ไปยัง view ได้ที่นี่
            }
        }
    }

    /**
     * ดึงระดับความเสี่ยงจากคะแนน
     */
    public String getRiskLevel() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int score = calculateTotalScore();

        if (score <= 2) {
            return "เสี่ยงน้อย";
        } else if (score >= 3 && score <= 5) {
            return "เสี่ยงปานกลาง";
        } else if (score >= 6 && score <= 8) {
            return "เสี่ยงสูง";
        } else if (score > 8) {
            return "เสี่ยงสูงมาก";
        }

        return "";
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงสูงหรือไม่ (คะแนน >= 6)
     */
    public boolean isHighRisk() {
        if (!isFormComplete()) {
            return false;
        }

        return calculateTotalScore() >= 6;
    }

    /**
     * แสดงคำแนะนำตามระดับความเสี่ยง
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int score = calculateTotalScore();

        if (score <= 2) {
            return "ความเสี่ยงน้อย - ควรตรวจสุขภาพประจำปี และรักษาพฤติกรรมสุขภาพที่ดีต่อไป";
        } else if (score >= 3 && score <= 5) {
            return "ความเสี่ยงปานกลาง - ควรปรับเปลี่ยนพฤติกรรม ควบคุมน้ำหนัก และตรวจน้ำตาลในเลือดทุก 1-3 ปี";
        } else if (score >= 6 && score <= 8) {
            return "ความเสี่ยงสูง - ควรพบแพทย์เพื่อตรวจน้ำตาลในเลือดโดยเร็ว และปรับเปลี่ยนพฤติกรรมอย่างจริงจัง";
        } else if (score > 8) {
            return "ความเสี่ยงสูงมาก - ควรพบแพทย์โดยด่วนเพื่อตรวจวินิจฉัยและรับการรักษา";
        }

        return "";
    }

    /**
     * ดึงข้อมูลสรุปแบบสั้น
     */
    public String getSummaryText() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int score = calculateTotalScore();
        String riskLevel = getRiskLevel();

        return String.format("คะแนน: %d - %s", score, riskLevel);
    }

    /**
     * ดึงข้อมูลที่กรอกแล้ว
     */
    public HealthRiskAssessmentInfo getFormData() {
        return this.healthRiskAssessmentInfo;
    }

    /**
     * ตรวจสอบค่าน้ำตาลในเลือด
     */
    public String getGlucoseStatus() {
        String fcbg = healthRiskAssessmentInfo.getFcbg();
        String fpg = healthRiskAssessmentInfo.getFpg();

        Double glucoseValue = null;
        String type = "";

        // ใช้ FPG ก่อนถ้ามี
        if (fpg != null && !fpg.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fpg);
                type = "FPG";
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        // ถ้าไม่มี FPG ให้ใช้ FCBG
        if (glucoseValue == null && fcbg != null && !fcbg.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fcbg);
                type = "FCBG";
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        if (glucoseValue == null) {
            return "ไม่มีข้อมูลน้ำตาลในเลือด";
        }

        String status;
        if (glucoseValue < 100) {
            status = "ปกติ";
        } else if (glucoseValue >= 100 && glucoseValue <= 125) {
            status = "เสี่ยงเบาหวาน";
        } else {
            status = "สงสัยเป็นเบาหวาน";
        }

        return String.format("%s: %.0f มก/ดล - %s", type, glucoseValue, status);
    }

    /**
     * ตรวจสอบว่ามีค่าน้ำตาลผิดปกติหรือไม่
     */
    public boolean hasAbnormalGlucose() {
        String fcbg = healthRiskAssessmentInfo.getFcbg();
        String fpg = healthRiskAssessmentInfo.getFpg();

        Double glucoseValue = null;

        if (fpg != null && !fpg.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fpg);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        if (glucoseValue == null && fcbg != null && !fcbg.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fcbg);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        return glucoseValue != null && glucoseValue >= 100;
    }
}