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

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;

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

    public CardiovascularRiskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if (shareViewModel == null) {
            cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
            shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
        }
        cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
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

        cardiovascularRiskInfo = new CardiovascularRiskInfo();

        // Load existing data if available
        loadData();
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

        // เพิ่ม Listeners
        setupListeners();
    }
    private void loadData() {
        try {
            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(getContext());
            SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            viewModel.getCardiovascularRiskLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
                if (data != null && data.getPersonId() != null) {
                    List<CardiovascularRiskInfo> riskInfos = sfCardiovascularRiskInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                    for (CardiovascularRiskInfo riskInfo : riskInfos) {
                        Log.d("CardiovascularRiskFragment", "loadData: riskInfo found: " + riskInfo);
                        setCardiovascularRiskInfo(riskInfo);
                        // ส่งข้อมูลผ่าน interface ไปยัง Activity
                        dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    }

                    // ถ้าไม่พบข้อมูล ให้ดึงข้อมูลพื้นฐานจาก PersonInfo
                    if (riskInfos.isEmpty()) {
                        // ดึงข้อมูลพื้นฐานจาก PersonInfo
                        try {
                            SfPersonInfoDao personInfoDao = new SfPersonInfoDao(getContext());
                            List<PersonInfo> personInfos = personInfoDao.getSfPersonInfoById(Integer.valueOf(data.getPersonId()));
                            if (!personInfos.isEmpty()) {
                                PersonInfo personInfo = personInfos.get(0);

                                // ตั้งค่า personId และ idcard
                                cardiovascularRiskInfo.setPersonId(data.getPersonId());
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

                                // คำนวณอายุจากวันเกิด
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

                                // ส่งข้อมูลผ่าน interface ไปยัง Activity
                                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                            }
                        } catch (Exception e) {
                            Log.e("CardiovascularRiskFragment", "Error loading initial data: " + e.getMessage());
                        }
                    }
                }

            });
        }
        catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error loading data: " + e.getMessage());
        }
    }
    private void highlightRiskRowByPercentage(double riskPercentage) {
        View view = getView();
        if (view == null) return;

        try {
            // ดึงอ้างอิงถึง TableRow ทั้งหมด
            TableRow lowRiskRow = view.findViewById(R.id.lowRiskRow);
            TableRow mediumRiskRow = view.findViewById(R.id.mediumRiskRow);
            TableRow highRiskRow = view.findViewById(R.id.highRiskRow);

            if (lowRiskRow == null || mediumRiskRow == null || highRiskRow == null) {
                Log.e("CardiovascularRiskFragment", "Cannot find risk rows");
                return;
            }

            // สีที่ใช้ในการไฮไลท์
            int highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
            int normalColor1 = Color.WHITE;
            int normalColor2 = ContextCompat.getColor(requireContext(), R.color.light_gray);

            // กำหนดสีให้ทุกแถวเป็นปกติก่อน
            lowRiskRow.setBackgroundColor(normalColor1);
            mediumRiskRow.setBackgroundColor(normalColor2);
            highRiskRow.setBackgroundColor(normalColor1);

            // ไฮไลท์แถวตามระดับความเสี่ยง (%)
            if (riskPercentage < 10.0) {
                // กลุ่มเสี่ยงน้อย
                lowRiskRow.setBackgroundColor(highlightColor);
//                updateRecommendation("low");
            } else if (riskPercentage >= 10.0 && riskPercentage <= 20.0) {
                // กลุ่มเสี่ยงปานกลาง
                mediumRiskRow.setBackgroundColor(highlightColor);
//                updateRecommendation("medium");
            } else if (riskPercentage > 20.0) {
                // กลุ่มเสี่ยงสูง
                highRiskRow.setBackgroundColor(highlightColor);
//                updateRecommendation("high");
            }

            Log.d("CardiovascularRiskFragment", "Highlighted risk row for percentage: " + riskPercentage + "%");

        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error highlighting risk row: " + e.getMessage());
        }
    }
