package com.example.hari.brickbreaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class HighScores extends AppCompatActivity {

    /**
     * Metoda volící se při startu aktivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_high_scores);
        SharedPreferences prefs = this.getSharedPreferences("breakoutKey", Context.MODE_PRIVATE);
        TextView t1 = (TextView)findViewById(R.id.textViewScore1);
        TextView t2 = (TextView)findViewById(R.id.textViewScore2);
        TextView t3 = (TextView)findViewById(R.id.textViewScore3);
        TextView t4 = (TextView)findViewById(R.id.textViewScore4);
        TextView t5 = (TextView)findViewById(R.id.textViewScore5);
        TextView t6 = (TextView)findViewById(R.id.textViewScore6);
        TextView t7 = (TextView)findViewById(R.id.textViewScore7);
        TextView t8 = (TextView)findViewById(R.id.textViewScore8);
        TextView t9 = (TextView)findViewById(R.id.textViewScore9);
        TextView t10 = (TextView)findViewById(R.id.textViewScore10);
        t1.setText("1: " + prefs.getInt("0",0));
        t2.setText("2: " + prefs.getInt("1",0));
        t3.setText("3: " + prefs.getInt("2",0));
        t4.setText("4: " + prefs.getInt("3",0));
        t5.setText("5: " + prefs.getInt("4",0));
        t6.setText("6: " + prefs.getInt("5",0));
        t7.setText("7: " + prefs.getInt("6",0));
        t8.setText("8: " + prefs.getInt("7",0));
        t9.setText("9: " + prefs.getInt("8",0));
        t10.setText("10: " + prefs.getInt("9",0));
    }
}
