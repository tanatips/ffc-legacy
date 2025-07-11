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
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessmentSummary;
import th.in.ffc.person.PersonScreeningForm15Activity;
import th.in.ffc.util.Log;

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
        int backgroundColor = Color.parseColor("#F8F9FA");
        int textColor = Color.parseColor("#2C3E50");

        if (totalScore == 0) {
            resultText = "ไม่มีความเสี่ยงต่อการฆ่าตัวตาย";
            resultCode = "1B0270";
            backgroundColor = Color.parseColor("#E8F5E8");
            textColor = Color.parseColor("#27AE60");
        } else if (totalScore >= 1 && totalScore <= 8) {
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับต่ำ";
            resultCode = "1B0271";
            backgroundColor = Color.parseColor("#FFF3CD");
            textColor = Color.parseColor("#F39C12");
        } else if (totalScore >= 9 && totalScore <= 16) {
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับปานกลาง";
            resultCode = "1B0272";
            backgroundColor = Color.parseColor("#FFE4CC");
            textColor = Color.parseColor("#E67E22");
        } else if (totalScore >= 17) {
            resultText = "มีความเสี่ยงต่อการฆ่าตัวตายระดับสูง";
            resultCode = "1B0273";
            backgroundColor = Color.parseColor("#F8D7DA");
            textColor = Color.parseColor("#E74C3C");
        }

        String finalText = resultText + "\n(" + resultCode + ")";

        // เพิ่มคำเตือนพิเศษสำหรับความเสี่ยงสูง
        if (totalScore >= 17) {
            finalText += "\n⚠️ ต้องการการแทรกแซงทันที!";
            textColor = Color.parseColor("#E74C3C");
        } else if (totalScore >= 9) {
            finalText += "\n⚠️ ควรติดตามอย่างใกล้ชิด";
            textColor = Color.parseColor("#E67E22");
        }

        tv8qResultDetail.setText(finalText);
        tv8qResultDetail.setTextColor(textColor);
        tv8qResultDetail.setBackgroundColor(backgroundColor);

        Log.d("SuicideAssessment8q", "Result displayed: " + finalText);
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
            riskLevel = "ไม่มีความเสี่ยง";
            highlightColor = highlightNone;
        } else if (totalScore >= 1 && totalScore <= 8) {
            row1_8.setBackgroundColor(highlightLow);
            resultCode = "1B0271";
            riskLevel = "ความเสี่ยงต่ำ";
            highlightColor = highlightLow;
        } else if (totalScore >= 9 && totalScore <= 16) {
            row9_16.setBackgroundColor(highlightMedium);
            resultCode = "1B0272";
            riskLevel = "ความเสี่ยงปานกลาง";
            highlightColor = highlightMedium;
        } else if (totalScore >= 17) {
            row17plus.setBackgroundColor(highlightHigh);
            resultCode = "1B0273";
            riskLevel = "ความเสี่ยงสูง";
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
            results.put("result_description", getResultDescription(totalScore));
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

        // อัปเดตการแสดงผลใหม่
        updateScoreDisplay();
        updateResultDisplay(totalScore);

        // เรียกใช้การ highlight ตารางเดิม (ถ้ายังต้องการ)
        highlightScoreRow(totalScore);

        // เพิ่มการตรวจสอบและอัปเดตสีใหม่
        updateRadioButtonColors();
        checkCriticalQuestions();

        // อัปเดตสถานะการกรอกข้อมูลใน Activity หลัก
        updateFormStatusInActivity();

        Log.d("SuicideAssessment8q", "Score and highlight updated - Total: " + totalScore);
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
        if (totalScore >= 17) {
            // ความเสี่ยงสูงมาก - แจ้งเตือนทันที
            Toast.makeText(getContext(),
                    "⚠️ ความเสี่ยงสูงมาก: " + totalScore + " คะแนน\n" +
                            "จำเป็นต้องดำเนินการแทรกแซงทันที",
                    Toast.LENGTH_SHORT).show();
//            showRiskAlert("⚠️ ความเสี่ยงสูงมาก",
//                    "คะแนน " + totalScore + " แสดงความเสี่ยงสูงมากต่อการฆ่าตัวตาย\n" +
//                            "จำเป็นต้องดำเนินการแทรกแซงทันที",
//                    Color.parseColor("#D32F2F"));
        } else if (totalScore >= 9) {
            // ความเสี่ยงปานกลาง
            Toast.makeText(getContext(),
                    "ℹ️ ความเสี่ยงปานกลาง: " + totalScore + " คะแนน\n" +
                            "ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด",
                    Toast.LENGTH_SHORT).show();
//            showRiskAlert("⚠️ ความเสี่ยงปานกลาง",
//                    "คะแนน " + totalScore + " แสดงความเสี่ยงปานกลางต่อการฆ่าตัวตาย\n" +
//                            "ควรให้คำปรึกษาและติดตามอย่างใกล้ชิด",
//                    Color.parseColor("#F57C00"));
        } else if (totalScore >= 1) {
            // ความเสี่ยงต่ำ
            Toast.makeText(getContext(),
                    "ℹ️ ความเสี่ยงต่ำ: " + totalScore + " คะแนน\n" +
                            "ควรให้คำแนะนำและสนับสนุน",
                    Toast.LENGTH_SHORT).show();
//            showRiskInfo("ℹ️ ความเสี่ยงต่ำ",
//                    "คะแนน " + totalScore + " แสดงความเสี่ยงต่ำต่อการฆ่าตัวตาย\n" +
//                            "ควรให้คำแนะนำและสนับสนุน");
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

            // รีเซ็ตการแสดงผล
            updateScoreDisplay();
            if (tv8qResultDetail != null) {
                tv8qResultDetail.setText("ยังไม่ได้ประเมิน");
                tv8qResultDetail.setTextColor(Color.parseColor("#7F8C8D"));
                tv8qResultDetail.setBackgroundColor(Color.parseColor("#F8F9FA"));
            }
        }
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
}