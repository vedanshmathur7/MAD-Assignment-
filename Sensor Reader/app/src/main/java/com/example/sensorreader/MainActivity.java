package com.example.sensorreader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView accXTextView, accYTextView, accZTextView;
    private TextView lightTextView;
    private TextView proximityTextView;
    private TextView statusTextView;
    private SensorHelper sensorHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accXTextView = findViewById(R.id.acc_x_value);
        accYTextView = findViewById(R.id.acc_y_value);
        accZTextView = findViewById(R.id.acc_z_value);
        lightTextView = findViewById(R.id.light_value);
        proximityTextView = findViewById(R.id.proximity_value);
        statusTextView = findViewById(R.id.status_message);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorHelper = new SensorHelper(sensorManager);

        accelerometer = sensorHelper.getAccelerometer();
        lightSensor = sensorHelper.getLightSensor();
        proximitySensor = sensorHelper.getProximitySensor();

        checkSensorAvailability();
    }

    private void checkSensorAvailability() {
        StringBuilder statusMessage = new StringBuilder();

        if (accelerometer == null) {
            statusMessage.append("No Accelerometer on this device.\n");
            accXTextView.setText("X: N/A");
            accYTextView.setText("Y: N/A");
            accZTextView.setText("Z: N/A");
        }
        if (lightSensor == null) {
            statusMessage.append("No Light Sensor on this device.\n");
            lightTextView.setText("Intensity: N/A");
        }
        if (proximitySensor == null) {
            statusMessage.append("No Proximity Sensor on this device.\n");
            proximityTextView.setText("Distance: N/A");
        }

        if (statusMessage.length() == 0) {
            statusTextView.setText("All required sensors are available.");
        } else {
            statusTextView.setText(statusMessage.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accXTextView.setText(String.format("X: %.2f", event.values[0]));
            accYTextView.setText(String.format("Y: %.2f", event.values[1]));
            accZTextView.setText(String.format("Z: %.2f", event.values[2]));
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            lightTextView.setText(String.format("Intensity: %.2f lx", event.values[0]));
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityTextView.setText(String.format("Distance: %.2f cm", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used, but required by SensorEventListener
    }
}