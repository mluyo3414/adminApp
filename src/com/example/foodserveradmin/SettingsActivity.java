package com.example.foodserveradmin;

import java.io.File;
import java.util.List;
import java.util.jar.Attributes.Name;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author James Dagres
 * @author Matt Luckham
 * 
 *         The administrator logins in by taking a picture which is checked to
 *         make sure it is a valid person's face. The admin is directed to the
 *         orders activity which displays all the current orders on the server.
 * 
 */

public class SettingsActivity extends Activity {

	protected EditText nameEdit;
	protected static String name;
	public static String IPandPort;
	protected Asyncserver myActivity;
	protected TextView status;
	protected Button takePic;
	private File sdImageMainDirectory;

	/**
	 * Setups the layouts for the Settings Activity. Starts listening for the
	 * admin to take a picture to login to the application.
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		takePic = (Button) findViewById(R.id.signIn);
		status = (TextView) findViewById(R.id.connectionStatus);
		nameEdit = (EditText) findViewById(R.id.admin_name);

		// Admin signs in if valid face is detected from picture.
		takeAPicture();
	}

	/**
	 * Inflate the menu; this adds items to the action bar if it is present.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Once picture is taken, admin can try to connect to the server.
	 */
	public void loginToServer() {
		// Connect to the server.
		myActivity = new Asyncserver(SettingsActivity.this);
		String stringPort = "8080";
		String stringIP = "54.201.86.103";
		IPandPort = stringIP + ":" + stringPort;
		myActivity.execute(IPandPort);
	}

	/**
	 * Starts the camera, admin must take the picture in order to access the
	 * server.
	 */
	public void takeAPicture() {

		takePic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create a new file for the picture.
				File root = new File(Environment.getExternalStorageDirectory()
						+ File.separator + "myDir" + File.separator);
				root.mkdirs();
				sdImageMainDirectory = new File(root, "AdminName");

				// Update administrator's picture.
				if (sdImageMainDirectory.exists()) {
					sdImageMainDirectory.delete();
					sdImageMainDirectory = new File(root, "Admin.png");
				}

				Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				startActivityForResult(intent, 0);
			}
		});
	}

	/**
	 * Check for a valid admin picture taken then start listening for
	 * administrator login button press.
	 * 
	 * @param requestCode
	 *          The request code for a valid picture.
	 * @param resultCode
	 *          The result code for a valid picture.
	 * @param data
	 *          The intent containing the administrators picture.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// Check if picture file was created.
		if (requestCode == 0 && resultCode == RESULT_OK
				&& sdImageMainDirectory.exists()) {
			// Configure the image for face detection.
			BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
			BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap myBitmap = BitmapFactory.decodeFile(
					sdImageMainDirectory.getAbsolutePath(), BitmapFactoryOptionsbfo);
			// Find the face within the picture.
			FaceDetector.Face[] myFace = new FaceDetector.Face[1];
			FaceDetector myFaceDetect = new FaceDetector(myBitmap.getWidth(),
					myBitmap.getHeight(), 1);
			myFaceDetect.findFaces(myBitmap, myFace);
			// Check if the face is valid.
			if (myFace[0] != null && myFace[0].confidence() >= .3) {
				Toast.makeText(getApplicationContext(), "Valid Admin!",
						Toast.LENGTH_SHORT).show();
				loginToServer();
			}
			else {
				Toast.makeText(getApplicationContext(), "Invalid Admin!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
