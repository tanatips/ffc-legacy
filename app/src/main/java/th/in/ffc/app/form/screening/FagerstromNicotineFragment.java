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
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    private TableLayout tbFagerstrome;
    private int currentHighlightedRow = -1;

    private int white;
    private int light_gray;
    private int highlightColor;


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
        tbFagerstrome = view.findViewById(R.id.tbFagerstrome);
        nicotineInfo = new NicotineInfo();
        rdoNicotineQ1 = view.findViewById(R.id.rdoNicotineQ1);
        rdoNicotineQ2 = view.findViewById(R.id.rdoNicotineQ2);
        rdoNicotineQ3 = view.findViewById(R.id.rdoNicotineQ3);
        rdoNicotineQ4 = view.findViewById(R.id.rdoNicotineQ4);
        rdoNicotineQ5 = view.findViewById(R.id.rdoNicotineQ5);
        rdoNicotineQ6 = view.findViewById(R.id.rdoNicotineQ6);
        rdoNicotineQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
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
//                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
                calculatePoints();
            }
        });
        loadData();
    }

    private void highlightScore(int score) {
        // ล้าง highlight เดิม (ถ้ามี)
        if (currentHighlightedRow != -1) {
            TableRow previousRow = (TableRow) tbFagerstrome.getChildAt(currentHighlightedRow);
            if (previousRow != null) {
                previousRow.setBackgroundColor(getRowDefaultColor(currentHighlightedRow));
            }
        }

        // หาแถวที่ต้อง highlight
        int rowToHighlight;
        if (score >= 0 && score <= 3) {
            rowToHighlight = 1;  // แถวแรกหลังหัวตาราง
        } else if (score >= 4 && score <= 5) {
            rowToHighlight = 2;
        } else if (score >= 6 && score <= 7) {
            rowToHighlight = 3;
        } else if (score >= 8 && score <= 9) {
            rowToHighlight = 4;
        } else if (score == 10) {
            rowToHighlight = 5;
        } else {
            return; // คะแนนไม่อยู่ในช่วงที่กำหนด
        }

        // ทำการ highlight แถวที่ตรงกับช่วงคะแนน
        TableRow rowToChange = (TableRow) tbFagerstrome.getChildAt(rowToHighlight);
        if (rowToChange != null) {
//            rowToChange.setBackgroundColor(getHighlightColor(score));
            rowToChange.setBackgroundColor(highlightColor);
            currentHighlightedRow = rowToHighlight;
        }
        TextView resultTextView = requireView().findViewById(R.id.resultFagerStromScore);
        if (resultTextView != null) {
            resultTextView.setText(String.format("คะแนนที่ได้: %d คะแนน", score));
        }

    }

    private int getRowDefaultColor(int rowIndex) {
        if (rowIndex == 0) { // หัวตาราง
            return getResources().getColor(R.color.purple_500);
        } else if (rowIndex % 2 == 0) { // แถวคู่
            return Color.parseColor("#F5F5F5");
        } else { // แถวคี่
            return Color.WHITE;
        }
    }

    /**
     * ฟังก์ชันกำหนดสี highlight ตามระดับคะแนน
     */
    private int getHighlightColor(int score) {
        if (score >= 0 && score <= 3) {
            return Color.parseColor("#E8F5E9"); // สีเขียวอ่อน
        } else if (score >= 4 && score <= 5) {
            return Color.parseColor("#FFF3E0"); // สีส้มอ่อน
        } else if (score >= 6 && score <= 7) {
            return Color.parseColor("#FFE0B2"); // สีส้ม
        } else if (score >= 8 && score <= 9) {
            return Color.parseColor("#FFCCBC"); // สีส้มแดง
        } else {
            return Color.parseColor("#FFCDD2"); // สีแดงอ่อน
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
        if (score >= 0 && score <= 3) {
            return "ไม่มีบ่วงคุดติดสารนิโคติน";
        } else if (score >= 4 && score <= 5) {
            return "คุณติดสารนิโคตินในระดับปานกลาง";
        } else if (score >= 6 && score <= 7) {
            return "คุณติดสารนิโคตินในระดับปานกลางและมีแนวโน้มอย่างมากในการพัฒนาไปเป็นการติดนิโคตินระดับสูง";
        } else if (score >= 8 && score <= 9) {
            return "คุณติดสารนิโคตินในระดับสูง";
        } else if (score == 10) {
            return "คุณติดสารนิโคตินในระดับสูงมาก";
        } else {
            return "คะแนนไม่อยู่ในช่วงที่กำหนด";
        }
    }

    private void loadData() {
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(getContext());
//        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        shareViewModel.getCigatetteAddictionTestMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if (data.getPersonId() != null) {
                List<NicotineInfo> nicotineInfos = sfNicotineInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for (NicotineInfo nicotineInfo : nicotineInfos) {
                    Log.d("smoker", "smoker infos:" + nicotineInfo);
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
//        updateScore(sum);
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

        // รีเซ็ตการแสดงผล
        TextView resultTextView = getView() != null ? getView().findViewById(R.id.resultFagerStromScore) : null;
        if (resultTextView != null) {
            resultTextView.setText("คะแนนที่ได้: - คะแนน");
        }

        // ล้าง highlight
        if (currentHighlightedRow != -1 && tbFagerstrome != null) {
            TableRow previousRow = (TableRow) tbFagerstrome.getChildAt(currentHighlightedRow);
            if (previousRow != null) {
                previousRow.setBackgroundColor(getRowDefaultColor(currentHighlightedRow));
            }
            currentHighlightedRow = -1;
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
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%)";
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
}