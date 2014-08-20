package com.artigile.android.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author ivanbahdanau
 */
public abstract class GameView extends SurfaceView {
    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void startGame();

    public abstract void stopGame();

    public abstract     void setGameSettings(SharedPreferences sharedPref);
}
