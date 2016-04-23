package th.in.ffc.map.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import th.in.ffc.R;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.service.GeneralAsyncTask;
import th.in.ffc.map.value.FILTER_GROUP;

public class BaseListPreference extends PreferenceActivity implements OnPreferenceChangeListener {

    private FILTER_GROUP type;
    private Handler handler;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = (FILTER_GROUP) this.getIntent().getExtras().getSerializable("type");
        this.getPreferenceManager().setSharedPreferencesName(type.getPreferenceName());
        this.addPreferencesFromResource(R.xml.base_preference);
        this.findPreference("check_all").setOnPreferenceChangeListener(this);

        final PreferenceScreen screen = (PreferenceScreen) this.findPreference("root_screen");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                CheckBoxPreference cb = new CheckBoxPreference(BaseListPreference.this);
                cb.setKey(b.getString("key"));
                cb.setTitle(b.getString("title"));
                screen.addPreference(cb);
                cb.setChecked(b.getBoolean("checked"));
                cb.setDependency("check_all");
            }
        };

        Runnable r = new Runnable() {
            @Override
            public void run() {
                DatabaseManager db = FGActivity.fgsys.getFGDatabaseManager().getDatabaseManager();

                if (db.openDatabase()) {
                    fetchInfo(db);
                    db.closeDatabase();
                }
            }
        };
        new GeneralAsyncTask(this, null, null, 0, 0).execute(r, null);
    }

    private boolean fetchInfo(DatabaseManager db) {

        String query = type.getQueryString();
        if (query == null)
            return true;

        Cursor cur = db.getCursor(query);
        SharedPreferences sh = this.getSharedPreferences(type.getPreferenceName(), Context.MODE_PRIVATE);

        if (cur.moveToFirst()) {
            do {
                Message msg = new Message();
                Bundle b = new Bundle(3);

                String key = cur.getString(0);

                b.putString("key", key);
                b.putString("title", cur.getString(1));
                b.putBoolean("checked", sh.getBoolean(key, true));

                msg.setData(b);

                handler.sendMessage(msg);

            } while (cur.moveToNext());
        }

        cur.close();
        cur = null;

        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean bool = (Boolean) newValue;
        PreferenceScreen root = (PreferenceScreen) this.findPreference("root_screen");
        for (int l = 1; l < root.getPreferenceCount(); l++) {
            CheckBoxPreference cb = (CheckBoxPreference) root.getPreference(l);
            cb.setChecked(bool);
        }
        return true;
    }
}
