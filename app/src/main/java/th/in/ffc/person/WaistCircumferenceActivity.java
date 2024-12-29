package th.in.ffc.person;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TableRow;


import androidx.appcompat.app.AppCompatActivity;


import th.in.ffc.R;

public class WaistCircumferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waist_circumference);
        if (getWindow() != null) {
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }
}