package th.in.ffc.app.form.screening;


import static th.in.ffc.util.DateConverter.convertToWesternDate;
import static th.in.ffc.util.TransactionIdGenerator.generateTransId;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import th.in.ffc.MainActivity;
import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.ThaiIdSmartcardReader;
import th.in.ffc.api.nhso.ApiCaller;
import th.in.ffc.api.nhso.ApiResponse;
import th.in.ffc.api.nhso.AuthenCodeRequest;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.screening.dao.DistrictDao;
import th.in.ffc.app.form.screening.dao.PersonDao;
import th.in.ffc.app.form.screening.dao.ProvinceDao;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.dao.SubDistrictDao;
import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.DistrictInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.ProvinceInfo;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.app.form.screening.model.SubDistrictInfo;
import th.in.ffc.code.HouseListDialog;
import th.in.ffc.person.BmiInfoActivity;
import th.in.ffc.person.BmiInfoDialogFragment;

import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.BMICalculator;
import th.in.ffc.util.BMILevel;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.NetworkUtils;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.util.ThaiDatePickerDialog;
import th.in.ffc.widget.SearchableSpinner;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.berry_med.monitordemo.activity.DeviceMainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonInfoFragment extends Fragment {

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

        districtInfos  = new ArrayList<>();
        subDistrictInfos = new ArrayList<>();
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
                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_error, null);
                        TextView tvErrorMessage = dialogView.findViewById(R.id.tvErrorMessage);
                        tvErrorMessage.setText(errorMessage);

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                                .setTitle("เกิดข้อผิดพลาด")
                                .setView(dialogView)
                                .setIcon(R.drawable.error)
                                .setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

                        // แสดง dialog บน UI thread
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> builder.show());
                        }
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
                            this.personInfo.setBirthday(convertToWesternDate(editText.getText().toString()));
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
    public void attachToFields(TextInputEditText... editTexts) {
        for (final TextInputEditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    setCurrentEditText(editText);
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if (currentEditText != null) {
                        TextFieldUpdater updater = fieldUpdaters.get(currentEditText);
                        if (updater != null) {
                            if(s!=null) {
                                if(!s.toString().isEmpty()) {
                                    updater.update(s.toString());
                                    dataPasser.onPersonInfo(personInfo);
                                }
                            }
                        }
                        if(editText == citizenId){
                            String idCard = editText.getText().toString();
                            if (idCard.length() == 13) {
                                PersonDao personDao = new PersonDao(getContext());
                                PersonDao.PersonInfo person = personDao.getPersonByIdcard(personInfo.getIdcard());
                                if(person!=null){
                                    personInfo.setHcode(person.getHcode());
                                }
                                personInfo.setIdcard(idCard);
                                dataPasser.onPersonInfo(personInfo);
                            } else {
//                                Toast.makeText(getContext(), "กรุณากรอกหมายเลขบัตรประชาชนให้ครบ 13 หลัก", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(editText == txtWeight || editText == txtHeight){
                            if(!txtWeight.getText().toString().equals("") && !txtHeight.getText().toString().equals("")) {
                                float weight = Float.valueOf(Objects.requireNonNull(txtWeight.getText().toString() != "" ? txtWeight.getText().toString() : "0"));
                                float height = Float.valueOf(Objects.requireNonNull(txtHeight.getText().toString() != "" ? txtHeight.getText().toString() : "0"));
//                                int bmi = BMILevel.calculateBMILevel(weight, height);
//                                txtBmi.setText(String.valueOf(bmi));
                                bmiCalculator.setWeight(weight);
                                bmiCalculator.setHeight(height);

                                if (!bmiCalculator.isValidInput()) {
                                    Toast.makeText (getContext(), "กรุณากรอกค่าที่มากกว่า 0", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                float bmi = bmiCalculator.calculateBMI();
//                                String category = bmiCalculator.getBMICategory();
                                txtBmi.setText(String.valueOf(bmi));
                                String category = bmiCalculator.getBMICategory();
//                                Toast.makeText(getContext(), category, Toast.LENGTH_SHORT).show();
                            }


                        }
//                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    validateInputs();
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
            personInfo.setBirthday(convertToWesternDate(birthDay));

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_info, container, false);
        initializeViews(view);
        initializeFieldUpdaters();
        attachToFields(citizenId, fname, lname, txtPhoneNo, txtHn,
                txtAuthenDate, txtAuthenNo, txtWeight, txtHeight,
                txtWaistCircumference, txtBp, txtBmi,
                txtSymptomsPressure, txtDiastolicPressure,txtBirthDay,txtHomeNo,txtVillageNo,txtPostalCode,txtTemperature);

        setupTextWatchers();
        loadData();
        if (!NetworkUtils.checkInternetAndShowDialog(getContext())) {
            NetworkUtils.showNoInternetDialog(getContext());
        }
        return view;
    }
    private void loadData(){

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getId()!=null){
                List<PersonInfo> persons =  SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(data.getId()));
//                 Toast.makeText(getContext(),person.getId(),Toast.LENGTH_SHORT);
                for(PersonInfo personinfo1 :persons){
                    this.personInfo = personinfo1;
                    setDataToViews(personinfo1);
                    updatePersonInfo();
//                    dataPasser.onPersonInfo(this.personInfo);
                }
            }
        });

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

                        // รับข้อมูลรูปภาพ
                        byte[] byteArray = data.getByteArrayExtra("image");

                        // รับข้อมูลบัตรในรูปแบบเดิม (สำหรับ backward compatibility)
                        String strIdcard = data.getStringExtra("result");

                        // รับข้อมูลแยกชิ้นใหม่
                        String citizenId = data.getStringExtra("citizenId");
                        String titleThai = data.getStringExtra("titleThai");
                        String firstNameThai = data.getStringExtra("firstNameThai");
                        String middleNameThai = data.getStringExtra("middleNameThai");
                        String lastNameThai = data.getStringExtra("lastNameThai");
                        String genderCode = data.getStringExtra("gender");
                        String birthDate = data.getStringExtra("birthDate");

                        // แสดงรูปภาพที่ได้จากบัตร
                        if (byteArray != null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                imgPerson.setImageBitmap(bitmap);
                                personInfo.setPhoto(byteArray);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // หากมีข้อผิดพลาดให้ใช้รูปภาพดีฟอลต์
                                imgPerson.setImageResource(R.drawable.ic_person);
                            }
                        }

                        // ใช้ข้อมูลแยกชิ้นใหม่ที่มีความแม่นยำสูง
                        if (citizenId != null && !citizenId.isEmpty()) {
                            PersonInfoFragment.this.citizenId.setText(citizenId);
                            personInfo.setIdcard(citizenId);
                        }

                        if (firstNameThai != null && !firstNameThai.isEmpty()) {
                            fname.setText(firstNameThai);
                            personInfo.setFname(firstNameThai);
                        }

                        if (lastNameThai != null && !lastNameThai.isEmpty()) {
                            lname.setText(lastNameThai);
                            personInfo.setLname(lastNameThai);
                        }

                        // ตั้งค่าเพศ
                        if (genderCode != null && !genderCode.isEmpty()) {
                            if (genderCode.equals("1")) {
                                rdoMale.setChecked(true);
                                personInfo.setGender("M");
                            } else if (genderCode.equals("2")) {
                                rdoFemale.setChecked(true);
                                personInfo.setGender("F");
                            }
                        }

                        // แปลงและตั้งค่าวันเกิด
                        if (birthDate != null && birthDate.length() == 8) {
                            try {
                                int day, month, year;
                                year = Integer.parseInt(birthDate.substring(0, 4));
                                month = Integer.parseInt(birthDate.substring(4, 6));
                                day = Integer.parseInt(birthDate.substring(6, 8));

                                // จัดรูปแบบวันที่ให้ถูกต้อง (dd/MM/yyyy) - พ.ศ.
                                String formattedBirthDate = String.format(Locale.US, "%02d/%02d/%d", day, month, year);
                                txtBirthDay.setText(formattedBirthDate);

                                // แปลงเป็น Western date สำหรับเก็บใน PersonInfo
                                personInfo.setBirthday(convertToWesternDate(formattedBirthDate));
                            } catch (NumberFormatException e) {
                                Log.e("PersonInfo", "Error parsing birth date: " + e.getMessage());
                            }
                        }

                        // Fallback: ใช้วิธีเดิมถ้าข้อมูลแยกชิ้นไม่สมบูรณ์
                        if ((citizenId == null || citizenId.isEmpty()) && strIdcard != null && !strIdcard.equals("")) {
                            String[] idcardInfo = strIdcard.split("#");
                            if (idcardInfo.length > 0) {
                                PersonInfoFragment.this.citizenId.setText(idcardInfo[0].toString());
                                personInfo.setIdcard(idcardInfo[0].toString());

                                if (idcardInfo.length > 2) {
                                    fname.setText(idcardInfo[2].toString());
                                    personInfo.setFname(idcardInfo[2].toString());
                                }

                                if (idcardInfo.length > 4) {
                                    lname.setText(idcardInfo[4].toString());
                                    personInfo.setLname(idcardInfo[4].toString());
                                }

                                // แก้ไขส่วนการประมวลผลวันเกิด
                                if (idcardInfo.length > 18 && idcardInfo[18].length() == 8) {
                                    try {
                                        int day, month, year;
                                        year = Integer.parseInt(idcardInfo[18].substring(0, 4));
                                        month = Integer.parseInt(idcardInfo[18].substring(4, 6));
                                        day = Integer.parseInt(idcardInfo[18].substring(6, 8));

                                        // จัดรูปแบบวันที่ให้ถูกต้อง (dd/MM/yyyy)
                                        String formattedBirthDate = String.format(Locale.US, "%02d/%02d/%d", day, month, year);
                                        txtBirthDay.setText(formattedBirthDate);

                                        // แปลงเป็น Western date สำหรับเก็บใน PersonInfo
                                        personInfo.setBirthday(convertToWesternDate(formattedBirthDate));
                                    } catch (NumberFormatException e) {
                                        Log.e("PersonInfo", "Error parsing birth date: " + e.getMessage());
                                    }
                                }

                                // ตั้งค่าเพศ (วิธีเดิม)
                                if (idcardInfo.length > 1) {
                                    if (idcardInfo[1].toString().equals("นาย")) {
                                        rdoMale.setChecked(true);
                                        personInfo.setGender("M");
                                    } else if (idcardInfo[1].toString().equals("นาง") || idcardInfo[1].toString().equals("นางสาว")) {
                                        rdoFemale.setChecked(true);
                                        personInfo.setGender("F");
                                    } else {
                                        rdoMale.setChecked(true); // default
                                        personInfo.setGender("M");
                                    }
                                }
                            }
                        }

                        // อัพเดทข้อมูลไปยัง dataPasser
                        dataPasser.onPersonInfo(personInfo);

                        Toast.makeText(getContext(), "นำเข้าข้อมูลจากบัตรประชาชนเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                    }

                    // เรียกฟังก์ชันเดิมสำหรับข้อมูลจากอุปกรณ์วัดสุขภาพ
                    getDataFromDevice(result);
                }
            }
    );
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
        if (idCard == null || idCard.length() != 13) {
            return false;
        }
        SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(getContext());
        // ตรวจสอบว่าเคยทำแบบสำรวจในปีงบประมาณปัจจุบันหรือไม่
        boolean isDuplicate = sfPersonInfoDao.isIdCardExistInCurrentFiscalYear(idCard);

        if (isDuplicate) {
            // ดึงข้อมูลการสำรวจล่าสุด
            PersonInfo existingSurvey = sfPersonInfoDao.getLatestSurveyByIdCard(idCard);

            if (existingSurvey != null) {
                showDuplicateIdCardDialog(existingSurvey);
            } else {
                Toast.makeText(getContext(),
                        "เลขบัตรประชาชนนี้เคยทำแบบสำรวจในปีงบประมาณปัจจุบันแล้ว",
                        Toast.LENGTH_LONG).show();
            }

            return false; // ไม่อนุญาตให้ใช้
        }

        return true; // อนุญาตให้ใช้
    }

    /**
     * แสดง Dialog แจ้งเตือนเมื่อพบเลขบัตรประชาชนซ้ำ
     * @param existingSurvey ข้อมูลการสำรวจที่มีอยู่แล้ว
     */
    private void showDuplicateIdCardDialog(PersonInfo existingSurvey) {
        // สร้าง custom view สำหรับ dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_duplicate_warning, null);

        // หา views ใน dialog
        TextView tvMessage = dialogView.findViewById(R.id.tvDuplicateMessage);
        TextView tvExistingInfo = dialogView.findViewById(R.id.tvExistingInfo);

        // จัดรูปแบบข้อความ
        String message = "เลขบัตรประชาชนนี้เคยทำแบบสำรวจในปีงบประมาณปัจจุบันแล้ว\n\n" +
                "ไม่สามารถทำแบบสำรวจซ้ำได้";

        String existingInfo = "ข้อมูลการสำรวจที่มีอยู่:\n" +
                "ชื่อ: " + existingSurvey.getFname() + " " + existingSurvey.getLname() + "\n" +
                "วันที่ทำแบบสำรวจ: " + formatDateForDisplay(existingSurvey.getCreated_date());

        tvMessage.setText(message);
        tvExistingInfo.setText(existingInfo);

        // สร้างและแสดง dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext())
                .setTitle("พบข้อมูลซ้ำ")
                .setView(dialogView)
                .setIcon(R.drawable.ic_warning) // ใช้ icon เตือน
                .setPositiveButton("ตกลง", (dialog, which) -> {
                    // เคลียร์ข้อมูลในฟอร์ม
                    clearForm();
                    dialog.dismiss();
                })
                .setCancelable(false); // ไม่ให้ปิด dialog โดยการกดข้างนอก

        AlertDialog dialog = builder.create();
        dialog.show();
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
        fname.setText("");
        lname.setText("");
        txtBirthDay.setText("");
        rdoMale.setChecked(false);
        rdoFemale.setChecked(false);
        txtPhoneNo.setText("");
        txtHn.setText("");
        txtAuthenDate.setText("");
        txtAuthenNo.setText("");
        txtWeight.setText("");
        txtHeight.setText("");
        txtWaistCircumference.setText("");
        txtBp.setText("");
        txtBmi.setText("");
        txtSymptomsPressure.setText("");
        txtDiastolicPressure.setText("");
        txtHomeNo.setText("");
        txtVillageNo.setText("");
        txtPostalCode.setText("");
        txtTemperature.setText("");

        // เคลียร์ dropdown
        spinnerProvince.setText("", false);
        spinnerDistrict.setText("", false);
        spinnerSubDistrict.setText("", false);

        // เคลียร์รูปภาพ
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
                    if (validateIdCardDuplicate(idCard)) {
                        // ถ้าไม่ซ้ำ ให้ดำเนินการปกติ
                        PersonDao personDao = new PersonDao(getContext());
                        PersonDao.PersonInfo person = personDao.getPersonByIdcard(idCard);
                        if (person != null) {
                            personInfo.setHcode(person.getHcode());
                        }
                        personInfo.setIdcard(idCard);
                        dataPasser.onPersonInfo(personInfo);

                        citizenId.setError(null);
                    } else {
                        // ถ้าซ้ำ ให้หยุดการทำงาน
                        return;
                    }
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

    // เพิ่ม interface สำหรับแจ้งสถานะการ validation
//    public interface OnDataPass {
//        void onPersonInfo(PersonInfo personInfo);
//        void onValidationStatusChanged(boolean isValid); // เพิ่มบรรทัดนี้
//    }
}