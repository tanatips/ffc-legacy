package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.ScreeningResultCodeDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.view.StressRiskGauge2QView;
import th.in.ffc.provider.ScreeningResultCode;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.Log;
import android.app.AlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression2qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression2qFragment extends Fragment {

    private static final String TAG = "StressDepression2qFragment";
    private StressDepression2qLiveData stressDepression2qLiveData;
    private SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepression2qInfo stressDepression2qInfo;

    private ScreeningResultCodeDao screeningResultDao;
    private int currentPersonId = -1;
    private int currentVisitNo = -1;

    private ArrayList<Integer> points;
    private RadioGroup rdoStress2qQ1;
    private RadioGroup rdoStress2qQ2;

    private TextView tv2qResult;
    private TextView tv2qResultDetail;

    int white;
    int light_gray;
    int highlightColor;


    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false}; // ตรวจสอบว่าตอบคำถามครบหรือไม่
    private ScreeningResultCodeDao screeningResultCodeDao;

    private ImageView ivStress2qInfoButton;

    private StressRiskGauge2QView stressRiskGauge;
    private TextView tvStressGaugeEmoji;
    private TextView tvStressGaugeScore;
    private TextView tvStressGaugeLevel;
    private TextView tvStressGaugeCode;
    private TextView tvStressGaugeRecommendation;
    private SeekBar seekBarStressGaugeTest;

    public StressDepression2qFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stress_depression2q, container, false);
        return view;
    }

    private void initializeViews(View view) {
        rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);

        // เชื่อมโยง TextView ใหม่
        tv2qResult = view.findViewById(R.id.tv2qResult);
        tv2qResultDetail = view.findViewById(R.id.tv2qResultDetail);
        
        // เพิ่มการ initialize info button
        ivStress2qInfoButton = view.findViewById(R.id.ivStress2qInfoButton);
        setupInfoButtonListener();
        initializeStressGaugeViews(view);

        white = ContextCompat.getColor(requireContext(), R.color.white);
        light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);

        // แสดงผลเริ่มต้น
        updateResultDisplay();
    }

    private void initializeStressGaugeViews(View view) {
        stressRiskGauge = view.findViewById(R.id.stressRiskGauge);
        tvStressGaugeEmoji = view.findViewById(R.id.tvStressGaugeEmoji);
        tvStressGaugeScore = view.findViewById(R.id.tvStressGaugeScore);
        tvStressGaugeLevel = view.findViewById(R.id.tvStressGaugeLevel);
        tvStressGaugeCode = view.findViewById(R.id.tvStressGaugeCode);
        tvStressGaugeRecommendation = view.findViewById(R.id.tvStressGaugeRecommendation);

        // สำหรับทดสอบ (สามารถลบออกได้)
        seekBarStressGaugeTest = view.findViewById(R.id.seekBarStressGaugeTest);
        setupStressGaugeTestControls();


        // อัปเดต Gauge ครั้งแรก
        updateStressGaugeDisplay();
    }
    private void setupInfoButtonListener() {
        if (ivStress2qInfoButton != null) {
            ivStress2qInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStress2qCriteriaDialog();
                }
            });
        }
    }
    private void showStress2qCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom layout สำหรับ dialog
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_stress_2q_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d(TAG, "แสดง Dialog เกณฑ์การประเมิน 2Q สำเร็จ");

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());

            // แสดง dialog แบบง่ายหากเกิดข้อผิดพลาด
            showSimpleStress2qCriteriaDialog();
        }
    }
    private void showSimpleStress2qCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "📊 เกณฑ์การคัดกรองโรคซึมเศร้า 2Q\n\n" +
                "😊 ตอบ 'ไม่มี' ทั้ง 2 ข้อ: ปกติ (1B0210)\n" +
                "🔶 ควรดูแลสุขภาพจิตให้ดีต่อไป\n\n" +

                "😟 ตอบ 'มี' อย่างน้อย 1 ข้อ: ผิดปกติ (1B0211)\n" +
                "🔶 แนะนำให้ทำแบบ 9Q เพิ่มเติม และปรึกษาแพทย์\n\n";

        builder.setTitle("📈 เกณฑ์การประเมิน 2Q")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String get2qEmoji(boolean hasPositiveAnswer) {
        if (hasPositiveAnswer) {
            return "😟"; // ผิดปกติ - หน้ากังวล
        } else {
            return "😊"; // ปกติ - หน้ายิ้ม
        }
    }
    /**
     * ดึงผลการประเมินพร้อม emoji
     */
    public String getAssessmentResultWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        // ตรวจสอบว่าตอบ "มี" อย่างน้อย 1 ข้อหรือไม่
        boolean hasPositive = false;

        if ("2".equals(stressDepression2qInfo.getQ1())) { // "2" = มี
            hasPositive = true;
        }

        if ("2".equals(stressDepression2qInfo.getQ2())) { // "2" = มี
            hasPositive = true;
        }

        String emoji = get2qEmoji(hasPositive);

        if (hasPositive) {
            return emoji + " ผิดปกติ (Abnormal) - มีความเสี่ยงต่อภาวะซึมเศร้า";
        } else {
            return emoji + " ปกติ (Normal) - ไม่มีความเสี่ยงต่อภาวะซึมเศร้า";
        }
    }

    /**
     * แสดงคำแนะนำตามผลการประเมินพร้อม emoji
     */
    public String getRecommendationWithEmoji() {
        if (!isFormComplete()) {
            return "📝 กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        String emoji = get2qEmoji(isAtRisk());

        if (isAtRisk()) {
            return emoji + " พบความเสี่ยงต่อภาวะซึมเศร้า ควรทำแบบประเมิน 9Q เพิ่มเติม และพิจารณาปรึกษาแพทย์หรือผู้เชี่ยวชาญด้านสุขภาพจิต";
        } else {
            return emoji + " ไม่พบความเสี่ยงต่อภาวะซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป หากมีอาการเปลี่ยนแปลงควรมาประเมินใหม่";
        }
    }

    public String getSummaryTextWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        int score = getTotalScore();
        String emoji = get2qEmoji(score > 0);

        if (score == 0) {
            return emoji + " ปกติ (ไม่มีอาการ)";
        } else if (score == 1) {
            return emoji + " ผิดปกติ (มีอาการ 1 ข้อ)";
        } else if (score == 2) {
            return emoji + " ผิดปกติ (มีอาการ 2 ข้อ)";
        }

        return "";
    }
    public void showCompletionStatusWithEmoji() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            String resultInfo = getAssessmentResultWithEmoji();
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + resultInfo;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("StressDepression2q", "Completion Status: " + message);
    }
    public String get9QRecommendationTextWithEmoji() {
        if (shouldDo9QAssessment()) {
            String emoji = get2qEmoji(true);
            return emoji + " ผลการประเมิน 2Q พบความเสี่ยง แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติมเพื่อความแม่นยำ";
        }
        return "";
    }

    private void validateAndSaveData() {
        // ตรวจสอบว่าตอบคำถามครบทุกข้อหรือไม่
        boolean allAnswered = true;
        for (boolean answered : questionAnswered) {
            if (!answered) {
                allAnswered = false;
                break;
            }
        }

        isFormValid = allAnswered;

        if (isFormValid) {
            // ถ้าตอบครบทุกข้อ ให้บันทึกข้อมูล
            Log.d("StressDepression2q", "ตอบคำถามครบทุกข้อแล้ว - บันทึกข้อมูล");
            dataPasser.onStressDepression2q(stressDepression2qInfo);
        } else {
            // ถ้ายังตอบไม่ครบ ให้แสดงข้อความแจ้งเตือน
            showIncompleteFormMessage();
        }
    }

    /**
     * แสดงข้อความแจ้งเตือนเมื่อตอบไม่ครบ
     */
    private void showIncompleteFormMessage() {
        String missingQuestions = getMissingQuestionsText();
    }

    /**
     * ตรวจสอบสถานะการตอบจาก RadioGroup
     */
    private void checkAnsweredStatus() {
        questionAnswered[0] = rdoStress2qQ1.getCheckedRadioButtonId() != -1;
        questionAnswered[1] = rdoStress2qQ2.getCheckedRadioButtonId() != -1;
    }

    /**
     * ตรวจสอบว่าแบบฟอร์มกรอกครบหรือไม่
     */
    public boolean isFormComplete() {
        return isFormValid;
    }

    /**
     * รีเซ็ตสถานะการตรวจสอบ (ใช้เมื่อล้างข้อมูล)
     */
    public void resetValidation() {
        isFormValid = false;
        for (int i = 0; i < questionAnswered.length; i++) {
            questionAnswered[i] = false;
        }
    }

    /**
     * ดึงรายการคำถามที่ยังไม่ได้ตอบ
     */
    private String getMissingQuestionsText() {
        ArrayList<String> missingQuestions = new ArrayList<>();

        for (int i = 0; i < questionAnswered.length; i++) {
            if (!questionAnswered[i]) {
                missingQuestions.add("ข้อ " + (i + 1));
            }
        }

        if (missingQuestions.isEmpty()) {
            return "";
        }

        return String.join(", ", missingQuestions);
    }

    private void setupListeners(){
        rdoStress2qQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress2qQ1_1) {
                    data= "1";
                    points.set(0,0);
                } else if (checkedId == R.id.rdoStress2qQ1_2) {
                    data= "2";
                    points.set(0,1);
                }
                stressDepression2qLiveData.setSelectedQ1(checkedId);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);

                stressDepression2qInfo.setQ1(data);
                stressDepression2qInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 1 แล้ว
                questionAnswered[0] = true;
                validateAndSaveData();

                updatePoints();
                updateResultDisplay();

                // บันทึก result code หากครบถ้วน
