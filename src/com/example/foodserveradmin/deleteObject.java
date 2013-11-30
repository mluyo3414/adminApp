package com.example.foodserveradmin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
/**
 * 
 * @author Miguel S
 * 
 * This activity deletes one client from the server when the order is ready
 *
 */
public class deleteObject extends AsyncTask<String, Void, String>
{

	String Location, Name, Order;
	OrdersListing activity_;
	public deleteObject( OrdersListing nextActivity )
	{
		// main activity instance to start next activity
		activity_ = nextActivity;
	}
/**
 * 
 * @param deleteOrder
 * @return
 * @throws URISyntaxException
 * @throws ClientProtocolException
 * @throws IOException
 * 
 * This activity posts which order should be deleted from the server by posting the nam
 * and product
 */
	public String deleteOrder( String deleteOrder ) throws URISyntaxException,
			ClientProtocolException, IOException
	{
		// parsing received information to post into server and delete orders
		Location= deleteOrder.substring( deleteOrder.indexOf( "=" )+1, deleteOrder.indexOf( "," ) );
		 Name = deleteOrder.substring( deleteOrder.indexOf("NAME")+5, deleteOrder.indexOf( "}" ) );
		 
		 Order = deleteOrder.substring( deleteOrder.indexOf( "ORDER" )+6,deleteOrder.indexOf("NAME") );
		 Order = Order.replaceAll( ",","" );
		 
		 
		HttpClient client = new DefaultHttpClient();
		HttpPost post =
				new HttpPost( "http://" + MainActivity.IPandPort + "/admin" );
		String data = "";
		try
		{
			// three parameters are posted to the server
			List<NameValuePair> nameValuePairs =
					new ArrayList<NameValuePair>( 1 );
			nameValuePairs.add( new BasicNameValuePair( "username", Name ) );
			nameValuePairs.add( new BasicNameValuePair( "order", Order ) );
			nameValuePairs.add( new BasicNameValuePair( "location", Location ) );
			post.setEntity( new UrlEncodedFormEntity( nameValuePairs ) );

			HttpResponse response = client.execute( post );
			BufferedReader rd =
					new BufferedReader( new InputStreamReader( response
							.getEntity().getContent() ) );
			// read from server
			String line = "";
			StringBuffer sb = new StringBuffer( "" );
			String newline = System.getProperty( "line.separator" );
			while ( (line = rd.readLine()) != null )
			{
				sb.append( line + newline );

			}
			rd.close();
			data = sb.toString();
			// get order status
			return (data);
		} catch ( IOException e )
		{
			data = "ERROR FROM SERVER";
			e.printStackTrace();
		}
		return data;

	}

	@Override
	protected String doInBackground( String... arg0 )
	{
		// TODO Auto-generated method stub
		String orderToBeDeleted = "";
		try
		{
			orderToBeDeleted = deleteOrder( arg0[0] );
		} catch ( ClientProtocolException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( URISyntaxException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orderToBeDeleted;
	}

	@Override
	protected void onPostExecute( String fromParseData )
	{
		

	}

}