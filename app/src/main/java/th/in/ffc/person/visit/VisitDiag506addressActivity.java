package th.in.ffc.person.visit;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.app.FFCSearchListDialog;
import th.in.ffc.code.Diag506ListDialog;
import th.in.ffc.code.DistrictListDialog;
import th.in.ffc.code.ProvinceListDialog;
import th.in.ffc.code.SubDistrictListDialog;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.VisitDiag506address;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableButton;

import java.util.Calendar;

public class VisitDiag506addressActivity extends FFCEditActivity {

    SearchableButton diagcode;
    ThaiDatePicker sickdatestart;
    ThaiDatePicker sickdatefind;
    ArrayFormatSpinner status;
    ThaiDatePicker deaddate;
    ArrayAdapter<CharSequence> statusAdapter;
    TextView deadlabel;
    //House
    EditText hno;
    EditText mu;
    EditText road;
    SearchableButton subdistcode;
    SearchableButton distcode;
    SearchableButton provcode;

    String EXTRA_DIAGCODE;
    String EXTRA_HNO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		Uri data = getIntent().getData();
//		share_visitno = data.getLastPathSegment();
        share_visitno = getIntent().getStringExtra(VisitDiag506address.VISITNO);
        System.out.println(share_visitno);
        System.out.println(getPcuCode());
        EXTRA_DIAGCODE = getIntent().getStringExtra(VisitDiag506address.DIAGCODE);
        setContentView(R.layout.visit_diag506address_activity);
        prepareSpinner();
        prepareDatePicker();
        setContentDisplay();
    }

    private void prepareSpinner() {
        // TODO Auto-generated method stub
        diagcode = (SearchableButton) findViewById(R.id.answer1);

        diagcode.setDialog(getSupportFragmentManager(),
                Diag506ListDialog.class, null, "diag506");
    }

    private void prepareDatePicker() {
        // TODO Auto-generated method stub
        sickdatestart = (ThaiDatePicker) findViewById(R.id.answer2);
        sickdatefind = (ThaiDatePicker) findViewById(R.id.answer3);
        deaddate = (ThaiDatePicker) findViewById(R.id.answer5);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        sickdatestart.updateDate(year, month, day);
        sickdatefind.updateDate(year, month, day);
        deaddate.updateDate(year, month, day);
    }


    private void setContentDisplay() {
        // TODO Auto-generated method stub
        diagcode = (SearchableButton) findViewById(R.id.answer1);
        diagcode.setEnabled(false);
        sickdatestart = (ThaiDatePicker) findViewById(R.id.answer2);
        sickdatefind = (ThaiDatePicker) findViewById(R.id.answer3);
        status = (ArrayFormatSpinner) findViewById(R.id.answer4);
        deadlabel = (TextView) findViewById(R.id.deadlabel);
        deaddate = (ThaiDatePicker) findViewById(R.id.answer5);
        //House
        hno = (EditText) findViewById(R.id.hno);
        mu = (EditText) findViewById(R.id.mu);
        road = (EditText) findViewById(R.id.road);
        subdistcode = (SearchableButton) findViewById(R.id.subdistcode);
        distcode = (SearchableButton) findViewById(R.id.distcode);
        provcode = (SearchableButton) findViewById(R.id.provcode);

        provcode.setDialog(getSupportFragmentManager(), ProvinceListDialog.class, "prov");
        distcode.setDialog(getSupportFragmentManager(), DistrictListDialog.class, "dist");
        subdistcode.setDialog(getSupportFragmentManager(), SubDistrictListDialog.class, "subdist");

        provcode.addTextChangedListener(watchprov);
        distcode.addTextChangedListener(watchdist);
//		subdistcode.setOnClickListener(locationHandler);


        status.setArray(R.array.status_sick);
        status.setOnItemSelectedListener(statushandler);

        getHouseDetail();
        System.out.println("Querying visitno:" + share_visitno + " pcucode:" + getPcuCode() + " where diagcode:" + EXTRA_DIAGCODE);
        setContentQuery(VisitDiag506address.CONTENT_URI, new String[]{
                        VisitDiag506address.DIAGCODE,
                        VisitDiag506address.SICKDATESTART,
                        VisitDiag506address.SICKDATEFIND, VisitDiag506address.STATUS,
                        VisitDiag506address.DEADDATE}, "visitno = ? and pcucode =? and diagcode=?",
                new String[]{share_visitno, getPcuCode(), EXTRA_DIAGCODE},
                VisitDiag506address.DEFAULT_SORTING);

        diagcode.setSelectionById(getIntent().getStringExtra(VisitDiag506address.DIAGCODE));
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // DEBUG
        if (array[0] != null)
            diagcode = (SearchableButton) findViewById(R.id.answer1);
        diagcode.setSelectionById(array[0]);
        updatePicker(sickdatestart, R.id.answer2, array[1]);
        updatePicker(sickdatefind, R.id.answer3, array[2]);
        if (array[3] != null)
            status.setSelection(checkStrangerForStringContent(array[3], "1"));
        status.setSelection(checkSpinnerResourceNonZeroBased(array[3],
                R.array.status_sick));
        if (array[3] == "2")
            ;
        updatePicker(deaddate, R.id.answer5, array[4]);

    }


    OnItemSelectedListener statushandler = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO Auto-generated method stub
            int choice = (arg0.getSelectedItemPosition() == 1) ? View.VISIBLE
                    : View.GONE;
            deadlabel.setVisibility(choice);
            deaddate.setVisibility(choice);

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

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
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);
        if (diagcode.getSelectId() != null)
            cv.put(VisitDiag506address.DIAGCODE, diagcode.getSelectId());
        retrieveDataFromThaiDatePicker(sickdatestart, cv,
                VisitDiag506address.SICKDATESTART, true);
        retrieveDataFromThaiDatePicker(sickdatefind, cv,
                VisitDiag506address.SICKDATEFIND, true);

        cv.put(VisitDiag506address.STATUS, status.getSelectionId());
        retrieveDataFromThaiDatePicker(deaddate, cv,
                VisitDiag506address.DEADDATE, deaddate.isShown());

        // check inreality date

        canCommit = diagcode.getSelectId() != null ? true : false;

        if (TextUtils.isEmpty(diagcode.getSelectId()))
            ERROR_MSG += "\n - " + getString(R.string.err_no_diagcode);
        doGetHouseData(cv);
        doCommit(cv, VisitDiag506address.CONTENT_URI, canCommit);

    }


    @Override
    protected void doCommit(ContentValues cv, Uri uri, Boolean canCommit) {
        // TODO Auto-generated method stub
        if (cv != null && canCommit) {
            System.out.println("Begin Update . . .");
            ContentResolver cr = getContentResolver();
            int rows = cr.update(uri, cv, "visitno=? AND diagcode=?",
                    new String[]{share_visitno, EXTRA_DIAGCODE});
            System.out.println("Update " + rows + " rows");
            if (rows == 1) {
                System.out.println("Update Success");
            } else {
                System.out.println("Update Failed swap to Insert . . .");
                cr.insert(uri, cv);
                System.out.println("Insert Success");
            }
            this.finish();
        } else {
            Toast.makeText(this, getString(R.string.toast_abort) + ERROR_MSG,
                    Toast.LENGTH_SHORT).show();
            ERROR_MSG = "";
        }
    }

    private void getHouseDetail() {
        // TODO Auto-generated method stub

        Cursor c = getContentResolver().query(
                Person.CONTENT_URI,
                new String[]{Person.ADDR_NO, Person.ADDR_MU,
                        Person.ADDR_ROAD, Person.ADDR_SUBDIST,
                        Person.ADDR_DIST, Person.ADDR_PROVICE}, "pid = ?",
                new String[]{share_pid}, Person.DEFAULT_SORTING);

        if (c.moveToFirst()) {

            EXTRA_HNO = c.getString(0);
            hno.setText(c.getString(0));
            mu.setText(c.getString(1));
            road.setText(c.getString(2));
            System.out.println("Prov:" + c.getString(5) + " Dist:" + c.getString(4) + " S.Dist:" + c.getString(3));
            provcode.setSelectionById(checkStrangerForStringContent(c.getString(5), "10"));
            distcode.setSelectionById(checkStrangerForStringContent(c.getString(4), "01"), "distcode =? AND provcode ='" + c.getString(5) + "'");
            subdistcode.setSelectionById(checkStrangerForStringContent(c.getString(3), "01"), "subdistcode=? AND distcode='" + c.getString(4) + "' AND provcode='" + c.getShort(5) + "'");
        }
    }

    private void doGetHouseData(ContentValues cv) {
        retrieveDataFromEditText(hno, cv, VisitDiag506address.HNO);
        retrieveDataFromEditText(mu, cv, VisitDiag506address.MU);
        retrieveDataFromEditText(road, cv, VisitDiag506address.ROAD);
        if (subdistcode.getSelectId() != null)
            cv.put(VisitDiag506address.SUBDISTCODE, subdistcode.getSelectId());
        if (distcode.getSelectId() != null)
            cv.put(VisitDiag506address.DISTCODE, distcode.getSelectId());
        if (provcode.getSelectId() != null)
            cv.put(VisitDiag506address.PROVCODE, provcode.getSelectId());
    }

    TextWatcher watchprov = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            Bundle bb = new Bundle();
            bb.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "provcode = '" + provcode.getSelectId() + "'");
            distcode.setDialog(getSupportFragmentManager(), DistrictListDialog.class, bb, "dist");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    TextWatcher watchdist = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            Bundle bb = new Bundle();
            bb.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "provcode = '" + provcode.getSelectId() + "' AND " + "distcode = '" + distcode.getSelectId() + "'");
            subdistcode.setDialog(getSupportFragmentManager(), SubDistrictListDialog.class, bb, "subdist");
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };
}
