/*
 * @author Rishi Kolvekar
 * 
 */
package com.opendroid.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.opendroid.ws.models.WsModel;

/**
 * The Class WebService. used for basic webservices parsing
 * 
 * @param <T>
 *            the generic type
 */
public class WebService<T extends WsModel> implements IWebService<T> {

	/** The context. */
	protected Context context;
	
	/** The auth. */
	private boolean auth;

	/** The debug. */
	private boolean debug;

	/** The clean url. */
	private boolean cleanUrl;

	/** The password. */
	private String username = "", password = "";

	/** The Constant TAG. */
	final static String TAG = "WebService";

	/** The Constant TYPE_POST. */

	private Method type = Method.TYPE_GET;

	/** The params. */
	Map<String, String> params;
	
	/** The access token needed. */
//	private boolean accessTokenNeeded;

	/*
	 * JsonDeserializer implementation
	 */
	private JsonDeserializer<T> deserializer;
	
	private Class<?> deserializeClass;

	private boolean hasAttachment;

	private String url;

	private Class<?> mapperClass;

	private List<Header> headers;

	private String attchmentKey;

	private File attachmentFle;

	/**
	 * Gets the url of webservices.
	 * 
	 * @return the url
	 */
//	protected abstract String getURL();

	/**
	 * @deprecated
	 * Instantiates a new web service for request method type (TYPE_GET).
	 */
	/*public WebService() {
		this.type = Method.TYPE_GET;
	}*/

	/**
	 * Instantiates a new web service for request method type (TYPE_GET or
	 * TYPE_POST).
	 * 
	 * @param params
	 *            parameters that need to be passed in request
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 */
	public WebService(String url,Map<String, String> params, WsConfiguration config) {
		this.params = params;
		this.url = url;
		if(config != null){
			this.type = config.getMethod();
			this.auth = config.isAuth();
			this.cleanUrl = config.isCleanUrl();
			this.debug = config.isDebug();
			this.username = config.getUsername();
			this.password = config.getPassword();
			this.hasAttachment = config.isHasAttachment();
			this.attchmentKey = config.getAttachmentKey();
			this.attachmentFle = config.getAttachmentFile();
			this.mapperClass = config.getMapperClass();
			this.headers = config.getHeaders();
		}
		Log.d("opendroid-ws => params", String.valueOf(params));
	}

	/**
	 * Instantiates a new web service and defaults to GET type.
	 * 
	 * @param params
	 *            parameters that need to be passed in request
	 * @deprecated
	 *            
	 */
	/*public WebService(Map<String, String> params) {
		type = Method.TYPE_GET;
		this.params = params;
		Log.d("opendroid-ws => params", String.valueOf(params));
	}*/

	/**
	 * Instantiates a new web service.
	 * 
	 * @param type
	 *            WebService.TYPE_GET or WebService.TYPE_POST
	 *            
	 * @deprecated           
	 */
	/*public WebService(Method type) {
		this.type = type;
	}*/

