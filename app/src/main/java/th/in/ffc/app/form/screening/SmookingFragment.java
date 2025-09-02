package th.in.ffc.app.form.screening;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.ScreeningResultCodeDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.view.SmokingRiskGaugeView;
import th.in.ffc.provider.ScreeningResultCode;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmookingFragment extends Fragment {

    private static final String TAG = "SmookingFragment";

    SharedViewModel shareViewModel;
    SmookingLiveData smookingLiveData;
    SfSmokerInfoDao sfSmokerInfoDao;

    private OnDataPass dataPasser;
    private SmokerInfo smokerInfo;
    private RadioGroup rdoSmokerGroup;
    private RadioGroup rdoSmokerAssist;
    private RadioGroup rdoSmokerRegularly;
    private RadioButton rdoSmokerGroup1;
    private RadioButton rdoSmokerGroup2;
    private RadioButton rdoSmokerGroup3;
    private RadioButton rdoSmokerAssist1;
    private RadioButton rdoSmokerAssist2;
    private RadioButton rdoSmokerAssist3;
    private RadioButton rdoSmokerRegularly1;
    private RadioButton rdoSmokerRegularly2;
    private RadioButton rdoSmokerRegularly3;

    // เพิ่มตัวแปรสำหรับแสดงคะแนน
    private TextView tvSmokingScore;
    private TextView tvSmokingRiskLevel;
    private boolean isUpdatingFromCode = false;

    // เพิ่มตัวแปรสำหรับปุ่ม info
    private ImageView ivInfoButton;

    // เพิ่มตัวแปรสำหรับ ScreeningResultCode
    private ScreeningResultCodeDao screeningResultCodeDao;
    private int currentPersonId = -1;
    private int currentVisitNo = -1;

    private SmokingRiskGaugeView smokingRiskGauge;
    private TextView tvGaugeEmoji;
    private TextView tvGaugeScore;
    private TextView tvGaugeLevel;
    private TextView tvGaugeCode;
    private TextView tvGaugeRecommendation;
    private SeekBar seekBarGaugeTest;

    private boolean isUpdatingFromScore = false;

    public SmookingFragment() {
        // Required empty public constructor
    }

    public static SmookingFragment newInstance(String param1, String param2) {
        SmookingFragment fragment = new SmookingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (shareViewModel == null) {
            StressDepression9qLiveData stressDepression9qLiveData = new StressDepression9qLiveData();
            shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
        }
        smookingLiveData = new SmookingLiveData();
        screeningResultCodeDao = new ScreeningResultCodeDao(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_smooking, container, false);
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
        initializeViews(view);
        setupListeners();
        loadData();

        // สังเกตการเปลี่ยนแปลงข้อมูลจาก ViewModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        initializeViews(view);
        initializeGaugeViews(view);
        setupListeners();
        loadData();

        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                currentPersonId = Integer.parseInt(personInfo.getId());
                if (personInfo.getVisitNo() != null && !personInfo.getVisitNo().isEmpty()) {
                    currentVisitNo = Integer.parseInt(personInfo.getVisitNo());
                }
                // ดึงข้อมูลคะแนนการสูบบุหรี่จาก SfDrugsDao
                loadSmokingScore(personInfo.getId());

                // ตรวจสอบข้อมูลที่มีอยู่แล้วใน ScreeningResultCode
                if (personInfo.getVisitNo() != null && !personInfo.getVisitNo().isEmpty()) {
                    loadFromScreeningResultCode(Integer.valueOf(personInfo.getId()), Integer.valueOf(personInfo.getVisitNo()));
                }
            }
        });

        // สังเกตคะแนน nicotine จาก AssistScore
