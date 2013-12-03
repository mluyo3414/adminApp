package com.example.foodserveradmin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author James Dagres
 * @author Matt Luckham
 * 
 *         Updates the OrdersListing activity with the new orders from the
 *         server.
 */
public class UpdateOrderListing extends AsyncTask<String, Void, String> {

	private OrdersListing orderActivity;
	private static JSONObject jObj = null;
	private JSONArray Jarray = null;

	/**
	 * Constructor for the UpdateOrderListing class.
	 * 
	 * @param orderListingActivity
	 *          UpdateOrderListing constructor
	 */
	public UpdateOrderListing(OrdersListing nextActivity) {
		orderActivity = nextActivity;
	}

	/**
	 * Retrieves the new orders from the server to update the order listing
	 * locally.
	 * 
	 * @param IPAndPort
	 *          The IP and Port for the server.
	 * @return data The data from the server.
	 */
	public String getInternetData(String IPAndPort) throws Exception {
		BufferedReader in = null;
		String data = "";
		try {
			HttpClient client = new DefaultHttpClient();
			URI website = new URI("http://" + IPAndPort + "/admin");
			HttpGet request = new HttpGet(website);
			HttpResponse response = client.execute(request);

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

			// returns response from the server
			return (data);
		}
		finally {
			{
				try {
					in.close();
					return (data);
				}
				catch (Exception e) {
					System.out
							.println("Configuration error in getInternetData class in UpdateOrderListing.java");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Attempts to connect to the server to update the order listing.
	 * 
	 * @param params
	 *          The IP and Port information to connect to the server.
	 * @return data The data from the server.
	 */
	@Override
	protected String doInBackground(String... params) {

		String ipAndPort = params[0];
		String data = "";

		try {
			// Response from the server
			data = getInternetData(ipAndPort);
		}
		catch (Exception e) {
			System.out
					.println("Error while getting a response from the server in doInBackground.");
			return null;
		}
		return data;
	}

	/**
	 * Returns to the OrdersListingActivity with the updated order list.
	 */
	@Override
	protected void onPostExecute(String fromParseData) {
		ArrayList<HashMap<String, String>> returningArrayList;
		returningArrayList = parseData(fromParseData);

		Intent in = new Intent(orderActivity, OrdersListing.class);
		in.putExtra("Data", returningArrayList);
		orderActivity.startActivity(in);
	}

	/**
	 * Receiving order data from the server.
	 * 
	 * @param rawData
	 *          Raw data received from the server.
	 * @return OrderArrayList Parsed data received from the server.
	 */
	protected ArrayList<HashMap<String, String>> parseData(String rawData) {
		// if there are orders in the server we will receive
		// a JSON array...
		ArrayList<HashMap<String, String>> OrderArrayList = new ArrayList<HashMap<String, String>>();
		String theNewData = "{\"Orders\": " + rawData + "}";

		try {
			jObj = new JSONObject(theNewData);
		}
		catch (JSONException e1) {
			System.out.println("Error creating a JSONObject in parseData method.");
			e1.printStackTrace();
		}
		try {
			// Getting Array of orders from the server.
			Jarray = jObj.getJSONArray("Orders");

			for (int i = 0; i < Jarray.length(); i++) {

				JSONObject c = Jarray.getJSONObject(i);

				String Phone = c.getString("PHONE");
				String Time = c.getString("TIME");
				String Name = c.getString("NAME");
				String Confirmation = c.getString("CONFIRMATION");
				String Order = c.getString("ORDER");
				String Total = c.getString("TOTAL");

				Order = Order.replace("[", "");
				Order = Order.replace("]", "");

				// Check for multiple orders per client.
				if (Order.contains(",")) {
					String[] str = Order.split(",");

					Order = "";
					String newLine = System.getProperty("line.separator");
					StringBuffer sb = new StringBuffer("");

					for (int j = 0; j < str.length; j++) {
						String temp = str[j].replaceAll("^\\s+|\\s+$", "");
						if (str.length - 1 > j) {
							sb.append(temp + newLine);
						}
						else {
							sb.append(temp);
						}
					}
					Order = sb.toString();
				}

				// Configure each order to display in the list view.
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("PHONE", "Phone Number: " + Phone);
				map.put("TIME", "Order Time: " + Time);
				map.put("NAME", "Client: " + Name);
				map.put("CONFIRMATION", "Confirmation #: " + Confirmation);
				map.put("ORDER", Order);
				map.put("TOTAL", "Total: $" + Total);

				OrderArrayList.add(map);
			}
		}
		catch (JSONException e) {
			System.out
					.println("JSONExcpetion error while creating a JSONObjec in parseData.");
			e.printStackTrace();
		}

		return OrderArrayList;
	}
}
