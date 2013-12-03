package com.example.foodserveradmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.example.foodserveradmin.ShakeEventListener.OnShakeListener;

/**
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author James Dagres
 * @author Matt Luckham
 * 
 *         This activity displays the response from the server. A number of
 *         orders are displayed if available
 */
public class OrdersListing extends Activity {

	TextView updateInfo;
	Intent in;
	Bundle b;
	ListView orders;
	ArrayList<HashMap<String, String>> orderArrayList;
	DeleteObject deletingObject;
	// Accelerometer variable
	private SensorManager sensorManager_;
	private Sensor accelerometer_;
	private ShakeEventListener shakeMeter_;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_from_server1);
		orders = (ListView) findViewById(R.id.orderListView);

		// getting information from previous activity
		in = getIntent();
		b = in.getExtras();
		orderArrayList = null;
		// starts the ACL
		sensorManager_ = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shakeMeter_ = new ShakeEventListener();
		// if the device is shaken, display a toast
		shakeMeter_.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake(int count) {
				handleShakeEvent(count);
			}

			private void handleShakeEvent(int count) {
				Toast.makeText(getApplicationContext(),
						"Hello " + SettingsActivity.name, Toast.LENGTH_SHORT).show();
			}
		});
		// getting information from previous activity
		if (b != null) {
			// using "Data" as the key
			orderArrayList = (ArrayList<HashMap<String, String>>) b.get("Data");
			// updates GUI with orders from the server if orders available
			updateGUI(orderArrayList);
		}
		else {
			Intent in = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(in);
		}
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
	 * Disables the back button for the Order activity to prevent admin from going
	 * back to previous order listing.
	 */
	@Override
	public void onBackPressed() {
	}

	/**
	 * Triggers new activity once the order is selected a box to delete that order
	 * will be displayed and then we will post that information to the server and
	 * also delete it from our app Array list containing the orders.
	 * 
	 * @param dataFromServer
	 *          Updates the Textview with the new information
	 */
	public void updateGUI(ArrayList<HashMap<String, String>> dataFromServer) {

		orderArrayList = dataFromServer;
		// ListView with each order containing all the order information for each
		// client.
		final ListAdapter adapter = new SimpleAdapter(this, orderArrayList,
				R.layout.activity_each_order, new String[] { "PHONE", "TIME", "NAME",
						"CONFIRMATION", "ORDER", "TOTAL" }, new int[] { R.id.Phone,
						R.id.Time, R.id.Name, R.id.Confirmation, R.id.Order, R.id.Total });
		orders.setAdapter(adapter);

		orders.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				AlertDialog.Builder adb = new AlertDialog.Builder(OrdersListing.this);
				// parsing name
				String[] names = orderArrayList.get(position).toString().split("NAME=");
				// String[] names =
				// orderArrayList.get(position).toString().split("NAME=");
				String[] temp = names[1].split(",");
				String name = temp[0].substring(8);

				adb.setTitle("Order Ready?");
				adb.setMessage("Are you sure you want to delete " + name + "'s order?");
				// alert dialog options
				final int positionToRemove = position;
				adb.setNegativeButton("Cancel", null);
				adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// if ok then start new async to post
						deletingObject = new DeleteObject(OrdersListing.this);
						((BaseAdapter) adapter).notifyDataSetChanged();
						SendTextNotification sendSMSMessage = new SendTextNotification(
								OrdersListing.this);
						sendSMSMessage.execute(orderArrayList.get(positionToRemove)
								.toString());
						deletingObject.execute(orderArrayList.remove(positionToRemove)
								.toString());
					}
				});
				adb.show();
			}
		});
	}

	/**
	 * Registers the Session Manager Listener onResume
	 */
	@Override
	public void onResume() {
		super.onResume();
		sensorManager_.registerListener(shakeMeter_, accelerometer_,
				SensorManager.SENSOR_DELAY_UI);
	}

	/**
	 * Unregister the Sensor Manager onPause
	 */
	@Override
	public void onPause() {
		sensorManager_.unregisterListener(shakeMeter_);
		super.onPause();
	}

	/**
	 * Overriding the onDestroy method for the accelerometer.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Checks if any of the refresh action is selected then responds. Refresh -
	 * Updates the order listing with the new orders on the server
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Toast.makeText(this, "Server Updated!", Toast.LENGTH_SHORT).show();
			// Refresh the orders in the order activity.
			UpdateOrderListing updateOrder = new UpdateOrderListing(
					OrdersListing.this);
			updateOrder.execute(SettingsActivity.IPandPort);
			break;
		default:
			break;
		}
		return true;
	}
}