//        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
//            if (data != null && data.getPersonId() != null && data.getNicotineScore() != null && !isUpdatingFromScore) {
//                try {
//                    int nicotineScore = Integer.parseInt(data.getNicotineScore());
//                    syncSmokingScoreFromAssistFragment(nicotineScore);
//                } catch (NumberFormatException e) {
//                    Log.e(TAG, "ไม่สามารถแปลงคะแนน nicotine เป็นตัวเลขได้: " + data.getNicotineScore());
//                    syncSmokingScoreFromAssistFragment(0);
//                }
//            }
//        });
    }
    private void initializeGaugeViews(View view) {
        smokingRiskGauge = view.findViewById(R.id.smokingRiskGauge);
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
    public void showGaugeTestControls(boolean show) {
        View layoutGaugeControl = getView().findViewById(R.id.layoutGaugeControl);
        if (layoutGaugeControl != null) {
            layoutGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    public void resetGauge() {
        if (smokingRiskGauge != null) {
            smokingRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }

    private void setupGaugeTestControls() {
        if (seekBarGaugeTest != null) {
            seekBarGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && smokingRiskGauge != null) {
                        smokingRiskGauge.setScore(progress);
                        SmokingRiskGaugeView.RiskLevel level = getCurrentRiskLevelFromScore(progress);

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

    private void calculateAndSyncSmokingScore() {
        try {
            int totalScore = calculateCurrentScore();

            // อัปเดต UI ใน SmookingFragment (รวมถึง gauge)
            updateSmokingScore(totalScore);

            // ส่งคะแนนไปยัง ViewModel เพื่อให้ AssistScoreFragment ใช้แสดงใน tvScoreA
            updateSmokingScoreInViewModel(totalScore);

            Log.d(TAG, "คะแนนการสูบบุหรี่รวม (ซิงค์): " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการคำนวณและซิงค์คะแนนการสูบบุหรี่: " + e.getMessage());
            displayScoreError();
        }
    }
    private void syncSmokingScoreFromAssistFragment(int score) {
        // แสดงคะแนนจาก AssistScoreFragment ใน tvSmokingScore
        if (tvSmokingScore != null) {
            String currentScore = tvSmokingScore.getText().toString();
            if (!currentScore.equals(String.valueOf(score))) {
                tvSmokingScore.setText(String.valueOf(score));

                // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
                updateScoreBackground(score);

                Log.d(TAG, "ซิงค์คะแนนจาก AssistScoreFragment ใน tvSmokingScore: " + score);
            }
        }

        // อัพเดตระดับความเสี่ยงและสีฟอนต์
        updateRiskLevelDisplay(score);

        // อัปเดต Gauge ด้วยคะแนนใหม่ (เพิ่มบรรทัดนี้)
        updateGaugeWithScore(score);

        Log.d(TAG, "อัพเดตระดับความเสี่ยงและ Gauge จากการซิงค์: " + score);
    }
    private void updateScoreBackground(int score) {
        if (tvSmokingScore == null) return;

        if (score >= 0 && score <= 3) {
            // ไม่มีความเสี่ยง - สีเขียว
            tvSmokingScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
            tvSmokingScore.setTextColor(Color.WHITE);
        } else if (score >= 4 && score <= 26) {
            // ความเสี่ยงปานกลาง - สีส้ม
            tvSmokingScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
            tvSmokingScore.setTextColor(Color.WHITE);
        } else if (score >= 27) {
            // ความเสี่ยงสูง - สีแดง
            tvSmokingScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
            tvSmokingScore.setTextColor(Color.WHITE);
        }
    }

    /**
     * อัปเดตการแสดงผลระดับความเสี่ยง
     */
    private void updateRiskLevelDisplay(int score) {
        if (tvSmokingRiskLevel == null) return;

        String emoji = getSmokingEmoji(score);
        String riskLevel;

        // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์ พร้อม emoji
        if (score >= 0 && score <= 3) {
            riskLevel = emoji + " ไม่มีความเสี่ยง";
            tvSmokingRiskLevel.setBackgroundResource(R.color.light_green);
            tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
        } else if (score >= 4 && score <= 26) {
            riskLevel = emoji + " ความเสี่ยงปานกลาง";
            tvSmokingRiskLevel.setBackgroundResource(R.color.light_orange);
            tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_orange));
        } else if (score >= 27) {
            riskLevel = emoji + " ความเสี่ยงสูง ต้องเลิกสูบ";
            tvSmokingRiskLevel.setBackgroundResource(R.color.light_red);
            tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
        } else {
            riskLevel = "😐 ยังไม่ได้ประเมิน";
            tvSmokingRiskLevel.setBackgroundResource(R.color.light_gray);
            tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
        }

        tvSmokingRiskLevel.setText(riskLevel);
    }
    /**
     * แสดงข้อผิดพลาดในการคำนวณคะแนน
     */
    private void displayScoreError() {
        if (tvSmokingScore != null) {
            tvSmokingScore.setText("-");
            tvSmokingScore.setBackgroundResource(R.color.light_gray);
            tvSmokingScore.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (tvSmokingRiskLevel != null) {
            tvSmokingRiskLevel.setText("😕 ไม่สามารถคำนวณได้");
            tvSmokingRiskLevel.setBackgroundResource(R.color.light_gray);
        }

        // รีเซ็ต gauge เป็น 0 (เพิ่มบรรทัดนี้)
        if (smokingRiskGauge != null) {
            smokingRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }
    /**
     * อัปเดต ViewModel กับคะแนนการสูบบุหรี่
     */
    private void updateSmokingScoreInViewModel(int score) {
        try {
            isUpdatingFromScore = true;

            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            // สร้าง AssistScore object ใหม่หรือใช้ที่มีอยู่
            th.in.ffc.app.form.screening.model.AssistScore assistScore =
                    new th.in.ffc.app.form.screening.model.AssistScore();

            // ตั้งค่าข้อมูลพื้นฐาน
            viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
                if (personInfo != null && personInfo.getId() != null) {
                    assistScore.setPersonId(personInfo.getId());
                    assistScore.setNicotineScore(String.valueOf(score));

                    // รักษาคะแนนแอลกอฮอล์ที่มีอยู่แล้ว (ถ้ามี)
                    th.in.ffc.app.form.screening.model.AssistScore currentScore =
                            viewModel.getAssistScoreMutableLiveData().getValue();
                    if (currentScore != null && currentScore.getAlcoholScore() != null) {
                        assistScore.setAlcoholScore(currentScore.getAlcoholScore());
                    }

                    // อัพเดตใน ViewModel
                    viewModel.setAssistScoreMutableLiveData(assistScore);

                    Log.d(TAG, "ส่งคะแนนการสูบบุหรี่ไป AssistScoreFragment ผ่าน ViewModel: " + score);

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

    private void initializeViews(View view) {
        smokerInfo = new SmokerInfo();
        sfSmokerInfoDao = new SfSmokerInfoDao(getContext());
        rdoSmokerGroup = view.findViewById(R.id.rdoSmokerGroup);
        rdoSmokerAssist = view.findViewById(R.id.rdoSmokerAssist);
        rdoSmokerRegularly = view.findViewById(R.id.rdoSmokerRegularly);
        rdoSmokerGroup1 = view.findViewById(R.id.rdoSmokerGroup1);
        rdoSmokerGroup2 = view.findViewById(R.id.rdoSmokerGroup2);
        rdoSmokerGroup3 = view.findViewById(R.id.rdoSmokerGroup3);
        rdoSmokerAssist1 = view.findViewById(R.id.rdoSmokerAssist1);
        rdoSmokerAssist2 = view.findViewById(R.id.rdoSmokerAssist2);
        rdoSmokerAssist3 = view.findViewById(R.id.rdoSmokerAssist3);
        rdoSmokerRegularly1 = view.findViewById(R.id.rdoSmokerRegularly1);
        rdoSmokerRegularly2 = view.findViewById(R.id.rdoSmokerRegularly2);
        rdoSmokerRegularly3 = view.findViewById(R.id.rdoSmokerRegularly3);

        // เชื่อมโยง TextView สำหรับแสดงคะแนน
        tvSmokingScore = view.findViewById(R.id.tvSmokingScore);
        tvSmokingRiskLevel = view.findViewById(R.id.tvSmokingRiskLevel);

        // เชื่อมโยงปุ่ม info
        ivInfoButton = view.findViewById(R.id.ivInfoButton);
    }
    /**
     * อัปเดต Gauge display ด้วยคะแนนปัจจุบัน
     */
    private void updateGaugeDisplay() {
        if (smokingRiskGauge == null) return;

        // ดึงคะแนนจาก TextView แทนการใช้ getCurrentSmokingScore()
        int totalScore = 0;
        if (tvSmokingScore != null && !tvSmokingScore.getText().toString().equals("-")) {
            try {
                totalScore = Integer.parseInt(tvSmokingScore.getText().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "ไม่สามารถแปลงคะแนนเป็นตัวเลขได้");
                totalScore = 0;
            }
        }

        SmokingRiskGaugeView.RiskLevel currentLevel = getCurrentRiskLevelFromScore(totalScore);

        // อัปเดต Gauge
        smokingRiskGauge.setScore(totalScore);

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

    public int getCurrentSmokingScore() {
        if (tvSmokingScore != null && !tvSmokingScore.getText().toString().equals("-")) {
            try {
                return Integer.parseInt(tvSmokingScore.getText().toString());
            } catch (NumberFormatException e) {
                Log.e(TAG, "ไม่สามารถแปลงคะแนนการสูบบุหรี่เป็นตัวเลขได้");
            }
        }
        return 0;
    }
    private SmokingRiskGaugeView.RiskLevel getCurrentRiskLevelFromScore(int score) {
        if (score >= 0 && score <= 3) {
            return new SmokingRiskGaugeView.RiskLevel(0, 3, "ไม่มีความเสี่ยง", "#27AE60", "😊", "1B520");
        } else if (score >= 4 && score <= 26) {
            return new SmokingRiskGaugeView.RiskLevel(4, 27, "ความเสี่ยงปานกลาง", "#F39C12", "🙂", "1B522");
        } else if (score >= 27 ) {
            return new SmokingRiskGaugeView.RiskLevel(28, 33, "ความเสี่ยงสูง", "#E67E22", "😟", "1B523");
        } else {
            return new SmokingRiskGaugeView.RiskLevel(21, 30, "ความเสี่ยงสูงมาก", "#E67E22", "😰", "1B523");
        }
    }

    /**
     * อัปเดต Gauge ด้วยคะแนนที่ระบุ
     */
    public void updateGaugeWithScore(int score) {
        if (smokingRiskGauge != null) {
            smokingRiskGauge.setScore(score);

            // อัปเดตข้อความทั้งหมดด้วย
            SmokingRiskGaugeView.RiskLevel currentLevel = getCurrentRiskLevelFromScore(score);

            if (tvGaugeEmoji != null) tvGaugeEmoji.setText(currentLevel.emoji);
            if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + score);
            if (tvGaugeLevel != null) {
                tvGaugeLevel.setText(currentLevel.label);
                tvGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
            }
            if (tvGaugeCode != null) tvGaugeCode.setText(currentLevel.code);

            Log.d(TAG, "อัปเดต Gauge ด้วยคะแนน: " + score);
        }
    }

    /**
     * ดึงระดับ Gauge ปัจจุบัน
     */
    public SmokingRiskGaugeView.RiskLevel getCurrentGaugeLevel() {
        if (smokingRiskGauge != null) {
            return smokingRiskGauge.getCurrentRiskLevel();
        }
        return getCurrentRiskLevelFromScore(0);
    }

    private void setupListeners() {
        ivInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmokingCriteriaDialog();
            }
        });

        rdoSmokerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromCode) return;

                String data = "";
                if (checkedId == R.id.rdoSmokerGroup1) {
                    data = "1";
                    clearSubsequentSelections();
                } else if (checkedId == R.id.rdoSmokerGroup2) {
                    data = "2";
                    clearSubsequentSelections();
                } else if (checkedId == R.id.rdoSmokerGroup3) {
                    data = "3";
                    enableAssistOptions();
                    clearRegularlySelection();
                }

                smokerInfo.setSmokerGroup(data);
                smookingLiveData.setSelectedRdoSmokerGroup(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                dataPasser.onSmokerInfo(smokerInfo);

//                calculateAndSyncSmokingScore();

                Log.d(TAG, "Selected SmokerGroup: " + data + ", Form Complete: " + isFormComplete());
            }
        });

        rdoSmokerAssist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromCode) return;

                String data = "";
                if (checkedId == R.id.rdoSmokerAssist1) {
                    data = "1";
                    clearRegularlySelection();
                } else if (checkedId == R.id.rdoSmokerAssist2) {
                    data = "2";
                    clearRegularlySelection();
                } else if (checkedId == R.id.rdoSmokerAssist3) {
                    data = "3";
                    // ไม่ล้าง regularly เพราะต้องให้เลือกต่อ
                }

                smokerInfo.setSmokerAssist(data);
                smookingLiveData.setSelectedRdoSmokerAssist(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                dataPasser.onSmokerInfo(smokerInfo);

//                calculateAndSyncSmokingScore();

                Log.d(TAG, "Selected SmokerAssist: " + data + ", Form Complete: " + isFormComplete());
            }
        });

        rdoSmokerRegularly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromCode) return;

                String data = "";
                if (checkedId == R.id.rdoSmokerRegularly1) {
                    data = "1";
                } else if (checkedId == R.id.rdoSmokerRegularly2) {
                    data = "2";
                } else if (checkedId == R.id.rdoSmokerRegularly3) {
                    data = "3";
                }

                smokerInfo.setSmokerRegularly(data);
                smookingLiveData.setSelectedRdoSmokerRegularly(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                dataPasser.onSmokerInfo(smokerInfo);
//                calculateAndSyncSmokingScore();

                Log.d(TAG, "Selected SmokerRegularly: " + data + ", Form Complete: " + isFormComplete());
            }
        });

        shareViewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), smookingLiveData -> {
            if (smookingLiveData != null && !isUpdatingFromCode) {
                isUpdatingFromCode = true;
                try {
                    if (smookingLiveData.getSelectedRdoSmokerGroup() != null) {
                        rdoSmokerGroup.check(smookingLiveData.getSelectedRdoSmokerGroup());
                    }
                    if (smookingLiveData.getSelectedRdoSmokerAssist() != null) {
                        rdoSmokerAssist.check(smookingLiveData.getSelectedRdoSmokerAssist());
                    }
                    if (smookingLiveData.getSelectedRdoSmokerRegularly() != null) {
                        rdoSmokerRegularly.check(smookingLiveData.getSelectedRdoSmokerRegularly());
                    }
//                    calculateAndSyncSmokingScore();
                } finally {
                    isUpdatingFromCode = false;
                }
            }
        });
    }

    /**
     * แสดง Dialog เกณฑ์การประเมินบุหรี่
     */
    private void showSmokingCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // สร้าง custom layout สำหรับ dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_smoking_criteria, null);

        builder.setView(dialogView);
