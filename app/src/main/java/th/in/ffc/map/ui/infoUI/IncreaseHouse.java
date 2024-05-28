package th.in.ffc.map.ui.infoUI;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.FFCSearchListDialog;
import th.in.ffc.code.PersonHouseListDialog;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.database.FGDatabaseManager;
import th.in.ffc.map.database.SpinnerItem;
import th.in.ffc.map.service.GeneralAsyncTask;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.HouseNumberInputFilter;
import th.in.ffc.widget.SearchableSpinner;

import java.util.Calendar;

public class IncreaseHouse extends FFCFragmentActivity implements OnClickListener {

    private FGSystemManager fgSystem;

    private DatabaseManager db;

    private EditText hid_text;
    private EditText hno_text;

    private Spinner village_list;
    private SearchableSpinner house_owner;
    private Spinner vola_service;
    private Spinner vola_name;

    private DatePicker surveydate;

    private Button ok_btn;
    private Button cancel_btn;

    private Button village_button, house_button;

    private long houseCodeNext = -1;

    private Handler handler = new Handler() {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    village_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
                case 1:
                    hid_text.setText(msg.obj.toString());
                    break;
                case 2:
                    vola_service.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
                case 3:
                    vola_name.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
                case 4:
                    //house_owner.setAdapter((ArrayAdapter<SpinnerItem>)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.increase_house_activity);

        fgSystem = FGActivity.fgsys;

        hid_text = (EditText) findViewById(R.id.hid_text);
        hno_text = (EditText) findViewById(R.id.hno_text);
        hno_text.setFilters(new InputFilter[]{new HouseNumberInputFilter()});

        village_list = (Spinner) findViewById(R.id.village_list);
        house_owner = (SearchableSpinner) findViewById(R.id.h_owner_text);
        vola_service = (Spinner) findViewById(R.id.vola_service);
        vola_name = (Spinner) findViewById(R.id.vola_name);

        surveydate = (DatePicker) findViewById(R.id.surveydate);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        village_button = (Button) findViewById(R.id.village_button);
        village_button.setOnClickListener(this);
        house_button = (Button) findViewById(R.id.increase_owner);
        house_button.setOnClickListener(this);


        this.initializeContent();

    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = Uri.withAppendedPath(Person.CONTENT_URI, "house");
        getContentResolver().notifyChange(uri, null);

        Bundle pidArgs = new Bundle();
        pidArgs.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "house.pid IS NULL OR house.pid <> person.pid");

        house_owner.setDialog(getSupportFragmentManager(), PersonHouseListDialog.class,
                pidArgs, "pid");
    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private void initializeContent() {
        Bundle pidArgs = new Bundle();
        pidArgs.putString(FFCSearchListDialog.EXTRA_APPEND_WHERE, "house.pid IS NULL OR house.pid <> person.pid");
        house_owner.setDialog(getSupportFragmentManager(), PersonHouseListDialog.class,
                pidArgs, "pid");
        // ArrayAdapter<String> arrayAdapterStringVillageName = this
        // .getArrayAdapterStringVillageName(arrayListVillage);
        // this.village_list.setAdapter(arrayAdapterStringVillageName);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                db = fgSystem.getFGDatabaseManager().getDatabaseManager();

                if (db.openDatabase()) {

                    // ----

                    Cursor cur = db.getCursor("SELECT max(hid) FROM house");

                    cur.moveToFirst();

                    String next = null;
                    if (cur.isNull(0))
                        next = "0";
                    else
                        next = (cur.getLong(0) + 1) + "";

                    cur.close();
                    cur = null;

                    sendMessage(1, next);

                    // ----

                    // ----

                    cur = db.getCursor("SELECT max(hcode) FROM house");

                    cur.moveToFirst();

                    if (cur.isNull(0))
                        houseCodeNext = 0;
                    else
                        houseCodeNext = (cur.getLong(0) + 1);

                    cur.close();
                    cur = null;

                    // ----

                    String query = "SELECT distinct pcucode FROM village";
                    cur = db.getCursor(query);

                    ArrayAdapter<SpinnerItem> temp_list = null;
                    if (cur.moveToFirst()) {
                        temp_list = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item_device);
                        do {
                            String str = cur.getString(0);
                            temp_list.add(new SpinnerItem(str, str));
                        } while (cur.moveToNext());
                    }
                    if (temp_list != null) {
                        sendMessage(2, temp_list);
                    }

                    cur.close();
                    cur = null;

                    // ----

                    query = "SELECT p1.fname,p1.pid FROM person p1,persontype p2 where p1.pid = p2.pid and p2.typecode = \"09\"";
                    cur = db.getCursor(query);
                    temp_list = null;
                    if (cur.moveToFirst()) {
                        temp_list = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item_device);
                        do {
                            temp_list.add(new SpinnerItem(cur.getString(0), cur.getString(1)));
                        } while (cur.moveToNext());
                    }
                    if (temp_list != null) {
                        sendMessage(3, temp_list);
                    }

                    cur.close();
                    cur = null;

                    // ----

//					query = "SELECT DISTINCT p1.pid,p1.fname FROM person p1 WHERE NOT EXISTS (SELECT h1.pid FROM house h1 WHERE h1.pid = p1.pid)";
//					cur = db.getCursor(query);
//					temp_list = null;
//					if (cur.moveToFirst()) {
//						temp_list = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item);
//						do {
//							temp_list.add(new SpinnerItem(cur.getString(1), "" + cur.getLong(0)));
//						} while (cur.moveToNext());
//					}
//					if (temp_list != null){
//						sendMessage(4, temp_list);
//					}
//
//					cur.close();
//					cur = null;
// ----


