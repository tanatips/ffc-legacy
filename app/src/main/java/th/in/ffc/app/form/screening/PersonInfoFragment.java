package th.in.ffc.app.form.screening;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.ThaiDatePicker;

public class PersonInfoFragment extends Fragment {

    int SMART_CARD_READER_CODE = 0;
    EditText citizenId, fname,lname ;
    ThaiDatePicker birthday;
    RadioGroup rdoGender;

    RadioButton rdoMale,rdoFemale;

    private OnDataPass dataPasser;
    PersonInfo personInfo;
    private EditText txtPhoneNo, txtHn;
    private EditText txtAuthenDate, txtAuthenNo, txtWeight, txtHeight;
    private EditText txtWaistCircumference, txtBp, txtBmi;
    private EditText txySymptomsPressure, txyDiastolicPressure;
    private Button smartcardReader;

   

    public PersonInfoFragment() {
        // Required empty public constructor
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
    private void initializeViews(View view) {
        personInfo = new PersonInfo();
        smartcardReader = view.findViewById(R.id.smartcard_reader);
        citizenId = view.findViewById(R.id.citizenId);
        fname = view.findViewById(R.id.fname);
        lname = view.findViewById(R.id.lname);
        birthday = view.findViewById(R.id.birthday);

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
        txySymptomsPressure = view.findViewById(R.id.txySymptomsPressure);
        txyDiastolicPressure = view.findViewById(R.id.txyDiastolicPressure);


    }
    private void DefaultValueForTest(){
        // Default for test

        int day,month,year;
        String bDay="25200511";
        year = Integer.parseInt(bDay.substring(0,4))-543;
        month = Integer.parseInt(bDay.substring(4,6))-1;
        day = Integer.parseInt(bDay.substring(6,8));
        birthday.updateDate(year , month, day);

        // Set values (example)
        citizenId.setText("1234567890123");
        fname.setText("สมชาย");
        lname.setText("ใจดี");
        txtPhoneNo.setText("0812345678");
        txtHn.setText("HN001");
        txtAuthenDate.setText("2024-11-24");
        txtAuthenNo.setText("AUTH001");
        txtWeight.setText("70");
        txtHeight.setText("170");
        txtWaistCircumference.setText("32");
        txtBp.setText("120/80");
        txtBmi.setText("24.2");
        txySymptomsPressure.setText("120");
        txyDiastolicPressure.setText("80");
        // Set default gender
        rdoMale.setChecked(true);
        updatePersonInfo();
    }
    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Called before text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called when text is being changed

                updatePersonInfo();

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called after text has been changed
                validateInputs();
            }
        };

        // Add TextWatcher to all EditText fields
        citizenId.addTextChangedListener(watcher);
        fname.addTextChangedListener(watcher);
        lname.addTextChangedListener(watcher);
        txtPhoneNo.addTextChangedListener(watcher);
        txtHn.addTextChangedListener(watcher);
        txtAuthenDate.addTextChangedListener(watcher);
        txtAuthenNo.addTextChangedListener(watcher);
        txtWeight.addTextChangedListener(watcher);
        txtHeight.addTextChangedListener(watcher);
        txtWaistCircumference.addTextChangedListener(watcher);
        txtBp.addTextChangedListener(watcher);
        txtBmi.addTextChangedListener(watcher);
        txySymptomsPressure.addTextChangedListener(watcher);
        txyDiastolicPressure.addTextChangedListener(watcher);
        smartcardReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SmartCardReaderActivity.class);
                activityResultLauncher.launch(intent);
            }
        });
        birthday.setOnDateUpdateListner(new ThaiDatePicker.OnDateUpdateListener() {
            @Override
            public void onDateUpdate(DateTime.Date date) {
                personInfo.setBirthday(date.toString());
//                Toast.makeText(getContext(),date.toString(),Toast.LENGTH_LONG).show();
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
//                        Toast.makeText(getContext(), "M", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rdoFemale:
                        personInfo.setGender("F");
                        dataPasser.onPersonInfo(personInfo);
//                        Toast.makeText(getContext(), "F", Toast.LENGTH_SHORT).show();
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

            // Birthday (assuming ThaiDatePicker returns String in required format)
            if (birthday != null) {
                personInfo.setBirthday(birthday.getDate().toString());
            }

            // Contact and Hospital Information
            personInfo.setPhone(getTextFromEditText(txtPhoneNo));
            personInfo.setHn(getTextFromEditText(txtHn));
            personInfo.setAuthen_date(getTextFromEditText(txtAuthenDate));
            personInfo.setAuthen_code(getTextFromEditText(txtAuthenNo));

            // Physical Measurements
            setDoubleValue(txtWeight, value -> personInfo.setWeight(value));
            setDoubleValue(txtHeight, value -> personInfo.setHeight(value));
            setDoubleValue(txtWaistCircumference, value -> personInfo.setWaist_size(value));

            // Blood Pressure Information
            personInfo.setBp(getTextFromEditText(txtBp));
            setDoubleValue(txySymptomsPressure, value -> personInfo.setSystolic_pressure(value));
            setDoubleValue(txyDiastolicPressure, value -> personInfo.setDiastolic_pressure(value));

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
        DefaultValueForTest();
        setupTextWatchers();
        return view;
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
                        if(strIdcard!=null){
                            String[] idcardInfo = strIdcard.split("#");
                            citizenId.setText(idcardInfo[0].toString());
                            fname.setText(idcardInfo[2].toString());
                            lname.setText(idcardInfo[4].toString());

                            int day,month,year;
                            year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
                            month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
                            day = Integer.parseInt(idcardInfo[18].substring(6,8));
                            birthday.updateDate(year , month, day);
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
    );
}