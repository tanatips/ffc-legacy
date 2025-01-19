package th.in.ffc.app.form.screening;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
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

import java.util.HashMap;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import androidx.appcompat.app.AlertDialog;

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
    private ImageButton smartcardReader;
    private TextInputEditText currentEditText;

    private Map<EditText, TextFieldUpdater> fieldUpdaters;
    private BMICalculator bmiCalculator;
//    private TextView dateTextView;
    private ImageButton selectDateButton, selectAuthenDateButton;
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

    private void setupDatePicker(){

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
        setupWeightInfoButton();
        setupBmiInfoButton();
        setupBloodPressureButton();
    }

    private void initializeViews(View view) {
        personInfo = new PersonInfo();
        smartcardReader = view.findViewById(R.id.smartcard_reader);
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

        view.findViewById(R.id.smartcard_reader);

//        dateTextView = view.findViewById(R.id.dateTextView);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        txtBirthDay = view.findViewById(R.id.txtBirthDay);

        selectDateButton.setOnClickListener(v -> showDatePickerDialog(this.txtBirthDay,this.personInfo.getBirthday()));
        selectAuthenDateButton = view.findViewById(R.id.selectAuthenDateButton);
        selectAuthenDateButton.setOnClickListener(v -> showDatePickerDialog(this.txtAuthenDate,this.personInfo.getAuthen_date()));

    }
    private void showDatePickerDialog(TextInputEditText editText, String mydate) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(mydate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

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
                            this.personInfo.setBirthday(DateConverter.convertToWesternDate(editText.getText().toString()));
                        }
                        else if(editText == txtAuthenDate) {
                            this.personInfo.setAuthen_date(DateConverter.convertToWesternDate(editText.getText().toString()));
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
                            updater.update(s.toString());
                            dataPasser.onPersonInfo(personInfo);
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
            personInfo.setBirthday(DateConverter.convertToWesternDate(getTextFromEditText(txtBirthDay)));

            // Contact and Hospital Information
            personInfo.setPhone(getTextFromEditText(txtPhoneNo));
            personInfo.setHn(getTextFromEditText(txtHn));
            personInfo.setAuthen_date(DateConverter.convertToWesternDate(getTextFromEditText(txtAuthenDate)));
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
                txtSymptomsPressure, txtDiastolicPressure);
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
//                            birthday.updateDate(year , month, day);
//                    f.hno.setText(idcardInfo[9].toString());
                            if(idcardInfo[1].toString().equals("นาย")) {
                                rdoMale.setChecked(true);
                            } else {
                                rdoMale.setChecked(true);
                            }
//                    String[] prenameArray = getResources().getStringArray(R.array.prename);
//                    String defaultValue = idcardInfo[1];
//                    int defaultPosition = -1;
//                    for (int i = 0; i < prenameArray.length; i++) {
//                        if (prenameArray[i].contains(defaultValue)) {
//                            defaultPosition = i;
//                            break;
//                        }
//                    }
//                    int day,month,year;
//                    year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
//                    month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
//                    day = Integer.parseInt(idcardInfo[18].substring(6,8));
//                    f.birthday.updateDate(year , month, day);
//                    f.prename.setSelection(defaultPosition);

//                    for(PersonDetailEditActivity.MyItem item: provinces){
//                        if(item.nane.equals(idcardInfo[16])){
//                            f.provcode.setSelectionById(item.id);
//                            System.out.println("Province ID: " + item.id + ", Name: " + item.nane);
//                            break;
//                        }
//                    }
//
//                    for(PersonDetailEditActivity.MyItem item: districts){
//                        if(item.nane.equals(idcardInfo[16])){
//                            f.distcode.setSelectionById(item.id);
//                            System.out.println("District ID: " + item.id + ", Name: " + item.nane);
//                            break;
//                        }
//                    }
                        }
                            }
                    }
                }
            }
    );
}