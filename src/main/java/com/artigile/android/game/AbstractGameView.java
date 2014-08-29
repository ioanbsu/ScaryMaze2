package com.artigile.android.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.view.SurfaceView;
import com.artigile.android.game.commonmodel.HasTimeModel;

import java.util.concurrent.Callable;

/**
 * @author ivanbahdanau
 */
public abstract class AbstractGameView extends SurfaceView implements SensorEventListener, Callable<Boolean> {
    public AbstractGameView(Context context) {
        super(context);
    }

    public abstract void startGame();

    public abstract void resetGame();

    public abstract void resetLevel();

    public abstract void setGameSettings(SharedPreferences sharedPref);

    public abstract boolean isGameAheadLevel1();

    public abstract void pause();

    protected abstract boolean isLevelCompleted();

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    protected void limitFrameRate(HasTimeModel hasTimeModel) {
        float timeDelta = System.nanoTime() - hasTimeModel.getTime();
        final int minTimeDelta = 500000;
        if (timeDelta < minTimeDelta) {
            try {
                Thread.sleep((long) (minTimeDelta - timeDelta) / 1000000);
            } catch (InterruptedException e) {

            }
        }
    }
}
