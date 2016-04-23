package th.in.ffc.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ThaiTimePicker extends LinearLayout {
    Spinner mSpnHour;
    TextView Spliter;
    Spinner mSpnMinute;
    Context mContext;

    public ThaiTimePicker(Context context) {
        super(context);
        this.addSpinner(context);
    }

    public ThaiTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addSpinner(context);
    }

    private void addSpinner(Context context) {
        mContext = context;
        mSpnHour = new Spinner(context);
        mSpnMinute = new Spinner(context);
        Spliter = new TextView(context);

        String[] Hour = new String[24];
        for (int i = 0; i < Hour.length; i++)
            Hour[i] = pad(i);
        String[] Minute = new String[60];
        for (int i = 0; i < Minute.length; i++) {
            Minute[i] = pad(i);

        }
        LayoutParams IntegerParam = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        ArrayAdapter<String> hAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, Hour);
        hAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnHour.setAdapter(hAdapter);
        this.addView(mSpnHour, IntegerParam);
        Spliter.setText(" : ");
        this.addView(Spliter);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, Minute);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnMinute.setAdapter(mAdapter);
        this.addView(mSpnMinute, IntegerParam);


    }

    public void updateTime(int h, int m) {
        mSpnHour.setSelection(h);
        mSpnMinute.setSelection(m);
    }

    public String toString() {
        String output = null;
        String h = Integer.toString(mSpnHour.getSelectedItemPosition());
        String m = Integer.toString(mSpnMinute.getSelectedItemPosition());

        output = pad(Integer.parseInt(h)) + ":" + pad(Integer.parseInt(m)) + ":00";
        return output;
    }

    public static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