//        builder.setTitle("เกณฑ์การประเมินบุหรี่");
        builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void clearSubsequentSelections() {
        isUpdatingFromCode = true;
        rdoSmokerAssist.clearCheck();
        rdoSmokerRegularly.clearCheck();

        rdoSmokerAssist1.setEnabled(false);
        rdoSmokerAssist2.setEnabled(false);
        rdoSmokerAssist3.setEnabled(false);

        rdoSmokerRegularly1.setEnabled(false);
        rdoSmokerRegularly2.setEnabled(false);
        rdoSmokerRegularly3.setEnabled(false);

        isUpdatingFromCode = false;

        smokerInfo.setSmokerAssist("");
        smokerInfo.setSmokerRegularly("");
        smookingLiveData.setSelectedRdoSmokerAssist(null);
        smookingLiveData.setSelectedRdoSmokerRegularly(null);
    }

    private void enableAssistOptions() {
        rdoSmokerAssist1.setEnabled(true);
        rdoSmokerAssist2.setEnabled(true);
        rdoSmokerAssist3.setEnabled(true);

        rdoSmokerRegularly1.setEnabled(true);
        rdoSmokerRegularly2.setEnabled(true);
        rdoSmokerRegularly3.setEnabled(true);
    }

    private void clearRegularlySelection() {
        isUpdatingFromCode = true;
        rdoSmokerRegularly.clearCheck();
        isUpdatingFromCode = false;

        smokerInfo.setSmokerRegularly("");
        smookingLiveData.setSelectedRdoSmokerRegularly(null);
    }

    /**
     * บันทึกผลการประเมินความเสี่ยงจากการสูบบุหรี่
     */
    public Uri saveSmokingResult(int personId, int visitno, String userCreate) {
        try {
            // สร้าง ScreeningResultData
            ScreeningResultCodeDao.ScreeningResultData data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_SMOKING_RISK;
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode และข้อมูลอื่นๆ ตามการเลือก
            setResultCodeFromSelection(data);

            // บันทึกข้อมูล
            Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d(TAG, "บันทึกผลการประเมินการสูบบุหรี่สำเร็จ: " + result.toString());
                Log.d(TAG, "รายละเอียด: personId=" + personId + ", visitno=" + visitno +
                        ", resultCode=" + data.resultCode + ", description=" + data.resultDescription);
                return result;
            } else {
                Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผลการประเมินการสูบบุหรี่");
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกผลการประเมินการสูบบุหรี่");
            return null;
        }
    }

    private void setResultCodeFromSelection(ScreeningResultCodeDao.ScreeningResultData data) {
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            return;
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        // ตรวจสอบตามลำดับการเลือก
        if ("1".equals(smokerGroup)) {
            // ไม่เคยสูบบุหรี่
            data.resultCode = "1B520"; // ใส่ code ที่ถูกต้องตาม RadioButton
            data.resultDescription = "ไม่เคยสูบบุหรี่";
            data.totalScore = 0;
            data.riskLevel = ScreeningResultCode.RISK_NORMAL;
            data.isAbnormal = false;
            data.recommendation = "ควรรักษาสถานะไม่สูบบุหรี่ต่อไป";

        } else if ("2".equals(smokerGroup)) {
            // เคยสูบ แต่ไม่ใช่ใน 3 เดือนที่ผ่านมา
            data.resultCode = "1B521"; // ใส่ code ที่ถูกต้องตาม RadioButton
            data.resultDescription = "เคยสูบบุหรี่ แต่ไม่ใช่ใน 3 เดือนที่ผ่านมา";
            data.totalScore = 2;
            data.riskLevel = ScreeningResultCode.RISK_LOW;
            data.isAbnormal = false;
            data.recommendation = "ดีที่เลิกสูบได้แล้ว ควรรักษาสถานะนี้ต่อไป";

        } else if ("3".equals(smokerGroup)) {
            // สูบบุหรี่เป็นประจำ - ต้องดูความถี่
            if (smokerAssist == null || smokerAssist.isEmpty()) {
                // ยังไม่ได้เลือกความถี่
                data.resultCode = "1B522"; // default สำหรับสูบเป็นประจำ
                data.resultDescription = "สูบบุหรี่เป็นประจำ (ยังไม่ระบุความถี่)";
                data.totalScore = 4;
                data.riskLevel = ScreeningResultCode.RISK_MODERATE;
                data.isAbnormal = true;
                data.recommendation = "ควรเลิกสูบบุหรี่และปรึกษาแพทย์";

            } else if ("1".equals(smokerAssist)) {
                // บางครั้ง บางคราว
                data.resultCode = "1B5221"; // ใส่ code ที่ถูกต้องตาม RadioButton
                data.resultDescription = "สูบบุหรี่บางครั้ง บางคราว";
                data.totalScore = 6;
                data.riskLevel = ScreeningResultCode.RISK_MODERATE;
                data.isAbnormal = true;
                data.recommendation = "ควรลดการสูบและหาวิธีเลิกสูบบุหรี่";

            } else if ("2".equals(smokerAssist)) {
                // บางครั้ง บางคราว
                data.resultCode = "1B5222"; // ใส่ code ที่ถูกต้องตาม RadioButton
                data.resultDescription = "สูบบุหรี่บางครั้ง บางคราว";
                data.totalScore = 7;
                data.riskLevel = ScreeningResultCode.RISK_MODERATE;
                data.isAbnormal = true;
                data.recommendation = "ควรลดการสูบและหาวิธีเลิกสูบบุหรี่";

            } else if ("3".equals(smokerAssist)) {
                // สูบเป็นประจำ - ต้องดูการให้คำแนะนำ
                if (smokerRegularly == null || smokerRegularly.isEmpty()) {
                    // ยังไม่ได้เลือกการให้คำแนะนำ
                    data.resultCode = "1B5223"; // default สำหรับสูบเป็นประจำ
                    data.resultDescription = "สูบบุหรี่เป็นประจำ (ยังไม่ระบุการให้คำแนะนำ)";
                    data.totalScore = 8;
                    data.riskLevel = ScreeningResultCode.RISK_HIGH;
                    data.isAbnormal = true;
                    data.recommendation = "ต้องเลิกสูบบุหรี่ทันที และปรึกษาแพทย์";

                } else if ("1".equals(smokerRegularly)) {
                    // ให้คำแนะนำ/ปรึกษา แบบที่ 1
                    data.resultCode = "1B52231"; // ใส่ code ที่ถูกต้องตาม RadioButton
                    data.resultDescription = "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 1";
                    data.totalScore = 9;
                    data.riskLevel = ScreeningResultCode.RISK_HIGH;
                    data.isAbnormal = true;
                    data.recommendation = "ต้องเลิกสูบบุหรี่ทันที พร้อมติดตามอย่างใกล้ชิด";

                } else if ("2".equals(smokerRegularly)) {
                    // ให้คำแนะนำ/ปรึกษา แบบที่ 2
                    data.resultCode = "1B52232"; // ใส่ code ที่ถูกต้องตาม RadioButton
                    data.resultDescription = "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 2";
                    data.totalScore = 10;
                    data.riskLevel = ScreeningResultCode.RISK_HIGH;
                    data.isAbnormal = true;
                    data.recommendation = "ต้องเลิกสูบบุหรี่ทันที พร้อมการรักษาเพิ่มเติม";

                } else if ("3".equals(smokerRegularly)) {
                    // ให้คำแนะนำ/ปรึกษา แบบที่ 3
                    data.resultCode = "1B52233"; // ใส่ code ที่ถูกต้องตาม RadioButton
                    data.resultDescription = "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 3";
                    data.totalScore = 12;
                    data.riskLevel = ScreeningResultCode.RISK_VERY_HIGH;
                    data.isAbnormal = true;
                    data.recommendation = "ต้องเลิกสูบบุหรี่ทันที พร้อมการรักษาแบบเร่งด่วน";
                }
            }
        }
    }

    public boolean saveSmokingResults(int personId, int visitno, String userCreate) {
        try {
            boolean success = true;

            // บันทึก Record 1: สถานะการสูบบุหรี่
            Uri result1 = saveSmokingStatusResult(personId, visitno, userCreate);
            if (result1 == null) {
                success = false;
                Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกสถานะการสูบบุหรี่");
            }

            // บันทึก Record 2: การให้คำแนะนำ (เฉพาะกรณีที่สูบบุหรี่)
            if ("3".equals(smokerInfo.getSmokerGroup()) &&
                    "3".equals(smokerInfo.getSmokerAssist()) &&
                    smokerInfo.getSmokerRegularly() != null &&
                    !smokerInfo.getSmokerRegularly().isEmpty()) {

                Uri result2 = saveSmokingAdviceResult(personId, visitno, userCreate);
                if (result2 == null) {
                    success = false;
                    Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกการให้คำแนะนำ");
                }
            }

            if (success) {
                Log.d(TAG, "บันทึกผลการประเมินการสูบบุหรี่สำเร็จทั้งหมด");
            }

            return success;

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกผลการประเมินการสูบบุหรี่");
            return false;
        }
    }

    private Uri saveSmokingAdviceResult(int personId, int visitno, String userCreate) {
        try {
            ScreeningResultCodeDao.ScreeningResultData data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_SMOKING_ADVICE; // ต้องเพิ่มใน ScreeningResultCode
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode ตามการให้คำแนะนำ
            String smokerRegularly = smokerInfo.getSmokerRegularly();
            if ("1".equals(smokerRegularly)) {
                data.resultCode = "1B530";
                data.resultDescription = "การให้คำแนะนำแบบที่ 1";
                data.totalScore = 1;
                data.riskLevel = ScreeningResultCode.RISK_MODERATE;
                data.isAbnormal = true;
                data.recommendation = "ให้คำแนะนำเกี่ยวกับการเลิกสูบบุหรี่";

            } else if ("2".equals(smokerRegularly)) {
                data.resultCode = "1B531";
                data.resultDescription = "การให้คำแนะนำแบบที่ 2";
                data.totalScore = 2;
                data.riskLevel = ScreeningResultCode.RISK_HIGH;
                data.isAbnormal = true;
                data.recommendation = "ให้คำแนะนำและติดตามการเลิกสูบบุหรี่";

            } else if ("3".equals(smokerRegularly)) {
                data.resultCode = "1B532";
                data.resultDescription = "การให้คำแนะนำแบบที่ 3";
                data.totalScore = 3;
                data.riskLevel = ScreeningResultCode.RISK_VERY_HIGH;
                data.isAbnormal = true;
                data.recommendation = "ให้คำแนะนำเร่งด่วนและส่งต่อผู้เชี่ยวชาญ";
            }

            Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d(TAG, "บันทึกการให้คำแนะนำสำเร็จ: " + data.resultCode + " - " + data.resultDescription);
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกการให้คำแนะนำ");
            return null;
        }
    }

    private Uri saveSmokingStatusResult(int personId, int visitno, String userCreate) {
        try {
            ScreeningResultCodeDao.ScreeningResultData data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_SMOKING_STATUS; // ต้องเพิ่มใน ScreeningResultCode
            data.screeningDate = DateConverter.getCurrentWesternDateTime();
            data.status = ScreeningResultCode.STATUS_ACTIVE;
            data.userCreate = userCreate;
            data.userUpdate = userCreate;

            // กำหนด resultCode ตามสถานะการสูบ
            String smokerGroup = smokerInfo.getSmokerGroup();
            if ("1".equals(smokerGroup)) {
                // ไม่สูบ
                data.resultCode = "1B52";
                data.resultDescription = "ไม่สูบบุหรี่";
                data.totalScore = 0;
                data.riskLevel = ScreeningResultCode.RISK_NORMAL;
                data.isAbnormal = false;
                data.recommendation = "ควรรักษาสถานะไม่สูบบุหรี่ต่อไป";

            } else if ("2".equals(smokerGroup)) {
                // เคยสูบบุหรี่แต่เลิกแล้ว
                data.resultCode = "1B51";
                data.resultDescription = "เคยสูบบุหรี่แต่เลิกแล้ว";
                data.totalScore = 0;
                data.riskLevel = ScreeningResultCode.RISK_NORMAL;
                data.isAbnormal = false;
                data.recommendation = "ดีที่เลิกสูบได้แล้ว ควรรักษาสถานะนี้ต่อไป";

            } else if ("3".equals(smokerGroup)) {
                // สูบบุหรี่
                data.resultCode = "1B51";
                data.resultDescription = "สูบบุหรี่";
                data.totalScore = 1;
                data.riskLevel = ScreeningResultCode.RISK_HIGH;
                data.isAbnormal = true;
                data.recommendation = "ควรเลิกสูบบุหรี่และปรึกษาแพทย์";
            }

            Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d(TAG, "บันทึกสถานะการสูบบุหรี่สำเร็จ: " + data.resultCode + " - " + data.resultDescription);
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกสถานะการสูบบุหรี่");
            return null;
        }
    }

    /**
     * กำหนด resultCode และ resultDescription ตามคะแนนความเสี่ยงจากการสูบบุหรี่
     */
    private void setResultCodeAndDescriptionSmoking(ScreeningResultCodeDao.ScreeningResultData data, int riskScore) {
        if (riskScore >= 0 && riskScore <= 3) {
            data.resultCode = "1B140"; // ไม่มีความเสี่ยง
            data.resultDescription = "ไม่มีความเสี่ยงจากการสูบบุหรี่";
        } else if (riskScore >= 4 && riskScore <= 26) {
            data.resultCode = "1B141"; // ความเสี่ยงปานกลาง
            data.resultDescription = "มีความเสี่ยงจากการสูบบุหรี่ระดับปานกลาง";
        } else {
            data.resultCode = "1B142"; // ความเสี่ยงสูง
            data.resultDescription = "มีความเสี่ยงจากการสูบบุหรี่ระดับสูง";
        }
    }

    /**
     * กำหนด riskLevel และ isAbnormal ตามคะแนนความเสี่ยงจากการสูบบุหรี่
     */
    private void setRiskLevelAndAbnormalSmoking(ScreeningResultCodeDao.ScreeningResultData data, int riskScore) {
        if (riskScore >= 0 && riskScore <= 3) {
            data.riskLevel = ScreeningResultCode.RISK_NORMAL;
            data.isAbnormal = false;
        } else if (riskScore >= 4 && riskScore <= 26) {
            data.riskLevel = ScreeningResultCode.RISK_MODERATE;
            data.isAbnormal = true;
        } else {
            data.riskLevel = ScreeningResultCode.RISK_HIGH;
            data.isAbnormal = true;
        }
    }

    /**
     * กำหนดคำแนะนำตามคะแนนความเสี่ยง
     */
    private String getRecommendationSmoking(int riskScore) {
        if (riskScore >= 0 && riskScore <= 3) {
            return "ไม่มีความเสี่ยงจากการสูบบุหรี่ ควรรักษาสถานะนี้ต่อไป";
        } else if (riskScore >= 4 && riskScore <= 26) {
            return "มีความเสี่ยงปานกลาง ควรได้รับคำแนะนำเกี่ยวกับการเลิกสูบบุหรี่";
        } else {
            return "มีความเสี่ยงสูง ควรเลิกสูบบุหรี่ทันที และปรึกษาแพทย์เพื่อขอคำแนะนำ";
        }
    }

    /**
     * บันทึกข้อมูลลง ScreeningResultCode (เมธอดหลัก)
     */
    public boolean saveToScreeningResultCode(int personId, int visitno, String userCreate) {
        try {
            if (!isFormComplete()) {
                Log.e(TAG, "ไม่สามารถบันทึกได้ - ข้อมูลไม่ครบถ้วน");
                return false;
            }

            boolean result = saveSmokingResults(personId, visitno, userCreate);

            if (result) {
                Log.d(TAG, "บันทึกผลการประเมินการสูบบุหรี่สำเร็จ");
                return true;
            } else {
                Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกผลการประเมินการสูบบุหรี่");
                return false;
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ในการบันทึกผลการประเมินการสูบบุหรี่");
            return false;
        }
    }

    /**
     * โหลดข้อมูลจาก ScreeningResultCode
     */
    public void loadFromScreeningResultCode(int personId, int visitno) {
        try {
            // โหลด Record 1: สถานะการสูบบุหรี่
            ScreeningResultCodeDao.ScreeningResultData statusData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_SMOKING_STATUS);

            // โหลด Record 2: การให้คำแนะนำ
            ScreeningResultCodeDao.ScreeningResultData adviceData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_SMOKING_ADVICE);

            if (statusData != null) {
                Log.d(TAG, "พบข้อมูลสถานะการสูบบุหรี่เดิม: " + statusData.resultCode + " - " + statusData.resultDescription);

                if (adviceData != null) {
                    Log.d(TAG, "พบข้อมูลการให้คำแนะนำเดิม: " + adviceData.resultCode + " - " + adviceData.resultDescription);

//                    Toast.makeText(getContext(),
//                            "โหลดข้อมูลการประเมินการสูบบุหรี่เดิม: " + statusData.resultDescription +
//                                    " และ " + adviceData.resultDescription,
//                            Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(getContext(),
//                            "โหลดข้อมูลการประเมินการสูบบุหรี่เดิม: " + statusData.resultDescription,
//                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "ไม่พบข้อมูลการประเมินการสูบบุหรี่เดิม");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดข้อมูลการประเมินการสูบบุหรี่");
        }
    }

    /**
     * ตรวจสอบว่ามีข้อมูลเดิมหรือไม่
     */
    public boolean hasExistingData(int personId, int visitno) {
        try {
            ScreeningResultCodeDao.ScreeningResultData statusData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_SMOKING_STATUS);
            return statusData != null;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบข้อมูลเดิม");
            return false;
        }
    }

    /**
     * แสดงผลการบันทึก
     */
    public void showSaveResult(boolean success, String message) {
        if (success) {
            Toast.makeText(getContext(),
                    "✅ บันทึกผลการประเมินการสูบบุหรี่สำเร็จ",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "❌ เกิดข้อผิดพลาดในการบันทึก: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * โหลดคะแนนการสูบบุหรี่จาก SfDrugsDao
     */
    private void loadSmokingScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                if (summaryMap.containsKey("a")) {
                    totalScore += summaryMap.get("a");
                }
            }

            updateSmokingScore(totalScore); // เรียก updateSmokingScore ที่จะอัปเดต gauge ด้วย
            Log.d(TAG, "โหลดคะแนนการสูบบุหรี่สำเร็จ: " + totalScore);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดคะแนน: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * คำนวณคะแนนจากการเลือกปัจจุบัน
     */
    private int calculateCurrentScore() {
        int score = 0;

        if (smokerInfo != null) {
            // Q2: ความถี่การใช้ (SmokerGroup)
            if (smokerInfo.getSmokerGroup() != null) {
                switch (smokerInfo.getSmokerGroup()) {
                    case "1": score += 0; break;  // ไม่เคย
                    case "2": score += 2; break;  // เคย แต่ไม่ใช่ใน 3 เดือนที่ผ่านมา
                    case "3": score += 4; break;  // ใช้ใน 3 เดือนที่ผ่านมา
                }
            }

            // Q3: ความถี่การใช้ในช่วง 3 เดือน (SmokerAssist)
            if (smokerInfo.getSmokerAssist() != null && "3".equals(smokerInfo.getSmokerGroup())) {
                switch (smokerInfo.getSmokerAssist()) {
                    case "1": score += 2; break;  // เดือนละครั้งหรือน้อยกว่า
                    case "2": score += 3; break;  // 2-4 ครั้งต่อเดือน
                    case "3": score += 4; break;  // 2-3 ครั้งต่อสัปดาห์ หรือมากกว่า
                }
            }

            // เพิ่มคะแนนจาก SmokerRegularly ถ้ามี
            if (smokerInfo.getSmokerRegularly() != null && "3".equals(smokerInfo.getSmokerAssist())) {
                switch (smokerInfo.getSmokerRegularly()) {
                    case "1": score += 1; break;
                    case "2": score += 2; break;
                    case "3": score += 4; break;
                }
            }
        }

        return score;
    }
    public String getAssessmentResultWithEmoji() {
        if (!isFormComplete()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        String emoji = getSmokingStatusEmoji();
        String result = getAssessmentResult();

        return emoji + " " + result;
    }
    public String getRiskLevelFromScoreWithEmoji() {
        if (!isFormComplete()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "😊 ไม่มีความเสี่ยง";
        } else if ("2".equals(smokerGroup)) {
            return "🙂 ความเสี่ยงต่ำ";
        } else if ("3".equals(smokerGroup)) {
            if ("1".equals(smokerAssist) || "2".equals(smokerAssist)) {
                return "😟 ความเสี่ยงปานกลาง";
            } else if ("3".equals(smokerAssist)) {
                if ("3".equals(smokerRegularly)) {
                    return "😱 ความเสี่ยงสูงมาก";
                } else {
                    return "😰 ความเสี่ยงสูง";
                }
            } else {
                return "😟 ความเสี่ยงปานกลาง";
            }
        }

        return "😐 ไม่ทราบระดับความเสี่ยง";
    }
    public String getRecommendationWithEmoji() {
        if (!isFormComplete()) {
            return "😐 กรุณากรอกข้อมูลให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "😊 ควรรักษาสถานะไม่สูบบุหรี่ต่อไป หลีกเลี่ยงสภาพแวดล้อมที่มีควันบุหรี่";
        } else if ("2".equals(smokerGroup)) {
            return "🙂 ดีที่เลิกสูบได้แล้ว ควรรักษาสถานะนี้ต่อไป และหลีกเลี่ยงการกลับไปสูบใหม่";
        } else if ("3".equals(smokerGroup)) {
            StringBuilder recommendation = new StringBuilder("😰 ควรเลิกสูบบุหรี่และปรึกษาแพทย์");

            if ("3".equals(smokerAssist) && smokerRegularly != null && !smokerRegularly.isEmpty()) {
                if ("1".equals(smokerRegularly)) {
                    recommendation.append(" - ให้คำแนะนำเกี่ยวกับการเลิกสูบบุหรี่");
                } else if ("2".equals(smokerRegularly)) {
                    recommendation.append(" - ให้คำแนะนำและติดตามการเลิกสูบบุหรี่อย่างใกล้ชิด");
                } else if ("3".equals(smokerRegularly)) {
                    recommendation.setLength(0); // ล้างข้อความเดิม
                    recommendation.append("😱 ให้คำแนะนำเร่งด่วนและส่งต่อผู้เชี่ยวชาญเพื่อการรักษา");
                }
            }

            return recommendation.toString();
        }

        return "😐 ควรปรึกษาแพทย์เพื่อรับคำแนะนำที่เหมาะสม";
    }

    public void checkHighRiskSmokingAlert() {
        if (isFormComplete() && isHighRisk()) {
            showHighRiskSmokingNotification();
        }
    }
    private void showHighRiskSmokingNotification() {
        try {
            String emoji = getSmokingStatusEmoji();
            String riskLevel = getRiskLevelFromScoreWithEmoji();
            String assessment = getAssessmentResultWithEmoji();

            String message = String.format(
                    "%s ตรวจพบความเสี่ยงสูงจากการสูบบุหรี่\n\n" +
                            "สถานะ: %s\n" +
                            "ระดับ: %s\n\n" +
                            "จำเป็นต้องได้รับการดูแลเป็นพิเศษ",
                    emoji, assessment.replace(emoji + " ", ""), riskLevel.replace(emoji + " ", "")
            );

            Log.w(TAG, message);

            // แสดง Toast แจ้งเตือน
            if (getContext() != null) {
                Toast.makeText(getContext(), emoji + " ตรวจพบความเสี่ยงสูงจากการสูบบุหรี่",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแสดงการแจ้งเตือน: " + e.getMessage());
        }
    }
    public String generateSmokingAssessmentReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== รายงานการประเมินความเสี่ยงจากการสูบบุหรี่ ===\n\n");

        if (!isFormComplete()) {
            report.append("สถานะ: ไม่สามารถสร้างรายงานได้\n");
            report.append("เหตุผล: ข้อมูลไม่ครบถ้วน\n");
            report.append("ข้อที่ยังไม่ได้กรอก: ").append(getValidationMessage()).append("\n");
            return report.toString();
        }

        // ข้อมูลพื้นฐาน
        report.append("สถานะการกรอกข้อมูล: ครบถ้วน (").append(getCompletionPercentage()).append("%)\n");
        report.append("วันที่ประเมิน: ").append(System.currentTimeMillis()).append("\n\n");

        // ผลการประเมิน
        String emoji = getSmokingStatusEmoji();
        report.append("ผลการประเมิน:\n");
        report.append("- สถานะ: ").append(getAssessmentResultWithEmoji()).append("\n");
        report.append("- ระดับความเสี่ยง: ").append(getRiskLevelFromScoreWithEmoji()).append("\n");
        report.append("- ความเสี่ยงสูง: ").append(isHighRisk() ? "ใช่" : "ไม่").append("\n\n");

        // คำแนะนำ
        report.append("คำแนะนำ:\n");
        report.append(getRecommendationWithEmoji()).append("\n\n");

        // ข้อมูลเพิ่มเติม
        if (isHighRisk()) {
            report.append("⚠️ การดำเนินการเร่งด่วน:\n");
            report.append("1. หยุดสูบบุหรี่ทันที\n");
            report.append("2. ปรึกษาแพทย์หรือผู้เชี่ยวชาญด้านการเลิกบุหรี่\n");
            report.append("3. เข้าร่วมโปรแกรมการเลิกบุหรี่\n");
            report.append("4. หลีกเลี่ยงสภาพแวดล้อมที่มีการสูบบุหรี่\n");
            report.append("5. ติดตามอาการถอนจากนิโคตินอย่างใกล้ชิด\n\n");
        }

        report.append("=== สิ้นสุดรายงาน ===");

        return report.toString();
    }
    /**
     * อัปเดตคะแนนและระดับความเสี่ยงบน UI
     */
    private void updateSmokingScore(int score) {
        if (tvSmokingScore != null) {
            tvSmokingScore.setText(String.valueOf(score));

            // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
            if (score >= 0 && score <= 3) {
                // ไม่มีความเสี่ยง - สีเขียว
                tvSmokingScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvSmokingScore.setTextColor(Color.WHITE);
            } else if (score >= 4 && score <= 26) {
                // ความเสี่ยงปานกลาง - สีส้ม
                tvSmokingScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvSmokingScore.setTextColor(Color.WHITE);
            } else if (score >= 27) {
                // ความเสี่ยงสูง - สีแดง
                tvSmokingScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvSmokingScore.setTextColor(Color.WHITE);
            }
        }

        if (tvSmokingRiskLevel != null) {
            String emoji = getSmokingEmoji(score);
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์ พร้อม emoji
            if (score >= 0 && score <= 3) {
                riskLevel = emoji + " ไม่มีความเสี่ยง";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_green);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 4 && score <= 26) {
                riskLevel = emoji + " ความเสี่ยงปานกลาง";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_orange);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_orange));
            } else if (score >= 27) {
                riskLevel = emoji + " ความเสี่ยงสูง ต้องเลิกสูบ";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_red);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "😐 ยังไม่ได้ประเมิน";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_gray);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvSmokingRiskLevel.setText(riskLevel);
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

        gradient.setCornerRadius(20f);
        return gradient;
    }

    private void loadData() {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data.getPersonId() != null) {
                List<SmokerInfo> smokerInfos = sfSmokerInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for (SmokerInfo smokerInfo : smokerInfos) {
                    Log.d("smoker", "smoker infos:" + smokerInfos);
                    setSmokerInfo(smokerInfo);
                    dataPasser.onSmokerInfo(smokerInfo);
                }
            }
        });
    }

    public void setSmokerInfo(SmokerInfo info) {
        Log.d(TAG, "setSmokerInfo called with: " +
                (info != null ? "Group=" + info.getSmokerGroup() + ", Assist=" + info.getSmokerAssist() + ", Regularly=" + info.getSmokerRegularly() : "null"));

        this.smokerInfo = info;
        updateUI();
    }

    private void updateUI() {
        if (this.smokerInfo != null) {
            isUpdatingFromCode = true;

            try {
                String smokerGroup = this.smokerInfo.getSmokerGroup();
                if (smokerGroup != null && !smokerGroup.isEmpty()) {
                    switch (smokerGroup) {
                        case "1":
                            rdoSmokerGroup1.setChecked(true);
                            clearSubsequentSelections();
                            break;
                        case "2":
                            rdoSmokerGroup2.setChecked(true);
                            clearSubsequentSelections();
                            break;
                        case "3":
                            rdoSmokerGroup3.setChecked(true);
                            enableAssistOptions();

                            String smokerAssist = this.smokerInfo.getSmokerAssist();
                            if (smokerAssist != null && !smokerAssist.isEmpty()) {
                                switch (smokerAssist) {
                                    case "1":
                                        rdoSmokerAssist1.setChecked(true);
                                        break;
                                    case "2":
                                        rdoSmokerAssist2.setChecked(true);
                                        break;
                                    case "3":
                                        rdoSmokerAssist3.setChecked(true);

                                        String smokerRegularly = this.smokerInfo.getSmokerRegularly();
                                        if (smokerRegularly != null && !smokerRegularly.isEmpty()) {
                                            switch (smokerRegularly) {
                                                case "1":
                                                    rdoSmokerRegularly1.setChecked(true);
                                                    break;
                                                case "2":
                                                    rdoSmokerRegularly2.setChecked(true);
                                                    break;
                                                case "3":
                                                    rdoSmokerRegularly3.setChecked(true);
                                                    break;
                                            }
                                        }
                                        break;
                                }
                            }
                            break;
                    }
                }
            } finally {
                isUpdatingFromCode = false;
            }
        }
    }

    public SmokerInfo getFormData() {
        return this.smokerInfo;
    }

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
     */
    public boolean isFormComplete() {
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            return false;
        }

        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                return false;
            }

            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
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

        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกสถานะการสูบบุหรี่");
            return message.toString();
        }

        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกความถี่ในการสูบ");
                return message.toString();
            }

            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกการให้คำแนะนำ/ปรึกษา");
                    return message.toString();
                }
            }
        }

        return "";
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        StringBuilder message = new StringBuilder();

        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
            message.append("• ยังไม่ได้เลือกสถานะการสูบบุหรี่ (ไม่เคยสูบ/เคยสูบ/สูบเป็นประจำ)");
            return message.toString();
        }

        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
                message.append("• ยังไม่ได้เลือกความถี่ในการสูบ (บางครั้ง/บางคราว/เป็นประจำ)");
                return message.toString();
            }

            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
                    message.append("• ยังไม่ได้เลือกการให้คำแนะนำ/ปรึกษา");
                    return message.toString();
                }
            }
        }

        return "";
    }

    /**
     * รีเซ็ตฟอร์มกลับเป็นค่าเริ่มต้น
     */
    public void resetForm() {
        isUpdatingFromCode = true;
        try {
            if (rdoSmokerGroup != null) {
                rdoSmokerGroup.clearCheck();
            }
            if (rdoSmokerAssist != null) {
                rdoSmokerAssist.clearCheck();
            }
            if (rdoSmokerRegularly != null) {
                rdoSmokerRegularly.clearCheck();
            }

            smokerInfo = new SmokerInfo();

            // รีเซ็ตการแสดงผลพร้อม emoji
            if (tvSmokingScore != null) {
                tvSmokingScore.setText("-");
                tvSmokingScore.setBackgroundResource(R.color.light_gray);
                tvSmokingScore.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            if (tvSmokingRiskLevel != null) {
                tvSmokingRiskLevel.setText("😐 ยังไม่ได้ประเมิน");
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_gray);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            // รีเซ็ต gauge (เพิ่มบรรทัดนี้)
            resetGauge();

        } finally {
            isUpdatingFromCode = false;
        }
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (smokerInfo == null) {
            return false;
        }

        return (smokerInfo.getSmokerGroup() != null && !smokerInfo.getSmokerGroup().isEmpty()) ||
                (smokerInfo.getSmokerAssist() != null && !smokerInfo.getSmokerAssist().isEmpty()) ||
                (smokerInfo.getSmokerRegularly() != null && !smokerInfo.getSmokerRegularly().isEmpty());
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            return 0;
        }

        if ("1".equals(smokerInfo.getSmokerGroup()) || "2".equals(smokerInfo.getSmokerGroup())) {
            return 100;
        }

        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                return 33;
            }

            if ("1".equals(smokerInfo.getSmokerAssist()) || "2".equals(smokerInfo.getSmokerAssist())) {
                return 100;
            }

            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    return 66;
                } else {
                    return 100;
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
            String riskInfo = getAssessmentResultWithEmoji();
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + riskInfo;

            // ตรวจสอบความเสี่ยงสูงและแจ้งเตือน
            if (isHighRisk()) {
                checkHighRiskSmokingAlert();
            }
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d(TAG, "Completion Status: " + message);
    }
    public String getSummaryTextWithEmoji() {
        if (!isFormComplete()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        String riskLevel = getRiskLevelFromScoreWithEmoji();
        String assessment = getAssessmentResultWithEmoji();

        return riskLevel + " (" + assessment.split(" ", 2)[1] + ")"; // เอาแค่ข้อความหลัง emoji แรก
    }
    public String getDetailedAssessmentResultWithEmoji() {
        if (!isFormComplete()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        String emoji = getSmokingStatusEmoji();
        String assessment = getAssessmentResult();
        String riskLevel = getRiskLevelFromScore();
        String recommendation = getRecommendation();
        boolean isHighRiskUser = isHighRisk();

        StringBuilder result = new StringBuilder();
        result.append("=== ผลการประเมินความเสี่ยงจากการสูบบุหรี่ ===\n");
        result.append("สถานะ: ").append(emoji).append(" ").append(assessment).append("\n");
        result.append("ระดับความเสี่ยง: ").append(getRiskLevelFromScoreWithEmoji()).append("\n");
        result.append("ความเสี่ยงสูง: ").append(isHighRiskUser ? "ใช่" : "ไม่").append("\n\n");

        if (isHighRiskUser) {
            result.append("⚠️ ความเสี่ยงสูง: ต้องการความช่วยเหลือเร่งด่วน\n");
        }

        result.append("\nคำแนะนำ:\n").append(getRecommendationWithEmoji());

        return result.toString();
    }
    public String createExportReportWithEmoji() {
        StringBuilder report = new StringBuilder();

        report.append("SMOKING_ASSESSMENT_REPORT").append("\n");
        report.append("TIMESTAMP:").append(System.currentTimeMillis()).append("\n");
        report.append("PERSON_ID:").append(currentPersonId).append("\n");
        report.append("VISIT_NO:").append(currentVisitNo).append("\n");

        if (isFormComplete()) {
            String emoji = getSmokingStatusEmoji();
            String assessment = getAssessmentResult();
            String riskLevel = getRiskLevelFromScore();

            report.append("STATUS:").append(emoji).append(" ").append(assessment).append("\n");
            report.append("RISK_LEVEL:").append(getRiskLevelFromScoreWithEmoji()).append("\n");
            report.append("IS_HIGH_RISK:").append(isHighRisk()).append("\n");
            report.append("COMPLETION:").append(getCompletionPercentage()).append("%\n");

            // ข้อมูลการเลือก
            if (smokerInfo != null) {
                report.append("SMOKER_GROUP:").append(smokerInfo.getSmokerGroup()).append("\n");
                report.append("SMOKER_ASSIST:").append(smokerInfo.getSmokerAssist()).append("\n");
                report.append("SMOKER_REGULARLY:").append(smokerInfo.getSmokerRegularly()).append("\n");
            }
        } else {
            report.append("STATUS:😐 INCOMPLETE").append("\n");
            report.append("COMPLETION:").append(getCompletionPercentage()).append("%\n");
        }

        return report.toString();
    }
    /**
     * ดึงผลการประเมิน
     */
    public String getAssessmentResult() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "ไม่เคยสูบบุหรี่";
        } else if ("2".equals(smokerGroup)) {
            return "เคยสูบบุหรี่ แต่ไม่ใช่ใน 3 เดือนที่ผ่านมา";
        } else if ("3".equals(smokerGroup)) {
            if ("1".equals(smokerAssist)) {
                return "สูบบุหรี่บางครั้ง บางคราว";
            } else if ("2".equals(smokerAssist)) {
                return "สูบบุหรี่บางครั้ง บางคราว";
            } else if ("3".equals(smokerAssist)) {
                if ("1".equals(smokerRegularly)) {
                    return "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 1";
                } else if ("2".equals(smokerRegularly)) {
                    return "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 2";
                } else if ("3".equals(smokerRegularly)) {
                    return "สูบบุหรี่เป็นประจำ - ให้คำแนะนำแบบที่ 3";
                } else {
                    return "สูบบุหรี่เป็นประจำ";
                }
            } else {
                return "สูบบุหรี่เป็นประจำ";
            }
        }

        return "ไม่ทราบสถานะ";
    }

    /**
     * ดึงระดับความเสี่ยงจากคะแนน
     */
    public String getRiskLevelFromScore() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "ไม่มีความเสี่ยง";
        } else if ("2".equals(smokerGroup)) {
            return "ความเสี่ยงต่ำ";
        } else if ("3".equals(smokerGroup)) {
            if ("1".equals(smokerAssist) || "2".equals(smokerAssist)) {
                return "ความเสี่ยงปานกลาง";
            } else if ("3".equals(smokerAssist)) {
                if ("3".equals(smokerRegularly)) {
                    return "ความเสี่ยงสูงมาก";
                } else {
                    return "ความเสี่ยงสูง";
                }
            } else {
                return "ความเสี่ยงปานกลาง";
            }
        }

        return "ไม่ทราบระดับความเสี่ยง";
    }

    /**
     * ดึงคำแนะนำตามผลการประเมิน
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณากรอกข้อมูลให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "ควรรักษาสถานะไม่สูบบุหรี่ต่อไป หลีกเลี่ยงสภาพแวดล้อมที่มีควันบุหรี่";
        } else if ("2".equals(smokerGroup)) {
            return "ดีที่เลิกสูบได้แล้ว ควรรักษาสถานะนี้ต่อไป และหลีกเลี่ยงการกลับไปสูบใหม่";
        } else if ("3".equals(smokerGroup)) {
            StringBuilder recommendation = new StringBuilder("ควรเลิกสูบบุหรี่และปรึกษาแพทย์");

            if ("3".equals(smokerAssist) && smokerRegularly != null && !smokerRegularly.isEmpty()) {
                if ("1".equals(smokerRegularly)) {
                    recommendation.append(" - ให้คำแนะนำเกี่ยวกับการเลิกสูบบุหรี่");
                } else if ("2".equals(smokerRegularly)) {
                    recommendation.append(" - ให้คำแนะนำและติดตามการเลิกสูบบุหรี่อย่างใกล้ชิด");
                } else if ("3".equals(smokerRegularly)) {
                    recommendation.append(" - ให้คำแนะนำเร่งด่วนและส่งต่อผู้เชี่ยวชาญเพื่อการรักษา");
                }
            }

            return recommendation.toString();
        }

        return "ควรปรึกษาแพทย์เพื่อรับคำแนะนำที่เหมาะสม";
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงสูงหรือไม่
     */
    public boolean isHighRisk() {
        if (!isFormComplete()) {
            return false;
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        // ถือว่าความเสี่ยงสูงถ้าสูบเป็นประจำ และเลือกตัวเลือกที่มีความเสี่ยงสูง
        if ("3".equals(smokerGroup) && "3".equals(smokerAssist)) {
            if ("2".equals(smokerRegularly) || "3".equals(smokerRegularly)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ดึงคะแนนรวม
     */
    public int getTotalScore() {
        if (!isFormComplete()) {
            return -1;
        }

        // คำนวณคะแนนเพื่อแสดงผลใน UI เท่านั้น
        return calculateCurrentScore();
    }

    /**
     * ดึงข้อความสรุปผลแบบสั้น
     */
    public String getSummaryText() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        String riskLevel = getRiskLevelFromScore();
        String assessment = getAssessmentResult();

        return riskLevel + " (" + assessment + ")";
    }

    /**
     * ดึงสถิติการประเมิน
     */
    public ScreeningResultCodeDao.ScreeningStatistics getStatistics() {
        try {
            // ดึงสถิติจากสถานะการสูบบุหรี่เป็นหลัก
            return screeningResultCodeDao.getStatisticsByType(ScreeningResultCode.TYPE_SMOKING_STATUS);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติการประเมินการสูบบุหรี่");
            return null;
        }
    }

    public ScreeningResultCodeDao.ScreeningStatistics getAdviceStatistics() {
        try {
            return screeningResultCodeDao.getStatisticsByType(ScreeningResultCode.TYPE_SMOKING_ADVICE);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติการให้คำแนะนำ");
            return null;
        }
    }

    /**
     * แสดงสถิติการประเมิน
     */
    public void showStatistics() {
        ScreeningResultCodeDao.ScreeningStatistics statusStats = getStatistics();
        ScreeningResultCodeDao.ScreeningStatistics adviceStats = getAdviceStatistics();

        StringBuilder message = new StringBuilder();

        if (statusStats != null) {
            message.append("สถิติสถานะการสูบบุหรี่:\n");
            message.append(String.format(
                    "จำนวนทั้งหมด: %d ครั้ง\n" +
                            "ไม่สูบ/เลิกแล้ว: %d ครั้ง\n" +
                            "สูบบุหรี่: %d ครั้ง\n",
                    statusStats.totalCount, statusStats.normalCount, statusStats.abnormalCount
            ));
        }

        if (adviceStats != null) {
            message.append("\nสถิติการให้คำแนะนำ:\n");
            message.append(String.format(
                    "จำนวนทั้งหมด: %d ครั้ง\n" +
                            "คะแนนเฉลี่ย: %.1f\n" +
                            "คะแนนสูงสุด: %d\n" +
                            "คะแนนต่ำสุด: %d",
                    adviceStats.totalCount, adviceStats.averageScore,
                    adviceStats.maxScore, adviceStats.minScore
            ));
        }

        if (message.length() > 0) {
            Log.d(TAG, message.toString());
        }
    }

    /**
     * ตั้งค่าข้อมูล person และ visit
     */
    public void setPersonAndVisitInfo(int personId, int visitNo) {
        this.currentPersonId = personId;
        this.currentVisitNo = visitNo;
    }
    private String getSmokingEmoji(int score) {
        if (score >= 0 && score <= 3) {
            return "😊"; // ไม่มีความเสี่ยง - หน้ายิ้ม
        } else if (score >= 4 && score <= 26) {
            return "😟"; // ความเสี่ยงปานกลาง - หน้ากังวล
        } else if (score >= 27) {
            return "😰"; // ความเสี่ยงสูง - หน้าตกใจ/กังวลมาก
        } else {
            return "😐"; // ยังไม่ได้ประเมิน - หน้าเฉยๆ
        }
    }
    private String getSmokingStatusEmoji() {
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null) {
            return "😐"; // ยังไม่ได้ประเมิน
        }

        String smokerGroup = smokerInfo.getSmokerGroup();
        String smokerAssist = smokerInfo.getSmokerAssist();
        String smokerRegularly = smokerInfo.getSmokerRegularly();

        if ("1".equals(smokerGroup)) {
            return "😊"; // ไม่เคยสูบ - หน้ายิ้ม
        } else if ("2".equals(smokerGroup)) {
            return "🙂"; // เคยสูบแต่เลิกแล้ว - หน้ายิ้มเบา
        } else if ("3".equals(smokerGroup)) {
            if ("1".equals(smokerAssist) || "2".equals(smokerAssist)) {
                return "😟"; // สูบบางครั้ง - หน้ากังวล
            } else if ("3".equals(smokerAssist)) {
                if ("3".equals(smokerRegularly)) {
                    return "😱"; // สูบเป็นประจำ + คำแนะนำระดับสูง - หน้าตกใจมาก
                } else {
                    return "😰"; // สูบเป็นประจำ - หน้าตกใจ/กังวลมาก
                }
            } else {
                return "😟"; // สูบบุหรี่ทั่วไป - หน้ากังวล
            }
        }

        return "😐"; // default
    }
}