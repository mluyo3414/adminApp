package com.example.foodserveradmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import android.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;

/**
 * 
 * @author Miguel Suarez
 * @author Carl Barbee
 * @author James Dagres
 * @author Matt Luckham
 * 
 *         This activity deletes one client from the server when the order is
 *         ready.
 * 
 */
public class SendTextNotification extends AsyncTask<String, Void, String> {

	String Phone, Time, Name, Order, Total, Confirmation;
	OrdersListing activity_;

	/**
	 * The constructor for the deletObject class.
	 * 
	 * @param nextActivity
	 *          main activity instance to start next activity
	 */
	public SendTextNotification(OrdersListing nextActivity) {
		activity_ = nextActivity;
	}

	/**
	 * This activity posts which order should be deleted from the server by
	 * posting the name and product
	 * 
	 * @param deleteOrder
	 * @return String
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String deleteOrder(String deleteOrder) throws URISyntaxException,
			ClientProtocolException, IOException {

		String newline = System.getProperty("line.separator");

		// Parsing order information to get the phone number.
		String Receiver = deleteOrder.substring(
				deleteOrder.indexOf("Phone Number: ") + 14,
				deleteOrder.indexOf(", TIME"));
		Name = deleteOrder.substring(deleteOrder.indexOf("Client: ") + 8,
				deleteOrder.indexOf(", CONFIRMATION="));
		Confirmation = deleteOrder.substring(deleteOrder
				.indexOf("Confirmation #: ") + 16);
		Confirmation = Confirmation.replace("}", "");
		
		// Set-ups the contact information to send the SMS message.
		// Twilio phone number.
		String Sender = "17572737857";
		String BodyMessage = Name + " your order is ready!" + newline
				+ "Confirmation: " + Confirmation + newline + "FoodNow Team";
		
		String userName = "AC04ea0cbe7c68c5a82bef5f55886c26ab";

		// TODO Maybe need {}'s ??
		String password = "7b980637738dc8d43d69f11a3d4f716e";

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"https://api.twilio.com/2010-04-01/Accounts/AC04ea0cbe7c68c5a82bef5f55886c26ab/SMS/Messages.xml");

		String encoding = "Basic "
				+ Base64.encodeToString((userName + ":" + password).getBytes(),
						Base64.URL_SAFE | Base64.NO_WRAP);
		post.setHeader("Authorization", encoding);

		String data = "";

		try {
			// three parameters are posted to the server
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("To", Receiver));
			nameValuePairs.add(new BasicNameValuePair("From", Sender));
			nameValuePairs.add(new BasicNameValuePair("Body", BodyMessage));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			// read from server
			String line = "";
			StringBuffer sb = new StringBuffer("");

			while ((line = rd.readLine()) != null) {
				sb.append(line + newline);
			}

			rd.close();
			data = sb.toString();
			// get order status
			return (data);
		}
		catch (IOException e) {
			data = "ERROR FROM SERVER";
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Sends a text message to the Twilio database to notify the client that their
	 * order is ready.
	 * 
	 * @param arg0
	 *          Order that is ready.
	 * @return OrderToBeDeleted The order that was deleted.
	 */
	@Override
	protected String doInBackground(String... arg0) {
		String orderToBeDeleted = "";
		try {
			orderToBeDeleted = deleteOrder(arg0[0]);
		}
		catch (ClientProtocolException e) {
			System.out.println("Error in HTTP protocol: Line 114 ");
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			System.out
					.println("Error: Some information could not be parsed when creating a URI: Line 118 ");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out
					.println("Error: Target failed to receive a valid HTTP response: Line 122");
			e.printStackTrace();
		}

		return orderToBeDeleted;
	}
}