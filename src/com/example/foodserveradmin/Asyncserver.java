package com.example.foodserveradmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.drm.DrmStore.Action;
import android.os.AsyncTask;
import android.text.Html;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author Matt Luckham
 * @author Jimmy Dagres
 * 
 *         This asynchronous class connects the admin with the server to
 *         retrieve all the orders
 */
public class Asyncserver extends AsyncTask<String, Void, String> {

	protected String ipAndPort;
	SettingsActivity activity_;
	static JSONObject jObj = null;
	JSONArray Jarray = null;

	/**
	 * Constructor for the AsyncServer class.
	 * 
	 * @param nextActivity
	 *          ConnectAsync constructor
	 */
	public Asyncserver(SettingsActivity nextActivity) {
		// main activity instance to start next activity
		activity_ = nextActivity;
	}

	/**
	 * Gets the order data from the server to populate the order activity.
	 * 
	 * @param IPAndPort
	 * @return response from server
	 * @throws Exception
	 */
	public String getInternetData(String IPAndPort) throws Exception {
		BufferedReader in = null;
		String data = "";
		try {
			// setup http client
			HttpClient client = new DefaultHttpClient();
			// process data from
			URI website = new URI("http://" + IPAndPort + "/admin");
			// request using get method
			HttpGet request = new HttpGet(website);
			HttpResponse response = client.execute(request);
			// string using buffered reader
			// streamreader bytes into characters
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String newline = System.getProperty("line.separator");
			while ((l = in.readLine()) != null) {
				sb.append(l + newline);
			}
			in.close();
			data = sb.toString();
			// returns responser from the server
			return (data);
		}
		finally {
			{
				try {
					in.close();
					return (data);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("IO Exception on the line 93.");
				}
			}
		}
	}

	/**
	 * Attempts to connect to the server.
	 * 
	 * @param params
	 *          The IP and port for the server.
	 * @return The response from the server.
	 */
	@Override
	protected String doInBackground(String... params) {
		ipAndPort = params[0];
		// connecting to server
		String data = "";
		try {
			// response from the server
			data = getInternetData(ipAndPort);
		}
		catch (Exception e) {
			System.out.println("Configuration error on line 113.");
			return null;
		}
		return data;
	}

	/**
	 * Creates an array list to display the orders from the server in the order
	 * activity.
	 */
	@Override
	protected void onPostExecute(String fromParseData) {
		ArrayList<HashMap<String, String>> returningArrayList;
		returningArrayList = parseData(fromParseData);
		Intent in = new Intent(activity_, OrdersListing.class);
		in.putExtra("Data", returningArrayList);
		activity_.startActivity(in);
	}

	/**
	 * Parses data received from the server.
	 * 
	 * @param rawData
	 *          from the server.
	 * @return ArrayList of Orders
	 */
	protected ArrayList<HashMap<String, String>> parseData(String rawData) {
		// If there are orders in the server we will receive
		// a JSON array...
		ArrayList<HashMap<String, String>> OrderArrayList = new ArrayList<HashMap<String, String>>();
		String theNewData = "{\"Orders\": " + rawData + "}";

		try {
			jObj = new JSONObject(theNewData);
		}
		catch (JSONException e1) {
			System.out.println("Error creating JSONOject on line 144.");
			e1.printStackTrace();
		}
		try {
			// Getting Array of orders since there are multiple orders in the
			// JSON array
			Jarray = jObj.getJSONArray("Orders");

			// looping through All objects
			for (int i = 0; i < Jarray.length(); i++) {
				JSONObject c = Jarray.getJSONObject(i);

				System.out.println("Order as JSONObject: " + c);

				// Storing each JSON item in variable
				String Location = c.getString("LOCATION");
				String Name = c.getString("NAME");
				String Order = c.getString("ORDER");
				// storing individual order( one per hashmap)
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("LOCATION", Location);
				map.put("NAME", Name);
				map.put("ORDER", Order);
				// Add each order to the list.
				OrderArrayList.add(map);
			}
		}
		catch (JSONException e) {
			System.out.println("Error creating multiple JSON Objects at line 172.");
			e.printStackTrace();
		}
		// Passes back arrayList to doInBackground.
		return OrderArrayList;
	}
}