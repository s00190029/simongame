package edu.rdonoghue.simongame;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class play_area extends AppCompatActivity implements SensorEventListener {

    // VAR DELCARE
    private final double NORTH_MOVE_FORWARD = 9.0;   // upper mag limit
    private final double NORTH_MOVE_BACKWARD = 7.0;   // lower mag limit
    boolean highLimit = false;  // detect high limit
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int counter = 0;   // step counter
    public float [] northValue;
    Button bred, bblue, byel, bgreen, fb;

    public TextView directory;
    private final int BLUE = 1;
    private final int RED = 2;
    private final int YELLOW = 3;
    private final int GREEN = 4;

    int arrayIndex = 0;
    int sequenceCount = 0;
    public static int sequenceMax =4;

    boolean north,south,east,west,neutral;
    int[] arrayInternal = new int[12];
    int[] playSeq = new int[12];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_area);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bred = findViewById(R.id.btn_red);
        bblue = findViewById(R.id.btn_blue);
        byel = findViewById(R.id.btn_yel);
        bgreen = findViewById(R.id.btn_green);
        //dir = findViewById(R.id.tv_dirState);
        Bundle b = getIntent().getExtras();
        arrayInternal = b.getIntArray("numbers");
    }

    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    private void flashButton(Button button) {
        fb = button;
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

                fb.setPressed(true);
                fb.invalidate();
                fb.performClick();
                Handler handler1 = new Handler();
                Runnable r1 = new Runnable() {
                    public void run() {
                        fb.setPressed(false);
                        fb.invalidate();
                    }
                };
                handler1.postDelayed(r1, 600);
            }
        };
        handler.postDelayed(r, 600);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        bblue.setText(String.valueOf(z));
        bred.setText(String.valueOf(x));
        byel.setText(String.valueOf(y));

        // COORDINATE VALUES
        float[] northC = {1,2,3,4};
        float[] southC = {1,2,3,4};
        float[] eastC = {1,2,3,4};
        float[] westC = {1,2,3,4};
        float[] neutralC = {1,2,3,4};


        //neutral
        if(((x >= neutralC[0])&& (y>=neutralC[1]))){
            directionReset();

            //  Toast.makeText(this, "neutral", Toast.LENGTH_SHORT).show();
            directory.setText("neutral");
        }
        else{

            isNeutral();

        }

        //  NORTH
        if(((x <=northC[0])&& (y<=northC[1]))&& isNeutral()==true){
            greenPress();
            directory.setText("north");
            neutral= false;
            bgreen.setPressed(true);
        }
        else{
            bgreen.setPressed(false);
            highLimit=true;
        }

        // SOUTH
        if(((x < southC[0] )&& (y> southC[1]))&&isNeutral() == true){
            bluePress();
            directory.setText("south");
            neutral = false;
            bblue.setPressed(true);
        }
        else{
            bblue.setPressed(false);
            highLimit=true;
        }
        //east
        if((x>eastC[0])&&(y>eastC[1])&& isNeutral() == true){
            yellowPress();
            directory.setText("east");
            neutral = false;
            byel.setPressed(true);
        }
        else{
            byel.setPressed(false);
            highLimit=true;
        }

        // WEST
        if((x<westC[0])&&(y<westC[1])&& isNeutral() == true){
            redPress();
            directory.setText("west");
            neutral = false;
            bred.setPressed(true);
        }
        else{
            bred.setPressed(false);
            highLimit=true;
        }

    }
    public void directionReset(){
        north = false;
        south = false;
        east= false;
        west = false;
    }
    public boolean isNeutral(){
        boolean answer = false;
        if((north == false)&&(south == false) && (east == false) &&(west == false)){
            answer =true;

        }
        else {
            answer = false;
        }
        return answer;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    // BUTTON PRESS
    public void doBlue(View view) {
        bluePress();
    }
    public void bluePress(){
        playSeq[arrayIndex++] = BLUE;
        seqVerify();
    }

    public void doYellow(View view) {
        yellowPress();
    }
    public void yellowPress(){
        playSeq[arrayIndex++] = YELLOW;
        seqVerify();
    }

    public void doGreen(View view) {
        greenPress();
    }
    public void greenPress(){
        playSeq[arrayIndex++] = GREEN;
        seqVerify();
    }
    public void doRed(View view){
        redPress();
    }
    public void redPress(){
        playSeq[arrayIndex++] = RED;
        seqVerify();
    }

    public void seqVerify(){
        sequenceCount++;
        if(sequenceCount == sequenceMax){
            if(compareArrays(playSeq, arrayInternal) == true){
                Toast.makeText(this, "correct", Toast.LENGTH_SHORT).show();
            }
            else { Toast.makeText(this, "you suck", Toast.LENGTH_SHORT).show(); }
        }
    }

    public boolean compareArrays(int[]array1in, int[]array2in){
        boolean same = true;
        for (int i = 0;i<array1in.length; i++){
            if(array1in[i]!=array2in[i]){
                same=false;
            }
            else { }
        }
        return same;
    }

}