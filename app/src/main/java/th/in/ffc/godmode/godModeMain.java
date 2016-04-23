package th.in.ffc.godmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.intent.Action;
import th.in.ffc.provider.DbOpenHelper;
import th.in.ffc.security.CryptographerService;
import th.in.ffc.widget.CursorStringIdAdapter;

public class godModeMain extends FFCFragmentActivity {

    private DecrypterServiceReceiver mDecrypterReceiver = new DecrypterServiceReceiver();
    @SuppressWarnings("unused")
    private boolean mRegistedDecrypter = false;
    private IntentFilter mDecryptFilter = new IntentFilter(Action.DECRYPT);
    private DbOpenHelper db = new DbOpenHelper(this);
    Spinner user;
    Button loginAs;
    Cursor mC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        setEnableAutoReLogin(false);
        setContentView(R.layout.god_mode_main);
        doDecryptDatabase();
    }

    private void doDecryptDatabase() {

        registerReceiver(mDecrypterReceiver, mDecryptFilter);
        mRegistedDecrypter = true;

        Intent service = new Intent(Action.DECRYPT);
        startService(service);

    }

    private class DecrypterServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean successed;

            successed = intent.getBooleanExtra(
                    CryptographerService.EXTRA_SUCCESS, false);
            if (successed) {
                Toast.makeText(godModeMain.this, "DATABASE DECRYPTED", Toast.LENGTH_SHORT).show();
                doInitialiseObject();
            } else {
                Toast.makeText(godModeMain.this, "Try Again,\nFail to decrypt database", Toast.LENGTH_SHORT).show();
                godModeMain.this.finish();
            }

        }
    }


    private static final String[] FROM = new String[]{
            "username",
            "password"
    };
    private static final int[] TO = new int[]{
            R.id.fullname,
            R.id.citizenID
    };

    void doInitialiseObject() {
        user = (Spinner) findViewById(R.id.selectedUser);
        mC = db.getReadableDatabase().rawQuery("SELECT username,password,pcucode FROM user", null);
        if (mC.moveToFirst()) {
            CursorStringIdAdapter adapter = new CursorStringIdAdapter(godModeMain.this, R.layout.person_list_item, mC, FROM, TO);
            user.setAdapter(adapter);
            loginAs = (Button) findViewById(R.id.submit);
            loginAs.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int pos = user.getSelectedItemPosition();
                    mC.moveToPosition(pos);
                    logIn(mC.getString(2), mC.getString(0));
                    Intent main = new Intent(Action.MAIN);
                    mC.close();
                    startActivity(main);
                    finish();

                }
            });
        }

    }

}
