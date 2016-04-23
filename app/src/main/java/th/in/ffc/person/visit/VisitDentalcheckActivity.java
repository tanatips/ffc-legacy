package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitDentalCheck;
import th.in.ffc.widget.ArrayFormatSpinner;

public class VisitDentalcheckActivity extends FFCEditActivity {

    EditText toothmilk;
    EditText toothmilkcorrupt;
    EditText toothpermanent;
    EditText toothpermanentcorrupt;
    RadioGroup tartar;
    ArrayFormatSpinner gumstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_dentalcheck_activity);
        setContentDisplay();
    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        toothmilk = (EditText) findViewById(R.id.answer1);
        toothmilkcorrupt = (EditText) findViewById(R.id.answer2);
        toothpermanent = (EditText) findViewById(R.id.answer3);
        toothpermanentcorrupt = (EditText) findViewById(R.id.answer4);
        tartar = (RadioGroup) findViewById(R.id.answer5);
        gumstatus = (ArrayFormatSpinner) findViewById(R.id.answer6);
        gumstatus.setArray(R.array.gumstatus);

        setContentQuery(VisitDentalCheck.CONTENT_URI, new String[]{
                        VisitDentalCheck.TOOTHMILK, VisitDentalCheck.TOOTHMILKCORRUPT,
                        VisitDentalCheck.TOOTHPERMANENT,
                        VisitDentalCheck.TOOTHPERMANENTCORRUPT,
                        VisitDentalCheck.TARTAR, VisitDentalCheck.GUMSTATUS
                }, "visitno =? AND pcucode =?", new String[]{share_visitno, getPcuCode()},
                VisitDentalCheck.DEFAULT_SORTING);

        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        toothmilk.setText(checkEditText(array[0]));
        toothmilkcorrupt.setText(checkEditText(array[1]));
        toothpermanent.setText(checkEditText(array[2]));
        toothpermanentcorrupt.setText(checkEditText(array[3]));
        if (array[4] != null)
            tartar.check(array[4].equals("1") ? R.id.rad2 : R.id.rad1);
        if (array[5] != null)
            gumstatus.setSelection(checkStrangerForStringContent(array[5], "1"));
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
        cv.put("visitno", share_visitno);

        retrieveDataFromEditText(toothmilk, cv, VisitDentalCheck.TOOTHMILK);
        retrieveDataFromEditText(toothmilkcorrupt, cv, VisitDentalCheck.TOOTHMILKCORRUPT);
        retrieveDataFromEditText(toothpermanent, cv, VisitDentalCheck.TOOTHPERMANENT);
        retrieveDataFromEditText(toothpermanentcorrupt, cv, VisitDentalCheck.TOOTHPERMANENTCORRUPT);
        cv.put(VisitDentalCheck.TARTAR, tartar.getCheckedRadioButtonId() == R.id.rad1 ? "0" : "1");
        cv.put(VisitDentalCheck.GUMSTATUS, gumstatus.getSelectionId());

        doCommit(cv, VisitDentalCheck.CONTENT_URI, true);
    }

}
