package com.example.foodserveradmin;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 
 * @author Miguel S
 * 
 *         This class contains information to activate the Accelerometer.
 * 
 */
public class ShakeEventListener implements SensorEventListener
{
	private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
	private static final int SHAKE_SLOPE_TIME_MS = 500;
	// resets after 3 secs
	private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;

	private OnShakeListener mListener;
	private long mShakeTimestamp;
	private int mShakeCount;

	public void setOnShakeListener( OnShakeListener onShakeListener )
	{
		this.mListener = onShakeListener;
	}

	public interface OnShakeListener
	{

		public void onShake( int count );
	}

	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy )
	{
	}

	/**
	 * Detects the movement of the ACL.
	 */
	@Override
	public void onSensorChanged( SensorEvent event )
	{

		if ( mListener != null )
		{
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			float gX = x / SensorManager.GRAVITY_EARTH;
			float gY = y / SensorManager.GRAVITY_EARTH;
			float gZ = z / SensorManager.GRAVITY_EARTH;

			float gForce =
					(float) java.lang.Math.sqrt( gX * gX + gY * gY + gZ * gZ );

			if ( gForce > SHAKE_THRESHOLD_GRAVITY )
			{
				final long now = System.currentTimeMillis();
				if ( mShakeTimestamp + SHAKE_SLOPE_TIME_MS > now )
				{
					return;
				}

				if ( mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now )
				{
					mShakeCount = 0;
				}

				mShakeTimestamp = now;
				// adding seconds to the counter
				mShakeCount++;

				mListener.onShake( mShakeCount );
			}
		}
	}
}
