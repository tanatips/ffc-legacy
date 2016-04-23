package th.in.ffc.map.preference;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import th.in.ffc.R;

public class PreferenceFilter extends PreferenceActivity implements OnPreferenceChangeListener {

    public static final String FILE_XML = "filter_preference";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getPreferenceManager().setSharedPreferencesName(FILE_XML);
        this.addPreferencesFromResource(R.xml.filter_preference);

        // -----------------

        CheckBoxPreference enabled_function = (CheckBoxPreference) this.findPreference("enable_checkbox");
        enabled_function.setOnPreferenceChangeListener(this);

        this.findPreference("house_subcat").setIntent(new Intent(this, HousePreference.class));

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean value = (Boolean) newValue;
        Log.d("TAG!", "Boolean Value is " + value);

        ((CheckBoxPreference) this.findPreference("house_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("temple_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("water_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("school_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("business_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("hospital_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("poi_checkbox")).setChecked(value);
        return true;
    }
}
