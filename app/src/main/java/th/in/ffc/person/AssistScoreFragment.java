package th.in.ffc.person;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;
import java.util.HashMap;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SharedViewModel;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssistScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssistScoreFragment extends Fragment {

    private static final String TAG = "AssistScoreFragment";

    private OnDataPass dataPasser;
    private Map<String, TextView> scoreTextViews = new HashMap<>();
    private String currentPersonId = null;
    private SharedViewModel viewModel;
    private ImageView btnInfo;

    public AssistScoreFragment() {
        // Required empty public constructor
    }

    public static AssistScoreFragment newInstance(AssistScore data) {
        AssistScoreFragment fragment = new AssistScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assist_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // กำหนด ID สำหรับ TextView ในแต่ละแถว
        setupScoreTextViews(view);

        // ตั้งค่าปุ่ม Info
        setupInfoButton(view);

        // สังเกตการเปลี่ยนแปลงข้อมูลจาก ViewModel
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                currentPersonId = personInfo.getId();
                // ดึงข้อมูลสรุปจาก SfDrugsDao
                loadDrugsSummary(personInfo.getId());
            }
        });

        // ยังคงดึงข้อมูล nicotine score จาก AssistScore (ตามโค้ดเดิม)
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                if (data.getNicotineScore() != null) {
                    // แสดงค่า nicotine score ใน TextView ของ row a (ยาสูบ)
                    if (scoreTextViews.containsKey("a")) {
                        try {
                            int nicotineScore = Integer.parseInt(data.getNicotineScore());
                            updateScoreDisplay("a", nicotineScore);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "ไม่สามารถแปลงคะแนน nicotine เป็นตัวเลขได้: " + data.getNicotineScore());
                        }
                    }
                }

                if (data.getAlcoholScore() != null) {
                    // แสดงค่า alcohol score ใน TextView ของ row b (แอลกอฮอล์)
                    if (scoreTextViews.containsKey("b")) {
                        try {
                            int alcoholScore = Integer.parseInt(data.getAlcoholScore());
                            updateScoreDisplay("b", alcoholScore);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "ไม่สามารถแปลงคะแนน alcohol เป็นตัวเลขได้: " + data.getAlcoholScore());
                        }
                    }
                }
            }
        });
    }

    /**
     * ตั้งค่าปุ่ม Info สำหรับแสดง Dialog เกณฑ์การประเมิน
     */
    private void setupInfoButton(View view) {
        btnInfo = view.findViewById(R.id.btnInfo);
        if (btnInfo != null) {
            btnInfo.setOnClickListener(v -> showCriteriaDialog());
        }
    }

    /**
     * แสดง Dialog เกณฑ์การประเมิน
     */
    private void showCriteriaDialog() {
        try {
            Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Inflate layout สำหรับ dialog
            View dialogView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_assessment_criteria, null);

            dialog.setContentView(dialogView);

            // ตั้งค่าขนาดของ dialog
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            }

            // จัดการปุ่มปิด dialog
            View btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);
            if (btnCloseDialog != null) {
                btnCloseDialog.setOnClickListener(v -> {
                    dialog.dismiss();
                    Log.d(TAG, "ปิด Dialog เกณฑ์การประเมิน");
                });
            }

            // ให้ dialog ปิดได้เมื่อแตะด้านนอก
            dialog.setCanceledOnTouchOutside(true);

            // แสดง dialog
            dialog.show();

            Log.d(TAG, "แสดง Dialog เกณฑ์การประเมินสำเร็จ");
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());
        }
    }

    /**
     * ตั้งค่า TextView สำหรับแสดงคะแนนแต่ละประเภทสารเสพติด
     */
    private void setupScoreTextViews(View view) {
        // ค้นหา TextView ที่อยู่ในคอลัมน์คะแนนของตาราง
        TextView tvScoreA = view.findViewById(R.id.tvScoreA);
        scoreTextViews.put("a", tvScoreA);

        // เพิ่ม ID ใน XML หรือค้นหาด้วยวิธีอื่น
        int[] scoreIds = new int[] {
                R.id.tvScoreB, R.id.tvScoreC, R.id.tvScoreD, R.id.tvScoreE,
                R.id.tvScoreF, R.id.tvScoreG, R.id.tvScoreH, R.id.tvScoreI,
                R.id.tvScoreJ
        };

        String[] substanceIds = new String[] {
                "b", "c", "d", "e", "f", "g", "h", "i", "j"
        };

        // สร้าง Map เพื่อเชื่อมโยง ID ของสารเสพติดกับ TextView
        for (int i = 0; i < scoreIds.length; i++) {
            TextView tv = view.findViewById(scoreIds[i]);
            if (tv != null) {
                scoreTextViews.put(substanceIds[i], tv);
            }
        }
    }

    /**
     * โหลดข้อมูลสรุปจาก SfDrugsDao และแสดงผลในตาราง
     */
    private void loadDrugsSummary(String personInfoId) {
        try {
            // คำนวณผลรวมของคำตอบจาก Q2 ถึง Q7
            String[] questions = { "Q2", "Q3", "Q4", "Q5", "Q6", "Q7" };

            // สร้าง Map เพื่อเก็บผลรวมของแต่ละสารเสพติด (a-j)
            Map<String, Integer> totalScores = new HashMap<>();

            // ดึงข้อมูลจากแต่ละคำถามและรวมคะแนน
            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                // นำคะแนนมารวมกัน
                for (Map.Entry<String, Integer> entry : summaryMap.entrySet()) {
                    String subquestion = entry.getKey();
                    Integer score = entry.getValue();

                    if (totalScores.containsKey(subquestion)) {
                        totalScores.put(subquestion, totalScores.get(subquestion) + score);
                    } else {
                        totalScores.put(subquestion, score);
                    }
                }
            }

            // แสดงผลรวมในตาราง
            for (Map.Entry<String, Integer> entry : totalScores.entrySet()) {
                String subquestion = entry.getKey();
                Integer totalScore = entry.getValue();

                updateScoreDisplay(subquestion, totalScore);
            }

            Log.d(TAG, "ดึงและแสดงข้อมูลสำเร็จ");
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูล: " + e.getMessage());
        }
    }

    /**
     * อัพเดตการแสดงคะแนนใน TextView
     */
    private void updateScoreDisplay(String substanceId, int score) {
        // ตรวจสอบว่ามี TextView สำหรับสารเสพติดชนิดนี้หรือไม่
        if (scoreTextViews.containsKey(substanceId)) {
            TextView tvScore = scoreTextViews.get(substanceId);
            if (tvScore != null) {
                // สร้างข้อความที่แสดงคะแนนพร้อมระดับการบำบัด
                String displayText = formatScoreWithTreatment(substanceId, score);
                tvScore.setText(displayText);

                // เพิ่มการเน้นสีพื้นหลังตามระดับคะแนน
                highlightScore(tvScore, substanceId, score);
            }
        }
    }

    /**
     * จัดรูปแบบการแสดงคะแนนพร้อม emoji และระดับการบำบัด
     */
    private String formatScoreWithTreatment(String substanceId, int score) {
        String treatmentLevel = getTreatmentLevel(substanceId, score);
        String emoji = getScoreEmoji(substanceId, score);

        if (score == 0) {
            return "0 " + emoji; // แสดง 0 พร้อม emoji
        }

        return score + " " + emoji + " " + treatmentLevel;
    }

    /**
     * ดึงระดับการบำบัดตามคะแนนและประเภทสารเสพติด
     */
    private String getTreatmentLevel(String substanceId, int score) {
        if (substanceId.equals("b")) { // แอลกอฮอล์
            if (score >= 0 && score <= 10) {
                return "ไม่ต้องบำบัด";
            } else if (score >= 11 && score <= 26) {
                return "ได้รับการบำบัดอย่างย่อ";
            } else if (score >= 27) {
                return "ได้รับการบำบัดรักษาเข้ม";
            }
        } else { // สารเสพติดอื่นๆ
            if (score >= 0 && score <= 3) {
                return "ไม่ต้องบำบัด";
            } else if (score >= 4 && score <= 26) {
                return "ได้รับการบำบัดอย่างย่อ";
            } else if (score >= 27) {
                return "ได้รับการบำบัดรักษาเข้ม";
            }
        }
        return "ไม่ทราบ";
    }

    /**
     * ดึง emoji ตามคะแนนและประเภทสารเสพติด
     */
    private String getScoreEmoji(String substanceId, int score) {
        if (substanceId.equals("b")) { // แอลกอฮอล์
            if (score >= 0 && score <= 10) {
                return "😊"; // ไม่ต้องบำบัด - หน้ายิ้ม
            } else if (score >= 11 && score <= 26) {
                return "😟"; // บำบัดอย่างย่อ - หน้ากังวล
            } else if (score >= 27) {
                return "😰"; // บำบัดรักษาเข้ม - หน้าตกใจ/กังวลมาก
            }
        } else { // สารเสพติดอื่นๆ
            if (score >= 0 && score <= 3) {
                return "😊"; // ไม่ต้องบำบัด - หน้ายิ้ม
            } else if (score >= 4 && score <= 26) {
                return "😟"; // บำบัดอย่างย่อ - หน้ากังวล
            } else if (score >= 27) {
                return "😰"; // บำบัดรักษาเข้ม - หน้าตกใจ/กังวลมาก
            }
        }

        // สำหรับคะแนนสูงมาก (เกิน 50)
        if (score >= 50) {
            return "😱"; // หน้าตกใจมาก
        }

        return "😐"; // default - หน้าเฉยๆ
    }

    /**
     * ดึง emoji สำหรับสารเสพติดแต่ละประเภท
     */
    private String getSubstanceEmoji(String substanceId) {
        switch (substanceId) {
            case "a":
                return "🚬"; // ยาสูบ
            case "b":
                return "🍺"; // แอลกอฮอล์
            case "c":
                return "🌿"; // กัญชา
            case "d":
                return "❄️"; // โคเคน
            case "e":
                return "💊"; // แอมเฟตามีน
            case "f":
                return "🧪"; // สารระเหย
            case "g":
                return "😴"; // ยากล่อมประสาท
            case "h":
                return "🌈"; // ยาหลอนประสาท
            case "i":
                return "💉"; // สารกลุ่มฝิ่น
            case "j":
                return "🔬"; // สารเสพติดอื่นๆ
            default:
                return "📊"; // default
        }
    }

    /**
     * เน้นสีพื้นหลังของคะแนนตามระดับความรุนแรง
     */
    private void highlightScore(TextView textView, String substanceId, int score) {
        int backgroundColor = android.R.color.transparent;
        int textColor = android.R.color.black;

        // กำหนดสีตามระดับการบำบัด
        if (substanceId.equals("b")) { // แอลกอฮอล์
            if (score >= 0 && score <= 10) {
                backgroundColor = R.color.light_green; // ไม่ต้องบำบัด - เขียว
                textColor = R.color.dark_green;
            } else if (score >= 11 && score <= 26) {
                backgroundColor = R.color.light_yellow; // บำบัดอย่างย่อ - เหลือง
                textColor = R.color.dark_yellow;
            } else if (score >= 27) {
                backgroundColor = R.color.light_red; // บำบัดเข้ม - แดง
                textColor = R.color.white;
            }
        } else { // สารเสพติดอื่นๆ
            if (score >= 0 && score <= 3) {
                backgroundColor = R.color.light_green; // ไม่ต้องบำบัด - เขียว
                textColor = R.color.dark_green;
            } else if (score >= 4 && score <= 26) {
                backgroundColor = R.color.light_yellow; // บำบัดอย่างย่อ - เหลือง
                textColor = R.color.dark_yellow;
            } else if (score >= 27) {
                backgroundColor = R.color.light_red; // บำบัดเข้ม - แดง
                textColor = R.color.white;
            }
        }

        // ใช้สีพื้นหลังและสีข้อความ
        if (backgroundColor != android.R.color.transparent) {
            textView.setBackgroundResource(backgroundColor);
            textView.setTextColor(getResources().getColor(textColor));
        }

        // เพิ่ม padding เพื่อความสวยงาม
        int padding = (int) (8 * getResources().getDisplayMetrics().density); // 8dp
        textView.setPadding(padding, padding, padding, padding);
    }

    /**
     * รีเฟรชคะแนนทั้งหมด
     */
    public void refreshScores() {
        if (currentPersonId != null) {
            loadDrugsSummary(currentPersonId);
            Log.d(TAG, "รีเฟรชคะแนนสำเร็จ");
        } else {
            Log.w(TAG, "ไม่สามารถรีเฟรชได้ - ไม่มี personId");
        }
    }

    /**
     * บังคับรีเฟรชคะแนนทั้งหมด
     */
    public void forceRefreshAllScores() {
        try {
            // ล้างคะแนนเดิมก่อน
            clearAllScores();

            // โหลดข้อมูลใหม่
            if (currentPersonId != null) {
                loadDrugsSummary(currentPersonId);

                // รีเฟรชข้อมูลจาก ViewModel
                if (viewModel != null) {
                    AssistScore assistScore = viewModel.getAssistScoreMutableLiveData().getValue();
                    if (assistScore != null) {
                        // อัพเดต nicotine score
                        if (assistScore.getNicotineScore() != null) {
                            try {
                                int nicotineScore = Integer.parseInt(assistScore.getNicotineScore());
                                updateScoreDisplay("a", nicotineScore);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "ไม่สามารถแปลงคะแนน nicotine เป็นตัวเลขได้");
                            }
                        }

                        // อัพเดต alcohol score
                        if (assistScore.getAlcoholScore() != null) {
                            try {
                                int alcoholScore = Integer.parseInt(assistScore.getAlcoholScore());
                                updateScoreDisplay("b", alcoholScore);
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "ไม่สามารถแปลงคะแนน alcohol เป็นตัวเลขได้");
                            }
                        }
                    }
                }

                Log.d(TAG, "บังคับรีเฟรชคะแนนทั้งหมดสำเร็จ");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบังคับรีเฟรช: " + e.getMessage());
        }
    }

    /**
     * ล้างคะแนนทั้งหมด
     */
    private void clearAllScores() {
        for (TextView textView : scoreTextViews.values()) {
            if (textView != null) {
                textView.setText("-");
                textView.setBackgroundResource(android.R.color.transparent);
                textView.setTextColor(getResources().getColor(android.R.color.black));
                textView.setPadding(0, 0, 0, 0);
            }
        }
        Log.d(TAG, "ล้างคะแนนทั้งหมดแล้ว");
    }

    /**
     * ดึงข้อมูลสรุปคะแนน
     */
    public String getScoreSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("📊 สรุปคะแนน ASSIST:\n\n");

        String[] substanceNames = {
                "🚬 ผลิตภัณฑ์ยาสูบ",
                "🍺 เครื่องดื่มแอลกอฮอล์",
                "🌿 กัญชา",
                "❄️ โคเคน",
                "💊 ยากระตุ้นประสาท",
                "🧪 สารระเหย",
                "😴 ยากล่อมประสาท",
                "🌈 ยาหลอนประสาท",
                "💉 สารกลุ่มฝิ่น",
                "🔬 สารเสพติดอื่นๆ"
        };

        String[] substanceIds = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };

        int totalHighRisk = 0;
        int totalMediumRisk = 0;
        int totalLowRisk = 0;

        for (int i = 0; i < substanceIds.length; i++) {
            String substanceId = substanceIds[i];
            TextView textView = scoreTextViews.get(substanceId);

            if (textView != null) {
                String scoreText = textView.getText().toString();
                if (!scoreText.equals("-") && !scoreText.isEmpty()) {
                    // แยกคะแนนออกจากข้อความ (format: "21 😰 ได้รับการบำบัดอย่างย่อ")
                    String[] parts = scoreText.split(" ");
                    if (parts.length >= 1) {
                        try {
                            int score = Integer.parseInt(parts[0]);
                            String treatmentLevel = getTreatmentLevel(substanceId, score);
                            String emoji = getScoreEmoji(substanceId, score);

                            summary.append(substanceNames[i]).append(": ").append(score).append(" ").append(emoji)
                                    .append(" ").append(treatmentLevel).append("\n");

                            // นับจำนวนตามระดับความเสี่ยง
                            if (treatmentLevel.contains("บำบัดรักษาเข้ม")) {
                                totalHighRisk++;
                            } else if (treatmentLevel.contains("บำบัดอย่างย่อ")) {
                                totalMediumRisk++;
                            } else {
                                totalLowRisk++;
                            }
                        } catch (NumberFormatException e) {
                            // ถ้าเป็นคะแนน 0
                            if (parts[0].equals("0")) {
                                summary.append(substanceNames[i]).append(": 0 😊\n");
                                totalLowRisk++;
                            }
                        }
                    }
                }
            }
        }

        // เพิ่มสรุปความเสี่ยงรวม
        summary.append("\n📈 สรุประดับการบำบัด:\n");
        summary.append("😊 ไม่ต้องบำบัด: ").append(totalLowRisk).append(" อย่าง\n");
        summary.append("😟 บำบัดอย่างย่อ: ").append(totalMediumRisk).append(" อย่าง\n");
        summary.append("😰 บำบัดรักษาเข้ม: ").append(totalHighRisk).append(" อย่าง");

        return summary.toString();
    }

    /**
     * ตรวจสอบว่ามีคะแนนแสดงอยู่หรือไม่
     */
    public boolean hasScoresDisplayed() {
        for (TextView textView : scoreTextViews.values()) {
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty()) {
                    if (text.equals("0") || text.startsWith("0 😊")) {
                        return true; // คะแนน 0 ก็นับว่ามีคะแนน
                    }
                    // ตรวจสอบรูปแบบ "21 😰 ได้รับการบำบัดอย่างย่อ"
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            Integer.parseInt(parts[0]);
                            return true;
                        } catch (NumberFormatException e) {
                            // ไม่ใช่ตัวเลข ข้ามไป
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * ดึงคะแนนรวมของสารเสพติดทั้งหมด
     */
    public int getTotalScore() {
        int total = 0;
        for (TextView textView : scoreTextViews.values()) {
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty()) {
                    if (text.equals("0") || text.startsWith("0 😊")) {
                        // คะแนน 0 ไม่ต้องบวก
                        continue;
                    }
                    // แยกคะแนนออกจากข้อความ
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            total += Integer.parseInt(parts[0]);
                        } catch (NumberFormatException e) {
                            // ไม่ใช่ตัวเลข ข้ามไป
                        }
                    }
                }
            }
        }
        return total;
    }

    /**
     * ดึงคะแนนสูงสุด
     */
    public int getHighestScore() {
        int highest = 0;
        for (TextView textView : scoreTextViews.values()) {
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty()) {
                    if (text.equals("0") || text.startsWith("0 😊")) {
                        // คะแนน 0 ไม่ต้องเปรียบเทียบ
                        continue;
                    }
                    // แยกคะแนนออกจากข้อความ
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            int score = Integer.parseInt(parts[0]);
                            if (score > highest) {
                                highest = score;
                            }
                        } catch (NumberFormatException e) {
                            // ไม่ใช่ตัวเลข ข้ามไป
                        }
                    }
                }
            }
        }
        return highest;
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงสูงหรือไม่
     */

    public boolean hasHighRisk() {
        for (String substanceId : scoreTextViews.keySet()) {
            TextView textView = scoreTextViews.get(substanceId);
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty() && !text.equals("0") && !text.startsWith("0 😊")) {
                    // แยกคะแนนออกจากข้อความ
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            int score = Integer.parseInt(parts[0]);
                            if (substanceId.equals("b") && score >= 27) { // แอลกอฮอล์
                                return true;
                            } else if (!substanceId.equals("b") && score >= 27) { // สารอื่นๆ
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            // ไม่ใช่ตัวเลข ข้ามไป
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * ดึงจำนวนสารเสพติดที่มีความเสี่ยง
     */
    public int getSubstancesWithRiskCount() {
        int count = 0;
        for (String substanceId : scoreTextViews.keySet()) {
            TextView textView = scoreTextViews.get(substanceId);
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty() && !text.equals("0") && !text.startsWith("0 😊")) {
                    // แยกคะแนนออกจากข้อความ
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            int score = Integer.parseInt(parts[0]);
                            if (substanceId.equals("b") && score >= 11) { // แอลกอฮอล์
                                count++;
                            } else if (!substanceId.equals("b") && score >= 4) { // สารอื่นๆ
                                count++;
                            }
                        } catch (NumberFormatException e) {
                            // ไม่ใช่ตัวเลข ข้ามไป
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * รีเซ็ตข้อมูลทั้งหมด
     */
    public void resetAllData() {
        clearAllScores();
        currentPersonId = null;
        Log.d(TAG, "รีเซ็ตข้อมูลทั้งหมดแล้ว");
    }

    /**
     * ดึงผลการประเมินพร้อม emoji
     */
    public String getAssessmentResultWithEmoji() {
        if (!hasScoresDisplayed()) {
            return "😐 ยังไม่ได้ประเมิน";
        }

        int highRiskCount = 0;
        int mediumRiskCount = 0;
        int lowRiskCount = 0;

        // นับจำนวนแต่ละระดับ
        for (String substanceId : scoreTextViews.keySet()) {
            TextView textView = scoreTextViews.get(substanceId);
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty()) {
                    String[] parts = text.split(" ");
                    if (parts.length >= 1) {
                        try {
                            int score = Integer.parseInt(parts[0]);
                            String treatmentLevel = getTreatmentLevel(substanceId, score);

                            if (treatmentLevel.contains("บำบัดรักษาเข้ม")) {
                                highRiskCount++;
                            } else if (treatmentLevel.contains("บำบัดอย่างย่อ")) {
                                mediumRiskCount++;
                            } else {
                                lowRiskCount++;
                            }
                        } catch (NumberFormatException e) {
                            // ถ้าเป็นคะแนน 0
                            if (parts[0].equals("0")) {
                                lowRiskCount++;
                            }
                        }
                    }
                }
            }
        }

        // ประเมินผลรวม
        if (highRiskCount > 0) {
            return "😰 มีสารเสพติดที่ต้องบำบัดรักษาเข้ม " + highRiskCount + " อย่าง";
        } else if (mediumRiskCount > 0) {
            return "😟 มีสารเสพติดที่ต้องบำบัดอย่างย่อ " + mediumRiskCount + " อย่าง";
        } else if (lowRiskCount > 0) {
            return "😊 ไม่มีความเสี่ยงจากสารเสพติด";
        }

        return "😐 ไม่สามารถประเมินได้";
    }

    /**
     * ดึงคำแนะนำพร้อม emoji
     */
    public String getRecommendationWithEmoji() {
        if (!hasScoresDisplayed()) {
            return "😐 กรุณากรอกข้อมูลให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        boolean hasHighRisk = hasHighRisk();
        int substancesWithRisk = getSubstancesWithRiskCount();

        if (hasHighRisk) {
            return "😱 ต้องการการรักษาเร่งด่วน! ควรปรึกษาแพทย์ผู้เชี่ยวชาญด้านสารเสพติดทันที";
        } else if (substancesWithRisk > 0) {
            return "😟 ควรได้รับการปรึกษาและคำแนะนำจากแพทย์เกี่ยวกับการใช้สารเสพติด";
        } else {
            return "😊 ดีมาก! ควรรักษาสถานะปัจจุบันและหลีกเลี่ยงการใช้สารเสพติดทุกชนิด";
        }
    }

    /**
     * สร้างรายงานประเมินพร้อม emoji
     */
    public String generateAssessmentReportWithEmoji() {
        StringBuilder report = new StringBuilder();

        report.append("📊 รายงานการประเมิน ASSIST\n");
        report.append("===========================\n\n");

        // ข้อมูลพื้นฐาน
        report.append("📋 สถานะการประเมิน: ");
        if (hasScoresDisplayed()) {
            report.append("✅ ครบถ้วน\n");
        } else {
            report.append("❌ ไม่ครบถ้วน\n");
        }

        // ผลการประเมิน
        report.append("\n🎯 ผลการประเมิน:\n");
        report.append(getAssessmentResultWithEmoji()).append("\n\n");

        // คะแนนรวม
        int totalScore = getTotalScore();
        int highestScore = getHighestScore();
        report.append("📈 คะแนน:\n");
        report.append("- คะแนนรวม: ").append(totalScore).append(" คะแนน\n");
        report.append("- คะแนนสูงสุด: ").append(highestScore).append(" คะแนน\n\n");

        // รายละเอียดแต่ละสารเสพติด
        report.append("📝 รายละเอียดแต่ละสารเสพติด:\n");
        String[] substanceNames = {
                "🚬 ยาสูบ", "🍺 แอลกอฮอล์", "🌿 กัญชา", "❄️ โคเคน", "💊 แอมเฟตามีน",
                "🧪 สารระเหย", "😴 ยากล่อมประสาท", "🌈 ยาหลอนประสาท", "💉 สารกลุ่มฝิ่น", "🔬 สารอื่นๆ"
        };
        String[] substanceIds = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };

        for (int i = 0; i < substanceIds.length; i++) {
            TextView textView = scoreTextViews.get(substanceIds[i]);
            if (textView != null) {
                String text = textView.getText().toString();
                if (!text.equals("-") && !text.isEmpty()) {
                    report.append("- ").append(substanceNames[i]).append(": ").append(text).append("\n");
                }
            }
        }

        // คำแนะนำ
        report.append("\n💡 คำแนะนำ:\n");
        report.append(getRecommendationWithEmoji()).append("\n\n");

        // การติดตาม
        if (hasHighRisk()) {
            report.append("⚠️ ข้อควรระวัง:\n");
            report.append("- ต้องการการติดตามอย่างใกล้ชิด\n");
            report.append("- ควรนัดพบแพทย์เป็นประจำ\n");
            report.append("- หลีกเลี่ยงสภาพแวดล้อมเสี่ยง\n\n");
        }

        report.append("===========================\n");
        report.append("📅 วันที่สร้างรายงาน: ").append(new java.util.Date().toString());

        return report.toString();
    }
}