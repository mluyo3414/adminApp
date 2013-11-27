package com.example.foodserveradmin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FaceDetection extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new myView(this));
	}

	private class myView extends View {

		private int imageWidth, imageHeight;
		private int numberOfFace = 5;
		private FaceDetector myFaceDetect;
		private FaceDetector.Face[] myFace;
		float myEyesDistance;
		int numberOfFaceDetected;

		Bitmap myBitmap;

		public myView(Context context) {
			super(context);
			
			Toast.makeText(getApplication(),
					"FaceDetection Start!", Toast.LENGTH_SHORT).show();

//			BitmapFactory.Options BitmapFactoryOptionsbfo = new BitmapFactory.Options();
//			BitmapFactoryOptionsbfo.inPreferredConfig = Bitmap.Config.RGB_565;
//			myBitmap = BitmapFactory.decodeResource(getResources(),
//					R.drawable.jennifer_lopez, BitmapFactoryOptionsbfo);
		
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.adminPictureFilePath, options);	
			
			imageWidth = myBitmap.getWidth();
			imageHeight = myBitmap.getHeight();
			myFace = new FaceDetector.Face[numberOfFace];

			myFaceDetect = new FaceDetector(imageWidth, imageHeight, numberOfFace);
			numberOfFaceDetected = myFaceDetect.findFaces(myBitmap, myFace);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			canvas.drawBitmap(myBitmap, 0, 0, null);

			Paint myPaint = new Paint();
			PointF myMidPoint;
			myPaint.setColor(Color.GREEN);
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setStrokeWidth(3);

			for (int i = 0; i < numberOfFaceDetected; i++) {
				Face face = myFace[i];
				myMidPoint = new PointF();
				face.getMidPoint(myMidPoint);
				myEyesDistance = face.eyesDistance();

				if (myFace[i].confidence() >= Face.CONFIDENCE_THRESHOLD) {
					canvas.drawRect((int) (myMidPoint.x - myEyesDistance * 2),
							(int) (myMidPoint.y - myEyesDistance * 2),
							(int) (myMidPoint.x + myEyesDistance * 2),
							(int) (myMidPoint.y + myEyesDistance * 2), myPaint);
				  Toast.makeText(getApplicationContext(),
							"Valid Admin!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}