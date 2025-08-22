package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.view.CardiovascularRiskGaugeView;
import android.widget.SeekBar;
import android.app.AlertDialog;
import android.widget.ImageView;
public class CardiovascularRiskFragment extends Fragment {

    private EditText edtAge, edtBP, edtWaist, edtHeight, edtCholesterol;
    private EditText edtRiskPercentage, edtRiskLevel;
    private CheckBox cbSmoking, cbDiabetes;
    private EditText edtRecommendation;
//    private TextView tvRiskScore;

    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;

    private SharedViewModel shareViewModel;
    private CardiovascularRiskLiveData cardiovascularRiskLiveData;
    private CardiovascularRiskInfo cardiovascularRiskInfo;

    private OnDataPass dataPasser;

    private CardiovascularRiskGaugeView cardiovascularRiskGauge;
    private TextView tvCardioGaugeEmoji;
    private TextView tvCardioGaugePercentage;
    private TextView tvCardioGaugeLevel;
    private TextView tvCardioGaugeCode;
    private TextView tvCardioGaugeRecommendation;
    private SeekBar seekBarCardioGaugeTest;
    private ImageView ivCardioInfoButton;

    private boolean isLoadingData = false;
    private boolean isObserverSetup = false;

    public CardiovascularRiskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // แก้ไข: ตรวจสอบว่ามี cardiovascularRiskLiveData อยู่แล้วหรือไม่
        if (shareViewModel.getCardiovascularRiskLiveDataMutableLiveData() != null &&
                shareViewModel.getCardiovascularRiskLiveDataMutableLiveData().getValue() != null) {
            // ใช้ข้อมูลที่มีอยู่แล้ว
            cardiovascularRiskLiveData = shareViewModel.getCardiovascularRiskLiveDataMutableLiveData().getValue();
            Log.d("CardiovascularRiskFragment", "Using existing LiveData with personId: " +
                    (cardiovascularRiskLiveData.getPersonId() != null ? cardiovascularRiskLiveData.getPersonId() : "null"));
        } else {
            // สร้างใหม่เฉพาะเมื่อไม่มีข้อมูล
            cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
            shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
            Log.d("CardiovascularRiskFragment", "Created new LiveData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cardiovascular_risk, container, false);
        initViews(view);
        return view;
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

        initializeCardioGaugeViews(view);

        // โหลดข้อมูลครั้งเดียวเท่านั้น และตรวจสอบเงื่อนไขเพิ่มเติม
        if (!isObserverSetup && shareViewModel != null) {
            loadData();
        } else {
            Log.d("CardiovascularRiskFragment", "Skipping loadData - observer already setup or viewModel null");
        }
    }
    private void logCurrentState() {
        Log.d("CardiovascularRiskFragment", "=== Current State ===");
        Log.d("CardiovascularRiskFragment", "isLoadingData: " + isLoadingData);
        Log.d("CardiovascularRiskFragment", "isObserverSetup: " + isObserverSetup);
        Log.d("CardiovascularRiskFragment", "personId: " +
                (cardiovascularRiskLiveData != null ? cardiovascularRiskLiveData.getPersonId() : "null"));
        Log.d("CardiovascularRiskFragment", "===================");
    }
    private void initViews(View view) {
        // ผูกตัวแปรกับ View elements เดิม
        edtAge = view.findViewById(R.id.edtAge);
        edtBP = view.findViewById(R.id.edtBP);
        edtWaist = view.findViewById(R.id.edtWaist);
        edtHeight = view.findViewById(R.id.edtHeight);
        edtCholesterol = view.findViewById(R.id.edtCholesterol);

        cbSmoking = view.findViewById(R.id.cbSmoking);
        cbDiabetes = view.findViewById(R.id.cbDiabetes);

        edtRecommendation = view.findViewById(R.id.edtRecommendation);

        rgGender = view.findViewById(R.id.rgGender);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        edtRiskPercentage = view.findViewById(R.id.edtRiskPercentage);
        edtRiskLevel = view.findViewById(R.id.editRickLevel);

        cardiovascularRiskGauge = view.findViewById(R.id.cardiovascularRiskGauge);
        tvCardioGaugeEmoji = view.findViewById(R.id.tvCardioGaugeEmoji);
        tvCardioGaugePercentage = view.findViewById(R.id.tvCardioGaugePercentage);
        tvCardioGaugeLevel = view.findViewById(R.id.tvCardioGaugeLevel);
        tvCardioGaugeCode = view.findViewById(R.id.tvCardioGaugeCode);
        tvCardioGaugeRecommendation = view.findViewById(R.id.tvCardioGaugeRecommendation);
        ivCardioInfoButton = view.findViewById(R.id.ivCardioInfoButton);
        seekBarCardioGaugeTest = view.findViewById(R.id.seekBarCardioGaugeTest);

        // ตั้งค่า Listeners
        setupListeners();
        setupCardioInfoButtonListener();
        setupCardioGaugeTestControls();

        // อัปเดต Gauge เริ่มต้น
        updateCardioGaugeDisplay();
    }
    private void initializeCardioGaugeViews(View view) {
        cardiovascularRiskGauge = view.findViewById(R.id.cardiovascularRiskGauge);
        tvCardioGaugeEmoji = view.findViewById(R.id.tvCardioGaugeEmoji);
        tvCardioGaugePercentage = view.findViewById(R.id.tvCardioGaugePercentage);
        tvCardioGaugeLevel = view.findViewById(R.id.tvCardioGaugeLevel);
        tvCardioGaugeCode = view.findViewById(R.id.tvCardioGaugeCode);
        tvCardioGaugeRecommendation = view.findViewById(R.id.tvCardioGaugeRecommendation);

        // เพิ่มบรรทัดนี้
        ivCardioInfoButton = view.findViewById(R.id.ivCardioInfoButton);

        // สำหรับทดสอบ (สามารถลบออกได้)
        seekBarCardioGaugeTest = view.findViewById(R.id.seekBarCardioGaugeTest);
        setupCardioGaugeTestControls();

        // ตั้งค่า Info Button
        setupCardioInfoButtonListener();

        // อัปเดต Gauge ครั้งแรก
        updateCardioGaugeDisplay();
    }
    private void setupCardioInfoButtonListener() {
        if (ivCardioInfoButton != null) {
            ivCardioInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardiovascularCriteriaDialog();
                }
            });
        }
    }
    private void showCardiovascularCriteriaDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // สร้าง custom layout สำหรับ dialog
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_cardiovascular_criteria, null);

            builder.setView(dialogView);
            builder.setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            Log.d("CardiovascularRiskFragment", "แสดง Dialog เกณฑ์การประเมินโรคหัวใจและหลอดเลือดสำเร็จ");

        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "เกิดข้อผิดพลาดในการแสดง Dialog: " + e.getMessage());
            showSimpleCardiovascularCriteriaDialog();
        }
    }
    private void showSimpleCardiovascularCriteriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String criteria = "❤️ เกณฑ์การประเมินความเสี่ยงโรคหัวใจและหลอดเลือด\n\n" +
                "ความเสี่ยงต่อการเกิดโรคในระยะเวลา 10 ปี:\n\n" +

                "😊 น้อยกว่า 10%: กลุ่มเสี่ยงน้อย\n" +
                "🔶 ควรคงสภาพปัจจุบันและตรวจสุขภาพประจำปี\n" +
                "🔶 รักษาพฤติกรรมสุขภาพที่ดี\n\n" +

                "😟 10-20%: กลุ่มเสี่ยงปานกลาง\n" +
                "🔶 ควรปรับเปลี่ยนพฤติกรรมสุขภาพ\n" +
                "🔶 ติดตามผลทุก 6 เดือน\n" +
                "🔶 ปรึกษาแพทย์เพื่อประเมินเพิ่มเติม\n\n" +

                "😰 มากกว่า 20%: กลุ่มเสี่ยงสูง\n" +
                "🔶 ต้องการการดูแลอย่างเร่งด่วน\n" +
                "🔶 ควรพบแพทย์เพื่อประเมินและวางแผนการรักษา\n" +
                "🔶 พิจารณาการใช้ยาป้องกัน\n" +
                "🔶 ติดตามอย่างใกล้ชิด\n\n" +

                "⚠️ หมายเหตุ: การประเมินนี้เป็นเพียงข้อมูลเบื้องต้น\n" +
                "ควรปรึกษาแพทย์เพื่อการวินิจฉัยและการรักษาที่แม่นยำ\n\n";

        builder.setTitle("📈 เกณฑ์การประเมินโรคหัวใจและหลอดเลือด")
                .setMessage(criteria)
                .setPositiveButton("✅ ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void setupCardioGaugeTestControls() {
        if (seekBarCardioGaugeTest != null) {
            seekBarCardioGaugeTest.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && cardiovascularRiskGauge != null && !isLoadingData) {
                        updateCardioGaugeWithPercentage(progress);

                        // อัปเดตค่าใน EditText โดยป้องกัน TextWatcher trigger
                        if (edtRiskPercentage != null) {
                            // ใช้ temporary flag เพื่อป้องกัน TextWatcher
                            boolean wasLoadingData = isLoadingData;
                            isLoadingData = true;

                            edtRiskPercentage.setText(String.valueOf(progress));

                            // อัพเดท model โดยตรง
                            if (cardiovascularRiskInfo != null) {
                                cardiovascularRiskInfo.setRiskPercentage(String.valueOf(progress));
                            }
                            if (cardiovascularRiskLiveData != null) {
                                cardiovascularRiskLiveData.setRiskPercentage(progress);
                            }

                            isLoadingData = wasLoadingData;

                            // แจ้ง dataPasser
                            if (dataPasser != null && cardiovascularRiskInfo != null) {
                                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                            }
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }
    private void updateCardioGaugeDisplay() {
        if (cardiovascularRiskGauge == null) return;

        double currentPercentage = getCurrentCardioRiskPercentage();
        CardiovascularRiskGaugeView.RiskLevel currentLevel = getCurrentCardioRiskLevelFromPercentage(currentPercentage);

        // อัปเดต Gauge
        cardiovascularRiskGauge.setRiskPercentage(currentPercentage);

        // อัปเดตข้อความ
        if (tvCardioGaugeEmoji != null) tvCardioGaugeEmoji.setText(currentLevel.emoji);
        if (tvCardioGaugePercentage != null) {
            tvCardioGaugePercentage.setText("ความเสี่ยง: " + String.format("%.1f", currentPercentage) + "%");
        }
        if (tvCardioGaugeLevel != null) {
            tvCardioGaugeLevel.setText(currentLevel.label);
            tvCardioGaugeLevel.setTextColor(Color.parseColor(currentLevel.color));
        }
        if (tvCardioGaugeCode != null) tvCardioGaugeCode.setText(currentLevel.code);
        if (tvCardioGaugeRecommendation != null) {
            String recommendation = getCardioRecommendation(currentPercentage);
            tvCardioGaugeRecommendation.setText(recommendation);

            // เปลี่ยนสีพื้นหลังตามระดับความเสี่ยง
            if (currentPercentage < 10.0) {
                tvCardioGaugeRecommendation.setBackgroundColor(Color.parseColor("#E8F5E8"));
                tvCardioGaugeRecommendation.setTextColor(Color.parseColor("#27AE60"));
            } else if (currentPercentage <= 20.0) {
                tvCardioGaugeRecommendation.setBackgroundColor(Color.parseColor("#FFF3CD"));
                tvCardioGaugeRecommendation.setTextColor(Color.parseColor("#856404"));
            } else {
                tvCardioGaugeRecommendation.setBackgroundColor(Color.parseColor("#F8D7DA"));
                tvCardioGaugeRecommendation.setTextColor(Color.parseColor("#721C24"));
            }
        }

        Log.d("CardiovascularRiskFragment", "Cardio Gauge updated - Percentage: " + currentPercentage + "%, Level: " + currentLevel.label);
    }
    private double getCurrentCardioRiskPercentage() {
        try {
            String riskPercentageStr = edtRiskPercentage.getText().toString().trim();
            if (!riskPercentageStr.isEmpty()) {
                return Double.parseDouble(riskPercentageStr);
            }
        } catch (NumberFormatException e) {
            Log.e("CardiovascularRiskFragment", "ไม่สามารถแปลงเปอร์เซ็นต์ความเสี่ยงเป็นตัวเลขได้");
        }
        return 0.0;
    }
    private CardiovascularRiskGaugeView.RiskLevel getCurrentCardioRiskLevelFromPercentage(double percentage) {
        if (percentage < 10.0) {
            return new CardiovascularRiskGaugeView.RiskLevel(0, 10, "กลุ่มเสี่ยงน้อย", "#27AE60", "😊", "CV_LOW");
        } else if (percentage >= 10.0 && percentage <= 20.0) {
            return new CardiovascularRiskGaugeView.RiskLevel(10, 20, "กลุ่มเสี่ยงปานกลาง", "#F39C12", "😟", "CV_MEDIUM");
        } else {
            return new CardiovascularRiskGaugeView.RiskLevel(20, 100, "กลุ่มเสี่ยงสูง", "#E74C3C", "😰", "CV_HIGH");
        }
    }
    private String getCardioRecommendation(double percentage) {
        if (percentage < 10.0) {
            return "✅ ควรคงสภาพปัจจุบันและตรวจสุขภาพประจำปี";
        } else if (percentage >= 10.0 && percentage <= 20.0) {
            return "⚠️ ควรปรับเปลี่ยนพฤติกรรมสุขภาพและติดตามผลทุก 6 เดือน";
        } else {
            return "🚨 ต้องการการดูแลอย่างเร่งด่วน - ควรพบแพทย์เพื่อประเมินและวางแผนการรักษา";
        }
    }
    public void updateCardioGaugeWithPercentage(double percentage) {
        if (cardiovascularRiskGauge != null) {
            cardiovascularRiskGauge.setRiskPercentage(percentage);
            updateCardioGaugeDisplay();
        }
    }

    private void loadData() {
        try {
            if (isObserverSetup) {
                Log.d("CardiovascularRiskFragment", "Observer already setup, skipping");
                return;
            }

            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(getContext());
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

            isObserverSetup = true;

            viewModel.getCardiovascularRiskLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Already loading data, skipping observer");
                    return;
                }

                Log.d("CardiovascularRiskFragment", "Observer triggered");
                isLoadingData = true;

                try {
                    String personId = null;

                    if (data != null && data.getPersonId() != null) {
                        personId = data.getPersonId();
                        Log.d("CardiovascularRiskFragment", "PersonId from LiveData: " + personId);
                    } else {
                        personId = getPersonIdFromAlternativeSource();
                        if (personId != null) {
                            Log.d("CardiovascularRiskFragment", "PersonId from alternative source: " + personId);
                            if (cardiovascularRiskLiveData == null) {
                                cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
                            }
                            cardiovascularRiskLiveData.setPersonId(personId);
                            // ไม่เรียก shareViewModel.setCardiovascularRiskLiveDataMutableLiveData ที่นี่
                            // เพื่อป้องกัน observer trigger ซ้ำ
                        }
                    }

                    if (personId != null) {
                        loadCardiovascularRiskData(personId, sfCardiovascularRiskInfoDao);
                    } else {
                        Log.w("CardiovascularRiskFragment", "PersonId is null, cannot load data");
                    }
                } finally {
                    isLoadingData = false;
                    Log.d("CardiovascularRiskFragment", "Loading data completed");
                }
            });
        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error loading data: " + e.getMessage());
            isLoadingData = false;
        }
    }
    private void logEditAttempt(String fieldName, String value) {
        Log.d("CardiovascularRiskFragment", "User editing " + fieldName + ": " + value +
                " (isLoadingData: " + isLoadingData + ", isObserverSetup: " + isObserverSetup + ")");
    }
    public void setCardiovascularRiskInfo(CardiovascularRiskInfo info) {
        this.cardiovascularRiskInfo = info;
        loadExistingData();
    }

    private String getPersonIdFromAlternativeSource() {
        // วิธีที่ 1: จาก Fragment Arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("personId")) {
            return args.getString("personId");
        }

        // วิธีที่ 2: จาก SharedPreferences
        if (getContext() != null) {
            android.content.SharedPreferences prefs = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            String personId = prefs.getString("current_person_id", null);
            if (personId != null) {
                return personId;
            }
        }

        // วิธีที่ 3: จาก Activity (ถ้า Activity มี method สำหรับ personId)
        if (getActivity() instanceof OnDataPass) {
            // สมมติว่า Activity มี method getCurrentPersonId()
            try {
                java.lang.reflect.Method method = getActivity().getClass().getMethod("getCurrentPersonId");
                Object result = method.invoke(getActivity());
                if (result instanceof String) {
                    return (String) result;
                }
            } catch (Exception e) {
                Log.d("CardiovascularRiskFragment", "Cannot get personId from Activity: " + e.getMessage());
            }
        }

        return null;
    }
    private void loadCardiovascularRiskData(String personId, SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao) {
        try {
            List<CardiovascularRiskInfo> riskInfos = sfCardiovascularRiskInfoDao.getByPersonId(Integer.valueOf(personId));

            if (!riskInfos.isEmpty()) {
                // มีข้อมูล risk info อยู่แล้ว
                for (CardiovascularRiskInfo riskInfo : riskInfos) {
                    Log.d("CardiovascularRiskFragment", "loadData: riskInfo found: " + riskInfo);
                    cardiovascularRiskInfo = new CardiovascularRiskInfo();
                    setCardiovascularRiskInfo(riskInfo);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                }
            } else {
                // ไม่มีข้อมูล risk info, โหลดข้อมูลพื้นฐานจาก PersonInfo
                loadBasicPersonInfo(personId);
            }
        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error loading cardiovascular risk data: " + e.getMessage());
        }
    }
    private void loadBasicPersonInfo(String personId) {
        try {
            SfPersonInfoDao personInfoDao = new SfPersonInfoDao(getContext());
            List<PersonInfo> personInfos = personInfoDao.getSfPersonInfoById(Integer.valueOf(personId));

            if (!personInfos.isEmpty()) {
                PersonInfo personInfo = personInfos.get(0);

                cardiovascularRiskInfo = new CardiovascularRiskInfo();
                cardiovascularRiskInfo.setPersonId(personId);
                cardiovascularRiskInfo.setIdcard(personInfo.getIdcard());

                // ตั้งค่าเพศ
                if (personInfo.getGender() != null) {
                    cardiovascularRiskInfo.setGender(personInfo.getGender());
                    if (personInfo.getGender().equals("M")) {
                        rbMale.setChecked(true);
                    } else if (personInfo.getGender().equals("F")) {
                        rbFemale.setChecked(true);
                    }
                }

                // คำนวณอายุ
                if (personInfo.getBirthday() != null && !personInfo.getBirthday().isEmpty()) {
                    try {
                        int age = th.in.ffc.util.AgeCalculator.calculateAge(personInfo.getBirthday());
                        cardiovascularRiskInfo.setAge(String.valueOf(age));
                        edtAge.setText(String.valueOf(age));
                    } catch (Exception e) {
                        Log.e("CardiovascularRiskFragment", "Error calculating age: " + e.getMessage());
                    }
                }

                // ตั้งค่าความดัน
                if (personInfo.getSystolic_pressure() > 0) {
                    cardiovascularRiskInfo.setBloodPressure(String.valueOf((int) personInfo.getSystolic_pressure()));
                    edtBP.setText(String.valueOf((int) personInfo.getSystolic_pressure()));
                }

                // ตั้งค่ารอบเอว
                if (personInfo.getWaist_size() > 0) {
                    cardiovascularRiskInfo.setWaistSize(String.valueOf((int) personInfo.getWaist_size()));
                    edtWaist.setText(String.valueOf((int) personInfo.getWaist_size()));
                }

                // ตั้งค่าส่วนสูง
                if (personInfo.getHeight() > 0) {
                    cardiovascularRiskInfo.setHeight(String.valueOf((int) personInfo.getHeight()));
                    edtHeight.setText(String.valueOf((int) personInfo.getHeight()));
                }

                // อัพเดต LiveData
                cardiovascularRiskLiveData.setPersonId(personId);
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);

                Log.d("CardiovascularRiskFragment", "Loaded basic person info for personId: " + personId);
            }
        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error loading basic person info: " + e.getMessage());
        }
    }
    public void setPersonId(String personId) {
        Log.d("CardiovascularRiskFragment", "Setting personId: " + personId);

        if (cardiovascularRiskLiveData == null) {
            cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
        }

        // ตรวจสอบว่า personId เปลี่ยนแปลงหรือไม่
        String currentPersonId = cardiovascularRiskLiveData.getPersonId();
        if (personId != null && personId.equals(currentPersonId)) {
            Log.d("CardiovascularRiskFragment", "PersonId unchanged, skipping update");
            return;
        }

        cardiovascularRiskLiveData.setPersonId(personId);

        // บันทึก personId ลง SharedPreferences
        if (getContext() != null) {
            android.content.SharedPreferences prefs = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            prefs.edit().putString("current_person_id", personId).apply();
        }

        // อัพเดท SharedViewModel อย่างระมัดระวัง
        if (shareViewModel != null && !isLoadingData) {
            shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isObserverSetup = false;
        isLoadingData = false;
    }
    private void loadExistingData() {
        if (cardiovascularRiskInfo == null) {
            cardiovascularRiskInfo = new CardiovascularRiskInfo();
        };

        isLoadingData = true;

        try {
            // Load age
            if (cardiovascularRiskInfo.getAge() != null) {
                edtAge.setText(cardiovascularRiskInfo.getAge());
            }

            // Load gender
            String gender = cardiovascularRiskInfo.getGender();
            if (gender != null) {
                if (gender.equals("M")) {
                    rbMale.setChecked(true);
                } else if (gender.equals("F")) {
                    rbFemale.setChecked(true);
                }
            }

            // Load blood pressure
            if (cardiovascularRiskInfo.getBloodPressure() != null) {
                edtBP.setText(cardiovascularRiskInfo.getBloodPressure());
            }

            // Load waist size
            if (cardiovascularRiskInfo.getWaistSize() != null) {
                edtWaist.setText(cardiovascularRiskInfo.getWaistSize());
            }

            // Load height
            if (cardiovascularRiskInfo.getHeight() != null) {
                edtHeight.setText(cardiovascularRiskInfo.getHeight());
            }

            // Load cholesterol
            if (cardiovascularRiskInfo.getCholesterol() != null) {
                edtCholesterol.setText(cardiovascularRiskInfo.getCholesterol());
            }

            // Load smoking status
            cbSmoking.setChecked(cardiovascularRiskInfo.getIsSmoking() != null &&
                    cardiovascularRiskInfo.getIsSmoking().equals("1"));

            // Load diabetes status
            cbDiabetes.setChecked(cardiovascularRiskInfo.getHasDiabetes() != null &&
                    cardiovascularRiskInfo.getHasDiabetes().equals("1"));

            // Load recommendation
            if (cardiovascularRiskInfo.getRecommendation() != null) {
                edtRecommendation.setText(cardiovascularRiskInfo.getRecommendation());
            }
            if (cardiovascularRiskInfo.getRiskPercentage() != null) {
                edtRiskPercentage.setText(cardiovascularRiskInfo.getRiskPercentage());
            }

            if (cardiovascularRiskInfo.getRiskLevel() != null) {
                edtRiskLevel.setText(cardiovascularRiskInfo.getRiskLevel());
            }
            updateCardioGaugeDisplay();
        }
        catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error loading existing data: " + e.getMessage());
        } finally {
            isLoadingData = false;
        }

    }
    public CardiovascularRiskInfo getCardiovascularRiskInfo() {
        return cardiovascularRiskInfo;
    }
    private void ensureCardiovascularRiskInfoExists() {
        if (cardiovascularRiskInfo == null) {
            cardiovascularRiskInfo = new CardiovascularRiskInfo();
            if (cardiovascularRiskLiveData != null) {
                cardiovascularRiskInfo.setVisitNo(cardiovascularRiskLiveData.getVisitNo());
                cardiovascularRiskInfo.setPersonId(cardiovascularRiskLiveData.getPersonId());
            }
        }

        if (cardiovascularRiskLiveData == null) {
            cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
            if (shareViewModel != null) {
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
            }
        }
    }
    private void setupListeners() {
        // Listener สำหรับ EditText
        ensureCardiovascularRiskInfoExists();

        final boolean[] isUpdatingFromCode = {false};
        edtRiskPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping RiskPercentage TextWatcher - loading data");
                    return;
                }
                // ป้องกันการเกิด loop โดยไม่เรียก calculateRisk() อีก
                if (!TextUtils.isEmpty(s)) {
                    try {
                        double percentage = Double.parseDouble(s.toString());

                        // อัพเดท Model อย่างปลอดภัย
                        if (cardiovascularRiskInfo != null) {
                            cardiovascularRiskInfo.setRiskPercentage(s.toString());
                        }
                        if (cardiovascularRiskLiveData != null) {
                            cardiovascularRiskLiveData.setRiskPercentage(percentage);
                        }

                        // อัพเดท SharedViewModel อย่างระมัดระวัง
                        updateSharedViewModelSafelyForInput();

                        if (dataPasser != null && cardiovascularRiskInfo != null) {
                            dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                        }

                        // อัพเดท Gauge
                        updateCardioGaugeWithPercentage(percentage);

                    } catch (NumberFormatException e) {
                        Log.e("CardiovascularRiskFragment", "Invalid percentage format: " + s.toString());
                    }
                } else {
                    updateCardioGaugeWithPercentage(0);
                }
            }
        });
        edtRiskLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping RiskLevel TextWatcher - loading data");
                    return;
                }

                if (!TextUtils.isEmpty(s)) {
                    try {
                        double level = Double.parseDouble(s.toString());

                        // อัพเดท Model อย่างปลอดภัย
                        if (cardiovascularRiskInfo != null) {
                            cardiovascularRiskInfo.setRiskLevel(s.toString());
                        }
                        if (cardiovascularRiskLiveData != null) {
                            cardiovascularRiskLiveData.setRiskLevel(level);
                        }

                        // อัพเดท SharedViewModel อย่างระมัดระวัง
                        updateSharedViewModelSafelyForInput();

                        if (dataPasser != null && cardiovascularRiskInfo != null) {
                            dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                        }

                    } catch (NumberFormatException e) {
                        Log.e("CardiovascularRiskFragment", "Invalid level format: " + s.toString());
                    }
                }
            }
        });
        // Listener สำหรับ CheckBox
        cbSmoking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping Smoking CheckBox - loading data");
                    return;
                }

                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setIsSmoking(isChecked ? "1" : "0");
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setIsSmoking(isChecked);
                }

                updateSharedViewModelSafelyForInput();

                if (dataPasser != null && cardiovascularRiskInfo != null) {
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                }
                calculateRisk();
            }
        });

        cbDiabetes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping Diabetes CheckBox - loading data");
                    return;
                }

                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setHasDiabetes(isChecked ? "1" : "0");
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setHasDiabetes(isChecked);
                }

                updateSharedViewModelSafelyForInput();

                if (dataPasser != null && cardiovascularRiskInfo != null) {
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                }
                calculateRisk();
            }
        });
        edtAge.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setAge(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setAge(s);
                }
                calculateRisk();
            }
        }));

        edtBP.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setBloodPressure(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setBloodPressure(s);
                }
                calculateRisk();
            }
        }));

        edtWaist.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setWaistSize(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setWaistSize(s);
                }
                calculateRisk();
            }
        }));

        edtHeight.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setHeight(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setHeight(s);
                }
                calculateRisk();
            }
        }));

        edtCholesterol.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setCholesterol(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setCholesterol(s);
                }
                calculateRisk();
            }
        }));

        edtRecommendation.addTextChangedListener(createSafeTextWatcher(new TextChangeCallback() {
            @Override
            public void onTextChanged(String s) {
                ensureCardiovascularRiskInfoExists();
                if (cardiovascularRiskInfo != null) {
                    cardiovascularRiskInfo.setRecommendation(s);
                }
                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setRecommendation(s);
                }
            }
        }));
        // ปรับปรุง Gender RadioGroup Listener เพื่อตั้งค่าเพศใน Model
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping Gender RadioGroup - loading data");
                    return;
                }

                String gender = "";
                if (checkedId == R.id.rbMale) {
                    gender = "M";
                    if (cardiovascularRiskInfo != null) {
                        cardiovascularRiskInfo.setGender("M");
                    }
                } else if (checkedId == R.id.rbFemale) {
                    gender = "F";
                    if (cardiovascularRiskInfo != null) {
                        cardiovascularRiskInfo.setGender("F");
                    }
                }

                if (cardiovascularRiskLiveData != null) {
                    cardiovascularRiskLiveData.setSelectedGender(checkedId);
                }

                updateSharedViewModelSafelyForInput();

                if (dataPasser != null && cardiovascularRiskInfo != null) {
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                }
                calculateRisk();
            }
        });
    }
    private void updateSharedViewModelSafelyForInput() {
        // อัพเดท SharedViewModel เฉพาะเมื่อเป็นการแก้ไขจาก user
        // และไม่อยู่ในระหว่างการโหลดข้อมูล
        if (shareViewModel != null && !isLoadingData && cardiovascularRiskLiveData != null) {
            // ไม่เรียก setCardiovascularRiskLiveDataMutableLiveData เพื่อป้องกัน observer loop
            // แค่อัพเดทข้อมูลใน object ที่มีอยู่แล้ว
            Log.d("CardiovascularRiskFragment", "Updating data from user input");

            // อัพเดทข้อมูลโดยไม่ trigger observer
            // เนื่องจากเราอัพเดทข้อมูลใน object เดิมแล้ว
        }
    }
    private void updateSharedViewModelSafely() {
        if (shareViewModel != null && !isLoadingData && cardiovascularRiskLiveData != null) {
            Log.d("CardiovascularRiskFragment", "Updating ViewModel safely");

            // อัพเดท SharedViewModel เฉพาะเมื่อจำเป็น
            if (!isObserverSetup) {
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
            } else {
                // ถ้า observer setup แล้ว ไม่ต้องอัพเดท ViewModel
                Log.d("CardiovascularRiskFragment", "Observer already setup, skipping ViewModel update");
            }
        }
    }
    private void updateSharedViewModelDirectly() {
        if (shareViewModel != null && cardiovascularRiskLiveData != null) {
            // อัพเดทข้อมูลโดยตรงใน LiveData object ที่มีอยู่แล้ว
            // โดยไม่สร้าง instance ใหม่
            Log.d("CardiovascularRiskFragment", "Updating ViewModel directly");
        }
    }

    private TextWatcher createSafeTextWatcher(final TextChangeCallback callback) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isLoadingData) {
                    Log.d("CardiovascularRiskFragment", "Skipping TextWatcher - loading data");
                    return;
                }

