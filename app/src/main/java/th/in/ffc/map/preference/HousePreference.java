package th.in.ffc.map.preference;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import th.in.ffc.R;
import th.in.ffc.map.value.FILTER_GROUP;

public class HousePreference extends PreferenceActivity implements OnPreferenceChangeListener {

    public static final String FILE_XML = "house_preference";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getPreferenceManager().setSharedPreferencesName(FILE_XML);
        this.addPreferencesFromResource(R.xml.house_preference);

        CheckBoxPreference house_all = (CheckBoxPreference) this.findPreference("house_checkbox_all");
        house_all.setOnPreferenceChangeListener(this);

        for (FILTER_GROUP fg : FILTER_GROUP.values()) {
            this.findPreference(fg.getSubCategory()).setIntent(new Intent(this, BaseListPreference.class).putExtra("type", fg));
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean value = (Boolean) newValue;
        ((CheckBoxPreference) this.findPreference("chronic_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("disease_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("pregnant_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("after-pregnant_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("unable_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("vola_checkbox")).setChecked(value);
        ((CheckBoxPreference) this.findPreference("old_checkbox")).setChecked(value);
        return true;
    }
}
