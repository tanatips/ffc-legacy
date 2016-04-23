package th.in.ffc.map.ui.infoUI;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.database.FGDatabaseManager;
import th.in.ffc.map.database.SpinnerItem;
import th.in.ffc.map.service.GeneralAsyncTask;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;

public class IncreaseWater extends FFCFragmentActivity implements OnClickListener {

    private FGSystemManager fgSystem;

    private DatabaseManager db;

    private Spinner village_list;
    private Spinner type_list;
    private Spinner sector_list;
    private Spinner sector_r;

    private EditText water_no;

    private Button ok_btn;
    private Button cancel_btn;

    private Button village_button;

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    village_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
                case 1:
                    water_no.setText(msg.obj.toString());
                    break;
                case 2:
                    type_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
                case 3:
                    sector_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    sector_r.setAdapter(sector_list.getAdapter());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.increase_water_activity);

        fgSystem = FGActivity.fgsys;

        village_list = (Spinner) findViewById(R.id.village_list);
        type_list = (Spinner) findViewById(R.id.water_type);
        sector_list = (Spinner) findViewById(R.id.sector);

        water_no = (EditText) findViewById(R.id.water_no);
        sector_r = (Spinner) findViewById(R.id.sector_responsible);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        village_button = (Button) findViewById(R.id.village_button);
        village_button.setOnClickListener(this);

        this.initializeContent();

    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private void initializeContent() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                db = fgSystem.getFGDatabaseManager().getDatabaseManager();

                if (db.openDatabase()) {

                    // ----
                    Cursor cur = db.getCursor("SELECT max(waterno) FROM villagewater");

                    cur.moveToFirst();

                    String next = null;
                    if (cur.isNull(0))
                        next = "0";
                    else
                        next = (cur.getInt(0) + 1) + "";

                    cur.close();
                    cur = null;

                    sendMessage(1, next);

                    // ----

                    sendMessage(0, getDataFromDatabase("SELECT distinct villcode,villname FROM village"));

                    sendMessage(2, getDataFromDatabase("SELECT distinct watertypecode,watertypename FROM cwatertype"));

                    sendMessage(3, getDataFromDatabase("SELECT distinct waterownercode,waterownername FROM cwaterowner"));

                    db.closeDatabase();
                }
            }
        };
        new GeneralAsyncTask(this, null, null, 0, -1).execute(r, null);
    }

    private ArrayAdapter<?> getDataFromDatabase(String query) {
        Cursor cur = db.getCursor(query);
        ArrayAdapter<SpinnerItem> temp_list = null;
        if (cur.moveToFirst()) {
            temp_list = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item);
            do {
                temp_list.add(new SpinnerItem(cur.getString(1), cur.getString(0)));
            } while (cur.moveToNext());
        }
        cur.close();
        cur = null;

        return temp_list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                if (db.openDatabase()) {
                    String pcu = getPcuCode();
                    String villcode = ((SpinnerItem) village_list.getSelectedItem()).getID();
                    String type_text = ((SpinnerItem) type_list.getSelectedItem()).getID();
                    String sector_code = ((SpinnerItem) sector_list.getSelectedItem()).getID();
                    String sector_name = ((SpinnerItem) sector_list.getSelectedItem()).getName();

                    int water_number = Integer.parseInt(water_no.getText().toString());
                    String sector_r_code = ((SpinnerItem) sector_r.getSelectedItem()).getID();

                    MARKER_TYPE type = MARKER_TYPE.WATER;

                    ContentValues cv = new ContentValues();
                    cv.put("pcucode", pcu);
                    cv.put("villcode", villcode);
                    cv.put(type.getColumnName(), water_number);
                    cv.put("owner", sector_code);
                    cv.put("operater", sector_r_code);
                    cv.put("watertype", type_text);

                    if (db.insert(type, cv)) {
                        String stringVillName = ((SpinnerItem) village_list.getSelectedItem()).getName();

                        fgSystem.getFGDatabaseManager().addVillageName(villcode, stringVillName);

                        Bundle addition = FGDatabaseManager.setWaterBundle(type_text, sector_name);

                        Spot water = new Spot(getPcuCode(), type, villcode, water_number, 0, 0, addition);
                        fgSystem.getFGDatabaseManager().addSpotToAvailable(water);

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
