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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.view.DepressionRiskGaugeView;
import th.in.ffc.util.Log;
import android.app.AlertDialog;
import android.widget.ImageView;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression9qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression9qFragment extends Fragment {

    StressDepression9qLiveData stressDepression9qLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepression9qInfo stressDepression9qInfo;

    private ArrayList<Integer> points;

    private RadioGroup[] radioGroups;

    private TableLayout depression9resultTable;
    private static final int QUESTION_COUNT = 9;
    private TextView depression9result;

    // เพิ่มตัวแปรสำหรับตรวจสอบข้อมูล
    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false, false, false, false, false, false, false, false}; // ตรวจสอบว่าตอบคำถามครบหรือไม่ (9 ข้อ)

    private TextView tv9qScore;
    private TextView tv9qResultDetail;

    private ImageView ivStress9qInfoButton;

    private DepressionRiskGaugeView depressionRiskGauge;
    private TextView tvDepressionGaugeEmoji;
    private TextView tvDepressionGaugeScore;
    private TextView tvDepressionGaugeLevel;
    private TextView tvDepressionGaugeCode;
    private TextView tvDepressionGaugeRecommendation;
    private SeekBar seekBarDepressionGaugeTest;


    public StressDepression9qFragment() {
        // Required empty public constructor
    }


    public static StressDepression9qFragment newInstance(String param1, String param2) {
        StressDepression9qFragment fragment = new StressDepression9qFragment();
        return fragment;
    }
    /**
     * ตรวจสอบความถูกต้องของข้อมูลและบันทึกข้อมูล
     */
    private void validateAndSaveData() {
        // ตรวจสอบว่าตอบคำถามครบทุกข้อหรือไม่
        boolean allAnswered = true;
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                allAnswered = false;
                break;
            }
        }

        isFormValid = allAnswered;

        // อัปเดตการแสดงผลทันที - ไม่ต้องรอให้ครบ
        displayPoints();

        if (isFormValid) {
            Log.d("StressDepression9q", "ตอบคำถามครบทุกข้อแล้ว - บันทึกข้อมูล");
            if (dataPasser != null) {
                dataPasser.onStressDepression9q(stressDepression9qInfo);
            }
        } else {
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
    private void initializeDepressionGaugeViews(View view) {
        depressionRiskGauge = view.findViewById(R.id.depressionRiskGauge);
        tvDepressionGaugeEmoji = view.findViewById(R.id.tvDepressionGaugeEmoji);
        tvDepressionGaugeScore = view.findViewById(R.id.tvDepressionGaugeScore);
        tvDepressionGaugeLevel = view.findViewById(R.id.tvDepressionGaugeLevel);
        tvDepressionGaugeCode = view.findViewById(R.id.tvDepressionGaugeCode);
        tvDepressionGaugeRecommendation = view.findViewById(R.id.tvDepressionGaugeRecommendation);


        // สำหรับทดสอบ (สามารถลบออกได้)
        seekBarDepressionGaugeTest = view.findViewById(R.id.seekBarDepressionGaugeTest);
        setupDepressionGaugeTestControls();
//        setupDepressionGaugeInfoButton();

        // อัปเดต Gauge ครั้งแรก
        updateDepressionGaugeDisplay();
    }
    private void setupDepressionGaugeTestControls() {
        if (seekBarDepressionGaugeTest != null) {
            seekBarDepressionGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && depressionRiskGauge != null) {
                        depressionRiskGauge.setScore(progress);
                        DepressionRiskGaugeView.DepressionLevel level = getCurrentDepressionLevelFromScore(progress);

                        // อัปเดตข้อความทดสอบ
                        if (tvDepressionGaugeEmoji != null) tvDepressionGaugeEmoji.setText(level.emoji);
                        if (tvDepressionGaugeScore != null) tvDepressionGaugeScore.setText("คะแนน: " + progress);
                        if (tvDepressionGaugeLevel != null) {
                            tvDepressionGaugeLevel.setText(level.label);
                            tvDepressionGaugeLevel.setTextColor(Color.parseColor(level.color));
                        }
                        if (tvDepressionGaugeCode != null) tvDepressionGaugeCode.setText(level.code);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }
    public void showDepressionGaugeTestControls(boolean show) {
        View layoutDepressionGaugeControl = getView().findViewById(R.id.layoutDepressionGaugeControl);
        if (layoutDepressionGaugeControl != null) {
            layoutDepressionGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // Method สำหรับรีเซ็ต Gauge
    public void resetDepressionGauge() {
        if (depressionRiskGauge != null) {
            depressionRiskGauge.setScore(0);
            updateDepressionGaugeDisplay();
        }
    }
    private void updateDepressionGaugeDisplay() {
        if (depressionRiskGauge == null) return;

        int totalScore = sumPoints();
        DepressionRiskGaugeView.DepressionLevel currentLevel = getCurrentDepressionLevelFromScore(totalScore);

        // อัปเดต Gauge
        depressionRiskGauge.setScore(totalScore);

        // อัปเดตข้อความ
        if (tvDepressionGaugeEmoji != null) tvDepressionGaugeEmoji.setText(currentLevel.emoji);
        if (tvDepressionGaugeScore != null) tvDepressionGaugeScore.setText("คะแนน: " + totalScore);
        if (tvDepressionGaugeLevel != null) {
            tvDepressionGaugeLevel.setText(currentLevel.label);
            tvDepressionGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvDepressionGaugeCode != null) tvDepressionGaugeCode.setText(currentLevel.code);

        Log.d("StressDepression9q", "Depression Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label);
    }

    private DepressionRiskGaugeView.DepressionLevel getCurrentDepressionLevelFromScore(int score) {
        if (score < 7) {
            return new DepressionRiskGaugeView.DepressionLevel(0, 6, "ไม่มีอาการของโรคซึมเศร้า", "#27AE60", "😊", "1B0260|1B0282");
        } else if (score >= 7 && score <= 12) {
            return new DepressionRiskGaugeView.DepressionLevel(7, 12, "มีอาการของโรคซึมเศร้าระดับน้อย", "#F39C12", "😐", "1B0261|1B0283");
        } else if (score >= 13 && score <= 18) {
            return new DepressionRiskGaugeView.DepressionLevel(13, 18, "มีอาการของโรคซึมเศร้าระดับปานกลาง", "#E67E22", "😟", "1B0262|1B0284");
        } else {
            return new DepressionRiskGaugeView.DepressionLevel(19, 27, "มีอาการของโรคซึมเศร้าระดับรุนแรง", "#E74C3C", "😰", "1B0263|1B0285");
        }
    }
    public void updateDepressionGaugeWithScore(int score) {
        if (depressionRiskGauge != null) {
            depressionRiskGauge.setScore(score);
            updateDepressionGaugeDisplay();
        }
    }
    public DepressionRiskGaugeView.DepressionLevel getCurrentDepressionGaugeLevel() {
        if (depressionRiskGauge != null) {
            return depressionRiskGauge.getCurrentDepressionLevel();
        }
        return getCurrentDepressionLevelFromScore(0);
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

    /**
     * ตรวจสอบว่าแบบฟอร์มกรอกครบหรือไม่
     */
    public boolean isFormComplete() {
        if (stressDepression9qInfo == null) {
            return false;
        }

        // ตรวจสอบว่าตอบครบทุกข้อหรือไม่
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                return false;
            }
        }

        return true;
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
     * ตรวจสอบสถานะการตอบจาก RadioGroup
     */
    private void checkAnsweredStatus() {
        questionAnswered[0] = getView().findViewById(R.id.rdoStress9qQ1).findViewById(R.id.rdoStress9qQ1_1) != null;
        questionAnswered[1] = getView().findViewById(R.id.rdoStress9qQ2).findViewById(R.id.rdoStress9qQ2_1) != null;
        questionAnswered[2] = getView().findViewById(R.id.rdoStress9qQ3).findViewById(R.id.rdoStress9qQ3_1) != null;
        questionAnswered[3] = getView().findViewById(R.id.rdoStress9qQ4).findViewById(R.id.rdoStress9qQ4_1) != null;
        questionAnswered[4] = getView().findViewById(R.id.rdoStress9qQ5).findViewById(R.id.rdoStress9qQ5_1) != null;
        questionAnswered[5] = getView().findViewById(R.id.rdoStress9qQ6).findViewById(R.id.rdoStress9qQ6_1) != null;
        questionAnswered[6] = getView().findViewById(R.id.rdoStress9qQ7).findViewById(R.id.rdoStress9qQ7_1) != null;
        questionAnswered[7] = getView().findViewById(R.id.rdoStress9qQ8).findViewById(R.id.rdoStress9qQ8_1) != null;
        questionAnswered[8] = getView().findViewById(R.id.rdoStress9qQ9).findViewById(R.id.rdoStress9qQ9_1) != null;

        // วิธีที่ถูกต้องในการตรวจสอบ RadioGroup
        RadioGroup rdoStress9qQ1 = getView().findViewById(R.id.rdoStress9qQ1);
        RadioGroup rdoStress9qQ2 = getView().findViewById(R.id.rdoStress9qQ2);
        RadioGroup rdoStress9qQ3 = getView().findViewById(R.id.rdoStress9qQ3);
        RadioGroup rdoStress9qQ4 = getView().findViewById(R.id.rdoStress9qQ4);
        RadioGroup rdoStress9qQ5 = getView().findViewById(R.id.rdoStress9qQ5);
        RadioGroup rdoStress9qQ6 = getView().findViewById(R.id.rdoStress9qQ6);
        RadioGroup rdoStress9qQ7 = getView().findViewById(R.id.rdoStress9qQ7);
        RadioGroup rdoStress9qQ8 = getView().findViewById(R.id.rdoStress9qQ8);
        RadioGroup rdoStress9qQ9 = getView().findViewById(R.id.rdoStress9qQ9);

        questionAnswered[0] = rdoStress9qQ1.getCheckedRadioButtonId() != -1;
        questionAnswered[1] = rdoStress9qQ2.getCheckedRadioButtonId() != -1;
        questionAnswered[2] = rdoStress9qQ3.getCheckedRadioButtonId() != -1;
        questionAnswered[3] = rdoStress9qQ4.getCheckedRadioButtonId() != -1;
        questionAnswered[4] = rdoStress9qQ5.getCheckedRadioButtonId() != -1;
        questionAnswered[5] = rdoStress9qQ6.getCheckedRadioButtonId() != -1;
        questionAnswered[6] = rdoStress9qQ7.getCheckedRadioButtonId() != -1;
        questionAnswered[7] = rdoStress9qQ8.getCheckedRadioButtonId() != -1;
        questionAnswered[8] = rdoStress9qQ9.getCheckedRadioButtonId() != -1;
    }

    /**
     * เมธอดสำหรับหน้าจออื่นที่ต้องการตรวจสอบความครบถ้วนของข้อมูล
     */
    public StressDepression9qInfo getFormData() {
        if (isFormValid) {
            return stressDepression9qInfo;
        } else {
            showIncompleteFormMessage();
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stressDepression9qLiveData = new StressDepression9qLiveData();
        shareViewModel = new SharedViewModel();
        stressDepression9qInfo = new StressDepression9qInfo();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(
                0,0,0,
                0,0,0,
                0,0,0));
    }

    private int sumPoints() {
        if (points == null || points.size() != 9) {
            return 0;
        }

        int totalScore = 0;
        for (int i = 0; i < 9; i++) {
            if (points.get(i) != null) {
                totalScore += points.get(i);
            }
        }

        return totalScore;
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

        // Initialize all UI components
        RadioGroup rdoStress9qQ1 = view.findViewById(R.id.rdoStress9qQ1);
        RadioGroup rdoStress9qQ2 = view.findViewById(R.id.rdoStress9qQ2);
        RadioGroup rdoStress9qQ3 = view.findViewById(R.id.rdoStress9qQ3);
        RadioGroup rdoStress9qQ4 = view.findViewById(R.id.rdoStress9qQ4);
        RadioGroup rdoStress9qQ5 = view.findViewById(R.id.rdoStress9qQ5);
        RadioGroup rdoStress9qQ6 = view.findViewById(R.id.rdoStress9qQ6);
        RadioGroup rdoStress9qQ7 = view.findViewById(R.id.rdoStress9qQ7);
        RadioGroup rdoStress9qQ8 = view.findViewById(R.id.rdoStress9qQ8);
        RadioGroup rdoStress9qQ9 = view.findViewById(R.id.rdoStress9qQ9);
        depression9resultTable = view.findViewById(R.id.depression9resultTable);
        depression9result = view.findViewById(R.id.depression9result);

        // Initialize new UI elements
        tv9qScore = view.findViewById(R.id.tv9qScore);
        tv9qResultDetail = view.findViewById(R.id.tv9qResultDetail);
        ivStress9qInfoButton = view.findViewById(R.id.ivStress9qInfoButton);
        setupInfoButtonListener();

        initializeDepressionGaugeViews(view);
        // Initial display update
        updateScoreDisplay();

        // แก้ไข RadioGroup Listeners ให้ถูกต้อง
        rdoStress9qQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ1_1) {
                    data = "1";
                    points.set(0, 0);
                } else if (checkedId == R.id.rdoStress9qQ1_2) {
                    data = "2";
                    points.set(0, 1);
                } else if (checkedId == R.id.rdoStress9qQ1_3) {
                    data = "3";
                    points.set(0, 2);
                } else if (checkedId == R.id.rdoStress9qQ1_4) {
                    data = "4";
                    points.set(0, 3);
                }

                stressDepression9qInfo.setQ1(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[0] = true;

                // อัปเดตการแสดงผลทันที
                displayPoints();
                validateAndSaveData();

                dataPasser.onStressDepression9q(stressDepression9qInfo);
                stressDepression9qLiveData.setSelectedQ1(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ2_1) {
                    data = "1";
                    points.set(1, 0);
                } else if (checkedId == R.id.rdoStress9qQ2_2) {
                    data = "2";
                    points.set(1, 1);
                } else if (checkedId == R.id.rdoStress9qQ2_3) {
                    data = "3";
                    points.set(1, 2);
                } else if (checkedId == R.id.rdoStress9qQ2_4) {
                    data = "4";
                    points.set(1, 3);
                }

                stressDepression9qInfo.setQ2(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[1] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ2(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ3_1) {
                    data = "1";
                    points.set(2, 0);
                } else if (checkedId == R.id.rdoStress9qQ3_2) {
                    data = "2";
                    points.set(2, 1);
                } else if (checkedId == R.id.rdoStress9qQ3_3) {
                    data = "3";
                    points.set(2, 2);
                } else if (checkedId == R.id.rdoStress9qQ3_4) {
                    data = "4";
                    points.set(2, 3);
                }

                stressDepression9qInfo.setQ3(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[2] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ3(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ4_1) {
                    data = "1";
                    points.set(3, 0);
                } else if (checkedId == R.id.rdoStress9qQ4_2) {
                    data = "2";
                    points.set(3, 1);
                } else if (checkedId == R.id.rdoStress9qQ4_3) {
                    data = "3";
                    points.set(3, 2);
                } else if (checkedId == R.id.rdoStress9qQ4_4) {
                    data = "4";
                    points.set(3, 3);
                }

                stressDepression9qInfo.setQ4(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[3] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ4(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ5_1) {
                    data = "1";
                    points.set(4, 0);
                } else if (checkedId == R.id.rdoStress9qQ5_2) {
                    data = "2";
                    points.set(4, 1);
                } else if (checkedId == R.id.rdoStress9qQ5_3) {
                    data = "3";
                    points.set(4, 2);
                } else if (checkedId == R.id.rdoStress9qQ5_4) {
                    data = "4";
                    points.set(4, 3);
                }

                stressDepression9qInfo.setQ5(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[4] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                dataPasser.onStressDepression9q(stressDepression9qInfo);
                stressDepression9qLiveData.setSelectedQ5(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ6_1) {
                    data = "1";
                    points.set(5, 0);
                } else if (checkedId == R.id.rdoStress9qQ6_2) {
                    data = "2";
                    points.set(5, 1);
                } else if (checkedId == R.id.rdoStress9qQ6_3) {
                    data = "3";
                    points.set(5, 2);
                } else if (checkedId == R.id.rdoStress9qQ6_4) {
                    data = "4";
                    points.set(5, 3);
                }

                stressDepression9qInfo.setQ6(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[5] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ6(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ7_1) {
                    data = "1";
                    points.set(6, 0);
                } else if (checkedId == R.id.rdoStress9qQ7_2) {
                    data = "2";
                    points.set(6, 1);
                } else if (checkedId == R.id.rdoStress9qQ7_3) {
                    data = "3";
                    points.set(6, 2);
                } else if (checkedId == R.id.rdoStress9qQ7_4) {
                    data = "4";
                    points.set(6, 3);
                }

                stressDepression9qInfo.setQ7(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[6] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ7(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ8_1) {
                    data = "1";
                    points.set(7, 0);
                } else if (checkedId == R.id.rdoStress9qQ8_2) {
                    data = "2";
                    points.set(7, 1);
                } else if (checkedId == R.id.rdoStress9qQ8_3) {
                    data = "3";
                    points.set(7, 2);
                } else if (checkedId == R.id.rdoStress9qQ8_4) {
                    data = "4";
                    points.set(7, 3);
                }

                stressDepression9qInfo.setQ8(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[7] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ8(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ9.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ9_1) {
                    data = "1";
                    points.set(8, 0);
                } else if (checkedId == R.id.rdoStress9qQ9_2) {
                    data = "2";
                    points.set(8, 1);
                } else if (checkedId == R.id.rdoStress9qQ9_3) {
                    data = "3";
                    points.set(8, 2);
                } else if (checkedId == R.id.rdoStress9qQ9_4) {
                    data = "4";
                    points.set(8, 3);
                }

                stressDepression9qInfo.setQ9(data);
                stressDepression9qInfo.setPoint(points);
                questionAnswered[8] = true;

                displayPoints();
                validateAndSaveData();
                updateTableHighlight();

                stressDepression9qLiveData.setSelectedQ9(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        loadData();
        view.post(() -> {
            updateScoreDisplay();
            updateDepressionGaugeDisplay(); // เพิ่มบรรทัดนี้
            if (isFormComplete()) {
                updateTableHighlight();
            }
        });
    }
    private void setupInfoButtonListener() {
        if (ivStress9qInfoButton != null) {
            ivStress9qInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStress9qCriteriaDialog();
                }
            });
        }
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

    private void  loadData(){
        SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepression9qLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<StressDepression9qInfo> stressDepression9qInfos = sfStressDepression9qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepression9qInfo stressDepression9qInfo :stressDepression9qInfos){
                    Log.d("Stress Depression 9q ", "stressDepression9qInfo infos:"+stressDepression9qInfo);
                    this.stressDepression9qInfo = stressDepression9qInfo;
                    loadExistingData();
                }
            }
        });
    }
    private void displayPoints() {
        int totalScore = sumPoints();

        // อัปเดตการแสดงผลใหม่
        updateScoreDisplay();
        updateResultDisplay(totalScore);

        // อัปเดต Depression Gauge ใหม่
        updateDepressionGaugeDisplay();

        // อัปเดตการแสดงผลเดิม (ถ้ามี)
        if (depression9result != null) {
            depression9result.setText(String.format("คะแนนที่ได้: %d คะแนน", totalScore));
        }

        Log.d("StressDepression9q", "Total Score: " + totalScore);
    }


    // เมธอดใหม่สำหรับอัปเดตการแสดงผลคะแนน
    private void updateScoreDisplay() {
        int totalScore = sumPoints();

        Log.d("StressDepression9q", "updateScoreDisplay - Total Score: " + totalScore);
        Log.d("StressDepression9q", "updateScoreDisplay - Form Complete: " + isFormComplete());

        if (tv9qScore != null) {
            tv9qScore.setText(String.valueOf(totalScore));
            updateScoreBackgroundColor(totalScore);
            Log.d("StressDepression9q", "Score displayed: " + totalScore);
        } else {
            Log.e("StressDepression9q", "tv9qScore is null!");
        }
    }
    private void updateScoreBackgroundColor(int score) {
        if (tv9qScore == null) {
            Log.e("StressDepression9q", "tv9qScore is null in updateScoreBackgroundColor");
            return;
        }

        int color;
        if (score < 7) {
            color = Color.parseColor("#27AE60"); // เขียว - ปกติ
        } else if (score >= 7 && score <= 12) {
            color = Color.parseColor("#F39C12"); // เหลือง - น้อย
        } else if (score >= 13 && score <= 18) {
            color = Color.parseColor("#E67E22"); // ส้ม - ปานกลาง
        } else {
            color = Color.parseColor("#E74C3C"); // แดง - รุนแรง
        }

        tv9qScore.setBackgroundColor(color);
        tv9qScore.setTextColor(Color.WHITE);

        Log.d("StressDepression9q", "Background color updated for score: " + score);
    }
    // ปรับปรุง loadExistingData() ให้ตรวจสอบสถานะการตอบ
    private void loadExistingData() {
        if (this.stressDepression9qInfo == null) return;

        // โหลดข้อมูลเดิมและอัปเดต points array
        for (int i = 0; i < QUESTION_COUNT; i++) {
            String value = getQuestionValue(i + 1);
            if (!value.equals("0") && !value.isEmpty()) {
                int radioButtonId = getResources().getIdentifier(
                        "rdoStress9qQ" + (i + 1) + "_" + value,
                        "id",
                        requireContext().getPackageName()
                );
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }

                // อัปเดต points array
                int pointValue = Integer.parseInt(value) - 1; // แปลง 1-4 เป็น 0-3
                points.set(i, pointValue);
            }
        }

        // อัปเดตการแสดงผล
        updateScoreDisplay();
        updateResultDisplay(sumPoints());
        updateTableHighlight();

        // ตรวจสอบสถานะการตอบ
        checkAnsweredStatus();
        validateAndSaveData();
    }
    private void setQuestionValue(int questionNumber, String value) {
        switch (questionNumber) {
            case 1: stressDepression9qInfo.setQ1(value); break;
            case 2: stressDepression9qInfo.setQ2(value); break;
            case 3: stressDepression9qInfo.setQ3(value); break;
            case 4: stressDepression9qInfo.setQ4(value); break;
            case 5: stressDepression9qInfo.setQ5(value); break;
            case 6: stressDepression9qInfo.setQ6(value); break;
            case 7: stressDepression9qInfo.setQ7(value); break;
            case 8: stressDepression9qInfo.setQ8(value); break;
            case 9: stressDepression9qInfo.setQ9(value); break;
        }
    }

    private String getQuestionValue(int questionNumber) {
        if (stressDepression9qInfo == null) {
            return "0";
        }

        String value = "";
        switch (questionNumber) {
            case 1: value = stressDepression9qInfo.getQ1(); break;
            case 2: value = stressDepression9qInfo.getQ2(); break;
            case 3: value = stressDepression9qInfo.getQ3(); break;
            case 4: value = stressDepression9qInfo.getQ4(); break;
            case 5: value = stressDepression9qInfo.getQ5(); break;
            case 6: value = stressDepression9qInfo.getQ6(); break;
            case 7: value = stressDepression9qInfo.getQ7(); break;
            case 8: value = stressDepression9qInfo.getQ8(); break;
            case 9: value = stressDepression9qInfo.getQ9(); break;
            default: return "0";
        }

        return (value != null) ? value : "0";
    }
    public void debugScoreDisplay() {
        Log.d("StressDepression9q", "=== DEBUG SCORE DISPLAY ===");
        Log.d("StressDepression9q", "tv9qScore: " + (tv9qScore != null ? "OK" : "NULL"));
        Log.d("StressDepression9q", "tv9qResultDetail: " + (tv9qResultDetail != null ? "OK" : "NULL"));
        Log.d("StressDepression9q", "points size: " + (points != null ? points.size() : "NULL"));
        Log.d("StressDepression9q", "Total score: " + sumPoints());
        Log.d("StressDepression9q", "Form complete: " + isFormComplete());

        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                Log.d("StressDepression9q", "Point[" + i + "]: " + points.get(i));
            }
        }

        if (stressDepression9qInfo != null) {
            for (int i = 1; i <= 9; i++) {
                Log.d("StressDepression9q", "Q" + i + ": " + getQuestionValue(i));
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stress_depression9q, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    private void updateTableHighlight() {
        // คำนวณคะแนนรวม
        String resultCode = "";
        int totalScore = 0;
        for (Integer point : points) {
            totalScore += point;
        }

        int defaultWhite = ContextCompat.getColor(requireContext(), R.color.white);
        int lightGray = ContextCompat.getColor(requireContext(), R.color.light_gray);

        // กำหนดสีสำหรับแต่ละระดับ
        int lightGreen = Color.parseColor("#E8F5E8");    // เขียวอ่อน - ปกติ
        int lightYellow = Color.parseColor("#FFF3CD");   // เหลือง - ระดับน้อย
        int lightRed = Color.parseColor("#F8D7DA");      // แดงอ่อน - ระดับปานกลาง
        int darkRed = Color.parseColor("#F5C6CB");       // แดงเข้ม - ระดับมาก

        // ล้าง highlight เดิม - กลับเป็นสีพื้นหลังเดิม
        for (int i = 1; i < depression9resultTable.getChildCount(); i++) {
            TableRow row = (TableRow) depression9resultTable.getChildAt(i);

            // กำหนดสีพื้นหลังเดิมตามแถว
            if (i == 1) {
                row.setBackgroundColor(lightGreen);  // แถวปกติ
            } else if (i == 2) {
                row.setBackgroundColor(lightYellow); // แถวระดับน้อย
            } else if (i == 3) {
                row.setBackgroundColor(lightRed);    // แถวระดับปานกลาง
            } else if (i == 4) {
                row.setBackgroundColor(darkRed);     // แถวระดับมาก
            }
        }

        // กำหนด highlight ตามช่วงคะแนน
        TableRow rowToHighlight = null;
        int highlightColor = Color.parseColor("#FFE066"); // สีเหลืองเข้มสำหรับ highlight

        if (totalScore < 7) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(1);
            resultCode = "1B0260|1B0282";
            highlightColor = Color.parseColor("#C8E6C9"); // เขียวเข้มขึ้น
        } else if (totalScore >= 7 && totalScore <= 12) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(2);
            resultCode = "1B0261:1B0283";
            highlightColor = Color.parseColor("#FFE082"); // เหลืองเข้มขึ้น
        } else if (totalScore >= 13 && totalScore <= 18) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(3);
            resultCode = "1B0262:1B0284";
            highlightColor = Color.parseColor("#FFAB91"); // ส้มอ่อน
        } else if (totalScore >= 19) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(4);
            resultCode = "1B0263:1B0285";
            highlightColor = Color.parseColor("#EF9A9A"); // แดงอ่อน
        }

        if (rowToHighlight != null) {
            rowToHighlight.setBackgroundColor(highlightColor); // highlight แถวที่ตรงกับช่วงคะแนน
        }

        depression9result.setText(String.format("คะแนนที่ได้: %d คะแนน (%s)", totalScore, resultCode));

        checkAnsweredStatus();
        validateAndSaveData();
    }
    // เพิ่มเมธอด validation ใน StressDepression9qFragment class

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าตอบคำถามครบหรือไม่
        ArrayList<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q): ยังไม่ได้ตอบข้อ ");

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
        if (stressDepression9qInfo == null) {
            return "คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q):\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingQuestions = new ArrayList<>();

        // ตรวจสอบแต่ละคำถาม
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                missingQuestions.add("ข้อ " + i + ": " + getQuestionDescription(i));
            }
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q):\n");
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
        View view = getView();
        if (view != null) {
            for (int i = 1; i <= 9; i++) {
                int radioGroupId = getResources().getIdentifier("rdoStress9qQ" + i, "id", requireContext().getPackageName());
                RadioGroup radioGroup = view.findViewById(radioGroupId);
                if (radioGroup != null) {
                    radioGroup.clearCheck();
                }
            }
        }

        // รีเซ็ต stressDepression9qInfo
        stressDepression9qInfo = new StressDepression9qInfo();

        // รีเซ็ต points
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));

        // รีเซ็ตสถานะการตรวจสอบ
        resetValidation();

        // รีเซ็ตการแสดงผล
        updateScoreDisplay();
        if (tv9qResultDetail != null) {
            tv9qResultDetail.setText("🤔 ยังไม่ได้ประเมิน");
            tv9qResultDetail.setTextColor(Color.parseColor("#7F8C8D"));
            tv9qResultDetail.setBackgroundColor(Color.parseColor("#F8F9FA"));
        }

        // รีเซ็ต Depression Gauge
        resetDepressionGauge();

        // ล้าง highlight ในตาราง
        clearTableHighlight();
    }
    public int getCurrentScore() {
        return sumPoints();
    }
    public String getCompletionStatus() {
        int answered = 0;
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null && !value.equals("0") && !value.isEmpty()) {
                answered++;
            }
        }
        return answered + "/9 ข้อ";
    }
    /**
     * ล้าง highlight ในตาราง
     */
    private void clearTableHighlight() {
        if (depression9resultTable != null) {
            int lightGreen = Color.parseColor("#E8F5E8");
            int lightYellow = Color.parseColor("#FFF3CD");
            int lightRed = Color.parseColor("#F8D7DA");
            int darkRed = Color.parseColor("#F5C6CB");

            for (int i = 1; i < depression9resultTable.getChildCount(); i++) {
                TableRow row = (TableRow) depression9resultTable.getChildAt(i);
                if (row != null) {
                    if (i == 1) row.setBackgroundColor(lightGreen);
                    else if (i == 2) row.setBackgroundColor(lightYellow);
                    else if (i == 3) row.setBackgroundColor(lightRed);
                    else if (i == 4) row.setBackgroundColor(darkRed);
                }
            }
        }
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (stressDepression9qInfo == null) {
            return false;
        }

        // ตรวจสอบว่ามีการตอบคำถามอย่างน้อย 1 ข้อหรือไม่
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null && !value.equals("0") && !value.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (stressDepression9qInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 9;

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null && !value.equals("0") && !value.isEmpty()) {
                completedQuestions++;
            }
        }

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

            // แสดงระดับความรุนแรงด้วย
            String severity = getDepressionSeverity();
            message += " - " + severity;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("StressDepression9q", "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ดึงรายชื่อคำถามที่ยังไม่ได้ตอบ
     */
    public ArrayList<Integer> getUnansweredQuestions() {
        ArrayList<Integer> unanswered = new ArrayList<>();

        if (stressDepression9qInfo == null) {
            for (int i = 1; i <= 9; i++) {
                unanswered.add(i);
            }
            return unanswered;
        }

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                unanswered.add(i);
            }
        }

        return unanswered;
    }

    /**
     * ดึงคำอธิบายของคำถามแต่ละข้อ
     */
    private String getQuestionDescription(int questionNumber) {
        switch (questionNumber) {
            case 1:
                return "เบื่อ ไม่สนใจอยากทำอะไร";
            case 2:
                return "ไม่สบายใจ ซึมเศร้า ท้อแท้";
            case 3:
                return "หลับยาก หรือหลับๆ ตื่นๆ หรือหลับมากไป";
            case 4:
                return "เหนื่อยง่าย หรือไม่ค่อยมีแรง";
            case 5:
                return "เบื่ออาหาร หรือกินมากเกินไป";
            case 6:
                return "รู้สึกไม่ดีกับตัวเอง คิดว่าตัวเองล้มเหลว";
            case 7:
                return "สมาธิไม่ดีเวลาทำอะไร";
            case 8:
                return "พูดช้า ทำอะไรช้าลง หรือกระสับกระส่าย";
            case 9:
                return "คิดทำร้ายตนเอง หรือคิดว่าถ้าตายไปคงจะดี";
            default:
                return "คำถามที่ " + questionNumber;
        }
    }

    /**
     * ตรวจสอบและเลื่อนไปยังคำถามแรกที่ยังไม่ได้ตอบ
     */
    public void scrollToFirstUnansweredQuestion() {
        ArrayList<Integer> unanswered = getUnansweredQuestions();
        if (!unanswered.isEmpty() && getView() != null) {
            int firstUnanswered = unanswered.get(0);

            int radioGroupId = getResources().getIdentifier(
                    "rdoStress9qQ" + firstUnanswered,
                    "id",
                    requireContext().getPackageName()
            );

            RadioGroup targetGroup = getView().findViewById(radioGroupId);
            if (targetGroup != null) {
                targetGroup.requestFocus();
                // สามารถเพิ่มการ scroll ไปยัง view ได้ที่นี่
            }
        }
    }

    /**
     * ดึงระดับความรุนแรงของภาวะซึมเศร้า
     */
    public String getDepressionSeverity() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int totalScore = getTotalScore();

        if (totalScore < 7) {
            return "ไม่มีอาการของโรคซึมเศร้า";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return "มีอาการของโรคซึมเศร้าระดับน้อย";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return "มีอาการของโรคซึมเศร้าระดับปานกลาง";
        } else if (totalScore >= 19) {
            return "มีอาการของโรคซึมเศร้าระดับรุนแรง";
        }

        return "";
    }

    /**
     * ดึงคะแนนรวม
     */
    public int getTotalScore() {
        if (!isFormComplete() || points == null) {
            return -1;
        }

        int totalScore = 0;
        for (Integer point : points) {
            if (point != null) {
                totalScore += point;
            }
        }

        return totalScore;
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงสูงหรือไม่ (คะแนน >= 19 หรือข้อ 9 >= 1)
     */
    public boolean isHighRisk() {
        if (!isFormComplete()) {
            return false;
        }

        // ตรวจสอบคะแนนรวม
        if (getTotalScore() >= 19) {
            return true;
        }

        // ตรวจสอบข้อ 9 (คิดทำร้ายตนเอง)
        String q9Value = getQuestionValue(9);
        if (q9Value != null && !q9Value.equals("1") && !q9Value.equals("0")) { // ถ้าตอบมากกว่า "ไม่มีเลย"
            return true;
        }

        return false;
    }

    /**
     * ตรวจสอบความเสี่ยงการฆ่าตัวตาย (ข้อ 9)
     */
    public boolean hasSuicidalRisk() {
        String q9Value = getQuestionValue(9);
        return q9Value != null && !q9Value.equals("1") && !q9Value.equals("0");
    }

    /**
     * แสดงคำแนะนำตามระดับความรุนแรง
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int totalScore = getTotalScore();

        if (hasSuicidalRisk()) {
            return "⚠️ พบความเสี่ยงในการทำร้ายตนเอง ควรพบแพทย์โดยด่วน!";
        }

        if (totalScore < 7) {
            return "ไม่มีอาการของโรคซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return "มีอาการซึมเศร้าระดับน้อย ควรพักผ่อนให้เพียงพอ ออกกำลังกาย และทำกิจกรรมที่ชื่นชอบ";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return "มีอาการซึมเศร้าระดับปานกลาง ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิต";
        } else if (totalScore >= 19) {
            return "มีอาการซึมเศร้าระดับรุนแรง ควรพบแพทย์เพื่อรับการรักษาโดยเร็ว";
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

        int score = getTotalScore();
        String severity = getDepressionSeverity();

        String summary = String.format("คะแนน: %d - %s", score, severity);

        if (hasSuicidalRisk()) {
            summary += " (⚠️ เสี่ยงทำร้ายตนเอง)";
        }

        return summary;
    }

    /**
     * ตรวจสอบว่าควรทำแบบประเมิน 8Q ต่อหรือไม่
     */
    public boolean shouldDo8QAssessment() {
        return isFormComplete() && hasSuicidalRisk();
    }

    /**
     * ดึงข้อความแนะนำให้ทำ 8Q
     */
    public String get8QRecommendationText() {
        if (shouldDo8QAssessment()) {
            return "⚠️ แนะนำให้ทำแบบประเมินการฆ่าตัวตาย 8Q เพิ่มเติม เนื่องจากพบความเสี่ยงในการทำร้ายตนเอง";
        }
        return "";
    }

    /**
     * ดึงรายการคำถามที่ตอบว่ามีอาการบ่อย (คะแนน >= 2)
     */
    public ArrayList<String> getFrequentSymptoms() {
        ArrayList<String> symptoms = new ArrayList<>();

        if (!isFormComplete()) {
            return symptoms;
        }

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null) {
                int score = Integer.parseInt(value) - 1; // แปลงเป็นคะแนน 0-3
                if (score >= 2) { // บ่อยครั้ง หรือ เกือบทุกวัน
                    symptoms.add("ข้อ " + i + ": " + getQuestionDescription(i));
                }
            }
        }

        return symptoms;
    }


    private void showStress9qCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom layout สำหรับ dialog
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_stress_9q_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("StressDepression9q", "แสดง Dialog เกณฑ์การประเมิน 9Q สำเร็จ");

        } catch (Exception e) {
            Log.e("StressDepression9q", "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());

            // แสดง dialog แบบง่ายหากเกิดข้อผิดพลาด
            showSimpleStress9qCriteriaDialog();
        }
    }

    private void showSimpleStress9qCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "📊 เกณฑ์การคัดกรองโรคซึมเศร้า 9Q\n\n" +
                "😊 < 7 คะแนน: ไม่มีอาการซึมเศร้า (1B0260/1B0282)\n" +
                "🔶 ควรดูแลสุขภาพจิตให้ดีต่อไป\n\n" +

                "😐 7-12 คะแนน: ซึมเศร้าระดับน้อย (1B0261/1B0283)\n" +
                "🔶 ควรพักผ่อนให้เพียงพอ ออกกำลังกาย\n\n" +

                "😟 13-18 คะแนน: ซึมเศร้าระดับปานกลาง (1B0262/1B0284)\n" +
                "🔶 ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิต\n\n" +

                "😰 ≥ 19 คะแนน: ซึมเศร้าระดับรุนแรง (1B0263/1B0285)\n" +
                "🔶 ควรพบแพทย์เพื่อรับการรักษาโดยเร็ว\n\n" +

                "⚠️ หมายเหตุ: หากข้อ 9 ตอบ 'มี' ใดๆ = มีความเสี่ยงการทำร้ายตนเอง\n" +
                "แนะนำทำแบบประเมิน 8Q เพิ่มเติม\n\n";

        builder.setTitle("📈 เกณฑ์การประเมิน 9Q")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // เพิ่ม emoji helper methods
    private String get9qEmoji(int totalScore) {
        if (totalScore < 7) {
            return "😊"; // ปกติ - หน้ายิ้ม
        } else if (totalScore >= 7 && totalScore <= 12) {
            return "😐"; // น้อย - หน้าเฉยๆ
        } else if (totalScore >= 13 && totalScore <= 18) {
            return "😟"; // ปานกลาง - หน้ากังวล
        } else if (totalScore >= 19) {
            return "😰"; // รุนแรง - หน้าตกใจ
        }
        return "🤔"; // ยังไม่ได้ประเมิน
    }

    private String get9qSuicidalRiskEmoji(boolean hasSuicidalRisk) {
        return hasSuicidalRisk ? "⚠️" : "";
    }

    // ปรับปรุง updateResultDisplay method ให้มี emoji
    private void updateResultDisplay(int totalScore) {
        if (tv9qResultDetail == null) {
            Log.e("StressDepression9q", "tv9qResultDetail is null!");
            return;
        }

        String resultText = "";
        String resultCode = "";
        int backgroundColor = Color.parseColor("#F8F9FA");
        int textColor = Color.parseColor("#2C3E50");
        String emoji = get9qEmoji(totalScore);

        if (totalScore < 7) {
            resultText = "ไม่มีอาการของโรคซึมเศร้า";
            resultCode = "1B0260|1B0282";
            backgroundColor = Color.parseColor("#E8F5E8");
            textColor = Color.parseColor("#27AE60");
        } else if (totalScore >= 7 && totalScore <= 12) {
            resultText = "มีอาการของโรคซึมเศร้าระดับน้อย";
            resultCode = "1B0261|1B0283";
            backgroundColor = Color.parseColor("#FFF3CD");
            textColor = Color.parseColor("#F39C12");
        } else if (totalScore >= 13 && totalScore <= 18) {
            resultText = "มีอาการของโรคซึมเศร้าระดับปานกลาง";
            resultCode = "1B0262|1B0284";
            backgroundColor = Color.parseColor("#FFE4CC");
            textColor = Color.parseColor("#E67E22");
        } else if (totalScore >= 19) {
            resultText = "มีอาการของโรคซึมเศร้าระดับรุนแรง";
            resultCode = "1B0263|1B0285";
            backgroundColor = Color.parseColor("#F8D7DA");
            textColor = Color.parseColor("#E74C3C");
        }

        // ตรวจสอบความเสี่ยงการฆ่าตัวตาย (ข้อ 9)
        String q9Value = getQuestionValue(9);
        boolean hasSuicidalRisk = q9Value != null && !q9Value.equals("1") && !q9Value.equals("0");
        String suicidalEmoji = get9qSuicidalRiskEmoji(hasSuicidalRisk);

        String finalText = emoji + " " + resultText + "\n(" + resultCode + ")";
//        if (hasSuicidalRisk) {
//            finalText += "\n" + suicidalEmoji + " พบความเสี่ยงการทำร้ายตนเอง";
//            textColor = Color.parseColor("#E74C3C");
//        }

        tv9qResultDetail.setText(finalText);
        tv9qResultDetail.setTextColor(textColor);
        tv9qResultDetail.setBackgroundColor(backgroundColor);

        Log.d("StressDepression9q", "Result displayed: " + finalText);
    }

    // ปรับปรุง methods อื่นๆ ให้มี emoji
    public String getDepressionSeverityWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        int totalScore = getTotalScore();
        String emoji = get9qEmoji(totalScore);

        if (totalScore < 7) {
            return emoji + " ไม่มีอาการของโรคซึมเศร้า";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return emoji + " มีอาการของโรคซึมเศร้าระดับน้อย";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return emoji + " มีอาการของโรคซึมเศร้าระดับปานกลาง";
        } else if (totalScore >= 19) {
            return emoji + " มีอาการของโรคซึมเศร้าระดับรุนแรง";
        }

        return "";
    }

    public String getRecommendationWithEmoji() {
        if (!isFormComplete()) {
            return "📝 กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int totalScore = getTotalScore();
        String emoji = get9qEmoji(totalScore);
        String suicidalEmoji = get9qSuicidalRiskEmoji(hasSuicidalRisk());

        if (hasSuicidalRisk()) {
            return suicidalEmoji + " พบความเสี่ยงในการทำร้ายตนเอง ควรพบแพทย์โดยด่วน!";
        }

        if (totalScore < 7) {
            return emoji + " ไม่มีอาการของโรคซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return emoji + " มีอาการซึมเศร้าระดับน้อย ควรพักผ่อนให้เพียงพอ ออกกำลังกาย และทำกิจกรรมที่ชื่นชอบ";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return emoji + " มีอาการซึมเศร้าระดับปานกลาง ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิต";
        } else if (totalScore >= 19) {
            return emoji + " มีอาการซึมเศร้าระดับรุนแรง ควรพบแพทย์เพื่อรับการรักษาโดยเร็ว";
        }

        return "";
    }

    public String getSummaryTextWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        int score = getTotalScore();
        String severity = getDepressionSeverityWithEmoji();
        String suicidalEmoji = get9qSuicidalRiskEmoji(hasSuicidalRisk());

        String summary = String.format("คะแนน: %d - %s", score, severity);

        if (hasSuicidalRisk()) {
            summary += " (" + suicidalEmoji + " เสี่ยงทำร้ายตนเอง)";
        }

        return summary;
    }

    public String get8QRecommendationTextWithEmoji() {
        if (shouldDo8QAssessment()) {
            String suicidalEmoji = get9qSuicidalRiskEmoji(true);
            return suicidalEmoji + " แนะนำให้ทำแบบประเมินการฆ่าตัวตาย 8Q เพิ่มเติม เนื่องจากพบความเสี่ยงในการทำร้ายตนเอง";
        }
        return "";
    }

    public void showCompletionStatusWithEmoji() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            String resultInfo = getDepressionSeverityWithEmoji();
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + resultInfo;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("StressDepression9q", "Completion Status: " + message);
    }

}