package com.artigile.android.game.maze;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextPaint;
import android.view.SurfaceHolder;
import com.artigile.android.R;
import com.artigile.android.game.Constants;
import com.artigile.android.game.Game;
import com.artigile.android.game.GameEvents;
import com.artigile.android.game.maze.math.BallPositionCalculator;
import com.artigile.android.game.maze.model.Ball;

import java.util.List;
import java.util.concurrent.*;

/**
 * The actual game logic happens here.
 * Uses SurfaceView to display all the UI components.
 *
 * @author ivanbahdanau
 */
public class MazeGame implements Callable<Boolean>, Game {

    private static final Ball ball = new Ball();
    private static final Paint defaultBackgroundColor = new Paint();
    public static final boolean DEBUG = false;

    private final SurfaceHolder surfaceHolder;
    private static boolean run = false;
    private ExecutorService threadPoolExecutorService = Executors.newSingleThreadExecutor();
    private float accelerationX = 0;
    private float accelerationY = 0;
    private Paint mazeBackground;
    private Bitmap ballBitmap;
    private Bitmap scaryFaceBitmap;
    private List<Rect> maze;
    private Future<Boolean> threadResult;
    private BallPositionCalculator ballPositionCalculator = new BallPositionCalculator();
    private int currentLevel = 1;
    private Context context;

    private boolean ballInMaze = true;

    //game configuration/settings
    private int scaryLevel = 2;
    private int screenHeight, screenWidth;
    private String gameMode = Constants.GAME_MODE_EASY;


    public MazeGame(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.metalball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, ball.getRadius() * 2, ball.getRadius() * 2, false);
        mazeBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader shader = new BitmapShader(BitmapFactory.decodeResource(context.getResources(), R.drawable.wood), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mazeBackground.setShader(shader);
        scaryFaceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scary);
    }

    @Override
    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        ballPositionCalculator.screenSizeChanged(screenHeight, screenWidth);
        maze = MazeBuilder.getLabirint1(screenHeight, screenWidth, currentLevel);
        scaryFaceBitmap = Bitmap.createScaledBitmap(scaryFaceBitmap, screenWidth, screenHeight, false);
        draw();
    }

    /**
     * The thread that is responsible for all UI calculations.
     *
     * @return boolean value. At this point is doesn't really matters what value is returned. We only use it here to
     * check that thread is finished it's execution.
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        while (!isLevelCompleted() && run) {
            ballPositionCalculator.calculatePosition(ball, accelerationX, accelerationY);
            //limit frame rate to max 60fps
            limitFrameRate();
            final boolean timeToScary = currentLevel >= scaryLevel && ball.getY() > screenWidth * 0.7;
            if (timeToScary) {
                scary();
                Intent scaredEvent = new Intent(GameEvents.SCARED.toString());
                LocalBroadcastManager.getInstance(context).sendBroadcast(scaredEvent);
                defaultBackgroundColor.setColor(Color.BLACK);
                return true;
            } else if (ballInMaze != isDotInsideMaze()) {
                if (ballInMaze) {
                    Intent ballLeftMazeEvent = new Intent(GameEvents.BALL_LEFT_MAZE.toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(ballLeftMazeEvent);
                    if (Constants.GAME_MODE_EASY.equals(gameMode)) {
                        defaultBackgroundColor.setColor(Color.parseColor("#660000"));
                    }
                } else {
                    defaultBackgroundColor.setColor(Color.BLACK);
                }

                ballInMaze = isDotInsideMaze();
            }
            draw();
        }
        if (isLevelCompleted()) {
            setLevel(currentLevel + 1);
            Intent levelDoneIntent = new Intent(GameEvents.LEVEL_DONE.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(levelDoneIntent);
        }
        return true;
    }

    /**
     * Starts game simulation.
     */
    @Override
    public void start() {
        ball.reset();
        run = true;
        ball.setTime(System.nanoTime());
        threadResult = threadPoolExecutorService.submit(this);
    }

    /**
     * Resets game to the initial state
     */
    @Override
    public void reset() {
        setLevel(1);
        resetLevel();
    }

    @Override
    public void pause() {
        run = false;
        if (threadResult != null) {
            try {
                threadResult.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * pauses the game
     */
    @Override
    public void resetLevel() {
        pause();
        ball.reset();
        draw();
    }


    @Override
    public void scary() {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                if (canvas != null) {
                    canvas.drawBitmap(scaryFaceBitmap, 0, 0, new Paint());
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public boolean isGameInProgress() {
        return currentLevel > 1;
    }

    @Override
    public boolean isScaryLevel() {
        return scaryLevel == currentLevel;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }
        this.accelerationX = event.values[0];
        this.accelerationY = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private boolean isLevelCompleted() {
        Rect rect = maze.get(maze.size() - 1);
        if (ball.getX() > 0
                && ball.getX() < rect.bottom && ball.getY() > rect.right - 50) {
            return true;
        }
        return false;
    }

    @Override
    public void setGameSettings(SharedPreferences sharedPref) {
        ballPositionCalculator.gameSettingsChanged(Float.valueOf(sharedPref.getString("prefComplexityLevel", "1")),
                Float.valueOf(sharedPref.getString("prefWallsBounceLevel", "0.5")));
        scaryLevel = Integer.valueOf(sharedPref.getString("prefScaryLevel", "2"));
        gameMode = sharedPref.getString("prefGameMode", Constants.GAME_MODE_EASY);
    }


    public void draw() {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                if (canvas != null) {
                    if (DEBUG) {
                        displayDebugInfo(canvas);
                    }
                    //call methods to draw and process next fame
                    canvas.drawPaint(defaultBackgroundColor);
                    for (Rect rect : maze) {
                        canvas.drawRect(rect, mazeBackground);
                    }
                    canvas.drawBitmap(ballBitmap, ball.getY() - ball.getRadius(), ball.getX() - ball.getRadius(), new Paint());
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void setLevel(int level) {
        currentLevel = level;
        maze = MazeBuilder.getLabirint1(screenHeight, screenWidth, currentLevel);
    }

    private void limitFrameRate() {
        float timeDelta = System.nanoTime() - ball.getTime();
        final int minTimeDelta = 500000;
        if (timeDelta < minTimeDelta) {
            try {
                Thread.sleep((long) (minTimeDelta - timeDelta) / 1000000);
            } catch (InterruptedException e) {

            }
        }
    }

    private boolean isDotInsideMaze() {
        for (Rect rect : maze) {
            if (ball.getY() >= rect.left && ball.getY() <= rect.right &&
                    ball.getX() >= rect.top && ball.getX() <= rect.bottom) {
                return true;
            }
        }
        return false;
    }

    private void displayDebugInfo(Canvas canvas) {
        Paint backgroundColor = new Paint();
        backgroundColor.setStyle(Paint.Style.FILL);
        if (isLevelCompleted()) {
            backgroundColor.setColor(Color.GREEN);
        } else if (isDotInsideMaze()) {
            backgroundColor.setColor(Color.BLACK);
        } else {
            backgroundColor.setColor(Color.parseColor("#660000"));
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(30);
        canvas.drawText("X: " + ball.getX() + " Y:" + ball.getY(), 20, 20, textPaint);

    }
}
