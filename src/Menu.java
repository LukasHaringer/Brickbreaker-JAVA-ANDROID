package com.example.hari.brickbreaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Menu extends AppCompatActivity {

    /**
     * Metoda volající se při startu aktivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
    }

    /**
     * Spustí novou hru.
     * @param v
     */
    public void newGame(View v) {
        Intent i = new Intent(this, BrickBreaker.class);
        startActivity(i);
    }

    /**
     * Zobrazí high scores.
     * @param v
     */
    public void highScores(View v) {
        Intent i = new Intent(this, HighScores.class);
        startActivity(i);
    }

    /**
     * Otevře nastavení ovládání.
     * @param v
     */
    public void controlSetup(View v) {
        Intent i = new Intent(this, Control.class);
        startActivity(i);
    }

    /**
     * Otevře informace o hře.
     * @param v
     */
    public void aboutGame(View v) {
        Intent i = new Intent(this, AboutGame.class);
        startActivity(i);
    }
}
