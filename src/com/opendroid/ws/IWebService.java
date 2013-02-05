/*
 * @author Rishi Kolvekar
 * 05-10-2012
 */
package com.opendroid.ws;

import java.io.IOException;

import com.opendroid.ws.models.WsModel;

/**
 * The Interface IWebService, for maintaining a standard interface of data communication between 
 * REST, SOAP and WCF based web-services.
 *
 * @param <T> the generic type
 */
public interface IWebService<T extends WsModel> {
	
	/**
	 * Gets the response object returned from webservices.
	 *
	 * @return the response object
	 */
	public Object getResponseObject() throws IOException;
	
	/**
	 * Gets the response array, useful for services that return array of objects.
	 *
	 * @return the response array
	 */
	public Object[] getResponseArray() throws IOException;
	
	/**
	 * Gets the response as plain  String, useful for services that return plain text.
	 *
	 * @return the response in string format
	 */
	public String getResponseString() throws IOException;
	
}
