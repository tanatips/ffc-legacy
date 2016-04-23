package th.in.ffc.person;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.DiagnosisListDialog;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.provider.PersonProvider.PersonDeath;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

import java.util.Calendar;

public class PersonDeathActivity extends FFCEditActivity {

    ThaiDatePicker deathdate;
    ArrayFormatSpinner deathplace;
    SearchableButton deatha;
    SearchableButton deathb;
    SearchableButton deathc;
    SearchableButton deathd;
    SearchableButton deathdisease;
    SearchableButton deathcause;
    TextView isfemale;
    ArrayFormatSpinner deliverconcern;
    ArrayFormatSpinner source;
    String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        //share_visitno = data.getLastPathSegment();
        share_pid = data.getLastPathSegment();
        share_pcucode = getIntent().getExtras().getString(PersonColumns._PCUCODEPERSON);
//		System.out.println(share_visitno);
//		System.out.println(getPcuCode());

        setContentView(R.layout.person_death_activity);
        setContentDisplay();
    }


    private void setContentDisplay() {
        // TODO Auto-generated method stub
        deathdate = (ThaiDatePicker) findViewById(R.id.answer1);
        deathplace = (ArrayFormatSpinner) findViewById(R.id.answer2);
        deatha = (SearchableButton) findViewById(R.id.answer3);
        deathb = (SearchableButton) findViewById(R.id.answer4);
        deathc = (SearchableButton) findViewById(R.id.answer5);
        deathd = (SearchableButton) findViewById(R.id.answer6);
        deathdisease = (SearchableButton) findViewById(R.id.answer7);
        deathcause = (SearchableButton) findViewById(R.id.answer8);
        deliverconcern = (ArrayFormatSpinner) findViewById(R.id.answer9);
        source = (ArrayFormatSpinner) findViewById(R.id.answer10);
        isfemale = (TextView) findViewById(R.id.isFemale);

        deatha.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathb.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathc.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathd.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathdisease.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathcause.setDialog(getSupportFragmentManager(), DiagnosisListDialog.class, "diagcode");
        deathplace.setArray(R.array.deathplace);
        deliverconcern.setArray(R.array.deathconcern);
        source.setArray(R.array.source);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        deathdate.updateDate(year, month, day);


        Cursor cursor = getContentResolver().query(Person.CONTENT_URI, new String[]{Person.SEX}, "pid = ?", new String[]{share_pid}, Person.DEFAULT_SORTING);
        if (cursor.moveToFirst())
            sex = cursor.getString(0);

        if (sex.equals("1")) {
            deliverconcern.setVisibility(View.GONE);
            isfemale.setVisibility(View.GONE);
        }

        setContentQuery(PersonDeath.CONTENT_URI, new String[]{
                        PersonDeath.DEATHDATE, PersonDeath.DEATHPLACE,
                        PersonDeath.DEATHA, PersonDeath.DEATHB, PersonDeath.DEATHC,
                        PersonDeath.DEATHD, PersonDeath.DEATHDISEASE,
                        PersonDeath.DEATHCAUSE, PersonDeath.DELIVERYCONCERN,
                        PersonDeath.SOURCE
                }, "pid =? AND pcucodeperson =?", new String[]{share_pid, share_pcucode},
                PersonDeath.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }


    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        if (array[0] != null)
            updatePicker(deathdate, R.id.answer1, array[0]);
        if (array[1] != null)
            deathplace.setSelection(checkStrangerForStringContent(array[1], "1"));
        if (array[2] != null)
            deatha.setSelectionById(checkStrangerForStringContent(array[2], null));
        if (array[3] != null)
            deathb.setSelectionById(checkStrangerForStringContent(array[3], null));
        if (array[4] != null)
            deathc.setSelectionById(checkStrangerForStringContent(array[4], null));
        if (array[5] != null)
            deathd.setSelectionById(checkStrangerForStringContent(array[5], null));
        if (array[6] != null)
            deathdisease.setSelectionById(checkStrangerForStringContent(array[6], null));
        if (array[7] != null)
            deathcause.setSelectionById(checkStrangerForStringContent(array[7], null));
        if (array[8] != null)
            deliverconcern.setSelection(checkStrangerForStringContent(array[8], "1"));
        if (array[9] != null)
            source.setSelection(checkStrangerForStringContent(array[9], "1"));
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
        cv.put("pcucodeperson", share_pcucode);
        cv.put("pid", share_pid);
        retrieveDataFromThaiDatePicker(deathdate, cv, PersonDeath.DEATHDATE, true);
        cv.put(PersonDeath.DEATHPLACE, deathplace.getSelectionId());
        if (deatha.getSelectId() != null)
            cv.put(PersonDeath.DEATHA, deatha.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHA);

        if (deathb.getSelectId() != null)
            cv.put(PersonDeath.DEATHB, deathb.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHB);

        if (deathc.getSelectId() != null)
            cv.put(PersonDeath.DEATHC, deathc.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHC);

        if (deathd.getSelectId() != null)
            cv.put(PersonDeath.DEATHD, deathd.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHD);

        if (deathdisease.getSelectId() != null)
            cv.put(PersonDeath.DEATHDISEASE, deathdisease.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHDISEASE);

        if (deathcause.getSelectId() != null)
            cv.put(PersonDeath.DEATHCAUSE, deathcause.getSelectId());
        else
            cv.putNull(PersonDeath.DEATHCAUSE);

        if (sex.equals("2"))
            cv.put(PersonDeath.DELIVERYCONCERN, deliverconcern.getSelectionId());
        else
            cv.putNull(PersonDeath.DELIVERYCONCERN);

        cv.put(PersonDeath.SOURCE, source.getSelectionId());

        updateTimeStamp(cv);
        canCommit = (deatha.getSelectId() != null && deathdate.getDate().toString() != null ? true : false);
        doCommit(cv, PersonDeath.CONTENT_URI, canCommit);
    }

    protected void doCommit(ContentValues cv, Uri uri, Boolean canCommit) {
        if (cv != null && canCommit) {
            System.out.println("Begin Update . . .");
            ContentResolver cr = getContentResolver();
            int rows = cr.update(uri, cv, "pid=?",
                    new String[]{share_pid});
            System.out.println("Update " + rows + " rows");
            if (rows == 1) {
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed swap to Insert . . .");
                cr.insert(uri, cv);
                System.out.println("Insert Success");
            }
            this.finish();
            Toast.makeText(this, R.string.toast_comit, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.toast_abort) + ERROR_MSG,
                    Toast.LENGTH_SHORT).show();
            ERROR_MSG = "";
        }
    }

}
