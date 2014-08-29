package com.artigile.android.game;

import android.app.AlertDialog;
import android.content.*;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.artigile.android.FullScreenUtil;
import com.artigile.android.MazeApp;
import com.artigile.android.R;
import com.artigile.android.game.maze.MagicMazeView;
import com.artigile.android.preferences.ScaryMazePreferencesActivity;
import com.google.android.gms.analytics.HitBuilders;

/**
 * @author ivanbahdanau
 */
public class GameActivity extends FragmentActivity {
    public static final String TAG = "GameActivity";

    private AbstractGameView magicMazeView;
    private LinearLayout startPanel;
    private LinearLayout continuePanel;
    private LinearLayout gameFailPanel;
    private BroadcastReceiver levelDoneReceiver;
    private BroadcastReceiver ballLeftMazeReceiver;
    private BroadcastReceiver scareMeReceiver;

    private SoundPool soundPool;
    private int soundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main_game_activity);
        startPanel = (LinearLayout) findViewById(R.id.gameStartPanel);
        continuePanel = (LinearLayout) findViewById(R.id.gameContinuePanel);
        gameFailPanel = (LinearLayout) findViewById(R.id.gameFailPanel);
        levelDoneReceiver = initLevelDoneReceiver();
        ballLeftMazeReceiver = initBallLeftMazeReceiver();
        scareMeReceiver = initScaredMazeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(levelDoneReceiver, new IntentFilter(GameEvents.LEVEL_DONE.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(ballLeftMazeReceiver, new IntentFilter(GameEvents.BALL_LEFT_MAZE.toString()));
        LocalBroadcastManager.getInstance(this).registerReceiver(scareMeReceiver, new IntentFilter(GameEvents.SCARED.toString()));
        magicMazeView = new MagicMazeView(this);
        registerForContextMenu(magicMazeView);
        initListeners();

        ((RelativeLayout) findViewById(R.id.gameMainView)).addView(magicMazeView, 0);

        displayPopup(PopupType.START_GAME);

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundId = soundPool.load(this, R.raw.scary, 1);
        SurfaceHolderContainer.setSurfaceHolder(((SurfaceView) findViewById(R.id.cameraRecordPreview)).getHolder());

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //re-reading settings data every time user returns back to game
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        magicMazeView.setGameSettings(sharedPref);
        FullScreenUtil.toggleHideyBar(getWindow(),TAG);
        if (magicMazeView.isGameAheadLevel1()) {
            displayPopup(PopupType.CONTINUTE_GAME);
        } else {
            resetGame();
        }
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
                Intent intent = new Intent();
                intent.setClass(GameActivity.this, VideosActivity.class);
                startActivity(intent);
                return true;
            case R.id.reset_game:
                resetGame();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        GameActivity.this.stopService(new Intent(GameActivity.this, RecorderService.class));
        magicMazeView.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GameActivity.this.stopService(new Intent(GameActivity.this, RecorderService.class));
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(levelDoneReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(ballLeftMazeReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(scareMeReceiver);
        resetGame();
        GameActivity.this.stopService(new Intent(GameActivity.this, RecorderService.class));
        super.onDestroy();

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
                displayPopup(PopupType.NONE);
                magicMazeView.startGame();
                ((MazeApp) getApplication()).getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("Game Event")
                        .setAction("Game Started")
                        .setLabel("By clicking on start button")
                        .setValue(1)
                        .build());
            }
        });
        findViewById(R.id.continueGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                displayPopup(PopupType.NONE);
                magicMazeView.startGame();
            }
        });
        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
        findViewById(R.id.tryAgainButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magicMazeView.startGame();
                displayPopup(PopupType.NONE);
            }
        });
        findViewById(R.id.restartGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });


    }


    private BroadcastReceiver initLevelDoneReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    findViewById(R.id.continueText).setVisibility(View.VISIBLE);
                    displayPopup(PopupType.CONTINUTE_GAME);

            }
        };
    }

    private BroadcastReceiver initBallLeftMazeReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
                String gameMode = sharedPref.getString("prefGameMode", Constants.GAME_MODE_RESTART);
                if (Constants.GAME_MODE_RESTART.equals(gameMode)) {
                    magicMazeView.resetLevel();
                    displayPopup(PopupType.CONTINUE_AFTER_FAIL);
                }
                if (Constants.GAME_MODE_EASY.equals(gameMode)) {
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
                }
            }
        };
    }

    private BroadcastReceiver initScaredMazeReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                soundPool.play(soundId, 1, 1, 99999, 0, 1);
                ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(1000);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        new AlertDialog.Builder(GameActivity.this)
                                .setTitle(R.string.app_name)
                                .setMessage(R.string.end_game_message)
                                .setIcon(R.drawable.smile)
                                .setPositiveButton(R.string.game_end_close_button, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetGame();
                                    }
                                })
                                .show();
                    }
                }, 5000);

                ((MazeApp) getApplication()).getTracker().send(new HitBuilders.EventBuilder()
                        .setCategory("Game Event")
                        .setAction("Scared")
                        .setLabel("User had been scared")
                        .setValue(1)
                        .build());
            }
        };
    }

    private void resetGame() {
        magicMazeView.resetGame();
        displayPopup(PopupType.START_GAME);
        GameActivity.this.stopService(new Intent(GameActivity.this, RecorderService.class));
    }

    private void displayPopup(PopupType popupType) {
        gameFailPanel.setVisibility(popupType == PopupType.CONTINUE_AFTER_FAIL ? View.VISIBLE : View.GONE);
        startPanel.setVisibility(popupType == PopupType.START_GAME ? View.VISIBLE : View.GONE);
        continuePanel.setVisibility(popupType == PopupType.CONTINUTE_GAME ? View.VISIBLE : View.GONE);
    }

    private static enum PopupType {
        START_GAME,
        CONTINUTE_GAME,
        CONTINUE_AFTER_FAIL,
        NONE;
    }
}
