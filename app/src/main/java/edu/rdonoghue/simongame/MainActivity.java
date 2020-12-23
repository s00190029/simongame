package edu.rdonoghue.simongame;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final int BLUE = 1;
    private final int RED = 2;
    private final int YELLOW = 3;
    private final int GREEN = 4;
    Button bred, bblue, byel, bgreen, fb;

    // SEQUENCE VARS
    int sequenceCount = 4, n = 0;
    private Object mutex = new Object();
    int[] gameSequence = new int[120];
    int arrayIndex = 0;

    // SENSORS
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // COUNTDOWN
    CountDownTimer ct = new CountDownTimer(6000,  1500) {

        public void onTick(long millisUntilFinished) {
            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1500);
            oneButton();
            //here you can have your logic to set text to edittext
        }

        @Override
        public void onFinish() {

            for (int i = 0; i < arrayIndex; i++){
                Log.d("gameSequence", String.valueOf(gameSequence[i]));
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INIT BUTTONS
        bred = findViewById(R.id.btn_red);
        bblue = findViewById(R.id.btn_blue);
        byel = findViewById(R.id.btn_yel);
        bgreen = findViewById(R.id.btn_green);

        // SET SENSORS TO ACCELEROMETER
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);



        // DATABASE TESTING
        DatabaseHandler db = new DatabaseHandler(this);
        db.emptyHiScores();     // empty table if required

            // Inserting hi scores
        Log.i("Insert: ", "Inserting ..");
        db.addHiScore(new HiScore("20 OCT 2020", "Frodo", 12));
        db.addHiScore(new HiScore("28 OCT 2020", "Dobby", 16));
        db.addHiScore(new HiScore("20 NOV 2020", "DarthV", 20));
        db.addHiScore(new HiScore("20 NOV 2020", "Bob", 18));
        db.addHiScore(new HiScore("22 NOV 2020", "Gemma", 22));
        db.addHiScore(new HiScore("30 NOV 2020", "Joe", 30));
        db.addHiScore(new HiScore("01 DEC 2020", "DarthV", 22));
        db.addHiScore(new HiScore("02 DEC 2020", "Gandalf", 132));


        // Reading all scores
        Log.i("Reading: ", "Reading all scores..");
        List<HiScore> hiScores = db.getAllHiScores();


        for (HiScore hs : hiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }

        Log.i("divider", "====================");

        HiScore singleScore = db.getHiScore(5);
        Log.i("High Score 5 is by ", singleScore.getPlayer_name() + " with a score of " +
                singleScore.getScore());

        Log.i("divider", "====================");

        // Calling SQL statement
        List<HiScore> top5HiScores = db.getTopFiveScores();

        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }
        Log.i("divider", "====================");

        HiScore hiScore = top5HiScores.get(top5HiScores.size() - 1);
        // hiScore contains the 5th highest score
        Log.i("fifth Highest score: ", String.valueOf(hiScore.getScore()) );

        // simple test to add a hi score
        int myCurrentScore = 40;
        // if 5th highest score < myCurrentScore, then insert new score
        if (hiScore.getScore() < myCurrentScore) {
            db.addHiScore(new HiScore("08 DEC 2020", "Elrond", 40));
        }

        Log.i("divider", "====================");

        // Calling SQL statement
        top5HiScores = db.getTopFiveScores();

        for (HiScore hs : top5HiScores) {
            String log =
                    "Id: " + hs.getScore_id() +
                            ", Date: " + hs.getGame_date() +
                            " , Player: " + hs.getPlayer_name() +
                            " , Score: " + hs.getScore();

            // Writing HiScore to log
            Log.i("Score: ", log);
        }

    }

    private void oneButton(){
        n = getRandom(sequenceCount);
        switch(n){
            case 1:
                flashButton(bblue);
                gameSequence[arrayIndex++]=BLUE;
                break;
            case 2:
                flashButton(bred);
                gameSequence[arrayIndex++]=RED;
                break;
            case 3:
                flashButton(byel);
                gameSequence[arrayIndex++]=YELLOW;
                break;
            case 4:
                flashButton(bgreen);
                gameSequence[arrayIndex++]=GREEN;
                break;

        }
    }

    // return # 1-maxValue
    private int getRandom(int maxValue) {
        return ((int) ((Math.random() * maxValue) + 1));
    }

    private void flashButton (Button btnIn){
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                btnIn.setPressed(true);
                btnIn.invalidate();
                btnIn.performClick();
                Handler handler1 = new Handler();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        btnIn.setPressed(false);
                        btnIn.invalidate();
                    }
                };
                handler1.postDelayed(r1, 400);
            }
        };
        handler.postDelayed(r, 400);
    }


    protected void onResume(){
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    public void doPlay(View view) {
        ct.start();
    }

    public void doTest(View view) {
        for (int i = 0; i < sequenceCount; i++) {
            int x = getRandom(sequenceCount);

            Toast.makeText(this, "Number = " + x, Toast.LENGTH_SHORT).show();

            if (x == 1)
                flashButton(bblue);
            else if (x == 2)
                flashButton(bred);
            else if (x == 3)
                flashButton(byel);
            else if (x == 4)
                flashButton(bgreen);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}