package com.example.hari.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Arrays;

public class BrickBreaker extends Activity {

    /**
     * View hry. Obsahuje logiku hry a reaguje na vstupy
     */
    BrickBreakerView brickBreakerView;

    /**
     * Inicializace breakoutView
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brickBreakerView = new BrickBreakerView(this);
        setContentView(brickBreakerView);
    }

    /**
     * Implementace BrickBreakerView. Vnitřní třída BrickBreaker.
     */
    class BrickBreakerView extends SurfaceView implements Runnable, SensorEventListener {

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Thread gameThread = null;
        Context context;
        SurfaceHolder ourHolder;
        Canvas canvas;
        Paint paint;

        /**
         * Proměnná určující zda je spuštěna hra.
         */
        volatile boolean playing;
        /**
         * Proměnná určující zda je hra pausnutá.
         */
        boolean paused = true;
        /**
         * Proměnná určující zda se má překreslovat hrací plocha.
         */
        boolean dontDraw = false;
        /**
         * Proměnná určující typ ovládáná 0 = dotykové, 1 = náklonem
         */
        int control = 0;

        /**
         * Proměnné určující frame rate
         */
        long fps;
        long gameSpeed = 1000;
        private long timeThisFrame;

        /**
         * Velikost obrazovky v pixelech
         */
        int screenX;
        int screenY;

        /**
         * Instance herních objektů
         */
        Paddle paddle;
        Ball ball;
        Brick[] bricks = new Brick[200];

        /**
         * Počet cihel
         */
        int numBricks = 0;

        /**
         * Proměnné obsahující zvuky
         */
        SoundPool soundPool;
        int beep = -1;
        int loseLife = -1;
        int explode = -1;

        /**
         * Proměnné pro sledování stavu hry.
         */
        int score = 0;
        int levelEnd = 240;
        int level = 1;
        int lives = 3;

        /**
         * Konstruktor BrickBreakerView.
         *
         * @param context
         */
        public BrickBreakerView(Context context) {
            super(context);
            this.context = context;

            /**
             * Nastavení typu ovládání.
             */
            SharedPreferences prefs = context.getSharedPreferences("breakoutControl", Context.MODE_PRIVATE);
            control = prefs.getInt("control", 0);
            /**
             * Nastavení akcelerometru pro ovládání náklonem.
             */
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            /**
             * Nastavení obrazovky a vykreslení objektů
             */
            ourHolder = getHolder();
            paint = new Paint();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            paddle = new Paddle(screenX, screenY);
            ball = new Ball(screenX, screenY);

            /**
             * Načtení zvuků
             */
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

            try {
                AssetManager assetManager = context.getAssets();
                AssetFileDescriptor descriptor;

                descriptor = assetManager.openFd("beep.ogg");
                beep = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("loseLife.ogg");
                loseLife = soundPool.load(descriptor, 0);

                descriptor = assetManager.openFd("explode.ogg");
                explode = soundPool.load(descriptor, 0);
            } catch (IOException e) {
                Log.e("error", "failed to load sound files");
            }

            createBricksAndRestart();

        }

        /**
         * Herní smyčka programu.
         */
        @Override
        public void run() {

            while (playing) {
                long startFrameTime = System.currentTimeMillis();

                if (!paused) {
                    update();
                }

                if (!dontDraw) {
                    draw();
                }

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = gameSpeed / timeThisFrame;
                }
            }
        }

        /**
         * Metoda volající se při dokončení levelu. Vytvoří nové cihly a zrychlí míček.
         */
        public void nextLevel() {
            ball.reset(screenX, screenY);
            paddle.center();
            levelEnd = levelEnd + 240;
            ball.increaseOverallVelocity();
            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;
            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
        }

        /**
         * Metoda spuštějící novou hru. Vyresetuje stav hry a vykreslí nové cihly.
         */
        public void createBricksAndRestart() {

            ball.reset(screenX, screenY);
            ball.resetOverallVelocity();
            paddle.center();
            levelEnd = 240;

            int brickWidth = screenX / 8;
            int brickHeight = screenY / 10;

            numBricks = 0;
            for (int column = 0; column < 8; column++) {
                for (int row = 0; row < 3; row++) {
                    bricks[numBricks] = new Brick(row, column, brickWidth, brickHeight);
                    numBricks++;
                }
            }
            score = 0;
            lives = 3;
            level = 1;
        }

