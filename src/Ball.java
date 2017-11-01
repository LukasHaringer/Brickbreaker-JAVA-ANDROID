package com.example.hari.brickbreaker;

import android.graphics.RectF;

public class Ball {
    /**
     * Poloha míčku
     */
    RectF rect;
    /**
     * Rychlost pohybu míčku
     */
    float overallVelocity;
    /**
     * Rychlost pohybu míčku po ose x
     */
    float xVelocity;
    /**
     * Rychlost pohybu míčku po ose y
     */
    float yVelocity;
    /**
     * Šířka míčku
     */
    float ballWidth = 10;
    /**
     * Výška míčku
     */
    float ballHeight = 10;

    /**
     * Kontruktor třídy Ball.
     *
     * @param screenX
     * @param screenY
     */
    public Ball(int screenX, int screenY) {
        overallVelocity = 500;
        xVelocity = 200;
        yVelocity = (overallVelocity - xVelocity) * -1;
        rect = new RectF();
    }

    /**
     * Vrátí polohu míčku.
     *
     * @return
     */
    public RectF getRect() {
        return rect;
    }

    /**
     * Updatuje polohu míčku.
     *
     * @param fps
     */
    public void update(long fps) {
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + ballWidth;
        rect.bottom = rect.top - ballHeight;
    }

    /**
     * Obrátí směr pohybu míčku po ose y.
     */
    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    /**
     * Obrátí směr pohybu míčku po ose x.
     */
    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }

    /**
     * Zvýší rychlost pohybu míčku.
     */
    public void increaseOverallVelocity() {
        overallVelocity = overallVelocity + 50;
    }

    /**
     * Vrátí rychlost pohybu míčku na původní úroveň.
     */
    public void resetOverallVelocity() {
        overallVelocity = 500;
    }

    /**
     * Metoda sloužící pro nastavení odrazu míčku.
     *
     * @param velocity
     */
    public void setXVelocity(float velocity) {
        xVelocity = velocity * 5;
        float velocityMa = overallVelocity - Math.abs(xVelocity);
        if (yVelocity > 0)
            yVelocity = velocityMa;
        else yVelocity = -1 * velocityMa;
    }

    /**
     * Metoda zajišťující, aby se míček nesekl při dotyku o pádlo.
     * @param y
     */
    public void clearObstacleY(float y) {
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    /**
     * Metoda zajišťující, aby se míček nesekl při dotyku o pádlo.
     * @param x
     */
    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + ballWidth;
    }

    /**
     * Metoda pro resetování hodnot míčku.
     * @param x
     * @param y
     */
    public void reset(int x, int y) {
        if (yVelocity > 0) yVelocity = yVelocity * -1;

        rect.left = x / 2;
        rect.top = y - 20;
        rect.right = x / 2 + ballWidth;
        rect.bottom = y - 20 - ballHeight;
    }

}