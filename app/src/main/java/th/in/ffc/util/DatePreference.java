package th.in.ffc.util;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.DatePicker;

public class DatePreference extends Preference implements OnDateSetListener {

    private String oldValue;

    private final String SEPERATOR = "-";

    public DatePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        oldValue = getInitialDate();
    }

    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        oldValue = getInitialDate();
    }

    public DatePreference(Context context) {
        super(context);

        oldValue = getInitialDate();
    }

    private String getInitialDate() {
        return "2003-01-01";
    }

    @Override
    protected void onClick() {
        super.onClick();

        String[] token = oldValue.split(SEPERATOR);
        DatePickerDialog dp = new DatePickerDialog(this.getContext(), this,
                Integer.parseInt(token[0]), Integer.parseInt(token[1]) - 1,
                Integer.parseInt(token[2]));
        dp.show();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,
                                     Object defaultValue) {

        String str = restorePersistedValue ? this
                .getPersistedString(getInitialDate()) : (String) defaultValue;

        if (!restorePersistedValue) {
            persistString(str);
        }

        this.oldValue = str;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {

        SharedPreferences.Editor sh = this.getEditor();
        String input = year + SEPERATOR
                + String.format("%02d", monthOfYear + 1) + SEPERATOR
                + String.format("%02d", dayOfMonth);
        sh.putString(getKey(), input);
        sh.commit();

        this.oldValue = input;

        this.notifyChanged();
    }

}
