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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.PersonData;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.util.Log;

public class HealthRiskAssessmentFragment extends Fragment {

    SharedViewModel shareViewModel;
    HealthRiskAssessmentLiveData healthRiskAssessmentLiveData;
    private OnDataPass dataPasser;
    private HealthRiskAssessmentInfo healthRiskAssessmentInfo;
    private EditText editFcbg;
    private EditText editFpg;

    // ตัวแปรป้องกัน infinite loop
    private boolean isAutoSelecting = false;
    private boolean isLoadingExistingData = false;
    private boolean isUpdatingFromTextWatcher = false;

    public HealthRiskAssessmentFragment() {
        // Required empty public constructor
    }

    public static HealthRiskAssessmentFragment newInstance(String param1, String param2) {
        HealthRiskAssessmentFragment fragment = new HealthRiskAssessmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (shareViewModel == null) {
            healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
            shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
        }
        healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health_risk_assessment, container, false);
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

        healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();

        // Initialize UI components
        setupRadioGroups(view);
        editFcbg = view.findViewById(R.id.edtFCBG);
        editFpg = view.findViewById(R.id.edtFPG);

        // Setup listeners and observers
        loadData();
        setupGlucoseInputListeners();
        setupFocusListeners();
        setupPersonDataObserver();