//                saveResultCodeIfComplete();
            }
        });

        rdoStress2qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress2qQ2_1) {
                    data= "1";
                    points.set(1,0);
                } else if (checkedId == R.id.rdoStress2qQ2_2) {
                    data= "2";
                    points.set(1,1);
                }
                stressDepression2qLiveData.setSelectedQ2(checkedId);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);

                stressDepression2qInfo.setQ2(data);
                stressDepression2qInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 2 แล้ว
                questionAnswered[1] = true;
                validateAndSaveData(); // เรียกใช้การตรวจสอบและบันทึก

                updatePoints();
                updateResultDisplay(); // เพิ่มการอัพเดทการแสดงผล

                // บันทึก result code หากครบถ้วน
//                saveResultCodeIfComplete();
            }
        });
    }
    /**
     * บันทึก screening result code หากข้อมูลครบถ้วน
     */
    private void saveResultCodeIfComplete() {
        if (isFormComplete() && currentPersonId != -1 && currentVisitNo != -1) {
            try {
                // ตรวจสอบผลการประเมิน
                boolean hasPositiveAnswer = false;
                String answer1 = stressDepression2qInfo.getQ1();
                String answer2 = stressDepression2qInfo.getQ2();

                // ตรวจสอบว่ามีการตอบ "มี" (รหัส "2") หรือไม่
                if ("2".equals(answer1) || "2".equals(answer2)) {
                    hasPositiveAnswer = true;
                }

                // บันทึกผลการคัดกรอง 2Q
                Uri result = screeningResultDao.save2QResult(
                        currentPersonId,
                        currentVisitNo,
                        hasPositiveAnswer,
                        "SYSTEM" // หรือ username ปัจจุบัน
                );

                if (result != null) {
                    Log.d("StressDepression2q", "บันทึก screening result code สำเร็จ: " + result.toString());
                } else {
                    Log.e("StressDepression2q", "เกิดข้อผิดพลาดในการบันทึก screening result code");
                }

            } catch (Exception e) {
                Log.e("StressDepression2q", "เกิดข้อผิดพลาดในการบันทึก screening result code");
            }
        }
    }
    public void setPersonAndVisitInfo(int personId, int visitNo) {
        this.currentPersonId = personId;
        this.currentVisitNo = visitNo;
    }

    private void updatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // Add points for Q1
        points.add(stressDepression2qInfo.getQ1().equals("1") ? 1 : 0);

        // Add points for Q2
        points.add(stressDepression2qInfo.getQ2().equals("1") ? 1 : 0);

        // Update the points in the model
        stressDepression2qInfo.setPoints(points);

        // Analyze results
        stressDepression2qInfo.analyze();
    }

    public void setStressInfo(StressDepression2qInfo info) {
        this.stressDepression2qInfo = info;
        loadExistingData();
    }

    // ปรับปรุง loadExistingData() ให้ตรวจสอบสถานะการตอบ
    private void loadExistingData() {
        if (stressDepression2qInfo == null) return;

        // Load Q1 data
        switch (stressDepression2qInfo.getQ1()) {
            case "1":
                ((RadioButton)rdoStress2qQ1.findViewById(R.id.rdoStress2qQ1_1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoStress2qQ1.findViewById(R.id.rdoStress2qQ1_2)).setChecked(true);
                break;
        }

        // Load Q2 data
        switch (stressDepression2qInfo.getQ2()) {
            case "1":
                ((RadioButton)rdoStress2qQ2.findViewById(R.id.rdoStress2qQ2_1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoStress2qQ2.findViewById(R.id.rdoStress2qQ2_2)).setChecked(true);
                break;
        }

        // ตรวจสอบสถานะการตอบจากข้อมูลที่โหลดมา
        checkAnsweredStatus();

        // อัปเดตสถานะความถูกต้องของข้อมูล
        validateAndSaveData();

        // อัพเดทการแสดงผล
        updateResultDisplay();
    }

    public StressDepression2qInfo getStressInfo() {
        return stressDepression2qInfo;
    }

    public static StressDepression2qFragment newInstance(String param1, String param2) {
        StressDepression2qFragment fragment = new StressDepression2qFragment();
        return fragment;
    }

    public StressDepression2qInfo getFormData() {
        if (isFormValid) {
            return stressDepression2qInfo;
        } else {
            showIncompleteFormMessage();
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stressDepression2qLiveData = new StressDepression2qLiveData();
        shareViewModel = new SharedViewModel();
        stressDepression2qInfo = new StressDepression2qInfo();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0,0));
        screeningResultDao = new ScreeningResultCodeDao(getContext());
        screeningResultCodeDao = new ScreeningResultCodeDao(getContext());
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
        View parentViewPager = (View) view.getParent();
        if (parentViewPager != null) {
            parentViewPager.post(() -> {
                int height = view.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = parentViewPager.getLayoutParams();
                layoutParams.height = height;
                parentViewPager.setLayoutParams(layoutParams);
            });
        }
        initializeViews(view);
        setupListeners();
        loadData();
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                currentPersonId = Integer.parseInt(personInfo.getId());
                if( personInfo.getVisitId() != null && !personInfo.getVisitId().isEmpty()){
                    currentVisitNo = Integer.parseInt(personInfo.getVisitId());
                }

            }
        });
    }

    private void loadData(){
        SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepression2qLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<StressDepression2qInfo> stressDepression2qInfos = sfStressDepression2qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepression2qInfo stressDepression2qInfo :stressDepression2qInfos){
                    Log.d("Stress Depression ", "stressDepression2qInfo infos:"+stressDepression2qInfo);
                    this.stressDepression2qInfo = stressDepression2qInfo;
                    loadExistingData();
                }
            }
        });
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
            data.screeningType = ScreeningResultCode.TYPE_STRESS_DEPRESSION_2Q; // "2Q"
            data.totalScore = getCurrentTotalScore(); // ใช้ method ที่มีอยู่
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode และ resultDescription ตาม 2Q
            boolean hasPositiveAnswer = data.totalScore > 0;
            setResultCodeAndDescription2Q(data, hasPositiveAnswer);

            // กำหนด riskLevel และ isAbnormal
            setRiskLevelAndAbnormal2Q(data, hasPositiveAnswer);

            // กำหนดคำแนะนำ
            data.recommendation = getRecommendation2Q(hasPositiveAnswer);

            // บันทึกข้อมูล
            Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d(TAG, "บันทึกผลการคัดกรอง 2Q สำเร็จ: " + result.toString());
                Log.d(TAG, "รายละเอียด: personId=" + personId + ", visitno=" + visitno +
                        ", score=" + data.totalScore + ", resultCode=" + data.resultCode +
                        ", hasPositiveAnswer=" + hasPositiveAnswer);
                return true;
            } else {
                Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผลการคัดกรอง 2Q");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกผลการคัดกรอง 2Q");
            return false;
        }
    }

    private String getRecommendation2Q(boolean hasPositiveAnswer) {
        if (hasPositiveAnswer) {
            return "แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติม และพิจารณาปรึกษาแพทย์";
        } else {
            return "ไม่พบความเสี่ยงต่อภาวะซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";
        }
    }
    private void setResultCodeAndDescription2Q(ScreeningResultCodeDao.ScreeningResultData data, boolean hasPositiveAnswer) {
        if (!hasPositiveAnswer) {
            data.resultCode = "1B0211";
            data.resultDescription = "ผิดปกติ และส่งต่อเจ้าหน้าที่";
        } else {
            data.resultCode = "1B0210";
            data.resultDescription = "ปกติ";
        }
    }

    /**
     * กำหนด riskLevel และ isAbnormal ตาม 2Q
     */
    private void setRiskLevelAndAbnormal2Q(ScreeningResultCodeDao.ScreeningResultData data, boolean hasPositiveAnswer) {
        if (hasPositiveAnswer) {
            data.riskLevel = ScreeningResultCode.RISK_HIGH;
            data.isAbnormal = true;
        } else {
            data.riskLevel = ScreeningResultCode.RISK_NORMAL;
            data.isAbnormal = false;
        }
    }
    private int getCurrentTotalScore() {
        if (stressDepression2qInfo != null) {
            return stressDepression2qInfo.getSum();
        }
        return 0;
    }
    public void loadFromScreeningResultCode(int personId, int visitno) {
        try {
            ScreeningResultCodeDao.ScreeningResultData existingData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_STRESS_DEPRESSION_2Q);

            if (existingData != null) {
                Log.d(TAG, "พบข้อมูลการประเมิน 2Q เดิม: คะแนน=" + existingData.totalScore +
                        ", ผลการประเมิน=" + existingData.resultDescription);

                Toast.makeText(getContext(),
                        "โหลดข้อมูลการประเมิน 2Q เดิม: " + existingData.resultDescription,
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "ไม่พบข้อมูลการประเมิน 2Q เดิม");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดข้อมูลการประเมิน 2Q");
        }
    }
    public boolean hasExistingData(int personId, int visitno) {
        try {
            ScreeningResultCodeDao.ScreeningResultData existingData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_STRESS_DEPRESSION_2Q);
            return existingData != null;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูลเดิม");
            return false;
        }
    }

    public void showSaveResult(boolean success, String message) {
        if (success) {
            Toast.makeText(getContext(),
                    "✅ บันทึกผลการประเมินซึมเศร้า 2Q สำเร็จ",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "❌ เกิดข้อผิดพลาดในการบันทึก: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }
    // เพิ่มเมธอด validation ใน StressDepression2qFragment class

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();
        ArrayList<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q): ยังไม่ได้ตอบข้อ ");
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
        if (stressDepression2qInfo == null) {
            return "คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q):\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingQuestions = new ArrayList<>();

        if (stressDepression2qInfo.getQ1() == null || stressDepression2qInfo.getQ1().equals("0") || stressDepression2qInfo.getQ1().isEmpty()) {
            missingQuestions.add("ข้อ 1: ใน 2 สัปดาห์ที่ผ่านมา รู้สึกหดหู่ เศร้า หรือท้อแท้สิ้นหวัง");
        }

        if (stressDepression2qInfo.getQ2() == null || stressDepression2qInfo.getQ2().equals("0") || stressDepression2qInfo.getQ2().isEmpty()) {
            missingQuestions.add("ข้อ 2: ใน 2 สัปดาห์ที่ผ่านมา รู้สึกเบื่อ ไม่สนใจอยากทำอะไร");
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q):\n");
            message.append("กรุณาตอบคำถามที่ยังไม่ได้ตอบ:\n");
            for (String question : missingQuestions) {
                message.append("• ").append(question).append("\n");
            }
            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * รีเซ็ตฟอร์มกลับเป็นค่าเริ่มต้น
     */
    public void resetForm() {
        // ล้างการเลือกทั้งหมด
        if (rdoStress2qQ1 != null) rdoStress2qQ1.clearCheck();
        if (rdoStress2qQ2 != null) rdoStress2qQ2.clearCheck();

        // รีเซ็ต stressDepression2qInfo
        stressDepression2qInfo = new StressDepression2qInfo();

        // รีเซ็ต points
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0, 0));

        // รีเซ็ตสถานะการตรวจสอบ
        resetValidation();

        // รีเซ็ตการแสดงผล
        updateResultDisplay();

        // รีเซ็ต Stress Gauge
        resetStressGauge();
    }
    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (stressDepression2qInfo == null) {
            return false;
        }

        return (!stressDepression2qInfo.getQ1().equals("0") && !stressDepression2qInfo.getQ1().isEmpty()) ||
                (!stressDepression2qInfo.getQ2().equals("0") && !stressDepression2qInfo.getQ2().isEmpty());
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (stressDepression2qInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 2;

        if (!stressDepression2qInfo.getQ1().equals("0") && !stressDepression2qInfo.getQ1().isEmpty()) completedQuestions++;
        if (!stressDepression2qInfo.getQ2().equals("0") && !stressDepression2qInfo.getQ2().isEmpty()) completedQuestions++;

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

            // แสดงผลการประเมินด้วย
            String result = getAssessmentResult();
            message += " - " + result;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("StressDepression2q", "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ดึงรายชื่อคำถามที่ยังไม่ได้ตอบ
     */
    public ArrayList<Integer> getUnansweredQuestions() {
        ArrayList<Integer> unanswered = new ArrayList<>();

        if (stressDepression2qInfo == null) {
            unanswered.add(1);
            unanswered.add(2);
            return unanswered;
        }

        if (stressDepression2qInfo.getQ1() == null || stressDepression2qInfo.getQ1().equals("0") || stressDepression2qInfo.getQ1().isEmpty()) {
            unanswered.add(1);
        }

        if (stressDepression2qInfo.getQ2() == null || stressDepression2qInfo.getQ2().equals("0") || stressDepression2qInfo.getQ2().isEmpty()) {
            unanswered.add(2);
        }

        return unanswered;
    }

    /**
     * ดึงคำอธิบายของคำถามแต่ละข้อ
     */
    private String getQuestionDescription(int questionNumber) {
        switch (questionNumber) {
            case 1:
                return "ใน 2 สัปดาห์ที่ผ่านมา รู้สึกหดหู่ เศร้า หรือท้อแท้สิ้นหวัง";
            case 2:
                return "ใน 2 สัปดาห์ที่ผ่านมา รู้สึกเบื่อ ไม่สนใจอยากทำอะไร";
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
                    targetGroup = rdoStress2qQ1;
                    break;
                case 2:
                    targetGroup = rdoStress2qQ2;
                    break;
            }

            if (targetGroup != null) {
                targetGroup.requestFocus();
                // สามารถเพิ่มการ scroll ไปยัง view ได้ที่นี่
            }
        }
    }

    /**
     * ดึงผลการประเมิน
     */
    public String getAssessmentResult() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        // ตรวจสอบว่าตอบ "มี" อย่างน้อย 1 ข้อหรือไม่
        boolean hasPositive = false;

        if ("2".equals(stressDepression2qInfo.getQ1())) { // "2" = มี
            hasPositive = true;
        }

        if ("2".equals(stressDepression2qInfo.getQ2())) { // "2" = มี
            hasPositive = true;
        }

        if (hasPositive) {
            return "ผิดปกติ (Abnormal) - มีความเสี่ยงต่อภาวะซึมเศร้า";
        } else {
            return "ปกติ (Normal) - ไม่มีความเสี่ยงต่อภาวะซึมเศร้า";
        }
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงหรือไม่ (ตอบ "มี" อย่างน้อย 1 ข้อ)
     */
    public boolean isAtRisk() {
        if (!isFormComplete()) {
            return false;
        }

        return "2".equals(stressDepression2qInfo.getQ1()) || "2".equals(stressDepression2qInfo.getQ2());
    }

    /**
     * แสดงคำแนะนำตามผลการประเมิน
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        if (isAtRisk()) {
            return "พบความเสี่ยงต่อภาวะซึมเศร้า ควรทำแบบประเมิน 9Q เพิ่มเติม และพิจารณาปรึกษาแพทย์หรือผู้เชี่ยวชาญด้านสุขภาพจิต";
        } else {
            return "ไม่พบความเสี่ยงต่อภาวะซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป หากมีอาการเปลี่ยนแปลงควรมาประเมินใหม่";
        }
    }

    /**
     * ดึงคะแนนรวม (สำหรับ 2Q คือจำนวนข้อที่ตอบ "มี")
     */
    public int getTotalScore() {
        if (!isFormComplete()) {
            return -1;
        }

        int score = 0;

        if ("2".equals(stressDepression2qInfo.getQ1())) {
            score++;
        }

        if ("2".equals(stressDepression2qInfo.getQ2())) {
            score++;
        }

        return score;
    }

    /**
     * ดึงข้อความสรุปผลแบบสั้น
     */
    public String getSummaryText() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int score = getTotalScore();

        if (score == 0) {
            return "ปกติ (ไม่มีอาการ)";
        } else if (score == 1) {
            return "ผิดปกติ (มีอาการ 1 ข้อ)";
        } else if (score == 2) {
            return "ผิดปกติ (มีอาการ 2 ข้อ)";
        }

        return "";
    }

    /**
     * ตรวจสอบว่าควรทำแบบประเมิน 9Q ต่อหรือไม่
     */
    public boolean shouldDo9QAssessment() {
        if (!isFormComplete()) {
            return false;
        }
        return getCurrentTotalScore() > 0; // หากมีคำตอบเป็น "ใช่" ข้อใดข้อหนึ่ง
    }

    /**
     * ดึงข้อความแนะนำให้ทำ 9Q
     */
    public String get9QRecommendationText() {
        if (shouldDo9QAssessment()) {
            return "ผลการประเมิน 2Q พบความเสี่ยง แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติมเพื่อความแม่นยำ";
        }
        return "";
    }
    public ScreeningResultCodeDao.ScreeningStatistics getStatistics() {
        try {
            return screeningResultCodeDao.getStatisticsByType(ScreeningResultCode.TYPE_STRESS_DEPRESSION_2Q);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติ 2Q");
            return null;
        }
    }
    public void showStatistics() {
        ScreeningResultCodeDao.ScreeningStatistics stats = getStatistics();
        if (stats != null) {
            String message = String.format(
                    "สถิติการประเมิน 2Q:\n" +
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
    private void setupStressGaugeTestControls() {
        if (seekBarStressGaugeTest != null) {
            seekBarStressGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && stressRiskGauge != null) {
                        stressRiskGauge.setScore(progress);
                        StressRiskGauge2QView.StressLevel level = getCurrentStressLevelFromScore(progress);

                        // อัปเดตข้อความทดสอบ
                        if (tvStressGaugeEmoji != null) tvStressGaugeEmoji.setText(level.emoji);
                        if (tvStressGaugeScore != null) tvStressGaugeScore.setText("คะแนน: " + progress + "/2");
                        if (tvStressGaugeLevel != null) {
                            tvStressGaugeLevel.setText(level.label);
                            tvStressGaugeLevel.setTextColor(Color.parseColor(level.color));
                        }
                        if (tvStressGaugeCode != null) tvStressGaugeCode.setText(level.code);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    private void showStressGaugeCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "📊 เกณฑ์การคัดกรองโรคซึมเศร้า 2Q Gauge\n\n" +
                "😊 0 คะแนน: ปกติ (1B0210)\n" +
                "🔶 ตอบ 'ไม่มี' ทั้ง 2 ข้อ\n" +
                "🔶 ควรดูแลสุขภาพจิตให้ดีต่อไป\n\n" +

                "😟 1-2 คะแนน: ผิดปกติ (1B0211)\n" +
                "🔶 ตอบ 'มี' อย่างน้อย 1 ข้อ\n" +
                "🔶 แนะนำให้ทำแบบ 9Q เพิ่มเติม และปรึกษาแพทย์\n\n";

        builder.setTitle("📈 เกณฑ์การประเมิน 2Q Gauge")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showStressGaugeTestControls(boolean show) {
        View layoutStressGaugeControl = getView().findViewById(R.id.layoutStressGaugeControl);
        if (layoutStressGaugeControl != null) {
            layoutStressGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // Method สำหรับรีเซ็ต Gauge
    public void resetStressGauge() {
        if (stressRiskGauge != null) {
            stressRiskGauge.setScore(0);
            updateStressGaugeDisplay();
        }
    }

    private void updateStressGaugeDisplay() {
        if (stressRiskGauge == null) return;

        int totalScore = getTotalScore();
        // ถ้ายังไม่ครบข้อมูล ให้ใช้ 0
        if (totalScore == -1) totalScore = 0;

        StressRiskGauge2QView.StressLevel currentLevel = getCurrentStressLevelFromScore(totalScore);

        // อัปเดต Gauge
        stressRiskGauge.setScore(totalScore);

        // อัปเดตข้อความ
        if (tvStressGaugeEmoji != null) tvStressGaugeEmoji.setText(currentLevel.emoji);
        if (tvStressGaugeScore != null) tvStressGaugeScore.setText("คะแนน: " + totalScore + "/2");
        if (tvStressGaugeLevel != null) {
            tvStressGaugeLevel.setText(currentLevel.label);
            tvStressGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvStressGaugeCode != null) tvStressGaugeCode.setText(currentLevel.code);

        Log.d(TAG, "Stress Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label);
    }

    private StressRiskGauge2QView.StressLevel getCurrentStressLevelFromScore(int score) {
        if (score == 0) {
            return new StressRiskGauge2QView.StressLevel(0, 0, "ปกติ", "#27AE60", "😊", "1B0210");
        } else {
            return new StressRiskGauge2QView.StressLevel(1, 2, "ผิดปกติ และส่งต่อเจ้าหน้าที่", "#E74C3C", "😟", "1B0211");
        }
    }

    public void updateStressGaugeWithScore(int score) {
        if (stressRiskGauge != null) {
            stressRiskGauge.setScore(score);
            updateStressGaugeDisplay();
        }
    }

    public StressRiskGauge2QView.StressLevel getCurrentStressGaugeLevel() {
        if (stressRiskGauge != null) {
            return stressRiskGauge.getCurrentStressLevel();
        }
        return getCurrentStressLevelFromScore(0);
    }

    // ปรับปรุง updateResultDisplay() method ให้เรียก updateStressGaugeDisplay()
    private void updateResultDisplay() {
        // ตรวจสอบว่าตอบคำถามครบหรือไม่
        if (!isFormComplete()) {
            // ยังไม่ได้ประเมิน
            if (tv2qResult != null) {
                tv2qResult.setText("-");
                tv2qResult.setBackgroundColor(Color.parseColor("#9E9E9E")); // สีเทา
                tv2qResult.setTextColor(Color.parseColor("#FFFFFF"));
            }

            if (tv2qResultDetail != null) {
                tv2qResultDetail.setText("🤔 ยังไม่ได้ประเมิน");
                tv2qResultDetail.setTextColor(Color.parseColor("#616161"));
                tv2qResultDetail.setBackgroundColor(Color.parseColor("#F5F5F5"));
            }

            // อัปเดต Stress Gauge
            updateStressGaugeDisplay();
            return;
        }

        // ตรวจสอบผลการประเมิน
        boolean hasPositiveAnswer = false;
        String answer1 = stressDepression2qInfo.getQ1();
        String answer2 = stressDepression2qInfo.getQ2();

        // ตรวจสอบว่ามีการตอบ "มี" (รหัส "2") หรือไม่
        if ("2".equals(answer1) || "2".equals(answer2)) {
            hasPositiveAnswer = true;
        }

        String emoji = get2qEmoji(hasPositiveAnswer);

        // อัพเดท tv2qResult
        if (tv2qResult != null) {
            if (hasPositiveAnswer) {
                tv2qResult.setText("ผิดปกติ");
                tv2qResult.setBackgroundColor(Color.parseColor("#E74C3C")); // แดง
                tv2qResult.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                tv2qResult.setText("ปกติ");
                tv2qResult.setBackgroundColor(Color.parseColor("#27AE60")); // เขียว
                tv2qResult.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        // อัพเดท tv2qResultDetail พร้อม emoji
        if (tv2qResultDetail != null) {
            if (hasPositiveAnswer) {
                tv2qResultDetail.setText(emoji + " ผิดปกติ และส่งต่อเจ้าหน้าที่ (1B0211)");
                tv2qResultDetail.setTextColor(Color.parseColor("#FFFFFF"));
                tv2qResultDetail.setBackgroundColor(Color.parseColor("#E74C3C")); // แดง
            } else {
                tv2qResultDetail.setText(emoji + " ปกติ (1B0210)");
                tv2qResultDetail.setTextColor(Color.parseColor("#FFFFFF"));
                tv2qResultDetail.setBackgroundColor(Color.parseColor("#27AE60")); // เขียว
            }
        }

        // อัปเดต Stress Gauge
        updateStressGaugeDisplay();
    }

}