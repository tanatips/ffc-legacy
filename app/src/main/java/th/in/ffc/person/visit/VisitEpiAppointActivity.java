package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.DrugVaccineListDialog;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitEpiAppoint;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.SearchableButton;

public class VisitEpiAppointActivity extends FFCEditActivity {

    SearchableButton vaccinecode;
    ThaiDatePicker appodate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        share_pid = getIntent().getExtras().getString(Person.PID);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_epiappoint_activity);
        prepareDatePicker();
        setContentDisplay();
    }

    private void prepareDatePicker() {
        // TODO Auto-generated method stub
        appodate = (ThaiDatePicker) findViewById(R.id.answer2);
        appodate.updateDate(Date.newInstance(DateTime.getCurrentDate()));
    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        vaccinecode = (SearchableButton) findViewById(R.id.answer1);
        appodate = (ThaiDatePicker) findViewById(R.id.answer2);
        vaccinecode.setDialog(getSupportFragmentManager(),
                DrugVaccineListDialog.class, "Vaccine");

        setContentQuery(VisitEpiAppoint.CONTENT_URI, new String[]{
                        VisitEpiAppoint.VACCINECODE, VisitEpiAppoint.APPODATE},
                "visitno =? AND pcucode =?", new String[]{share_visitno,
                        getPcuCode()}, VisitEpiAppoint.DEFAULT_SORTING);

        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        if (array[0] != null)
            vaccinecode.setSelectionById(checkStrangerForStringContent(array[0], "510001"));
        if (array[1] != null)
            updatePicker(appodate, R.id.answer2, array[1]);

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

        cv.put(VisitEpiAppoint.VACCINECODE, vaccinecode.getSelectId());
        retrieveDataFromThaiDatePicker(appodate, cv, VisitEpiAppoint.APPODATE, true);
        canCommit = (!TextUtils.isEmpty(vaccinecode.getSelectId()) ? true : false);
        doCommit(cv, VisitEpiAppoint.CONTENT_URI, canCommit);
    }

}