        /**
         * Metoda starající se o update pozic objektů.
         */
        public void update() {

            /**
             * Pohyb míčku a plošiny
             */
            paddle.update(fps);
            ball.update(fps);

            /**
             * Kontrola kolize míčku s cihlou
             */
            for (int i = 0; i < numBricks; i++) {
                if (bricks[i].getVisibility()) {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect())) {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                        soundPool.play(explode, 1, 1, 0, 0, 1);
                    }
                }
            }

            /**
             * Kontrola kolize míčku s plošinou
             */
            if (RectF.intersects(paddle.getRect(), ball.getRect())) {
                ball.setXVelocity(ball.getRect().centerX() - paddle.getRect().centerX());
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top - 2);
                soundPool.play(beep, 1, 1, 0, 0, 1);
            }

            /**
             * Kontrola kolize míčku se spodkem obrazovky
             */
            if (ball.getRect().bottom > screenY) {
                ball.reverseYVelocity();
                ball.clearObstacleY(screenY - 2);

                lives--;
                soundPool.play(loseLife, 1, 1, 0, 0, 1);

                if (lives == 0) {
                    drawLost();
                    paused = true;
                    dontDraw = true;
                    createBricksAndRestart();
                }
            }

            /**
             * Kontrola kolize míčku s horním okrajem obrazovky
             */
            if (ball.getRect().top < 0) {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);

                soundPool.play(beep, 1, 1, 0, 0, 1);
            }

            /**
             * Kontrola kolize míčku s levým okrajem obrazovky
             */
            if (ball.getRect().left < 0) {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
                soundPool.play(beep, 1, 1, 0, 0, 1);
            }

            /**
             * Kontrola kolize míčku s pravým okrajem obrazovky
             */
            if (ball.getRect().right > screenX - 10) {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
                soundPool.play(beep, 1, 1, 0, 0, 1);
            }

            /**
             * Kontrola zda je dokončen současný level
             */
            if (score / levelEnd != 0 && score != 0) {
                drawWin();
                paused = true;
                dontDraw = true;
                nextLevel();
            }
        }

        /**
         * Metoda sloužící pro uložení score mezi high scores
         */
        public void saveScore() {
            int pole[] = new int[11];
            SharedPreferences prefs = context.getSharedPreferences("breakoutKey", Context.MODE_PRIVATE);

            for (int i = 0; i < 10; i++) {
                pole[i] = (int) (prefs.getInt("" + i, 0));
            }
            pole[10] = (score);
            Arrays.sort(pole);
            SharedPreferences.Editor editor = prefs.edit();
            for (int i = 10; i > 0; i--) {
                editor.putInt("" + (10 - i), pole[i]);
            }
            editor.commit();
        }

        /**
         * Metoda starající se o vykreslování objektů.
         */
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.BLACK);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawRect(paddle.getRect(), paint);
                canvas.drawRect(ball.getRect(), paint);
                paint.setColor(Color.RED);
                for (int i = 0; i < numBricks; i++) {
                    if (bricks[i].getVisibility()) {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(40);
                canvas.drawText("Score: " + score + "   Lives: " + lives + "   Level: " + level, 10, 50, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * Uloží score a vykreslí informaci o prohře
         */
        public void drawLost() {

            saveScore();
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                if (lives <= 0) {
                    paint.setTextSize(90);
                    canvas.drawText("YOU HAVE LOST!", 10, screenY / 2, paint);
                }
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * Metoda vykreslující informace o dohraném levelu.
         */
        public void drawWin() {
            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();
                paint.setTextSize(90);
                canvas.drawText("LEVEL" + ++level, 10, screenY / 2, paint);
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        /**
         * Metoda pro pausnutí hry.
         */
        public void pause() {
            playing = false;
            paused = true;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }
        }

        /**
         * Metoda pro resume hry.
         */
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        /**
         * Metoda pro reagovaní na dotyk obrazovky. Slouží pro ovládání plošiny.
         *
         * @param motionEvent
         * @return
         */
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:

                        paused = false;
                        dontDraw = false;
                        if (control == 0) {
                            if (motionEvent.getX() > screenX / 2) {
                                paddle.setMovementState(paddle.RIGHT);
                            } else {
                                paddle.setMovementState(paddle.LEFT);
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:

                        if (control == 0) {
                            paddle.setMovementState(paddle.STOPPED);
                        }
                        break;
                }
            }
            return true;
        }

        /**
         * Metoda pro reagovaní na náklon obrazovky. Slouží pro ovládání plošiny.
         *
         * @param sensorEvent
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (control == 1) {
                Sensor mySensor = sensorEvent.sensor;
                if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float x = sensorEvent.values[0];
                    float y = sensorEvent.values[1];
                    float z = sensorEvent.values[2];

                    if (y > 1) {
                        paddle.setMovementState(paddle.RIGHT);
                    } else if (y < -1) {
                        paddle.setMovementState(paddle.LEFT);
                    } else {
                        paddle.setMovementState(paddle.STOPPED);
                    }

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            //Do nothing.
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        brickBreakerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        brickBreakerView.pause();
    }

}