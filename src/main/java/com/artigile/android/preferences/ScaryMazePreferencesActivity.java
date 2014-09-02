package com.artigile.android.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.artigile.android.MazeApp;
import com.google.android.gms.analytics.HitBuilders;

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


    @Override
    protected void onResume() {
        super.onResume();
        ((MazeApp) getApplication()).getTracker("Preferences").send(new HitBuilders.EventBuilder()
                .setCategory("App Event")
                .setAction("Preferences screen displayed")
                .setValue(1)
                .build());
    }
}
