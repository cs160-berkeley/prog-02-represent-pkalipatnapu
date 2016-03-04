package com.prad.cs160.represent;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.util.Random;

public class ShakeDetectorService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
        Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Minimum acceleration needed to count as a shake movement
    private static final int MIN_SHAKE_ACCELERATION = 50;
    
    // Minimum number of movements to register a shake
    private static final int MIN_MOVEMENTS = 4;
    
    // Maximum time (in milliseconds) for the whole shake to occur
    private static final int MAX_SHAKE_DURATION = 1000;

	// Indexes for x, y, and z values
	private static final int X = 0;
	private static final int Y = 1;
	private static final int Z = 2;

	// Start time for the shake detection
	long startTime = 0;
	
	// Counter for shake movements
	int moveCount = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This method will be called when the accelerometer detects a change.
        // Check if event qualifies, if it does do the actions.
    	if (eventQualifies(event)) {
            // Send message to phone with the zip code.
            Intent sendIntent = new Intent(this.getBaseContext(), WatchToPhoneService.class);
            Random r = new Random();
            int zip = r.nextInt(49999)*2; // TODO(prad): Delete this. Using only even zip for demo.
            sendIntent.putExtra(WatchToPhoneService.ZIP_CODE, zip);
            startService(sendIntent);

            // Start Main Activity using the same zip.
            Intent intent = new Intent(this.getBaseContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MainActivity.ZIP_CODE, zip);
            startActivity(intent);
        }
    }

    private boolean eventQualifies(SensorEvent event){
        long now = System.currentTimeMillis();

        // Set the startTime if it was reset to zero
        long elapsedTime = now - startTime;
        if (elapsedTime < MAX_SHAKE_DURATION) {
            // Still part of the same shake.
            return false;
        }
        // Otherwise shake starts now.
        startTime = now;
        // Get the max linear acceleration in any direction
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration(event);
        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            // Keep track of all the movements
            moveCount++;
            // Check if enough movements have been made to qualify as a shake
            if (moveCount > MIN_MOVEMENTS) {
                // Reset for the next one!
                resetShakeDetection();
                // It's a shake! Notify the listener.
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
  	    // Intentionally blank
    }

    private float getMaxCurrentLinearAcceleration(SensorEvent event) {
    	// Start by setting the value to the x value
    	float maxLinearAcceleration = event.values[X];
    	
    	// Check if the y value is greater
        if (event.values[Y] > maxLinearAcceleration) {
        	maxLinearAcceleration = event.values[Y];
        }
        
        // Check if the z value is greater
        if (event.values[Z] > maxLinearAcceleration) {
        	maxLinearAcceleration = event.values[Z];
        }
        
        // Return the greatest value
        return maxLinearAcceleration;
    }
    
    private void resetShakeDetection() {
    	startTime = 0;
    	moveCount = 0;
    }
}
