package com.example.sensorreader; // Defines the package name for the application

import android.content.Context; // Imports Context to access system services
import android.hardware.Sensor; // Imports Sensor class to represent various sensors
import android.hardware.SensorEvent; // Imports SensorEvent which holds sensor data
import android.hardware.SensorEventListener; // Imports interface to receive sensor updates
import android.hardware.SensorManager; // Imports SensorManager to manage sensor hardware
import android.os.Bundle; // Imports Bundle for passing data between activities
import android.widget.TextView; // Imports TextView to display text in the UI
import androidx.appcompat.app.AppCompatActivity; // Imports base class for activities using modern features

public class MainActivity extends AppCompatActivity implements SensorEventListener { // Main class extending Activity and implementing sensor listeners

    private SensorManager sensorManager; // Manager to handle all sensor-related operations
    private Sensor accelerometer; // Object to represent the accelerometer sensor
    private Sensor lightSensor; // Object to represent the light sensor
    private Sensor proximitySensor; // Object to represent the proximity sensor

    private TextView accXTextView, accYTextView, accZTextView; // TextViews for X, Y, Z accelerometer values
    private TextView lightTextView; // TextView for light intensity value
    private TextView proximityTextView; // TextView for proximity distance value
    private TextView statusTextView; // TextView to show availability of sensors
    private SensorHelper sensorHelper; // Helper class instance to fetch specific sensors

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the activity is first created
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method
        setContentView(R.layout.activity_main); // Sets the layout for this activity from the XML file

        accXTextView = findViewById(R.id.acc_x_value); // Links Java variable to the X-axis TextView in XML
        accYTextView = findViewById(R.id.acc_y_value); // Links Java variable to the Y-axis TextView in XML
        accZTextView = findViewById(R.id.acc_z_value); // Links Java variable to the Z-axis TextView in XML
        lightTextView = findViewById(R.id.light_value); // Links Java variable to the light sensor TextView
        proximityTextView = findViewById(R.id.proximity_value); // Links Java variable to the proximity sensor TextView
        statusTextView = findViewById(R.id.status_message); // Links Java variable to the status message TextView

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Gets the system service for sensors
        sensorHelper = new SensorHelper(sensorManager); // Initializes the helper class with the manager

        accelerometer = sensorHelper.getAccelerometer(); // Gets the accelerometer sensor instance
        lightSensor = sensorHelper.getLightSensor(); // Gets the light sensor instance
        proximitySensor = sensorHelper.getProximitySensor(); // Gets the proximity sensor instance

        checkSensorAvailability(); // Checks if the required sensors exist on this device
    }

    private void checkSensorAvailability() { // Helper method to check and report sensor status
        StringBuilder statusMessage = new StringBuilder(); // String builder to accumulate status messages

        if (accelerometer == null) { // Checks if accelerometer is missing
            statusMessage.append("No Accelerometer on this device.\n"); // Adds message if missing
            accXTextView.setText("X: N/A"); // Sets display text to Not Available
            accYTextView.setText("Y: N/A"); // Sets display text to Not Available
            accZTextView.setText("Z: N/A"); // Sets display text to Not Available
        }
        if (lightSensor == null) { // Checks if light sensor is missing
            statusMessage.append("No Light Sensor on this device.\n"); // Adds message if missing
            lightTextView.setText("Intensity: N/A"); // Sets display text to Not Available
        }
        if (proximitySensor == null) { // Checks if proximity sensor is missing
            statusMessage.append("No Proximity Sensor on this device.\n"); // Adds message if missing
            proximityTextView.setText("Distance: N/A"); // Sets display text to Not Available
        }

        if (statusMessage.length() == 0) { // If no error messages were added
            statusTextView.setText("All required sensors are available."); // Informs user all sensors are present
        } else {
            statusTextView.setText(statusMessage.toString()); // Displays the list of missing sensors
        }
    }

    @Override
    protected void onResume() { // Called when activity starts interacting with the user
        super.onResume(); // Calls superclass onResume
        if (accelerometer != null) { // Registers listener only if sensor exists
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // Starts listening to accelerometer
        }
        if (lightSensor != null) { // Registers listener only if sensor exists
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL); // Starts listening to light sensor
        }
        if (proximitySensor != null) { // Registers listener only if sensor exists
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL); // Starts listening to proximity sensor
        }
    }

    @Override
    protected void onPause() { // Called when activity goes into the background
        super.onPause(); // Calls superclass onPause
        sensorManager.unregisterListener(this); // Stops all sensor updates to save battery
    }

    @Override
    public void onSensorChanged(SensorEvent event) { // Called when sensor data changes
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // Checks if event is from accelerometer
            accXTextView.setText(String.format("X: %.2f", event.values[0])); // Updates X-axis text
            accYTextView.setText(String.format("Y: %.2f", event.values[1])); // Updates Y-axis text
            accZTextView.setText(String.format("Z: %.2f", event.values[2])); // Updates Z-axis text
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) { // Checks if event is from light sensor
            lightTextView.setText(String.format("Intensity: %.2f lx", event.values[0])); // Updates light intensity text
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) { // Checks if event is from proximity sensor
            proximityTextView.setText(String.format("Distance: %.2f cm", event.values[0])); // Updates proximity distance text
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { // Called when sensor accuracy changes
        // Not used, but required by SensorEventListener interface
    }
}
