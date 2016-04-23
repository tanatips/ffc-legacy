package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.HospitalListDialog;
import th.in.ffc.provider.PersonProvider.VisitAncDeliver;
import th.in.ffc.provider.PersonProvider.VisitAncMotherCare;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

import java.util.Calendar;

public class VisitAncmothercareActivity extends FFCEditActivity {

    EditText pregno;
    RadioGroup locatecare;
    RadioGroup funduslevel;
    RadioGroup wabad;
    RadioGroup breast;
    RadioGroup milk;
    RadioGroup men;
    ArrayFormatSpinner alb;
    ArrayFormatSpinner sugar;
    ArrayFormatSpinner tear;
    SearchableButton hosservice;
    RadioGroup resultcheck;
    CheckBox checkappoint;
    ThaiDatePicker dateappoint;

    ArrayAdapter<CharSequence> albAdapter;
    ArrayAdapter<CharSequence> sugarAdapter;
    ArrayAdapter<CharSequence> tearAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_ancmothercare_activity);
        preparePicker();
        prepareSpinner();
        setContentDisplay();
    }

    private void preparePicker() {

        checkappoint = (CheckBox) findViewById(R.id.checkappointcare);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer13);
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

    private void prepareSpinner() {
        hosservice = (SearchableButton) findViewById(R.id.answer11);
        hosservice.setDialog(getSupportFragmentManager(),
                HospitalListDialog.class, "hosservice");

        // generateFromResource(alb, albAdapter, R.id.answer8,
        // R.array.mothercare_level);
        // generateFromResource(sugar, sugarAdapter, R.id.answer9,
        // R.array.mothercare_level);
        // generateFromResourceNonZeroBased(tear, tearAdapter, R.id.answer10,
        // R.array.tear);
    }

    private void setContentDisplay() {
        pregno = (EditText) findViewById(R.id.answer1);
        locatecare = (RadioGroup) findViewById(R.id.answer2);
        funduslevel = (RadioGroup) findViewById(R.id.answer3);
        wabad = (RadioGroup) findViewById(R.id.answer4);
        breast = (RadioGroup) findViewById(R.id.answer5);
        milk = (RadioGroup) findViewById(R.id.answer6);
        men = (RadioGroup) findViewById(R.id.answer7);
        alb = (ArrayFormatSpinner) findViewById(R.id.answer8);
        sugar = (ArrayFormatSpinner) findViewById(R.id.answer9);
        tear = (ArrayFormatSpinner) findViewById(R.id.answer10);
        hosservice = (SearchableButton) findViewById(R.id.answer11);
        resultcheck = (RadioGroup) findViewById(R.id.answer12);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer13);
        checkappoint = (CheckBox) findViewById(R.id.checkappointcare);

        alb.setArray(R.array.mothercare_level);
        sugar.setArray(R.array.mothercare_level);
        tear.setArray(R.array.tear);

        setContentQuery(VisitAncMotherCare.CONTENT_URI, new String[]{
                        VisitAncMotherCare.PREGNO, VisitAncMotherCare.LOCATECARE,
                        VisitAncMotherCare.FUNDUSLEVEL, VisitAncMotherCare.WABAD,
                        VisitAncMotherCare.BREAST, VisitAncMotherCare.MILK,
                        VisitAncMotherCare.MEN, VisitAncMotherCare.ALB,
                        VisitAncMotherCare.SUGAR, VisitAncMotherCare.TEAR,
                        VisitAncMotherCare.HOSSERVICE, VisitAncMotherCare.RESULTCHECK,
                        VisitAncMotherCare.DATEAPPOINTCARE}, "visitno = ?",
                new String[]{share_visitno},
                VisitAncMotherCare.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        // codeScreen
        for (String a : array) {
            System.out.println("ARRAY :" + a);
        }

        pregno.setText(checkEditText(array[0]));
        setCheckRadio(array[1], locatecare);
        setCheckRadio(array[2], funduslevel);
        setCheckRadio(array[3], wabad);
        setCheckRadio(array[4], breast);
        setCheckRadio(array[5], milk);
        setCheckRadio(array[6], men);
        if (array[7] != null)
            alb.setSelection(checkStrangerForStringContent(array[7], "1"));
        if (array[8] != null)
            sugar.setSelection(checkStrangerForStringContent(array[8], "1"));
        if (array[9] != null)
            tear.setSelection(checkStrangerForStringContent(array[9], "1"));
        if (array[10] != null)
            hosservice.setSelectionById(array[10]);
        setCheckRadio(array[11], resultcheck);
        if (array[12] != null) {
            checkappoint.setChecked(true);
            updatePicker(dateappoint, R.id.answer13, array[12]);
        }

    }

    private void setCheckRadio(String data, RadioGroup Rg) {
        if (TextUtils.isEmpty(data) && data.equals("-")) {
            Rg.check(R.id.rad1);
        } else {
            int select = Integer.parseInt(data);
            switch (select) {
                case 1:
                    Rg.check(R.id.rad1);
                    break;
                case 2:
                    Rg.check(R.id.rad2);
                    break;
                case 3:
                    Rg.check(R.id.rad3);
                    break;
                default:
                    Rg.check(R.id.rad1);
                    break;
            }
        }
    }

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
        cv.put("pcucodeperson", getPcuCode());
        cv.put("visitno", share_visitno);
        cv.put("pid", share_pid);
        cv.put("datecare", DateTime.getCurrentDate());
        retrieveDataFromEditText(pregno, cv, VisitAncDeliver.PREGNO);
        if (hosservice.getSelectId() != null)
            cv.put(VisitAncMotherCare.HOSSERVICE, hosservice.getSelectId());
        else
            cv.put(VisitAncMotherCare.HOSSERVICE, "00000");
        cv.put(VisitAncMotherCare.ALB, alb.getSelectionId());
        cv.put(VisitAncMotherCare.SUGAR, sugar.getSelectionId());
        cv.put(VisitAncMotherCare.TEAR, tear.getSelectionId());

        retrieveDataFromRadioGroup(locatecare, cv,
                VisitAncMotherCare.LOCATECARE);
        retrieveDataFromRadioGroup(funduslevel, cv,
                VisitAncMotherCare.FUNDUSLEVEL);
        retrieveDataFromRadioGroup(wabad, cv, VisitAncMotherCare.WABAD);
        retrieveDataFromRadioGroup(breast, cv, VisitAncMotherCare.BREAST);
        retrieveDataFromRadioGroup(milk, cv, VisitAncMotherCare.MILK);
        retrieveDataFromRadioGroup(men, cv, VisitAncMotherCare.MEN);
        retrieveDataFromRadioGroup(resultcheck, cv,
                VisitAncMotherCare.RESULTCHECK);
        retrieveDataFromThaiDatePicker(dateappoint, cv,
                VisitAncMotherCare.DATEAPPOINTCARE, checkappoint.isChecked());
        updateTimeStamp(cv);

        if (pregno.getText().toString().isEmpty())
            ERROR_MSG += "\n - " + getString(R.string.err_no_pregno);
        else
            canCommit = true;

        if (checkappoint.isChecked())
            canCommit = checkDate(dateappoint);

        doCommit(cv, VisitAncMotherCare.CONTENT_URI, canCommit);
    }

}
