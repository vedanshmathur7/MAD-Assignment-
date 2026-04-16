package com.example.sensorreader; // Defines the package name

import android.hardware.Sensor; // Imports Sensor class
import android.hardware.SensorManager; // Imports SensorManager class

public class SensorHelper { // Helper class to simplify sensor retrieval

    private final SensorManager sensorManager; // Stores the sensor manager instance

    public SensorHelper(SensorManager sensorManager) { // Constructor taking SensorManager as input
        this.sensorManager = sensorManager; // Assigns the provided manager to the local field
    }

    public Sensor getAccelerometer() { // Method to get the accelerometer sensor
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Returns the default accelerometer
    }

    public Sensor getLightSensor() { // Method to get the light sensor
        return sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // Returns the default light sensor
    }

    public Sensor getProximitySensor() { // Method to get the proximity sensor
        return sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY); // Returns the default proximity sensor
    }
}
