package com.example.sensorreader;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorHelper {

    private final SensorManager sensorManager;

    public SensorHelper(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public Sensor getAccelerometer() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public Sensor getLightSensor() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    public Sensor getProximitySensor() {
        return sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }
}
