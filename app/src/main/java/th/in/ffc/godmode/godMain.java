package th.in.ffc.godmode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;
import th.in.ffc.R;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;

public class godMain extends Activity {

    ToggleButton toMain;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.god_mode_activity);

        toMain = (ToggleButton) findViewById(R.id.tbtn1);
        toMain.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                if (count == getDatePuzzle()) {
                    count = 0;
                    Intent go = new Intent(godMain.this, godModeMain.class);
                    startActivity(go);
                    godMain.this.finish();
                } else {
                    count = 0;
                    Dialog settingsDialog = new Dialog(godMain.this);
                    settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                    settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.custom_diaglogue_with_img
                            , null));
                    settingsDialog.show();
                }
                return false;
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                count += 1;
            }
        });
    }

    int getDatePuzzle() {
        int result = 0;
        Date d = Date.newInstance(DateTime.getCurrentDate());
        int day = d.day;
        result = day % 10;

        return result;
    }

}
