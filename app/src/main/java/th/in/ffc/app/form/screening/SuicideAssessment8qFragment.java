package th.in.ffc.app.form.screening;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.ScreeningResultCodeDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessmentSummary;
import th.in.ffc.app.form.screening.view.SuicideRiskGaugeView;
import th.in.ffc.person.PersonScreeningForm15Activity;
import th.in.ffc.provider.ScreeningResultCode;
import th.in.ffc.util.Log;
import android.widget.ImageView;
import org.json.JSONArray;
import th.in.ffc.session.UserSessionManager;

public class SuicideAssessment8qFragment extends Fragment {

    SuicideAssessment8qLiveData suicideAssessment8qLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private SuicideAssessment8qInfo suicideAssessment8qInfo;

//    private SuicideAssessment8qInfo suicideInfo;
//    private RadioGroup[] mainQuestionGroups;
//    private RadioGroup subQuestionGroup;
    private static final int MAIN_QUESTION_COUNT = 8;
    private TextView tv8qScore;
    private TextView tv8qResultDetail;

    // เพิ่มตัวแปรสำหรับติดตามสถานะการตอบคำถาม
    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false, false, false, false, false, false, false}; // 8 ข้อ
    private ImageView ivSuicide8qInfoButton;
    private SuicideRiskGaugeView suicideRiskGauge;
    private TextView tvGaugeEmoji;
    private TextView tvGaugeScore;
    private TextView tvGaugeLevel;
    private TextView tvGaugeCode;
    private TextView tvGaugeRecommendation;
    private SeekBar seekBarGaugeTest;
    private ScreeningResultCodeDao screeningResultCodeDao;
    private int currentPersonId = -1;
    private int currentVisitNo = -1;


    public SuicideAssessment8qFragment() {
        // Required empty public constructor
    }

    public static SuicideAssessment8qFragment newInstance(String param1, String param2) {
        SuicideAssessment8qFragment fragment = new SuicideAssessment8qFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suicideAssessment8qLiveData = new SuicideAssessment8qLiveData();
        shareViewModel = new SharedViewModel();
        suicideAssessment8qInfo = new SuicideAssessment8qInfo();

        screeningResultCodeDao = new ScreeningResultCodeDao(getContext());
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

        // Initialize UI elements
        RadioGroup rdoSuicideQ1 = view.findViewById(R.id.rdoSuicideQ1);
        RadioGroup rdoSuicideQ2 = view.findViewById(R.id.rdoSuicideQ2);
        RadioGroup rdoSuicideQ3 = view.findViewById(R.id.rdoSuicideQ3);
        RadioGroup rdoSuicideQ3_2_1 = view.findViewById(R.id.rdoSuicideQ3_2_1);
        RadioGroup rdoSuicideQ4 = view.findViewById(R.id.rdoSuicideQ4);
        RadioGroup rdoSuicideQ5 = view.findViewById(R.id.rdoSuicideQ5);
        RadioGroup rdoSuicideQ6 = view.findViewById(R.id.rdoSuicideQ6);
        RadioGroup rdoSuicideQ7 = view.findViewById(R.id.rdoSuicideQ7);
        RadioGroup rdoSuicideQ8 = view.findViewById(R.id.rdoSuicideQ8);

        // Initialize new UI elements
        tv8qScore = view.findViewById(R.id.tv8qScore);
        tv8qResultDetail = view.findViewById(R.id.tv8qResultDetail);
        ivSuicide8qInfoButton = view.findViewById(R.id.ivSuicide8qInfoButton);
        setupInfoButtonListener();
        initializeGaugeViews(view);

        // Initial display update
        updateScoreDisplay();

        // Setup RadioGroup listeners with updated validation
        rdoSuicideQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ1_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ1_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ1(data);
                questionAnswered[0] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ1(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        rdoSuicideQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ2_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ2_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ2(data);
                questionAnswered[1] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ2(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ3_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ3_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ3(data);
                questionAnswered[2] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ3(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ3_2_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ3_2_1_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ3_2_1_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ3_2_1(data);

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ3_2_1(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ4_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ4_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ4(data);
                questionAnswered[3] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ4(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ5_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ5_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ5(data);
                questionAnswered[4] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ5(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ6_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ6_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ6(data);
                questionAnswered[5] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ6(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ7_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ7_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ7(data);
                questionAnswered[6] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ7(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ8_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ8_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ8(data);
                questionAnswered[7] = true;

                updateScoreAndHighlight();
                validateAndSaveData();

                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
                suicideAssessment8qLiveData.setSelectedQ8(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        loadData();

        // Update display after view is created
        view.post(() -> {
            updateScoreDisplay();
            if (isFormComplete()) {
                updateScoreAndHighlight();
            }
        });

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                currentPersonId = Integer.parseInt(personInfo.getId());
                if (personInfo.getVisitNo() != null && !personInfo.getVisitNo().isEmpty()) {
                    currentVisitNo = Integer.parseInt(personInfo.getVisitNo());

                    // โหลดข้อมูลเดิมจาก ScreeningResultCode (ถ้ามี)
                    loadFromScreeningResultCode(currentPersonId, currentVisitNo);
                }
            }
        });
    }
    public String getDetailedValidationMessage() {
        if (suicideAssessment8qInfo == null) {
            return "การประเมินการฆ่าตัวตายด้วย 8 คำถาม(8Q):\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        List<String> missingQuestions = new ArrayList<>();
        String[] questionDescriptions = {
                "ข้อ 1: คิดอยากตาย หรือ คิดว่าตายไปจะดีกว่า",
                "ข้อ 2: อยากทำร้ายตัวเอง หรือ ทำให้ตัวเองบาดเจ็บ",
                "ข้อ 3: คิดเกี่ยวกับการฆ่าตัวตาย",
                "ข้อ 4: แผนการที่จะฆ่าตัวตาย",
                "ข้อ 5: ได้เตรียมการที่จะทำร้ายตนเองหรือเตรียมการจะฆ่าตัวตาย",
                "ข้อ 6: ได้ทำให้ตนเองบาดเจ็บแต่ไม่ตั้งใจที่จะทำให้เสียชีวิต",
                "ข้อ 7: ได้พยายามฆ่าตัวตายโดยคาดหวัง/ตั้งใจที่จะให้ตาย",
                "ข้อ 8: ท่านเคยพยายามฆ่าตัวตาย"
        };

        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (int i = 0; i < answers.length; i++) {
            if (answers[i] == null || answers[i].equals("0") || answers[i].isEmpty()) {
                missingQuestions.add(questionDescriptions[i]);
            }
        }

        // ตรวจสอบคำถามย่อย Q3_2_1
        if (suicideAssessment8qInfo.getQ3().equals("2") &&
                (suicideAssessment8qInfo.getQ3_2_1().equals("0") || suicideAssessment8qInfo.getQ3_2_1().isEmpty())) {
            missingQuestions.add("คำถามย่อย 3.1: ท่านสามารถควบคุมความอยากฆ่าตัวตายได้หรือไม่");
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("การประเมินการฆ่าตัวตายด้วย 8 คำถาม(8Q):\n");
            message.append("กรุณาตอบคำถามที่ยังไม่ได้ตอบ:\n");
            for (String question : missingQuestions) {
                message.append("• ").append(question).append("\n");
            }
            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }
    public boolean isCriticalRisk() {
        return isFormComplete() && calculateTotalScore() >= 17;
    }

    public String getCriticalRiskMessage() {
        if (!isCriticalRisk()) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        message.append("🆘 ความเสี่ยงวิกฤต!\n\n");
        message.append("คะแนนรวม: ").append(calculateTotalScore()).append(" คะแนน\n");
        message.append("ระดับ: ความเสี่ยงสูง\n\n");

        message.append("📞 ดำเนินการทันที:\n");
        message.append("• ส่งต่อผู้เชี่ยวชาญโดยด่วน\n");
        message.append("• ประเมินความปลอดภัยสิ่งแวดล้อม\n");
        message.append("• แจ้งญาติใกล้ชิด\n");
        message.append("• จัดการดูแลอย่างใกล้ชิด\n");
        message.append("• ติดต่อสายด่วนสุขภาพจิต 1323");

        return message.toString();
    }

    public String getAssessmentResultWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        int totalScore = calculateTotalScore();
        return getResultDescriptionWithEmoji(totalScore);
    }
    private String getRecommendation8Q() {
        if (!isFormComplete()) {
            return "กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int totalScore = calculateTotalScore();

        if (totalScore >= 17) {
            return "⚠️ ความเสี่ยงสูงมาก! ต้องดำเนินการแทรกแซงทันที และส่งต่อผู้เชี่ยวชาญโดยด่วน";
        } else if (totalScore >= 9) {
            return "🚨 ความเสี่ยงปานกลาง ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด พิจารณาส่งต่อผู้เชี่ยวชาญ";
        } else if (totalScore >= 1) {
            return "⚠️ ความเสี่ยงต่ำ ควรให้การสนับสนุนและคำแนะนำ ติดตามสถานการณ์";
        } else {
            return "✅ ไม่มีความเสี่ยง ควรส่งเสริมสุขภาพจิตต่อไป";
        }
    }

    public boolean requiresFollowUp() {
        return isFormComplete() && calculateTotalScore() >= 1;
    }
    public String getFollowUpType() {
        if (!requiresFollowUp()) {
            return "NO_FOLLOW_UP";
        }

        int score = calculateTotalScore();
        if (score >= 17) {
            return "IMMEDIATE_INTERVENTION";
        } else if (score >= 9) {
            return "CLOSE_MONITORING";
        } else {
            return "SUPPORT_COUNSELING";
        }
    }
    public String getReportSummary() {
        if (!isFormComplete()) {
            return "การประเมินยังไม่สมบูรณ์";
        }

        StringBuilder report = new StringBuilder();
        report.append("=== รายงานการประเมินความเสี่ยงการฆ่าตัวตาย 8Q ===\n\n");

        int totalScore = calculateTotalScore();
        report.append("คะแนนรวม: ").append(totalScore).append(" คะแนน\n");
        report.append("ผลการประเมิน: ").append(getResultDescription(totalScore)).append("\n");
        report.append("ระดับความเสี่ยง: ").append(getSuicideRiskSeverityLevel()).append("\n\n");

        List<String> riskAnswers = getRiskAnswers();
        if (!riskAnswers.isEmpty()) {
            report.append("คำตอบที่เป็นความเสี่ยง:\n");
            for (String risk : riskAnswers) {
                report.append("• ").append(risk).append("\n");
            }
            report.append("\n");
        }

        report.append("คำแนะนำ: ").append(getRecommendation8Q()).append("\n");

        if (requiresFollowUp()) {
            report.append("การติดตาม: ").append(getFollowUpType()).append("\n");
        }

        return report.toString();
    }
    private String getSuicideRiskSeverityLevel() {
        int totalScore = calculateTotalScore();

        if (totalScore == 0) {
            return "NO_RISK";
        } else if (totalScore >= 1 && totalScore <= 8) {
            return "LOW_RISK";
        } else if (totalScore >= 9 && totalScore <= 16) {
            return "MODERATE_RISK";
        } else if (totalScore >= 17) {
            return "HIGH_RISK";
        }

        return "UNKNOWN";
    }
    public String getSummaryTextWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 ยังไม่ได้ประเมิน";
        }

        int score = calculateTotalScore();
        String emoji = get8qEmoji(score);
        String resultDescription = getResultDescriptionWithEmoji(score);
        String criticalEmoji = get8qCriticalEmoji(score >= 17);

        String summary = String.format("คะแนน: %d - %s", score, resultDescription);

        if (score >= 17) {
            summary += " (" + criticalEmoji + " วิกฤติ!)";
        } else if (score >= 9) {
            summary += " (⚠️ ต้องติดตาม)";
        }

        return summary;
    }
    public ScreeningResultCodeDao.ScreeningStatistics getStatistics() {
        try {
            return screeningResultCodeDao.getStatisticsByType(ScreeningResultCode.TYPE_SUICIDE_ASSESSMENT_8Q);
        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการดึงสถิติ 8Q: " + e.getMessage());
            return null;
        }
    }
    public void showSaveResult(boolean success, String message) {
        if (success) {
            Toast.makeText(getContext(),
                    "✅ บันทึกผลการประเมินการฆ่าตัวตาย 8Q สำเร็จ",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(),
                    "❌ เกิดข้อผิดพลาดในการบันทึก: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showStatistics() {
        ScreeningResultCodeDao.ScreeningStatistics stats = getStatistics();
        if (stats != null) {
            String message = String.format(
                    "สถิติการประเมิน 8Q:\n" +
                            "จำนวนทั้งหมด: %d ครั้ง\n" +
                            "ปกติ: %d ครั้ง\n" +
                            "ผิดปกติ: %d ครั้ง\n" +
                            "คะแนนเฉลี่ย: %.1f\n" +
                            "คะแนนสูงสุด: %d\n" +
                            "คะแนนต่ำสุด: %d",
                    stats.totalCount, stats.normalCount, stats.abnormalCount,
                    stats.averageScore, stats.maxScore, stats.minScore
            );

            Log.d("SuicideAssessment8q", message);
        }
    }

    private void initializeGaugeViews(View view) {
        suicideRiskGauge = view.findViewById(R.id.suicideRiskGauge);
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
                    if (fromUser && suicideRiskGauge != null) {
                        suicideRiskGauge.setScore(progress);
                        SuicideRiskGaugeView.RiskLevel level = getCurrentRiskLevelFromScore(progress);

                        // อัปเดตข้อความทดสอบ
                        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(level.emoji);
                        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + progress);
                        if (tvGaugeLevel != null) {
                            tvGaugeLevel.setText(level.label);
                            tvGaugeLevel.setTextColor(Color.parseColor(level.color));
                        }
                        if (tvGaugeCode != null) tvGaugeCode.setText(level.code);
//                        if (tvGaugeRecommendation != null) {
//                            updateGaugeRecommendation(progress, level);
//                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }
    public void showGaugeTestControls(boolean show) {
        View layoutGaugeControl = getView().findViewById(R.id.layoutGaugeControl);
        if (layoutGaugeControl != null) {
            layoutGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    // Method สำหรับรีเซ็ต Gauge
    public void resetGauge() {
        if (suicideRiskGauge != null) {
            suicideRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }
    private void updateGaugeDisplay() {
        if (suicideRiskGauge == null) return;

        int totalScore = calculateTotalScore();
        SuicideRiskGaugeView.RiskLevel currentLevel = getCurrentRiskLevelFromScore(totalScore);

        // อัปเดต Gauge
        suicideRiskGauge.setScore(totalScore);

        // อัปเดตข้อความ
        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(currentLevel.emoji);
        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + totalScore);
        if (tvGaugeLevel != null) {
            tvGaugeLevel.setText(currentLevel.label);
            tvGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvGaugeCode != null) tvGaugeCode.setText(currentLevel.code);
//        if (tvGaugeRecommendation != null) {
//            updateGaugeRecommendation(totalScore, currentLevel);
//        }

        Log.d("SuicideAssessment8q", "Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label);
    }
    private void updateGaugeRecommendation(int totalScore, SuicideRiskGaugeView.RiskLevel level) {
        String recommendation = "";
        int backgroundColor = Color.parseColor("#F8F9FA");
        int textColor = Color.parseColor("#2C3E50");

        if (totalScore >= 17) {
            recommendation = "🆘 ต้องการการแทรกแซงทันที!";
            backgroundColor = Color.parseColor("#F8D7DA");
            textColor = Color.parseColor("#E74C3C");
        } else if (totalScore >= 9) {
            recommendation = "🚨 ควรติดตามอย่างใกล้ชิด";
            backgroundColor = Color.parseColor("#FFE4CC");
            textColor = Color.parseColor("#E67E22");
        } else if (totalScore >= 1) {
            recommendation = "⚠️ ควรให้การสนับสนุน";
            backgroundColor = Color.parseColor("#FFF3CD");
            textColor = Color.parseColor("#F39C12");
        } else {
            recommendation = "✅ สถานะปกติ";
            backgroundColor = Color.parseColor("#E8F5E8");
            textColor = Color.parseColor("#27AE60");
        }

        tvGaugeRecommendation.setText(recommendation);
        tvGaugeRecommendation.setTextColor(textColor);
        tvGaugeRecommendation.setBackgroundColor(backgroundColor);
    }
    private SuicideRiskGaugeView.RiskLevel getCurrentRiskLevelFromScore(int score) {
        if (score == 0) {
            return new SuicideRiskGaugeView.RiskLevel(0, 0, "ไม่มีแนวโน้มฆ่าตัวตายในปัจจุบัน", "#27AE60", "😊", "1B0270");
        } else if (score >= 1 && score <= 8) {
            return new SuicideRiskGaugeView.RiskLevel(1, 8, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน ระดับต่ำ", "#F39C12", "😐", "1B0271");
        } else if (score >= 9 && score <= 16) {
            return new SuicideRiskGaugeView.RiskLevel(9, 16, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน ระดับปานกลาง", "#E67E22", "😟", "1B0272");
        } else {
            return new SuicideRiskGaugeView.RiskLevel(17, 52, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน ระดับเสี่ยงสูง", "#E74C3C", "😰", "1B0273");
        }
    }

    private void setupInfoButtonListener() {
        if (ivSuicide8qInfoButton != null) {
            ivSuicide8qInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSuicide8qCriteriaDialog();
                }
            });
        }
    }
    private void showSimpleSuicide8qCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "📊 เกณฑ์การประเมินความเสี่ยงการฆ่าตัวตาย 8Q\n\n" +
                "😊 0 คะแนน: ไม่มีความเสี่ยง (1B0270)\n" +
                "🔶 ไม่มีแนวโน้มฆ่าตัวตายในปัจจุบัน\n\n" +

                "😐 1-8 คะแนน: ความเสี่ยงต่ำ (1B0271)\n" +
                "🔶 ให้คำปรึกษาและสนับสนุน\n\n" +

                "😟 9-16 คะแนน: ความเสี่ยงปานกลาง (1B0272)\n" +
                "🔶 ต้องติดตามอย่างใกล้ชิด\n\n" +

                "😰 ≥ 17 คะแนน: ความเสี่ยงสูง (1B0273)\n" +
                "🔶 ต้องการการแทรกแซงทันที!\n\n" +

                "⚠️ หมายเหตุ: ความเสี่ยงสูงต้องการการแทรกแซงทันที\n" +
                "ควรส่งต่อผู้เชี่ยวชาญและติดตามอย่างใกล้ชิด\n\n";

        builder.setTitle("📈 เกณฑ์การประเมิน 8Q")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String get8qEmoji(int totalScore) {
        if (totalScore == 0) {
            return "😊"; // ไม่มีความเสี่ยง - หน้ายิ้ม
        } else if (totalScore >= 1 && totalScore <= 8) {
            return "😐"; // ความเสี่ยงต่ำ - หน้าเฉยๆ
        } else if (totalScore >= 9 && totalScore <= 16) {
            return "😟"; // ความเสี่ยงปานกลาง - หน้ากังวล
        } else if (totalScore >= 17) {
            return "😰"; // ความเสี่ยงสูง - หน้าตกใจ
        }
        return "🤔"; // ยังไม่ได้ประเมิน
    }
    private String get8qCriticalEmoji(boolean isCritical) {
        return isCritical ? "⚠️" : "";
    }
    public String getResultDescriptionWithEmoji(int totalScore) {
        String emoji = get8qEmoji(totalScore);

        if (totalScore == 0) {
            return emoji + " ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
        } else if (totalScore >= 1 && totalScore <= 8) {
            return emoji + " มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
        } else if (totalScore >= 9 && totalScore <= 16) {
            return emoji + " มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
        } else if (totalScore >= 17) {
            return emoji + " มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
        }
        return "";
    }
    public String getRecommendationWithEmoji() {
        if (!isFormComplete()) {
            return "📝 กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int totalScore = calculateTotalScore();
        String emoji = get8qEmoji(totalScore);
        String criticalEmoji = get8qCriticalEmoji(totalScore >= 17);

        if (totalScore >= 17) {
            return criticalEmoji + " ความเสี่ยงสูงมาก! ต้องดำเนินการแทรกแซงทันที และส่งต่อผู้เชี่ยวชาญ";
        } else if (totalScore >= 9) {
            return emoji + " ความเสี่ยงปานกลาง ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด";
        } else if (totalScore >= 1) {
            return emoji + " ความเสี่ยงต่ำ ควรให้การสนับสนุนและคำแนะนำ";
        } else {
            return emoji + " ไม่มีความเสี่ยง ควรส่งเสริมสุขภาพจิตต่อไป";
        }
    }
    public void showCompletionStatusWithEmoji() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            String resultInfo = getResultDescriptionWithEmoji(calculateTotalScore());
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + resultInfo;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("SuicideAssessment8q", "Completion Status: " + message);
    }
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        List<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("การประเมินการฆ่าตัวตายด้วย 8 คำถาม(8Q): ยังไม่ได้ตอบข้อ ");
            for (int i = 0; i < unansweredQuestions.size(); i++) {
                if (i > 0) {
                    message.append(", ");
                }
                message.append(unansweredQuestions.get(i));
            }
        }

        return message.toString();
    }
    public List<Integer> getUnansweredQuestions() {
        List<Integer> unanswered = new ArrayList<>();

        if (suicideAssessment8qInfo == null) {
            for (int i = 1; i <= 8; i++) {
                unanswered.add(i);
            }
            return unanswered;
        }

        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (int i = 0; i < answers.length; i++) {
            if (answers[i] == null || answers[i].equals("0") || answers[i].isEmpty()) {
                unanswered.add(i + 1);
            }
        }

        return unanswered;
    }
    public int getCompletionPercentage() {
        if (suicideAssessment8qInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 8;

        // ตรวจสอบคำถามหลัก 8 ข้อ
        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (String answer : answers) {
            if (answer != null && !answer.equals("0") && !answer.isEmpty()) {
                completedQuestions++;
            }
        }

        // ตรวจสอบคำถามย่อย Q3_2_1 หากจำเป็น
        if (suicideAssessment8qInfo.getQ3().equals("2")) {
            if (!suicideAssessment8qInfo.getQ3_2_1().equals("0") &&
                    !suicideAssessment8qInfo.getQ3_2_1().isEmpty()) {
                // คำถามย่อยนี้ไม่นับเป็นคำถามแยก แต่เป็นส่วนหนึ่งของ Q3
            }
        }

        return (completedQuestions * 100) / totalQuestions;
    }

    public String getHighRiskQuestionAdviceWithEmoji() {
        if (suicideAssessment8qInfo == null) return "";

        StringBuilder advice = new StringBuilder();
        advice.append("📋 คำแนะนำเพิ่มเติม:\n");

        // ตรวจสอบคำถามที่ให้คะแนนสูงพร้อม emoji
        if ("2".equals(suicideAssessment8qInfo.getQ1())) {
            advice.append("💭 พบการคิดทำร้ายตนเอง - ต้องประเมินความปลอดภัยทันที\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ2())) {
            advice.append("🔪 มีความรู้สึกอยากทำร้ายตัวเอง - ควรส่งต่อผู้เชี่ยวชาญ\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ3())) {
            advice.append("💀 มีแผนการฆ่าตัวตาย - ความเสี่ยงสูงมาก\n");
            if ("2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
                advice.append("🚨 ไม่สามารถควบคุมตนเองได้ - จำเป็นต้องมีการดูแลอย่างใกล้ชิด\n");
            }
        }
        if ("2".equals(suicideAssessment8qInfo.getQ4())) {
            advice.append("📋 มีแผนการฆ่าตัวตายที่ชัดเจน - ต้องแทรกแซงทันที\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ5())) {
            advice.append("🎯 มีการเตรียมการฆ่าตัวตาย - ความเสี่ยงสูงมาก\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ6())) {
            advice.append("🩹 เคยทำร้ายตัวเอง - เพิ่มความเสี่ยง\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ7())) {
            advice.append("⚰️ มีประวัติพยายามฆ่าตัวตายอย่างจริงจัง - ความเสี่ยงสูงมาก\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ8())) {
            advice.append("🔄 เคยพยายามฆ่าตัวตาย - เพิ่มความเสี่ยงการกระทำซ้ำ\n");
        }

        return advice.toString();
    }
    private void showSuicide8qCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom layout สำหรับ dialog
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_suicide_8q_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("SuicideAssessment8q", "แสดง Dialog เกณฑ์การประเมิน 8Q สำเร็จ");

        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());

            // แสดง dialog แบบง่ายหากเกิดข้อผิดพลาด
            showSimpleSuicide8qCriteriaDialog();
        }
    }

    private void updateScoreDisplay() {
        int totalScore = calculateTotalScore();

        Log.d("SuicideAssessment8q", "updateScoreDisplay - Total Score: " + totalScore);

        if (tv8qScore != null) {
            tv8qScore.setText(String.valueOf(totalScore));
            updateScoreBackgroundColor(totalScore);
            Log.d("SuicideAssessment8q", "Score displayed: " + totalScore);
        } else {
            Log.e("SuicideAssessment8q", "tv8qScore is null!");
        }
    }
    private void validateAndSaveData() {
        // ตรวจสอบว่าตอบคำถามครบทุกข้อหรือไม่
        boolean allAnswered = true;
        for (int i = 0; i < 8; i++) {
            if (!questionAnswered[i]) {
                allAnswered = false;
                break;
            }
        }

        // ตรวจสอบคำถามย่อย Q3_2_1 (หากจำเป็น)
        boolean q3SubComplete = true;
        if (suicideAssessment8qInfo.getQ3().equals("2")) { // หากตอบ "มี" ในคำถาม Q3
            q3SubComplete = !suicideAssessment8qInfo.getQ3_2_1().equals("0");
        }

        isFormValid = allAnswered && q3SubComplete;

        // อัปเดตการแสดงผลทันที - ไม่ต้องรอให้ครบ
        updateScoreDisplay();
        updateResultDisplay(calculateTotalScore());

        if (isFormValid) {
            Log.d("SuicideAssessment8q", "ตอบคำถามครบทุกข้อแล้ว - บันทึกข้อมูล");
            if (dataPasser != null) {
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);
            }
        }

        // อัปเดตสถานะใน Activity
        updateFormStatusInActivity();
    }

    private void updateScoreBackgroundColor(int score) {
        if (tv8qScore == null) {
            Log.e("SuicideAssessment8q", "tv8qScore is null in updateScoreBackgroundColor");
            return;
        }

        int color;
        if (score == 0) {
            color = Color.parseColor("#27AE60"); // เขียว - ไม่มีความเสี่ยง
        } else if (score >= 1 && score <= 8) {
            color = Color.parseColor("#F39C12"); // เหลือง - ความเสี่ยงต่ำ
        } else if (score >= 9 && score <= 16) {
            color = Color.parseColor("#E67E22"); // ส้ม - ความเสี่ยงปานกลาง
        } else {
            color = Color.parseColor("#E74C3C"); // แดง - ความเสี่ยงสูง
        }

        tv8qScore.setBackgroundColor(color);
        tv8qScore.setTextColor(Color.WHITE);

        Log.d("SuicideAssessment8q", "Background color updated for score: " + score);
    }
    private void updateResultDisplay(int totalScore) {
        if (tv8qResultDetail == null) {
            Log.e("SuicideAssessment8q", "tv8qResultDetail is null!");
            return;
        }

        String resultText = "";
        String resultCode = "";
        String emoji = "";
        String statusEmoji = "";
        int backgroundColor = Color.parseColor("#F8F9FA");
        int textColor = Color.parseColor("#2C3E50");

        if (totalScore == 0) {
            emoji = "😊";
            statusEmoji = "✅";
            resultText = "ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
            resultCode = "1B0270";
            backgroundColor = Color.parseColor("#E8F5E8");
            textColor = Color.parseColor("#27AE60");
        } else if (totalScore >= 1 && totalScore <= 8) {
            emoji = "😐";
            statusEmoji = "⚠️";
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
            resultCode = "1B0271";
            backgroundColor = Color.parseColor("#FFF3CD");
            textColor = Color.parseColor("#F39C12");
        } else if (totalScore >= 9 && totalScore <= 16) {
            emoji = "😟";
            statusEmoji = "🚨";
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
            resultCode = "1B0272";
            backgroundColor = Color.parseColor("#FFE4CC");
            textColor = Color.parseColor("#E67E22");
        } else if (totalScore >= 17) {
            emoji = "😰";
            statusEmoji = "🆘";
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
            resultCode = "1B0273";
            backgroundColor = Color.parseColor("#F8D7DA");
            textColor = Color.parseColor("#E74C3C");
        }

        // สร้างข้อความหลักพร้อม emoji
        String finalText = emoji + " " + resultText + "\n(" + resultCode + ")";

        // เพิ่มคำเตือนพิเศษสำหรับแต่ละระดับพร้อม emoji
//        if (totalScore >= 17) {
//            finalText += "\n" + statusEmoji + " ต้องการการแทรกแซงทันที!";
//            finalText += "\n🏥 ส่งต่อผู้เชี่ยวชาญโดยด่วน";
//            textColor = Color.parseColor("#E74C3C");
//        } else if (totalScore >= 9) {
//            finalText += "\n" + statusEmoji + " ควรติดตามอย่างใกล้ชิด";
//            finalText += "\n👨‍⚕️ แนะนำพบแพทย์เพื่อปรึกษา";
//            textColor = Color.parseColor("#E67E22");
//        } else if (totalScore >= 1) {
//            finalText += "\n" + statusEmoji + " ควรให้การสนับสนุน";
//            finalText += "\n🤝 ให้คำปรึกษาและกำลังใจ";
//            textColor = Color.parseColor("#F39C12");
//        } else {
//            finalText += "\n" + statusEmoji + " สถานะปกติ";
//            finalText += "\n🌟 ควรส่งเสริมสุขภาพจิตต่อไป";
//            textColor = Color.parseColor("#27AE60");
//        }

        tv8qResultDetail.setText(finalText);
        tv8qResultDetail.setTextColor(textColor);
        tv8qResultDetail.setBackgroundColor(backgroundColor);

        Log.d("SuicideAssessment8q", "Result displayed with emoji: " + finalText);
    }
    private void loadData(){
        SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSuicideAssessment8qMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<SuicideAssessment8qInfo> suicideAssessment8qInfos = sfSuicideAssessment8qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(SuicideAssessment8qInfo suicideAssessment8qInfo1 :suicideAssessment8qInfos){
                    Log.d("suicideAssessment8qInfo1 ", "suicideAssessment8qInfo1 infos:"+suicideAssessment8qInfo1);
//                    this.suicideAssessment8qInfo = stressDepression2qInfo;
                    setSuicideAssessment8qInfo(suicideAssessment8qInfo1);
                }
            }
        });
    }
    private void setQuestionValue(int questionNumber, String value) {
        switch (questionNumber) {
            case 1: suicideAssessment8qInfo.setQ1(value); break;
            case 2: suicideAssessment8qInfo.setQ2(value); break;
            case 3: suicideAssessment8qInfo.setQ3(value); break;
            case 4: suicideAssessment8qInfo.setQ4(value); break;
            case 5: suicideAssessment8qInfo.setQ5(value); break;
            case 6: suicideAssessment8qInfo.setQ6(value); break;
            case 7: suicideAssessment8qInfo.setQ7(value); break;
            case 8: suicideAssessment8qInfo.setQ8(value); break;
        }
    }

    public void setSuicideAssessment8qInfo(SuicideAssessment8qInfo info) {
        this.suicideAssessment8qInfo = info;
        loadExistingData();
    }
    private void loadExistingData() {
        if (suicideAssessment8qInfo == null) return;

        // Load main questions
        for (int i = 0; i < MAIN_QUESTION_COUNT; i++) {
            String value = getQuestionValue(i + 1);
            if (!value.equals("0")) {
                int radioButtonId = getResources().getIdentifier(
                        "rdoSuicideQ" + (i + 1) + "_" + (Integer.parseInt(value)),
                        "id",
                        requireContext().getPackageName()
                );
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }

                // อัปเดตสถานะการตอบ
                questionAnswered[i] = true;
            }
        }

        // Load Q3 sub-question if necessary
        if (suicideAssessment8qInfo.getQ3().equals("1") || suicideAssessment8qInfo.getQ3().equals("2")) {
            String subValue = suicideAssessment8qInfo.getQ3_2_1();
            if (!subValue.equals("0")) {
                RadioButton radioButton = requireView().findViewById(
                        subValue.equals("1") ? R.id.rdoSuicideQ3_2_1_1 : R.id.rdoSuicideQ3_2_1_2
                );
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
            }
        }

        // อัปเดตการแสดงผลหลังจากโหลดข้อมูล
        updateScoreAndHighlight();
        validateAndSaveData();
    }

    // เพิ่มเมธอดสำหรับดึงคะแนนปัจจุบัน
    public int getCurrentScore() {
        return calculateTotalScore();
    }

    // เพิ่มเมธอดสำหรับดึงสถานะความสมบูรณ์
    public String getCompletionStatus() {
        int answered = 0;
        for (boolean isAnswered : questionAnswered) {
            if (isAnswered) answered++;
        }

        // ตรวจสอบคำถามย่อย
        if (suicideAssessment8qInfo.getQ3().equals("2") &&
                !suicideAssessment8qInfo.getQ3_2_1().equals("0")) {
            answered++; // นับคำถามย่อยด้วย
        }

        return answered + "/8 ข้อ";
    }

    public boolean isFormComplete() {
        if (suicideAssessment8qInfo == null) {
            return false;
        }

        // ตรวจสอบคำถามหลัก Q1-Q8
        boolean q1Complete = !suicideAssessment8qInfo.getQ1().equals("0");
        boolean q2Complete = !suicideAssessment8qInfo.getQ2().equals("0");
        boolean q3Complete = !suicideAssessment8qInfo.getQ3().equals("0");
        boolean q4Complete = !suicideAssessment8qInfo.getQ4().equals("0");
        boolean q5Complete = !suicideAssessment8qInfo.getQ5().equals("0");
        boolean q6Complete = !suicideAssessment8qInfo.getQ6().equals("0");
        boolean q7Complete = !suicideAssessment8qInfo.getQ7().equals("0");
        boolean q8Complete = !suicideAssessment8qInfo.getQ8().equals("0");

        // ตรวจสอบคำถามย่อย Q3_2_1 (หากจำเป็น)
        boolean q3SubComplete = true;
        if (suicideAssessment8qInfo.getQ3().equals("2")) { // หากตอบ "มี" ในคำถาม Q3
            q3SubComplete = !suicideAssessment8qInfo.getQ3_2_1().equals("0");
        }

        return q1Complete && q2Complete && q3Complete && q4Complete &&
                q5Complete && q6Complete && q7Complete && q8Complete && q3SubComplete;
    }

    private String getQuestionValue(int questionNumber) {
        switch (questionNumber) {
            case 1: return suicideAssessment8qInfo.getQ1();
            case 2: return suicideAssessment8qInfo.getQ2();
            case 3: return suicideAssessment8qInfo.getQ3();
            case 4: return suicideAssessment8qInfo.getQ4();
            case 5: return suicideAssessment8qInfo.getQ5();
            case 6: return suicideAssessment8qInfo.getQ6();
            case 7: return suicideAssessment8qInfo.getQ7();
            case 8: return suicideAssessment8qInfo.getQ8();
            default: return "0";
        }
    }

    public SuicideAssessment8qInfo getSuicideAssessment8qInfo() {
        return suicideAssessment8qInfo;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suicide_assessment8q, container, false);
    }

    private int calculateTotalScore() {
        int totalScore = 0;

        // คำนวณคะแนนตามตารางคะแนนที่กำหนด
        if ("2".equals(suicideAssessment8qInfo.getQ1())) totalScore += 1;  // ถ้ามี = 1 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ2())) totalScore += 2;  // ถ้ามี = 2 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ3())) totalScore += 6;  // ถ้ามี = 6 คะแนน

        // ถ้าตอบมีในข้อ 3 และตอบไม่ได้ในคำถามย่อย
        if ("2".equals(suicideAssessment8qInfo.getQ3()) &&
                "2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
            totalScore += 8;
        }

        if ("2".equals(suicideAssessment8qInfo.getQ4())) totalScore += 8;  // ถ้ามี = 8 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ5())) totalScore += 9;  // ถ้ามี = 9 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ6())) totalScore += 4;  // ถ้ามี = 4 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ7())) totalScore += 10; // ถ้ามี = 10 คะแนน
        if ("2".equals(suicideAssessment8qInfo.getQ8())) totalScore += 4;  // ถ้ามี = 4 คะแนน

        return totalScore;
    }
    private void highlightScoreRow(int totalScore) {
        // หา reference ของแต่ละแถว
        TableRow row0 = getView().findViewById(R.id.scoreRow0);
        TableRow row1_8 = getView().findViewById(R.id.scoreRow1_8);
        TableRow row9_16 = getView().findViewById(R.id.scoreRow9_16);
        TableRow row17plus = getView().findViewById(R.id.scoreRow17plus);

        // เก็บสีพื้นหลังเดิมไว้
        int defaultWhite = ContextCompat.getColor(requireContext(), R.color.white);
        int lightGray = ContextCompat.getColor(requireContext(), R.color.light_gray);

        // สีไฮไลท์ตามระดับความเสี่ยง
        int highlightNone = ContextCompat.getColor(requireContext(), R.color.highlight_green);    // เขียวอ่อน - ไม่มีความเสี่ยง
        int highlightLow = ContextCompat.getColor(requireContext(), R.color.warning_light);      // ส้มอ่อน - ความเสี่ยงต่ำ
        int highlightMedium = ContextCompat.getColor(requireContext(), R.color.highlight_yellow); // เหลือง - ความเสี่ยงปานกลาง
        int highlightHigh = ContextCompat.getColor(requireContext(), R.color.highlight_red);     // แดงอ่อน - ความเสี่ยงสูง

        // รีเซ็ตสีพื้นหลังเป็นค่าเริ่มต้น
        row0.setBackgroundColor(defaultWhite);
        row1_8.setBackgroundColor(defaultWhite);
        row9_16.setBackgroundColor(defaultWhite);
        row17plus.setBackgroundColor(defaultWhite);

        // ไฮไลท์แถวตามช่วงคะแนนและเก็บรหัสที่เกี่ยวข้อง
        String resultCode = "";
        String riskLevel = "";
        int highlightColor = defaultWhite;

        if (totalScore == 0) {
            row0.setBackgroundColor(highlightNone);
            resultCode = "1B0270";
            riskLevel = "ไม่มีแนวโน้มฆ่าตัวต่ายในปัจจุบัน";
            highlightColor = highlightNone;
        } else if (totalScore >= 1 && totalScore <= 8) {
            row1_8.setBackgroundColor(highlightLow);
            resultCode = "1B0271";
            riskLevel = "มีแนวโน้มฆ่าตัวต่ายในปัจจุบัน ระดับต่ำ";
            highlightColor = highlightLow;
        } else if (totalScore >= 9 && totalScore <= 16) {
            row9_16.setBackgroundColor(highlightMedium);
            resultCode = "1B0272";
            riskLevel = "มีแนวโน้มฆ่าตัวต่ายในปัจจุบัน ระดับปานกลาง";
            highlightColor = highlightMedium;
        } else if (totalScore >= 17) {
            row17plus.setBackgroundColor(highlightHigh);
            resultCode = "1B0273";
            riskLevel = "มีแนวโน้มฆ่าตัวต่ายในปัจจุบัน ระดับสูง";
            highlightColor = highlightHigh;
        }

        // แสดงผลคะแนนและรหัสในรูปแบบที่สวยงาม
        TextView resultTextView = getView().findViewById(R.id.resultTextView);
        if (resultTextView != null) {
            String resultText = String.format("คะแนนที่ได้: %d คะแนน\n%s (%s)",
                    totalScore, riskLevel, resultCode);
            resultTextView.setText(resultText);

            // เพิ่มไอคอนตามระดับความเสี่ยง
            if (totalScore >= 17) {
                // เตือนความเสี่ยงสูง
                resultTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_warning, 0, 0, 0);
                resultTextView.setCompoundDrawablePadding(8);
            } else if (totalScore >= 9) {
                // เตือนความเสี่ยงปานกลาง
                resultTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_info, 0, 0, 0);
                resultTextView.setCompoundDrawablePadding(8);
            } else if (totalScore >= 1) {
                // ความเสี่ยงต่ำ
                resultTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_circle, 0, 0, 0);
                resultTextView.setCompoundDrawablePadding(8);
            } else {
                // ไม่มีความเสี่ยง
                resultTextView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_circle, 0, 0, 0);
                resultTextView.setCompoundDrawablePadding(8);
            }
        }

        // เพิ่มเอฟเฟกต์การเปลี่ยนสี
        animateRowHighlight(getActiveRow(totalScore), highlightColor);
    }
    /**
     * รับแถวที่ต้องไฮไลท์ตามคะแนน
     */
    private TableRow getActiveRow(int totalScore) {
        if (totalScore == 0) {
            return getView().findViewById(R.id.scoreRow0);
        } else if (totalScore >= 1 && totalScore <= 8) {
            return getView().findViewById(R.id.scoreRow1_8);
        } else if (totalScore >= 9 && totalScore <= 16) {
            return getView().findViewById(R.id.scoreRow9_16);
        } else {
            return getView().findViewById(R.id.scoreRow17plus);
        }
    }
    /**
     * เพิ่มเอฟเฟกต์การเปลี่ยนสีแบบ animation
     */
    private void animateRowHighlight(TableRow targetRow, int highlightColor) {
        if (targetRow == null) return;

        // สร้าง animation สำหรับการเปลี่ยนสี
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                ContextCompat.getColor(requireContext(), R.color.white),
                highlightColor
        );

        colorAnimator.setDuration(500); // 0.5 วินาที
        colorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        colorAnimator.addUpdateListener(animation -> {
            int animatedColor = (int) animation.getAnimatedValue();
            targetRow.setBackgroundColor(animatedColor);
        });

        colorAnimator.start();

        // เพิ่มเอฟเฟกต์การสั่น (pulse) เล็กน้อย
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(targetRow, "scaleX", 1.0f, 1.02f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(targetRow, "scaleY", 1.0f, 1.02f, 1.0f);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(scaleX, scaleY);
        pulseSet.setDuration(300);
        pulseSet.start();
    }
    /**
     * เพิ่มเมธอดสำหรับอัปเดตสีของ RadioButton เมื่อถูกเลือก
     */
    private void updateRadioButtonColors() {
        if (getView() == null || suicideAssessment8qInfo == null) return;

        // อัปเดตสี RadioButton สำหรับแต่ละคำถาม
        updateQuestionRadioColors(R.id.rdoSuicideQ1, suicideAssessment8qInfo.getQ1());
        updateQuestionRadioColors(R.id.rdoSuicideQ2, suicideAssessment8qInfo.getQ2());
        updateQuestionRadioColors(R.id.rdoSuicideQ3, suicideAssessment8qInfo.getQ3());
        updateQuestionRadioColors(R.id.rdoSuicideQ3_2_1, suicideAssessment8qInfo.getQ3_2_1());
        updateQuestionRadioColors(R.id.rdoSuicideQ4, suicideAssessment8qInfo.getQ4());
        updateQuestionRadioColors(R.id.rdoSuicideQ5, suicideAssessment8qInfo.getQ5());
        updateQuestionRadioColors(R.id.rdoSuicideQ6, suicideAssessment8qInfo.getQ6());
        updateQuestionRadioColors(R.id.rdoSuicideQ7, suicideAssessment8qInfo.getQ7());
        updateQuestionRadioColors(R.id.rdoSuicideQ8, suicideAssessment8qInfo.getQ8());
    }
    /**
     * อัปเดตสีของ RadioGroup ตามคำตอบ
     */
    private void updateQuestionRadioColors(int radioGroupId, String answer) {
        RadioGroup radioGroup = getView().findViewById(radioGroupId);
        if (radioGroup == null) return;

        // สีสำหรับคำตอบที่แตกต่างกัน
        int selectedColor = ContextCompat.getColor(requireContext(), R.color.suicide_primary);
        int riskColor = ContextCompat.getColor(requireContext(), R.color.risk_high);
        int safeColor = ContextCompat.getColor(requireContext(), R.color.risk_none);

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                // ถ้าเลือก "มี" (ค่า "2") ให้เป็นสีแดง, ถ้าเลือก "ไม่มี" (ค่า "1") ให้เป็นสีเขียว
                if (answer.equals("2")) {
                    radioButton.setTextColor(riskColor);
                    radioButton.setTypeface(null, Typeface.BOLD);
                } else if (answer.equals("1")) {
                    radioButton.setTextColor(safeColor);
                    radioButton.setTypeface(null, Typeface.BOLD);
                }
            } else {
                // รีเซ็ตสีสำหรับตัวเลือกที่ไม่ได้เลือก
                radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark));
                radioButton.setTypeface(null, Typeface.NORMAL);
            }
        }
    }
    /**
     * เพิ่มเมธอดสำหรับแสดงสถิติการตอบคำถาม
     */
    public void showAnswerStatistics() {
        if (suicideAssessment8qInfo == null) return;

        int totalAnswered = 0;
        int riskAnswers = 0; // จำนวนคำตอบที่เป็น "มี"

        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (String answer : answers) {
            if (!answer.equals("0")) {
                totalAnswered++;
                if (answer.equals("2")) {
                    riskAnswers++;
                }
            }
        }

        // ตรวจสอบคำถามย่อย Q3_2_1
        if (!suicideAssessment8qInfo.getQ3_2_1().equals("0")) {
            totalAnswered++;
            if (suicideAssessment8qInfo.getQ3_2_1().equals("2")) {
                riskAnswers++;
            }
        }

        String statisticsMessage = String.format(
                "สถิติการตอบคำถาม:\n" +
                        "- ตอบแล้ว: %d/%d คำถาม\n" +
                        "- คำตอบ 'มี': %d ข้อ\n" +
                        "- คำตอบ 'ไม่มี': %d ข้อ\n" +
                        "- ความครบถ้วน: %.1f%%",
                totalAnswered, MAIN_QUESTION_COUNT,
                riskAnswers,
                totalAnswered - riskAnswers,
                (totalAnswered / (float) MAIN_QUESTION_COUNT) * 100
        );

        if (getContext() != null) {
            Toast.makeText(getContext(), statisticsMessage, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * เพิ่มเมธอดสำหรับ Export ข้อมูลเป็น JSON
     */
    public String exportDataAsJson() {
        if (suicideAssessment8qInfo == null) {
            return "{}";
        }

        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("assessment_type", "suicide_assessment_8q");
            jsonData.put("timestamp", System.currentTimeMillis());
            jsonData.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            // ข้อมูลคำถาม
            JSONObject questions = new JSONObject();
            questions.put("q1", suicideAssessment8qInfo.getQ1());
            questions.put("q2", suicideAssessment8qInfo.getQ2());
            questions.put("q3", suicideAssessment8qInfo.getQ3());
            questions.put("q3_2_1", suicideAssessment8qInfo.getQ3_2_1());
            questions.put("q4", suicideAssessment8qInfo.getQ4());
            questions.put("q5", suicideAssessment8qInfo.getQ5());
            questions.put("q6", suicideAssessment8qInfo.getQ6());
            questions.put("q7", suicideAssessment8qInfo.getQ7());
            questions.put("q8", suicideAssessment8qInfo.getQ8());
            jsonData.put("questions", questions);

            // ผลการประเมิน
            int totalScore = calculateTotalScore();
            JSONObject results = new JSONObject();
            results.put("total_score", totalScore);
            results.put("result_code", getResultCode(totalScore));
            results.put("result_description", getResultDescriptionWithEmoji(totalScore));
            results.put("risk_level", getRiskLevel(totalScore));
            results.put("is_complete", isFormComplete());
            jsonData.put("results", results);

            return jsonData.toString(2); // Pretty print with indentation

        } catch (JSONException e) {
            e.printStackTrace();
            return "{ \"error\": \"Failed to export data\" }";
        }
    }

    /**
     * รับระดับความเสี่ยงเป็นข้อความ
     */
    private String getRiskLevel(int totalScore) {
        if (totalScore == 0) return "no_risk";
        else if (totalScore <= 8) return "low_risk";
        else if (totalScore <= 16) return "moderate_risk";
        else return "high_risk";
    }

    /**
     * เพิ่มการตรวจสอบและแจ้งเตือนเมื่อมีการตอบคำถามที่เสี่ยงสูง
     */
    private void checkCriticalQuestions() {
        if (suicideAssessment8qInfo == null) return;

        List<String> criticalWarnings = new ArrayList<>();

        // ตรวจสอบคำถามวิกฤต
        if ("2".equals(suicideAssessment8qInfo.getQ1())) {
            criticalWarnings.add("• พบการคิดอยากตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ2())) {
            criticalWarnings.add("• มีความต้องการทำร้ายตนเอง");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ3())) {
            criticalWarnings.add("• มีการคิดเกี่ยวกับการฆ่าตัวตาย");
            if ("2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
                criticalWarnings.add("• ไม่สามารถควบคุมความคิดฆ่าตัวตายได้");
            }
        }
        if ("2".equals(suicideAssessment8qInfo.getQ4())) {
            criticalWarnings.add("• มีแผนการฆ่าตัวตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ5())) {
            criticalWarnings.add("• มีการเตรียมการฆ่าตัวตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ7())) {
            criticalWarnings.add("• เคยพยายามฆ่าตัวตายอย่างจริงจัง");
        }

        // แสดงการเตือนหากมีคำตอบที่เสี่ยง
//        if (!criticalWarnings.isEmpty() && getContext() != null) {
//            StringBuilder warningMessage = new StringBuilder();
//            warningMessage.append("⚠️ พบสัญญาณเตือนสำคัญ:\n\n");
//            for (String warning : criticalWarnings) {
//                warningMessage.append(warning).append("\n");
//            }
//            warningMessage.append("\n🚨 แนะนำให้ดำเนินการตามขั้นตอนการแทรกแซงวิกฤต");
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("สัญญาณเตือนความเสี่ยงสูง")
//                    .setMessage(warningMessage.toString())
//                    .setPositiveButton("รับทราบ", null)
//                    .setNegativeButton("ดูคำแนะนำ", (dialog, which) -> showDetailedAdvice())
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setCancelable(false)
//                    .show();
//        }
    }

    // เพิ่มฟังก์ชันอัพเดทคะแนนและไฮไลท์
    /**
     * อัปเดตเมธอด updateScoreAndHighlight ให้เรียกใช้ฟังก์ชันใหม่
     */
    private void updateScoreAndHighlight() {
        int totalScore = calculateTotalScore();

        // อัปเดตการแสดงผลเดิม
        updateScoreDisplay();
        updateResultDisplay(totalScore);

        // อัปเดต Gauge ใหม่
        updateGaugeDisplay();

        // เรียกใช้การ highlight ตารางเดิม (ถ้ายังต้องการ)
        highlightScoreRow(totalScore);

        // เพิ่มการตรวจสอบและอัปเดตสีใหม่
        updateRadioButtonColors();
        checkCriticalQuestions();

        // แจ้งเตือนหากมีความเสี่ยงสูง
//        checkAndNotifyRiskLevel(totalScore);

        // อัปเดตสถานะการกรอกข้อมูลใน Activity หลัก
        updateFormStatusInActivity();

        Log.d("SuicideAssessment8q", "Score, highlight and gauge updated - Total: " + totalScore);
    }
    /**
     * อัปเดตสถานะการกรอกข้อมูลใน Activity หลัก
     */
    private void updateFormStatusInActivity() {
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();
            boolean isComplete = isFormComplete();
            activity.updateFormStatus("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", isComplete);
        }
    }
    /**
     * ตรวจสอบคำถามที่มีคะแนนสูงและให้คำแนะนำเพิ่มเติม
     */
    public String getHighRiskQuestionAdvice() {
        if (suicideAssessment8qInfo == null) return "";

        StringBuilder advice = new StringBuilder();
        advice.append("คำแนะนำเพิ่มเติม:\n");

        // ตรวจสอบคำถามที่ให้คะแนนสูง
        if ("2".equals(suicideAssessment8qInfo.getQ1())) {
            advice.append("• พบการคิดทำร้ายตนเอง - ต้องประเมินความปลอดภัยทันที\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ2())) {
            advice.append("• มีความรู้สึกอยากตาย - ควรส่งต่อผู้เชี่ยวชาญ\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ3())) {
            advice.append("• มีแผนการฆ่าตัวตาย - ความเสี่ยงสูงมาก\n");
            if ("2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
                advice.append("• ไม่สามารถควบคุมตนเองได้ - จำเป็นต้องมีการดูแลอย่างใกล้ชิด\n");
            }
        }
        if ("2".equals(suicideAssessment8qInfo.getQ5())) {
            advice.append("• มีประวัติพยายามฆ่าตัวตาย - เพิ่มความเสี่ยง\n");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ7())) {
            advice.append("• มีแผนการฆ่าตัวตายที่ชัดเจน - ต้องแทรกแซงทันที\n");
        }

        return advice.toString();
    }
    /**
     * ตรวจสอบและแจ้งเตือนระดับความเสี่ยงอัตโนมัติ
     */
    private void checkAndNotifyRiskLevel(int totalScore) {
        String emoji = get8qEmoji(totalScore);
        String criticalEmoji = get8qCriticalEmoji(totalScore >= 17);

        if (totalScore >= 17) {
            // ความเสี่ยงสูงมาก - แจ้งเตือนทันที
            Toast.makeText(getContext(),
                    criticalEmoji + " ความเสี่ยงสูงมาก: " + totalScore + " คะแนน\n" +
                            "จำเป็นต้องดำเนินการแทรกแซงทันที",
                    Toast.LENGTH_SHORT).show();
        } else if (totalScore >= 9) {
            // ความเสี่ยงปานกลาง
            Toast.makeText(getContext(),
                    emoji + " ความเสี่ยงปานกลาง: " + totalScore + " คะแนน\n" +
                            "ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด",
                    Toast.LENGTH_SHORT).show();
        } else if (totalScore >= 1) {
            // ความเสี่ยงต่ำ
            Toast.makeText(getContext(),
                    emoji + " ความเสี่ยงต่ำ: " + totalScore + " คะแนน\n" +
                            "ควรให้คำแนะนำและสนับสนุน",
                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * แสดงการแจ้งเตือนความเสี่ยง
     */
    private void showRiskAlert(String title, String message, int titleColor) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom title view
            TextView titleView = new TextView(getContext());
            titleView.setText(title);
            titleView.setTextColor(titleColor);
            titleView.setTextSize(18);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setPadding(24, 24, 24, 8);

            builder.setCustomTitle(titleView)
                    .setMessage(message)
                    .setPositiveButton("รับทราบ", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }
    /**
     * แสดงข้อมูลความเสี่ยง (สำหรับความเสี่ยงต่ำ)
     */
    private void showRiskInfo(String title, String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), title + "\n" + message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * ตรวจสอบความครบถ้วนของข้อมูลแบบละเอียด
     * @return ข้อความแสดงรายการคำถามที่ยังไม่ได้ตอบ
     */
    public String getIncompleteQuestions() {
        if (suicideAssessment8qInfo == null) {
            return "ยังไม่ได้เริ่มตอบคำถาม";
        }

        StringBuilder incompleteQuestions = new StringBuilder();

        if (suicideAssessment8qInfo.getQ1().equals("0")) {
            incompleteQuestions.append("คำถามที่ 1, ");
        }
        if (suicideAssessment8qInfo.getQ2().equals("0")) {
            incompleteQuestions.append("คำถามที่ 2, ");
        }
        if (suicideAssessment8qInfo.getQ3().equals("0")) {
            incompleteQuestions.append("คำถามที่ 3, ");
        }
        if (suicideAssessment8qInfo.getQ4().equals("0")) {
            incompleteQuestions.append("คำถามที่ 4, ");
        }
        if (suicideAssessment8qInfo.getQ5().equals("0")) {
            incompleteQuestions.append("คำถามที่ 5, ");
        }
        if (suicideAssessment8qInfo.getQ6().equals("0")) {
            incompleteQuestions.append("คำถามที่ 6, ");
        }
        if (suicideAssessment8qInfo.getQ7().equals("0")) {
            incompleteQuestions.append("คำถามที่ 7, ");
        }
        if (suicideAssessment8qInfo.getQ8().equals("0")) {
            incompleteQuestions.append("คำถามที่ 8, ");
        }

        // ตรวจสอบคำถามย่อย
        if (suicideAssessment8qInfo.getQ3().equals("2") &&
                suicideAssessment8qInfo.getQ3_2_1().equals("0")) {
            incompleteQuestions.append("คำถามย่อย 3.1, ");
        }

        if (incompleteQuestions.length() > 0) {
            // ลบเครื่องหมายจุลภาคและช่องว่างท้ายสุด
            incompleteQuestions.setLength(incompleteQuestions.length() - 2);
            return "กรุณาตอบ: " + incompleteQuestions.toString();
        }

        return "";
    }

    /**
     * แสดงสถานะการกรอกข้อมูล
     */
    public void showCompletionStatus() {
        if (isFormComplete()) {
            int totalScore = calculateTotalScore();
            String resultCode = getResultCode(totalScore);
            // แสดงผลสำเร็จ
            if (getContext() != null) {
                Toast.makeText(getContext(),
                        "กรอกข้อมูลครบถ้วนแล้ว\nคะแนนรวม: " + totalScore + " (" + resultCode + ")",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            String incompleteMsg = getIncompleteQuestions();
            if (getContext() != null) {
                Toast.makeText(getContext(), incompleteMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * รับรหัสผลการประเมิน
     */
    private String getResultCode(int totalScore) {
        if (totalScore == 0) {
            return "1B0270";
        } else if (totalScore >= 1 && totalScore <= 8) {
            return "1B0271";
        } else if (totalScore >= 9 && totalScore <= 16) {
            return "1B0272";
        } else if (totalScore >= 17) {
            return "1B0273";
        }
        return "";
    }

    /**
     * รับคำอธิบายผลการประเมิน
     */
    public String getResultDescription(int totalScore) {
        if (totalScore == 0) {
            return "ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
        } else if (totalScore >= 1 && totalScore <= 8) {
            return "มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
        } else if (totalScore >= 9 && totalScore <= 16) {
            return "มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
        } else if (totalScore >= 17) {
            return "มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
        }
        return "";
    }
    /**
     * ตรวจสอบและแสดงการเตือนหากมีความเสี่ยงสูง
     */
    public void checkHighRiskAlert() {
        if (suicideAssessment8qInfo != null) {
            int totalScore = calculateTotalScore();

            // แจ้งเตือนหากมีความเสี่ยงสูง
            if (totalScore >= 17) {
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("⚠️ ความเสี่ยงสูง")
                            .setMessage("ผลการประเมินแสดงว่าผู้รับบริการมีความเสี่ยงสูงต่อการฆ่าตัวตาย\n" +
                                    "กรุณาดำเนินการตามแนวทางการให้คำปรึกษาและส่งต่อผู้เชี่ยวชาญ")
                            .setPositiveButton("รับทราบ", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            } else if (totalScore >= 9) {
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("⚠️ ความเสี่ยงปานกลาง")
                            .setMessage("ผู้รับบริการมีความเสี่ยงปานกลางต่อการฆ่าตัวตาย\n" +
                                    "ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด")
                            .setPositiveButton("รับทราบ", null)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            }
        }
    }
    /**
     * แสดงคำแนะนำแบบละเอียดสำหรับผู้ให้บริการ
     */
    public void showDetailedAdvice() {
        if (suicideAssessment8qInfo == null) return;

        int totalScore = calculateTotalScore();
        String advice = getHighRiskQuestionAdvice();
        String resultDescription = getResultDescription(totalScore);

        StringBuilder fullAdvice = new StringBuilder();
        fullAdvice.append("ผลการประเมิน: ").append(resultDescription).append("\n\n");

        if (!advice.isEmpty()) {
            fullAdvice.append(advice).append("\n");
        }

        // เพิ่มแนวทางการจัดการ
        fullAdvice.append("แนวทางการจัดการ:\n");
        if (totalScore >= 17) {
            fullAdvice.append("• ส่งต่อจิตแพทย์หรือนักจิตวิทยาทันที\n");
            fullAdvice.append("• ประเมินความปลอดภัยของสภาพแวดล้อม\n");
            fullAdvice.append("• แจ้งญาติใกล้ชิดเพื่อช่วยดูแล\n");
            fullAdvice.append("• กำหนดการนัดติดตามในระยะสั้น\n");
        } else if (totalScore >= 9) {
            fullAdvice.append("• ให้คำปรึกษาและการสนับสนุน\n");
            fullAdvice.append("• กำหนดการติดตามสม่ำเสมอ\n");
            fullAdvice.append("• ประเมินปัจจัยเสี่ยงอื่นๆ\n");
        } else if (totalScore >= 1) {
            fullAdvice.append("• ให้ความรู้เรื่องการดูแลสุขภาพจิต\n");
            fullAdvice.append("• สร้างเครือข่ายสนับสนุน\n");
        }

        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("คำแนะนำสำหรับผู้ให้บริการ")
                    .setMessage(fullAdvice.toString())
                    .setPositiveButton("รับทราบ", null)
                    .setNegativeButton("พิมพ์รายงาน", (dialog, which) -> {
                        // สามารถเพิ่มฟังก์ชันพิมพ์รายงานได้ที่นี่
                        Toast.makeText(getContext(), "ฟังก์ชันพิมพ์รายงานจะเพิ่มในเวอร์ชันถัดไป",
                                Toast.LENGTH_SHORT).show();
                    })
                    .show();
        }
    }
    public void resetForm() {
        if (suicideAssessment8qInfo != null) {
            suicideAssessment8qInfo.setQ1("0");
            suicideAssessment8qInfo.setQ2("0");
            suicideAssessment8qInfo.setQ3("0");
            suicideAssessment8qInfo.setQ3_2_1("0");
            suicideAssessment8qInfo.setQ4("0");
            suicideAssessment8qInfo.setQ5("0");
            suicideAssessment8qInfo.setQ6("0");
            suicideAssessment8qInfo.setQ7("0");
            suicideAssessment8qInfo.setQ8("0");
        }

        // รีเซ็ตสถานะการตอบ
        for (int i = 0; i < questionAnswered.length; i++) {
            questionAnswered[i] = false;
        }

        isFormValid = false;

        // รีเซ็ต RadioButtons
        if (getView() != null) {
            clearAllRadioGroups();

            // รีเซ็ตการแสดงผลพร้อม emoji
            updateScoreDisplay();
            if (tv8qResultDetail != null) {
                tv8qResultDetail.setText("🤔 ยังไม่ได้ประเมิน");
                tv8qResultDetail.setTextColor(Color.parseColor("#7F8C8D"));
                tv8qResultDetail.setBackgroundColor(Color.parseColor("#F8F9FA"));
            }
        }
        resetGauge();
    }
    public void updateGaugeWithScore(int score) {
        if (suicideRiskGauge != null) {
            suicideRiskGauge.setScore(score);
            updateGaugeDisplay();
        }
    }
    public SuicideRiskGaugeView.RiskLevel getCurrentGaugeLevel() {
        if (suicideRiskGauge != null) {
            return suicideRiskGauge.getCurrentRiskLevel();
        }
        return getCurrentRiskLevelFromScore(0);
    }
    public boolean hasDataChanged() {
        if (suicideAssessment8qInfo == null) {
            return false;
        }

        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (String answer : answers) {
            if (answer != null && !answer.equals("0") && !answer.isEmpty()) {
                return true;
            }
        }

        // ตรวจสอบคำถามย่อย
        if (!suicideAssessment8qInfo.getQ3_2_1().equals("0") &&
                !suicideAssessment8qInfo.getQ3_2_1().isEmpty()) {
            return true;
        }

        return false;
    }
    public boolean hasRiskAnswers() {
        if (suicideAssessment8qInfo == null) {
            return false;
        }

        String[] answers = {
                suicideAssessment8qInfo.getQ1(),
                suicideAssessment8qInfo.getQ2(),
                suicideAssessment8qInfo.getQ3(),
                suicideAssessment8qInfo.getQ4(),
                suicideAssessment8qInfo.getQ5(),
                suicideAssessment8qInfo.getQ6(),
                suicideAssessment8qInfo.getQ7(),
                suicideAssessment8qInfo.getQ8()
        };

        for (String answer : answers) {
            if ("2".equals(answer)) { // ตอบ "มี"
                return true;
            }
        }

        // ตรวจสอบคำถามย่อย
        if ("2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
            return true;
        }

        return false;
    }
    public List<String> getRiskAnswers() {
        List<String> riskAnswers = new ArrayList<>();

        if (suicideAssessment8qInfo == null) {
            return riskAnswers;
        }

        if ("2".equals(suicideAssessment8qInfo.getQ1())) {
            riskAnswers.add("ข้อ 1: คิดอยากตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ2())) {
            riskAnswers.add("ข้อ 2: อยากทำร้ายตัวเอง");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ3())) {
            riskAnswers.add("ข้อ 3: คิดเกี่ยวกับการฆ่าตัวตาย");
            if ("2".equals(suicideAssessment8qInfo.getQ3_2_1())) {
                riskAnswers.add("ข้อ 3.1: ไม่สามารถควบคุมความคิดได้");
            }
        }
        if ("2".equals(suicideAssessment8qInfo.getQ4())) {
            riskAnswers.add("ข้อ 4: มีแผนการฆ่าตัวตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ5())) {
            riskAnswers.add("ข้อ 5: มีการเตรียมการฆ่าตัวตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ6())) {
            riskAnswers.add("ข้อ 6: เคยทำร้ายตัวเองโดยไม่ตั้งใจให้ตาย");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ7())) {
            riskAnswers.add("ข้อ 7: เคยพยายามฆ่าตัวตายอย่างจริงจัง");
        }
        if ("2".equals(suicideAssessment8qInfo.getQ8())) {
            riskAnswers.add("ข้อ 8: เคยพยายามฆ่าตัวตาย");
        }

        return riskAnswers;
    }
    public String getAssessmentSummaryWithEmoji() {
        if (!isFormComplete()) {
            return "🤔 การประเมินยังไม่สมบูรณ์";
        }

        int score = calculateTotalScore();
        String emoji = get8qEmoji(score);
        String resultDescription = getResultDescriptionWithEmoji(score);
        String criticalEmoji = get8qCriticalEmoji(score >= 17);

        StringBuilder summary = new StringBuilder();
        summary.append("📊 สรุปการประเมิน 8Q:\n");
        summary.append("คะแนนรวม: ").append(score).append(" คะแนน\n");
        summary.append("ผลการประเมิน: ").append(resultDescription).append("\n");

        if (score >= 17) {
            summary.append(criticalEmoji).append(" สถานะ: วิกฤติ - ต้องการการแทรกแซงทันที!\n");
        } else if (score >= 9) {
            summary.append("⚠️ สถานะ: ต้องติดตามอย่างใกล้ชิด\n");
        } else if (score >= 1) {
            summary.append("ℹ️ สถานะ: ควรให้การสนับสนุน\n");
        } else {
            summary.append("✅ สถานะ: ปกติ\n");
        }

        // เพิ่มรายการคำตอบที่เป็นความเสี่ยง
        List<String> riskAnswers = getRiskAnswers();
        if (!riskAnswers.isEmpty()) {
            summary.append("\n🚨 พบสัญญาณเตือน:\n");
            for (String risk : riskAnswers) {
                summary.append("• ").append(risk).append("\n");
            }
        }

        return summary.toString();
    }

    public boolean isHighRisk() {
        if (!isFormComplete()) {
            return false;
        }

        return calculateTotalScore() >= 17;
    }
    private void clearAllRadioGroups() {
        if (getView() == null) return;

        RadioGroup[] radioGroups = {
                getView().findViewById(R.id.rdoSuicideQ1),
                getView().findViewById(R.id.rdoSuicideQ2),
                getView().findViewById(R.id.rdoSuicideQ3),
                getView().findViewById(R.id.rdoSuicideQ3_2_1),
                getView().findViewById(R.id.rdoSuicideQ4),
                getView().findViewById(R.id.rdoSuicideQ5),
                getView().findViewById(R.id.rdoSuicideQ6),
                getView().findViewById(R.id.rdoSuicideQ7),
                getView().findViewById(R.id.rdoSuicideQ8)
        };

        for (RadioGroup rg : radioGroups) {
            if (rg != null) {
                rg.clearCheck();
            }
        }
    }
    public SuicideAssessmentSummary getAssessmentSummary() {
        if (suicideAssessment8qInfo == null) {
            return new SuicideAssessmentSummary();
        }

        int totalScore = calculateTotalScore();
        String resultCode = getResultCode(totalScore);
        String resultDescription = getResultDescription(totalScore);
        boolean isComplete = isFormComplete();
        String advice = getHighRiskQuestionAdvice();

        return new SuicideAssessmentSummary(
                totalScore,
                resultCode,
                resultDescription,
                isComplete,
                advice,
                suicideAssessment8qInfo
        );
    }
    public boolean saveToScreeningResultCode(int personId, int visitno, String userCreate) {
        try {
            if (!isFormComplete()) {
                Log.e("SuicideAssessment8q", "ไม่สามารถบันทึกได้ เนื่องจากข้อมูลไม่ครบถ้วน");
                return false;
            }

            if (screeningResultCodeDao == null) {
                Log.e("SuicideAssessment8q", "screeningResultCodeDao is null");
                return false;
            }

            // คำนวณคะแนนรวม
            int totalScore = calculateTotalScore();

            // กำหนดรหัสผลการประเมิน
            String resultCode = getResultCode(totalScore);

            // กำหนดคำอธิบายผลการประเมิน
            String resultDescription = getResultDescription(totalScore);

            // กำหนดสถานะ (ปกติ/ผิดปกติ)
            String status = (totalScore == 0) ? "ปกติ" : "ผิดปกติ";

            // กำหนดระดับความรุนแรง
            String severity = getSuicideRiskSeverityLevel();

            // สร้างข้อมูลเพิ่มเติม (JSON format)
            String additionalData = createAdditionalDataJson();

            // สร้าง object สำหรับบันทึก (คล้าย 2Q)
            ScreeningResultCodeDao.ScreeningResultData data = new ScreeningResultCodeDao.ScreeningResultData();
            data.personId = personId;
            data.visitno = visitno;
            data.screeningType = ScreeningResultCode.TYPE_SUICIDE_ASSESSMENT_8Q;
            data.totalScore = totalScore;
            data.resultCode = resultCode;
            data.resultDescription = resultDescription;
            data.status = status;
            data.severityLevel = severity;
            data.additionalInfo = additionalData;
            data.userCreate = userCreate;
            data.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // บันทึกลงฐานข้อมูลแบบเดียวกับ 2Q
            android.net.Uri result = screeningResultCodeDao.saveScreeningResult(data);

            if (result != null) {
                Log.d("SuicideAssessment8q", "บันทึกผลการประเมิน 8Q สำเร็จ - " +
                        "PersonId: " + personId + ", VisitNo: " + visitno +
                        ", Score: " + totalScore + ", Result: " + resultDescription +
                        ", URI: " + result.toString());

                // บันทึก log พิเศษสำหรับความเสี่ยงสูง
                if (totalScore >= 17) {
                    Log.w("SuicideAssessment8q", "⚠️ บันทึกผลการประเมิน 8Q - พบความเสี่ยงสูงมาก! " +
                            "PersonId: " + personId + ", Score: " + totalScore);
                }

                return true;
            } else {
                Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการบันทึกผลการประเมิน 8Q - ได้ null URI");
                return false;
            }

        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "Exception ในการบันทึกผลการประเมิน 8Q: " + e.getMessage());
            return false;
        }
    }

    /**
     * สร้างข้อมูลเพิ่มเติมในรูปแบบ JSON
     */
    private String createAdditionalDataJson() {
        try {
            JSONObject additionalData = new JSONObject();

            // ข้อมูลพื้นฐาน
            additionalData.put("assessment_type", "suicide_assessment_8q");
            additionalData.put("version", "1.0");
            additionalData.put("timestamp", System.currentTimeMillis());
            additionalData.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

            // ข้อมูลคำตอบ
            JSONObject answers = new JSONObject();
            answers.put("q1", suicideAssessment8qInfo.getQ1());
            answers.put("q2", suicideAssessment8qInfo.getQ2());
            answers.put("q3", suicideAssessment8qInfo.getQ3());
            answers.put("q3_2_1", suicideAssessment8qInfo.getQ3_2_1());
            answers.put("q4", suicideAssessment8qInfo.getQ4());
            answers.put("q5", suicideAssessment8qInfo.getQ5());
            answers.put("q6", suicideAssessment8qInfo.getQ6());
            answers.put("q7", suicideAssessment8qInfo.getQ7());
            answers.put("q8", suicideAssessment8qInfo.getQ8());
            additionalData.put("answers", answers);

            // ข้อมูลการวิเคราะห์
            JSONObject analysis = new JSONObject();
            analysis.put("total_score", calculateTotalScore());
            analysis.put("is_complete", isFormComplete());
            analysis.put("completion_percentage", getCompletionPercentage());
            analysis.put("has_risk_answers", hasRiskAnswers());
            analysis.put("is_critical_risk", isCriticalRisk());
            analysis.put("requires_follow_up", requiresFollowUp());
            analysis.put("follow_up_type", getFollowUpType());
            additionalData.put("analysis", analysis);

            // รายการคำตอบที่เป็นความเสี่ยง
            List<String> riskAnswers = getRiskAnswers();
            if (!riskAnswers.isEmpty()) {
                JSONArray riskArray = new JSONArray();
                for (String risk : riskAnswers) {
                    riskArray.put(risk);
                }
                additionalData.put("risk_answers", riskArray);
            }

            // คำแนะนำ
            additionalData.put("recommendation", getRecommendation8Q());

            return additionalData.toString();

        } catch (JSONException e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการสร้าง JSON: " + e.getMessage());
            return "{}";
        }
    }

    /**
     * แปลงและโหลดข้อมูลเพิ่มเติมจาก JSON
     */
    private void parseAndLoadAdditionalData(String additionalDataJson) {
        try {
            JSONObject additionalData = new JSONObject(additionalDataJson);

            if (additionalData.has("answers")) {
                JSONObject answers = additionalData.getJSONObject("answers");

                // โหลดคำตอบกลับมา (ถ้าต้องการ)
                if (answers.has("q1")) suicideAssessment8qInfo.setQ1(answers.getString("q1"));
                if (answers.has("q2")) suicideAssessment8qInfo.setQ2(answers.getString("q2"));
                if (answers.has("q3")) suicideAssessment8qInfo.setQ3(answers.getString("q3"));
                if (answers.has("q3_2_1")) suicideAssessment8qInfo.setQ3_2_1(answers.getString("q3_2_1"));
                if (answers.has("q4")) suicideAssessment8qInfo.setQ4(answers.getString("q4"));
                if (answers.has("q5")) suicideAssessment8qInfo.setQ5(answers.getString("q5"));
                if (answers.has("q6")) suicideAssessment8qInfo.setQ6(answers.getString("q6"));
                if (answers.has("q7")) suicideAssessment8qInfo.setQ7(answers.getString("q7"));
                if (answers.has("q8")) suicideAssessment8qInfo.setQ8(answers.getString("q8"));

                // โหลดข้อมูลเก่าแล้ว อัปเดตการแสดงผล
                loadExistingData();
                updateScoreAndHighlight();
            }

            Log.d("SuicideAssessment8q", "โหลดข้อมูลเพิ่มเติมจาก JSON สำเร็จ");

        } catch (JSONException e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการแปลง JSON: " + e.getMessage());
        }
    }
    public void loadFromScreeningResultCode(int personId, int visitno) {
        try {
            if (screeningResultCodeDao == null) {
                Log.e("SuicideAssessment8q", "screeningResultCodeDao is null - ไม่สามารถโหลดข้อมูลได้");
                return;
            }

            ScreeningResultCodeDao.ScreeningResultData existingData =
                    screeningResultCodeDao.getResultByTypePersonAndVisit(
                            personId, visitno, ScreeningResultCode.TYPE_SUICIDE_ASSESSMENT_8Q);

            if (existingData != null) {
                Log.d("SuicideAssessment8q", "พบข้อมูลการประเมิน 8Q เดิม: คะแนน=" + existingData.totalScore +
                        ", ผลการประเมิน=" + existingData.resultDescription);

                // แสดงข้อความแจ้งผู้ใช้
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "โหลดข้อมูลการประเมิน 8Q เดิม: " + existingData.resultDescription,
                            Toast.LENGTH_SHORT).show();
                }

                // โหลดข้อมูลรายละเอียดจาก additionalData ถ้ามี
                if (existingData.additionalInfo != null && !existingData.additionalInfo.isEmpty()) {
                    parseAndLoadAdditionalData(existingData.additionalInfo);
                }

            } else {
                Log.d("SuicideAssessment8q", "ไม่พบข้อมูลการประเมิน 8Q เดิม");
            }

        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการโหลดข้อมูลการประเมิน 8Q: " + e.getMessage());
        }
    }

    /**
     * ตรวจสอบว่าควรดำเนินการบันทึกข้อมูลอัตโนมัติหรือไม่
     */
    public boolean shouldAutoSave() {
        return isFormComplete() && hasDataChanged();
    }

    /**
     * บันทึกข้อมูลอัตโนมัติเมื่อแบบฟอร์มครบถ้วน
     */
    public void autoSaveIfComplete() {
        if (shouldAutoSave() && currentPersonId > 0 && currentVisitNo > 0) {
            try {
                // ดึง userCreate จาก session
                android.content.Context context = getContext();
                if (context != null) {
                    th.in.ffc.session.UserSessionManager sessionManager =
                            new th.in.ffc.session.UserSessionManager(context);
                    String userCreate = sessionManager.getUser();

                    if (userCreate != null && !userCreate.isEmpty()) {
                        boolean saveSuccess = saveToScreeningResultCode(currentPersonId, currentVisitNo, userCreate);

                        if (saveSuccess) {
                            Log.d("SuicideAssessment8q", "บันทึกอัตโนมัติสำเร็จ");
                        } else {
                            Log.w("SuicideAssessment8q", "บันทึกอัตโนมัติไม่สำเร็จ");
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการบันทึกอัตโนมัติ: " + e.getMessage());
            }
        }
    }

    /**
     * ดึงสถิติการใช้งานแบบฟอร์ม
     */
    public String getUsageStatistics() {
        try {
            ScreeningResultCodeDao.ScreeningStatistics stats = getStatistics();
            if (stats != null) {
                return String.format(Locale.getDefault(),
                        "📊 สถิติการประเมิน 8Q:\n" +
                                "• ทั้งหมด: %d ครั้ง\n" +
                                "• ปกติ: %d ครั้ง (%.1f%%)\n" +
                                "• ผิดปกติ: %d ครั้ง (%.1f%%)\n" +
                                "• คะแนนเฉลี่ย: %.1f\n" +
                                "• คะแนนสูงสุด: %d\n" +
                                "• คะแนนต่ำสุด: %d",
                        stats.totalCount,
                        stats.normalCount, (stats.totalCount > 0 ? (stats.normalCount * 100.0f / stats.totalCount) : 0),
                        stats.abnormalCount, (stats.totalCount > 0 ? (stats.abnormalCount * 100.0f / stats.totalCount) : 0),
                        stats.averageScore,
                        stats.maxScore,
                        stats.minScore
                );
            }
        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการดึงสถิติ: " + e.getMessage());
        }

        return "ไม่สามารถดึงสถิติได้";
    }

    /**
     * ตรวจสอบและแจ้งเตือนเมื่อมีความเสี่ยงวิกฤต
     */
    public void checkAndAlertCriticalRisk() {
        if (isCriticalRisk()) {
            String criticalMessage = getCriticalRiskMessage();

            // บันทึก log เตือน
            Log.w("SuicideAssessment8q", "🚨 ตรวจพบความเสี่ยงวิกฤต! คะแนน: " + calculateTotalScore());

            // แสดงการเตือนให้ผู้ใช้
            if (getContext() != null) {
                Toast.makeText(getContext(),
                        "🆘 ความเสี่ยงวิกฤต! กรุณาดำเนินการทันที",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * รีเซ็ตและล้างข้อมูลการประเมิน
     */
    public void clearAssessmentData() {
        try {
            // รีเซ็ตข้อมูลใน object
            resetForm();

            // ล้างข้อมูลจากฐานข้อมูลท้องถิ่น (ถ้าต้องการ)
            if (currentPersonId > 0 && currentVisitNo > 0) {
                // สามารถเพิ่มการลบข้อมูลจาก local database ได้ที่นี่
                Log.d("SuicideAssessment8q", "ล้างข้อมูลการประเมิน 8Q สำหรับ PersonId: " +
                        currentPersonId + ", VisitNo: " + currentVisitNo);
            }

            // อัปเดตการแสดงผล
            updateScoreAndHighlight();

            Log.d("SuicideAssessment8q", "ล้างข้อมูลการประเมิน 8Q เรียบร้อย");

        } catch (Exception e) {
            Log.e("SuicideAssessment8q", "เกิดข้อผิดพลาดในการล้างข้อมูล: " + e.getMessage());
        }
    }

    /**
     * ตรวจสอบว่าผู้ใช้ควรได้รับการส่งต่อหรือไม่
     */
    public boolean shouldRefer() {
        return isFormComplete() && calculateTotalScore() >= 9; // ความเสี่ยงปานกลางขึ้นไป
    }

    /**
     * ดึงข้อมูลสำหรับการส่งต่อ
     */
    public String getReferralInfo() {
        if (!shouldRefer()) {
            return "ไม่จำเป็นต้องส่งต่อ";
        }

        int totalScore = calculateTotalScore();
        StringBuilder info = new StringBuilder();

        info.append("📋 ข้อมูลสำหรับการส่งต่อ:\n\n");
        info.append("• คะแนนรวม: ").append(totalScore).append(" คะแนน\n");
        info.append("• ระดับความเสี่ยง: ").append(getResultDescription(totalScore)).append("\n");
        info.append("• ประเภทการติดตาม: ").append(getFollowUpType()).append("\n\n");

        List<String> riskAnswers = getRiskAnswers();
        if (!riskAnswers.isEmpty()) {
            info.append("🚨 สัญญาณเตือน:\n");
            for (String risk : riskAnswers) {
                info.append("• ").append(risk).append("\n");
            }
            info.append("\n");
        }

        info.append("💡 คำแนะนำ: ").append(getRecommendation8Q());

        return info.toString();
    }

}