        // Check for bundle arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            setPersonDataFromOtherScreens(arguments);
        }
    }

    private void setupRadioGroups(View view) {
        RadioGroup rdoHealthRiskQ1 = view.findViewById(R.id.rdoHealthRiskQ1);
        RadioGroup rdoHealthRiskQ2 = view.findViewById(R.id.rdoHealthRiskQ2);
        RadioGroup rdoHealthRiskQ3 = view.findViewById(R.id.rdoHealthRiskQ3);
        RadioGroup rdoHealthRiskQ4 = view.findViewById(R.id.rdoHealthRiskQ4);
        RadioGroup rdoHealthRiskQ5 = view.findViewById(R.id.rdoHealthRiskQ5);
        RadioGroup rdoHealthRiskQ6 = view.findViewById(R.id.rdoHealthRiskQ6);

        // Q1 - Age
        rdoHealthRiskQ1.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ1_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ1(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ1(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });

        // Q2 - Gender
        rdoHealthRiskQ2.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ2_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ2(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ2(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });

        // Q3 - BMI
        rdoHealthRiskQ3.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ3_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ3(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ3(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });

        // Q4 - Waist
        rdoHealthRiskQ4.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ4_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ4(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ4(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });

        // Q5 - Blood Pressure
        rdoHealthRiskQ5.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ5_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ5(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ5(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });

        // Q6 - Family History
        rdoHealthRiskQ6.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (isAutoSelecting || isLoadingExistingData) return;

            String data = getSelectedValue(checkedId, "rdoHealthRiskQ6_");
            if (!data.isEmpty()) {
                healthRiskAssessmentLiveData.setSelectHealthRiskQ6(checkedId);
                updateSharedViewModel();
                healthRiskAssessmentInfo.setHealthRiskQ6(data);
                notifyDataPasser();
                updateScoreAndHighlight();
            }
        });
    }

    private String getSelectedValue(int checkedId, String prefix) {
        String resourceName = getResources().getResourceEntryName(checkedId);
        if (resourceName.startsWith(prefix)) {
            return resourceName.substring(prefix.length());
        }
        return "";
    }

    private void updateSharedViewModel() {
        if (shareViewModel != null) {
            shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
        }
    }

    private void notifyDataPasser() {
        if (dataPasser != null && !isAutoSelecting && !isLoadingExistingData) {
            dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
        }
    }

    private void setupPersonDataObserver() {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonDataLiveData().observe(getViewLifecycleOwner(), personData -> {
            if (personData != null && !isLoadingExistingData) {
                Log.d("HealthRiskAssessment", "Received PersonData: " + personData.toString());
                autoSelectFromPersonData(personData);
            }
        });
    }

    private void loadData() {
        SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getHealthRiskAssessmentLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data.getPersonId() != null) {
                List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos =
                        sfHealthRiskAssessmentInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));

                for (HealthRiskAssessmentInfo healthRiskAssessmentInfo1 : healthRiskAssessmentInfos) {
                    Log.d("healthRiskAssessmentInfo1", "healthRiskAssessmentInfo1 infos:" + healthRiskAssessmentInfo1);
                    setHealthRiskInfo(healthRiskAssessmentInfo1);
                }
            }
        });
    }

    public void setHealthRiskInfo(HealthRiskAssessmentInfo info) {
        this.healthRiskAssessmentInfo = info;
        loadExistingData();
    }

    private void loadExistingData() {
        if (healthRiskAssessmentInfo == null) return;

        isLoadingExistingData = true;

        try {
            // Load all questions silently
            loadQuestionSilently("rdoHealthRiskQ1_", healthRiskAssessmentInfo.getHealthRiskQ1());
            loadQuestionSilently("rdoHealthRiskQ2_", healthRiskAssessmentInfo.getHealthRiskQ2());
            loadQuestionSilently("rdoHealthRiskQ3_", healthRiskAssessmentInfo.getHealthRiskQ3());
            loadQuestionSilently("rdoHealthRiskQ4_", healthRiskAssessmentInfo.getHealthRiskQ4());
            loadQuestionSilently("rdoHealthRiskQ5_", healthRiskAssessmentInfo.getHealthRiskQ5());
            loadQuestionSilently("rdoHealthRiskQ6_", healthRiskAssessmentInfo.getHealthRiskQ6());

            // Load glucose values - ใช้ method ใหม่ที่รองรับ empty string
            setTextSilently(editFcbg, healthRiskAssessmentInfo.getFcbg());
            setTextSilently(editFpg, healthRiskAssessmentInfo.getFpg());

            updateScoreAndHighlight();
            updateGlucoseHighlightFromCurrentData(); // อัพเดต glucose highlight

        } finally {
            isLoadingExistingData = false;
        }
    }
    private void loadQuestionSilently(String prefix, String value) {
        if (value != null && !value.equals("0")) {
            selectRadioButtonSilently(prefix + value);
        }
    }

    private void setTextSilently(EditText editText, String value) {
        isUpdatingFromTextWatcher = true;
        try {
            // บันทึกตำแหน่ง cursor ปัจจุบัน
            int cursorPosition = editText.getSelectionStart();

            String currentText = editText.getText().toString();
            String newText = (value != null) ? value : "";

            // ตั้งค่าข้อความใหม่เฉพาะเมื่อมีการเปลี่ยนแปลง
            if (!currentText.equals(newText)) {
                editText.setText(newText);

                // คำนวณตำแหน่ง cursor ใหม่
                int newCursorPosition = Math.min(cursorPosition, newText.length());

                // ตั้งตำแหน่ง cursor ให้ถูกต้อง
                if (newCursorPosition >= 0 && newCursorPosition <= newText.length()) {
                    editText.setSelection(newCursorPosition);
                }
            }
        } catch (Exception e) {
            // กรณีมีข้อผิดพลาด ให้ตั้งค่าแบบปกติ
            String newText = (value != null) ? value : "";
            editText.setText(newText);
            editText.setSelection(newText.length()); // ย้าย cursor ไปท้ายสุด
        } finally {
            isUpdatingFromTextWatcher = false;
        }
    }

    private void autoSelectFromPersonData(PersonData personData) {
        if (personData == null || isLoadingExistingData) return;

        isAutoSelecting = true;

        try {
            Log.d("HealthRiskAssessment", "Starting auto-selection from PersonData");

            // Auto-select Age
            autoSelectAge(personData);

            // Auto-select Gender
            autoSelectGender(personData);

            // Auto-select BMI
            autoSelectBMI(personData);

            // Auto-select Waist Circumference
            autoSelectWaist(personData);

            // Auto-select Hypertension
            autoSelectHypertension(personData);

            // Auto-select Family History
            autoSelectFamilyHistory(personData);

            // Auto-fill glucose values
            autoFillGlucoseValues(personData);

            // Update score and notify
            updateScoreAndHighlight();
            notifyDataPasser();

        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error in autoSelectFromPersonData: " + e.getMessage());
        } finally {
            isAutoSelecting = false;
        }
    }

    private void autoSelectAge(PersonData personData) {
        if (personData.getAge() != null) {
            int age = personData.getAge();
            String ageCategory = "";

            if (age >= 34 && age <= 39) ageCategory = "1";
            else if (age >= 40 && age <= 44) ageCategory = "2";
            else if (age >= 45 && age <= 49) ageCategory = "3";
            else if (age >= 50) ageCategory = "4";

            if (!ageCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ1(ageCategory);
                selectRadioButtonSilently("rdoHealthRiskQ1_" + ageCategory);
                Log.d("HealthRiskAssessment", "Auto-selected age: " + ageCategory + " for age: " + age);
            }
        }
    }

    private void autoSelectGender(PersonData personData) {
        if (personData.getGender() != null) {
            String gender = personData.getGender().toLowerCase();
            String genderCategory = "";

            if (gender.equals("female") || gender.equals("หญิง") || gender.equals("f") || gender.equals("2")) {
                genderCategory = "1"; // หญิง
            } else if (gender.equals("male") || gender.equals("ชาย") || gender.equals("m") || gender.equals("1")) {
                genderCategory = "2"; // ชาย
            }

            if (!genderCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ2(genderCategory);
                selectRadioButtonSilently("rdoHealthRiskQ2_" + genderCategory);
                Log.d("HealthRiskAssessment", "Auto-selected gender: " + genderCategory + " for: " + gender);
            }
        }
    }

    private void autoSelectBMI(PersonData personData) {
        if (personData.getBmi() != null) {
            double bmi = personData.getBmi();
            String bmiCategory = "";

            if (bmi < 23) bmiCategory = "1";
            else if (bmi >= 23 && bmi < 27.5) bmiCategory = "2";
            else if (bmi >= 27.5) bmiCategory = "3";

            if (!bmiCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ3(bmiCategory);
                selectRadioButtonSilently("rdoHealthRiskQ3_" + bmiCategory);
                Log.d("HealthRiskAssessment", "Auto-selected BMI: " + bmiCategory + " for BMI: " + bmi);
            }
        }
    }

    private void autoSelectWaist(PersonData personData) {
        if (personData.getWaistCircumference() != null && personData.getGender() != null) {
            double waist = personData.getWaistCircumference();
            String gender = personData.getGender().toLowerCase();
            String waistCategory = "";

            if (gender.equals("male") || gender.equals("ชาย") || gender.equals("m") || gender.equals("1")) {
                waistCategory = waist < 90 ? "1" : "2";
            } else if (gender.equals("female") || gender.equals("หญิง") || gender.equals("f") || gender.equals("2")) {
                waistCategory = waist < 80 ? "1" : "2";
            }

            if (!waistCategory.isEmpty()) {
                healthRiskAssessmentInfo.setHealthRiskQ4(waistCategory);
                selectRadioButtonSilently("rdoHealthRiskQ4_" + waistCategory);
                Log.d("HealthRiskAssessment", "Auto-selected waist: " + waistCategory + " for waist: " + waist + " cm");
            }
        }
    }

    private void autoSelectHypertension(PersonData personData) {
        if (personData.hasHypertension() != null) {
            String bpCategory = personData.hasHypertension() ? "2" : "1";
            healthRiskAssessmentInfo.setHealthRiskQ5(bpCategory);
            selectRadioButtonSilently("rdoHealthRiskQ5_" + bpCategory);
            Log.d("HealthRiskAssessment", "Auto-selected hypertension: " + bpCategory);
        }
    }

    private void autoSelectFamilyHistory(PersonData personData) {
        if (personData.hasFamilyDiabetesHistory() != null) {
            String familyHistoryCategory = personData.hasFamilyDiabetesHistory() ? "2" : "1";
            healthRiskAssessmentInfo.setHealthRiskQ6(familyHistoryCategory);
            selectRadioButtonSilently("rdoHealthRiskQ6_" + familyHistoryCategory);
            Log.d("HealthRiskAssessment", "Auto-selected family diabetes history: " + familyHistoryCategory);
        }
    }

    private void autoFillGlucoseValues(PersonData personData) {
        // ตรวจสอบว่า EditText กำลัง focus อยู่หรือไม่
        boolean fcbgHasFocus = editFcbg.hasFocus();
        boolean fpgHasFocus = editFpg.hasFocus();

        // Handle FCBG - ไม่ auto-fill ถ้าผู้ใช้กำลังพิมพ์อยู่
        if (!fcbgHasFocus) {
            if (personData.getFcbg() != null) {
                String fcbgValue = String.valueOf(personData.getFcbg());
                setTextSilently(editFcbg, fcbgValue);
                healthRiskAssessmentInfo.setFcbg(fcbgValue);
                Log.d("HealthRiskAssessment", "Auto-filled FCBG: " + fcbgValue);
            } else {
                setTextSilently(editFcbg, "");
                healthRiskAssessmentInfo.setFcbg("");
            }
        }

        // Handle FPG - ไม่ auto-fill ถ้าผู้ใช้กำลังพิมพ์อยู่
        if (!fpgHasFocus) {
            if (personData.getFpg() != null) {
                String fpgValue = String.valueOf(personData.getFpg());
                setTextSilently(editFpg, fpgValue);
                healthRiskAssessmentInfo.setFpg(fpgValue);
                Log.d("HealthRiskAssessment", "Auto-filled FPG: " + fpgValue);
            } else {
                setTextSilently(editFpg, "");
                healthRiskAssessmentInfo.setFpg("");
            }
        }

        // Update glucose highlighting based on current values
        updateGlucoseHighlightFromCurrentData();
    }
    private String validateAndFormatNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            // ลบช่องว่างและตัวอักษรที่ไม่ใช่ตัวเลขหรือจุดทศนิยม
            String cleaned = input.replaceAll("[^0-9.]", "");

            // ตรวจสอบว่ามีจุดทศนิยมมากกว่า 1 จุดหรือไม่
            String[] parts = cleaned.split("\\.");
            if (parts.length > 2) {
                // มีจุดทศนิยมมากกว่า 1 จุด ให้เก็บแค่ 2 ส่วนแรก
                cleaned = parts[0] + "." + parts[1];
            }

            // ตรวจสอบว่าเป็นตัวเลขที่ถูกต้องหรือไม่
            if (!cleaned.isEmpty()) {
                Double.parseDouble(cleaned);
            }

            return cleaned;
        } catch (NumberFormatException e) {
            return input; // คืนค่าเดิมถ้าไม่สามารถ format ได้
        }
    }
    private void setupFocusListeners() {
        editFcbg.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // เมื่อผู้ใช้พิมพ์เสร็จแล้ว ให้ validate ค่า
                String text = editFcbg.getText().toString().trim();
                String validatedText = validateAndFormatNumber(text);

                if (!text.equals(validatedText)) {
                    setTextSilently(editFcbg, validatedText);
                    healthRiskAssessmentInfo.setFcbg(validatedText);
                }
            }
        });

        editFpg.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // เมื่อผู้ใช้พิมพ์เสร็จแล้ว ให้ validate ค่า
                String text = editFpg.getText().toString().trim();
                String validatedText = validateAndFormatNumber(text);

                if (!text.equals(validatedText)) {
                    setTextSilently(editFpg, validatedText);
                    healthRiskAssessmentInfo.setFpg(validatedText);
                }
            }
        });
    }
    private void updateGlucoseHighlightFromCurrentData() {
        // ตรวจสอบค่า FCBG และ FPG ปัจจุบัน
        String fcbgStr = healthRiskAssessmentInfo.getFcbg();
        String fpgStr = healthRiskAssessmentInfo.getFpg();

        Double glucoseValue = null;

        // ใช้ค่าที่มีข้อมูลสำหรับ highlight (ให้ความสำคัญกับ FPG ก่อน)
        if (fpgStr != null && !fpgStr.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fpgStr);
            } catch (NumberFormatException e) {
                // ถ้า FPG ไม่ใช่ตัวเลข ให้ลอง FCBG
            }
        }

        if (glucoseValue == null && fcbgStr != null && !fcbgStr.isEmpty()) {
            try {
                glucoseValue = Double.parseDouble(fcbgStr);
            } catch (NumberFormatException e) {
                // ถ้าทั้งคู่ไม่ใช่ตัวเลข ให้ clear highlight
            }
        }

        if (glucoseValue != null) {
            highlightGlucoseRow(glucoseValue);
        } else {
            clearGlucoseHighlight();
        }
    }

    private void selectRadioButtonSilently(String radioButtonName) {
        try {
            int radioButtonId = getResources().getIdentifier(
                    radioButtonName, "id", requireContext().getPackageName());

            if (radioButtonId != 0) {
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                    Log.d("HealthRiskAssessment", "Silently selected: " + radioButtonName);
                } else {
                    Log.w("HealthRiskAssessment", "RadioButton not found: " + radioButtonName);
                }
            } else {
                Log.w("HealthRiskAssessment", "RadioButton ID not found for: " + radioButtonName);
            }
        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error selecting radio button silently: " + radioButtonName);
        }
    }

    public void setPersonDataFromOtherScreens(Bundle personDataBundle) {
        PersonData personData = new PersonData();

        try {
            if (personDataBundle.containsKey("age")) {
                personData.setAge(personDataBundle.getInt("age"));
            }
            if (personDataBundle.containsKey("gender")) {
                personData.setGender(personDataBundle.getString("gender"));
            }
            if (personDataBundle.containsKey("bmi")) {
                personData.setBmi(personDataBundle.getDouble("bmi"));
            }
            if (personDataBundle.containsKey("waist_circumference")) {
                personData.setWaistCircumference(personDataBundle.getDouble("waist_circumference"));
            }
            if (personDataBundle.containsKey("has_hypertension")) {
                personData.setHasHypertension(personDataBundle.getBoolean("has_hypertension"));
            }
            if (personDataBundle.containsKey("family_diabetes_history")) {
                personData.setHasFamilyDiabetesHistory(personDataBundle.getBoolean("family_diabetes_history"));
            }
            if (personDataBundle.containsKey("fcbg")) {
                personData.setFcbg(personDataBundle.getDouble("fcbg"));
            }
            if (personDataBundle.containsKey("fpg")) {
                personData.setFpg(personDataBundle.getDouble("fpg"));
            }

            autoSelectFromPersonData(personData);

        } catch (Exception e) {
            Log.e("HealthRiskAssessment", "Error in setPersonDataFromOtherScreens: " + e.getMessage());
        }
    }

    public HealthRiskAssessmentInfo getHealthRiskAssessmentInfo() {
        return healthRiskAssessmentInfo;
    }

    private int calculateTotalScore() {
        int totalScore = 0;

        // คะแนนอายุ
        if ("3".equals(healthRiskAssessmentInfo.getHealthRiskQ1())) totalScore += 1;  // 45-49 ปี
        if ("4".equals(healthRiskAssessmentInfo.getHealthRiskQ1())) totalScore += 2;  // 50 ปีขึ้นไป

        // คะแนนเพศ (ชาย = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ2())) totalScore += 2;

        // คะแนน BMI
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ3())) totalScore += 3;  // 23-27.5
        if ("3".equals(healthRiskAssessmentInfo.getHealthRiskQ3())) totalScore += 5;  // >= 27.5

        // คะแนนรอบเอว (เกิน = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ4())) totalScore += 2;

        // คะแนนความดัน (มี = 2 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ5())) totalScore += 2;

        // คะแนนประวัติครอบครัว (มี = 4 คะแนน)
        if ("2".equals(healthRiskAssessmentInfo.getHealthRiskQ6())) totalScore += 4;

        return totalScore;
    }

    private void highlightScoreRow(int totalScore) {
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        int highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);

        TableRow row1 = getView().findViewById(R.id.scoreRow1);
        TableRow row2 = getView().findViewById(R.id.scoreRow2);
        TableRow row3 = getView().findViewById(R.id.scoreRow3);
        TableRow row4 = getView().findViewById(R.id.scoreRow4);

        // รีเซ็ตสีพื้นหลัง
        row1.setBackgroundColor(white);
        row2.setBackgroundColor(light_gray);
        row3.setBackgroundColor(white);
        row4.setBackgroundColor(light_gray);

        // ไฮไลท์แถวตามคะแนน
        if (totalScore <= 2) {
            row1.setBackgroundColor(highlightColor);
        } else if (totalScore >= 3 && totalScore <= 5) {
            row2.setBackgroundColor(highlightColor);
        } else if (totalScore >= 6 && totalScore <= 8) {
            row3.setBackgroundColor(highlightColor);
        } else if (totalScore > 8) {
            row4.setBackgroundColor(highlightColor);
        }

        TextView resultTextView = getView().findViewById(R.id.resultHealthRiskScrollView);
        if (resultTextView != null) {
            resultTextView.setText(String.format("คะแนนที่ได้: %d คะแนน", totalScore));
        }
    }

    private void updateScoreAndHighlight() {
        int totalScore = calculateTotalScore();
        highlightScoreRow(totalScore);
    }

    private void highlightGlucoseRow(double glucoseValue) {
        TableRow row1 = getView().findViewById(R.id.glucoseRow1);
        TableRow row2 = getView().findViewById(R.id.glucoseRow2);
        TableRow row3 = getView().findViewById(R.id.glucoseRow3);

        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        int highlight = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);

        row1.setBackgroundColor(white);
        row2.setBackgroundColor(light_gray);
        row3.setBackgroundColor(white);

        if (glucoseValue < 100) {
            row1.setBackgroundColor(highlight);
        } else if (glucoseValue >= 100 && glucoseValue <= 125) {
            row2.setBackgroundColor(highlight);
        } else if (glucoseValue >= 126) {
            row3.setBackgroundColor(highlight);
        }
    }


    private void setupGlucoseInputListeners() {
        // FCBG Listener
        editFcbg.addTextChangedListener(new TextWatcher() {
            private boolean isInternalUpdate = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingFromTextWatcher || isAutoSelecting || isLoadingExistingData || isInternalUpdate) {
                    return;
                }

                String text = s.toString().trim();

                // บันทึกตำแหน่ง cursor ปัจจุบัน
                int cursorPosition = editFcbg.getSelectionStart();

                if (text.isEmpty()) {
                    // กรณีลบข้อความหมด - clear ข้อมูล
                    healthRiskAssessmentInfo.setFcbg("");
                    clearGlucoseHighlight();
                    notifyDataPasser();
                } else {
                    try {
                        double fcbgValue = Double.parseDouble(text);
                        healthRiskAssessmentInfo.setFcbg(text);
                        highlightGlucoseRow(fcbgValue);
                        notifyDataPasser();
                    } catch (NumberFormatException e) {
                        // กรณีกรอกข้อมูลไม่ถูกต้อง - เก็บข้อความเอาไว้แต่ไม่ highlight
                        healthRiskAssessmentInfo.setFcbg(text);
                        clearGlucoseHighlight();
                        notifyDataPasser();
                    }
                }
            }
        });

        // FPG Listener
        editFpg.addTextChangedListener(new TextWatcher() {
            private boolean isInternalUpdate = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdatingFromTextWatcher || isAutoSelecting || isLoadingExistingData || isInternalUpdate) {
                    return;
                }

                String text = s.toString().trim();

                // บันทึกตำแหน่ง cursor ปัจจุบัน
                int cursorPosition = editFpg.getSelectionStart();

                if (text.isEmpty()) {
                    // กรณีลบข้อความหมด - clear ข้อมูล
                    healthRiskAssessmentInfo.setFpg("");
                    clearGlucoseHighlight();
                    notifyDataPasser();
                } else {
                    try {
                        double fpgValue = Double.parseDouble(text);
                        healthRiskAssessmentInfo.setFpg(text);
                        highlightGlucoseRow(fpgValue);
                        notifyDataPasser();
                    } catch (NumberFormatException e) {
                        // กรณีกรอกข้อมูลไม่ถูกต้อง - เก็บข้อความเอาไว้แต่ไม่ highlight
                        healthRiskAssessmentInfo.setFpg(text);
                        clearGlucoseHighlight();
                        notifyDataPasser();
                    }
                }
            }
        });
    }
    // เพิ่ม method ใหม่สำหรับ clear glucose highlight
    private void clearGlucoseHighlight() {
        TableRow row1 = getView().findViewById(R.id.glucoseRow1);
        TableRow row2 = getView().findViewById(R.id.glucoseRow2);
        TableRow row3 = getView().findViewById(R.id.glucoseRow3);

        int white = ContextCompat.getColor(requireContext(), R.color.white);
        int light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);

        // รีเซ็ตสีพื้นหลังเป็นสีเดิม
        row1.setBackgroundColor(white);
        row2.setBackgroundColor(light_gray);
        row3.setBackgroundColor(white);
    }



    // ลบ method setPersonDataAndAutoSelect() และ selectRadioButton() ที่ซ้ำออก
}