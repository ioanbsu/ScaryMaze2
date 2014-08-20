package com.artigile.android.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author ivanbahdanau
 */
public class ScaryMazePreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ScaryMazePreferencesFragment())
                .commit();
    }
}
