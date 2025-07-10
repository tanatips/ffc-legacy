package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlcoholFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlcoholFragment extends Fragment {

    // Constants
    private static final String SUBSTANCE_ALCOHOL = "b";
    private static final String TAG = "AlcoholFragment";

    // LiveData and ViewModel
    DrinkingLiveData drinkingLiveData;
    SharedViewModel shareViewModel;

    // Data and callback
    private OnDataPass dataPasser;
    private DrinkingInfo drinkingInfo;

    // UI Components
    private RadioGroup rdoDrinking;
    private RadioGroup rdoDrinkingFrequency;
    private RadioGroup rdoDrinkingAlway;
    RadioButton rdoDrinkingFrequency1;
    RadioButton rdoDrinkingFrequency2;
    RadioButton rdoDrinkingFrequency3;

    // Score display components
    private TextView tvAlcoholScore;
    private TextView tvAlcoholRiskLevel;

    // Flag to prevent loops
    private boolean isUpdatingFromScore = false;

    public AlcoholFragment() {
        // Required empty public constructor
    }

    public static AlcoholFragment newInstance(String param1, String param2) {
        AlcoholFragment fragment = new AlcoholFragment();
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
        drinkingLiveData = new DrinkingLiveData();
        shareViewModel = new SharedViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alcohol, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Initialize data
        drinkingInfo = new DrinkingInfo();

        // Setup listeners
        setupRadioGroupListeners();

        // Setup ViewModel observers
        setupViewModelObservers();

        // Load data
        loadData();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews(View view) {
        rdoDrinking = view.findViewById(R.id.rdoDrinking);
        rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);

        // Score display components
        tvAlcoholScore = view.findViewById(R.id.tvAlcoholScore);
        tvAlcoholRiskLevel = view.findViewById(R.id.tvAlcoholRiskLevel);
    }

    /**
     * Setup RadioGroup listeners
     */
    private void setupRadioGroupListeners() {
        // Main drinking selection
        rdoDrinking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDriking(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinking1) {
                    data = "1";
                    clearNestedSelections();
                } else if(checkedId == R.id.rdoDrinking2) {
                    data = "2";
                    clearNestedSelections();
                } else if(checkedId == R.id.rdoDrinking3) {
                    data = "3";
                }

                drinkingInfo.setDrinking(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }
            }
        });

        // Drinking frequency selection
        rdoDrinkingFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDrikingFrequency(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinkingFrequency1) {
                    data = "1";
                    clearAlwaySelection();
                } else if(checkedId == R.id.rdoDrinkingFrequency2) {
                    data = "2";
                    clearAlwaySelection();
                } else if(checkedId == R.id.rdoDrinkingFrequency3) {
                    data = "3";
                }

                drinkingInfo.setDrinkingFrequency(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }
            }
        });

        // Always drinking selection
        rdoDrinkingAlway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (isUpdatingFromScore) return; // Prevent loop

                drinkingLiveData.setSelectedRdoDrikingAlway(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);

                String data = "";
                if(checkedId == R.id.rdoDrinkingAlway1) {
                    data = "1";
                } else if(checkedId == R.id.rdoDrinkingAlway2) {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingAlway3) {
                    data = "3";
                }

                drinkingInfo.setDrinkingAlway(data);
                if (dataPasser != null) {
                    dataPasser.onDrinkingInfo(drinkingInfo);
                }
            }
        });
    }

    /**
     * Setup ViewModel observers
     */
    private void setupViewModelObservers() {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe drinking data changes
        viewModel.getDrinkingMutableLiveData().observe(getViewLifecycleOwner(), drinkingLiveData -> {
            if(drinkingLiveData != null && !isUpdatingFromScore){
                if(drinkingLiveData.getSelectedRdoDriking() != null) {
                    rdoDrinking.check(drinkingLiveData.getSelectedRdoDriking());
                }
                if(drinkingLiveData.getSelectedRdoDrikingFrequency() != null) {
                    rdoDrinkingFrequency.check(drinkingLiveData.getSelectedRdoDrikingFrequency());
                }
                if(drinkingLiveData.getSelectedRdoDrikingAlway() != null) {
                    rdoDrinkingAlway.check(drinkingLiveData.getSelectedRdoDrikingAlway());
                }
            }
        });

        // Observe person info for score calculation and synchronization
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
            if (personInfo != null && personInfo.getId() != null) {
                // คำนวณคะแนนแอลกอฮอล์และซิงค์กับ AssistScoreFragment
                calculateAndSyncAlcoholScore(personInfo.getId());
            }
        });

        // Observe ASSIST score changes (รับค่าจาก AssistScoreFragment)
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null && !isUpdatingFromScore) {
                // Display alcohol score from AssistScoreFragment (tvScoreB)
                if (data.getAlcoholScore() != null) {
                    try {
                        int alcoholScore = Integer.parseInt(data.getAlcoholScore());
                        // แสดงคะแนนจาก AssistScoreFragment ใน tvAlcoholScore
                        syncAlcoholScoreFromAssistFragment(alcoholScore);
                        Log.d(TAG, "ซิงค์คะแนนแอลกอฮอล์จาก AssistScoreFragment: " + alcoholScore);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "ไม่สามารถแปลงคะแนน alcohol เป็นตัวเลขได้: " + data.getAlcoholScore());
                        syncAlcoholScoreFromAssistFragment(0);
                    }
                }

                // Display nicotine score if available
                if (data.getNicotineScore() != null) {
                    try {
                        int nicotineScore = Integer.parseInt(data.getNicotineScore());
                        Log.d(TAG, "คะแนนบุหรี่: " + nicotineScore);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "ไม่สามารถแปลงคะแนนบุหรี่เป็นตัวเลขได้: " + data.getNicotineScore());
                    }
                }
            }
        });
    }

    /**
     * Load drinking data from database
     */
    private void loadData(){
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if(data.getId() != null){
                List<DrinkingInfo> drinkingInfos = sfDrinkingInfoDao.getByPersonId(Integer.valueOf(data.getId()));
                for(DrinkingInfo drinkingInfo : drinkingInfos){
                    Log.d(TAG, "drinking infos: " + drinkingInfo);
                    this.drinkingInfo = drinkingInfo;
                    loadExistingData();
                }

                // Load alcohol score
                loadAlcoholScore(data.getId());
            }
        });
    }

    /**
     * Load and display alcohol score from ASSIST (Fixed to prevent infinite loop)
     */
    private void loadAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณคะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Calculate total score for alcohol (substance "b")
            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                if (summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                    int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                    totalScore += questionScore;
                    Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                } else {
                    Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                }
            }

            // Update score display
            updateAlcoholScoreDisplay(totalScore);

            // Update ViewModel safely (prevent loop)
            updateAlcoholScoreInViewModelSafe(totalScore, personInfoId);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม: " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Update alcohol score and risk level
     * @param score Alcohol score from ASSIST
     */
    private void updateAlcoholScore(int score) {
        // Display score in TextView with color
        if (tvAlcoholScore != null) {
            tvAlcoholScore.setText(String.valueOf(score));

            // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
            if (score == 0) {
                // ไม่ดื่ม - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 1 && score <= 10) {
                // ความเสี่ยงต่ำ - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 11 && score <= 26) {
                // ความเสี่ยงปานกลาง - สีส้ม
                tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 27) {
                // ความเสี่ยงสูง - สีแดง
                tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#700e03"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            }

            Log.d(TAG, "อัพเดตคะแนนแอลกอฮอล์ใน UI: " + score);
        }

        // Update risk level with dynamic colors
        if (tvAlcoholRiskLevel != null) {
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์
            if (score == 0) {
                riskLevel = "ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 1 && score <= 10) {
                riskLevel = "ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 11 && score <= 26) {
                riskLevel = "บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 27) {
                riskLevel = "บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);
            Log.d(TAG, "อัพเดตระดับความเสี่ยง: " + riskLevel);
        }

        // Auto-select radio buttons based on score (only if no existing data)
        autoSelectBasedOnScore(score);
        Log.d(TAG, "เลือก radio button อัตโนมัติตามคะแนน: " + score);
    }

    /**
     * Calculate and sync alcohol score with AssistScoreFragment
     */
    private void calculateAndSyncAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณและซิงค์คะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Calculate total score for alcohol (substance "b")
            for (String question : questions) {
                try {
                    Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                            Integer.valueOf(personInfoId), question);

                    if (summaryMap != null && summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                        int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                        totalScore += questionScore;
                        Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                    } else {
                        Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนน " + question + ": " + e.getMessage());
                }
            }

            // อัพเดต UI ใน AlcoholFragment
            updateAlcoholScore(totalScore);

            // ส่งคะแนนไปยัง ViewModel เพื่อให้ AssistScoreFragment ใช้แสดงใน tvScoreB
            updateAlcoholScoreInViewModel(totalScore);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม (ซิงค์): " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการคำนวณและซิงค์คะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Sync alcohol score from AssistScoreFragment (tvScoreB) to tvAlcoholScore
     */
    private void syncAlcoholScoreFromAssistFragment(int score) {
        // แสดงคะแนนจาก AssistScoreFragment ใน tvAlcoholScore
        if (tvAlcoholScore != null) {
            String currentScore = tvAlcoholScore.getText().toString();
            if (!currentScore.equals(String.valueOf(score))) {
                tvAlcoholScore.setText(String.valueOf(score));

                // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
                if (score == 0) {
                    // ไม่ดื่ม - สีเขียว
                    tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 1 && score <= 10) {
                    // ความเสี่ยงต่ำ - สีเขียว
                    tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 11 && score <= 26) {
                    // ความเสี่ยงปานกลาง - สีส้ม
                    tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                } else if (score >= 27) {
                    // ความเสี่ยงสูง - สีแดง
                    tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                    tvAlcoholScore.setTextColor(Color.WHITE);
                }

                Log.d(TAG, "ซิงค์คะแนนจาก AssistScoreFragment ใน tvAlcoholScore: " + score);
            }
        }

        // อัพเดตระดับความเสี่ยงและสีฟอนต์
        if (tvAlcoholRiskLevel != null) {
            String riskLevel;

            // กำหนดระดับความเสี่ยงตามคะแนนและเปลี่ยนสีฟอนต์
            if (score == 0) {
                riskLevel = "ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 1 && score <= 10) {
                riskLevel = "ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
            } else if (score >= 11 && score <= 26) {
                riskLevel = "บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
            } else if (score >= 27) {
                riskLevel = "บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
            } else {
                riskLevel = "ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);
            Log.d(TAG, "อัพเดตระดับความเสี่ยงจากการซิงค์: " + riskLevel);
        }

        // เลือก radio button อัตโนมัติตามคะแนน (เฉพาะกรณีที่ยังไม่มีข้อมูลเดิม)
        autoSelectBasedOnScore(score);
        Log.d(TAG, "เลือก radio button อัตโนมัติจากการซิงค์: " + score);
    }
    /**
     * Calculate and update alcohol score directly from database (เก็บไว้เผื่อใช้งานอื่น)
     */
    private void calculateAndUpdateAlcoholScore(String personInfoId) {
        try {
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            Log.d(TAG, "คำนวณคะแนนแอลกอฮอล์สำหรับ person ID: " + personInfoId);

            // Get score from each question for alcohol
            for (String question : questions) {
                try {
                    Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                            Integer.valueOf(personInfoId), question);

                    if (summaryMap != null && summaryMap.containsKey(SUBSTANCE_ALCOHOL)) {
                        int questionScore = summaryMap.get(SUBSTANCE_ALCOHOL);
                        totalScore += questionScore;
                        Log.d(TAG, question + " แอลกอฮอล์: " + questionScore);
                    } else {
                        Log.d(TAG, question + " แอลกอฮอล์: 0 (ไม่มีข้อมูล)");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "เกิดข้อผิดพลาดในการดึงคะแนน " + question + ": " + e.getMessage());
                }
            }

            // Update UI
            updateAlcoholScore(totalScore);

            // Update ViewModel to sync with AssistScoreFragment
            updateAlcoholScoreInViewModel(totalScore);

            Log.d(TAG, "คะแนนแอลกอฮอล์รวม: " + totalScore);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการคำนวณคะแนนแอลกอฮอล์: " + e.getMessage());
            displayScoreError();
        }
    }

    /**
     * Update alcohol score in ViewModel (ส่งข้อมูลไป AssistScoreFragment)
     */
    private void updateAlcoholScoreInViewModel(int score) {
        try {
            // เพื่อป้องกัน infinite loop ให้ใช้ flag
            isUpdatingFromScore = true;

            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            // สร้าง AssistScore object ใหม่หรือใช้ที่มีอยู่
            AssistScore assistScore = new AssistScore();

            // ตั้งค่าข้อมูลพื้นฐาน
            viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), personInfo -> {
                if (personInfo != null && personInfo.getId() != null) {
                    assistScore.setPersonId(personInfo.getId());
                    assistScore.setAlcoholScore(String.valueOf(score));

                    // รักษาคะแนนบุหรี่ที่มีอยู่แล้ว (ถ้ามี)
                    AssistScore currentScore = viewModel.getAssistScoreMutableLiveData().getValue();
                    if (currentScore != null && currentScore.getNicotineScore() != null) {
                        assistScore.setNicotineScore(currentScore.getNicotineScore());
                    }

                    // อัพเดตใน ViewModel
                    viewModel.setAssistScoreMutableLiveData(assistScore);

                    Log.d(TAG, "ส่งคะแนนแอลกอฮอล์ไป AssistScoreFragment ผ่าน ViewModel: " + score);

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

    /**
     * Update alcohol score in ViewModel safely (prevent infinite loop)
     */
    private void updateAlcoholScoreInViewModelSafe(int score, String personId) {
        try {
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            AssistScore assistScore = new AssistScore();
            assistScore.setPersonId(personId);
            assistScore.setAlcoholScore(String.valueOf(score));

            viewModel.setAssistScoreMutableLiveData(assistScore);

            Log.d(TAG, "อัพเดตคะแนนแอลกอฮอล์ใน ViewModel (Safe): " + score);

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการอัพเดต ViewModel (Safe): " + e.getMessage());
        }
    }

    /**
     * Update score display and risk level (improved to prevent loop)
     */
    private void updateAlcoholScoreDisplay(int score) {
        // Check if score changed to prevent duplicate updates
        if (tvAlcoholScore != null) {
            String currentScore = tvAlcoholScore.getText().toString();
            if (currentScore.equals(String.valueOf(score))) {
                Log.d(TAG, "คะแนนไม่เปลี่ยนแปลง: " + score);
                return;
            }
            tvAlcoholScore.setText(String.valueOf(score));

            // เปลี่ยนสีพื้นหลังตามช่วงคะแนน
            if (score == 0) {
                // ไม่ดื่ม - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 1 && score <= 10) {
                // ความเสี่ยงต่ำ - สีเขียว
                tvAlcoholScore.setBackground(createGradientDrawable("#27AE60", "#2ECC71"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 11 && score <= 26) {
                // ความเสี่ยงปานกลาง - สีส้ม
                tvAlcoholScore.setBackground(createGradientDrawable("#F39C12", "#E67E22"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            } else if (score >= 27) {
                // ความเสี่ยงสูง - สีแดง
                tvAlcoholScore.setBackground(createGradientDrawable("#E74C3C", "#C0392B"));
                tvAlcoholScore.setTextColor(Color.WHITE);
            }
        }

        if (tvAlcoholRiskLevel != null) {
            String riskLevel;

            // Determine risk level และเปลี่ยนสีฟอนต์
            if (score == 0) {
                riskLevel = "ไม่ดื่ม";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
                // เลือก radio button สำหรับคะแนน 0
                selectDrinkingRadioButtonIfNeeded(0);
            } else if (score >= 1 && score <= 10) {
                riskLevel = "ไม่ต้องบำบัด";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_green);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_green));
                // เลือก radio button สำหรับคะแนน 1-10
                selectDrinkingRadioButtonIfNeeded(1);
            } else if (score >= 11 && score <= 26) {
                riskLevel = "บำบัดอย่างย่อ";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_yellow);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_yellow));
                // เลือก radio button สำหรับคะแนน 11-26
                selectDrinkingRadioButtonIfNeeded(2);
            } else if (score >= 27) {
                riskLevel = "บำบัดเข้มข้น";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_red);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.dark_red));
                // เลือก radio button สำหรับคะแนน 27+
                selectDrinkingRadioButtonIfNeeded(3);
            } else {
                riskLevel = "ยังไม่ได้ประเมิน";
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            tvAlcoholRiskLevel.setText(riskLevel);

            Log.d(TAG, "อัพเดตระดับความเสี่ยงและเลือก RadioButton: " + riskLevel + " (คะแนน: " + score + ")");
        }
    }
    /**
     * Select radio button only when no existing data (prevent loop)
     */
    private void selectDrinkingRadioButtonIfNeeded(int drinkingLevel) {
        boolean hasExistingData = false;

        // ตรวจสอบว่ามีข้อมูลเดิมหรือไม่
        if (drinkingInfo != null && drinkingInfo.getDrinking() != null &&
                !drinkingInfo.getDrinking().equals("0") && !drinkingInfo.getDrinking().isEmpty()) {
            hasExistingData = true;
            Log.d(TAG, "มีข้อมูลการดื่มอยู่แล้ว: " + drinkingInfo.getDrinking());
        }

        if (rdoDrinking != null && rdoDrinking.getCheckedRadioButtonId() != -1) {
            hasExistingData = true;
            Log.d(TAG, "มี RadioButton เลือกอยู่แล้ว: " + rdoDrinking.getCheckedRadioButtonId());
        }

        // ถ้าไม่มีข้อมูลเดิม ให้เลือกอัตโนมัติตามคะแนน
        if (!hasExistingData) {
            selectDrinkingRadioButton(drinkingLevel);
            Log.d(TAG, "เลือก RadioButton อัตโนมัติตามคะแนน: " + drinkingLevel);
        } else {
            Log.d(TAG, "ไม่เลือก RadioButton เพราะมีข้อมูลอยู่แล้ว");
        }
    }
    /**
     * Select radio button based on drinking level (fixed to prevent loop)
     */
    private void selectDrinkingRadioButton(int drinkingLevel) {
        try {
            isUpdatingFromScore = true; // Prevent listener triggers

            switch (drinkingLevel) {
                case 0: // Score 0 = No drinking
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking1); // ไม่ดื่ม
                        if (rdoDrinkingFrequency != null) {
                            rdoDrinkingFrequency.clearCheck();
                        }
                        if (rdoDrinkingAlway != null) {
                            rdoDrinkingAlway.clearCheck();
                        }
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("1");
                        drinkingInfo.setDrinkingFrequency("0");
                        drinkingInfo.setDrinkingAlway("0");
                    }
                    Log.d(TAG, "เลือก: ไม่ดื่ม (คะแนน 0)");
                    break;

                case 1: // Score 1-10 = Drink + Rarely + ไม่ต้องบำบัด
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency1); // นาน ๆ ครั้ง
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway1); // ไม่ต้องบำบัด
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("1");
                        drinkingInfo.setDrinkingAlway("1");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + นาน ๆ ครั้ง + ไม่ต้องบำบัด (คะแนน 1-10)");
                    break;

                case 2: // Score 11-26 = Drink + Sometimes + บำบัดอย่างย่อ
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency2); // เป็นครั้งคราว
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway2); // บำบัดอย่างย่อ
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("2");
                        drinkingInfo.setDrinkingAlway("2");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + เป็นครั้งคราว + บำบัดอย่างย่อ (คะแนน 11-26)");
                    break;

                case 3: // Score 27+ = Drink + Always + บำบัดเข้มข้น
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency3); // เป็นประจำ
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway3); // บำบัดเข้มข้น
                    }
                    if (drinkingInfo != null) {
                        drinkingInfo.setDrinking("3");
                        drinkingInfo.setDrinkingFrequency("3");
                        drinkingInfo.setDrinkingAlway("3");
                    }
                    Log.d(TAG, "เลือก: ดื่ม + เป็นประจำ + บำบัดเข้มข้น (คะแนน 27+)");
                    break;

                default:
                    clearAllSelections();
                    break;
            }

            // Send data through dataPasser
            if (dataPasser != null && drinkingInfo != null) {
                dataPasser.onDrinkingInfo(drinkingInfo);
            }

        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการเลือก Radio Button การดื่ม: " + e.getMessage());
        } finally {
            isUpdatingFromScore = false; // Re-enable listeners
        }
    }

    /**
     * Helper methods for assessment and UI updates
     */
    private RiskAssessment assessRisk(int score) {
        if (score == 0) {
            return new RiskAssessment("ไม่ดื่ม", R.color.light_green);
        } else if (score >= 1 && score <= 10) {
            return new RiskAssessment("ไม่ต้องบำบัด", R.color.light_green);
        } else if (score >= 11 && score <= 26) {
            return new RiskAssessment("บำบัดอย่างย่อ", R.color.light_yellow);
        } else if (score >= 27) {
            return new RiskAssessment("บำบัดเข้มข้น", R.color.light_red);
        } else {
            return new RiskAssessment("ยังไม่ได้ประเมิน", android.R.color.transparent);
        }
    }

    private void updateScoreColors(RiskAssessment risk) {
        if (tvAlcoholRiskLevel != null && tvAlcoholScore != null) {
            if (risk.backgroundColor != android.R.color.transparent) {
                tvAlcoholRiskLevel.setBackgroundResource(risk.backgroundColor);
//                tvAlcoholScore.setBackgroundResource(risk.backgroundColor);
            }
        }
    }

    private boolean shouldAutoSelectBasedOnScore() {
        return drinkingInfo == null ||
                drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().equals("0") ||
                drinkingInfo.getDrinking().isEmpty();
    }

    private void autoSelectBasedOnScore(int score) {
        Log.d(TAG, "autoSelectBasedOnScore: คะแนน = " + score);

        if (score == 0) {
            selectDrinkingRadioButton(0); // ไม่ดื่ม
        } else if (score >= 1 && score <= 10) {
            selectDrinkingRadioButton(1); // ดื่ม + นาน ๆ ครั้ง + ไม่ต้องบำบัด
        } else if (score >= 11 && score <= 26) {
            selectDrinkingRadioButton(2); // ดื่ม + ครั้งคราว + บำบัดอย่างย่อ
        } else if (score >= 27) {
            selectDrinkingRadioButton(3); // ดื่ม + เป็นประจำ + บำบัดเข้มข้น
        }
    }

    private void displayScoreError() {
        if (tvAlcoholScore != null) {
            tvAlcoholScore.setText("-");
            tvAlcoholScore.setBackgroundResource(R.color.light_gray);
            tvAlcoholScore.setTextColor(getResources().getColor(R.color.darker_gray));
        }
        if (tvAlcoholRiskLevel != null) {
            tvAlcoholRiskLevel.setText("ไม่สามารถคำนวณได้");
            tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
        }
    }

    /**
     * Clear selections helper methods
     */
    private void clearNestedSelections() {
        if (rdoDrinkingFrequency != null) {
            rdoDrinkingFrequency.clearCheck();
        }
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinkingFrequency("0");
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    private void clearAlwaySelection() {
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    private void clearAllSelections() {
        if (rdoDrinking != null) {
            rdoDrinking.clearCheck();
        }
        if (rdoDrinkingFrequency != null) {
            rdoDrinkingFrequency.clearCheck();
        }
        if (rdoDrinkingAlway != null) {
            rdoDrinkingAlway.clearCheck();
        }
        if (drinkingInfo != null) {
            drinkingInfo.setDrinking("0");
            drinkingInfo.setDrinkingFrequency("0");
            drinkingInfo.setDrinkingAlway("0");
        }
    }

    /**
     * Select existing radio button based on risk level
     * @param level 1=ไม่ต้องบำบัด, 2=บำบัดอย่างย่อ, 3=บำบัดเข้มข้น, 0=ไม่เลือก
     */
    private void selectExistingRadioButton(int level) {
        View rootView = getView();
        if (rootView != null) {
            RadioButton rdoTreatmentLow = null;
            RadioButton rdoTreatmentMedium = null;
            RadioButton rdoTreatmentHigh = null;
            RadioGroup rdoTreatmentGroup = null;

            try {
                // Find RadioButton by text content
                rdoTreatmentLow = findRadioButtonByText(rootView, "1B602");
                rdoTreatmentMedium = findRadioButtonByText(rootView, "1B603");
                rdoTreatmentHigh = findRadioButtonByText(rootView, "1B604");

                // Find RadioGroup containing these RadioButtons
                if (rdoTreatmentLow != null) {
                    ViewGroup parent = (ViewGroup) rdoTreatmentLow.getParent();
                    if (parent instanceof RadioGroup) {
                        rdoTreatmentGroup = (RadioGroup) parent;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "ไม่สามารถหา RadioButton ได้: " + e.getMessage());
            }

            // Select RadioButton based on score
            String selectedLevel = "";
            RadioButton targetRadioButton = null;

            switch (level) {
                case 1: // 0-10 score
                    selectedLevel = "1B602 ระดับเสี่ยงต่ำ(0-10) >>> 1B610 การให้คำแนะนำ (brief advice)";
                    targetRadioButton = rdoTreatmentLow;
                    break;
                case 2: // 11-26 score
                    selectedLevel = "1B603 ระดับเสี่ยงปานกลาง(11-26) >>> 1B611 การให้คำปรึกษาแบบสั้น (brief counseling)";
                    targetRadioButton = rdoTreatmentMedium;
                    break;
                case 3: // 27+ score
                    selectedLevel = "1B604 ระดับเสี่ยงสูง(คะแนนตั้งแต่ 27 ขึ้นไป) >>> 1B612 การส่งต่อเพื่อรับการประเมินและการบำบัดโดยผู้เชี่ยวชาญ(refer)";
                    targetRadioButton = rdoTreatmentHigh;
                    break;
                default:
                    selectedLevel = "ยังไม่ได้ประเมิน";
                    break;
            }

            Log.d(TAG, "ระดับการบำบัดที่แนะนำ: " + selectedLevel);

            // Select RadioButton
            if (targetRadioButton != null && rdoTreatmentGroup != null) {
                rdoTreatmentGroup.check(targetRadioButton.getId());
                Log.d(TAG, "เลือก RadioButton สำเร็จ");
            } else {
                Log.w(TAG, "ไม่พบ RadioButton ที่ต้องการเลือก");
            }
        }
    }

    /**
     * Find RadioButton by text content
     */
    private RadioButton findRadioButtonByText(View rootView, String searchText) {
        if (rootView instanceof RadioButton) {
            RadioButton radioButton = (RadioButton) rootView;
            String text = radioButton.getText().toString();
            if (text.contains(searchText)) {
                return radioButton;
            }
        } else if (rootView instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) rootView;
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton found = findRadioButtonByText(group.getChildAt(i), searchText);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Load existing data from DrinkingInfo
     */
    private void loadExistingData() {
        if (this.drinkingInfo == null) return;

        isUpdatingFromScore = true; // Prevent listeners during data loading

        try {
            // Set drinking selection
            switch (drinkingInfo.getDrinking()) {
                case "1":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking1)).setChecked(true);
                    }
                    break;
                case "2":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking2)).setChecked(true);
                    }
                    break;
                case "3":
                    if (rdoDrinking != null) {
                        ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking3)).setChecked(true);
                    }
                    // Load nested selections only if "3" is selected
                    if (drinkingInfo.getDrinkingFrequency() != null) {
                        switch (drinkingInfo.getDrinkingFrequency()) {
                            case "1":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency1)).setChecked(true);
                                }
                                break;
                            case "2":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency2)).setChecked(true);
                                }
                                break;
                            case "3":
                                if (rdoDrinkingFrequency != null) {
                                    ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency3)).setChecked(true);
                                }
                                // Load "always" selection only if frequency is "3"
                                if (drinkingInfo.getDrinkingAlway() != null) {
                                    switch (drinkingInfo.getDrinkingAlway()) {
                                        case "1":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway1)).setChecked(true);
                                            }
                                            break;
                                        case "2":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway2)).setChecked(true);
                                            }
                                            break;
                                        case "3":
                                            if (rdoDrinkingAlway != null) {
                                                ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway3)).setChecked(true);
                                            }
                                            break;
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการโหลดข้อมูลเดิม: " + e.getMessage());
        } finally {
            isUpdatingFromScore = false; // Re-enable listeners
        }
    }

    /**
     * Set drinking info from external source
     */
    public void setDrinkingInfo(DrinkingInfo info) {
        this.drinkingInfo = info;
        loadExistingData();
    }

    /**
     * Risk assessment helper class
     */
    private static class RiskAssessment {
        String description;
        int backgroundColor;

        RiskAssessment(String description, int backgroundColor) {
            this.description = description;
            this.backgroundColor = backgroundColor;
        }
    }

    /**
     * Inner class for StressDepression9q LiveData Model
     */
    public static class StressDepression9qLiveDataModel extends ViewModel {
        StressDepression9qLiveData stressDepression9qLiveData;

        public StressDepression9qLiveData getStressDepression9qLiveData() {
            return stressDepression9qLiveData;
        }

        public void setStressDepression9qLiveData(StressDepression9qLiveData stressDepression9qLiveData) {
            this.stressDepression9qLiveData = stressDepression9qLiveData;
        }
    }
    // เพิ่มเมธอด validation ใน AlcoholFragment class

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่
     */
    public boolean isFormComplete() {
        // ตรวจสอบว่าได้เลือกสถานะการดื่มแล้วหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            return false;
        }

        // ถ้าเลือก "ดื่ม" (option 3) ต้องเลือกความถี่ด้วย
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                return false;
            }

            // ถ้าเลือก "ดื่มเป็นประจำ" (option 3 ใน frequency) ต้องเลือกการบำบัดด้วย
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
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

        // ตรวจสอบว่าได้เลือกสถานะการดื่มหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกสถานะการดื่มสุรา");
            return message.toString();
        }

        // ถ้าเลือก "ดื่ม" แต่ยังไม่ได้เลือกความถี่
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกความถี่ในการดื่ม");
                return message.toString();
            }

            // ถ้าเลือก "ดื่มเป็นประจำ" แต่ยังไม่ได้เลือกการบำบัด
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    message.append("แบบประเมินการดื่มสุรา: ยังไม่ได้เลือกการให้คำแนะนำ/บำบัด");
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

        // ตรวจสอบว่าได้เลือกสถานะการดื่มหรือไม่
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            message.append("แบบประเมินการดื่มสุรา:\n");
            message.append("• ยังไม่ได้เลือกสถานะการดื่มสุรา (ไม่ดื่ม/เคยดื่ม/ดื่ม)");
            return message.toString();
        }

        // ตรวจสอบการเลือกความถี่ (สำหรับผู้ที่ดื่ม)
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                message.append("แบบประเมินการดื่มสุรา:\n");
                message.append("• เลือกว่า 'ดื่ม' แล้ว\n");
                message.append("• ยังไม่ได้เลือกความถี่ในการดื่ม (นานๆครั้ง/ครั้งคราว/เป็นประจำ)");
                return message.toString();
            }

            // ตรวจสอบการให้คำแนะนำ/บำบัด (สำหรับผู้ที่ดื่มเป็นประจำ)
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    message.append("แบบประเมินการดื่มสุรา:\n");
                    message.append("• เลือกว่า 'ดื่ม' แล้ว\n");
                    message.append("• เลือกว่า 'ดื่มเป็นประจำ' แล้ว\n");
                    message.append("• ยังไม่ได้เลือกการให้คำแนะนำ/บำบัด");
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
        isUpdatingFromScore = true;
        try {
            // ล้างการเลือกทั้งหมด
            if (rdoDrinking != null) {
                rdoDrinking.clearCheck();
            }
            if (rdoDrinkingFrequency != null) {
                rdoDrinkingFrequency.clearCheck();
            }
            if (rdoDrinkingAlway != null) {
                rdoDrinkingAlway.clearCheck();
            }

            // รีเซ็ต drinkingInfo
            drinkingInfo = new DrinkingInfo();

            // รีเซ็ตคะแนนและระดับความเสี่ยง
            if (tvAlcoholScore != null) {
                tvAlcoholScore.setText("-");
                tvAlcoholScore.setBackgroundResource(R.color.light_gray);
                tvAlcoholScore.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            if (tvAlcoholRiskLevel != null) {
                tvAlcoholRiskLevel.setText("ยังไม่ได้ประเมิน");
                tvAlcoholRiskLevel.setBackgroundResource(R.color.light_gray);
                tvAlcoholRiskLevel.setTextColor(getResources().getColor(R.color.darker_gray));
            }

            // ล้างข้อมูลใน LiveData
            if (drinkingLiveData != null) {
                drinkingLiveData.setSelectedRdoDriking(null);
                drinkingLiveData.setSelectedRdoDrikingFrequency(null);
                drinkingLiveData.setSelectedRdoDrikingAlway(null);
            }
        } finally {
            isUpdatingFromScore = false;
        }
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (drinkingInfo == null) {
            return false;
        }

        return (drinkingInfo.getDrinking() != null && !drinkingInfo.getDrinking().isEmpty() && !drinkingInfo.getDrinking().equals("0")) ||
                (drinkingInfo.getDrinkingFrequency() != null && !drinkingInfo.getDrinkingFrequency().isEmpty() && !drinkingInfo.getDrinkingFrequency().equals("0")) ||
                (drinkingInfo.getDrinkingAlway() != null && !drinkingInfo.getDrinkingAlway().isEmpty() && !drinkingInfo.getDrinkingAlway().equals("0"));
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (drinkingInfo == null || drinkingInfo.getDrinking() == null ||
                drinkingInfo.getDrinking().isEmpty() || drinkingInfo.getDrinking().equals("0")) {
            return 0;
        }

        // หากเลือก "ไม่ดื่ม" หรือ "เคยดื่ม" ถือว่าครบ 100%
        if ("1".equals(drinkingInfo.getDrinking()) || "2".equals(drinkingInfo.getDrinking())) {
            return 100;
        }

        // หากเลือก "ดื่ม" ต้องตรวจสอบขั้นตอนต่อไป
        if ("3".equals(drinkingInfo.getDrinking())) {
            if (drinkingInfo.getDrinkingFrequency() == null ||
                    drinkingInfo.getDrinkingFrequency().isEmpty() ||
                    drinkingInfo.getDrinkingFrequency().equals("0")) {
                return 33; // กรอก 1/3
            }

            // หากเลือก "นานๆครั้ง" หรือ "ครั้งคราว" ถือว่าครบ 100%
            if ("1".equals(drinkingInfo.getDrinkingFrequency()) || "2".equals(drinkingInfo.getDrinkingFrequency())) {
                return 100;
            }

            // หากเลือก "เป็นประจำ" ต้องเลือกการบำบัดด้วย
            if ("3".equals(drinkingInfo.getDrinkingFrequency())) {
                if (drinkingInfo.getDrinkingAlway() == null ||
                        drinkingInfo.getDrinkingAlway().isEmpty() ||
                        drinkingInfo.getDrinkingAlway().equals("0")) {
                    return 66; // กรอก 2/3
                } else {
                    return 100; // กรอกครบทุกขั้นตอน
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

        Log.d(TAG, "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ดึงข้อมูลที่กรอกแล้ว
     */
    public DrinkingInfo getFormData() {
        return this.drinkingInfo;
    }

    /**
     * ตรวจสอบระดับความเสี่ยงจากคะแนน
     */
    public String getRiskLevelFromScore() {
        if (tvAlcoholScore == null || tvAlcoholScore.getText().toString().equals("-")) {
            return "ยังไม่ได้ประเมิน";
        }

        try {
            int score = Integer.parseInt(tvAlcoholScore.getText().toString());

            if (score == 0) {
                return "ไม่ดื่ม";
            } else if (score >= 1 && score <= 10) {
                return "ไม่ต้องบำบัด (ความเสี่ยงต่ำ)";
            } else if (score >= 11 && score <= 26) {
                return "บำบัดอย่างย่อ (ความเสี่ยงปานกลาง)";
            } else if (score >= 27) {
                return "บำบัดเข้มข้น (ความเสี่ยงสูง)";
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "ไม่สามารถแปลงคะแนนเป็นตัวเลขได้");
        }

        return "ยังไม่ได้ประเมิน";
    }

    /**
     * ตรวจสอบว่าข้อมูลที่กรอกสอดคล้องกับคะแนนหรือไม่
     */
    public boolean isDataConsistentWithScore() {
        if (tvAlcoholScore == null || tvAlcoholScore.getText().toString().equals("-")) {
            return true; // ถ้ายังไม่มีคะแนน ถือว่าสอดคล้อง
        }

        try {
            int score = Integer.parseInt(tvAlcoholScore.getText().toString());

            // ตรวจสอบความสอดคล้อง
            if (score == 0 && !"1".equals(drinkingInfo.getDrinking())) {
                Log.w(TAG, "คะแนน 0 แต่ไม่ได้เลือก 'ไม่ดื่ม'");
                return false;
            }

            if (score > 0 && "1".equals(drinkingInfo.getDrinking())) {
                Log.w(TAG, "มีคะแนนแต่เลือก 'ไม่ดื่ม'");
                return false;
            }

        } catch (NumberFormatException e) {
            Log.e(TAG, "ไม่สามารถตรวจสอบความสอดคล้องได้");
        }

        return true;
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
//    private android.graphics.drawable.GradientDrawable createGradientDrawable(String startColor, String endColor) {
//        android.graphics.drawable.GradientDrawable gradient = new android.graphics.drawable.GradientDrawable(
//                android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT,
//                new int[]{
//                        Color.parseColor(startColor),
//                        Color.parseColor(endColor)
//                }
//        );
//
//        // ตั้งค่ามุมโค้ง
//        gradient.setCornerRadius(20f);
//
//        return gradient;
//    }
}