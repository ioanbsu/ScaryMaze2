package com.artigile.android.game.ballrush;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import com.artigile.android.game.AbstractGameView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ivanbahdanau
 */
public class BallRushGameView extends AbstractGameView {

    private ExecutorService threadPoolExecutorService = Executors.newSingleThreadExecutor();

    public BallRushGameView(Context context) {
        super(context);
    }

    private static boolean run = false;

    private Future<Boolean> threadResult;

    @Override
    public void startGame() {
        run = true;
        threadResult = threadPoolExecutorService.submit(this);

    }

    @Override
    public void resetGame() {

    }

    @Override
    public void resetLevel() {

    }

    @Override
    public void setGameSettings(SharedPreferences sharedPref) {

    }

    @Override
    public boolean isGameAheadLevel1() {
        return false;
    }

    @Override
    public void pause() {

    }


    @Override
    public Boolean call() throws Exception {
        while (!isLevelCompleted() && run) {

        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }


    @Override
    protected boolean isLevelCompleted() {
        return false;
    }

}
