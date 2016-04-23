package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.DrugVaccineListDialog;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitDiagAppoint;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

import java.util.Calendar;

public class VisitDiagappointActivity extends FFCEditActivity {

    SearchableButton diagcode;
    ThaiDatePicker appodate;
    ArrayFormatSpinner appotype;
    EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_diagappoint_activity);
        prepareDatePicker();
        setContentDisplay();
    }

    private void prepareDatePicker() {
        // TODO Auto-generated method stub
        appodate = (ThaiDatePicker) findViewById(R.id.answer2);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        appodate.updateDate(year, month, day);
    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        diagcode = (SearchableButton) findViewById(R.id.answer1);
        appodate = (ThaiDatePicker) findViewById(R.id.answer2);
        appotype = (ArrayFormatSpinner) findViewById(R.id.answer3);
        comment = (EditText) findViewById(R.id.answer4);
        diagcode.setDialog(getSupportFragmentManager(), DrugVaccineListDialog.class, "Vaccine");
        appotype.setArray(R.array.appotype);

        setContentQuery(VisitDiagAppoint.CONTENT_URI, new String[]{
                        VisitDiagAppoint.DIAGCODE, VisitDiagAppoint.APPODATE, VisitDiagAppoint.APPOTYPE, VisitDiagAppoint.COMMENT
                }, "visitno =? AND pcucode =?", new String[]{share_visitno, getPcuCode()},
                VisitDiagAppoint.DEFAULT_SORTING);

        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        if (array[0] != null)
            diagcode.setSelectionById(checkStrangerForStringContent(array[0], "510001"));
        if (array[1] != null) ;
        updatePicker(appodate, R.id.answer2, array[1]);
        if (array[2] != null)
            appotype.setSelection(checkStrangerForStringContent(array[2], "1"));
        comment.setText(checkEditText(array[3]));
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
        cv.put(VisitDiagAppoint.DIAGCODE, diagcode.getSelectId());
        retrieveDataFromThaiDatePicker(appodate, cv, VisitDiagAppoint.APPODATE, true);
        cv.put(VisitDiagAppoint.APPOTYPE, appotype.getSelectionId());
        retrieveDataFromEditText(comment, cv, VisitDiagAppoint.COMMENT);

        doCommit(cv, VisitDiagAppoint.CONTENT_URI, true);
    }

}
