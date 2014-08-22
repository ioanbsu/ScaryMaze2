package com.artigile.android.game.maze.math;

import com.artigile.android.game.maze.model.Ball;

/**
 * @author ivanbahdanau
 */
public class BallPositionCalculator {

    public static final int MAX_VELOCITY = 500;

    public static final float MICROSECONDS_IN_SEC = 1000000000F;

    private int screenWidth;

    private int screenHeight;

    private float complexity;
    private float wallsSoftness;

    private static final float MIN_VELOCITY = 0.1F;


    public void screenSizeChanged(int screenHeight, int screenWidth) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public Ball calculatePosition(Ball ball, float accelerationX, float accelerationY) {
        float timeDelta = (System.nanoTime() - ball.getTime()) / MICROSECONDS_IN_SEC * 20 * complexity;
        if (timeDelta / MICROSECONDS_IN_SEC > 1) { //in case there were some big delay we don't conut it.
            timeDelta = 0;
        }
        ball.setTime(System.nanoTime());
        accelerationX *= -1;
        accelerationY *= -1;

        ball = checkForZeroSpeed(ball);
        accelerationX = calculateAcceleration(ball.getVelocityX(), accelerationX);
        accelerationY = calculateAcceleration(ball.getVelocityY(), accelerationY);

        ball.setVelocityX(calculateVelocity(accelerationX, ball.getVelocityX(), timeDelta));
        ball.setVelocityY(calculateVelocity(accelerationY, ball.getVelocityY(), timeDelta));

        ball.setX(calculateCoordinate(ball.getX(), ball.getVelocityX(), accelerationX, timeDelta));
        ball.setY(calculateCoordinate(ball.getY(), ball.getVelocityY(), accelerationY, timeDelta));


        if (ball.getX() < ball.getRadius()) {
            ball.setVelocityX(Math.abs(ball.getVelocityX()) * wallsSoftness);
            ball.setX(ball.getRadius());
        }
        if (ball.getY() < ball.getRadius()) {
            ball.setVelocityY(Math.abs(ball.getVelocityY()) * wallsSoftness);
            ball.setY(ball.getRadius());
        }

        if (ball.getX() > screenHeight - ball.getRadius()) {
            ball.setVelocityX(-Math.abs(ball.getVelocityX()) * wallsSoftness);
            ball.setX(screenHeight - ball.getRadius());
        }
        if (ball.getY() > screenWidth - ball.getRadius()) {
            ball.setVelocityY(-Math.abs(ball.getVelocityY()) * wallsSoftness);
            ball.setY(screenWidth - ball.getRadius());
        }
        return ball;
    }

    public void gameSettingsChanged(float complexity, float wallsSoftness) {
        this.complexity = complexity;
        this.wallsSoftness = wallsSoftness;
    }

    private Ball checkForZeroSpeed(Ball ball) {
        if (Math.abs(ball.getVelocityX()) < MIN_VELOCITY) {
            ball.setVelocityX(0);
        }
        if (Math.abs(ball.getVelocityY()) < MIN_VELOCITY) {
            ball.setVelocityY(0);
        }
        return ball;
    }

    private float calculateAcceleration(float velocity, float acceleration) {
        float mu = .1F;
        if (velocity > 0) {
            acceleration -= mu;
        } else if (velocity < 0) {
            acceleration += mu;
        }
        return acceleration;
    }


    private float calculateCoordinate(float currentCoord, float currentSpeed, float acceleration, float timeDelta) {
        return (currentCoord + currentSpeed * timeDelta + acceleration * timeDelta * timeDelta / 2);
    }

    private float calculateVelocity(float a, float v0, float timeDelta) {
        float calculatedVelocity = a * timeDelta + v0;
        if (Math.abs(calculatedVelocity) > MAX_VELOCITY) {
            return MAX_VELOCITY * Math.signum(calculatedVelocity);
        }
        return calculatedVelocity;
    }
}