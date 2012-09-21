package com.opendroid.ws;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.opendroid.ws.models.WSModel;

public abstract class ExchangeWebService<T extends WSModel> {

	WSModel model = null;

	public ExchangeWebService() {
		super();
	}

	public ExchangeWebService(WSModel model) {
		this.model = model;
	}

	private ProgressDialog dialog;
	protected Context context;

	private boolean auth;
	private boolean debug;
	private boolean cleanUrl;

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

		if (isDebug()) {
			InputStream src2 = fetchStream(getURL());
			String myString;
			try {
				myString = readInputStreamAsString(src2);
				Log.d(TAG, "DEBUG : " + myString);
			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		if (source != null) {
			Gson gson = new Gson();
			Reader reader = new InputStreamReader(source);

			try {
				response = (T[]) gson.fromJson(reader, getMapperClass());
			} catch (JsonSyntaxException jse) {
				response = null;

				Log.e(TAG, "JSE : " + jse.getMessage());
				Log.e(TAG, "CAUSE : " + jse.getCause());
				Log.e(TAG, "CLASS : " + jse.getClass());

				// if(jse.getMessage().contains(cs))

			}

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
		if (isDebug()) {
			try {
				InputStream source2 = fetchStream(getURL());
				String myString = readInputStreamAsString(source2);
				Log.d(TAG, "DEBUG : " + myString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		if (params != null) {

			String paramSeperator = "?";

			if (cleanUrl) {
				paramSeperator = "/";
			} else {
				paramSeperator = "?";
			}

			for (String key : params.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(key, params.get(key)
						+ ""));
			}
		}

		DefaultHttpClient client = new DefaultHttpClient();

		if (isAuth()) {
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					"admin" + ":" + "1234");
			client.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					credentials);
		}

		// HttpUriRequest request = null;
		if (type == TYPE_GET) {
			
//			throw new MethodNotSupportedException("GET 	method is not yet suported");
			Log.e(TAG, "GET not yet supported");
			/*try {
				HttpGet get = new HttpGet(url);

				StringEntity se = new StringEntity("");
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				get.setEntity(se);
				HttpResponse response = client.execute(post);
				if (response != null) {
					InputStream in = response.getEntity().getContent();
					return in;
				} else {
					Log.e(TAG, "Response found null");
					return null;
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}*/
		} else {

			try {
				HttpPost post = new HttpPost(url);

				/*
				 * format json from bean
				 */
				
				
				
				StringEntity se = new StringEntity(new Gson().toJson(model));
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				post.setEntity(se);
				HttpResponse response = client.execute(post);
				if (response != null) {
					InputStream in = response.getEntity().getContent();
					return in;
				} else {
					Log.e(TAG, "Response found null");
					return null;
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}
		return null;
		
	}

	private InputStream fetchResponse(HttpUriRequest request,
			DefaultHttpClient client) {
		try {

			Log.d(TAG, ("executing request " + request.getRequestLine()));
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
	 *            the new access token modelneeded
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

	public boolean isAuth() {
		return auth;
	}

	public void needsAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isCleanUrl() {
		return cleanUrl;
	}

	public void setCleanUrl(boolean cleanUrl) {
		this.cleanUrl = cleanUrl;
	}

}