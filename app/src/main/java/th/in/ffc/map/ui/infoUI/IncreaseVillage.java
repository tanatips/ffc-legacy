package th.in.ffc.map.ui.infoUI;

import android.content.ContentValues;
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
import th.in.ffc.map.database.SpinnerItem;
import th.in.ffc.map.service.GeneralAsyncTask;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.MISC_ENUM;

public class IncreaseVillage extends FFCFragmentActivity implements OnClickListener {
    private FGSystemManager fgSystem;

    private DatabaseManager db;

    private Spinner metro_list;

    private EditText village_code;
    private EditText village_no;
    private EditText village_name_text;
    private EditText village_cup_text;
    private EditText village_post_text;

    private Button ok_btn;
    private Button cancel_btn;

    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    village_no.setText(msg.obj.toString());
                    break;
                case 1:
                    village_code.setText(msg.obj.toString());
                    break;
                case 2:
                    metro_list.setAdapter((ArrayAdapter<SpinnerItem>) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.increase_village_activity);

        fgSystem = FGActivity.fgsys;

        metro_list = (Spinner) findViewById(R.id.metro_list);

        village_no = (EditText) findViewById(R.id.village_no);
        village_name_text = (EditText) findViewById(R.id.village_name);
        village_code = (EditText) findViewById(R.id.village_code);
        village_cup_text = (EditText) findViewById(R.id.village_cub);
        village_post_text = (EditText) findViewById(R.id.village_post);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);

        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

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
                db = IncreaseVillage.this.fgSystem.getFGDatabaseManager().getDatabaseManager();

                if (db.openDatabase()) {
                    // ----
                    Cursor cur = db.getCursor("SELECT max(villcode) FROM village");

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
                    cur = db.getCursor("SELECT max(villno) FROM village");

                    cur.moveToFirst();

                    next = null;
                    if (cur.isNull(0))
                        next = "0";
                    else
                        next = (cur.getInt(0) + 1) + "";

                    cur.close();
                    cur = null;

                    sendMessage(0, next);
                    // ----

                    db.closeDatabase();
                }

                CharSequence[] list = IncreaseVillage.this.getResources().getTextArray(R.array.metro_list);
                ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(fgSystem.getFGActivity(), R.layout.list_item);
                for (int i = 0; i < list.length; i++) {
                    adapter.add(new SpinnerItem(list[i].toString(), "" + (i + 1)));
                }
                sendMessage(2, adapter);
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
                    String villcode = village_code.getText().toString();

                    int village_number = Integer.parseInt(village_no.getText().toString());
                    String village_name = village_name_text.getText().toString();

                    String metro = ((SpinnerItem) metro_list.getSelectedItem()).getID();

                    String village_cup = village_cup_text.getText().toString();
                    String village_post = village_post_text.getText().toString();

                    MISC_ENUM vill = MISC_ENUM.VILLAGE;

                    ContentValues cv = new ContentValues();
                    cv.put("pcucode", pcu);
                    cv.put(vill.getColumnName(), villcode);
                    cv.put("villno", village_number);
                    cv.put("villname", village_name);
                    cv.put("villmetro", metro);
                    cv.put("cup", village_cup);
                    cv.put("postcode", village_post);
                    cv.put("xgis", 0);
                    cv.put("ygis", 0);

                    if (db.insertVillage(vill, cv)) {
                        fgSystem.getFGDatabaseManager().addVillageName(villcode, village_name);

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
                break;
            case R.id.cancel_btn:
                setResult(RESULT_CANCELED);
                break;
        }
        this.finish();
    }
}
