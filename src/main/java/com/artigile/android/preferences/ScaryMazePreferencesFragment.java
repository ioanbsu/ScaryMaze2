package com.artigile.android.preferences;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.artigile.android.R;

/**
 * @author ivanbahdanau
 */
public class ScaryMazePreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        findPreference("prefScaryLevel").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    Integer value = Integer.valueOf(newValue + "");
                    return value > 1 && value < 11;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }
}
