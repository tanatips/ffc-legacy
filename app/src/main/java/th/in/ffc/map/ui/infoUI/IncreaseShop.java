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

public class IncreaseShop extends FFCFragmentActivity implements OnClickListener {

    private FGSystemManager fgSystem;

    private DatabaseManager db;

    private Spinner village_list;
    private Spinner shop_type;
    private Spinner alchohol_permit;

    private EditText business_no;
    private EditText shop_name;
    private EditText shop_address;
    private EditText owner_text;

    private RadioGroup fresh1;
    private RadioGroup food_drink_1;

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
                    business_no.setText(msg.obj.toString());
                    break;
                case 2:
                    shop_type.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.increase_shop_activity);

        fgSystem = FGActivity.fgsys;

        village_list = (Spinner) findViewById(R.id.village_list);
        shop_type = (Spinner) findViewById(R.id.shop_type);
        alchohol_permit = (Spinner) findViewById(R.id.alchohol_permit_list);

        business_no = (EditText) findViewById(R.id.business_no);
        shop_name = (EditText) findViewById(R.id.shop_name);
        shop_address = (EditText) findViewById(R.id.shop_address);
        owner_text = (EditText) findViewById(R.id.owner_text);

        fresh1 = (RadioGroup) findViewById(R.id.fresh_food_group);
        food_drink_1 = (RadioGroup) findViewById(R.id.food_drink_group);

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
        db = fgSystem.getFGDatabaseManager().getDatabaseManager();

        if (db.openDatabase()) {

            // ----

            Cursor cur = db.getCursor("SELECT max(businessno) FROM villagebusiness");

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

            sendMessage(2, getDataFromDatabase("SELECT distinct businesstypecode,businessdesc FROM cbusiness"));

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
                    String shop_type_code = ((SpinnerItem) shop_type.getSelectedItem()).getID();
                    String shop_type_text = ((SpinnerItem) shop_type.getSelectedItem()).getName();

                    int business_number = Integer.parseInt(business_no.getText().toString());
                    String business_name = shop_name.getText().toString();
                    String address = shop_address.getText().toString();
                    String owner = owner_text.getText().toString();

                    String freshment = null;
                    switch (fresh1.getCheckedRadioButtonId()) {
                        case R.id.no1:
                            freshment = "0";
                            break;
                        case R.id.yes1:
                            freshment = "1";
                            break;
                        case R.id.idk1:
                            freshment = "9";
                            break;
                    }

                    String food1 = null;
                    switch (food_drink_1.getCheckedRadioButtonId()) {
                        case R.id.no1:
                            food1 = "0";
                            break;
                        case R.id.yes1:
                            food1 = "1";
                            break;
                        case R.id.idk1:
                            food1 = "9";
                            break;
                    }

                    String drug1 = null;
                    switch (alchohol_permit.getSelectedItemPosition()) {
                        case 0:
                            drug1 = "0";
                            break;
                        case 1:
                            drug1 = "c";
                            break;
                        case 2:
                            drug1 = "w";
                            break;
                        case 3:
                            drug1 = "2";
                            break;
                        case 4:
                            drug1 = "9";
                            break;
                    }

                    MARKER_TYPE type = MARKER_TYPE.BUSINESS;

                    ContentValues cv = new ContentValues();
                    cv.put("pcucode", pcu);
                    cv.put("villcode", villcode);
                    cv.put(type.getColumnName(), business_number);
                    cv.put("businessname", business_name);
                    cv.put("businesstype", shop_type_code);
                    cv.put("address", address);
                    cv.put("owner", owner);
                    cv.put("freshmart", freshment);
                    cv.put("foodanddrink", food1);
                    cv.put("alchoholpermit", drug1);

                    if (db.insert(type, cv)) {
                        String stringVillName = ((SpinnerItem) village_list.getSelectedItem()).getName();

                        fgSystem.getFGDatabaseManager().addVillageName(villcode, stringVillName);

                        Bundle addition = FGDatabaseManager.setBusinessBundle(business_name, shop_type_text);

                        Spot shop = new Spot(getPcuCode(), type, villcode, business_number, 0, 0, addition);
                        fgSystem.getFGDatabaseManager().addSpotToAvailable(shop);

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
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FinalValue.INT_REQUEST_NEW_VILLAGE && resultCode == Activity.RESULT_OK) {
            this.initializeContent();
        }
    }
}
