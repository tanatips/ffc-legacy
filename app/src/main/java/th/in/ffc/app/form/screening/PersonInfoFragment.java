package th.in.ffc.app.form.screening;


import static th.in.ffc.util.DateConverter.convertToWesternDate;
import static th.in.ffc.util.TransactionIdGenerator.generateTransId;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.ThaiIdSmartcardReader;
import th.in.ffc.api.nhso.ApiResponse;
import th.in.ffc.api.nhso.AuthenCodeRequest;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.screening.dao.DistrictDao;
import th.in.ffc.app.form.screening.dao.PersonDao;
import th.in.ffc.app.form.screening.dao.ProvinceDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.dao.SubDistrictDao;
import th.in.ffc.app.form.screening.model.DistrictInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.ProvinceInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.app.form.screening.model.SubDistrictInfo;

import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.BMICalculator;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.NetworkUtils;
import th.in.ffc.util.ProfileImage;
import th.in.ffc.util.ThaiDatePickerDialog;
import th.in.ffc.widget.SearchableSpinner;

import com.berry_med.monitordemo.activity.DeviceMainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonInfoFragment extends Fragment {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1001; ;
    int SMART_CARD_READER_CODE = 0;

//    ThaiDatePicker birthday;
    RadioGroup rdoGender;

    RadioButton rdoMale,rdoFemale;

    private OnDataPass dataPasser;
    PersonInfo personInfo;

    TextInputEditText citizenId, fname,lname ;
    private TextInputEditText txtPhoneNo, txtHn,txtBirthDay;
    private TextInputEditText txtAuthenDate, txtAuthenNo, txtWeight, txtHeight;
    private TextInputEditText txtWaistCircumference, txtBp, txtBmi;
    private TextInputEditText txtSymptomsPressure, txtDiastolicPressure;
    private TextInputEditText txtHomeNo,txtVillageNo,txtPostalCode,txtTemperature;
    private ImageButton smartcardReader,imgPermission, btnDeviceSsp;
    private TextInputEditText currentEditText;

    private Map<EditText, TextFieldUpdater> fieldUpdaters;
    private BMICalculator bmiCalculator;
//    private TextView dateTextView;
    private ImageButton selectDateButton, btnAuthenCode;
    private AutoCompleteTextView spinnerProvince, spinnerDistrict, spinnerSubDistrict;

    String provinceCode, districtCode, subDistrictCode;
    String provinceName, districtName, subDistrictName;

    List<ProvinceInfo> provinceInfos;

    List<DistrictInfo> districtInfos;

    List<SubDistrictInfo> subDistrictInfos;
//    private ProgressDialog progressDialog;

    private ProgressBar progressBar;

    private FrameLayout progressBarContainer;
    private TextView progressBarText;

    private SearchableSpinner house;

    private int DEVICE_RESULT_ONE = 101;
    private ImageView imgPerson;

    private String pcuCode;
    private boolean isDialogShowing = false;
    private boolean isValidatingIdCard = false;

    private boolean isEditMode = false;
    private String originalIdCard = "";
    private boolean isValidAge = false;
    private int currentAge = 0;



    private interface TextFieldUpdater {
        void update(String value);
    }
    private void initializeFieldUpdaters() {
        this.fieldUpdaters = new HashMap<>();
        // Map each EditText to its corresponding update function
        fieldUpdaters.put(citizenId, value -> personInfo.setIdcard(value));
        fieldUpdaters.put(fname, value -> personInfo.setFname(value));
        fieldUpdaters.put(lname, value -> personInfo.setLname(value));
        fieldUpdaters.put(txtPhoneNo, value -> personInfo.setPhone(value));
        fieldUpdaters.put(txtHn, value -> personInfo.setHn(value));
        fieldUpdaters.put(txtAuthenDate, value -> personInfo.setAuthen_date(value));
        fieldUpdaters.put(txtAuthenNo, value -> personInfo.setAuthen_code(value));
        fieldUpdaters.put(txtWeight, value -> personInfo.setWeight(Double.valueOf(!Objects.equals(value, "") ?value:"0")));
        fieldUpdaters.put(txtHeight, value -> personInfo.setHeight(Double.valueOf(!Objects.equals(value, "") ?value:"0")));
        fieldUpdaters.put(txtWaistCircumference, value -> personInfo.setWaist_size(Double.valueOf(!Objects.equals(value, "") ?value:"0")));
        fieldUpdaters.put(txtBp, value -> personInfo.setBp(value));
        fieldUpdaters.put(txtBmi, value -> personInfo.setBmi(value));
        fieldUpdaters.put(txtSymptomsPressure, value -> personInfo.setSystolic_pressure(Double.valueOf(value)));
        fieldUpdaters.put(txtDiastolicPressure, value -> personInfo.setDiastolic_pressure(Double.valueOf(value)));
        fieldUpdaters.put(txtBirthDay, value -> personInfo.setBirthday(value));
        fieldUpdaters.put(txtHomeNo, value -> personInfo.setHomeNo(value));
        fieldUpdaters.put(txtVillageNo, value -> personInfo.setVillageNo(value));
        fieldUpdaters.put(txtTemperature, value -> personInfo.setTemperature(Double.valueOf(value)));

        districtInfos  = new ArrayList<>();
        subDistrictInfos = new ArrayList<>();
    }
    private boolean validateAge(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date birth = sdf.parse(birthDate);

            if (birth == null) {
                return false;
            }

            // คำนวณอายุ
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birth);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            // ปรับอายุถ้ายังไม่ถึงวันเกิดในปีนี้
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            currentAge = age;

            // ตรวจสอบช่วงอายุ: 15-59 ปี
            boolean isValid = (age >= 15 && age <= 59);

            Log.d("PersonInfoFragment", "Age validation - Age: " + age + ", Valid: " + isValid);

            return isValid;

        } catch (Exception e) {
            Log.e("PersonInfoFragment", "Error calculating age: " + e.getMessage());
            return false;
        }
    }
    private void showAgeValidationDialog(int age) {
        // ป้องกันการเปิด Dialog ซ้ำ
        if (isDialogShowing) {
            return;
        }

        isDialogShowing = true;

        // สร้าง custom view สำหรับ dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_age_warning, null);
        TextView tvMessage = dialogView.findViewById(R.id.tvAgeMessage);
        TextView tvAgeInfo = dialogView.findViewById(R.id.tvAgeInfo);

        String message;
        String ageInfo = "อายุปัจจุบันของท่าน: " + age + " ปี";

        if (age < 15) {
            message = "แบบประเมินนี้เหมาะสำหรับผู้ที่มีอายุ 15-59 ปี\n\n" +
                    "ท่านมีอายุน้อยกว่า 15 ปี จึงไม่สามารถทำแบบประเมินนี้ได้\n\n";
        } else {
            message = "แบบประเมินนี้เหมาะสำหรับผู้ที่มีอายุ 15-59 ปี\n\n" +
                    "ท่านมีอายุมากกว่า 59 ปี จึงไม่สามารถทำแบบประเมินนี้ได้\n\n";
        }

        tvMessage.setText(message);
        tvAgeInfo.setText(ageInfo);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("อายุไม่อยู่ในเกณฑ์")
                .setView(dialogView)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("ปิด", (dialog, which) -> {

                    clearInvalidAgeData();
                    isDialogShowing = false;
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> {
                    isDialogShowing = false;
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getAgeGroupDescription(int age) {
        if (age >= 15 && age <= 34) {
            return "กลุ่มอายุ 15-34 ปี: เหมาะสำหรับการคัดกรองพื้นฐาน";
        } else if (age >= 35 && age <= 59) {
            return "กลุ่มอายุ 35-59 ปี: เหมาะสำหรับการคัดกรองแบบละเอียด รวมถึงการตรวจเบาหวานและหัวใจ";
        } else {
            return "อายุไม่อยู่ในเกณฑ์สำหรับแบบประเมินนี้";
        }
    }
    private void showAgeGroupInfo(int age) {
        String ageGroupMsg = getAgeGroupDescription(age);

        // แสดง Toast ข้อมูลกลุ่มอายุ
        Toast.makeText(getContext(),
                "อายุ: " + age + " ปี\n" + ageGroupMsg,
                Toast.LENGTH_LONG).show();
    }
    private void clearInvalidAgeData() {
        // หยุดการทำงานของ TextWatcher ชั่วคราว
        isValidatingIdCard = true;

        try {
            // เคลียร์เลขบัตรประชาชน
            citizenId.setText("");
            citizenId.setError(null);
            personInfo.setIdcard("");

            // เคลียร์ชื่อ
            fname.setText("");
            fname.setError(null);
            personInfo.setFname("");

            // เคลียร์นามสกุล
            lname.setText("");
            lname.setError(null);
            personInfo.setLname("");

            // เคลียร์วันเกิด
            txtBirthDay.setText("");
            txtBirthDay.setError(null);
            personInfo.setBirthday("");

            // เคลียร์เพศ
            rdoMale.setChecked(false);
            rdoFemale.setChecked(false);
            personInfo.setGender("");

            // เคลียร์รูปภาพ
            imgPerson.setImageResource(R.drawable.ic_person);
            personInfo.setPhoto(null);

            // รีเซ็ตค่าการตรวจสอบอายุ
            currentAge = 0;
            isValidAge = false;

            // อัพเดท PersonInfo ใน dataPasser
            dataPasser.onPersonInfo(personInfo);

            // Focus ไปที่ช่องเลขบัตรประชาชน
            citizenId.requestFocus();

            Toast.makeText(getContext(), "เคลียร์ข้อมูลเนื่องจากอายุไม่อยู่ในเกณฑ์", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("PersonInfoFragment", "Error clearing invalid age data: " + e.getMessage());
        } finally {
            // คืนค่าสถานะการทำงานของ TextWatcher
            isValidatingIdCard = false;
        }
    }
    private void setupBmiInfoButton() {
        View view = getView();
        if (view != null) {
            ImageButton bmiInfoButton = view.findViewById(R.id.btnBmiInfo);
            if (bmiInfoButton != null) {
                bmiInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View dialogView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.activity_bmi_info, null);

                        TableLayout tableLayout = dialogView.findViewById(R.id.bmi_table);
                        String bmi = txtBmi.getText().toString();
                        highlightBMIRow(tableLayout,Double.valueOf(bmi));

                        new AlertDialog.Builder(requireContext())
                                .setTitle("เกณฑ์การแปลผลค่า BMI")
                                .setView(dialogView)
                                .setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    private void highlightBMIRow(TableLayout tableLayout, double bmi) {
                        // คืนค่าสีพื้นหลังเป็นค่าเริ่มต้นของทุกแถว
                        for (int i = 1; i < tableLayout.getChildCount(); i++) {
                            View row = tableLayout.getChildAt(i);
                            row.setBackgroundColor(i % 2 == 0 ?
                                    Color.parseColor("#F5F5F5") : Color.TRANSPARENT);
                        }

                        // ไฮไลท์แถวตามค่า BMI
                        int rowToHighlight;
                        int highlightColor = Color.parseColor("#FFE0B2"); // สีที่จะใช้ไฮไลท์

                        if (bmi < 18.5) rowToHighlight = 1;
                        else if (bmi <= 22.9) rowToHighlight = 2;
                        else if (bmi <= 24.9) rowToHighlight = 3;
                        else if (bmi <= 29.9) rowToHighlight = 4;
                        else rowToHighlight = 5;

                        // ไฮไลท์แถวที่ตรงกับค่า BMI
                        if (rowToHighlight < tableLayout.getChildCount()) {
                            View rowView = tableLayout.getChildAt(rowToHighlight);
                            rowView.setBackgroundColor(highlightColor);
                        }
                    }
                });
            }
        }
    }

    private void setupWeightInfoButton(){
        View view = getView();
        if (view != null) {
            ImageButton weightInfoButton = view.findViewById(R.id.btnWeightInfo);
            if (weightInfoButton != null) {

                weightInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View dialogView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.activity_waist_circumference, null);

                        TableLayout tableLayout = dialogView.findViewById(R.id.waistTable);
                        Double waistCircumference = Double.valueOf(txtWaistCircumference.getText().toString().equals("")?"0":txtWaistCircumference.getText().toString());
                        Boolean isMale = personInfo.getGender().equals("M");

                        highlightWaistRow(tableLayout,waistCircumference,isMale);
                        new AlertDialog.Builder(requireContext())
                                .setTitle("แปลผลเส้นรอบเอว(ประเมินภาวะอ้วน)")
                                .setView(dialogView)
                                .setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    private void highlightWaistRow(TableLayout tableLayout, double waistSize, boolean isMale) {
                        // คืนค่าสีพื้นหลังเป็นค่าเริ่มต้น
                        for (int i = 1; i < tableLayout.getChildCount(); i++) {
                            View row = tableLayout.getChildAt(i);
                            row.setBackgroundColor(i % 2 == 0 ?
                                    Color.parseColor("#F5F5F5") : Color.TRANSPARENT);
                        }
                        int rowToHighlight;
                        int highlightColor = Color.parseColor("#FFE0B2"); // สีที่จะใช้ไฮไลท์
                        if (isMale) {
                            if (waistSize < 90) {
                                rowToHighlight = 1;
                            } else {
                                rowToHighlight = 2;
                            }
                        } else {
                            if (waistSize < 80) {
                                rowToHighlight = 1;
                            } else {
                                rowToHighlight = 2;
                            }
                        }
                        if (rowToHighlight < tableLayout.getChildCount()) {
                            View rowView = tableLayout.getChildAt(rowToHighlight);
                            rowView.setBackgroundColor(highlightColor);
                        }
                    }
                });
            }
        }
    }

    private void setupBloodPressureButton(){
        View view = getView();
        if (view != null) {
            ImageButton bloodPressureButton = view.findViewById(R.id.btnBloodPressure);
            if (bloodPressureButton != null) {
                bloodPressureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View dialogView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.activity_blood_pressure_table, null);

                        TableLayout tableLayout = dialogView.findViewById(R.id.bloodPressureTable);
                        Double sym = Double.valueOf(txtSymptomsPressure.getText().toString().equals("")?"0":txtSymptomsPressure.getText().toString());
                        Double dia = Double.valueOf(txtDiastolicPressure.getText().toString().equals("")?"0":txtDiastolicPressure.getText().toString());

                        highlightRow(tableLayout,sym,dia);

                        new AlertDialog.Builder(requireContext())
                                .setTitle("แปลผลความเสี่ยงการเกิดโรคความดันโลหิตสูง")
                                .setView(dialogView)
                                .setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                    private void highlightRow(TableLayout tableLayout, Double sbp, Double dbp) {

                        for (int i = 1; i < tableLayout.getChildCount(); i++) {
                            View row = tableLayout.getChildAt(i);
                            row.setBackgroundColor(i % 2 == 0 ?
                                    Color.parseColor("#F5F5F5") : Color.TRANSPARENT);
                        }
                        int rowToHighlight = 0;
                        int highlightColor = Color.parseColor("#FFE0B2"); // สีที่จะใช้ไฮไลท์
                        if (sbp >= 140 && dbp < 90) {
                            tableLayout.getChildAt(7).setBackgroundColor(highlightColor); // ISH row
                            return;
                        }

                        if (sbp >= 180 || dbp >= 110) {
                            rowToHighlight = 6; // Hypertension ระดับ 3
                        }
                        else if (sbp >= 160 || dbp >= 100) {
                            rowToHighlight = 5; // Hypertension ระดับ 2
                        }
                        else if (sbp >= 140 || dbp >= 90) {
                            rowToHighlight = 4; // Hypertension ระดับ 1
                        }
                        else if (sbp >= 130 || dbp >= 85) {
                            rowToHighlight = 3; // High normal
                        }
                        else if (sbp >= 120 || dbp >= 80) {
                            rowToHighlight = 2; // Normal
                        }
                        else if (sbp < 120 && dbp < 80) {
                            rowToHighlight = 1; // Optimal
                        }
                        else {
                            return; // ไม่มีแถวที่ตรงกับเงื่อนไข
                        }
                        if (rowToHighlight < tableLayout.getChildCount()) {
                            View rowView = tableLayout.getChildAt(rowToHighlight);
                            rowView.setBackgroundColor(highlightColor);
                        }
                    }

                });
            }
        }
    }

    public void setCurrentEditText(TextInputEditText editText) {
        this.currentEditText = editText;
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
        view.post(() -> {
            progressBarContainer = requireActivity().findViewById(R.id.progressBarContainer);
            progressBarText = requireActivity().findViewById(R.id.progressBarText);
        });
        setupWeightInfoButton();
        setupBmiInfoButton();
        setupBloodPressureButton();
        setupDropdowns();
        UserSessionManager userSessionManager = new UserSessionManager(getContext());
        pcuCode = userSessionManager.getPcuCode();
    }
    private void showProgressBar(String message) {
        progressBarContainer = getActivity().findViewById(R.id.progressBarContainer);
        progressBarText = getActivity().findViewById(R.id.progressBarText);
        if (progressBarContainer != null && progressBarText != null) {
            progressBarText.setText(message);
            progressBarContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBarContainer != null) {
            progressBarContainer.setVisibility(View.GONE);
        }
    }
    private void initializeViews(View view) {
        personInfo = new PersonInfo();
        smartcardReader = view.findViewById(R.id.smartcard_reader);
        imgPermission = view.findViewById(R.id.imgPermission);
        citizenId = view.findViewById(R.id.citizenId);
        fname = view.findViewById(R.id.fname);
        lname = view.findViewById(R.id.lname);
        imgPerson = view.findViewById(R.id.imgPerson); // เพิ่มบรรทัดนี้

        rdoGender = view.findViewById(R.id.rdoGender);
        rdoMale = view.findViewById(R.id.rdoMale);
        rdoFemale = view.findViewById(R.id.rdoFemale);

        txtPhoneNo = view.findViewById(R.id.txtPhoneNo);
        txtHn = view.findViewById(R.id.txtHn);
        txtAuthenDate = view.findViewById(R.id.txtAuthenDate);
        txtAuthenNo = view.findViewById(R.id.txtAuthenNo);
        txtWeight = view.findViewById(R.id.txtWeight);
        txtHeight = view.findViewById(R.id.txtHeight);
        txtWaistCircumference = view.findViewById(R.id.txtWaistCircumference);
        txtBp = view.findViewById(R.id.txtBp);
        txtBmi = view.findViewById(R.id.txtBmi);
        txtSymptomsPressure = view.findViewById(R.id.txtSymptomsPressure);
        txtDiastolicPressure = view.findViewById(R.id.txtDiastolicPressure);
        bmiCalculator = new BMICalculator();
        btnAuthenCode = view.findViewById(R.id.btnAuthenCode);

        view.findViewById(R.id.smartcard_reader);

//        dateTextView = view.findViewById(R.id.dateTextView);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        txtBirthDay = view.findViewById(R.id.txtBirthDay);

// ค้นหาข้อมูล
//        PersonDao.PersonInfo person = personDao.getPersonByIdcard("1234567890123");
//        house = (SearchableSpinner) view.findViewById(R.id.spinnerHcode);
//        house.setDialog(getActivity().getSupportFragmentManager(),
//                HouseListDialog.class, "house");
// =
//        house.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                personInfo.setHcode(String.valueOf(house.getSelectedItemId()));
//                dataPasser.onPersonInfo(personInfo);
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });




        selectDateButton.setOnClickListener(v -> showDatePickerDialog(this.txtBirthDay,this.personInfo.getBirthday()));
        btnAuthenCode.setOnClickListener(v -> {

            if (!NetworkUtils.checkInternetAndShowDialog(getContext())) {
                return; // ออกจากเมธอดเมื่อไม่มีการเชื่อมต่อ
            }

            // ตรวจสอบการเชื่อมต่ออินเทอร์เน็ต
            if (!NetworkUtils.isInternetAvailable(getContext())) {
                NetworkUtils.showNoInternetDialog(getContext());
                return;
            }
            AuthenCodeRequest request = new AuthenCodeRequest();
            request.setPid(citizenId.getText().toString());
            request.setFirstName(fname.getText().toString());
            request.setLastName(lname.getText().toString());
            request.setSex(rdoMale.isChecked() ? "1" : "2");
            request.setBirthDay(convertToWesternDate(txtBirthDay.getText().toString()));
            request.setHn(txtHn.getText().toString());
            request.setHcode(pcuCode);
            request.setSourceId("A-MED");
            request.setTransId(generateTransId());
            request.setServiceCode("PG0060001");
            showProgressBar("Authenticating...");

            NhsoApiCaller apiCaller = new NhsoApiCaller(getContext());
            apiCaller.getAuthenCode(request, new NhsoApiCaller.AuthenCodeCallback() {
                @Override
                public void onSuccess(ApiResponse response) {
//                    dismissProgressDialog();
                    hideProgressBar();
                    if(response.getAuthenCode()!=null) {
                    txtAuthenNo.setText(response.getAuthenCode());
                    Calendar cal = Calendar.getInstance();
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int month = cal.get(Calendar.MONTH) + 1;
                    int yearBE = cal.get(Calendar.YEAR) + 543;
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    int second = cal.get(Calendar.SECOND);

                    // กำหนดวันที่และเวลาปัจจุบันให้กับ txtAuthenDate
                    String currentThaiDateTime = String.format(Locale.US, "%d/%d/%d %02d:%02d:%02d",
                            day, month, yearBE, hour, minute, second);
                    txtAuthenDate.setText(currentThaiDateTime);

                    // อัพเดทค่าใน personInfo - แปลงเป็นรูปแบบ yyyy-MM-dd HH:mm:ss
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)); // ใช้ปีคริสตศักราชตามปกติ
                    String westernDateTime = outputFormat.format(cal.getTime());
                    personInfo.setAuthen_date(westernDateTime);
//                    txtAuthenDate.setText(DateConverter.convertToThaiBuddhistDate(DateTime.getCurrentDate()))
                        personInfo.setAuthen_code(response.getAuthenCode());

                    }
                    else {
                        Toast.makeText(getContext(),response.getDataError(),Toast.LENGTH_SHORT);
                    }
//                    personInfo.setAuthen_date(DateConverter.convertToWesternDate(txtAuthenDate.getText().toString()));
//                    txtAuthenDate.setText(response.getAuthenDate());
                }
                @Override
                public void onError(Exception e) {
//                    dismissProgressDialog();
                    hideProgressBar();
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        });

        imgPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.checkInternetAndShowDialog(getContext())) {
                    NetworkUtils.showNoInternetDialog(getContext());
                }

                SfTokenDao sfTokenDao = new SfTokenDao(getContext());
                List<SfToken> tokens = sfTokenDao.getAllTokens();

                String tokenAuth = ""; // Default empty string
                if (!tokens.isEmpty()) {
                    // Get the most recent token (last token in the list)
                    SfToken latestToken = tokens.get(tokens.size() - 1);
                    tokenAuth = latestToken.getTokenAuth();
                }
                NhsoApiCaller apiCaller = new NhsoApiCaller(getContext());
                String citizenIdString = citizenId.getText().toString().trim();
                apiCaller.testRealPersonApi(citizenIdString, tokenAuth, new NhsoApiCaller.RealPersonApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // สร้าง custom view สำหรับแสดงข้อมูล
                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permission_info, null);
                        TextView tvPermissionInfo = dialogView.findViewById(R.id.tvPermissionInfo);
                        tvPermissionInfo.setText(response);

                        // Parse ข้อมูลจาก response สำหรับนำมาใส่ใน form
                        String fullName = "";
                        String birthDate = "";
                        String firstName = "";
                        String lastName = "";
                        String gender = "";

                        try {
                            // แยกข้อมูลจากข้อความที่มีรูปแบบเป็น text
                            String[] lines = response.split("\n");

                            for (String line : lines) {
                                line = line.trim();

                                // ดึงชื่อ-นามสกุล
                                if (line.startsWith("ชื่อ-นามสกุล:")) {
                                    fullName = line.substring("ชื่อ-นามสกุล:".length()).trim();
                                    // แยกชื่อและนามสกุล
                                    String[] nameParts = fullName.trim().split("\\s+");
                                    if (nameParts.length >= 2) {
                                        firstName = nameParts[0];
                                        // รวมส่วนที่เหลือเป็นนามสกุล
                                        StringBuilder lastNameBuilder = new StringBuilder();
                                        for (int i = 1; i < nameParts.length; i++) {
                                            if (i > 1) lastNameBuilder.append(" ");
                                            lastNameBuilder.append(nameParts[i]);
                                        }
                                        lastName = lastNameBuilder.toString();
                                    } else if (nameParts.length == 1) {
                                        firstName = nameParts[0];
                                        lastName = "";
                                    }
                                }

                                // ดึงเพศ
                                if (line.startsWith("เพศ:")) {
                                    gender = line.substring("เพศ:".length()).trim();
                                }

                                // ดึงวันเกิด
                                if (line.startsWith("วันเกิด:")) {
                                    birthDate = line.substring("วันเกิด:".length()).trim();
                                }
                            }

                        } catch (Exception e) {
                            Log.e("Permission Check", "Error parsing response: " + e.getMessage());
                        }

                        // เก็บข้อมูลไว้ใน final variables เพื่อใช้ใน onClick
                        final String finalFirstName = firstName;
                        final String finalLastName = lastName;
                        final String finalBirthDate = birthDate;
                        final String finalGender = gender;

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialog_AppCompat)
                                .setTitle("ตรวจสอบสิทธิ์")
                                .setView(dialogView)
                                .setIcon(R.drawable.permission)
                                .setPositiveButton("ตกลง", (dialog, which) -> {
                                    // นำข้อมูลมาใส่ใน form เมื่อกดตกลง
                                    if (!finalFirstName.isEmpty()) {
                                        fname.setText(finalFirstName);
                                        personInfo.setFname(finalFirstName);
                                    }

                                    if (!finalLastName.isEmpty()) {
                                        lname.setText(finalLastName);
                                        personInfo.setLname(finalLastName);
                                    }

                                    // ตั้งค่าเพศ
                                    if (!finalGender.isEmpty()) {
                                        if (finalGender.equals("ชาย")) {
                                            rdoMale.setChecked(true);
                                            personInfo.setGender("M");
                                        } else if (finalGender.equals("หญิง")) {
                                            rdoFemale.setChecked(true);
                                            personInfo.setGender("F");
                                        }
                                    }

                                    if (!finalBirthDate.isEmpty()) {
                                        try {
                                            // แปลงวันที่จากรูปแบบไทย "21 พฤษภาคม 2520" เป็นรูปแบบ dd/MM/yyyy
                                            String thaiBirthDate = convertToThaiBuddhistDate(finalBirthDate);
                                            txtBirthDay.setText(thaiBirthDate);

                                            // แปลงเป็น Western date สำหรับเก็บใน PersonInfo
                                            String westernDate = convertThaiDateToWestern(finalBirthDate);
                                            if (westernDate != null) {
                                                personInfo.setBirthday(westernDate);
                                            }
                                            // ตรวจสอบอายุทันทีหลังจากเลือกวันเกิด
                                            isValidAge = validateAge(westernDate);

                                            if (!isValidAge) {
                                                // แสดง Dialog เตือนถ้าอายุไม่อยู่ในเกณฑ์
                                                showAgeValidationDialog(currentAge);
                                            } else {
                                                // แสดงข้อมูลกลุ่มอายุถ้าอายุอยู่ในเกณฑ์
                                                showAgeGroupInfo(currentAge);
                                            }
                                        } catch (Exception e) {
                                            Log.e("Date Conversion", "Error converting birth date: " + e.getMessage());
                                        }
                                    }

                                    // อัพเดท personInfo ใน dataPasser
                                    dataPasser.onPersonInfo(personInfo);

                                    dialog.dismiss();
                                })
                                .setNegativeButton("ยกเลิก", (dialog, which) -> dialog.dismiss());

                        // แสดง dialog บน UI thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            });
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // สร้าง dialog สำหรับแสดงข้อผิดพลาด

                        showFormattedErrorDialog(errorMessage);
