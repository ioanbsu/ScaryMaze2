package com.artigile.android.game.maze;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import com.artigile.android.game.GameView;
import com.artigile.android.game.SurfaceHolderContainer;

/**
 * @author ivanbahdanau
 */
public class MagicMazeView extends GameView implements SurfaceHolder.Callback {

    private SensorManager mSensorManager;

    private MazeGame mazeGame;


    public MagicMazeView(Context context) {
        super(context);
        mazeGame = new MazeGame(getHolder(), context);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mazeGame, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        getHolder().addCallback(this);
    }


    @Override
    public void startGame() {
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
        SurfaceHolderContainer.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        SurfaceHolderContainer.setSurfaceHolder(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mazeGame.resetLevel();
        SurfaceHolderContainer.setSurfaceHolder(null);
    }

    @Override
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        super.setOnCreateContextMenuListener(l);
    }
}
