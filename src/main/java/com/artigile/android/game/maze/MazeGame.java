package com.artigile.android.game.maze;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.text.TextPaint;
import android.view.SurfaceHolder;
import com.artigile.android.R;
import com.artigile.android.game.Game;
import com.artigile.android.math.BallPositionCalculator;
import com.artigile.android.model.Ball;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author ivanbahdanau
 */
public class MazeGame implements Callable<Boolean>, SensorEventListener, Game {

    private ExecutorService threadPoolExecutorService = Executors.newSingleThreadExecutor();

    private SurfaceHolder surfaceHolder;
    private float accelerationX = 0;
    private float accelerationY = 0;
    private Paint backgroundColor;
    private Paint mazeBackground;
    private Bitmap ballBitmap;
    private List<Rect> maze;
    private Future<Boolean> threadResult;

    public static final Ball ball = new Ball();


    private BallPositionCalculator ballPositionCalculator = new BallPositionCalculator();
    private static boolean run = false;


    public MazeGame(SurfaceHolder surfaceHolder, Context context) {
        this.surfaceHolder = surfaceHolder;
        ballBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.metalball);
        ballBitmap = Bitmap.createScaledBitmap(ballBitmap, ball.getRadius() * 2, ball.getRadius() * 2, false);

        mazeBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapShader shader = new BitmapShader(BitmapFactory.decodeResource(context.getResources(), R.drawable.wood), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mazeBackground.setShader(shader);
        backgroundColor = new Paint();
        backgroundColor.setStyle(Paint.Style.FILL);
    }

    public boolean isRun() {
        return run;
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        ballPositionCalculator.screenSizeChanged(screenHeight, screenWidth);
        maze = MazeBuilder.getLabirint1(screenHeight, screenWidth, 1);
    }

    @Override
    public Boolean call() throws Exception {
        while (run) {
            limitFrameRate();
            ballPositionCalculator.calculatePosition(ball, accelerationX, accelerationY);
            isLevelCompleted();
            if (isLevelCompleted()) {
                backgroundColor.setColor(Color.GREEN);
            } else if (checkIfDotInsideMaze(ball)) {
                backgroundColor.setColor(Color.BLACK);
            } else {
                backgroundColor.setColor(Color.parseColor("#660000"));
            }
            //limit frame rate to max 60fps
            //limitFrameRate();
            draw();
        }
        return true;
    }

    @Override
    public void start() {
        run = true;
        ball.setTime(System.nanoTime());
        threadResult = threadPoolExecutorService.submit(this);
    }

    @Override
    public void stop() {
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
        ball.reset();
        draw();
    }


    @Override
    public void pause() {
        run = false;
        draw();
    }


    @Override
    public void scary() {

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

    public void setGameSettings(SharedPreferences sharedPref) {
        ballPositionCalculator.gameSettingsChanged(Float.valueOf(sharedPref.getString("prefComplexityLevel", "1")),
                Float.valueOf(sharedPref.getString("prefWallsBounceLevel", "0.5")));
    }


    private void draw() {
        Canvas canvas = null;
        try {
            canvas = surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                if (canvas != null) {
                    //call methods to draw and process next fame
                    canvas.drawPaint(backgroundColor);
                    for (Rect rect : maze) {
                        canvas.drawRect(rect, mazeBackground);
                    }
                    canvas.drawBitmap(ballBitmap, ball.getY() - ball.getRadius(), ball.getX() - ball.getRadius(), new Paint());
                    TextPaint textPaint = new TextPaint();
                    textPaint.setTextSize(30);
                    canvas.drawText("X: " + ball.getX() + " Y:" + ball.getY(), 20, 20, textPaint);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
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

    private boolean checkIfDotInsideMaze(Ball ball) {
        for (Rect rect : maze) {
            if (ball.getY() >= rect.left && ball.getY() <= rect.right &&
                    ball.getX() >= rect.top && ball.getX() <= rect.bottom) {
                return true;
            }
        }
        return false;
    }
}
