package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;
import android.widget.TextView;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import java.util.Map;

// เพิ่มในส่วน import

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmookingFragment extends Fragment {


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
    public SmookingFragment() {
        // Required empty public constructor
    }
    // เพิ่มตัวแปรสำหรับแสดงคะแนน
    private TextView tvSmokingScore;
    private TextView tvSmokingRiskLevel;
    private boolean isUpdatingFromCode = false;

    // ตัวแปรเดิมทั้งหมด...

    public static SmookingFragment newInstance(String param1, String param2) {
        SmookingFragment fragment = new SmookingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (shareViewModel == null) {
            StressDepression9qLiveData stressDepression9qLiveData =new StressDepression9qLiveData();
            shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
        }
        smookingLiveData = new SmookingLiveData();

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
        smokerInfo = new SmokerInfo();
        sfSmokerInfoDao = new SfSmokerInfoDao(getContext());
        rdoSmokerGroup = view.findViewById(R.id.rdoSmokerGroup);
        rdoSmokerAssist = view.findViewById(R.id.rdoSmokerAssist);
        rdoSmokerRegularly = view.findViewById(R.id.rdoSmokerRegularly);
//        rdoSmokerAssist.setVisibility(View.INVISIBLE);
//        rdoSmokerRegularly.setVisibility(View.INVISIBLE);
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

        // โค้ดเดิมทั้งหมด...
        smokerInfo = new SmokerInfo();
        sfSmokerInfoDao = new SfSmokerInfoDao(getContext());
        rdoSmokerGroup = view.findViewById(R.id.rdoSmokerGroup);
        rdoSmokerAssist = view.findViewById(R.id.rdoSmokerAssist);
        rdoSmokerRegularly = view.findViewById(R.id.rdoSmokerRegularly);

        // เพิ่มการ observe คะแนนจาก SharedViewModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // สังเกตการเปลี่ยนแปลงข้อมูลจาก ViewModel
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                // ดึงข้อมูลคะแนนการสูบบุหรี่จาก SfDrugsDao
                loadSmokingScore(personInfo.getId());
            }
        });

        // สังเกตคะแนน nicotine จาก AssistScore
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data.getPersonId() != null && data.getNicotineScore() != null) {
                // แสดงคะแนนบุหรี่ - แปลง String เป็น int
                try {
                    int nicotineScore = Integer.parseInt(data.getNicotineScore());
                    updateSmokingScore(nicotineScore);
                } catch (NumberFormatException e) {
                    Log.e("SmookingFragment", "ไม่สามารถแปลงคะแนน nicotine เป็นตัวเลขได้: " + data.getNicotineScore());
                    updateSmokingScore(0); // ใช้ค่าเริ่มต้นเป็น 0
                }
            }
        });