//                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_error, null);
//                        TextView tvErrorMessage = dialogView.findViewById(R.id.tvErrorMessage);
//                        tvErrorMessage.setText(errorMessage);
//
//                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
//                                .setTitle("เกิดข้อผิดพลาด")
//                                .setView(dialogView)
//                                .setIcon(R.drawable.error)
//                                .setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());
//
//                        // แสดง dialog บน UI thread
//                        if (getActivity() != null) {
//                            getActivity().runOnUiThread(() -> builder.show());
//                        }
                    }
                });
            }

            // Method สำหรับแปลงวันที่เป็นรูปแบบไทย
            private String convertToThaiBuddhistDate(String thaiDateString) {
                try {
                    // รูปแบบวันที่ที่ได้จาก API: "21 พฤษภาคม 2520"
                    String[] months = {
                            "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
                            "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
                    };

                    String[] dateParts = thaiDateString.trim().split("\\s+");
                    if (dateParts.length == 3) {
                        String day = dateParts[0];
                        String monthThai = dateParts[1];
                        String yearBE = dateParts[2];

                        // หาเดือนในรูปแบบตัวเลข
                        int monthNumber = 0;
                        for (int i = 0; i < months.length; i++) {
                            if (months[i].equals(monthThai)) {
                                monthNumber = i + 1;
                                break;
                            }
                        }

                        if (monthNumber > 0) {
                            // แปลงเป็นรูปแบบ dd/MM/yyyy (พุทธศักราช)
                            return String.format(Locale.US, "%02d/%02d/%s",
                                    Integer.parseInt(day), monthNumber, yearBE);
                        }
                    }

                    return thaiDateString; // คืนค่าเดิมหากแปลงไม่ได้
                } catch (Exception e) {
                    Log.e("Date Conversion", "Error converting date: " + e.getMessage());
                    return thaiDateString; // คืนค่าเดิมหากแปลงไม่ได้
                }
            }

            // Method สำหรับแปลงวันที่ไทยเป็นรูปแบบ Western สำหรับเก็บใน PersonInfo
            private String convertThaiDateToWestern(String thaiDateString) {
                try {
                    String[] months = {
                            "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
                            "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
                    };

                    String[] dateParts = thaiDateString.trim().split("\\s+");
                    if (dateParts.length == 3) {
                        int day = Integer.parseInt(dateParts[0]);
                        String monthThai = dateParts[1];
                        int yearBE = Integer.parseInt(dateParts[2]);

                        // หาเดือนในรูปแบบตัวเลข
                        int monthNumber = 0;
                        for (int i = 0; i < months.length; i++) {
                            if (months[i].equals(monthThai)) {
                                monthNumber = i + 1;
                                break;
                            }
                        }

                        if (monthNumber > 0) {
                            // แปลงปีพุทธศักราชเป็นคริสต์ศักราช
                            int yearAD = yearBE - 543;

                            // แปลงเป็นรูปแบบ yyyy-MM-dd
                            return String.format(Locale.US, "%04d-%02d-%02d", yearAD, monthNumber, day);
                        }
                    }

                    return null;
                } catch (Exception e) {
                    Log.e("Date Conversion", "Error converting Thai date to Western: " + e.getMessage());
                    return null;
                }
            }
        });

        spinnerProvince = view.findViewById(R.id.spinnerProvince);
        spinnerDistrict = view.findViewById(R.id.spinnerDistrict);
        spinnerSubDistrict = view.findViewById(R.id.spinnerSubDistrict);
        spinnerProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerProvince.showDropDown();
            }
        });
        spinnerDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerDistrict.showDropDown();
            }
        });
        spinnerSubDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerSubDistrict.showDropDown();
            }
        });
        spinnerProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedProvince = parent.getItemAtPosition(position).toString();
                provinceCode = getProvinceCodeByName(selectedProvince);
                provinceName = selectedProvince;
                personInfo.setProvCode(provinceCode);
                personInfo.setProvName(selectedProvince);
                dataPasser.onPersonInfo(personInfo);
                provinceName = selectedProvince;
                updateDistricts(selectedProvince);
            }
        });
        spinnerDistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = parent.getItemAtPosition(position).toString();
                String selectedProvince = spinnerProvince.getText().toString();
                districtName = selectedDistrict;
                districtCode = getDistrictCodeByNameAndProvinceCode(selectedDistrict, getProvinceCodeByName(selectedProvince));
                personInfo.setDistName(districtName);
                personInfo.setDistCode(districtCode);
                dataPasser.onPersonInfo(personInfo);
                updateSubDistricts(selectedProvince, selectedDistrict);
            }
        });
        spinnerSubDistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                subDistrictName = adapterView.getItemAtPosition(i).toString();
                subDistrictCode = getSubDistrictCodeByNameAndDistrictCode(subDistrictName, districtCode);
                personInfo.setSubDistCode(subDistrictCode);
                personInfo.setSubDistName(subDistrictName);
                dataPasser.onPersonInfo(personInfo);
            }
        });
        txtHomeNo = view.findViewById(R.id.txtHouseNo);
        txtVillageNo = view.findViewById(R.id.txtVillageNo);
        txtPostalCode = view.findViewById(R.id.txtPostalCode);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        btnDeviceSsp = (ImageButton) view.findViewById(R.id.btnDeviceSsp);
        btnDeviceSsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DeviceMainActivity.class);
