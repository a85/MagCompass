package com.rickreation.magcompass;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.rickreation.filters.kalman.KalmanState;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends Activity implements SensorEventListener {
    public static final String TAG = "HomeActivity";

    private SensorManager mSensorManager;

    private static double[] displayValues = new double[3];
    private static float[] mOrientationValues = new float[3];
    private static float[] aValues = new float[3];
    private static float[] mValues = new float[3];
    private static float[] values = new float[3];
    private static float[] newValues = new float[3];
    private static float[] inR = new float[9];
    private static float[] outR = new float[9];
    private static float[] vals = new float[3];

    private static final float IMPOSSIBLE_ANGLE = 10;

    private float speedAvg = 0;
    private float speedSum = 0;
    private float rotAvg = 0;
    private float rotSum = 0;
    private float rotSqrsum = 0;
    private float rotStdDeviation = 0;

    private float kFilteringFactor = 0.1f;
    private float[] accel = new float[3];
    private ArrayList<Float> tempOrientationValues;
    private ArrayList<Float> speedValues;

    private Handler sensorHandler;
    private SensorReader sensorReader;
    private SensorWriter sensorWriter;

    private TextView m0;
    private FrameLayout frameContainer;

    private FrameView frameView;

    private KalmanState km0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        m0 = (TextView) findViewById(R.id.text_m0);
        frameContainer = (FrameLayout) findViewById(R.id.frameContainer);
        frameView = (FrameView) findViewById(R.id.frameView);

        //Values from interactive-matter.eu…
        km0 = new KalmanState(1.3833094, 0.0625, 32.0, 0.043228418);
        sensorHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up the sensors and listen for their events
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        sensorReader = new SensorReader();
        sensorWriter = new SensorWriter();
        sensorHandler.postDelayed(sensorWriter, 50);
    }

    public void onSensorChanged(SensorEvent evt) {
        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            aValues = evt.values;

        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mValues = evt.values;
        }
        setOrientationAndSpeed();
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void getOrientation() {
        Date now = new Date();
        vals[0] = values[0];
        vals[1] = values[1];
        vals[2] = values[2];

        rotAvg = 0;
        rotSum = 0;
        speedAvg = 0;
        speedSum = 0;
        rotSqrsum = 0;
        rotStdDeviation = 0;

        if (accel[0] == IMPOSSIBLE_ANGLE) {
            accel[0] = vals[0];
            accel[1] = vals[1];
            accel[2] = vals[2];
        }

        accel[0] = vals[0] * kFilteringFactor + accel[0] * (1.0f - kFilteringFactor);
        accel[1] = vals[1] * kFilteringFactor + accel[1] * (1.0f - kFilteringFactor);
        accel[2] = vals[2] * kFilteringFactor + accel[2] * (1.0f - kFilteringFactor);

        mOrientationValues[0] = accel[0];
        mOrientationValues[1] = accel[1];
        mOrientationValues[2] = accel[2];

        displayValues[0] = Math.round(Math.toDegrees(Math.round(mOrientationValues[0] * 100) / 100f));
        displayValues[1] = Math.round(Math.toDegrees(Math.round(mOrientationValues[1] * 100) / 100f));
        displayValues[2] = Math.round(Math.toDegrees(Math.round(mOrientationValues[2] * 100) / 100f));

        km0.update(vals[0]);


    }
    
    private class SensorWriter implements Runnable {
        public void run() {
            sensorHandler.postDelayed(sensorWriter, 50);

            double val = km0.getX();
            val = Math.round(Math.toDegrees(Math.round(val * 100) / 100f));
            


            if(val < 0) {
                val = 360 + val;
            }

            m0.setText("m0 Kalman = " + val);
            frameView.setRotation(val);
        }
    }

    private class SensorReader implements Runnable {

        public void run() {

        }
    }

    private float[] setOrientationAndSpeed() {
        SensorManager.getRotationMatrix(inR, null, aValues, mValues);
        SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
                SensorManager.AXIS_Z, outR);
        SensorManager.getOrientation(outR, newValues);
        if (Math.abs(Math.round(Math.toDegrees(newValues[0]))) != Math.toDegrees(Math.PI)) {
            values[0] = newValues[0];
            values[1] = newValues[1];
            values[2] = newValues[2];
        }

        getOrientation();
        return values;
    }


}
