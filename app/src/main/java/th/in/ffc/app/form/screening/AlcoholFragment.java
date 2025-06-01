package th.in.ffc.app.form.screening;

import android.content.Context;
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
import java.util.HashMap;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlcoholFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlcoholFragment extends Fragment {

    DrinkingLiveData drinkingLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private DrinkingInfo drinkingInfo;
    private RadioGroup rdoDrinking;
    private RadioGroup rdoDrinkingFrequency;
    private RadioGroup rdoDrinkingAlway;
    RadioButton rdoDrinkingFrequency1;
    RadioButton rdoDrinkingFrequency2;
    RadioButton rdoDrinkingFrequency3;

    // เพิ่มตัวแปรสำหรับแสดงคะแนน
    private TextView tvAlcoholScore;
    private TextView tvAlcoholRiskLevel;

    public AlcoholFragment() {
        // Required empty public constructor
    }

    private void clearNestedSelections() {
        rdoDrinkingFrequency.clearCheck();
        rdoDrinkingAlway.clearCheck();
        drinkingInfo.setDrinkingFrequency("0");
        drinkingInfo.setDrinkingAlway("0");
    }

    private void clearAlwaySelection() {
        rdoDrinkingAlway.clearCheck();
        drinkingInfo.setDrinkingAlway("0");
    }

    private void initializeViews(View view) {
        rdoDrinking = view.findViewById(R.id.rdoDrinking);
        rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);

        // เพิ่มการเชื่อมโยง TextView สำหรับแสดงคะแนน
        tvAlcoholScore = view.findViewById(R.id.tvAlcoholScore);
        tvAlcoholRiskLevel = view.findViewById(R.id.tvAlcoholRiskLevel);

        // Initially disable nested groups
        //  rdoDrinkingFrequency.setVisibility(View.GONE);
        //  rdoDrinkingAlway.setVisibility(View.GONE);
    }

    public void setDrinkingInfo(DrinkingInfo info) {
        this.drinkingInfo = info;
        loadExistingData();
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

    public static AlcoholFragment newInstance(String param1, String param2) {
        AlcoholFragment fragment = new AlcoholFragment();
        return fragment;
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

    private void loadData(){
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getId()!=null){
                List<DrinkingInfo> drinkingInfos = sfDrinkingInfoDao.getByPersonId(Integer.valueOf(data.getId()));
                for(DrinkingInfo drinkingInfo :drinkingInfos){
//                    setDataToViews(smokerInfo);
                    Log.d("alcohol ", "drinking infos:"+drinkingInfo);
                    this.drinkingInfo = drinkingInfo;
                    loadExistingData();
                }

                // โหลดคะแนนแอลกอฮอล์
                loadAlcoholScore(data.getId());
            }
        });
    }

    /**
     * โหลดและแสดงคะแนนแอลกอฮอล์จาก ASSIST
     */
    private void loadAlcoholScore(String personInfoId) {
        try {
            // คำนวณผลรวมของคำตอบจาก Q2 ถึง Q7 สำหรับแอลกอฮอล์ (substance "b")
            String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7"};
            int totalScore = 0;

            // ดึงข้อมูลจากแต่ละคำถามและรวมคะแนนสำหรับแอลกอฮอล์
            for (String question : questions) {
                Map<String, Integer> summaryMap = SfDrugsDao.getSummaryMapBySubquestion(
                        Integer.valueOf(personInfoId), question);

                // ดึงคะแนนของแอลกอฮอล์ (substance "b")
                if (summaryMap.containsKey("b")) {
                    totalScore += summaryMap.get("b");
                }
            }

            // แสดงคะแนนและระดับความเสี่ยง
            updateAlcoholScoreDisplay(totalScore);

            Log.d("AlcoholFragment", "คะแนนแอลกอฮอล์: " + totalScore);

        } catch (Exception e) {
            Log.e("AlcoholFragment", "เกิดข้อผิดพลาดในการดึงคะแนนแอลกอฮอล์: " + e.getMessage());
            // แสดงข้อความเมื่อไม่สามารถดึงข้อมูลได้
            if (tvAlcoholScore != null) {
                tvAlcoholScore.setText("-");
            }
            if (tvAlcoholRiskLevel != null) {
                tvAlcoholRiskLevel.setText("ไม่สามารถคำนวณได้");
            }
        }
    }

    /**
     * อัพเดตการแสดงผลคะแนนและระดับความเสี่ยงแอลกอฮอล์
     */
    private void updateAlcoholScoreDisplay(int score) {
        if (tvAlcoholScore != null) {
            tvAlcoholScore.setText(String.valueOf(score));
        }

        if (tvAlcoholRiskLevel != null) {
            String riskLevel;
            int backgroundColor;
            int textColor = android.R.color.black;

            // กำหนดระดับความเสี่ยงตามเกณฑ์แอลกอฮอล์และเลือก Radio Button ที่มีอยู่แล้ว
            if (score == 0) {
                riskLevel = "ไม่ดื่ม";
                backgroundColor = R.color.light_green;
                // เลือก Radio Button "ไม่ดื่ม"
                selectDrinkingRadioButton(0);
                selectExistingRadioButton(1);
            } else if (score >= 1 && score <= 10) {
                riskLevel = "ไม่ต้องบำบัด";
                backgroundColor = R.color.light_green;
                // เลือก Radio Button "ดื่ม" และ "นาน ๆ ครั้ง"
                selectDrinkingRadioButton(1);
                selectExistingRadioButton(1);
            } else if (score >= 11 && score <= 26) {
                riskLevel = "บำบัดอย่างย่อ";
                backgroundColor = R.color.light_yellow;
                // เลือก Radio Button "ดื่ม" และ "เป็นครั้งคราว"
                selectDrinkingRadioButton(2);
                selectExistingRadioButton(2);
            } else if (score >= 27) {
                riskLevel = "บำบัดเข้มข้น";
                backgroundColor = R.color.light_red;
                textColor = android.R.color.white;
                // เลือก Radio Button "ดื่ม" และ "เป็นประจำ"
                selectDrinkingRadioButton(3);
                selectExistingRadioButton(3);
            } else {
                riskLevel = "ยังไม่ได้ประเมิน";
                backgroundColor = android.R.color.transparent;
                selectDrinkingRadioButton(0);
                selectExistingRadioButton(0);
            }

            tvAlcoholRiskLevel.setText(riskLevel);

            // ตั้งค่าสีพื้นหลัง
            if (backgroundColor != android.R.color.transparent) {
                tvAlcoholRiskLevel.setBackgroundResource(backgroundColor);
                tvAlcoholScore.setBackgroundResource(backgroundColor);
            }
        }
    }

    /**
     * เลือก Radio Button สำหรับการดื่มสุราตามคะแนน
     * @param drinkingLevel 0=ไม่ดื่ม, 1=ดื่ม+นาน ๆ ครั้ง, 2=ดื่ม+เป็นครั้งคราว, 3=ดื่ม+เป็นประจำ
     */
    private void selectDrinkingRadioButton(int drinkingLevel) {
        try {
            switch (drinkingLevel) {
                case 0: // คะแนน 0 = ไม่ดื่ม
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking1); // ไม่ดื่ม
                        // ล้างการเลือกในส่วนความถี่
                        if (rdoDrinkingFrequency != null) {
                            rdoDrinkingFrequency.clearCheck();
                        }
                        if (rdoDrinkingAlway != null) {
                            rdoDrinkingAlway.clearCheck();
                        }
                    }
                    Log.d("AlcoholFragment", "เลือก: ไม่ดื่ม (คะแนน 0)");
                    break;

                case 1: // คะแนน 1-10 = ดื่ม + นาน ๆ ครั้ง
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency1); // นาน ๆ ครั้ง
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.clearCheck(); // ล้างการเลือกในส่วน "เป็นประจำ"
                    }
                    Log.d("AlcoholFragment", "เลือก: ดื่ม + นาน ๆ ครั้ง (คะแนน 1-10)");
                    break;

                case 2: // คะแนน 11-26 = ดื่ม + เป็นครั้งคราว
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency2); // เป็นครั้งคราว
                    }
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.clearCheck(); // ล้างการเลือกในส่วน "เป็นประจำ"
                    }
                    Log.d("AlcoholFragment", "เลือก: ดื่ม + เป็นครั้งคราว (คะแนน 11-26)");
                    break;

                case 3: // คะแนน 27+ = ดื่ม + เป็นประจำ
                    if (rdoDrinking != null) {
                        rdoDrinking.check(R.id.rdoDrinking3); // ดื่ม
                    }
                    if (rdoDrinkingFrequency != null) {
                        rdoDrinkingFrequency.check(R.id.rdoDrinkingFrequency3); // เป็นประจำ
                    }
                    // เลือกระดับการดื่มเป็นประจำ (ต้องเลือกอย่างใดอย่างหนึ่งใน rdoDrinkingAlway)
                    if (rdoDrinkingAlway != null) {
                        rdoDrinkingAlway.check(R.id.rdoDrinkingAlway2); // เลือกตัวกลาง
                    }
                    Log.d("AlcoholFragment", "เลือก: ดื่ม + เป็นประจำ (คะแนน 27+)");
                    break;

                default:
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
                    Log.d("AlcoholFragment", "ล้างการเลือกทั้งหมด");
                    break;
            }

            // อัพเดต drinkingInfo object
            updateDrinkingInfoFromSelection(drinkingLevel);

        } catch (Exception e) {
            Log.e("AlcoholFragment", "เกิดข้อผิดพลาดในการเลือก Radio Button การดื่ม: " + e.getMessage());
        }
    }

    /**
     * อัพเดต DrinkingInfo object ตามการเลือก
     */
    private void updateDrinkingInfoFromSelection(int drinkingLevel) {
        if (drinkingInfo != null) {
            switch (drinkingLevel) {
                case 0: // ไม่ดื่ม
                    drinkingInfo.setDrinking("1");
                    drinkingInfo.setDrinkingFrequency("0");
                    drinkingInfo.setDrinkingAlway("0");
                    break;
                case 1: // ดื่ม + นาน ๆ ครั้ง
                    drinkingInfo.setDrinking("3");
                    drinkingInfo.setDrinkingFrequency("1");
                    drinkingInfo.setDrinkingAlway("0");
                    break;
                case 2: // ดื่ม + เป็นครั้งคราว
                    drinkingInfo.setDrinking("3");
                    drinkingInfo.setDrinkingFrequency("2");
                    drinkingInfo.setDrinkingAlway("0");
                    break;
                case 3: // ดื่ม + เป็นประจำ
                    drinkingInfo.setDrinking("3");
                    drinkingInfo.setDrinkingFrequency("3");
                    drinkingInfo.setDrinkingAlway("2");
                    break;
                default:
                    drinkingInfo.setDrinking("0");
                    drinkingInfo.setDrinkingFrequency("0");
                    drinkingInfo.setDrinkingAlway("0");
                    break;
            }

            // ส่งข้อมูลผ่าน dataPasser
            if (dataPasser != null) {
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        }
    }

    /**
     * เลือก Radio Button ที่มีอยู่แล้วในหน้าจอตามคะแนน
     * ตาม Radio Button ที่แสดงในรูป:
     * - 1B602 ระดับเสี่ยงต่ำ(0-10) >>> 1B610 การให้คำแนะนำ (brief advice)
     * - 1B603 ระดับเสี่ยงปาน กลาง(11-26) >>> 1B611 การให้คำปรึกษาแบบสั้น (brief counseling)
     * - 1B604 ระดับเสี่ยงสูง(คะแนนตั้งแต่ 27 ขึ้นไป) >>> 1B612 การส่งต่อเพื่อรับการประเมินและการบำบัดโดยผู้เชี่ยวชาญ(refer)
     * @param level 1=ไม่ต้องบำบัด, 2=บำบัดอย่างย่อ, 3=บำบัดเข้มข้น, 0=ไม่เลือก
     */
    private void selectExistingRadioButton(int level) {
        // หา RadioGroup และ RadioButton ที่มีอยู่แล้วในหน้าจอ
        // ขึ้นอยู่กับว่า Radio Button เหล่านี้มี ID อะไร

        // สมมติว่า Radio Button มี ID ดังนี้ (ต้องเช็คจาก XML จริง):
        // R.id.rdoTreatmentLow    - สำหรับ 1B602/1B610 (0-10 คะแนน)
        // R.id.rdoTreatmentMedium - สำหรับ 1B603/1B611 (11-26 คะแนน)
        // R.id.rdoTreatmentHigh   - สำหรับ 1B604/1B612 (27+ คะแนน)

        View rootView = getView();
        if (rootView != null) {
            RadioButton rdoTreatmentLow = null;
            RadioButton rdoTreatmentMedium = null;
            RadioButton rdoTreatmentHigh = null;
            RadioGroup rdoTreatmentGroup = null;

            // ลองหา Radio Button จาก text ที่แสดง (ถ้าไม่มี ID ที่ชัดเจน)
            try {
                // วิธีหา RadioButton จาก text content
                rdoTreatmentLow = findRadioButtonByText(rootView, "1B602");
                rdoTreatmentMedium = findRadioButtonByText(rootView, "1B603");
                rdoTreatmentHigh = findRadioButtonByText(rootView, "1B604");

                // หา RadioGroup ที่บรรจุ RadioButton เหล่านี้
                if (rdoTreatmentLow != null) {
                    ViewGroup parent = (ViewGroup) rdoTreatmentLow.getParent();
                    if (parent instanceof RadioGroup) {
                        rdoTreatmentGroup = (RadioGroup) parent;
                    }
                }
            } catch (Exception e) {
                Log.e("AlcoholFragment", "ไม่สามารถหา RadioButton ได้: " + e.getMessage());
            }

            // เลือก RadioButton ตามคะแนน
            String selectedLevel = "";
            RadioButton targetRadioButton = null;

            switch (level) {
                case 1: // 0-10 คะแนน
                    selectedLevel = "1B602 ระดับเสี่ยงต่ำ(0-10) >>> 1B610 การให้คำแนะนำ (brief advice)";
                    targetRadioButton = rdoTreatmentLow;
                    break;
                case 2: // 11-26 คะแนน
                    selectedLevel = "1B603 ระดับเสี่ยงปานกลาง(11-26) >>> 1B611 การให้คำปรึกษาแบบสั้น (brief counseling)";
                    targetRadioButton = rdoTreatmentMedium;
                    break;
                case 3: // 27+ คะแนน
                    selectedLevel = "1B604 ระดับเสี่ยงสูง(คะแนนตั้งแต่ 27 ขึ้นไป) >>> 1B612 การส่งต่อเพื่อรับการประเมินและการบำบัดโดยผู้เชี่ยวชาญ(refer)";
                    targetRadioButton = rdoTreatmentHigh;
                    break;
                default:
                    selectedLevel = "ยังไม่ได้ประเมิน";
                    break;
            }

            Log.d("AlcoholFragment", "ระดับการบำบัดที่แนะนำ: " + selectedLevel);

            // เลือก RadioButton
            if (targetRadioButton != null && rdoTreatmentGroup != null) {
                rdoTreatmentGroup.check(targetRadioButton.getId());
                Log.d("AlcoholFragment", "เลือก RadioButton สำเร็จ");
            } else {
                Log.w("AlcoholFragment", "ไม่พบ RadioButton ที่ต้องการเลือก");
            }
        }
    }

    /**
     * หา RadioButton จาก text ที่มีอยู่
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

    private void loadExistingData() {
        if (this.drinkingInfo == null) return;

        // Set drinking selection
        switch (drinkingInfo.getDrinking()) {
            case "1":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking2)).setChecked(true);
                break;
            case "3":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking3)).setChecked(true);
                // Load nested selections only if "3" is selected
                if (drinkingInfo.getDrinkingFrequency() != null) {
                    switch (drinkingInfo.getDrinkingFrequency()) {
                        case "1":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency1)).setChecked(true);
                            break;
                        case "2":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency2)).setChecked(true);
                            break;
                        case "3":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency3)).setChecked(true);
                            // Load "always" selection only if frequency is "3"
                            if (drinkingInfo.getDrinkingAlway() != null) {
                                switch (drinkingInfo.getDrinkingAlway()) {
                                    case "1":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway1)).setChecked(true);
                                        break;
                                    case "2":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway2)).setChecked(true);
                                        break;
                                    case "3":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway3)).setChecked(true);
                                        break;
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioGroup rdoDrinking = view.findViewById(R.id.rdoDrinking);
        RadioGroup rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        RadioButton rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        RadioButton rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        RadioButton rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);
        RadioGroup rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        RadioButton rdoDrinkingAlway1 = view.findViewById(R.id.rdoDrinkingAlway1);
        RadioButton rdoDrinkingAlway2 = view.findViewById(R.id.rdoDrinkingAlway2);
        RadioButton rdoDrinkingAlway3 = view.findViewById(R.id.rdoDrinkingAlway3);
//        rdoDrinkingFrequency.setVisibility(View.INVISIBLE);
//        rdoDrinkingAlway.setVisibility(View.INVISIBLE);
        initializeViews(view);
        drinkingInfo = new DrinkingInfo();
        rdoDrinking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoDrinking3){
//                    rdoDrinkingFrequency.setVisibility(View.VISIBLE);
//                }
//                else {
//                    rdoDrinkingFrequency.setVisibility(View.INVISIBLE);
//                    rdoDrinkingFrequency1.setChecked(false);
//                    rdoDrinkingFrequency2.setChecked(false);
//                    rdoDrinkingFrequency3.setChecked(false);
//                }
                drinkingLiveData.setSelectedRdoDriking(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinking1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinking2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinking3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinking(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });
        rdoDrinkingFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoDrinkingFrequency3){
//                    rdoDrinkingAlway.setVisibility(View.VISIBLE);
//                }
//                else {
//                    rdoDrinkingAlway.setVisibility(View.INVISIBLE);
//                    rdoDrinkingAlway1.setChecked(false);
//                    rdoDrinkingAlway2.setChecked(false);
//                    rdoDrinkingAlway3.setChecked(false);
//                }
                drinkingLiveData.setSelectedRdoDrikingFrequency(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinkingFrequency1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinkingFrequency2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingFrequency3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinkingFrequency(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });

        rdoDrinkingAlway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                drinkingLiveData.setSelectedRdoDrikingAlway(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinkingAlway1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinkingAlway2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingAlway3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinkingAlway(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });

        shareViewModel.getDrinkingMutableLiveData().observe(getViewLifecycleOwner(), drinkingLiveData -> {
            if(drinkingLiveData != null){
                if(drinkingLiveData.getSelectedRdoDriking()!=null) {
                    rdoDrinking.check(drinkingLiveData.getSelectedRdoDriking());
                }
                if(drinkingLiveData.getSelectedRdoDrikingFrequency()!=null) {
                    rdoDrinkingFrequency.check(drinkingLiveData.getSelectedRdoDrikingFrequency());
                }
                if(drinkingLiveData.getSelectedRdoDrikingAlway()!=null) {
                    rdoDrinkingAlway.check(drinkingLiveData.getSelectedRdoDrikingAlway());
                }
            }
        });
        loadData();
    }

    public static class StressDepression9qLiveDataModel  extends ViewModel {
        public StressDepression9qLiveData getStressDepression9qLiveData() {
            return stressDepression9qLiveData;
        }

        public void setStressDepression9qLiveData(StressDepression9qLiveData stressDepression9qLiveData) {
            this.stressDepression9qLiveData = stressDepression9qLiveData;
        }

        StressDepression9qLiveData stressDepression9qLiveData;
    }
}