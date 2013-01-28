package com.opendroid.ws;

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
	
	private WsConfiguration(final Builder builder)
	{
		this.auth = builder.auth;
		this.username = builder.username;
		this.password = builder.password;
		this.debug = builder.debug;
		this.cleanUrl = builder.cleanUrl;
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

	/**
	 * Builder pattern for setting all configurations.
	 */
	public static class Builder {
		
		/** The auth. */
		private boolean auth;
		
		/** The username. */
		private String username;
		
		/** The password. */
		private String password;

		private boolean debug;

		private boolean cleanUrl;
		
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
		
		public WsConfiguration build()
		{
			return new WsConfiguration(this);
		}
		
	}

}
