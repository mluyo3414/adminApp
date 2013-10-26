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
 * @author
	Miguel S 
	This asynchronous class connects the admin with the server
 *         to retrieve all the orders
 */
public class Asyncserver extends AsyncTask<String, Void, String>
{

	protected String ipAndPort;
	// main activity to call back
	MainActivity activity_;
	static JSONObject jObj = null;
	JSONArray Jarray = null;

	/**
	 * 
	 * @param nextActivity
	 *            ConnectAsync constructor
	 */
	public Asyncserver( MainActivity nextActivity )
	{
		// main activity instance to start next activity
		activity_ = nextActivity;
		activity_.connectButton.setEnabled( false );
	}

	/**
	 * 
	 * @param IPAndPort
	 * @return response from server
	 * @throws Exception
	 */
	public String getInternetData( String IPAndPort ) throws Exception
	{
		BufferedReader in = null;
		String data = "";
		try
		{
			// setup http client
			HttpClient client = new DefaultHttpClient();
			// process data from
			URI website = new URI( "http://" + IPAndPort + "/admin" );
			// request using get method
			HttpGet request = new HttpGet( website );
			HttpResponse response = client.execute( request );
			// string using buffered reader
			// streamreader bytes into characters
			in =
					new BufferedReader( new InputStreamReader( response
							.getEntity().getContent() ) );
			StringBuffer sb = new StringBuffer( "" );
			String l = "";
			String newline = System.getProperty( "line.separator" );
			while ( (l = in.readLine()) != null )
			{
				sb.append( l + newline );
			}
			in.close();
			data = sb.toString();
			// returns responser from the server
			return (data);

		} finally
		{
			
			{
				try
				{
					in.close();
					return (data);

				} catch ( Exception e )
				{
					e.printStackTrace();

				}

			}

		}
	}

	@Override
	protected String doInBackground( String... params )
	{

		ipAndPort = params[0];

		// connecting to server
		String data = "";
		try
		{
			// response from the server
			data = getInternetData( ipAndPort );
		} catch ( Exception e )
		{
			
			return null;
		}

		return data;

	}


	@Override
	protected void onPostExecute( String fromParseData )
	{

		// creates an Arraylist so next activity can display
		// orders in a ListView

		ArrayList<HashMap<String, String>> returningArrayList;
		returningArrayList = parseData( fromParseData );

		Intent in = new Intent( activity_, FromServer1.class );

		in.putExtra( "Data", returningArrayList );
		

		activity_.startActivity( in );

	}

	/**
	 * 
	 * @param rawData
	 * @return ArrayList of Orders Parses data received from the server
	 */
	protected ArrayList<HashMap<String, String>> parseData( String rawData )
	{
		// if there are orders in the server we will receive
		// a JSON array...
		ArrayList<HashMap<String, String>> OrderArrayList =
				new ArrayList<HashMap<String, String>>();
		String theNewData = "{\"Orders\": " + rawData + "}";

		try
		{
			jObj = new JSONObject( theNewData );
		} catch ( JSONException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			// Getting Array of orders since there are multiple orders in the
			// json array
			Jarray = jObj.getJSONArray( "Orders" );

			// looping through All objects
			for( int i = 0; i < Jarray.length(); i++ )
			{
				JSONObject c = Jarray.getJSONObject( i );

				// Storing each json item in variable
				String Location = c.getString( "LOCATION" );
				String Name = c.getString( "NAME" );
				String Order = c.getString( "ORDER" );
				// storing individual order( one per hashmap)
				HashMap<String, String> map = new HashMap<String, String>();
				map.put( "LOCATION", Location );
				map.put( "NAME", Name );
				map.put( "ORDER", Order );
				// add each order to the list
				OrderArrayList.add( map );

			}

		} catch ( JSONException e )
		{
			e.printStackTrace();
		}
		// passes back arrayList to doInBackground
		return OrderArrayList;
	}

}
