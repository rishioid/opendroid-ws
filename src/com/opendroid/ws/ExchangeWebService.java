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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.opendroid.ws.models.WsModel;

//implemented Iwebservice
public class ExchangeWebService<T extends WsModel> implements IWebService<T> {

	WsModel model = null;
	private boolean auth;
	private boolean debug;
	private boolean cleanUrl;
	private String username = "";
	private String password = "";
	protected Context context;
	final static String TAG = "WebService";
	/** The Constant TYPE_POST. */
	private Method type = Method.TYPE_POST;
	/** The params. */
	Map<String, String> params;
	/** The access token needed. */
//	private boolean accessTokenNeeded;
	/** The accesstoken. */
//	private String accesstoken;
	private String url;
	@SuppressWarnings("unused")
	private boolean hasAttachment;
	private Class<?> mapperClass;
	private List<Header> headers;
	@SuppressWarnings("unused")
	private String attchmentKey;
	@SuppressWarnings("unused")
	private File attachmentFle;
	
	//made constructor private
//	private ExchangeWebService() {
//		super();
//	}

//	public ExchangeWebService(WsModel model) {
//		this.model = model;
//		type = Method.TYPE_GET;
//	}
	//new constructor with WsConfig
	public ExchangeWebService(String url,WsModel model,WsConfiguration config ) {
		this.model = model;
		this.url = url;
		if(null != config){
			this.type = config.getMethod();
			this.debug = config.isDebug();
			this.auth = config.isAuth();
			this.cleanUrl = config.isCleanUrl();
			this.username = config.getUsername();
			this.password = config.getPassword();
			//TODO: MULTIPART SUPPORT TO BE ADDED
			this.hasAttachment = config.isHasAttachment();
			this.attchmentKey = config.getAttachmentKey();
			this.attachmentFle = config.getAttachmentFile();
			this.mapperClass = config.getMapperClass();
			this.headers = config.getHeaders();
		}
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
//	protected abstract String getURL();

	/**
	 * @author Rishi K Gets the mapper class.
	 * 
	 * @return the mapper class
	 */
//	protected abstract Class<?> getMapperClass();

	/**
	 * @author Rishi K Gets the response in form of array of mapping class.
	 * 
	 * @return the response array
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public T[] getResponseArray() throws IOException {
		T[] response = null;
		InputStream source = fetchStream(url);
		String responseString = readInputStreamAsString(source);
		if (isDebug()) {
				Log.d(TAG, "DEBUG : " + responseString);
		}
		if (source != null) {
			Gson gson = new Gson();
			try {
				if(mapperClass == null)
					throw new IllegalStateException("Mapper class can not be null for response Array. Set mapper class in WsConfig object.");
				response = (T[]) gson.fromJson(responseString, mapperClass);
			} catch (JsonSyntaxException jse) {
				response = null;

				Log.e(TAG, "JSE : " + jse.getMessage());
				Log.e(TAG, "CAUSE : " + jse.getCause());
				Log.e(TAG, "CLASS : " + jse.getClass());
			}
		}
		return response;
	} // end callWebService()

	/**
	 * @author Rishi K Gets the response in form of object of mapping class.
	 * 
	 * @return the response object
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public T getResponseObject() throws IOException {
		T response = null;
		InputStream source = fetchStream(url);
			String responseString = readInputStreamAsString(source);
		if (isDebug()) {
				Log.d(TAG, "DEBUG : " + responseString);
		}
		if (source != null) {
			Gson gson = new Gson();
			if(mapperClass == null)
				throw new IllegalStateException("Mapper class can not be null for response Object. Set mapper class in WsConfig object.");
			response = (T) gson.fromJson(responseString, mapperClass);
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
	private InputStream fetchStream(String url) throws IllegalStateException{

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		//TODO: add multipart support
		if(!cleanUrl){
			if (params != null) {
				for (String key : params.keySet()) {
					nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
				}
			}
		}

		DefaultHttpClient client = new DefaultHttpClient();

		if (isAuth()) {
			if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
				throw new IllegalStateException("Username & Password can not be null or empty when authentication is required. Please set Username and Password in WsConfig Object.");
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					username + ":" + password);
			client.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
					credentials);
		}
		
		if (type == Method.TYPE_GET) {
//			throw new MethodNotSupportedException("GET 	method is not yet suported");
//			Log.e(TAG, "GET not yet supported");
			try {
				HttpGet get = new HttpGet(url);

//				StringEntity se = new StringEntity("");
//				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
//						"application/json"));
//				get.setEntity(se);
				
				if(headers != null){
					get.setHeaders((Header[])headers.toArray());
				}
				
				HttpResponse response = client.execute(get);
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
		} else if(type == Method.TYPE_POST){

			try {
				HttpPost post = new HttpPost(url);

				/*
				 * format json from bean
				 */
				
				if(headers != null){
					post.setHeaders((Header[])headers.toArray());
				}
				
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
		}else if(type == Method.TYPE_PUT){
			try {
				HttpPut put = new HttpPut(url);

				/*
				 * format json from bean
				 */
				
				if(headers != null){
					put.setHeaders((Header[])headers.toArray());
				}
				
				StringEntity se = new StringEntity(new Gson().toJson(model));
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				put.setEntity(se);
				HttpResponse response = client.execute(put);
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
		}else if(type == Method.TYPE_DELETE){
			try {
				HttpDelete delete = new HttpDelete(url);

				/*
				 * format json from bean
				 */
				
				if(headers != null){
					delete.setHeaders((Header[])headers.toArray());
				}
				
				StringEntity se = new StringEntity(new Gson().toJson(model));
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				//TODO: add params to delete
//				delete.setEntity(se);
				HttpResponse response = client.execute(delete);
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

	/*private InputStream fetchResponse(HttpUriRequest request,
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
	}*/

	/**
	 * Checks if is access token needed.
	 * 
	 * @return true, if is access token needed
	 */
//	public boolean isAccessTokenNeeded() {
//		return accessTokenNeeded;
//	}

	/**
	 * Sets the access token needed.
	 * 
	 * @param accessTokenNeeded
	 *            the new access token modelneeded
	 */
//	public void setAccessTokenNeeded(boolean accessTokenNeeded) {
//		this.accessTokenNeeded = accessTokenNeeded;
//	}

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

//	public void needsAuth(boolean auth) {
//		this.auth = auth;
//	}

	public boolean isDebug() {
		return debug;
	}

//	public void setDebug(boolean debug) {
//		this.debug = debug;
//	}

	public boolean isCleanUrl() {
		return cleanUrl;
	}

	@Override
	public String getResponseString() throws IOException,
			IllegalAccessException {
		return null;
	}

//	public void setCleanUrl(boolean cleanUrl) {
//		this.cleanUrl = cleanUrl;
//	}
}