	/**
	 * Gets the response array.
	 * 
	 * @return the response array
	 * @author Gets the response in form of array of mapping class.
	 * @throws IOException
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("unchecked")
	public T[] getResponseArray() throws IOException, IllegalAccessException {
		T[] response = null;
		InputStream source = fetchStream(url);
		String responseString = readInputStreamAsString(source);
 
		if (isDebug()) {
			Log.d(TAG, "DEBUG : " + responseString);
		}

		if (source != null) {

			Gson gson = null;

			if (deserializer != null) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder
						.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
				gsonBuilder.registerTypeAdapter(deserializeClass, deserializer);
				gson = gsonBuilder.create();
			} else {
				gson = new Gson();
			}
			try {
				if(mapperClass == null)
					throw new IllegalStateException("Mapper class can not be null for response array. Set mapper class in WsConfig object.");
				response = (T[]) gson.fromJson(responseString, mapperClass);
				  
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
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("unchecked")
	public T getResponseObject() throws IOException, IllegalAccessException {
		T response = null;
		InputStream source = fetchStream(url);
		String responseString = readInputStreamAsString(source);
		
		if (isDebug()) {
 			Log.d(TAG, "DEBUG : " + responseString);
		}
		if (source != null) {

			Gson gson = null;

			if (deserializer != null) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder
						.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
				gsonBuilder.registerTypeAdapter(deserializeClass, deserializer);
				gson = gsonBuilder.create();
			} else {
				gson = new Gson();
			}
			if(mapperClass == null)
				throw new IllegalStateException("Mapper class can not be null for response Object. Set mapper class in WsConfig object.");
			response = (T) gson.fromJson(responseString, mapperClass);
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
	 * @throws IllegalAccessException 
	 */
	public String getResponseString() throws IOException, IllegalAccessException {
		InputStream source = fetchStream(url);
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
	 * @throws IllegalAccessException 
	 */
	protected InputStream fetchStream(String url) throws IllegalAccessException {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		MultipartEntity multipartEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		if(!cleanUrl){
			if (params != null) {
				for (String key : params.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key, params.get(key)
							+ ""));
				}
			}
		}

		DefaultHttpClient client = new DefaultHttpClient();
		
		if (hasAttachment) {

			Log.d(TAG, "File hasImageAttachment: " + hasAttachment);
			if(TextUtils.isEmpty(attchmentKey) || null == attachmentFle)
				throw new IllegalAccessException("Attachment key or Attachment File can not be null or empty when has attachment is set ti true in WsConfig.");
			Log.d(TAG, "File attached: " + attachmentFle.getAbsolutePath());
			multipartEntity
					.addPart(attchmentKey, new FileBody(attachmentFle));
			//TODO: MULTIPART IS INCOMPLETE

		}

		if (isAuth()) {
			if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
				throw new IllegalStateException("Username &/or Password can not be null or empty when authentication is required. Please set Username and Password in WsConfig Object.");
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					this.username + ":" + this.password);
			client.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					credentials);
		}
		
		if (type == Method.TYPE_GET) {
			Log.d(TAG, "IN GET");
			if(!isCleanUrl()){
				String paramString = URLEncodedUtils.format(nameValuePairs,
						HTTP.UTF_8);
				url += "?" + paramString;
			}
			HttpGet request = new HttpGet(url);
			if(headers != null){
				request.setHeaders((Header[])headers.toArray());
			}
			return fetchResponse(request, client);
		} else if(type == Method.TYPE_POST){

			Log.d(TAG, "IN POST");
			HttpPost request = new HttpPost(url);
			if(headers != null){
				request.setHeaders((Header[])headers.toArray());
			}
			try {
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
				return fetchResponse(request, client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(type == Method.TYPE_PUT){

			Log.d(TAG, "IN PUT");
			HttpPut request = new HttpPut(url);
			if(headers != null){
				request.setHeaders((Header[])headers.toArray());
			}
			try {
				request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
				return fetchResponse(request, client);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(type == Method.TYPE_DELETE){

			Log.d(TAG, "IN DELETE");
			HttpDelete request = new HttpDelete(url);
			if(headers != null){
				request.setHeaders((Header[])headers.toArray());
			}
			try {
				//TODO: HOW TO ADD PARAMS TO DELETE METHOD
//				request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
//						HTTP.UTF_8));
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
			if(debug)
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
	protected static String readInputStreamAsString(InputStream in)
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
	 * Checks if is clean url example : http://sme.site.com/hello/world.
	 * 
	 * @return true, if is clean url
	 */
	public boolean isCleanUrl() {
		return cleanUrl;
	}

	public void registerTypeAdapter(Class<?> deserializeClass,
			JsonDeserializer<T> deserializer) {
		this.deserializer = deserializer;
		this.deserializeClass = deserializeClass;
	}
}
