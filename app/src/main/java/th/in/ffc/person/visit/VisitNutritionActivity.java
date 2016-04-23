package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.Visit;
import th.in.ffc.provider.PersonProvider.VisitNutrition;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;
import th.in.ffc.util.JhcisNutritionFormular;
import th.in.ffc.util.ThaiDatePicker;

import java.util.Calendar;

public class VisitNutritionActivity extends FFCEditActivity {
    EditText weight;
    EditText tall;
    EditText headcycle;
    EditText toothnew;
    EditText toothcorrupt;
    RadioGroup navel;
    EditText remark;
    TextView nlevel;
    TextView nlevel2;
    TextView nlevel3;
    CheckBox checkappoint;
    ThaiDatePicker dateappoint;
    String birthDate;
    String sex;
    String lv1;
    String lv2;
    String lv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_nutrition_activity);
        prepareDatePicker();
        setContentDisplay();
    }

    private void prepareDatePicker() {
        // TODO Auto-generated method stub
        checkappoint = (CheckBox) findViewById(R.id.checkappointcare);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer11);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dateappoint.updateDate(year, month, day);

        checkappoint.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                dateappoint.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        weight = (EditText) findViewById(R.id.answer1);
        tall = (EditText) findViewById(R.id.answer2);
        headcycle = (EditText) findViewById(R.id.answer3);
        toothnew = (EditText) findViewById(R.id.answer4);
        toothcorrupt = (EditText) findViewById(R.id.answer5);
        navel = (RadioGroup) findViewById(R.id.answer6);
        remark = (EditText) findViewById(R.id.answer7);
        nlevel = (TextView) findViewById(R.id.answer8);
        nlevel2 = (TextView) findViewById(R.id.answer9);
        nlevel3 = (TextView) findViewById(R.id.answer10);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer11);

        weight.addTextChangedListener(nlevelCalculator);
        tall.addTextChangedListener(nlevelCalculator);
        setContentQuery(VisitNutrition.CONTENT_URI, new String[]{
                        VisitNutrition.WEIGHT, VisitNutrition.TALL,
                        VisitNutrition.HEADCYCLE, VisitNutrition.TOOTHNEW,
                        VisitNutrition.TOOTHCORRUPT, VisitNutrition.NAVEL,
                        VisitNutrition.REMARK, VisitNutrition.NLEVEL,
                        VisitNutrition.NLEVEL2, VisitNutrition.NLEVEL3, VisitNutrition.DATEAPPOINT
                }, "visitno =? AND pcucode =?", new String[]{share_visitno, getPcuCode()},
                VisitNutrition.DEFAULT_SORTING);
        getInformation();
        //get Current weight and height
        getCurrentInfo(weight, tall);

        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void getCurrentInfo(EditText w, EditText h) {
        Cursor c = getContentResolver().query(Visit.CONTENT_URI, new String[]{Visit.WEIGHT, Visit.HEIGHT}, "visitno=?", new String[]{share_visitno}, "visitno desc");
        if (c.moveToFirst()) {
            w.setText(c.getString(0));
            h.setText(c.getString(1));
        }
    }

    private void getInformation() {
        // TODO Auto-generated method stub
        Cursor c = getContentResolver().query(Person.CONTENT_URI, new String[]{Person.BIRTH, Person.SEX}, "pid = ?", new String[]{share_pid},
                Person.DEFAULT_SORTING);
        if (c.moveToFirst()) {
            this.birthDate = c.getString(0);
            this.sex = c.getString(1);
        } else
            System.out.println("Failed to get BirthDate");
    }


    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        weight.setText(checkEditText(array[0]));
        tall.setText(checkEditText(array[1]));
        headcycle.setText(checkEditText(array[2]));
        toothnew.setText(checkEditText(array[3]));
        toothcorrupt.setText(checkEditText(array[4]));
        if (array[5] != null)
            navel.check(array[5].equals("1") ? R.id.rad2 : R.id.rad1);
        remark.setText(checkEditText(array[6]));
        //NLEVEL 3 RESERVER

        checkappoint.setChecked(array[10] != null ? true : false);
        if (checkappoint.isChecked())
            updatePicker(dateappoint, R.id.answer11, array[10]);

    }

    TextWatcher nlevelCalculator = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            String w = weight.getText().toString();
            String h = tall.getText().toString();
            Date current = Date.newInstance(DateTime.getCurrentDate());
            Date born = Date.newInstance(birthDate);
            AgeCalculator ac = new AgeCalculator(current, born);
            Date YearAge = ac.calulate();

            if (!TextUtils.isEmpty(w) && !TextUtils.isEmpty(h) && !w.equals("0") && !h.equals("0")) {
                double weight = Double.parseDouble(w);
                double height = Double.parseDouble(h);
                int heightNotPrecise = Integer.parseInt(h);
                int y = YearAge.year;
                int age = YearAge.month;
                age = age + (y * 12);

                lv1 = JhcisNutritionFormular.UpdateNutritionStateAgeLessThen18AgeWeight(age, weight, sex);
                lv2 = JhcisNutritionFormular.UpdateNutritionStateAgeLessThen18AgeHeight(age, height, sex);
                lv3 = JhcisNutritionFormular.UpdateNutritionStateAgeLessThen18WeightHeight(weight, heightNotPrecise, sex);
                String[] nlv1 = getResources().getStringArray(R.array.nlevel1);
                String[] nlv2 = getResources().getStringArray(R.array.nlevel2);
                String[] nlv3 = getResources().getStringArray(R.array.nlevel3);

                nlevel.setText(nlv1[Integer.parseInt(lv1)]);

                nlevel2.setText(nlv2[Integer.parseInt(lv2)]);
                nlevel3.setText(nlv3[Integer.parseInt(lv3)]);
            }

        }
    };

    @Override
    protected void Delete() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void Edit() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void Update() {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);
        cv.put(VisitNutrition.WEIGHT, (!TextUtils.isEmpty(weight.getText()) ? weight.getText().toString() : "0"));
        cv.put(VisitNutrition.TALL, (!TextUtils.isEmpty(tall.getText()) ? tall.getText().toString() : "0"));
        cv.put(VisitNutrition.HEADCYCLE, (!TextUtils.isEmpty(headcycle.getText()) ? headcycle.getText().toString() : "0"));
        cv.put(VisitNutrition.TOOTHNEW, (!TextUtils.isEmpty(toothnew.getText()) ? toothnew.getText().toString() : "0"));
        cv.put(VisitNutrition.TOOTHCORRUPT, (!TextUtils.isEmpty(toothcorrupt.getText()) ? toothcorrupt.getText().toString() : "0"));
        cv.put(VisitNutrition.NAVEL, navel.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitNutrition.REMARK, remark.getText().toString());
        if (!lv1.equals("6"))
            cv.put(VisitNutrition.NLEVEL, lv1);
        if (!lv1.equals("5"))
            cv.put(VisitNutrition.NLEVEL2, lv2);
        if (!lv1.equals("6"))
            cv.put(VisitNutrition.NLEVEL3, lv3);
        retrieveDataFromThaiDatePicker(dateappoint, cv, VisitNutrition.DATEAPPOINT, checkappoint.isChecked());
        updateTimeStamp(cv);
        doCommit(cv, VisitNutrition.CONTENT_URI, true);

    }

}
