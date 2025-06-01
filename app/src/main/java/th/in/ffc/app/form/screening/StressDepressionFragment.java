package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

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
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.util.Log;

public class StressDepressionFragment extends Fragment {

    StressDepressionLiveData stressDepressionLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepressionInfo stressDepressionInfo;
    ArrayList<Integer> points;

    private RadioGroup rdoObesityQ1;
    private RadioGroup rdoObesityQ2;
    private RadioGroup rdoObesityQ3;
    private RadioGroup rdoObesityQ4;
    private RadioGroup rdoObesityQ5;
    private int currentScore = -1;
    private TableLayout tableLayout;

    private static final String COLOR_HIGHLIGHT_1 = "#C8E6C9"; // Light green for low stress
    private static final String COLOR_HIGHLIGHT_2 = "#FFCC80"; // Light orange for medium stress
    private static final String COLOR_HIGHLIGHT_3 = "#EF9A9A"; // Light red for high stress
    private static final String COLOR_HIGHLIGHT_4 = "#E57373"; // Darker red for highest stress
    private static final String COLOR_DEFAULT = "#FFFFFF"; // White background

    int white;
    int light_gray;
    int highlightColor;

    // ตัวแปรสำหรับตรวจสอบข้อมูล
    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false, false, false, false}; // ตรวจสอบว่าตอบคำถามครบหรือไม่

    public StressDepressionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stress_depression, container, false);
    }

    private void initializeTable(View view) {
        tableLayout = view.findViewById(R.id.stressScoreTable);

        // Add header row
        TableRow headerRow = new TableRow(getContext());
        headerRow.addView(createTextView("คะแนน", true));
        headerRow.addView(createTextView("ความเครียด", true));
        headerRow.addView(createTextView("รหัส", true));
        tableLayout.addView(headerRow);

        // Add data rows
        addTableRow(tableLayout, "0 - 4", "เครียดน้อย","1B132",0);
        addTableRow(tableLayout, "5 - 7", "เครียดปานกลาง","1B133",1);
        addTableRow(tableLayout, "8 - 9", "เครียดมาก","1B134",2);
        addTableRow(tableLayout, "10 - 15", "เครียดมากที่สุด","1B135",3);

        white = ContextCompat.getColor(requireContext(), R.color.white);
        light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
    }

    // ปรับปรุง addTableRow method เพื่อใช้สีที่สอดคล้องกัน
    private void addTableRow(TableLayout table, String score, String description, String code, int position) {
        TableRow row = new TableRow(getContext());

        // Create score column
        TextView scoreView = createTextView(score, false);
        scoreView.setPadding(16, 24, 16, 24);

        // Create description column
        TextView descView = createTextView(description, false);
        descView.setPadding(16, 24, 16, 24);

        TextView codeView = createTextView(code, false);
        codeView.setPadding(16, 24, 16, 24);

        // Set background color based on stress level (not alternating)
        String backgroundColor = getBackgroundColorForPosition(position);
        row.setBackgroundColor(Color.parseColor(backgroundColor));

        row.addView(scoreView);
        row.addView(descView);
        row.addView(codeView);
        table.addView(row);
    }
    // เพิ่ม method สำหรับกำหนดสีพื้นหลังของแต่ละแถว
    private String getBackgroundColorForPosition(int position) {
        switch (position) {
            case 0: return "#E8F5E8"; // เขียวอ่อน - เครียดน้อย
            case 1: return "#FFFDE7"; // เหลืองอ่อน - เครียดปานกลาง
            case 2: return "#FFEBEE"; // แดงอ่อน - เครียดมาก
            case 3: return "#FFCDD2"; // แดงเข้ม - เครียดมากที่สุด
            default: return "#FFFFFF"; // สีขาว (default)
        }
    }
    private TextView createTextView(String text, boolean isHeader) {
        TextView textView = new TextView(getContext());
        textView.setText(text);

        if (isHeader) {
            textView.setBackgroundColor(Color.parseColor("#9C27B0")); // Purple header
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(18);
            textView.setPadding(16, 24, 16, 24);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            textView.setTextSize(14);
            textView.setTextColor(Color.BLACK);
        }

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 1); // Small margin between rows
        textView.setLayoutParams(params);

        return textView;
    }

    public static StressDepressionFragment newInstance(String param1, String param2) {
        StressDepressionFragment fragment = new StressDepressionFragment();
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
        stressDepressionLiveData = new StressDepressionLiveData();
        shareViewModel = new SharedViewModel();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0,0,0,0,0,0));
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
        rdoObesityQ1 = view.findViewById(R.id.rdoObesityQ1);
        rdoObesityQ2 = view.findViewById(R.id.rdoObesityQ2);
        rdoObesityQ3 = view.findViewById(R.id.rdoObesityQ3);
        rdoObesityQ4 = view.findViewById(R.id.rdoObesityQ4);
        rdoObesityQ5 = view.findViewById(R.id.rdoObesityQ5);
        stressDepressionInfo = new StressDepressionInfo();

        setupRadioGroupListeners();
        loadData();
        initializeTable(view);
    }

    /**
     * ตั้งค่า Listener สำหรับ RadioGroup ทั้งหมด
     */
    private void setupRadioGroupListeners() {
        rdoObesityQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ1(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);

                String data = "";
                if(R.id.rdoObesityQ1_1 == i) {
                    data = "1";
                    points.set(0,0);
                }
                else if(R.id.rdoObesityQ1_2 == i) {
                    data = "2";
                    points.set(0,1);
                }
                else if(R.id.rdoObesityQ1_3 == i) {
                    data = "3";
                    points.set(0,2);
                }
                else if(R.id.rdoObesityQ1_4 == i) {
                    data = "4";
                    points.set(0,3);
                }
                stressDepressionInfo.setQ1(data);
                stressDepressionInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 1 แล้ว
                questionAnswered[0] = true;
                validateAndSaveData();
                calculatePoints();
            }
        });

        rdoObesityQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ2(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ2_1 == i) {
                    data = "1";
                    points.set(1,0);
                }
                else if(R.id.rdoObesityQ2_2 == i) {
                    data = "2";
                    points.set(1,1);
                }
                else if(R.id.rdoObesityQ2_3 == i) {
                    data = "3";
                    points.set(1,2);
                }
                else if(R.id.rdoObesityQ2_4 == i) {
                    data = "4";
                    points.set(1,3);
                }
                stressDepressionInfo.setQ2(data);
                stressDepressionInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 2 แล้ว
                questionAnswered[1] = true;
                validateAndSaveData();
                calculatePoints();
            }
        });

        rdoObesityQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ3(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ3_1 == i) {
                    data = "1";
                    points.set(2,0);
                }
                else if(R.id.rdoObesityQ3_2 == i) {
                    data = "2";
                    points.set(2,1);
                }
                else if(R.id.rdoObesityQ3_3 == i) {
                    data = "3";
                    points.set(2,2);
                }
                else if(R.id.rdoObesityQ3_4 == i) {
                    data = "4";
                    points.set(2,3);
                }
                stressDepressionInfo.setQ3(data);
                stressDepressionInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 3 แล้ว
                questionAnswered[2] = true;
                validateAndSaveData();
                calculatePoints();
            }
        });

        rdoObesityQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ4(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ4_1 == i) {
                    data = "1";
                    points.set(3,0);
                }
                else if(R.id.rdoObesityQ4_2 == i) {
                    data = "2";
                    points.set(3,1);
                }
                else if(R.id.rdoObesityQ4_3 == i) {
                    data = "3";
                    points.set(3,2);
                }
                else if(R.id.rdoObesityQ4_4 == i) {
                    data = "4";
                    points.set(3,3);
                }
                stressDepressionInfo.setQ4(data);
                stressDepressionInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 4 แล้ว
                questionAnswered[3] = true;
                validateAndSaveData();
                calculatePoints();
            }
        });

        rdoObesityQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ5(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ5_1 == i) {
                    data = "1";
                    points.set(4,0);
                }
                else if(R.id.rdoObesityQ5_2 == i) {
                    data = "2";
                    points.set(4,1);
                }
                else if(R.id.rdoObesityQ5_3 == i) {
                    data = "3";
                    points.set(4,2);
                }
                else if(R.id.rdoObesityQ5_4 == i) {
                    data = "4";
                    points.set(4,3);
                }
                stressDepressionInfo.setQ5(data);
                stressDepressionInfo.setPoints(points);

                // ตรวจสอบว่าตอบคำถามที่ 5 แล้ว
                questionAnswered[4] = true;
                validateAndSaveData();
                calculatePoints();
            }
        });
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
            Log.d("StressDepression", "ตอบคำถามครบทุกข้อแล้ว - บันทึกข้อมูล");
            dataPasser.onStressDepression(stressDepressionInfo);
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
     * ตรวจสอบสถานะการตอบจาก RadioGroup
     */
    private void checkAnsweredStatus() {
        questionAnswered[0] = rdoObesityQ1.getCheckedRadioButtonId() != -1;
        questionAnswered[1] = rdoObesityQ2.getCheckedRadioButtonId() != -1;
        questionAnswered[2] = rdoObesityQ3.getCheckedRadioButtonId() != -1;
        questionAnswered[3] = rdoObesityQ4.getCheckedRadioButtonId() != -1;
        questionAnswered[4] = rdoObesityQ5.getCheckedRadioButtonId() != -1;
    }

    public void setUserScore(int score) {
        currentScore = score;
        updateTableHighlight();
    }

    private void updateTableHighlight() {
        // First set alternating colors for all rows
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);

            // Set default alternating background colors
            if (i % 2 == 0) {
                row.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray
            } else {
                row.setBackgroundColor(Color.parseColor("#FFFFFF")); // White
            }
        }

        // Then highlight the row that matches the score range with appropriate color
        if (currentScore >= 0) {
            int rowIndex = getRowIndexForScore(currentScore);
            if (rowIndex > 0 && rowIndex < tableLayout.getChildCount()) {
                TableRow row = (TableRow) tableLayout.getChildAt(rowIndex);

                // Set highlight color based on stress level
                String highlightColor = getHighlightColorForScore(currentScore);
                row.setBackgroundColor(Color.parseColor(highlightColor));
            }
        }

        int totalScore = currentScore;
        String resultCode = getResultCode(totalScore);
        String stressLevel = getStressLevelText(totalScore);
        TextView resultTextView = getView().findViewById(R.id.resultStressDepressionScore);
        if (resultTextView != null) {
            resultTextView.setText(String.format("คะแนนที่ได้: %d คะแนน (%s - %s)",
                    totalScore, resultCode, stressLevel));
        }
    }
    // เพิ่ม method ใหม่สำหรับกำหนดสี highlight ตามระดับคะแนน
    private String getHighlightColorForScore(int score) {
        if (score >= 0 && score <= 4) {
            return "#C8E6C9"; // เขียวอ่อน - เครียดน้อย
        } else if (score >= 5 && score <= 7) {
            return "#FFF9C4"; // เหลืองอ่อน - เครียดปานกลาง
        } else if (score >= 8 && score <= 9) {
            return "#FFCDD2"; // แดงอ่อน - เครียดมาก
        } else if (score >= 10 && score <= 15) {
            return "#EF9A9A"; // แดงเข้ม - เครียดมากที่สุด
        }
        return "#FFFFFF"; // สีขาว (default)
    }
    // เพิ่ม method สำหรับดึงข้อความระดับความเครียด
    private String getStressLevelText(int score) {
        if (score >= 0 && score <= 4) return "เครียดน้อย";
        if (score >= 5 && score <= 7) return "เครียดปานกลาง";
        if (score >= 8 && score <= 9) return "เครียดมาก";
        if (score >= 10 && score <= 15) return "เครียดมากที่สุด";
        return "";
    }
    private String getHighlightColorForRow(int rowIndex) {
        switch (rowIndex) {
            case 1: return COLOR_HIGHLIGHT_1;
            case 2: return COLOR_HIGHLIGHT_2;
            case 3: return COLOR_HIGHLIGHT_3;
            case 4: return COLOR_HIGHLIGHT_4;
            default: return COLOR_DEFAULT;
        }
    }

    private int getRowIndexForScore(int score) {
        if (score >= 0 && score <= 4) return 1;
        if (score >= 5 && score <= 7) return 2;
        if (score >= 8 && score <= 9) return 3;
        if (score >= 10 && score <= 19) return 4;
        return -1;
    }

    private String getResultCode(int score) {
        if (score >= 0 && score <= 4) return "1B132";
        if (score >= 5 && score <= 7) return "1B133";
        if (score >= 8 && score <= 9) return "1B134";
        if (score >= 10 && score <= 19) return "1B135";
        return "";
    }

    private void loadData(){
        SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepressionLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if(data.getPersonId()!=null){
                List<StressDepressionInfo> stressDepressionInfos = sfStressDepressionInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepressionInfo stressDepressionInfo :stressDepressionInfos){
                    Log.d("Stress Depression", "Stress Depression info:"+stressDepressionInfo);
                    setStressDepressionInfo(stressDepressionInfo);
                }
            }
        });
    }

    public void setStressDepressionInfo(StressDepressionInfo info) {
        this.stressDepressionInfo = info;
        updateUI();
    }

    private void updateUI() {
        if (stressDepressionInfo == null) return;

        // Set answers from existing data
        setRadioGroupFromAnswer(rdoObesityQ1, stressDepressionInfo.getQ1(), "rdoObesityQ1_");
        setRadioGroupFromAnswer(rdoObesityQ2, stressDepressionInfo.getQ2(), "rdoObesityQ2_");
        setRadioGroupFromAnswer(rdoObesityQ3, stressDepressionInfo.getQ3(), "rdoObesityQ3_");
        setRadioGroupFromAnswer(rdoObesityQ4, stressDepressionInfo.getQ4(), "rdoObesityQ4_");
        setRadioGroupFromAnswer(rdoObesityQ5, stressDepressionInfo.getQ5(), "rdoObesityQ5_");

        // ตรวจสอบสถานะการตอบจากข้อมูลที่โหลดมา
        checkAnsweredStatus();

        calculatePoints();
    }

    private void setRadioGroupFromAnswer(RadioGroup group, String answer, String idPrefix) {
        if (!answer.equals("0")) {
            int radioId = getResources().getIdentifier(
                    idPrefix + answer,
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                group.check(radioId);
            }
        }
    }

    private void calculatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // แปลงค่าคำตอบเป็นคะแนน (0-3 คะแนน)
        points.add(getPointFromAnswer(stressDepressionInfo.getQ1()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ2()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ3()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ4()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ5()));

        stressDepressionInfo.setPoints(points);

        // คำนวณผลอัตโนมัติ (getSum() จะคำนวณ resultCode และ resultDescription ให้)
        stressDepressionInfo.getSum();
        setUserScore(stressDepressionInfo.getSum());
    }

    private int getPointFromAnswer(String answer) {
        // แปลงคำตอบเป็นคะแนน
        switch (answer) {
            case "1": return 0; // เป็นน้อยมากหรือแทบไม่มี
            case "2": return 1; // เป็นบางครั้ง
            case "3": return 2; // เป็นบ่อยครั้ง
            case "4": return 3; // เป็นประจำ
            default: return 0;
        }
    }

    /**
     * เมธอดสำหรับหน้าจออื่นที่ต้องการตรวจสอบความครบถ้วนของข้อมูล
     */
    public StressDepressionInfo getFormData() {
        if (isFormValid) {
            return stressDepressionInfo;
        } else {
            showIncompleteFormMessage();
            return null;
        }
    }
}