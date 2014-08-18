package com.warriormill.warriorengine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.content.Context;

public class WarriorEngine extends Activity implements SensorEventListener{
	
	static SensorManager m_sensorManager;
   	static float []m_lastMagFields;
   	static float []m_lastAccels;
   	private static float[] m_rotationMatrix = new float[16];
   	private static float[] m_remappedR = new float[16];
   	private static float[] m_orientation = new float[4];
    	
   	/* fix random noise by averaging tilt values */
   	final static int AVERAGE_BUFFER = 30;
   	static float []m_prevPitch = new float[AVERAGE_BUFFER];
   	static float m_lastPitch = 0.f;
   	static float m_lastYaw = 0.f;
   	/* current index int m_prevEasts */
   	static int m_pitchIndex = 0;
 
   	static float []m_prevRoll = new float[AVERAGE_BUFFER];
   	static float m_lastRoll = 0.f;
   	/* current index into m_prevTilts */
   	static int m_rollIndex = 0;
 
   	/* center of the rotation */
   	static private float m_tiltCentreX = 0.f;
   	static private float m_tiltCentreY = 0.f;
   	static private float m_tiltCentreZ = 0.f;
    	
   	private void registerListeners(){
   		m_sensorManager.registerListener(this, 
   			m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
   			SensorManager.SENSOR_DELAY_GAME);
       	m_sensorManager.registerListener(this,
       		m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
       		SensorManager.SENSOR_DELAY_GAME);
       	m_sensorManager.registerListener(this,
       		m_sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
       		SensorManager.SENSOR_DELAY_GAME);
   	}
    	
   	private void unregisterListeners(){
   		m_sensorManager.unregisterListener(this);
   	}
 
   	@Override
   	public void onAccuracyChanged(Sensor sensor, int accuracy) {
   	}
 
   	@Override
   	public void onSensorChanged(SensorEvent event) {
       	switch (event.sensor.getType()){
       		case Sensor.TYPE_ACCELEROMETER:
       			accel(event);
       			break;
       		case Sensor.TYPE_MAGNETIC_FIELD:
           		mag(event);
           		break;
           	case Sensor.TYPE_GAME_ROTATION_VECTOR:
           		//rot(event);
           		break;
           	default:
           		Log.e("error", "unknown event type");
        }
   	}
 
   	private void accel(SensorEvent event) {
       	if (m_lastAccels == null) {
           	m_lastAccels = new float[3];
       	}
 
       	System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
   	}
 
   	private void mag(SensorEvent event) {
       	if (m_lastMagFields == null) {
           	m_lastMagFields = new float[3];
       	}
 
       	System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);
   	}
 
   	static Filter [] m_filters = { new Filter(), new Filter(), new Filter() };
   	private static class Filter {
       	static final int AVERAGE_BUFFER = 10;
       	float []m_arr = new float[AVERAGE_BUFFER];
       	int m_idx = 0;
       	public float append(float val) {
           	m_arr[m_idx] = val;
           	m_idx++;
           	if (m_idx == AVERAGE_BUFFER)
               	m_idx = 0;
           	return avg();
       	}
       	public float avg() {
           	float sum = 0;
           	for (float x: m_arr)
               	sum += x;
           	return sum / AVERAGE_BUFFER;
       	}
 
   	}
 
   	public static float computeAngle() {
   		if(m_lastMagFields == null || m_lastAccels == null) { return 0; }
       	if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastAccels, m_lastMagFields)) {
           	SensorManager.getOrientation(m_rotationMatrix, m_orientation);
 
            /* 1 radian = 57.2957795 degrees */
            /* [0] : yaw, rotation around z axis
            * [1] : pitch, rotation around x axis
            * [2] : roll, rotation around y axis */
        	float yaw = (float) Math.toDegrees(m_orientation[0]);
           	float pitch = (float) Math.toDegrees(m_orientation[1]);
           	float roll = (float) Math.toDegrees(m_orientation[2]);
 
           	m_lastYaw = m_filters[0].append(yaw);
           	m_lastPitch = m_filters[1].append(pitch);
           	m_lastRoll = m_filters[2].append(roll);
           	
           	return m_lastPitch;
       	}
       	return 0;
    }

    /** Called when the activity is first created. */
    private GameEngineView stv;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        stv = new GameEngineView(this);
        setContentView(stv);
        stv.requestFocus();
        stv.setKeepScreenOn(true);
    }
    
    
	@Override
	public void onResume(){
    	m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    	registerListeners();
    	super.onResume();
    }
    	
    @Override
	public void onPause(){
		unregisterListeners();
		super.onPause();
	}
}