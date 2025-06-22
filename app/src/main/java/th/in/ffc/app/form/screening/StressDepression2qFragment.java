package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression2qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression2qFragment extends Fragment {

    private StressDepression2qLiveData stressDepression2qLiveData;
    private SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepression2qInfo stressDepression2qInfo;

    private ArrayList<Integer> points;
    private RadioGroup rdoStress2qQ1;
    private RadioGroup rdoStress2qQ2;
    private TableLayout resultTable;
    int white;
    int light_gray;
    int highlightColor;
    // เพิ่มตัวแปรสำหรับตรวจสอบข้อมูล
    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false}; // ตรวจสอบว่าตอบคำถามครบหรือไม่

    public StressDepression2qFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stress_depression2q, container, false);

        // ผูก views
        rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);
        resultTable = view.findViewById(R.id.depression2resultTable);

        return view;
    }
    private void initializeViews(View view) {
        rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);
        resultTable = view.findViewById(R.id.depression2resultTable);
        white = ContextCompat.getColor(requireContext(), R.color.white);
        light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
    }
    private void updateTableHighlight() {
        try {
            // รับค่าการเลือกจาก RadioGroup ทั้งสอง
            String answer1 = null;
            String answer2 = null;

            // ตรวจสอบคำตอบข้อ 1
            int checkedId1 = rdoStress2qQ1.getCheckedRadioButtonId();
            if (checkedId1 == R.id.rdoStress2qQ1_1) {
                answer1 = "ไม่มี";
            } else if (checkedId1 == R.id.rdoStress2qQ1_2) {
                answer1 = "มี";
            }

            // ตรวจสอบคำตอบข้อ 2
            int checkedId2 = rdoStress2qQ2.getCheckedRadioButtonId();
            if (checkedId2 == R.id.rdoStress2qQ2_1) {
                answer2 = "ไม่มี";
            } else if (checkedId2 == R.id.rdoStress2qQ2_2) {
                answer2 = "มี";
            }

            // ถ้ายังตอบไม่ครบ ไม่ต้อง highlight
            if (answer1 == null || answer2 == null) {
                clearHighlights();
                return;
            }

            if (resultTable != null && resultTable.getChildCount() >= 3) {
                // ดึง TableRow ที่ต้องการ highlight
                TableRow normalRow = (TableRow) resultTable.getChildAt(1);
                TableRow abnormalRow = (TableRow) resultTable.getChildAt(2);

                // รีเซ็ตสีพื้นหลังเริ่มต้น
                normalRow.setBackgroundColor(white);
                abnormalRow.setBackgroundColor(white);

                // ถ้าตอบ "ไม่มี" ทั้งสองข้อ
                if ("ไม่มี".equals(answer1) && "ไม่มี".equals(answer2)) {
                    normalRow.setBackgroundColor(highlightColor);
                }
                // ถ้ามีการตอบ "มี" อย่างน้อย 1 ข้อ
                else if ("มี".equals(answer1) || "มี".equals(answer2)) {
                    abnormalRow.setBackgroundColor(highlightColor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ตรวจสอบความถูกต้องของข้อมูลและบันทึกข้อมูล
     */
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
        if (!missingQuestions.isEmpty()) {
//            Toast.makeText(getContext(),
//                    "กรุณาตอบคำถามให้ครบถ้วน: " + missingQuestions,
//                    Toast.LENGTH_SHORT).show();
        }
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
    private void clearHighlights() {
        // ล้าง highlight ทั้งหมด
        TableRow normalRow = (TableRow) resultTable.getChildAt(1);
        TableRow abnormalRow = (TableRow) resultTable.getChildAt(2);
        normalRow.setBackgroundColor(white);
        abnormalRow.setBackgroundColor(light_gray);
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
                validateAndSaveData(); // เรียกใช้การตรวจสอบและบันทึก

                updatePoints();
                updateTableHighlight();
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
                updateTableHighlight();
            }
        });
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
    // เพิ่มเมธอด validation ใน StressDepression2qFragment class

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าตอบคำถามครบหรือไม่
        ArrayList<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q): ยังไม่ได้ตอบข้อ ");

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

        // ล้าง highlight ในตาราง
        clearHighlights();
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
        return isFormComplete() && isAtRisk();
    }

    /**
     * ดึงข้อความแนะนำให้ทำ 9Q
     */
    public String get9QRecommendationText() {
        if (shouldDo9QAssessment()) {
            return "⚠️ แนะนำให้ทำแบบประเมิน 9Q เพิ่มเติม เนื่องจากพบความเสี่ยงต่อภาวะซึมเศร้า";
        }
        return "";
    }

}