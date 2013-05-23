/*
 * @author Rishi Kolvekar
 * 
 */
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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.opendroid.ws.models.WsModel;

// TODO: Auto-generated Javadoc
/**
 * The Class WebService. used for basic webservices parsing
 * 
 * @param <T>
 *            the generic type
 */
public abstract class WebService<T extends WsModel> implements IWebService {

	/** The dialog. */
	private ProgressDialog dialog;

	/** The context. */
	protected Context context;

	/** The auth. */
	private boolean auth;

	/** The debug. */
	private boolean debug;

	/** The clean url. */
	private boolean cleanUrl;

	/** The password. */
	private String username, password;

	/** The Constant TAG. */
	final static String TAG = "WebService";

	/** The Constant TYPE_POST. */
	public final static int TYPE_GET = 0, TYPE_POST = 1;

	/** The type. */
	int type = 0;

	/** The params. */
	Map<String, String> params;

	/** The access token needed. */
	private boolean accessTokenNeeded;

	/**
	 * Gets the url of webservices.
	 * 
	 * @return the url
	 */
	protected abstract String getURL();

	/**
	 * Instantiates a new web service for request method type (TYPE_GET).
	 */
	public WebService() {
		this.type = TYPE_GET;
	}

	/**
	 * Instantiates a new web service for request method type (TYPE_GET or
	 * TYPE_POST).
	 * 
	 * @param params
	 *            parameters that need to be passed in request
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 */
	public WebService(Map<String, String> params, int type) {
		this.type = type;
		this.params = params;
		Log.d("opendroid-ws => params", String.valueOf(params));
	}

	/**
	 * Instantiates a new web service and defaults to GET type.
	 * 
	 * @param params
	 *            parameters that need to be passed in request
	 */
	public WebService(Map<String, String> params) {
		type = TYPE_GET;
		this.params = params;
		Log.d("opendroid-ws => params", String.valueOf(params));
	}

	/**
	 * Instantiates a new web service.
	 * 
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 */
	public WebService(int type) {
		this.type = type;
	}

	/**
	 * Gets the mapper class.
	 * 
	 * @return the mapper class
	 * @author Gets the mapper class for web service response.
	 */
	protected abstract Class<?> getMapperClass();

	/**
	 * Gets the response array.
	 * 
	 * @return the response array
	 * @author Gets the response in form of array of mapping class.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public T[] getResponseArray() throws IOException {
		T[] response = null;
		InputStream source = fetchStream(getURL());

		if (isDebug()) {
			InputStream src2 = fetchStream(getURL());
			String myString;

			myString = readInputStreamAsString(src2);
			Log.d(TAG, "DEBUG : " + myString);

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
	 * Gets the response object.
	 * 
	 * @return the response object
	 * @author Gets the response in form of object of mapping class.
	 * @throws IOException
	 */
	public T getResponseObject() throws IOException {
		T response = null;
		InputStream source = fetchStream(getURL());
		if (isDebug()) {

			InputStream source2 = fetchStream(getURL());
			String myString = readInputStreamAsString(source2);
			Log.d(TAG, "DEBUG : " + myString);

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
	 * Gets the response in String format. Use this method when you want to
	 * fetch String data deom webservice
	 * 
	 * @return the response object
	 * @author Gets the response in form of object of mapping class.
	 * @throws IOException
	 */
	public String getResponseString() throws IOException {
		InputStream source = fetchStream(getURL());
		String responseString = readInputStreamAsString(source);
		if (isDebug()) {
			Log.d(TAG, responseString);
		}
		return responseString;
	}

	/**
	 * Fetch stream.
	 * 
	 * @param url
	 *            the url
	 * @return the input stream
	 * @author Fetch stream.
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
					this.username + ":" + this.password);
			client.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					credentials);
		}

		// HttpUriRequest request = null;
		if (type == TYPE_GET) {
			Log.d(TAG, "IN GET");
			String paramString = URLEncodedUtils.format(nameValuePairs,
					HTTP.UTF_8);
			url += "?" + paramString;
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

	/**
	 * Fetch response.
	 * 
	 * @param request
	 *            the request
	 * @param client
	 *            the client
	 * @return the input stream
	 */
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
	 * Read input stream as string.
	 * 
	 * @param in
	 *            the in
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static String readInputStreamAsString(InputStream in)
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

	/**
	 * Checks if is auth.
	 * 
	 * @return true, if is auth
	 */
	public boolean isAuth() {
		return auth;
	}

	/**
	 * Sets browser level authentication enabled.
	 * 
	 * @param auth
	 *            set true if your service needs browser level authentication
	 */
	public void needsAuth(boolean auth) {
		this.auth = auth;
	}

	/**
	 * Sets browser level authentication credentials.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public void setAuthCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * if set to true, then application will print web service output in logcat
	 * as tag "DEBUG" WARNING : do not use this method while some service that
	 * inserts some data on the server, else data will be inserted twice.
	 * WARNING : this method slows down your request, so remove/comment once its
	 * usage is done, use this method for debugging purpose only.
	 * 
	 * @return true, if is debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Sets the debug, . if set to true, then application will print web service
	 * output in logcat as tag "DEBUG" WARNING : do not use this method while
	 * some service that inserts some data on the server, else data will be
	 * inserted twice. WARNING : this method slows down your request, so
	 * remove/comment once its usage is done, use this method for debugging
	 * purpose only.
	 * 
	 * @param debug
	 *            the new debug
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Checks if is clean url example : http://sme.site.com/hello/world.
	 * 
	 * @return true, if is clean url
	 */
	public boolean isCleanUrl() {
		return cleanUrl;
	}

	/**
	 * Sets the clean url. example : http://sme.site.com/hello/world..
	 * 
	 * @param cleanUrl
	 *            the new clean url
	 */
	public void setCleanUrl(boolean cleanUrl) {
		this.cleanUrl = cleanUrl;
	}

}
