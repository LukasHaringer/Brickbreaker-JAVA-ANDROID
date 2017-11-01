package com.example.hari.brickbreaker;

import android.graphics.RectF;

public class Brick {

    /**
     * Instance cihly
     */
    private RectF rect;

    /**
     * Určuje zda je cihla viditelná
     */
    private boolean isVisible;

    /**
     * Konstruktor třidy Brick
     * @param row
     * @param column
     * @param width
     * @param height
     */
    public Brick(int row, int column, int width, int height){
        isVisible = true;
        int padding = 1;
        rect = new RectF(column * width + padding,
                row * height + padding,
                column * width + width - padding,
                row * height + height - padding);
    }

    /**
     * Vrací instanci cihly
     * @return
     */
    public RectF getRect(){
        return this.rect;
    }

    /**
     * Nastaví cihlu neviditelnou
     */
    public void setInvisible(){
        isVisible = false;
    }

    /**
     * Vrátí informaci o stavu viditelnosti cihli
     * @return
     */
    public boolean getVisibility(){
        return isVisible;
    }
}