package com.artigile.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.artigile.android.game.GameActivity;
import com.artigile.android.tutorial.TutorialActivity;

/**
 * @author ivanbahdanau
 */
public class MainActivity extends Activity {


    /**
     * here we check what is user configuration - if he wants to view tutorial or not
     * @param savedInstanceState saved instance parameter
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean displayTutorial = sharedPref.getBoolean("prefDisplayTutorial", true);

        Intent intent = null;
        if (displayTutorial) {
            intent = new Intent(this, TutorialActivity.class);
        } else {
            intent = new Intent(this, GameActivity.class);
        }
        startActivity(intent);
    }


}
