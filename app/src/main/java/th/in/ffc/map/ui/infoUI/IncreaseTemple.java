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
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;

public class IncreaseTemple extends FFCFragmentActivity implements OnClickListener {

    private FGSystemManager fgSystem;

    private DatabaseManager db;

    private Spinner village_list;
    private Spinner religion_list;

    private EditText temple_no;
    private EditText temple_name;
    private EditText temple_address;

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
                    temple_no.setText(msg.obj.toString());
                    break;
                case 2:
                    religion_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.increase_temple_activity);

        fgSystem = FGActivity.fgsys;

        village_list = (Spinner) findViewById(R.id.village_list);
        religion_list = (Spinner) findViewById(R.id.religion_list);

        temple_no = (EditText) findViewById(R.id.temple_no);
        temple_name = (EditText) findViewById(R.id.temple_name);
        temple_address = (EditText) findViewById(R.id.temple_address);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        village_button = (Button) findViewById(R.id.village_button);
        village_button.setOnClickListener(this);


        initializeContent();
    }

    private void sendMessage(int what, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    private void initializeContent() {
        db = fgSystem.getFGDatabaseManager().getDatabaseManager();
        if (db.openDatabase()) {

            // ----

            Cursor cur = db.getCursor("SELECT max(templeno) FROM villagetemple");

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

            sendMessage(2, getDataFromDatabase("SELECT distinct religioncode,religionname FROM creligion"));

            db.closeDatabase();
        }
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
                    String religion = ((SpinnerItem) religion_list.getSelectedItem()).getID();

                    String religion_name = ((SpinnerItem) religion_list.getSelectedItem()).getName();

                    String temple_name_text = temple_name.getText().toString();
                    String address = temple_address.getText().toString();

                    int temple_number = Integer.parseInt(temple_no.getText().toString());

                    MARKER_TYPE type = MARKER_TYPE.TEMPLE;

                    ContentValues cv = new ContentValues();
                    cv.put("pcucode", pcu);
                    cv.put("villcode", villcode);
                    cv.put(type.getColumnName(), temple_number);
                    cv.put("templename", temple_name_text);
                    cv.put("religion", religion);
                    cv.put("address", address);

                    if (db.insert(type, cv)) {
                        String stringVillName = ((SpinnerItem) village_list.getSelectedItem()).getName();

                        fgSystem.getFGDatabaseManager().addVillageName(villcode, stringVillName);

                        Bundle addition = FGDatabaseManager.setTempleBundle(temple_name_text, address, religion_name);

                        Spot temple = new Spot(getPcuCode(), type, villcode, temple_number, 0, 0, addition);

                        fgSystem.getFGDatabaseManager().addSpotToAvailable(temple);

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
