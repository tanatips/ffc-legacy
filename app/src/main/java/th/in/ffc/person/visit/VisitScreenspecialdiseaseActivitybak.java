package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.persist.otherListPersist;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitScreenspecialdisease;
import th.in.ffc.widget.ArrayFormatSpinner;

import java.util.ArrayList;

public class VisitScreenspecialdiseaseActivitybak extends FFCEditActivity {

    Spinner codeScreen;
    ArrayFormatSpinner codeResult;
    EditText remart;
    ArrayAdapter<CharSequence> codeResultAdapter;
    ArrayAdapter<CharSequence> codeScreenAdapter;
    ArrayList<otherListPersist> codeScreenArray = new ArrayList<otherListPersist>();
    RadioGroup Q1;
    RadioGroup Q2;
    RadioGroup Q3;
    RadioGroup Q4;
    RadioGroup Q5;
    RadioGroup Q6;
    RadioGroup Q7;
    RadioGroup Q8;
    RadioGroup Q9;
    RadioGroup Q10;
    RadioGroup Q11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_screenspecialdisease_activity);
        prepareSpinner();
        setContentDisplay();
    }

    @SuppressWarnings("deprecation")
    private void prepareSpinner() {
        // TODO Auto-generated method stub
        generateFromDatabase(codeScreen, codeScreenAdapter, codeScreenArray, R.id.codescreen, "SELECT * FROM cscreenotherdisease");
    }

    private void setContentDisplay() {
        codeScreen = (Spinner) findViewById(R.id.codescreen);
        codeResult = (ArrayFormatSpinner) findViewById(R.id.coderesult);
        codeResult.setArray(R.array.coderesult);
        remart = (EditText) findViewById(R.id.remart);
        Q1 = (RadioGroup) findViewById(R.id.answer1);
        Q2 = (RadioGroup) findViewById(R.id.answer2);
        Q3 = (RadioGroup) findViewById(R.id.answer3);
        Q4 = (RadioGroup) findViewById(R.id.answer4);
        Q5 = (RadioGroup) findViewById(R.id.answer5);
        Q6 = (RadioGroup) findViewById(R.id.answer6);
        Q7 = (RadioGroup) findViewById(R.id.answer7);
        Q8 = (RadioGroup) findViewById(R.id.answer8);
        Q9 = (RadioGroup) findViewById(R.id.answer9);
        Q10 = (RadioGroup) findViewById(R.id.answer10);
        Q11 = (RadioGroup) findViewById(R.id.answer11);

        setContentQuery(VisitScreenspecialdisease.CONTENT_URI, new String[]{
                        VisitScreenspecialdisease.CODESCREEN,
                        VisitScreenspecialdisease.CODERESULT, VisitScreenspecialdisease.REMART,
                        VisitScreenspecialdisease.DEPRESSED,
                        VisitScreenspecialdisease.FEDUP, VisitScreenspecialdisease.Q91,
                        VisitScreenspecialdisease.Q92, VisitScreenspecialdisease.Q93,
                        VisitScreenspecialdisease.Q94, VisitScreenspecialdisease.Q95,
                        VisitScreenspecialdisease.Q96, VisitScreenspecialdisease.Q97,
                        VisitScreenspecialdisease.Q98, VisitScreenspecialdisease.Q99},
                "visitno =? AND pcucode = ?", new String[]{share_visitno, getPcuCode()},
                VisitScreenspecialdisease.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    @SuppressWarnings("deprecation")
    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        codeScreen.setSelection(checkSpinnerQuery(array[0], codeScreenArray));
        codeResult.setSelection(checkStrangerForStringContent(array[1], "1"));
        remart.setText(checkEditText(array[2]));
        setCheckRadio(array[3], Q1);
        setCheckRadio(array[4], Q2);
        setCheckRadio(array[5], Q3);
        setCheckRadio(array[6], Q4);
        setCheckRadio(array[7], Q5);
        setCheckRadio(array[8], Q6);
        setCheckRadio(array[9], Q7);
        setCheckRadio(array[10], Q8);
        setCheckRadio(array[11], Q9);
        setCheckRadio(array[12], Q10);
        setCheckRadio(array[13], Q11);
    }

    private void setCheckRadio(String data, RadioGroup Rg) {
        if (TextUtils.isEmpty(data) || data.equals("-")) {
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

    @SuppressWarnings("deprecation")
    @Override
    protected void Update() {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);

        // retrive CodeScreen
        retrieveDataFromSpinnerWithQuery(codeScreen, cv, VisitScreenspecialdisease.CODESCREEN, codeScreenArray);
        cv.put(VisitScreenspecialdisease.CODERESULT, codeResult.getSelectionId());
        retrieveDataFromEditText(remart, cv, VisitScreenspecialdisease.REMART);
        retrieveDataFromRadioGroup(Q1, cv, VisitScreenspecialdisease.DEPRESSED);
        retrieveDataFromRadioGroup(Q2, cv, VisitScreenspecialdisease.FEDUP);
        retrieveDataFromRadioGroup(Q3, cv, VisitScreenspecialdisease.Q91);
        retrieveDataFromRadioGroup(Q4, cv, VisitScreenspecialdisease.Q92);
        retrieveDataFromRadioGroup(Q5, cv, VisitScreenspecialdisease.Q93);
        retrieveDataFromRadioGroup(Q6, cv, VisitScreenspecialdisease.Q94);
        retrieveDataFromRadioGroup(Q7, cv, VisitScreenspecialdisease.Q95);
        retrieveDataFromRadioGroup(Q8, cv, VisitScreenspecialdisease.Q96);
        retrieveDataFromRadioGroup(Q9, cv, VisitScreenspecialdisease.Q97);
        retrieveDataFromRadioGroup(Q10, cv, VisitScreenspecialdisease.Q98);
        retrieveDataFromRadioGroup(Q11, cv, VisitScreenspecialdisease.Q99);
        doCommit(cv, VisitScreenspecialdisease.CONTENT_URI, true);

    }
}
