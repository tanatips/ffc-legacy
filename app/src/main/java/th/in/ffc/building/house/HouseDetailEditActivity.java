package th.in.ffc.building.house;

import android.content.ContentValues;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.widget.*;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.app.FFCSearchListDialog;
import th.in.ffc.code.PersonHouseListDialog;
import th.in.ffc.code.PersonProtagonistListDialog;
import th.in.ffc.persist.PersonPersist;
import th.in.ffc.persist.otherListPersist;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.util.ThaiDatePicker;
import th.in.ffc.util.ThaiTimePicker;
import th.in.ffc.widget.ArrayFormatSpinner;
import th.in.ffc.widget.SearchableSpinner;

import java.util.ArrayList;

public class HouseDetailEditActivity extends FFCEditActivity {

    // Object
    Spinner villcode;
    EditText hid;
    EditText hno;
    SearchableSpinner pid;
    SearchableSpinner pidvola;
    TextView housesurveydate;
    ArrayFormatSpinner houseChar;
    ArrayFormatSpinner houseCharground;
    ArrayFormatSpinner housearea;
    EditText communityno;
    EditText houseroad;
    TextView dateregister;
    EditText telephonehouse;
    Spinner usernamedoc;
    SearchableSpinner headhealthhouse;

    ArrayList<otherListPersist> villcodeArray = new ArrayList<otherListPersist>();
    ArrayList<PersonPersist> pidArray = new ArrayList<PersonPersist>();
    ArrayList<PersonPersist> pidvolaArray = new ArrayList<PersonPersist>();
    ArrayList<otherListPersist> userdocArray = new ArrayList<otherListPersist>();
    ArrayList<PersonPersist> headhealthhouseArray = new ArrayList<PersonPersist>();

    ArrayAdapter<CharSequence> villcodeAdapter;
    ArrayAdapter<CharSequence> pidAdapter;
    ArrayAdapter<CharSequence> pidvolaAdapter;
    ArrayAdapter<CharSequence> userdocAdapter;
    ArrayAdapter<CharSequence> headhealthhouseAdapter;

    ThaiDatePicker datepicker1;
    ThaiDatePicker datepicker2;
    ThaiTimePicker timePicker;
    Button setDateBTN1;
    Button setDateBTN2;
    Button setTimeBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_detail_edit);
        prepareSpinner();
        setContentDisplay();

    }

    @SuppressWarnings("deprecation")
    private void prepareSpinner() {
        // TODO Auto-generated method stub

        generateFromDatabase(villcode, villcodeAdapter, villcodeArray,
                R.id.answer1, "SELECT villcode,villname FROM village");

        Bundle pidArgs = new Bundle();
        pidArgs.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "house.pid IS NULL OR house.pid <> person.pid OR house.hcode='" + share_hcode + "'");
        pidArgs.putString(FFCSearchListDialog.EXTRA_DEFAULT_WHERE,
                "person.hcode='" + share_hcode + "'");
        pid = (SearchableSpinner) findViewById(R.id.answer2);
        pid.setDialog(getSupportFragmentManager(), PersonHouseListDialog.class,
                pidArgs, "pid");

        Bundle volaArgs = new Bundle();
        volaArgs.putString(PersonProtagonistListDialog.KEY_TYPE,
                PersonProtagonistListDialog.TYPE_VOLA);
        pidvola = (SearchableSpinner) findViewById(R.id.answer3);
        pidvola.setDialog(getSupportFragmentManager(), PersonProtagonistListDialog.class, volaArgs, "vola");

        headhealthhouse = (SearchableSpinner) findViewById(R.id.answer10);
        Bundle args = new Bundle();
        args.putString(PersonProtagonistListDialog.KEY_TYPE,
                PersonProtagonistListDialog.TYPE_HEALTH_HEAD);
        headhealthhouse.setDialog(getSupportFragmentManager(),
                PersonProtagonistListDialog.class, args, "health_head");
    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub

        villcode = (Spinner) findViewById(R.id.answer1);
        pid = (SearchableSpinner) findViewById(R.id.answer2);
        pidvola = (SearchableSpinner) findViewById(R.id.answer3);
        houseChar = (ArrayFormatSpinner) findViewById(R.id.answer4);
        houseChar.setArray(R.array.houseChar);
        houseCharground = (ArrayFormatSpinner) findViewById(R.id.answer5);
        houseCharground.setArray(R.array.houseCharground);
        housearea = (ArrayFormatSpinner) findViewById(R.id.answer6);
        housearea.setArray(R.array.houseArea);
        communityno = (EditText) findViewById(R.id.answer7);
        houseroad = (EditText) findViewById(R.id.answer8);
        telephonehouse = (EditText) findViewById(R.id.answer9);
        telephonehouse
                .addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        usernamedoc = (Spinner) findViewById(R.id.answer10);
        headhealthhouse = (SearchableSpinner) findViewById(R.id.answer10);

        setContentQuery(House.CONTENT_URI, new String[]{House.VILLCODE,
                        House.PID, House.PIDVOLA, House.CHAR, House.CHARGROUND,
                        House.AREA, House.COMMUNITY, House.ROAD, House.TEL,
                        House.HEADHEALTHHOUSE}, "hcode = ?",
                new String[]{share_hcode}, House.DEFAULT_SORTING);

        System.out.println("SHARE_HCODE = " + share_hcode);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    @SuppressWarnings("deprecation")
    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        villcode.setSelection(checkSpinnerQuery(array[0], villcodeArray));
        pid.setSelection(checkSpinnerPersonQuery(array[1], pidArray));
        pidvola.setSelection(checkSpinnerPersonQuery(array[2], pidvolaArray));
        houseChar.setSelection(checkStrangerForStringContent(array[3], "1"));
        houseCharground.setSelection(checkStrangerForStringContent(array[4],
                "1"));
        housearea.setSelection(checkStrangerForStringContent(array[5], "1"));
        communityno.setText(checkEditText(array[6]));
        houseroad.setText(checkEditText(array[7]));
        telephonehouse.setText(checkEditText(array[8]));
        if (array[9] != null)
            headhealthhouse.setSelectionById(checkStrangerForLongContent(
                    array[9], (long) 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void Update() {

        ContentValues cv = new ContentValues();

        retrieveDataFromSpinnerWithQuery(villcode, cv, "villcode",
                villcodeArray);
        retrieveDataFromSpinnerPerson(pid, cv, "pid", pidArray);
        retrieveDataFromSpinnerPerson(pidvola, cv, "pidvola", pidvolaArray);
        cv.put(House.PID, pid.getSelectedItemId());
        cv.put(House.PIDVOLA, pidvola.getSelectedItemId());
        cv.put(House.CHAR, houseChar.getSelectionId());
        cv.put(House.CHARGROUND, houseCharground.getSelectionId());
        cv.put(House.AREA, housearea.getSelectionId());
        System.out.println(houseChar.getSelectionId() + " "
                + houseCharground.getSelectionId() + " "
                + housearea.getSelectionId());
        retrieveDataFromEditText(communityno, cv, "communityno");
        retrieveDataFromEditText(houseroad, cv, "road");
        retrieveDataFromEditText(telephonehouse, cv, "telephonehouse");
        cv.put(House.HEADHEALTHHOUSE, headhealthhouse.getSelectedItemId());
        updateTimeStamp(cv);

        doHouseCommit(cv, House.CONTENT_URI, true);

    }

    @Override
    protected void Delete() {
    }

    @Override
    protected void Edit() {
    }

}