//                startActivityForResult(intent,DEVICE_RESULT_ONE);
                activityResultLauncher.launch(intent);
            }
        });
    }

    private String getSubDistrictCodeByNameAndDistrictCode(String subDistrictName, String districtCode) {
        for (SubDistrictInfo subDistrictInfo : subDistrictInfos) {
            if (subDistrictInfo.getName().equals(subDistrictName) && subDistrictInfo.getDistCode().equals(districtCode)) {
                return subDistrictInfo.getSubdistCode();
            }
        }
        return null; // Return a default value if not found
    }

    private void updateDistricts(String provinceName) {
        DistrictDao districtDao = new DistrictDao(getContext());
        // ดึงข้อมูลอำเภอตามจังหวัดที่เลือก
        String provinceCode = getProvinceCodeByName(provinceName);
        districtInfos = districtDao.getDistrictsByProvinceCode(provinceCode);
        List<String> districtNames = new ArrayList<>();
        for (DistrictInfo district : districtInfos) {
            districtNames.add(district.getName());
        }

        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                districtNames
        );
        spinnerDistrict.setAdapter(districtAdapter);
        spinnerDistrict.setText("", false);
        spinnerSubDistrict.setText("", false);
    }

    private void updateSubDistricts(String provinceName, String districtName) {
        SubDistrictDao subDistrictDao = new SubDistrictDao(getContext());
        String provinceCode = getProvinceCodeByName(provinceName);
        String districtCode = getDistrictCodeByNameAndProvinceCode(districtName, provinceCode);
        // ดึงข้อมูลตำบลตามอำเภอที่เลือก
        subDistrictInfos = subDistrictDao.getSubdistrictsByDistrictCode(districtCode, provinceCode);
        List<String> subDistrictNames = new ArrayList<>();
        for (SubDistrictInfo subDistrict : subDistrictInfos) {
            subDistrictNames.add(subDistrict.getName());
        }

        ArrayAdapter<String> subDistrictAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_dropdown_item_1line,
                subDistrictNames
        );
        spinnerSubDistrict.setAdapter(subDistrictAdapter);
        spinnerSubDistrict.setText("", false);
    }
    private void setupDropdowns() {
        // Example data, replace with actual data from your database
        String[] provinces = new String[0];
        String[] districts = new String[0];
        String[] subDistricts = new String[0];
        ProvinceDao provinceDao = new ProvinceDao(getContext());
        provinceInfos = provinceDao.getAllProvinces();
        provinces = new String[provinceInfos.size()];
        for (int i = 0; i < provinceInfos.size(); i++) {
            provinces[i] = provinceInfos.get(i).getName();
        }

        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, provinces);
        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1, districts);
        ArrayAdapter<String> subDistrictAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, subDistricts);
        Log.d("Dropdown", "Province count: " + provinceAdapter.getCount());
        Log.d("Dropdown", "District count: " + districtAdapter.getCount());
        Log.d("Dropdown", "Sub-district count: " + subDistrictAdapter.getCount());
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subDistrictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerDistrict.setAdapter(districtAdapter);
        spinnerSubDistrict.setAdapter(subDistrictAdapter);
        spinnerProvince.setThreshold(1);
        spinnerDistrict.setThreshold(1);
        spinnerSubDistrict.setThreshold(1);
    }



    private String getProvinceCodeByName(String provinceName) {
        for (ProvinceInfo provinceInfo : provinceInfos) {
            if (provinceInfo.getName().equals(provinceName)) {
                return provinceInfo.getProvCode();
            }
        }
        return null; // หรือค่าเริ่มต้นที่เหมาะสมถ้าไม่พบ
    }
    private String getDistrictCodeByNameAndProvinceCode(String districtName, String provinceCode) {
        for (DistrictInfo districtInfo : districtInfos) {
            if (districtInfo.getName().equals(districtName) && districtInfo.getProvCode().equals(provinceCode)) {
                return districtInfo.getDistCode();
            }
        }
        return null; // Return a default value if not found
    }

    private void showDatePickerDialog(TextInputEditText editText, String mydate) {
        try {
            Calendar calendar = Calendar.getInstance();

            // ถ้า mydate ไม่เป็น null ให้ใช้วันที่ที่ส่งมา
            if (mydate != null && !mydate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = sdf.parse(mydate);
                calendar.setTime(date);
            }
            // ถ้า mydate เป็น null จะใช้วันที่ปัจจุบัน (calendar จะมีค่าเป็นวันที่ปัจจุบันอยู่แล้ว)

            ThaiDatePickerDialog dialog = new ThaiDatePickerDialog.Builder(getContext())
                    .setDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH))
                    .setListener((view, year, month, dayOfMonth) -> {
                        int thaiYear = year + 543;
                        String formattedDate = String.format(new Locale("th", "TH"),
                                "%d/%d/%d",
                                dayOfMonth, month + 1, thaiYear);
                        editText.setText(formattedDate);

                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        if(editText == txtBirthDay) {
                            String westernDate = convertToWesternDate(editText.getText().toString());
                            this.personInfo.setBirthday(westernDate);

                            // ตรวจสอบอายุทันทีหลังจากเลือกวันเกิด
                            isValidAge = validateAge(westernDate);

                            if (!isValidAge) {
                                // แสดง Dialog เตือนถ้าอายุไม่อยู่ในเกณฑ์
                                showAgeValidationDialog(currentAge);
                            } else {
                                // แสดงข้อมูลกลุ่มอายุถ้าอายุอยู่ในเกณฑ์
                                showAgeGroupInfo(currentAge);
                            }
                        }
                        else if(editText == txtAuthenDate) {
                            this.personInfo.setAuthen_date(convertToWesternDate(editText.getText().toString()));
                        }
                        dataPasser.onPersonInfo(this.personInfo);
                    })
                    .build();

            dialog.show();
        }
        catch (Exception ex){
            Toast.makeText(getContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
     private void setDataToViews(PersonInfo person) {
        if (person != null) {
            if (person.getPhoto() != null && person.getPhoto().length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(person.getPhoto(), 0, person.getPhoto().length);
                    if (bitmap != null) {
                        imgPerson.setImageBitmap(bitmap);
                        ProfileImage.saveImageToStorage(getContext(), bitmap, person.getIdcard());
                    } else {
                        // ถ้าแปลงเป็น Bitmap ไม่สำเร็จ ให้ใช้รูปดีฟอลต์
                        imgPerson.setImageResource(R.drawable.ic_person);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // กรณีเกิดข้อผิดพลาด ให้ใช้รูปดีฟอลต์
                    imgPerson.setImageResource(R.drawable.ic_person);
                }
            } else {
                // กรณีไม่มีข้อมูลรูปภาพ ให้ใช้รูปดีฟอลต์
                imgPerson.setImageResource(R.drawable.ic_person);
            }
            citizenId.setText(person.getIdcard());
            fname.setText(person.getFname());
            lname.setText(person.getLname());
            txtBirthDay.setText(DateConverter.convertToThaiBuddhistDate(person.getBirthday()));
            if (person.getBirthday() != null && !person.getBirthday().isEmpty()) {
                isValidAge = validateAge(person.getBirthday());
                if (isValidAge) {
                    Log.d("PersonInfoFragment", "Loaded person age: " + currentAge + " years, Age group: " + getAgeGroup());
                } else {
                    Log.w("PersonInfoFragment", "Loaded person has invalid age: " + currentAge + " years");
                }
            }
            txtAuthenDate.setText(DateConverter.convertToThaiBuddhistDateTime(person.getAuthen_date()));
            if ("M".equals(person.getGender())) {
                rdoMale.setChecked(true);
            } else if ("F".equals(person.getGender())) {
                rdoFemale.setChecked(true);
            }
            txtPhoneNo.setText(person.getPhone());
            txtHn.setText(person.getHn());
            txtBmi.setText(person.getBmi());
            txtAuthenDate.setText(person.getAuthen_date());
            txtAuthenNo.setText(person.getAuthen_code());

            // แปลงค่าตัวเลขเป็น String
            txtWeight.setText(String.valueOf(person.getWeight()));
            txtHeight.setText(String.valueOf(person.getHeight()));
            txtWaistCircumference.setText(String.valueOf(person.getWaist_size()));

            txtBp.setText(person.getBp());
            txtSymptomsPressure.setText(String.valueOf(person.getSystolic_pressure()));
            txtDiastolicPressure.setText(String.valueOf(person.getDiastolic_pressure()));
            txtHomeNo.setText(person.getHomeNo());
            txtVillageNo.setText(person.getVillageNo());
            txtPostalCode.setText(person.getPostCode());
            txtTemperature.setText(String.valueOf(person.getTemperature()));
//            if(person.getHcode() != null) {
//                house.setSelectionById(Long.valueOf(person.getHcode()));
//            }
            if(person.getProvCode() != null) {
                spinnerProvince.setText(person.getProvName(), false);
                provinceCode = person.getProvCode();
                provinceName = person.getProvName();
            }
            if(person.getDistCode() != null) {
                spinnerDistrict.setText(person.getDistName(), false);
                districtCode = person.getDistCode();
                districtName = person.getDistName();
            }
            if(person.getSubDistCode() != null) {
                spinnerSubDistrict.setText(person.getSubDistName(), false);
                subDistrictCode = person.getSubDistCode();
                subDistrictName = person.getSubDistName();
            }

        }
    }
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    // หากต้องการขอ permission
    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }
    public void attachToFields(TextInputEditText... editTexts) {
        for (final TextInputEditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    setCurrentEditText(editText);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextFieldUpdater updater = fieldUpdaters.get(currentEditText);
                    if (updater != null) {
                        if(s!=null) {
                            if(!s.toString().isEmpty()) {
//                                if(NumberValidator.isDouble(s.toString())) {
                                    if(!s.toString().equals(".")) {
                                        updater.update(s.toString());
                                        dataPasser.onPersonInfo(personInfo);
                                    }
//                                }
                            }
                        }
                    }

                    if(editText == citizenId){
                        String idCard = editText.getText().toString();
                        // ใช้ validateThaiIdCard() แทนการตรวจสอบความยาวอย่างเดียว
                        if (validateThaiIdCard(idCard)) {
                            PersonDao personDao = new PersonDao(getContext());
                            PersonDao.PersonInfo person = personDao.getPersonByIdcard(personInfo.getIdcard());
                            if(person!=null){
                                personInfo.setHcode(person.getHcode());
                            }
                            personInfo.setIdcard(idCard);
                            dataPasser.onPersonInfo(personInfo);

                            // แสดงข้อความยืนยัน
//                            Toast.makeText(getContext(), "เลขบัตรประชาชนถูกต้อง", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // BMI calculation logic ยังคงเหมือนเดิม
                    if(editText == txtWeight || editText == txtHeight){
                        if(!txtWeight.getText().toString().equals("") && !txtHeight.getText().toString().equals("")) {
                            float weight = Float.valueOf(Objects.requireNonNull(txtWeight.getText().toString() != "" ? txtWeight.getText().toString() : "0"));
                            float height = Float.valueOf(Objects.requireNonNull(txtHeight.getText().toString() != "" ? txtHeight.getText().toString() : "0"));

                            bmiCalculator.setWeight(weight);
                            bmiCalculator.setHeight(height);

                            if (!bmiCalculator.isValidInput()) {
                                Toast.makeText(getContext(), "กรุณากรอกค่าที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            float bmi = bmiCalculator.calculateBMI();
                            txtBmi.setText(String.valueOf(bmi));
                            String category = bmiCalculator.getBMICategory();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // แทนที่ validateInputs() ด้วย validateInputsEnhanced()
                    validateInputsEnhanced();
                }
            });
        }
    }
    private void setupTextWatchers() {
        smartcardReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ThaiIdSmartcardReader.class);
                activityResultLauncher.launch(intent);
            }
        });

        // ตั้งค่า TextWatcher สำหรับเลขบัตรประชาชนแยกต่างหาก
        setupCitizenIdTextWatcher();

        rdoGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find which radio button is selected
                switch (checkedId) {
                    case R.id.rdoMale:
                        personInfo.setGender("M");
                        dataPasser.onPersonInfo(personInfo);
                        break;
                    case R.id.rdoFemale:
                        personInfo.setGender("F");
                        dataPasser.onPersonInfo(personInfo);
                        break;
                }
            }
        });

        txtHomeNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                personInfo.setHomeNo(txtHomeNo.getText().toString());
                dataPasser.onPersonInfo(personInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtVillageNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                personInfo.setVillageNo(txtVillageNo.getText().toString());
                dataPasser.onPersonInfo(personInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                personInfo.setPostCode(txtPostalCode.getText().toString());
                dataPasser.onPersonInfo(personInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtTemperature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(txtTemperature.getText().toString().equals("")) {
                    personInfo.setTemperature(0);
                }
                else {
                    personInfo.setTemperature(Float.parseFloat(txtTemperature.getText().toString()));
                }
                dataPasser.onPersonInfo(personInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void updatePersonInfo() {
        try {
            // ตรวจสอบเลขบัตรประชาชนก่อนบันทึก
            String idCardText = getTextFromEditText(citizenId);
            if (!validateThaiIdCard(idCardText)) {
                Toast.makeText(getContext(), "กรุณากรอกเลขบัตรประชาชนให้ถูกต้อง", Toast.LENGTH_LONG).show();
                return; // หยุดการบันทึกถ้าเลขบัตรไม่ถูกต้อง
            }
            // Basic Information
            personInfo.setIdcard(getTextFromEditText(citizenId));
            personInfo.setFname(getTextFromEditText(fname));
            personInfo.setLname(getTextFromEditText(lname));

            // Gender
            if (rdoMale.isChecked()) {
                personInfo.setGender("M");
            } else if (rdoFemale.isChecked()) {
                personInfo.setGender("F");
            }
            String birthDay = getTextFromEditText(txtBirthDay);
            String westernBirthDay = convertToWesternDate(birthDay);
            personInfo.setBirthday(westernBirthDay);

            isValidAge = validateAge(westernBirthDay);

            // Contact and Hospital Information
            personInfo.setPhone(getTextFromEditText(txtPhoneNo));
            personInfo.setHn(getTextFromEditText(txtHn));
            String authenDate = getTextFromEditText(txtAuthenDate);
            personInfo.setAuthen_date(convertToWesternDate(authenDate));
            personInfo.setAuthen_code(getTextFromEditText(txtAuthenNo));

            // Physical Measurements
            setDoubleValue(txtWeight, value -> personInfo.setWeight(value));
            setDoubleValue(txtHeight, value -> personInfo.setHeight(value));
            setDoubleValue(txtWaistCircumference, value -> personInfo.setWaist_size(value));

            // Blood Pressure Information
            personInfo.setBp(getTextFromEditText(txtBp));
            setDoubleValue(txtSymptomsPressure, value -> personInfo.setSystolic_pressure(value));
            setDoubleValue(txtDiastolicPressure, value -> personInfo.setDiastolic_pressure(value));

            personInfo.setCreated_by("SYSTEM");
            personInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            dataPasser.onPersonInfo(personInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_info, container, false);
        initializeViews(view);
        initializeFieldUpdaters();
        attachToFields(citizenId, fname, lname, txtPhoneNo, txtHn,
                txtAuthenDate, txtAuthenNo, txtWeight, txtHeight,
                txtWaistCircumference, txtBp, txtBmi,
                txtSymptomsPressure, txtDiastolicPressure,txtBirthDay,txtHomeNo,txtVillageNo,txtPostalCode, txtTemperature);

        // ลบการเรียก setupTextWatchers() เดิม - ใช้แค่ setupEnhancedTextWatchers()
        setupEnhancedTextWatchers();

        loadData();
        if (!NetworkUtils.checkInternetAndShowDialog(getContext())) {
            NetworkUtils.showNoInternetDialog(getContext());
        }
        return view;
    }
    private void setupEnhancedTextWatchers() {
        smartcardReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ThaiIdSmartcardReader.class);
                activityResultLauncher.launch(intent);
            }
        });

        // ใช้ Enhanced Citizen ID TextWatcher แทนของเดิม
        setupEnhancedCitizenIdTextWatcher();

        rdoGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdoMale:
                        personInfo.setGender("M");
                        dataPasser.onPersonInfo(personInfo);
                        break;
                    case R.id.rdoFemale:
                        personInfo.setGender("F");
                        dataPasser.onPersonInfo(personInfo);
                        break;
                }
            }
        });

        // TextWatchers อื่นๆ ยังคงเหมือนเดิม
        txtHomeNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                personInfo.setHomeNo(txtHomeNo.getText().toString());
                dataPasser.onPersonInfo(personInfo);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // ... TextWatchers อื่นๆ ยังคงเหมือนเดิม
    }
    private void loadData(){

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getId()!=null){
                List<PersonInfo> persons =  SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(data.getId()));

                for(PersonInfo personinfo1 : persons){
                    this.personInfo = personinfo1;

                    // ตั้งค่าโหมดแก้ไขและเลขบัตรประชาชนต้นฉบับ
                    if (personinfo1.getIdcard() != null && !personinfo1.getIdcard().isEmpty()) {
                        setEditMode(true, personinfo1.getIdcard());
                        Log.d("PersonInfoFragment", "Setting edit mode with ID: " + personinfo1.getIdcard());
                    }

                    setDataToViews(personinfo1);
                    updatePersonInfo();
                }
            } else {
                // ถ้าไม่มี ID แสดงว่าเป็นการสร้างใหม่
//                setEditMode(false, "");
                resetToCreateMode();
                Log.d("PersonInfoFragment", "Setting create mode");
            }
        });

    }
    public void resetToCreateMode() {
        setEditMode(false, "");
        clearForm();
    }
    private String getTextFromEditText(EditText editText) {
        return editText != null ? editText.getText().toString().trim() : "";
    }

    private void setDoubleValue(EditText editText, Consumer<Double> setter) {
        String value = getTextFromEditText(editText);
        if (!value.isEmpty()) {
            try {
                setter.accept(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                editText.setError("กรุณากรอกตัวเลขที่ถูกต้อง");
            }
        }
    }
//    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult result) {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                        byte[] byteArray = data.getByteArrayExtra("image");
//                        String strIdcard = data.getStringExtra("result");
//                        // แสดงรูปภาพที่ได้จากบัตร
//                        if (byteArray != null) {
//                            try {
//                                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                                imgPerson.setImageBitmap(bitmap);
//                                personInfo.setPhoto(byteArray);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                // หากมีข้อผิดพลาดให้ใช้รูปภาพดีฟอลต์
//                                imgPerson.setImageResource(R.drawable.ic_person);
//                            }
//                        }
//                        if(strIdcard!=null && !strIdcard.equals("")){
//                            String[] idcardInfo = strIdcard.split("#");
//                            if(idcardInfo.length>0){
//                                citizenId.setText(idcardInfo[0].toString());
//                                fname.setText(idcardInfo[2].toString());
//                                lname.setText(idcardInfo[4].toString());
//
//                                // แก้ไขส่วนการประมวลผลวันเกิด
//                                if(idcardInfo.length > 18 && idcardInfo[18].length() == 8) {
//                                    try {
//                                        int day, month, year;
//                                        year = Integer.parseInt(idcardInfo[18].substring(0,4));
//                                        month = Integer.parseInt(idcardInfo[18].substring(4,6)); // ไม่ต้องลบ 1
//                                        day = Integer.parseInt(idcardInfo[18].substring(6,8));
//
//                                        // จัดรูปแบบวันที่ให้ถูกต้อง (dd/MM/yyyy)
//                                        String formattedBirthDate = String.format(Locale.US, "%02d/%02d/%d", day, month, year);
//                                        txtBirthDay.setText(formattedBirthDate);
//
//                                        // แปลงเป็น Western date สำหรับเก็บใน PersonInfo
//                                        personInfo.setBirthday(convertToWesternDate(formattedBirthDate));
//                                    } catch (NumberFormatException e) {
//                                        Log.e("PersonInfo", "Error parsing birth date: " + e.getMessage());
//                                    }
//                                }
//
//                                // ตั้งค่าเพศ
//                                if(idcardInfo[1].toString().equals("นาย")) {
//                                    rdoMale.setChecked(true);
//                                    personInfo.setGender("M");
//                                } else if(idcardInfo[1].toString().equals("นาง") || idcardInfo[1].toString().equals("นางสาว")) {
//                                    rdoFemale.setChecked(true);
//                                    personInfo.setGender("F");
//                                } else {
//                                    rdoMale.setChecked(true); // default
//                                    personInfo.setGender("M");
//                                }
//
//                                // อัพเดทข้อมูลไปยัง dataPasser
//                                dataPasser.onPersonInfo(personInfo);
//                            }
//                        }
//                    }
//                    getDataFromDevice(result);
//                }
//            }
//    );
// ส่วนที่ต้องอัปเดตใน PersonInfoFragment.java
// แทนที่ activityResultLauncher ที่มีอยู่เดิม

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        // รับข้อมูลเลขบัตรประชาชน
                        String citizenIdFromCard = data.getStringExtra("citizenId");

                        // ตรวจสอบความถูกต้องของเลขบัตรประชาชนจากสมาร์ทคาร์ด
                        if (citizenIdFromCard != null && !citizenIdFromCard.isEmpty()) {
                            if (validateThaiIdCard(citizenIdFromCard)) {
                                PersonInfoFragment.this.citizenId.setText(citizenIdFromCard);
                                personInfo.setIdcard(citizenIdFromCard);
                                Toast.makeText(getContext(), "เลขบัตรประชาชนจากบัตรถูกต้อง", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "เลขบัตรประชาชนจากบัตรไม่ถูกต้อง", Toast.LENGTH_LONG).show();
                                return; // หยุดการประมวลผลถ้าเลขบัตรไม่ถูกต้อง
                            }
                        }

                        // รับข้อมูลอื่นๆ จากบัตร
                        byte[] byteArray = data.getByteArrayExtra("image");
                        String firstNameThai = data.getStringExtra("firstNameThai");
                        String lastNameThai = data.getStringExtra("lastNameThai");
                        String genderCode = data.getStringExtra("gender");
                        String birthDate = data.getStringExtra("birthDate");

                        // ประมวลผลข้อมูลอื่นๆ ตามปกติ...
                        if (byteArray != null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                imgPerson.setImageBitmap(bitmap);
                                personInfo.setPhoto(byteArray);
                                 ProfileImage.saveImageToStorage(getContext(), bitmap, citizenIdFromCard);
                            } catch (Exception e) {
                                e.printStackTrace();
                                imgPerson.setImageResource(R.drawable.ic_person);
                            }
                        }

                        // ประมวลผลข้อมูลอื่นๆ...
                        if (firstNameThai != null && !firstNameThai.isEmpty()) {
                            fname.setText(firstNameThai);
                            personInfo.setFname(firstNameThai);
                        }

                        if (lastNameThai != null && !lastNameThai.isEmpty()) {
                            lname.setText(lastNameThai);
                            personInfo.setLname(lastNameThai);
                        }
                        if (genderCode != null && !genderCode.isEmpty()) {
                            processGenderFromCard(genderCode);
                        }

                        // ประมวลผลวันเกิด
                        if (birthDate != null && !birthDate.isEmpty()) {
//                            processBirthDateFromCard(birthDate);
                            String formattedBirthDate = DateConverter.convertBirthDateYYYYMMDDToThai(birthDate);
                           txtBirthDay.setText(DateConverter.convertBirthDateYYYYMMDDToThai(birthDate));
                            // แปลงเป็น Western date สำหรับเก็บใน PersonInfo
                           personInfo.setBirthday(convertToWesternDate(formattedBirthDate));
                        }
                        // ตั้งค่าเพศและวันเกิดตามปกติ...

                        // อัพเดทข้อมูลไปยัง dataPasser
                        dataPasser.onPersonInfo(personInfo);
                    }

                    getDataFromDevice(result);
                }
            }
    );

    private void processGenderFromCard(String genderCode) {
        try {
            // ตรวจสอบรูปแบบต่างๆ ของรหัสเพศ
            switch (genderCode.trim().toUpperCase()) {
                case "1":
                case "M":
                case "MALE":
                case "ชาย":
                    rdoMale.setChecked(true);
                    rdoFemale.setChecked(false);
                    personInfo.setGender("M");
                    Log.d("PersonInfoFragment", "Gender set to Male from card data: " + genderCode);
                    break;

                case "2":
                case "F":
                case "FEMALE":
                case "หญิง":
                    rdoFemale.setChecked(true);
                    rdoMale.setChecked(false);
                    personInfo.setGender("F");
                    Log.d("PersonInfoFragment", "Gender set to Female from card data: " + genderCode);
                    break;

                default:
                    Log.w("PersonInfoFragment", "Unknown gender code from card: " + genderCode);
                    // ค่าเริ่มต้นเป็นชาย
                    rdoMale.setChecked(true);
                    rdoFemale.setChecked(false);
                    personInfo.setGender("M");
                    Toast.makeText(getContext(), "ไม่สามารถระบุเพศจากบัตร กำหนดเป็นชายเป็นค่าเริ่มต้น", Toast.LENGTH_SHORT).show();
                    break;
            }
        } catch (Exception e) {
            Log.e("PersonInfoFragment", "Error processing gender from card: " + e.getMessage());
            // ค่าเริ่มต้นเป็นชาย
            rdoMale.setChecked(true);
            rdoFemale.setChecked(false);
            personInfo.setGender("M");
        }
    }

    private void getDataFromDevice(ActivityResult result){
       if (result.getResultCode() == Activity.RESULT_OK) {
           Intent data = result.getData();
           if(data.getStringExtra("ECGInfo")!=null) {

               String ecgInfo = data.getStringExtra("ECGInfo");
               String spO2Info = data.getStringExtra("SPO2Info");

               String tempInfo = data.getStringExtra("TEMPInfo");
               String nibpInfo = data.getStringExtra("NIBPInfo");

               String[] ecgTemp = ecgInfo.split(":");
               String heartRate = ecgTemp[1].replace("Resp Rate", "");
               String RespRate = ecgTemp[2];

               String[] spO2Temp = spO2Info.split(":");
               String spO2 = spO2Temp[1].replace("SPO2", "");
               String spO2PluseRate = spO2Temp[2];

               String strHigh = "High:";
               String strLow = "Low:";
               String strMean = "Mean:";
               int indexHigh = nibpInfo.indexOf(strHigh);
               int indexLow = nibpInfo.indexOf(strLow);
               int indexMean = nibpInfo.indexOf(strMean);
               String hight = nibpInfo.substring(indexHigh + strHigh.length(), indexLow - 1);
               String low = nibpInfo.substring(indexLow + strLow.length(), indexMean - 1);
               String tmp = tempInfo.replace("TEMP:", "").replace("°C", "").trim();
               if (tmp.trim().indexOf("-") < 0) {
                   txtTemperature.setText(tmp);
               }
               if (spO2PluseRate.indexOf("-") < 0) {
                   txtBp.setText(spO2PluseRate);
               }
               if (hight.indexOf("-") < 0 && low.indexOf("-") < 0) {
                   txtSymptomsPressure.setText(hight);
                   txtDiastolicPressure.setText(low);
               }
           }
       }
   }
    // เพิ่ม Method ใหม่ใน PersonInfoFragment.java

    /**
     * ตรวจสอบเลขบัตรประชาชนซ้ำในปีงบประมาณปัจจุบัน
     * @param idCard เลขบัตรประชาชน
     * @return true ถ้าไม่ซ้ำ (สามารถใช้ได้), false ถ้าซ้ำ (ไม่สามารถใช้ได้)
     */
    private boolean validateIdCardDuplicate(String idCard) {
        if (idCard == null || idCard.length() != 13 || isDialogShowing) {
            return false;
        }

        // ถ้าอยู่ในโหมดแก้ไขและเลขบัตรประชาชนเป็นของเดิม ให้ผ่านการตรวจสอบ
        if (isEditMode && idCard.equals(originalIdCard)) {
            Log.d("PersonInfoFragment", "Edit mode: Same ID card, skipping duplicate check");
            return true;
        }

        SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(getContext());
        boolean isDuplicate = sfPersonInfoDao.isIdCardExistInCurrentFiscalYear(idCard);

        if (isDuplicate) {
            PersonInfo existingSurvey = sfPersonInfoDao.getLatestSurveyByIdCard(idCard);

            if (existingSurvey != null) {
                showDuplicateIdCardDialog(existingSurvey);
            } else {
                Toast.makeText(getContext(),
                        "เลขบัตรประชาชนนี้เคยทำแบบสำรวจในปีงบประมาณปัจจุบันแล้ว",
                        Toast.LENGTH_LONG).show();
            }

            return false;
        }

        return true;
    }

    /**
     * แสดง Dialog แจ้งเตือนเมื่อพบเลขบัตรประชาชนซ้ำ
     * @param existingSurvey ข้อมูลการสำรวจที่มีอยู่แล้ว
     */
    private void showDuplicateIdCardDialog(PersonInfo existingSurvey) {
        // ป้องกันการเปิด Dialog ซ้ำ
        if (isDialogShowing) {
            return;
        }

        isDialogShowing = true;

        // สร้าง custom view สำหรับ dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_duplicate_warning, null);
        TextView tvMessage = dialogView.findViewById(R.id.tvDuplicateMessage);
        TextView tvExistingInfo = dialogView.findViewById(R.id.tvExistingInfo);

        String message = "เลขบัตรประชาชนนี้เคยทำแบบสำรวจในปีงบประมาณปัจจุบันแล้ว\n\n" +
                "ไม่สามารถทำแบบสำรวจซ้ำได้";

        String existingInfo = "ข้อมูลการสำรวจที่มีอยู่:\n" +
                "ชื่อ: " + existingSurvey.getFname() + " " + existingSurvey.getLname() + "\n" +
                "วันที่ทำแบบสำรวจ: " + formatDateForDisplay(existingSurvey.getCreated_date());

        tvMessage.setText(message);
        tvExistingInfo.setText(existingInfo);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("พบข้อมูลซ้ำ")
                .setView(dialogView)
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("ตกลง", (dialog, which) -> {
                    // เคลียร์ข้อมูลในฟอร์มโดยไม่ทริกเกอร์ TextWatcher
                    clearFormSafely();
                    isDialogShowing = false; // รีเซ็ตสถานะ
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> {
                    // รีเซ็ตสถานะเมื่อ Dialog ถูกปิด
                    isDialogShowing = false;
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void clearFormSafely() {
        // หยุดการทำงานของ TextWatcher ชั่วคราว
        isValidatingIdCard = true;

        // เคลียร์ข้อมูลโดยไม่ทริกเกอร์ validation
        citizenId.removeTextChangedListener(null);
        citizenId.setText("");
        citizenId.setError(null);

        fname.setText("");
        fname.setError(null);

        lname.setText("");
        lname.setError(null);

        txtBirthDay.setText("");
        rdoMale.setChecked(false);
        rdoFemale.setChecked(false);
        txtPhoneNo.setText("");
        txtHn.setText("");
        txtAuthenDate.setText("");
        txtAuthenNo.setText("");

        txtWeight.setText("");
        txtWeight.setError(null);

        txtHeight.setText("");
        txtHeight.setError(null);

        txtWaistCircumference.setText("");
        txtBp.setText("");
        txtBmi.setText("");
        txtSymptomsPressure.setText("");
        txtDiastolicPressure.setText("");
        txtHomeNo.setText("");
        txtVillageNo.setText("");
        txtPostalCode.setText("");

        txtTemperature.setText("");
        txtTemperature.setError(null);

        // เคลียร์ dropdown และรูปภาพ
        spinnerProvince.setText("", false);
        spinnerDistrict.setText("", false);
        spinnerSubDistrict.setText("", false);
        imgPerson.setImageResource(R.drawable.ic_person);

        // รีเซ็ต PersonInfo object
        personInfo = new PersonInfo();

        // ตั้งค่า TextWatcher กลับ
        setupEnhancedCitizenIdTextWatcher();

        // Focus กลับไปที่ช่องเลขบัตรประชาชน
        citizenId.requestFocus();

        // รีเซ็ตสถานะ
        isValidatingIdCard = false;

        Toast.makeText(getContext(), "เคลียร์ข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
    }

    /**
     * แปลงวันที่เป็นรูปแบบที่อ่านง่าย
     * @param dateString วันที่ในรูปแบบ yyyy-MM-dd HH:mm:ss
     * @return วันที่ในรูปแบบไทย
     */
    private String formatDateForDisplay(String dateString) {
        try {
            if (dateString == null || dateString.isEmpty()) {
                return "ไม่ระบุ";
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("th", "TH"));

            Date date = inputFormat.parse(dateString);
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.YEAR, 543); // แปลงเป็นพุทธศักราช

                return outputFormat.format(cal.getTime());
            }

            return dateString;
        } catch (Exception e) {
            Log.e("PersonInfoFragment", "Error formatting date: " + e.getMessage());
            return dateString;
        }
    }

    /**
     * เคลียร์ข้อมูลในฟอร์ม
     */
    private void clearForm() {
        citizenId.setText("");
        citizenId.setError(null); // เคลียร์ error message

        fname.setText("");
        fname.setError(null);

        lname.setText("");
        lname.setError(null);

        txtBirthDay.setText("");
        rdoMale.setChecked(false);
        rdoFemale.setChecked(false);
        txtPhoneNo.setText("");
        txtHn.setText("");
        txtAuthenDate.setText("");
        txtAuthenNo.setText("");

        txtWeight.setText("");
        txtWeight.setError(null);

        txtHeight.setText("");
        txtHeight.setError(null);

        txtWaistCircumference.setText("");
        txtBp.setText("");
        txtBmi.setText("");
        txtSymptomsPressure.setText("");
        txtDiastolicPressure.setText("");
        txtHomeNo.setText("");
        txtVillageNo.setText("");
        txtPostalCode.setText("");

        txtTemperature.setText("");
        txtTemperature.setError(null);

        // เคลียร์ dropdown และรูปภาพ
        spinnerProvince.setText("", false);
        spinnerDistrict.setText("", false);
        spinnerSubDistrict.setText("", false);
        imgPerson.setImageResource(R.drawable.ic_person);

        // รีเซ็ต PersonInfo object
        personInfo = new PersonInfo();

        // Focus กลับไปที่ช่องเลขบัตรประชาชน
        citizenId.requestFocus();

        Toast.makeText(getContext(), "เคลียร์ข้อมูลเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
    }

    // อัปเดต Method validateInputs() ที่มีอยู่เดิม
    private void validateInputs() {
        boolean isValid = true;

        // Validate Citizen ID
        String citizenIdText = citizenId.getText().toString().trim();
        if (citizenIdText.isEmpty()) {
//            citizenId.setError("กรุณากรอกเลขบัตรประชาชน");
            isValid = false;
        } else if (citizenIdText.length() != 13) {
//            citizenId.setError("เลขบัตรประชาชนต้องมี 13 หลัก");
            isValid = false;
        } else {
            // ตรวจสอบความซ้ำในปีงบประมาณปัจจุบัน
//            if (!validateIdCardDuplicate(citizenIdText)) {
//                citizenId.setError("เลขบัตรประชาชนนี้เคยทำแบบสำรวจแล้ว");
//                isValid = false;
//            } else {
//                citizenId.setError(null);
//            }
        }

        // Validate Name
        if (fname.getText().toString().trim().isEmpty()) {
            fname.setError("กรุณากรอกชื่อ");
            isValid = false;
        } else {
            fname.setError(null);
        }

        if (lname.getText().toString().trim().isEmpty()) {
            lname.setError("กรุณากรอกนามสกุล");
            isValid = false;
        } else {
            lname.setError(null);
        }

        // Validate numeric fields
        try {
            if (!txtWeight.getText().toString().trim().isEmpty()) {
                double weight = Double.parseDouble(txtWeight.getText().toString());
                if (weight <= 0 || weight > 300) {
                    txtWeight.setError("น้ำหนักไม่ถูกต้อง");
                    isValid = false;
                }
            }

            if (!txtHeight.getText().toString().trim().isEmpty()) {
                double height = Double.parseDouble(txtHeight.getText().toString());
                if (height <= 0 || height > 250) {
                    txtHeight.setError("ส่วนสูงไม่ถูกต้อง");
                    isValid = false;
                }
            }
        } catch (NumberFormatException e) {
            isValid = false;
        }

        // เก็บสถานะการ validate ไว้ใน PersonInfo สำหรับใช้ตอนบันทึก
        personInfo.setValidationPassed(isValid);

        // ส่งสัญญาณไปยัง Activity ว่าข้อมูลพร้อมบันทึกหรือไม่
        if (dataPasser != null) {
            dataPasser.onValidationStatusChanged(isValid);
        }
    }
    private void displayFormattedIdCard() {
        String rawIdCard = citizenId.getText().toString();
        if (validateThaiIdCard(rawIdCard)) {
            String formattedId = formatIdCard(rawIdCard);
            // สามารถใช้แสดงในรายงานหรือ UI อื่นๆ
            Log.d("ID_DISPLAY", "Formatted ID: " + formattedId);
        }
    }
    // อัปเดต TextWatcher สำหรับ citizenId
    private void setupCitizenIdTextWatcher() {
        citizenId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ไม่ต้องทำอะไร
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String idCard = s.toString().trim();

                if (idCard.length() == 13) {
                    // ตรวจสอบความซ้ำ
//                    if (validateIdCardDuplicate(idCard)) {
                        // ถ้าไม่ซ้ำ ให้ดำเนินการปกติ
                        PersonDao personDao = new PersonDao(getContext());
                        PersonDao.PersonInfo person = personDao.getPersonByIdcard(idCard);
                        if (person != null) {
                            personInfo.setHcode(person.getHcode());
                        }
                        personInfo.setIdcard(idCard);
                        dataPasser.onPersonInfo(personInfo);

                        citizenId.setError(null);
//                    } else {
//                        // ถ้าซ้ำ ให้หยุดการทำงาน
//                        return;
//                    }
                } else if (idCard.length() > 0 && idCard.length() < 13) {
//                    citizenId.setError("กรุณากรอกหมายเลขบัตรประชาชนให้ครบ 13 หลัก");
                } else {
                    citizenId.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInputs();
            }
        });
    }
    /**
     * ตรวจสอบความถูกต้องของเลขบัตรประชาชนไทย
     * @param idCard เลขบัตรประชาชน 13 หลัก
     * @return true ถ้าถูกต้อง, false ถ้าไม่ถูกต้อง
     */
    private boolean validateThaiIdCard(String idCard) {
        // ตรวจสอบว่าเป็น null หรือ empty
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }

        // ลบช่องว่างและขีดกลาง (ถ้ามี)
        idCard = idCard.replaceAll("\\s+", "").replaceAll("-", "");

        // ตรวจสอบความยาว
        if (idCard.length() != 13) {
            return false;
        }

        // ตรวจสอบว่าเป็นตัวเลขทั้งหมด
        if (!idCard.matches("\\d{13}")) {
            return false;
        }

        // ตรวจสอบว่าไม่ใช่เลขที่ซ้ำกันทั้งหมด (เช่น 1111111111111)
        if (idCard.matches("(\\d)\\1{12}")) {
            return false;
        }

        // ตรวจสอบด้วยอัลกอริทึมการคำนวณหลักตรวจสอบ
        return validateIdCardChecksum(idCard);
    }

    /**
     * ตรวจสอบหลักตรวจสอบของเลขบัตรประชาชนไทย
     * @param idCard เลขบัตรประชาชน 13 หลัก
     * @return true ถ้าหลักตรวจสอบถูกต้อง, false ถ้าไม่ถูกต้อง
     */
    private boolean validateIdCardChecksum(String idCard) {
        try {
            int sum = 0;

            // คำนวณผลรวมของหลัก 12 หลักแรก คูณด้วยน้ำหนัก (13, 12, 11, ..., 2)
            for (int i = 0; i < 12; i++) {
                int digit = Character.getNumericValue(idCard.charAt(i));
                int weight = 13 - i;
                sum += digit * weight;
            }

            // คำนวณหลักตรวจสอบ
            int remainder = sum % 11;
            int checkDigit;

            if (remainder < 2) {
                checkDigit = 1 - remainder;
            } else {
                checkDigit = 11 - remainder;
            }

            // เปรียบเทียบกับหลักสุดท้ายของเลขบัตรประชาชน
            int lastDigit = Character.getNumericValue(idCard.charAt(12));

            return checkDigit == lastDigit;

        } catch (Exception e) {
            return false;
        }
    }
    /**
     * ตรวจสอบและแสดงข้อผิดพลาดของเลขบัตรประชาชน
     * @param idCard เลขบัตรประชาชน
     * @return ข้อความข้อผิดพลาด หรือ null ถ้าถูกต้อง
     */
    private String getIdCardValidationError(String idCard) {
        // ตรวจสอบว่าเป็น null หรือ empty
        if (idCard == null || idCard.trim().isEmpty()) {
            return "กรุณากรอกเลขบัตรประชาชน";
        }

        // ลบช่องว่างและขีดกลาง
        String cleanIdCard = idCard.replaceAll("\\s+", "").replaceAll("-", "");

        // ตรวจสอบความยาว
//        if (cleanIdCard.length() != 13) {
//            return "เลขบัตรประชาชนต้องมี 13 หลัก";
//        }
//
//        // ตรวจสอบว่าเป็นตัวเลขทั้งหมด
//        if (!cleanIdCard.matches("\\d{13}")) {
//            return "เลขบัตรประชาชนต้องเป็นตัวเลขเท่านั้น";
//        }

        // ตรวจสอบว่าไม่ใช่เลขที่ซ้ำกันทั้งหมด
        if (cleanIdCard.matches("(\\d)\\1{12}")) {
            return "เลขบัตรประชาชนไม่ถูกต้อง";
        }
        if(cleanIdCard.matches("\\d{13}")) {
            // ตรวจสอบหลักตรวจสอบ
            if (!validateIdCardChecksum(cleanIdCard)) {
                return "เลขบัตรประชาชนไม่ถูกต้อง (หลักตรวจสอบไม่ตรงกับอัลกอริทึม)";
            }
        }

        return null; // ไม่มีข้อผิดพลาด
    }

    /**
     * จัดรูปแบบเลขบัตรประชาชนให้มีขีดกลาง
     * @param idCard เลขบัตรประชาชน 13 หลัก
     * @return เลขบัตรประชาชนที่จัดรูปแบบแล้ว (X-XXXX-XXXXX-XX-X)
     */
    private String formatIdCard(String idCard) {
        if (idCard == null || idCard.length() != 13) {
            return idCard;
        }

        // ลบขีดกลางและช่องว่างที่มีอยู่
        String cleanIdCard = idCard.replaceAll("\\s+", "").replaceAll("-", "");

        if (cleanIdCard.length() == 13) {
            return String.format("%s-%s-%s-%s-%s",
                    cleanIdCard.substring(0, 1),   // หลักที่ 1
                    cleanIdCard.substring(1, 5),   // หลักที่ 2-5
                    cleanIdCard.substring(5, 10),  // หลักที่ 6-10
                    cleanIdCard.substring(10, 12), // หลักที่ 11-12
                    cleanIdCard.substring(12, 13)  // หลักที่ 13 (หลักตรวจสอบ)
            );
        }

        return idCard;
    }

    /**
     * ทดสอบตัวอย่างเลขบัตรประชาชน
     * (ใช้สำหรับการทดสอบเท่านั้น)
     */
    private void testIdCardValidation() {
        String[] testIds = {
                "1234567890123",     // ไม่ถูกต้อง
                "1111111111111",     // เลขซ้ำ ไม่ถูกต้อง
                "1234567890124",     // ไม่ถูกต้อง
                "1100700166953",     // ตัวอย่างที่ถูกต้อง (สมมติ)
                "abc1234567890",     // มีตัวอักษร ไม่ถูกต้อง
                "12345678901",       // สั้นเกินไป
                "12345678901234"     // ยาวเกินไป
        };

        for (String testId : testIds) {
            boolean isValid = validateThaiIdCard(testId);
            String error = getIdCardValidationError(testId);
            String formatted = formatIdCard(testId);

            Log.d("ID_VALIDATION", String.format(
                    "ID: %s | Valid: %s | Error: %s | Formatted: %s",
                    testId, isValid, error, formatted
            ));
        }
    }
   /**
    * อัปเดต TextWatcher สำหรับ citizenId ให้รวมการตรวจสอบใหม่
    */
   private void setupEnhancedCitizenIdTextWatcher() {
       citizenId.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               // ไม่ต้องทำอะไร
           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               // ป้องกันการทำงานขณะที่ Dialog แสดงอยู่หรือกำลังทำการ validation
               if (isDialogShowing || isValidatingIdCard) {
                   return;
               }

               String idCard = s.toString().trim();

               // ลบข้อผิดพลาดเดิม
               citizenId.setError(null);

               if (idCard.length() == 0) {
                   return; // ถ้าไม่มีข้อมูลให้ไม่ต้องแสดงข้อผิดพลาด
               }

               // ตรวจสอบรูปแบบและความถูกต้อง
               String validationError = getIdCardValidationError(idCard);

               if (validationError != null) {
                   citizenId.setError(validationError);
                   return;
               }

               // ถ้าเลขบัตรประชาชนถูกต้องตามรูปแบบ
               if (idCard.length() == 13 && validateThaiIdCard(idCard)) {
                   // ตั้งสถานะว่ากำลังตรวจสอบ
                   isValidatingIdCard = true;

                   // ตรวจสอบความซ้ำในฐานข้อมูล
//                   if (validateIdCardDuplicate(idCard)) {
                       // ถ้าไม่ซ้ำ ให้ดำเนินการปกติ
//                       PersonDao personDao = new PersonDao(getContext());
//                       PersonDao.PersonInfo person = personDao.getPersonByIdcard(idCard);
//                       if (person != null) {
//                           if(!person.getTypelive().equals("4")) {
//                               personInfo.setHcode(person.getHcode());
//                               personInfo.setIdcard(idCard);
//                               dataPasser.onPersonInfo(personInfo);
//                          } else {
//                               showTypeLive4Dialog();
//                               // ไม่ set idcard เพราะจะ clear ใน dialog
//                               // รีเซ็ตสถานะก่อน return
//                               isValidatingIdCard = false;
//                               return;
//                           }
//                       } else {
//                           showNoDataFoundDialogSimple();
//                       }


                       personInfo.setIdcard(idCard);
                       dataPasser.onPersonInfo(personInfo);

//                       if (!isEditMode || !idCard.equals(originalIdCard)) {
//                           Toast.makeText(getContext(), "เลขบัตรประชาชนถูกต้อง", Toast.LENGTH_SHORT).show();
//                       }
//
//                       // แสดงข้อความยืนยันว่าเลขบัตรประชาชนถูกต้อง
//                       Toast.makeText(getContext(), "เลขบัตรประชาชนถูกต้อง", Toast.LENGTH_SHORT).show();
//                   }

                   // รีเซ็ตสถานะ
                   isValidatingIdCard = false;
               }
           }
           private void showTypeLive4Dialog() {
               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
               builder.setTitle("แจ้งเตือน");
               builder.setMessage("ไม่สามารถทำแบบสำรวจได้ เนื่องจากไม่ใช่คนในพื้นที่");
               builder.setCancelable(false); // ป้องกันการปิด dialog โดยการกด back หรือนอก dialog

               builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // Clear ค่าใน textbox citizenId
                       citizenId.setText("");

                       // Clear ค่าใน personInfo.idcard
                       personInfo.setIdcard("");

                       // ปิด dialog
                       dialog.dismiss();

                       // รีเซ็ตสถานะ dialog
                       isDialogShowing = false;
                   }
               });

               // ตั้งสถานะว่า dialog กำลังแสดง
               isDialogShowing = true;

               AlertDialog dialog = builder.create();
               dialog.show();
           }
           @Override
           public void afterTextChanged(Editable s) {
               // ป้องกันการเรียก validation ขณะที่ Dialog แสดงอยู่
               if (!isDialogShowing && !isValidatingIdCard) {
                   validateInputsEnhanced();
               }
           }
       });
   }
    private void showNoDataFoundDialogSimple() {
        // ป้องกันการเปิด Dialog ซ้ำ
        if (isDialogShowing) {
            return;
        }

        isDialogShowing = true;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("ไม่พบข้อมูล")
                .setMessage("ไม่พบข้อมูลสำหรับเลขบัตรประชาชนนี้\n\n" +
                        "กรุณาตรวจสอบเลขบัตรประชาชนอีกครั้ง หรือติดต่อเจ้าหน้าที่เพื่อเพิ่มข้อมูลในระบบ")
                .setIcon(R.drawable.ic_info) // หรือ R.drawable.ic_warning
                .setPositiveButton("ตกลง", (dialog, which) -> {
                    // เคลียร์เลขบัตรประชาชนและ focus กลับไป
                    citizenId.setText("");
                    citizenId.setError(null);
                    personInfo.setIdcard("");

                    // เคลียร์รูปภาพ
                    imgPerson.setImageResource(R.drawable.ic_person);
                    personInfo.setPhoto(null);

                    // เคลียร์ชื่อ
                    fname.setText("");
                    fname.setError(null);
                    personInfo.setFname("");

                    // เคลียร์นามสกุล
                    lname.setText("");
                    lname.setError(null);
                    personInfo.setLname("");

                    // เคลียร์วันเกิด
                    txtBirthDay.setText("");
                    txtBirthDay.setError(null);
                    personInfo.setBirthday("");

                    // เคลียร์เพศ
                    rdoMale.setChecked(false);
                    rdoFemale.setChecked(false);
                    personInfo.setGender("");

                    // รีเซ็ตค่าการตรวจสอบอายุ
                    currentAge = 0;
                    isValidAge = false;

                    // อัพเดท PersonInfo ใน dataPasser
                    dataPasser.onPersonInfo(personInfo);

                    // Focus กลับไปที่ช่องเลขบัตรประชาชน
                    citizenId.requestFocus();

                    isDialogShowing = false;
                    dialog.dismiss();
                })
                .setOnDismissListener(dialog -> {
                    isDialogShowing = false;
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * อัปเดต validateInputs() ให้รวมการตรวจสอบเลขบัตรประชาชนใหม่
     */
    private void validateInputsEnhanced() {
        // ป้องกันการทำงานขณะที่ Dialog แสดงอยู่
        if (isDialogShowing) {
            return;
        }

        boolean isValid = true;

        // Validate Citizen ID with enhanced validation
        String citizenIdText = citizenId.getText().toString().trim();
        String idCardError = getIdCardValidationError(citizenIdText);

        if (idCardError != null) {
            citizenId.setError(idCardError);
            isValid = false;
        } else {
            // ตรวจสอบความซ้ำในปีงบประมาณปัจจุบัน (แต่ไม่แสดง Dialog)
//            if (citizenIdText.length() == 13) {
//                SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(getContext());
//                boolean isDuplicate = sfPersonInfoDao.isIdCardExistInCurrentFiscalYear(citizenIdText);
//
//                if (isDuplicate && !(isEditMode && citizenIdText.equals(originalIdCard))) {
//                    citizenId.setError("เลขบัตรประชาชนนี้เคยทำแบบสำรวจแล้ว");
//                    isValid = false;
//                } else {
//                    citizenId.setError(null);
//                }
//            }
        }

        // Validate Name
        if (fname.getText().toString().trim().isEmpty()) {
            fname.setError("กรุณากรอกชื่อ");
            isValid = false;
        } else {
            fname.setError(null);
        }

        if (lname.getText().toString().trim().isEmpty()) {
            lname.setError("กรุณากรอกนามสกุล");
            isValid = false;
        } else {
            lname.setError(null);
        }

        // Validate Birth Date and Age
        String birthDateText = txtBirthDay.getText().toString().trim();
        if (birthDateText.isEmpty()) {
            txtBirthDay.setError("กรุณาเลือกวันเกิด");
            isValid = false;
            isValidAge = false;
        } else {
            String westernDate = convertToWesternDate(birthDateText);
            isValidAge = validateAge(westernDate);

            if (!isValidAge) {
                txtBirthDay.setError("อายุต้องอยู่ในช่วง 15-59 ปี (อายุปัจจุบัน: " + currentAge + " ปี)");
                isValid = false;
            } else {
                txtBirthDay.setError(null);
            }
        }

        // Validate numeric fields
        try {
            if (!txtWeight.getText().toString().trim().isEmpty()) {
                double weight = Double.parseDouble(txtWeight.getText().toString());
                if (weight <= 1 || weight > 300) {
                    txtWeight.setError("น้ำหนักไม่ถูกต้อง (1-300 กิโลกรัม)");
                    isValid = false;
                } else {
                    txtWeight.setError(null);
                }
            }

            if (!txtHeight.getText().toString().trim().isEmpty()) {
                double height = Double.parseDouble(txtHeight.getText().toString());
                if (height <= 1 || height > 250) {
                    txtHeight.setError("ส่วนสูงไม่ถูกต้อง (1-250 เซนติเมตร)");
                    isValid = false;
                } else {
                    txtHeight.setError(null);
                }
            }

            if (!txtTemperature.getText().toString().trim().isEmpty()) {
                double temperature = Double.parseDouble(txtTemperature.getText().toString());
                if (temperature < 30 || temperature > 45) {
                    txtTemperature.setError("อุณหภูมิไม่ถูกต้อง (30-45 องศาเซลเซียส)");
                    isValid = false;
                } else {
                    txtTemperature.setError(null);
                }
            }
            if (!txtWaistCircumference.getText().toString().trim().isEmpty()) {
                try {
                    double waistCircumference = Double.parseDouble(txtWaistCircumference.getText().toString());
                    if (waistCircumference < 20 || waistCircumference > 200) {
                        txtWaistCircumference.setError("รอบเอวไม่ถูกต้อง (20-200 เซนติเมตร)");
                        isValid = false;
                    } else {
                        txtWaistCircumference.setError(null);
                    }
                } catch (NumberFormatException e) {
                    txtWaistCircumference.setError("กรุณากรอกตัวเลขที่ถูกต้อง");
                    isValid = false;
                }
            }

            if (!txtBp.getText().toString().trim().isEmpty()) {
                try {
                    double heartRate = Double.parseDouble(txtBp.getText().toString());
                    if (heartRate < 40 || heartRate > 200) {
                        txtBp.setError("อัตราการเต้นของหัวใจไม่ถูกต้อง (40-200 ครั้งต่อนาที)");
                        isValid = false;
                    } else {
                        txtBp.setError(null);
                    }
                } catch (NumberFormatException e) {
                    txtBp.setError("กรุณากรอกตัวเลขที่ถูกต้อง");
                    isValid = false;
                }
            }

            if (!txtSymptomsPressure.getText().toString().trim().isEmpty()) {
                try {
                    double systolicPressure = Double.parseDouble(txtSymptomsPressure.getText().toString());
                    if (systolicPressure < 70 || systolicPressure > 250) {
                        txtSymptomsPressure.setError("ความดันโลหิตตัวบนไม่ถูกต้อง (70-250 mmHg)");
                        isValid = false;
                    } else {
                        txtSymptomsPressure.setError(null);
                    }
                } catch (NumberFormatException e) {
                    txtSymptomsPressure.setError("กรุณากรอกตัวเลขที่ถูกต้อง");
                    isValid = false;
                }
            }

            if (!txtDiastolicPressure.getText().toString().trim().isEmpty()) {
                try {
                    double diastolicPressure = Double.parseDouble(txtDiastolicPressure.getText().toString());
                    if (diastolicPressure < 40 || diastolicPressure > 150) {
                        txtDiastolicPressure.setError("ความดันโลหิตตัวล่างไม่ถูกต้อง (40-150 mmHg)");
                        isValid = false;
                    } else {
                        txtDiastolicPressure.setError(null);
                    }
                } catch (NumberFormatException e) {
                    txtDiastolicPressure.setError("กรุณากรอกตัวเลขที่ถูกต้อง");
                    isValid = false;
                }
            }
            if (!txtSymptomsPressure.getText().toString().trim().isEmpty() &&
                    !txtDiastolicPressure.getText().toString().trim().isEmpty()) {
                try {
                    double systolic = Double.parseDouble(txtSymptomsPressure.getText().toString());
                    double diastolic = Double.parseDouble(txtDiastolicPressure.getText().toString());

                    if (systolic <= diastolic) {
                        txtSymptomsPressure.setError("ความดันตัวบนต้องมากกว่าความดันตัวล่าง");
                        txtDiastolicPressure.setError("ความดันตัวล่างต้องน้อยกว่าความดันตัวบน");
                        isValid = false;
                    }
                } catch (NumberFormatException e) {
                    // จัดการข้อผิดพลาดแล้วในการตรวจสอบแต่ละฟิลด์
                }
            }

        } catch (NumberFormatException e) {
            isValid = false;
        }

        isValid = isValid && isValidAge;
        // เก็บสถานะการ validate ไว้ใน PersonInfo สำหรับใช้ตอนบันทึก
        if (personInfo != null) {
            personInfo.setValidationPassed(isValid);
        }

        // ส่งสัญญาณไปยัง Activity ว่าข้อมูลพร้อมบันทึกหรือไม่
        if (dataPasser != null) {
            dataPasser.onValidationStatusChanged(isValid);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // รีเซ็ตสถานะเมื่อ Fragment ถูกทำลาย
        isDialogShowing = false;
        isValidatingIdCard = false;
    }
    public boolean isAgeValidForAssessment() {
        return isValidAge && currentAge >= 15 && currentAge <= 59;
    }

    // เพิ่มเมธอดสำหรับ get อายุปัจจุบัน
    public int getCurrentAge() {
        return currentAge;
    }
    public String getAgeGroup() {
        if (currentAge >= 15 && currentAge <= 34) {
            return "15-34";
        } else if (currentAge >= 35 && currentAge <= 59) {
            return "35-59";
        } else {
            return "INVALID";
        }
    }
    /**
     * แยกและจัดรูปแบบข้อความ Error จากข้อความที่มี JSON
     * @param errorMessage ข้อความ Error ที่ได้จาก API
     * @return ข้อความที่จัดรูปแบบแล้ว
     */
    private String formatErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            return "เกิดข้อผิดพลาดที่ไม่ทราบสาเหตุ";
        }

        try {
            // แยกรหัสข้อผิดพลาดออกมา
            String errorCode = "";
            String jsonPart = "";

            // หารหัสข้อผิดพลาดจากข้อความ
            if (errorMessage.contains("รหัสข้อผิดพลาด:")) {
                String[] parts = errorMessage.split("\\{", 2);
                if (parts.length >= 1) {
                    errorCode = parts[0].trim(); // รหัสข้อผิดพลาด: 202
                    if (parts.length >= 2) {
                        jsonPart = "{" + parts[1]; // {"dataError":"..."}
                    }
                }
            } else {
                // ถ้าไม่มีรหัสข้อผิดพลาด ให้ใช้ข้อความทั้งหมดเป็น JSON
                jsonPart = errorMessage;
            }

            // แยกข้อความจาก JSON
            String dataErrorMessage = extractDataErrorFromJson(jsonPart);

            // จัดรูปแบบข้อความใหม่
            StringBuilder formattedMessage = new StringBuilder();

            if (!errorCode.isEmpty()) {
                formattedMessage.append(errorCode).append("\n");
            }

            if (!dataErrorMessage.isEmpty()) {
                formattedMessage.append(dataErrorMessage);
            } else {
                formattedMessage.append("ไม่สามารถดึงข้อมูลข้อผิดพลาดได้");
            }

            return formattedMessage.toString();

        } catch (Exception e) {
            // หากเกิดข้อผิดพลาดในการแยกข้อความ ให้คืนข้อความเดิม
            Log.e("ErrorParser", "Error parsing error message: " + e.getMessage());
            return errorMessage;
        }
    }

    /**
     * แยกข้อความ dataError จาก JSON string
     * @param jsonString JSON string ที่มี dataError
     * @return ข้อความใน dataError หรือ empty string หากไม่พบ
     */
    private String extractDataErrorFromJson(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                return "";
            }

            // ลบข้อความที่ไม่ใช่ JSON ออก
            String cleanJson = jsonString.trim();
            if (!cleanJson.startsWith("{")) {
                // หาตำแหน่งเริ่มต้นของ JSON
                int jsonStart = cleanJson.indexOf("{");
                if (jsonStart != -1) {
                    cleanJson = cleanJson.substring(jsonStart);
                } else {
                    return "";
                }
            }

            // Parse JSON
            JSONObject jsonObject = new JSONObject(cleanJson);

            // ดึงข้อความจาก dataError
            if (jsonObject.has("dataError")) {
                return jsonObject.getString("dataError");
            }

            // ลองหาใน key อื่นๆ ที่อาจมี
            if (jsonObject.has("error")) {
                return jsonObject.getString("error");
            }

            if (jsonObject.has("message")) {
                return jsonObject.getString("message");
            }

            if (jsonObject.has("errorMessage")) {
                return jsonObject.getString("errorMessage");
            }

            return "";

        } catch (JSONException e) {
            Log.e("ErrorParser", "JSON parsing error: " + e.getMessage());

            // ถ้า parse JSON ไม่ได้ ให้ลองใช้ regex แยกข้อความ
            return extractDataErrorWithRegex(jsonString);
        }
    }
    /**
     * ใช้ regex แยกข้อความ dataError เมื่อ parse JSON ไม่ได้
     * @param text ข้อความที่มี dataError
     * @return ข้อความใน dataError หรือ empty string หากไม่พบ
     */
    private String extractDataErrorWithRegex(String text) {
        try {
            // Pattern สำหรับหา "dataError":"ข้อความ"
            String pattern = "\"dataError\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(text);

            if (m.find()) {
                return m.group(1);
            }

            // ลองหา pattern อื่นๆ
            String[] patterns = {
                    "\"error\"\\s*:\\s*\"([^\"]+)\"",
                    "\"message\"\\s*:\\s*\"([^\"]+)\"",
                    "\"errorMessage\"\\s*:\\s*\"([^\"]+)\""
            };

            for (String pat : patterns) {
                java.util.regex.Pattern pattern1 = java.util.regex.Pattern.compile(pat);
                java.util.regex.Matcher matcher = pattern1.matcher(text);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            return "";

        } catch (Exception e) {
            Log.e("ErrorParser", "Regex extraction error: " + e.getMessage());
            return "";
        }
    }

    private void showFormattedErrorDialog(String errorMessage) {
        String formattedError = formatErrorMessage(errorMessage);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("เกิดข้อผิดพลาด")
                .setMessage(formattedError)
                .setIcon(R.drawable.error)
                .setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void setEditMode(boolean isEditMode, String originalIdCard) {
        this.isEditMode = isEditMode;
        this.originalIdCard = originalIdCard != null ? originalIdCard : "";
        Log.d("PersonInfoFragment", "Edit mode set to: " + isEditMode + ", Original ID: " + originalIdCard);
    }
}