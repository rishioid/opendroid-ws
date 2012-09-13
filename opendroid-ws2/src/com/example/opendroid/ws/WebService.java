package com.example.opendroid.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.opendroid.ws.models.WSModel;
import com.google.gson.Gson;

/**
 * The Class WebService.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class WebService<T extends WSModel> {
	
	private ProgressDialog dialog;
	protected Context context;
	
	final static String TAG = "WebService";

	/** The Constant TYPE_POST. */
	public final static int TYPE_GET = 0, TYPE_POST = 1;

	/** The type. */
	int type = 0;

	/** The params. */
	Map<String, String> params;

	/** The access token needed. */
	private boolean accessTokenNeeded;

	/** The accesstoken. */
	private String accesstoken;

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	protected abstract String getURL();

	/**
	 * @author Rishi K Instantiates a new web service for request method type
	 *         (TYPE_GET or TYPE_POST).
	 * @param params
	 *            parameters that need to be passed in request
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 */
	protected WebService(Map<String, String> params, int type) {
		this.type = type;
		this.params = params;
	}

	/**
	 * @author Rishi K Instantiates a new web service and defaults to GET type.
	 * @param params
	 *            parameters that need to be passed in request
	 */
	protected WebService(Map<String, String> params) {
		type = TYPE_GET;
		this.params = params;
	}

	/**
	 * Instantiates a new web service.
	 * 
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 */
	protected WebService(int type) {
		this.type = type;
	}

	/**
	 * @author Rishi K Gets the mapper class.
	 * 
	 * @return the mapper class
	 */
	protected abstract Class<?> getMapperClass();

	/**
	 * @author Rishi K Gets the response in form of array of mapping class.
	 * 
	 * @return the response array
	 */
	@SuppressWarnings("unchecked")
	public T[] getResponseArray() {
		T[] response = null;
		InputStream source = fetchStream(getURL());
		
		/* InputStream src2 = fetchStream(getURL()); String myString; try {
		  myString = readInputStreamAsString(src2); Log.d(getClass(),
		  "RESULT : " + myString); } catch (IOException e) {
			  e.printStackTrace(); 
			  
		  }*/
		

		if (source != null) {
			Gson gson = new Gson();
			Reader reader = new InputStreamReader(source);

			response = (T[]) gson.fromJson(reader, getMapperClass());

			Log.d("TAG", "Response:  " + response);
		}
		return response;
	} // end callWebService()

	/**
	 * @author Rishi K Gets the response in form of object of mapping class.
	 * 
	 * @return the response object
	 */
	public T getResponseObject() {
		T response = null;
		InputStream source = fetchStream(getURL());
//		try {
//			String myString = readInputStreamAsString(source);
//			Log.d(getClass(), "RESULT : " + myString);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		if (source != null) {
			Gson gson = new Gson();
			Reader reader = new InputStreamReader(source);
			response = (T) gson.fromJson(reader, getMapperClass());

			Log.d("TAG", "Response:  " + response.toString());
		} else {
			Log.e(TAG, "Response found null !!");
		}
		return response;
	} // end callWebService()

	/**
	 * @author Rishi K Fetch stream.
	 * 
	 * @param url
	 *            the url
	 * @return the input stream
	 */
	private InputStream fetchStream(String url) {

		// String authentication = "admin:1234";
		// String encoding = Base64.encodeToString(authentication.getBytes(),
		// Base64.NO_WRAP);

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (params != null) {
			for (String key : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)
						+ ""));
			}
		}

		DefaultHttpClient client = new DefaultHttpClient();

		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				"admin" + ":" + "1234");
		client.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				credentials);

		// HttpUriRequest request = null;
		if (type == TYPE_GET) {
			Log.d(TAG, "IN GET");
			String paramString = URLEncodedUtils.format(nameValuePairs,
					HTTP.UTF_8);
			url += paramString;
			HttpGet request = new HttpGet(url);

			return fetchResponse(request, client);
		} else {

			Log.d(TAG, "IN POST");
			HttpPost request = new HttpPost(url);

			try {
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
				// request.setHeader("Authorization", "Basic " + encoding);
				return fetchResponse(request, client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	private InputStream fetchResponse(HttpUriRequest request,
			DefaultHttpClient client) {
		try {

			Log.d(TAG,
					("executing request " + request.getRequestLine()));
			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.d(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + request.getURI());
				return null;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		} catch (IOException e) {
			request.abort();
			// Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
		}

		return null;
	}

	/**
	 * Checks if is access token needed.
	 * 
	 * @return true, if is access token needed
	 */
	public boolean isAccessTokenNeeded() {
		return accessTokenNeeded;
	}

	/**
	 * Sets the access token needed.
	 * 
	 * @param accessTokenNeeded
	 *            the new access token needed
	 */
	public void setAccessTokenNeeded(boolean accessTokenNeeded) {
		this.accessTokenNeeded = accessTokenNeeded;
	}

	public static String readInputStreamAsString(InputStream in)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}



}
