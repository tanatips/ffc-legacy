package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.app.FFCSearchListDialog;
import th.in.ffc.code.DiagnosisListDialog;
import th.in.ffc.code.PersonListDialog;
import th.in.ffc.person.PersonFragment;
import th.in.ffc.provider.PersonProvider.Behavior;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.Visit;
import th.in.ffc.provider.PersonProvider.VisitOldter;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;
import th.in.ffc.widget.SearchableSpinner;

public class VisitOldterActivity extends FFCEditActivity {
    ArrayFormatSpinner dentalcheck;
    EditText sleephour;
    RadioGroup sleep_q1;
    RadioGroup clubmember;
    EditText clubname;
    RadioGroup funded;
    EditText money_funded;
    RadioGroup carekeeper;
    //	EditText carekeeper_name;
    SearchableSpinner carekeeper_name;
    RadioGroup pressure_q1;

    ArrayFormatSpinner q_congenital;
    SearchableButton q_diag;

    ArrayFormatSpinner pressure_work;
    ArrayFormatSpinner pressure_family;
    ArrayFormatSpinner pressure_social;

    LinearLayout ifHaveQ;
    LinearLayout ifClub;
    LinearLayout ifFund;
    LinearLayout ifCare;
    LinearLayout ifPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_oldter_activity);
        setContentDisplay();
    }

    private void setContentDisplay() {
        dentalcheck = (ArrayFormatSpinner) findViewById(R.id.answer1);
        dentalcheck.setArray(R.array.dentalcheck);
        sleephour = (EditText) findViewById(R.id.answer2);
        sleep_q1 = (RadioGroup) findViewById(R.id.answer3);
        clubmember = (RadioGroup) findViewById(R.id.answer4);
        clubname = (EditText) findViewById(R.id.answer5);
        funded = (RadioGroup) findViewById(R.id.answer6);
        money_funded = (EditText) findViewById(R.id.answer7);
        carekeeper = (RadioGroup) findViewById(R.id.answer8);
        //carekeeper_name = (EditText) findViewById(R.id.answer9);
        carekeeper_name = (SearchableSpinner) findViewById(R.id.answer9);
        Bundle bb = new Bundle();
        bb.putString(PersonFragment.EXTRA_HCODE, getHCODE());
        bb.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "hcode=" + getHCODE());
        carekeeper_name.setDialog(getSupportFragmentManager(), PersonListDialog.class, bb, "person by House");
        pressure_q1 = (RadioGroup) findViewById(R.id.answer10);
        pressure_work = (ArrayFormatSpinner) findViewById(R.id.answer11);
        pressure_family = (ArrayFormatSpinner) findViewById(R.id.answer12);
        pressure_social = (ArrayFormatSpinner) findViewById(R.id.answer14);

        q_congenital = (ArrayFormatSpinner) findViewById(R.id.qdisease);
        q_congenital.setArray(R.array.q_congenitaldisease);
        q_diag = (SearchableButton) findViewById(R.id.qdiagcode);
        q_diag.setDialog(getSupportFragmentManager(),
                DiagnosisListDialog.class, "diagcode");

        ifHaveQ = (LinearLayout) findViewById(R.id.ifHaveQ);
        ifClub = (LinearLayout) findViewById(R.id.clubNameIfHave);
        ifFund = (LinearLayout) findViewById(R.id.fundedIfRecv);
        ifCare = (LinearLayout) findViewById(R.id.ifHaveCare);
        ifPressure = (LinearLayout) findViewById(R.id.ifHavePressure);

        pressure_work.setArray(R.array.hi_lo);
        pressure_family.setArray(R.array.hi_lo);
        pressure_social.setArray(R.array.hi_lo);

        q_congenital.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (q_congenital.getSelectionId().equals("2")) {
                    ifHaveQ.setVisibility(View.VISIBLE);
                } else {
                    ifHaveQ.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        clubmember.setOnCheckedChangeListener(radCheckListener);
        funded.setOnCheckedChangeListener(radCheckListener);
        carekeeper.setOnCheckedChangeListener(radCheckListener);
        pressure_q1.setOnCheckedChangeListener(radCheckListener);

        setContentQuery(VisitOldter.CONTENT_URI, new String[]{
                        VisitOldter.DENTALCHECK, VisitOldter.SLEEPHOUR,
                        VisitOldter.SLEEP_Q1, VisitOldter.CLUBNAME, VisitOldter.FUNDED,
                        VisitOldter.MONEY_FUNDED, VisitOldter.CAREKEEPER_NAME,
                        VisitOldter.PRESSURE_Q1, VisitOldter.PRESSURE_WORK,
                        VisitOldter.PRESSURE_FAMILY, VisitOldter.PRESSURE_SOCIAL,
                        VisitOldter.Q_CONGENITALDISEASE, VisitOldter.DIAGCODE},
                "visitno =? AND pcucode =?", new String[]{share_visitno,
                        getPcuCode()}, VisitOldter.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private String getHCODE() {
        String hcode = null;
        Cursor c = getContentResolver().query(Person.CONTENT_URI, new String[]{Person.HCODE}, "pid =? AND pcucodeperson=?", new String[]{share_pid, share_pcucode}, "pid desc");
        if (c.moveToFirst()) {
            hcode = c.getString(0);
        }
        return hcode;
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        // for (String a : array) {
        // System.out.println("ARRAY " + a);
        // }
        if (array[0] != null)
            dentalcheck.setSelection(checkStrangerForStringContent(array[0],
                    "0"));
        sleephour.setText(array[1]);
        if (array[2] != null)
            sleep_q1.check(array[2].equals("0") ? R.id.rad1 : R.id.rad2);
        if (array[3] != null) {
            clubmember.check(R.id.rad2);
            clubname.setText(array[3]);
        } else {
            clubmember.check(R.id.rad1);
        }
        if (array[4].equals("1")) {
            funded.check(R.id.rad2);
            money_funded.setText(array[5]);
        } else {
            funded.check(R.id.rad1);
        }
        if (array[6] != null) {
            carekeeper.check(R.id.rad2);
            carekeeper_name.setSelectionById(Long.parseLong(array[6]));
        } else {
            carekeeper.check(R.id.rad1);
        }

        if (array[7] != null)
            pressure_q1.check(array[7].equals("0") ? R.id.rad1 : R.id.rad2);
        if (array[8] != null)
            pressure_work.setSelection(checkStrangerForStringContent(array[8],
                    "0"));
        if (array[9] != null)
            pressure_family.setSelection(checkStrangerForStringContent(
                    array[9], "0"));
        if (array[10] != null)
            pressure_social.setSelection(checkStrangerForStringContent(
                    array[10], "0"));
        if (array[11] != null)
            q_congenital.setSelection(checkStrangerForStringContent(array[11], "1"));
        if (array[12] != null)
            q_diag.setSelectionById(checkStrangerForStringContent(array[12], null));
    }

    private void setContentVisibilityHandler(RadioGroup obj) {
        // TODO Auto-generated method stub
        int value = (obj.getCheckedRadioButtonId() == R.id.rad1
                || obj.getCheckedRadioButtonId() == R.id.rad3 ? View.GONE
                : View.VISIBLE);
        switch (obj.getId()) {
            case R.id.answer4:
                ifClub.setVisibility(value);
                break;
            case R.id.answer6:
                ifFund.setVisibility(value);
                break;
            case R.id.answer8:
                ifCare.setVisibility(value);
                break;
            case R.id.answer10:
                ifPressure.setVisibility(value);
                break;
            default:
                break;
        }
    }

    OnCheckedChangeListener radCheckListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            setContentVisibilityHandler(group);
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
        cv.put("pcucodeperson", getPcuCode());
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);
        cv.put(VisitOldter.DENTALCHECK, dentalcheck.getSelectionId());
        if (!TextUtils.isEmpty(sleephour.getText()))
            cv.put(VisitOldter.SLEEPHOUR, sleephour.getText().toString());
        else
            cv.put(VisitOldter.SLEEPHOUR, 0);
        cv.put(VisitOldter.SLEEP_Q1,
                sleep_q1.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");

        cv.put(VisitOldter.FUNDED,
                funded.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitOldter.PRESSURE_Q1,
                pressure_q1.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");

        if (clubmember.getCheckedRadioButtonId() == R.id.rad2)
            cv.put(VisitOldter.CLUBNAME, clubname.getText().toString());
        else
            cv.putNull(VisitOldter.CLUBNAME);

        if (funded.getCheckedRadioButtonId() == R.id.rad2)
            cv.put(VisitOldter.MONEY_FUNDED, money_funded.getText().toString());
        else
            cv.put(VisitOldter.MONEY_FUNDED, 0);

        if (carekeeper.getCheckedRadioButtonId() == R.id.rad2)
            cv.put(VisitOldter.CAREKEEPER_NAME, carekeeper_name.getSelectedItemId());
        else
            cv.putNull(VisitOldter.CAREKEEPER_NAME);

        if (pressure_q1.getCheckedRadioButtonId() == R.id.rad2) {
            cv.put(VisitOldter.PRESSURE_WORK, pressure_work.getSelectionId());
            cv.put(VisitOldter.PRESSURE_FAMILY,
                    pressure_family.getSelectionId());
            // cv.put(VisitOldter.PRESSURE_MONEY,pressure_money.getSelectionId());
            cv.put(VisitOldter.PRESSURE_SOCIAL,
                    pressure_social.getSelectionId());
        } else {
            cv.putNull(VisitOldter.PRESSURE_WORK);
            cv.putNull(VisitOldter.PRESSURE_FAMILY);
            // cv.putNull(VisitOldter.PRESSURE_MONEY);
            cv.putNull(VisitOldter.PRESSURE_SOCIAL);
        }

        cv.put(VisitOldter.Q_CONGENITALDISEASE, q_congenital.getSelectionId());

        if (q_congenital.getSelectionId().equals("2"))
            cv.put(VisitOldter.DIAGCODE, q_diag.getSelectId().toString());
        else
            cv.putNull(VisitOldter.DIAGCODE);
        cv.put("carekeeper", (carekeeper.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1"));
        cv.put("clubmember", clubmember.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");


        canCommit = true;
        if (q_congenital.getSelectionId().equals("2"))
            canCommit = q_diag.getSelectId() != null ? true : false;

        updateTimeStamp(cv);
        doFetchOtherInformation(cv);
        doCommit(cv, VisitOldter.CONTENT_URI, canCommit);
    }

    private void doFetchOtherInformation(ContentValues cv) {
        // TODO Auto-generated method stub
        Cursor c = getContentResolver().query(Person.CONTENT_URI,
                new String[]{Person.SEX, Person.BIRTH}, "pid = ?",
                new String[]{share_pid}, Person.DEFAULT_SORTING);
        if (c.moveToFirst()) {
            cv.put(Person.PID, share_pid);
            cv.put(Person.SEX, c.getString(0));
            Date current = Date.newInstance(DateTime.getCurrentDate());
            Date born = Date.newInstance(c.getString(1));
            AgeCalculator ac = new AgeCalculator(current, born);
            Date age = ac.calulate();
            cv.put(VisitOldter.AGE, age.year);
        }

        c = getContentResolver().query(Visit.CONTENT_URI,
                new String[]{Visit.WEIGHT, Visit.HEIGHT, Visit.WAIST},
                "visitno = ?", new String[]{share_visitno},
                Visit.DEFAULT_SORTING);
        if (c.moveToFirst()) {
            cv.put(VisitOldter.WEIGHT, c.getString(0));
            cv.put(VisitOldter.HEIGHT, c.getString(1));
            cv.put(VisitOldter.WAIST, c.getString(2));
        }

        c = getContentResolver()
                .query(Behavior.CONTENT_URI,
                        new String[]{Behavior.EXERCISE, Behavior.WISKY,
                                Behavior.CIGA, Behavior.TONIC, Behavior.SUGAR,
                                Behavior.SALT, Behavior.DRUGBYSELF,
                                Behavior.ACCIDENT}, "pid = ?",
                        new String[]{share_pid}, Behavior.DEFAULT_SORTING);
        if (c.moveToFirst()) {
            cv.put(VisitOldter.EXERCISE, c.getString(0));
            cv.put(VisitOldter.WISKY, c.getString(1));
            cv.put(VisitOldter.CIGA, c.getString(2));
            cv.put(VisitOldter.TONIC, c.getString(3));
            cv.put(VisitOldter.SUGAR, c.getString(4));
            cv.put(VisitOldter.SALT, c.getString(5));
            cv.put(VisitOldter.DRUGBYYOURSEFT, c.getString(6));
            cv.put(VisitOldter.BIGACCIDENTEVER, c.getString(7));
        }

    }

}
