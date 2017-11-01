package com.example.hari.brickbreaker;

import android.graphics.RectF;

public class Paddle {

    /**
     * Poloha plošiny
     */
    private RectF rect;

    /**
     * Výška šířka plošiny
     */
    private float length;
    private float height;

    /**
     * X,y poloha plošiny
     */
    private float x;
    private float y;

    /**
     * Rychlost pohybu plošiny
     */
    private float paddleSpeed;

    /**
     * Velikost obrazovky
     */
    private int xscreen;
    private int yscreen;

    /**
     * Pohybové stavy plošiny
     */
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    /**
     * Aktuální pohybový stav plošiny.
     */
    private int paddleMoving = STOPPED;

    /**
     * Konstruktor třídy Paddle
     * @param screenX
     * @param screenY
     */
    public Paddle(int screenX, int screenY){
        xscreen = screenX;
        yscreen = screenY;
        length = 130;
        height = 20;

        x = screenX / 2;
        y = screenY - 20;

        rect = new RectF(x, y, x + length, y + height);

        paddleSpeed = 350;
    }

    /**
     * Vrátí polohu plošiny
     * @return
     */
    public RectF getRect(){
        return rect;
    }

    /**
     * Nastaví pohyb plošiny
     * @param state
     */
    public void setMovementState(int state){
        paddleMoving = state;
    }

    /**
     * Vrátí délku plošiny
     * @return
     */
    public float getLenght(){
        return length;
    }

    /**
     * Metoda pro update plohy plošiny.
     * @param fps
     */
    public void update(long fps){
        if(paddleMoving == LEFT){
            if(x > 0){
            x = x - paddleSpeed / fps;}
        }

        if(paddleMoving == RIGHT){
            if(x < xscreen-length){
            x = x + paddleSpeed / fps;
        }
        }

        rect.left = x;
        rect.right = x + length;
    }

    /**
     * Metoda pro vrácení plošiny zpět do středu obrazovky.
     */
    public void center(){
        x = xscreen / 2;
        y = yscreen - 20;

        rect = new RectF(x, y, x + length, y + height);
    }

}