//        boolean isInDialog = getParentFragment() instanceof DialogFragment;

        rdoSmokerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // หยุดการทำงานหาก isUpdatingFromCode เป็น true
                if (isUpdatingFromCode) return;

                String data = "";
                if(checkedId == R.id.rdoSmokerGroup1) {
                    data = "1";
                    // ล้างการเลือกขั้นตอนต่อไป
                    isUpdatingFromCode = true;
                    rdoSmokerAssist.clearCheck();
                    rdoSmokerRegularly.clearCheck();
                    rdoSmokerAssist1.setChecked(false);
                    rdoSmokerAssist2.setChecked(false);
                    rdoSmokerAssist3.setChecked(false);

                    rdoSmokerAssist1.setEnabled(false);
                    rdoSmokerAssist2.setEnabled(false);
                    rdoSmokerAssist3.setEnabled(false);

                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);

                    isUpdatingFromCode = false;
                    smokerInfo.setSmokerAssist("");
                    smokerInfo.setSmokerRegularly("");
                    smookingLiveData.setSelectedRdoSmokerAssist(null);
                    smookingLiveData.setSelectedRdoSmokerRegularly(null);
                } else if(checkedId == R.id.rdoSmokerGroup2) {
                    data = "2";
                    // ล้างการเลือกขั้นตอนต่อไป
                    isUpdatingFromCode = true;
                    rdoSmokerAssist.clearCheck();
                    rdoSmokerRegularly.clearCheck();
                    rdoSmokerAssist1.setChecked(false);
                    rdoSmokerAssist2.setChecked(false);
                    rdoSmokerAssist3.setChecked(false);

                    rdoSmokerAssist1.setEnabled(false);
                    rdoSmokerAssist2.setEnabled(false);
                    rdoSmokerAssist3.setEnabled(false);

                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);

                    isUpdatingFromCode = false;
                    smokerInfo.setSmokerAssist("");
                    smokerInfo.setSmokerRegularly("");
                    smookingLiveData.setSelectedRdoSmokerAssist(null);
                    smookingLiveData.setSelectedRdoSmokerRegularly(null);
                } else if(checkedId == R.id.rdoSmokerGroup3) {
                    data = "3";
                    // เก็บการเลือกเดิมไว้ แต่ล้าง SmokerRegularly
                    isUpdatingFromCode = true;
                    rdoSmokerAssist.clearCheck();
                    rdoSmokerRegularly.clearCheck();

                    rdoSmokerAssist1.setChecked(false);
                    rdoSmokerAssist2.setChecked(false);
                    rdoSmokerAssist3.setChecked(false);

                    rdoSmokerAssist1.setEnabled(true);
                    rdoSmokerAssist2.setEnabled(true);
                    rdoSmokerAssist3.setEnabled(true);


                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);

                    isUpdatingFromCode = false;
                    smokerInfo.setSmokerRegularly("");
                }

                smokerInfo.setSmokerGroup(data);
                smookingLiveData.setSelectedRdoSmokerGroup(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                // คำนวณคะแนนแบบ real-time
//                calculateAndUpdateScore();

                dataPasser.onSmokerInfo(smokerInfo);

                Log.d("SmookingFragment", "Selected SmokerGroup: " + data + ", Form Complete: " + isFormComplete());
            }
        });
        rdoSmokerAssist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // หยุดการทำงานหาก isUpdatingFromCode เป็น true
                if (isUpdatingFromCode) return;

                String data = "";
                if(checkedId == R.id.rdoSmokerAssist1) {
                    data = "1";
                    // ล้าง SmokerRegularly เพราะไม่ต้องตอบขั้นตอนต่อไป
                    isUpdatingFromCode = true;
                    rdoSmokerRegularly.clearCheck();
                    isUpdatingFromCode = false;
                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);

                    smookingLiveData.setSelectedRdoSmokerRegularly(null);
                    smokerInfo.setSmokerRegularly("");
                } else if(checkedId == R.id.rdoSmokerAssist2) {
                    data = "2";
                    // ล้าง SmokerRegularly เพราะไม่ต้องตอบขั้นตอนต่อไป
                    isUpdatingFromCode = true;
                    rdoSmokerRegularly.clearCheck();
                    isUpdatingFromCode = false;
                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);

                    smokerInfo.setSmokerRegularly("");
                    smookingLiveData.setSelectedRdoSmokerRegularly(null);
                } else if(checkedId == R.id.rdoSmokerAssist3) {
                    data = "3";
                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);
                    // เก็บการเลือกเดิมไว้ หรือล้างก็ได้
                }

                smokerInfo.setSmokerAssist(data);
                smookingLiveData.setSelectedRdoSmokerAssist(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                // คำนวณคะแนนแบบ real-time
//                calculateAndUpdateScore();

                dataPasser.onSmokerInfo(smokerInfo);

                Log.d("SmookingFragment", "Selected SmokerAssist: " + data + ", Form Complete: " + isFormComplete());
            }
        });

        rdoSmokerRegularly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // หยุดการทำงานหาก isUpdatingFromCode เป็น true
                if (isUpdatingFromCode) return;

                String data = "";
                if(checkedId == R.id.rdoSmokerRegularly1) {
                    data = "1";
                } else if(checkedId == R.id.rdoSmokerRegularly2) {
                    data = "2";
                } else if(checkedId == R.id.rdoSmokerRegularly3) {
                    data = "3";
                }

                smokerInfo.setSmokerRegularly(data);
                smookingLiveData.setSelectedRdoSmokerRegularly(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                // คำนวณคะแนนแบบ real-time
//                calculateAndUpdateScore();

                dataPasser.onSmokerInfo(smokerInfo);

                Log.d("SmookingFragment", "Selected SmokerRegularly: " + data + ", Form Complete: " + isFormComplete());
            }
        });

        shareViewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), smookingLiveData -> {
            if(smookingLiveData != null && !isUpdatingFromCode){
                isUpdatingFromCode = true;
                try {
                    if(smookingLiveData.getSelectedRdoSmokerGroup() != null) {
                        rdoSmokerGroup.check(smookingLiveData.getSelectedRdoSmokerGroup());
                    }
                    if(smookingLiveData.getSelectedRdoSmokerAssist() != null) {
                        rdoSmokerAssist.check(smookingLiveData.getSelectedRdoSmokerAssist());
                    }
                    if(smookingLiveData.getSelectedRdoSmokerRegularly() != null) {
                        rdoSmokerRegularly.check(smookingLiveData.getSelectedRdoSmokerRegularly());
                    }
                } finally {
                    isUpdatingFromCode = false;
                }
            }
        });
        loadData();
    }
    /**
     * โหลดคะแนนการสูบบุหรี่จาก SfDrugsDao
     */
    private void loadSmokingScore(String personInfoId) {
        try {
            // คำนวณผลรวมของคำตอบจาก Q2 ถึง Q7 สำหรับยาสูบ (substance a)
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            // ดึงข้อมูลจากแต่ละคำถามและรวมคะแนนสำหรับยาสูบ (a)
            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                // เอาเฉพาะคะแนนของยาสูบ (substance a)
                if (summaryMap.containsKey("a")) {
                    totalScore += summaryMap.get("a");
                }
            }

            // แสดงคะแนนรวม
            updateSmokingScore(totalScore);

            Log.d("SmookingFragment", "โหลดคะแนนการสูบบุหรี่สำเร็จ: " + totalScore);
        } catch (Exception e) {
            Log.e("SmookingFragment", "เกิดข้อผิดพลาดในการโหลดคะแนน: " + e.getMessage());
        }
    }
    /**
     * คำนวณและอัปเดตคะแนนแบบ real-time
     */
    private void calculateAndUpdateScore() {
        try {
            // คำนวณคะแนนจากการเลือกปัจจุบัน
            int score = calculateCurrentScore();
            updateSmokingScore(score);
        } catch (Exception e) {
            Log.e("SmookingFragment", "เกิดข้อผิดพลาดในการคำนวณคะแนน: " + e.getMessage());
        }
    }
    /**
     * คำนวณคะแนนจากการเลือกปัจจุบัน
     */
    private int calculateCurrentScore() {
        int score = 0;

        // คำนวณจากการเลือกใน RadioGroup ต่างๆ
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

    /**
     * อัปเดตการแสดงคะแนนและระดับความเสี่ยง
     */
    /**
     * อัปเดตการแสดงคะแนนและระดับความเสี่ยง (เวอร์ชันใช้สีที่กำหนดเอง)
     */
    private void updateSmokingScore(int score) {
        if (tvSmokingScore != null) {
            tvSmokingScore.setText(String.valueOf(score));

            // เปลี่ยนสี background และ text color ของ tvSmokingScore ตามระดับคะแนน
            if (score >= 0 && score <= 3) {
                // ไม่มีความเสี่ยง - สีเขียว
                tvSmokingScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvSmokingScore.setTextColor(Color.WHITE);
            } else if (score >= 4 && score <= 26) {
                // ความเสี่ยงปานกลาง - สีส้ม
                tvSmokingScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvSmokingScore.setTextColor(Color.WHITE);
            } else {
                // ความเสี่ยงสูง - สีแดง
                tvSmokingScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvSmokingScore.setTextColor(Color.WHITE);
            }
        }

        if (tvSmokingRiskLevel != null) {
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนน (สำหรับยาสูบ)
            if (score >= 0 && score <= 3) {
                riskLevel = "ไม่มีความเสี่ยง";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_green);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 4 && score <= 26) {
                riskLevel = "ความเสี่ยงปานกลาง";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else {
                riskLevel = "ความเสี่ยงสูง";
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_red);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            }

            tvSmokingRiskLevel.setText(riskLevel);
        }
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
    private void loadData(){

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<SmokerInfo> smokerInfos = sfSmokerInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(SmokerInfo smokerInfo :smokerInfos){
//                    setDataToViews(smokerInfo);
                    Log.d("smoker", "smoker infos:"+smokerInfos);
                    setSmokerInfo(smokerInfo);
                }


            }
        });

    }
    private void setDataToViews(SmokerInfo smokerInfo){

    }
    public void setSmokerInfo(SmokerInfo info) {
        Log.d("SmookingFragment", "setSmokerInfo called with: " +
                (info != null ? "Group=" + info.getSmokerGroup() + ", Assist=" + info.getSmokerAssist() + ", Regularly=" + info.getSmokerRegularly() : "null"));

        this.smokerInfo = info;
        updateUI();
    }
    private void updateUI() {
        if (this.smokerInfo != null) {
            // ตั้งค่าป้องกัน loop
            isUpdatingFromCode = true;

            try {
                // Set SmokerGroup
                String smokerGroup = this.smokerInfo.getSmokerGroup();
                if (smokerGroup != null && !smokerGroup.isEmpty()) {
                    switch (smokerGroup) {
                        case "1":
                            rdoSmokerGroup1.setChecked(true);
                            rdoSmokerAssist.clearCheck();
                            rdoSmokerRegularly.clearCheck();
                            rdoSmokerAssist1.setChecked(false);
                            rdoSmokerAssist2.setChecked(false);
                            rdoSmokerAssist3.setChecked(false);

                            rdoSmokerAssist1.setEnabled(false);
                            rdoSmokerAssist2.setEnabled(false);
                            rdoSmokerAssist3.setEnabled(false);

                            rdoSmokerRegularly1.setChecked(false);
                            rdoSmokerRegularly2.setChecked(false);
                            rdoSmokerRegularly3.setChecked(false);

                            rdoSmokerRegularly1.setEnabled(false);
                            rdoSmokerRegularly2.setEnabled(false);
                            rdoSmokerRegularly3.setEnabled(false);
                            break;
                        case "2":
                            rdoSmokerGroup2.setChecked(true);

                            rdoSmokerAssist.clearCheck();
                            rdoSmokerRegularly.clearCheck();
                            rdoSmokerAssist1.setChecked(false);
                            rdoSmokerAssist2.setChecked(false);
                            rdoSmokerAssist3.setChecked(false);

                            rdoSmokerAssist1.setEnabled(false);
                            rdoSmokerAssist2.setEnabled(false);
                            rdoSmokerAssist3.setEnabled(false);

                            rdoSmokerRegularly1.setChecked(false);
                            rdoSmokerRegularly2.setChecked(false);
                            rdoSmokerRegularly3.setChecked(false);

                            rdoSmokerRegularly1.setEnabled(false);
                            rdoSmokerRegularly2.setEnabled(false);
                            rdoSmokerRegularly3.setEnabled(false);

                            break;
                        case "3":
                            rdoSmokerGroup3.setChecked(true);
                            rdoSmokerAssist.clearCheck();
                            rdoSmokerRegularly.clearCheck();

                            rdoSmokerAssist1.setChecked(false);
                            rdoSmokerAssist2.setChecked(false);
                            rdoSmokerAssist3.setChecked(false);

                            rdoSmokerAssist1.setEnabled(true);
                            rdoSmokerAssist2.setEnabled(true);
                            rdoSmokerAssist3.setEnabled(true);


                            rdoSmokerRegularly1.setChecked(false);
                            rdoSmokerRegularly2.setChecked(false);
                            rdoSmokerRegularly3.setChecked(false);

                            rdoSmokerRegularly1.setEnabled(true);
                            rdoSmokerRegularly2.setEnabled(true);
                            rdoSmokerRegularly3.setEnabled(true);

                            // Set SmokerAssist if SmokerGroup is 3
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

                                        // Set SmokerRegularly if SmokerAssist is 3
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
                // ปิดการป้องกัน loop
                isUpdatingFromCode = false;
            }

            // คำนวณคะแนนหลังจากอัปเดต UI เสร็จแล้ว
//            calculateAndUpdateScore();
        }
    }
    public SmokerInfo getFormData() {
        return  this.smokerInfo;
    }
    // เพิ่มเมธอดเหล่านี้ใน SmookingFragment.java

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
     */
    public boolean isFormComplete() {
        // ตรวจสอบว่าได้เลือก SmokerGroup แล้วหรือไม่
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            return false;
        }

        // ถ้าเลือก "สูบบุหรี่เป็นประจำ" (option 3) ต้องเลือก SmokerAssist ด้วย
        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                return false;
            }

            // ถ้าเลือก "สูบเป็นประจำ" (option 3 ใน SmokerAssist) ต้องเลือก SmokerRegularly ด้วย
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

        // ตรวจสอบว่าได้เลือกสถานะการสูบบุหรี่หรือไม่
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกสถานะการสูบบุหรี่");
            return message.toString();
        }

        // ถ้าเลือก "สูบบุหรี่เป็นประจำ" แต่ยังไม่ได้เลือกความถี่
        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกความถี่ในการสูบ");
                return message.toString();
            }

            // ถ้าเลือก "สูบเป็นประจำ" แต่ยังไม่ได้เลือกระดับการให้คำแนะนำ
            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่: ยังไม่ได้เลือกการให้คำแนะนำ/ปรึกษา");
                    return message.toString();
                }
            }
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าได้เลือกสถานะการสูบบุหรี่หรือไม่
        if (smokerInfo == null || smokerInfo.getSmokerGroup() == null || smokerInfo.getSmokerGroup().isEmpty()) {
            message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
            message.append("• ยังไม่ได้เลือกสถานะการสูบบุหรี่ (ไม่เคยสูบ/เคยสูบ/สูบเป็นประจำ)");
            return message.toString();
        }

        // ตรวจสอบการเลือกความถี่ (สำหรับผู้ที่สูบเป็นประจำ)
        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
                message.append("• ยังไม่ได้เลือกความถี่ในการสูบ (บางครั้ง/บางคราว/เป็นประจำ)");
                return message.toString();
            }

            // ตรวจสอบการให้คำแนะนำ (สำหรับผู้ที่สูบเป็นประจำ)
            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    message.append("แบบประเมินความเสี่ยงจากการสูบบุหรี่:\n");
                    message.append("• ยังไม่ได้เลือกการให้คำแนะนำ/ปรึกษา");
                    return message.toString();
                }
            }
        }

        return ""; // ไม่มีข้อผิดพลาด
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

            // รีเซ็ต smokerInfo
            smokerInfo = new SmokerInfo();

            // รีเซ็ตคะแนน
            if (tvSmokingScore != null) {
                tvSmokingScore.setText("-");
                tvSmokingScore.setBackgroundResource(R.color.light_gray);
                tvSmokingScore.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            if (tvSmokingRiskLevel != null) {
                tvSmokingRiskLevel.setText("ยังไม่ได้ประเมิน");
                tvSmokingRiskLevel.setBackgroundResource(R.color.light_gray);
                tvSmokingRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }
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

        // หากเลือก "ไม่เคยสูบ" หรือ "เคยสูบแต่ไม่ใช่ใน 3 เดือนที่ผ่านมา" ถือว่าครบ 100%
        if ("1".equals(smokerInfo.getSmokerGroup()) || "2".equals(smokerInfo.getSmokerGroup())) {
            return 100;
        }

        // หากเลือก "สูบเป็นประจำ" ต้องตรวจสอบขั้นตอนต่อไป
        if ("3".equals(smokerInfo.getSmokerGroup())) {
            if (smokerInfo.getSmokerAssist() == null || smokerInfo.getSmokerAssist().isEmpty()) {
                return 33; // กรอก 1/3
            }

            if ("1".equals(smokerInfo.getSmokerAssist()) || "2".equals(smokerInfo.getSmokerAssist())) {
                return 100; // ไม่ต้องตอบขั้นตอนถัดไป
            }

            if ("3".equals(smokerInfo.getSmokerAssist())) {
                if (smokerInfo.getSmokerRegularly() == null || smokerInfo.getSmokerRegularly().isEmpty()) {
                    return 66; // กรอง 2/3
                } else {
                    return 100; // กรอบครบทุกขั้นตอน
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
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%)";
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("SmookingFragment", "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}