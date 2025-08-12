package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.ScreeningResultCodeDao;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.provider.ScreeningResultCode;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.Log;
import android.app.AlertDialog;
import android.widget.ImageView;
import th.in.ffc.app.form.screening.view.AlcoholRiskGaugeView;
import android.widget.SeekBar;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlcoholFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlcoholFragment extends Fragment {

    // Constants
    private static final String SUBSTANCE_ALCOHOL = "b";
    private static final String TAG = "AlcoholFragment";

    // LiveData and ViewModel
    DrinkingLiveData drinkingLiveData;
    SharedViewModel shareViewModel;

    // Data and callback
    private OnDataPass dataPasser;
    private DrinkingInfo drinkingInfo;

    // UI Components
    private RadioGroup rdoDrinking;
    private RadioGroup rdoDrinkingFrequency;
    private RadioGroup rdoDrinkingAlway;
    RadioButton rdoDrinkingFrequency1;
    RadioButton rdoDrinkingFrequency2;
    RadioButton rdoDrinkingFrequency3;

    // Score display components
    private TextView tvAlcoholScore;
    private TextView tvAlcoholRiskLevel;

    // Flag to prevent loops
    private boolean isUpdatingFromScore = false;



    private ScreeningResultCodeDao screeningResultCodeDao;
    private int currentPersonId = -1;
    private int currentVisitNo = -1;
    private ImageView ivAlcoholInfoButton;
    private AlcoholRiskGaugeView alcoholRiskGauge;
    private TextView tvGaugeEmoji;
    private TextView tvGaugeScore;
    private TextView tvGaugeLevel;
    private TextView tvGaugeCode;
    private TextView tvGaugeRecommendation;
    private SeekBar seekBarGaugeTest;

    public AlcoholFragment() {
        // Required empty public constructor
    }

    public static AlcoholFragment newInstance(String param1, String param2) {
        AlcoholFragment fragment = new AlcoholFragment();
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drinkingLiveData = new DrinkingLiveData();
        shareViewModel = new SharedViewModel();
        screeningResultCodeDao = new ScreeningResultCodeDao(getContext());
    }
    private void initializeGaugeViews(View view) {
        alcoholRiskGauge = view.findViewById(R.id.alcoholRiskGauge);
        tvGaugeEmoji = view.findViewById(R.id.tvGaugeEmoji);
        tvGaugeScore = view.findViewById(R.id.tvGaugeScore);
        tvGaugeLevel = view.findViewById(R.id.tvGaugeLevel);
        tvGaugeCode = view.findViewById(R.id.tvGaugeCode);
        tvGaugeRecommendation = view.findViewById(R.id.tvGaugeRecommendation);

        // สำหรับทดสอบ (สามารถลบออกได้)
        seekBarGaugeTest = view.findViewById(R.id.seekBarGaugeTest);
        setupGaugeTestControls();

        // อัปเดต Gauge ครั้งแรก
        updateGaugeDisplay();
    }
    private void setupGaugeTestControls() {
        if (seekBarGaugeTest != null) {
            seekBarGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && alcoholRiskGauge != null) {
                        alcoholRiskGauge.setScore(progress);
                        AlcoholRiskGaugeView.RiskLevel level = getCurrentRiskLevelFromScore(progress);

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

    /**
     * Show/hide gauge test controls
     */
    public void showGaugeTestControls(boolean show) {
        View layoutGaugeControl = getView().findViewById(R.id.layoutGaugeControl);
        if (layoutGaugeControl != null) {
            layoutGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Reset gauge to zero
     */
    public void resetGauge() {
        if (alcoholRiskGauge != null) {
            alcoholRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }

    /**
     * Update gauge display with current score
     */
    private void updateGaugeDisplay() {
        if (alcoholRiskGauge == null) return;

        int totalScore = getCurrentAlcoholScore();
        AlcoholRiskGaugeView.RiskLevel currentLevel = getCurrentRiskLevelFromScore(totalScore);

        // อัปเดต Gauge
        alcoholRiskGauge.setScore(totalScore);

        // อัปเดตข้อความ
        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(currentLevel.emoji);
        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + totalScore);
        if (tvGaugeLevel != null) {
            tvGaugeLevel.setText(currentLevel.label);
            tvGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvGaugeCode != null) tvGaugeCode.setText(currentLevel.code);

        Log.d(TAG, "Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label);
    }

    /**
     * Get risk level from score for alcohol assessment
     */
    private AlcoholRiskGaugeView.RiskLevel getCurrentRiskLevelFromScore(int score) {
        if (score == 0) {
            return new AlcoholRiskGaugeView.RiskLevel(0, 0, "ไม่ดื่ม", "#27AE60", "😊", "1B600");
        } else if (score >= 1 && score <= 10) {
            return new AlcoholRiskGaugeView.RiskLevel(1, 10, "ไม่ต้องบำบัด (ความเสี่ยงต่ำ)", "#2ECC71", "🙂", "1B602");
        } else if (score >= 11 && score <= 26) {
            return new AlcoholRiskGaugeView.RiskLevel(11, 26, "บำบัดอย่างย่อ (ความเสี่ยงปานกลาง)", "#F39C12", "😟", "1B603");
        } else {
            return new AlcoholRiskGaugeView.RiskLevel(27, 40, "บำบัดเข้มข้น (ความเสี่ยงสูง)", "#E74C3C", "😰", "1B604");
        }
    }

    /**
     * Update gauge with specific score
     */
    public void updateGaugeWithScore(int score) {
        if (alcoholRiskGauge != null) {
            alcoholRiskGauge.setScore(score);
            updateGaugeDisplay();
        }
    }

    /**
     * Get current gauge level
     */
    public AlcoholRiskGaugeView.RiskLevel getCurrentGaugeLevel() {
        if (alcoholRiskGauge != null) {
            return alcoholRiskGauge.getCurrentRiskLevel();
        }
        return getCurrentRiskLevelFromScore(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alcohol, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Initialize data
        drinkingInfo = new DrinkingInfo();

        // Setup listeners
        setupRadioGroupListeners();
        setupInfoButtonListener();


        // Setup ViewModel observers
        setupViewModelObservers();
        initializeGaugeViews(view);
        // Load data
        loadData();

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                currentPersonId = Integer.parseInt(personInfo.getId());
                if (personInfo.getVisitNo() != null && !personInfo.getVisitNo().isEmpty()) {
                    currentVisitNo = Integer.parseInt(personInfo.getVisitNo());
                }
            }
        });
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews(View view) {
        rdoDrinking = view.findViewById(R.id.rdoDrinking);
        rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);

        // Score display components
        tvAlcoholScore = view.findViewById(R.id.tvAlcoholScore);
        tvAlcoholRiskLevel = view.findViewById(R.id.tvAlcoholRiskLevel);

        ivAlcoholInfoButton = view.findViewById(R.id.ivAlcoholInfoButton);
    }
    private void setupInfoButtonListener() {
        // ตั้งค่า listener สำหรับปุ่ม info
        if (ivAlcoholInfoButton != null) {
            ivAlcoholInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlcoholCriteriaDialog();
                }
            });
        }
    }
    private void showAlcoholCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom layout สำหรับ dialog
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_alcohol_criteria, null);

            builder.setView(dialogView);
