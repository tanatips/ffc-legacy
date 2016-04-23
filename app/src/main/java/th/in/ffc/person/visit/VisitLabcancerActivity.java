package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.HospitalListDialog;
import th.in.ffc.provider.PersonProvider.VisitLabcancer;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

public class VisitLabcancerActivity extends FFCEditActivity {

    ArrayFormatSpinner typecancer;
    ArrayFormatSpinner result;
    SearchableButton hosname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_labcancer_activity);
        prepareSpinner();
        setContentDisplay();
    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        typecancer = (ArrayFormatSpinner) findViewById(R.id.answer1);
        result = (ArrayFormatSpinner) findViewById(R.id.answer2);
        hosname = (SearchableButton) findViewById(R.id.answer3);

        typecancer.setArray(R.array.typecancer);
        result.setArray(R.array.cancer_result);

        setContentQuery(VisitLabcancer.CONTENT_URI, new String[]{
                        VisitLabcancer.DATECHECK, VisitLabcancer.TYPECANCER,
                        VisitLabcancer.RESULT, VisitLabcancer.HOSLAB}, "visitno = ?",
                new String[]{share_visitno}, VisitLabcancer.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        for (String a : array) {
            System.out.println("ARRAY : " + a);
        }
        typecancer.setSelection(checkStrangerForStringContent(array[1], "1"));
        result.setSelection(checkStrangerForStringContent(array[2], "1"));

        if (array[3] != null)
            hosname.setSelectionById(array[3]);
    }

    private void prepareSpinner() {
        // TODO Auto-generated method stub
        hosname = (SearchableButton) findViewById(R.id.answer3);
        hosname.setDialog(getSupportFragmentManager(), HospitalListDialog.class, null, "hoslab");
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
        cv.put(VisitLabcancer.PCUCODEPERSON, getPcuCode());
        cv.put(VisitLabcancer.PCUCODE, getPcuCode());
        cv.put(VisitLabcancer.PID, share_pid);
        cv.put(VisitLabcancer.VISITNO, share_visitno);
        cv.put(VisitLabcancer.HOSSERVICE, getPcuCode());
        setCurrentDate(cv, VisitLabcancer.DATECHECK);
        updateTimeStamp(cv);

        cv.put(VisitLabcancer.TYPECANCER, typecancer.getSelectionId());
        cv.put(VisitLabcancer.RESULT, result.getSelectionId());
        retrieveDataFromSearchableButton(hosname, cv, VisitLabcancer.HOSLAB);

        doCommit(cv, VisitLabcancer.CONTENT_URI, true);

    }

}