                    query = "SELECT distinct villcode,villname FROM village";
                    cur = db.getCursor(query);
                    temp_list = null;
                    if (cur.moveToFirst()) {
                        temp_list = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item_device);
                        do {
                            temp_list.add(new SpinnerItem(cur.getString(1), cur.getString(0)));
                        } while (cur.moveToNext());
                    }
                    if (temp_list != null) {
                        sendMessage(0, temp_list);
                    }

                    cur.close();
                    cur = null;

                    db.closeDatabase();
                }
            }
        };

        new GeneralAsyncTask(this, null, null, 0, -1).execute(r, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                if (db.openDatabase()) {
                    String pcu = getPcuCode();
                    long hcode = houseCodeNext;
                    String vill_code = ((SpinnerItem) village_list.getSelectedItem()).getID();
                    String hid = hid_text.getText().toString();
                    String hno = hno_text.getText().toString();
                    long pid = house_owner.getSelectedItemId();
                    String pcu_p = vola_service.getSelectedItem().toString();
                    String pidvola = ((SpinnerItem) vola_name.getSelectedItem()).getID();
                    final Calendar c = Calendar.getInstance();
                    String date = surveydate.getYear() + "-" + surveydate.getMonth() + "-" + surveydate.getDayOfMonth() + " "
                            + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND) + ".0";

                    MARKER_TYPE type = MARKER_TYPE.HOUSE;

                    String stringDateTime = DateTime.getCurrentDateTime();
                    String hcode_col = type.getColumnName();

                    ContentValues cv = new ContentValues();
                    cv.put("pcucodeperson", pcu);
                    cv.put("pcucode", pcu);
                    cv.put(hcode_col, hcode);
                    cv.put("villcode", vill_code);
                    cv.put("hid", hid);
                    cv.put("hno", hno);
                    cv.put("pid", pid);
                    cv.put("pcucodepersonvola", pcu_p);
                    cv.put("pidvola", pidvola);
                    cv.put("housesurveydate", date);
                    cv.put("dateregister", stringDateTime);
                    cv.put("dateupdate", stringDateTime);

                    // String query =
                    // "insert into house(,,,,,,,,,) values('"+pcu+"','"+pcu+"',"+hcode+",'"+vill_code+"','"+hid+"','"+hno+"',"+pid+",'"+pcu_p+"','"+pidvola+"','"+date+"')";
                    // Log.d("TAG!", query);

                    if (db.insert(type, cv)) {
                        String stringVillName = ((SpinnerItem) village_list.getSelectedItem()).getName();

                        fgSystem.getFGDatabaseManager().addVillageName(vill_code, stringVillName);

                        ContentValues content_updated = new ContentValues();
                        content_updated.put(hcode_col, hcode);
                        content_updated.put("dateupdate", stringDateTime);

                        db.update("person", content_updated, "pid = " + pid);

                        Cursor cursor = db.getCursor("SELECT person.pid, COUNT(personchronic.pid) AS afield FROM person LEFT JOIN personchronic ON person.pid = personchronic.pid WHERE person.pid = " + pid + " GROUP BY person.pid");
                        cursor.moveToFirst();
                        int intCountPIDPersonchronic = cursor.getInt(1);
                        String stringColor = null;
                        if (intCountPIDPersonchronic > 0) {
                            stringColor = FinalValue.STRING_RED;
                        } else {
                            stringColor = FinalValue.STRING_GREEN;
                        }

                        cursor.close();

                        cursor = db.getCursor("SELECT person.pid, COUNT(persontype.pid) AS bfield FROM person LEFT JOIN persontype ON person.pid = persontype.pid WHERE person.pid = " + pid + " GROUP BY person.pid");
                        cursor.moveToFirst();
                        int intCountPIDPersontype = cursor.getInt(1);
                        boolean booleanSpecial = false;
                        if (intCountPIDPersontype > 0) {
                            booleanSpecial = true;
                        }

                        cursor.close();
                        cursor = null;

                        Bundle addition = FGDatabaseManager.setHouseBundle(hno, stringColor, booleanSpecial, null);

                        Spot house = new Spot(getPcuCode(), type, vill_code, (int) hcode, 0, 0, addition);
                        fgSystem.getFGDatabaseManager().addSpotToAvailable(house);

                        Log.d("TAG!", "Pass!");
                        setResult(RESULT_OK);
                    } else {
                        Log.e("TAG!", "Fail!");
                        Toast.makeText(this, this.getResources().getString(R.string.try_again), Toast.LENGTH_LONG).show();
                        db.closeDatabase();
                        return;
                    }
                    db.closeDatabase();
                }
                finish();
                break;
            case R.id.cancel_btn:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.village_button:
                this.startActivityForResult(new Intent(this, IncreaseVillage.class), FinalValue.INT_REQUEST_NEW_VILLAGE);
                break;
            case R.id.increase_owner:
                Intent add = new Intent(Action.INSERT);
                add.addCategory(Category.PERSON);
                add.addCategory(Category.DEFAULT);
                startActivity(add);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FinalValue.INT_REQUEST_NEW_VILLAGE && resultCode == Activity.RESULT_OK) {
            this.initializeContent();
        }
    }
}