//            builder.setTitle("เกณฑ์การประเมินความเสี่ยงจากการดื่มแอลกอฮอล์");
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d(TAG, "แสดง Dialog เกณฑ์การประเมินแอลกอฮอล์สำเร็จ");

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());

            // แสดง dialog แบบง่ายหากเกิดข้อผิดพลาด
            showSimpleAlcoholCriteriaDialog();
        }
    }
    private void showSimpleAlcoholCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "🍺 เกณฑ์การประเมินความเสี่ยงจากการดื่มแอลกอฮอล์\n\n" +
                "😊 0 คะแนน: ไม่ดื่ม (1B600)\n" +
                "🔶 ไม่มีความเสี่ยง\n\n" +

                "🙂 1-10 คะแนน: ไม่ต้องบำบัด (1B602)\n" +
                "🔶 ความเสี่ยงต่ำ - ให้คำแนะนำ\n\n" +

                "😟 11-26 คะแนน: บำบัดอย่างย่อ (1B603)\n" +
                "🔶 ความเสี่ยงปานกลาง - ให้คำปรึกษา\n\n" +

                "😰 ≥ 27 คะแนน: บำบัดเข้มข้น (1B604)\n" +
                "🔶 ความเสี่ยงสูง - ส่งต่อผู้เชี่ยวชาญ\n\n" +

                "⚠️ หมายเหตุ: ความเสี่ยงสูงต้องการการแทรกแซงทันที\n" +
                "ควรส่งต่อผู้เชี่ยวชาญและติดตามอย่างใกล้ชิด\n\n";

        builder.setTitle("📈 เกณฑ์การประเมินแอลกอฮอล์")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Setup ViewModel observers
     */
    private void setupViewModelObservers() {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe drinking data changes
        viewModel.getDrinkingMutableLiveData().observe(getViewLifecycleOwner(), drinkingLiveData -> {
            if(drinkingLiveData != null && !isUpdatingFromScore){
                if(drinkingLiveData.getSelectedRdoDriking() != null) {
                    rdoDrinking.check(drinkingLiveData.getSelectedRdoDriking());
                }
                if(drinkingLiveData.getSelectedRdoDrikingFrequency() != null) {
                    rdoDrinkingFrequency.check(drinkingLiveData.getSelectedRdoDrikingFrequency());
                }
                if(drinkingLiveData.getSelectedRdoDrikingAlway() != null) {
                    rdoDrinkingAlway.check(drinkingLiveData.getSelectedRdoDrikingAlway());
                }
            }
        });

        // Observe person info for score calculation and synchronization
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                // คำนวณคะแนนแอลกอฮอล์และซิงค์กับ AssistScoreFragment
                calculateAndSyncAlcoholScore(personInfo.getId());
            }
        });

        // Observe ASSIST score changes (รับค่าจาก AssistScoreFragment)
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null && !isUpdatingFromScore) {
                // Display alcohol score from AssistScoreFragment (tvScoreB)
                if (data.getAlcoholScore() != null) {
                    try {
                        int alcoholScore = Integer.parseInt(data.getAlcoholScore());
                        // แสดงคะแนนจาก AssistScoreFragment ใน tvAlcoholScore
                        syncAlcoholScoreFromAssistFragment(alcoholScore);
                        Log.d(TAG, "ซิงค์คะแนนแอลกอฮอล์จาก AssistScoreFragment: " + alcoholScore);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "ไม่สามารถแปลงคะแนน alcohol เป็นตัวเลขได้: " + data.getAlcoholScore());
                        syncAlcoholScoreFromAssistFragment(0);
                    }
                }

                // Display nicotine score if available
                if (data.getNicotineScore() != null) {
                    try {
                        int nicotineScore = Integer.parseInt(data.getNicotineScore());
                        Log.d(TAG, "คะแนนบุหรี่: " + nicotineScore);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "ไม่สามารถแปลงคะแนนบุหรี่เป็นตัวเลขได้: " + data.getNicotineScore());
                    }
                }
            }
        });
    }

    /**
     * Load drinking data from database
     */
    private void loadData(){
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if(data.getId() != null){
                List<DrinkingInfo> drinkingInfos = sfDrinkingInfoDao.getByPersonId(Integer.valueOf(data.getId()));
                for(DrinkingInfo drinkingInfo : drinkingInfos){
                    Log.d(TAG, "drinking infos: " + drinkingInfo);
                    this.drinkingInfo = drinkingInfo;
                    loadExistingData();
                }

                // Load alcohol score
                loadAlcoholScore(data.getId());

                // ตรวจสอบข้อมูลที่มีอยู่แล้วใน ScreeningResultCode
                if (data.getVisitNo() != null && !data.getVisitNo().isEmpty()) {
                    loadFromScreeningResultCode(Integer.valueOf(data.getId()), Integer.valueOf(data.getVisitNo()));
                }
            }
        });
    }
    public interface AlcoholAssessmentListener {
        void onAssessmentComplete(int score, String riskLevel, boolean isHighRisk);
        void onHighRiskDetected(int score, String recommendation);
        void onDataChanged(int completionPercentage);
        void onSaveSuccess(String message);
        void onSaveError(String error);
    }

    private AlcoholAssessmentListener assessmentListener;

    public void setAssessmentListener(AlcoholAssessmentListener listener) {
        this.assessmentListener = listener;
    }
    private void notifyAssessmentComplete() {
        if (assessmentListener != null && isFormComplete()) {
            int score = getCurrentAlcoholScore();
            String riskLevel = getRiskLevelFromScore();
            boolean isHighRisk = isHighRisk();

            assessmentListener.onAssessmentComplete(score, riskLevel, isHighRisk);

            if (isHighRisk) {
                assessmentListener.onHighRiskDetected(score, getRecommendation());
            }
        }
    }
    public String createExportReport() {
        StringBuilder report = new StringBuilder();

        report.append("ALCOHOL_ASSESSMENT_REPORT").append("\n");
        report.append("TIMESTAMP:").append(System.currentTimeMillis()).append("\n");
        report.append("PERSON_ID:").append(currentPersonId).append("\n");
        report.append("VISIT_NO:").append(currentVisitNo).append("\n");

        if (isFormComplete()) {
            int score = getCurrentAlcoholScore();
            report.append("SCORE:").append(score).append("\n");
            report.append("RISK_LEVEL:").append(ScreeningResultCode.getAlcoholRiskLevel(score)).append("\n");
            report.append("RESULT_CODE:").append(ScreeningResultCode.getAlcoholAnswerResultCode(score)).append("\n");
            report.append("IS_ABNORMAL:").append(ScreeningResultCode.isAlcoholAbnormal(score)).append("\n");
            report.append("IS_HIGH_RISK:").append(ScreeningResultCode.isAlcoholHighRisk(score)).append("\n");
            report.append("COMPLETION:").append(getCompletionPercentage()).append("%\n");

            // ข้อมูลการเลือก
            if (drinkingInfo != null) {
                report.append("DRINKING:").append(drinkingInfo.getDrinking()).append("\n");
                report.append("FREQUENCY:").append(drinkingInfo.getDrinkingFrequency()).append("\n");
                report.append("TREATMENT:").append(drinkingInfo.getDrinkingAlway()).append("\n");
            }
        } else {
            report.append("STATUS:INCOMPLETE").append("\n");
            report.append("COMPLETION:").append(getCompletionPercentage()).append("%\n");
        }

        return report.toString();
    }


    /**
     * Load and display alcohol score from ASSIST (Fixed to prevent infinite loop)
     */
    private void loadAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณคะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Calculate total score for alcohol (substance "b")
            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                if (summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                    int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                    totalScore += questionScore;
                    Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                } else {
                    Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                }
            }

            // Update score display
            updateAlcoholScoreDisplay(totalScore);

            // Update ViewModel safely (prevent loop)
            updateAlcoholScoreInViewModelSafe(totalScore, personInfoId);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม: " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Update alcohol score and risk level
     * @param score Alcohol score from ASSIST
     */
    private void updateAlcoholScore(int score) {
        // Display score in TextView with color
        if (tvAlcoholScore != null) {
            tvAlcoholScore.setText(String.valueOf(score));

            // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
            if (score == 0) {
                // ไม่ดื่ม - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 1 && score <= 10) {
                // ความเสี่ยงต่ำ - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 11 && score <= 26) {
                // ความเสี่ยงปานกลาง - สีส้ม
                tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 27) {
                // ความเสี่ยงสูง - สีแดง
                tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#700e03"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            }

            Log.d(TAG, "อัพเดตคะแนนแอลกอฮอล์ใน UI: " + score);
        }

        // Update risk level with dynamic colors
        if (tvAlcoholRiskLevel != null) {
            String emoji = getRiskEmoji(score);
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์ พร้อม emoji
            if (score == 0) {
                riskLevel = emoji + " ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 1 && score <= 10) {
                riskLevel = emoji + " ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 11 && score <= 26) {
                riskLevel = emoji + " บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 27) {
                riskLevel = emoji + " บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "😐 ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);
            Log.d(TAG, "อัพเดตระดับความเสี่ยง: " + riskLevel);
        }

        // Auto-select radio buttons based on score (only if no existing data)
        autoSelectBasedOnScore(score);
        updateGaugeWithScore(score);
        Log.d(TAG, "เลือก radio button อัตโนมัติตามคะแนน: " + score);
    }

    /**
     * Calculate and sync alcohol score with AssistScoreFragment
     */
    private void calculateAndSyncAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณและซิงค์คะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Calculate total score for alcohol (substance "b")
            for (String question : questions) {
                try {
                    Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                            Integer.valueOf(personInfoId), question);

                    if (summaryMap != null && summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                        int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                        totalScore += questionScore;
                        Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                    } else {
                        Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนน " + question + ": " + e.getMessage());
                }
            }

            // อัพเดต UI ใน AlcoholFragment
            updateAlcoholScore(totalScore);

            // ส่งคะแนนไปยัง ViewModel เพื่อให้ AssistScoreFragment ใช้แสดงใน tvScoreB
            updateAlcoholScoreInViewModel(totalScore);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม (ซิงค์): " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการคำนวณและซิงค์คะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Sync alcohol score from AssistScoreFragment (tvScoreB) to tvAlcoholScore
     */
    private void syncAlcoholScoreFromAssistFragment(int score) {
        // แสดงคะแนนจาก AssistScoreFragment ใน tvAlcoholScore
        if (tvAlcoholScore != null) {
            String currentScore = tvAlcoholScore.getText().toString();
            if (!currentScore.equals(String.valueOf(score))) {
                tvAlcoholScore.setText(String.valueOf(score));

                // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
                if (score == 0) {
                    tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 1 && score <= 10) {
                    tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 11 && score <= 26) {
                    tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 27) {
                    tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                }
                updateGaugeWithScore(score);
                Log.d(TAG, "ซิงค์คะแนนจาก AssistScoreFragment ใน tvAlcoholScore: " + score);
            }
        }

        // อัพเดตระดับความเสี่ยงและสีฟอนต์
        if (tvAlcoholRiskLevel != null) {
            String emoji = getRiskEmoji(score);
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์ พร้อม emoji
            if (score == 0) {
                riskLevel = emoji + " ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 1 && score <= 10) {
                riskLevel = emoji + " ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 11 && score <= 26) {
                riskLevel = emoji + " บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 27) {
                riskLevel = emoji + " บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "😐 ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);
            Log.d(TAG, "อัพเดตระดับความเสี่ยงจากการซิงค์: " + riskLevel);
        }

        // เลือก radio button อัตโนมัติตามคะแนน (เฉพาะกรณีที่ยังไม่มีข้อมูลเดิม)
        autoSelectBasedOnScore(score);
        Log.d(TAG, "เลือก radio button อัตโนมัติจากการซิงค์: " + score);
    }
    /**
     * Calculate and update alcohol score directly from database (เก็บไว้เผื่อใช้งานอื่น)
     */
    private void calculateAndUpdateAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณคะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Get score from each question for alcohol
            for (String question : questions) {
                try {
                    Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                            Integer.valueOf(personInfoId), question);

                    if (summaryMap != null && summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                        int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                        totalScore += questionScore;
                        Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                    } else {
                        Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนน " + question + ": " + e.getMessage());
                }
            }

            // Update UI
            updateAlcoholScore(totalScore);

            // Update ViewModel to sync with AssistScoreFragment
            updateAlcoholScoreInViewModel(totalScore);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม: " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการคำนวณคะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Update alcohol score in ViewModel (ส่งข้อมูลไป AssistScoreFragment)
     */
    private void updateAlcoholScoreInViewModel(int score) {
        try {
            // เพื่อป้องกัน infinite loop ให้ใช้ flag
            isUpdatingFromScore = true;

            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            // สร้าง AssistScore object ใหม่หรือใช้ที่มีอยู่
            AssistScore assistScore = new AssistScore();

            // ตั้งค่าข้อมูลพื้นฐาน
            viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
                if (personInfo != null && personInfo.getId() != null) {
                    assistScore.setPersonId(personInfo.getId());
                    assistScore.setAlcoholScore(String.valueOf(score));

                    // รักษาคะแนนบุหรี่ที่มีอยู่แล้ว (ถ้ามี)
                    AssistScore currentScore = viewModel.getAssistScoreMutableLiveData().getValue();
                    if (currentScore != null && currentScore.getNicotineScore() != null) {
                        assistScore.setNicotineScore(currentScore.getNicotineScore());
                    }

                    // อัพเดตใน ViewModel
                    viewModel.setAssistScoreMutableLiveData(assistScore);

                    Log.d(TAG, "ส่งคะแนนแอลกอฮอล์ไป AssistScoreFragment ผ่าน ViewModel: " + score);

                    // ปิด observer หลังจากใช้งานแล้ว
                    viewModel.getPersonInfoLiveDataMutableLiveData().removeObservers(this);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการส่งข้อมูลไป ViewModel: " + e.getMessage());
        } finally {
            isUpdatingFromScore = false;
        }
    }

    /**
     * Update alcohol score in ViewModel safely (prevent infinite loop)
     */
    private void updateAlcoholScoreInViewModelSafe(int score, String personId) {
        try {
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            AssistScore assistScore = new AssistScore();
            assistScore.setPersonId(personId);
            assistScore.setAlcoholScore(String.valueOf(score));

            viewModel.setAssistScoreMutableLiveData(assistScore);

            Log.d(TAG, "อัพเดตคะแนนแอลกอฮอล์ใน ViewModel (Safe): " + score);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการอัพเดต ViewModel (Safe): " + e.getMessage());
        }
    }

    /**
     * Update score display and risk level (improved to prevent loop)
     */
    private void updateAlcoholScoreDisplay(int score) {
        // Check if score changed to prevent duplicate updates
        if (tvAlcoholScore != null) {
            String currentScore = tvAlcoholScore.getText().toString();
            if (currentScore.equals(String.valueOf(score))) {
                Log.d(TAG, "คะแนนไม่เปลี่ยนแปลง: " + score);
                return;
            }
            tvAlcoholScore.setText(String.valueOf(score));

            // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
            if (score == 0) {
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 1 && score <= 10) {
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 11 && score <= 26) {
                tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 27) {
                tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            }
        }

        if (tvAlcoholRiskLevel != null) {
            String emoji = getRiskEmoji(score);
            String riskLevel;

            // Determine risk level และเปลี่ยนสีฟอนต์ พร้อม emoji
            if (score == 0) {
                riskLevel = emoji + " ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
                selectDrinkingRadioButtonIfNeeded(0);
            } else if (score >= 1 && score <= 10) {
                riskLevel = emoji + " ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
                selectDrinkingRadioButtonIfNeeded(1);
            } else if (score >= 11 && score <= 26) {
                riskLevel = emoji + " บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
                selectDrinkingRadioButtonIfNeeded(2);
            } else if (score >= 27) {
                riskLevel = emoji + " บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
                selectDrinkingRadioButtonIfNeeded(3);
            } else {
                riskLevel = "😐 ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);
            updateGaugeDisplay();
            Log.d(TAG, "อัพเดตระดับความเสี่ยงและเลือก RadioButton: " + riskLevel + " (คะแนน: " + score + ")");
        }
    }
    /**
     * Select radio button only when no existing data (prevent loop)
     */
    private void selectDrinkingRadioButtonIfNeeded(int drinkingLevel) {
        boolean hasExistingData = false;

        // ตรวจสอบว่ามีข้อมูลเดิมหรือไม่
        if (drinkingInfo != null && drinkingInfo.getDrinking() != null &&
                !drinkingInfo.getDrinking().equals("0") && !drinkingInfo.getDrinking().isEmpty()) {
            hasExistingData = true;
            Log.d(TAG, "มีข้อมูลการดื่มอยู่แล้ว: " + drinkingInfo.getDrinking());
        }

        if (rdoDrinking != null && rdoDrinking.getCheckedRadioButtonId() != -1) {
            hasExistingData = true;
            Log.d(TAG, "มี RadioButton เลือกอยู่แล้ว: " + rdoDrinking.getCheckedRadioButtonId());
        }

        // ถ้าไม่มีข้อมูลเดิม ให้เลือกอัตโนมัติตามคะแนน
        if (!hasExistingData) {
            selectDrinkingRadioButton(drinkingLevel);
            Log.d(TAG, "เลือก RadioButton อัตโนมัติตามคะแนน: " + drinkingLevel);
        } else {
            Log.d(TAG, "ไม่เลือก RadioButton เพราะมีข้อมูลอยู่แล้ว");
        }
    }
    /**
     * Select radio button based on drinking level (fixed to prevent loop)
     */
    private void selectDrinkingRadioButton(int drinkingLevel) {
        try {
            isUpdatingFromScore = true; // Prevent listener triggers

            switch (drinkingLevel) {
                case 0: // Score 0 = No drinking
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking1); // ไม่ดื่ม
                        if (rdoDrinkingFrequency != null) {
                            rdoDrinkingFrequency.clearCheck();
                        }
                        if (rdoDrinkingAlway != null) {
                            rdoDrinkingAlway.clearCheck();
                        }
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("1");
                        drinkingInfo.setDrinkingFrequency("0");
                        drinkingInfo.setDrinkingAlway("0");
                    }
                    Log.d(TAG, "เลือก: ไม่ดื่ม (คะแนน 0)");
                    break;

                case 1: // Score 1-10 = Drink + Rarely + ไม่ต้องบำบัด
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency1); // นาน ๆ ครั้ง
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway1); // ไม่ต้องบำบัด
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("1");
                        drinkingInfo.setDrinkingAlway("1");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + นาน ๆ ครั้ง + ไม่ต้องบำบัด (คะแนน 1-10)");
                    break;

                case 2: // Score 11-26 = Drink + Sometimes + บำบัดอย่างย่อ
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency2); // เป็นครั้งคราว
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway2); // บำบัดอย่างย่อ
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("2");
                        drinkingInfo.setDrinkingAlway("2");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + เป็นครั้งคราว + บำบัดอย่างย่อ (คะแนน 11-26)");
                    break;

                case 3: // Score 27+ = Drink + Always + บำบัดเข้มข้น
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency3); // เป็นประจำ
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway3); // บำบัดเข้มข้น
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("3");
                        drinkingInfo.setDrinkingAlway("3");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + เป็นประจำ + บำบัดเข้มข้น (คะแนน 27+)");
                    break;

                default:
                    clearAllSelections();
                    break;
            }

            // Send data through dataPasser
            if (dataPasser != null && drinkingInfo != null) {
                dataPasser.onDrinkingInfo(drinkingInfo);
            }

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการเลือก Radio Button การดื่ม: " + e.getMessage());
        } finally {
            isUpdatingFromScore = false; // Re-enable listeners
        }
    }

    /**
     * Helper methods for assessment and UI updates
     */
    private RiskAssessment assessRisk(int score) {
        if (score == 0) {
            return new RiskAssessment("ไม่ดื่ม", R.color.light_green);
        } else if (score >= 1 && score <= 10) {
            return new RiskAssessment("ไม่ต้องบำบัด", R.color.light_green);
        } else if (score >= 11 && score <= 26) {
            return new RiskAssessment("บำบัดอย่างย่อ", R.color.light_yellow);
        } else if (score >= 27) {
            return new RiskAssessment("บำบัดเข้มข้น", R.color.light_red);
        } else {
            return new RiskAssessment("ยังไม่ได้ประเมิน", android.R.color.transparent);
        }
    }

    private void updateScoreColors(RiskAssessment risk) {
        if (tvAlcoholRiskLevel != null && tvAlcoholScore != null) {
            if (risk.backgroundColor != android.R.color.transparent) {
                tvAlcoholRiskLevel.setBackgroundResource(risk.backgroundColor);
//                tvAlcoholScore.setBackgroundResource(risk.backgroundColor);
            }
        }
    }

    private boolean shouldAutoSelectBasedOnScore() {
        return drinkingInfo == null ||
                drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().equals("0") ||
                drinkingInfo.getDrinking().isEmpty();
    }

    private void autoSelectBasedOnScore(int score) {
        Log.d(TAG, "autoSelectBasedOnScore: คะแนน = " + score);

        if (score == 0) {
            selectDrinkingRadioButton(0); // ไม่ดื่ม
        } else if (score >= 1 && score <= 10) {
            selectDrinkingRadioButton(1); // ดื่ม + นาน ๆ ครั้ง + ไม่ต้องบำบัด
        } else if (score >= 11 && score <= 26) {
            selectDrinkingRadioButton(2); // ดื่ม + ครั้งคราว + บำบัดอย่างย่อ
        } else if (score >= 27) {
            selectDrinkingRadioButton(3); // ดื่ม + เป็นประจำ + บำบัดเข้มข้น
        }
    }

    private void displayScoreError() {
        if (tvAlcoholScore != null) {
            tvAlcoholScore.setText("-");
            tvAlcoholScore.setBackgroundResource(R.color.light_gray);
            tvAlcoholScore.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (tvAlcoholRiskLevel != null) {
            tvAlcoholRiskLevel.setText("😕 ไม่สามารถคำนวณได้");
            tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
        }
        if (alcoholRiskGauge != null) {
            alcoholRiskGauge.setScore(0);
        }
    }


    /**
     * Clear selections helper methods
     */
    private void clearNestedSelections() {
        if (rdoDrinkingFrequency != null) {
            rdoDrinkingFrequency.clearCheck();
        }
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinkingFrequency("0");
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    private void clearAlwaySelection() {
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    private void clearAllSelections() {
        if (rdoDrinking != null) {
            rdoDrinking.clearCheck();
        }
        if (rdoDrinkingFrequency != null) {
            rdoDrinkingFrequency.clearCheck();
        }
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinking("0");
            drinkingInfo.setDrinkingFrequency("0");
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    /**
     * Select existing radio button based on risk level
     * @param level 1=ไม่ต้องบำบัด, 2=บำบัดอย่างย่อ, 3=บำบัดเข้มข้น, 0=ไม่เลือก
     */
    private void selectExistingRadioButton(int level) {
        View rootView = getView();
        if (rootView != null) {
            RadioButton rdoTreatmentLow = null;
            RadioButton rdoTreatmentMedium = null;
            RadioButton rdoTreatmentHigh = null;
            RadioGroup rdoTreatmentGroup = null;

            try {
                // Find RadioButton by text content
                rdoTreatmentLow = findRadioButtonByText(rootView, "1B602");
                rdoTreatmentMedium = findRadioButtonByText(rootView, "1B603");
                rdoTreatmentHigh = findRadioButtonByText(rootView, "1B604");

                // Find RadioGroup containing these RadioButtons
                if (rdoTreatmentLow != null) {
                    ViewGroup parent = (ViewGroup) rdoTreatmentLow.getParent();
                    if (parent instanceof RadioGroup) {
                        rdoTreatmentGroup = (RadioGroup) parent;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "ไม่สามารถหา RadioButton ได้: " + e.getMessage());
            }

            // Select RadioButton based on score
            String selectedLevel = "";
            RadioButton targetRadioButton = null;

            switch (level) {
                case 1: // 0-10 score
                    selectedLevel = "1B602 ระดับเสี่ยงต่ำ(0-10) >>> 1B610 การให้คำแนะนำ (brief advice)";
                    targetRadioButton = rdoTreatmentLow;
                    break;
                case 2: // 11-26 score
                    selectedLevel = "1B603 ระดับเสี่ยงปานกลาง(11-26) >>> 1B611 การให้คำปรึกษาแบบสั้น (brief counseling)";
                    targetRadioButton = rdoTreatmentMedium;
                    break;
                case 3: // 27+ score
                    selectedLevel = "1B604 ระดับเสี่ยงสูง(คะแนนตั้งแต่ 27 ขึ้นไป) >>> 1B612 การส่งต่อเพื่อรับการประเมินและการบำบัดโดยผู้เชี่ยวชาญ(refer)";
                    targetRadioButton = rdoTreatmentHigh;
                    break;
                default:
                    selectedLevel = "ยังไม่ได้ประเมิน";
                    break;
            }

            Log.d(TAG, "ระดับการบำบัดที่แนะนำ: " + selectedLevel);

            // Select RadioButton
            if (targetRadioButton != null && rdoTreatmentGroup != null) {
                rdoTreatmentGroup.check(targetRadioButton.getId());
                Log.d(TAG, "เลือก RadioButton สำเร็จ");
            } else {
                Log.w(TAG, "ไม่พบ RadioButton ที่ต้องการเลือก");
            }
        }
    }

    /**
     * Find RadioButton by text content
     */
    private RadioButton findRadioButtonByText(View rootView, String searchText) {
        if (rootView instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) rootView;
            String text = radioButton.getText().toString();
            if (text.contains(searchText)) {
                return radioButton;
            }
        } else if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton found = findRadioButtonByText(group.getChildAt(i), searchText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Load existing data from DrinkingInfo
     */
    private void loadExistingData() {
        if (this.drinkingInfo == null) return;

        isUpdatingFromScore = true; // Prevent listeners during data loading

        try {
            // Set drinking selection
            switch (drinkingInfo.getDrinking()) {
                case "1":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking1)).setChecked(true);
                    }
                    break;
                case "2":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking2)).setChecked(true);
                    }
                    break;
                case "3":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking3)).setChecked(true);
                    }
                    // Load nested selections only if "3" is selected
                    if (drinkingInfo.getDrinkingFrequency() != null) {
                        switch (drinkingInfo.getDrinkingFrequency()) {
                            case "1":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency1)).setChecked(true);
                                }
                                break;
                            case "2":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency2)).setChecked(true);
                                }
                                break;
                            case "3":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency3)).setChecked(true);
                                }
                                // Load "always" selection only if frequency is "3"
                                if (drinkingInfo.getDrinkingAlway() != null) {
                                    switch (drinkingInfo.getDrinkingAlway()) {
                                        case "1":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway1)).setChecked(true);
                                            }
                                            break;
                                        case "2":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway2)).setChecked(true);
                                            }
                                            break;
                                        case "3":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway3)).setChecked(true);
                                            }
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดข้อมูลเดิม: " + e.getMessage());
        } finally {
            isUpdatingFromScore = false; // Re-enable listeners
        }
    }

    /**
     * Set drinking info from external source
     */
    public void setDrinkingInfo(DrinkingInfo info) {
        this.drinkingInfo = info;
        loadExistingData();
    }

    /**
     * Risk assessment helper class
     */
    private static class RiskAssessment {
        String description;
        int backgroundColor;

        RiskAssessment(String description, int backgroundColor) {
            this.description = description;
            this.backgroundColor = backgroundColor;
        }
    }

    /**
     * Inner class for StressDepression9q LiveData Model
     */
    public static class StressDepression9qLiveDataModel extends ViewModel {
        StressDepression9qLiveData stressDepression9qLiveData;

        public StressDepression9qLiveData getStressDepression9qLiveData() {
            return stressDepression9qLiveData;
        }

        public void setStressDepression9qLiveData(StressDepression9qLiveData stressDepression9qLiveData) {
            this.stressDepression9qLiveData = stressDepression9qLiveData;
        }
    }
    // เพิ่มเมธอด validation ใน AlcoholFragment class

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
     */
    public boolean isFormComplete() {
        // ตรวจสอบว่าได้เลือกสถานะการดื่มแล้วหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            return false;
        }

        // ถ้าเลือก "ดื่ม" (option 3) ต้องเลือกความถี่ด้วย
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                return false;
            }

            // ถ้าเลือก "ดื่มเป็นประจำ" (option 3 ใน frequency) ต้องเลือกการบำบัดด้วย
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าได้เลือกสถานะการดื่มหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกสถานะการดื่มสุรา");
            return message.toString();
        }

        // ถ้าเลือก "ดื่ม" แต่ยังไม่ได้เลือกความถี่
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกความถี่ในการดื่ม");
                return message.toString();
            }

            // ถ้าเลือก "ดื่มเป็นประจำ" แต่ยังไม่ได้เลือกการบำบัด
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกการให้คำแนะนำ/บำบัด");
                    return message.toString();
                }
            }
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าได้เลือกสถานะการดื่มหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            message.append("แบบประเมินการดื่มสุรา:\n");
            message.append("• ยังไม่ได้เลือกสถานะการดื่มสุรา (ไม่ดื่ม/เคยดื่ม/ดื่ม)");
            return message.toString();
        }

        // ตรวจสอบการเลือกความถี่ (สำหรับผู้ที่ดื่ม)
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                message.append("แบบประเมินการดื่มสุรา:\n");
                message.append("• เลือกว่า 'ดื่ม' แล้ว\n");
                message.append("• ยังไม่ได้เลือกความถี่ในการดื่ม (นานๆครั้ง/ครั้งคราว/เป็นประจำ)");
                return message.toString();
            }

            // ตรวจสอบการให้คำแนะนำ/บำบัด (สำหรับผู้ที่ดื่มเป็นประจำ)
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    message.append("แบบประเมินการดื่มสุรา:\n");
                    message.append("• เลือกว่า 'ดื่ม' แล้ว\n");
                    message.append("• เลือกว่า 'ดื่มเป็นประจำ' แล้ว\n");
                    message.append("• ยังไม่ได้เลือกการให้คำแนะนำ/บำบัด");
                    return message.toString();
                }
            }
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * รีเซ็ตฟอร์มกลับเป็นค่าเริ่มต้น
     */
    public void resetForm() {
        isUpdatingFromScore = true;
        try {
            // ล้างการเลือกทั้งหมด
            if (rdoDrinking != null) {
                rdoDrinking.clearCheck();
            }
            if (rdoDrinkingFrequency != null) {
                rdoDrinkingFrequency.clearCheck();
            }
            if (rdoDrinkingAlway != null) {
                rdoDrinkingAlway.clearCheck();
            }

            // รีเซ็ต drinkingInfo
            drinkingInfo = new DrinkingInfo();

            // รีเซ็ตคะแนนและระดับความเสี่ยง
            if (tvAlcoholScore != null) {
                tvAlcoholScore.setText("-");
                tvAlcoholScore.setBackgroundResource(R.color.light_gray);
                tvAlcoholScore.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            if (tvAlcoholRiskLevel != null) {
                tvAlcoholRiskLevel.setText("ยังไม่ได้ประเมิน");
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            // ล้างข้อมูลใน LiveData
            if (drinkingLiveData != null) {
                drinkingLiveData.setSelectedRdoDriking(null);
                drinkingLiveData.setSelectedRdoDrikingFrequency(null);
                drinkingLiveData.setSelectedRdoDrikingAlway(null);
            }
            resetGauge();
        } finally {
            isUpdatingFromScore = false;
        }
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (drinkingInfo == null) {
            return false;
        }

        return (drinkingInfo.getDrinking() != null && !drinkingInfo.getDrinking().isEmpty() && !drinkingInfo.getDrinking().equals("0")) ||
                (drinkingInfo.getDrinkingFrequency() != null && !drinkingInfo.getDrinkingFrequency().isEmpty() && !drinkingInfo.getDrinkingFrequency().equals("0")) ||
                (drinkingInfo.getDrinkingAlway() != null && !drinkingInfo.getDrinkingAlway().isEmpty() && !drinkingInfo.getDrinkingAlway().equals("0"));
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            return 0;
        }

        // หากเลือก "ไม่ดื่ม" หรือ "เคยดื่ม" ถือว่าครบ 100%
        if ("1".equals(drinkingInfo.getDrinking()) || "2".equals(drinkingInfo.getDrinking())) {
            return 100;
        }

        // หากเลือก "ดื่ม" ต้องตรวจสอบขั้นตอนต่อไป
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                return 33; // กรอก 1/3
            }

            // หากเลือก "นานๆครั้ง" หรือ "ครั้งคราว" ถือว่าครบ 100%
            if ("1".equals(drinkingInfo.getDrinkingFrequency()) || "2".equals(drinkingInfo.getDrinkingFrequency())) {
                return 100;
            }

            // หากเลือก "เป็นประจำ" ต้องเลือกการบำบัดด้วย
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    return 66; // กรอก 2/3
                } else {
                    return 100; // กรอกครบทุกขั้นตอน
                }
            }
        }

        return 0;
    }

    /**
     * แสดงสถานะการกรอกข้อมูล
     */
    public void showCompletionStatus() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            String riskInfo = getRiskLevelWithEmoji();
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + riskInfo;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d(TAG, "Completion Status: " + message);
    }

    /**
     * ดึงข้อมูลที่กรอกแล้ว
     */
    public DrinkingInfo getFormData() {
        return this.drinkingInfo;
    }

    /**
     * ตรวจสอบระดับความเสี่ยงจากคะแนน
     */
    public String getRiskLevelFromScore() {
        if (tvAlcoholScore == null || tvAlcoholScore.getText().toString().equals("-")) {
            return "ยังไม่ได้ประเมิน";
        }

        try {
            int score = Integer.parseInt(tvAlcoholScore.getText().toString());
            String riskLevel = ScreeningResultCode.getAlcoholRiskLevel(score);

            // แปลงเป็นภาษาไทย
            switch (riskLevel) {
                case ScreeningResultCode.RISK_NONE:
                    return "ไม่ดื่ม";
                case ScreeningResultCode.RISK_LOW:
                    return "ไม่ต้องบำบัด (ความเสี่ยงต่ำ)";
                case ScreeningResultCode.RISK_MEDIUM:
                    return "บำบัดอย่างย่อ (ความเสี่ยงปานกลาง)";
                case ScreeningResultCode.RISK_HIGH:
                    return "บำบัดเข้มข้น (ความเสี่ยงสูง)";
                default:
                    return "ยังไม่ได้ประเมิน";
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "ไม่สามารถแปลงคะแนนเป็นตัวเลขได้");
            return "ยังไม่ได้ประเมิน";
        }
    }

    /**
     * ตรวจสอบว่าข้อมูลที่กรอกสอดคล้องกับคะแนนหรือไม่
     */
    public boolean isDataConsistentWithScore() {
        if (tvAlcoholScore == null || tvAlcoholScore.getText().toString().equals("-")) {
            return true; // ถ้ายังไม่มีคะแนน ถือว่าสอดคล้อง
        }

        try {
            int score = Integer.parseInt(tvAlcoholScore.getText().toString());

            // ตรวจสอบความสอดคล้อง
            if (score == 0 && !"1".equals(drinkingInfo.getDrinking())) {
                Log.w(TAG, "คะแนน 0 แต่ไม่ได้เลือก 'ไม่ดื่ม'");
                return false;
            }

            if (score > 0 && "1".equals(drinkingInfo.getDrinking())) {
                Log.w(TAG, "มีคะแนนแต่เลือก 'ไม่ดื่ม'");
                return false;
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "ไม่สามารถตรวจสอบความสอดคล้องได้");
        }

        return true;
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
    public boolean saveToScreeningResultCode(int personId, int visitno, String userCreate) {
        try {
            if (!isFormComplete()) {
                Log.e(TAG, "ไม่สามารถบันทึกได้ - ข้อมูลไม่ครบถ้วน");
                return false;
            }

            // สร้าง ScreeningResultData
            ScreeningResultCodeDao.ScreeningResultData data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_ALCOHOL_ANSWER_SCREENING; // "ALCOHOL"
            data.totalScore = getCurrentAlcoholScore();
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode และ resultDescription ตามคะแนนแอลกอฮอล์
            setResultCodeAndDescriptionAlcohol(data, data.totalScore);

            // กำหนด riskLevel และ isAbnormal
            setRiskLevelAndAbnormalAlcohol(data, data.totalScore);

            // กำหนดคำแนะนำ
            data.recommendation = getRecommendationAlcohol(data.totalScore);

            // บันทึกข้อมูล
            screeningResultCodeDao.saveScreeningResult(data);

            data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_ALCOHOL_ADVICE_SCREENING; // "ALCOHOL"
            data.totalScore = getCurrentAlcoholScore();
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode และ resultDescription ตามคะแนนแอลกอฮอล์
            setAnswerResultCodeAndDescriptionAlcohol(data, data.totalScore);

            // กำหนด riskLevel และ isAbnormal
//            setRiskLevelAndAbnormalAlcohol(data, data.totalScore);

            // กำหนดคำแนะนำ
//            data.recommendation = getRecommendationAlcohol(data.totalScore);

            // บันทึกข้อมูล
            Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d(TAG, "บันทึกผลการประเมินแอลกอฮอล์สำเร็จ: " + result.toString());
                Log.d(TAG, "รายละเอียด: personId=" + personId + ", visitno=" + visitno +
                        ", score=" + data.totalScore + ", resultCode=" + data.resultCode +
                        ", riskLevel=" + data.riskLevel);
                return true;
            } else {
                Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผลการประเมินแอลกอฮอล์");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกผลการประเมินแอลกอฮอล์: " + e.getMessage());
            return false;
        }
    }

    /**
     * กำหนด resultCode และ resultDescription ตามคะแนนแอลกอฮอล์
     */
    private void setResultCodeAndDescriptionAlcohol(ScreeningResultCodeDao.ScreeningResultData data, int score) {
        data.resultCode = ScreeningResultCode.getAlcoholAdviceResultCode(score);
        data.resultDescription = ScreeningResultCode.getAlcoholResultDescription(score);
    }

    private void setAnswerResultCodeAndDescriptionAlcohol(ScreeningResultCodeDao.ScreeningResultData data, int score) {
        data.resultCode = ScreeningResultCode.getAlcoholAnswerResultCode(score);
        data.resultDescription = ScreeningResultCode.getAlcoholResultDescription(score);
    }


    /**
     * กำหนด riskLevel และ isAbnormal ตามคะแนนแอลกอฮอล์
     */
    private void setRiskLevelAndAbnormalAlcohol(ScreeningResultCodeDao.ScreeningResultData data, int score) {
        data.riskLevel = ScreeningResultCode.getAlcoholRiskLevel(score);
        data.isAbnormal = ScreeningResultCode.isAlcoholAbnormal(score);
    }

    /**
     * ดึงคำแนะนำตามคะแนนแอลกอฮอล์
     */
    private String getRecommendationAlcohol(int score) {
        return ScreeningResultCode.getAlcoholRecommendation(score);
    }

    /**
     * ดึงคะแนนแอลกอฮอล์ปัจจุบัน
     */
    public int getCurrentAlcoholScore() {
        if (tvAlcoholScore != null && !tvAlcoholScore.getText().toString().equals("-")) {
            try {
                return Integer.parseInt(tvAlcoholScore.getText().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "ไม่สามารถแปลงคะแนนแอลกอฮอล์เป็นตัวเลขได้");
            }
        }
        return 0;
    }

    /**
     * โหลดข้อมูลจาก ScreeningResultCode
     */
    public void loadFromScreeningResultCode(int personId, int visitno) {
        try {
            ScreeningResultCodeDao.ScreeningResultData existingData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_ALCOHOL_ANSWER_SCREENING);

            if (existingData != null) {
                Log.d(TAG, "พบข้อมูลการประเมินแอลกอฮอล์เดิม: คะแนน=" + existingData.totalScore +
                        ", ผลการประเมิน=" + existingData.resultDescription);

//                Toast.makeText(getContext(),
//                        "โหลดข้อมูลการประเมินแอลกอฮอล์เดิม: " + existingData.resultDescription,
//                        Toast.LENGTH_SHORT).show();

                // อัพเดทการแสดงผลด้วยคะแนนที่โหลดมา
                updateAlcoholScoreDisplay(existingData.totalScore);
            } else {
                Log.d(TAG, "ไม่พบข้อมูลการประเมินแอลกอฮอล์เดิม");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดข้อมูลการประเมินแอลกอฮอล์: " + e.getMessage());
        }
    }
    private String getRiskEmoji(int score) {
        if (score == 0) {
            return "😊"; // ไม่ดื่ม - หน้ายิ้ม
        } else if (score >= 1 && score <= 10) {
            return "🙂"; // ความเสี่ยงต่ำ - หน้ายิ้มเบาๆ
        } else if (score >= 11 && score <= 26) {
            return "😟"; // ความเสี่ยงปานกลาง - หน้ากังวล
        } else if (score >= 27) {
            return "😰"; // ความเสี่ยงสูง - หน้าตกใจ/กังวลมาก
        } else {
            return "😐"; // ยังไม่ได้ประเมิน - หน้าเฉยๆ
        }
    }
    public String getRiskLevelWithEmoji() {
        if (tvAlcoholScore == null || tvAlcoholScore.getText().toString().equals("-")) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        try {
            int score = Integer.parseInt(tvAlcoholScore.getText().toString());
            String emoji = getRiskEmoji(score);
            String riskLevel = ScreeningResultCode.getAlcoholRiskLevel(score);

            // แปลงเป็นภาษาไทยพร้อม emoji
            switch (riskLevel) {
                case ScreeningResultCode.RISK_NONE:
                    return emoji + " ไม่ดื่ม";
                case ScreeningResultCode.RISK_LOW:
                    return emoji + " ไม่ต้องบำบัด (ความเสี่ยงต่ำ)";
                case ScreeningResultCode.RISK_MEDIUM:
                    return emoji + " บำบัดอย่างย่อ (ความเสี่ยงปานกลาง)";
                case ScreeningResultCode.RISK_HIGH:
                    return emoji + " บำบัดเข้มข้น (ความเสี่ยงสูง)";
                default:
                    return "😐 ยังไม่ได้ประเมิน";
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "ไม่สามารถแปลงคะแนนเป็นตัวเลขได้");
            return "😐 ยังไม่ได้ประเมิน";
        }
    }

    /**
     * ตรวจสอบว่ามีข้อมูลเดิมหรือไม่
     */
    public boolean hasExistingData(int personId, int visitno) {
        try {
            ScreeningResultCodeDao.ScreeningResultData existingData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_ALCOHOL_ANSWER_SCREENING);
            return existingData != null;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูลเดิม: " + e.getMessage());
            return false;
        }
    }

    /**
     * แสดงผลการบันทึก
     */
    public void showSaveResult(boolean success, String message) {
        if (success) {
            Toast.makeText(getContext(),
                    "✅ บันทึกผลการประเมินแอลกอฮอล์สำเร็จ",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "❌ เกิดข้อผิดพลาดในการบันทึก: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ตรวจสอบว่าเป็นความเสี่ยงสูงหรือไม่
     */
    public boolean isHighRisk() {
        int score = getCurrentAlcoholScore();
        return ScreeningResultCode.isAlcoholHighRisk(score);
    }

    /**
     * ดึงสถิติการประเมิน
     */
    public ScreeningResultCodeDao.ScreeningStatistics getStatistics() {
        try {
            return screeningResultCodeDao.getStatisticsByType(ScreeningResultCode.TYPE_ALCOHOL_ANSWER_SCREENING);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติแอลกอฮอล์: " + e.getMessage());
            return null;
        }
    }

    /**
     * แสดงสถิติการประเมิน
     */
    public void showStatistics() {
        ScreeningResultCodeDao.ScreeningStatistics stats = getStatistics();
        if (stats != null) {
            String message = String.format(
                    "สถิติการประเมินแอลกอฮอล์:\n" +
                            "จำนวนทั้งหมด: %d ครั้ง\n" +
                            "ปกติ: %d ครั้ง\n" +
                            "ผิดปกติ: %d ครั้ง\n" +
                            "คะแนนเฉลี่ย: %.1f\n" +
                            "คะแนนสูงสุด: %d\n" +
                            "คะแนนต่ำสุด: %d",
                    stats.totalCount, stats.normalCount, stats.abnormalCount,
                    stats.averageScore, stats.maxScore, stats.minScore
            );

            Log.d(TAG, message);
        }
    }

    /**
     * ดึงข้อความสรุปผลการประเมิน
     */
    public String getAssessmentSummary() {
        if (!isFormComplete()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        int score = getCurrentAlcoholScore();
        String emoji = getRiskEmoji(score);
        String riskLevel = getRiskLevelFromScore();
        String resultCode = ScreeningResultCode.getAlcoholAnswerResultCode(score);

        return String.format("%s คะแนน: %d, %s (รหัส: %s)", emoji, score, riskLevel.replace(emoji + " ", ""), resultCode);
    }

    /**
     * ดึงคำแนะนำสำหรับผู้ใช้
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณากรอกข้อมูลให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int score = getCurrentAlcoholScore();
        return ScreeningResultCode.getAlcoholRecommendation(score);
    }
    public String getDetailedAssessmentResult() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int score = getCurrentAlcoholScore();
        String riskLevel = getRiskLevelFromScore();
        String resultCode = ScreeningResultCode.getAlcoholAnswerResultCode(score);
        String resultDescription = ScreeningResultCode.getAlcoholResultDescription(score);
        String recommendation = getRecommendation();
        boolean isAbnormal = ScreeningResultCode.isAlcoholAbnormal(score);
        boolean isHighRisk = ScreeningResultCode.isAlcoholHighRisk(score);

        StringBuilder result = new StringBuilder();
        result.append("=== ผลการประเมินความเสี่ยงจากการดื่มแอลกอฮอล์ ===\n");
        result.append("คะแนนรวม: ").append(score).append(" คะแนน\n");
        result.append("ระดับความเสี่ยง: ").append(riskLevel).append("\n");
        result.append("รหัสผลการประเมิน: ").append(resultCode).append("\n");
        result.append("คำอธิบาย: ").append(resultDescription).append("\n");
        result.append("สถานะ: ").append(isAbnormal ? "ผิดปกติ" : "ปกติ").append("\n");

        if (isHighRisk) {
            result.append("⚠️ ความเสี่ยงสูง: ต้องการความช่วยเหลือเร่งด่วน\n");
        }

        result.append("\nคำแนะนำ:\n").append(recommendation);

        return result.toString();
    }
    public void checkHighRiskAlert() {
        if (isFormComplete() && isHighRisk()) {
            int score = getCurrentAlcoholScore();
            String emoji = getRiskEmoji(score);
            String message = String.format(
                    "%s ตรวจพบความเสี่ยงสูงจากการดื่มแอลกอฮอล์\n\n" +
                            "คะแนน: %d คะแนน\n" +
                            "ระดับ: %s\n\n" +
                            "คำแนะนำ: %s",
                    emoji,
                    score,
                    getRiskLevelFromScore(),
                    getRecommendation()
            );

            Log.w(TAG, message);
        }
    }
    public String getRiskTrend() {
        if (!isFormComplete()) {
            return "ไม่สามารถวิเคราะห์ได้";
        }

        int score = getCurrentAlcoholScore();

        if (score == ScreeningResultCode.ALCOHOL_SCORE_NO_DRINKING) {
            return "ไม่มีความเสี่ยง - ควรคงสภาพปัจจุบัน";
        } else if (score >= ScreeningResultCode.ALCOHOL_SCORE_LOW_RISK_MIN &&
                score <= ScreeningResultCode.ALCOHOL_SCORE_LOW_RISK_MAX) {
            if (score <= 3) {
                return "ความเสี่ยงต่ำ - สถานการณ์ควบคุมได้";
            } else if (score <= 7) {
                return "ความเสี่ยงต่ำ-ปานกลาง - ควรระวังและติดตาม";
            } else {
                return "ความเสี่ยงต่ำ-สูง - เข้าใกล้เกณฑ์เสี่ยง";
            }
        } else if (score >= ScreeningResultCode.ALCOHOL_SCORE_MEDIUM_RISK_MIN &&
                score <= ScreeningResultCode.ALCOHOL_SCORE_MEDIUM_RISK_MAX) {
            if (score <= 18) {
                return "ความเสี่ยงปานกลาง - ต้องการการแทรกแซง";
            } else {
                return "ความเสี่ยงปานกลาง-สูง - ใกล้เกณฑ์ความเสี่ยงสูง";
            }
        } else {
            return "ความเสี่ยงสูงมาก - ต้องการการบำบัดเร่งด่วน";
        }
    }
    public String generateAssessmentReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== รายงานการประเมินความเสี่ยงจากการดื่มแอลกอฮอล์ ===\n\n");

        if (!isFormComplete()) {
            report.append("สถานะ: ไม่สามารถสร้างรายงานได้\n");
            report.append("เหตุผล: ข้อมูลไม่ครบถ้วน\n");
            report.append("ข้อที่ยังไม่ได้กรอก: ").append(getValidationMessage()).append("\n");
            return report.toString();
        }

        // ข้อมูลพื้นฐาน
        report.append("สถานะการกรอกข้อมูล: ครบถ้วน (").append(getCompletionPercentage()).append("%)\n");
        report.append("วันที่ประเมิน: ").append(DateConverter.getCurrentWesternDateTime()).append("\n\n");

        // ผลการประเมิน
        int score = getCurrentAlcoholScore();
        report.append("ผลการประเมิน:\n");
        report.append("- คะแนนรวม: ").append(score).append(" คะแนน\n");
        report.append("- ระดับความเสี่ยง: ").append(getRiskLevelFromScore()).append("\n");
        report.append("- รหัสผล: ").append(ScreeningResultCode.getAlcoholAnswerResultCode(score)).append("\n");
        report.append("- สถานะ: ").append(ScreeningResultCode.isAlcoholAbnormal(score) ? "ผิดปกติ" : "ปกติ").append("\n\n");

        // การวิเคราะห์
        report.append("การวิเคราะห์:\n");
        report.append("- แนวโน้มความเสี่ยง: ").append(getRiskTrend()).append("\n");
        report.append("- ความเสี่ยงสูง: ").append(isHighRisk() ? "ใช่" : "ไม่").append("\n\n");

        // คำแนะนำ
        report.append("คำแนะนำ:\n");
        report.append(getRecommendation()).append("\n\n");

        // ข้อมูลเพิ่มเติม
        if (isHighRisk()) {
            report.append("⚠️ การดำเนินการเร่งด่วน:\n");
            report.append("1. ปรึกษาแพทย์หรือผู้เชี่ยวชาญด้านการบำบัดสารเสพติด\n");
            report.append("2. พิจารณาเข้าร่วมโปรแกรมบำบัดเข้มข้น\n");
            report.append("3. ติดตามอาการอย่างใกล้ชิด\n");
            report.append("4. หลีกเลี่ยงสถานการณ์เสี่ยง\n\n");
        }

        report.append("=== สิ้นสุดรายงาน ===");

        return report.toString();
    }
    public boolean saveAssessmentReport(String filePath) {
        try {
            String report = generateAssessmentReport();
            // สามารถเพิ่มการบันทึกไฟล์ได้ที่นี่
            Log.d(TAG, "รายงานการประเมิน:\n" + report);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกรายงาน: " + e.getMessage());
            return false;
        }
    }
    private void autoSaveIfComplete() {
        if (isFormComplete() && currentPersonId != -1 && currentVisitNo != -1) {
            try {
                UserSessionManager sessionManager = new UserSessionManager(getContext());
                String userCreate = sessionManager.getUser();

                if (userCreate != null && !userCreate.isEmpty()) {
                    // บันทึกข้อมูลอัตโนมัติ
                    boolean saveSuccess = saveToScreeningResultCode(currentPersonId, currentVisitNo, userCreate);

                    if (saveSuccess) {
                        Log.d(TAG, "บันทึกข้อมูลแอลกอฮอล์อัตโนมัติสำเร็จ");

                        // ตรวจสอบความเสี่ยงสูงและแจ้งเตือน
                        if (isHighRisk()) {
                            showHighRiskNotification();
                        }
                    } else {
                        Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกอัตโนมัติ");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception ในการบันทึกอัตโนมัติ: " + e.getMessage());
            }
        }
    }
    private void showHighRiskNotification() {
        try {
            int score = getCurrentAlcoholScore();
            String emoji = getRiskEmoji(score);
            String riskLevel = getRiskLevelFromScore();

            String message = String.format(
                    "%s ตรวจพบความเสี่ยงสูงจากการดื่มแอลกอฮอล์\n\n" +
                            "คะแนน: %d คะแนน\n" +
                            "ระดับ: %s\n\n" +
                            "จำเป็นต้องได้รับการดูแลเป็นพิเศษ",
                    emoji, score, riskLevel
            );

            Log.w(TAG, message);

            // แสดง Toast แจ้งเตือน
            if (getContext() != null) {
                Toast.makeText(getContext(), emoji + " ตรวจพบความเสี่ยงสูงจากการดื่มแอลกอฮอล์",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแสดงการแจ้งเตือน: " + e.getMessage());
        }
    }
    private void setupRadioGroupListeners() {
        // Main drinking selection
        rdoDrinking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDriking(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinking1) {
                    data = "1";
                    clearNestedSelections();
                } else if(checkedId == R.id.rdoDrinking2) {
                    data = "2";
                    clearNestedSelections();
                } else if(checkedId == R.id.rdoDrinking3) {
                    data = "3";
                }

                drinkingInfo.setDrinking(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }

                // เพิ่มการบันทึกอัตโนมัติ
//                autoSaveIfComplete();
            }
        });

        // Drinking frequency selection
        rdoDrinkingFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDrikingFrequency(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinkingFrequency1) {
                    data = "1";
                    clearAlwaySelection();
                } else if(checkedId == R.id.rdoDrinkingFrequency2) {
                    data = "2";
                    clearAlwaySelection();
                } else if(checkedId == R.id.rdoDrinkingFrequency3) {
                    data = "3";
                }

                drinkingInfo.setDrinkingFrequency(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }

                // เพิ่มการบันทึกอัตโนมัติ
//                autoSaveIfComplete();
            }
        });

        // Always drinking selection
        rdoDrinkingAlway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDrikingAlway(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinkingAlway1) {
                    data = "1";
                } else if(checkedId == R.id.rdoDrinkingAlway2) {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingAlway3) {
                    data = "3";
                }

                drinkingInfo.setDrinkingAlway(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }

                // เพิ่มการบันทึกอัตโนมัติ
//                autoSaveIfComplete();
            }
        });
    }
}