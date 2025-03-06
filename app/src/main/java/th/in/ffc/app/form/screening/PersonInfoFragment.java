package th.in.ffc.app.form.screening;


import static th.in.ffc.util.DateConverter.convertToWesternDate;
import static th.in.ffc.util.TransactionIdGenerator.generateTransId;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import th.in.ffc.MainActivity;
import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.api.nhso.ApiCaller;
import th.in.ffc.api.nhso.ApiResponse;
import th.in.ffc.api.nhso.AuthenCodeRequest;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.form.screening.dao.DistrictDao;
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
import th.in.ffc.person.BmiInfoActivity;
import th.in.ffc.person.BmiInfoDialogFragment;
import th.in.ffc.util.BMICalculator;
import th.in.ffc.util.BMILevel;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.util.ThaiDatePickerDialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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
    private TextInputEditText txtHomeNo,txtVillageNo,txtPostalCode;
    private ImageButton smartcardReader,imgPermission;
    private TextInputEditText currentEditText;

    private Map<EditText, TextFieldUpdater> fieldUpdaters;
    private BMICalculator bmiCalculator;
//    private TextView dateTextView;
    private ImageButton selectDateButton, selectAuthenDateButton, btnAuthenCode;
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

        selectDateButton.setOnClickListener(v -> showDatePickerDialog(this.txtBirthDay,this.personInfo.getBirthday()));
        selectAuthenDateButton = view.findViewById(R.id.selectAuthenDateButton);
        selectAuthenDateButton.setOnClickListener(v -> showDatePickerDialog(this.txtAuthenDate,this.personInfo.getAuthen_date()));
        btnAuthenCode.setOnClickListener(v -> {
            AuthenCodeRequest request = new AuthenCodeRequest();
            request.setPid(citizenId.getText().toString());
            request.setFirstName(fname.getText().toString());
            request.setLastName(lname.getText().toString());
            request.setSex(rdoMale.isChecked() ? "1" : "2");
            request.setBirthDay(convertToWesternDate(txtBirthDay.getText().toString()));

            request.setHn(txtHn.getText().toString());
            request.setHcode("11471");
            request.setSourceId("BKKCC");
            request.setTransId(generateTransId());
            request.setServiceCode("PG0060001");
            request.setSubDistrict(subDistrictName);
            request.setSubDistrictCode(subDistrictCode);
            request.setDistrict(districtName);
            request.setDistrictCode(districtCode);
            request.setProvince(provinceName);
            request.setProvinceCode(provinceCode);
            showProgressBar("Authenticating...");
//            showProgressDialog();

            NhsoApiCaller apiCaller = new NhsoApiCaller(getContext());
            apiCaller.getAuthenCode(request, new NhsoApiCaller.AuthenCodeCallback() {
                @Override
                public void onSuccess(ApiResponse response) {
//                    dismissProgressDialog();
                    hideProgressBar();
                    txtAuthenNo.setText(response.getAuthenCode());
                    txtAuthenDate.setText(DateConverter.convertToThaiBuddhistDate(DateTime.getCurrentDate()));
                    personInfo.setAuthen_code(response.getAuthenCode());
                    personInfo.setAuthen_date(DateConverter.convertToWesternDate(txtAuthenDate.getText().toString()));
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
//                        String formattedResponse = formatResponseData(response);
                        // สร้าง custom view สำหรับแสดงข้อมูล
                        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permission_info, null);
                        TextView tvPermissionInfo = dialogView.findViewById(R.id.tvPermissionInfo);
                        tvPermissionInfo.setText(response);

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialog_AppCompat)
                                .setTitle("ตรวจสอบสิทธิ์")
                                .setView(dialogView)
                                .setIcon(R.drawable.permission)
                                .setPositiveButton("ตกลง", (dialog, which) -> dialog.dismiss());

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
                    // ฟังก์ชันสำหรับจัดรูปแบบข้อมูลที่ได้จาก API ให้อ่านง่ายขึ้น
                    private String formatResponseData(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            StringBuilder formattedData = new StringBuilder();

                            // ข้อมูลส่วนบุคคล
                            formattedData.append("ข้อมูลส่วนตัว\n");
                            formattedData.append("-----------------------------------------\n");

                            if (jsonObject.has("pid")) {
                                formattedData.append("เลขประจำตัวประชาชน: ").append(jsonObject.getString("pid")).append("\n");
                            }

                            if (jsonObject.has("fullName")) {
                                formattedData.append("ชื่อ-นามสกุล: ").append(jsonObject.getString("fullName")).append("\n");
                            }

                            if (jsonObject.has("sex")) {
                                formattedData.append("เพศ: ").append(jsonObject.getString("sex")).append("\n");
                            }

                            if (jsonObject.has("age")) {
                                formattedData.append("อายุ: ").append(jsonObject.getString("age")).append("\n");
                            }

                            if (jsonObject.has("birthDate")) {
                                formattedData.append("วันเกิด: ").append(jsonObject.getString("birthDate")).append("\n");
                            }

                            if (jsonObject.has("nationDescription")) {
                                formattedData.append("สัญชาติ: ").append(jsonObject.getString("nationDescription")).append("\n");
                            }

                            if (jsonObject.has("provinceName")) {
                                formattedData.append("จังหวัด: ").append(jsonObject.getString("provinceName")).append("\n");
                            }

                            // ข้อมูลสิทธิ์การรักษา
                            formattedData.append("ข้อมูลสิทธิ์การรักษา\n");
                            formattedData.append("-----------------------------------------\n");

                            if (jsonObject.has("mainInscl")) {
                                formattedData.append("สิทธิหลัก: ").append(jsonObject.getString("mainInscl")).append("\n");
                            }

                            if (jsonObject.has("subInscl")) {
                                formattedData.append("สิทธิย่อย: ").append(jsonObject.getString("subInscl")).append("\n");
                            }

                            // ข้อมูลสถานพยาบาล
                            formattedData.append("ข้อมูลสถานพยาบาล\n");
                            formattedData.append("-----------------------------------------\n");

                            if (jsonObject.has("hospMain")) {
                                formattedData.append("สถานพยาบาลหลัก: ").append(jsonObject.getString("hospMain")).append("\n");
                            }

                            if (jsonObject.has("hospSub")) {
                                formattedData.append("สถานพยาบาลรอง: ").append(jsonObject.getString("hospSub")).append("\n");
                            }

                            if (jsonObject.has("hospMainOp")) {
                                formattedData.append("สถานพยาบาลประจำ: ").append(jsonObject.getString("hospMainOp")).append("\n");
                            }

                            return formattedData.toString();
                        } catch (JSONException e) {
                            // กรณีไม่สามารถแปลงเป็น JSON ได้ ให้แสดงข้อมูลเดิม
                            Log.e("API_FORMAT", "Error formatting JSON: " + e.getMessage());
                            return response;
                        }
                    }
                });

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
            citizenId.setText(person.getIdcard());
            fname.setText(person.getFname());
            lname.setText(person.getLname());
            txtBirthDay.setText(DateConverter.convertToThaiBuddhistDate(person.getBirthday()));
            txtAuthenDate.setText(DateConverter.convertToThaiBuddhistDate(person.getAuthen_date()));
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
                Intent intent = new Intent(getContext(), SmartCardReaderActivity.class);
                activityResultLauncher.launch(intent);
            }
        });
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
            personInfo.setBirthday(convertToWesternDate(getTextFromEditText(txtBirthDay)));

            // Contact and Hospital Information
            personInfo.setPhone(getTextFromEditText(txtPhoneNo));
            personInfo.setHn(getTextFromEditText(txtHn));
            personInfo.setAuthen_date(convertToWesternDate(getTextFromEditText(txtAuthenDate)));
            personInfo.setAuthen_code(getTextFromEditText(txtAuthenNo));

            // Physical Measurements
            setDoubleValue(txtWeight, value -> personInfo.setWeight(value));
            setDoubleValue(txtHeight, value -> personInfo.setHeight(value));
            setDoubleValue(txtWaistCircumference, value -> personInfo.setWaist_size(value));

            // Blood Pressure Information
            personInfo.setBp(getTextFromEditText(txtBp));
            setDoubleValue(txtSymptomsPressure, value -> personInfo.setSystolic_pressure(value));
            setDoubleValue(txtDiastolicPressure, value -> personInfo.setDiastolic_pressure(value));

            // Default values
            personInfo.setSend_to_claim(0);
            personInfo.setCreated_by("SYSTEM");
            personInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            dataPasser.onPersonInfo(personInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void validateInputs() {
        // You can implement validation logic here
        // For example:
        boolean isValid = true;

        // Validate Citizen ID
        String citizenIdText = citizenId.getText().toString().trim();
        if (citizenIdText.isEmpty()) {
            citizenId.setError("กรุณากรอกเลขบัตรประชาชน");
            isValid = false;
        } else if (citizenIdText.length() != 13) {
            citizenId.setError("เลขบัตรประชาชนต้องมี 13 หลัก");
            isValid = false;
        }

        // Validate Name
        if (fname.getText().toString().trim().isEmpty()) {
            fname.setError("กรุณากรอกชื่อ");
            isValid = false;
        }

        if (lname.getText().toString().trim().isEmpty()) {
            lname.setError("กรุณากรอกนามสกุล");
            isValid = false;
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

        // You might want to enable/disable a submit button based on validation
        // submitButton.setEnabled(isValid);
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
                txtSymptomsPressure, txtDiastolicPressure,txtBirthDay,txtHomeNo,txtVillageNo,txtPostalCode);

        setupTextWatchers();
        loadData();
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
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        byte[] byteArray = data.getByteArrayExtra("image");
                        String strIdcard = data.getStringExtra("result");
                        if (byteArray != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    imgPerson.setImageBitmap(bitmap);
                        }
                        if(strIdcard!=null && !strIdcard.equals("")){

                            String[] idcardInfo = strIdcard.split("#");
                            if(idcardInfo.length>0){
                            citizenId.setText(idcardInfo[0].toString());
                            fname.setText(idcardInfo[2].toString());
                            lname.setText(idcardInfo[4].toString());

                            int day,month,year;
                            year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
                            month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
                            day = Integer.parseInt(idcardInfo[18].substring(6,8));
                            txtBirthDay.setText(day+"/"+month+"/"+year);

                            if(idcardInfo[1].toString().equals("นาย")) {
                                rdoMale.setChecked(true);
                            } else {
                                rdoMale.setChecked(true);
                            }
                        }
                        }
                    }
                }
            }
    );
}