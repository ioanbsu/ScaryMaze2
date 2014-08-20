package com.artigile.android.model;

/**
 * @author ivanbahdanau
 */
public class Ball {

    private float x;

    private float y;

    private float velocityX;

    private float velocityY;

    private long time;

    private int radius = 40;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void reset() {
        x = radius;
        y = radius;
        velocityX = 0;
        velocityY = 0;
        time = 0;
    }
}
