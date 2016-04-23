package th.in.ffc.googlemap.directions;
/*package th.in.ffc.googlemap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;

public class DirectionXML extends AsyncTask<String, String, String> {
	private HttpPost httpPost;

	public DirectionXML() {

	}

	@Override
	protected String doInBackground(String... params) {
		String url = "http://maps.googleapis.com/maps/api/directions/json";
		String start = "?origin=" + params[0];
		String end = "&destination=" + params[1];
		String mode = "&sensor=false&units=metric&mode=driving";
		String addr = url + start + end + mode;
		StringBuilder str = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		httpPost = new HttpPost(addr);
		List<NameValuePair> entity = new ArrayList<NameValuePair>();
		httpPost = new HttpPost(addr);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(entity));
			HttpResponse response = client.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			Log.d("TEST", "status code : " + statusCode);
			if (statusCode == 200) { // Status OK
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					str.append(line);
				}
			} else {
				Log.e("Log", "Failed to download result..");
			}
		} catch (ClientProtocolException e) {
			Log.d("TEST", "ERROR ClientProtocol " + e.toString());
		} catch (IOException e) {
			Log.d("TEST", "ERROR IOException " + e.toString());
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httpPost = new HttpPost(addr);
		HttpResponse response = httpClient.execute(httpPost, localContext);
		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			str.append(line);
		}
		
		return str.toString();

	}

}
*/