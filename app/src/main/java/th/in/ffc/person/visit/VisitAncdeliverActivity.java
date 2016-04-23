package th.in.ffc.person.visit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.code.HospitalListDialog;
import th.in.ffc.persist.otherListPersist;
import th.in.ffc.provider.PersonProvider.VisitAncDeliver;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.util.ThaiTimePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

import java.util.ArrayList;
import java.util.Calendar;

public class VisitAncdeliverActivity extends FFCEditActivity {

    EditText pregno;
    SearchableButton hosservice;
    ThaiDatePicker datedeliver;
    ThaiTimePicker delivertime;
    ArrayFormatSpinner deliverresult;
    ArrayFormatSpinner operater;
    ArrayFormatSpinner delivertype;
    EditText numdeadinpreg;
    ArrayFormatSpinner deliverplace;
    Spinner deliverend;
    EditText signabnormalanc;
    EditText signabnormaldeliver;
    CheckBox checkappoint;
    ThaiDatePicker dateappoint;

    ArrayAdapter<CharSequence> deliverresultAdapter;
    ArrayAdapter<CharSequence> operaterAdapter;
    ArrayAdapter<CharSequence> delivertypeAdapter;
    ArrayAdapter<CharSequence> deliverplaceAdapter;
    ArrayAdapter<CharSequence> deliverendAdapter;
    ArrayList<otherListPersist> deliverArray = new ArrayList<otherListPersist>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = data.getLastPathSegment();
        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_ancdeliver_activity);
        preparePicker();
        prepareSpinner();
        setContentDisplay();
    }

    private void preparePicker() {
        datedeliver = (ThaiDatePicker) findViewById(R.id.answer3);
        delivertime = (ThaiTimePicker) findViewById(R.id.answer4);
        checkappoint = (CheckBox) findViewById(R.id.checkappointcare);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer13);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        datedeliver.updateDate(year, month, day);
        delivertime.updateTime(hour, min);
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

    @SuppressWarnings("deprecation")
    private void prepareSpinner() {
        hosservice = (SearchableButton) findViewById(R.id.answer2);
        hosservice.setDialog(getSupportFragmentManager(), HospitalListDialog.class, "hosservice");
//		generateFromResourceNonZeroBased(deliverresult, deliverresultAdapter, R.id.answer5, R.array.deliverresult);
//		generateFromResourceNonZeroBased(operater,operaterAdapter, R.id.answer6, R.array.operater);
//		generateFromResourceNonZeroBased(delivertype, delivertypeAdapter, R.id.answer7,R.array.delivertype);
//		generateFromResourceNonZeroBased(deliverplace, deliverplaceAdapter, R.id.answer9, R.array.deliverplace);
        generateFromDatabase(deliverend, deliverendAdapter, deliverArray, R.id.answer10, "SELECT diseasecode,diseasenamethai FROM cdisease WHERE diseasecode Like 'O%'");

    }

    private void setContentDisplay() {
        pregno = (EditText) findViewById(R.id.answer1);
        hosservice = (SearchableButton) findViewById(R.id.answer2);
        datedeliver = (ThaiDatePicker) findViewById(R.id.answer3);
        delivertime = (ThaiTimePicker) findViewById(R.id.answer4);
        deliverresult = (ArrayFormatSpinner) findViewById(R.id.answer5);
        operater = (ArrayFormatSpinner) findViewById(R.id.answer6);
        delivertype = (ArrayFormatSpinner) findViewById(R.id.answer7);
        numdeadinpreg = (EditText) findViewById(R.id.answer8);
        deliverplace = (ArrayFormatSpinner) findViewById(R.id.answer9);
        deliverend = (Spinner) findViewById(R.id.answer10);
        signabnormaldeliver = (EditText) findViewById(R.id.answer11);
        signabnormalanc = (EditText) findViewById(R.id.answer12);
        dateappoint = (ThaiDatePicker) findViewById(R.id.answer13);
        checkappoint = (CheckBox) findViewById(R.id.checkappointcare);

        deliverresult.setArray(R.array.deliverresult);
        operater.setArray(R.array.operater);
        delivertype.setArray(R.array.delivertype);
        deliverplace.setArray(R.array.deliverplace);

        setContentQuery(VisitAncDeliver.CONTENT_URI, new String[]{VisitAncDeliver.PREGNO, VisitAncDeliver.HOSSERVICE, VisitAncDeliver.DATEDELIVER,
                VisitAncDeliver.DELIVERTIME, VisitAncDeliver.DELIVERRESULT, VisitAncDeliver.OPERATER, VisitAncDeliver.DELIVERTYPE, VisitAncDeliver.NUMDATEINPREG,
                VisitAncDeliver.DELIVERPLACE, VisitAncDeliver.DELIVEREND, VisitAncDeliver.SIGNABNORMALANC, VisitAncDeliver.SIGNABNORMALDELIVER, VisitAncDeliver.DATEAPPOINTCARE}, "visitno = ?", new String[]{share_visitno}, VisitAncDeliver.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    @SuppressWarnings("deprecation")
    private void setContentForUpdate() {
        // TODO Auto-generated method stu5b
        //codeScreen
        for (String a : array) {
            System.out.println("ARRAY :" + a);
        }

        pregno.setText(checkEditText(array[0]));
        if (array[1] != null)
            hosservice.setSelectionById(array[1]);
        if (array[2] != null)
            updatePicker(datedeliver, R.id.answer3, array[2]);
        if (array[3] != null)
            updateTime(delivertime, R.id.answer4, array[3]);
        //Invalid
        if (array[4] != null)
            deliverresult.setSelection(checkStrangerForStringContent(array[4], "�"));
        if (array[5] != null)
            operater.setSelection(checkStrangerForStringContent(array[5], "1"));
        if (array[6] != null)
            delivertype.setSelection(checkStrangerForStringContent(array[6], "1"));
        numdeadinpreg.setText(checkEditText(array[7]));
        if (array[8] != null)
            deliverplace.setSelection(checkStrangerForStringContent(array[8], "1"));
        deliverend.setSelection(checkSpinnerQuery(array[9], deliverArray));
        signabnormalanc.setText(checkEditText(array[10]));
        signabnormaldeliver.setText(checkEditText(array[11]));
        if (array[12] != null) {
            checkappoint.setChecked(true);
            updatePicker(dateappoint, R.id.answer13, array[12]);
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
        cv.put("pcucodeperson", getPcuCode());
        cv.put("pid", share_pid);

        retrieveDataFromEditText(pregno, cv, VisitAncDeliver.PREGNO);
        if (hosservice.getSelectId() != null)
            cv.put(VisitAncDeliver.HOSSERVICE, hosservice.getSelectId());
        else
            cv.put(VisitAncDeliver.HOSSERVICE, "00000");
        retrieveDataFromThaiDatePicker(datedeliver, cv, VisitAncDeliver.DATEDELIVER, true);
        cv.put(VisitAncDeliver.DELIVERTIME, delivertime.toString());
//		retrieveDataFromSpinnerWithResourceNonZeroBased(deliverresult, cv, VisitAncDeliver.DELIVERRESULT, R.array.deliverresult);
//		retrieveDataFromSpinnerWithResourceNonZeroBased(operater, cv, VisitAncDeliver.OPERATER, R.array.operater);
//		retrieveDataFromSpinnerWithResourceNonZeroBased(delivertype,cv, VisitAncDeliver.DELIVERTYPE, R.array.delivertype);
        cv.put(VisitAncDeliver.DELIVERRESULT, deliverresult.getSelectionId());
        cv.put(VisitAncDeliver.OPERATER, operater.getSelectionId());
        cv.put(VisitAncDeliver.DELIVERTYPE, delivertype.getSelectionId());
        cv.put(VisitAncDeliver.DELIVERPLACE, deliverplace.getSelectionId());
        retrieveDataFromEditText(numdeadinpreg, cv, VisitAncDeliver.NUMDATEINPREG);
//		retrieveDataFromSpinnerWithResourceNonZeroBased(deliverplace, cv, VisitAncDeliver.DELIVERPLACE, R.array.deliverplace);
        retrieveDataFromSpinnerWithQuery(deliverend, cv, VisitAncDeliver.DELIVEREND, deliverArray);
        retrieveDataFromEditText(signabnormalanc, cv, VisitAncDeliver.SIGNABNORMALANC);
        retrieveDataFromEditText(signabnormaldeliver, cv, VisitAncDeliver.SIGNABNORMALDELIVER);
        retrieveDataFromThaiDatePicker(dateappoint, cv, VisitAncDeliver.DATEAPPOINTCARE, checkappoint.isChecked());
        updateTimeStamp(cv);
        //CheckRequireData
        //Debug

        if (pregno.getText().toString().isEmpty())
            ERROR_MSG += "\n - " + getString(R.string.err_no_pregno);
        else
            canCommit = true;

        if (checkappoint.isChecked())
            canCommit = checkDate(dateappoint);

        doCheckBeForeCommit(cv, canCommit);
    }

    private void doCheckBeForeCommit(ContentValues cv, Boolean commit) {
        // Update
        if (cv != null && commit) {
            System.out.println("Begin Update . . .");
            ContentResolver cr = getContentResolver();
            int rows = cr.update(VisitAncDeliver.CONTENT_URI, cv,
                    "visitno=?", new String[]{share_visitno});
            System.out.println("Update " + rows + " rows");
            if (rows == 1) {
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed swap to Insert . . .");
                cr.insert(VisitAncDeliver.CONTENT_URI, cv);
                System.out.println("Insert Success");
            }
            this.finish();
            Toast.makeText(this, R.string.toast_comit, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.toast_abort) + ERROR_MSG, Toast.LENGTH_SHORT)
                    .show();
            ERROR_MSG = "";
        }
    }

}
