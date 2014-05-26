package com.opendroid.ws;

import java.io.File;
import java.util.List;

import org.apache.http.Header;

import com.opendroid.ws.IWebService.Method;
/**
 * Set all your configuration at time of initialization
 */
public class WsConfiguration {
	
	/** The auth. */
	private final boolean auth;
	
	/** The username. */
	private final String username;
	
	/** The password. */
	private final String password;

	private final boolean debug;

	private final boolean cleanUrl;
	
	private final Method method;
	
	private final boolean hasAttachment;
	
	private final String attachmentKey;
	
	private final File attachmentFile;
	
	private final Class<?> mapperClass;
	
	private final List<Header> headers;
	private WsConfiguration(final Builder builder)
	{
		this.auth = builder.auth;
		this.username = builder.username;
		this.password = builder.password;
		this.debug = builder.debug;
		this.cleanUrl = builder.cleanUrl;
		this.method = builder.method;
		this.hasAttachment = builder.hasAttachment;
		this.attachmentKey = builder.attachmentKey;
		this.attachmentFile = builder.attachmentFile;
		this.mapperClass = builder.mapperClass;
		this.headers = builder.headers;
	}
	
	public boolean isCleanUrl() {
		return cleanUrl;
	}

	public boolean isDebug() {
		return debug;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public boolean isAuth() {
		return auth;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public boolean isHasAttachment() {
		return hasAttachment;
	}
	
	public String getAttachmentKey() {
		return attachmentKey;
	}

	public File getAttachmentFile() {
		return attachmentFile;
	}

	public Class<?> getMapperClass() {
		return mapperClass;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	/**
	 * Builder pattern for setting all configurations.
	 */
	public static class Builder {
		
		/** The auth. */
		private boolean auth;
		
		/** The username. */
		private String username = "";
		
		/** The password. */
		private String password = "";

		private boolean debug;

		private boolean cleanUrl;
		
		private Method method = Method.TYPE_POST;
		
		private boolean hasAttachment;
		
		private String attachmentKey;
		
		private File attachmentFile;
		
		private Class<?> mapperClass;
		
		private List<Header> headers;
		/**
		 * Checks if is authentication is needed for this webservice.
		 *
		 * @return true, if authentication is needed
		 */
		public boolean isAuth() {
			return auth;
		}
		
		/**
		 * Set this flag as true if your webservice needs browser authentication.
		 *
		 * @param auth the auth
		 * @return the builder
		 */
		public Builder setAuth(boolean auth) {
			this.auth = auth;
			return this;
		}
		
		/**
		 * Sets browser level authentication credentials.
		 *
		 * @param username the username
		 * @param password the password
		 * @return the builder
		 */
		public Builder setAuthCredentials(String username, String password) {
			this.username = username;
			this.password = password;
			return this;
		}

		/**
		 * authentication username
		 *
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * authentication password
		 *
		 * @return the password
		 */
		public String getPassword() {
			return password;
		}
		
		/**
		 * if set to true, then application will print web service output in logcat
		 * as tag "DEBUG" WARNING : do not use this method while some service that
		 * inserts some data on the server, else data will be inserted twice.
		 * 
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
		 * output in logcat as tag "DEBUG" 
		 * 
		 * WARNING : do not use this method while
		 * some service that inserts some data on the server, else data will be
		 * inserted twice. WARNING : this method slows down your request, so
		 * remove/comment once its usage is done, use this method for debugging
		 * purpose only.
		 * 
		 * @param debug
		 *            the new debug
		 */
		public Builder setDebug(boolean debug) {
			this.debug = debug;
			return this;
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
		public Builder setCleanUrl(boolean cleanUrl) {
			this.cleanUrl = cleanUrl;
			return this;
		}
		
		public Builder setMethod(Method method) {
			this.method = method;
			return this;
		}
		
		public Builder setHasAttachment(boolean hasAttachment) {
			this.hasAttachment = hasAttachment;
			return this;
		}
		
		public Builder setAttachmentKey(String attachmentKey) {
			this.attachmentKey = attachmentKey;
			return this;
		}

		public Builder setAttachmentFile(File attachmentFile) {
			this.attachmentFile = attachmentFile;
			return this;
		}
		
		public Builder setMapperClass(Class<?> mapperClass) {
			this.mapperClass = mapperClass;
			return this;
		}

		public Builder setHeaders(List<Header> headers) {
			this.headers = headers;
			return this;
		}

		public WsConfiguration build()
		{
			return new WsConfiguration(this);
		}
		
	}

}
