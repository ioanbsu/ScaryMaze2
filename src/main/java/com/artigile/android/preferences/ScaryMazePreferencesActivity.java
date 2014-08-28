package com.artigile.android.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author ivanbahdanau
 */
public class ScaryMazePreferencesActivity extends PreferenceActivity {

    public static final String TAG = "ScaryMazePreferencesActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ScaryMazePreferencesFragment())
                .commit();
    }

}
