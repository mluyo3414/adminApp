package com.example.foodserveradmin;

import com.example.foodserveradmin.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodserveradmin.ShakeEventListener.OnShakeListener;

/**
 * 
 * @author Miguel S 
 * This is the first Activity where the admin inputs the IP and
 * port (admin app).
 * 
 * 
 */
public class MainActivity extends Activity
{
	EditText ipAddress;
	EditText portNumber;
	EditText nameEdit;
	Button connectButton;
	String stringIP;
	String stringPort;
	static String IPandPort;
	static String name;
	Asyncserver myActivity;
	TextView status;
	Button takePic;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// starts the layout objects
		ipAddress = (EditText) findViewById( R.id.IpAddress );
		portNumber = (EditText) findViewById( R.id.IpPort );
		connectButton = (Button) findViewById( R.id.connectButton );
		takePic = (Button) findViewById( R.id.signIn );
		status = (TextView) findViewById( R.id.connectionStatus );
		connectButton.setEnabled( false );
		nameEdit = (EditText) findViewById( R.id.admin_name );
		// security to sign in with a picture...
		// user needs to take a picture ( future face recognition) to access
		// the server

		buttonPressed2();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	/**
	 * Once picture is taken, admin can try to connect to the server.
	 */
	public void buttonPressed()
	{
		//allows connection to server
		connectButton.setEnabled( true );
		//admin can't log in twice
		takePic.setEnabled( false );

		connectButton.setOnClickListener( new View.OnClickListener()
		{

			@SuppressLint( "NewApi" )
			public void onClick( View view )
			{
				if ( !(ipAddress.getText().toString().isEmpty())
						&& !(portNumber.getText().toString().isEmpty()) )

				{
					// get values from Text edit and tries to connect to the
					// server
					myActivity = new Asyncserver( MainActivity.this );
					stringPort = portNumber.getText().toString();
					stringIP = ipAddress.getText().toString();
					IPandPort = stringIP + ":" + stringPort;
					// start AsyncTask
					myActivity.execute( IPandPort );
				}
				else
				{
					//if server parameters are empty
					Toast.makeText( getApplicationContext(),

					"Please enter server information ", Toast.LENGTH_SHORT )
							.show();
					buttonPressed();
				}
			}
		} );

	}

	/**
	 * Starts the camera, admin must take the picture in order to access the
	 * server
	 */
	public void buttonPressed2()
	{
		connectButton.setEnabled( false );

		takePic.setOnClickListener( new View.OnClickListener()
		{

			@Override
			public void onClick( View v )
			{
				name = nameEdit.getText().toString();
				Intent intent =
						new Intent(
								android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
				startActivityForResult( intent, 0 );

			}
		} );

	}


	/**
	 * Camera activity: if valid picture is taken then go to buttonpressed() to
	 * enable connection
	 */
	protected void onActivityResult( int requestCode, int resultCode,
			Intent data )
	{
		connectButton.setEnabled( false );

		if ( requestCode == 0 && resultCode == RESULT_OK )
		{

			Bitmap image = (Bitmap) data.getExtras().get( "data" );
			buttonPressed();
		}

	}
}
