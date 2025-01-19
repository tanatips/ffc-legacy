package th.in.ffc.person;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import th.in.ffc.R;
public class CardiovascularRiskFragment extends Fragment {

    private EditText edtAge, edtGender, edtBP, edtWaist, edtHeight, edtCholesterol;
    private CheckBox cbSmoking, cbDiabetes;
    private RadioGroup rgRiskLevel;
    private EditText edtRecommendation;

    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;

    public CardiovascularRiskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cardiovascular_risk, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        // ผูกตัวแปรกับ View elements
        edtAge = view.findViewById(R.id.edtAge);
//        edtGender = view.findViewById(R.id.edtGender);
        edtBP = view.findViewById(R.id.edtBP);
        edtWaist = view.findViewById(R.id.edtWaist);
        edtHeight = view.findViewById(R.id.edtHeight);
        edtCholesterol = view.findViewById(R.id.edtCholesterol);

        cbSmoking = view.findViewById(R.id.cbSmoking);
        cbDiabetes = view.findViewById(R.id.cbDiabetes);

//        rgRiskLevel = view.findViewById(R.id.rgRiskLevel);
        edtRecommendation = view.findViewById(R.id.edtRecommendation);

        rgGender = view.findViewById(R.id.rgGender);
        rbMale = view.findViewById(R.id.rbMale);
        rbFemale = view.findViewById(R.id.rbFemale);

        // เพิ่ม Listeners
        setupListeners();
    }

    private void setupListeners() {
        // ตัวอย่าง listener สำหรับการกดปุ่ม radio
//        rgRiskLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                updateRecommendation(checkedId);
//            }
//        });

        // Listener สำหรับ CheckBox
        cbSmoking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateRisk();
            }
        });

        cbDiabetes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                calculateRisk();
            }
        });

        // TextWatcher สำหรับ EditText ที่รับตัวเลข
        TextWatcher numberWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    calculateRisk();
                }
            }
        };

        // เพิ่ม TextWatcher ให้กับ EditText ทั้งหมด
        edtAge.addTextChangedListener(numberWatcher);
        edtBP.addTextChangedListener(numberWatcher);
        edtWaist.addTextChangedListener(numberWatcher);
        edtHeight.addTextChangedListener(numberWatcher);
        edtCholesterol.addTextChangedListener(numberWatcher);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String gender = "";
                if (checkedId == R.id.rbMale) {
                    gender = "ชาย";
                } else if (checkedId == R.id.rbFemale) {
                    gender = "หญิง";
                }
                // ใช้ค่า gender ตามต้องการ
                calculateRisk();
            }
        });
    }

    private void calculateRisk() {
        try {
            // ดึงค่าจาก EditText และ RadioGroup
            String age = edtAge.getText().toString();

            // ดึงค่าเพศจาก RadioGroup
            String gender = "";
            if (rgGender.getCheckedRadioButtonId() == R.id.rbMale) {
                gender = "ชาย";
            } else if (rgGender.getCheckedRadioButtonId() == R.id.rbFemale) {
                gender = "หญิง";
            }

            String bp = edtBP.getText().toString();
            String waist = edtWaist.getText().toString();
            String height = edtHeight.getText().toString();
            String cholesterol = edtCholesterol.getText().toString();

            // ดึงค่าจาก CheckBox
            boolean isSmoking = cbSmoking.isChecked();
            boolean hasDiabetes = cbDiabetes.isChecked();

            // ตรวจสอบว่ามีข้อมูลครบหรือไม่
            if (validateInput(age, bp, waist, height) && !gender.isEmpty()) {
                // คำนวณความเสี่ยง
                double riskScore = calculateRiskScore(
                        Integer.parseInt(age),
                        gender,
                        Integer.parseInt(bp),
                        Integer.parseInt(waist),
                        Integer.parseInt(height),
                        cholesterol.isEmpty() ? 0 : Integer.parseInt(cholesterol),
                        isSmoking,
                        hasDiabetes
                );

                // แสดงผลลัพธ์
                showRiskLevel(riskScore);
            } else {
                if (gender.isEmpty()) {
                    Toast.makeText(getContext(), "กรุณาเลือกเพศ", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            // จัดการกรณีที่ข้อมูลไม่ถูกต้อง
            Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ถูกต้อง", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String... inputs) {
        for (String input : inputs) {
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบ", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // เพิ่มการตรวจสอบเพศ
        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "กรุณาเลือกเพศ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
    private void showRiskLevel(double score) {
        // เลือก RadioButton ตามระดับความเสี่ยง
//        if (score < 5) {
//            rgRiskLevel.check(R.id.rbLowRisk);
//        } else if (score < 10) {
//            rgRiskLevel.check(R.id.rbMediumRisk);
//        } else {
//            rgRiskLevel.check(R.id.rbHighRisk);
//        }
    }

    private void updateRecommendation(int checkedId) {
        String recommendation = "";
//        if (checkedId == R.id.rbLowRisk) {
//            recommendation = "แนะนำให้ควบคุมปัจจัยเสี่ยงและตรวจสุขภาพประจำปี";
//        } else if (checkedId == R.id.rbMediumRisk) {
//            recommendation = "แนะนำให้ปรับเปลี่ยนพฤติกรรมและติดตามผลทุก 6 เดือน";
//        } else if (checkedId == R.id.rbHighRisk) {
//            recommendation = "แนะนำให้พบแพทย์เพื่อประเมินและวางแผนการรักษา";
//        }
        edtRecommendation.setText(recommendation);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // ถ้าต้องการส่งข้อมูลกลับไปยัง Activity
            // dataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }
}