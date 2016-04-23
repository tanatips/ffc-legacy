package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitBabycare;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.ArrayFormatSpinner;

import java.util.Calendar;

public class VisitBabycareActivity extends FFCEditActivity {

    ArrayFormatSpinner locatecare;
    RadioGroup navel;
    RadioGroup skin;
    RadioGroup urine;
    RadioGroup feci;
    RadioGroup babyhealth;
    CheckBox checkappointcare;
    ThaiDatePicker dateappointcare;

    ArrayAdapter<CharSequence> locateadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_babycare_activity);
        prepareDatePicker();
        setContentDisplay();
    }


    private void prepareDatePicker() {
        // TODO Auto-generated method stub
        checkappointcare = (CheckBox) findViewById(R.id.checkappointcare);
        dateappointcare = (ThaiDatePicker) findViewById(R.id.answer7);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dateappointcare.updateDate(year, month, day);

        checkappointcare
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        dateappointcare.setVisibility(isChecked ? View.VISIBLE
                                : View.GONE);
                    }
                });

    }

    private void setContentDisplay() {

        // locatecare=(RadioGroup)findViewById(R.id.answer1);
        locatecare = (ArrayFormatSpinner) findViewById(R.id.answer1);
        locatecare.setArray(R.array.locatecare);
        navel = (RadioGroup) findViewById(R.id.answer2);
        skin = (RadioGroup) findViewById(R.id.answer3);
        urine = (RadioGroup) findViewById(R.id.answer4);
        feci = (RadioGroup) findViewById(R.id.answer5);
        babyhealth = (RadioGroup) findViewById(R.id.answer6);
        checkappointcare = (CheckBox) findViewById(R.id.checkappointcare);
        dateappointcare = (ThaiDatePicker) findViewById(R.id.answer7);

        setContentQuery(VisitBabycare.CONTENT_URI, new String[]{
                        VisitBabycare.LOCATECARE, VisitBabycare.NAVEL,
                        VisitBabycare.SKIN, VisitBabycare.URINE, VisitBabycare.FECI,
                        VisitBabycare.BABYHEALTH, VisitBabycare.DATEAPPOINTCARE},
                "visitno =?", new String[]{share_visitno},
                VisitBabycare.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        for (String a : array) {
            System.out.println("ARRAT " + a);
        }

        locatecare.setSelection(checkStrangerForStringContent(array[0], "1"));
        if (array[1] != null)
            navel.check(array[1].equalsIgnoreCase("1") ? R.id.rad2 : R.id.rad1);
        if (array[2] != null)
            skin.check(array[2].equalsIgnoreCase("1") ? R.id.rad2 : R.id.rad1);
        if (array[3] != null)
            urine.check(array[3].equalsIgnoreCase("1") ? R.id.rad2 : R.id.rad1);
        if (array[4] != null)
            feci.check(array[4].equalsIgnoreCase("1") ? R.id.rad2 : R.id.rad1);
        if (array[5] != null)
            babyhealth.check(array[5].equalsIgnoreCase("1") ? R.id.rad2 : R.id.rad1);

        checkappointcare.setChecked(array[6] != null ? true : false);
        if (checkappointcare.isChecked())
            updatePicker(dateappointcare, R.id.answer7, array[6]);
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

        cv.put("pcucodeperson", getPcuCode());
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);
        cv.put("pid", share_pid);
        // Auto generate datefp by System
        setCurrentDate(cv, VisitBabycare.DATECARE);

        // cv.put("locatecare",
        // locatecare.getCheckedRadioButtonId()==R.id.rad1?"0":"1");
        cv.put(VisitBabycare.LOCATECARE, locatecare.getSelectionId());
        cv.put(VisitBabycare.NAVEL, navel.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitBabycare.SKIN, skin.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitBabycare.URINE, urine.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitBabycare.FECI, feci.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitBabycare.BABYHEALTH, babyhealth.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");

        if (checkappointcare.isChecked()) {
            retrieveDataFromThaiDatePicker(dateappointcare, cv,
                    VisitBabycare.DATEAPPOINTCARE, checkappointcare.isChecked());
            //canCommit = checkSickDate(dateappointcare);
        } else
            cv.putNull(VisitBabycare.DATEAPPOINTCARE);
        updateTimeStamp(cv);
        doCommit(cv, VisitBabycare.CONTENT_URI, true);
    }

}
