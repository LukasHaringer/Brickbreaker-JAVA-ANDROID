package com.example.hari.brickbreaker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Control extends AppCompatActivity {

    /**
     * Metoda volící se při startu aktivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_control);
    }

    /**
     * Nastaví ovládání aplikace dotykem.
     * @param v
     */
    public void setTouchControl(View v){
        SharedPreferences prefs = this.getSharedPreferences("breakoutControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("control", 0);
        editor.commit();
        finish();
    }

    /**
     * Nastaví ovládání aplikace náklonem.
     * @param v
     */
    public void setYawnControl(View v){
        SharedPreferences prefs = this.getSharedPreferences("breakoutControl", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("control", 1);
        editor.commit();
        finish();
    }

}