//                if (!TextUtils.isEmpty(s)) {
                    callback.onTextChanged(s.toString());
                    updateSharedViewModelSafelyForInput();

                    if (dataPasser != null && cardiovascularRiskInfo != null) {
                        dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    }
//                }
            }
        };
    }
    private interface TextChangeCallback {
        void onTextChanged(String text);
    }
    public void showCardioGaugeTestControls(boolean show) {
        View layoutCardioGaugeControl = getView().findViewById(R.id.layoutCardioGaugeControl);
        if (layoutCardioGaugeControl != null) {
            layoutCardioGaugeControl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public void resetCardioGauge() {
        if (cardiovascularRiskGauge != null) {
            cardiovascularRiskGauge.setRiskPercentage(0);
            updateCardioGaugeDisplay();
        }
    }

    public CardiovascularRiskGaugeView.RiskLevel getCurrentCardioGaugeLevel() {
        if (cardiovascularRiskGauge != null) {
            return cardiovascularRiskGauge.getCurrentRiskLevel();
        }
        return getCurrentCardioRiskLevelFromPercentage(0);
    }

    // เพิ่ม method สำหรับตรวจสอบระดับความเสี่ยง
    public boolean isCardiovascularHighRisk() {
        double percentage = getCurrentCardioRiskPercentage();
        return percentage > 20.0;
    }

    public boolean isCardiovascularMediumRisk() {
        double percentage = getCurrentCardioRiskPercentage();
        return percentage >= 10.0 && percentage <= 20.0;
    }

    public boolean isCardiovascularLowRisk() {
        double percentage = getCurrentCardioRiskPercentage();
        return percentage < 10.0;
    }
    public void checkCardiovascularHighRiskAlert() {
        if (isCardiovascularHighRisk()) {
            double percentage = getCurrentCardioRiskPercentage();
            String emoji = getCurrentCardioRiskLevelFromPercentage(percentage).emoji;
            String message = String.format(
                    "%s ตรวจพบความเสี่ยงสูงต่อโรคหัวใจและหลอดเลือด\n\n" +
                            "ความเสี่ยง: %.1f%%\n" +
                            "ระดับ: %s\n\n" +
                            "คำแนะนำ: %s",
                    emoji,
                    percentage,
                    "กลุ่มเสี่ยงสูง",
                    getCardioRecommendation(percentage)
            );

            Log.w("CardiovascularRiskFragment", message);

            // แสดง Toast แจ้งเตือน
            if (getContext() != null) {
                Toast.makeText(getContext(), emoji + " ตรวจพบความเสี่ยงสูงต่อโรคหัวใจและหลอดเลือด",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getCardiovascularRiskSummary() {
        double percentage = getCurrentCardioRiskPercentage();
        CardiovascularRiskGaugeView.RiskLevel level = getCurrentCardioRiskLevelFromPercentage(percentage);
        return String.format("%s ความเสี่ยง: %.1f%%, %s", level.emoji, percentage, level.label);
    }
    private TextWatcher createTextWatcher(Runnable onTextChanged) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    // อัพเดท model ตามฟิลด์ที่เกี่ยวข้อง
                    // (ต้องระบุแยกแต่ละฟิลด์)

                    if (onTextChanged != null) {
                        onTextChanged.run();
                    }
                }
            }
        };
    }
    private void calculateRisk() {
        try {
            // ตรวจสอบว่ามี Risk Percentage กรอกไว้หรือไม่
            String riskPercentageStr = edtRiskPercentage.getText().toString().trim();

            if (!riskPercentageStr.isEmpty()) {
                double riskPercentage = Double.parseDouble(riskPercentageStr);

                // ไฮไลท์แถวตาม %

                Log.d("CardiovascularRiskFragment", "Risk calculated: " + riskPercentage + "%");
            } else {

            }

        } catch (NumberFormatException e) {
            Log.e("CardiovascularRiskFragment", "Error calculating risk: " + e.getMessage());

        }
    }
    private boolean validateInput(String... inputs) {
        for (String input : inputs) {
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // เพิ่มการตรวจสอบเพศจาก Model แทนที่จะตรวจสอบจาก RadioGroup โดยตรง
        if (cardiovascularRiskInfo.getGender() == null || cardiovascularRiskInfo.getGender().isEmpty()) {
            Toast.makeText(getContext(), "กรุณาเลือกเพศ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void setupEditTextClickListeners() {
        // สร้าง InputMethodManager
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // เพิ่ม OnClickListener ให้กับ EditText ต่างๆ
        View.OnClickListener clickListener = v -> {
            v.requestFocus();
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        };

        // ตั้งค่า click listener สำหรับแต่ละ EditText
        edtAge.setOnClickListener(clickListener);
        edtBP.setOnClickListener(clickListener);
        edtWaist.setOnClickListener(clickListener);
        edtHeight.setOnClickListener(clickListener);
        edtCholesterol.setOnClickListener(clickListener);
        edtRiskPercentage.setOnClickListener(clickListener);
        edtRiskLevel.setOnClickListener(clickListener);
        edtRecommendation.setOnClickListener(clickListener);
    }
    private double calculateRiskScore(int age, String gender, int bp, int waist,
                                      int height, int cholesterol, boolean isSmoking,
                                      boolean hasDiabetes) {
        double score = 0;

        // คะแนนตามอายุ
        if (age >= 50) score += 2;
        else if (age >= 45) score += 1;

        // คะแนนตามเพศ
        if (gender.equals("ชาย")) {
            score += 2;
        }

        // คะแนนความดัน
        if (bp >= 140) score += 2;
        else if (bp >= 130) score += 1;

        // คะแนนรอบเอว (แยกตามเพศ)
        if (gender.equals("ชาย")) {
            if (waist >= 90) score += 2;
        } else {
            if (waist >= 80) score += 2;
        }

        // คะแนนโคเลสเตอรอล
        if (cholesterol > 0) {
            if (cholesterol >= 240) score += 2;
            else if (cholesterol >= 200) score += 1;
        }

        // คะแนนการสูบบุหรี่
        if (isSmoking) score += 2;

        // คะแนนเบาหวาน
        if (hasDiabetes) score += 2;

        return score;
    }

    private void updateRecommendation(String riskLevel) {
        String recommendation = "";

        switch (riskLevel) {
            case "low":
                recommendation = "ความเสี่ยงน้อย (< 10%): แนะนำให้ควบคุมปัจจัยเสี่ยงและตรวจสุขภาพประจำปี รักษาพฤติกรรมสุขภาพที่ดี";
                break;
            case "medium":
                recommendation = "ความเสี่ยงปานกลาง (10-20%): แนะนำให้ปรับเปลี่ยนพฤติกรรมสุขภาพ ติดตามผลทุก 6 เดือน และปรึกษาแพทย์";
                break;
            case "high":
                recommendation = "ความเสี่ยงสูง (> 20%): แนะนำให้พบแพทย์เพื่อประเมินและวางแผนการรักษาอย่างเร่งด่วน พิจารณาการใช้ยา";
                break;
            default:
                recommendation = "";
                break;
        }

        if (!recommendation.isEmpty()) {
            cardiovascularRiskInfo.setRecommendation(recommendation);
            edtRecommendation.setText(recommendation);

            // อัพเดท LiveData
            cardiovascularRiskLiveData.setRecommendation(recommendation);
            shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
            dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
        }
    }
    public String getRiskLevelFromPercentage(double percentage) {
        if (percentage < 10.0) {
            return "กลุ่มเสี่ยงน้อย";
        } else if (percentage >= 10.0 && percentage <= 20.0) {
            return "กลุ่มเสี่ยงปานกลาง";
        } else {
            return "กลุ่มเสี่ยงสูง";
        }
    }
    public boolean isFormComplete() {
        try {
            // ตรวจสอบว่ามีข้อมูล Risk Percentage หรือไม่
            String riskPercentageStr = edtRiskPercentage.getText().toString().trim();
            String riskLevelStr = edtRiskLevel.getText().toString().trim();
            String colesterolStr = edtCholesterol.getText().toString().trim();

            return !riskPercentageStr.isEmpty() && !riskLevelStr.isEmpty() &&
                    !colesterolStr.isEmpty() &&
                    cardiovascularRiskInfo != null &&
                    cardiovascularRiskInfo.getAge() != null &&
                    !cardiovascularRiskInfo.getAge().isEmpty() &&
                    cardiovascularRiskInfo.getGender() != null &&
                    !cardiovascularRiskInfo.getGender().isEmpty();

        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error checking form completion: " + e.getMessage());
            return false;
        }
    }
    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        if (cardiovascularRiskInfo == null) {
            return "แบบประเมินความเสี่ยงโรคหัวใจและหลอดเลือด:\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingFields = new ArrayList<>();

        // ตรวจสอบอายุ
        if (cardiovascularRiskInfo.getAge() == null ||
                cardiovascularRiskInfo.getAge().trim().isEmpty()) {
            missingFields.add("อายุ");
        }

        // ตรวจสอบเพศ
        if (cardiovascularRiskInfo.getGender() == null ||
                cardiovascularRiskInfo.getGender().trim().isEmpty()) {
            missingFields.add("เพศ");
        }

        // ตรวจสอบความดันโลหิต
        if (cardiovascularRiskInfo.getBloodPressure() == null ||
                cardiovascularRiskInfo.getBloodPressure().trim().isEmpty()) {
            missingFields.add("ความดันโลหิต (Systolic)");
        }

        // ตรวจสอบรอบเอว
        if (cardiovascularRiskInfo.getWaistSize() == null ||
                cardiovascularRiskInfo.getWaistSize().trim().isEmpty()) {
            missingFields.add("รอบเอว");
        }

        // ตรวจสอบส่วนสูง
        if (cardiovascularRiskInfo.getHeight() == null ||
                cardiovascularRiskInfo.getHeight().trim().isEmpty()) {
            missingFields.add("ส่วนสูง");
        }

        // ตรวจสอบคอเลสเตอรอล
        if (cardiovascularRiskInfo.getCholesterol() == null ||
                cardiovascularRiskInfo.getCholesterol().trim().isEmpty()) {
            missingFields.add("คอเลสเตอรอล");
        }

        // ตรวจสอบสถานะการสูบบุหรี่ และเบาหวาน (เป็น optional แต่ควรมี)
        boolean hasSmokingStatus = cardiovascularRiskInfo.getIsSmoking() != null &&
                !cardiovascularRiskInfo.getIsSmoking().trim().isEmpty();
        boolean hasDiabetesStatus = cardiovascularRiskInfo.getHasDiabetes() != null &&
                !cardiovascularRiskInfo.getHasDiabetes().trim().isEmpty();

        if (!hasSmokingStatus) {
            missingFields.add("สถานะการสูบบุหรี่");
        }

        if (!hasDiabetesStatus) {
            missingFields.add("ประวัติเบาหวาน");
        }

        // ตรวจสอบเปอร์เซ็นต์ความเสี่ยง
        String riskPercentageStr = "";
        if (edtRiskPercentage != null) {
            riskPercentageStr = edtRiskPercentage.getText().toString().trim();
        }
        boolean hasRiskPercentage = !riskPercentageStr.isEmpty();

        if (!hasRiskPercentage) {
            missingFields.add("เปอร์เซ็นต์ความเสี่ยง");
        }

        String riskLevelStr = "";
        if (edtRiskLevel != null) {
            riskLevelStr = edtRiskLevel.getText().toString().trim();
        }
        boolean hasRiskLevel = !riskLevelStr.isEmpty();

        if (!hasRiskLevel) {
            missingFields.add("ระดับความเสี่ยง");
        }

        if (!missingFields.isEmpty()) {
            StringBuilder message = new StringBuilder("แบบประเมินความเสี่ยงโรคหัวใจและหลอดเลือด:\n");
            message.append("กรุณากรอกข้อมูลที่ยังไม่ได้กรอก:\n");

            for (String field : missingFields) {
                message.append("• ").append(field).append("\n");
            }

            // เพิ่มคำแนะนำเพิ่มเติม
//            if (!hasRiskPercentage && missingFields.size() > 1) {
//                message.append("\nหมายเหตุ: เมื่อกรอกข้อมูลพื้นฐานครบถ้วนแล้ว ระบบจะคำนวณเปอร์เซ็นต์ความเสี่ยงให้อัตโนมัติ\n");
//            }

            // เพิ่มข้อมูลเกี่ยวกับความสำคัญของการประเมิน
            if (missingFields.contains("อายุ") || missingFields.contains("เพศ") ||
                    missingFields.contains("ความดันโลหิต (Systolic)")) {
                message.append("\nข้อมูลอายุ เพศ และความดันโลหิต เป็นปัจจัยสำคัญในการประเมินความเสี่ยง\n");
            }

            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * ตรวจสอบว่าข้อมูลครบถ้วนหรือไม่ (เวอร์ชันที่เข้มงวดกว่า isFormComplete)
     */
    public boolean isFormCompleteDetailed() {
        if (cardiovascularRiskInfo == null) {
            return false;
        }

        // ข้อมูลพื้นฐานที่จำเป็น
        boolean hasAge = cardiovascularRiskInfo.getAge() != null &&
                !cardiovascularRiskInfo.getAge().trim().isEmpty();
        boolean hasGender = cardiovascularRiskInfo.getGender() != null &&
                !cardiovascularRiskInfo.getGender().trim().isEmpty();
        boolean hasBloodPressure = cardiovascularRiskInfo.getBloodPressure() != null &&
                !cardiovascularRiskInfo.getBloodPressure().trim().isEmpty();
        boolean hasWaistSize = cardiovascularRiskInfo.getWaistSize() != null &&
                !cardiovascularRiskInfo.getWaistSize().trim().isEmpty();
        boolean hasHeight = cardiovascularRiskInfo.getHeight() != null &&
                !cardiovascularRiskInfo.getHeight().trim().isEmpty();
        boolean hasCholesterol = cardiovascularRiskInfo.getCholesterol() != null &&
                !cardiovascularRiskInfo.getCholesterol().trim().isEmpty();

        // ข้อมูลเสริม (ควรมี)
        boolean hasSmokingStatus = cardiovascularRiskInfo.getIsSmoking() != null;
        boolean hasDiabetesStatus = cardiovascularRiskInfo.getHasDiabetes() != null;

        // ผลการประเมิน
        String riskPercentageStr = "";
        if (edtRiskPercentage != null) {
            riskPercentageStr = edtRiskPercentage.getText().toString().trim();
        }
        boolean hasRiskPercentage = !riskPercentageStr.isEmpty();

        boolean hasRiskLevel = edtRiskLevel != null && edtRiskLevel.getText() != null &&
                !edtRiskLevel.getText().toString().trim().isEmpty();

        return hasAge && hasGender && hasBloodPressure && hasWaistSize &&
                hasHeight && hasCholesterol && hasSmokingStatus &&
                hasDiabetesStatus && hasRiskPercentage && hasRiskLevel;
    }

    /**
     * ดึงรายชื่อข้อมูลที่ยังไม่ได้กรอก
     */
    public ArrayList<String> getMissingFields() {
        ArrayList<String> missing = new ArrayList<>();

        if (cardiovascularRiskInfo == null) {
            missing.add("ข้อมูลทั้งหมด");
            return missing;
        }

        if (cardiovascularRiskInfo.getAge() == null ||
                cardiovascularRiskInfo.getAge().trim().isEmpty()) {
            missing.add("อายุ");
        }

        if (cardiovascularRiskInfo.getGender() == null ||
                cardiovascularRiskInfo.getGender().trim().isEmpty()) {
            missing.add("เพศ");
        }

        if (cardiovascularRiskInfo.getBloodPressure() == null ||
                cardiovascularRiskInfo.getBloodPressure().trim().isEmpty()) {
            missing.add("ความดันโลหิต");
        }

        if (cardiovascularRiskInfo.getWaistSize() == null ||
                cardiovascularRiskInfo.getWaistSize().trim().isEmpty()) {
            missing.add("รอบเอว");
        }

        if (cardiovascularRiskInfo.getHeight() == null ||
                cardiovascularRiskInfo.getHeight().trim().isEmpty()) {
            missing.add("ส่วนสูง");
        }

        if (cardiovascularRiskInfo.getCholesterol() == null ||
                cardiovascularRiskInfo.getCholesterol().trim().isEmpty()) {
            missing.add("คอเลสเตอรอล");
        }

        if (cardiovascularRiskInfo.getIsSmoking() == null) {
            missing.add("สถานะการสูบบุหรี่");
        }

        if (cardiovascularRiskInfo.getHasDiabetes() == null) {
            missing.add("ประวัติเบาหวาน");
        }

        String riskPercentageStr = "";
        if (edtRiskPercentage != null) {
            riskPercentageStr = edtRiskPercentage.getText().toString().trim();
        }
        if (riskPercentageStr.isEmpty()) {
            missing.add("เปอร์เซ็นต์ความเสี่ยง");
        }
        if (edtRiskLevel != null && edtRiskLevel.getText() != null &&
                edtRiskLevel.getText().toString().trim().isEmpty()) {
            missing.add("ระดับความเสี่ยง");
        }

        return missing;
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (cardiovascularRiskInfo == null) {
            return 0;
        }

        int completedFields = 0;
        int totalFields = 9; // จำนวนฟิลด์ทั้งหมดที่ต้องกรอก

        if (cardiovascularRiskInfo.getAge() != null &&
                !cardiovascularRiskInfo.getAge().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getGender() != null &&
                !cardiovascularRiskInfo.getGender().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getBloodPressure() != null &&
                !cardiovascularRiskInfo.getBloodPressure().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getWaistSize() != null &&
                !cardiovascularRiskInfo.getWaistSize().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getHeight() != null &&
                !cardiovascularRiskInfo.getHeight().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getCholesterol() != null &&
                !cardiovascularRiskInfo.getCholesterol().trim().isEmpty()) completedFields++;

        if (cardiovascularRiskInfo.getIsSmoking() != null) completedFields++;

        if (cardiovascularRiskInfo.getHasDiabetes() != null) completedFields++;

        String riskPercentageStr = "";
        if (edtRiskPercentage != null) {
            riskPercentageStr = edtRiskPercentage.getText().toString().trim();
        }
        if (!riskPercentageStr.isEmpty()) completedFields++;

        String riskLevelStr = "";
        if (edtRiskLevel != null) {
            riskLevelStr = edtRiskLevel.getText().toString().trim();
        }
        if (!riskLevelStr.isEmpty()) completedFields++;

        return (completedFields * 100) / totalFields;
    }

    /**
     * แสดงสถานะการกรอกข้อมูล
     */
    public void showCompletionStatus() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%)";

            // แสดงระดับความเสี่ยงด้วย
            double riskPercentage = getCurrentCardioRiskPercentage();
            String riskLevel = getRiskLevelFromPercentage(riskPercentage);
            message += " - " + riskLevel + " (" + String.format("%.1f", riskPercentage) + "%)";
        } else if (percentage > 0) {
            ArrayList<String> missing = getMissingFields();
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - ยังขาด: " +
                    String.join(", ", missing.subList(0, Math.min(3, missing.size())));
            if (missing.size() > 3) {
                message += " และอีก " + (missing.size() - 3) + " รายการ";
            }
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("CardiovascularRiskFragment", "Completion Status: " + message);

    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (cardiovascularRiskInfo == null) {
            return false;
        }

        // ตรวจสอบว่ามีการกรอกข้อมูลอย่างน้อย 1 ฟิลด์หรือไม่
        boolean hasData = false;

        hasData |= (cardiovascularRiskInfo.getAge() != null &&
                !cardiovascularRiskInfo.getAge().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getGender() != null &&
                !cardiovascularRiskInfo.getGender().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getBloodPressure() != null &&
                !cardiovascularRiskInfo.getBloodPressure().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getWaistSize() != null &&
                !cardiovascularRiskInfo.getWaistSize().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getHeight() != null &&
                !cardiovascularRiskInfo.getHeight().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getCholesterol() != null &&
                !cardiovascularRiskInfo.getCholesterol().trim().isEmpty());
        hasData |= (cardiovascularRiskInfo.getIsSmoking() != null);
        hasData |= (cardiovascularRiskInfo.getHasDiabetes() != null);

        // ตรวจสอบผลการประเมินด้วย
        if (edtRiskPercentage != null) {
            String riskPercentageStr = edtRiskPercentage.getText().toString().trim();
            hasData |= !riskPercentageStr.isEmpty();
        }
        if (edtRiskLevel != null) {
            String riskLevelStr = edtRiskLevel.getText().toString().trim();
            hasData |= !riskLevelStr.isEmpty();
        }

        return hasData;
    }
}