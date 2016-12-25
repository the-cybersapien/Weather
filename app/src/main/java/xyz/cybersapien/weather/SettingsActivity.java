package xyz.cybersapien.weather;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by ogcybersapien on 21/11/16.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add general preferences
        addPreferencesFromResource(R.xml.pref_general);

        //Setting up location Preferences
        Preference location = findPreference(getString(R.string.pref_location_key));
        bindPreferenceSummaryToValue(location);

        //Setting up the unit Preferences
        Preference units = findPreference(getString(R.string.pref_units_key));
        bindPreferenceSummaryToValue(units);
    }

    private void bindPreferenceSummaryToValue(Preference preference){

        //Set listener
        preference.setOnPreferenceChangeListener(this);
        //Trigger the Listener Immediately
        onPreferenceChange( preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(),""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String strValue = o.toString();
        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(strValue);
            if (prefIndex >=0){
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(strValue);
        }

        return true;
    }
}