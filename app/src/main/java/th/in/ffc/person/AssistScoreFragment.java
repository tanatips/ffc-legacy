package th.in.ffc.person;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private OnDataPass dataPasser;
    private Map<String, TextView> scoreTextViews = new HashMap<>();

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

        // กำหนด ID สำหรับ TextView ในแต่ละแถว (ถ้ายังไม่มีใน layout)
        setupScoreTextViews(view);

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // สังเกตการเปลี่ยนแปลงข้อมูลจาก ViewModel
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                // ดึงข้อมูลสรุปจาก SfDrugsDao
                loadDrugsSummary(personInfo.getId());
            }
        });

        // ยังคงดึงข้อมูล nicotine score จาก AssistScore (ตามโค้ดเดิม)
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data.getPersonId() != null) {
                if (data.getNicotineScore() != null) {
                    // แสดงค่า nicotine score ใน TextView ของ row a (ยาสูบ)
                    if (scoreTextViews.containsKey("a")) {
                        scoreTextViews.get("a").setText(data.getNicotineScore().toString());
                    }
                }
            }
        });
    }

    /**
     * ตั้งค่า TextView สำหรับแสดงคะแนนแต่ละประเภทสารเสพติด
     */
    private void setupScoreTextViews(View view) {
        // ค้นหา TextView ที่อยู่ในคอลัมน์คะแนนของตาราง
        // แถวแรกมี ID อยู่แล้ว เพิ่ม ID สำหรับแถวอื่นๆ
        TextView tvScoreA = view.findViewById(R.id.tvScoreA);
        scoreTextViews.put("a", tvScoreA);

        // เพิ่ม ID ใน XML หรือค้นหาด้วยวิธีอื่น (ในที่นี้จะสมมติว่าได้เพิ่ม ID ใน XML แล้ว)
        // หมายเหตุ: คุณจะต้องเพิ่ม ID เหล่านี้ในไฟล์ XML fragment_assist_score.xml
        int[] scoreIds = new int[]{
                R.id.tvScoreB, R.id.tvScoreC, R.id.tvScoreD, R.id.tvScoreE,
                R.id.tvScoreF, R.id.tvScoreG, R.id.tvScoreH, R.id.tvScoreI,
                R.id.tvScoreJ //, R.id.tvScoreK, R.id.tvScoreL
        };

        String[] substanceIds = new String[]{
                "b", "c", "d", "e", "f", "g", "h", "i", "j" // , "k", "l"
        };
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

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
            // คำนวณผลรวมของคำตอบจาก Q2 ถึง Q7 (หรือตามที่ต้องการ)
            // ในที่นี้จะรวมคำตอบจาก Q2-Q7 เพื่อคำนวณคะแนน ASSIST
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};

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

                // ตรวจสอบว่ามี TextView สำหรับสารเสพติดชนิดนี้หรือไม่
                if (scoreTextViews.containsKey(subquestion)) {
                    TextView tvScore = scoreTextViews.get(subquestion);
                    if (tvScore != null) {
                        tvScore.setText(String.valueOf(totalScore));

                        // เพิ่มการเน้นสีพื้นหลังตามระดับคะแนน
                        highlightScore(tvScore, subquestion, totalScore);
                    }
                }
            }

            Log.d("AssistScoreFragment", "ดึงและแสดงข้อมูลสำเร็จ");
        } catch (Exception e) {
            Log.e("AssistScoreFragment", "เกิดข้อผิดพลาดในการดึงข้อมูล: " + e.getMessage());
        }
    }

    /**
     * เน้นสีพื้นหลังของคะแนนตามระดับความรุนแรง
     */
    private void highlightScore(TextView textView, String substanceId, int score) {
        int backgroundColor = android.R.color.transparent;

        // กำหนดช่วงคะแนนตามประเภทสารเสพติด
        if (substanceId.equals("b")) { // แอลกอฮอล์
            if (score >= 0 && score <= 10) {
                backgroundColor = R.color.light_green; // ไม่ต้องบำบัด
            } else if (score >= 11 && score <= 26) {
                backgroundColor = R.color.light_yellow; // บำบัดอย่างย่อ
            } else if (score >= 27) {
                backgroundColor = R.color.light_red; // บำบัดเข้มข้น
            }
        } else { // สารเสพติดอื่นๆ
            if (score >= 0 && score <= 3) {
                backgroundColor = R.color.light_green; // ไม่ต้องบำบัด
            } else if (score >= 4 && score <= 26) {
                backgroundColor = R.color.light_yellow; // บำบัดอย่างย่อ
            } else if (score >= 27) {
                backgroundColor = R.color.light_red; // บำบัดเข้มข้น
            }
        }

        if (backgroundColor != android.R.color.transparent) {
            textView.setBackgroundResource(backgroundColor);
        }
    }
}