//    private void showRiskLevel(double score) {
//        highlightRiskRow((int)Math.ceil(score));
//    }
//    private void highlightRiskRow(int score) {
//        // หาแถวที่ต้องการไฮไลท์
//        View view = getView();
//        if (view == null) return;
//
//        try {
//            // ดึงอ้างอิงถึง TableRow ทั้งหมด
//            TableRow row1 = view.findViewById(R.id.headerRow); // Row เริ่มต้น
//            if (row1 == null) {
//                Log.e("CardiovascularRiskFragment", "Cannot find headerRow");
//                return;
//            }
//
//            ViewGroup parent = (ViewGroup) row1.getParent();
//            if (parent == null || parent.getChildCount() < 4) {
//                Log.e("CardiovascularRiskFragment", "Parent is null or does not have enough children");
//                return;
//            }
//
//            TableRow row2 = (TableRow) parent.getChildAt(2); // Row ที่ 2
//            TableRow row3 = (TableRow) parent.getChildAt(3); // Row ที่ 3
//
//            if (row2 == null || row3 == null) {
//                Log.e("CardiovascularRiskFragment", "Cannot find row2 or row3");
//                return;
//            }
//
//            // สีที่ใช้ในการไฮไลท์
//            int highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
//
//            // กำหนดสีให้ทุกแถวเป็นปกติก่อน
//            row1.setBackgroundColor(Color.WHITE);
//            row2.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray));
//            row3.setBackgroundColor(Color.WHITE);
//
//            // ไฮไลท์แถวตามระดับความเสี่ยง
//            if (score <= 2) {
//                row1.setBackgroundColor(highlightColor);
//            } else if (score >= 3 && score <= 5) {
//                row2.setBackgroundColor(highlightColor);
//            } else if (score >= 6) {
//                row3.setBackgroundColor(highlightColor);
//            }
//        } catch (Exception e) {
//            Log.e("CardiovascularRiskFragment", "Error highlighting risk row: " + e.getMessage());
//        }
//    }
//
    public void setCardiovascularRiskInfo(CardiovascularRiskInfo info) {
        this.cardiovascularRiskInfo = info;
        loadExistingData();
    }
    private void loadExistingData() {
        if (cardiovascularRiskInfo == null) return;

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
        if(cardiovascularRiskInfo.getRiskPercentage() != null) {
            edtRiskPercentage.setText(cardiovascularRiskInfo.getRiskPercentage());
        }

        if(cardiovascularRiskInfo.getRiskLevel() != null) {
            edtRiskLevel.setText(cardiovascularRiskInfo.getRiskLevel());
        }

        // ถ้ามีข้อมูลที่จำเป็น ให้คำนวณความเสี่ยง
//        try {
//            if (cardiovascularRiskInfo.getAge() != null &&
//                    cardiovascularRiskInfo.getGender() != null &&
//                    cardiovascularRiskInfo.getBloodPressure() != null &&
//                    cardiovascularRiskInfo.getWaistSize() != null) {
//                calculateRisk();
//            }
//        } catch (Exception e) {
//            // จัดการข้อผิดพลาดที่อาจเกิดขึ้น
//            Toast.makeText(getContext(), "เกิดข้อผิดพลาดในการโหลดข้อมูล", Toast.LENGTH_SHORT).show();
//        }
    }
    public CardiovascularRiskInfo getCardiovascularRiskInfo() {
        return cardiovascularRiskInfo;
    }
    private void setupListeners() {
        // Listener สำหรับ EditText
        edtRiskPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable s) {
                // ป้องกันการเกิด loop โดยไม่เรียก calculateRisk() อีก
                if (!TextUtils.isEmpty(s)) {
                    try {
                        double percentage = Double.parseDouble(s.toString());

                        // อัพเดท Model
                        cardiovascularRiskInfo.setRiskPercentage(s.toString());
                        cardiovascularRiskLiveData.setRiskPercentage(percentage);
                        shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                        dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);

                        // ไฮไลท์แถวตาม %
                        highlightRiskRowByPercentage(percentage);

                    } catch (NumberFormatException e) {
                        Log.e("CardiovascularRiskFragment", "Invalid percentage format: " + s.toString());
                    }
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
                if (!TextUtils.isEmpty(s)) {
                    try {
                        double level = Double.parseDouble(s.toString());

                        // อัพเดท Model
                        cardiovascularRiskInfo.setRiskLevel(s.toString());
                        cardiovascularRiskLiveData.setRiskLevel(level);
                        shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                        dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);

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
                cardiovascularRiskInfo.setIsSmoking(isChecked ? "1" : "0");
                cardiovascularRiskLiveData.setIsSmoking(isChecked);
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                calculateRisk();
            }
        });

        cbDiabetes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cardiovascularRiskInfo.setHasDiabetes(isChecked ? "1" : "0");
                cardiovascularRiskLiveData.setHasDiabetes(isChecked);
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                calculateRisk();
            }
        });
        edtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cardiovascularRiskInfo.setAge(s.toString());
                    cardiovascularRiskLiveData.setAge(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    calculateRisk();
                }
            }
        });
        // TextWatcher for blood pressure
        edtBP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cardiovascularRiskInfo.setBloodPressure(s.toString());
                    cardiovascularRiskLiveData.setBloodPressure(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    calculateRisk();
                }
            }
        });
        edtWaist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cardiovascularRiskInfo.setWaistSize(s.toString());
                    cardiovascularRiskLiveData.setWaistSize(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    calculateRisk();
                }
            }
        });
        // TextWatcher for height
        edtHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cardiovascularRiskInfo.setHeight(s.toString());
                    cardiovascularRiskLiveData.setHeight(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    calculateRisk();
                }
            }
        });

        // TextWatcher for cholesterol
        edtCholesterol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    cardiovascularRiskInfo.setCholesterol(s.toString());
                    cardiovascularRiskLiveData.setCholesterol(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                    calculateRisk();
                }
            }
        });
        // TextWatcher for recommendation
        edtRecommendation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    cardiovascularRiskInfo.setRecommendation(s.toString());
                    cardiovascularRiskLiveData.setRecommendation(s.toString());
                    shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                    dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);
                }
            }
        });
        // ปรับปรุง Gender RadioGroup Listener เพื่อตั้งค่าเพศใน Model
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String gender = "";
                if (checkedId == R.id.rbMale) {
                    gender = "M"; // ตั้งค่าเป็น M เพื่อให้สอดคล้องกับระบบ
                    cardiovascularRiskInfo.setGender("M");
                } else if (checkedId == R.id.rbFemale) {
                    gender = "F"; // ตั้งค่าเป็น F เพื่อให้สอดคล้องกับระบบ
                    cardiovascularRiskInfo.setGender("F");
                }

                // เพิ่มการอัปเดต LiveData
                cardiovascularRiskLiveData.setSelectedGender(checkedId);
                shareViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);
                dataPasser.onCardiovascularRiskInfo(cardiovascularRiskInfo);

                // ทำการคำนวณความเสี่ยงใหม่
                calculateRisk();
            }
        });
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
                highlightRiskRowByPercentage(riskPercentage);

                Log.d("CardiovascularRiskFragment", "Risk calculated: " + riskPercentage + "%");
            } else {
                // ถ้าไม่มี % ให้ล้างการไฮไลท์
                clearRiskHighlight();
            }

        } catch (NumberFormatException e) {
            Log.e("CardiovascularRiskFragment", "Error calculating risk: " + e.getMessage());
            clearRiskHighlight();
        }
    }
    private void clearRiskHighlight() {
        View view = getView();
        if (view == null) return;

        try {
            TableRow lowRiskRow = view.findViewById(R.id.lowRiskRow);
            TableRow mediumRiskRow = view.findViewById(R.id.mediumRiskRow);
            TableRow highRiskRow = view.findViewById(R.id.highRiskRow);

            if (lowRiskRow != null && mediumRiskRow != null && highRiskRow != null) {
                lowRiskRow.setBackgroundColor(Color.WHITE);
                mediumRiskRow.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray));
                highRiskRow.setBackgroundColor(Color.WHITE);
            }
        } catch (Exception e) {
            Log.e("CardiovascularRiskFragment", "Error clearing risk highlight: " + e.getMessage());
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

            return !riskPercentageStr.isEmpty() &&
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

}