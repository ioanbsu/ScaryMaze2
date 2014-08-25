package com.artigile.android.game;

import android.content.SharedPreferences;
import android.hardware.SensorEventListener;

/**
 * @author ivanbahdanau
 */
public interface Game extends SensorEventListener {

    void setScreenSize(int screenWidth, int screenHeight);

    void start();

    void reset();

    void resetLevel();

    void scary();

    boolean isGameInProgress();

    boolean isScaryLevel();

    void pause();

    void setGameSettings(SharedPreferences sharedPref);

    void draw();
}
