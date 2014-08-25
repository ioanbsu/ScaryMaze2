package com.artigile.android.game.maze;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import com.artigile.android.game.Game;
import com.artigile.android.game.GameView;
import com.artigile.android.game.RecorderService;

/**
 * @author ivanbahdanau
 */
public class MagicMazeView extends GameView implements SurfaceHolder.Callback {

    private SensorManager mSensorManager;

    private Game mazeGame;
    private Context context;


    public MagicMazeView(Context context) {
        super(context);
        this.context = context;
        mazeGame = new MazeGame(getHolder(), context);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mazeGame, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        getHolder().addCallback(this);
    }


    @Override
    public void startGame() {
        if (mazeGame.isScaryLevel()) {
            Intent recordIntent = new Intent(context, RecorderService.class);
            recordIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(recordIntent);
        }
        mazeGame.start();
    }

    @Override
    public void resetGame() {
        mazeGame.reset();
    }

    @Override
    public void resetLevel() {
        mazeGame.resetLevel();
    }

    @Override
    public void setGameSettings(SharedPreferences sharedPref) {
        mazeGame.setGameSettings(sharedPref);
    }

    @Override
    public boolean isGameInProgress() {
        return mazeGame.isGameInProgress();
    }

    @Override
    public void pause() {
        mazeGame.pause();
    }

    @Override
    public boolean showContextMenu() {
        return super.showContextMenu();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mazeGame.setScreenSize(w, h);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mazeGame.draw();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mazeGame.resetLevel();
    }

    @Override
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        super.setOnCreateContextMenuListener(l);
    }
}
