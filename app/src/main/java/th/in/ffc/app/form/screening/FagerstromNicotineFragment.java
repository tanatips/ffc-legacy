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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;
import th.in.ffc.app.form.screening.view.NicotineRiskGaugeView;
import android.widget.SeekBar;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FagerstromNicotineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FagerstromNicotineFragment extends Fragment {

    CigaretteAddictionTestLiveData cigaretteAddictionTest;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    NicotineInfo nicotineInfo;

    ArrayList<Integer> points;

    private RadioGroup rdoNicotineQ1;
    private RadioGroup rdoNicotineQ2;
    private RadioGroup rdoNicotineQ3;
    private RadioGroup rdoNicotineQ4;
    private RadioGroup rdoNicotineQ5;
    private RadioGroup rdoNicotineQ6;
    private TextView resultInterpretation;
    private int currentHighlightedRow = -1;

    // เพิ่มตัวแปรสำหรับแสดงผลแบบใหม่
    private TextView tvNicotineScore;
    private TextView tvNicotineAddictionLevel;
    private boolean isUpdatingFromCode = false;

    // เพิ่มตัวแปรสำหรับปุ่ม info
    private ImageView ivNicotineInfoButton;

    private int white;
    private int light_gray;
    private int highlightColor;

    private NicotineRiskGaugeView nicotineRiskGauge;
    private TextView tvGaugeEmoji;
    private TextView tvGaugeScore;
    private TextView tvGaugeLevel;
    private TextView tvGaugeCode;
    private TextView tvGaugeRecommendation;
    private SeekBar seekBarGaugeTest;

    public FagerstromNicotineFragment() {

    }

    public static FagerstromNicotineFragment newInstance(String param1, String param2) {
        FagerstromNicotineFragment fragment = new FagerstromNicotineFragment();
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
        cigaretteAddictionTest = new CigaretteAddictionTestLiveData();
        shareViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fagerstrom_nicotine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        white = ContextCompat.getColor(requireContext(), R.color.white);
        light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);

        View parentViewPager = (View) view.getParent();
        if (parentViewPager != null) {
            parentViewPager.post(() -> {
                int height = view.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = parentViewPager.getLayoutParams();
                layoutParams.height = height;
                parentViewPager.setLayoutParams(layoutParams);
            });
        }

        points = new ArrayList<>();
        points.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));
        resultInterpretation = view.findViewById(R.id.resultInterpretation);

        // เชื่อมโยง TextView สำหรับแสดงผลแบบใหม่
        tvNicotineScore = view.findViewById(R.id.tvNicotineScore);
        tvNicotineAddictionLevel = view.findViewById(R.id.tvNicotineAddictionLevel);

        // เชื่อมโยงปุ่ม info
        ivNicotineInfoButton = view.findViewById(R.id.ivNicotineInfoButton);

        nicotineInfo = new NicotineInfo();
        rdoNicotineQ1 = view.findViewById(R.id.rdoNicotineQ1);
        rdoNicotineQ2 = view.findViewById(R.id.rdoNicotineQ2);
        rdoNicotineQ3 = view.findViewById(R.id.rdoNicotineQ3);
        rdoNicotineQ4 = view.findViewById(R.id.rdoNicotineQ4);
        rdoNicotineQ5 = view.findViewById(R.id.rdoNicotineQ5);
        rdoNicotineQ6 = view.findViewById(R.id.rdoNicotineQ6);

        setupListeners();
        initializeGaugeViews(view);
        // เพิ่มการ observe คะแนนจาก SharedViewModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // สังเกตการเปลี่ยนแปลงข้อมูลจาก ViewModel
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                // ดึงข้อมูลคะแนนการติดนิโคติน
                loadNicotineScore(personInfo.getId());
            }
        });

        // สังเกตคะแนน nicotine จาก AssistScore
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data.getPersonId() != null && data.getNicotineScore() != null) {
                try {
                    int nicotineScore = Integer.parseInt(data.getNicotineScore());
                    updateNicotineScore(nicotineScore);
                } catch (NumberFormatException e) {
                    Log.e("FagerstromNicotineFragment", "ไม่สามารถแปลงคะแนน nicotine เป็นตัวเลขได้: " + data.getNicotineScore());
                    updateNicotineScore(0); // ใช้ค่าเริ่มต้นเป็น 0
                }
            }
        });

        loadData();
    }

    private void setupListeners() {
        // เพิ่ม listener สำหรับปุ่ม info
        ivNicotineInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNicotineCriteriaDialog();
            }
        });

        rdoNicotineQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ1(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if (R.id.rdoNicotineQ1_1 == i) {
                    data = "1";
                    points.set(0, 0);
                } else if (R.id.rdoNicotineQ1_2 == i) {
                    data = "2";
                    points.set(0, 1);
                } else if (R.id.rdoNicotineQ1_3 == i) {
                    data = "3";
                    points.set(0, 2);
                } else if (R.id.rdoNicotineQ1_4 == i) {
                    data = "4";
                    points.set(0, 3);
                }
                nicotineInfo.setNicotine1(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });

        rdoNicotineQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ2(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if (R.id.rdoNicotineQ2_1 == i) {
                    data = "1";
                    points.set(1, 3);
                } else if (R.id.rdoNicotineQ2_2 == i) {
                    data = "2";
                    points.set(1, 2);
                } else if (R.id.rdoNicotineQ2_3 == i) {
                    data = "3";
                    points.set(1, 1);
                } else if (R.id.rdoNicotineQ2_4 == i) {
                    data = "4";
                    points.set(1, 0);
                }
                nicotineInfo.setNicotine2(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });

        rdoNicotineQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ3(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if (R.id.rdoNicotineQ3_1 == i) {
                    data = "1";
                    points.set(2, 1);
                } else if (R.id.rdoNicotineQ3_2 == i) {
                    data = "2";
                    points.set(2, 0);
                }
                nicotineInfo.setNicotine3(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });

        rdoNicotineQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ4(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);

                String data = "";
                if (R.id.rdoNicotineQ4_1 == i) {
                    data = "1";
                    points.set(3, 1);
                } else if (R.id.rdoNicotineQ4_2 == i) {
                    data = "2";
                    points.set(3, 0);
                }
                nicotineInfo.setNicotine4(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });

        rdoNicotineQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ5(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if (R.id.rdoNicotineQ5_1 == i) {
                    data = "1";
                    points.set(4, 1);
                } else if (R.id.rdoNicotineQ5_2 == i) {
                    data = "2";
                    points.set(4, 0);
                }
                nicotineInfo.setNicotine5(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });

        rdoNicotineQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (isUpdatingFromCode) return;

                cigaretteAddictionTest.setSelectedRdoQ6(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if (R.id.rdoNicotineQ6_1 == i) {
                    data = "1";
                    points.set(5, 1);
                } else if (R.id.rdoNicotineQ6_2 == i) {
                    data = "2";
                    points.set(5, 0);
                }
                cigaretteAddictionTest.setPoints(points);

                nicotineInfo.setNicotine6(data);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });
    }
    private void initializeGaugeViews(View view) {
        nicotineRiskGauge = view.findViewById(R.id.nicotineRiskGauge);
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

    /**
     * Setup gauge test controls (for debugging)
     */
    private void setupGaugeTestControls() {
        if (seekBarGaugeTest != null) {
            seekBarGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && nicotineRiskGauge != null) {
                        nicotineRiskGauge.setScore(progress);
                        NicotineRiskGaugeView.AddictionLevel level = getCurrentAddictionLevelFromScore(progress);

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
    private String getNicotineCode() {
        if (nicotineInfo == null) {
            return "1B500"; // default code
        }

        // ดึง code จากข้อ 1 (จำนวนบุหรี่ต่อวัน)
        String q1Code = "";
        switch (nicotineInfo.getNicotine1()) {
            case "1":
                q1Code = "1B501"; // 10 มวนหรือน้อยกว่า
                break;
            case "2":
                q1Code = "1B502"; // 11-20 มวน
                break;
            case "3":
                q1Code = "1B503"; // 21-30 มวน
                break;
            case "4":
                q1Code = "1B503"; // มากกว่า 31 มวน (ใช้ code เดียวกับ 21-30)
                break;
            default:
                q1Code = "1B500"; // default
        }

        // ดึง code จากข้อ 2 (เวลาที่สูบบุหรี่มวนแรก)
        String q2Code = "";
        switch (nicotineInfo.getNicotine2()) {
            case "1":
                q2Code = "1B504"; // ภายใน 5 นาที
                break;
            case "2":
                q2Code = "1B504"; // 6-30 นาที (ใช้ code เดียวกับ ภายใน 5 นาที)
                break;
            case "3":
                q2Code = "1B505"; // 31-60 นาที
                break;
            case "4":
                q2Code = "1B506"; // มากกว่า 60 นาที
                break;
            default:
                q2Code = "1B500"; // default
        }
        String code = "";
        // ถ้าตอบข้อ 1 แล้ว ให้ใช้ code จากข้อ 1
        if (!nicotineInfo.getNicotine1().equals("0")) {
            code = q1Code;
        }
        // ถ้าตอบข้อ 2 แล้ว ให้ใช้ code จากข้อ 2
        if (!nicotineInfo.getNicotine2().equals("0")) {
            code +=","+q2Code;
        }
        return code;
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
        if (nicotineRiskGauge != null) {
            nicotineRiskGauge.setScore(0);
            updateGaugeDisplay();
        }
    }

    /**
     * Update gauge display with current score
     */
    private void updateGaugeDisplay() {
        if (nicotineRiskGauge == null) return;

        int totalScore = getCurrentNicotineScore();
        NicotineRiskGaugeView.AddictionLevel currentLevel = getCurrentAddictionLevelFromScore(totalScore);

        // อัปเดต Gauge
        nicotineRiskGauge.setScore(totalScore);

        // อัปเดตข้อความ
        if (tvGaugeEmoji != null) tvGaugeEmoji.setText(currentLevel.emoji);
        if (tvGaugeScore != null) tvGaugeScore.setText("คะแนน: " + totalScore);
        if (tvGaugeLevel != null) {
            tvGaugeLevel.setText(currentLevel.label);
            tvGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvGaugeCode != null) tvGaugeCode.setText(getNicotineCode()); // ใช้ code จากคำตอบจริง

        Log.d("FagerstromNicotineFragment", "Gauge updated - Score: " + totalScore + ", Level: " + currentLevel.label + ", Code: " + getNicotineCode());
    }
    private int getCurrentNicotineScore() {
        if (nicotineInfo != null && nicotineInfo.getSum() != null) {
            return nicotineInfo.getSum();
        }
        return 0;
    }

    /**
     * Get addiction level from score for nicotine assessment
     */
    private NicotineRiskGaugeView.AddictionLevel getCurrentAddictionLevelFromScore(int fagerstromScore) {
        String currentCode = getNicotineCode(); // ใช้ code จากคำตอบจริง

        if (fagerstromScore >= 0 && fagerstromScore <= 3) {
            return new NicotineRiskGaugeView.AddictionLevel(0, 3, "ไม่นับว่าคุณติดสารนิโคติน", "#27AE60", "😊", currentCode);
        } else if (fagerstromScore >= 4 && fagerstromScore <= 5) {
            return new NicotineRiskGaugeView.AddictionLevel(4, 5, "คุณติดสารนิโคตินในระดับปานกลาง", "#F1C40F", "🙂", currentCode);
        } else if (fagerstromScore >= 6 && fagerstromScore <= 7) {
            return new NicotineRiskGaugeView.AddictionLevel(6, 7, "คุณติดสารนิโคตินในระดับปานกลางและมีแนวโน้มอย่างมากในการพัฒนาไปเป็นการติดนิโคตินระดับสูง", "#FF9800", "😟", currentCode);
        } else if (fagerstromScore >= 8 && fagerstromScore <= 9) {
            return new NicotineRiskGaugeView.AddictionLevel(8, 9, "คุณติดสารนิโคตินในระดับสูง", "#E74C3C", "😰", currentCode);
        } else {
            return new NicotineRiskGaugeView.AddictionLevel(10, 10, "คุณติดสารนิโคตินในระดับสูงมาก", "#C0392B", "😱", currentCode);
        }
    }

    /**
     * Update gauge with specific score
     */
    public void updateGaugeWithScore(int score) {
        if (nicotineRiskGauge != null) {
            nicotineRiskGauge.setScore(score);
            updateGaugeDisplay();
        }
    }

    /**
     * Get current gauge level
     */
    public NicotineRiskGaugeView.AddictionLevel getCurrentGaugeLevel() {
        if (nicotineRiskGauge != null) {
            return nicotineRiskGauge.getCurrentAddictionLevel();
        }
        return getCurrentAddictionLevelFromScore(0);
    }

    /**
     * แสดง Dialog เกณฑ์การประเมินการติดนิโคติน
     */
    private void showNicotineCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // สร้าง custom layout สำหรับ dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nicotine_criteria, null);

        builder.setView(dialogView);
//        builder.setTitle("เกณฑ์การประเมินการติดนิโคติน");
        builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * โหลดคะแนนการติดนิโคติน
     */
    private void loadNicotineScore(String personInfoId) {
        try {
            // ดึงข้อมูลจาก database หรือคำนวณจากข้อมูลปัจจุบัน
            if (nicotineInfo != null && nicotineInfo.getSum() != null) {
                updateNicotineScore(nicotineInfo.getSum());
            }
            Log.d("FagerstromNicotineFragment", "โหลดคะแนนการติดนิโคตินสำเร็จ");
        } catch (Exception e) {
            Log.e("FagerstromNicotineFragment", "เกิดข้อผิดพลาดในการโหลดคะแนน: " + e.getMessage());
        }
    }

    /**
     * อัปเดตการแสดงคะแนนและระดับการติดนิโคติน
     */
    private void updateNicotineScore(int score) {
        if (tvNicotineScore != null) {
            tvNicotineScore.setText(String.valueOf(score));

            // เปลี่ยนสี background และ text color ของ tvNicotineScore ตามระดับคะแนน
            if (score >= 0 && score <= 3) {
                // ไม่มีการติด - สีเขียว
                tvNicotineScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvNicotineScore.setTextColor(Color.WHITE);
            } else if (score >= 4 && score <= 5) {
                // การติดระดับปานกลาง - สีเหลือง
                tvNicotineScore.setBackground(createGradientDrawable("#F1C40F", "#F39C12"));
                tvNicotineScore.setTextColor(Color.WHITE);
            } else if (score >= 6 && score <= 7) {
                // การติดระดับปานกลาง-สูง - สีส้ม
                tvNicotineScore.setBackground(createGradientDrawable("#FF9800", "#FF6B35"));
                tvNicotineScore.setTextColor(Color.WHITE);
            } else if (score >= 8 && score <= 9) {
                // การติดระดับสูง - สีแดง
                tvNicotineScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvNicotineScore.setTextColor(Color.WHITE);
            } else if (score >= 10) {
                // การติดระดับสูงมาก - สีแดงเข้ม
                tvNicotineScore.setBackground(createGradientDrawable("#C0392B", "#A93226"));
                tvNicotineScore.setTextColor(Color.WHITE);
            }
        }

        if (tvNicotineAddictionLevel != null) {
            String emoji = getNicotineEmoji(score); // เพิ่ม emoji
            String addictionLevel;

            // กำหนดระดับการติดนิโคตินตามคะแนน พร้อม emoji
            if (score >= 0 && score <= 3) {
                addictionLevel = emoji + " ไม่นับว่าคุณติดสารนิโคติน";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_green);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 4 && score <= 5) {
                addictionLevel = emoji + " คุณติดสารนิโคตินในระดับปานกลาง";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_yellow);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 6 && score <= 7) {
                addictionLevel = emoji + " คุณติดสารนิโคตินในระดับปานกลางและมีแนวโน้มอย่างมากในการพัฒนาไปเป็นการติดนิโคตินระดับสูง";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_orange);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.dark_orange));
            } else if (score >= 8 && score <= 9) {
                addictionLevel = emoji + " คุณติดสารนิโคตินในระดับสูง";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_red);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else if (score >= 10) {
                addictionLevel = emoji + " คุณติดสารนิโคตินในระดับสูงมาก";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_dark_red);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.dark_dark_red));
            } else {
                addictionLevel = "😐 ยังไม่ได้ประเมิน";
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_gray);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvNicotineAddictionLevel.setText(addictionLevel);
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

    // รักษาเมธอดเก่าไว้สำหรับ backward compatibility
    private void highlightScore(int score) {
        // อัพเดตคะแนนและการแปลผล
        TextView resultTextView = requireView().findViewById(R.id.resultFagerStromScore);
        if (resultTextView != null) {
            resultTextView.setText(String.format("คะแนนที่ได้: %d คะแนน", score));
        }

        // แสดงการแปลผลใต้คะแนน
        updateInterpretation(score);

        // อัปเดตการแสดงผลแบบใหม่
        updateNicotineScore(score);
        updateGaugeDisplay();
    }

    private void updateInterpretation(int score) {
        if (resultInterpretation != null) {
            String interpretation = getScoreInterpretation(score);
            int backgroundColor = getInterpretationBackgroundColor(score);

            resultInterpretation.setText(interpretation);
            resultInterpretation.setBackgroundColor(backgroundColor);
            resultInterpretation.setVisibility(View.VISIBLE);
        }
    }

    private int getInterpretationBackgroundColor(int score) {
        if (score >= 0 && score <= 3) {
            return Color.parseColor("#27AE60"); // เขียว
        } else if (score >= 4 && score <= 5) {
            return Color.parseColor("#F1C40F"); // เหลืองอ่อน
        } else if (score >= 6 && score <= 7) {
            return Color.parseColor("#F39C12"); // เหลืองแก่
        } else if (score >= 8 && score <= 9) {
            return Color.parseColor("#E67E22"); // แดงอ่อน
        } else if (score == 10) {
            return Color.parseColor("#C0392B"); // แดงเข้ม
        } else {
            return Color.parseColor("#95A5A6"); // เทา (default)
        }
    }

    private void updateScore(int newScore) {
        highlightScore(newScore);

        // อัพเดตข้อความแสดงผลเพิ่มเติม (ถ้ามี)
        String interpretation = getScoreInterpretation(newScore);
        // TODO: แสดงข้อความตีความผลคะแนนในส่วนอื่นๆ ของ UI
    }

    /**
     * ฟังก์ชันสำหรับรับข้อความแปลผลคะแนน
     */
    private String getScoreInterpretation(int score) {
        String emoji = getNicotineEmoji(score);

        if (score >= 0 && score <= 3) {
            return emoji + " ไม่นับว่าคุณติดสารนิโคติน";
        } else if (score >= 4 && score <= 5) {
            return emoji + " คุณติดสารนิโคตินในระดับปานกลาง";
        } else if (score >= 6 && score <= 7) {
            return emoji + " คุณติดสารนิโคตินในระดับปานกลางและมีแนวโน้มอย่างมากในการพัฒนาไปเป็นการติดนิโคตินระดับสูง";
        } else if (score >= 8 && score <= 9) {
            return emoji + " คุณติดสารนิโคตินในระดับสูง";
        } else if (score == 10) {
            return emoji + " คุณติดสารนิโคตินในระดับสูงมาก";
        } else {
            return "😐 คะแนนไม่อยู่ในช่วงที่กำหนด";
        }
    }
    private void showHighRiskNicotineNotification() {
        if (isHighRiskNicotine()) {
            try {
                int score = nicotineInfo.getSum();
                String emoji = getNicotineEmoji(score);
                String riskLevel = getScoreInterpretation(score);

                String message = String.format(
                        "%s ตรวจพบการติดนิโคตินระดับสูง\n\n" +
                                "คะแนน: %d คะแนน\n" +
                                "ระดับ: %s\n\n" +
                                "จำเป็นต้องได้รับการดูแลเป็นพิเศษ",
                        emoji, score, riskLevel.replace(emoji + " ", "")
                );

                Log.w("FagerstromNicotineFragment", message);

                // แสดง Toast แจ้งเตือน
                if (getContext() != null) {
                    Toast.makeText(getContext(), emoji + " ตรวจพบการติดนิโคตินระดับสูง",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("FagerstromNicotineFragment", "เกิดข้อผิดพลาดในการแสดงการแจ้งเตือน: " + e.getMessage());
            }
        }
    }
    public String getNicotineRecommendation() {
        if (nicotineInfo == null || nicotineInfo.getSum() == null) {
            return "กรุณากรอกข้อมูลให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int score = nicotineInfo.getSum();
        String emoji = getNicotineEmoji(score);

        if (score >= 0 && score <= 3) {
            return emoji + " แนะนำ: คงสภาพปัจจุบัน หลีกเลี่ยงการสูบบุหรี่";
        } else if (score >= 4 && score <= 5) {
            return emoji + " แนะนำ: พิจารณาการให้คำปรึกษาเบื้องต้น และการสร้างแรงจูงใจในการเลิกบุหรี่";
        } else if (score >= 6 && score <= 7) {
            return emoji + " แนะนำ: ต้องการการให้คำปรึกษาเชิงลึก และการติดตามอย่างใกล้ชิด";
        } else if (score >= 8 && score <= 9) {
            return emoji + " แนะนำ: ต้องการการบำบัดเข้มข้น รวมถึงการใช้ยาช่วยเลิกบุหรี่";
        } else if (score >= 10) {
            return emoji + " แนะนำ: ต้องการการบำบัดเข้มข้นอย่างเร่งด่วน ปรึกษาแพทย์ผู้เชี่ยวชาญ";
        } else {
            return "😐 ไม่สามารถให้คำแนะนำได้";
        }
    }
    public String generateNicotineAssessmentReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== รายงานการประเมินการติดนิโคติน (Fagerstrom) ===\n\n");

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
        int score = nicotineInfo.getSum();
        String emoji = getNicotineEmoji(score);
        report.append("ผลการประเมิน:\n");
        report.append("- คะแนนรวม: ").append(score).append(" คะแนน\n");
        report.append("- ระดับการติด: ").append(getScoreInterpretation(score)).append("\n");
        report.append("- ความเสี่ยงสูง: ").append(isHighRiskNicotine() ? "ใช่" : "ไม่").append("\n\n");

        // คำแนะนำ
        report.append("คำแนะนำ:\n");
        report.append(getNicotineRecommendation()).append("\n\n");

        // ข้อมูลเพิ่มเติม
        if (isHighRiskNicotine()) {
            report.append("⚠️ การดำเนินการเร่งด่วน:\n");
            report.append("1. ปรึกษาแพทย์หรือผู้เชี่ยวชาญด้านการเลิกบุหรี่\n");
            report.append("2. พิจารณาใช้ยาช่วยเลิกบุหรี่\n");
            report.append("3. เข้าร่วมโปรแกรมการเลิกบุหรี่\n");
            report.append("4. หลีกเลี่ยงสถานการณ์เสี่ยง\n");
            report.append("5. ติดตามอาการขาดนิโคตินอย่างใกล้ชิด\n\n");
        }

        report.append("=== สิ้นสุดรายงาน ===");

        return report.toString();
    }
    public void checkHighRiskNicotineAlert() {
        if (isFormComplete() && isHighRiskNicotine()) {
            showHighRiskNicotineNotification();
        }
    }
    public boolean isHighRiskNicotine() {
        if (nicotineInfo == null || nicotineInfo.getSum() == null) {
            return false;
        }
        return nicotineInfo.getSum() >= 8; // คะแนน 8 ขึ้นไปถือว่าเสี่ยงสูง
    }
    public String getNicotineRiskLevelWithEmoji() {
        if (nicotineInfo == null || nicotineInfo.getSum() == null) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        int score = nicotineInfo.getSum();
        return getScoreInterpretation(score);
    }

    private void loadData() {
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(getContext());
        shareViewModel.getCigatetteAddictionTestMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if (data.getPersonId() != null) {
                List<NicotineInfo> nicotineInfos = sfNicotineInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for (NicotineInfo nicotineInfo : nicotineInfos) {
                    Log.d("nicotine", "nicotine infos:" + nicotineInfo);
                    setNicotineInfo(nicotineInfo);

                    if (nicotineInfo.getSum() != null) {
                        AssistScore assistScore = new AssistScore();
                        assistScore.setNicotineScore(nicotineInfo.getSum().toString());
                        assistScore.setPersonId(nicotineInfo.getPersonId());
                        shareViewModel.setAssistScoreMutableLiveData(assistScore);
                    }
                    dataPasser.onNicotineInfo(nicotineInfo);
                }
            }
        });
    }

    public void setNicotineInfo(NicotineInfo info) {
        this.nicotineInfo = info;
        updateUI();
    }

    private void updateUI() {
        if (nicotineInfo == null) return;

        // ตั้งค่าป้องกัน loop
        isUpdatingFromCode = true;

        try {
            // Set Question 1
            if (!nicotineInfo.getNicotine1().equals("0")) {
                int radioId = getResources().getIdentifier(
                        "rdoNicotineQ1_" + nicotineInfo.getNicotine1(),
                        "id", requireContext().getPackageName());
                if (radioId != 0) {
                    rdoNicotineQ1.check(radioId);
                }
            }

            // Set Question 2
            if (!nicotineInfo.getNicotine2().equals("0")) {
                int radioId = getResources().getIdentifier(
                        "rdoNicotineQ2_" + nicotineInfo.getNicotine2(),
                        "id", requireContext().getPackageName());
                if (radioId != 0) {
                    rdoNicotineQ2.check(radioId);
                }
            }

            // Set Question 3-6
            setRadioGroupValue(rdoNicotineQ3, nicotineInfo.getNicotine3(), "rdoNicotineQ3_");
            setRadioGroupValue(rdoNicotineQ4, nicotineInfo.getNicotine4(), "rdoNicotineQ4_");
            setRadioGroupValue(rdoNicotineQ5, nicotineInfo.getNicotine5(), "rdoNicotineQ5_");
            setRadioGroupValue(rdoNicotineQ6, nicotineInfo.getNicotine6(), "rdoNicotineQ6_");
        } finally {
            // ปิดการป้องกัน loop
            isUpdatingFromCode = false;
        }

        calculatePoints();
    }

    private void setRadioGroupValue(RadioGroup group, String value, String idPrefix) {
        if (!value.equals("0")) {
            int radioId = getResources().getIdentifier(
                    idPrefix + value,
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                group.check(radioId);
            }
        }
    }

    public NicotineInfo getNicotineInfo() {
        return nicotineInfo;
    }

    private void calculatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // Question 1: How many cigarettes per day?
        switch (nicotineInfo.getNicotine1()) {
            case "1":
                points.add(0);
                break; // 10 or less
            case "2":
                points.add(1);
                break; // 11-20
            case "3":
                points.add(2);
                break; // 21-30
            case "4":
                points.add(3);
                break; // 31 or more
            default:
                points.add(0);
        }

        // Question 2: Time to first cigarette
        switch (nicotineInfo.getNicotine2()) {
            case "1":
                points.add(3);
                break; // Within 5 minutes
            case "2":
                points.add(2);
                break; // 6-30 minutes
            case "3":
                points.add(1);
                break; // 31-60 minutes
            case "4":
                points.add(0);
                break; // After 60 minutes
            default:
                points.add(0);
        }

        // Question 3: Smoke more in morning?
        points.add(nicotineInfo.getNicotine3().equals("1") ? 1 : 0);

        // Question 4: Which cigarette would you hate to give up?
        points.add(nicotineInfo.getNicotine4().equals("1") ? 1 : 0);

        // Question 5: Find it difficult to refrain?
        points.add(nicotineInfo.getNicotine5().equals("1") ? 1 : 0);

        // Question 6: Smoke when ill?
        points.add(nicotineInfo.getNicotine6().equals("1") ? 1 : 0);

        nicotineInfo.setPoints(points);

        // Calculate total
        int sum = 0;
        for (Integer point : points) {
            sum += point;
        }
        nicotineInfo.setSum(sum);
        cigaretteAddictionTest.setScore(sum);
        highlightScore(sum);
        AssistScore assistScore = new AssistScore();
        assistScore.setNicotineScore(nicotineInfo.getSum().toString());
        assistScore.setPersonId(nicotineInfo.getPersonId());
        shareViewModel.setAssistScoreMutableLiveData(assistScore);
        updateGaugeDisplay();
    }

    public boolean isFormComplete() {
        if (nicotineInfo == null) {
            return false;
        }

        // ตรวจสอบว่าตอบครบทุกข้อหรือไม่
        return !nicotineInfo.getNicotine1().equals("0") &&
                !nicotineInfo.getNicotine2().equals("0") &&
                !nicotineInfo.getNicotine3().equals("0") &&
                !nicotineInfo.getNicotine4().equals("0") &&
                !nicotineInfo.getNicotine5().equals("0") &&
                !nicotineInfo.getNicotine6().equals("0");
    }

    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        if (nicotineInfo == null || nicotineInfo.getNicotine1().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 1");
            return message.toString();
        }

        if (nicotineInfo.getNicotine2().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 2");
            return message.toString();
        }

        if (nicotineInfo.getNicotine3().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 3");
            return message.toString();
        }

        if (nicotineInfo.getNicotine4().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 4");
            return message.toString();
        }

        if (nicotineInfo.getNicotine5().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 5");
            return message.toString();
        }

        if (nicotineInfo.getNicotine6().equals("0")) {
            message.append("แบบประเมินการติดนิโคติน: ยังไม่ได้ตอบข้อ 6");
            return message.toString();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    public String getDetailedValidationMessage() {
        if (nicotineInfo == null) {
            return "แบบประเมินการติดนิโคติน Fagerstrom:\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingQuestions = new ArrayList<>();

        if (nicotineInfo.getNicotine1().equals("0")) {
            missingQuestions.add("ข้อ 1: จำนวนบุหรี่ที่สูบต่อวัน");
        }

        if (nicotineInfo.getNicotine2().equals("0")) {
            missingQuestions.add("ข้อ 2: เวลาที่สูบบุหรี่มวนแรกหลังตื่นนอน");
        }

        if (nicotineInfo.getNicotine3().equals("0")) {
            missingQuestions.add("ข้อ 3: การสูบบุหรี่ในช่วงชั่วโมงแรกหลังตื่นนอน");
        }

        if (nicotineInfo.getNicotine4().equals("0")) {
            missingQuestions.add("ข้อ 4: บุหรี่มวนใดที่เลิกยากที่สุด");
        }

        if (nicotineInfo.getNicotine5().equals("0")) {
            missingQuestions.add("ข้อ 5: ความยากลำบากในการอดสูบบุหรี่");
        }

        if (nicotineInfo.getNicotine6().equals("0")) {
            missingQuestions.add("ข้อ 6: การสูบบุหรี่เมื่อป่วย");
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("แบบประเมินการติดนิโคติน Fagerstrom:\n");
            message.append("กรุณาตอบคำถามที่ยังไม่ได้ตอบ:\n");
            for (String question : missingQuestions) {
                message.append("• ").append(question).append("\n");
            }
            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    public void resetForm() {
        // ตั้งค่าป้องกัน loop
        isUpdatingFromCode = true;

        try {
            // ล้างการเลือกทั้งหมด
            if (rdoNicotineQ1 != null) rdoNicotineQ1.clearCheck();
            if (rdoNicotineQ2 != null) rdoNicotineQ2.clearCheck();
            if (rdoNicotineQ3 != null) rdoNicotineQ3.clearCheck();
            if (rdoNicotineQ4 != null) rdoNicotineQ4.clearCheck();
            if (rdoNicotineQ5 != null) rdoNicotineQ5.clearCheck();
            if (rdoNicotineQ6 != null) rdoNicotineQ6.clearCheck();

            // รีเซ็ต nicotineInfo
            nicotineInfo = new NicotineInfo();

            // รีเซ็ต points
            points = new ArrayList<>();
            points.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));

            // รีเซ็ตการแสดงผลแบบใหม่พร้อม emoji
            if (tvNicotineScore != null) {
                tvNicotineScore.setText("-");
                tvNicotineScore.setBackgroundResource(R.color.light_gray);
                tvNicotineScore.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            if (tvNicotineAddictionLevel != null) {
                tvNicotineAddictionLevel.setText("😐 ยังไม่ได้ประเมิน");
                tvNicotineAddictionLevel.setBackgroundResource(R.color.light_gray);
                tvNicotineAddictionLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            // รีเซ็ตการแสดงผลเก่า
            TextView resultTextView = getView() != null ? getView().findViewById(R.id.resultFagerStromScore) : null;
            if (resultTextView != null) {
                resultTextView.setText("คะแนนที่ได้: - คะแนน");
            }

            // ซ่อนการแปลผล
            if (resultInterpretation != null) {
                resultInterpretation.setVisibility(View.GONE);
            }

            currentHighlightedRow = -1;
        } finally {
            // ปิดการป้องกัน loop
            isUpdatingFromCode = false;
        }
    }

    public boolean hasDataChanged() {
        if (nicotineInfo == null) {
            return false;
        }

        return !nicotineInfo.getNicotine1().equals("0") ||
                !nicotineInfo.getNicotine2().equals("0") ||
                !nicotineInfo.getNicotine3().equals("0") ||
                !nicotineInfo.getNicotine4().equals("0") ||
                !nicotineInfo.getNicotine5().equals("0") ||
                !nicotineInfo.getNicotine6().equals("0");
    }

    public int getCompletionPercentage() {
        if (nicotineInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 6;

        if (!nicotineInfo.getNicotine1().equals("0")) completedQuestions++;
        if (!nicotineInfo.getNicotine2().equals("0")) completedQuestions++;
        if (!nicotineInfo.getNicotine3().equals("0")) completedQuestions++;
        if (!nicotineInfo.getNicotine4().equals("0")) completedQuestions++;
        if (!nicotineInfo.getNicotine5().equals("0")) completedQuestions++;
        if (!nicotineInfo.getNicotine6().equals("0")) completedQuestions++;

        return (completedQuestions * 100) / totalQuestions;
    }

    public void showCompletionStatus() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            String riskInfo = getNicotineRiskLevelWithEmoji();
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%) - " + riskInfo;

            // ตรวจสอบความเสี่ยงสูงและแจ้งเตือน
            if (isHighRiskNicotine()) {
                checkHighRiskNicotineAlert();
            }
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("FagerstromNicotineFragment", "Completion Status: " + message);
        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public List<Integer> getUnansweredQuestions() {
        List<Integer> unanswered = new ArrayList<>();

        if (nicotineInfo == null) {
            for (int i = 1; i <= 6; i++) {
                unanswered.add(i);
            }
            return unanswered;
        }

        if (nicotineInfo.getNicotine1().equals("0")) unanswered.add(1);
        if (nicotineInfo.getNicotine2().equals("0")) unanswered.add(2);
        if (nicotineInfo.getNicotine3().equals("0")) unanswered.add(3);
        if (nicotineInfo.getNicotine4().equals("0")) unanswered.add(4);
        if (nicotineInfo.getNicotine5().equals("0")) unanswered.add(5);
        if (nicotineInfo.getNicotine6().equals("0")) unanswered.add(6);

        return unanswered;
    }

    private String getQuestionDescription(int questionNumber) {
        switch (questionNumber) {
            case 1:
                return "จำนวนบุหรี่ที่สูบต่อวัน";
            case 2:
                return "เวลาที่สูบบุหรี่มวนแรกหลังตื่นนอน";
            case 3:
                return "การสูบบุหรี่ในช่วงชั่วโมงแรกหลังตื่นนอน";
            case 4:
                return "บุหรี่มวนใดที่เลิกยากที่สุด";
            case 5:
                return "ความยากลำบากในการอดสูบบุหรี่";
            case 6:
                return "การสูบบุหรี่เมื่อป่วย";
            default:
                return "คำถามที่ " + questionNumber;
        }
    }

    public void scrollToFirstUnansweredQuestion() {
        List<Integer> unanswered = getUnansweredQuestions();
        if (!unanswered.isEmpty()) {
            int firstUnanswered = unanswered.get(0);
            RadioGroup targetGroup = null;

            switch (firstUnanswered) {
                case 1:
                    targetGroup = rdoNicotineQ1;
                    break;
                case 2:
                    targetGroup = rdoNicotineQ2;
                    break;
                case 3:
                    targetGroup = rdoNicotineQ3;
                    break;
                case 4:
                    targetGroup = rdoNicotineQ4;
                    break;
                case 5:
                    targetGroup = rdoNicotineQ5;
                    break;
                case 6:
                    targetGroup = rdoNicotineQ6;
                    break;
            }

            if (targetGroup != null) {
                targetGroup.requestFocus();
                // สามารถเพิ่มการ scroll ไปยัง view ได้ที่นี่
            }
        }
    }
    private String getNicotineEmoji(int score) {
        if (score >= 0 && score <= 3) {
            return "😊"; // ไม่ติด - หน้ายิ้ม
        } else if (score >= 4 && score <= 5) {
            return "🙂"; // ติดระดับปานกลาง - หน้ายิ้มเบาๆ
        } else if (score >= 6 && score <= 7) {
            return "😟"; // ติดระดับปานกลาง-สูง - หน้ากังวล
        } else if (score >= 8 && score <= 9) {
            return "😰"; // ติดระดับสูง - หน้าตกใจ/กังวลมาก
        } else if (score >= 10) {
            return "😱"; // ติดระดับสูงมาก - หน้าตกใจมาก
        } else {
            return "😐"; // ยังไม่ได้ประเมิน - หน้าเฉยๆ
        }
    }

}