package com.artigile.android.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.artigile.android.R;
import com.artigile.android.game.maze.MagicMazeView;
import com.artigile.android.preferences.ScaryMazePreferencesActivity;

/**
 * @author ivanbahdanau
 */
public class GameActivity extends FragmentActivity {
    public static final String TAG = "GameActivity";

    private GameView magicMazeView;
    private LinearLayout startPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_game_activity);
        startPanel = (LinearLayout) findViewById(R.id.gameStartPanel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        magicMazeView = new MagicMazeView(this);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gameMainLayout);
        relativeLayout.addView(magicMazeView, 0);
        registerForContextMenu(magicMazeView);
        initListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        magicMazeView.setGameSettings(sharedPref);
        magicMazeView.stopGame();
        toggleHideyBar();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_settings:
                showSettings();
                return true;
            case R.id.show_videos:

                return true;
            case R.id.stop_game:
                magicMazeView.stopGame();
                startPanel.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    private void showSettings() {
        Intent intent = new Intent();
        intent.setClass(GameActivity.this, ScaryMazePreferencesActivity.class);
        startActivityForResult(intent, 0);
    }


    private void initListeners() {
        findViewById(R.id.startGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                startPanel.setVisibility(View.GONE);
                magicMazeView.startGame();
            }
        });
        findViewById(R.id.settingsLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
    }


    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private void toggleHideyBar() {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.i(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
